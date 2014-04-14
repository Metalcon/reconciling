package lastFMAlbum;

public class LastFMAlbumTest {
	public static void main(String[] args) {
		LastFMAlbumApi test = new LastFMAlbumApi();
		String testMbid = "dbf1b3c0-1efb-4e5f-9fe1-4c42b144aa24";
		String testAlbum = "Reign in blood";
		String testArtist = "Slayer";
		Album testBoth = test.getTracksByMuidOrName(testMbid, testAlbum,
				testArtist);
		System.out.println(testBoth.toString());

		// Album mbidTest = test.getTracksByMbid(testMbid);
		// Album nameTest = test.getTracksByName(testArtist, testAlbum);
		// if (mbidTest != null) {
		// System.out.println(mbidTest.toString());
		// }
		// if (nameTest != null) {
		// System.out.println(nameTest.toString());
		// }

	}
}
