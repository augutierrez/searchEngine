/**
 * @author tony This class will hold
 */
public class Result implements Comparable<Result> {
	private final String directory;
	private int count;
	private int totalWords;
	private double score;

	/**
	 * @param directory
	 * @param count
	 * @param score
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

	public String getDirectory() {
		return this.directory;
	}

	public String getCount() {

		return Integer.toString(count);
	}

	public double getScore() {
		return this.score;
	}

	@Override
	public int compareTo(Result result) {
		// TODO Auto-generated method stub
		// add comparing of counts and location as well.
		// we want it in descending order
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
			return sCheck * -1;
		}
	}

	@Override
	public String toString() {

		return "location: " + this.directory + " count: " + this.getCount() + " score: " + this.getScore();
	}
}


