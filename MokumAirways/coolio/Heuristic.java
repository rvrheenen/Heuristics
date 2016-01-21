package coolio;

import java.util.Random;

public class Heuristic {
	int[][] heuristics;
	int current = 0;
	int numberOfHeurs = 4;
	int totalTries;
	static final int 
				KM 	   = 0,
				TIME   = 1,
				FUEL   = 2,
				DIST   = 3,
				RESULT = 4;
				
	Heuristic(int total_tries){
		heuristics = new int[total_tries][5];
		for(int i = 0; i < numberOfHeurs; i++){
			heuristics[current][i] = generateRandom(1, 400);
		}
		totalTries = total_tries;
	}
	
	Heuristic(int[] a){
		heuristics 			= new int[1][5];
		heuristics[0][DIST] = a[0];
		heuristics[0][FUEL] = a[1];
		heuristics[0][KM] 	= a[2];
		heuristics[0][TIME] = a[3];
	}
	
	/*
	 * Pass KMs
	 * Time of day
	 * Distance to base
	 * Fuel level
	 */
	
	private int generateRandom(int Min, int Max){
		return Min + (int)(Math.random() * ((Max - Min) + 1));
	}
	
	public int getKMs(){
		return heuristics[current][KM];
	}
	
	public int getTime(){
		return heuristics[current][TIME];
	}
	
	public int getFuel(){
		return heuristics[current][FUEL];
	}
	
	public int getDist(){
		return heuristics[current][DIST];
	}
	
	public int[] getBestResult(){
		int currentBest = 0;
		for(int i = 1; i < totalTries; i++){
			if(heuristics[i][RESULT] > heuristics[currentBest][RESULT]){
				currentBest = i;
			}
		}
		return heuristics[currentBest];
	}
	
	public void setResult(int result){
		heuristics[current][RESULT] = result;
	}
	
	public void next(){
		current++;
		if(current < numberOfHeurs){
			for(int i = 0; i < numberOfHeurs; i++){
				heuristics[current][i] = generateRandom(1, 20);
			}
		}
		
	}
}
