package freebaseclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;

import api.LastFMEventMetaData;

public class LastFMTest {

	public static void main(String[] args) throws IOException, ParseException {
		String query = "iron maiden";
		LastFMApi lastFMApi = new LastFMApi();

		List<LastFMEventMetaData> eventContainer = new ArrayList<LastFMEventMetaData>();
		eventContainer = lastFMApi.lastFmApiEventCall(query, 10, 1);
		for (int i = 0; i < eventContainer.size(); i++) {
			System.out.println("Element " + (i + 1));
			System.out.println(eventContainer.get(i).toString());
		}
	}
}
