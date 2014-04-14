package lastFMEvents;

public class GeoLocation {

	private Double geoLat;
	private Double geoLong;

	public Double getGeoLat() {
		return geoLat;
	}

	public void setGeoLat(Double geoLat) {
		this.geoLat = geoLat;
	}

	public Double getGeoLong() {
		return geoLong;
	}

	public void setGeoLong(Double geoLong) {
		this.geoLong = geoLong;
	}

	public String toString() {
		String response = "";
		response = "Latitude: " + geoLat + "\n" + "Longitude: " + geoLong
				+ "\n";
		return response;
	}

}
