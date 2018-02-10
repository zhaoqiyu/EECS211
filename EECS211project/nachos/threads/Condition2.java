package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 * 
 * <p>
 * You must implement this.
 * 
 * @see nachos.threads.Condition
 */
public class Condition2 {
	/**
	 * Allocate a new condition variable.
	 * 
	 * @param conditionLock the lock associated with this condition variable.
	 * The current thread must hold this lock whenever it uses <tt>sleep()</tt>,
	 * <tt>wake()</tt>, or <tt>wakeAll()</tt>.
	 */
	public Condition2(Lock conditionLock) {
		this.conditionLock = conditionLock;
		Sleepinglist = new LinkedList<KThread>();
	}

	/**
	 * Atomically release the associated lock and go to sleep on this condition
	 * variable until another thread wakes it using <tt>wake()</tt>. The current
	 * thread must hold the associated lock. The thread will automatically
	 * reacquire the lock before <tt>sleep()</tt> returns.
	 */
	public void sleep() {//adding function for sleep
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		conditionLock.release();
		boolean intStatus = Machine.interrupt().disable();
		Sleepinglist.add(KThread.currentThread());
		KThread.currentThread().sleep();
		Machine.interrupt().restore(intStatus);
		conditionLock.acquire();
		
	}

	/**
	 * Wake up at most one thread sleeping on this condition variable. The
	 * current thread must hold the associated lock.
	 */
	public void wake() {//adding function for wake
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		if (!Sleepinglist.isEmpty()) {
			conditionLock.release();
			boolean intStatus = Machine.interrupt().disable();
			Sleepinglist.removeFirst().ready();
			Machine.interrupt().restore(intStatus);
			conditionLock.acquire();
		}
	}

	/**
	 * Wake up all threads sleeping on this condition variable. The current
	 * thread must hold the associated lock.
	 */
	public void wakeAll() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		while (!Sleepinglist.isEmpty())
			wake();
	}

	private Lock conditionLock;
	private LinkedList<KThread>Sleepinglist;
}
