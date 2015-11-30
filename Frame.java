public class Frame {
	public MAC_Header macHeader;
	public String networkData;
	public int FCS;
	public int size; // in bytes
	private WirelessDevice associatedDevice;
	
	public Frame(){
		FCS = 0;
		setAssociatedDevice(null);
		macHeader = new MAC_Header();
	}
	
	public void loadNetworkData(String data){
		networkData = data;
		calculateFCS();
	}
	
	public void calculateFCS(){
		char charArr[] = networkData.toCharArray();
		int checksum = 0;
		
		for(int i = 0; i < networkData.length(); i++){
			checksum += charArr[i];
		}
		this.FCS = checksum;
	}

	public WirelessDevice getAssociatedDevice() {
		return associatedDevice;
	}

	public void setAssociatedDevice(WirelessDevice associatedDevice) {
		this.associatedDevice = associatedDevice;
	}
}
