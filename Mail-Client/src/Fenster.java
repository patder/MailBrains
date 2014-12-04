import java.util.ArrayList;
//lalalalala
public class Fenster {
	protected ArrayList<String> kommandoliste;
	protected Fenster aktuell;
	
	public Fenster(){
		kommandoliste=new ArrayList<String>();
		aktuell=new Startfenster();
	}
	
	public void kommandos() {
		System.out.println("Sie haben die M�glichkeit folgende Kommandos einzugeben: ");
		for (int i = 0; i < kommandoliste.size(); i++) {
			System.out.print(kommandoliste.get(i) + ", ");
		}
	}

	




	public Fenster getAktuell(){
		return aktuell;
	}

	public void setAktuell(Fenster f){
		aktuell=f;
	}
	}
