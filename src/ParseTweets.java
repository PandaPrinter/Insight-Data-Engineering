import java.io.PrintWriter;
import java.util.*;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ParseTweets {
	public void extractInfo(String line, PrintWriter printWriter) {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(line);
			JSONObject jsonObject = (JSONObject) obj;
			String timestamp = (String) jsonObject.get("created_at");
			JSONObject entities = (JSONObject) jsonObject.get("entities");
			// ensure the incoming tweet is not the rate-limit message
			if (entities != null) {
				JSONArray hashtags = (JSONArray) entities.get("hashtags");
				Set<String> tweetSet = new HashSet<String>();
				Iterator iterator = hashtags.iterator();
				// extract each hashtag
				while (iterator.hasNext()) {
					JSONObject innerObj = (JSONObject) iterator.next();
					tweetSet.add((String) innerObj.get("text"));
				}
				BuildGraph bg = new BuildGraph();
				bg.drawGraph(timestamp, tweetSet, printWriter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
