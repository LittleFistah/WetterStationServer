package de.karcher.jan.server.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import de.karcher.jan.server.controller.Server;
import de.karcher.jan.server.util.Logger;
import de.karcher.jan.server.util.ServerConfig;
import de.karcher.jan.server.util.Tags;

public class ListenerClient extends Thread {

	private Server server;
	private Logger logger;
	private ServerSocket socket;
	private ServerConfig cfg;
	private Socket client;

	public ListenerClient(Server server, Logger logger, ServerSocket socket, ServerConfig cfg) {
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
			logger.addDefaultLog(Tags.LCLIENT.print(0) + e.getMessage());
		}
		this.start();
	}

	@Override
	public void run() {
		cfg.setListeningClient(true);
		logger.addDefaultLog(Tags.LCLIENT.print(0) + "Listener auf " + socket.getInetAddress().getHostAddress() + ":"
				+ socket.getLocalPort() + " wurde gestartet.");
		while (cfg.isListeningClient()) {
			try {
				client = socket.accept();
				if (client.isConnected()) {
					logger.addDefaultLog(Tags.LCLIENT.print(0) + "Neuer Client verbunden.");
					server.reportClient(client);
				}
			} catch (SocketTimeoutException toe) {
			} catch (IOException e) {
				logger.addDefaultLog(Tags.LSTATION.print(0) + e.getMessage());
			}
		}
	}

}
