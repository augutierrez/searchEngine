
public class ThreadSafeIndex extends InvertedIndex {

	/** The lock used to protect concurrent access to the underlying set. */
	private final SimpleReadWriteLock lock;

	/**
	 * Initializes a thread-safe indexed set.
	 *
	 * @param sorted whether the set should be sorted
	 */
	public ThreadSafeIndex() {
		// NOTE: DO NOT MODIFY THIS METHOD
		super();
		lock = new SimpleReadWriteLock();
	}

}
