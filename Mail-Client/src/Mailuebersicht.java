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


public class Mailuebersicht {
	private static ArrayList<Mail> mails;
	private static File offlineMails;
	private static int aktuelleSeite;
	public static Konto konto;
	private static ArrayList<String> kommandoliste;
	
	public Mailuebersicht(Konto k) {

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

		for(int i=0;i<mails.size()&&i<25;i++){
			Mail tmp=mails.get(i);
			System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());
		}

		
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

	public static void loeschen(){
		auswaehlen();
	}

	
	
	public static void verfassen(Konto acc, String empfaenger, String betreff,
            String text) throws AddressException, MessagingException
    {
        // Properties �ber die Systemeigenschaften anlegen
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", acc.getSmtpHost());
        properties.setProperty("mail.smtp.port", String.valueOf(acc.getPort()));
        properties.setProperty("mail.smtp.auth", "true");
         
        // properties.put("mail.smtp.starttls.enable", "true");
        
        // session erstellen
        Session session = Session.getDefaultInstance(properties, acc.getPasswordAuthentication());

        // nachricht erzeugen
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(acc.getMailAdresse()));
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
		Spamfilter sp=new Spamfilter();
		sp.auswaehlen();
	}

	public static void adressbuch(){
		Adressbuch ad=new Adressbuch();
		ad.auswaehlen();
	}

	public static void kommandos() {
		System.out.println("Sie haben die M�glichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(kommandoliste.get(i) + ", ");
		}
		auswaehlen();
	}


}
