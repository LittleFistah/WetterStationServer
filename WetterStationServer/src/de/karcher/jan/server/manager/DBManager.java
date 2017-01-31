package de.karcher.jan.server.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import de.karcher.jan.server.controller.Server;
import de.karcher.jan.server.util.Logger;
import de.karcher.jan.server.util.Tags;

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
	private final String driver = "com.mysql.jdbc.Driver";
	private final String user = "root";
	private final String pswd = "";

	private boolean isConnected;

	private String query;
	private Connection con;
	private Statement stmt;
	private ResultSet res;
	private ResultSetMetaData resmeta;

	public DBManager(Server server, Logger logger) {
		init(server, logger);
	}

	private void init(Server server, Logger logger) {
		this.server = server;
		this.logger = logger;
		try {
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(dbServer + db, user, pswd);
			isConnected = true;
		} catch (SQLException sqlE) {
			if (sqlE.getMessage().contains("Unknown database")) {
				try {
					con = DriverManager.getConnection(dbServer, user, pswd);
					query = "CREATE DATABASE " + db;
					stmt = con.createStatement();
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
		// Tabellen prüfen und ggf. anlegen
		query = "CREATE TABLE IF NOT EXISTS `daten` ( `Haupt` int(1) NOT NULL,  `Region` int(1) NOT NULL,  `Temp` float NOT NULL,  `Status` int(1) NOT NULL,  `Wind` int(2) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1";

	}
}
