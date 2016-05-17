package com.redhat.fis.dms.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
	"vehicleId",
	"make",
	"model",
	"type",
	"year",
	"price",
	"inventoryCount"
})
@XmlRootElement(name = "vehicle")
public class Vehicle implements Serializable {

	private static final long serialVersionUID = 6614349012208666446L;
	
	@XmlElement(required = true)
	private String vehicleId;

	@XmlElement(required = true)
	private String make;

	@XmlElement(required = true)
	private String model;

	@XmlElement(required = true)
	private String type;

	@XmlElement(required = true)
	private String year;

	@XmlElement(required = true)
	private Integer price;

	@XmlElement(required = true)
	private Integer inventoryCount;
	
	
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
	public Integer getInventoryCount() {
		return inventoryCount;
	}
	public void setInventoryCount(Integer inventoryCount) {
		this.inventoryCount = inventoryCount;
	}
	
	@Override
	public boolean equals(Object v) {
		Vehicle other = (Vehicle) v;
		if ( this == other)
			return true;
		if ( this.getVehicleId() == other.getVehicleId() )
			return true;
		return false;
	}
}
