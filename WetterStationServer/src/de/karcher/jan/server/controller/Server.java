package de.karcher.jan.server.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import de.karcher.jan.server.gui.MainFrame;
import de.karcher.jan.server.handler.HandlerStation;
import de.karcher.jan.server.listener.ListenerStation;
import de.karcher.jan.server.manager.DataManager;
import de.karcher.jan.server.util.Logger;
import de.karcher.jan.server.util.ServerConfig;
import de.karcher.jan.server.util.Tags;
import de.karcher.jan.server.util.WetterData;

public class Server {

	private Logger logger;
	private ServerConfig serverConfig;
	private DataManager dataManager;
	private ServerSocket serverSocketStation, serverSocketClient;
	private ListenerStation lStation;
	private MainFrame window;

	private int stationId, stationCount;

	private ArrayList<HandlerStation> listStation;

	public Server() {
		init();
	}

	private void init() {
		serverConfig = new ServerConfig();
		dataManager = new DataManager();
		logger = new Logger();

		try {
			serverSocketStation = new ServerSocket(serverConfig.getPortStation());
			serverSocketClient = new ServerSocket(serverConfig.getPortClient());
		} catch (IOException e) {
			logger.addDefaultLog(e.getMessage());
		}

		listStation = new ArrayList<HandlerStation>();

		lStation = new ListenerStation(this, logger, serverSocketStation, serverConfig);
		
		window = new MainFrame(this);
	}

	public void reportStation(Socket station) {
		logger.addDefaultLog(Tags.SERVER.print(0) + "Neue Station anmelden.");
		listStation.add(new HandlerStation(this, logger, station, ++stationId));
		stationCount++;
	}
	
	public void shutdownServer(){
		System.out.println("shutdown Server");
	}
	
	public synchronized void shutdownStation(int id){
		for (HandlerStation hs : listStation){
			if(hs.getStationId() == id){
				listStation.remove(hs);
				stationCount--;
			}
		}
	}
	
	public synchronized void setData(int hr, int r, char typ, int value, double value1) {
		dataManager.setData(hr, r, typ, value, value1);
	}
	public synchronized ArrayList<WetterData> getAllData(){
		return dataManager.getAllData();
	}
	public synchronized int getDefaultUITimeout(){
		return serverConfig.getDefaultUITimeout();
	}
}
