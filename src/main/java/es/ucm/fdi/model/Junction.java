package es.ucm.fdi.model;

import java.util.*;

/**
 * Represents a Junction in the Simulator
 * @author Daniel Herranz
 *
 */
public class Junction extends SimObject {
	public static final String REPORT_HEADER = "junction_report";
	
	private List<IncomingRoads> junctionDeque = new ArrayList<>();
	private List<Road> outgoingRoadsList = new ArrayList<>();
	private Map<Road, IncomingRoads> junctionMap = new HashMap<>();
	private int trafficLight = 0;
	
	public int getTrafficLight() {
		return trafficLight;
	}

	public void setTrafficLight(int trafficLight) {
		this.trafficLight = trafficLight;
	}

	public Junction(String id) {
		super(id);
	}

	public List<IncomingRoads> getJunctionDeque() {
		return junctionDeque;
	}

	public Map<Road, IncomingRoads> getJunctionMap() {
		return junctionMap;
	}

	public List<Road> getOutgoingRoadsList() {
		return outgoingRoadsList;
	}

	/**
	 * Push a Vehicle in the Queue of the Road
	 * @param v
	 */
	public void entraVehiculo(Vehicle v){
		if (!junctionMap.get(v.getRoad()).roadDeque.contains(v)) {
			junctionMap.get(v.getRoad()).roadDeque.offerLast(v); //Metemos el vehiculo en la cola siempre que no este ya.
		}
	}
	
	/**
	 * Modifies the traffic Light
	 */
	public void advanceLight(){ 
		try {
			trafficLight = (trafficLight + 1) % junctionDeque.size();
		} catch (ArithmeticException e) {
			trafficLight = 0; //Desde donde se realiza esta llamada esta excepcion no se va a dar nunca, porque se comprueba que junctionDeque.size() != 0.
		}
	}
	
	protected static class IncomingRoads {
		private Deque<Vehicle> roadDeque;
		private Road road;
		
		public IncomingRoads(Road road) {
			super();
			this.roadDeque = new ArrayDeque<>();
			this.road = road;
		}
		
		public Road getRoad() {
			return road;
		}

		public Deque<Vehicle> getRoadDeque() {
			return roadDeque;
		}
	}
	
	/**
	 * Report a Junction given the statement of the project
	 */
	protected void fillReportDetails(Map<String, String> out) {
		
		String aux = "";
		for(int i = 0; i < junctionDeque.size(); ++i){
			if(i == trafficLight) {
				aux += "(" + junctionDeque.get(i).road.getId() + ",green,[";
			} else {
				aux += "(" + junctionDeque.get(i).road.getId() + ",red,[";
			}
			
			for(Iterator<Vehicle> itr = junctionDeque.get(i).roadDeque.iterator(); itr.hasNext();){
				aux += itr.next().getId();
				if (itr.hasNext()) {
					aux += ",";
				}
			}
			if(i != junctionDeque.size() - 1) {
				aux += "]),";
			} else {
				aux += "])";
			}
		}
		out.put("queues", aux);
	}
	
	protected String getReportHeader() {
		return REPORT_HEADER;
	}
	
	/**
	 * Advance a Junction. It moves only the first car of the queue and advances the Traffic Light
	 */
	public void avanza() {
		if(!junctionDeque.isEmpty()) {
			if (!junctionDeque.get(trafficLight ).roadDeque.isEmpty()) {
				//el array de incomingRoads no este vacio y la cola que indica el semaforo tampoco
				junctionDeque.get(trafficLight).roadDeque.getFirst().moverASiguienteCarretera(); //movemos el vehiculo a la carretera en funcion de su itinerario
				junctionDeque.get(trafficLight).roadDeque.removeFirst(); //eliminar vehiculo de la cola
			}
			advanceLight();
		}
	}
	
	/**
	 * Adds a Incoming Road to the Junction
	 * @param r
	 */
	public void addIncomingRoad(Road r) {
		IncomingRoads ir = new IncomingRoads(r);
		junctionDeque.add(ir);
		junctionMap.put(r, ir);
		trafficLight = junctionDeque.size() - 1; //al introducir una nueva carretera, se modifica el semaforo para que en el siguiente tick, al aumentar su valor
		//se aumente de forma correcta y se ponga a 0, dejando pasar a la carretera que se agrego primero.
	}
}
