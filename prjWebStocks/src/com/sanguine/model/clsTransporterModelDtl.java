package com.sanguine.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tbltransportermasterdtl")
public class clsTransporterModelDtl implements  Serializable {

		private static final long serialVersionUID = 1L;
		public clsTransporterModelDtl(){}

	//Variable Declaration
	

	@Id	
	@Column(name="intId")
	private long intId;	@GeneratedValue
	
	@Column(name="strTransCode")
	private String strTransCode;
	
	@Column(name="strVehCode")
	private String strVehCode;
	
	@Column(name="strVehNo")
	private String strVehNo;
	
	@Column(name="strClientCode")
	private String strClientCode;
	
	
	public long getIntId() {
		return intId;
	}

	public void setIntId(long intId) {
		this.intId = intId;
	}

	public String getStrTransCode() {
		return strTransCode;
	}

	public void setStrTransCode(String strTransCode) {
		this.strTransCode = strTransCode;
	}

	

	public String getStrVehCode() {
		return strVehCode;
	}

	public void setStrVehCode(String strVehCode) {
		this.strVehCode = strVehCode;
	}

	public String getStrVehNo() {
		return strVehNo;
	}

	public void setStrVehNo(String strVehNo) {
		this.strVehNo = strVehNo;
	}

	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
}
