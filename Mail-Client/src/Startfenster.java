import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter; 

public class Startfenster extends Fenster{

	private ArrayList<Konto> konten;
	private File inFile;
	private File inXML;

	public Startfenster(){
		konten=new ArrayList<Konto>();
		inFile=new File("Konten.txt");
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
	
	
	
	private void makeList(){
		try{
			Scanner sc = new Scanner(inFile);
			while( sc.hasNextLine()){
				String properties [] = sc.nextLine().split(";");
				Konto neuesKonto = new Konto(properties[0], properties[1], properties[2], properties[3], properties[4], Double.parseDouble(properties[5]));
				konten.add(neuesKonto);
				SAXBuilder builder = new SAXBuilder(); 
			}
		}
		catch(Exception e){
			System.out.println("Fehler beim Einlesen der Kontoliste");
		}
	}
	
	private void neuesKonto(){
		//hole daten f�r das zu speicherne Konto
		Scanner sc = new Scanner(System.in);
		System.out.println("Bitte geben Sie Ihren Namen ein:");
		String name = sc.next();
		System.out.println("Bitte geben Sie Ihre Mail-Addresse ein:");
		String adresse = sc.next();
		System.out.println("Bitte geben Sie Ihr Passwort ein:");
		String passwort;
		if ( System.console() != null ){
			passwort = new String( System.console().readPassword() );
		}
		else{
			System.out.println("Fehler bei Passworteingabe");
			System.exit(1);
		}
		System.out.println("Bitte geben Sie die gewuenschte Aktualisierungsrate ein(in sek)");
		double refRate = sc.nextDouble();
						
		Konto neuesKonto = new Konto(name, adresse, passwort refRate);
		
		
		
		speichereKonto(neuesKonto);	
//		while(true){
//			System.out.println("(1)imap");
//			System.out.println("(2)pop3");
//			int typ = sc.nextInt();
//			if(!(typ == 1 || typ == 2)){
//				System.out.println("ungueltige Eingabe, bitt erneut eingeben:");
//			}
//			else{
//				if(typ == 1){
//					protocol = "imap";
//				}
//				else{
//					protocol = "pop3";
//				}
//				break;
//			}
//			
//		}	
		//connection war ok, wird gespeichert
	}
	
	//Speichere Konto in "inFile"
	public void speichereKonto(Konto k){
		FileWriter fw;
//		String smtp = getsmpt(adresse);
//		String typ = getTyp(adresse);
//		String server = getServer(adresse); 
		try{
			fw=new FileWriter(inFile);
//			fw.append(adresse + ";" +  + ";" + protocol);
			fw.close();
		}catch(Exception e){
			System.out.println("Der Spamfilter konnte nicht ge�ffnet werden.");
		}
	}
	
	
	public void aendern(){
		
	}
	
	private void holeKonten(){
		Document doc = null;

        try {
            // Das Dokument erstellen
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(inXML);
            XMLOutputter fmt = new XMLOutputter();

            // Wurzelelement wird auf root gesetzt
            Element root = doc.getRootElement();
             
            //Liste aller vorhandenen Mailkonten als Elemente
            List alleKonten = root.getChildren();
            
            for(int i = 0; i < alleKonten.size(); i++){
            	String name = ((Element) alleKonten.get(0)).getChild("name").getValue();
            	String adresse = ((Element) alleKonten.get(0)).getChild("adresse").getValue();
            	String server = ((Element) alleKonten.get(0)).getChild("server").getValue();
            	String smtpServer = ((Element) alleKonten.get(0)).getChild("smtpServer").getValue();    
            	int port = Integer.parseInt(((Element) alleKonten.get(0)).getChild("port").getValue());
            	String protocol = ((Element) alleKonten.get(0)).getChild("protocol").getValue();
            	double refRate = Double.parseDouble(((Element) alleKonten.get(0)).getChild("refRate").getValue());
            	Konto k = new Konto(name, adresse, server, smtpServer,port, protocol, refRate);
            	konten.add(k);
            }
        }
        catch(Exception e){
        	System.out.println("Datei Fehlerhaft oder nicht gefunden");
        }
	}

	public void waehlen(){

	}

	public void loeschen(){
		
	}

	public void beenden(){
		System.exit(1);	
	}
}
