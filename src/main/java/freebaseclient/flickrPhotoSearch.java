package freebaseclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * An experimental implementation of Flickr Queries. Aiming to request data
 * relevant to our Metalcon interests from Flickr.
 * 
 * @author Christian Schowalter
 * 
 */
public class flickrPhotoSearch {
	public static Properties properties = new Properties();

	public static void main(String[] args) throws IOException {
		try {
			properties.load(new FileInputStream("flickr.properties"));
		} catch (FileNotFoundException fnfe) {
			System.err.println("missing flickr properties");
		}

	}
}
