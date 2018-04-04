package es.ucm.fdi.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.Map.Entry;

import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.MultiTreeMap;

/**
 * Simulates the execution
 * @author Daniel Herranz
 *
 */
public class TrafficSimulator {
	private RoadMap r;
	private int timeCounter;
	private MultiTreeMap<Integer, Event> Events = new MultiTreeMap <>();
	
	public TrafficSimulator() {
		r = new RoadMap();
		timeCounter = 0;
	}
	
	/**
	 * Insert the event in order to execute it if the time is previous than the Simulation Time.
	 * @param e
	 * @throws RuntimeException
	 */
	public void insertaEvento(Event e) {
		if (e.getTime() < timeCounter) {
			throw new RuntimeException("The time you have given for this event is previous than the current time");
		} else {
			Events.putValue(e.getTime(), e);
		}
	}
	
	/**
	 * Execute the simulation of the trafficSimulator a number of pasosSimulacion
	 * @param out
	 * @param pasosSimulacion
	 * @throws IOException
	 */
	public void execute(OutputStream out, int pasosSimulacion) throws IOException{
		int limiteTiempo = timeCounter + pasosSimulacion - 1;
		while (timeCounter <= limiteTiempo) {
			eventProcess();
			advance();
			++timeCounter;
			writeReport(out);
		}
	}
	
	/**
	 * Process the events for the timeCounter of the simulator
	 * @throws IllegalArgumentException
	 */
	public void eventProcess() {
		try {
			List<Event> arrayEvent = Events.get(timeCounter);
			if(arrayEvent != null) {
				int i = 0;
				while (i < arrayEvent.size()) {
					Event e = arrayEvent.get(i);
					e.execute(r);
					++i;
				}
			}
			
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("There was an error while processing the events", e);
		}
	}
	
	/**
	 * We advance the Roads and the Junctions.
	 * @throws IllegalArgumentException
	 */
	public void advance() {
		try {
			for (Road ro: r.getRoads()) {
				if(ro.getNumVehicles() > 0) {
					ro.avanza();
				}
			}
			for (Junction j : r.getJunctions()) {
				j.avanza();
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("There was an error while advancing the objects", e);
		}
	}
	
	/**
	 * Create an IniSection from a Map given the statement of the project
	 * @param report
	 * @return
	 */
	IniSection createIniSection(Map<String, String> report) {
		IniSection ini = new IniSection(report.get(""));
		report.remove("");
		for (Entry<String, String> e : report.entrySet()) {
			ini.setValue(e.getKey(), e.getValue());
		}
		return ini;
	}

	/**
	 * Append reports for each object to main report
	 * @param objects
	 * @param report
	 */
	private void fillReport(Iterable<? extends SimObject> objects, Ini report) {
		Map<String, String> objectReport = new LinkedHashMap<>();
		for (SimObject o : objects) {
			o.report(timeCounter, objectReport);
			report.addsection(createIniSection(objectReport));
			objectReport.clear();
		}
	}

	/**
	 * Writes a report in a OutputStream. The order is Junctions, then Roads and at last Vehicles.
	 * @param out
	 * @throws IOException
	 */
	public void writeReport(OutputStream out) throws IOException {
		Ini file = new Ini();
		fillReport(r.getJunctions(), file);
		fillReport(r.getRoads(), file);
		fillReport(r.getVehicles(), file);
		file.store(out);
	}
}
