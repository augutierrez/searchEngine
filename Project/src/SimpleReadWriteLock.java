import java.util.ConcurrentModificationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Maintains a pair of associated locks, one for read-only operations and one
 * for writing. The read lock may be held simultaneously by multiple reader
 * threads, so long as there are no writers. The write lock is exclusive, but
 * also tracks which thread holds the lock. If unlock is called by any other
 * thread, a {@link ConcurrentModificationException} is thrown.
 *
 * @see SimpleLock
 * @see SimpleReadWriteLock
 */
public class SimpleReadWriteLock {

	/** The lock used for reading. */
	private final SimpleLock readerLock;

	/** The lock used for writing. */
	private final SimpleLock writerLock;

	/**
	 * Number of active readers
	 */
	private int readers;

	/**
	 * Number of active writers
	 */
	private int writers;

	/**
	 * Log for the class
	 */
	private static Logger log = LogManager.getLogger();

	/**
	 * Object that is used as a lock for ReadLock() and WriteLock()
	 */
	private final Object object;

	/**
	 * Constructor method
	 */
	public SimpleReadWriteLock() {
		readerLock = new ReadLock();
		writerLock = new WriteLock();
		readers = 0;
		writers = 0;
		object = new Object();
	}

	/**
	 * Returns the reader lock.
	 *
	 * @return the reader lock
	 */
	public SimpleLock readLock() {
		// NOTE: DO NOT MODIFY THIS METHOD
		return readerLock;
	}

	/**
	 * Returns the writer lock.
	 *
	 * @return the writer lock
	 */
	public SimpleLock writeLock() {
		// NOTE: DO NOT MODIFY THIS METHOD
		return writerLock;
	}

	/**
	 * Determines whether the thread running this code and the other thread are in
	 * fact the same thread.
	 *
	 * @param other the other thread to compare
	 * @return true if the thread running this code and the other thread are not
	 *         null and have the same ID
	 *
	 * @see Thread#getId()
	 * @see Thread#currentThread()
	 */
	public static boolean sameThread(Thread other) {
		// NOTE: DO NOT MODIFY THIS METHOD
		return other != null && other.getId() == Thread.currentThread().getId();
	}

	/**
	 * Used to maintain simultaneous read operations.
	 */
	private class ReadLock implements SimpleLock {

		/**
		 * Will wait until there are no active writers in the system, and then will
		 * increase the number of active readers.
		 */
		@Override
		public void lock() {
			synchronized (object) {
				while (writers > 0) {
					try {
						object.wait();
					} catch (InterruptedException e) {
						// log and re-interrupt
						log.debug("Inside readlock");
						Thread.currentThread().interrupt();
					}
				}
				readers++;
			}
		}

		/**
		 * Will decrease the number of active readers, and notify any waiting threads if
		 * necessary.
		 */
		@Override
		public void unlock() {
			synchronized (object) {
				readers--;
				object.notifyAll(); // TODO This can only wake up a writer
				// TODO Only notify if readers is 0
			}
		}

	}

	/**
	 * Used to maintain exclusive write operations.
	 */
	private class WriteLock implements SimpleLock {

		/**
		 * Holds current thread that is writing
		 */
		Thread writer;

		/**
		 * Will wait until there are no active readers or writers in the system, and
		 * then will increase the number of active writers and update which thread holds
		 * the write lock.
		 */
		@Override
		public void lock() {
			synchronized (object) {
				while (writers > 0 || readers > 0) {
					try {
						object.wait();
					} catch (InterruptedException e) {
						// log and re-interrupt
						Thread.currentThread().interrupt();
					}
				}
				writer = Thread.currentThread();
				writers++;
			}
		}

		/**
		 * Will decrease the number of active writers, and notify any waiting threads if
		 * necessary. If unlock is called by a thread that does not hold the lock, then
		 * a {@link ConcurrentModificationException} is thrown.
		 *
		 * @see #sameThread(Thread)
		 *
		 * @throws ConcurrentModificationException if unlock is called without
		 *                                         previously calling lock or if unlock
		 *                                         is called by a thread that does not
		 *                                         hold the write lock
		 */
		@Override
		public void unlock() throws ConcurrentModificationException {
			synchronized (object) {
				if (SimpleReadWriteLock.sameThread(writer)) {

					writers--;
					writer = null;
					object.notifyAll();
					// writerLock.unlock();
				} else
					throw new ConcurrentModificationException();
			}
		}
	}
}