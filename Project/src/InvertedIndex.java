import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Antonio Gutierrez
 */
public class InvertedIndex {

	/**
	 * Nested Data Structure that stores all the -path data
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
	 * Adds all the information from the given index.
	 * 
	 * @param index - the index whose information will be added
	 */
	public void addAll(InvertedIndex index) {
		Set<String> wordSet = index.map.keySet();
		Iterator<String> wordIterator = wordSet.iterator();
		while (wordIterator.hasNext()) {
			String word = wordIterator.next();
			if (!this.map.containsKey(word)) {
				this.map.put(word, index.map.get(word));
			} else {
				Set<String> locationSet = index.map.get(word).keySet();
				Iterator<String> locationIterator = locationSet.iterator();
				while (locationIterator.hasNext()) {
					String location = locationIterator.next();
					if (!this.map.get(word).containsKey(location)) {
						this.map.get(word).put(location, index.map.get(word).get(location));
					} else {
						this.map.get(word).get(location).addAll(index.map.get(word).get(location));
					}
				}
			}
		}
		for (String location : index.wordCount.keySet()) {
			if (!wordCount.containsKey(location) || wordCount.get(location) < index.getWordCounts(location)) {
				wordCount.put(location, index.wordCount.get(location));
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

	/**
	 * Get the counts for a text file in a specific location
	 * 
	 * @param location - the location we want the counts of
	 * @return the counts of the specified location
	 */
	public Integer getWordCounts(String location) {
		if(wordCount.containsKey(location)){
			return wordCount.get(location);
		}
		return null;
	}
	
	/**
	 * Takes the set and performs a partial search. Words that start with each query
	 * word will be matched as well.
	 * 
	 * @param queries - the set of queries
	 * @return the same set with newly added queries for partial search
	 */
	public ArrayList<InvertedIndex.Result> partialSearch(Set<String> queries) {
		HashMap<String, InvertedIndex.Result> lookUp = new HashMap<>();
		ArrayList<InvertedIndex.Result> results = new ArrayList<>();

		for (String query : queries) {
			for (String word : map.tailMap(query).keySet()) {
				if (word.startsWith(query)) {
					search(word, results, lookUp);
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Creates a list of results based off the queries passed to it and the type of
	 * search.
	 * 
	 * @param queries - set of queries
	 * @return a list of results
	 */
	public ArrayList<InvertedIndex.Result> exactSearch(Set<String> queries) {
		// stores results in order to access them faster
		HashMap<String, InvertedIndex.Result> lookUp = new HashMap<>();
		ArrayList<InvertedIndex.Result> results = new ArrayList<>();

		for (String word : queries) {
			if (map.containsKey(word)) {
				search(word, results, lookUp);
			}
		}
		Collections.sort(results);
		return results;
	}
	
	/**
	 * Searches for the word passed and adds it to the list of results if it's a new
	 * search
	 * 
	 * @param word    - the word to search
	 * @param results - the list of current results
	 * @param lookUp  - a map of current results
	 */
	private void search(String word, ArrayList<Result> results, HashMap<String, InvertedIndex.Result> lookUp) {
		for (String location : this.map.get(word).keySet()) {
			if (lookUp.containsKey(location)) {
				lookUp.get(location).update(word);
			} else {
				Result result = this.new Result(location, word);
				results.add(result);
				lookUp.put(location, result);
			}
		}
	}
	
	/**
	 * Helper method that calls on partialSearch() or exactSearch()
	 * 
	 * @param queries - the queries to search
	 * @param partial - whether to call partial
	 * @return - a list of results
	 */
	public ArrayList<Result> generateSearch(Set<String> queries, boolean partial) {
		if (partial) {
			return partialSearch(queries);
		} else {
			return exactSearch(queries);
		}
	}

	@Override
	public String toString() {
		return map.toString();
	}

	/**
	 * Search Result Objects that help score matches
	 * 
	 * @author tony
	 */
	public class Result implements Comparable<Result> {
		/**
		 * the location of the text file for this result
		 */
		private final String location;

		/**
		 * the amount of times our queries show up in this text file
		 */
		private int count;

		/**
		 * the metric used to decide the importance of this text file for the search
		 * query
		 */
		private double score;

		/**
		 * Result constructor method
		 * 
		 * @param location - the location of the text file
		 * @param word     - the word for the result
		 */
		public Result(String location, String word) {
			this.location = location;
			this.update(word);
		}

		@Override
		public int compareTo(Result result) {
			int sCheck = Double.compare(this.score, result.getScore());

			if (sCheck == 0) {
				int cCheck = Integer.compare(this.count, Integer.parseInt(result.getCount()));
				if (cCheck == 0) {
					int lCheck = this.getDirectory().compareToIgnoreCase(result.getDirectory());
					return lCheck;
				} else {
					return cCheck * -1;
				}
			} else {
				return (sCheck * -1);
			}
		}

		/**
		 * Updates the count and score for a result object
		 * 
		 * @param word - the query word that will update the score for this location
		 */
		private void update(String word) {
			this.count += map.get(word).get(this.location).size();
			this.score = (double) this.count / wordCount.get(location);
		}

		/**
		 * Returns the location of the text file for this result
		 * 
		 * @return directory
		 */
		public String getDirectory() {
			return this.location;
		}

		/**
		 * Returns the amount of times the query shows up in the text file
		 * 
		 * @return count
		 */
		public String getCount() {

			return Integer.toString(count);
		}

		/**
		 * Returns the score for the text file
		 * 
		 * @return score
		 */
		public double getScore() {
			return this.score;
		}

		@Override
		public String toString() {
			return "location: " + this.location + " count: " + this.getCount() + " score: " + this.getScore();
		}

	}
}
