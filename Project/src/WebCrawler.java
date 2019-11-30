import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class that interacts with an InvertedIndex to store data from webpages
 * 
 * @author tony
 *
 */
public class WebCrawler {

	/**
	 * The stemmer used for the path's data
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	private final WorkQueue workQueue;

	private final ThreadSafeInvertedIndex index;

	private final int numLinks;

	private final ArrayList<URL> urlList;
	


	/**
	 * Constructor method for WebCrawler
	 * 
	 * @param workQueue - the WorkQueue for the class
	 * @param numLinks  - the limit of redirects requested
	 */
	public WebCrawler(ThreadSafeInvertedIndex index, WorkQueue workQueue, int numLinks) {
		this.index = index;
		this.workQueue = workQueue;
		this.numLinks = numLinks;
		urlList = new ArrayList<URL>();
	}

	/**
	 * @param url
	 * @throws InterruptedException
	 * @throws MalformedURLException
	 */
	public void crawl(String url) throws InterruptedException, MalformedURLException {
		URL cleaned = LinkParser.clean(new URL(url));
		crawl(cleaned);

		workQueue.finish();
	}

	public void crawl(URL url) throws MalformedURLException {
		//like directory iterator but for urls
		/*
		 * I want to learn what this is doing better, so we are going to mess around
		 * with it to see what its doing Try to take out the execute component and just
		 * printout all the sites, see if the ones we are need is found if they are,
		 * this means its prob not an issue with regex
		 * 
		 * URL lists during execute?
		 */
		boolean add = false;
		boolean maxedOut = false;
		synchronized (urlList) {
			System.out.println("Made it here: " + url.toString());
			if (urlList.size() < numLinks && !urlList.contains(url)) {
				urlList.add(url);
				add = true;
			}
			if (urlList.size() == numLinks) {
				maxedOut = true;
			}
		}
		if (add) {
			storeHtml(url);
		}
		if (maxedOut) {
			return;
		}
		// call here
		String html = HtmlFetcher.fetch(url, 3);
		if (html == null) {
			return;
		}
		// need to edit out unecessary stuff ( head tags)
//		synchronized (workQueue) {
		for (URL tempUrl : LinkParser.listLinks(url, html)) {
			URL tempCleaned = LinkParser.clean(tempUrl);
			boolean contain = false;
			synchronized (urlList) {

				if (urlList.contains(tempCleaned)) {
					contain = true;
				}
				}
			System.out.println("URL : " + tempCleaned.toString() + " is in list : " + contain);
			if (!contain && urlList.size() < numLinks) {
				storeHtml(tempCleaned);
				urlList.add(tempCleaned);
//					crawl(tempCleaned);
				}
			}
//		}
		// check that we don't go over limit, also might want to just call execute right
		// away - that way it starts working until we find more matches, just use array
		// list as way to keep record
	}

	public void storeHtml(URL url) {
		workQueue.execute(new Task(url));
	}

	/**
	 * Adds all the content from the text content form the website onto the
	 * InvertedIndex
	 * 
	 * @param url   - the url of the website
	 * @param html  - the html text content
	 * @param index - the InvertedIndex used to store the text
	 */
	public static void addHtml(String url, String html, ThreadSafeInvertedIndex index) {
		int counter = 1;
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		for (String word : TextParser.parse(html)) {
			index.add(stemmer.stem(word).toString(), url, counter++);
		}
	}


	/*
	 * We basically need to get to the point where we extract the words from the
	 * webpages. Then we pass it to add() with a word, the string url (seed) and the
	 * position (index of the word on the page, starts at 1)
	 */

	private class Task implements Runnable {
		/**
		 * The file task will read from
		 */
		private URL url;

		/**
		 * The index task will store information from its file in.
		 */


		/**
		 * The constructor method for task
		 * 
		 * @param url  - the url from a website
		 * @param html - the text from the website that will be stored in the
		 *             InvertedIndex
		 */
		public Task(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			String html = HtmlFetcher.fetch(url, 3);
			if (html == null) {
				return;
			}
			String cleanedHtml = HtmlCleaner.stripHtml(html);

			// multithreading - perhaps overwrite fetch to create a new
			// if html is null return

			int counter = 1;
			Stemmer stemmer = new SnowballStemmer(DEFAULT);
			synchronized (index) {
				for (String word : TextParser.parse(cleanedHtml)) {
					// maybe do local index and then addall
					index.add(stemmer.stem(word).toString(), url.toString(), counter++);
				}
			}
			try {
				crawl(url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				return;
			}
		}
	}

}
