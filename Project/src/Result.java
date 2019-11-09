/*
 * TODO Since each Result object depends on data from a specific instance of an
 * inverted index... it makes sense to move this class inside of inverted index
 * as a public non-static inner class.
 * 
 * Then this object can access map and wordCount directly. There is no longer any
 * need to store totalWords because where you need it you can do wordCount.get(directory)
 * 
	public void update(String word) {
		this.count += map.get(word).get(directory).size();
		this.score = (double) this.count / this.wordCount.get(directory);
	}
 */

/**
 * @author tony This class will hold
 */
public class Result implements Comparable<Result> {
	/**
	 * the location of the textfile for this result
	 */
	private final String directory; // TODO Refactor to location

	/**
	 * the amount of times our queries show up in this text file
	 */
	private int count;

	/**
	 * the total amount of words in the text file
	 */
	private int totalWords;

	/**
	 * the metric used to decide the importance of this text file for the search
	 * query
	 */
	private double score;

	/**
	 * Constructor method
	 * 
	 * @param directory  - the location of the text file
	 * @param count      - the amount of times our query shows up in the text file
	 * @param totalWords - the amount of total words for the text file
	 */
	public Result(String directory, int count, int totalWords) {
		// TODO Don't add the quotes here---will mess with our other stuff
		this.directory = '"' + directory + '"'; // added this so I can simplify SJW
		this.count = count;
		this.totalWords = totalWords;
		this.score = (double) count / totalWords;
	}
	
	/*
	 * TODO There is no super great way to output a custom object to JSON. So hard-code
	 * something for Result objects if you need.
	 */

	/**
	 * Updates the count for the queries in the text file, as well as the score of
	 * the result.
	 * 
	 * @param count - the amount of times a query shows up
	 */
	public void add(int count) {
		this.count += count;
		this.score = (double) this.count / this.totalWords;
	}

	/**
	 * Returns the location of the text file for this result
	 * 
	 * @return directory
	 */
	public String getDirectory() {
		return this.directory;
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

	@Override
	public String toString() {
		return "location: " + this.directory + " count: " + this.getCount() + " score: " + this.getScore();
	}
}


