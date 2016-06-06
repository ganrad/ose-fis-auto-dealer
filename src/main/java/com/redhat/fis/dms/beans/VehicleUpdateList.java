package com.redhat.fis.dms.beans;

import com.mongodb.BasicDBObject;
import java.util.List;
import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import com.redhat.fis.dms.model.Vehicle;

public class VehicleUpdateList {

	public List getUpdateDocList(Exchange exObj) {
		Message inMsgObj = exObj.getIn();
		Vehicle vehicle = (Vehicle) inMsgObj.getBody();

		List<BasicDBObject> updObjects = new ArrayList<BasicDBObject>();

		BasicDBObject hdrObj = new BasicDBObject("vehicleId",inMsgObj.getHeader("vehicleid"));
		updObjects.add(hdrObj);

		BasicDBObject vehObj = 
			new BasicDBObject("vehicleId",inMsgObj.getHeader("vehicleid"));
		vehObj.append("make",vehicle.getMake());
		vehObj.append("model",vehicle.getModel());
		vehObj.append("type",vehicle.getType());
		vehObj.append("year",vehicle.getYear());
		vehObj.append("price",vehicle.getPrice());
		vehObj.append("inventoryCount",vehicle.getInventoryCount());
		updObjects.add(vehObj);

		return(updObjects);
	}
}
