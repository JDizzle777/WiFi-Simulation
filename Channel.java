import java.util.LinkedList;
import java.util.Queue;

public class Channel {
	public final int channelNum;
	public final double GHz;
	private Queue<Frame> framesToAP;
	private Queue<Frame> framesToHosts;
	public boolean inUse;

	public Channel(int chNum, double ghz){
		this.channelNum = chNum;
		this.GHz = ghz;
		this.inUse = false;
		framesToAP = new LinkedList<Frame>();
		framesToHosts = new LinkedList<Frame>();
	}

	public Frame senseChannel(){
		return framesToHosts.peek();
	}
	
	public boolean isEmpty(){
		return framesToHosts.isEmpty();
	}
	
	public Frame getFrameFromHost() {
		return framesToAP.remove();
	}
	
	public Frame getFrameFromAP(){
		return framesToHosts.remove();
	}

	public void sendFrameToHost(Frame f) {
		this.framesToHosts.add(f);
	}
	
	public void sendFrameToAP(Frame f) {
		this.framesToAP.add(f);
	}
}
