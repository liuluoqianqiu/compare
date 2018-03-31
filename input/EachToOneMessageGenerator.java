package input;

import java.util.HashMap;
import java.util.Random;

import applications.*;
import core.Application;
import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import input.ContentMessageGenerator;
import routing.SprayAndWaitRouter;
import increment.RSU;
import increment.Vehicle;
public class EachToOneMessageGenerator extends MessageEventGenerator {
	private static String MSG_REQUEST = "request";
	private static String MSG_CONTENT = "content";
	protected static HashMap<String, HashMap<Integer, Double>> ReqCreateTable = new HashMap<String, HashMap<Integer, Double>>();
	public static int requestCounter=0;
    private int nextFromOffset; //next index to use from the "from" range
    private int contentId = 79; //preload content id from 0 - 79 
    public EachToOneMessageGenerator(Settings s) {
    	super(s);
    	this.nextFromOffset = 0;
    	this.nextEventsTime = 3600;
    }
    @Override
    public ExternalEvent nextEvent() {
        int responseSize = 0; /* no responses requested */
        int msgSize;
        int interval;
        int from;
        int to;
        String msgType = null;
        String contentId = null;
        Random rand=new Random(System.currentTimeMillis());
        msgType = drawMsgType();
        from = rand.nextInt(270)+30;
        msgSize = drawMessageSize();
        to = rand.nextInt(270)+30;
        if(msgType.equals(MSG_REQUEST)) {
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
        	 
        }
        MessageCreateEvent mce = new MessageCreateEvent(from, to, getID(),
                msgSize, responseSize, this.nextEventsTime);
 
        mce.addContentId(contentId);
        mce.addMsgType(msgType);
        if(msgType.equals(MSG_REQUEST)){
        	if (!ReqCreateTable.containsKey(contentId)){
        		HashMap<Integer, Double> secLayer=new HashMap<Integer, Double>();
        		ReqCreateTable.put(contentId, secLayer);
        	}
        	double time= this.nextEventsTime;
        	ReqCreateTable.get(contentId).put(from, time);
        	requestCounter++;
        }
       
            interval = drawNextEventTimeDiff();
            this.nextEventsTime += 20;
            //System.out.println(this.nextEventsTime);
        if (this.msgTime != null && this.nextEventsTime > this.msgTime[1]) {
            this.nextEventsTime = Double.MAX_VALUE;  //next event would be later than the end time
        }
        return mce;
    }
    /**
     * 判断content数量是否超过100，超过则只产生request
     * 否则按比例随机产生request或content
     * @return
     * @author chennora
     */
    public String drawMsgType() {
    		return MSG_REQUEST;
    }
	public double nextEventsTime() {
		return this.nextEventsTime;
	}
    
    /**contentId
     * @return
     */
    public String newContentId() {
    	this.contentId++;
		return this.contentId + "";
    }
    public String getContentId() {
    	Random rand = new Random(System.currentTimeMillis());
    	int num = rand.nextInt(100);
    	int contentNum = Message.AllContents.size();
    	if(num >=80) {
    		while(true) {
    			int index  = rand.nextInt(contentNum);
    			if(index%10 == 0 || index%10 == 1)
    				return Integer.toString(index);
    		}
    	}else {
    		while(true) {
    			int index = rand.nextInt(contentNum);
    			if(index%10 >= 2 && index%10 <= 9)
    				return Integer.toString(index);
    		}
    	}
    }
    public static HashMap<String, HashMap<Integer, Double>> getReqTable(){
    	return ReqCreateTable;
    }
   public static int getReqCounter(){
	   return requestCounter;
   }
}