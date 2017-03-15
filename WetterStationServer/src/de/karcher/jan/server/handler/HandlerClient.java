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
	private String mainRegion, subRegion, typ;
	private boolean isMittel, isVoll, isWetter;
	private Thread sender = new Thread() {
		public void run() {
			try {
				writer.write("<I-ID>" + clientId + "\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			long lastSend = 0l;
			ArrayList<WetterData> data;
			String upd = "";
			try {
				while (true) {
					if (System.currentTimeMillis() - lastSend > server.getClientIntervall()) {
						writer.write("<I-SCNT>" + server.getStationCount() + "\n");
						writer.flush();
						writer.write("<I-CCNT>" + server.getClientCount() + "\n");
						writer.flush();
						data = server.getAllData();

						if (isWetter) {
							for (WetterData x : data) {
								if (x.getHauptregion() == Integer.valueOf(mainRegion)
										&& x.getRegion() == Integer.valueOf(subRegion)) {
									upd = "<u>" + x.getStatus() + ":" + x.getWindstaerke() + ":" + x.getTemperatur();
								}
							}
						} else if (isMittel) {
							int dW = 0;
							int dS = 0;
							double dT = 0d;
							for (WetterData x : data) {
								if (x.getHauptregion() == Integer.valueOf(mainRegion)) {
									dW += x.getWindstaerke();
									dS += x.getStatus();
									dT += x.getTemperatur();
								}
							}
							dW /= 10;
							dS /= 10;
							dT /= 10;
							upd = "<u>" + dS + ":" + dW + ":" + dT;
						} else if (isVoll) {
							upd = "<u>";
							for (WetterData x : data) {
								switch (typ) {
								case "S":
									upd += x.getStatus();
									break;
								case "W":
									upd += x.getWindstaerke();
									break;
								case "T":
									upd += x.getTemperatur();
									break;
								}
								if (x.getHauptregion() == 9 && x.getRegion() == 9) {
								}else{
									upd+=":";
								}
							}
						}
						if (upd != "") {
							writer.write(upd + "\n");
							writer.flush();
						}
						lastSend = System.currentTimeMillis();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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

		isWetter = false;
		isMittel = false;
		isVoll = false;

		sender.start();
		this.start();
	}

	@Override
	public void run() {
		logger.addDefaultLog(Tags.HCLIENT.print(clientId) + "Neuer Client angemeldet.");
		String line;
		try {
			while ((line = reader.readLine()).compareTo("<e>") != 0) {

				tmpDate = new Date();
				lastRequest = df.format(tmpDate);
				requestCount++;
				System.out.println(line);
				if (line.contains("<r>W")) {
					isWetter = true;
					isMittel = false;
					isVoll = false;
					mainRegion = line.substring(4, 5);
					subRegion = line.substring(5, 6);
				} else if (line.contains("<r>M")) {
					isWetter = false;
					isMittel = true;
					isVoll = false;
					mainRegion = line.substring(4, 5);
				} else if (line.contains("<r>V")) {
					isWetter = false;
					isMittel = false;
					isVoll = true;
					typ = line.substring(4, 5);
				}
			}
			shutdown();
		} catch (IOException e) {
			logger.addHandlerLog(Tags.HCLIENT.print(clientId) + e.getMessage());
			shutdown();
		}
	}

	private void shutdown() {
		server.shutdownClient(clientId);
		server = null;
		logger = null;
		try {
			writer.write("<e>\n");
			writer.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			client.close();
			reader.close();
			writer.close();
		} catch (IOException e) {
			logger.addHandlerLog(Tags.HCLIENT.print(clientId) + e.getMessage());
		}
		this.interrupt();
	}

	public void shutdownServer() {
		server = null;
		logger = null;
		try {
			writer.write("<e>\n");
			writer.flush();
			writer.close();
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
}
