package de.karcher.jan.server.handler;

import java.net.Socket;

import de.karcher.jan.server.controller.Server;

public class HandlerStation extends Thread {

	private Socket station;
	private int stationId;

	public HandlerStation(Server server, Socket station, int id) {
		init(server, station, id);
	}

	private void init(Server server, Socket station, int id) {
		this.station = station;
		this.stationId = id;
	}

	@Override
	public void run() {
		while (true) {

		}
	}
}
