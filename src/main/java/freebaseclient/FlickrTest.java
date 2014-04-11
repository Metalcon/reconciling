package freebaseclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import api.FlickrPhoto;

public class FlickrTest {

	public static void main(String[] args) throws IOException {
		FlickrAPI flickrTest = new FlickrAPI();
		String query = "wacken";
		String licenses = "4,5,6,7,8";
		List<FlickrPhoto> photoTest = new ArrayList<FlickrPhoto>();
		/*
		 * lizenzen rausschmeißen als übergabe, hardcoded foto id übernehmen in
		 * datentyp url zusammenbasteln aus foto id und username
		 * https://www.flickr.com/photos/rs-foto/12138916916/ place id nicht
		 * mehr manuel auflösen sondern im call selbst suche kombinieren mit
		 * ortsnamen oder geocoordinate tags mit reinnehmen ins datenobjekt und
		 * mit abspeichern
		 */
		photoTest = flickrTest.getPhotosByPlaceAndQuery("Band", licenses,
				"_4rcXJpXVL054cc");
		// photoTest = flickrTest.getPhotosByQuery(query, licenses);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				"test.html")));
		for (int i = 0; i < Math.min(50, photoTest.size()); i++) {
			System.out.println(photoTest.get(i).toString());
			bw.write(photoTest.get(i).toString());
		}
		System.out.println(photoTest.get(0).toString());
		// photoTest = flickrTest.getPhotosByQuery(query, licenses);
		bw.close();

	}
}
