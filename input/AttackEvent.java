package input;

import increment.RSU;
import increment.Vehicle;

import java.util.HashMap;
import java.util.Random;

import core.Connection;
import core.Message;
import core.Settings;

public class AttackEvent extends MessageEventGenerator {
	static final int Timepiece = 3600;
	private static final String MSG_REQUEST = "request";
	public AttackEvent(Settings s) {
		super(s);
		this.nextEventsTime=4*Timepiece;
		// TODO Auto-generated constructor stub
	}
	 public ExternalEvent nextEvent() {
		  int responseSize = 0; /* no responses requested */
	        int msgSize;
	        int from;
	        int to;
	        String msgType = null;
	        String contentId = null;
	        Random rand=new Random(System.currentTimeMillis());
	        msgSize = drawMessageSize();
	        to = rand.nextInt(270)+30;
	        msgType = MSG_REQUEST;
	        from = rand.nextInt(270)+30;
	        msgSize = drawMessageSize();
	        	do{
	        	contentId = this.getContentId();
	        	Vehicle vel = Vehicle.velByAddr(from);
	            if(!vel.getConnections().isEmpty()){
	            	Connection con = vel.getConnections().get(0);
	            	to=con.getTo().getAddress();
	            }
	            else{
	            	to = RSU.rsuNearVehicle(from).getAddress();
	            }
	        	}while(!Message.AllContents.containsKey(contentId));
	        	 
	        MessageCreateEvent mce = new MessageCreateEvent(from, to, getID(),
	                msgSize, responseSize, this.nextEventsTime);
	 
	        mce.addContentId(contentId);
	        mce.addMsgType(msgType);
	        if(msgType.equals(MSG_REQUEST)){
	        	if (!EachToOneMessageGenerator.ReqCreateTable.containsKey(contentId)){
	        		HashMap<Integer, Double> secLayer=new HashMap<Integer, Double>();
	        		EachToOneMessageGenerator.ReqCreateTable.put(contentId, secLayer);
	        	}
	        	double time= this.nextEventsTime;
	        	EachToOneMessageGenerator.ReqCreateTable.get(contentId).put(from, time);
	        	EachToOneMessageGenerator.requestCounter++;
	        }
	            this.nextEventsTime += 1;
	        return mce;
	     
	    }
	 public String getContentId() {
	    	Random rand = new Random(System.currentTimeMillis());
	    	int num = rand.nextInt(10)+500;
	        return Integer.toString(num);
	    }
}
