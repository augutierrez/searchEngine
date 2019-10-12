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
		// TODO Auto-generated method stub
		// add comparing of counts and location as well.
		// we want it in descending order

		System.out.println("This " + this.toString());
		System.out.println("That " + result.toString());
		int sCheck = Double.compare(this.score, result.getScore());
		System.out.println("sCheck: " + sCheck);

		if (sCheck == 0) {
			int cCheck = Integer.compare(this.count, Integer.parseInt(result.getCount()));
			System.out.println("cCheck: " + cCheck);
			if (cCheck == 0) {
				int lCheck = this.getDirectory().compareToIgnoreCase(result.getDirectory());
				System.out.println("lCheck: " + lCheck);
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


