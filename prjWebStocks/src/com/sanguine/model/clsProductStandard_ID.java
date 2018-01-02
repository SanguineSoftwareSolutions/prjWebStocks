package com.sanguine.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

//@Embeddable
@SuppressWarnings("serial")
public class clsProductStandard_ID implements Serializable {
	
	private long id;
	private String strClientCode;
	
	
public clsProductStandard_ID(){}
	
	public clsProductStandard_ID(long id,String strClientCode){
		this.id=id;
		this.strClientCode=strClientCode;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

}
