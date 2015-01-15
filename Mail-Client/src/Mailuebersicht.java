import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;

import java.net.ConnectException;
import java.util.Properties;


import java.util.Scanner;
import java.util.concurrent.Exchanger;
import java.util.concurrent.locks.ReentrantLock;

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
	private static int  messageCounter;
	private static int anzahlMails;

	private static ReentrantLock muhtex;
	private static ReentrantLock kuhtex;
	private static Thread t;


 	private static void holeMails(String met)throws AuthenticationFailedException{
		kuhtex.lock();
		//Die folgenden Zeilen vllt auch lieber in ein Methode (wie bei getSession nur halt für pop3)
		Properties props = System.getProperties();
		props.setProperty("mail.pop3.host", konto.getPop3Server());
		props.setProperty( "mail.pop3.port", "995" );
		props.setProperty("mail.pop3.auth", "true");
		props.put("mail.pop3.starttls.enable", "true");
		//Brauch man folgende props überhaupt??
		props.setProperty("mail.pop3.socketFactory.fallback", "false");
		props.setProperty("mail.pop3.socketFactory.class","javax.net.ssl.SSLSocketFactory");

		//Session erstellen
		Session session = Session.getInstance(props, konto.getPasswordAuthentication());
		try {
			Store store = session.getStore("pop3"); // -->no such provider exception
			store.connect(konto.getAdress(),konto.getPassword()); // --> authentication failed exception

			Folder inboxfolder=store.getDefaultFolder().getFolder("INBOX");
			


			inboxfolder.open(Folder.READ_ONLY);
			anzahlMails=inboxfolder.getMessageCount();
			
			/**
			//--->noch nicht getestet worden!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			if(messageCounter == inboxfolder.getMessageCount() && met.equals("aktualisieren")){
				inboxfolder.close(false);
				inboxfolder.getStore().close();
				return;//--> wenn sich die anzahl der mails nicht verändert hat werden keine neuen mails geholt
			}
			 **/
			messageCounter = inboxfolder.getMessageCount();

			
			
			Message[] msg=null;
			if(met.equals("naechste")) {
				int bis=inboxfolder.getMessageCount()-(aktuelleSeite-1)*25;
				int von=inboxfolder.getMessageCount()-(aktuelleSeite-1)*25-24;
				if(von!=inboxfolder.getMessageCount()&&bis<inboxfolder.getMessageCount()&&von>0){
					msg = inboxfolder.getMessages(von, bis);
				}else{
					msg = inboxfolder.getMessages(von, inboxfolder.getMessageCount());
				}
			}else if(met.equals("vorherige")){
				int von=inboxfolder.getMessageCount()-(aktuelleSeite-2)*25-24;
				int bis=inboxfolder.getMessageCount()-(aktuelleSeite-2)*25;
				if(von!=inboxfolder.getMessageCount()&&bis<inboxfolder.getMessageCount()&&von>0){
					msg = inboxfolder.getMessages(von, bis);
				}else{
					msg = inboxfolder.getMessages(von, inboxfolder.getMessageCount());
				}
			}else if(met.equals("aktualisieren")) {
				int bis=inboxfolder.getMessageCount()-(aktuelleSeite-1)*25;
				int von=inboxfolder.getMessageCount()-(aktuelleSeite-1)*25-24;
				if(bis>0&&von>0){
					msg = inboxfolder.getMessages(von, bis);
				}
			}else{
				System.out.println("Es werden bereits die aeltesten Mails angezeigt");
				kuhtex.unlock();
				return;
			}

			//Einlesen in die Arraylist mails (ist Attribut dieser Klasse)
			for(int i=msg.length-1;i>=0;i--){
				try {
					//Empfangsdatum von Typ Date zu String
					DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Date d=msg[i].getSentDate();
					String reportDate = df.format(d);

					//Wie mit Mulitpart Nachrichten umgehen?
					if ( msg[i].isMimeType( "multipart/*" ) ) {
						Multipart mp = (Multipart) msg[i].getContent();
						String inhalt="";
						Part part = mp.getBodyPart(0);
						String disp = part.getDisposition();
						MimeBodyPart mimePart = (MimeBodyPart) part;
						if (mimePart.isMimeType("text/xml")) {
							inhalt+=String.valueOf(msg[i].getContent());
						}
						else{
							inhalt+="Multipart Nachricht. Anzeigen des Inhalts nicht moeglich.";
						}
						Mail m = new Mail(msg[i].getFrom()[0].toString(), msg[i].getSubject(), inhalt, reportDate);
						mails.add(m);
					}
					else{
						Mail m = new Mail(msg[i].getFrom()[0].toString(), msg[i].getSubject(), String.valueOf(msg[i].getContent()), reportDate);
						mails.add(m);
					}
				}catch(Exception e){
					System.out.println("Der Content der Mail verursacht IO Probleme.");
				}
			}
			inboxfolder.close(false);
			inboxfolder.getStore().close();
		}catch (AuthenticationFailedException e) {
			throw new AuthenticationFailedException(e.getMessage());
		}catch(NoSuchProviderException e){
			System.out.println("Die Mails konnten nicht vom Server geholt werden, das der Server nicht existiert.");
		}catch(MessagingException e2){
			System.out.println("Weiß nicht was die Fehlermeldung im Kontext heißt "+e2.getMessage());
		}

		//Holen der offline-Mails
		ArrayList <Mail> offlineMails = new ArrayList<Mail>();
		Document doc=null;
		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build("offlineMails.xml");

			// Wurzelelement wird auf root gesetzt
			Element myRoot = doc.getRootElement().getChild(konto.getAdress().replace('@', 'p'));

			//Liste aller vorhandenen Mailkonten als Elemente
			List alleMails = myRoot.getChildren();

			for(int i = 0; i < alleMails.size(); i++){
				String adresse = ((Element) alleMails.get(i)).getChild("adresse").getValue();
				String betreff = ((Element) alleMails.get(i)).getChild("betreff").getValue();
				String nachricht = ((Element) alleMails.get(i)).getChild("nachricht").getValue();
				String empfangsdatum = ((Element) alleMails.get(i)).getChild("empfangsdatum").getValue();

				Mail mail = new Mail(adresse, betreff, nachricht, empfangsdatum);
				offlineMails.add(mail);
			}
		}
		catch(Exception e){
			System.out.println(e);
			System.out.println("Fehler beim Laden der Offlinemails, Datei fehlerhaft oder nicht gefunden.");
		}

		//Vergleichen der vom Server geholten Mails mit den offline gespeicherten Mails
		for(int i=0;i<mails.size();i++){
			for(int j=0;j<offlineMails.size();j++){
				if(mails.get(i).getAdresse().equals(offlineMails.get(j).getAdresse())&&
				   mails.get(i).getEmpfangsdatum().equals(offlineMails.get(j).getEmpfangsdatum())){
					mails.get(i).setOffline(true);
					break;
				}
			}
		}
		kuhtex.unlock();
	}
	public static void init(Konto k){
		//Initialisierung der Attribute

		muhtex= new ReentrantLock();
		kuhtex = new ReentrantLock();
	 	messageCounter = 0;

		sc=Startfenster.sc;
		kommandoliste=new ArrayList<String>();
		konto=k;
		mails= new ArrayList<Mail>();
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

		//Mails vom Server holen
		try {
			holeMails("aktualisieren");
			alle();
			auswaehlen();
		}catch(AuthenticationFailedException e){
			System.out.println("Das eingebene Passwort ist falsch!");

		}
	}

	public static void alle(){
		try{
			Spamfilter.output = new File("Spamfilter.txt");
			Spamfilter.adressen = new ArrayList<String>();
			Spamfilter.holeSpam();
			System.out.println(Spamfilter.adressen.get(Spamfilter.adressen.size()-1));

			for(int i=0;i<mails.size();i++) {
				if(Spamfilter.adressen.contains(mails.get(i).getAdresse())){
					System.out.println(i + 1 + "\tX\t<----Spam----->"  + "\t\t\t" + "\t" + mails.get(i).getEmpfangsdatum());
				}
				else{
					Mail tmp = mails.get(i);
					if(tmp.getOffline()==true) {
						System.out.println(i + 1 + "\tX\t" + tmp.getAdresse() + "\t" + tmp.getBetreff() + "\t" + tmp.getEmpfangsdatum());
					}else{
						System.out.println(i + 1 + "\t" + tmp.getAdresse() + "\t" + tmp.getBetreff() + "\t" + tmp.getEmpfangsdatum());
					}
				}

			}
			System.out.println("");
			System.out.println("Mailanzahl: "+anzahlMails+" Seitenanzahl: "+new Double(Math.ceil(anzahlMails/25)).intValue()+" aktuelle Seite: "+aktuelleSeite);

		}
		catch(Exception e){
			System.out.println("Was ist denn los mit dir(Spamfilter filtern...)");
		}


	}
	private static void auswaehlen() {
		t = new Thread(new loopThread());
		t.start();
		while(true){
			kommandos();
			int eingabe=-1;
			try {
				eingabe = Integer.parseInt(sc.nextLine());
			}catch(NumberFormatException e){
				System.out.println("Ungueltige Eingabe.");
			}
			muhtex.lock();
			switch(eingabe){
				case 1: kommandos();
					break;
				case 2: t.interrupt(); return;

				case 3: naechste();
					break;
				case 4: vorherige();
					break;
				case 5: try {
					verfassen();
				} catch (Exception e) {
					e.printStackTrace();
				}
					break;
				case 6: loeschen(true); return;

				case 7: anzeigen();
					break;
				case 8: seite();
					break;
				case 9: adressbuch();
					break;
				case 10: spamfilter();
					break;
				case 11: aktualisieren();
					break;
				case 12: speichern();
					break;
				case 13: aendern();
					break;
			}
			muhtex.unlock();
		}
	}
	public static ArrayList<Mail> getMails() {
		return mails;
	}

	private static void speichern(){
		System.out.println("Bitte geben Sie die Nummer der Mail an, die sie offline speichern möchten");
		alle();
		int nummer=Integer.parseInt(sc.nextLine());
		Document doc = null;
		if(mails.get(nummer-1).getOffline()==false) {
			try {
				// Das Dokument erstellen
				SAXBuilder builder = new SAXBuilder();
				doc = builder.build(offlineMails);
				XMLOutputter fmt = new XMLOutputter();
				// Wurzelelement wird auf root gesetzt
				Element root = doc.getRootElement();

				//Die mail an das aktuelle konto anhengen
				Mail tmp = mails.get(nummer - 1);
				Element akt = root.getChild((konto.getAdress()).replace('@', 'p'));
				if (akt != null) {
					String s = "";
					if (tmp.getAdresse().contains("<") && tmp.getAdresse().contains(">")) {
						s = tmp.getAdresse().substring(tmp.getAdresse().indexOf("<") + 1, tmp.getAdresse().indexOf(">"));
					} else {
						s = tmp.getAdresse();
					}
					System.out.println(s.replace('@', 'p'));
					Element neu = new Element(s.replace('@', 'p'));
					neu.addContent(new Element("adresse").addContent(tmp.getAdresse()));
					neu.addContent(new Element("betreff").addContent(tmp.getBetreff()));
					neu.addContent(new Element("nachricht").addContent(tmp.getNachricht()));
					neu.addContent(new Element("empfangsdatum").addContent(tmp.getEmpfangsdatum()));
					akt.addContent(neu);
					XMLOutputter outp = new XMLOutputter();
					outp.setFormat(Format.getPrettyFormat());
					outp.output(doc, new FileOutputStream(offlineMails));
				} else {
					System.out.println("Fehler: Warum gibt es das aktuelle Konto nicht als Kind vom root?");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage() + "Die Mail konnte nicht offline gespeichert werden.");
			}
			mails.get(nummer - 1).setOffline(true);
		}else{
			System.out.println("Diese Mail wurde bereits offline gespeichert.");
		}
	}

	private static void anzeigen(){
		if(mails.size()<1){
			System.out.println("Es sind keine Mails vorhanden.");
		}else {
			System.out.println("Bitte geben Sie die Nummer der Mail an, die Sie sehen möchten.");
			alle();
			int nummer = Integer.parseInt(sc.nextLine());
			System.out.println(mails.get(nummer - 1).getNachricht()+"\n");
		}
	}

	private static void seite(){
		System.out.println("Bitte geben Sie die Nummer der Seite an, zu der Sie springen möchten");
		int nummer=Integer.parseInt(sc.nextLine());
		aktuelleSeite=nummer;
		alle();
	}

	private static void naechste(){
		aktuelleSeite++;
		try {
			mails.clear();
			holeMails("naechste");
			alle();
			System.out.println("");
		}catch(Exception e){
			System.out.println("Die Mails konnten nicht aktualisiert werden");
			aktuelleSeite--;
		}
	}

	private static void vorherige(){
		if(aktuelleSeite!=1) {
			try {
				mails.clear();
				holeMails("vorherige");
				aktuelleSeite--;
				alle();
			} catch (Exception e) {
				System.out.println("Die Mails konnten nicht aktualisiert werden");
			}
		}else{
			System.out.println("Ihre Mails sind bereits die Aktuellsten");
		}
	}

	private static void aktualisieren(){
		try {
			mails.clear();
			holeMails("aktualisieren");
			alle();
		}catch(Exception e){
			System.out.println("Die Mails konnten nicht aktualisiert werden");
		}
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

	private static void verfassen() throws AddressException, MessagingException
	{
		Session session=getSession();
		// nachricht erzeugen
		System.out.println("Geben Sie den gewuenschten Empfaenger an: ");
		try {
			Adressbuch.konto=konto;
			Adressbuch.setAdressen(new ArrayList<String>());
			Adressbuch.adressDat=new File("adressbuch.xml");
			Adressbuch.holeAdressen();
			System.out.println("Sie koennen folgende Adresse aus ihrem Adressbuch auswaehlen: \n0: nicht gespeicherte Adresse eingeben");
			for (int i = 0; i < Adressbuch.getAdressen().size(); i++) {
				System.out.println(i + 1 + ": " + Adressbuch.getAdressen().get(i));
			}
			System.out.println("");
		}catch(Exception e){
			System.out.println("\nSie haben noch keine Eintraege im Adressbuch.\n0: nicht gespeicherte Adresse eingeben");
		}
		System.out.println("Empfaenger: ");
		String empfaenger="";
		int eingabe=-1;
		try {
			eingabe = Integer.parseInt(sc.nextLine());
		}catch(NumberFormatException e){
			System.out.println("Keine gueltige Eingabe");
			return;
		}
		if(eingabe==0) {
			empfaenger = sc.nextLine();
		}else {
			empfaenger = Adressbuch.getAdressen().get(eingabe - 1);
		}

		System.out.println("Betreff:");
		String betreff=sc.nextLine();
		System.out.println("Nachricht:");
		String text=sc.nextLine(); // wenn man hier nur next macht kommt ne fehlermeldung beim int parsen in der methode auswaehlen, wie können wir das hier aber trz aendern
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(konto.getAdress()));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(empfaenger, false));

			// Betreff
			msg.setSubject(betreff);

			// Nachricht
			msg.setText(text);

			// E-Mail versenden
			Transport.send(msg);
		}catch(Exception e){
			System.out.println("Die Mail konnte aus uns unerklärlichen Gründen nicht gesendet werden. \n Bitte versuchen Sie es erneut.");
		}
	}

	private static void spamfilter(){
		Spamfilter.init();
	}

	private static void adressbuch(){
		Adressbuch.init(konto);
	}

	private static void kommandos() {
		System.out.println("Sie haben die Moeglichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(i+1+": "+kommandoliste.get(i)+"\n");
		}
	}

	private static void loeschen(boolean boohoo){
		int i = -1;
		while(true){
			if(boohoo){
				System.out.println("Sind Sie sicher das sie dieses Konto loeschen wollen? (0)ja, (1)abbrechen");
				try{
					i = sc.nextInt();
					if(!(i == 1 || i == 0)){
						throw new Exception();
					}
				}
				catch(Exception e){
					System.out.println("Ungueltige Eingabe bem loeschen");
				}
			}

			if(i == 1){
				return;
			}
			if(i == -1 || i == 0){
				if(i == 0){
					t.interrupt();
				}
				break;
			}
		}


		Document doc = null;
		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(Startfenster.inXML);

			// Wurzelelement wird auf root gesetzt
			Element root = doc.getRootElement();
			List children = root.getChildren();
			root.removeContent(root.getChild(konto.getAdress().replace("@", "p")));
			XMLOutputter outp = new XMLOutputter();
			outp.setFormat( Format.getPrettyFormat() );
			outp.output( doc, new FileOutputStream(Startfenster.datName));
		}
		catch(Exception e){
			System.out.println("Fehler beim Loeschen von Konto");
		}
	}
	private static void loeschen(int i){
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

	private static void aendern(){
		System.out.println("Welchen Eintrag wollen Sie aendern?");
		int i = -2;
		int port = 25;
		double ref = 10.0;
		System.out.println("1) Kontoname: " + konto.getName());
		System.out.println("2) Adresse: " + konto.getAdress());
		System.out.println("3) Ausgangsserver: " + konto.getServer());
		System.out.println("4) SMTP-Server: " + konto.getSmtpServer());
		System.out.println("5) Protokol: " + konto.getProtocol());
		System.out.println("6) Port: " + konto.getPort());
		System.out.println("7) Aktualisierungsrate: " + konto.getRefRate());

		while(true){
			System.out.println("Welchen Eintrag wollen Sie aendern?");
			try{
				i =Integer.parseInt(sc.nextLine());
				if(i < 1 || i > 7){
					throw new Exception();
				}else{
					break;
				}
			}
			catch(Exception e){
				System.out.println("Ungueltige Eingabe beum waehlen des Eintrags");
			}
		}

		System.out.println("Geben Sie den neuen Wert ein: ");
		String neu = "";
		try{
			if(i == 6){
				neu = sc.nextLine();
				port = Integer.parseInt(neu);
			}
			else{
				if(i == 7){
					neu = sc.nextLine();
					ref = Double.parseDouble(neu);
				}
				else{
					neu = sc.nextLine();
				}

			}
		}
		catch(Exception e){
			System.out.println("Fehler beim Aendern eines Eintrags, ungueltige Eingabe");
			return;
		}
		Document doc = null;
		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();

			doc = builder.build(Startfenster.inXML);


			// Wurzelelement wird auf root gesetzt
			Element root = doc.getRootElement();

			//Liste aller vorhandenen Mailkonten als Elemente
			String st = konto.getAdress().replace('@', 'p');

			Konto neuesKonto = new Konto(konto.getName(), konto.getAdress(), konto.getServer(),konto.getSmtpServer(), konto.getPop3Server(),  konto.getPort(), konto.getProtocol(), konto.getRefRate());
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
			loeschen(false);
			Startfenster.speichereKonto(neuesKonto);

		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Fehler beim Aendern des Attributs");
		}
	}

	private static class loopThread implements Runnable{
		public void run(){
			while(true){
				try {
					Thread.sleep((long)konto.getRefRate()*1000);
					if(kuhtex.isLocked()){
						continue;
					}
					if(muhtex.isLocked()){
						holeMails("aktualisieren");
					}
					else {
						aktualisieren();
						kommandos();
					}

				} catch (Exception e) {
					System.out.println("Thread Probleme...");
				}
			}
		}
	}
}
