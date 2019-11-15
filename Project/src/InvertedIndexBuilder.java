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
	 * The InvertedIndex that will be utilized
	 */
	private final InvertedIndex index;

	/**
	 * Constructor method for InvertedIndexBuilder
	 * 
	 * @param index - the inverted index to build to.
	 */
	public InvertedIndexBuilder(InvertedIndex index) {
		this.index = index;
	}

	/**
	 * Non static DirectoryIterator method - Iterates over a directory to extract
	 * files
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
				addPath(path);
			}
		}
	}

	/**
	 * Non static addPath method - Extracts information from the file passed and
	 * each word found in the file
	 * 
	 * @param path - the path to add to the inverted index
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void addPath(Path path) throws FileNotFoundException, IOException {
		addPath(path, this.index);
	}

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
	 * Extracts information from the file passed and each word found in the file is
	 * passed to add()
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
}