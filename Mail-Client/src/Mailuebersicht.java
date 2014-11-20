import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class Mailuebersicht extends Fenster {
	private ArrayList<Mail> mails;
	private File offlineMails;
	private int aktuelleSeite;
	
	public Mailuebersicht() {
		mails= new ArrayList<Mail>(); //Mails müssen vom Server geholt werden
		
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
			fw=new FileWriter(offlineMails);
			Mail tmp=mails.get(nummer);
			fw.append("<mail nummer="+nummer+" >\n"
					+ "\t<adresse> "+tmp.getAdresse()+" <\\adresse>\n"
					+ "\t<betreff> "+tmp.getBetreff()+" <\\betreff>\n"
					+ "\t<nachricht>"+tmp.getNachricht()+" <\\nachricht>\n"
					+ "<\\mail>\n\n");
			fw.close();			
		}catch(Exception e){
			System.out.println("Der Spamfilter konnte nicht geöffnet werden.");
		}		
	}
	
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
	

}
