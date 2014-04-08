package freebaseclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;

import api.LastFMEventMetaData;

public class LastFMTest {

	public static void main(String[] args) throws IOException, ParseException {
		String mbid = "ca891d65-d9b0-4258-89f7-e6ba29d83767";
		String query = "amon amarth";
		LastFMApi lastFMApi = new LastFMApi();

		List<LastFMEventMetaData> eventContainer = new ArrayList<LastFMEventMetaData>();
		List<LastFMEventMetaData> eventContainer2 = new ArrayList<LastFMEventMetaData>();

		eventContainer = lastFMApi.lastFmApiBandnameEventCall(query, 20, 0);
		eventContainer2 = lastFMApi.lastFMApiMbidEventCall(mbid, 10, 1);

		for (int i = 0; i < eventContainer.size(); i++) {
			System.out.println("Element " + (i + 1));
			System.out.println(eventContainer.get(i).toString());
		}

		for (int i = 0; i < eventContainer2.size(); i++) {
			System.out.println("Element " + (i + 1));
			System.out.println(eventContainer2.get(i).toString());
		}
	}
}
