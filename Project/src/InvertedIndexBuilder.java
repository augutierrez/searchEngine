import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author tony Used to help generate data for the InvertedIndex
 */
public class InvertedIndexBuilder {

	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Checks if a path is a text file
	 * 
	 * @param path - the path that's being considered
	 * @return true/ false
	 */
	public boolean isText(Path path) {
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
	public void directoryIterator(Path path, InvertedIndex index) throws FileNotFoundException, IOException {
		if (path != null) {
			if (Files.isDirectory(path)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
					for (Path currPath : listing)
						directoryIterator(currPath, index);
				}
			} else {
				if (Files.isRegularFile(path) && isText(path)) { // is this where I check isText()?
					addPath(path, index);
				}
			}
		}
	}

	/**
	 * Extracts information from the file passed and each word found in the file and
	 * passes it to add()
	 * 
	 * @param path : the path to the file that it will read from
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void addPath(Path path, InvertedIndex index) throws FileNotFoundException, IOException {

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			String pathName = path.toString();
			int counter = 1;
			Stemmer stemmer = new SnowballStemmer(DEFAULT);
			while ((line = reader.readLine()) != null) {
//				String[] wordsInLine = TextParser.parse(line);
				CharSequence wordsInLine = stemmer.stem(line);
				System.out.println("AFTER STEM");
				System.out.println(wordsInLine);
//				TreeSet<String> set = ;

				for (String word : TextFileStemmer.uniqueStems(line)) { // is there a difference in efficiency between
																		// set and directly
													// putting
											// TextFileStem
					/*
					 * TODO uniqueStems is creating 1 stemmer per line Create a stemmer before the
					 * while loop and reuse it.
					 * 
					 * Places words into another data structure, which then must be moved into the
					 * inverted index.
					 * 
					 * As soon as you have a stemmed word, add it to the index only.
					 */
//					TreeSet<String> set = TextFileStemmer.uniqueStems(word);
//					word = set.first();
					// TODO path.toString() is called for every single word... value never
					// changes... save it in a variable before the while loop and reuse

					index.add(word, pathName, counter);
					counter++;
				}
				// wordCount.put(path.toString(), counter - 1); // TODO Remove
			}
		}

	}
	
	// TODO Remind Sophie in project 2 code reviews to prep this class for project 3

}
