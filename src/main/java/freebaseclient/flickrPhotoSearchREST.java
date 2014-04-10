package freebaseclient;

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

public class flickrPhotoSearchREST {
	public static Properties properties = new Properties();
	static GenericUrl url = new GenericUrl(
			"https://api.flickr.com/services/rest/");

	public static void main(String[] args) throws IOException, ParseException,
			java.text.ParseException {
		try {
			properties.load(new FileInputStream("flickr.properties"));
		} catch (FileNotFoundException fnfe) {
			System.err.println("missing flickr properties");
		}

		// you can look up the license-IDs via the request
		// flickr.photos.licenses.getInfo which get exactly the same information
		// as stated here
		// https://secure.flickr.com/services/api/flickr.photos.licenses.getInfo.html

		// all licenses excluding "All Rights Reserved"
		String licenseIdsWithoutAllRightsReserved = "1,2,3,4,5,6,7,8";

		// same as above but without CC-NC
		String licenseIdsUnproblematic = "4,5,6,7,8";

		// only CC-NC (useful to find out if it is worth the effort to find a
		// way to use this footage)
		String licenseNCOnly = "1,2,3";

		// this is what we won't use because of it's copyright
		String licenseEvil = "0";

		String place = "Wacken";
		String placeId = getPlaceId(place);
		System.out.println("placeId: " + placeId);
		List<String> photoIds = new ArrayList<String>();

		photoIds = getPhotosFromPlace(placeId, licenseIdsUnproblematic, 1);
		System.out.println("all photos from " + place + ": " + photoIds);
		DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
		Date minTakenDate = new Date();
		minTakenDate = formatter.parse("05-08-2010");
		Date maxTakenDate = new Date();
		maxTakenDate = formatter.parse("07-08-2010");
		List<String> eventPhotoIdsFree = new ArrayList<String>();
		List<String> eventPhotoIdsWithNc = new ArrayList<String>();
		List<String> eventPhotoIdsCCNC = new ArrayList<String>();
		String eventName = "Wacken";

		eventPhotoIdsWithNc = getEventPhotos(placeId, eventName,
				minTakenDate.getTime(), maxTakenDate.getTime(),
				licenseIdsWithoutAllRightsReserved);
		System.out.println("Wacken 2012 photos CC*" + eventPhotoIdsWithNc);

		eventPhotoIdsFree = getEventPhotos(placeId, eventName,
				minTakenDate.getTime(), maxTakenDate.getTime(),
				licenseIdsUnproblematic);
		System.out.println("Wacken 2012 photos (unproblematic!) "
				+ eventPhotoIdsFree);

		eventPhotoIdsCCNC = getEventPhotos(placeId, eventName,
				minTakenDate.getTime(), maxTakenDate.getTime(), licenseNCOnly);

		System.out
				.println("Wacken 2012 photoso CC-NC only" + eventPhotoIdsCCNC);

		List<String> photosByTextQuery = new ArrayList<String>();
		photosByTextQuery = getPhotos(eventName,
				licenseIdsWithoutAllRightsReserved);

		System.out.println("this is what happens if you only search by text: "
				+ photosByTextQuery);

	}

	private static List<String> getPhotos(String queryText, String licenses)
			throws IOException, ParseException {

		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license");
		url.put("license", licenses);
		url.put("sort", "relevance");
		url.put("format", "json");
		System.out.println(url);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse response = request.execute();
		List<String> photoIds = new ArrayList<String>();
		photoIds = parsePhotoResponse(response.parseAsString());
		return photoIds;
	}

	private static List<String> getEventPhotos(String placeId,
			String eventName, long minTakenDate, long maxTakenDate,
			String licenses) throws IOException, ParseException {

		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("place_id", placeId);
		url.put("text", eventName);
		url.put("extras", "url_o,owner_name,license");
		url.put("license", licenses);
		url.put("min_taken_date", minTakenDate);
		url.put("min_taken_date", maxTakenDate);
		url.put("sort", "relevance");
		url.put("format", "json");
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse response = request.execute();
		List<String> photoIds = new ArrayList<String>();
		photoIds = parsePhotoResponse(response.parseAsString());
		return photoIds;
	}

	private static String getPlaceId(String place) throws IOException,
			ParseException, org.json.simple.parser.ParseException {
		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.places.find");
		url.put("query", place);
		url.put("format", "json");

		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse response = request.execute();
		// System.out.println(response.parseAsString());
		List<String> placeIds = new ArrayList<String>();
		placeIds = parsePlacesResponse(response.parseAsString());

		// of course this can be altered to return more results
		// for some reason, index 2 contains the first entry
		return placeIds.get(0);
	}

	private static List<String> parsePlacesResponse(String jsonResponse)
			throws IOException, ParseException {
		List<String> tempList = new ArrayList<String>();

		String jsonResponseWithoutPadding = jsonResponse.substring(
				jsonResponse.indexOf("(") + 1, jsonResponse.lastIndexOf(")"));
		JSONObject response = new JSONObject();
		JSONParser jsonparser = new JSONParser();
		try {
			response = (JSONObject) jsonparser
					.parse(jsonResponseWithoutPadding);
			JSONObject responsePhotos = (JSONObject) response.get("places");
			JSONArray photoList = (JSONArray) responsePhotos.get("place");
			for (int i = 0; i < photoList.size(); ++i) {
				JSONObject photoData = (JSONObject) photoList.get(i);

				tempList.add(photoData.get("place_id").toString());
			}
		} catch (ClassCastException ce) {
			System.err
					.println("Typecast failed. Response is probably broken. Can be caused by bad request");
		}
		return tempList;
	}

	private static List<String> getPhotosFromPlace(String placeId,
			String licenses, int page) throws IOException, ParseException {

		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("place_id", placeId);
		url.put("extras", "url_o,owner_name,license");
		url.put("license", licenses);
		url.put("format", "json");
		url.put("sort", "relevance");
		url.put("page", page);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse response = request.execute();
		List<String> photoIds = new ArrayList<String>();
		photoIds = parsePhotoResponse(response.parseAsString());
		return photoIds;
	}

	private static List<String> parsePhotoResponse(String jsonResponse)
			throws IOException, org.json.simple.parser.ParseException {

		List<String> tempList = new ArrayList<String>();
		String jsonResponseWithoutPadding = jsonResponse.substring(
				jsonResponse.indexOf("(") + 1, jsonResponse.lastIndexOf(")"));
		JSONObject response = new JSONObject();
		JSONParser jsonparser = new JSONParser();
		int page;
		int pages;
		try {
			response = (JSONObject) jsonparser
					.parse(jsonResponseWithoutPadding);
			JSONObject responsePhotos = (JSONObject) response.get("photos");
			page = Integer.parseInt(responsePhotos.get("page").toString());
			System.out.println(page);
			pages = Integer.parseInt(responsePhotos.get("pages").toString());
			JSONArray photoList = (JSONArray) responsePhotos.get("photo");
			for (int i = 0; i < photoList.size(); ++i) {
				JSONObject photoData = (JSONObject) photoList.get(i);
				tempList.add(photoData.get("url_o").toString());
			}
		} catch (ClassCastException ce) {
			System.err
					.println("Typecast failed. Response is probably broken. Can be caused by bad request");
		}
		// if (page < pages) {request next page}
		return tempList;
	}
}
