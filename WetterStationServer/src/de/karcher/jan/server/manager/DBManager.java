package de.karcher.jan.server.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.karcher.jan.server.controller.Server;
import de.karcher.jan.server.util.Logger;
import de.karcher.jan.server.util.Tags;
import de.karcher.jan.server.util.WetterData;

//
// 31.01.2017
// Yannick Gassert
// Korrektur Rechtschreibung
// --------------------------

public class DBManager {

	private Server server;
	private Logger logger;

	private final String dbServer = "jdbc:mysql://127.0.0.1:3306/";
	private final String db = "wetterstation";
	private final String tbDaten = "daten";
	private final String tbUpdate = "update";
	private final String driver = "com.mysql.jdbc.Driver";
	private final String user = "root";
	private final String pswd = "";

	private String query;
	private Connection con;
	private Statement stmt;
	private ResultSet res;

	private ArrayList<WetterData> wetterData;

	private String lastUpd;

	private Thread saver = new Thread() {
		public void run() {
			while (true) {
				try {
					sleep(60000 * 5);
				} catch (InterruptedException e) {
				}
				wetterData = server.getAllData();
				for (WetterData x : wetterData) {
					try {
						query = "UPDATE " + tbDaten + " SET `Temp`='" + x.getTemperatur() + "',`Status`='"
								+ x.getStatus() + "',`Wind`='" + x.getWindstaerke() + "' WHERE `Haupt`='"
								+ x.getHauptregion() + "' AND `Region`='" + x.getRegion() + "'";
						stmt = con.createStatement();
						stmt.executeUpdate(query);
					} catch (SQLException e) {
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					query = "TRUNCATE `" + tbUpdate+"`";
					stmt = con.createStatement();
					stmt.executeUpdate(query);
					lastUpd = new SimpleDateFormat("YYYYMMddHHmmss").format(new Date());
					query = "INSERT INTO `" + tbUpdate + "` VALUES ('" + lastUpd + "')";
					stmt = con.createStatement();
					stmt.executeUpdate(query);
					logger.addDBLog(Tags.DBMANAGER.print(0) + "Daten wurden in DB gespeichert (" + lastUpd + ").");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
	};

	public DBManager(Server server, Logger logger) {
		init(server, logger);
	}

	private void init(Server server, Logger logger) {
		this.server = server;
		this.logger = logger;
		wetterData = new ArrayList<WetterData>();
		try {
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(dbServer + db, user, pswd);
		} catch (SQLException sqlE) {
			if (sqlE.getMessage().contains("Unknown database")) {
				try {
					con = DriverManager.getConnection(dbServer, user, pswd);
					query = "CREATE DATABASE " + db;
					stmt = con.createStatement();
					logger.addDBLog(Tags.DBMANAGER.print(0) + "DB (" + db + ") wurde angelegt.");
					stmt.executeUpdate(query);
					con = DriverManager.getConnection(dbServer + db, user, pswd);
				} catch (SQLException e) {
					logger.addDBLog(Tags.DBMANAGER.print(0) + "Fehler beim Anlegen der DB.");
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			logger.addDBLog(Tags.DBMANAGER.print(0) + "Fehler beim Verbinden mit der DB.");
		}
		try {
			// Tabellen prüfen und ggf. anlegen
			query = "CREATE TABLE IF NOT EXISTS `" + tbDaten
					+ "` ( `Haupt` int(1) NOT NULL,  `Region` int(1) NOT NULL,  `Temp` float NOT NULL,  `Status` int(1) NOT NULL,  `Wind` int(2) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1";
			stmt = con.createStatement();
			stmt.executeUpdate(query);

			query = "SELECT COUNT(1) FROM " + tbDaten;
			stmt = con.createStatement();
			res = stmt.executeQuery(query);
			while (res.next()) {
				if (res.getInt(1) != 100) {
					query = "TRUNCATE " + tbDaten;
					stmt = con.createStatement();
					stmt.executeUpdate(query);
					query = "INSERT INTO `daten` (`Haupt`, `Region`, `Temp`, `Status`, `Wind`) VALUES (0, 0, 0, 0, 0),(0, 1, 0, 0, 0), (0, 2, 0, 0, 0),(0, 3, 0, 0, 0),(0, 4, 0, 0, 0),(0, 5, 0, 0, 0),(0, 6, 0, 0, 0),(0, 7, 0, 0, 0),(0, 8, 0, 0, 0),(0, 9, 0, 0, 0),(1, 0, 0, 0, 0),(1, 1, 0, 0, 0),(1, 2, 0, 0, 0),(1, 3, 0, 0, 0),(1, 4, 0, 0, 0),(1, 5, 0, 0, 0),(1, 6, 0, 0, 0),(1, 7, 0, 0, 0),(1, 8, 0, 0, 0),(1, 9, 0, 0, 0),(2, 0, 0, 0, 0),(2, 1, 0, 0, 0),(2, 2, 0, 0, 0),(2, 3, 0, 0, 0),(2, 4, 0, 0, 0),(2, 5, 0, 0, 0),(2, 6, 0, 0, 0),(2, 7, 0, 0, 0),(2, 8, 0, 0, 0),(2, 9, 0, 0, 0),(3, 0, 0, 0, 0),(3, 1, 0, 0, 0),(3, 2, 0, 0, 0),(3, 3, 0, 0, 0),(3, 4, 0, 0, 0),(3, 5, 0, 0, 0),(3, 6, 0, 0, 0),(3, 7, 0, 0, 0),(3, 8, 0, 0, 0),(3, 9, 0, 0, 0),(4, 0, 0, 0, 0),(4, 1, 0, 0, 0),(4, 2, 0, 0, 0),(4, 3, 0, 0, 0),(4, 4, 0, 0, 0),(4, 5, 0, 0, 0),(4, 6, 0, 0, 0),(4, 7, 0, 0, 0),(4, 8, 0, 0, 0),(4, 9, 0, 0, 0),(5, 0, 0, 0, 0),(5, 1, 0, 0, 0),(5, 2, 0, 0, 0),(5, 3, 0, 0, 0),(5, 4, 0, 0, 0),(5, 5, 0, 0, 0),(5, 6, 0, 0, 0),(5, 7, 0, 0, 0),(5, 8, 0, 0, 0),(5, 9, 0, 0, 0),(6, 0, 0, 0, 0),(6, 1, 0, 0, 0),(6, 2, 0, 0, 0),(6, 3, 0, 0, 0),(6, 4, 0, 0, 0),(6, 5, 0, 0, 0),(6, 6, 0, 0, 0),(6, 7, 0, 0, 0),(6, 8, 0, 0, 0),(6, 9, 0, 0, 0),(7, 0, 0, 0, 0),(7, 1, 0, 0, 0),(7, 2, 0, 0, 0),(7, 3, 0, 0, 0),(7, 4, 0, 0, 0),(7, 5, 0, 0, 0),(7, 6, 0, 0, 0),(7, 7, 0, 0, 0),(7, 8, 0, 0, 0),(7, 9, 0, 0, 0),(8, 0, 0, 0, 0),(8, 1, 0, 0, 0),(8, 2, 0, 0, 0),(8, 3, 0, 0, 0),(8, 4, 0, 0, 0),(8, 5, 0, 0, 0),(8, 6, 0, 0, 0),(8, 7, 0, 0, 0),(8, 8, 0, 0, 0),(8, 9, 0, 0, 0),(9, 0, 0, 0, 0),(9, 1, 0, 0, 0),(9, 2, 0, 0, 0),(9, 3, 0, 0, 0),(9, 4, 0, 0, 0),(9, 5, 0, 0, 0),(9, 6, 0, 0, 0),(9, 7, 0, 0, 0),(9, 8, 0, 0, 0),(9, 9, 0, 0, 0);";
					stmt = con.createStatement();
					stmt.executeUpdate(query);
				}
			}

			logger.addDBLog(Tags.DBMANAGER.print(0) + "Tabelle (" + tbDaten + ") wurde gefunden/angelegt.");
			// YYYYMMDDHHmmss
			query = "CREATE TABLE IF NOT EXISTS `" + tbUpdate
					+ "` ( `LastUpd` varchar(14) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1";
			stmt = con.createStatement();
			stmt.executeUpdate(query);
			logger.addDBLog(Tags.DBMANAGER.print(0) + "Tabelle (" + tbUpdate + ") wurde gefunden/angelegt.");
		} catch (SQLException e) {
			logger.addDBLog(Tags.DBMANAGER.print(0) + "Fehler beim Anlegen der Tabellen.");
			e.printStackTrace();
		}
		// Bestehende Daten auswerten
		wetterData.clear();
		try {
			query = "SELECT * FROM " + tbDaten;
			stmt = con.createStatement();
			res = stmt.executeQuery(query);
			while (res.next()) {
				wetterData.add(
						new WetterData(res.getInt(1), res.getInt(2), res.getDouble(3), res.getInt(4), res.getInt(5)));
			}
			server.setInitData(wetterData);
			logger.addDBLog(Tags.DBMANAGER.print(0) + "Initial lesen der DB erfolgreich.");
		} catch (SQLException e) {
			logger.addDBLog(Tags.DBMANAGER.print(0) + "Fehler beim Initial lesen der DB.");
			e.printStackTrace();
		}
		saver.start();
	}

	public void shutdown() {
		wetterData = server.getAllData();
		for (WetterData x : wetterData) {
			try {
				query = "UPDATE " + tbDaten + " SET `Temp`='" + x.getTemperatur() + "',`Status`='"
						+ x.getStatus() + "',`Wind`='" + x.getWindstaerke() + "' WHERE `Haupt`='"
						+ x.getHauptregion() + "' AND `Region`='" + x.getRegion() + "'";
				stmt = con.createStatement();
				stmt.executeUpdate(query);
			} catch (SQLException e) {
			}
		}
		try {
			query = "TRUNCATE " + tbUpdate;
			stmt = con.createStatement();
			stmt.executeUpdate(query);
			lastUpd = new SimpleDateFormat("YYYYMMDDHHmmss").format(new Date());
			query = "INSERT INTO " + tbUpdate + " VALUES ('" + lastUpd + "')";
			stmt = con.createStatement();
			stmt.executeQuery(query);
			logger.addDBLog(Tags.DBMANAGER.print(0) + "Daten wurden in DB gespeichert (" + lastUpd + ").");
		} catch (SQLException e) {
		}
	}
}
