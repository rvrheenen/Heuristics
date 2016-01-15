package coolio;

public class MainProgram {
	OpgeslagenData data = new OpgeslagenData();
	int currentStad = 0;
	int currentTime = 0;
	static int maxTime = 20 * 60;
	static int refuel = 60;
	static int land = 60;
	static int speed = 800;
	int totalWorth = 0;
	static int maxPassengers = 199;
	int KMsLeft = 3199;
	static int maxKMs = 3199;
	
	MainProgram(){
		for(int x = 0; x < data.AFSTAND.length; x++){
			for(int y = 0; y < data.AFSTAND[0].length; y++){
				if(data.AFSTAND[x][y] > 3199){
					data.toTest[x] = 0;
				}
			}
		}
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
				int newTime = currentTime + flyTime(x) + refuel;
				if(newTime < maxTime){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean canFlyBack(int dest){
		if(currentTime + flyTime(dest) + flyTime(0) < maxTime){
			return true;
		}
		return false;
	}
	
	public int flyTime(int a){
		return data.AFSTAND[currentStad][a] / 800 * 60 + 60;
	}
	
	public int score(int destination, int passengers){
		int total = 0;
		total += data.AFSTAND[currentStad][destination];
		int passKM = total * passengers;
		total = passKM + total;
		return total;
	}
	
	public int maxPassenger(int destination){
		if(data.PASSENGERS[currentStad][destination] > maxPassengers ){
			return maxPassengers;
		} else {
			return data.PASSENGERS[currentStad][destination];
		}
	}
	
	public void start(String[] args){
		
		
		
		//for(int i = 0; i < 10; i++){
		while(true){
			int bestCity = currentStad;
			int bestWorth = 0;
			for(int x = 0; x < data.PASSENGERS.length; x++){
				if(data.toTest[x] == 1 && possibleFlight(x)){
					int worth = score(x, maxPassenger(x));
					if(worth > bestWorth){
						bestCity = x;
						bestWorth = worth;
					}
				}
			}
			if(bestWorth == 0){
				break;
			}
			
			
			int passengersToBring = maxPassenger(bestCity);
			currentTime += (data.AFSTAND[currentStad][bestCity] / speed)*60 + land;
			if(needToTank(bestCity)){
				currentTime += refuel;
				System.out.println("REFUEL!!!!");
				KMsLeft = maxKMs;
			}
			KMsLeft -= data.AFSTAND[currentStad][bestCity];
			totalWorth += data.AFSTAND[currentStad][bestCity] * passengersToBring;
			data.PASSENGERS[currentStad][bestCity] -= passengersToBring;
			System.out.println("afstand:" +data.AFSTAND[currentStad][bestCity]);
			currentStad = bestCity;
			
			System.out.println("Volgende stad: "+data.steden[bestCity]+" "+bestWorth+" time: "+currentTime/60);
			
		}
		int passengersToBring = maxPassenger(0);
		currentTime = currentTime + (data.AFSTAND[currentStad][0] / speed)*60 + land;
		totalWorth += data.AFSTAND[currentStad][0] * passengersToBring;
		System.out.println("Volgende stad: "+data.steden[0]+" "+score(0,passengersToBring)+" time: "+currentTime/60);
		System.out.println("Totaal: "+totalWorth);
	}
	
	public static void main(String[] args){
		int startTime = (int) System.currentTimeMillis();
		new MainProgram().start(args);
		int spendTime = (int) System.currentTimeMillis() - startTime;
		System.out.println("totalRunningTime: "+spendTime);
	}
}
