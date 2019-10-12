/**
 * @author tony This class will hold
 */
public class Result implements Comparable<Result> {
	/**
	 * 
	 */
	private final String directory;
	/**
	 * 
	 */
	private int count;
	/**
	 * 
	 */
	private int totalWords;
	/**
	 * 
	 */
	private double score;

	/**
	 * @param directory
	 * @param count
	 * @param totalWords
	 */
	public Result(String directory, int count, int totalWords) {
		this.directory = directory;
		this.count = count;
		this.totalWords = totalWords;
		this.score = (double) count / totalWords;
	}

	/**
	 * @param count
	 */
	public void add(int count) {
		this.count += count;
		this.score = (double) this.count / this.totalWords;
	}

	/**
	 * @return a a
	 */
	public String getDirectory() {
		return this.directory;
	}

	/**
	 * @return a a
	 */
	public String getCount() {

		return Integer.toString(count);
	}

	/**
	 * @return a a
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


