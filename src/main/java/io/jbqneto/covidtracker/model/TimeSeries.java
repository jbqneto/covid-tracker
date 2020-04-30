package io.jbqneto.covidtracker.model;


public class TimeSeries {
	public static final String SERVICE_CONFIRMED_GLOBAL = "confirmed_global";
	public static final String SERVICE_RECOVERED_GLOBAL = "recovered_global";
	public static final String SERVICE_DEATHS_GLOBAL = "deaths_global";

	private String country;
	private String state;
	private long latitude;
	private long longitude;
	private String[] dates;
	private int[] values;
	private String service;
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public long getLatitude() {
		return latitude;
	}
	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}
	public long getLongitude() {
		return longitude;
	}
	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}
	public String[] getDates() {
		return dates;
	}
	public void setDates(String[] dates) {
		this.dates = dates;
	}
	public int[] getValues() {
		return values;
	}
	public void setValues(int[] values) {
		this.values = values;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	
}
