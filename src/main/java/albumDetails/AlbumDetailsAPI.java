package albumDetails;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import lastFMAlbum.Album;
import lastFMAlbum.LastFMAlbumApi;

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

/**
 * 
 * @author tobi, Christian Schowalter
 * 
 */

public class AlbumDetailsAPI {

	public Properties properties = new Properties();

	/**
	 * 
	 * @param bandMid
	 *            The required unique freebase mid for a band you want to search
	 *            for all songs
	 * @return Returns a List<Album> containing all albums with all tracks
	 * 
	 *         This method requires a freebase mid for a band and looks through
	 *         the lastFM API for songs. For that it first asks freebase for
	 *         mids for all the albums, then goes through the list and requests
	 *         all primary releases, and saves the new mids. When it has mids
	 *         for all albums it extracts the equivalent musicbrainz ids. And
	 *         with that it can make a request to the lastFM Api. If this fails
	 *         and returns no result it sends a second request to lastFM, this
	 *         time with the name of the band and the name of the album.
	 */
	public List<Album> getAlbums(String bandMid) {

		List<MusicbrainzAlbum> albums = new ArrayList<MusicbrainzAlbum>();
		albums = getAlbumMids(bandMid);
		getPrimaryAlbums(albums);
		getMusicbrainzIds(albums);
		List<Album> output = new ArrayList<Album>();
		LastFMAlbumApi getLastFMInfo = new LastFMAlbumApi();
		for (int i = 0; i < albums.size(); i++) {
			Album tempAlbum = new Album();
			String tempMbid = albums.get(i).getMbid();
			String tempAlbumName = albums.get(i).getAlbum();
			String tempArtist = albums.get(i).getArtist();
			tempAlbum = getLastFMInfo.getTracksByMuidOrName(tempMbid,
					tempAlbumName, tempArtist);
			output.add(tempAlbum);
		}
		return output;
	}

	/**
	 * 
	 * @param primaryAlbumMidList
	 * 
	 *            This internal method fills the last field of the
	 *            MusicbrainzAlbum objects and extracts the musicbrainz ids for
	 *            all albums
	 */
	private void getMusicbrainzIds(List<MusicbrainzAlbum> primaryAlbumMidList) {
		for (MusicbrainzAlbum iterator : primaryAlbumMidList) {
			GenericUrl url = new GenericUrl(
					"https://www.googleapis.com/freebase/v1/topic"
							+ iterator.getMid());
			url.put("filter", "/common/topic/topic_equivalent_webpage");
			url.put("limit", "9001");
			url.put("key", properties.get("API_KEY"));
			JSONObject response = makeHttpRequest(url);
			JSONObject responseProperty = (JSONObject) response.get("property");

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
			if (resultUrl != null) {
				String[] result = resultUrl.split("group/");
				iterator.setMbid(result[1]);
			}
		}
	}

	/**
	 * 
	 * @param albumList
	 *            This internal method changes the mids to the albums to the
	 *            ones of the primary releases.
	 */

	private void getPrimaryAlbums(List<MusicbrainzAlbum> albumList) {
		GenericUrl url = new GenericUrl(
				"https://www.googleapis.com/freebase/v1/mqlread");
		String albumMid = null;
		for (MusicbrainzAlbum iterator : albumList) {
			albumMid = iterator.getMid();
			String query = "[{\"mid\": \"" + albumMid
					+ "\", \"/music/album/primary_release\": null}]";
			url.put("query", query);
			url.put("key", properties.get("API_KEY"));
			JSONObject response = makeHttpRequest(url);
			JSONArray responseResult = (JSONArray) response.get("result");
			JSONObject responseResults = (JSONObject) responseResult.get(0);
			iterator.setMid(responseResults.get("mid").toString());
		}
	}

	/**
	 * 
	 * @param url
	 * @return
	 * 
	 *         Internal helper method for making http requests. It receives a
	 *         Url and makes a http request with it. Afterwards it parses the
	 *         result and returns the result json.
	 */
	private JSONObject makeHttpRequest(GenericUrl url) {
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

	/**
	 * 
	 * @param bandMid
	 * @return
	 * 
	 *         This internal method gets as input the unique freebase mid and
	 *         makes a request to receive all of the bands' albums
	 * 
	 */
	private List<MusicbrainzAlbum> getAlbumMids(String bandMid) {
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
				+ "\",\"/music/artist/album\":[{\"name\":null , \"mid\":null}], \"name\" : null }]";
		url.put("query", query);
		url.put("key", properties.get("API_KEY"));
		JSONObject response = makeHttpRequest(url);
		System.out.println(response);
		JSONArray responseResult = (JSONArray) response.get("result");
		JSONObject responseResultEntry = (JSONObject) responseResult.get(0);
		JSONArray responseResultEntryArray = (JSONArray) responseResultEntry
				.get("/music/artist/album");
		String Bandname = responseResultEntry.get("name").toString();
		System.out.println(Bandname);
		List<MusicbrainzAlbum> returnList = new ArrayList<MusicbrainzAlbum>();

		for (int i = 0; i < responseResultEntryArray.size(); i++) {
			JSONObject albumEntry = (JSONObject) responseResultEntryArray
					.get(i);
			MusicbrainzAlbum musicbrainzAlbum = new MusicbrainzAlbum();
			musicbrainzAlbum.setArtist(Bandname);
			musicbrainzAlbum.setMid(albumEntry.get("mid").toString());
			musicbrainzAlbum.setAlbum(albumEntry.get("name").toString());
			returnList.add(musicbrainzAlbum);
		}
		return returnList;
	}
}
