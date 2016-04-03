import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BuildGraph {
	private static String averageDegree = "0.00"; // store previous value and
													// initialize it to 0.00
	static timestampSort comparator = new timestampSort();
	private static Map<String, Set<String>> edgeList = new HashMap<>();
	private static Queue<Tweet> tweetList = new PriorityQueue<Tweet>(comparator);

	class Tweet {
		String timeStamp;
		Set<String> hashTagSet;

		public Tweet(String timeStamp, Set<String> hashTagSet) {
			this.timeStamp = timeStamp;
			this.hashTagSet = hashTagSet;
		}
	}

	public void drawGraph(String timeStamp, Set<String> hashTagSet, PrintWriter printWriter) {
		if (!tweetList.isEmpty()) {
			// compare timestamps of incoming tweet with the oldest tweet
			long timeDifference = timeComparison(timeStamp, tweetList.peek().timeStamp);
			if (timeDifference >= 0) {
				processInWindowTweet(timeStamp, hashTagSet, printWriter, timeDifference);
			} else {
				// write previous degree value directly
				printWriter.println(averageDegree);
			}
		} else {
			// update the graph and the tweetlist with the incoming tweet
			updateOutput(timeStamp, hashTagSet, printWriter);
		}
	}

	public void processInWindowTweet(String timeStamp, Set<String> hashTagSet, PrintWriter printWriter,
			long timeDifference) {
		// if the timestamp of incoming tweet is beyond the current window
		if (timeDifference >= 60) {
			List<Tweet> temp = new ArrayList<>();
			// evict all the outdated tweets and save them into a list
			while (timeDifference >= 60) {
				temp.add(tweetList.poll());
				if (tweetList.isEmpty()) {
					break;
				}
				timeDifference = timeComparison(timeStamp, tweetList.peek().timeStamp);
			}
			removeEdge(temp); // update the graph by removing all the outdated
								// edges
		}
		updateOutput(timeStamp, hashTagSet, printWriter);
	}

	public void removeEdge(List<Tweet> outdatedTweets) {
		for (Tweet t : outdatedTweets) {
			for (String hashTag : t.hashTagSet) {
				if (edgeList.containsKey(hashTag)) {
					edgeList.get(hashTag).removeAll(t.hashTagSet);
					// check if the graph contains disconnected nodes
					if (edgeList.get(hashTag).isEmpty()) {
						edgeList.remove(hashTag);
					}
				}
			}
		}
	}

	public void updateOutput(String timeStamp, Set<String> hashTagSet, PrintWriter printWriter) {
		tweetList.add(new Tweet(timeStamp, hashTagSet));
		// no graph update if the tweet doesn't have at least two hashtags
		if (hashTagSet.size() < 2) {
			// write previous degree value directly
			printWriter.println(averageDegree);
		} else {
			addEdge(hashTagSet);
			averageDegree = degreeCalculation(edgeList);
			printWriter.println(averageDegree);
		}
	}

	public void addEdge(Set<String> hashTagSet) {
		Iterator<String> it = hashTagSet.iterator();
		while (it.hasNext()) {
			String ht = it.next();
			List<String> temp = new ArrayList<String>();
			temp.addAll(hashTagSet);
			temp.remove(ht);
			if (edgeList.containsKey(ht)) {
				edgeList.get(ht).addAll(temp);
			} else {
				edgeList.put(ht, new HashSet<String>(temp));
			}
		}
	}

	public String degreeCalculation(Map<String, Set<String>> edgeList) {
		if (edgeList.isEmpty()) {
			return "0.00";
		}
		int size = edgeList.size();
		double sum = 0;
		for (Set<String> list : edgeList.values()) {
			sum += list.size();
		}
		// truncate the value to two digits after the decimal place
		return String.format("%.2f", Math.floor(sum / size * 100) / 100);
	}

	public long timeComparison(String timeStamp, String timeStampPeek) {
		long timeOfIncomingTweet = processTimeStamp(timeStamp).getTime() / 1000;
		long timeOfPeekTweet = processTimeStamp(timeStampPeek).getTime() / 1000;
		return timeOfIncomingTweet - timeOfPeekTweet;
	}

	public static Date processTimeStamp(String timeStamp) {
		String timeStampFormat = "EEE MMM dd HH:mm:ss ZZZZ yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(timeStampFormat, Locale.ENGLISH);
		sdf.setLenient(true);
		try {
			return sdf.parse(timeStamp);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	static class timestampSort implements Comparator<Tweet> {
		public int compare(Tweet o1, Tweet o2) {

			if (processTimeStamp(o1.timeStamp).after(processTimeStamp(o2.timeStamp))) {
				return 1;
			} else if (processTimeStamp(o1.timeStamp).before(processTimeStamp(o2.timeStamp))) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
