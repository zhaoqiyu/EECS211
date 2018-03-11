private static class PingTest implements Runnable {
    PingTest(int which) {
        this.which = which;
    }

    public void run() {
        for (int i=0; i<5; i++) {
        System.out.println("*** thread " + which + " looped "
                   + i + " times");
        KThread.currentThread().yield();
        }
    }

    private int which;

public static void PriorityTest(){
    boolean status = Machine.interrupt().disable();//关中断，setPriority()函数中要求关中断
    final KThread a = new KThread(new PingTest(1)).setName("thread1");
    new PriorityScheduler().setPriority(a,2);
    System.out.println("thread1的优先级为："+new PriorityScheduler().getThreadState(a).priority);
    KThread b = new KThread(new PingTest(2)).setName("thread2");
    new PriorityScheduler().setPriority(b,4);
    System.out.println("thread2的优先级为："+new PriorityScheduler().getThreadState(b).priority);
    KThread c = new KThread(new Runnable(){
        public void run(){
            for (int i=0; i<5; i++) {
                if(i==2) 
                    a.join();
                System.out.println("*** thread 3 looped "
                           + i + " times");
                KThread.currentThread().yield();
            }
        }
    }).setName("thread3");
    new PriorityScheduler().setPriority(c,6);
    System.out.println("thread3的优先级为："+new PriorityScheduler().getThreadState(c).priority);
    a.fork();
    b.fork();
    c.fork();
    Machine.interrupt().restore(status);
}