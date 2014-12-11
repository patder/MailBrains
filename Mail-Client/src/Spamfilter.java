import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class Spamfilter extends Fenster {
	
	private static ArrayList<String> adressen;
	private static File output;
	private static ArrayList<String> kommandoliste;
	
	public Spamfilter(){
		kommandoliste=new ArrayList<String>();
		adressen=new ArrayList<String>();
		output=new File("Spamfilter.txt");
		
		// Initalisierung der kommandoliste
		kommandoliste.add("hinzufuegen");
		kommandoliste.add("loeschen");
		kommandoliste.add("kommandos");
		kommandoliste.add("zurueck");
		
	}

	public static void auswaehlen() {
		System.out.println("Wählen Sie durch Eingabe der jeweiligen Zahl über die Tastatur den gewünschten Menüpunkt");
		Scanner sc=new Scanner(System.in);
		int eingabe=sc.nextInt();

		switch(eingabe){
			case 1: hinzufuegen();
				break;
			case 2:loeschen();
				break;
			case 3: kommandos();
				break;
			case 4: zurueck();
		}
	}
	
	public static void hinzufuegen(){
		Scanner sc=new Scanner(System.in);
		System.out.println("Bitte geben Sie die Adresse ein, die Sie hinzufuegen wollen.");
		String adresse=sc.next(); //Bedingungen für die Adresse ergaenzen
		adressen.add(adresse);
		FileWriter fw;
		try{
			fw=new FileWriter(output);
			fw.append(adresse);
			fw.close();
		}catch(Exception e){
			System.out.println("Der Spamfilter konnte nicht ge�ffnet werden.");
		}
		sc.close();
		auswaehlen();
	}
	
	public static void loeschen(){
		Scanner sc=new Scanner(System.in);
		System.out.println("Bitte geben Sie die Nummer der Mail ein, die Sie loeschen wollen.");
		int nummer=sc.nextInt(); //Bedingungen für die Nummer ergaenzen
		adressen.remove(nummer);
		FileWriter fw;
		try{
			sc=new Scanner(output);
			//in der Datei die richtige zeile loeschen
		}catch(Exception e){
			System.out.println("Der Spamfilter konnte nicht ge�ffnet werden.");
		}
		sc.close();
		auswaehlen();
	}
	
	public static ArrayList<String> getAdressen() {
		return adressen;
	}

	public  void setAdressen(ArrayList<String> adressen) {
		this.adressen = adressen;
	}

	public static void kommandos() {
		System.out.println("Sie haben die M�glichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(kommandoliste.get(i) + ", ");
		}
		auswaehlen();
	}

	public static void zurueck(){
		auswaehlen();
	}
}
