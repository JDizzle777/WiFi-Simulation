public class MAC_Header {
	public byte frameControl[] = new byte[2];
	public byte durationID[] = new byte[2];
	public String destinationMAC;
	public String sourceMAC;
	public byte sequenceControl[] = new byte[2];
	
	/*
	 * FrameControl[] is made up of:
	 *     D15-D14    D13-D12   D11-D8   D7     D6       D5      D4       D3         D2     D1    D0
	 * 	Protocol_Vers  Type    Subtype  ToDS  FromDS  MoreFrag  Retry  PwrMangmt  MoreData  WEP	 Order
	 */
	
	public void setBit(byte field, int pos){
		field |= (1 << pos);
	}
	
	public void clearBit(byte field, int pos){
		field &= ~(1 << pos);
	}
}
