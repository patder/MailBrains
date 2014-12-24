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

public class Adressbuch {

	//enthaelt alle Adressen des aktuellen Kontos
	public static ArrayList<String> adressen;
	public static File adressDat;
	private static ArrayList<String> kommandoliste;
	private static Konto konto;
	public static String datName="adressbuch.xml";
	public static void init(Konto k){
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

		//anzeigen der Adressen
		adressDat=new File(datName);
		holeAdressen();
		for(int i=0;i<adressen.size()&&i<25;i++){
			String tmp=adressen.get(i);
			System.out.println(i+1+"\t"+tmp);
		}

		auswaehlen();
	}

	public static void auswaehlen() {
		System.out.println("Wählen Sie durch Eingabe der jeweiligen Zahl über die Tastatur den gewünschten Menüpunkt");
		kommandos();
		Scanner sc=new Scanner(System.in);
		int eingabe=sc.nextInt();

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

	public ArrayList<String> getAdressen() {
		return adressen;
	}
	public void setAdressen(ArrayList<String> adr) {
		adressen = adr;
	}

	public static void anzeigen(){
		System.out.println("Ihr Adressbuch enthält folgende Adressen: ");
		for (int i = 0; i <adressen.size(); i++) {
			System.out.print(i+1+": "+adressen.get(i)+"\n");
		}
	}

	public static void hinzufuegen(){
		System.out.println("Bitte geben Sie den Namen und die Adresse ein, die Sie hinzufuegen moechten.");
		Scanner sc=new Scanner(System.in);
		String name=sc.nextLine();
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
			System.out.println(" Die Adresse konnte nicht gespeichert werden.");
		}
	}

	public static void loeschen(){
		System.out.println("Bitte geben Sie einen Namen der Person ein, die Sie aus Ihrem Adressbuch loeschen wollen.");
		Scanner sc=new Scanner(System.in);
		String name=sc.next();
		Document doc = null;
		try {

			//TODO hier muss noch der xmloutputter benutzt werden
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(adressDat);
			XMLOutputter fmt = new XMLOutputter();
			// Wurzelelement wird auf root gesetzt
			Element root = doc.getRootElement();
			//Liste aller vorhandenen Adressen als Elemente
			List alleAdressen = root.getChildren();

			root.removeChild("name");
		}catch(Exception e){
			System.out.println("Der Adresseintrag konnte nicht geloescht werden.");
		}
	}

	public static void aendern(){
		System.out.println("Bitte geben Sie die Nummer des Eintrags ein, dessen Adresse Sie aendern wollen.");
		Scanner sc=new Scanner(System.in);
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

	public static void kommandos() {
		System.out.println("Sie haben die Moeglichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(i+1+": "+kommandoliste.get(i)+"\n");
		}
	}

	public static void zurueck(){
		//absichtlich leer
	}
//
	private static void holeAdressen(){
		Document doc = null;
		adressen.clear();
		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(adressDat);

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

			System.out.println("hi4");
			for(int i = 0; i < alleKonten.size(); i++){
				String adresse = ((Element)alleKonten.get(i)).getChild("adresse").getValue();
				adressen.add(adresse);
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("Adressbuch-hole-Adressen: Datei Fehlerhaft oder nicht gefunden");
		}
	}
}
