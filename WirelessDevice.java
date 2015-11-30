public class WirelessDevice implements Runnable{
	public String Name;
	private AccessPoint AP;
	private final String MAC;
	private String localIPaddress;
	public  String publicIPaddress;
	private int signalStrength;  	// from 1 to 100
	public int obstacles = 0;
	private int metersAwayFromAP;
	private double throughput;
	Stopwatch stopwatch;
	long runtime;
	boolean running;
	
	
	///////////////////////////////////////////////////////////////////////////
	public WirelessDevice(String name){
		this.Name = name;
		this.AP = null;
		this.MAC = Integer.toHexString(AccessPoint.randInt(0,255)) + "-" + Integer.toHexString(AccessPoint.randInt(0,255)) + "-" + 
				   Integer.toHexString(AccessPoint.randInt(0,255)) + "-" + Integer.toHexString(AccessPoint.randInt(0,255)) + "-" + 
				   Integer.toHexString(AccessPoint.randInt(0,255)) + "-" + Integer.toHexString(AccessPoint.randInt(0,255));
		this.localIPaddress = null;
		this.publicIPaddress = null;
		this.stopwatch = new Stopwatch();
		this.throughput = 0.0;
		this.runtime = 0;
		running = false;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public boolean isMAC(String mac){
		if(mac == this.MAC){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public void connectToAP(AccessPoint ap){
		sendAssociationReq(ap);
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public void sendAssociationReq(AccessPoint ap){
		//System.out.println(Name + " sending association request frame to " + AP.getSSID());
		ap.requestConnection(this);
	}

	
	///////////////////////////////////////////////////////////////////////////
	public void receiveAssociationReply(AccessPoint ap, int distance){
		this.AP = ap;
		this.metersAwayFromAP = distance;
		obstacles = AccessPoint.randInt(0, 2);
		getIPFromDHCP();
		calculateSignalStrength();
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public void getIPFromDHCP(){
		// variables
		String localIP = AP.getLocalIP();
		int num;
		String tokens[] = localIP.split("\\.");
		
		// look for last number
		num = Integer.parseInt(tokens[3]);
		num++;
		num+=AP.getLocationInAP(this)+1;
		tokens[3] = "" + num;
		
		// set new local ip
		this.localIPaddress = tokens[0] + '.' + tokens[1] + '.' + tokens[2] + '.' + tokens[3];
		
		// set public ip
		this.publicIPaddress = AP.getPublicIP();
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public void calculateSignalStrength(){
		double freq = AP.channel_2G.GHz * Math.pow(10, 9);
		double wavelength = MainSimulation.lightspeed / freq;
		
		double signalLoss = 4 * Math.PI * metersAwayFromAP;
		signalLoss = signalLoss / wavelength;
		signalLoss = Math.pow(signalLoss, 2);
		signalLoss = signalLoss / 1000000;
		signalStrength = (int)(100 - (signalLoss * obstacles * 2));
	}
	
	
	public void updateThroughput(int size, double time){
		if(throughput == 0.0){
			throughput = size / time;
		}
		else{
			double newThpt = size / time;
			throughput = (throughput + newThpt) / 2;
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public void RequestFromInternet(){
		// load frame and header with information
		Frame frame = new Frame();
		frame.loadNetworkData("request for webpage from " + Websites.getRandom() + ".com");
		frame.macHeader.sourceMAC = this.MAC;
		frame.macHeader.destinationMAC = this.AP.getMAC();
		frame.setAssociatedDevice(this);
		frame.size = 20 + frame.networkData.length();
		
		// "sense" the channel to see if it's in use
		while(AP.channel_2G.inUse){
			// there was a collision so increase counter & wait random amnt of time
			MainSimulation.numCollisions++;
			try {
				Thread.sleep(AccessPoint.randInt(50,150));
			} catch (InterruptedException e) {}
		}
		
		// send request frame to the AP
		AP.channel_2G.inUse = true;
		stopwatch.start();
		AP.channel_2G.sendFrameToAP(frame);
		
		try {
			Thread.sleep((100-signalStrength));
		} catch (InterruptedException e) {}
		AP.channel_2G.inUse = false;
		AP.getRequestedFromInternet();
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public void ReceiveReplyFromInternet(){
		// for parsing content in frame
		Frame IPdatagram[] = new Frame[4];
		int i = 0;
		
		// "sense" the channel to see if it's in use
		while(AP.channel_2G.inUse){
			// there was a collision so increase counter & wait random amnt of time
			MainSimulation.numCollisions++;
			try {
				Thread.sleep(AccessPoint.randInt(50,200));
			} catch (InterruptedException e) {}
		}
		
		// receive frames from the access point
		AP.channel_2G.inUse = true;
	
		// package frames into IP datagram and send to network layer in wireless device	
		while(!AP.channel_2G.isEmpty()){
			IPdatagram[i] = AP.channel_2G.getFrameFromAP();
			System.out.println(Name + " received " + IPdatagram[i].networkData);
			i++;
		}
		
		try {
			Thread.sleep((100-signalStrength));
		} catch (InterruptedException e) {}
		AP.channel_2G.inUse = false;
		updateThroughput(IPdatagram[0].size, stopwatch.elapsedTime());
	}
	
	/**
	 * This thread runs a new wireless device on the wifi LAN.  It sends 
	 * requests to the access point randomly, trying as accurately as possible 
	 * to simulate a normal user on their smart phone or tablet.
	 * @pre the wireless device should already be connected to an access point.
	 * 		if it is not, this algorithm will not work correctly.
	
	 */
	///////////////////////////////////////////////////////////////////////////
	public void run(){
		// variables
		int randomTime,i;
		Stopwatch timer = new Stopwatch();
		
		// start simulation
		running = true;
		timer.start();
		while((timer.elapsedTime()*1000) < this.runtime){
			// request packets from the internet at random times (1-30 sec)
			randomTime = AccessPoint.randInt(1000,3200);
			
			// request a series of packets from random webpage
			for(i = 0; i < AccessPoint.randInt(1,10); i++){
				RequestFromInternet();
			}
			
			// delay
			try {
				Thread.sleep(randomTime);
			} catch (InterruptedException e) {}
		}
		running = false;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public void setRunTime(long val){
		this.runtime = val;
	}
	
	
	// getters ////////////////////////////////////////////////////////////////
	public AccessPoint getAP() {
		return AP;
	}
	
	
	public String getMAC() {
		return MAC;
	}

	
	public String getLocalIP() {
		return localIPaddress;	
	}
	

	public String getPublicIP(){
		return publicIPaddress;
	}
	
	
	public double getThroughput(){
		return throughput;
	}
	
	public int getSignalStrength(){
		return signalStrength;
	}
	
	public int getDistance(){
		return metersAwayFromAP;
	}
}
