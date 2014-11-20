import java.util.ArrayList;

public class Adressbuch extends Fenster{

	private ArrayList<String> adressen;

	public Adressbuch(){
		adressen=new ArrayList<String>();

		//Initialisierung der Kommandoliste
		kommandoliste.add("hinzufuegen");
		kommandoliste.add("loeschen");
		kommandoliste.add("aendern");
		kommandoliste.add("zurueck");
	}
	public ArrayList<String> getAdressen() {
		return adressen;
	}
	public void setAdressen(ArrayList<String> adressen) {
		this.adressen = adressen;
	}

	public void hinzufuegen(){

	}

	public void loeschen(){

	}

}
