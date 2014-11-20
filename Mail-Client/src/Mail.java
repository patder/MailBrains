
public class Mail {
	private String adresse;
	private String betreff;
	private String nachricht;
	private String empfangsdatum;
	private boolean offline;
	
	public Mail(String ad, String be, String na, String ed){
		adresse=ad;
		betreff=be;
		nachricht=na;
		empfangsdatum=ed;
		offline=false;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getBetreff() {
		return betreff;
	}

	public void setBetreff(String betreff) {
		this.betreff = betreff;
	}

	public String getNachricht() {
		return nachricht;
	}

	public void setNachricht(String nachricht) {
		this.nachricht = nachricht;
	}

	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public String getEmpfangsdatum() {
		return empfangsdatum;
	}

	public void setEmpfangsdatum(String empfangsdatum) {
		this.empfangsdatum = empfangsdatum;
	}

}
