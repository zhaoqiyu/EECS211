package nachos.threads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nachos.machine.*;
import nachos.threads.KThread.PingTest;


/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	
	
    private static final char debugAlarm = 'a';
    
    private List<KThread> waitQueue;
    private List<Long> timeQueue;
    private Lock queueLock;
    private long time_Created;
	

	
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 * 
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */
	public Alarm() {
		
		Lib.debug(debugAlarm, "Creating Alarm" + Machine.timer().getTime());
        time_Created = Machine.timer().getTime();
        waitQueue = new ArrayList<KThread>();
        timeQueue = new ArrayList<Long>();
        queueLock = new Lock();
		
		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread that
	 * should be run.
	 */
	public void timerInterrupt() {
//		KThread.currentThread().yield();
		
	    boolean preState = Machine.interrupt().disable();

	    
	    KThread thread;
        //If there is a task that is waiting, restore it to ready status
        for (int i = 0; i < waitQueue.size(); i++) {
            if (Machine.timer().getTime() > timeQueue.get(i)) {
                waitQueue.get(i).ready();
                waitQueue.remove(i);
                timeQueue.remove(i);
            }
        }
	    
	    Machine.interrupt().restore(preState);
	    
	    KThread.yield();
	}

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
	 * in the timer interrupt handler. The thread must be woken up (placed in
	 * the scheduler ready set) during the first timer interrupt where
	 * 
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 * 
	 * @param x the minimum number of clock ticks to wait.
	 * 
	 * @see nachos.machine.Timer#getTime()
	 */
	public void waitUntil(long x) {
		// for now, cheat just to get something working (busy waiting is bad)
//		long wakeTime = Machine.timer().getTime() + x;
//		while (wakeTime > Machine.timer().getTime())
//			KThread.yield();
		
		//Sets current thread as waitThread

        //Initializes wakeTime with x ticks
        long wakeTime = Machine.timer().getTime() + x;

        //Disable interrupts
        boolean intStatus = Machine.interrupt().disable();
        queueLock.acquire();

        //Puts task to sleep for x ticks
        waitQueue.add(KThread.currentThread());
        Lib.debug(debugAlarm, "Added new task size="+ waitQueue.size() + " timeCreated="+this.time_Created);
        timeQueue.add(wakeTime);

        queueLock.release();

        KThread.sleep();

        //Restores interrupts
        Machine.interrupt().restore(intStatus);
	}
	public static void alarmTest() {
		KThread t1=new KThread(new PingTest(1)).setName("forked thread");
		t1.fork();
		
		KThread t2=new KThread(new PingTest(2)).setName("forked thread");
		t2.fork();
		
		
		
        System.out.println("**** Alarm testing begins ****");

        for(int i = 0;i<5;i++){
            if(i == 2){
                System.out.println("my thread leave, the time is:"+Machine.timer().getTime()+", around 1700clock ticks");
                new Alarm().waitUntil(800);
                System.out.println("Thread 1 is backed, the time is:"+Machine.timer().getTime());
            }
            System.out.println("*** my thread looped "
                       + i + " times");
            KThread.currentThread().yield();
        }
	}

	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
