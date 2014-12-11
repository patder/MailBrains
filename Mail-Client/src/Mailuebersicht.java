import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
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
	private static File offlineMails;
	private static int aktuelleSeite;
	public static Konto konto;
	private static ArrayList<String> kommandoliste;

	public Mailuebersicht(){};
	
	public static void init(Konto k){

		kommandoliste=new ArrayList<String>();
		konto=k;
		mails= new ArrayList<Mail>(); //Mails m�ssen vom Server geholt werden
		offlineMails=new File("offlineMails.xml");
		
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
		offlineMails=new File("offlineMails.xml");
		
		aktuelleSeite=1;

		try {
			Element root = new Element("konten");
			Document doc = new Document(root);
			Element e1=new Element(k.getName());
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(doc,new FileOutputStream(offlineMails));
		}catch(Exception e){
			System.out.println("Die Adressbuch-Datei konnte nicht initialisiert werden.");
		}

		for(int i=0;i<mails.size()&&i<25;i++) {
			Mail tmp = mails.get(i);
			System.out.println(i + "\t" + tmp.getAdresse() + "\t" + tmp.getBetreff() + "\t" + tmp.getEmpfangsdatum());
		}
		auswaehlen();
	}

	public static void auswaehlen() {
		System.out.println("Wählen Sie durch Eingabe der jeweiligen Zahl über die Tastatur den gewünschten Menüpunkt");
		Scanner sc=new Scanner(System.in);
		int eingabe=sc.nextInt();

		switch(eingabe){
			case 1: kommandos();
				break;
			case 2: ausloggen();
				break;
			case 3: naechste();
				break;
			case 4: vorherige();
				break;
			case 5: verfassen();
				break;
			case 6: loeschen();
				break;
			case 7: anzeigen();
				break;
			case 8: adressbuch();
				break;
			case 9: spamfilter();
				break;
			case 10: aktualisieren();
				break;
			case 11: speichern();
				break;
			case 12: aendern();
				break;
		}
	}
	public static ArrayList<Mail> getMails() {
		return mails;
	}

	public static void speichern(){
		System.out.println("Bitte geben Sie die Nummer der Mail an, die sie offline speichern möchten");
		Scanner sc=new Scanner(System.in);
		int nummer=sc.nextInt();
		Document doc = null;
		try {
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
		auswaehlen();
	}

	public static void anzeigen(){
		System.out.println("Bitte geben Sie die Nummer der Mail an, die Sie sehen möchten.");
		Scanner sc=new Scanner(System.in);
		int nummer=sc.nextInt();
		System.out.println(mails.get(nummer).getNachricht());
		auswaehlen();
	}
	
	public static void seite(){
		System.out.println("Bitte geben Sie die Nummer der Seite an, zu der Sie springen möchten");
		Scanner sc=new Scanner(System.in);
		int nummer=sc.nextInt();
		aktuelleSeite=nummer;
		for(int i=(nummer-1)*25;i<mails.size()&&i<i+25;i++){
			Mail tmp=mails.get(i);
			System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());
		}
		auswaehlen();
	}
	
	public static void naechste(){
		aktuelleSeite++;
		for(int i=(aktuelleSeite-1)*25;i<mails.size()&&i<i+25;i++){
			Mail tmp=mails.get(i);
			System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());
		}
		auswaehlen();
	}
	
	public static void vorherige(){
		aktuelleSeite--;
		for(int i=(aktuelleSeite-1)*25;i<mails.size()&&i<i+25;i++){
			Mail tmp=mails.get(i);
			System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());
		}
		auswaehlen();
	}

	public static void aktualisieren(){
		auswaehlen();
	}
	
	
	public static Session getSession(){ 
		Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", konto.getServer());
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
		
		Scanner sc=new Scanner(System.in);
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
		auswaehlen();
    }

	public static void ausloggen(){
		//leer
	}

	public static void spamfilter(){
		Spamfilter.init();
	}

	public static void adressbuch(){
		Adressbuch.init();
	}

	public static void kommandos() {
		System.out.println("Sie haben die M�glichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(kommandoliste.get(i) + ", ");
		}
		auswaehlen();
	}

	public static void loeschen(){		
		System.out.println("was wollen Sie loeschen?");
		Scanner sc = new Scanner(System.in);
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
		auswaehlen();
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
		auswaehlen();
	}

	public static void aendern(){
		System.out.println("welchen Eintag wollen Sie aendern?");
		Scanner sc = new Scanner(System.in);
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

            Konto neuesKonto = new Konto(Startfenster.konten.get(nr-1).getName(), Startfenster.konten.get(nr-1).getAdress(), Startfenster.konten.get(nr-1).getServer(), Startfenster.konten.get(nr-1).getSmtpServer(), Startfenster.konten.get(nr-1).getPort(), Startfenster.konten.get(nr-1).getProtocol(), Startfenster.konten.get(nr-1).getRefRate());
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
		auswaehlen();
	}


}
