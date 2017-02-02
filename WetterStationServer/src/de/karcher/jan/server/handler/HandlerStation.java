package de.karcher.jan.server.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.karcher.jan.server.controller.Server;
import de.karcher.jan.server.util.Logger;
import de.karcher.jan.server.util.Tags;

public class HandlerStation extends Thread {

	private Server server;
	private Logger logger;
	private Socket station;
	private int stationId, updateCount;
	private BufferedReader reader;
	private Date tmpDate;
	private String firstContact, lastUpdate;
	private SimpleDateFormat df;

	public HandlerStation(Server server, Logger logger, Socket station, int id) {
		init(server, logger, station, id);
	}

	private void init(Server server, Logger logger, Socket station, int id) {
		this.server = server;
		this.logger = logger;
		this.station = station;
		this.stationId = id;
		try {
			reader = new BufferedReader(new InputStreamReader(station.getInputStream()));
		} catch (IOException e) {
			logger.addHandlerLog(Tags.HSTATION.print(stationId) + e.getMessage());
		}
		df = new SimpleDateFormat("HH:mm:ss yyyy");
		tmpDate = new Date();
		firstContact = df.format(tmpDate);
		
		this.start();
	}

	@Override
	public void run() {
		logger.addDefaultLog(Tags.HSTATION.print(stationId) + "Neue Station angemeldet.");		
		String line;
		try {
			while ((line = reader.readLine()).compareTo("<e>") != 0) {
				if (line.contains("<u>")) {
					performUpdate(line.substring(3));
					tmpDate = new Date();
					lastUpdate = df.format(tmpDate);
					updateCount++;
				}
			}
			shutdown();
		} catch (IOException e) {
			logger.addHandlerLog(Tags.HSTATION.print(stationId) + e.getMessage());
		}
	}
	
	private void shutdown(){
		server.shutdownStation(stationId);
		server = null;
		logger = null;
		try {
			station.close();
			reader.close();
		} catch (IOException e) {
			logger.addHandlerLog(Tags.HSTATION.print(stationId) + e.getMessage());
		}
		this.interrupt();		
	}

	private void performUpdate(String update) {
		logger.addHandlerLog(Tags.HSTATION.print(stationId) + "Neues Update empfangen: "+update);
		String[] subs = update.split(":");
		int hr = 0;
		int r = 0;
		try {
			hr = Integer.valueOf(subs[0].substring(0, 1));
			r = Integer.valueOf(subs[0].substring(1));
		} catch (Exception e) {
			if (!e.getMessage().contains("For input string:")) {
				e.printStackTrace();
			} else {
				r = Integer.valueOf(subs[0].substring(0));
				hr = 0;
			}
		}
		char typ = subs[1].toCharArray()[0];
		int value = 0;
		double value1 = 0;
		if (typ == 'T') {
			value1 = Double.valueOf(subs[2]);
		} else {
			value = Integer.valueOf(subs[2]);
		}
		server.setData(hr, r, typ, value, value1);
	}
	
	public void shutdownServer(){
		server = null;
		logger = null;
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(station.getOutputStream()));
			bw.write("<e>\n");
			bw.flush();
			bw.close();
			reader.close();
			station.close();
		} catch (IOException e) {
			logger.addHandlerLog(Tags.HSTATION.print(stationId) + e.getMessage());			
		}		
		this.interrupt();
	}
	public int getStationId() {
		return stationId;
	}
	public int getUpdateCount() {
		return updateCount;
	}
	public String getFirstContact() {
		return firstContact;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}	
}
