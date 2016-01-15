package visualiser;
//v1.3:No Changes
//v1.2:No Changes
//v1.1:Changelog >> De dienstregeling constructor wordt nu niet direct meer aangeroepen, in plaats daarvan wordt maakDienstregeling() aangeroepen.
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
/*	De Global Traffic class bevat vooral methoden die helpen bij de grafische
	weergave van de applet en de interactie met de applet
	Ook wordt hier de representatie opgeslagen en kan je hier je algoritme in 
	schrijven
*/
@SuppressWarnings("serial")
public class GlobalTraffic extends JPanel
{
//	private static final int REGELDIKTE = 13;	//Het aantal pixels dat een lijn hoog is
	
	private static final Color[] LIJN_KLEUREN = {	//Elk vliegtuig heeft een vaste kleur
		Color.cyan,
		Color.blue,
		Color.green,
		Color.red,
		Color.yellow,
		Color.magenta
	};
	private Dienstregeling		dienstregeling;			//De dienstregeling van Mokum Airlines
	private double				scaleFactor;			//Variabele om te bepalen hoe groot de planning getekend moet worden
	private BufferedImage		achtergrond;			//Variabele om de kaart in op te slaan
	private BufferedImage		scaledImg;

	public GlobalTraffic(Dienstregeling dienstregeling) {
		this.dienstregeling = dienstregeling;
		init();
	}

	//Regelt alles wat grafisch weergegeven moet worden
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		tekenKaart(g);
		tekenDienstregelingOpKaart(g, dienstregeling);
	}
	
	//Initialiseert de applet
	public void init() {
		scaleFactor = 0.4;
		
		try {
			achtergrond = ImageIO.read(new File("resources/europe-scaled.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Tekent een dienstregeling op de kaart
	public void tekenDienstregelingOpKaart(Graphics g, Dienstregeling dienstregeling) {
		
		for(int i=0; i< dienstregeling.geefGrootte(); i++) {
			tekenRouteOpKaart(g, dienstregeling.geefVliegtuig(i), LIJN_KLEUREN[i]);
		}
		
	}
	
	//Tekent een route op de kaart
	public void tekenRouteOpKaart(Graphics g, Vliegtuig vliegtuig, Color c) {
		if(vliegtuig.geefAantalLandingen() > 1) {
			g.setColor(c);
			for(int i=1; i<vliegtuig.geefAantalLandingen();i++) {
				int[] vertrekpunt 	= vliegtuig.geefLanding(i-1).geefCoordinaten();
				int[] bestemming	= vliegtuig.geefLanding(i).geefCoordinaten();
				g.drawLine(vertrekpunt[0], vertrekpunt[1], bestemming[0], bestemming[1]);
			}
		}
	}
		
	//Tekent de kaart in de applet met alle steden er op
	public void tekenKaart(Graphics g) {
		g.drawImage(achtergrond, 0, 0,null);
		for (int i=0; i<City.CITIES.size();i++) {
			City location 	  = City.CITIES.get(i);
			int[] coordinaten = location.getCoordinates();
			Color color 	  = location.getColor();

			g.setColor(color);
			g.fillRect(coordinaten[0] - 3, coordinaten[1] - 3, 6, 6);
			g.setColor(color.darker());
			g.drawRect(coordinaten[0] - 3, coordinaten[1] - 3, 6, 6);
		}
	}

	public void setDienstregeling(Dienstregeling dienstregeling) {
		this.dienstregeling = dienstregeling;
	}

	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(701,599);
	}
}