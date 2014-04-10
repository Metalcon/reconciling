package freebaseclient;

public class FlickrTest {

	public static void main(String[] args) {
		FlickrAPI flickrTest = new FlickrAPI();
		String query = "wacken";
		String licenses = "4,5,6,7,8";
		flickrTest.getPhotosByQuery(query, licenses);
	}

}
