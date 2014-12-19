import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Spamfilter {
	
	private static ArrayList<String> adressen;
	private static File output;
	private static ArrayList<String> kommandoliste;
	
	public static void init(){
		kommandoliste=new ArrayList<String>();
		adressen=new ArrayList<String>();
		output=new File("Spamfilter.txt");
		
		// Initalisierung der kommandoliste
		kommandoliste.add("hinzufuegen");
		kommandoliste.add("loeschen");
		kommandoliste.add("kommandos");
		kommandoliste.add("zurueck");
		auswaehlen();
	}

	public static void auswaehlen() {
		System.out.println("Wählen Sie durch Eingabe der jeweiligen Zahl über die Tastatur den gewünschten Menüpunkt");
		Scanner sc=new Scanner(System.in);
		int eingabe=sc.nextInt();

		switch(eingabe){
			case 1: hinzufuegen(); auswaehlen();
				break;
			case 2:loeschen(); auswaehlen();
				break;
			case 3: kommandos(); auswaehlen();
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
	}
	
	public static void loeschen(){
		Scanner sc=new Scanner(System.in);
		System.out.println("Bitte geben Sie die Nummer der Mail ein, die Sie loeschen wollen.");
		int nummer=sc.nextInt(); //Bedingungen für die Nummer ergaenzen
		adressen.remove(nummer);
		try{
			File kopie = new File("kopie.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(output)));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(kopie)));
			int counter = 0;
			String line;
			while((line = br.readLine()) != null){
				if(counter != nummer){
					bw.write(line);
					bw.newLine();
				}
				counter++;
			}
			bw.close();
			br.close();
		 	output=kopie; //geht das oder geht das nicht weil das iwie mit referenz oder sowat nicht funkt
		}catch(Exception e){
			System.out.println("Der Spamfilter konnte nicht ge�ffnet werden.");
		}
		sc.close();
	}
	
	public ArrayList<String> getAdressen() {
		return adressen;
	}

	public  void setAdressen(ArrayList<String> adressen) {
		this.adressen = adressen;
	}

	public static void kommandos() {
		System.out.println("Sie haben die Moeglichkeit folgende Kommandos einzugeben: ");
		for (int i = 1; i < kommandoliste.size(); i++) {
			System.out.print(i+": "+kommandoliste.get(i-1)+"\n");
		}
	}

	public static void zurueck(){
		//absichtlich leer
	}
}
