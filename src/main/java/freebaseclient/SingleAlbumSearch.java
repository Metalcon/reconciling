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

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class SingleAlbumSearch {
	public static Properties properties = new Properties();

	public static void main(String[] args) {
		try {
			properties.load(new FileInputStream("freebase.properties"));
			HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory requestFactory = httpTransport
					.createRequestFactory();
			JSONParser parser = new JSONParser();
			GenericUrl url = new GenericUrl(
					"https://www.googleapis.com/freebase/v1/mqlread");

			// TODO: implement reading mid from file
			String bandMid = "/m/014_xj";
			String query = "[{\"mid\":\""
					+ bandMid
					+ "\",\"/music/artist/album\":[{\"name\":null , \"mid\":null}]}]";
			url.put("query", query);

			System.out.println("my API-Key: " + properties.get("API_KEY"));
			url.put("key", properties.get("API_KEY"));
			HttpRequest request = requestFactory.buildGetRequest(url);
			System.out.println("url: " + url);
			HttpResponse httpResponse = request.execute();
			JSONObject response = (JSONObject) parser.parse(httpResponse
					.parseAsString());
			JSONArray candidates = (JSONArray) response.get("candidate");
			// for (Object candidate : candidates) {
			// System.out.print(JsonPath.read(candidate,"$.mid").toString()+" | "
			// + JsonPath.read(candidate,"$.name").toString()+" | " +
			// JsonPath.read(candidate,"$.notable.name").toString() + " (");
			// System.out.println(JsonPath.read(candidate,"$.confidence").toString()+")");
			// }
			System.out.println(response.toString());
			// TODO: implement extracting information (and writing to file)
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<String> returnAlbums(String bandMid) {
		try {
			properties.load(new FileInputStream("freebase.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
		JSONParser parser = new JSONParser();
		GenericUrl url = new GenericUrl(
				"https://www.googleapis.com/freebase/v1/mqlread");
		String query = "[{\"mid\":\""
				+ bandMid
				+ "\",\"/music/artist/album\":[{\"name\":null , \"mid\":null}]}]";
		url.put("query", query);
		url.put("key", properties.get("API_KEY"));
		try {
			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			JSONObject response = (JSONObject) parser.parse(httpResponse
					.parseAsString());
			JSONArray candidates = (JSONArray) response.get("candidate");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> returnList = new ArrayList<String>();
		return returnList;
	}
}
