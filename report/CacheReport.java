package report;

import input.Data;
import input.EachToOneMessageGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import applications.*;
import core.Application;
import core.ApplicationListener;
import core.DTNHost;
import core.Message;
import core.MessageListener;

public class CacheReport extends Report implements MessageListener,ApplicationListener {
	private static String MSG_REQUEST = "request";
	private static String MSG_TYPE = "msgType";
	private static String YES_HIT = "yHit";
	private static String NO_HIT = "nHit";
	private String contentId = "contentId";
	
	private static String Model = "mm";
	private static String CS_Size = "csSize";
	
	
	private int nrOfRequest;
	private int nrOfResponse;
	private int nrOfReceivedRequest;
	private int nrOfHit;
	private int nrOfReplaceContent;
	private int response;
	private int contentCounter;
	private int hop;
	private List<Double> latency = new ArrayList<Double>(); 
	private List<Integer> hopTable = new ArrayList<Integer>();
	private List<Integer> hoptable = new ArrayList<Integer>();
	private HashMap<Integer, HashMap<String, Integer>> hitTable = new HashMap<Integer, HashMap<String, Integer>>();
	
	private static List<HashMap<String,String>> storageTable = new ArrayList<HashMap<String,String>>();
	private List<Integer> busUsage = new ArrayList<Integer>();
	private List<Integer> taxiUsage = new ArrayList<Integer>();
	private List<Integer> byBusUsage = new ArrayList<Integer>();
	private List<Integer> byCarUsage = new ArrayList<Integer>();
	private List<Integer> usage = new ArrayList<Integer>();
	
	
	
	@Override
	public void gotEvent(String event, Object params, Application app,DTNHost host) {
		// TODO Auto-generated method stub
		if (isWarmup()) {
			//addWarmupID(m.getId());
			return;
		}
		double delay;
		//Data d;
		if(event.equals("response")) {
			this.nrOfResponse ++;
			if(params instanceof Data){
				Data d=(Data)params;
				String contentId = d.getId();
				if (EachToOneMessageGenerator.getReqTable().containsKey(contentId)){
					HashMap<Integer, Double> secLayer=new HashMap<Integer,Double>();
					secLayer=EachToOneMessageGenerator.getReqTable().get(contentId);
					//System.out.println(secLayer.containsKey(host.getAddress()));
					if (secLayer.containsKey(host.getAddress())){
						double reqTime = secLayer.get(host.getAddress());
						delay = d.getTime()-reqTime;
						latency.add(delay);
						this.response++;
						secLayer.remove(host.getAddress());
						EachToOneMessageGenerator.getReqTable().put(contentId,secLayer);
					}
				}
			}
		}else if(event.equals("hit")) {
			this.nrOfHit ++;
		}else if(event.equals("receiveRequest")) {
			this.nrOfReceivedRequest ++;
		}else if(event.equals("replace")){
			this.nrOfReplaceContent ++;
		}
		else if(event.equals("hop")){
			this.hop++;
			hoptable.add((Integer) params);
		}
	}

	@Override
	public void newMessage(Message m) {
		// TODO Auto-generated method stub
		if (isWarmup()) {
			addWarmupID(m.getId());
			return;
		}
		
		if(MSG_REQUEST.equals(m.getProperty(MSG_TYPE))){
			this.nrOfRequest ++;
		}
	}

	@Override
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageTransferred(Message m, DTNHost from, DTNHost to,
			boolean firstDelivery) {
		// TODO Auto-generated method stub

	}

	public static List<HashMap<String,String>> getStorageTable() {
		// TODO Auto-generated method stub
		return storageTable;
	}
	public CacheReport() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
		this.nrOfRequest = 0;
		this.nrOfResponse = 0;
		this.response = 0;
		this.nrOfReceivedRequest = 0;
		this.nrOfHit = 0;
		this.nrOfReplaceContent = 0;
		this.contentCounter=0;
		this.hop=0;
		//this.SumHitRatio=0.0;
		//this.SyHit=0.0;
		//ReqCreateTable = EachToOneMessageGenerator.getReqTable();
		hitTable=VehicleApplication.getHitTable();
		hopTable=VehicleApplication.getHopTable();
	}
}
