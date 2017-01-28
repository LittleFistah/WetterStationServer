package de.karcher.jan.server.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import de.karcher.jan.server.controller.Server;
import de.karcher.jan.server.util.Logger;
import de.karcher.jan.server.util.ServerConfig;
import de.karcher.jan.server.util.Tags;

public class ListenerStation extends Thread {

	private Server server;
	private Logger logger;
	private ServerSocket socket;
	private ServerConfig cfg;
	private Socket station;

	public ListenerStation(Server server, Logger logger, ServerSocket socket, ServerConfig cfg) {
		init(server, logger, socket, cfg);
	}

	private void init(Server server, Logger logger, ServerSocket socket, ServerConfig cfg) {
		this.server = server;
		this.logger = logger;
		this.socket = socket;
		this.cfg = cfg;
		try {
			socket.setSoTimeout(cfg.getDefaultTimeout());
		} catch (SocketException e) {
			logger.addDefaultLog(Tags.LSTATION.print() + e.getMessage());
		}
		this.start();
	}

	@Override
	public void run() {
		cfg.setListeningStation(true);
		logger.addDefaultLog(Tags.LSTATION.print() + "Listener auf " + socket.getInetAddress().getHostAddress() + ":"
				+ socket.getLocalPort() + " wurde gestartet.");
		while (cfg.isListeningStation()) {
			try {
				station = socket.accept();
				if (station.isConnected()) {
					logger.addDefaultLog(Tags.LSTATION.print() + "Neue Station verbunden.");
					server.reportStation(station);
				}
			} catch (SocketTimeoutException toe) {
			} catch (IOException e) {
				logger.addDefaultLog(Tags.LSTATION.print() + e.getMessage());
			}
		}
	}
}
