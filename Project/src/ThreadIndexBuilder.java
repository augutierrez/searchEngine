
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author tony A thread safe version of IndexBuilder
 */
public class ThreadIndexBuilder {

	/**
	 * WorkQueue for the class
	 */
	private final WorkQueue wq;

	/**
	 * The ThreadSafeInvertedIndex that is storing the data
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Constructor method
	 * 
	 * @param index      - The ThreadSafeInvertedIndex that is storing the data
	 * @param numThreads - the number of threads requested for WorkQueue
	 */
	public ThreadIndexBuilder(ThreadSafeInvertedIndex index, int numThreads) {
		this.index = index;
		wq = new WorkQueue(numThreads);
	}

	/**
	 * The stemmer used for the path's data
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Checks if a path is a text file
	 * 
	 * @param path - the path that's being considered
	 * @return true/ false
	 */
	public static boolean isText(Path path) {
		String lower = path.toString().toLowerCase();
		return lower.endsWith(".txt") || lower.endsWith(".text");
	}

	/**
	 * Calls directory Iterator to pass tasks to the WorkQueue
	 * 
	 * @param path       : the to find files from
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void directoryBuilder(Path path)
			throws FileNotFoundException, IOException, InterruptedException {
		directoryIterator(path);
		wq.finish();
	}

	/**
	 * Non static DirectoryIterator method - Iterates over a directory to extract
	 * files and hands it as a task to WorkQueue
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void directoryIterator(Path path) throws FileNotFoundException, IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				for (Path currPath : listing)
					directoryIterator(currPath);
			}
		} else {
			if (Files.isRegularFile(path) && isText(path)) {
				wq.execute(new task(path, index));
			}
		}
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
	public static class task implements Runnable {
		/**
		 * The file task will read from
		 */
		public Path path;

		/**
		 * The index task will store information from its file in.
		 */
		public final ThreadSafeInvertedIndex index;

		/**
		 * The constructor method for task
		 * 
		 * @param path
		 * @param index
		 */
		public task(Path path, ThreadSafeInvertedIndex index) {
			this.path = path;
			this.index = index;
		}

		@Override
		public void run() {
			try {
				addPath(path, index);
			} catch (IOException e) {
				System.err.println("Invalid file found in path :" + path);
			}
		}

	}
}
