package albumDetails;

import java.util.ArrayList;
import java.util.List;

import lastFMAlbum.Album;

public class AlbumDetailsAPI {

	public List<Album> getAlbums(String bandMid) {

		// TODO: get album-mids via band-mids
		List<Album> albums = new ArrayList<Album>();
		albums = FreebaseAlbumSearch.getAlbumMids(bandMid);

		// TODO: get primary-release-mids to alum-mids

		// TODO: get lastfm details to primary-release and store them as an
		// Album List

		// TODO: return Album List.
		return null;
	}
}
