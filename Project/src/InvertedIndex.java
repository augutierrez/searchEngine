import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * @author Antonio Gutierrez
 */
public class InvertedIndex {
	/**
	 *Nested Data Structure that stores all the -path data
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> map;

	/**
	 * Structure used to store data for counts (location,counts)
	 */
	private final TreeMap<String, Integer> wordCount;
	
	
	/**
	 * Constructor method
	 */
	public InvertedIndex() {
		map = new TreeMap<>();
		wordCount = new TreeMap<>();
	}
	
	/**
	 * 
	 */
//	HashMap<String, HashSet<Result>> querySet = new HashMap<>();

	// the data structure that will get printed:
	TreeMap<String, ArrayList<Result>> readyToPrint = new TreeMap<>();


	/**
	 * 
	 * @param set
	 * @param type
	 */

	public void generate(TreeSet<String> set, String type) {
		StringBuffer buffer = new StringBuffer();
		if (!set.isEmpty()) {
			for (String word : set) {
				buffer.append(word);
				buffer.append(' ');
			}
			// deletes extra space
			buffer.deleteCharAt(buffer.length() - 1);
			readyToPrint.put(buffer.toString(), generateResults(set, type));
		}
	}

	/**
	 * @param set
	 * @return A list with words- ----
	 * 
	 */
	public TreeSet<String> partialSearch(TreeSet<String> set) {

		TreeSet<String> returnSet = new TreeSet<>();
		Iterator<String> stems = set.iterator();

		while (stems.hasNext()) {
			Iterator<String> iterate = map.keySet().iterator();
			String stem = stems.next();
			while (iterate.hasNext()) {
				String key = iterate.next();
				if (key.startsWith(stem))
					returnSet.add(key);
			}
		}

		return returnSet;
	}

	/**
	 * @param set
	 * @param type
	 * @return a a
	 */
	public ArrayList<Result> generateResults(TreeSet<String> set, String type) {

		if (type.equals("partial")) {
			set.addAll(partialSearch(set));
		}
		ArrayList<Result> query = new ArrayList<>();
		for (String word : set) {
			// traversing the maps within our map

			// searching for exact word
			if (map.containsKey(word)) {
				Result result;

				for (Map.Entry<String, TreeSet<Integer>> entry1 : map.get(word).entrySet()) {
					String location = entry1.getKey();
					// Files file = Files.createFile(location);
					int counts = entry1.getValue().size();
					int totalWords = wordCount.get(location);
					// if we have this result already, then update it
					boolean contains = false;
					for (Result tempResult : query) {
						if (tempResult.getDirectory().equals(location)) {
							contains = true;
							tempResult.add(counts);
							break;
						}
					}
					if (!contains) {
						result = new Result(location, counts, totalWords);
						query.add(result);
					}
				}
			}
		}
		Collections.sort(query);
		return query;

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
		if (!wordCount.containsKey(path) || wordCount.get(path) < position) {
			wordCount.put(path, position);
		}

	}

	/**
	 * Creates a writer for the -counts flag and outputs to the file passed
	 * 
	 * @param path : the path we are going to write to
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
	 * @param path : name of the output file
	 * @throws IOException
	 */
	public void indexWriter(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.asNestedObjectInNestedObject(map, path);
		}
	}

	/**
	 * @param path
	 * @throws IOException
	 */
	public void queryWriter(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.searchOutput(readyToPrint, path);
		}
	}

	/**
	 * Returns whether the Inverted Index contains the word
	 * 
	 * @param word - the word to look up
	 * @return true/false
	 */
	public boolean contains(String word) {
		return map.containsKey(word);
	}

	/**
	 * Returns whether the InvertedIndex contains a location for a specific word
	 * 
	 * @param word     - the word associated with the location
	 * @param location - the location we are confirming exists
	 * @return true/false
	 */
	public boolean contains(String word, String location) {
		return map.containsKey(word) && map.get(word).containsKey(location);
	}

	/**
	 * Returns whether the InvertedIndex contains a positions for a specific
	 * location of a word
	 * 
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
	 * Returns an unmodifiable set of the InvertedIndex's words
	 * 
	 * @return a set of the words in the map
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(map.keySet());
	}

	/**
	 * Returns an unmodifiable set of the InvertedIndex's locations
	 * 
	 * @param word - the word associated with the set of locations
	 * @return the set of locations requested
	 */
	public Set<String> getLocations(String word) {
		if (map.containsKey(word)) {
			return Collections.unmodifiableSet(map.get(word).keySet());
		}
		return Collections.emptySet();
	}

	/**
	 * Returns an unmodifiable set of the InvertedIndex's positions
	 * 
	 * @param word     - the word associated with the set
	 * @param location - the location associated with the set
	 * @return the set requested
	 */
	public Set<Integer> getPositions(String word, String location) {
		if (map.containsKey(word)) {
			if (map.get(word).containsKey(location)) {
				return Collections.unmodifiableSet(map.get(word).get(location));
			}
		}
		return Collections.emptySet();
	}

	@Override
	public String toString() {
		return map.toString();
	}

	/**
	 * @param location
	 * @return
	 */
	public int getWordCounts(String location) {
		return wordCount.get(location);
	}

}
