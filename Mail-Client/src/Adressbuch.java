import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Das Adressbuch realisiert die Ansicht in der man Kontakte speichern kann.
 * Es ist außerdem moeglich, Kontakte zu loeschen oder zu aendern,
 * sich die bereits gespeichtern Kontakte anzeigen zu lassen,
 * sich die moeglichen Kommandos anzeigen zu lassen und in
 * die vorherige Ansicht zurueckzukehren.
 */
public class Adressbuch {

	//enthaelt alle Adressen des aktuellen Kontos
	private static ArrayList<String> adressen;
	public static File adressDat;
	private static ArrayList<String> kommandoliste;
	public static Konto konto;
	private static Scanner sc;
	public static String datName="adressbuch.xml";

	/**
	 * Die Methode init wird aufgerufen, wenn man in die Startfenster-Ansicht gelangen will.
	 * Die Klassenattribute werden mit Werten belegt.
	 * Dafür werden u.a. die Adressen aus der XML-Datei eingelesen.
	 * Die bereits gespeicherten Adressen werden einmal ausgegeben.
	 * @param k
	 */
	public static void init(Konto k){
		sc=Startfenster.sc;
		konto=k;
		kommandoliste=new ArrayList<String>();
		adressen=new ArrayList<String>();

		//Initialisierung der Kommandoliste
		kommandoliste.add("hinzufuegen");
		kommandoliste.add("loeschen");
		kommandoliste.add("aendern");
		kommandoliste.add("zurueck");
		kommandoliste.add("kommandos");
		kommandoliste.add("anzeigen");

		//Anzeigen der Adressen
		adressDat=new File(datName);
		try {
			holeAdressen();
		}catch(Exception e){
			System.out.println("Die Adressbuch-Datei konnte nicht geoeffnet werde.");
		}
		Startfenster.clearAll();
		anzeigen();
		auswaehlen();
	}

	private static void auswaehlen() {
		System.out.println("Wählen Sie durch Eingabe der jeweiligen Zahl über die Tastatur den gewünschten Menüpunkt");
		kommandos();
		int eingabe=Integer.parseInt(sc.nextLine());

		switch(eingabe){
			case 1: hinzufuegen(); auswaehlen();
				break;
			case 2:loeschen(); auswaehlen();
				break;
			case 3: aendern(); auswaehlen();
				break;
			case 4: zurueck();
				break;
			case 5: kommandos(); auswaehlen();
				break;
			case 6: anzeigen(); auswaehlen();
				break;
		}
	}

	public static ArrayList<String> getAdressen() {
		return adressen;
	}
	public static void setAdressen(ArrayList<String> adr) {
		adressen = adr;
	}

	private static void anzeigen(){
		System.out.println("Ihr Adressbuch enthält folgende Adressen: ");
		for (int i = 0; i <adressen.size(); i++) {
			System.out.println(i + 1 + ": " + adressen.get(i));
		}
		System.out.println("");
	}

	private static void hinzufuegen(){
		System.out.println("Bitte geben Sie den Namen und die Adresse ein, die Sie hinzufuegen moechten.");
		String name=sc.nextLine();
		String adr=sc.nextLine();
		Document doc = null;
		if(!adressen.contains(adr)) {
			try {
				// Das Dokument erstellen
				SAXBuilder builder = new SAXBuilder();
				doc = builder.build(adressDat);

				// Wurzelelement wird auf root gesetzt
				Element root = doc.getRootElement();

				//neuer Eintrag an aktuelle Konto (Kind) anhaengen
				Element akt = root.getChild((konto.getAdress()).replace('@', 'p'));
				if (akt != null) {
					Element neu = new Element(adr.replace('@', 'p'));
					neu.addContent(new Element("name").addContent(name));
					neu.addContent(new Element("adresse").addContent(adr));
					akt.addContent(neu);
					XMLOutputter outp = new XMLOutputter();
					outp.setFormat(Format.getPrettyFormat());
					outp.output(doc, new FileOutputStream(Adressbuch.adressDat));
					adressen.add(adr);
				} else {
					System.out.println("Fehler: Warum gibt es das aktuelle Konto nicht als Kind vom root?");
				}
			} catch (Exception e) {
				System.out.println(" Die Adresse konnte nicht gespeichert werden\n.");
			}
		}else{
			System.out.println("Diese Adresse haben Sie bereits in Ihrem Adressbuch gespeichert.\n");
		}
	}

	private static void loeschen(){
		System.out.println("Bitte geben Sie die Nummer der Person ein, die Sie aus Ihrem Adressbuch loeschen wollen.");
		anzeigen();
		int nummer=Integer.parseInt(sc.nextLine());
		Document doc = null;
		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(adressDat);
			XMLOutputter fmt = new XMLOutputter();
			// Wurzelelement wird auf root gesetzt
			Element root = doc.getRootElement();
			//Liste aller vorhandenen Adressen als Elemente
			List alleAdressen = root.getChildren();
			//Loeschen
			root.getChild(konto.getAdress().replace('@','p')).removeChild(adressen.get(nummer-1).replace('@','p'));
			//gescheite Ausgabe
			fmt.setFormat(Format.getPrettyFormat());
			fmt.output(doc, new FileOutputStream(Adressbuch.adressDat));

			adressen.remove(nummer-1);
		}catch(Exception e){
			System.out.println("Der Adresseintrag konnte nicht geloescht werden.");
		}
	}

	private static void aendern(){
		System.out.println("Bitte geben Sie die Nummer des Eintrags ein, dessen Adresse Sie aendern wollen.");
		int nummer=Integer.parseInt(sc.nextLine());
		System.out.println("Bitte geben Sie die neue Adresse ein.");
		String adr=sc.nextLine();
		Document doc = null;
		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(adressDat);
			// Wurzelelement wird auf root gesetzt
			Element root = doc.getRootElement();

			//neuer Eintrag an aktuelle Konto (Kind) anhaengen
			Element akt=root.getChild((konto.getAdress()).replace('@', 'p'));
			if(akt!=null) {
				String name=akt.getChild(adressen.get(nummer - 1)).getChild("name").getValue();
				akt.removeChild(adressen.get(nummer-1));
				Element neu=new Element(adr.replace('@','p'));
				neu.addContent(new Element("name").addContent(name));
				neu.addContent(new Element("adresse").addContent(adr));
				akt.addContent(neu);
				XMLOutputter outp = new XMLOutputter();
				outp.setFormat( Format.getPrettyFormat() );
				outp.output(doc, new FileOutputStream(Adressbuch.adressDat));
				adressen.add(adr);
			}else{
				System.out.println("Warum gibt es das aktuelle Konto nicht als Kind vom root?");
			}
		}catch(Exception e){
			System.out.println("Der Adresseintrag konnte nicht geloescht werden.");
		}
	}

	private static void kommandos() {
		System.out.println("Sie haben die Moeglichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(i+1+": "+kommandoliste.get(i)+"\n");
		}
	}

	private static void zurueck(){
		//absichtlich leer
	}

	public static void holeAdressen()throws Exception{
		Document doc = null;
		adressen.clear();
		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build("adressbuch.xml");

			// Wurzelelement wird auf root gesetzt
			Element root = doc.getRootElement();

			//Liste aller aller gespeicherten Adressen des aktuellen Kontos
			Element akt=root.getChild(konto.getAdress().replace('@','p'));

			List alleKonten;
			if(akt!=null) {
				alleKonten = akt.getChildren();
			}else{
				alleKonten=new ArrayList(); //List selber ist abstract
			}

			for(int i = 0; i < alleKonten.size(); i++){
				String adresse = ((Element)alleKonten.get(i)).getChild("adresse").getValue();
				adressen.add(adresse);
			}
		}
		catch(Exception e){
			throw new Exception(e.getMessage()+"hi");
		}
	}
}
