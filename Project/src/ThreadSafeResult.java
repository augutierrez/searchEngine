
public class ThreadSafeResult extends Result {

//	/**
//	 * the location of the textfile for this result
//	 */
//	private final String directory;
//
//	/**
//	 * the amount of times our queries show up in this text file
//	 */
//	private int count;
//
//	/**
//	 * the total amount of words in the text file
//	 */
//	private int totalWords;
//
//	/**
//	 * the metric used to decide the importance of this text file for the search
//	 * query
//	 */
//	private double score;

	/** The lock used to protect concurrent access to the underlying set. */
	private final SimpleReadWriteLock lock;

	/**
	 * Constructor method
	 * 
	 * @param directory  - the location of the text file
	 * @param count      - the amount of times our query shows up in the text file
	 * @param totalWords - the amount of total words for the text file
	 */
	public ThreadSafeResult(String directory, int count, int totalWords) {
		super(directory, count, totalWords);
		lock = new SimpleReadWriteLock();
	}

	/**
	 * Updates the count for the queries in the text file, as well as the score of
	 * the result.
	 * 
	 * @param count - the amount of times a query shows up
	 */
	public void add(int count) {
		lock.writeLock().lock();
		super.add(count);
		lock.writeLock().unlock();
	}

	/**
	 * Returns the location of the text file for this result
	 * 
	 * @return directory
	 */
	public String getDirectory() {
		lock.readLock().lock();
		try {
			return super.getDirectory();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Returns the amount of times the query shows up in the text file
	 * 
	 * @return count
	 */
	public String getCount() {
		lock.readLock().lock();
		try {
			return super.getCount();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Returns the score for the text file
	 * 
	 * @return score
	 */
	public double getScore() {
		lock.readLock().lock();
		try {
			return super.getScore();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int compareTo(Result result) {
		lock.readLock().lock();
		try {
			return super.compareTo(result);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}
}
