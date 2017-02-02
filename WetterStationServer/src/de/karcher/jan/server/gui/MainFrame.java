package de.karcher.jan.server.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import de.karcher.jan.server.controller.Server;
import de.karcher.jan.server.util.ServerConfig;

public class MainFrame extends JFrame {

	private Server server;
	private ServerConfig cfg;
	private JTabbedPane tp;

	public MainFrame(Server server,ServerConfig cfg) {
		init(server,cfg);
	}

	private void init(Server server, ServerConfig cfg) {
		this.server = server;
		this.cfg = cfg;
		super.addWindowListener(new WindowListener() {
			@Override
			public void windowClosed(WindowEvent e) {
				server.shutdownServer();
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				server.shutdownServer();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}
		});
		super.setResizable(false);
		super.setTitle("WetterStation - Server");
		
		tp = new JTabbedPane();		
		tp.add("Informationen", new InfoPanel(server, cfg));
		tp.add("Wetterdaten", new DataPane(server).getPanel());
		
		super.add(tp);
		super.pack();
		
		super.setVisible(true);
	}
}
