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
	private String headliner;
	private String eventWebsite;
	private List<String> tags;

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

	public String getHeadliner() {
		return headliner;
	}

	public void setHeadliner(String headliner) {
		this.headliner = headliner;
	}

	public String getEventWebsite() {
		return eventWebsite;
	}

	public void setEventWebsite(String eventWebsite) {
		this.eventWebsite = eventWebsite;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String toString() {
		String response = "------------------------\n";
		response += "Event-ID: " + eventId + "\n" + "Event-Title: " + title
				+ "\n" + "Artists: " + artists + "\n" + "Headliner: "
				+ headliner + "\n" + "Start-Date: " + startDate + "\n"
				+ "End-Date: " + endDate + "\n" + "Event-Website: "
				+ eventWebsite + "\n" + "Tags: " + tags + "\n"
				+ "Venue-Informations: \n" + venue.toString() + "\n";
		return response;
	}

}
