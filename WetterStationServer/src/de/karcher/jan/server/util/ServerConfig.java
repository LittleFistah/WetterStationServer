package de.karcher.jan.server.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerConfig {

	private String ip;
	private final int portStation = 5555;
	private final int portClient = 6666;
	private final int defaultTimeout = 1500;
	private final int defaultUITimeout = 5000;
	private final int clientIntervall = 10000;
	private boolean isRunning, isListeningStation, isListeningClient;

	public ServerConfig() {
		try {
			this.ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.isRunning = true;
	}

	public String getIp() {
		return ip;
	}

	public int getPortStation() {
		return portStation;
	}

	public int getPortClient() {
		return portClient;
	}

	public boolean isListeningClient() {
		return isListeningClient;
	}

	public boolean isListeningStation() {
		return isListeningStation;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setListeningClient(boolean isListeningClient) {
		this.isListeningClient = isListeningClient;
	}

	public void setListeningStation(boolean isListeningStation) {
		this.isListeningStation = isListeningStation;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public int getDefaultTimeout() {
		return defaultTimeout;
	}

	public int getDefaultUITimeout() {
		return defaultUITimeout;
	}

	public int getClientIntervall() {
		return clientIntervall;
	}
}
