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
import com.jayway.jsonpath.JsonPath;

public class AlbumDetailsAPI {

	public static Properties properties = new Properties();

	public static void main(String[] args) {
		List<String> albums = new ArrayList<String>();
		albums = getAlbumMids("/m/014_xj");
		List<String> primaryAlbums = new ArrayList<String>();
		primaryAlbums = getPrimaryAlbums(albums);

		// List<Album> output = new ArrayList<Album>();
		// LastFMAlbumApi getLastFMInfo = new LastFMAlbumApi();
		// for (int i = 0; i < primaryAlbums.size(); i++) {
		// Album tempAlbum = new Album();
		// String temp = primaryAlbums.get(i);
		// tempAlbum = getLastFMInfo.getTracksByMbid(temp);
		// output.add(tempAlbum);
		// }

		if (albums.equals(primaryAlbums)) {
			System.out
					.println("you've got the primary albums in the first request already. Try more albums to find out if this is always the case!");
		}
		// for (int i = 0; i < output.size(); i++) {
		// System.out.println(output.get(i).toString());
		// }
	}

	public List<Album> getAlbums(String bandMid) {

		// get album-mids via band-mids
		List<Album> albums = new ArrayList<Album>();
		List<String> albumMidList = new ArrayList<String>();
		albumMidList = getAlbumMids(bandMid);

		// get primary-release-mids to album-mids
		List<String> primaryAlbumMidList = new ArrayList<String>();
		primaryAlbumMidList = getPrimaryAlbums(albumMidList);

		// TODO: get Muiscbrainz-id
		List<String> musicbrainzIds = new ArrayList<String>();
		musicbrainzIds = getMusicbrainzIds(primaryAlbumMidList);

		// TODO: get lastfm details to primary-release and store them as an
		// Album List

		// TODO: return Album List.
		return albums;
	}

	private static List<String> getMusicbrainzIds(
			List<String> primaryAlbumMidList) {
		for (int i = 0; i < primaryAlbumMidList.size(); ++i) {
			GenericUrl url = new GenericUrl(
					"https://www.googleapis.com/freebase/v1/topic"
							+ primaryAlbumMidList.get(i));
			url.put("filter", "/common/topic/topic_equivalent_webpage");
			url.put("limit", "9001");
			url.put("key", properties.get("API_KEY"));
			JSONObject response = makeHttpRequest(url);
			JSONObject responseProperty = (JSONObject) response.get("property");
			System.out.println(responseProperty);

			// sometimes Freebase does not have any information to a mid
			if (responseProperty == null) {

				continue;
			}
			JSONObject responsePropertyValues = (JSONObject) responseProperty
					.get("/common/topic/topic_equivalent_webpage");
			JSONArray pages = (JSONArray) responsePropertyValues.get("values");

			String resultUrl = null;
			for (Object page : pages) {
				GenericUrl pageUrl = new GenericUrl(JsonPath.read(page,
						"$.value").toString());
				if (pageUrl.toString().contains("http://musicbrainz.org")) {
					resultUrl = pageUrl.toString();
					break;
				}

			}
			System.out.println(resultUrl);
		}

		return null;
	}

	private static List<String> getPrimaryAlbums(List<String> albumMidList) {
		GenericUrl url = new GenericUrl(
				"https://www.googleapis.com/freebase/v1/mqlread");

		List<String> returnList = new ArrayList<String>();

		String albumMid = null;
		for (int i = 0; i < albumMidList.size(); ++i) {
			albumMid = albumMidList.get(i);
			String query = "[{\"mid\": \"" + albumMid
					+ "\", \"/music/album/primary_release\": null}]";
			url.put("query", query);
			url.put("key", properties.get("API_KEY"));
			System.out.println(url);
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
