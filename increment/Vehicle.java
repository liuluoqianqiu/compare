package increment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import applications.*;
import core.Application;
import core.Coord;
import core.DTNHost;
import core.MessageListener;
import core.ModuleCommunicationBus;
import core.MovementListener;
import core.NetworkInterface;
import movement.MovementModel;
import routing.MessageRouter;
public class Vehicle extends DTNHost{

	public static List<Vehicle> AllVehicles = new ArrayList<Vehicle>();
	private VehicleHistoryInfo vehicleHistoryInfo;
	public Vehicle(List<MessageListener> msgLs, List<MovementListener> movLs,
			String groupId, List<NetworkInterface> interf,
			ModuleCommunicationBus comBus, MovementModel mmProto,
			MessageRouter mRouterProto) {
		super(msgLs, movLs, groupId, interf, comBus, mmProto, mRouterProto);
		// TODO Auto-generated constructor stub
		this.vehicleHistoryInfo = new VehicleHistoryInfo();
	}
	public void addHistoryInfo(Coord trajectory, int timeSeg) {
		vehicleHistoryInfo.getLine().add(trajectory);
		Block block = Block.getBlockById(Block.calBlockId(trajectory));
		if(vehicleHistoryInfo.getBlockHistory().containsKey(timeSeg))
			//若已存在该时间片，则加入该时间片对应Block列表
			vehicleHistoryInfo.getBlockHistory().get(timeSeg).add(block);
		else {
			//若不存在，则新建该时间片对应列表
			ArrayList<Block> blocks = new ArrayList<Block>();
			blocks.add(block);
			vehicleHistoryInfo.getBlockHistory().put(timeSeg, blocks);
		}
	}
	public void updateCdnPit(String contentId, double pop) {
		Collection<Application> apps = this.getRouter().getApplications(null);
		for(Application app: apps) {
			VehicleApplication cdn = (VehicleApplication)app;
			cdn.updatePitFromRsu(contentId, pop);
		}
	}
	public void updateCdnCs(String contentId, int size, double receivedtime) {
		Collection<Application> apps = this.getRouter().getApplications(null);
		for(Application app: apps) {
			VehicleApplication cdn = (VehicleApplication)app;
			cdn.addNewContent(contentId,size,receivedtime);
		}
	}
	public static Vehicle velByAddr(int hostAddr) {
		int rsuNum = RSU.AllRSUs.size();
		return AllVehicles.get(hostAddr - rsuNum);  
	}
}
