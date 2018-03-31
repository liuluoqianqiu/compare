package increment;

import java.util.ArrayList;
import java.util.List;

import core.Coord;
import core.DTNHost;
import core.MessageListener;
import core.ModuleCommunicationBus;
import core.MovementListener;
import core.NetworkInterface;
import movement.MovementModel;
import routing.MessageRouter;
public class RSU extends DTNHost {
public static List<RSU> AllRSUs = new ArrayList<RSU>();
	public RSU(List<MessageListener> msgLs, List<MovementListener> movLs,
			String groupId, List<NetworkInterface> interf,
			ModuleCommunicationBus comBus, MovementModel mmProto,
			MessageRouter mRouterProto) {
		super(msgLs, movLs, groupId, interf, comBus, mmProto, mRouterProto);
	}


	/**
	 * 返回距离某车辆节点最近的RSU
	 * @param hostAddr
	 * @return
	 */
	public static RSU rsuNearVehicle(int hostAddr) {
		Vehicle host = Vehicle.velByAddr(hostAddr);
		Coord location = host.getLocation();
		RSU nearRsu = null;
		double temp = 100000.0;
		for(RSU rsu: AllRSUs) {
			double distance = location.distance(rsu.getLocation());
			if(distance<temp) {//如果距离更小，那么选这个rsu
				temp = distance;
				nearRsu = rsu;
			}
		}
		
		return nearRsu;
	}
}
