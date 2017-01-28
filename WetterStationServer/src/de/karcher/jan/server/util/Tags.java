package de.karcher.jan.server.util;

public enum Tags {

	LSTATION, LCLIENT, SERVER, HSTATION, HCLIENT;

	public String print() {
		switch (this) {
		case SERVER:
			return "[Server] ";
		case LSTATION:
			return "[ListenerStation] ";
		case LCLIENT:
			return "[ListenerClient] ";
		case HSTATION:
			return "[HandlerStation] ";
		case HCLIENT:
			return "[HandlerClient] ";
		}
		return null;
	}
}
