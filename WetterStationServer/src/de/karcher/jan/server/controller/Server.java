package de.karcher.jan.server.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import de.karcher.jan.server.handler.HandlerStation;
import de.karcher.jan.server.listener.ListenerStation;
import de.karcher.jan.server.util.Logger;
import de.karcher.jan.server.util.ServerConfig;
import de.karcher.jan.server.util.Tags;

public class Server {

	private Logger logger;
	private ServerConfig serverConfig;
	private ServerSocket serverSocketStation, serverSocketClient;
	private ListenerStation lStation;

	private int stationId,stationCount;

	private ArrayList<HandlerStation> listStation;

	public Server() {
		init();
	}

	private void init() {
		serverConfig = new ServerConfig();
		logger = new Logger();

		try {
			serverSocketStation = new ServerSocket(serverConfig.getPortStation());
			serverSocketClient = new ServerSocket(serverConfig.getPortClient());
		} catch (IOException e) {
			logger.addDefaultLog(e.getMessage());
		}

		listStation = new ArrayList<HandlerStation>();

		lStation = new ListenerStation(this, logger, serverSocketStation, serverConfig);
	}

	public void reportStation(Socket station) {
		logger.addDefaultLog(Tags.SERVER.print() + "Neue Station anmelden.");
		listStation.add(new HandlerStation(this, station, ++stationId));
		stationCount++;
	}
}
