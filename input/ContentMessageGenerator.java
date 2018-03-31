package input;

import increment.RSU;
import increment.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import applications.*;
import core.Application;
import core.Connection;
import core.Message;
import core.Settings;
import core.SettingsError;
import core.SimClock;

public class ContentMessageGenerator extends MessageEventGenerator {

	private List<Integer> fromIds;
	private int contentId = 5000;

	private static String MSG_CONTENT = "content";
	private static HashMap<String, Integer> ReplicaCounter = new HashMap<String, Integer>();
	
	public ContentMessageGenerator(Settings s) {
		super(s);
		this.fromIds = new ArrayList<Integer>();
		
		if (toHostRange == null) {
			throw new SettingsError("Destination host (" + TO_HOST_RANGE_S + 
					") must be defined");
		}
		for (int i = hostRange[0]; i < hostRange[1]; i++) {
			fromIds.add(i);
		}
		Collections.shuffle(fromIds, rng);
	}
	/** 
	 * Returns the next message creation event
	 * @see input.EventQueue#nextEvent()
	 */
	public ExternalEvent nextEvent() {
		int responseSize = 0; /* no responses requested */
		int from;
		int to;
		int msgSize = drawMessageSize();
		String contentId = this.newContentId();
		String msgType = MSG_CONTENT;	
		Random rand=new Random(System.currentTimeMillis());
		to=rand.nextInt(270)+30;
		from = rand.nextInt(30);
    	Message.AllContents.put(contentId, msgSize);
//    	Vehicle vel = Vehicle.velByAddr(from);
//    	for(Application app: vel.getRouter().getApplications(null)) {
//    		VehicleApplication cdn = (VehicleApplication)app;
//    		cdn.addNewContent(contentId, msgSize, SimClock.getTime());
//    	}
//		if (to == from) { /* skip self */
//			if (this.fromIds.size() == 0) { /* oops, no more from addresses */
//				this.nextEventsTime = Double.MAX_VALUE;
//				return new ExternalEvent(Double.MAX_VALUE);
//			} else {
//				from = this.fromIds.remove(0);
//			}
//		}
		if (this.contentId < 0) {
			this.nextEventsTime = Double.MAX_VALUE; /* no messages left */
		} else {
			//this.nextEventsTime += drawNextEventTimeDiff();
			this.nextEventsTime +=0.5;// drawNextEventTimeDiff();
		}
		RSU vel = RSU.AllRSUs.get(from);
//    	for(Application app: vel.getRouter().getApplications(null)) {
//    		RsuApplication cdn = (RsuApplication)app;
//    		cdn.addNewContent(contentId, msgSize, SimClock.getTime());
//    	}
		  if(!vel.getConnections().isEmpty()){
          	Connection con = vel.getConnections().get(0);
          	to=con.getTo().getAddress();
          }
		MessageCreateEvent mce = new MessageCreateEvent(from, to, getID(), 
				msgSize, responseSize, this.nextEventsTime);
		mce.addContentId(contentId);
        mce.addMsgType(msgType);
		return mce;
	}
	
    public String newContentId() {
    	this.contentId--;
		return this.contentId + "";
    }
    
    public static HashMap<String, Integer> getReplicaCounter(){
    	return ReplicaCounter;
    }

}
