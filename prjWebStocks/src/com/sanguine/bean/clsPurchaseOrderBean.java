package com.sanguine.bean;

import java.util.List;

import com.sanguine.model.clsPOTaxDtlModel;
import com.sanguine.model.clsPurchaseOrderDtlModel;
import com.sanguine.model.clsTCMasterModel;

public class clsPurchaseOrderBean {
	private String strPOCode;
	private String dtPODate ;
	private String strSuppCode;
	private String strAgainst ;
	private String strSOCode ;
	private double dblTotal ;
	private String strVAddress1;
	private String strVAddress2;
	private String strVCity ;
	private String strVState;
	private String strVCountry;
	private String strVPin ;
	private String strSAddress1;
	private String strSAddress2;
	private String strSCity ;
	private String strSState ;
	private String strSCountry;
	private String strSPin ;
	private String strYourRef;
	private String strPerRef;
	private String strEOE ;
	private String strCode;
	private String strUserCreated;
	private String dteDateCreated ;
	private String strUserModified;
	private String dteLastModified ;
	private String strClosePO ;
	private String strAuthorise;
	private String dtDelDate ;
	private double dblTaxAmt ;
	private double dblExtra ;
	private double dblFinalAmt;
	private String strExcise ;
	private double dblDiscount;
	private String strPayMode;
	private String strCurrency;
	private String strAmedment;
	private String strAmntNO ;
	private String stredit ;
	private String strUserAmed;
	private String dtPayDate ;
	private double dblConversion;	
	private List<clsTCMasterModel> listTCMaster;
	
	private List<clsPurchaseOrderDtlModel> listPODtlModel;
	
	private List<clsPOTaxDtlModel> listPOTaxDtl;

	
	public List<clsPOTaxDtlModel> getListPOTaxDtl() {
		return listPOTaxDtl;
	}

	public void setListPOTaxDtl(List<clsPOTaxDtlModel> listPOTaxDtl) {
		this.listPOTaxDtl = listPOTaxDtl;
	}

	public String getStrPOCode() {
		return strPOCode;
	}

	public void setStrPOCode(String strPOCode) {
		this.strPOCode = strPOCode;
	}

	

	public String getStrSuppCode() {
		return strSuppCode;
	}

	public void setStrSuppCode(String strSuppCode) {
		this.strSuppCode = strSuppCode;
	}

	public String getStrAgainst() {
		return strAgainst;
	}

	public void setStrAgainst(String strAgainst) {
		this.strAgainst = strAgainst;
	}

	public String getStrSOCode() {
		return strSOCode;
	}

	public void setStrSOCode(String strSOCode) {
		this.strSOCode = strSOCode;
	}

	public double getDblTotal() {
		return dblTotal;
	}

	public void setDblTotal(double dblTotal) {
		this.dblTotal = dblTotal;
	}

	public String getStrVAddress1() {
		return strVAddress1;
	}

	public void setStrVAddress1(String strVAddress1) {
		this.strVAddress1 = strVAddress1;
	}

	public String getStrVAddress2() {
		return strVAddress2;
	}

	public void setStrVAddress2(String strVAddress2) {
		this.strVAddress2 = strVAddress2;
	}

	public String getStrVCity() {
		return strVCity;
	}

	public void setStrVCity(String strVCity) {
		this.strVCity = strVCity;
	}

	public String getStrVState() {
		return strVState;
	}

	public void setStrVState(String strVState) {
		this.strVState = strVState;
	}

	public String getStrVCountry() {
		return strVCountry;
	}

	public void setStrVCountry(String strVCountry) {
		this.strVCountry = strVCountry;
	}

	public String getStrVPin() {
		return strVPin;
	}

	public void setStrVPin(String strVPin) {
		this.strVPin = strVPin;
	}

	public String getStrSAddress1() {
		return strSAddress1;
	}

	public void setStrSAddress1(String strSAddress1) {
		this.strSAddress1 = strSAddress1;
	}

	public String getStrSAddress2() {
		return strSAddress2;
	}

	public void setStrSAddress2(String strSAddress2) {
		this.strSAddress2 = strSAddress2;
	}

	public String getStrSCity() {
		return strSCity;
	}

	public void setStrSCity(String strSCity) {
		this.strSCity = strSCity;
	}

	public String getStrSState() {
		return strSState;
	}

	public void setStrSState(String strSState) {
		this.strSState = strSState;
	}

	public String getStrSCountry() {
		return strSCountry;
	}

	public void setStrSCountry(String strSCountry) {
		this.strSCountry = strSCountry;
	}

	public String getStrSPin() {
		return strSPin;
	}

	public void setStrSPin(String strSPin) {
		this.strSPin = strSPin;
	}

	public String getStrYourRef() {
		return strYourRef;
	}

	public void setStrYourRef(String strYourRef) {
		this.strYourRef = strYourRef;
	}

	public String getStrPerRef() {
		return strPerRef;
	}

	public void setStrPerRef(String strPerRef) {
		this.strPerRef = strPerRef;
	}

	public String getStrEOE() {
		return strEOE;
	}

	public void setStrEOE(String strEOE) {
		this.strEOE = strEOE;
	}

	public String getStrCode() {
		return strCode;
	}

	public void setStrCode(String strCode) {
		this.strCode = strCode;
	}

	/*public String getStrTC1() {
		return strTC1;
	}

	public void setStrTC1(String strTC1) {
		this.strTC1 = strTC1;
	}

	public String getStrTC2() {
		return strTC2;
	}

	public void setStrTC2(String strTC2) {
		this.strTC2 = strTC2;
	}

	public String getStrTC3() {
		return strTC3;
	}

	public void setStrTC3(String strTC3) {
		this.strTC3 = strTC3;
	}

	public String getStrTC4() {
		return strTC4;
	}

	public void setStrTC4(String strTC4) {
		this.strTC4 = strTC4;
	}

	public String getStrTC5() {
		return strTC5;
	}

	public void setStrTC5(String strTC5) {
		this.strTC5 = strTC5;
	}

	public String getStrTC6() {
		return strTC6;
	}

	public void setStrTC6(String strTC6) {
		this.strTC6 = strTC6;
	}

	public String getStrTC7() {
		return strTC7;
	}

	public void setStrTC7(String strTC7) {
		this.strTC7 = strTC7;
	}

	public String getStrTC8() {
		return strTC8;
	}

	public void setStrTC8(String strTC8) {
		this.strTC8 = strTC8;
	}

	public String getStrTC9() {
		return strTC9;
	}

	public void setStrTC9(String strTC9) {
		this.strTC9 = strTC9;
	}

	public String getStrTC10() {
		return strTC10;
	}

	public void setStrTC10(String strTC10) {
		this.strTC10 = strTC10;
	}

	public String getStrTC11() {
		return strTC11;
	}

	public void setStrTC11(String strTC11) {
		this.strTC11 = strTC11;
	}

	public String getStrTC12() {
		return strTC12;
	}

	public void setStrTC12(String strTC12) {
		this.strTC12 = strTC12;
	}
*/
	public String getStrUserCreated() {
		return strUserCreated;
	}

	public void setStrUserCreated(String strUserCreated) {
		this.strUserCreated = strUserCreated;
	}

	public String getDteDateCreated() {
		return dteDateCreated;
	}

	public void setDteDateCreated(String dteDateCreated) {
		this.dteDateCreated = dteDateCreated;
	}

	public String getStrUserModified() {
		return strUserModified;
	}

	public void setStrUserModified(String strUserModified) {
		this.strUserModified = strUserModified;
	}

	public String getDteLastModified() {
		return dteLastModified;
	}

	public void setDteLastModified(String dteLastModified) {
		this.dteLastModified = dteLastModified;
	}

	

	public String getStrClosePO() {
		return strClosePO;
	}

	public void setStrClosePO(String strClosePO) {
		this.strClosePO = strClosePO;
	}

	public String getStrAuthorise() {
		return strAuthorise;
	}

	public void setStrAuthorise(String strAuthorise) {
		this.strAuthorise = strAuthorise;
	}

	public String getDtDelDate() {
		return dtDelDate;
	}

	public void setDteDelDate(String dtDelDate) {
		this.dtDelDate = dtDelDate;
	}

	public double getDblTaxAmt() {
		return dblTaxAmt;
	}

	public void setDblTaxAmt(double dblTaxAmt) {
		this.dblTaxAmt = dblTaxAmt;
	}

	public double getDblExtra() {
		return dblExtra;
	}

	public void setDblExtra(double dblExtra) {
		this.dblExtra = dblExtra;
	}

	public double getDblFinalAmt() {
		return dblFinalAmt;
	}

	public void setDblFinalAmt(double dblFinalAmt) {
		this.dblFinalAmt = dblFinalAmt;
	}

	public String getStrExcise() {
		return strExcise;
	}

	public void setStrExcise(String strExcise) {
		this.strExcise = strExcise;
	}

	public double getDblDiscount() {
		return dblDiscount;
	}

	public void setDblDiscount(double dblDiscount) {
		this.dblDiscount = dblDiscount;
	}

	public String getStrPayMode() {
		return strPayMode;
	}

	public void setStrPayMode(String strPayMode) {
		this.strPayMode = strPayMode;
	}

	public String getStrCurrency() {
		return strCurrency;
	}

	public void setStrCurrency(String strCurrency) {
		this.strCurrency = strCurrency;
	}

	/*public String getStrTC13() {
		return strTC13;
	}

	public void setStrTC13(String strTC13) {
		this.strTC13 = strTC13;
	}

	public String getStrTC14() {
		return strTC14;
	}

	public void setStrTC14(String strTC14) {
		this.strTC14 = strTC14;
	}

	public String getStrTC15() {
		return strTC15;
	}

	public void setStrTC15(String strTC15) {
		this.strTC15 = strTC15;
	}

	public String getStrTC16() {
		return strTC16;
	}

	public void setStrTC16(String strTC16) {
		this.strTC16 = strTC16;
	}

	public String getStrTC17() {
		return strTC17;
	}

	public void setStrTC17(String strTC17) {
		this.strTC17 = strTC17;
	}

	public String getStrTC18() {
		return strTC18;
	}

	public void setStrTC18(String strTC18) {
		this.strTC18 = strTC18;
	}

	public String getStrTC19() {
		return strTC19;
	}

	public void setStrTC19(String strTC19) {
		this.strTC19 = strTC19;
	}

	public String getStrTC20() {
		return strTC20;
	}

	public void setStrTC20(String strTC20) {
		this.strTC20 = strTC20;
	}

	public String getStrTC21() {
		return strTC21;
	}

	public void setStrTC21(String strTC21) {
		this.strTC21 = strTC21;
	}

	public String getStrTC22() {
		return strTC22;
	}

	public void setStrTC22(String strTC22) {
		this.strTC22 = strTC22;
	}

	public String getStrTC23() {
		return strTC23;
	}

	public void setStrTC23(String strTC23) {
		this.strTC23 = strTC23;
	}

	public String getStrTC24() {
		return strTC24;
	}

	public void setStrTC24(String strTC24) {
		this.strTC24 = strTC24;
	}
*/
	public String getStrAmedment() {
		return strAmedment;
	}

	public void setStrAmedment(String strAmedment) {
		this.strAmedment = strAmedment;
	}

	public String getStrAmntNO() {
		return strAmntNO;
	}

	public void setStrAmntNO(String strAmntNO) {
		this.strAmntNO = strAmntNO;
	}

	public String getStredit() {
		return stredit;
	}

	public void setStredit(String stredit) {
		this.stredit = stredit;
	}

	public String getStrUserAmed() {
		return strUserAmed;
	}

	public void setStrUserAmed(String strUserAmed) {
		this.strUserAmed = strUserAmed;
	}

	
	public double getDblConversion() {
		return dblConversion;
	}

	public void setDblConversion(double dblConversion) {
		this.dblConversion = dblConversion;
	}

	public List<clsPurchaseOrderDtlModel> getListPODtlModel() {
		return listPODtlModel;
	}

	public void setListPODtlModel(List<clsPurchaseOrderDtlModel> listPODtlModel) {
		this.listPODtlModel = listPODtlModel;
	}

	public List<clsTCMasterModel> getListTCMaster() {
		return listTCMaster;
	}

	public void setListTCMaster(List<clsTCMasterModel> listTCMaster) {
		this.listTCMaster = listTCMaster;
	}

	public void setDtDelDate(String dtDelDate) {
		this.dtDelDate = dtDelDate;
	}

	public String getDtPODate() {
		return dtPODate;
	}

	public void setDtPODate(String dtPODate) {
		this.dtPODate = dtPODate;
	}

	public String getDtPayDate() {
		return dtPayDate;
	}

	public void setDtPayDate(String dtPayDate) {
		this.dtPayDate = dtPayDate;
	}

	
}
