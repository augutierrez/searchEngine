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
		int sCheck = Double.compare(result.getScore(), this.score);
		if (sCheck == 0) {
			int cCheck = Integer.compare(Integer.parseInt(result.getCount()), this.count);
			if (cCheck == 0) {
				int lCheck = this.getDirectory().compareTo(result.getDirectory());
				return lCheck;
			} else {
				return cCheck;
			}
		} else {
			return sCheck;
		}
	}

	@Override
	public String toString() {

		return "location: " + this.directory + " count: " + this.getCount() + " score: " + this.getScore();
	}
}


