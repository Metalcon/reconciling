package api;

import java.util.Date;
import java.util.List;

public class LastFMEventMetaData {

	private String artist;
	private Venue venue;
	private Date startDate;
	private Date endDate;
	private int eventId;
	private String title;
	private List<String> artists;

	// isFestival()
	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public List<String> getArtists() {
		return artists;
	}

	public void setArtists(List<String> artists) {
		this.artists = artists;
	}

	public boolean isFestival() {
		if (endDate == null) {
			return false;
		} else {
			return true;
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String toString() {
		String response = "------------------------\n";
		response += "Event-ID: " + getEventId() + "\n" + "Event-Title: "
				+ getTitle() + "\n" + "Artists: " + getArtists() + "\n"
				+ "Start-Date: " + startDate + "\n" + "End-Date: " + endDate
				+ "\n" + "Venue-Informations: \n" + venue.toString() + "\n";
		return response;
	}
}
