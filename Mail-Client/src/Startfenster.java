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
	private static Scanner sc = new Scanner(System.in);
	static String datName = "KontenListe.xml";
	public static void init(){
		inXML=new File(datName);

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


		// Initalisierung der kommandoliste
		kommandoliste.add("neues Konto anlegen");
		kommandoliste.add("bestehendes Konto waehlen");
		kommandoliste.add("Kommandos anzeigen");
		kommandoliste.add("beenden");
		auswaehlen();
	}
	
	public static void auswaehlen() {
		holeKonten();
		System.out.println("Sie haben die Moeglichkeit folgende Kommandos einzugeben: ");
		for (int i = 1; i <= kommandoliste.size(); i++) {
			System.out.print(i+": "+kommandoliste.get(i-1)+"\n");
		}
		int eingabe=Integer.parseInt(sc.nextLine());
		
		switch(eingabe){
		case 1:
				try {
					neuesKonto(); // kein 'auswaehlen' danach, weil am Ende der Methode wird init von Mailuebersicht aufgerufen
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				break;
		case 2: kontoWaehlen(); auswaehlen();
				break;
			case 3: kommandos(); auswaehlen();
				break;
		case 4: verlassen();
			    break;
		}
	}


	public static void kommandos() {
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
			System.exit(1);
		}*/
		passwort=sc.nextLine();

		System.out.println("Bitte geben Sie die gewuenschte Aktualisierungsrate ein(in sek)");
		double refRate =Double.parseDouble(sc.nextLine());
		
		
		Konto konto = new Konto(name, adresse, passwort, refRate);
		Mailuebersicht.konto=new Konto(name, adresse, passwort, refRate);
		Session s=Mailuebersicht.getSession();
		
		Transport tr = s.getTransport("smtp");
		
		try{
			tr.connect(konto.getSmtpServer(), konto.getAdress(), passwort);
		}catch (AuthenticationFailedException e){
			System.out.println("Verbindung konnte nicht hergestellt werden, bitte ueberpruefen sie ihre Eingaben");
			neuesKonto();
		}
		System.out.println("Verbindung hergestellt");
		try {
			speichereKonto(konto);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		Mailuebersicht.init(konto);
	}

	public static void speichereKonto(Konto k) throws JDOMException, IOException{
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
	        paddy.addContent(new Element("port").addContent(k.getPort()+""));
	        paddy.addContent(new Element("protocol").addContent(k.getProtocol()));
	        paddy.addContent(new Element("refRate").addContent(k.getRefRate()+""));
	        root.addContent(paddy);
	        XMLOutputter outp = new XMLOutputter();
	        outp.setFormat( Format.getPrettyFormat() );
	        outp.output( doc, new FileOutputStream(datName));
		}
		catch(Exception e){
			System.out.println(e);
			System.out.println("Fehler beim schreiben eines neuen Kontos");
		}
	}

	private static void holeKonten(){
		Document doc = null;
		konten.clear();
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
		System.out.println("Sie koennen aus folgenden Konten auswaehlen: ");
		for(int i = 0; i  < konten.size(); i++){
			System.out.println(i+1 + ")/t" + konten.get(i).getName() + "/t" + konten.get(i).getAdress()+"\n");
		}
		int i = sc.nextInt();
		Konto konto=konten.get(i-1);
		Mailuebersicht.init(konto);
	}

	public static void verlassen(){
		System.exit(1);
	}
}
