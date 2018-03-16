package es.ucm.fdi.model;

import java.util.Map;

import es.ucm.fdi.model.Junction.IncomingRoads;

public class RoundRobinJunction extends JunctionWithTimeSlice {
	
	private int maxTimeSlice;
	private int minTimeSlice;
	
	public RoundRobinJunction(String id, int maxTimeSlice, int minTimeSlice) {
		super(id);
		this.maxTimeSlice = maxTimeSlice;
		this.minTimeSlice = minTimeSlice;
	}
	
	protected void fillReportDetails(Map<String, String> out) {
		out.put("type", "rr");
		super.fillReportDetails(out);
	}
	
	public void turnLightOn(IncomingRoadWithTimeSlice irs){
		
	}
	
	public void turnLightOff(IncomingRoadWithTimeSlice irs){
		
	}
	
	@Override
	public void advanceLight(){
		try {
			setTrafficLight((getTrafficLight() + 1) % getJunctionDeque().size());
		} catch (ArithmeticException e) {
			setTrafficLight(0); //Desde donde se realiza esta llamada esta excepcion no se va a dar nunca, porque se comprueba que junctionDeque.size() != 0.
		}
	}
	
	@Override
	public void addIncomingRoad(Road r) {
		IncomingRoads ir = new IncomingRoads(r);
		getJunctionDeque().add(ir);
		getJunctionMap().put(r, ir);
		setTrafficLight(getJunctionDeque().size() - 1); //al introducir una nueva carretera, se modifica el semaforo para que en el siguiente tick, al aumentar su valor
		//se aumente de forma correcta y se ponga a 0, dejando pasar a la carretera que se agrego primero.
	}
}
