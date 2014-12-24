import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;

import java.net.ConnectException;
import java.util.Properties;
 

import java.util.Scanner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Mailuebersicht {
	private static ArrayList<Mail> mails;
	public static File offlineMails;
	public static String datName="offlineMails.xml";
	private static int aktuelleSeite;
	public static Konto konto;
	private static ArrayList<String> kommandoliste;
	private static Scanner sc;

	private static void holeMails(){
		//Die folgenden Zeilen vllt auch lieber in ein Methode (wie bei getSession nur halt für pop3)
		Properties properties = System.getProperties();
		properties.put("mail.pop3s.host", konto.getPop3Server());
		properties.put("mail.pop3s.port", "995");
		properties.put("mail.pop3s.starttls.enable", "true");

		//Session erstellen
		Session session = Session.getDefaultInstance(properties, konto.getPasswordAuthentication());
		try {
			Store store = session.getStore("pop3");
			store.connect(konto.getPop3Server(),konto.getName(),konto.getPassword());
			Folder folder=store.getDefaultFolder();
			Folder inboxfolder=folder.getFolder("INBOX");
			inboxfolder.open(Folder.READ_ONLY);
			Message [] msg=inboxfolder.getMessages();

			//Einlesen in die Arraylist mails (ist Attribut dieser Klasse)
			for(int i=0;i<msg.length;i++){
				try {
					//Empfangsdatum von Typ Date zu String
					DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					String reportDate = df.format(msg[i].getReceivedDate());
					//Erstellen der Mail
					Mail m = new Mail(msg[i].getFrom()[0].toString(), msg[i].getSubject(), String.valueOf(msg[i].getContent()), reportDate); //weiß nicht ob das mit dem Contetn so klappt ist eigl von Typ Object
					mails.set(i, m);
				}catch(IOException e){
					System.out.println("Der Content der Mail verursacht IO Probleme.");
				}
			}
		}catch(NoSuchProviderException e){
			System.out.println("Die Mails konnten nicht vom Server geholt werden, das der Server nicht existiert.");
		}catch(MessagingException e2){
			System.out.println("Weiß nicht was die Fehlermeldung im Kontext heißt "+e2.getMessage());
		}

	}
	public static void init(Konto k){
		//Initialisierung der Attribute
		sc=new Scanner(System.in);
		kommandoliste=new ArrayList<String>();
		konto=k;
		mails= new ArrayList<Mail>(); //Mails m�ssen vom Server geholt werden
		offlineMails=new File(datName);
		aktuelleSeite=1;
		
		// Initalisierung der kommandoliste
		kommandoliste.add("kommandos");
		kommandoliste.add("ausloggen");
		kommandoliste.add("naechste");
		kommandoliste.add("vorherige");
		kommandoliste.add("verfassen");
		kommandoliste.add("loeschen");
		kommandoliste.add("anzeigen");
		kommandoliste.add("seite");
		kommandoliste.add("adressbuch");
		kommandoliste.add("spamfilter");
		kommandoliste.add("aktualisieren");
		kommandoliste.add("speichern");
		kommandoliste.add("aendern");

		//In der Adressbuch-Datei das aktuelle Konto als Kind anhängen
		Document doc = null;
		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(Adressbuch.adressDat);

			// Wurzelelement wird auf root gesetzt
			Element root = doc.getRootElement();

			//Liste aller Adressen des aktuellen Kontos
			List<Element> alleKonten = root.getChildren();

			boolean vorhanden=false;
			for(int i=0;i<alleKonten.size();i++){
				if(alleKonten.get(i).getName().equals(konto.getAdress().replace('@', 'p'))){
					vorhanden=true;
					break;
				}
			}
			if(!vorhanden){
				root.addContent(new Element(konto.getAdress().replace('@', 'p')));
				XMLOutputter outp = new XMLOutputter();
				outp.setFormat(Format.getPrettyFormat());
				outp.output(doc, new FileOutputStream(Adressbuch.adressDat));
			}
		}
		catch(Exception e){
			System.out.println(e);
			System.out.println("Mail-Uebersicht-init: Adressbuch-Datei Fehlerhaft oder nicht gefunden");
		}

		//In der OfflineMails-Datei das aktuelle Konto als Kind anhängen
		doc = null;
		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(offlineMails);

			// Wurzelelement wird auf root gesetzt
			Element root = doc.getRootElement();

			//Liste aller Adressen des aktuellen Kontos
			List<Element> alleKonten = root.getChildren();

			boolean vorhanden=false;
			for(int i=0;i<alleKonten.size();i++){
				System.out.println(alleKonten.get(i));
				if(alleKonten.get(i).getName().equals(konto.getAdress().replace('@','p'))){
					vorhanden=true;
					break;
				}
			}
			if(!vorhanden){
				root.addContent(new Element(konto.getAdress().replace('@', 'p')));
				XMLOutputter outp = new XMLOutputter();
				outp.setFormat(Format.getPrettyFormat());
				outp.output(doc, new FileOutputStream(offlineMails));
			}
		}
		catch(Exception e){
			System.out.println(e);
			System.out.println("Mail-Uebersicht-init: offline-Mails-Datei Fehlerhaft oder nicht gefunden");
		}

		holeMails();
		for(int i=0;i<mails.size()&&i<25;i++) {
			Mail tmp = mails.get(i);
			System.out.println(i+1 + "\t" + tmp.getAdresse() + "\t" + tmp.getBetreff() + "\t" + tmp.getEmpfangsdatum());
		}
		auswaehlen();
	}

	public static void auswaehlen() {
		System.out.println("Wählen Sie durch Eingabe der jeweiligen Zahl über die Tastatur den gewünschten Menüpunkt");
		kommandos();
		int eingabe=Integer.parseInt(sc.nextLine());

		switch(eingabe){
			case 1: kommandos(); auswaehlen();
				break;
			case 2: ausloggen();
				break;
			case 3: naechste(); auswaehlen();
				break;
			case 4: vorherige(); auswaehlen();
				break;
			case 5: try {
				verfassen();
			} catch (Exception e) {
				e.printStackTrace();
			}auswaehlen();
				break;
			case 6: loeschen(); auswaehlen();
				break;
			case 7: anzeigen(); auswaehlen();
				break;
			case 8: seite(); auswaehlen();
				break;
			case 9: adressbuch(); auswaehlen();
				break;
			case 10: spamfilter();
				break;
			case 11: aktualisieren(); auswaehlen();
				break;
			case 12: speichern(); auswaehlen();
				break;
			case 13: aendern(); auswaehlen();
				break;
		}
	}
	public static ArrayList<Mail> getMails() {
		return mails;
	}

	public static void speichern(){
		System.out.println("Bitte geben Sie die Nummer der Mail an, die sie offline speichern möchten");
		int nummer=Integer.parseInt(sc.nextLine());
		Document doc = null;
		try {
			//TODO hier muss noch der xml outputter benutzt werden
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(offlineMails);
			XMLOutputter fmt = new XMLOutputter();
			// Wurzelelement wird auf root gesetzt
			Element root = doc.getRootElement();
			//Liste aller vorhandenen Adressen als Elemente
			List alleAdressen = root.getChildren();

			Mail tmp=mails.get(nummer);
			Element e1=new Element("person");
			root.getChild(konto.getName()).addContent(e1);
			e1.addContent(new Element("adresse").addContent(tmp.getAdresse()));
			e1.addContent(new Element("betreff").addContent(tmp.getBetreff()));
			e1.addContent(new Element("nachricht").addContent(tmp.getNachricht()));
			e1.addContent(new Element("empfangsdatum").addContent(tmp.getEmpfangsdatum()));
		}catch(Exception e){
			System.out.println("Das Konto konnte nicht offline gespeichert werden.");
		}
	}

	public static void anzeigen(){
		System.out.println("Bitte geben Sie die Nummer der Mail an, die Sie sehen möchten.");
		for(int i=0;i<mails.size()&&i<25;i++) {
			Mail tmp = mails.get(i);
			System.out.println(i+1 + "\t" + tmp.getAdresse() + "\t" + tmp.getBetreff() + "\t" + tmp.getEmpfangsdatum());
		}
		Scanner sc=new Scanner(System.in);
		int nummer=Integer.parseInt(sc.nextLine());
		System.out.println(mails.get(nummer-1).getNachricht());
	}
	
	public static void seite(){
		System.out.println("Bitte geben Sie die Nummer der Seite an, zu der Sie springen möchten");
		int nummer=Integer.parseInt(sc.nextLine());
		aktuelleSeite=nummer;
		for(int i=(nummer-1)*25;i<mails.size()&&i<i+25;i++){
			Mail tmp=mails.get(i);
			System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());
		}
	}
	
	public static void naechste(){
		aktuelleSeite++;
		for(int i=(aktuelleSeite-1)*25;i<mails.size()&&i<i+25;i++){
			Mail tmp=mails.get(i);
			System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());
		}
	}
	
	public static void vorherige(){
		aktuelleSeite--;
		for(int i=(aktuelleSeite-1)*25;i<mails.size()&&i<i+25;i++){
			Mail tmp=mails.get(i);
			System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());
		}
	}

	public static void aktualisieren(){

		//TODO
	}

	public static Session getSession(){ 
		Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", konto.getSmtpServer());
        properties.setProperty("mail.smtp.port", String.valueOf(konto.getPort()));
        properties.setProperty("mail.smtp.auth", "true");
         
        properties.put("mail.smtp.starttls.enable", "true");
        
        // session erstellen
        Session session = Session.getDefaultInstance(properties, konto.getPasswordAuthentication());
		return session;
	}

	public static void verfassen() throws AddressException, MessagingException
    {
		Session session=getSession();
        // nachricht erzeugen

		System.out.println("Empfaenger:");
		String empfaenger=sc.nextLine();
		System.out.println("Betreff:");
		String betreff=sc.nextLine();
		System.out.println("Nachricht:");
		String text=sc.next();
		
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(konto.getAdress()));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(empfaenger, false));
 
        // Betreff
        msg.setSubject(betreff);
         
        // Nachricht
        msg.setText(text);
         
        // E-Mail versenden
        Transport.send(msg);
    }

	public static void ausloggen(){
		// absichtlich leer
		sc.close();
	}

	public static void spamfilter(){
		Spamfilter.init();
	}

	public static void adressbuch(){
		Adressbuch.init(konto);
	}

	public static void kommandos() {
		System.out.println("Sie haben die Moeglichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(i+1+": "+kommandoliste.get(i)+"\n");
		}
	}

	public static void loeschen(){		
		System.out.println("was wollen Sie loeschen?");
		int i = -1;
		try{
			i = sc.nextInt();
			if(i > Startfenster.konten.size() || i < 1){
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
            doc = builder.build(Startfenster.inXML);
            
            // Wurzelelement wird auf root gesetzt
            Element root = doc.getRootElement();
            List children = root.getChildren();
            root.removeContent(((Element)children.get(i-1)));
	        XMLOutputter outp = new XMLOutputter();
	        outp.setFormat( Format.getPrettyFormat() );
	        outp.output( doc, new FileOutputStream(Startfenster.datName));
		}
		catch(Exception e){
			System.out.println("Fehler beim loeschen von Konto");
		}
	}
	public static void loeschen(int i){		
		Document doc = null;
		try {
            // Das Dokument erstellen
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(Startfenster.inXML);
            
            // Wurzelelement wird auf root gesetzt
            Element root = doc.getRootElement();
            List children = root.getChildren();
            root.removeContent(((Element)children.get(i-1)));
	        XMLOutputter outp = new XMLOutputter();
	        outp.setFormat( Format.getPrettyFormat() );
	        outp.output( doc, new FileOutputStream(Startfenster.datName));
		}
		catch(Exception e){
			System.out.println("Fehler beim loeschen von Konto");
		}
	}

	public static void aendern(){
		System.out.println("welchen Eintag wollen Sie aendern?");
		int i = -2;
		int port = 25;
		double ref = 10.0;
		try{
			i =sc.nextInt();
			if(i < 1 || i > Startfenster.konten.size()){
				sc.close();
				throw new Exception();
			}
		}
		catch(Exception e){
			System.out.println("Ungueltige Eingabe");
			return;
		}
		int nr = i;
		System.out.println("1) Kontoname: " + Startfenster.konten.get(i-1).getName());
		System.out.println("2) Adresse: " + Startfenster.konten.get(i-1).getAdress());
		System.out.println("3) Ausgangsserver: " + Startfenster.konten.get(i-1).getServer());
		System.out.println("4) SMTP-Server: " + Startfenster.konten.get(i-1).getSmtpServer());
		System.out.println("5) Protokol: " + Startfenster.konten.get(i-1).getProtocol());
		System.out.println("6) Port: " + Startfenster.konten.get(i-1).getPort());
		System.out.println("7) Aktualisierungsrate: " + Startfenster.konten.get(i-1).getRefRate());
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
		System.out.println("Geben Sie den Neuen Wert ein: ");
		String neu = "";
		try{
			if(i == 6){
				neu = sc.next();
				port = Integer.parseInt(neu);
			}
			else{
				if(i == 7){
					neu = sc.next();
					ref = Double.parseDouble(neu);
				}
				else{
					neu = sc.next();
				}

			}
		}
		catch(Exception e){
			System.out.println("Fehler bei aendern eines Eintags, Ungueltige Eingabe");
			return;
		}
		sc.close();
		Document doc = null;
		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();

			doc = builder.build(Startfenster.inXML);


			// Wurzelelement wird auf root gesetzt
			Element root = doc.getRootElement();

			//Liste aller vorhandenen Mailkonten als Elemente
			String st = Startfenster.konten.get(i-1).getAdress().replace('@', 'p');

            Konto neuesKonto = new Konto(Startfenster.konten.get(nr-1).getName(), Startfenster.konten.get(nr-1).getAdress(), Startfenster.konten.get(nr-1).getServer(), Startfenster.konten.get(nr-1).getSmtpServer(), Startfenster.konten.get(nr-1).getPop3Server(),  Startfenster.konten.get(nr-1).getPort(), Startfenster.konten.get(nr-1).getProtocol(), Startfenster.konten.get(nr-1).getRefRate());
            switch(i){
				case 1:		konto.setName(neu);
							neuesKonto.setName(neu);
							break;
				case 2:		konto.setServer(neu);
							neuesKonto.setAdress(neu);
							break;
				case 3:		konto.setSmtpServer(neu);
							neuesKonto.setServer(neu);
							break;
				case 4:		konto.setSmtpServer(neu);
							neuesKonto.setSmtpServer(neu);
							break;
				case 5:		konto.setProtocol(neu);
							neuesKonto.setProtocol(neu);
							break;
				case 6:		konto.setPort(port);
							neuesKonto.setPort(port);
							break;
				case 7:		konto.setRefRate(ref);
							neuesKonto.setRefRate(ref);
							break;
				default:	break;
            }
	        loeschen(nr);
	        Startfenster.speichereKonto(neuesKonto);
            
		}
		catch(Exception e){
			System.out.println("Fehler bei aendern des Attributs");
		}
	}


}
