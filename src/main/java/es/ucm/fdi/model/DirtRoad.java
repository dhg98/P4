package es.ucm.fdi.model;

import java.util.Comparator;
import java.util.Map;

import es.ucm.fdi.util.MultiTreeMap;

public class DirtRoad extends Road{

	public DirtRoad(String id, int size, int maxVel, Junction start, Junction end) {
		super(id, size, maxVel, start, end);
	}
	
	@Override
	public int modificarVelBase() {
		return getMaxVel();
	}
	
	protected void fillReportDetails(Map<String, String> out) {
		out.put("type", "dirt");
		super.fillReportDetails(out);
	}
	
	@Override
	public void avanza() {
		modificarVelBase();
		MultiTreeMap<Integer, Vehicle> actualizado = new MultiTreeMap<>(Comparator.comparing(Integer::intValue).reversed());
		int numCochesAveriados = 0;
		int velBase = modificarVelBase();
		for(Vehicle v: getStreet().innerValues()){
			if(v.getTiempoAveria() > 0) {
				numCochesAveriados++;
			} else {
				v.setVelocidadActual(velBase / (1 + numCochesAveriados));
			}
			v.avanza();
			actualizado.putValue(v.getLocation(), v);
		}
		setStreet(actualizado);
	}
}
