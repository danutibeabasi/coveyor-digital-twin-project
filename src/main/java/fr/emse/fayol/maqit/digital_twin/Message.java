package fr.emse.fayol.maqit.digital_twin;

public class Message {
 private String type;	
 private String dataSource;
 private String receiver;
 private String unit;
 private String value;
 private int scope;
 private int samplingInterval;
 private int id = 0;
 public static int ID = 0;
 
 public Message(String type, String dataSource, String value) {
	this.type = type;
	this.dataSource = dataSource;
	this.value = value;
	id = ID++;
}
 
 public String getType() {
		return type;
}
public void setType(String type) {
	this.type = type;
} 
 
public String getDataSource() {
	return dataSource;
}
public void setDataSource(String dataSource) {
	this.dataSource = dataSource;
}
public String getUnit() {
	return unit;
}
public void setUnit(String unit) {
	this.unit = unit;
}
public String getValue() {
	return value;
}
public void setValue(String value) {
	this.value = value;
}
public int getScope() {
	return scope;
}
public void setScope(int scope) {
	this.scope = scope;
}
public int getSamplingInterval() {
	return samplingInterval;
}
public void setSamplingInterval(int samplingInterval) {
	this.samplingInterval = samplingInterval;
}

@Override
public String toString() {
	return "Message " + id + " [type=" + type + ", dataSource=" + dataSource + ", receiver=" + receiver + ", value=" + value + "]";
}

public String getReceiver() {
	return receiver;
}

public void setReceiver(String receiver) {
	this.receiver = receiver;
}

 
 
}
