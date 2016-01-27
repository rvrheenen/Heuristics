package coolio;

public class MainProgram {
	static 	double 	maxTime 	   = 20 * 60;
	static 	double 	refuel  	   = 60;
	static 	double 	land 		   = 60;
	static 	int 	speed 		   = 800;
	static 	int 	maxPassengers  = 199;
	static 	int 	maxKMs 		   = 3199;
	static 	int 	tries 		   = 200000;
	static 	int 	startCity 	   = 0;
	static 	int 	theoreticalMax = 2547287;
	static 	int 	numberOfPlanes = 6;
			double 	currentTime	   = 0;
			int 	totalWorth 	   = 0;
			int 	KMsLeft 	   = 3199;
			int 	vTotal 		   = 0;
			int 	currentStad;
			
	OpgeslagenData 	data 	 = new OpgeslagenData();
	Heuristic 		h 		 = new Heuristic(tries);
	String[][] 		schedule = new String[6][50];

	public MainProgram() {
		for (int x = 0; x < data.AFSTAND.length; x++) {
			for (int y = 0; y < data.AFSTAND[0].length; y++) {
				if (data.AFSTAND[x][y] > 3199) {
					data.toTest[x] = 0;
				}
			}
		}
	}

	public int[][] getPassMatrix() {
		return data.PASSENGERS;
	}

	public String[][] getSchedule(int[][] a, int[][] b) {
		data.PASSENGERS = a;
		data.AFSTAND   	= b;
		currentStad  	= startCity;
		int[] hValues 	= {14,10,18,19}; // The precalculated heuristics of time dist pass fuel
		h 				= new Heuristic(hValues);
		
		for (int i = 0; i < numberOfPlanes; i++) {
			schedule[i][0] = "0";
			doOne(i);
			resetForNextPlane();
		}
		schedule[5][49] = Integer.toString(vTotal);
		return schedule;
	}

	public void reset() {
		currentTime = 0;
		totalWorth 	= 0;
		KMsLeft 	= maxKMs;
		currentStad = startCity;
		data 		= new OpgeslagenData();
	}

	void resetForNextPlane() {
		currentTime = 0;
		totalWorth 	= 0;
		KMsLeft 	= maxKMs;
		currentStad = startCity;
	}

	public boolean possibleFlight(int x) { // Check if the flight is possible 
		if (canFlyBack(x)) {
			double newTime = currentTime + flyTime(x);
			if (needToTank(x)) newTime += refuel;
			return (newTime < maxTime);
		}
		return false;
	}

	public boolean needToTank(int x) { // Check if necessary to tank to get to destination
		int newDistance = KMsLeft - data.AFSTAND[currentStad][x];
		if (newDistance < 0) return true;
		return false;
	}
	
	public boolean canFlyBack(int dest) { // Check if Amsterdam is still reachable from targetted destination
		double totalTime = currentTime + flyTime(dest) + flyTime(startCity);
		if (totalTime < maxTime) {
			if (KMsLeft - data.AFSTAND[currentStad][dest] - data.AFSTAND[dest][startCity] < 0) {
				totalTime += land;
			}
			return (totalTime < maxTime);
		}
		return false;
	}

	public double flyTime(int a) { // Calculates flying time for current city to  
		return (double)data.AFSTAND[currentStad][a] / 800 * 60 + 60;
	}

	public int score(int destination, int passengers) { // Applies heuristics to the destination and passengers count
		int score = 0;
		int afstand = data.AFSTAND[currentStad][destination];
		score += flyTime(destination) * h.getTime(); // time heuristic
		score += afstand 			  * h.getDist(); // distance heuristic
		score += afstand * passengers * h.getKMs();  // passenger km heuristic
		score -= (KMsLeft - afstand)  * h.getFuel(); // fuel heuristic
		return score;
	}

	public int maxPassenger(int destination) { // Gets the maximum amount of passenger which can be taken to destination
		if (data.PASSENGERS[currentStad][destination] > maxPassengers) {
			return maxPassengers;
		} else {
			return data.PASSENGERS[currentStad][destination];
		}
	}

	public void doALot() { // Used when calculating heuristics
		int[][] totals = new int[20][5];
		for (int j = 0; j < 20; j++) {
			h = new Heuristic(tries);
			for (int i = 0; i < tries; i++) {
				int test = doOne(0);
				h.setResult(test);
				h.next();
				reset();
				data = new OpgeslagenData();
			}
			totals[j] = h.getBestResult();
		}
		int best = 0;
		for (int i = 1; i < totals.length; i++) {
			if (totals[i][h.RESULT] > totals[best][h.RESULT]) {
				best = i;
			}
		}
		System.out.println(totals[best][h.DIST]);
		System.out.println(totals[best][h.FUEL]);
		System.out.println(totals[best][h.KM]);
		System.out.println(totals[best][h.TIME]);
		System.out.println(totals[best][h.RESULT]);
	}

	public void start(String[] args) {
		int switcher = 1; // SWITCH 1: Generate schedules, 2: Generate heuristics
		if (switcher == 1) {
			int[] hValues = {14,10,18,19}; // The precalculated heuristics of time dist pass fuel
			h = new Heuristic(hValues);
			int total = 0;
			for (int i = 0; i < numberOfPlanes; i++) {
				int localTotal = doOne(i);
				total += localTotal;
				System.out.println(localTotal);
				resetForNextPlane();
			}
			double percentage = (double) total / ((double) theoreticalMax * (double) numberOfPlanes) * 100.0;
				   percentage = Math.round(percentage);
			System.out.println("Total for schedule all planes: " + total + " - " + percentage + "%");
		} else {
			doALot();
		}
	}

	public int doOne(int whichPlane){ // Generate one flight schedule. 
		String next 	 = "";
		int    scheduleI = 1; 
		int    bestWorth = 10;
		
		while(bestWorth != 0){
			int bestCity = currentStad;
			bestWorth 	 = 0;
			for(int x = 0; x < data.PASSENGERS.length; x++){
				if(data.toTest[x] == 1 && possibleFlight(x) && x != currentStad){
					int worth = score(x, maxPassenger(x));
					if(worth > bestWorth){
						bestCity  = x;
						bestWorth = worth;
					}
				}
			}
			if(bestWorth != 0){
				int 	passengersToBring = maxPassenger(bestCity);
						next 			  = ""+bestCity;
				double 	tempTime 	  	  = ((double)data.AFSTAND[currentStad][bestCity] / speed)*60 + land;
				
				System.out.println("This flight takes: "+tempTime);
				currentTime += tempTime;
				
				if(needToTank(bestCity)){
					System.out.println("Refueling");
					currentTime += (double)refuel;
					next 		= next + "T";
					KMsLeft 	= maxKMs;
				}
				KMsLeft -= data.AFSTAND[currentStad][bestCity];
				int saveKms = data.AFSTAND[currentStad][bestCity] * passengersToBring;
				totalWorth += saveKms;
				
				data.PASSENGERS[currentStad][bestCity] -= passengersToBring;				
				System.out.println("afstand:" +data.AFSTAND[currentStad][bestCity]);
				
				currentStad = bestCity;
				System.out.println("Aantal passagiers"+passengersToBring);
				System.out.println("Volgende stad: "+data.steden[bestCity]+" PassKM "+saveKms+" time: "+currentTime);
				
				schedule[whichPlane][scheduleI] = next;
				scheduleI ++;
			}
		}
		next = "" + startCity;
		
		int passengersToBring = maxPassenger(startCity);
		
		if(needToTank(startCity)){
			System.out.println("Refueling");
			next 	= next+"T";
			KMsLeft = maxKMs;
		}
		
		schedule[whichPlane][scheduleI] = next;
		currentTime = currentTime + (data.AFSTAND[currentStad][startCity] / speed) * 60 + land;
		totalWorth += data.AFSTAND[currentStad][startCity] * passengersToBring;
		
		System.out.println("afstand:" +data.AFSTAND[currentStad][startCity]);
		System.out.println("Aantal passagiers"+passengersToBring);
		System.out.println("Volgende stad: "+data.steden[startCity]+" "+score(startCity,passengersToBring)+" time: "+currentTime);
		
		KMsLeft -= data.AFSTAND[currentStad][startCity];
		
		System.out.println("Nog in de tank: "+KMsLeft);
		System.out.println("Totaal: "+totalWorth);
		
		vTotal += totalWorth;
		if(KMsLeft < 0)	totalWorth = 0;
		if(currentTime > maxTime) System.out.println("HELP!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		System.out.println("=============== NEXT SCHEDULE ===============");
		return totalWorth;
	}

	public static void main(String[] args) {
		int startTime = (int) System.currentTimeMillis();
		new MainProgram().start(args);
		int spendTime = (int) System.currentTimeMillis() - startTime;
		System.out.println("totalRunningTime: " + spendTime);
	}
}
