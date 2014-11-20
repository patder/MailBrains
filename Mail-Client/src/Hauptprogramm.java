import java.util.Scanner;
public class Hauptprogramm {
	public static void main(String argv[]){
		Scanner sc=new Scanner(System.in);
		Fenster oben=new Fenster();
		while(true){
			String befehl=sc.next();
			if(oben.getAktuell().kommandoliste.contains(befehl)){
				if(oben.getAktuell().getClass().getName()=="Startfenster"){
					Startfenster tmp=(Startfenster)oben.getAktuell();
					if(befehl=="waehlen"){
						tmp.waehlen(); //Ich wusste jetzt nicht genau wie du die Methoden umgenannt hast, Pat
					}
					else if(befehl=="loeschen"){
						tmp.loeschen();
					}
					else if(befehl=="kommandos"){
						tmp.kommandos();
					}
					else if(befehl=="aendern"){
						tmp.aendern();
					}
					else if(befehl=="beenden"){
						tmp.beenden();
						break; //Endlosschleife wird abgebrochen
					}
					else{
						System.out.println("Dieser Befehl ist leider nicht ausführbar.");
					}
				}
				else if(oben.getAktuell().getClass().getName()=="Spamfilter"){
					Spamfilter tmp=(Spamfilter)oben.getAktuell();
					if(befehl=="hinzufuegen"){
						tmp.hinzufuegen();
					}
					else if(befehl=="loeschen"){
						tmp.loeschen();
					}
					else if(befehl=="kommandos"){
						tmp.kommandos();
					}
					else if(befehl=="zurueck"){
						tmp.zurueck();
					}
					else{
						System.out.println("Dieser Befehl ist leider nicht ausführbar.");
					}
				}
				else if(oben.getAktuell().getClass().getName()=="Adressbuch"){
					Adressbuch tmp=(Adressbuch)oben.getAktuell();
					if(befehl=="hinzufuegen"){
						tmp.hinzufuegen();
					}
					else if(befehl=="loeschen"){
						tmp.loeschen();
					}
					else if(befehl=="aendern"){

					}
					else if(befehl=="zurueck"){

					}
				}
				else if(oben.getAktuell().getClass().getName()=="Mailuebersicht"){
					Mailuebersicht tmp=(Mailuebersicht)oben.getAktuell();
					if(befehl=="kommandos"){
						tmp.kommandos();
					}
					else if(befehl=="ausloggen"){
						tmp.ausloggen();
					}
					else if(befehl=="naechste"){
						tmp.naechste();
					}
					else if(befehl=="vorherige"){
						tmp.vorherige();
					}
					else if(befehl=="verfassen"){
						tmp.verfassen();
					}
					else if(befehl=="loeschen"){
						tmp.loeschen();
					}
					else if(befehl=="seite"){
						tmp.seite();
					}
					else if(befehl=="adressbuch"){
						tmp.adressbuch();
					}
					else if(befehl=="spamfilter"){
						tmp.spamfilter();
					}
					else if(befehl=="aktualisieren"){
						tmp.aktualisieren();
					}
					else if(befehl=="speichern"){
						tmp.speichern();
					}
					else{
						System.out.println("Dieser Befehl ist leider nicht ausführbar.");
					}
				}
				else{
					System.out.println("Was ist hier los?");
				}
			}
			else{
				System.out.println("Dieser Befehl ist leider nicht ausführbar.");
			}

		}
	}

}
