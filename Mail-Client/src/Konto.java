
public class Konto {
	private String adress;
	private String password;
	private String server;
	private String smtpServer;
	private String typ; 
	
	public Konto(String myA, String server, String smtp, String typ){
		this.adress = myA;
		this.server = server;
		this.smtpServer = smtp;
		this.typ = typ;
	}
	
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getTyp() {
		return typ;
	}
	public void setTyp(String typ) {
		this.typ = typ;
	}
	public String getSmtpServer() {
		return smtpServer;
	}
	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}
	
	
	
}
