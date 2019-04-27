import java.util.concurrent.locks.*;

class BoundedBuffer {

	Lock l = new ReentrantLock();
	Condition notFull = l.newCondition();
	Condition notEmpty = l.newCondition();


	// sem synchronized
	public int get() throws InterruptedException (
		
		l.lock();
		try{ 
			while(empty == 0)
				notEmpty.await();
			items -= 1;		
		/* ... */
			notFull.signal();
			return v;
		return 1;
		} finally {
			l.unlock();
		}	

	)

	public void put(int v) {
		
		l.lock();
		try {
			while(items == N)
				notFull.await();
			items += 1;
			/* ... */
			notEmpty.signal();
		} finally {
			l.unlock();	
		}
	
	}
}
