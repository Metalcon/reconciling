package freebaseclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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

	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException, ParseException,
			org.json.simple.parser.ParseException {
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

		String place = "Wacken";
		String placeId = getPlaceId(place);
		System.out.println(placeId);
		List<String> photoIds = new ArrayList<String>();
		photoIds = getPhotosFromPlace(placeId, licenseIdsUnproblematic);
		System.out.println(photoIds);
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
		eventPhotoIdsFree = getEventPhotos(placeId, eventName,
				minTakenDate.getTime(), maxTakenDate.getTime(),
				licenseIdsUnproblematic);
		eventPhotoIdsCCNC = getEventPhotos(placeId, eventName,
				minTakenDate.getTime(), maxTakenDate.getTime(), licenseNCOnly);
	}

	// TODO: include event dates to query!
	private static List<String> getEventPhotos(String placeId,
			String eventName, long minTakenDate, long maxTakenDate,
			String licenses) throws IOException, ParserConfigurationException,
			SAXException, org.json.simple.parser.ParseException {

		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("place_id", placeId);
		url.put("text", eventName);
		url.put("extras", "url_o");
		url.put("license", licenses);
		url.put("min_taken_date", minTakenDate);
		url.put("min_taken_date", maxTakenDate);
		url.put("format", "json");
		System.out.println(url);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse response = request.execute();
		List<String> photoIds = new ArrayList<String>();
		photoIds = parsePhotoResponse(response.parseAsString());
		return photoIds;
	}

	private static String getPlaceId(String place) throws IOException,
			ParserConfigurationException, SAXException {
		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.places.find");
		url.put("query", place);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse response = request.execute();
		// System.out.println(response.parseAsString());
		List<String> placeIds = new ArrayList<String>();
		placeIds = parsePlacesResponse(response.parseAsString());

		// of course this can be altered to return more results
		// for some reason, index 2 contains the first entry
		return placeIds.get(0);
	}

	private static List<String> parsePlacesResponse(String xmlResponse)
			throws ParserConfigurationException, SAXException, IOException {
		List<String> tempList = new ArrayList<String>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlResponse));
		Document document = db.parse(is);
		NodeList places = document.getElementsByTagName("places");
		for (int i = 0; i < places.getLength(); i++) {
			Element place = (Element) places.item(i);
			Node title = place.getElementsByTagName("place").item(0);
			NamedNodeMap nnm = title.getAttributes();
			String idTemp = nnm.getNamedItem("place_id").toString().split("=")[1];
			tempList.add(idTemp);
		}
		return tempList;
	}

	private static List<String> getPhotosFromPlace(String placeId,
			String licenses) throws IOException, ParserConfigurationException,
			SAXException, org.json.simple.parser.ParseException {

		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("place_id", placeId);
		url.put("extras", "url_o");
		url.put("license", licenses);
		url.put("format", "json");
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse response = request.execute();
		List<String> photoIds = new ArrayList<String>();
		photoIds = parsePhotoResponse(response.parseAsString());
		return photoIds;
	}

	private static List<String> parsePhotoResponse(String jsonResponse)
			throws ParserConfigurationException, IOException,
			org.json.simple.parser.ParseException {

		List<String> tempList = new ArrayList<String>();

		String jsonResponseWithoutPadding = jsonResponse.substring(
				jsonResponse.indexOf("(") + 1, jsonResponse.lastIndexOf(")"));
		JSONObject response = new JSONObject();
		JSONParser jsonparser = new JSONParser();
		try {
			response = (JSONObject) jsonparser
					.parse(jsonResponseWithoutPadding);
			JSONObject responsePhotos = (JSONObject) response.get("photos");
			JSONArray photoList = (JSONArray) responsePhotos.get("photo");
			for (int i = 0; i < photoList.size(); ++i) {
				JSONObject photoData = (JSONObject) photoList.get(i);

				tempList.add(photoData.get("url_o").toString());
			}
		} catch (ClassCastException ce) {
			System.err
					.println("Typecast failed. Response is probably broken. Can be caused by bad request");
		}

		// System.out.println(jsonResponse);
		System.out.println(tempList);
		return tempList;

	}
}
