package de.karcher.jan.server.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.karcher.jan.server.controller.Server;
import de.karcher.jan.server.util.ServerConfig;

public class InfoPanel extends JPanel{
	
	private Server server;
	private ServerConfig cfg;
	
	private JLabel jlIP,jlPortStation,jlPortClient;
	
	public InfoPanel(Server server, ServerConfig cfg) {
		init(server,cfg);
	}

	private void init(Server server, ServerConfig cfg) {
		this.server = server;
		this.cfg = cfg;
		
		this.setLayout(new GridLayout(15, 1));
		jlIP = new JLabel("Server IP-Adresse: "+cfg.getIp());
		this.add(jlIP);
		jlPortStation = new JLabel("Port Stationen: "+cfg.getPortStation());
		this.add(jlPortStation);
		jlPortClient = new JLabel("Port Clients: "+cfg.getPortClient());
		this.add(jlPortClient);
		
	}

}
