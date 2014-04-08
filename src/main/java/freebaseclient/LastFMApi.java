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

import api.GeoLocation;
import api.LastFMEventMetaData;
import api.Venue;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class LastFMApi {

	public Properties properties = new Properties();

	/**
	 * 
	 * @param bandName
	 *            defines the band you want to retrieve events for
	 * @param maxResults
	 *            Defines the number of results you want to get (1-50)
	 * @param festivalsOnly
	 *            Can select to show only festivals (1 for only Festivals, 0 for
	 *            all events)
	 * @throws IOException
	 * @throws ParseException
	 * @return Returns a container from the type ArrayList<LastFMEventMetaData>
	 *         containing the informations to the events for the requested band
	 * 
	 *         This method can look up events for one specific band. It can
	 *         retrieve between one and fifty results at the same time. It
	 *         returns the looked up events as an ArrayList containing the
	 *         filled LastFMEventMetaData objects
	 */

	public List<LastFMEventMetaData> lastFmApiEventCall(String bandName,
			int maxResults, int festivalsOnly) {
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
		url.put("method", "artist.getevents");
		url.put("artist", bandName);
		url.put("format", "json");
		url.put("limit", maxResults);
		url.put("autocorrect", "1");
		url.put("festivalsonly", festivalsOnly);
		url.put("api_key", properties.get("API_KEY"));
		JSONObject response = makeHttpRequest(url);
		System.out.println(url);
		return processingSearchResults(response);
	}

	/**
	 * helper function to make an http request to lastFM
	 * 
	 * @param url
	 * @return
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
	 * @param response
	 *            is the response produced in the common call to the api in the
	 *            method lastFmApiEventCall
	 * @return container Is containing all informations extracted from the
	 *         lastFM database for the call of all events of one specific
	 *         band.It is stored in an ArrayList<LastFMEventMetaData>
	 * 
	 *         This method is a help method for the lastFMApiEventCall to
	 *         outsource the processing. Here the response JSON is beeing read
	 *         out and all relevant informations put into LastFMEventMetaData
	 *         objects.
	 * 
	 */

	private List<LastFMEventMetaData> processingSearchResults(
			JSONObject response) {
		JSONObject responseEvents = (JSONObject) response.get("events");
		List<LastFMEventMetaData> container = new ArrayList<LastFMEventMetaData>();
		if (responseEvents.containsKey("event")
				&& responseEvents.containsKey("event")) {
			JSONObject responseAttr = (JSONObject) responseEvents.get("@attr");
			int responseLimit = Integer.parseInt(responseAttr.get("perPage")
					.toString());
			int responseTotal = Integer.parseInt(responseAttr.get("total")
					.toString());
			if (responseLimit > 1 && responseTotal > 1) {
				JSONArray responseEventsEvent = (JSONArray) responseEvents
						.get("event");
				for (int i = 0; i < responseEventsEvent.size(); i++) {
					LastFMEventMetaData temp = new LastFMEventMetaData();
					JSONObject responseEventEntry = (JSONObject) responseEventsEvent
							.get(i);
					processingData(responseEventEntry, temp);
					container.add(temp);
				}
			} else {
				LastFMEventMetaData temp = new LastFMEventMetaData();
				JSONObject responseEventsEvent = (JSONObject) responseEvents
						.get("event");
				processingData(responseEventsEvent, temp);
				container.add(temp);
			}

			return container;
		} else {
			System.out.println("No entries found!");
			return container;
		}
	}

	/**
	 * 
	 * @param responseEventEntry
	 *            is a single JSONObject for lastFM, already filled with only
	 *            the content of "event"
	 * @param temp
	 *            is the temporary LastFMEventMetaData object created to be put
	 *            into the container later
	 * 
	 *            This is a internal helper method to fill a object from the
	 *            type LastFMMetaData with the values from the JSONField "event"
	 */

	private void processingData(JSONObject responseEventEntry,
			LastFMEventMetaData temp) {

		// fills the id field

		if (testsIfFilled(responseEventEntry, "id")) {
			temp.setEventId(Integer.parseInt(responseEventEntry.get("id")
					.toString()));
		}

		// fills the title field
		if (testsIfFilled(responseEventEntry, "title")) {
			temp.setTitle(responseEventEntry.get("title").toString());
		}

		// fills the field Artists
		JSONObject responseEventsEventArtists = (JSONObject) responseEventEntry
				.get("artists");
		Object typeTest = responseEventsEventArtists.get("artist");
		List<String> tempArtistList = new ArrayList<String>();
		if (typeTest instanceof JSONArray) {
			JSONArray responseEventsEventArtistsArtist = (JSONArray) typeTest;
			if (!responseEventsEventArtistsArtist.isEmpty()) {
				for (int j = 0; j < responseEventsEventArtistsArtist.size(); j++) {
					tempArtistList.add(responseEventsEventArtistsArtist.get(j)
							.toString());
				}
				temp.setArtists(tempArtistList);
			}
		} else {
			if (testsIfFilled(responseEventEntry, "artists"))
				tempArtistList
						.add(responseEventEntry.get("artists").toString());
			temp.setArtists(tempArtistList);
		}

		// fills the field venue
		JSONObject responseEventEntryVenue = (JSONObject) responseEventEntry
				.get("venue");
		Venue venueTemp = new Venue();

		// fills the field VenueId
		if (testsIfFilled(responseEventEntryVenue, "id")) {
			venueTemp.setVenueId(responseEventEntryVenue.get("id").toString());
		}
		// fills the field VenueName
		if (testsIfFilled(responseEventEntryVenue, "name")) {
			venueTemp.setVenueName(responseEventEntryVenue.get("name")
					.toString());
		}
		JSONObject responseEventEntryVenueLocation = (JSONObject) responseEventEntryVenue
				.get("location");
		JSONObject responseEventEntryVenueLocationGeo = (JSONObject) responseEventEntryVenueLocation
				.get("geo:point");

		// creates a geolocation for the venue from the type
		// GeoLocation()
		GeoLocation geoTemp = new GeoLocation();

		if (testsIfFilled(responseEventEntryVenueLocationGeo, "geo:lat")) {
			geoTemp.setGeoLat(Double
					.parseDouble(responseEventEntryVenueLocationGeo.get(
							"geo:lat").toString()));
		} else {
			geoTemp.setGeoLat(null);
		}
		if (testsIfFilled(responseEventEntryVenueLocationGeo, "geo:long")) {
			geoTemp.setGeoLong(Double
					.parseDouble(responseEventEntryVenueLocationGeo.get(
							"geo:long").toString()));
		} else {
			geoTemp.setGeoLong(null);
		}
		venueTemp.setGeoLocation(geoTemp);

		// fills the city field
		if (testsIfFilled(responseEventEntryVenueLocation, "city")) {
			venueTemp.setCity(responseEventEntryVenueLocation.get("city")
					.toString());
		}
		// fills the county field
		if (testsIfFilled(responseEventEntryVenueLocation, "country")) {
			venueTemp.setCountry(responseEventEntryVenueLocation.get("country")
					.toString());
		}
		// fills the street field
		if (testsIfFilled(responseEventEntryVenueLocation, "street")) {
			venueTemp.setStreet(responseEventEntryVenueLocation.get("street")
					.toString());
		}
		// fills the postalcode field
		if (testsIfFilled(responseEventEntryVenueLocation, "postalcode")) {
			venueTemp.setPostalCode(responseEventEntryVenueLocation.get(
					"postalcode").toString());
		}
		// fills the website field
		if (testsIfFilled(responseEventEntryVenue, "url")) {
			venueTemp.setVenueWebsite(responseEventEntryVenue.get("url")
					.toString());
		}
		temp.setVenue(venueTemp);

		// fills the startDate field
		if (testsIfFilled(responseEventEntry, "startDate")) {
			String tempFormatDate = responseEventEntry.get("startDate")
					.toString();
			DateFormat formatter = new SimpleDateFormat(
					"EEE', 'dd MMM yyyy HH:mm:ss");
			Date date = null;
			try {
				date = formatter.parse(tempFormatDate);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			temp.setStartDate(date);
		}

		// fills the endDate field
		if (responseEventEntry.containsKey("endDate")) {
			String tempFormatDate = responseEventEntry.get("endDate")
					.toString();
			DateFormat formatter = new SimpleDateFormat(
					"EEE', 'dd MMM yyyy HH:mm:ss");
			Date date = null;
			try {
				date = formatter.parse(tempFormatDate);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			temp.setEndDate(date);
		}
	}

	/**
	 * 
	 * @param jsonTemp
	 * @param type
	 * @return
	 * 
	 *         This method tests if a specific field is filled or not. Needed
	 *         before filling the fields, otherwise the programm would get
	 *         NullPointerExceptions for empty fields. Returns true if the
	 *         JSONField contains a string, false if it is empty
	 */

	private boolean testsIfFilled(JSONObject jsonTemp, String type) {
		if (jsonTemp.get(type).equals("")) {
			return false;
		}
		if (jsonTemp.get(type) != null) {
			return true;
		} else {
			return false;
		}
	}
}
