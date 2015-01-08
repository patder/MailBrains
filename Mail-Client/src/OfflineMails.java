import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import javax.mail.MessagingException;
import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;

/**
 * Created by Patrick on 08.01.2015.
 */
public class OfflineMails {
    private static ArrayList <Mail> mailList = new ArrayList<Mail>();
    private static ArrayList<String> kommandoliste=new ArrayList<String>();
    private static Scanner sc;
    private static int seite = 1;
    private static int upper = 25;
    private static int lower = 1;

    public static void initOffline(String mailAdresse){
        Document doc = null;
        sc=Startfenster.sc;

        try {
            //TODO XMLoutputter benutzen
            // Das Dokument erstellen
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build("offlineMails.xml");

            // Wurzelelement wird auf root gesetzt
            Element myRoot = doc.getRootElement().getChild(mailAdresse);

            //Liste aller vorhandenen Mailkonten als Elemente
            List alleMails = myRoot.getChildren();

            for(int i = 0; i < alleMails.size(); i++){
                String adresse = ((Element) alleMails.get(i)).getChild("adresse").getValue();
                String betreff = ((Element) alleMails.get(i)).getChild("betreff").getValue();
                String nachricht = ((Element) alleMails.get(i)).getChild("nachricht").getValue();
                String empfangsdatum = ((Element) alleMails.get(i)).getChild("empfangsdatum").getValue();

                Mail mail = new Mail(adresse, betreff, nachricht, empfangsdatum);
                mailList.add(mail);
            }
        }
        catch(Exception e){
            System.out.println(e);
            System.out.println("Fehler beim Laden der Offlinemails, Datei fehlerhaft oder nicht gefunden.");
        }
        if(mailList.size() < 25){
            upper = mailList.size();
        }

        kommandoliste.add("Mail anzeigen");
        kommandoliste.add("naechste Seite");
        kommandoliste.add("vorherige Seite");
        kommandoliste.add("zum Startfenster");
        auswaehlen();
    }

    public static void auswaehlen(){
        for (int i = lower-1; i < upper; i++) {
            Mail tmp = mailList.get(i);
            System.out.println(i + 1 + "\t" + tmp.getAdresse() + "\t" + tmp.getBetreff() + "\t" + tmp.getEmpfangsdatum());
        }

        boolean boo = true;
        while(boo) {
            System.out.println("Bitte geben Sie Ihre Wahl an.");
            for (int i = 0; i < kommandoliste.size(); i++) {
                System.out.println((i + 1) + ": " + kommandoliste.get(i));
            }

            int eingabe = -1;
            while (eingabe < 1 || eingabe > 4) {
                try {
                    eingabe = Integer.parseInt(sc.nextLine());
                    if (eingabe < 1 || eingabe > 4) {
                        System.out.println("Fehlerhafte Eingabe, bitte geben gueltigen Befehl eingeben:");
                    }
                } catch (Exception e) {
                    System.out.println("Fehlerhafte Eingabe, bitte geben gueltigen Befehl eingeben:");
                }

            }//schleife zur sicheren Befehlseingabe

            switch (eingabe) {
                case 1:
                    try {
                        zeigeMail(); // kein 'auswaehlen' danach, weil am Ende der Methode wird init von Mailuebersicht aufgerufen
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    weiter(1);
                    break;
                case 3:
                    zurueck(-1);
                    break;
                case 4:
                    boo = false;
                    break;
            }
        }
    }



    public static void zeigeMail(){
        int eingabe = -1;
        while (eingabe < 0 || eingabe > 25) {
            try {
                eingabe = Integer.parseInt(sc.nextLine());
                if (eingabe < 0 || eingabe > 25) {
                    System.out.println("Fehlerhafte Eingabe, bitte geben gueltigen Befehl eingeben:\n0: abbrechen");
                }
            } catch (Exception e) {
                System.out.println("Fehlerhafte Eingabe, bitte geben gueltigen Befehl eingeben:\n0: abbrechen");
            }

        }//schleife zur sicheren Befehlseingabe
        if(eingabe == 0){
            return;
        }
        System.out.println(mailList.get(eingabe).getAdresse() + "\t" + mailList.get(eingabe).getBetreff() + "\t" + mailList.get(eingabe).getEmpfangsdatum());
        System.out.println("\n" + mailList.get(eingabe).getNachricht());
        System.out.println("\n\n0: zurück");

        eingabe = -1;
        while (eingabe != 0) {
            try {
                eingabe = Integer.parseInt(sc.nextLine());
                if (eingabe != 0) {
                    System.out.println("Fehlerhafte Eingabe, bitte geben gueltigen Befehl eingeben:");
                }
            } catch (Exception e) {
                System.out.println("Fehlerhafte Eingabe, bitte geben gueltigen Befehl eingeben:");
            }

        }//schleife zur sicheren Befehlseingabe
    }



    private static void weiter(int i){
        if(true){

        }
    }


    private static void zurueck(int i){
        if(true){

        }
    }
}
