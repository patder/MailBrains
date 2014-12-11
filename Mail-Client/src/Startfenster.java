import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.util.List;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
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

	private static ArrayList<Konto> konten;
	private static File inXML;
	private ArrayList<String> elemList = new ArrayList<String>();
	
	public Startfenster(){
		 ArrayList<String> kommandoliste=new ArrayList<String>();
		konten=new ArrayList<Konto>();
		inXML= new File("KontenListe.xml");
		holeKonten();
		
		// Initalisierung der kommandoliste
		kommandoliste.add("waehlen");
		kommandoliste.add("loeschen");
		kommandoliste.add("kommandos");
		kommandoliste.add("aendern");
		kommandoliste.add("beenden");
		
		System.out.println("0/tneues Konto anlegen");
		for(int i = 0; i  < konten.size(); i++){
			System.out.println(i+1 + "/t" + konten.get(i).getName() + "/t" + konten.get(i).getAdress());
		}
	}
	
	public static void auswaehlen(){
		System.out.println("Wählen Sie durch Eingabe der jeweiligen Zahl über die Tastatur den gewünschten Menüpunkt");
		Scanner sc=new Scanner(System.in);
		int eingabe=sc.nextInt();
		
		switch(eingabe){
		case 1: neuesKonto();
				break;
		case 2: kontoWaehlen();
				break;
		case 3: verlassen();
			    break;
		}
	}
	
	
	public void kommandos() {
		System.out.println("Sie haben die Mï¿½glichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(kommandoliste.get(i) + ", ");
		}
	}	

	
	
	private static void neuesKonto(){
		//hole daten fuer das zu speichernde Konto
		Scanner sc = new Scanner(System.in);
		System.out.println("Bitte geben Sie Ihren Namen ein:");
		String name = sc.next();
		System.out.println("Bitte geben Sie Ihre Mail-Addresse ein:");
		String adresse = sc.next();
		String st = adresse.replace('@', 'p');
		if(elemList.contains(st)){
			System.out.println("Adresse ist schon vorhanden");
			sc.close();
			return;
		}		
		System.out.println("Bitte geben Sie Ihr Passwort ein:");
		String passwort="";
		if ( System.console() != null ){
			passwort = new String( System.console().readPassword() );
		}
		else{
			System.out.println("Fehler bei Passworteingabe");
			System.exit(1);
		}

		System.out.println("Bitte geben Sie die gewuenschte Aktualisierungsrate ein(in sek)");
		double refRate = sc.nextDouble();
		sc.close();			
		
		
		Konto neuesKonto = new Konto(name, adresse, passwort, refRate);
		Session s=Senden.getSession(neuesKonto);
		
		Transport tr = s.getTransport("smtp");
		
		try{
			tr.connect(neuesKonto.getSmtpServer(), neuesKonto.getAdress(), passwort);
		}catch (AuthenticationFailedException e){
			System.out.println("Verbindung konnte nicht hergestellt werden, bitte überprüfen sie ihre Eingaben");
			neuesKonto();
		}
		System.out.println("Verbindung hergestellt");
		
//		if(!knownAdress(neuesKonto)){
//			System.out.println("BItte gegeb sie noch die folgenden Werte an: ");
//			
//			
//		}
//		if(!goodValues(neuesKonto)){
//			System.out.println("Die angegebenen Werte sind fehlerhaft, das Konto wurde nicht erstellt");
//		}
		try {
			speichereKonto(neuesKonto);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	
	
	
	
	//Speichere Konto in "inFile"
	public static void speichereKonto(Konto k) throws JDOMException, IOException{
		try{
			Document doc = null;
	        SAXBuilder builder = new SAXBuilder();
	        doc = builder.build(inXML);
	        
	        Element root = doc.getRootElement();
	        String tmp = k.getAdress();
	        tmp.replace('@', 'p');
	        Element paddy = new Element(tmp);
	        paddy.addContent(new Element("name").addContent(k.getName()));
	        paddy.addContent(new Element("adresse").addContent(k.getAdress()));
	        paddy.addContent(new Element("server").addContent(k.getServer()));
	        paddy.addContent(new Element("smtpServer").addContent(k.getSmtpServer()));
	        paddy.addContent(new Element("port").addContent(k.getPort()+""));
	        paddy.addContent(new Element("protocol").addContent(k.getProtocol()));
	        paddy.addContent(new Element("refRate").addContent(k.getRefRate()+""));
	        root.addContent(paddy);
	        XMLOutputter outp = new XMLOutputter();
	        outp.setFormat( Format.getPrettyFormat() );
	        outp.output( doc, new FileOutputStream( "XMLModelKontenDatei"));
		}
		catch(Exception e){
			System.out.println(e);
			System.out.println("Fehler beim schreiben eines neuen Kontos");
		}
	}

	
	
	public void aendern(){
		System.out.println("welchen Eintag wollen Sie aendern?");
		Scanner sc = new Scanner(System.in);
		int i = -2;
		try{
			i =sc.nextInt();
			if(i < 1 || i > konten.size()){
				sc.close();
				throw new Exception();
			}
		}
		catch(Exception e){
			System.out.println("Ungueltige Eingabe");
			return;
		}
		System.out.println("1) Kontoname: " + konten.get(i-1).getName());
		System.out.println("2) Adresse: " + konten.get(i-1).getAdress());
		System.out.println("3) Ausgangsserver: " + konten.get(i-1).getServer());
		System.out.println("4) SMTP-Server: " + konten.get(i-1).getSmtpServer());
		System.out.println("5) Protokol: " + konten.get(i-1).getProtocol());
		System.out.println("6) Port: " + konten.get(i-1).getPort());
		System.out.println("7) Aktualisierungsrate: " + konten.get(i-1).getRefRate());
		System.out.println("welchen Eintag wollen Sie aendern?");
		try{
			i =sc.nextInt();
			if(i < 1 || i > 7){
				sc.close();
				throw new Exception();
			}
		}
		catch(Exception e){
			System.out.println("Ungueltige Eingabe");
			return;
		}
		System.out.println("Geben Sie den Neuen Weret ein: ");
		String neu = "";
		try{
			if(i == 6){
				neu = sc.next();
				int tmp = Integer.parseInt(neu);
			}
			else{
				if(i == 7){
					neu = sc.next();
					double tmp = Double.parseDouble(neu);
				}
				else{
					neu = sc.next();
				}
				
			}
		}
		catch(Exception e){
			sc.close();
			System.out.println("Fehler bei aendern eines Eintags, Ungueltige Eingabe");
			return;
		}
		sc.close();
		Document doc = null;
        try {
            // Das Dokument erstellen
            SAXBuilder builder = new SAXBuilder();
            
            doc = builder.build(inXML);
            
            
            // Wurzelelement wird auf root gesetzt
            Element root = doc.getRootElement();
             
            //Liste aller vorhandenen Mailkonten als Elemente
            String st = konten.get(i-1).getAdress().replace('@', 'p');

            switch(i){
				case 1:		root.getChild(st).getChild("name").removeContent();
							root.getChild(st).getChild("name").addContent(neu);
							break;
				case 2:		root.getChild(st).getChild("adresse").removeContent();
							root.getChild(st).getChild("adresse").addContent(neu);
							break;
				case 3:		root.getChild(st).getChild("server").removeContent();
							root.getChild(st).getChild("server").addContent(neu);
							break;
				case 4:		root.getChild(st).getChild("smtpServer").removeContent();
							root.getChild(st).getChild("smtpServer").addContent(neu);
							break;	
				case 5:		root.getChild(st).getChild("port").removeContent();
							root.getChild(st).getChild("port").addContent(neu);
							break;
				case 6:		root.getChild(st).getChild("protocol").removeContent();
							root.getChild(st).getChild("protocol").addContent(neu);
							break;
				case 7:		root.getChild(st).getChild("refRate").removeContent();
							root.getChild(st).getChild("refRate").addContent(neu);
							break;
				default:	break;
            }
	        XMLOutputter outp = new XMLOutputter();
	        outp.setFormat( Format.getPrettyFormat());
	        outp.output( doc, new FileOutputStream( "XMLModelKontenDatei"));
            
        }
        catch(Exception e){
        	System.out.println("Fehler bei aendern des Attributs");
        }
	}
	
	
	
	private void holeKonten(){
		Document doc = null;

        try {
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
            	int port = Integer.parseInt(((Element) alleKonten.get(i)).getChild("port").getValue());
            	String protocol = ((Element) alleKonten.get(i)).getChild("protocol").getValue();
            	double refRate = Double.parseDouble(((Element) alleKonten.get(i)).getChild("refRate").getValue());
            	Konto k1 = new Konto(name, adresse, server, smtpServer,port, protocol, refRate);
            	String st = adresse.replace('@', 'p');
            	elemList.add(st);
            	konten.add(k1);
            }
        }
        catch(Exception e){
        	System.out.println(e);
        	System.out.println("Datei Fehlerhaft oder nicht gefunden");
        }
	}
	

	public static void kontoWaehlen(){
//		System.out.println("was moechten Sie waehlen?( Zum abbrechen waehlen Sie -1) ");
//		Scanner sc = new Scanner(System.in);
//		String st = sc.next();
//		sc.close();
//		int i = -2;
//		try{
//			i = Integer.parseInt(st);
//			if(i < -1 || i > konten.size()){
//				throw new Exception();
//			}
//		}
//		catch(Exception e){
//			System.out.println("Ungueltige Eingabe");
//		}
//		if(i == 0){
//			neuesKonto();
//			return;
//		}
//		if(i > 0 && i <=konten.size()){
//			Mailuebersicht mU = new Mailuebersicht(konten.get(i));
//			return;
//		}
//		return;
		
		Mailuebersicht.kommandos();
	}

	public void loeschen(){
		System.out.println("was wollen Sie loeschen?");
		Scanner sc = new Scanner(System.in);
		int i = -1;
		try{
			i = sc.nextInt();
			if(i > konten.size() || i < 1){
				sc.close();
				throw new Exception();
			}
		}
		catch(Exception e){
			System.out.println("ungueltige Eingabe");
			return;
		}
		sc.close();
		Document doc = null;
		try {
            // Das Dokument erstellen
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(inXML);
            
            // Wurzelelement wird auf root gesetzt
            Element root = doc.getRootElement();
            List children = root.getChildren();
            root.removeChildren(((Element)children.get(i-1)).getValue());
	        XMLOutputter outp = new XMLOutputter();
	        outp.setFormat( Format.getPrettyFormat() );
	        outp.output( doc, new FileOutputStream( "XMLModelKontenDatei"));
		}
		catch(Exception e){
			System.out.println("Fehler beim loeschen von Konto");
		}
	}

	public static void verlassen(){
		System.exit(1);	
	}
}
