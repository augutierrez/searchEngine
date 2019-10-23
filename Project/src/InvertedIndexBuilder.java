import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Used to help generate data for the InvertedIndex
 * 
 * @author tony
 */
public class InvertedIndexBuilder {

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
	 * Accepts a path, and iterates over it if it is a directory, or simply adds it
	 * to the data structure if it is a file.
	 * 
	 * @param path  : the path we will extract information from
	 * @param index : the data structure we are storing in
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void directoryIterator(Path path, InvertedIndex index) throws FileNotFoundException, IOException {
		if (path != null) {
			if (Files.isDirectory(path)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
					for (Path currPath : listing)
						directoryIterator(currPath, index);
				}
			} else {
				if (Files.isRegularFile(path) && isText(path)) {
					addPath(path, index);
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
	// Remind Sophie in project 2 code reviews to prep this class for project 3
}