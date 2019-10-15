import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Antonio Gutierrez
 */
public class InvertedIndex {
	
	/**
	 * Nested Data Structure that stores all the words
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> map;

	/**
	 * Used for counts flag
	 */
	private final TreeMap<String, Integer> wordCount;

	/**
	 * Constructor
	 */
	public InvertedIndex() {
		map = new TreeMap<>();
		wordCount = new TreeMap<>();
	}
	
	/**
	 * Receives words from addPath() and stores it in the map data structure
	 * 
	 * @param word     : the words that must be added
	 * @param path     : the path where the word was found
	 * @param position : the line the word was found in the file
	 */
	public void add(String word, String path, int position) {
		map.putIfAbsent(word, new TreeMap<>());
		map.get(word).putIfAbsent(path, new TreeSet<>());
		map.get(word).get(path).add(position);
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
		if (FileReader.checkText(path)) {
			try (BufferedReader reader = Files.newBufferedReader(path)) {
				String line;
				int counter = 1;
				while ((line = reader.readLine()) != null) {
					String[] wordsInLine = TextParser.parse(line);

					for (String word : wordsInLine) {
						TreeSet<String> set = TextFileStemmer.uniqueStems(word);
						word = set.first();
						add(word, path.toString(), counter);
						counter++;
					}
					wordCount.put(path.toString(), counter - 1);
				}
			}
		}
	}

	/**
	 * Creates a writer for the -counts flag and outputs to the file passed
	 * 
	 * @param path : path of output file
	 * @throws IOException
	 */
	public void countsWriter(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);) {
			SimpleJsonWriter.asObject(wordCount, path);
		}
	}

	/**
	 * Creates a writer for the -index flag and outputs to the file passed
	 * 
	 * @param path : path of the output file
	 * @throws IOException
	 */
	public void indexWriter(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.asNestedObjectInNestedObject(map, path);
		}

	}

	/**
	 * @param word - the word to look up
	 * @return true/false
	 */
	public boolean contains(String word) {
		return map.containsKey(word);
	}

	/**
	 * @param word     - the word associated with the location
	 * @param location - the location we are confirming exists
	 * @return true/false
	 */
	public boolean contains(String word, String location) {
		return map.containsKey(word) && map.get(word).containsKey(location);
	}

	/**
	 * @param word     - the word that is associated with the position
	 * @param location - the location that is associated with the position
	 * @param position - the position we are confirming exists
	 * @return true/false
	 */
	public boolean contains(String word, String location, int position) {
		return map.containsKey(word) && map.get(word).containsKey(location)
				&& map.get(word).get(location).contains(position);
	}

	/**
	 * @return a set of the words in the map
	 */
	public Set<String> getWords() {
		return map.keySet();
	}

	/**
	 * @param word - the word associated with the set of locations
	 * @return the set of locations requested
	 */
	public Set<String> getLocations(String word) {
		if (map.containsKey(word))
			return map.get(word).keySet();
		return null;
	}

	/**
	 * @param word     - the word associated with the set
	 * @param location - the location associated with the set
	 * @return the set requested
	 */
	public Set<Integer> getPositions(String word, String location) {
		if (map.containsKey(word)) {
			if (map.get(word).containsKey(location))
				return map.get(word).get(location);
		}
		return null;
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
}
