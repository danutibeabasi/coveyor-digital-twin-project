package fr.emse.fayol.maqit.digital_twin;

public abstract class ControlAgent implements Runnable, MessageHandler{
	protected Connexion connection;	
	protected String clientName;  

	public abstract void connect();	
	public abstract void disconnect();	
}