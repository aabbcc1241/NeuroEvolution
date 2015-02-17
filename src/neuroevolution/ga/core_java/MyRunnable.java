package neuroevolution.ga.core_java;

public abstract class MyRunnable implements Runnable {
    final int IThread;
    final int N;
    Thread t;

    MyRunnable(int IThreadIN, int NIN) {
        IThread = IThreadIN;
        N = NIN;
        t = new Thread(this);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        for (int i = 0; i < N; i++)
            if ((i % GA.NThread) == IThread)
                action(i);
    }

    public abstract void action(int i);

    public void start() {
        t.start();
    }

    public boolean isAlive() {
        return t.isAlive();
    }
}
