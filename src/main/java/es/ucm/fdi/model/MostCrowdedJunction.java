package es.ucm.fdi.model;

import java.util.Iterator;
import java.util.Map;

import es.ucm.fdi.model.Junction.IncomingRoads;
import es.ucm.fdi.model.JunctionWithTimeSlice.IncomingRoadWithTimeSlice;

public class MostCrowdedJunction extends JunctionWithTimeSlice {

	public MostCrowdedJunction(String id) {
		super(id);
	}
	
	@Override
	public void advanceLight(){
		
		IncomingRoadWithTimeSlice irs = (IncomingRoadWithTimeSlice)getJunctionDeque().get(getTrafficLight());
		if(irs.getTimeSlice() == irs.getUsedTimeUnits()) {
			int maximo = 0, i = 0, posMax = 0;
			for(IncomingRoads itrs: getJunctionDeque()){
				if(itrs.getRoadDeque().size() > maximo && i != getTrafficLight()) {
					maximo = itrs.getRoadDeque().size();
					posMax = i;
				}
				i++;
			}
			
			setTrafficLight(posMax);
			irs = (IncomingRoadWithTimeSlice)getJunctionDeque().get(posMax);
			irs.setTimeSlice(Math.max(maximo/2, 1));
			irs.setUsedTimeUnits(0);
		} else {
			irs.setUsedTimeUnits(irs.getUsedTimeUnits() + 1);
		}
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
	
	/**
	 * Reports a Most Crowded Junction given the statement of the project.
	 */
	@Override
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
		out.put("type", "mc");
	}
	
	@Override
	public void addIncomingRoad(Road r) {
		IncomingRoadWithTimeSlice ir = new IncomingRoadWithTimeSlice(r, 0 , 0, 0);
		getJunctionDeque().add(ir);
		getJunctionMap().put(r, ir);
		setTrafficLight(getJunctionDeque().size() - 1); //al introducir una nueva carretera, se modifica el semaforo para que en el siguiente tick, al aumentar su valor
		//se aumente de forma correcta y se ponga a 0, dejando pasar a la carretera que se agrego primero.
	}
}
