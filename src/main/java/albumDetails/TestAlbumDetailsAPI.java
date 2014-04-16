package albumDetails;

import java.util.ArrayList;
import java.util.List;

import lastFMAlbum.Album;

public class TestAlbumDetailsAPI {

	public static void main(String[] args) {
		List<Album> output = new ArrayList<Album>();
		AlbumDetailsAPI testApi = new AlbumDetailsAPI();
		output = testApi.getAlbums("/m/04rcr");
		for (int i = 0; i < output.size(); i++) {
			System.out.println(output.get(i).toString());
		}

	}
}
