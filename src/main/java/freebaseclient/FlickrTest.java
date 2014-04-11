package freebaseclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import api.FlickrPhoto;

public class FlickrTest {

	public static void main(String[] args) throws IOException {
		FlickrAPI flickrTest = new FlickrAPI();
		String query = "wacken 2012";
		String placeName = "wacken";
		String maxDateString = "Aug 04 19:00:00 CEST 2010";
		String minDateString = "Aug 04 19:00:00 CEST 2010";
		double lat = 54.02261;
		double lon = 9.37995;
		Date maxDate = new Date();
		Date minDate = new Date();
		DateFormat df = new SimpleDateFormat("MMM dd kk:mm:ss zzz yyyy");
		try {
			maxDate = df.parse(maxDateString);
			minDate = df.parse(minDateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<FlickrPhoto> photoTest = new ArrayList<FlickrPhoto>();
		// photoTest = flickrTest.getPhotosByQuery(query);
		// photoTest = flickrTest.getPhotosByQueryAndPlace(query, placeName);
		// photoTest = flickrTest.getPhotosByQueryAndGeoCoord(query, 54.02261,
		// 9.37995);
		// photoTest = flickrTest.getPhotosByQueryGeoCoordAndTime(query, lat,
		// lon, minDate, maxDate);
		photoTest = flickrTest.getPhotosByQueryPlaceAndTime(query, placeName,
				minDate, maxDate);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				"test.html")));
		for (int i = 0; i < Math.min(20, photoTest.size()); i++) {
			System.out.println(photoTest.get(i).toString());
			bw.write(photoTest.get(i).toString());
		}
		bw.close();
		System.out.println("done!");

	}
}
