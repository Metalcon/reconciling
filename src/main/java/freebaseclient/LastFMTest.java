package freebaseclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;

import api.LastFMEventMetaData;

public class LastFMTest {

	public static void main(String[] args) throws IOException, ParseException {
		String query = "ironmaiden";
		LastFMApi lastFMApi = new LastFMApi();

		List<LastFMEventMetaData> eventContainer = new ArrayList<LastFMEventMetaData>();
		eventContainer = lastFMApi.lastFmApiEventCall(query, 2, 0);

		System.out.println(eventContainer.get(0).toString());
		System.out.println(eventContainer.get(1).toString());
	}
}
