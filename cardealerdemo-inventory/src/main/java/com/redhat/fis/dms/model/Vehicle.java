package com.redhat.fis.dms.model;

public class Vehicle {
	
	private String vehicleId;
	private String make;
	private String model;
	private String type;
	private String year;
	private Integer price;
	private Integer inventorycount;
	
	
	public String getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Integer getInventorycount() {
		return inventorycount;
	}
	public void setInventorycount(Integer inventorycount) {
		this.inventorycount = inventorycount;
	}
	
	
	

}
