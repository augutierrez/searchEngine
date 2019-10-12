import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * TODO Keep storage and string/directory/file parsing separate.
 * 
 * Move anything that parses string/directory/files into a builder or generator class.
 */

/**
 * @author Antonio Gutierrez
 */
public class InvertedIndex {
	/*
	 * TODO Always use public or private, and other keywords, when making members.
	 * 
	 * Both map and word count should be private final
	 * 
	 * And should be initialized in the constructor
	 */
	
	/**
	 * Nested Data Structure that stores all the words
	 */
	TreeMap<String, TreeMap<String, TreeSet<Integer>>> map = new TreeMap<>();

	/**
	 * used for counts flag
	 */
	TreeMap<String, Integer> wordCount = new TreeMap<>();
	
	/**
	 * Receives words from addPath() and stores it in the map data structure
	 * 
	 * @param word     : the words that must be added
	 * @param path     : the path where the word was found
	 * @param position : the line the word was found in the file
	 */
	public void add(String word, String path, int position) {
		/*
		 * TODO Stemming in your add method makes this class much less general.
		 */
		TreeSet<String> set = TextFileStemmer.uniqueStems(word);
		word = set.first();
		if (!map.containsKey(word)) {

			TreeMap<String, TreeSet<Integer>> tempMap = new TreeMap<>();
			map.put(word, tempMap);

			map.put(word, tempMap);
		}
		if (!map.get(word).containsKey(path)) {

			TreeSet<Integer> tempSet = new TreeSet<>();
			map.get(word).put(path, tempSet);

		}
		map.get(word).get(path).add(position);
		
		/* TODO Simplify just a bit more
		map.putIfAbsent(word, new TreeMap<>());
		map.get(word).putIfAbsent(path, new TreeSet<>());
		map.get(word).get(path).add(position);
		*/

	}

	/**
	 * Extracts information from the file passed and each word found in the file and
	 * passes it to add()
	 * 
	 * @param path : the path to the file that it will read from
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void addPath(Path path) throws FileNotFoundException, IOException {
		// TODO Checking if something is a text file should be a static method
		if (path.toString().toLowerCase().endsWith(".txt") || path.toString().toLowerCase().endsWith(".text")) {
			// TODO Files.newBufferedReader(...)
			try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {

			String line;

			int counter = 1;
			while ((line = reader.readLine()) != null) {
				String[] wordsInLine = TextParser.parse(line);

				for (String word : wordsInLine) {
					add(word, path.toString(), counter);
					counter++;
				}
				wordCount.put(path.toString(), counter - 1);
			}
			}
		}
	}

	/**
	 * Accepts a path, and iterates over it if it is a directory, or simply adds it
	 * to the data structure if it is a file.
	 * 
	 * @param path : the path we will extract information from
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void directoryIterator(Path path) throws FileNotFoundException, IOException {
		if (path != null) {
			if (Files.isDirectory(path)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
					for (Path currPath : listing)
						directoryIterator(currPath);
				}
			} else {
				if (Files.isRegularFile(path))
					addPath(path);
			}
		}
	}

	/**
	 * Creates a writer for the -counts flag and outputs to the file passed
	 * 
	 * @param name : name of output file
	 * @throws IOException
	 */
	public void countsWriter(String name) throws IOException {

		File file = new File(name);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));) {
			SimpleJsonWriter.asObject(wordCount, Paths.get(file.toString()));
		}

	}

	/**
	 * Creates a writer for the -index flag and outputs to the file passed
	 * 
	 * @param name : name of the output file
	 * @throws IOException
	 */
	public void indexWriter(String name) throws IOException {

		File file = new File(name); // TODO NO MORE FILE!

		// TODO Files.newBufferedWriter(path, StandardCharsets.UTF8)
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			SimpleJsonWriter.asNestedObjectInNestedObject(map, Paths.get(file.toString()));
		}

	}

	/**
	 * Reads the file passed
	 * 
	 * @param file : the file it will read
	 * @throws IOException
	 */
	public void readFile(Path file) throws IOException {
		// TODO index.json should only appear in Driver
		try (BufferedReader br = Files.newBufferedReader(Paths.get("index.json"))) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		}
	}
	
	/*
	 * TODO Add more methods so developers have more ways of safely getting the
	 * data in your index without having to always dump it to file.
	 * 
	 * public boolean contains(String word)
	 * public boolean contains(String word, String location)
	 * public boolean contains(String word, String location, int position)
	 * 
	 * public Set<String> getWords()
	 * public Set<String> getLocations(String word) unmodifiable version of map.get(word).keySet()
	 * public Set<Integer> getPositions(String word, String location)
	 * 
	 * toString()
	 */

}
