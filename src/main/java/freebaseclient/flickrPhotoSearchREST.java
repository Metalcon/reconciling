package freebaseclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
			ParserConfigurationException, SAXException {
		try {
			properties.load(new FileInputStream("flickr.properties"));
		} catch (FileNotFoundException fnfe) {
			System.err.println("missing flickr properties");
		}

		GenericUrl url = new GenericUrl("https://api.flickr.com/services/rest/");

		String place = "Koblenz";
		String placeId = getPlaceId(place);
		System.out.println(placeId);

	}

	private static String getPlaceId(String place) throws IOException,
			ParserConfigurationException, SAXException {
		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
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
		System.out.println(xmlResponse);
		NodeList places = document.getElementsByTagName("places");
		for (int i = 0; i < places.getLength(); i++) {
			Element place = (Element) places.item(i);
			Node title = place.getElementsByTagName("place").item(0);
			NamedNodeMap nnm = title.getAttributes();
			tempList.add(nnm.getNamedItem("place_id").toString());
		}
		return tempList;
	}

	private static String getPhotosFromPlace(String placeId) throws IOException {

		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();
		url.put("api_key", properties.get("API_KEY"));
		url.put("method", "flickr.photos.search");
		url.put("place_id", placeId);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse response = request.execute();
		System.out.println(response.parseAsString());
		return response.parseAsString();
	}
}
