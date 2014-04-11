package api;

import java.util.List;

public class FlickrPhoto {
	String title;
	String photoId;
	String url;
	String mediaStatus;
	String ownerName;
	List<String> tags;
	String license;
	int views;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMediaStatus() {
		return mediaStatus;
	}

	public void setMediaStatus(String mediaStatus) {
		this.mediaStatus = mediaStatus;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String toString() {
		String response = "";
		response = "------------------------" + "\n" + "Titel: " + title + "\n"
				+ "Id: " + photoId + "\n" + "Url:<img src=\" " + url
				+ "\" width=\"400px\" >\n" + "MediaStatus: " + mediaStatus
				+ "\n" + "Owner-Name: " + ownerName + "\n" + "License: "
				+ license + "\n" + "Tags:" + tags + "\n" + "Views: " + views
				+ "\n <br>";
		return response;
	}

	/**
	 * 
	 * @return String containing the finished flickr-link to the picture
	 * 
	 *         This method retrieves a flickr link that brings you to the flickr
	 *         page for that image
	 */
	public String getLink() {
		String response = "";
		response = "https://www.flickr.com/photos/" + ownerName + "/" + photoId;
		return response;
	}
}
