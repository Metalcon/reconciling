package albumDetails;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import lastFMAlbum.Album;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class AlbumDetailsAPI {

	public static Properties properties = new Properties();

	public static void main(String[] args) {
		List<String> albums = new ArrayList<String>();
		albums = getAlbumMids("/m/014_xj");
		System.out.println(albums);
	}

	public List<Album> getAlbums(String bandMid) {

		// TODO: get album-mids via band-mids
		List<Album> albums = new ArrayList<Album>();
		List<String> albumMidList = new ArrayList<String>();
		albumMidList = getAlbumMids(bandMid);

		// TODO: get primary-release-mids to alum-mids

		// TODO: get lastfm details to primary-release and store them as an
		// Album List

		// TODO: return Album List.
		return null;
	}

	private static JSONObject makeHttpRequest(GenericUrl url) {
		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
		HttpRequest request;
		try {
			request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(httpResponse.parseAsString());

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getAlbumMids(String bandMid) {
		try {
			properties.load(new FileInputStream("freebase.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GenericUrl url = new GenericUrl(
				"https://www.googleapis.com/freebase/v1/mqlread");
		String query = "[{\"mid\":\""
				+ bandMid
				+ "\",\"/music/artist/album\":[{\"name\":null , \"mid\":null}]}]";
		url.put("query", query);
		url.put("key", properties.get("API_KEY"));
		System.out.println(url);
		JSONObject response = makeHttpRequest(url);
		JSONArray responseResult = (JSONArray) response.get("result");
		JSONObject responseResultEntry = (JSONObject) responseResult.get(0);
		JSONArray responseResultEntryArray = (JSONArray) responseResultEntry
				.get("/music/artist/album");
		List<String> returnList = new ArrayList<String>();
		for (int i = 0; i < responseResultEntryArray.size(); i++) {
			JSONObject albumEntry = (JSONObject) responseResultEntryArray
					.get(i);
			returnList.add(albumEntry.get("mid").toString());
		}
		return returnList;
	}
}
