package src;
//v1.3:Vergeten aanpassing door te voeren, MaakVertrekArray
//old		for(int i=1; i<aantalLandingen; i++) {
//new		for(int i=2; i<aantalLandingen; i++) {

//v1.3: MaakVertrekArray// voorkomt de arrayoutofbounds exception 3, wanneer je een lege route hebt
//old		return new int[][] {{0, 0, 0}};
//new		return new int[][] {{0, 0, 0,0}};

//v1.2:MaakVertrekArray
//old		for(int i=1; i<aantalLandingen -1; i++) {
//new		for(int i=1; i<aantalLandingen; i++) {

//v1.2:GeefRouteDuur
// Converteert niet meer bij elke landing naar een int, maar doet dit pas bij het retouneren van het totaal.

//v1.1: Changelog >> Verwijderlanding if(index < aantalLandingen) >> if(index >= aantalLandignen)
/*	Het Vliegtuig object bevat gegevens van het vliegtuigtype dat Mokum Airlines
 gebruikt en bevat methoden om de vliegtuig routes te maken
 */
public class Vliegtuig {

	private static final String THUISHAVEN = "Amsterdam";
	private static final int MAX_ROUTE_LENGTE = 20; //Meer dan 20 landingen komt niet voor
	
	private static final int MAX_PASSAGIERS = 199; // Het maximaal aantal
													// passagiers dat het
													// vliegtuig kan vervoeren
	private static final int MAX_BEREIK = 3199; // Het aantal kilometers dat het
												// vliegtuig kan afleggen op 1
												// tank
	private static final int VLIEGTUIG_SNELHEID = 800; // Maximale snelheid van
														// het vliegtuig in km/h
	private int aantalLandingen; // De route die het vliegtuig aflegt: een
									// rijtje landingen
	private Landing[] route;

	// Constructors
	public Vliegtuig() {
		aantalLandingen = 0;
		route = new Landing[MAX_ROUTE_LENGTE];
	}

	public Vliegtuig(int duur) {
		maakRandomRoute(duur);
	}

	/* Methoden */
	// Geeft het aantal landingen in de huidige route van het vliegtuig
	public int geefAantalLandingen() {
		return aantalLandingen;
	}

	// Geeft de snelheid van het vliegtuig
	public int geefSnelheid() {
		return VLIEGTUIG_SNELHEID;
	}

	// Geeft de capaciteit van het vliegtuig
	public int geefCapaciteit() {
		return MAX_PASSAGIERS;
	}

	// Geeft het bereik van het vliegtuig
	public int geefBereik() {
		return MAX_BEREIK;
	}

	// Geeft de landing op index in de route
	public Landing geefLanding(int index) {
		if (index > aantalLandingen - 1) {
			System.out.println("geefLanding: voor index " + index
					+ " is er geen landing");
			return null;
		}
		return route[index];
	}

	// Geeft aan of de route de thuishaven aandoet
	public boolean langsThuishavenGeweest() {
		for (int i = 0; i < aantalLandingen; i++) {
			if (geefLanding(i).geefLocatieNaam().equalsIgnoreCase(THUISHAVEN)) {
				return true;
			}
		}
		return false;
	}

	// Plant de tankbeurten dusdanig dat het vliegtuig pas tankt als het in de
	// lucht droog komt te staan wanneer dit niet gebeurt. !! GAAT ER VAN UIT
	// DAT
	// ER NOG NIET GETANKT WORDT TIJDENS EEN LANDING
	public void planTankbeurten() {
		if (aantalLandingen > 1) {

			int bereik = MAX_BEREIK;
			for (int i = 1; i < aantalLandingen; i++) {
				int afstand = geefLanding(i - 1).geefAfstandNaar(
						geefLanding(i).geefLoc());
				if (afstand > MAX_BEREIK) {
					System.out
							.println("Een vliegtuig vliegt naar een bestemming die buiten zijn bereik ligt");
				}
				if (afstand > bereik) {
					geefLanding(i - 1).wijzigTankbeurt(true);
					bereik = MAX_BEREIK - afstand;
				} else {
					bereik -= afstand;
				}
			}
		}
	}

	// Retouneert de verzameling landingen van dit vliegtuig vanaf beginIndex
	// met lengte
	public Landing[] geefSubroute(int beginIndex, int lengte) {
		if ((aantalLandingen - beginIndex - lengte) >= 0) {
			Landing[] resultaat = new Landing[lengte];
			for (int i = beginIndex; i < beginIndex + lengte; i++) {
				resultaat[i - beginIndex] = route[i].clone();
			}
			return resultaat;
		}

		System.out.println("Een subroute met lengte " + lengte
				+ " is te groot vanaf " + beginIndex);
		return null; // Een subroute met deze lengte is te groot vanaf
						// beginIndex
	}

	// Geeft de duur van de route
	public int geefRouteDuur() {
		double routeTijd = 0;
		if (aantalLandingen == 0) {
			return 0;
		}

		routeTijd += route[0].geefGrondtijd();
		for (int i = 1; i < aantalLandingen; i++) {
			Landing punt = route[i];
			double tmp = (double) punt.geefAfstandNaar(route[i - 1].geefLoc())
					/ VLIEGTUIG_SNELHEID * 60;
			routeTijd += tmp + punt.geefTotaleGrondtijd();
		}

		// Bij de laatste landing maakt het niet meer uit hoe lang het toestel
		// aan de grond staat
		routeTijd -= route[aantalLandingen - 1].geefTotaleGrondtijd();
		if (routeTijd < 0) {
			return 0;
		}
		return (int) routeTijd;
	}

	// Voegt toeTeVoegen achteraan toe aan de huidige route als dit mogelijk is
	public boolean voegRouteToe(Landing[] toeTeVoegen) {
		int nieuweLengte = this.aantalLandingen + toeTeVoegen.length;
		if (nieuweLengte > MAX_ROUTE_LENGTE) {
			System.out
					.print("CombineerRoutes: lengte van samengevoegde route is te groot ");
			System.out.println("(" + this.aantalLandingen + " + "
					+ toeTeVoegen.length + " > " + MAX_ROUTE_LENGTE + ")");
			return false;
		}
		for (int i = this.aantalLandingen; i < nieuweLengte; i++) {
			this.route[i] = toeTeVoegen[i - this.aantalLandingen].clone();
		}
		this.aantalLandingen = nieuweLengte;
		return true;
	}

	// Maakt een route van random landingen die ONGEVEER duur minuten kost
	public void maakRandomRoute(int duur) {
		aantalLandingen = 0;
		route = new Landing[MAX_ROUTE_LENGTE];
		while (geefRouteDuur() < duur) {
			if (!voegRandomLandingToe()) {
				break;
			}
		}
		planTankbeurten();
	}

	// WijzigLanding bestaat uit meerdere methoden die eigenschappen van een
	// landing veranderen
	// Bijvoorbeeld grondtijd, locatie of tanken
	public boolean wijzigLanding(int index, City nieuweLoc, int nieuweGrondtijd,
			boolean hierTanken) {
		// <= : een wijziging mag de eerstvolgende lege index zijn of een
		// bestaande index. Maar nooit groter dan de lengte van de route
		if (index <= aantalLandingen && index < route.length) {
			route[index] = new Landing(nieuweLoc, nieuweGrondtijd, hierTanken);
			return true;
		}

		System.out
				.println("WijzigLanding: meegegeven index " + index
						+ "is te groot. (" + aantalLandingen + "/"
						+ route.length + ")");
		return false;
	}

	public boolean wijzigLanding(int index, City nieuweLoc) {
		// <= : een wijziging mag de eerstvolgende lege index zijn of een
		// bestaande index. Maar nooit groter dan de lengte van de route
		if (index <= aantalLandingen && index < route.length) {
			route[index].wijzigLocatie(nieuweLoc);
			return true;
		}

		System.out
				.println("WijzigLanding: meegegeven index " + index
						+ "is te groot. (" + aantalLandingen + "/"
						+ route.length + ")");
		return false;
	}

	// Wijzigt de grondtijd van de landing op index
	public boolean wijzigGrondtijd(int index, int nieuweGrondtijd) {
		// <= : een wijziging mag de eerstvolgende lege index zijn of een
		// bestaande index. Maar nooit groter dan de lengte van de route
		if (index <= aantalLandingen && index < route.length) {
			route[index].wijzigGrondtijd(nieuweGrondtijd);
			return true;
		}

		System.out
				.println("WijzigLanding: meegegeven index " + index
						+ "is te groot. (" + aantalLandingen + "/"
						+ route.length + ")");
		return false;
	}

	// Voegt een landing toe op de eerstvolgende lege plek in de route
	public boolean voegLandingToe(City nieuweLoc, int nieuweGrondtijd,
			boolean hierTanken) {
		if (aantalLandingen < route.length) {
			route[aantalLandingen] = new Landing(nieuweLoc, nieuweGrondtijd,
					hierTanken);
			aantalLandingen++;
			return true;
		}

		System.out.println("VoegLandingToe: Array is al vol");
		return false;
	}

	// Voegt een random landing toe op de eerstvolgende lege plek in de route
	public boolean voegRandomLandingToe() {
		if (aantalLandingen < route.length) {
			route[aantalLandingen] = new Landing();
			aantalLandingen++;
			return true;
		}

		System.out.println("VoegLandingToe: Array is al vol");
		return false;
	}

	// Verwijdert de landing op index
	public boolean verwijderLanding(int index) {
		if (index < route.length) {
			aantalLandingen--; // door dit hier te zetten krijgen we geen array
								// out of bounds in de komende for loop
			for (int i = index; i < aantalLandingen; i++) {
				route[i] = route[i + 1];
			}
			route[aantalLandingen] = null;

			if (index >= aantalLandingen) {
				System.out.println("VerwijderLanding: index was al leeg");
			}
			return true;
		}

		System.out.println("verwijderLanding: te grote index (" + index + "/"
				+ (route.length - 1) + ")");
		return false;
	}

	// Voegt een Landing toe op index
	public boolean insertLanding(int index, Landing landing) {
		if (index < route.length) {
			for (int i = aantalLandingen; i > index; i--) {
				route[i] = route[i - 1];
			}
			route[index] = landing;
			aantalLandingen++;
		}
		System.out.println("insertLanding: te grote index (" + index + "/"
				+ (route.length - 1) + ")");
		return false;
	}

	// Maakt een array met vertrektijden van dit vliegtuig
	// Het geeft terug een array van opstijgingen met de info:
	// 1) tijdstip van vertrek 2) vertreklocatie 3)bestemming 4)vliegtuig
	// capaciteit
	public int[][] maakVertrekArray() {
		// Als het vliegtuig opstijgt
		if (aantalLandingen > 1) {
			int time = route[0].geefGrondtijd(); // niet totale grondtijd want
													// het is het eerste punt op
													// de route
			int[][] result = { { time, route[0].geefLoc(), route[1].geefLoc(),
					MAX_PASSAGIERS } };

			for (int i = 2; i < aantalLandingen; i++) {
				Landing a = route[i - 1];
				Landing b = route[i];
				double tmp = (double) a.geefAfstandNaar(b.geefLoc())
						/ VLIEGTUIG_SNELHEID * 60; // *60 om per min ipv uur te
													// krijgen
				time += (int) tmp + b.geefTotaleGrondtijd();
				int[][] array = { { time, a.geefLoc(), b.geefLoc(),
						MAX_PASSAGIERS } };
				result = joinMultiArrays(result, array);
			}

			return result;
		}
		// Het vliegtuig stijgt niet eens op
		return new int[][] { { 0, 0, 0, 0 } };
	}

	// Voegt twee multi arrays samen
	private int[][] joinMultiArrays(int[][] a, int[][] b) {
		int[][] result = new int[a.length + b.length][2]; // de [2] kan elke
															// waarde zijn,
															// tijdelijke
															// stakeholder die
															// overschreven
															// wordt
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i];
		}
		for (int i = a.length; i < result.length; i++) {
			result[i] = b[i - a.length];
		}

		return result;
	}

	// Cloont een vliegtuig object
	public Vliegtuig clone() {
		Vliegtuig result = new Vliegtuig();
		for (int i = 0; i < this.aantalLandingen; i++) {
			result.route[i] = this.route[i].clone();
			result.aantalLandingen++;
		}

		return result;
	}
}
