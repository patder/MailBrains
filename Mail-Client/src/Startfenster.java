import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
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
	
	

	private void neuesKonto(){
		//hole daten f�r das zu speicherne Konto
		Scanner sc = new Scanner(System.in);
		System.out.println("Bitte geben Sie Ihren Namen ein:");
		String name = sc.next();
		System.out.println("Bitte geben Sie Ihre Mail-Addresse ein:");
		String adresse = sc.next();
		System.out.println("Bitte geben Sie Ihr Passwort ein:");
		String passwort="";
		if ( System.console() != null ){
			passwort = new String( System.console().readPassword() );
		}
		else{
			System.out.println("Fehler bei Passworteingabe");
			System.exit(1);
		}
		System.out.println("Bitte geben Sie die gewuenschte Aktualisierungsrate ein(in sek)");
		double refRate = sc.nextDouble();
						
		Konto neuesKonto = new Konto(name, adresse, passwort, refRate);
		
		
		
		try {
			speichereKonto(neuesKonto);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
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
	public void speichereKonto(Konto k) throws JDOMException, IOException{
		try{
			Document doc = null;
	        SAXBuilder builder = new SAXBuilder();
	        doc = builder.build(inXML);
	        
	        Element root = doc.getRootElement();
	        Element paddy = new Element(k.getAdress());
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
	        outp.output( doc, new FileOutputStream( "XMLModelKontenDatei"));
		}
		catch(Exception e){
			System.out.println(e);
			System.out.println("Fehler beim schreiben eines neuen Kontos");
		}
	}

	
	public void aendern(){
		
	}
	
	private void holeKonten(){
		Document doc = null;

        try {
            // Das Dokument erstellen
            SAXBuilder builder = new SAXBuilder();
            System.out.println("Check 1");
            
            doc = builder.build(inXML);
            System.out.println("Check 2");
            
            
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
            	konten.add(k1);
            }
        }
        catch(Exception e){
        	System.out.println(e);
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
