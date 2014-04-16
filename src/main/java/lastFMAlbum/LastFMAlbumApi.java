package lastFMAlbum;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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

public class LastFMAlbumApi {
	public Properties properties = new Properties();

	/**
	 * 
	 * @param mbid
	 *            A String containing the musicbrainz id for an album
	 * @return An Album object including all tracks
	 * 
	 *         This method takes a String with the musicbrainz id and looks
	 *         through lastFM for a hit. If it finds an album it returns an
	 *         object with the album informations, including all tracks for that
	 *         album
	 */

	public Album getTracksByMbid(String mbid) {
		GenericUrl url = createUrl();
		url.put("mbid", mbid);
		JSONObject response = makeHttpRequest(url);
		System.out.println("tracksByMbid: " + url);
		return processingSearchResults(response);
	}

	/**
	 * 
	 * @param artist
	 *            String with the name of an artist that you need to find an
	 *            album for
	 * @param albumName
	 *            String with the name of an album for the same artist
	 * @return An Album object including all tracks
	 * 
	 *         This method takes 2 Strings to specify an album for an artist and
	 *         looks though lastFM for informations on that album. If it finds
	 *         it, it returns an object with the album informations, including
	 *         all tracks for that object
	 */
	public Album getTracksByName(String artist, String albumName) {
		GenericUrl url = createUrl();
		url.put("artist", artist);
		url.put("album", albumName);
		JSONObject response = makeHttpRequest(url);
		System.out.println("tracksByName: " + url);
		return processingSearchResults(response);
	}

	/**
	 * 
	 * @param mbid
	 *            This is the musicbrainz id for the album
	 * @param albumName
	 * @param artist
	 * @return Album object with the informations processed from lastFM
	 * 
	 *         This method takes both the mbid and the artistname / albumname to
	 *         lookup. First it tries to look for the muid, but if it fails it
	 *         does the lookup with the combination artist / album.
	 */
	public Album getTracksByMuidOrName(String mbid, String albumName,
			String artist) {
		GenericUrl url = createUrl();
		url.put("mbid", mbid);
		JSONObject response = makeHttpRequest(url);
		if (response.containsKey("error")) {
			url = createUrl();
			url.put("artist", artist);
			url.put("album", albumName);
			response = makeHttpRequest(url);
		}
		System.out.println(url);
		return processingSearchResults(response);
	}

	/**
	 * 
	 * @param response
	 *            A JSONObject with the results from the lastFM request
	 * @return Returns an album object
	 * 
	 *         This is an internal method to process the JSON from lastFM and to
	 *         fill the Album object
	 */
	private Album processingSearchResults(JSONObject response) {
		if (!response.containsKey("error")) {
			JSONObject responseAlbum = (JSONObject) response.get("album");
			Album returnAlbum = new Album();
			returnAlbum.setArtist(responseAlbum.get("artist").toString());
			returnAlbum.setAlbumName(responseAlbum.get("name").toString());
			returnAlbum.setMbid(responseAlbum.get("mbid").toString());
			returnAlbum.setPlaycount(Integer.parseInt(responseAlbum.get(
					"playcount").toString()));
			JSONArray responseAlbumImage = (JSONArray) responseAlbum
					.get("image");
			for (int i = 0; i < responseAlbumImage.size(); i++) {
				JSONObject responseAlbumImageEntry = (JSONObject) responseAlbumImage
						.get(i);
				if (responseAlbumImageEntry.get("size").toString()
						.equals("mega")) {
					returnAlbum.setImage(responseAlbumImageEntry.get("#text")
							.toString());
				}
			}
			String tempDate = responseAlbum.get("releasedate").toString();
			if ((tempDate.contains("00:00"))) {
				DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
				Date date = null;
				try {
					date = formatter.parse(tempDate);
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
				returnAlbum.setReleaseDate(date);
			}
			List<Track> tempTrackList = new ArrayList<Track>();
			Object tracksTypeTest = responseAlbum.get("tracks");
			if (!(tracksTypeTest instanceof String)) {
				JSONObject responseAlbumTracks = (JSONObject) responseAlbum
						.get("tracks");
				Object trackTypeTest = responseAlbumTracks.get("track");
				if (trackTypeTest instanceof JSONArray) {
					JSONArray responseAlbumTracksTrack = (JSONArray) responseAlbumTracks
							.get("track");
					for (int i = 0; i < responseAlbumTracksTrack.size(); i++) {
						Track tempTrack = new Track();
						JSONObject track = (JSONObject) responseAlbumTracksTrack
								.get(i);
						tempTrack.setName(track.get("name").toString());
						tempTrack.setMbid(track.get("mbid").toString());
						JSONObject trackAttr = (JSONObject) track.get("@attr");
						tempTrack.setRank(Integer.parseInt(trackAttr
								.get("rank").toString()));
						if (!track.get("duration").equals(""))
							tempTrack.setDuration(Integer.parseInt(track.get(
									"duration").toString()));
						tempTrackList.add(tempTrack);
					}
					returnAlbum.setTracks(tempTrackList);
					return returnAlbum;
				} else {
					JSONObject track = (JSONObject) responseAlbumTracks
							.get("track");
					Track tempTrack = new Track();
					tempTrack.setName(track.get("name").toString());
					tempTrack.setMbid(track.get("mbid").toString());
					JSONObject trackAttr = (JSONObject) track.get("@attr");
					tempTrack.setRank(Integer.parseInt(trackAttr.get("rank")
							.toString()));
					if (!track.get("duration").equals(""))
						tempTrack.setDuration(Integer.parseInt(track.get(
								"duration").toString()));
					tempTrackList.add(tempTrack);
				}
			}
			returnAlbum.setTracks(tempTrackList);
			return returnAlbum;

		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @return JSONObject
	 * 
	 *         This is an internal helper method to make a http request. It just
	 *         requires a url and returns a JSON with the answer
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
	 * @return
	 * 
	 *         This internal helper method simply creates the generic parts of
	 *         the url used in getTracksByMbid() and getTracksByName()
	 */
	private GenericUrl createUrl() {
		try {
			properties.load(new FileInputStream("lastfm.properties"));
		} catch (FileNotFoundException e) {
			System.out
					.println("you need a file lastfm.properties. Read the readme for more information!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GenericUrl url = new GenericUrl("http://ws.audioscrobbler.com/2.0/");
		url.put("method", "album.getInfo");
		url.put("format", "json");
		url.put("autocorrect", "1");
		url.put("api_key", properties.get("API_KEY"));
		return url;
	}
}
