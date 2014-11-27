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

public class Adressbuch extends Fenster{

	private ArrayList<String> adressen;
	private File adressDat;

	public Adressbuch(){
		adressen=new ArrayList<String>();
		adressDat=new File("adressbuch.xml");

		//Initialisierung der Kommandoliste
		kommandoliste.add("hinzufuegen");
		kommandoliste.add("loeschen");
		kommandoliste.add("aendern");
		kommandoliste.add("zurueck");
		kommandoliste.add("kommandos");

		try {
			Element root = new Element("adressen");
			Document doc = new Document(root);
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(doc,new FileOutputStream(adressDat));
		}catch(Exception e){
		System.out.println("Die Adressbuch-Datei konnte nicht initialisiert werden.");
		}

		for(int i=0;i<adressen.size()&&i<25;i++){
			String tmp=adressen.get(i);
			System.out.println(i+"\t"+tmp);
		}
	}
	public ArrayList<String> getAdressen() {
		return adressen;
	}
	public void setAdressen(ArrayList<String> adressen) {
		this.adressen = adressen;
	}

	public void hinzufuegen(){
		System.out.println("Bitte geben Sie den Namen und die Adresse ein, die Sie hinzufuegen moechten.");
		Scanner sc=new Scanner(System.in);
		String name=sc.next();
		String adr=sc.next();
		adressen.add(adr);
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

			root.addContent(new Element("name")
					.addContent(new Element("adresse").addContent(adr)));
		}catch(Exception e){
			System.out.println("Die Adresse konnte nicht gespeichert werden.");
		}
	}

	public void loeschen(){
		System.out.println("Bitte geben Sie einen Namen der Person ein, die Sie aus Ihrem Adressbuch loeschen wollen.");
		Scanner sc=new Scanner(System.in);
		String name=sc.next();
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

			root.removeChild("name");
		}catch(Exception e){
			System.out.println("Der Adresseintrag konnte nicht geloescht werden.");
		}
	}

	public void aendern(){
		System.out.println("Bitte geben Sie den Namen der Person ein, deren Adresse Sie aendern wollen.");
		Scanner sc=new Scanner(System.in);
		String name=sc.next();
		System.out.println("Bitte geben Sie die neue Adresse ein.");
		String adr=sc.next();
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

			root.removeChild("name");
			root.addContent(new Element("name")
					.addContent(new Element("adresse").addContent(adr)));
		}catch(Exception e){
			System.out.println("Der Adresseintrag konnte nicht geloescht werden.");
		}

	}

}
