package fr.emse.fayol.maqit.digital_twin;

public class ConveyorDS extends ControlAgent {
    private Conveyor conveyor;
    private Stock stock;
    private boolean connected;
    private String mpath;

    public ConveyorDS(Conveyor conveyor, Stock stock, String ip, String port) {
        this.conveyor = conveyor;
        this.stock = stock;
        this.connection = new Connexion(ip, port);
        this.connected = false;
    }

    // Getter for mpath
    public String getMpath() {
        return this.mpath;
    }



    @Override
    public void connect() {
        try {
            connection.connect(clientName);
            connected = true;
            // Subscribe to topics that the digital shadow needs to listen to
            connection.subscribe(conveyor.getMpath() + "/actuators/status", this);
            connection.subscribe(conveyor.getMpath() + "/sensors/ir", this);
            connection.subscribe(conveyor.getMpath() + "/actuators/request", this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connected) {
                connection.unsubscribe(this);
                connection.disconnect();
                connected = false;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(String topic, String content) {
        String[] tokens = topic.split("/");
        String messageType = tokens[tokens.length - 1];

        switch (messageType) {
            case "status":
                break;
            case "ir":
                break;
            case "request":
                break;
            default:
                break;
        }
    }

    @Override
    public void run() {
        while (connected) { // Uses the connected flag to control the loop
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore interrupted status
                break; // Exit the loop if the thread is interrupted
            }
        }
    }

    public void stopConveyorDS() {
        disconnect();
    }

}

