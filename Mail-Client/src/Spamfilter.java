import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class Spamfilter extends Fenster {
	
	private ArrayList<String> adressen;
	private File output;
	
	public Spamfilter(){
		adressen=new ArrayList<String>();
		output=new File("Spamfilter.txt");
		
		// Initalisierung der kommandoliste
		kommandoliste.add("hinzufuegen");
		kommandoliste.add("loeschen");
		kommandoliste.add("kommandos");
		kommandoliste.add("zurueck");
		
	}
	
	public void hinzufuegen(String adresse){
		adressen.add(adresse);
		FileWriter fw;
		try{
			fw=new FileWriter(output);
			fw.append(adresse);
			fw.close();
		}catch(Exception e){
			System.out.println("Der Spamfilter konnte nicht geöffnet werden.");
		}
	}
	
	public void loeschen(int nummer){
		adressen.remove(nummer);
		FileWriter fw;
		try{
			Scanner sc=new Scanner(output);
			//in der Datei die richtige zeile loeschen
		}catch(Exception e){
			System.out.println("Der Spamfilter konnte nicht geöffnet werden.");
		}
	}
	
	public ArrayList<String> getAdressen() {
		return adressen;
	}

	public void setAdressen(ArrayList<String> adressen) {
		this.adressen = adressen;
	}

	
}
