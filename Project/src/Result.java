/**
 * @author tony This class will hold
 */
public class Result implements Comparable<Result> {
	private final String directory;
	private int count;
	private int totalWords;
	private float score;

	/**
	 * @param directory
	 * @param count
	 * @param score
	 */
	public Result(String directory, int count, int totalWords) {
		this.directory = directory;
		this.count = count;
		this.totalWords = totalWords;
		this.score = (float) count / totalWords;
	}

	/**
	 * @param count
	 */
	public void add(int count) {
		this.count += count;
		this.score = (float) this.count / this.totalWords;
	}

	public String getDirectory() {
		return this.directory;
	}

	public String getCount() {

		return Integer.toString(count);
	}

	public float getScore() {
		return this.score;
	}

	@Override
	public int compareTo(Result result) {
		// TODO Auto-generated method stub
		return Float.compare(result.getScore(), this.score);
	}
}


