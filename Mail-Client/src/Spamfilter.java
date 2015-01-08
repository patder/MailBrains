import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Spamfilter {
	////
	private static ArrayList<String> adressen;
	private static File output;
	private static ArrayList<String> kommandoliste;
	private static Scanner sc;
	
	public static void init(){
		kommandoliste=new ArrayList<String>();
		adressen=new ArrayList<String>();
		output=new File("Spamfilter.txt");
		sc=Startfenster.sc;

		// Initalisierung der kommandoliste
		kommandoliste.add("hinzufuegen");
		kommandoliste.add("loeschen");
		kommandoliste.add("kommandos");
		kommandoliste.add("zurueck");
		kommandoliste.add("anzeigen");

		//
		holeSpam();
		auswaehlen();
	}

	private static void holeSpam(){
		try {
			Scanner tmp = new Scanner(output);
			while(tmp.hasNextLine()){
				adressen.add(tmp.nextLine());
			}
			tmp.close();
		}catch(FileNotFoundException e){
			//ist normal wenn noch keine Mails als Spam gespeichert werden
		}
	}

	public static void auswaehlen() {
		System.out.println("Wählen Sie durch Eingabe der jeweiligen Zahl über die Tastatur den gewünschten Menüpunkt");
		kommandos();
		int eingabe=Integer.parseInt(sc.nextLine());
		switch(eingabe){
			case 1: hinzufuegen(); auswaehlen();
				break;
			case 2:loeschen(); auswaehlen();
				break;
			case 3: kommandos(); auswaehlen();
				break;
			case 4: zurueck();
				break;
			case 5: anzeigen();auswaehlen();
				break;
			default: System.out.println("Keine gueltige Eingabe, wählen Sie erneut:"); auswaehlen();
		}
	}

	private static void anzeigen(){
		System.out.println("");
		if(0<adressen.size()) {
			System.out.println("Folgende Mails haben Sie als Spam markiert.");
			for (int i = 0; i < adressen.size(); i++) {
				System.out.println(i + 1 + ": " + adressen.get(i));
			}
		}else{
			System.out.println("Sie haben noch keine Mails als Spam markiert.");
		}
		System.out.println("");
	}

	public static void hinzufuegen(){
		System.out.println("Bitte geben Sie die Adresse ein, die Sie hinzufuegen wollen.");
		String adresse=sc.nextLine();
		adressen.add(adresse);
		BufferedWriter fw;
		try{
			fw=new BufferedWriter(new FileWriter(output,true));
			fw.append(adresse);
			fw.newLine();
			fw.close();
		}catch(Exception e){
			System.out.println("Der Spamfilter konnte nicht geoeffnet werden.");
		}
	}
	
	public static void loeschen(){
		int nummer;
		while(true) {
			System.out.println("Bitte geben Sie die Nummer der Adresse ein, die Sie loeschen wollen.");
			anzeigen();
			nummer = Integer.parseInt(sc.nextLine()); //Bedingungen für die Nummer ergaenzen
			if (nummer <= 0 || nummer > adressen.size()) {
				System.out.println("Keine gültige Eingabe");
			}
			else
				break;
		}

		adressen.remove(nummer-1);
		try{
			File kopie=new File("kopie.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(output)));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(kopie)));
			int counter = 1;
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
			String dest=output.getPath();
			output.delete();
			kopie.renameTo(new File(dest));
		}catch(Exception e){
			System.out.println(e.getMessage()+"Der Spamfilter konnte nicht geoeffnet werden.");
		}
	}
	
	public ArrayList<String> getAdressen() {
		return adressen;
	}

	public  void setAdressen(ArrayList<String> adressen) {
		this.adressen = adressen;
	}

	public static void kommandos() {
		System.out.println("Sie haben die Moeglichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(i+1+": "+kommandoliste.get(i)+"\n");
		}
	}

	public static void zurueck(){
		//absichtlich leer
	}
}
