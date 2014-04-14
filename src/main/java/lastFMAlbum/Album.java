package lastFMAlbum;

import java.util.Date;
import java.util.List;

public class Album {

	private String name;
	private String Artist;
	private String mbid;
	private Date releaseDate;
	private String image;
	private int playcount;
	private List<Track> album;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return Artist;
	}

	public void setArtist(String artist) {
		Artist = artist;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Track> getAlbum() {
		return album;
	}

	public void setAlbum(List<Track> album) {
		this.album = album;
	}

	public int getPlaycount() {
		return playcount;
	}

	public void setPlaycount(int playcount) {
		this.playcount = playcount;
	}

}
