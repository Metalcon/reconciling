package freebaseclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
 * @author tobi
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
	 * @param licenses
	 *            defines what kind of licensed photos should be returned.
	 *            Multiple licenses are possible. Our default is "4,5,6,7,8"<br>
	 *            0 = all rights reserved. <br>
	 *            1 = Attribution-NonCommercial-ShareAlike License <br>
	 *            2 = Attribution-NonCommercial License <br>
	 *            3 = Attribution-NonCommercial-NoDerivs License <br>
	 *            4 = Attribution License <br>
	 *            5 = Attribution-ShareAlike License <br>
	 *            6 = Attribution-NoDerivs License <br>
	 *            7 = No known copyright restrictions <br>
	 *            8 = United States Government Work <br>
	 * @return Returns a List<FlickrPhoto> with all found photos matching the
	 *         query
	 * 
	 *         This method requires a search term and the kind of licenses
	 *         defined above. It delivers a List of FlickrPhoto objects
	 *         containing the search results
	 */

	public List<FlickrPhoto> getPhotosByQuery(String queryText, String licenses) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license, views, media");
		url.put("license", licenses);
		url.put("sort", "relevance");
		url.put("format", "json");
		String response = makeHttpRequest(url);
		System.out.println(url);
		List<FlickrPhoto> photoIds = new ArrayList<FlickrPhoto>();
		parsePhotoResponse(response, queryText, licenses, photoIds);
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
	 * @param licenses
	 *            This is the licenses string that is passed down from
	 *            getPhotosByQuery()
	 * @return returns a List<FlickrPhoto> with the parsed results
	 * 
	 *         This method is an internal method to parse the Photo results into
	 *         FlickrPhoto objects and puts them into a List
	 */

	private void parsePhotoResponse(String jsonResponse, String queryText,
			String licenses, List<FlickrPhoto> tempList) {

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
				photoTemp.setTitle(photoData.get("title").toString());
				photoTemp.setOwnerName(photoData.get("ownername").toString());
				photoTemp.setViews(Integer.parseInt(photoData.get("views")
						.toString()));
				tempList.add(photoTemp);
			}
			if (page < pages) {
				getNextPage(queryText, licenses, ++page, tempList);
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
	 * @param licenses
	 *            This is the licenses string that is passed down from
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

	private void getNextPage(String queryText, String licenses, int page,
			List<FlickrPhoto> tempList) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license,views,media");
		url.put("license", licenses);
		url.put("sort", "relevance");
		url.put("format", "json");
		url.put("page", page);
		String response = makeHttpRequest(url);
		parsePhotoResponse(response, queryText, licenses, tempList);

	}

	public List<FlickrPhoto> getPhotosByPlaceAndQuery(String queryText,
			String licenses, String placeId, List<FlickrPhoto> photoIds) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license,views,media");
		url.put("license", licenses);
		url.put("sort", "relevance");
		url.put("format", "json");
		url.put("place_id", placeId);
		System.out.println(url);
		String response = makeHttpRequest(url);
		System.out.println(response);
		parsePhotoResponse(response, queryText, licenses, photoIds);
		return photoIds;
	}

	public String getPlaceIdByName(String place) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.places.find");
		url.put("query", place);
		url.put("format", "json");
		String response = makeHttpRequest(url);
		String placeId = parsePlacesResponse(response);
		return placeId;
	}

	public String getPlaceIdByGeoCoord(double lat, double lon) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.places.findByLatLon");
		url.put("lat", lat);
		url.put("lon", lon);
		url.put("format", "json");
		System.out.println(url);
		String response = makeHttpRequest(url);
		String placeId = parsePlacesResponse(response);
		return placeId;
	}

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

	public List<FlickrPhoto> getPhotosByPlaceQueryAndTime(String queryText,
			String licenses, String placeId, Date minTakenDate,
			Date maxTakenDate) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license,views,media");
		url.put("license", licenses);
		url.put("sort", "relevance");
		url.put("format", "json");
		url.put("place_id", placeId);
		url.put("minTakenDate", minTakenDate.getTime());
		url.put("maxTakenDate", maxTakenDate.getTime());
		System.out.println(url);
		String response = makeHttpRequest(url);
		System.out.println(response);
		List<FlickrPhoto> photoIds = new ArrayList<FlickrPhoto>();
		parsePhotoResponse(response, queryText, licenses, photoIds);
		return photoIds;
	}
}
