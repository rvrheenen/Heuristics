package visualiser;
//v1.3: No Changes
//v1.2: No Changes
//v1.1: No Changes
import java.util.Random;
/*	De Landing class bevat alle informatie over elke landing van een vliegtuig.
	De belangrijkste informatie is de locatie van de landing en de grondtijd op
	deze locatie
*/
public class Landing {

	/* ********************************************************************** *\
	|= ========================== Variabelen ================================ =|
	\* ********************************************************************** */
	private	City 	locatie;				// De stad van de landing
	private	int		grondtijd;				// De tijd in minuten die het toestel EXTRA aan de grond blijft
	private	int 	verwerktijd;			// De verplichte tijd in minuten aan de grond (tanken + passagiers wisselen)
	private boolean	hierTanken;				// Of het vliegtuig wel of niet tankt tijdens deze landing
	private Random	RANDOM = new Random(); 	// Nodig voor het genereren van random getallen
	
	private static final		int		VERWERK_TIJD	= 60; 	//De tijd in minuten die nodig is om het toestel schoon te maken en passagiers te verwisselen
	private static final		int		TANK_TIJD		= 60; 	//De tijd in minuten die EXTRA nodig om te tanken
	private static final		int		MAX_GRONDTIJD	= 180; 	//De maximale tijd in minuten dat een vliegtuig EXTRA aan de grond staat (deze ben je vrij te veranderen)
	
	/* ********************************************************************** *\
	|= ======================== Constructors ================================ =|
	\* ********************************************************************** */
	public Landing(City loc, int grondtijd, boolean hierTanken) {
		this.grondtijd 		= grondtijd;
		this.locatie		= loc;
		this.hierTanken		= hierTanken;
		this.verwerktijd 	= bepaalVerwerktijd();
	}
	
	public Landing(City locatie) {
		this.locatie 		= locatie;
		this.grondtijd		= 0;
		this.hierTanken		= false;
		this.verwerktijd 	= bepaalVerwerktijd();
	}
	
	public Landing() {
		this.locatie		= City.CITIES.get(RANDOM.nextInt(City.CITIES.size()));
		this.grondtijd		= 0;
		this.hierTanken		= false;
		this.verwerktijd 	= bepaalVerwerktijd();
	}
	
	/* ********************************************************************** *\
	|= ============================== Methoden ============================== =|
	\* ********************************************************************** */
	//Geeft de naam van de stad van de landing
	public City geefLocatie() {
		return locatie;
	}
	
	public String geefLocatieNaam(){
		return locatie.getName();
	}
	
	//Geeft de index van de stad van de landing
	public int geefLoc() {
		return City.CITIES.indexOf(locatie);
	}
	
	//Geeft de EXTRA grondtijd die gepland staat bij deze landing
	public int geefGrondtijd() {
		return grondtijd;
	}
	
	//Geeft de tijd die nodig is om passagiers te wisselen etc
	public int geefVerwerktijd() {
		return verwerktijd;
	}
	
	//Geeft de totale tijd dat het vliegtuig aan de grond staat (grondtijd + verwerktijd)
	public int geefTotaleGrondtijd() {
		return grondtijd + verwerktijd;
	}
	
	//Geeft aan of er tijdens deze landing getankt wordt
	public boolean hierTanken() {
		return hierTanken;
	}
	
	//Geeft de afstand van deze landing naar een nieuwe bestemming (index)
	public int geefAfstandNaar(int bestemming) {
		return City.AFSTAND[geefLoc()][bestemming];
	}
	
	//Geeft de afstand tussen twee bestemmingen (indexen)
	public int geefAfstandTussen(int thuis, int bestemming) {
		return City.AFSTAND[thuis][bestemming];
	}
	
	//Geeft de coordinaten van de stad van deze landing
	public int[] geefCoordinaten() {
		return City.CITIES.get(geefLoc()).getCoordinates();
	}
	
	//Geeft de coordinaten van de stadsindex loc
	public int[] geefCoordinaten(int loc) {
		return City.CITIES.get(loc).getCoordinates();
	}
	
	//Verwisselt de waarde van hierTanken
	public void swapTankbeurt() {
		if(hierTanken) {
			hierTanken = false;
		} else {
			hierTanken = true;
		}
		verwerktijd = bepaalVerwerktijd();
	}
	
	//Wijzigt de waarde van hierTanken naar nieuweWaarde
	public void wijzigTankbeurt(boolean nieuweWaarde) {
		hierTanken 	= nieuweWaarde;
		verwerktijd = bepaalVerwerktijd();
	}
	
	//Wijzigt de grondtijd van deze landing naar nieuweWaarde
	public boolean wijzigGrondtijd(int nieuweWaarde) {
		if(nieuweWaarde >=0 ) {
			grondtijd = nieuweWaarde;
			return true;
		}
		System.out.println("Error, " + nieuweWaarde + " is geen geldige waarde voor wijzigGrondtijd");
		return false;
	}
	
	//Wijzigt de locatie van deze landing en update de tijden
	public boolean wijzigLocatie(City nieuweWaarde) {
		if(geefIndex(nieuweWaarde.getName()) != -1) {
			locatie 	= nieuweWaarde;
			verwerktijd	= bepaalVerwerktijd();
			return true;
		}
		System.out.println("Error, " + nieuweWaarde + " is geen geldige waarde voor wijzigLocatie");
		return false;
	}

	//Geeft de index van een stad
	private int geefIndex(String naam) {
		for(int i=0; i<City.CITIES.size();i++) {
			if(naam.equalsIgnoreCase(City.CITIES.get(i).getName())) {
				return i;
			}
		}
		
		return -1;
	}
	
	//Bepaalt hoe lang het toestel minimaal aan de grond moet staan
	private int bepaalVerwerktijd() {
		if(hierTanken) {
			return VERWERK_TIJD + TANK_TIJD;
		}
		
		return VERWERK_TIJD;
	}
		
	//Cloont het landing object
	public Landing clone() {
		return new Landing(this.locatie, this.grondtijd, this.hierTanken);
	}
}
