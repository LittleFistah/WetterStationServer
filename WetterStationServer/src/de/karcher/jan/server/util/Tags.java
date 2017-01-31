package de.karcher.jan.server.util;

public enum Tags {

	LSTATION, LCLIENT, SERVER, HSTATION, HCLIENT, DBMANAGER;

	public String print(int i) {
		switch (this) {
		case SERVER:
			return "[Server] ";
		case LSTATION:
			return "[ListenerStation] ";
		case LCLIENT:
			return "[ListenerClient] ";
		case HSTATION:
			return "[HandlerStation "+ i+"] ";
		case HCLIENT:
			return "[HandlerClient "+ i+"] ";
		case DBMANAGER:
			return "[DBManager] ";
		}
		return null;
	}
}
