package coolio;

public class MainProgram {
	OpgeslagenData data = new OpgeslagenData();
	int currentStad = 0;
	int currentTime = 6 * 60;
	int maxTime = 26 * 60;
	int refuel = 60;
	int land = 60;
	int speed = 800;
	int totalWorth = 0;
	
	MainProgram(){
		for(int x = 0; x < data.AFSTAND.length; x++){
			for(int y = 0; y < data.AFSTAND[0].length; y++){
				if(data.AFSTAND[x][y] > 3199){
					data.toTest[x] = 0;
				}
			}
		}
	}
	
	public boolean canFlyBack(){
		if(currentTime + flyTime(0) < maxTime){
			return true;
		}
		return false;
	}
	
	public int flyTime(int a){
		return data.AFSTAND[currentStad][a] / 800 * 60 + 60;
	}
	
	public void start(String[] args){
		
		
		
		//for(int i = 0; i < 10; i++){
		while(currentTime <= maxTime && canFlyBack()){
			int bestCity = currentStad;
			int bestWorth = 0;
			for(int x = 0; x < data.PASSENGERS.length; x++){
				int worth = data.PASSENGERS[currentStad][x] * data.AFSTAND[currentStad][x];
				if(worth > bestWorth && data.toTest[currentStad] != '0'){
					bestCity = x;
					bestWorth = worth;
				}
			}
			currentTime = currentTime + (data.AFSTAND[currentStad][bestCity] / 800)*60 + 60;
			data.PASSENGERS[currentStad][bestCity] = 0;
			currentStad = bestCity;
			totalWorth += bestWorth;
			System.out.println("Volgende stad: "+data.steden[bestCity]+" "+bestWorth+" time: "+currentTime/60);
		}
		int worth = data.PASSENGERS[currentStad][0] * data.AFSTAND[currentStad][0];
		currentTime = currentTime + (data.AFSTAND[currentStad][0] / 800)*60 + 60;
		System.out.println("Volgende stad: "+data.steden[0]+" "+worth+" time: "+currentTime/60);
		System.out.println("Totaal: "+totalWorth);
	}
	
	public static void main(String[] args){
		
		new MainProgram().start(args);
	}
}
