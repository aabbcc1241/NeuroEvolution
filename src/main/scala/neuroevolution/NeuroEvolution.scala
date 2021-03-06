package neuroevolution

import neuroevolution.geneticalgorithm.ProblemType.ProblemType
import neuroevolution.geneticalgorithm.{GA, ProblemType}
import neuroevolution.neuralnetwork.{ActivationFunction, Perceptron}

/**
 * @param eval_perceptron_function
 * parameter
 * [Double]: input of perceptron
 * [Double]: output of perceptron
 * result
 * Double: score of the perceptron
 */

class NeuroEvolution(n_Bit_Weight: Int, n_Bit_Bias: Int, numberOfNodes: Array[Int], activationFunction: ActivationFunction,
                     get_perceptron_inputs: => Array[Array[Double]], eval_perceptron_function: (Array[Double], Array[Double]) => Double,
                     val popSize: Int = 32, var pSelection: Double = 0.25d,
                     var pMutationPow: Double = 0.01d, aMutationPow: Double = 0.1d,
                     var parent_immutable: Boolean,
                     val problemType: ProblemType = ProblemType.Minimize,
                     var loopInterval: Long = 100)
  extends Thread {
  val bitSize: Int = Perceptron.getNumberOfWeight(numberOfNodes) * n_Bit_Weight + numberOfNodes.sum * n_Bit_Bias
  val converter: Converter = new Converter(N_BIT_WEIGHT = n_Bit_Weight, N_BIT_BIAS = n_Bit_Bias, numberOfNodes, BIT_SIZE = bitSize, activationFunction)
  val ga: GA = new GA(POP_SIZE = popSize, BIT_SIZE = bitSize, P_SELECTION = pSelection,
    P_MUTATION_POW = pMutationPow, A_MUTATION_POW = aMutationPow, PARENT_IMMUTABLE = parent_immutable,
    EVAL_FITNESS_FUNCTION = evalFitness_function,
    PROBLEM_TYPE = problemType,
    LOOP_INTERVAL = loopInterval
  )

  def evalFitness_function(rawCode: Array[Boolean]): Double = {
    val perceptron: Perceptron = converter.decode(rawCode)
    val inputs: Array[Array[Double]] = get_perceptron_inputs
    val output_fitness: Array[Double] = Array.fill[Double](inputs.length)(0d)
    inputs.indices.par.foreach(i => output_fitness(i) = eval_perceptron_function(inputs(i), perceptron.run(inputs(i))))
    val avg: Double = output_fitness.sum * 1d / output_fitness.length
    var overallFitness: Double = avg
    output_fitness.foreach(fitness => overallFitness += Math.pow(fitness - avg, 2))
    overallFitness
  }

  def getBestPerceptron = {
    converter.decode(ga.getBestGene.rawCode)
  }

  override def run = {
    ga.start
  }

  def saveAllToFile(filename: String, isRaw: Boolean) = {
    ga.saveAllToFile(filename, isRaw)
  }

  def saveBestToFile(filename: String, isRaw: Boolean) = {
    ga.saveBestToFile(filename, isRaw)
  }
}
