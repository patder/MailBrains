import java.util.ArrayList;
//lalalalala
public class Fenster {
	
	private Spamfilter spam;
	private Adressbuch adress;
	private Mailuebersicht mail;
	private Startfenster start;
	protected ArrayList<String> kommandoliste;
	protected Fenster aktuell;
	
	public Fenster(){
		spam=new Spamfilter();
		adress=new Adressbuch();
		mail=new Mailuebersicht();	
		start=new Startfenster();
		kommandoliste=new ArrayList<String>();
		aktuell=start;
	}
	
	public void kommandos(){
		System.out.println("Sie haben die M�glichkeit folgende Kommandos einzugeben: ");
		for(int i=0;i<kommandoliste.size();i++){
			System.out.print(kommandoliste.get(i)+", ");
		}
	}

	public void spamfilter(){
		aktuell=spam;
		for(int i=0;i<spam.getAdressen().size();i++){
			System.out.println(spam.getAdressen().get(i));
		}
	}
	
	public void adressbuch(){
		aktuell=adress;
		for(int i=0;i<adress.getAdressen().size();i++){
			//formatiert (siehe Lastenheft) ausgeben
		}
	}
	
	public void zurueck(){
		if(aktuell.equals(spam)||aktuell.equals(adress)){
			aktuell=mail;
			for(int i=0;i<mail.getMails().size()&&i<25;i++){
				Mail tmp=mail.getMails().get(i);
				System.out.println(i+"\t"+tmp.getAdresse()+"\t"+tmp.getBetreff()+"\t"+tmp.getEmpfangsdatum());
			}
		}
	}



	public Fenster getAktuell(){
		return aktuell;
	}

	public Fenster setAktuell(Fenster f){
		aktuell=f;
	}
	}
