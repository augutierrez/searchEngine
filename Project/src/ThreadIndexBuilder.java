
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author tony A thread safe version of IndexBuilder
 */
public class ThreadIndexBuilder extends InvertedIndexBuilder { // TODO extends IndexBuilder

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
	
	// TODO Remove the overlap that doesn't change


//
//	/**
//	 * Calls directory Iterator to pass tasks to the WorkQueue
//	 * 
//	 * @param path       : the to find files from
//	 * @throws FileNotFoundException
//	 * @throws IOException
//	 * @throws InterruptedException
//	 */
//	public void directoryBuilder(Path path) // TODO Remove
//			throws FileNotFoundException, IOException, InterruptedException {
//		super.directoryIterator(path);
//		workQueue.finish();
//	}

	// TODO Override
	/**
	 * Non static DirectoryIterator method - Iterates over a directory to extract
	 * files and hands it as a task to WorkQueue
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Override
	public void directoryIterator(Path path) throws FileNotFoundException, IOException {
		super.directoryIterator(path);
		try {
			workQueue.finish();
		} catch (InterruptedException e) {
			throw new IOException();
		}
	}
	
	@Override
	public void addPath(Path path) {
		workQueue.execute(new task(path));
	}

	/**
	 * Extracts information from the file passed and each word found in the file and
	 * passes it to add()
	 * 
	 * @param path  : the path to the file that it will read from
	 * @param index : the InvertedIndex that will store the path's data
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 */
	public static void addPath(Path path, InvertedIndex index) throws FileNotFoundException, IOException {

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			String pathName = path.toString();
			int counter = 1;
			Stemmer stemmer = new SnowballStemmer(DEFAULT);
			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					index.add(stemmer.stem(word).toString(), pathName, counter++);
				}
			}
		}

	}

	/**
	 * @author tony
	 *
	 *         Tasks for the WorkQueue
	 */
	private class task implements Runnable {
		/**
		 * The file task will read from
		 */
		private Path path;

		/**
		 * The constructor method for task
		 * 
		 * @param path
		 * @param index
		 */
		public task(Path path) {
			this.path = path;
		}

		@Override
		public void run() {
			try {
				InvertedIndex local = new InvertedIndex();
				addPath(path, local);
				System.out.println("HERE!");
				index.addAll(local);
				System.out.println("HERE");
				
				/*
				 * TODO 
				InvertedIndex local = new InvertedIndex();
				addPath(path, local);
				index.addAll(local); <--- will block, but for little time and infrequently
				 */
			} catch (IOException e) {
				System.err.println("Invalid file found in path :" + path);
			}
		}

	}
}
