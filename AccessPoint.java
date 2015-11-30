import java.util.Random;

public class AccessPoint {
	// data members
	private final String localIP;
	private final String publicIP;
	private final String SSID;
	private final String MAC;
	private WirelessDevice hostList[];
	private int numHosts = 0;
	public final Channel channel_2G;
	public boolean busy;
	
	///////////////////////////////////////////////////////////////////////////
	public static int randInt(int min, int max) {
		Random random = new Random();
		
		int randomNum = random.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	
	// member functions ///////////////////////////////////////////////////////
	public AccessPoint(String ssid, String ip_loc, String ip_pub){
		this.localIP = ip_loc;
		this.publicIP = ip_pub;
		this.SSID = ssid;
		this.MAC = Integer.toHexString(randInt(0,255)) + "-" + Integer.toHexString(randInt(0,255)) + "-" + 
				   Integer.toHexString(randInt(0,255)) + "-" + Integer.toHexString(randInt(0,255)) + "-" + 
				   Integer.toHexString(randInt(0,255)) + "-" + Integer.toHexString(randInt(0,255));
		this.busy = false;
		hostList = new WirelessDevice[20];
		channel_2G = new Channel(1, 2.412);
	}

	
	///////////////////////////////////////////////////////////////////////////
	public void requestConnection(WirelessDevice device){
		sendAssociationRes(device, true);
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public void sendAssociationRes(WirelessDevice device, Boolean success){
		if(success){
			hostList[numHosts++] = device;
			device.receiveAssociationReply(this, randInt(1, 30));
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public void getRequestedFromInternet(){
		// get frame info from the wireless host
		int rtt;
		Frame frame = channel_2G.getFrameFromHost();
		String delim = "[ ]+";
		String[] tokens = frame.networkData.split(delim);
		String website = tokens[4];
		
		// output
		System.out.println(SSID + " received " + frame.networkData + " from " + frame.getAssociatedDevice().Name);
	
		// set rtt time based on signal strength
		if(frame.getAssociatedDevice().getSignalStrength() > 67){
			rtt = randInt(100,350);
		}
		else if(frame.getAssociatedDevice().getSignalStrength() < 68 && frame.getAssociatedDevice().getSignalStrength() > 34){
			rtt = randInt(350,650);
		}
		else if(frame.getAssociatedDevice().getSignalStrength() < 34 && frame.getAssociatedDevice().getSignalStrength() > 0){
			rtt = randInt(650,950);
		}
		else{
			System.out.println("Signal strength too low -- cannot send data to device: " + frame.getAssociatedDevice().getMAC() + '.');
			return;
		}
		// wait until public link is done with previous request
		while(this.busy);
		
		// set busy
		this.busy = true;
		
		// get the requested info from the website
		try {
			Thread.sleep(rtt);
		} catch (InterruptedException e) {}
		this.busy = false;
		
		// send the information on the channel back to the host
		int size = randInt(800,2200);
		
		if(size <= 900){
			Frame newFrame = new Frame();
			newFrame.size = size;
			newFrame.networkData = size + "Kb: reply to " + frame.macHeader.sourceMAC + " from " + website;
			channel_2G.sendFrameToHost(newFrame);
		}
		else{// fragment
			for(int i = size, j = 1; i > 0; i -= 900, j++){
				Frame newFrame = new Frame();
				newFrame.size = size;
				if(i > 900){
					newFrame.networkData = 900 + "";
				}
				else{
					newFrame.networkData = i + "";
				}
				newFrame.networkData += "Kb: reply fragment " + j + " to " + frame.macHeader.sourceMAC + " from " + website;
				int k = newFrame.networkData.length();
				for(int l = 0; l < (70-k); l++){
					newFrame.networkData += " ";
				}
				newFrame.networkData += "Collisions: " + MainSimulation.numCollisions;
				channel_2G.sendFrameToHost(newFrame);
			}	
		}
		// tell the host to receive from channel
		frame.getAssociatedDevice().ReceiveReplyFromInternet();
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public WirelessDevice[] getHosts(){
		return hostList;	
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public String getSSID() {
		return SSID;
	}

	
	///////////////////////////////////////////////////////////////////////////
	public String getMAC() {
		return MAC;
	}

	
	///////////////////////////////////////////////////////////////////////////
	public String getLocalIP() {
		return localIP;
	}

	
	///////////////////////////////////////////////////////////////////////////
	public String getPublicIP() {
		return publicIP;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public int getNumHosts(){
		return numHosts;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public int getLocationInAP(WirelessDevice device){
		for(int i = 0; i < numHosts; i++){
			if(hostList[i].Name == device.Name){
				return i;
			}
		}
		return -1;
	}
}
