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

	private static void holeMails()throws AuthenticationFailedException{
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
			Message [] msg=inboxfolder.getMessages();

			//Einlesen in die Arraylist mails (ist Attribut dieser Klasse)
			for(int i=0;i<msg.length;i++){
				try {
					//Empfangsdatum von Typ Date zu String
					DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Date d=msg[i].getSentDate();
					String reportDate = df.format(d);

					//Wie mit Mulitpart Nachrichten umgehen?
						if ( msg[i].isMimeType( "multipart/*" ) ) {
							Multipart mp = (Multipart) msg[i].getContent();
							if (mp.getCount() > 1) {
								Part part = mp.getBodyPart(0);
							}

							// Laufe über alle Teile (Anhänge)
							String inhalt="";
							Part part = mp.getBodyPart(0);
							String disp = part.getDisposition();
							if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
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

	}
	public static void init(Konto k){
		//Initialisierung der Attribute
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
			holeMails();
			for(int i=0;i<mails.size()&&i<25;i++) {
				Mail tmp = mails.get(i);
				System.out.println(i+1 + "\t" + tmp.getAdresse() + "\t" + tmp.getBetreff() + "\t" + tmp.getEmpfangsdatum());
			}
			System.out.println("");
			auswaehlen();
		}catch(AuthenticationFailedException e){
			System.out.println("Das eingebene Passwort ist falsch!");

		}

	}

	public static void auswaehlen() {
		System.out.println("Wählen Sie durch Eingabe der jeweiligen Zahl über die Tastatur den gewünschten Menüpunkt");
		kommandos();
		int eingabe=-1;
		try {
			eingabe = Integer.parseInt(sc.nextLine());
		}catch(NumberFormatException e){
			System.out.println("Bitte geben Sie Ihre Wahl erneut an.");
			auswaehlen();
		}

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
			}
				auswaehlen();
				break;
			case 6: loeschen(); auswaehlen();
				break;
			case 7: anzeigen(); auswaehlen();
				break;
			case 8: seite(); auswaehlen();
				break;
			case 9: adressbuch(); auswaehlen();
				break;
			case 10: spamfilter(); auswaehlen();
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
		for(int i=0;i<mails.size()&&i<25;i++) {
			Mail tmp = mails.get(i);
			System.out.println(i+1 + "\t" + tmp.getAdresse() + "\t" + tmp.getBetreff() + "\t" + tmp.getEmpfangsdatum());
		}
		int nummer=Integer.parseInt(sc.nextLine());
		Document doc = null;
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
				Element neu = new Element(tmp.getAdresse().replace('@', 'p'));
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
		}catch(Exception e){
			System.out.println(e.getMessage()+"Die Mail konnte nicht offline gespeichert werden.");
		}
	}

	public static void anzeigen(){
		if(mails.size()<1){
			System.out.println("Es sind keine Mails vorhanden.");
		}else {
			System.out.println("Bitte geben Sie die Nummer der Mail an, die Sie sehen möchten.");
			for (int i = 0; i < mails.size() && i < 25; i++) {
				Mail tmp = mails.get(i);
				System.out.println(i + 1 + "\t" + tmp.getAdresse() + "\t" + tmp.getBetreff() + "\t" + tmp.getEmpfangsdatum());
			}
			int nummer = Integer.parseInt(sc.nextLine());
			System.out.println(mails.get(nummer - 1).getNachricht()+"\n");
		}
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
		if(aktuelleSeite!=0&&aktuelleSeite>=mails.size()) {
			for (int i = (aktuelleSeite - 1) * 25; i < mails.size() && i < i + 25; i++) {
				Mail tmp = mails.get(i);
				System.out.println(i + "\t" + tmp.getAdresse() + "\t" + tmp.getBetreff() + "\t" + tmp.getEmpfangsdatum());
			}
			aktuelleSeite++;
		}else{
			System.out.println("Die angezeigten Mails sind bereits die aeltesten.\n");
		}
	}

	public static void vorherige(){
		if(aktuelleSeite!=0&&aktuelleSeite>=mails.size()) {
			for (int i = aktuelleSeite * 25; i < mails.size() && i < i + 25; i++) {
				Mail tmp = mails.get(i);
				System.out.println(i + "\t" + tmp.getAdresse() + "\t" + tmp.getBetreff() + "\t" + tmp.getEmpfangsdatum());
			}
			aktuelleSeite--;
		}else{
			System.out.println("Die angezeigten Mails sind bereits die aktuellsten.\n");
		}
	}

	public static void aktualisieren(){
		try {
			mails.clear();
			holeMails();
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

	public static void verfassen() throws AddressException, MessagingException
    {
		Session session=getSession();
        // nachricht erzeugen

		System.out.println("Empfaenger:");
		String empfaenger=sc.nextLine();
		System.out.println("Betreff:");
		String betreff=sc.nextLine();
		System.out.println("Nachricht:");
		String text=sc.nextLine(); // wenn man hier nur next macht kommt ne fehlermeldung beim int parsen in der methode auswaehlen, wie können wir das hier aber trz aendern

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
