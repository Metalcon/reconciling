package freebaseclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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

	public List<FlickrPhoto> getPhotosByQuery(String queryText, String licenses) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license");
		url.put("license", licenses);
		url.put("sort", "relevance");
		url.put("format", "json");
		System.out.println(url);
		JSONObject response = makeHttpRequest(url);
		System.out.println(response);
		List<FlickrPhoto> photoIds = new ArrayList<FlickrPhoto>();
		try {
			photoIds = parsePhotoResponse(response, queryText, licenses);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return photoIds;
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

	private List<FlickrPhoto> parsePhotoResponse(JSONObject jsonpResponse,
			String queryText, String licenses) throws IOException,
			org.json.simple.parser.ParseException {

		List<FlickrPhoto> tempList = new ArrayList<FlickrPhoto>();
		String jsonpTemp = jsonpResponse.toString();
		String jsonResponse = jsonpTemp.substring(jsonpTemp.indexOf("(") + 1,
				jsonpTemp.lastIndexOf(")"));
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
				photoTemp.setMediaStatus(photoData.get("key").toString());
				photoTemp.setOwnerName(photoData.get("ownername").toString());
				photoTemp.setViews(Integer.parseInt(photoData.get("views")
						.toString()));
				tempList.add(photoTemp);
			}
			if (page < pages) {
				getNextPage(queryText, licenses, 2, tempList);
			}
		} catch (ClassCastException ce) {
			System.err
					.println("Typecast failed. Response is probably broken. Can be caused by bad request");
		}
		// if (page < pages) {request next page}
		return tempList;
	}

	private void getNextPage(String queryText, String licenses, int page,
			List<FlickrPhoto> tempList) {
		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("text", queryText);
		url.put("extras", "url_o,owner_name,license");
		url.put("license", licenses);
		url.put("sort", "relevance");
		url.put("format", "json");
		url.put("page", page);
		JSONObject jsonpResponse = makeHttpRequest(url);
		String jsonpTemp = jsonpResponse.toString();
		String jsonResponse = jsonpTemp.substring(jsonpTemp.indexOf("(") + 1,
				jsonpTemp.lastIndexOf(")"));
		JSONParser jsonparser = new JSONParser();
		try {
			JSONObject response = (JSONObject) jsonparser.parse(jsonResponse);
			JSONObject responsePhotos = (JSONObject) response.get("photos");
			int newPage = Integer.parseInt(responsePhotos.get("page")
					.toString());
			int pages = Integer
					.parseInt(responsePhotos.get("pages").toString());
			JSONArray photoList = (JSONArray) responsePhotos.get("photo");
			for (int i = 0; i < photoList.size(); ++i) {
				FlickrPhoto photoTemp = new FlickrPhoto();
				JSONObject photoData = (JSONObject) photoList.get(i);
				photoTemp.setUrl(photoData.get("url_o").toString());
				photoTemp.setLicense(photoData.get("license").toString());
				photoTemp.setMediaStatus(photoData.get("key").toString());
				photoTemp.setOwnerName(photoData.get("ownername").toString());
				photoTemp.setViews(Integer.parseInt(photoData.get("views")
						.toString()));
				tempList.add(photoTemp);
			}
			if (newPage < pages) {
				getNextPage(queryText, licenses, newPage, tempList);
			}
		} catch (ClassCastException ce) {
			System.err
					.println("Typecast failed. Response is probably broken. Can be caused by bad request");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
