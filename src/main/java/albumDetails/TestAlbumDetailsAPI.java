package albumDetails;

import java.util.ArrayList;
import java.util.List;

import lastFMAlbum.Album;

public class TestAlbumDetailsAPI {

	public static void main(String[] args) {
		List<Album> output = new ArrayList<Album>();
		AlbumDetailsAPI testApi = new AlbumDetailsAPI();
		String ensiferum = "/m/03y2lh";
		String metallica = "/m/04rcr";
		String wintersun = "/m/04473z";
		String inflames = "/m/0370w8";
		String amonamarth = "/m/03v5x0";
		output = testApi.getAlbums(wintersun);
		for (int i = 0; i < output.size(); i++) {
			System.out.println(output.get(i).toString());
		}

	}
}
