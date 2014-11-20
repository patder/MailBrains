
public class Konto {
	private String name;
	private String adress;
	private String password;
	private String server;
	private String smtpServer;
	private String protocol; 
	private double refRate;
	
	public Konto(String name, String myA, String server, String smtp, String protocol, double refRate){
		this.name = name;
		this.adress = myA;
		this.server = server;
		this.smtpServer = smtp;
		this.protocol = protocol;
		this.refRate = refRate;;
		
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
	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
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

	public void setRefRate(int refRate) {
		this.refRate = refRate;
	}
	
	
	
}
