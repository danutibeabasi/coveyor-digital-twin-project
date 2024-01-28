package fr.emse.fayol.maqit.digital_twin;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Conveyor extends ControlAgent implements Runnable{
	
	private int motorSpeed;
	private int defaultSpeed;
	private Stock stock;
	private int length; 
	private int productPosition = 0;  
	private boolean shutdown;
	private int waitingTime = 1000;
	private String name;
	private int products;
	private String mpath;
	
	public Conveyor(String uuid, String clientName, Stock stock,int defaultSpeed, String ip, String port, String mpath) {  	
		super();
		this.mpath = mpath;
		this.name = uuid;
		motorSpeed = 0;
		this.defaultSpeed = (defaultSpeed < 100) ? defaultSpeed : 100;
		this.clientName = clientName;
		this.stock = stock;
		shutdown = false;
		products = 0;
		connection = new Connexion(ip,port);
	}

	public Conveyor(String uuid, Stock stock,int motorSpeed, String mpath) {  	
		super();
		this.mpath = mpath;
		this.name = uuid;
		this.defaultSpeed = (motorSpeed < 100) ?  motorSpeed : 100;
		this.stock = stock;
		products = 0;
		connection = new Connexion();
	}
	
	public void connect() {
		try {
			connection.connect(name);
		} catch(Exception me) {
			me.printStackTrace();
		}
	} 

	public void disconnect() {
		try {
			connection.unsubscribe(this);
			connection.disconnect();
		} catch(Exception me) {
			me.printStackTrace();
		}
	}  

	private void updateSpeed(int value) {
		motorSpeed = (value < 100) ? value : 100;	  
	}
	
	private boolean isShutdown() {
		return shutdown;
	}
	
	public void handleMessage(String topic, String content) {
		System.out.println("Message arrived");
		System.out.println("Topic: " + topic);
		System.out.println("Message: " + content);
	
		String[] stb = topic.split("/");
		String source = stb[3];
		String type = stb[5] + "_" +stb[6];
		addMessage(new Message(type,source,content));	
	}

	public void init() {
	    products = 1;	
	    updateSpeed(defaultSpeed);	
		if(stock.isEmpty()) {
			stop();
		}
		else {
			start();
		}
	}

	public void waitProduct() {
		stop();
		products++ ;
	}

	public void nextProduct() {
		if (stock.destock()) {														
			start();
		}
	}
	
	public void run(){
	    init();
		while (!shutdown) {
			Message m = connection.readMessage();			
			if(!Objects.isNull(m)) {
				String source = m.getDataSource();
				if(source.compareTo("conveyor")==0) {
					if (m.getType().compareTo("actuators_request")==0) {
						if (m.getValue().length() > 1) {
							int value = Integer.parseInt(m.getValue().substring(1));
							updateSpeed(value);						
						} else {
							updateSpeed(0);
						}
					} else if (m.getType().compareTo("sensors_ir")==0) {
						if (m.getValue().compareTo("0") == 0) {
							waitProduct();
						} else if (m.getValue().compareTo("1") == 0) {
							nextProduct();
						}
					}
				}	
			} else {
				try {
					Thread.sleep(waitingTime);
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}	

	private String createTopic(String key) {
		return key;
	}
	
	public void initValues(List<String> topics) {		
		for(String mtopic:topics){
			try{
				connection.subscribe(mtopic, this); 
			} catch(Exception me) {
				me.printStackTrace();
			}
		}
	}

	public void updateValues(List<String> topics) {		
		for(String mtopic:topics){
			try{
				connection.subscribe(mtopic, this); 
			} catch(Exception me) {
				me.printStackTrace();
			}
		}
	}

	public void addMessage(Message m) {
		connection.addMessage(m);	
	}  
	
	private void start() {
		try {
			connection.publish(mpath+"/actuators/request", "R"+defaultSpeed);   
		} catch(Exception me) {
			me.printStackTrace();
		}
	}

	private void stop() {
		try {
			connection.publish(mpath+"/actuators/request", "S"); 			
		} catch(Exception me) {
			me.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception, InterruptedException, FileNotFoundException, ParseException, IOException {
		JSONParser parser = new JSONParser();
		JSONObject a = (JSONObject) parser.parse(new FileReader("ini.json"));
		String brokerip = (String) a.get("broker_ip");
	  	String brokerport = (String) a.get("broker_port");
	  	String clientname = (String) a.get("broker_port");
	  	int stockvalue = Math.toIntExact((Long) a.get("stock_value"));
	  	int stockmax = Math.toIntExact((Long) a.get("stock_max"));
    	int motorspeed = Math.toIntExact((Long) a.get("initial_percent_speed"));
		String id = (String) a.get("conveyor_id");
		String path = (String) a.get("path");
    	JSONArray subs = (JSONArray) a.get("subscribe");
    	List<String> ltopics = new ArrayList<String>();
    	for(Object elt: subs) {
    		ltopics.add(path+id+(String)elt);
    	}    
	
		Stock stock = new Stock(stockvalue,stockmax);
		Conveyor conveyor = new Conveyor(id,stock,motorspeed,path+id);
		//Conveyor conveyor = new Conveyor(id,clientname,stock,motorspeed,brokerip,brokerport);
		//conveyor.connect();
		
		String mqttIP = brokerip+":"+brokerport;
    	MqttManager mqttClient = new MqttManager(clientname, mqttIP);        
    	mqttClient.connect();  
    	mqttClient.initializeCallback();		
		conveyor.connection.setClient(mqttClient);
		conveyor.initValues(ltopics);		
		conveyor.run();				 
		mqttClient.disconnect();
	}

    public String getMpath() {
        return null;
    }
}
