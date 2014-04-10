package api;

public class FlickrPhoto {
	String title;
	String url;
	String mediaStatus;
	String ownerName;
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

	public String toString() {
		String response = "";
		response = "------------------------" + "\n" + "Titel: " + title + "\n"
				+ "Url: " + url + "\n" + "MediaStatus: " + mediaStatus + "\n"
				+ "Owner-Name: " + ownerName + "\n" + "License: " + license
				+ "\n" + "Views: " + views + "\n";
		return response;
	}
}
