package lastFMAlbum;

public class LastFMAlbumTest {
	public static void main(String[] args) {
		LastFMAlbumApi test = new LastFMAlbumApi();
		Album mbidTest = test
				.getTracksByMbid("de62ef35-adfd-4b05-b964-b656017880c3");
		Album nameTest = test.getTracksByName("Wintersun", "Wintersun");
		System.out.println(mbidTest.toString());
		System.out.println(nameTest.toString());
	}
}
