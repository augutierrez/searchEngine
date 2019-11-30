import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author tony A thread safe version of IndexBuilder
 */
public class ThreadIndexBuilder extends InvertedIndexBuilder {

	/**
	 * WorkQueue for the class
	 */
	private final WorkQueue workQueue;

	/**
	 * The ThreadSafeInvertedIndex that is storing the data
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Constructor method
	 * 
	 * @param index     - The ThreadSafeInvertedIndex that is storing the data
	 * @param workQueue - the workQueue for the class
	 */
	public ThreadIndexBuilder(ThreadSafeInvertedIndex index, WorkQueue workQueue) {
		super(index);
		this.index = index;
		this.workQueue = workQueue;
	}
	
	@Override
	/**
	 * Non static DirectoryIterator method - Iterates over a directory to extract
	 * files and hands it as a task to WorkQueue
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void directoryIterator(Path path) throws FileNotFoundException, IOException {
		super.directoryIterator(path);
		try {
			workQueue.finish();
		} catch (InterruptedException e) {
			throw new IOException();
		}
	}
	

	/**
	 * Non static addPath method - Extracts information from the file passed and
	 * each word found in the file
	 * 
	 * @param path - the path to add to the inverted index
	 */
	@Override
	public void addPath(Path path){
		workQueue.execute(new task(path));
	}

	/**
	 * @author tony
	 *
	 *         Tasks for the WorkQueue
	 */
	private class task implements Runnable { // TODO Task
		/**
		 * The file task will read from
		 */
		private Path path;

		/**
		 * The index task will store information from its file in.
		 */

		/**
		 * The constructor method for task
		 * 
		 * @param path - the path to extract information from
		 */
		public task(Path path) {
			this.path = path;
		}

		@Override
		public void run() {
			try {
				InvertedIndex local = new InvertedIndex();
				addPath(path, local);
				index.addAll(local);
				 
			} catch (IOException e) {
				System.err.println("Invalid file found in path :" + path);
			}
		}

	}
}
