package de.karcher.jan.server.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.karcher.jan.server.controller.Server;
import de.karcher.jan.server.util.Logger;
import de.karcher.jan.server.util.Tags;
import de.karcher.jan.server.util.WetterData;

public class HandlerClient extends Thread {

	private Server server;
	private Logger logger;
	private Socket client;
	private int clientId, requestCount;
	private BufferedReader reader;
	private BufferedWriter writer;
	private Date tmpDate;
	private String firstContact, lastRequest;
	private SimpleDateFormat df;
	private boolean isAllRequested, isMainRegion;
	private String mainRegion,subRegion;
	private Thread sender = new Thread() {
		public void run() {
			long lastSend = 0l;
			ArrayList<WetterData> data;
			while(true){
				if(lastSend - System.currentTimeMillis() > server.getClientIntervall()){
					data = server.getAllData();
				}
			}
		};
	};

	public HandlerClient(Server server, Logger logger, Socket client, int id) {
		init(server, logger, client, id);
	}

	private void init(Server server, Logger logger, Socket client, int id) {
		this.server = server;
		this.logger = logger;
		this.client = client;
		this.clientId = id;
		try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		} catch (IOException e) {
			logger.addHandlerLog(Tags.HCLIENT.print(clientId) + e.getMessage());
		}
		df = new SimpleDateFormat("HH:mm:ss yyyy");
		tmpDate = new Date();
		firstContact = df.format(tmpDate);

		this.start();
	}

	@Override
	public void run() {
		logger.addDefaultLog(Tags.HCLIENT.print(clientId) + "Neuer Client angemeldet.");
		String line;
		try {
			while ((line = reader.readLine()).compareTo("<e>") != 0) {
				if (line.contains("<r>")) {
					getRequest(line.substring(3));
					tmpDate = new Date();
					lastRequest = df.format(tmpDate);
					requestCount++;
				}
			}
			shutdown();
		} catch (IOException e) {
			logger.addHandlerLog(Tags.HCLIENT.print(clientId) + e.getMessage());
		}
	}

	private void getRequest(String x){
		if (x.substring(1, 2).equals("00")){
			setIsAllRequested(true);
		}else if (x.substring(2,2).equals("*")){
			setMainRegion(x.substring(1, 1));
			setIsMainRegion(true);
		}else{
			setMainRegion(x.substring(1, 1));
			setSubRegion(x.substring(2, 2));
		}
		
	}
	
	private void shutdown() {
		server.shutdownClient(clientId);
		server = null;
		logger = null;
		try {
			client.close();
			reader.close();
		} catch (IOException e) {
			logger.addHandlerLog(Tags.HCLIENT.print(clientId) + e.getMessage());
		}
		this.interrupt();
	}
	public void shutdownServer() {
		server = null;
		logger = null;
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			bw.write("<e>\n");
			bw.flush();
			bw.close();
			reader.close();
			client.close();
		} catch (IOException e) {
			logger.addHandlerLog(Tags.HSTATION.print(clientId) + e.getMessage());
		}
		this.interrupt();
	}

	public int getClientId() {
		return clientId;
	}

	public int getUpdateCount() {
		return requestCount;
	}

	public String getFirstContact() {
		return firstContact;
	}

	public String getLastUpdate() {
		return lastRequest;
	}
	private synchronized boolean getIsMainRegion(){
		return isMainRegion;
	}
	private synchronized boolean getIsAllRequested(){
		return isAllRequested;
	}
	private synchronized void setIsMainRegion(boolean b){ 
		isMainRegion = b;
	}
	private synchronized void setIsAllRequested(boolean b){
		isAllRequested = b;
	}
	private synchronized String getMainRegion(){
		return mainRegion;
	}
	private synchronized String getSubRegion(){
		return subRegion;
	}
	private synchronized void setMainRegion(String s){ 
		mainRegion = s;
	}
	private synchronized void setSubRegion(String s){
		subRegion = s;
	}
}
