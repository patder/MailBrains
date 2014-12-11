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


public class Mailuebersicht extends Fenster {
	private ArrayList<Mail> mails;
	private File offlineMails;
	private int aktuelleSeite;
	public Konto konto;
	private ArrayList<String> kommandoliste;
	
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
	
	public ArrayList<Mail> getMails() {
		return mails;
	}

	public void speichern(){
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
	}

	public void anzeigen(){
		System.out.println("Bitte geben Sie die Nummer der Mail an, die Sie sehen möchten.");
		Scanner sc=new Scanner(System.in);
		int nummer=sc.nextInt();
		System.out.println(mails.get(nummer).getNachricht());
	}
	
	public void seite(){
		System.out.println("Bitte geben Sie die Nummer der Seite an, zu der Sie springen möchten");
		Scanner sc=new Scanner(System.in);
		int nummer=sc.nextInt();
		aktuelleSeite=nummer;
		for(int i=(nummer-1)*25;i<mails.size()&&i<i+25;i++){
			Mail tmp=mails.get(i);
			System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());
		}
	}
	
	public void naechste(){
		aktuelleSeite++;
		for(int i=(aktuelleSeite-1)*25;i<mails.size()&&i<i+25;i++){
			Mail tmp=mails.get(i);
			System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());
		}
	}
	
	public void vorherige(){
		aktuelleSeite--;
		for(int i=(aktuelleSeite-1)*25;i<mails.size()&&i<i+25;i++){
			Mail tmp=mails.get(i);
			System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());

		}
	}

	public void aktualisieren(){

	}

	public void loeschen(){

	}

	public void verfassen(){

	}
	
	
	public static void send(Konto acc, String empfaenger, String betreff,
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
    }

	public void ausloggen(){

	}

	public void spamfilter(){

	}

	public void adressbuch(){

	}

	public void kommandos() {
		System.out.println("Sie haben die M�glichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(kommandoliste.get(i) + ", ");
		}
		auswaehlen();
	}


}
