package albumDetails;

public class MusicbrainzAlbum {

	private String mid;
	private String mbid;
	private String artist;
	private String album;

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getMbid() {
		return mbid;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	@Override
	public String toString() {
		return "MusicbrainzAlbum [mid=" + mid + ", mbid=" + mbid + ", artist="
				+ artist + ", album=" + album + "]";
	}

}
