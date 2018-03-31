package applications;

import increment.*;
import input.ContentMessageGenerator;
import input.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import routing.SprayAndWaitRouter;
import core.Application;
import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;

public class RsuApplication extends Application {
	private static HashMap AttackTable = new HashMap<String,List<Integer>>();
	private static HashMap StatisTable = new HashMap<String,List<Integer>>();
	private static String MSG_RESPONSE = "response";
	private static String MSG_REQUEST = "request";
	private static String MSG_CONTENT = "content";
	private static String MSG_RELAY_REQ = "relay_request";
	private static String MSG_RELAY_RSP = "relay_response";
	private ArrayList<HashMap<String,ArrayList<Double>>> popTable;
	private static String MSG_POPULARITY = "popularity" ;
	private static String MSG_TIME_REQUEST = "timeRequest" ;
	
	private static String CONTENT_ID = "contentId";
	private static String MSG_TYPE = "msgType";
	
	private HashMap<String, Integer> csTable ;
	private HashMap<String, Double> csTable2;
	public static final String POP_COM = "popCom";
	public static final String POP_STORE = "storePop";
	public static HashMap<String, Double>pitTable = new HashMap<String, Double>();
	private static HashMap<String, DTNHost> fibTable = new HashMap<String, DTNHost>();
	
	public String appID = null;
	private static int MAX_MSG_NUM = 500;
	
	private double storePop;
	private double popCom;
	
	private double RT;
	@Override
	public Message handle(Message msg, DTNHost host) {
		// TODO Auto-generated method stub
		if(msg.getFrom()==host)
			return msg;
		String msgType = (String)msg.getProperty(MSG_TYPE);
		boolean received = false;
//		String contentId = (String)msg.getProperty(CONTENT_ID);
//		String msgId = msg.getId();
//		if((MSG_RESPONSE.equals(msgType)) || (MSG_RELAY_RSP.equals(msgType))){
//			msg = handleContent(msg, host);
//			received = true;
//		}
	 if(MSG_REQUEST.equals(msgType) || MSG_RELAY_REQ.equals(msgType)){
			addInpopTable(msg,msg.getReceiveTime());
				msg = handleRequest(msg, host);
				received = true;
		}
//		else
//		{
//			msg = handleContent(msg,host);
//		}
		msg.setReplaced(received);
		return msg;
	}
	public Message handleRequest(Message msg, DTNHost host) {
		String contentId = (String)msg.getProperty(CONTENT_ID);
		Message m =new Message(host,msg.getFrom(),msg.getid(),Message.AllContents.get(contentId));
		m.addProperty(MSG_TYPE, MSG_RESPONSE);
		m.addProperty(CONTENT_ID, contentId);
		m.addProperty(SprayAndWaitRouter.MSG_COUNT_PROPERTY, 
				msg.getProperty(SprayAndWaitRouter.MSG_COUNT_PROPERTY));
		msg=m;
		return msg;	
	}
	public boolean containsInPit(String contentId) {
			if(pitTable.containsKey(contentId)) 
				return true;		
		return false;
	}
	public void updatePIT(String contentId, double timeRequest) {
		boolean exist = false;
			if(pitTable.containsKey(contentId)) {
				exist = true;
			}
		if(!exist) {
			pitTable.put(contentId, null);
		}
	}
	public Message handleContent(Message msg, DTNHost host) {
		String contentId = (String)msg.getProperty(CONTENT_ID); 
		if(!fibTable.containsKey(contentId)) {
			fibTable.put(contentId, msg.getFrom());
		}
		if(host.equals(msg.getTo())) {
			if(!containsInCs(contentId)) {
				addNewContent(contentId, msg.getSize(), msg.getReceiveTime());
			}
		}else{
			if(containsInPit(contentId) && !containsInCs(contentId))
				addNewContent(contentId, msg.getSize(), msg.getReceiveTime());
			else if(containsInCs(contentId))
			{
				csTable2.remove(contentId);
				csTable2.put(contentId, msg.getReceiveTime());
			}
		}
		return msg;
	}
	public void addNewContent(String contentId, int size, double receiveTime) {
		if(!containsInCs(contentId)){
			if(this.csTable.size() < MAX_MSG_NUM) {
				this.csTable.put(contentId, size);
				this.csTable2.put(contentId, receiveTime);
				updatePIT(contentId, receiveTime);
			
			}
			else{
				double updateTime=receiveTime;
			String min = null;	
			for(String item: csTable2.keySet()){
				if(updateTime>csTable2.get(item))
				{
					updateTime =csTable2.get(item);
					min=item;
				}
			}
			if(min!=null){
			this.csTable.remove(min);
			this.csTable2.remove(min);
			int dcounter = ContentMessageGenerator.getReplicaCounter().get(min);
			dcounter--;
			sendEventToListeners("replace", null, null);
			ContentMessageGenerator.getReplicaCounter().put(min, dcounter);
			this.csTable.put(contentId, size);
			this.csTable2.put(contentId, receiveTime);
			int counter = ContentMessageGenerator.getReplicaCounter().get(contentId);
			counter++;
			ContentMessageGenerator.getReplicaCounter().put(contentId, counter);
			updatePIT(contentId, receiveTime);
			}
		}
		}
		else
		{
		this.csTable2.remove(contentId);
		this.csTable2.put(contentId, receiveTime);
		}
	}
	public boolean containsInCs(String contentId) {
		if(this.csTable.containsKey(contentId))
			return true;
		else 
			return false;
	}
	public RsuApplication(Settings s) {
		this.csTable = new HashMap<String, Integer>();
		this.csTable2= new HashMap<String, Double> ();
		this.fibTable = new HashMap<String, DTNHost>();
		this.pitTable = new HashMap<String, Double>();
		this.popTable = new ArrayList<HashMap<String,ArrayList<Double>>>();
	}
	public RsuApplication(RsuApplication app) {
		super(app);
		this.csTable = new HashMap<String, Integer>();
		this.csTable2= new HashMap<String, Double> ();
		this.fibTable = new HashMap<String, DTNHost>();
		this.pitTable = new HashMap<String, Double>();
		this.popTable = new ArrayList<HashMap<String,ArrayList<Double>>>();
	}
		@Override
	public void update(DTNHost host) {
		// TODO Auto-generated method stub
		return ;
	}
	public void detect(DTNHost vel)
	{
		
	}
	@Override
	public Application replicate() {
		// TODO Auto-generated method stub
		return new RsuApplication(this);
	}
	public void addInpopTable(Message m,double receiveTime)
	{
		if(this.popTable==null)
			this.popTable = new ArrayList<HashMap<String,ArrayList<Double>>>();
		int index = SimClock.getIntTime()/3600;
		if(this.popTable.size()>index)
		{if(this.popTable.get(index).containsKey((String)m.getProperty(CONTENT_ID)))
		{
			ArrayList<Double> InterestList = this.popTable.get(index).get((String)m.getProperty(CONTENT_ID));
			InterestList.add(receiveTime);
		}
		else
		{
			ArrayList<Double> InterestList = new ArrayList<Double>();
			InterestList.add(receiveTime);
			this.popTable.get(index).put((String)m.getProperty(CONTENT_ID), InterestList);
		}}
		else
		{
			for(int i=this.popTable.size();i<=index;i++)
			{
				this.popTable.add(new HashMap<String,ArrayList<Double>>());
			}
			ArrayList<Double> InterestList = new ArrayList<Double>();
			InterestList.add(receiveTime);
			this.popTable.get(index).put((String)m.getProperty(CONTENT_ID), InterestList);
		}
	}
}
