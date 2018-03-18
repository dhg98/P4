package es.ucm.fdi.model;

import java.util.Iterator;
import java.util.Map;

public class RoundRobinJunction extends JunctionWithTimeSlice {
	
	private int maxTimeSlice;
	private int minTimeSlice;
	
	public RoundRobinJunction(String id, int maxTimeSlice, int minTimeSlice) {
		super(id);
		this.maxTimeSlice = maxTimeSlice;
		this.minTimeSlice = minTimeSlice;
	}
	
	public void avanza() {
		if(!getJunctionDeque().isEmpty()) {
			if (!getJunctionDeque().get(getTrafficLight()).getRoadDeque().isEmpty()) {
				//el array de incomingRoads no este vacio y la cola que indica el semaforo tampoco
				IncomingRoadWithTimeSlice irs = (IncomingRoadWithTimeSlice)getJunctionDeque().get(getTrafficLight());
				irs.getRoadDeque().getFirst().moverASiguienteCarretera(); //movemos el vehiculo a la carretera en funcion de su itinerario
				irs.getRoadDeque().removeFirst(); //eliminar vehiculo de la cola
				irs.setNumVehicles(irs.getNumVehicles() + 1);
			}
			advanceLight();
		}
	}
	
	@Override
	public void advanceLight() {
		if (getJunctionDeque() == null) {
			super.advanceLight();
		} else {
			IncomingRoadWithTimeSlice irs = (IncomingRoadWithTimeSlice)getJunctionDeque().get(getTrafficLight());
			if(irs.getTimeSlice() - 1 == irs.getUsedTimeUnits()) {
				if(irs.isUsed()) {
					irs.setTimeSlice(Math.min(maxTimeSlice, irs.getTimeSlice() + 1));
				} else if (irs.getNumVehicles() == 0) {
					irs.setTimeSlice(Math.max(minTimeSlice, irs.getTimeSlice() - 1));
				}
				irs.setUsedTimeUnits(0);	
				super.advanceLight();
			} else {
				irs.setUsedTimeUnits(irs.getUsedTimeUnits() + 1);
			}
		}
	}
	
	/**
	 * Report a Junction given the statement of the project
	 */
	protected void fillReportDetails(Map<String, String> out) {
		
		String aux = "";
		for(int i = 0; i < getJunctionDeque().size(); ++i){
			
			IncomingRoadWithTimeSlice irs = (IncomingRoadWithTimeSlice)getJunctionDeque().get(i);
			if(i == getTrafficLight()) {
				aux += "(" + irs.getRoad().getId() + ",green:" + (irs.getTimeSlice() - irs.getUsedTimeUnits()) + ",[";
			} else {
				aux += "(" + irs.getRoad().getId() + ",red,[";
			}
			
			for(Iterator<Vehicle> itr = irs.getRoadDeque().iterator(); itr.hasNext();){
				aux += itr.next().getId();
				if (itr.hasNext()) {
					aux += ",";
				}
			}
			if(i != getJunctionDeque().size() - 1) {
				aux += "]),";
			} else {
				aux += "])";
			}
		}
		out.put("queues", aux);
		out.put("type", "rr");
	}
	
	@Override
	public void addIncomingRoad(Road r) {
		IncomingRoadWithTimeSlice ir = new IncomingRoadWithTimeSlice(r, maxTimeSlice , 0, 0);
		getJunctionDeque().add(ir);
		getJunctionMap().put(r, ir);
		setTrafficLight(getJunctionDeque().size() - 1); //al introducir una nueva carretera, se modifica el semaforo para que en el siguiente tick, al aumentar su valor
		//se aumente de forma correcta y se ponga a 0, dejando pasar a la carretera que se agrego primero.
	}
}
