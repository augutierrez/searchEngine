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
	 * Takes the set and adds stems that start with the stems inside it for partial
	 * search.
	 * 
	 * @param set - the set of queries
	 * @return the same set with newly added queries for partial search
	 */
	public TreeSet<String> partialSearch(TreeSet<String> set) {

		TreeSet<String> returnSet = new TreeSet<>();
		Iterator<String> stems = set.iterator();

		/*
		 * TODO This is a linear search, so usually a better way...
		 */
		while (stems.hasNext()) {
			Iterator<String> iterate = this.getWords().iterator();
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
	 * Creates a list of results based off the queries passed to it and the type of
	 * search.
	 * 
	 * @param set     - set of queries
	 * @param partial : whether or not to perform partial search
	 * @return a list of results
	 */
	public ArrayList<InvertedIndex.Result> generateResults(TreeSet<String> set, boolean partial) {
		HashMap<String, InvertedIndex.Result> lookup = new HashMap<>();

		if (partial) {
			set.addAll(partialSearch(set));
		}

		ArrayList<InvertedIndex.Result> query = new ArrayList<>();
		for (String word : set) {
			if (this.contains(word)) {
				InvertedIndex.Result result;

				for (String location : this.getLocations(word)) {
					int counts = this.getPositions(word, location).size();
					// if we have this result already, then update it
					if (lookup.containsKey(location)) {
						lookup.get(location).update(word);
					}
					else {
						result = this.new Result(location, counts);
						query.add(result);
						lookup.put(location, result);
					}
				}
			}
		}
		Collections.sort(query);
		return query;
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
		private String location;

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
		 * @param location - the location of the text file
		 * @param count    - the number of times the query word shows up in the text
		 */
		public Result(String location, int count) {
			this.location = location;
			this.count = count;
			this.score = (double) this.count / wordCount.get(location);
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
		public void update(String word) {
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
