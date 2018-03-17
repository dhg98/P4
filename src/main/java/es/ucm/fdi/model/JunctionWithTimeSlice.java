package es.ucm.fdi.model;

import java.util.ArrayDeque;
import java.util.Deque;

public class JunctionWithTimeSlice extends Junction {
	
	public JunctionWithTimeSlice(String id) {
		super(id);
	}

	protected static class IncomingRoadWithTimeSlice extends Junction.IncomingRoads {
		
		private int timeSlice;
		private int usedTimeUnits;
		private int numVehicles; 
		
		public IncomingRoadWithTimeSlice(Road road, int timeSlice, int usedTimeUnits, int numVehicles) {
			super(road);
			this.timeSlice = timeSlice;
			this.usedTimeUnits = usedTimeUnits;
			this.numVehicles = numVehicles;
		}
		
		public void setTimeSlice(int timeSlice) {
			this.timeSlice = timeSlice;
		}


		public void setUsedTimeUnits(int usedTimeUnits) {
			this.usedTimeUnits = usedTimeUnits;
		}


		public void setNumVehicles(int numVehicles) {
			this.numVehicles = numVehicles;
		}


		public int getTimeSlice() {
			return timeSlice;
		}
		
		public int getNumVehicles() {
			return numVehicles;
		}

		public int getUsedTimeUnits() {
			return usedTimeUnits;
		}
		
		public boolean isUsed() {
			return numVehicles == usedTimeUnits;
		}
	}
}
