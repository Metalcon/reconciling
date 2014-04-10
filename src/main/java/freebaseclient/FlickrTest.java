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
		photoTest = flickrTest.getPhotosByQuery(query, licenses);
		System.out.println(photoTest.get(0).toString());
	}

}
