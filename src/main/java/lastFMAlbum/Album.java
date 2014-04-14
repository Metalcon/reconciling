package lastFMAlbum;

import java.util.Date;
import java.util.List;

public class Album {

	private String albumName;
	private String artist;
	private String mbid;
	private Date releaseDate;
	private String image;
	private int playcount;
	private List<Track> tracks;

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String name) {
		this.albumName = name;
	}

	public String getMbid() {
		return mbid;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getPlaycount() {
		return playcount;
	}

	public void setPlaycount(int playcount) {
		this.playcount = playcount;
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

	public String toString() {
		String response = "";
		response = "Artist: " + artist + "\n" + "Album: " + albumName + "\n"
				+ "Mbid : " + mbid + "\n" + "Playcount: " + playcount + "\n"
				+ "Image: " + image + "\n" + "Release: " + releaseDate + "\n";
		return response;
	}
}
