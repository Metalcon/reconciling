package freebaseclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import api.FlickrPhoto;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * 
 * @author tobi, Christian Schowalter
 * 
 */

public class FlickrAPI {
	public Properties properties;

	public FlickrAPI() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream("flickr.properties"));
		} catch (FileNotFoundException e) {
			System.err
					.println("you need a file flickr.properties. For more information, read the README.md");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param queryText
	 *            Is a String of a search term.
	 * @return Returns a List<FlickrPhoto> with all found photos matching the
	 *         query
	 * 
	 *         This method requires a search term and the kind of licenses
	 *         defined above. It delivers a List of FlickrPhoto objects
	 *         containing the search results
	 */

	public List<FlickrPhoto> getPhotosByQuery(String queryText) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license, views, media, tags");
		url.put("license", "4,5,6,7,8");
		url.put("sort", "relevance");
		url.put("format", "json");
		System.out.println(url);
		String response = makeHttpRequest(url);
		List<FlickrPhoto> photoIds = new ArrayList<FlickrPhoto>();
		parsePhotoResponse(response, queryText, photoIds);
		return photoIds;
	}

	/**
	 * helper function to make an http request to flickr
	 * 
	 * @param url
	 * @return Returns a JSON String
	 */

	private String makeHttpRequest(GenericUrl url) {
		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
		HttpRequest request;
		try {
			request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			String httpResponseString = httpResponse.parseAsString();
			String response = httpResponseString.substring(
					httpResponseString.indexOf("(") + 1,
					httpResponseString.lastIndexOf(")"));
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param jsonResponse
	 *            This is the String containing the results created from
	 *            makeHttpRequest(), in form of a JSON
	 * @param queryText
	 *            This is the search term string that is passed down from
	 *            getPhotosByQuery()
	 * @return returns a List<FlickrPhoto> with the parsed results
	 * 
	 *         This method is an internal method to parse the Photo results into
	 *         FlickrPhoto objects and puts them into a List
	 */

	private void parsePhotoResponse(String jsonResponse, String queryText,
			List<FlickrPhoto> tempList) {

		JSONParser jsonparser = new JSONParser();
		try {
			JSONObject response = (JSONObject) jsonparser.parse(jsonResponse);
			JSONObject responsePhotos = (JSONObject) response.get("photos");
			int page = Integer.parseInt(responsePhotos.get("page").toString());
			int pages = Integer
					.parseInt(responsePhotos.get("pages").toString());
			JSONArray photoList = (JSONArray) responsePhotos.get("photo");
			for (int i = 0; i < photoList.size(); ++i) {
				FlickrPhoto photoTemp = new FlickrPhoto();
				JSONObject photoData = (JSONObject) photoList.get(i);
				photoTemp.setUrl(photoData.get("url_o").toString());
				photoTemp.setLicense(photoData.get("license").toString());
				photoTemp.setMediaStatus(photoData.get("media_status")
						.toString());
				List<String> tagList = new ArrayList<String>();
				String tagString = photoData.get("tags").toString();
				if (!tagString.isEmpty()) {
					tagList = Arrays.asList(tagString.split(" "));
				}
				photoTemp.setTags(tagList);
				photoTemp.setPhotoId(photoData.get("id").toString());
				photoTemp.setTitle(photoData.get("title").toString());
				photoTemp.setOwnerName(photoData.get("ownername").toString());
				photoTemp.setViews(Integer.parseInt(photoData.get("views")
						.toString()));
				System.out.println(photoTemp);
				tempList.add(photoTemp);
			}
			if (page < pages) {
				getNextPage(queryText, ++page, tempList);
			}
		} catch (ClassCastException ce) {
			System.err
					.println("Typecast failed. Response is probably broken. Can be caused by bad request");
		} catch (ParseException e) {
			System.err.println("Error parsing response");
		}
		// if (page < pages) {request next page}
	}

	/**
	 * 
	 * @param queryText
	 *            This is the search term string that is passed down from
	 *            getPhotosByQuery()
	 * @param page
	 *            This is a int counter to make a new request to flickr with
	 *            this page number
	 * @param tempList
	 *            This is a List<FlickrPhoto> with the search results
	 * 
	 *            An internal method to navigate multiple pages of search
	 *            results. It adds the results to the existing List created in
	 *            parsePhotoResponse()
	 */

	private void getNextPage(String queryText, int page,
			List<FlickrPhoto> tempList) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license,views,media, tags");
		url.put("license", "4,5,6,7,8");
		url.put("sort", "relevance");
		url.put("format", "json");
		url.put("page", page);
		String response = makeHttpRequest(url);
		parsePhotoResponse(response, queryText, tempList);
	}

	/**
	 * 
	 * @param queryText
	 *            String containing the search terms
	 * @param placeName
	 *            String containing the name of place
	 * @return Returns a list with the results to the search
	 * 
	 *         This method is used to look for Photos with the name of a place
	 *         combined with a search term
	 */
	public List<FlickrPhoto> getPhotosByQueryAndPlace(String queryText,
			String placeName) {
		String placeId = getPlaceIdByName(placeName);
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license,views,media, tags");
		url.put("license", "4,5,6,7,8");
		url.put("sort", "relevance");
		url.put("format", "json");
		url.put("place_id", placeId);
		System.out.println(url);
		String response = makeHttpRequest(url);
		List<FlickrPhoto> photoIds = new ArrayList<FlickrPhoto>();
		parsePhotoResponse(response, queryText, photoIds);
		return photoIds;
	}

	/**
	 * 
	 * @param queryText
	 *            This is a search term you want to retrieve photos for
	 * @param lat
	 *            Double with the latitude
	 * @param lon
	 *            Double with the longitide
	 * @return Returns a List<FlickrPhoto> with the results
	 * 
	 *         This method is used to request Photos with specific
	 *         geo-coordinates and delivers a container with the search results,
	 *         in FlickrPhoto format
	 */
	public List<FlickrPhoto> getPhotosByQueryAndGeoCoord(String queryText,
			double lat, double lon) {
		String placeId = getPlaceIdByGeoCoord(lat, lon);
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license,views,media, tags");
		url.put("license", "4,5,6,7,8");
		url.put("sort", "relevance");
		url.put("format", "json");
		url.put("place_id", placeId);
		System.out.println(url);
		String response = makeHttpRequest(url);
		List<FlickrPhoto> photoIds = new ArrayList<FlickrPhoto>();
		parsePhotoResponse(response, queryText, photoIds);
		return photoIds;
	}

	/**
	 * 
	 * @param place
	 *            Defines the place that you need the flickrPlaceId for. Can
	 *            search only for place ("Koblenz"), but can it specify more by
	 *            adding the state ("Koblenz Rheinland-Pfalz") /
	 *            ("Koblenz RLP"). Just to show a few examples
	 * @return Returns the unique flickrPlaceId
	 * 
	 *         This method can be used to retrieve FlickrPlaceIDs by name
	 */
	private String getPlaceIdByName(String place) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.places.find");
		url.put("query", place);
		url.put("format", "json");
		String response = makeHttpRequest(url);
		String placeId = parsePlacesResponse(response);
		return placeId;
	}

	/**
	 * 
	 * @param lat
	 *            The latitude coordinate
	 * @param lon
	 *            The longitude coordinate
	 * @return Returns the unique flickrPlaceID
	 * 
	 *         This method can be used to retrieve FlickrPlaceIDs by its geo
	 *         coordinates
	 */

	private String getPlaceIdByGeoCoord(double lat, double lon) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.places.findByLatLon");
		url.put("lat", lat);
		url.put("lon", lon);
		url.put("format", "json");
		String response = makeHttpRequest(url);
		String placeId = parsePlacesResponse(response);
		return placeId;
	}

	/**
	 * 
	 * @param jsonResponse
	 *            Is the JSON containing the information for the place generated
	 *            in getPlaceIdByGeoCoord() and getPlaceIdByName()
	 * @return String with the unique FlickrPlaceID
	 * 
	 *         This is a internal helper method to parse the JSON files
	 *         containing the informations about the places
	 */

	private String parsePlacesResponse(String jsonResponse) {
		JSONParser jsonparser = new JSONParser();
		String returnString = "";
		try {
			JSONObject response = (JSONObject) jsonparser.parse(jsonResponse);
			JSONObject responsePhotos = (JSONObject) response.get("places");
			JSONArray photoList = (JSONArray) responsePhotos.get("place");
			JSONObject photoData = (JSONObject) photoList.get(0);
			returnString = photoData.get("place_id").toString();
		} catch (ClassCastException ce) {
			System.err
					.println("Typecast failed. Response is probably broken. Can be caused by bad request");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnString;
	}

	/**
	 * 
	 * @param queryText
	 *            Is a String of a search term.
	 * @param placeName
	 *            Is a String with a name of a place you want to combine the
	 *            search with
	 * @param minTakenDate
	 *            The earliest date photos looking for were taken
	 * @param maxTakenDate
	 *            The latest date photos looking for were taken
	 * @return Returns a List<FlickrPhoto> with all found photos matching the
	 *         query
	 * 
	 *         This method requires a search term and a place name to find
	 *         photos. In addition you need to specify the time when the photos
	 *         you are looking for were taken. It delivers a List of FlickrPhoto
	 *         objects containing the search results
	 */

	public List<FlickrPhoto> getPhotosByQueryPlaceAndTime(String queryText,
			String placeName, Date minTakenDate, Date maxTakenDate) {
		String placeId = getPlaceIdByName(placeName);
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license,views,media, tags");
		url.put("license", "4,5,6,7,8");
		url.put("sort", "relevance");
		url.put("format", "json");
		url.put("place_id", placeId);
		url.put("minTakenDate", minTakenDate.getTime());
		url.put("maxTakenDate", maxTakenDate.getTime());
		String response = makeHttpRequest(url);
		List<FlickrPhoto> photoIds = new ArrayList<FlickrPhoto>();
		parsePhotoResponse(response, queryText, photoIds);
		return photoIds;
	}

	/**
	 * 
	 * @param queryText
	 *            Is a String of a search term
	 * @param lat
	 *            Is a double with the latitude
	 * @param lon
	 *            Is a double with the longitude
	 * @param minTakenDate
	 *            The earliest date photos looking for were taken, as Date()
	 *            Format
	 * @param maxTakenDate
	 *            The latest date photos looking for were taken, as Date()
	 *            Format
	 * @return Returns a List<FlickrPhoto> with all found photos matching the
	 *         query
	 * 
	 *         This method requires a search term and specific geo-coordinates
	 *         for a place that you need photos for. In addition to this, you
	 *         need to specify the time when the photos were taken.
	 */
	public List<FlickrPhoto> getPhotosByQueryGeoCoordAndTime(String queryText,
			double lat, double lon, Date minTakenDate, Date maxTakenDate) {
		String placeId = getPlaceIdByGeoCoord(lat, lon);
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license,views,media, tags");
		url.put("license", "4,5,6,7,8");
		url.put("sort", "relevance");
		url.put("format", "json");
		url.put("place_id", placeId);
		url.put("minTakenDate", minTakenDate.getTime());
		url.put("maxTakenDate", maxTakenDate.getTime());
		String response = makeHttpRequest(url);
		List<FlickrPhoto> photoIds = new ArrayList<FlickrPhoto>();
		parsePhotoResponse(response, queryText, photoIds);
		return photoIds;
	}
}
