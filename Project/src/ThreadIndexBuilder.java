import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class ThreadIndexBuilder extends InvertedIndexBuilder {

	/**
	 * The stemmer used for the path's data
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

//	private final ThreadSafeInvertedIndex index;

//	public ThreadIndexBuilder(ThreadSafeInvertedIndex index) {
//		this.index = index;
//	}

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

	public static void directoryBuilder(Path path, ThreadSafeInvertedIndex index, int numThreads)
			throws FileNotFoundException, IOException, InterruptedException {

		WorkQueue wq = new WorkQueue(numThreads);

		directoryIterator(path, index, wq);

		// ends the queue

//		try {
		wq.finish();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			Thread.currentThread().interrupt();
//		}
//		wq.shutdown();
	}

	/**
	 * Accepts a path, and iterates over it if it is a directory, or simply adds it
	 * to the data structure if it is a file.
	 * 
	 * @param path  : the path we will extract information from
	 * @param index : the data structure we are storing in
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void directoryIterator(Path path, ThreadSafeInvertedIndex index, WorkQueue wq)
			throws FileNotFoundException, IOException {
		if (path != null) {
			if (Files.isDirectory(path)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
					for (Path currPath : listing)
						directoryIterator(currPath, index, wq);
				}
			} else {
				// maybe add path should be calling wq execute with a runnable object that holds
				// the path .
				if (Files.isRegularFile(path) && isText(path)) {
					// addPath(path, index);
					wq.execute(new task(path, index));
				}
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

	public static class task implements Runnable {
		public Path path;
		public final ThreadSafeInvertedIndex index;

		public task(Path path, ThreadSafeInvertedIndex index) {
			this.path = path;
			this.index = index;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				addPath(path, index);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
