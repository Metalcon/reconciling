package freebaseclient;

import java.io.FileInputStream;
import java.util.Properties;

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
 * the mid of a records primary release
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
			JSONParser parser = new JSONParser();
			GenericUrl url = new GenericUrl(
					"https://www.googleapis.com/freebase/v1/mqlread");
			String albumMid = "/m/01c1gx";
			String query = "[{				  \"mid\": \""
					+ albumMid
					+ "\",				  \"/music/album/release_date\": null,				  \"/music/album/primary_release\": {				    \"track_list\": [{				      \"/music/release_track/length\": null,				      \"/music/release_track/track_number\": null,				      \"id\": null,				      \"name\": null				    }]				  }				}]";
			url.put("query", query);
			System.out.println(url);
			System.out.println("my API-Key: " + properties.get("API_KEY"));
			url.put("key", properties.get("API_KEY"));
			HttpRequest request = requestFactory.buildGetRequest(url);
			System.out.println(url);
			HttpResponse httpResponse = request.execute();
			// JSONObject response = (JSONObject) parser.parse(httpResponse
			// .parseAsString());
			System.out.println(httpResponse.parseAsString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}