package com.sanguine.webpms.bean;

import java.util.List;

public class clsCheckInBean{
	
//Variable Declaration
	private String strCheckInNo;

	private String strRegistrationNo;

	private String strType;

	private String strAgainstDocNo;

	private String dteArrivalDate;

	private String dteDepartureDate;

	private String strUserCreated;

	private String strUserEdited;

	private String dteDateCreated;

	private String dteDateEdited;

	private String strClientCode;
	
	private String tmeArrivalTime;
	
	private String tmeDepartureTime;
	
	private String strRoomNo;
	
	private String strRoomDesc;
	
	private String strExtraBedCode;
	
	private String strExtraBedDesc;
	
	private int intNoOfAdults;
	
	private int intNoOfChild;
	
	private String strPayeeGuestCode;

	private List<clsCheckInDetailsBean> listCheckInDetailsBean;
	
	
//Setter-Getter Methods
	public String getStrCheckInNo(){
		return strCheckInNo;
	}
	public void setStrCheckInNo(String strCheckInNo){
		this.strCheckInNo=strCheckInNo;
	}

	public String getStrRegistrationNo(){
		return strRegistrationNo;
	}
	public void setStrRegistrationNo(String strRegistrationNo){
		this.strRegistrationNo=strRegistrationNo;
	}

	public String getStrType(){
		return strType;
	}
	public void setStrType(String strType){
		this.strType=strType;
	}

	public String getStrAgainstDocNo() {
		return strAgainstDocNo;
	}
	public void setStrAgainstDocNo(String strAgainstDocNo) {
		this.strAgainstDocNo = strAgainstDocNo;
	}
	
	public String getDteArrivalDate(){
		return dteArrivalDate;
	}
	public void setDteArrivalDate(String dteArrivalDate){
		this.dteArrivalDate=dteArrivalDate;
	}

	public String getDteDepartureDate(){
		return dteDepartureDate;
	}
	public void setDteDepartureDate(String dteDepartureDate){
		this.dteDepartureDate=dteDepartureDate;
	}

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this.strUserCreated=strUserCreated;
	}

	public String getStrUserEdited(){
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited){
		this.strUserEdited=strUserEdited;
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
		this.strClientCode=strClientCode;
	}
		
	public String getTmeArrivalTime() {
		return tmeArrivalTime;
	}
	public void setTmeArrivalTime(String tmeArrivalTime) {
		this.tmeArrivalTime = tmeArrivalTime;
	}
	
	public String getTmeDepartureTime() {
		return tmeDepartureTime;
	}
	public void setTmeDepartureTime(String tmeDepartureTime) {
		this.tmeDepartureTime = tmeDepartureTime;
	}
	
	public List<clsCheckInDetailsBean> getListCheckInDetailsBean() {
		return listCheckInDetailsBean;
	}
	public void setListCheckInDetailsBean(
			List<clsCheckInDetailsBean> listCheckInDetailsBean) {
		this.listCheckInDetailsBean = listCheckInDetailsBean;
	}
	public String getStrRoomNo() {
		return strRoomNo;
	}
	public void setStrRoomNo(String strRoomNo) {
		this.strRoomNo = strRoomNo;
	}
	public String getStrRoomDesc() {
		return strRoomDesc;
	}
	public void setStrRoomDesc(String strRoomDesc) {
		this.strRoomDesc = strRoomDesc;
	}
	public String getStrExtraBedCode() {
		return strExtraBedCode;
	}
	public void setStrExtraBedCode(String strExtraBedCode) {
		this.strExtraBedCode = strExtraBedCode;
	}
	public String getStrExtraBedDesc() {
		return strExtraBedDesc;
	}
	public void setStrExtraBedDesc(String strExtraBedDesc) {
		this.strExtraBedDesc = strExtraBedDesc;
	}
	public int getIntNoOfAdults() {
		return intNoOfAdults;
	}
	public void setIntNoOfAdults(int intNoOfAdults) {
		this.intNoOfAdults = intNoOfAdults;
	}
	public int getIntNoOfChild() {
		return intNoOfChild;
	}
	public void setIntNoOfChild(int intNoOfChild) {
		this.intNoOfChild = intNoOfChild;
	}
	public String getStrPayeeGuestCode() {
		return strPayeeGuestCode;
	}
	public void setStrPayeeGuestCode(String strPayeeGuestCode) {
		this.strPayeeGuestCode = strPayeeGuestCode;
	}

	
}
