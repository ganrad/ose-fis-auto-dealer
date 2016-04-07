package com.redhat.fis.dms.mockprocessor;

import java.util.ArrayList;
import java.util.List;

import com.redhat.fis.dms.model.Vehicle;

public class VehicleGenerator {
	
	private List<Vehicle> carpool;
	
	public VehicleGenerator(){
		generateCarpool();
	}

		public List<Vehicle> avaliablePriceRangeVehicle(Integer min, Integer max){
			List<Vehicle> avaliableVehicles = new ArrayList<Vehicle>();			
			for( Vehicle car:carpool){
				if(car.getPrice()>=min && car.getPrice()<=max)
					avaliableVehicles.add(car);
			}
			return avaliableVehicles;
		}
		
		public Vehicle getVehicle(String vehicleId){
			
			for( Vehicle car:carpool){
				if(car.getVehicleId().equalsIgnoreCase(vehicleId))
					return car;
			}
			return null;
		}
		
		
		private void generateCarpool(){
			carpool = new ArrayList<Vehicle>();
			
			Vehicle vno01= new Vehicle();
			vno01.setMake("BMW");
			vno01.setModel("M235i");
			vno01.setInventorycount(50);
			vno01.setPrice(45145);
			vno01.setType("front-engine, rear- or 4-wheel-drive, 4-passenger, 2-door coupe");
			vno01.setVehicleId("vno01");
			vno01.setYear("2016");
			carpool.add(vno01); 
			
			Vehicle vno02= new Vehicle();
			vno02.setMake("CADILLAC");
			vno02.setModel("CTS Vsport");
			vno02.setInventorycount(50);
			vno02.setPrice(60950);
			vno02.setType("front-engine, rear-wheel-drive, 5-passenger, 4-door sedan");
			vno02.setVehicleId("vno02");
			vno02.setYear("2016");
			
			Vehicle vno03= new Vehicle();
			vno03.setMake("Chevrolet");
			vno03.setModel("CAMARO-NEW-NEW");
			vno03.setInventorycount(50);
			vno03.setPrice(28490);
			vno03.setType("6-speed manual, 8-speed automatic with manual shifting mode");
			vno03.setVehicleId("vno03");
			vno03.setYear("2016");
			carpool.add(vno02); 
			
			Vehicle vno04= new Vehicle();
			vno04.setMake("Ford");
			vno04.setModel("MUSTANG SHELBY GT350/GT350R");
			vno04.setInventorycount(50);
			vno04.setPrice(49995);
			vno04.setType("front-engine, rear-wheel-drive, 2- or 4-passenger, 2-door coupe");
			vno04.setVehicleId("vno04");
			vno04.setYear("2016");
			carpool.add(vno03); 
			
			Vehicle vno05= new Vehicle();
			vno05.setMake("HONDA");
			vno05.setModel("ACCORD");
			vno05.setInventorycount(50);
			vno05.setPrice(22925);
			vno05.setType("6-speed manual, 6-speed automatic with manual shifting mode, continuously variable automatic");
			vno05.setVehicleId("vno05");
			vno05.setYear("2016");
			carpool.add(vno04); 
			
			Vehicle vno06= new Vehicle();
			vno06.setMake("MAZDA");
			vno06.setModel("MX-5 MIATA");
			vno06.setInventorycount(50);
			vno06.setPrice(25735);
			vno06.setType("front-engine, rear-wheel-drive, 2-passenger, 2-door convertible");
			vno06.setVehicleId("vno06");
			vno06.setYear("2016");
			carpool.add(vno05); 
			
			Vehicle vno07= new Vehicle();
			vno07.setMake("TESLA");
			vno07.setModel("S70 / 70D");
			vno07.setInventorycount(50);
			vno07.setPrice(71200);
			vno07.setType("rear- or front-and-rear-motor, rear- or 4-wheel-drive, 5- or 7-passenger, 4- or 5-door hatchback");
			vno07.setVehicleId("vno07");
			vno07.setYear("2016");
			carpool.add(vno06); 
		}
	
}
