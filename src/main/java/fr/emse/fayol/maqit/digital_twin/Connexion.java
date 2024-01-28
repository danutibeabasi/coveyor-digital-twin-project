package fr.emse.fayol.maqit.digital_twin;

import java.util.Stack;

public class Connexion {
	
	private String ip;
    private String port;
	
	protected volatile Stack<Message> mailBox;
	protected MqttManager mqttClient;

	public Connexion(String ip, String port) {
		mailBox = new Stack<Message>();
		this.ip = ip;  //193.49.165.40
    	this.port = port;
	}

	public Connexion() {
		mailBox = new Stack<Message>();		
	}
	
	public void addMessage(Message m) {
			mailBox.add(m);
	}
	
	public Message readMessage() {
			if (mailBox.size()>0)
				return mailBox.pop();
			return null;
	}

	public void setClient(MqttManager mm) {
		mqttClient = mm;
	}
	
	public void connect(String name) throws Exception {
		String mqttIP = ip+":"+port;
        mqttClient = new MqttManager(name, mqttIP);        
        mqttClient.connect();  
        mqttClient.initializeCallback();      
	}

	public void disconnect() throws Exception {		
        mqttClient.disconnect();          
	}

	public void subscribe(String topic, MessageHandler who) throws Exception {
		mqttClient.subscribe(topic, who);
	}

	public void unsubscribe(String topic, MessageHandler who) throws Exception {
		mqttClient.unsubscribe(topic, who);
	}

	public void unsubscribe(MessageHandler who) throws Exception {
		mqttClient.unsubscribe(who);
	}

	public void publish(String topic, String value) throws Exception {
		mqttClient.publish(topic, value);
	}
}
