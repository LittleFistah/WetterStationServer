package de.karcher.jan.server.util;

public class WetterData {

	private int hauptregion;
	private int region;
	private double temperatur;
	private int status;
	private int windstaerke;

	public WetterData(int hauptregion, int region, double temperatur, int status, int windstaerke) {
		this.hauptregion = hauptregion;
		this.region = region;
		this.temperatur = temperatur;
		this.status = status;
		this.windstaerke = windstaerke;
	}

	public int getHauptregion() {
		return hauptregion;
	}

	public int getRegion() {
		return region;
	}

	public double getTemperatur() {
		return temperatur;
	}

	public int getStatus() {
		return status;
	}

	public int getWindstaerke() {
		return windstaerke;
	}

	public void setTemperatur(double temperatur) {
		this.temperatur = temperatur;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setWindstaerke(int windstaerke) {
		this.windstaerke = windstaerke;
	}
}
