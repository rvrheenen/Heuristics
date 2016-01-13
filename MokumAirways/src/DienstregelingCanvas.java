package src;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;


public class DienstregelingCanvas extends JPanel {
	private static final Color[] LIJN_KLEUREN = {	//Elk vliegtuig heeft een vaste kleur
		Color.cyan,
		Color.blue,
		Color.green,
		Color.red,
		Color.yellow,
		Color.magenta
	};
	
	private Dienstregeling dienstregeling;
	private double scaleFactor = 0.45;
	
	public DienstregelingCanvas(Dienstregeling dienstregeling){
		this.dienstregeling = dienstregeling;
	}
	
	public void setScaleFactor(double scaleFactor){
		this.scaleFactor = scaleFactor;
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		
		tekenDienstregeling(g, dienstregeling);
	}
	
	//Tekent de planning van de dienstregeling in de applet
	public void tekenDienstregeling(Graphics g, Dienstregeling dienstregeling) {
		double tmp = (double) dienstregeling.telPassagiersKilometers() / 1000;
		g.drawString(tmp+" x1000 passenger kilometers", 10, 20);
		tekenTijdlijn(0, 40,g);
		g.setColor(Color.gray);
		g.fillRect(130, 50, (int) (32 * (60 * scaleFactor)), 80);
		for(int i=0; i< dienstregeling.geefGrootte(); i++) {
			tekenRoute(g, dienstregeling.geefVliegtuig(i), i, 10, 50+13*i);
		}
	}
	
	
	//Tekent een route in de applet
	public void tekenRoute(Graphics g, Vliegtuig vliegtuig, int vliegtuigNr, int xPointer, int yPointer) {
		if(vliegtuig.geefAantalLandingen() > 0) {
			//Teken Vliegtuig ID
			g.setColor(Color.black);
			g.drawString("#" + vliegtuigNr + " || " + vliegtuig.geefRouteDuur(), xPointer, yPointer+11);
			g.setColor(LIJN_KLEUREN[vliegtuigNr]);
			g.drawRect(xPointer, yPointer, 100, 13);
			xPointer += 100;
			
			xPointer += tekenThuisbasis(vliegtuig, xPointer, yPointer, g);
			if(vliegtuig.geefAantalLandingen() > 1) {
				for(int i=1; i<vliegtuig.geefAantalLandingen(); i++) {
					xPointer += tekenVlucht(vliegtuig, i, xPointer, yPointer,g);					
				}
			}
		}
	}
	
	//Tekent een tijdlijn
	public void tekenTijdlijn(int x, int y, Graphics g)	{
		g.setColor(Color.black);
		g.drawString("Home", x + 20, y);
		for(int i=0; i<32;i++) {
			double convert	= x + 130 + i * (60 * scaleFactor);
			int xPointer	= (int) convert;
			int time		= (6 + i) % 24;
			String timeStr;
			if(i<4) {
				timeStr = "0" + time;
			} else if(i>17 && i < 28) {
				int tmp = i-18;
				timeStr = "0" + tmp;
			} else {
				timeStr = ""+time;
			}
			
			if(i == 20) {
				g.setColor(Color.red);
			}
			
			if(i == 25) {
				g.setColor(Color.black);
			}
			g.drawString(timeStr, xPointer, y);
			g.drawRect(xPointer, y, 1, 13);
		}
	}
	
	//Tekent het beginpunt van een route in de applet
	public int tekenThuisbasis(Vliegtuig vliegtuig, int xPointer, int yPointer, Graphics g) {
		//Teken ThuisBasis
		Landing thuis		= vliegtuig.geefLanding(0);
		double tmp			= thuis.geefGrondtijd() * scaleFactor;
		int	   grondtijd	= (int) tmp;
		
		g.setColor(thuis.geefLocatie().getColor());
		g.fillRect(xPointer, yPointer, 30 + grondtijd, 13);
		xPointer += 30;
		g.setColor(Color.white);
		g.drawRect(xPointer, yPointer, grondtijd, 13);
		
		g.setColor(getForeGroundColorBasedOnBGBrightness(thuis.geefLocatie().getColor()));
		g.drawString(thuis.geefLocatieNaam() + " (" + thuis.geefTotaleGrondtijd() + ")", xPointer -30, yPointer +11);
		
		xPointer += grondtijd;
		
		return grondtijd + 30;
	}
	
	//Tekent een vlucht in een route in de applet
	public int tekenVlucht(Vliegtuig vliegtuig, int i, int xPointer, int yPointer, Graphics g) {
		//bepaal lengte van de vlucht
		Landing vertrek 	= vliegtuig.geefLanding(i-1);
		Landing doel	 	= vliegtuig.geefLanding(i);
		double  tmp		 	= (double) vertrek.geefAfstandNaar(doel.geefLoc()) / vliegtuig.geefSnelheid() * (60 * scaleFactor);
		int	  	routeTijd	= (int) tmp;
		
		//bepaal grondtijd
		tmp 			= doel.geefTotaleGrondtijd() * scaleFactor;
		int grondtijd 	= (int) tmp;
		tmp				= doel.geefVerwerktijd() * scaleFactor;
		int verwerktijd = (int) tmp;
		
		//Teken de vlucht
		g.setColor(Color.gray);
		g.fillRect(xPointer, yPointer, routeTijd, 13);
		xPointer += routeTijd;
		//Teken de Landing
		g.setColor(doel.geefLocatie().getColor());
		g.fillRect(xPointer, yPointer, grondtijd, 13);
		if(doel.hierTanken()) {g.setColor(Color.red);}
		else {g.setColor(Color.white);}
		g.drawRect(xPointer, yPointer, verwerktijd, 13);
		g.setColor(getForeGroundColorBasedOnBGBrightness(doel.geefLocatie().getColor()));
		g.drawString(doel.geefLocatieNaam() + " (" + doel.geefTotaleGrondtijd() + ")", xPointer, yPointer +11);
		xPointer += grondtijd;
		
		return routeTijd + grondtijd;
	}
	
	private static int getBrightness(Color c) {
	    return (int) Math.sqrt(
	      c.getRed()   * c.getRed()   * .241 +
	      c.getGreen() * c.getGreen() * .691 +
	      c.getBlue()  * c.getBlue()  * .068);
	}
	 
	public static Color getForeGroundColorBasedOnBGBrightness(Color color) {
	    return (getBrightness(color) < 130) ? Color.white : Color.black;
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(this.getParent().getSize().width,130);
	}

	public Dienstregeling getDienstregeling() {
		return dienstregeling;
	}

	public void setDienstregeling(Dienstregeling dienstregeling) {
		this.dienstregeling = dienstregeling;
	}
}
