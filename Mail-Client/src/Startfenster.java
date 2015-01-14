import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.util.List;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter; 

public class Startfenster{

	static ArrayList<Konto> konten;
	static File inXML;
	static ArrayList<String> elemList = new ArrayList<String>();
	private static ArrayList<String> kommandoliste=new ArrayList<String>();
	public static Scanner sc;
	static String datName = "KontenListe.xml";

	public static void init(){
		sc=new Scanner(System.in);
		inXML=new File(datName);

		//Erster Aufbau von KontenListe-Datei
		if (!inXML.exists()) {
			try {
				inXML.createNewFile();
				try {
					Element root = new Element("kontenListe");
					Document doc = new Document(root);
					XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
					out.output(doc,new FileOutputStream(inXML));
				}catch(Exception e){
					System.out.println("Die KontenListe-Datei konnte nicht initialisiert werden.");
				}
			} catch (IOException e) {
				System.err.println("Error creating " + inXML.toString());
			}
		}
			konten=new ArrayList<Konto>();

		//Erster Aufbau der Adressbuch-Datei
		Adressbuch.adressDat=new File(Adressbuch.datName);
		if (!Adressbuch.adressDat.exists()) {
			try {
				Adressbuch.adressDat.createNewFile();
				try {
					Element root = new Element("adressdat");
					Document doc = new Document(root);
					XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
					out.output(doc,new FileOutputStream(Adressbuch.adressDat));

				}catch(Exception e){
					System.out.println("Die Adressbuch-Datei konnte nicht initialisiert werden.");
				}
			} catch (IOException e) {
				System.err.println("Error creating " + inXML.toString());
			}
		}

		//Erster Aufbau der offlineMails-Datei
		Mailuebersicht.offlineMails=new File(Mailuebersicht.datName);
		if (!Mailuebersicht.offlineMails.exists()) {
			try {
				Mailuebersicht.offlineMails.createNewFile();
				try {
					Element root = new Element("offlineMails");
					Document doc = new Document(root);
					XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
					out.output(doc,new FileOutputStream(Mailuebersicht.offlineMails));

				}catch(Exception e){
					System.out.println("Die OfflineMails-Datei konnte nicht initialisiert werden.");
				}
			} catch (IOException e) {
				System.err.println("Error creating " + inXML.toString());
			}
		}

		// Initalisierung der kommandoliste
		kommandoliste.add("neues Konto anlegen");
		kommandoliste.add("bestehendes Konto waehlen");
		kommandoliste.add("Kommandos anzeigen");
		kommandoliste.add("beenden");
		auswaehlen();
	}

	private static void auswaehlen() {
		while(true){
			holeKonten();
			System.out.println("Sie haben die Moeglichkeit folgende Kommandos einzugeben: ");
			for (int i = 1; i <= kommandoliste.size(); i++) {
				System.out.print(i+": "+kommandoliste.get(i-1)+"\n");
			}
			int eingabe = -1;
			while(eingabe < 1 || eingabe > 4){
				try{
					eingabe=Integer.parseInt(sc.nextLine());
					if(eingabe < 1 || eingabe > 4){
						System.out.println("Fehlerhafte Eingabe, bitte geben gueltigen Befehl eingeben:");
					}
				}
				catch(Exception e){
					System.out.println("Fehlerhafte Eingabe, bitte geben gueltigen Befehl eingeben:");
				}

			}//schleife zur sicheren Befehlseingabe
			switch(eingabe){
				case 1:
					try {
						neuesKonto(); // kein 'auswaehlen' danach, weil am Ende der Methode wird init von Mailuebersicht aufgerufen
					} catch (MessagingException e) {
						e.printStackTrace();
					}
					break;
				case 2: kontoWaehlen();
					break;
				case 3: clearAll();
					break;
				case 4: verlassen();
					break;
			}
		}

		//Warum nicht statt dem auswaehlen endlosschleife drum herum??!
	}

	private static void kommandos() {
		System.out.println("Sie haben die Moeglichkeit folgende Kommandos einzugeben: ");
		for (int i = 1; i < kommandoliste.size(); i++) {
			System.out.print(i+": "+kommandoliste.get(i-1)+"\n");
		}
	}

	private static void neuesKonto() throws MessagingException{
		//hole daten fuer das zu speichernde Konto
		System.out.println("Bitte geben Sie Ihren Namen ein:");
		String name = sc.nextLine();
		System.out.println("Bitte geben Sie Ihre Mail-Addresse ein:");
		String adresse = sc.nextLine();
		String st = adresse.replace('@', 'p');
		if(elemList.contains(st)){
			clearAll();
			System.out.println("Adresse ist schon vorhanden");
			return;
		}		
		System.out.println("Bitte geben Sie Ihr Passwort ein:");
		String passwort="";
		/*if ( System.console() != null ){
			passwort = new String( System.console().readPassword() );
		}
		else{
			System.out.println("Fehler bei Passworteingabe");
			System.exit(1); nicht exit alle nachfolgende muss dann auch ins if
		}*/
		passwort=sc.nextLine();

		System.out.println("Bitte geben Sie die gewuenschte Aktualisierungsrate ein(in sek)");
		double refRate =Double.parseDouble(sc.nextLine());
		
		
		Konto konto = new Konto(name, adresse, passwort, refRate);
		Mailuebersicht.konto=konto;
		Session s=Mailuebersicht.getSession();
		
		Transport tr = s.getTransport("smtp");

		try{
			tr.connect(konto.getAdress(), passwort);
			System.out.println("Verbindung hergestellt...");
			speichereKonto(konto);
		}catch (AuthenticationFailedException e){
			clearAll();
			System.out.println("Startfenster_neuesKonto: Verbindung konnte nicht hergestellt werden, bitte ueberpruefen sie ihre Eingaben");
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mailuebersicht.init(konto);

	}

	private static void speichereKonto(Konto k) throws JDOMException, IOException{
		try{
			Document doc = null;
	        SAXBuilder builder = new SAXBuilder();
	        doc = builder.build(inXML);
	        
	        Element root = doc.getRootElement();
	        String tmp = k.getAdress();
	        tmp = tmp.replace('@', 'p');
	        Element paddy = new Element(tmp);
	        paddy.addContent(new Element("name").addContent(k.getName()));
	        paddy.addContent(new Element("adresse").addContent(k.getAdress()));
	        paddy.addContent(new Element("server").addContent(k.getServer()));
	        paddy.addContent(new Element("smtpServer").addContent(k.getSmtpServer()));
			paddy.addContent(new Element("pop3Server").addContent(k.getPop3Server()));
	        paddy.addContent(new Element("port").addContent(k.getPort()+""));
	        paddy.addContent(new Element("protocol").addContent(k.getProtocol()));
	        paddy.addContent(new Element("refRate").addContent(k.getRefRate()+""));
	        root.addContent(paddy);
	        XMLOutputter outp = new XMLOutputter();
	        outp.setFormat( Format.getPrettyFormat() );
	        outp.output( doc, new FileOutputStream(inXML));
		}
		catch(Exception e){
			clearAll();
			System.out.println(e);
			System.out.println("Fehler beim schreiben eines neuen Kontos");
		}
	}

	private static void holeKonten(){
		Document doc = null;
		konten.clear();
        try {
			//TODO XMLoutputter benutzen
            // Das Dokument erstellen
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(inXML);

            // Wurzelelement wird auf root gesetzt
            Element root = doc.getRootElement();
             
            //Liste aller vorhandenen Mailkonten als Elemente
            List alleKonten = root.getChildren();
            
            for(int i = 0; i < alleKonten.size(); i++){
            	String name = ((Element) alleKonten.get(i)).getChild("name").getValue();
            	String adresse = ((Element) alleKonten.get(i)).getChild("adresse").getValue();
            	String server = ((Element) alleKonten.get(i)).getChild("server").getValue();
            	String smtpServer = ((Element) alleKonten.get(i)).getChild("smtpServer").getValue();
				String pop3Server = ((Element) alleKonten.get(i)).getChild("pop3Server").getValue();
            	int port = Integer.parseInt(((Element) alleKonten.get(i)).getChild("port").getValue());
            	String protocol = ((Element) alleKonten.get(i)).getChild("protocol").getValue();
            	double refRate = Double.parseDouble(((Element) alleKonten.get(i)).getChild("refRate").getValue());
            	Konto k1 = new Konto(name, adresse, server, smtpServer, pop3Server, port, protocol, refRate);
            	String st = adresse.replace('@', 'p');
            	elemList.add(st);
            	konten.add(k1);
            }
        }
        catch(Exception e){
        	System.out.println(e);
        	System.out.println("Startfenster-hole-Konten: Datei Fehlerhaft oder nicht gefunden");
        }
	}

	private static void kontoWaehlen(){
		clearAll();
		System.out.println("Sie koennen aus folgenden Konten auswaehlen: ");
		System.out.println("0: abbrechen");
		for(int i = 0; i  < konten.size(); i++){
			System.out.println(i+1 + ": " + konten.get(i).getName() + "\t" + konten.get(i).getAdress());
		}
		int i = -1;
		while(i < 0 || i > konten.size()){
			try{
				i =Integer.parseInt(sc.nextLine());
				if(i < 0 || i > konten.size()){
					System.out.println("Falsche Eingabe, bitte geben sie einen gueltigen Befehl ein:");
				}
			}
			catch(Exception e){
				System.out.println("Falsche Eingabe, bitte geben sie einen gueltigen Befehl ein:");
			}
		}
		if(i == 0){
			clearAll();
		}
		else{
			Konto konto=konten.get(i-1);
			System.out.println("\nEinloggen: Passwort eingeben\nOfflinemails anzeigen: ohne Eingabe bestätigen");
			String st = sc.nextLine();
			if(st.equals("")){
				clearAll();
				OfflineMails.initOffline(konto.getAdress());
				clearAll();
				return;
			}
			konto.setPassword(st);
			Mailuebersicht.init(konto);
		}

	}


	public static void clearAll(){
		for(int i = 0; i < 53; i++){
			System.out.println();
		}
	}
	private static void verlassen(){
		System.out.println("Auf Wiedersehen");
		System.exit(1);
	}
}
