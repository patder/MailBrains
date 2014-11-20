import java.util.ArrayList;

public class Adressbuch extends Fenster{
	private ArrayList<String> adressen;
	public ArrayList<String> getAdressen() {
		return adressen;
	}
	public void setAdressen(ArrayList<String> adressen) {
		this.adressen = adressen;
	}
	public Adressbuch(){
		adressen=new ArrayList<String>();
	}

}
