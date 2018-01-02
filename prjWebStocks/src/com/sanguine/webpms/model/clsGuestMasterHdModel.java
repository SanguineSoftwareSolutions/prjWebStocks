package com.sanguine.webpms.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

@Entity
@Table(name="tblguestmaster")
@IdClass(clsGuestMasterModel_ID.class)

public class clsGuestMasterHdModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsGuestMasterHdModel(){}

	public clsGuestMasterHdModel(clsGuestMasterModel_ID objModelID){
		strGuestCode = objModelID.getStrGuestCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strGuestCode",column=@Column(name="strGuestCode")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strGuestCode")
	private String strGuestCode;

	@Column(name="strGuestPrefix")
	private String strGuestPrefix;

	@Column(name="strFirstName")
	private String strFirstName;

	@Column(name="strMiddleName")
	private String strMiddleName;

	@Column(name="strLastName")
	private String strLastName;

	@Column(name="strGender")
	private String strGender;

	@Column(name="dteDOB")
	private String dteDOB;

	@Column(name="strDesignation")
	private String strDesignation;

	@Column(name="strAddress")
	private String strAddress;

	@Column(name="strCity")
	private String strCity;

	@Column(name="strState")
	private String strState;

	@Column(name="strCountry")
	private String strCountry;

	@Column(name="strNationality")
	private String strNationality;

	@Column(name="intPinCode")
	private long intPinCode;
	
	@Column(name="lngMobileNo")
	private long lngMobileNo;

	@Column(name="lngFaxNo")
	private long lngFaxNo;

	@Column(name="strEmailId")
	private String strEmailId;

	@Column(name="strPANNo")
	private String strPANNo;

	@Column(name="strArrivalFrom")
	private String strArrivalFrom;

	@Column(name="strProceedingTo")
	private String strProceedingTo;

	@Column(name="strStatus")
	private String strStatus;

	@Column(name="strVisitingType")
	private String strVisitingType;

	@Column(name="strPassportNo")
	private String strPassportNo;

	@Column(name="dtePassportIssueDate")
	private String dtePassportIssueDate;

	@Column(name="dtePassportExpiryDate")
	private String dtePassportExpiryDate;

	@Column(name="strCorporate")
	private String strCorporate;
	
	@Column(name="strUserCreated" ,updatable=false)
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated" ,updatable=false)
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strClientCode")
	private String strClientCode;

//Setter-Getter Methods
	public String getStrGuestCode(){
		return strGuestCode;
	}
	public void setStrGuestCode(String strGuestCode){
		this. strGuestCode = (String) setDefaultValue( strGuestCode, "");
	}

	public String getStrGuestPrefix(){
		return strGuestPrefix;
	}
	public void setStrGuestPrefix(String strGuestPrefix){
		this. strGuestPrefix = (String) setDefaultValue( strGuestPrefix, "");
	}

	public String getStrFirstName(){
		return strFirstName;
	}
	public void setStrFirstName(String strFirstName){
		this. strFirstName = (String) setDefaultValue( strFirstName, "");
	}

	public String getStrMiddleName(){
		return strMiddleName;
	}
	public void setStrMiddleName(String strMiddleName){
		this. strMiddleName = (String) setDefaultValue( strMiddleName, "");
	}

	public String getStrLastName(){
		return strLastName;
	}
	public void setStrLastName(String strLastName){
		this. strLastName = (String) setDefaultValue( strLastName, "");
	}

	public String getStrGender(){
		return strGender;
	}
	public void setStrGender(String strGender){
		this. strGender = (String) setDefaultValue( strGender, "");
	}

	public String getDteDOB(){
		return dteDOB;
	}
	public void setDteDOB(String dteDOB){
		this.dteDOB=dteDOB;
	}

	public String getStrDesignation(){
		return strDesignation;
	}
	public void setStrDesignation(String strDesignation){
		this. strDesignation = (String) setDefaultValue( strDesignation, "");
	}

	public String getStrAddress(){
		return strAddress;
	}
	public void setStrAddress(String strAddress){
		this. strAddress = (String) setDefaultValue( strAddress, "");
	}

	public String getStrCity(){
		return strCity;
	}
	public void setStrCity(String strCity){
		this. strCity = (String) setDefaultValue( strCity, "");
	}

	public String getStrState(){
		return strState;
	}
	public void setStrState(String strState){
		this. strState = (String) setDefaultValue( strState, "");
	}

	public String getStrCountry(){
		return strCountry;
	}
	public void setStrCountry(String strCountry){
		this. strCountry = (String) setDefaultValue( strCountry, "");
	}

	public String getStrNationality(){
		return strNationality;
	}
	public void setStrNationality(String strNationality){
		this. strNationality = (String) setDefaultValue( strNationality, "");
	}

	

	public long getLngMobileNo(){
		return lngMobileNo;
	}
	public void setLngMobileNo(long lngMobileNo){
		this. lngMobileNo = (Long) setDefaultValue( lngMobileNo, "0");
	}

	public long getLngFaxNo(){
		return lngFaxNo;
	}
	public void setLngFaxNo(long lngFaxNo){
		this. lngFaxNo = (Long) setDefaultValue( lngFaxNo, "0");
	}

	public String getStrEmailId(){
		return strEmailId;
	}
	public void setStrEmailId(String strEmailId){
		this. strEmailId = (String) setDefaultValue( strEmailId, "");
	}

	public String getStrPANNo(){
		return strPANNo;
	}
	public void setStrPANNo(String strPANNo){
		this. strPANNo = (String) setDefaultValue( strPANNo, "");
	}

	public String getStrArrivalFrom(){
		return strArrivalFrom;
	}
	public void setStrArrivalFrom(String strArrivalFrom){
		this. strArrivalFrom = (String) setDefaultValue( strArrivalFrom, "");
	}

	public String getStrProceedingTo(){
		return strProceedingTo;
	}
	public void setStrProceedingTo(String strProceedingTo){
		this. strProceedingTo = (String) setDefaultValue( strProceedingTo, "");
	}

	public String getStrStatus(){
		return strStatus;
	}
	public void setStrStatus(String strStatus){
		this. strStatus = (String) setDefaultValue( strStatus, "");
	}

	public String getStrVisitingType(){
		return strVisitingType;
	}
	public void setStrVisitingType(String strVisitingType){
		this. strVisitingType = (String) setDefaultValue( strVisitingType, "");
	}

	public String getStrPassportNo(){
		return strPassportNo;
	}
	public void setStrPassportNo(String strPassportNo){
		this. strPassportNo = (String) setDefaultValue( strPassportNo, "");
	}

	public String getDtePassportIssueDate(){
		return dtePassportIssueDate;
	}
	public void setDtePassportIssueDate(String dtePassportIssueDate){
		this.dtePassportIssueDate=dtePassportIssueDate;
	}

	public String getDtePassportExpiryDate(){
		return dtePassportExpiryDate;
	}
	public void setDtePassportExpiryDate(String dtePassportExpiryDate){
		this.dtePassportExpiryDate=dtePassportExpiryDate;
	}

	public String getStrCorporate(){
		return strCorporate;
	}
	public void setStrCorporate(String strCorporate){
		this. strCorporate = (String) setDefaultValue( strCorporate, "");
	}

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this. strUserCreated = (String) setDefaultValue( strUserCreated, "");
	}

	public String getStrUserEdited(){
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited){
		this. strUserEdited = (String) setDefaultValue( strUserEdited, "");
	}

	public String getDteDateCreated(){
		return dteDateCreated;
	}
	public void setDteDateCreated(String dteDateCreated){
		this.dteDateCreated=dteDateCreated;
	}

	public String getDteDateEdited(){
		return dteDateEdited;
	}
	public void setDteDateEdited(String dteDateEdited){
		this.dteDateEdited=dteDateEdited;
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "");
	}


//Function to Set Default Values
	private Object setDefaultValue(Object value, Object defaultValue){
		if(value !=null && (value instanceof String && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Double && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Integer && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Long && value.toString().length()>0)){
			return value;
		}
		else{
			return defaultValue;
		}
	}

	public long getIntPinCode() {
		return intPinCode;
	}

	public void setIntPinCode(long intPinCode) {
		this.intPinCode = intPinCode;
	}



}
