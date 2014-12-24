
public class Konto {
	private String name;
	private String adress;
	private String password;
	private String server;
	private String smtpServer;
	private String pop3Server;
	private String protocol; 
	private int port;
	private double refRate;
	
	public Konto(String name, String myA, String server, String smtp, String pop3, int port, String protocol, double refRate){
		this.name = name;
		this.adress = myA;
		this.server = server;
		this.smtpServer = smtp;
		this.pop3Server = pop3;
		this.port = port;
		this.protocol = protocol;
		this.refRate = refRate;
	}
	
    public Konto(String name,String mailAdresse, String passwort, double refRate){
    	this.name=name;
    	this.refRate=refRate;
    	this.adress=mailAdresse;
    	this.password=passwort;
    	this.port=25;
    	//try block falls keine mailadresse
    	String provider=mailAdresse.split("@")[1];
		if(provider.equals("gmx.de")||provider.equals("gmx.net")) {
			smtpServer = "mail.gmx.net";
			pop3Server="pop.gmx.net";
		}
		else if (provider.equals("gmail.com")||provider.equals("googlemail.com")) {
			smtpServer="smtp.googlemail.com";
			pop3Server="pop.gmail.com";
    	}
		else if(provider.equals("web.de")){
			smtpServer="smtp.web.de";
			pop3Server="pop3.web.de";
		}

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
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getSmtpServer() {
		return smtpServer;
	}
	public void setSmtpServer(String pop3Server) {
		this.smtpServer = smtpServer;
	}
	public String getPop3Server() {
		return pop3Server;
	}
	public void setPop3Server(String pop3Server) {
		this.pop3Server = pop3Server;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRefRate() {
		return refRate;
	}

	public void setRefRate(double refRate) {
		this.refRate = refRate;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public MailAuthenticator getPasswordAuthentication() {
		return new MailAuthenticator(adress, password);
	}

	public MailAuthenticator getPasswordAuthentication(String passwort) {
		return new MailAuthenticator(adress, password);
	}
	
	
}
