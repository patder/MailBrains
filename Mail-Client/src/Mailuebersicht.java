import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import org.jdom.*;
import org.jdom.output.*;


public class Mailuebersicht extends Fenster {
	private ArrayList<Mail> mails;
	private File offlineMails;
	private int aktuelleSeite;
	
	public Mailuebersicht() {
		mails= new ArrayList<Mail>(); //Mails m�ssen vom Server geholt werden
		
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
		
		for(int i=0;i<mails.size()&&i<25;i++){
			Mail tmp=mails.get(i);
			System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());
		}
		
	}
	
	public ArrayList<Mail> getMails() {
		return mails;
	}

	public void speichern(int nummer){
		FileWriter fw;
		try{
			Mail tmp=mails.get(nummer);
			Element root = new Element("konten");
			Document doc = new Document(root);
			root.addContent(new Element("person")
					.addContent(new Element("adresse").addContent(tmp.getAdresse()))
					.addContent(new Element("betreff").addContent(tmp.getBetreff()))
					.addContent(new Element("nachricht").addContent(tmp.getNachricht()))
					.addContent(new Element("empfangsdatum").addContent(tmp.getEmpfangsdatum()));
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(doc, new FileOutputStream("konten.xml"));
			fw.close();			
		}catch(Exception e){
			System.out.println("Der Spamfilter konnte nicht ge�ffnet werden.");
		}
	}
	///
	public void anzeigen(int nummer){
		System.out.println(mails.get(nummer).getNachricht());
	}
	
	public void seite(int nummer){
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

	public void ausloggen(){

	}

	public void seite(){

	}

	public void speichern(){

	}

}
