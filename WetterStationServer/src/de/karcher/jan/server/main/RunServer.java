package de.karcher.jan.server.main;

import java.awt.EventQueue;

import de.karcher.jan.server.controller.Server;

public class RunServer{

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Server server = new Server();
			}
		});
	}
}
