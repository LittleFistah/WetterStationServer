package de.karcher.jan.server.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.jws.HandlerChain;

import de.karcher.jan.server.gui.MainFrame;
import de.karcher.jan.server.handler.HandlerClient;
import de.karcher.jan.server.handler.HandlerStation;
import de.karcher.jan.server.listener.ListenerClient;
import de.karcher.jan.server.listener.ListenerStation;
import de.karcher.jan.server.manager.DBManager;
import de.karcher.jan.server.manager.DataManager;
import de.karcher.jan.server.util.Logger;
import de.karcher.jan.server.util.ServerConfig;
import de.karcher.jan.server.util.Tags;
import de.karcher.jan.server.util.WetterData;

public class Server {

	private Logger logger;
	private ServerConfig serverConfig;
	private DataManager dataManager;
	private DBManager dbManager;
	private ServerSocket serverSocketStation, serverSocketClient;
	private ListenerStation lStation;
	private ListenerClient lClient;
	private MainFrame window;

	private int stationId, stationCount;
	private int clientId, clientCount;

	private ArrayList<HandlerStation> listStation;
	private ArrayList<HandlerClient> listClient;
	
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
		listClient = new ArrayList<HandlerClient>();

		lStation = new ListenerStation(this, logger, serverSocketStation, serverConfig);
		lClient = new ListenerClient(this, logger, serverSocketClient, serverConfig);
		
		
		dbManager = new DBManager(this, logger);
				window = new MainFrame(this,serverConfig);
	}

	public void reportStation(Socket station) {
		logger.addDefaultLog(Tags.SERVER.print(0) + "Neue Station anmelden.");
		listStation.add(new HandlerStation(this, logger, station, ++stationId));
		stationCount++;
	}
	public void reportClient(Socket client) {
		logger.addDefaultLog(Tags.SERVER.print(0) + "Neuer Client anmelden.");
		listClient.add(new HandlerClient(this, logger, client, ++clientId));
		clientCount++;		
	}
	
	public synchronized void shutdownServer(){
		for(Iterator<HandlerStation> it = listStation.iterator(); it.hasNext();){
			HandlerStation x = it.next();
			x.shutdownServer();
			it.remove();
		}
		
		
		System.exit(0);
	}
	
	public synchronized void shutdownStation(int id){
		for(Iterator<HandlerStation> it = listStation.iterator(); it.hasNext();){
			HandlerStation x = it.next();
			if (x.getStationId() == id){
				it.remove();
				stationCount--;
			}
		}		
	}
	public void shutdownClient(int id) {
		for(Iterator<HandlerClient> it = listClient.iterator(); it.hasNext();){
			HandlerClient x = it.next();
			if (x.getClientId() == id){
				it.remove();
				clientCount--;
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
	public synchronized int getClientIntervall(){
		return serverConfig.getClientIntervall();
	}
	public synchronized void setInitData(ArrayList<WetterData> data){
		dataManager.setInitData(data);
	}


}
