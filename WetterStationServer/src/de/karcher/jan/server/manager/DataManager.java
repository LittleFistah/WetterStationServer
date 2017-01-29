package de.karcher.jan.server.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.karcher.jan.server.util.WetterData;

public class DataManager {
	
	ArrayList<WetterData> data = new ArrayList<WetterData>();

	public DataManager() {
		init();
	}

	private void init() {
		for (int hauptregion = 0; hauptregion < 10; hauptregion++) {
			for (int region = 0; region < 10; region++) {
				WetterData tmp = new WetterData(hauptregion, region, 0, 0, 0);
				data.add(tmp);
			}
		}
	}

	public void setData(int hauptregion, int region, char typ, int value, double value1) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getHauptregion() == hauptregion && data.get(i).getRegion() == region) {
				switch (typ) {
				case 'T':
					data.get(i).setTemperatur(value1);
					break;
				case 'S':
					data.get(i).setStatus(value);
					break;
				case 'W':
					data.get(i).setWindstaerke(value);
					break;
				}
			}
		}
	}
	
	public ArrayList<WetterData> getAllData() {
		sort();
		return data;
	}
	private void sort(){
		Collections.sort(data, new Comparator<WetterData>() {
			@Override
			public int compare(WetterData o1, WetterData o2) {
				String a = String.valueOf(o1.getHauptregion());
				String b = String.valueOf(o2.getHauptregion());
				return a.compareTo(b);
			}
		});
	}
}
