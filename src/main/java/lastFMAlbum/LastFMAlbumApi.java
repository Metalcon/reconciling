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

	public Album getTracksByMbid(String mbid) {
		GenericUrl url = createUrl();
		url.put("mbid", mbid);
		JSONObject response = makeHttpRequest(url);
		System.out.println("tracksByMbid: " + url);
		return processingSearchResults(response);
	}

	public Album getTracksByName(String artist, String albumName) {
		GenericUrl url = createUrl();
		url.put("artist", artist);
		url.put("album", albumName);
		JSONObject response = makeHttpRequest(url);
		System.out.println("tracksByName: " + url);
		return processingSearchResults(response);
	}

	private Album processingSearchResults(JSONObject response) {
		JSONObject responseAlbum = (JSONObject) response.get("album");
		Album returnAlbum = new Album();
		returnAlbum.setArtist(responseAlbum.get("artist").toString());
		returnAlbum.setAlbumName(responseAlbum.get("name").toString());
		returnAlbum.setMbid(responseAlbum.get("mbid").toString());
		returnAlbum.setPlaycount(Integer.parseInt(responseAlbum
				.get("playcount").toString()));
		JSONArray responseAlbumImage = (JSONArray) responseAlbum.get("image");
		for (int i = 0; i < responseAlbumImage.size(); i++) {
			JSONObject responseAlbumImageEntry = (JSONObject) responseAlbumImage
					.get(i);
			if (responseAlbumImageEntry.get("size").toString().equals("mega")) {
				returnAlbum.setImage(responseAlbumImageEntry.get("#text")
						.toString());
			}
		}
		String tempDate = responseAlbum.get("releasedate").toString();
		DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
		Date date = null;
		try {
			date = formatter.parse(tempDate);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		returnAlbum.setReleaseDate(date);
		List<Track> tempTrackList = new ArrayList<Track>();
		JSONObject responseAlbumTracks = (JSONObject) responseAlbum
				.get("tracks");
		JSONArray responseAlbumTracksTrack = (JSONArray) responseAlbumTracks
				.get("track");
		for (int i = 0; i < responseAlbumTracksTrack.size(); i++) {
			Track tempTrack = new Track();
			JSONObject track = (JSONObject) responseAlbumTracksTrack.get(i);
			tempTrack.setName(track.get("name").toString());
			tempTrack.setMbid(track.get("mbid").toString());
			JSONObject trackAttr = (JSONObject) track.get("@attr");
			tempTrack.setRank(Integer
					.parseInt(trackAttr.get("rank").toString()));
			tempTrack.setDuration(Integer.parseInt(track.get("duration")
					.toString()));
		}
		returnAlbum.setTracks(tempTrackList);
		return returnAlbum;
	}

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

	private GenericUrl createUrl() {
		try {
			properties.load(new FileInputStream("lastfm.properties"));
		} catch (FileNotFoundException e) {
			System.out
					.println("you need a file freebase.properties. look in your git for freebase.properties.sample and rename it");
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
