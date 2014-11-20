import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Startfenster extends Fenster{
	private ArrayList<Konto> konten;
	private File inFile;
	public Startfenster(){
		konten=new ArrayList<Konto>();
		inFile=new File("Konten.txt");
		makeList();
		
		// Initalisierung der kommandoliste
		kommandoliste.add("waehle");
		kommandoliste.add("loeschen");
		kommandoliste.add("kommandos");
		kommandoliste.add("aendern");
		
		
		
	}
	
	
	
	private void makeList(){
		try{
			Scanner sc = new Scanner(inFile);
			while( sc.hasNextLine()){
				String properties [] = sc.nextLine().split(";");
				Konto neuesKonto = new Konto(properties[0], properties[1], properties[2], properties[3]);
				konten.add(neuesKonto);
			}
		}
		catch(Exception e){
			System.out.println("Fehler beim Einlesen der Kontoliste");
		}
	}
	
	private void neuesKonto(){
		//hole daten für das zu speicherne Konto
		Scanner sc = new Scanner(System.in);
		System.out.println("Bitte geben Sie Ihre Mail-Addresse ein:");
		String adresse = sc.next();
		System.out.println("Bitte geben Sie Ihren Mail-Server ein:");
		String server = sc.next();
		System.out.println("Bitte geben Sie Ihren smtpServer ein:");
		String smtp = sc.next();
		System.out.println("Bitte waehlen ein Protocol: ");
		String protocol;
		while(true){
			System.out.println("(1)imap");
			System.out.println("(2)pop3");
			int typ = sc.nextInt();
			if(!(typ == 1 || typ == 2)){
				System.out.println("ungueltige Eingabe, bitt erneut eingeben:");
			}
			else{
				if(typ == 1){
					protocol = "imap";
				}
				else{
					protocol = "pop3";
				}
				break;
			}
			
		}	
		//connection war ok, wird gespeichert
		speichereKonto(adresse, server, protocol);		
	}
	
	//Speichere Konto in "inFile"
	private void speichereKonto(String adresse, String server, String protocol){
		FileWriter fw;
		try{
			fw=new FileWriter(inFile);
			fw.append(adresse + ";" + server + ";" + protocol);
			fw.close();
		}catch(Exception e){
			System.out.println("Der Spamfilter konnte nicht geöffnet werden.");
		}
	}
	
	
	public void aendern(){
		
	}
	

}
