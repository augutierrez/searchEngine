import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
//	public TreeSet<String> partialSearch(Set<String> set) { // TODO Remove, no copies
//		TreeSet<String> returnSet = new TreeSet<>();
//		Iterator<String> stems = set.iterator();
//		while (stems.hasNext()) {
//			String stem = stems.next();
//			Iterator<String> tailMapIterate = map.tailMap(stem).keySet().iterator();
//			String word;
//			while (tailMapIterate.hasNext()) {
//				word = tailMapIterate.next();
//				if (word.startsWith(stem)) {
//					returnSet.add(word);
//				} else {
//					break;
//				}
//			}
//		}
//		return returnSet;
//	}
	public ArrayList<InvertedIndex.Result> partialSearch(Set<String> queries) {
		HashMap<String, InvertedIndex.Result> lookup = new HashMap<>();
		ArrayList<InvertedIndex.Result> results = new ArrayList<>();

		for (String query : queries) {
			for (String word : map.tailMap(query).keySet()) {
				if (word.startsWith(query)) {
					// then do stuff!
					queries.add(word);
				} else {
					break;
				}
			}
		}

		return results;
	}
	
	/* TODO partial search

HashMap<String, InvertedIndex.Result> lookup = new HashMap<>();
ArrayList<InvertedIndex.Result> results = new ArrayList<>();

for (String query : queries) {
	for (String word : map.tailMap(stem).keySet()) {
		if (word.startsWith(query)) {
			then do stuff!
		}
		else {
			break;
		}
	}
}
	 */


	/**
	 * Creates a list of results based off the queries passed to it and the type of
	 * search.
	 * 
	 * @param set     - set of queries
	 * @param partial : whether or not to perform partial search
	 * @return a list of results
	 */
	// TODO public ArrayList<InvertedIndex.Result> generateResults(Set<String> set, boolean partial) {
	public ArrayList<InvertedIndex.Result> generateResults(Set<String> set, boolean partial) {
		// stores results in order to access them faster
		HashMap<String, InvertedIndex.Result> lookup = new HashMap<>();

		if (partial) {
			set.addAll(partialSearch(set));
		}

		/*
		 * TODO Break out exact search and partial search logic.
		 */
		
		ArrayList<InvertedIndex.Result> query = new ArrayList<>();
		for (String word : set) {
			if (this.contains(word)) {
				InvertedIndex.Result result;

				for (String location : this.getLocations(word)) {
					// if we have this result already, then update it
					if (lookup.containsKey(location)) {
						lookup.get(location).update(word);
					}
					else {
						result = this.new Result(location, word);
						query.add(result);
						lookup.put(location, result);
					}
				}
			}
		}
		Collections.sort(query);
		return query;
	}
	
	/**
	 * Searches for the word passed and adds it to the list of results if it's a new
	 * search
	 * 
	 * @param word      - the word to search
	 * @param results   - the list of current results
	 * @param lookUpMap - a map of current results
	 * @return
	 */
	private Result Search(String word, ArrayList<InvertedIndex.Result> results,
			HashMap<String, InvertedIndex.Result> lookUpMap) {
		for (String location : this.map.get(word).keySet()) {
			if (lookup.containsKey(location)) {
				lookup.get(location).update(word);
			} else {
				result = this.new Result(location, word);
				query.add(result);
				lookup.put(location, result);
			}
		}
	}
	// TODO LOOK UP MAP CAN BE GLOBAL
/* TODO Create a search helper method (private)

				for (String location : this.map.get(word).keyset()) {
					if (lookup.containsKey(location)) {
						lookup.get(location).update(word);
					}
					else {
						result = this.new Result(location, word);
						query.add(result);
						lookup.put(location, result);
					}
				}

 */
	

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
