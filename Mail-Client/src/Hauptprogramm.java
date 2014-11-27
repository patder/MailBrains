import java.util.Scanner;
public class Hauptprogramm {
	public static void main(String argv[]){
		Scanner sc=new Scanner(System.in);
		Fenster oben=new Fenster();
		while(true){
			System.out.println("Es gibt folgende Befehle zur Auswahl mit den zugehörigen Nummern:");
			for(int i=0;i<oben.getAktuell().kommandoliste.size();i++){
				System.out.println(oben.getAktuell().kommandoliste.get(i)+": "+i);
			}
			int befehl=sc.nextInt();
			if(befehl<oben.getAktuell().kommandoliste.size()){
				if(oben.getAktuell().getClass().getName()=="Startfenster"){
					Startfenster tmp=(Startfenster)oben.getAktuell();
					if(befehl==0){
						tmp.waehlen(); //Ich wusste jetzt nicht genau wie du die Methoden umgenannt hast, Pat
					}
					else if(befehl==1){
						tmp.loeschen();
					}
					else if(befehl==2){
						tmp.kommandos();
					}
					else if(befehl==3){
						tmp.aendern();
					}
					else if(befehl==4){
						tmp.beenden();
						break; //Endlosschleife wird abgebrochen
					}
					else{
						System.out.println("Dieser Befehl ist leider nicht ausführbar.");
					}
				}
				else if(oben.getAktuell().getClass().getName()=="Spamfilter"){
					Spamfilter tmp=(Spamfilter)oben.getAktuell();
					if(befehl==0){
						tmp.hinzufuegen();
					}
					else if(befehl==1){
						tmp.loeschen();
					}
					else if(befehl==2){
						tmp.kommandos();
					}
					else if(befehl==3){
						tmp.zurueck();
					}
					else{
						System.out.println("Dieser Befehl ist leider nicht ausführbar.");
					}
				}
				else if(oben.getAktuell().getClass().getName()=="Adressbuch"){
					Adressbuch tmp=(Adressbuch)oben.getAktuell();
					if(befehl==0){
						tmp.hinzufuegen();
					}
					else if(befehl==1){
						tmp.loeschen();
					}
					else if(befehl==2){
						tmp.aendern();
					}
					else if(befehl==3){
						tmp.zurueck();
					}
					else if(befehl==4){
						tmp.kommandos();
					}
				}
				else if(oben.getAktuell().getClass().getName()=="Mailuebersicht"){
					Mailuebersicht tmp=(Mailuebersicht)oben.getAktuell();
					if(befehl==0){
						tmp.kommandos();
					}
					else if(befehl==1){
						tmp.ausloggen();
					}
					else if(befehl==2){
						tmp.naechste();
					}
					else if(befehl==3){
						tmp.vorherige();
					}
					else if(befehl==4){
						tmp.verfassen();
					}
					else if(befehl==5){
						tmp.loeschen();
					}
					else if(befehl==6){
						tmp.seite();
					}
					else if(befehl==7){
						tmp.adressbuch();
					}
					else if(befehl==8){
						tmp.spamfilter();
					}
					else if(befehl==9){
						tmp.aktualisieren();
					}
					else if(befehl==10){
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
