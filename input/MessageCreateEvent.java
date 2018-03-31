/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import core.DTNHost;
import core.Message;
import core.World;

/**
 * External event for creating a message.
 */
public class MessageCreateEvent extends MessageEvent {
	private int size;
	private int responseSize;
	
	//自定义message类型
	String msgType;
	//自定义contentid
	String contentId = null;
	/**
	 * Creates a message creation event with a optional response request
	 * @param from The creator of the message
	 * @param to Where the message is destined to
	 * @param id ID of the message
	 * @param size Size of the message
	 * @param responseSize Size of the requested response message or 0 if
	 * no response is requested
	 * @param time Time, when the message is created
	 */
	public MessageCreateEvent(int from, int to, String id, int size,
			int responseSize, double time) {
		super(from,to, id, time);
		this.size = size;
		this.responseSize = responseSize;
	}

	
	/**
	 * 为消息添加新字段
	 * @param type
	 */
	public void addMsgType(String type) {
		this.msgType = type;
	}
	
	public void addContentId(String contentId) {
		this.contentId = contentId;
	}
	
	/**
	 * Creates the message this event represents. 
	 */
	@Override
	public void processEvent(World world) {
		DTNHost to = world.getNodeByAddress(this.toAddr);
		DTNHost from = world.getNodeByAddress(this.fromAddr);			
		
		Message m = new Message(from, to, this.id, this.size);
		m.setResponseSize(this.responseSize);
		
		//每新创建一个消息，就为该消息创建一个新字段
		m.addProperty("msgType", this.msgType);
		m.addProperty("contentId", this.contentId);
		
		from.createNewMessage(m);
		
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + fromAddr + "->" + toAddr + "] " +
		"size:" + size + " CREATE";
	}

}
