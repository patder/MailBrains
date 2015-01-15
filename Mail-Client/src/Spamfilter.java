import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Die Klasse Spamfilter realisiert eine weitere Ansicht des Programms.
 * Man kann Adressen in seinen Spamfilter hinzufuegen, gespeicherte Adressen loeschen,
 * sich die moeglichen Kommandos anzeigen lassen, in die vorherige Ansicht zurueckkehren
 * und sich die bereits gespeicherten Adressen anzeigen lassen.
 */
public class Spamfilter {

	public static ArrayList<String> adressen;
	public static File output;
	private static ArrayList<String> kommandoliste;
	private static Scanner sc;

	/**
	 * Die Methode init wird aufgerufen, um in die Spamfilteransicht zu gelangen.
	 * Sie belegt die Klassenattribute mit Werten.
	 * Dafür werden u.a. die Adressen aus der Text-Datei eingelesen.
	 */
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

		holeSpam();
		Startfenster.clearAll();
		auswaehlen();
	}

	public static void holeSpam(){
		try {
			Scanner tmp = new Scanner(output);
			while(tmp.hasNextLine()){
				adressen.add(tmp.nextLine());
			}
			tmp.close();
		}catch(FileNotFoundException e){
			//ist normal wenn noch keine Mails als Spam gespeichert werden
		}
		Startfenster.clearAll();
	}

	private static void auswaehlen() {

		while(true){
			int eingabe = -1;
			anzeigen();
			kommandos();
			while(eingabe < 1 || eingabe > 5) {
				try {
					eingabe = Integer.parseInt(sc.nextLine());
					if (eingabe < 1 || eingabe > 5) {
						System.out.println("Fehlerhafte Eingabe, bitte geben gueltigen Befehl eingeben:");
					}
				} catch (Exception e) {
					System.out.println("Fehlerhafte Eingabe, bitte geben gueltigen Befehl eingeben:");
				}
			}

			switch(eingabe){
				case 1:
					hinzufuegen();
					break;
				case 2:loeschen();
					break;
				case 3: Startfenster.clearAll();
					break;
				case 4: return;

				case 5:Startfenster.clearAll();
					break;
			}
		}

	}

	private static void anzeigen(){
		Startfenster.clearAll();
		if(0<adressen.size()) {
			System.out.println("Folgende Mailadressen haben Sie als Spam markiert.");
			for (int i = 0; i < adressen.size(); i++) {
				System.out.println(i + 1 + ": " + adressen.get(i));
			}
			System.out.println();
		}else{
			System.out.println("Sie haben noch keine Mails als Spam markiert.");
		}
	}

	private static void hinzufuegen(){
		System.out.println("Bitte geben Sie die Adresse ein, die Sie hinzufuegen wollen(0: abbrechen).");
		String adresse=sc.nextLine(); //Bedingungen für die Adresse ergaenzen
		if(adresse.equals("0")){
			return;
		}
		adressen.add(adresse);
		BufferedWriter fw;
		try{
			fw=new BufferedWriter(new FileWriter(output,true));
			fw.append(adresse);
			fw.newLine();
			fw.close();
		}catch(Exception e){
			System.out.println("Der Spamfilter konnte nicht ge�ffnet werden.");
		}
	}

	private static void loeschen(){
		Startfenster.clearAll();
		System.out.println("Bitte geben Sie die Nummer der Adresse ein, die Sie loeschen wollen(0: abbrechen).");

		int nummer = -1;
		while(nummer <0 || nummer > adressen.size()){
			try{
				nummer=Integer.parseInt(sc.nextLine());
				if(nummer <0 || nummer > adressen.size()){
					throw new Exception();
				}
			}
			catch(Exception e){
				System.out.println("Ungueltige Eingabe, bitte erneut versuchen.");
			}
		}
		if(nummer == 0){
			return;
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
			System.out.println(dest);
			output.delete();
			kopie.renameTo(new File(dest));
		}catch(Exception e){
			System.out.println(e.getMessage()+"Der Spamfilter konnte nicht geoeffnet werden.");
		}
	}

	private ArrayList<String> getAdressen() {
		return adressen;
	}

	private  void setAdressen(ArrayList<String> adressen) {
		this.adressen = adressen;
	}

	private static void kommandos() {
		System.out.println("Sie haben die Moeglichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(i+1+": "+kommandoliste.get(i)+"\n");
		}
	}

	private static void zurueck(){
		//absichtlich leer
	}
}
