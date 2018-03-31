package applications;

import core.*;
import increment.RSU;
import increment.Vehicle;
import input.ContentMessageGenerator;
import input.Data;
import input.EachToOneMessageGenerator;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import report.CacheReport;
import routing.SprayAndWaitRouter;

public class VehicleApplication extends Application{
	private static final String MSG_RESPONSE = "response";
	private static final String MSG_REQUEST = "request";
	private static final String MSG_CONTENT = "content";
	private static final String MSG_RELAY_REQ = "relay_request";
	private static final String MSG_RELAY_RSP = "relay_response";
	
	private static final String MSG_POPULARITY = "popularity" ;
	private static final String MSG_TIME_REQUEST = "timeRequest" ;
	private static final int Timepiece = 3600;
	private static final String CONTENT_ID = "contentId";
	private static final String MSG_TYPE = "msgType";
	private static final String YES_HIT = "yHit";
	private static final String NO_HIT = "nHit";
	private static final String CS_Size = "csSize";
	
	private HashMap<String, Double> requestRatio = new HashMap<String, Double>();
	private HashMap<String, Double> deviation = new HashMap<String, Double>();
	public static final String POP_COM = "popCom";
	public static final String POP_STORE = "storePop";
	private HashMap<String, Integer> csTable ;
	private HashMap<String, Double> csTable2;
	private ArrayList<HashMap<String,ArrayList<Double>>> popTable;
	private HashMap<String, Double> pitTable ;
	private HashMap<String, HashSet<DTNHost>> fibTable ;
	private static List<Integer> HopTable = new ArrayList<Integer> ();
	private static List<DTNHost> Attacker = new LinkedList<DTNHost> ();
	private static HashMap<Integer,HashMap<String, Integer>> hitTable = 
			new HashMap<Integer, HashMap<String,Integer>>();
	private static HashMap<String, List<Integer>> responseTable = 
			new HashMap<String,List<Integer>>();
	public String appID = null;
	private HashMap<String, Double> finalPop = new HashMap<String, Double>();
	private HashMap<String, Double> formerPop = new HashMap<String, Double>();
	private HashMap<String, ArrayList<Double>> receiveTable;
	private ArrayList<HashMap<String,HashMap<Integer,Integer>>> recordTable;
	
	private double yuzhi = 0;
	private double incre = 3;
	private int size = 0;
	private double yuanzhi =0;
	private double pianyi = 0;
	private static int MAX_MSG_NUM = 50;
	private double storePop;
	private double popCom;
	private HashMap<String,Integer> storet = new HashMap<String,Integer>();
	private HashMap<String,Double> sPop = new HashMap<String,Double>();
	private HashMap<String,Double> cPop = new HashMap<String,Double>();
	public VehicleApplication(VehicleApplication app)
	{
		super(app);
		this.csTable = new HashMap<String, Integer>();
		this.csTable2 =new HashMap<String, Double> ();
		this.fibTable = new HashMap<String, HashSet<DTNHost>>();
		this.pitTable = new HashMap<String, Double>();
		this.storePop =  app.getStorePop();
		this.popTable = new ArrayList<HashMap<String,ArrayList<Double>>>();
		this.recordTable = new ArrayList<HashMap<String,HashMap<Integer,Integer>>> ();
		for(int i = 0;i<=12;i++)
		{
			this.popTable.add(new HashMap<String,ArrayList<Double>>());
			this.recordTable.add(new HashMap<String, HashMap<Integer,Integer>>());
		}
	
	}
	public VehicleApplication(Settings s)
	{
		this.csTable = new HashMap<String, Integer>();
		this.csTable2= new HashMap<String, Double> ();
		this.fibTable = new HashMap<String, HashSet<DTNHost>>();
		this.pitTable = new HashMap<String, Double>();
		this.popTable = new ArrayList<HashMap<String,ArrayList<Double>>>();
		this.recordTable = new ArrayList<HashMap<String,HashMap<Integer,Integer>>> ();
		for(int i = 0;i<=12;i++)
		{
			this.popTable.add(new HashMap<String,ArrayList<Double>>());
			this.recordTable.add(new HashMap<String, HashMap<Integer,Integer>>());
		}
		if(s.contains(POP_STORE))
			this.storePop = s.getDouble(POP_STORE);
	}
	@Override
	public void update(DTNHost host) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Application replicate() {
		return new VehicleApplication(this);
	}
	public double getStorePop() {
		return storePop;
	}
	public void updatePitFromRsu(String contentId, double pop) {
//		boolean exist = false;
//		for(HashMap<String, Double> local: this.pitTable) {
//			if(local.containsKey(contentId)){
//				double newPop = storePop;
//				local.put(MSG_POPULARITY, newPop);
//				exist = true;
//			}
//		}
//		if(!exist) {
//			HashMap<String, Double> data = new HashMap<String, Double>();
//			data.put(contentId, null);
//			data.put(MSG_POPULARITY, storePop);
//			data.put(MSG_TIME_REQUEST, SimClock.getTime());
//			pitTable.add(data);
//		}
	}
	public boolean containsInCs(String contentId) {
		if(this.csTable.containsKey(contentId))
			return true;
		else 
			return false;
	}
	public void addNewContent(String contentId, int size, double receiveTime) {
		if(!containsInCs(contentId)){
			if(this.csTable.size() < MAX_MSG_NUM) {
				this.csTable.put(contentId, size);
				this.csTable2.put(contentId, receiveTime);
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
			this.csTable.put(contentId, size);
			this.csTable2.put(contentId, receiveTime);
			}
		}
		}
		else
		{
		this.csTable2.remove(contentId);
		this.csTable2.put(contentId, receiveTime);
		}
	}
public void AddInPIT(String contentId, double timeRequest) {	
		if(containsInPit(contentId))
		{
			if(pitTable.get(contentId)<timeRequest)
			{
				pitTable.remove(contentId);
				pitTable.put(contentId, timeRequest);
			}
		}
		else
		{
			pitTable.put(contentId, timeRequest);
		}
	}
public boolean containsInPit(String contentId) {
	return pitTable.containsKey(contentId);
}

public Message handle(Message msg, DTNHost host) {
	// TODO Auto-generated method stub
	if(msg.getFrom()==host)
		return msg;
	String msgType = (String)msg.getProperty(MSG_TYPE);
//	System.out.println(msgType);
	Vehicle vel = (Vehicle)host;
	String contentId = (String)msg.getProperty(CONTENT_ID);
	boolean received = false;
	if(MSG_RESPONSE.equals(msgType) || MSG_RELAY_RSP.equals(msgType)){
		
		msg =handleResponse(msg,host);	
	}
	else if(MSG_REQUEST.equals(msgType) || MSG_RELAY_REQ.equals(msgType)){
		addInpopTable(msg,msg.getReceiveTime());
		AddInPIT(contentId, msg.getCreationTime());
			msg = handleRequest(msg,host);
	}
	else{
		addNewContent(contentId, Message.AllContents.get(contentId), msg.getReceiveTime());
	}
	msg.setReplaced(received);
	return msg;
}
public Message handleResponse(Message msg, DTNHost host) {
	String contentId = (String)msg.getProperty(CONTENT_ID);
	Vehicle vel = (Vehicle)host;
//	this.fibTable.put(contentId, host);
	if(this.fibTable.containsKey(contentId))
	{
		this.fibTable.get(contentId).add(host);
	}
	else
	{
		HashSet<DTNHost> hs = new HashSet<DTNHost>();
		hs.add(host);
		this.fibTable.put(contentId, hs);
	}
	boolean received = false;
	HashMap<String, HashMap<Integer, Double>> reqtable = EachToOneMessageGenerator.getReqTable();
	if(host.equals(msg.getTo())){
		if(reqtable.containsKey(contentId))
		{
			if(reqtable.get(contentId).containsKey(vel.getAddress()))
			{
				reqtable.get(contentId).remove(vel.getAddress());
				if(reqtable.get(contentId).size()==0)
					reqtable.remove(contentId);
				msg = handleContent(msg, host);
				received = true;
				return msg;
			}
			else
			{
				msg.setTtl(0);
				return msg;
			}
		}
		else
		{
			msg.setTtl(0);
			return msg;
		}	
	}
	else{
		msg = handleContent(msg, host);
		if(!reqtable.containsKey(contentId))
		{
			msg.setTtl(0);
			return msg;
			
		}
		else
		{
			if(!reqtable.get(contentId).containsKey(vel.getAddress()))
			{
				msg.setTtl(0);
				return msg;
			}
		}
		return msg;
	}

}
public Message handleRequest(Message msg, DTNHost host) {
	// TODO Auto-generated method stub
	String contentId = (String)msg.getProperty(CONTENT_ID);
	String msgId = msg.getId();
	
	HashMap<String,Integer> map=new HashMap<String,Integer>();
	if(this.csTable.containsKey(contentId)) {			
		Message m = new Message(host, msg.getFrom(), msgId, this.csTable.get(contentId));
		this.csTable2.remove(contentId);
		this.csTable2.put(contentId, msg.getReceiveTime());
		m.addProperty(MSG_TYPE, MSG_RESPONSE);
		m.addProperty(CONTENT_ID, contentId);
		m.addProperty(SprayAndWaitRouter.MSG_COUNT_PROPERTY, 
				msg.getProperty(SprayAndWaitRouter.MSG_COUNT_PROPERTY));
		msg = m;
	}
	else if(this.fibTable.containsKey(contentId)) {
		Message m = new Message(msg.getFrom(), fibTable.get(contentId).iterator().next(), msgId, msg.getSize());
		m.addProperty(MSG_TYPE, MSG_RELAY_REQ);
		m.addProperty(CONTENT_ID, contentId);
		m.addProperty(SprayAndWaitRouter.MSG_COUNT_PROPERTY, 
				msg.getProperty(SprayAndWaitRouter.MSG_COUNT_PROPERTY));
		msg = m;
	}
	else{
		if(!host.getConnections().isEmpty()){
		for(Connection con: host.getConnections()){
			Message m = new Message(msg.getFrom(), con.getTo(), msg.getId(), msg.getSize());
			m.addProperty(MSG_TYPE, MSG_RELAY_REQ);
			m.addProperty(CONTENT_ID, contentId);
			msg = m;
			host.getRouter().createNewMessage(msg);
		}
		}
	}
	return msg;
}
public Message handleContent(Message msg, DTNHost host) {
	// TODO Auto-generated method stub
	String contentId = (String)msg.getProperty(CONTENT_ID); 
	if(!fibTable.containsKey(contentId)) {
		HashSet<DTNHost> hs = new HashSet<DTNHost>();
		hs.add(msg.getFrom());
		fibTable.put(contentId, hs);
	}
	else if(!fibTable.get(contentId).contains(msg.getFrom()))
	{
		HashSet<DTNHost> hs = fibTable.get(contentId);
		hs.add(msg.getFrom());
	}
	//int i =countT(contentId);
		if(containsInPit(contentId) && !containsInCs(contentId))
			{
//			boolean a =this.cachesheild(i);
//			if(a)
			addNewContent(contentId, msg.getSize(), msg.getReceiveTime());}
		else if(containsInCs(contentId))
		{
			csTable2.remove(contentId);
			csTable2.put(contentId, msg.getReceiveTime());
		}
	return msg;
}
public static HashMap<Integer, HashMap<String, Integer>> getHitTable() {
	return hitTable;
}
public static List<Integer> getHopTable() {
	return HopTable;
}
public void computePop()
{
	
}
public void addInpopTable(Message m,double receiveTime)
{
	if(this.popTable==null)
	{	this.popTable = new ArrayList<HashMap<String,ArrayList<Double>>>();
	}
	int index = SimClock.getIntTime()/Timepiece;
	if(this.popTable.size()>index)
	{
		if(this.popTable.get(index).containsKey((String)m.getProperty(CONTENT_ID)))
		{
			ArrayList<Double> InterestList = this.popTable.get(index).get((String)m.getProperty(CONTENT_ID));
			InterestList.add(receiveTime);
		}
		else
		{
			ArrayList<Double> InterestList = new ArrayList<Double>();
			InterestList.add(receiveTime);
			this.popTable.get(index).put((String)m.getProperty(CONTENT_ID), InterestList);
		}
	}
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
//	public int countT(String contentId)
//	{
//		if(this.storet.containsKey(contentId))
//		{
//			int i =this.storet.get(contentId)+1;
//			this.storet.remove(contentId);
//			this.storet.put(contentId, i);
//			return i;
//		}
//		else
//		{
//			this.storet.put(contentId, 1);
//			return 1;
//		}
//	}
//	public boolean cachesheild(int t)
//	{
//		double gailv = 1.0/(1.0+Math.exp(20-t));
//		Random rand=new Random(System.currentTimeMillis());
//		double a  = rand.nextGaussian();
//		if(a<gailv)
//		{
//			return true;
//		}
//		else
//			return false;
//	}
	public boolean checkcpa()
	{
		int index = SimClock.getIntTime()/Timepiece-1;
		if(index>0)
		{
			int i=0;
			double j = 0.0;
			this.receiveTable = this.popTable.get(index);
			if(this.receiveTable.isEmpty())
			{
				return false;
			}
			this.requestRatio = new HashMap<String,Double>();
			this.deviation = new HashMap<String,Double>();
			for(String item : this.receiveTable.keySet()){
				for(double time : this.receiveTable.get(item)){
					j++;
					i++;
				}
				this.requestRatio.put(item, j);//这个地方有问题，初始情况requestRatio这个表应该为空，没有进行判定
				j = 0;
			}
			for(String item: this.receiveTable.keySet())
			{
				j = this.requestRatio.remove(item);
				this.requestRatio.put(item,j/i );
			}
			for(String item:this.receiveTable.keySet())
			{
				double a =0;
				for(String template:this.receiveTable.keySet())
				{
					a+=this.requestRatio.get(template)-this.requestRatio.get(item);
				}
				this.deviation.put(item, a);
			}
			for(String item:deviation.keySet())
			{
				if(deviation.get(item)>yuzhi)
					return true;
			}
			yuanzhi = ((yuanzhi * size)+average(this.deviation)*this.deviation.size())/(size+this.deviation.size());
			pianyi = (pianyi*pianyi*size + fangcha(this.deviation))/(size+this.deviation.size());
			pianyi = Math.sqrt(pianyi);
			yuzhi = yuanzhi + pianyi * incre;
			return false;
		}
		int i=0;
		double j = 0.0;
		this.receiveTable = this.popTable.get(index);
		if(this.receiveTable.isEmpty())
		{
			if(this.formerPop!=null)
			{
				for(String item:this.formerPop.keySet())
				{
					double a = this.formerPop.get(item);
					this.finalPop.put(item, a);
				}
			}
			return false;
		}
		this.requestRatio = new HashMap<String,Double>();
		this.deviation = new HashMap<String,Double>();
		for(String item : this.receiveTable.keySet()){
			for(double time : this.receiveTable.get(item)){
				j++;
				i++;
			}
			this.requestRatio.put(item, j);//这个地方有问题，初始情况requestRatio这个表应该为空，没有进行判定
			j = 0;
		}
		for(String item: this.receiveTable.keySet())
		{
			j = this.requestRatio.remove(item);
			this.requestRatio.put(item,j/i );
		}
		for(String item:this.receiveTable.keySet())
		{
			double a =0;
			for(String template:this.receiveTable.keySet())
			{
				a+=this.requestRatio.get(template)-this.requestRatio.get(item);
			}
			this.deviation.put(item, a);
		}
		size += this.deviation.size();
		yuanzhi = average(this.deviation);
		pianyi = biaozhuncha(this.deviation);
		yuzhi = yuanzhi + pianyi * incre;
		return false;
	}
	public double average(ArrayList<Double> a){
		double sum = 0;
		for(double value: a)
		{
			sum += value;
		}
		sum /= a.size();
		return sum;
	}
	public double average(HashMap<String,Double> deviation){
		ArrayList<Double> a = new ArrayList<Double>();
		for(String item:deviation.keySet())
		{
			a.add(deviation.get(item));
		}
		return average(a);
	}
	public double biaozhuncha(HashMap<String,Double> deviation){
		ArrayList<Double> a = new ArrayList<Double>();
		for(String item:deviation.keySet())
		{
			a.add(deviation.get(item));
		}
		return biaozhuncha(a);
	}
	public double biaozhuncha(ArrayList<Double> a){
		double sum = 0;
		double returnValue = 0;
		sum= average(a);
		for(double value :a)
		{
			returnValue+=(sum-value)*(sum-value);
		}
		returnValue = Math.sqrt(returnValue/(a.size()));
		return returnValue;
	}
	public double fangcha(HashMap<String,Double> a)//这个方差计算没有除以n
	{
		ArrayList<Double> b = new ArrayList<Double>();
		for(String item:deviation.keySet())
		{
			b.add(deviation.get(item));
		}
		return fangcha(b);
	}
	public double fangcha(ArrayList<Double> a)
	{
		double sum = average(a);
		double returnvalue =0;
		for(double value:a)
		{
			returnvalue +=(sum-value)*(sum-value);
		}
		return returnvalue;
	}
}
