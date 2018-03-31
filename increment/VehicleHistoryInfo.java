package increment;

import java.util.ArrayList;
import java.util.HashMap;

import core.Coord;

public class VehicleHistoryInfo {

private ArrayList<Coord> line;
	
	private HashMap<Integer, ArrayList<Block>> blockHistory; 

	/**
	 * ¹¹Ôìº¯Êý
	 */
	public VehicleHistoryInfo() {
		this.line = new ArrayList<Coord>();
		this.blockHistory = new HashMap<Integer, ArrayList<Block>>();
	}
	
	public ArrayList<Coord> getLine() {
		return line;
	}

	
	public HashMap<Integer, ArrayList<Block>> getBlockHistory() {
		return blockHistory;
	}


	public void setLine(ArrayList<Coord> trajectoryLine) {
		this.line = trajectoryLine;
	}
}
