package coolio;

public class MainProgram {
	OpgeslagenData data = new OpgeslagenData();
	int currentStad;
	int currentTime = 0;
	static int maxTime = 21 * 60;
	static int refuel = 100;
	static int land = 60;
	static int speed = 800;
	int totalWorth = 0;
	static int maxPassengers = 199;
	int KMsLeft = 3199;
	static int maxKMs = 3199;
	static int tries = 200000;
	static int startCity = 0;
	static int theoreticalMax = 2228402;
	static int numberOfPlanes = 6;
	Heuristic h= new Heuristic(tries);
	String[][] schedule = new String[6][50];
	
	public MainProgram(){
		for(int x = 0; x < data.AFSTAND.length; x++){
			for(int y = 0; y < data.AFSTAND[0].length; y++){
				if(data.AFSTAND[x][y] > 3199){
					data.toTest[x] = 0;
				}
			}
		}
		
	}
	
	public int[][] getPassMatrix(){
		return data.PASSENGERS;
	}
	
	public String[][] getSchedule(int[][] a, int[][] b){
		data.PASSENGERS = a;
		data.AFSTAND 	= b;
		currentStad = startCity;
		int[] theOne = {344,314,146,352};
		h = new Heuristic(theOne);
		for(int i = 0; i < numberOfPlanes; i++){
			schedule[i][0] = "0";
			doOne(i);
			resetForNextPlane();
		}
		return schedule;
	}
	
	public void reset(){
		currentTime = 0;
		totalWorth = 0;
		KMsLeft = maxKMs;
		currentStad = startCity;
		data = new OpgeslagenData();
	}
	
	void resetForNextPlane(){
		currentTime = 0;
		totalWorth = 0;
		KMsLeft = maxKMs;
		currentStad = startCity;
	}
	
	public boolean needToTank(int x){
		int newDistance = KMsLeft - data.AFSTAND[currentStad][x];
		if(newDistance < 0){
			return true;
		}
		return false;
	}
	
	public boolean possibleFlight(int x){
		if(canFlyBack(x)){
			if(needToTank(x)){
				int newTime = currentTime + flyTime(x) + refuel;
				if(newTime < maxTime){
					return true;
				}
			} else {
				int newTime = currentTime + flyTime(x);
				if(newTime < maxTime){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean canFlyBack(int dest){
		int totalTime = currentTime + flyTime(dest) + flyTime(startCity);
		if(totalTime < maxTime){
			if(KMsLeft - data.AFSTAND[currentStad][dest] - data.AFSTAND[dest][startCity] < 0){
				totalTime += land;
			}
			if(totalTime < maxTime){
				return true;
			}
		}
		return false;
	}
	
	public int flyTime(int a){
		return data.AFSTAND[currentStad][a] / 800 * 60 + 60;
	}
	
	public int score(int destination, int passengers){
		int total = 0;
		
		int afstand = data.AFSTAND[currentStad][destination];
		// time
		total += flyTime(destination) * h.getTime();
		// dist
		total += afstand * h.getDist();
		// pass km
		total += afstand * passengers * h.getKMs();
		// fuel
		total -= (KMsLeft - afstand)* h.getDist();

		return total;
	}
	
	public int maxPassenger(int destination){
		if(data.PASSENGERS[currentStad][destination] > maxPassengers ){
			return maxPassengers;
		} else {
			return data.PASSENGERS[currentStad][destination];
		}
	}
	
	public void doALot(){
		int[][] totals = new int[20][5];
		for(int j = 0; j < 20; j++){
			h = new Heuristic(tries);
			for(int i = 0; i < tries; i++){
				int test = doOne(0);
				h.setResult(test);
				h.next();
				reset();
				data = new OpgeslagenData();
			}
			totals[j] = h.getBestResult();
		}
		int best = 0;
		for(int i = 1; i < totals.length; i++){
			if(totals[i][h.RESULT] > totals[best][h.RESULT]){
				best = i;
			}
		}
		System.out.println(totals[best][h.DIST]);
		System.out.println(totals[best][h.FUEL]);
		System.out.println(totals[best][h.KM]);
		System.out.println(totals[best][h.TIME]);
		System.out.println(totals[best][h.RESULT]);
	}
	
	public void start(String[] args){
		if(1==1){
			int[] theOne = {344,314,146,352};
			h = new Heuristic(theOne);
			int total = 0;
			for(int i = 0; i < numberOfPlanes; i++){
				int localTotal = doOne(i);
				total += localTotal;
				System.out.println(localTotal);
				resetForNextPlane();
			}
			double percentage = (double)total / ((double)theoreticalMax * (double)numberOfPlanes) * 100.0;
			percentage = Math.round(percentage);
			System.out.println("Total for schedule all planes: "+total+" - "+percentage+"%");
			
		} else {
			doALot();
		}
	}
	
	public int doOne(int whichPlane){
		int bestWorth = 10;
		String next = "";
		int scheduleI = 1;
		while(bestWorth != 0){
			int bestCity = currentStad;
			bestWorth = 0;
			for(int x = 0; x < data.PASSENGERS.length; x++){
				if(data.toTest[x] == 1 && possibleFlight(x) && x != currentStad){
					int worth = score(x, maxPassenger(x));
					if(worth > bestWorth){
						bestCity = x;
						bestWorth = worth;
					}
				}
			}
			if(bestWorth != 0){
			
			
			
			int passengersToBring = maxPassenger(bestCity);
			next = ""+bestCity;
			currentTime += (data.AFSTAND[currentStad][bestCity] / speed)*60 + land;
			if(needToTank(bestCity)){
				currentTime += refuel*2;
				System.out.println("REFUEL!!!!");
				next = next+"T";
				KMsLeft = maxKMs;
			}
			KMsLeft -= data.AFSTAND[currentStad][bestCity];
			int saveKms = data.AFSTAND[currentStad][bestCity] * passengersToBring;
			totalWorth += saveKms;
			data.PASSENGERS[currentStad][bestCity] -= passengersToBring;
			System.out.println("afstand:" +data.AFSTAND[currentStad][bestCity]);
			currentStad = bestCity;
			System.out.println("Aantal passagiers"+passengersToBring);
			System.out.println("Volgende stad: "+data.steden[bestCity]+" PassKM "+saveKms+" time: "+currentTime/60);
			schedule[whichPlane][scheduleI] = next;
			scheduleI ++;
			}
		}
		next = ""+startCity;
		int passengersToBring = maxPassenger(startCity);
		if(needToTank(startCity)){
			next = next+"T";
			KMsLeft = maxKMs;
		}
		schedule[whichPlane][scheduleI] = next;
		currentTime = currentTime + (data.AFSTAND[currentStad][startCity] / speed)*60 + land;
		totalWorth += data.AFSTAND[currentStad][startCity] * passengersToBring;
		System.out.println("afstand:" +data.AFSTAND[currentStad][startCity]);
		System.out.println("Aantal passagiers"+passengersToBring);
		System.out.println("Volgende stad: "+data.steden[startCity]+" "+score(startCity,passengersToBring)+" time: "+currentTime/60);
		KMsLeft -= data.AFSTAND[currentStad][startCity];
		System.out.println("Nog in de tank: "+KMsLeft);
		System.out.println("Totaal: "+totalWorth);
		if(KMsLeft < 0){
			totalWorth = 0;
		}
		System.out.println("=============== NEXT SCHEDULE ===============");
		return totalWorth;
	}
	
	public static void main(String[] args){
		int startTime = (int) System.currentTimeMillis();
		new MainProgram().start(args);
		int spendTime = (int) System.currentTimeMillis() - startTime;
		System.out.println("totalRunningTime: "+spendTime);
	}
}
