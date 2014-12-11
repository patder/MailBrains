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


		}
	}

}
