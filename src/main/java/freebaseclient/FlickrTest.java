package freebaseclient;

import java.util.ArrayList;
import java.util.List;

import api.FlickrPhoto;

public class FlickrTest {

	public static void main(String[] args) {
		FlickrAPI flickrTest = new FlickrAPI();
		String query = "wacken";
		String licenses = "4,5,6,7,8";
		List<FlickrPhoto> photoTest = new ArrayList<FlickrPhoto>();

		String placeIDTest = flickrTest.getPlaceIdByName("Koblenz");
		System.out.println(placeIDTest);

		photoTest = flickrTest.getPhotosByQuery(query, licenses);

	}

}
