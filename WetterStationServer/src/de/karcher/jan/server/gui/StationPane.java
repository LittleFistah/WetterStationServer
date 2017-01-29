package de.karcher.jan.server.gui;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.karcher.jan.server.controller.Server;
import de.karcher.jan.server.util.WetterData;

public class StationPane extends JPanel {

	private Server server;
	private ArrayList<WetterData> dataWetterData;

	private JTable dataTable;
	private DefaultTableModel dataTableModel = null;
	private JScrollPane dataTblScroll;
	private final String[] dataHeader = { "Hauptregion", "Region", "Temperatur", "Status", "Windstärke" };

	private int updIntervall;
	private boolean isDataUpd = true;

	private Thread dataTbl = new Thread() {
		public void run() {
			while (isDataUpd) {
				dataWetterData = server.getAllData();
				dataTableModel.setRowCount(0);
				for (WetterData x : dataWetterData) {
					Object[] dataObj = { x.getHauptregion() + "", x.getRegion() + "", x.getTemperatur() + "",
							x.getStatus() + "", x.getWindstaerke() + "" };
					dataTableModel.addRow(dataObj);
				}
				try {
					sleep(updIntervall);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
	};

	public StationPane(Server server) {
		init(server);
	}

	private void init(Server server) {
		this.server = server;
		updIntervall = server.getDefaultUITimeout();
		
		dataWetterData = new ArrayList<WetterData>();
		dataTableModel = new DefaultTableModel(dataHeader, 0);
		dataTable = new JTable(dataTableModel);

		dataTblScroll = new JScrollPane(dataTable);

		this.add(dataTblScroll);
		dataTbl.start();
	}

	public JPanel getPanel() {
		return this;
	}
}
