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
		List<String> primaryAlbums = new ArrayList<String>();
		primaryAlbums = getPrimaryAlbums(albums);
		System.out.println(primaryAlbums);

		if (albums.equals(primaryAlbums)) {
			System.out
					.println("you've got the primary albums in the first request already. Try more albums to find out if this is always the case!");
		}
	}

	public List<Album> getAlbums(String bandMid) {

		// TODO: get album-mids via band-mids
		List<Album> albums = new ArrayList<Album>();
		List<String> albumMidList = new ArrayList<String>();
		albumMidList = getAlbumMids(bandMid);

		// TODO: get primary-release-mids to alum-mids
		List<String> primaryAlbumMidList = new ArrayList<String>();
		primaryAlbumMidList = getPrimaryAlbums(albumMidList);

		// TODO: get lastfm details to primary-release and store them as an
		// Album List

		// TODO: return Album List.
		return null;
	}

	private static List<String> getPrimaryAlbums(List<String> albumMidList) {
		GenericUrl url = new GenericUrl(
				"https://www.googleapis.com/freebase/v1/mqlread");

		// requesting /common/topic/topic_equivalent_webpage via this JSON
		// kind of request does always get a null response so we have to
		// waste a second request to get the musicbrainz id.

		List<String> returnList = new ArrayList<String>();

		String albumMid = null;
		for (int i = 0; i < albumMidList.size(); ++i) {
			albumMid = albumMidList.get(i);
			String query = "[{\"mid\": \"" + albumMid
					+ "\", \"/music/album/primary_release\": null}]";
			url.put("query", query);
			url.put("key", properties.get("API_KEY"));
			JSONObject response = makeHttpRequest(url);
			JSONArray responseResult = (JSONArray) response.get("result");
			JSONObject responseResults = (JSONObject) responseResult.get(0);
			String primaryReleaseMid = responseResults.get("mid").toString();
			returnList.add(responseResults.get("mid").toString());

		}
		return returnList;
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
