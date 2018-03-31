package input;

import increment.RSU;
import increment.Vehicle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import applications.RsuApplication;
import applications.VehicleApplication;
import core.Application;
import core.Connection;
import core.Message;
import core.Settings;
import core.SimClock;

public class PeriodicityEvent extends MessageEventGenerator {
	public static final int Timepiece = 3600;
	 public PeriodicityEvent(Settings s) {
	    	super(s);
	    	this.nextEventsTime = 3600;
	    }
	 static int i =1;
	 public ExternalEvent nextEvent() {
	        int responseSize = 0; /* no responses requested */
	        int index = SimClock.getIntTime()/Timepiece;
	        if(index>i)
	        {
	        	for(int a =30;a<300;a++)
	        	{
	        		Vehicle rsu = Vehicle.velByAddr(a);
	        		Collection<Application> apps = rsu.getRouter().getApplications(null);
	        		for(Application app:apps)
	        		{
	        			VehicleApplication Rsu = (VehicleApplication) app;
	        			if(Rsu.checkcpa())//&&index>10)
	        				System.out.println("Attack detected"+ SimClock.getIntTime());
	        		}
	        	}
	        		i = index;
	        }
	        MessageCreateEvent mce = new MessageCreateEvent(0, 0, getID(),
	               0, responseSize, this.nextEventsTime);
	        mce.addMsgType("request");
	        mce.addContentId(0+"");
	        this.nextEventsTime +=this.drawNextEventTimeDiff();
	        return mce;
	     
	    }
}
