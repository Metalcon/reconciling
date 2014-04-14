package freebaseclient;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * 
 * An experimental implementation of a request, which is supposed to determine
 * the mid of a records primary release and request its musicbrainz id
 * 
 * @author Christian Schowalter
 * 
 */
public class SingleAlbumPrimaryReleaseSearch {
	public static Properties properties = new Properties();

	public static void main(String[] args) {
		try {
			properties.load(new FileInputStream("freebase.properties"));
			HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory requestFactory = httpTransport
					.createRequestFactory();
			GenericUrl url = new GenericUrl(
					"https://www.googleapis.com/freebase/v1/mqlread");
			String albumMid = "/m/01c1gx";
			// requesting /common/topic/topic_equivalent_webpage via this JSON
			// kind of request does always get a null response so we have to
			// waste a second request to get the musicbrainz id.
			String query = "[{\"mid\": \"" + albumMid
					+ "\", \"/music/album/primary_release\": null}]";
			url.put("query", query);
			System.out.println(url);
			System.out.println("my API-Key: " + properties.get("API_KEY"));
			url.put("key", properties.get("API_KEY"));
			HttpRequest request = requestFactory.buildGetRequest(url);
			System.out.println(url);
			HttpResponse httpResponse = request.execute();
			JSONParser parser = new JSONParser();
			JSONObject response = (JSONObject) parser.parse(httpResponse
					.parseAsString());
			JSONArray responseResult = (JSONArray) response.get("result");
			JSONObject responseResults = (JSONObject) responseResult.get(0);
			System.out.println(responseResults);
			List<GenericUrl> resultList = new ArrayList<GenericUrl>();
			String primaryReleaseName = responseResults.get(
					"/music/album/primary_release").toString();
			String primaryReleaseMid = responseResults.get("mid").toString();

			System.out.println(primaryReleaseMid);

			GenericUrl url2 = new GenericUrl(
					"https://www.googleapis.com/freebase/v1/topic"
							+ primaryReleaseMid);

			url2.put("filter", "/common/topic/topic_equivalent_webpage");
			url2.put("limit", "9001");

			System.out.println(url2);
			System.out.println("my API-Key: " + properties.get("API_KEY"));
			url.put("key", properties.get("API_KEY"));
			HttpRequest request2 = requestFactory.buildGetRequest(url2);
			System.out.println(url2);
			HttpResponse httpResponse2 = request2.execute();

			System.out.println(httpResponse2.parseAsString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}