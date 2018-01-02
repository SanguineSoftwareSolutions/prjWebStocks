package com.sanguine.crm.bean;

import java.util.List;

import com.sanguine.crm.model.clsInvSettlementdtlModel;
import com.sanguine.crm.model.clsInvoiceModelDtl;
import com.sanguine.crm.model.clsInvoiceTaxDtlModel;
import com.sanguine.model.clsPOTaxDtlModel;

public class clsInvoiceBean {
	
	//Variable Declaration
		private String strInvCode;

		private long intid;

		private String dteInvDate;

		private String strAgainst;

		private String strSOCode;

		private String strCustCode;

		private String strPONo;

		private String strNarration;

		private String strPackNo;

		private String strLocCode;

		private String strVehNo;

		private String strMInBy;

		private String strTimeInOut;

		private String strUserCreated;

		private String dteCreatedDate;

		private String strUserModified;

		private String dteLastModified;

		private String strAuthorise;

		private String strDktNo;

		private String strSAdd1;

		private String strSAdd2;

		private String strSCity;

		private String strSState;

		private String strSCtry;

		private String strSPin;

		private String strInvNo;
		
		private String strReaCode;

		private String strSerialNo;

		private String strWarrPeriod;

		private String strWarraValidity;

		private String strClientCode;
		
		private String strSCountry;
		
		private String strProdType;
		
		private String strLocName;
		
		private String strCustName;
		
		private List<clsInvoiceDtlBean> listclsInvoiceModelDtl;
		
	
		
		private Double dblTaxAmt;
		
		private Double dblTotalAmt;
		
		private Double dblSubTotalAmt;
		
		private Double dblUnitPrice;
		
		private List<clsInvoiceTaxDtlBean> listInvoiceTaxDtl;
		
		private Double dblDiscount;
		
		private Double dblDiscountAmt;
		
		private String strExciseable;
		
		private String strSettlementCode;
		
		private List<clsInvSettlementdtlModel> listInvsettlementdtlModel;
		
		
		
		//Getter And Setter 
		

		public long getIntid() {
			return intid;
		}

		public void setIntid(long intid) {
			this.intid = intid;
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

		public String getStrCustCode() {
			return strCustCode;
		}

		public void setStrCustCode(String strCustCode) {
			this.strCustCode = strCustCode;
		}

		public String getStrPONo() {
			return strPONo;
		}

		public void setStrPONo(String strPONo) {
			this.strPONo = strPONo;
		}

		public String getStrNarration() {
			return strNarration;
		}

		public void setStrNarration(String strNarration) {
			this.strNarration = strNarration;
		}

		public String getStrPackNo() {
			return strPackNo;
		}

		public void setStrPackNo(String strPackNo) {
			this.strPackNo = strPackNo;
		}

		public String getStrLocCode() {
			return strLocCode;
		}

		public void setStrLocCode(String strLocCode) {
			this.strLocCode = strLocCode;
		}

		public String getStrVehNo() {
			return strVehNo;
		}

		public void setStrVehNo(String strVehNo) {
			this.strVehNo = strVehNo;
		}

		public String getStrMInBy() {
			return strMInBy;
		}

		public void setStrMInBy(String strMInBy) {
			this.strMInBy = strMInBy;
		}

		public String getStrTimeInOut() {
			return strTimeInOut;
		}

		public void setStrTimeInOut(String strTimeInOut) {
			this.strTimeInOut = strTimeInOut;
		}

		public String getStrUserCreated() {
			return strUserCreated;
		}

		public void setStrUserCreated(String strUserCreated) {
			this.strUserCreated = strUserCreated;
		}

		public String getDteCreatedDate() {
			return dteCreatedDate;
		}

		public void setDteCreatedDate(String dteCreatedDate) {
			this.dteCreatedDate = dteCreatedDate;
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

		public String getStrAuthorise() {
			return strAuthorise;
		}

		public void setStrAuthorise(String strAuthorise) {
			this.strAuthorise = strAuthorise;
		}

		public String getStrDktNo() {
			return strDktNo;
		}

		public void setStrDktNo(String strDktNo) {
			this.strDktNo = strDktNo;
		}

		public String getStrSAdd1() {
			return strSAdd1;
		}

		public void setStrSAdd1(String strSAdd1) {
			this.strSAdd1 = strSAdd1;
		}

		public String getStrSAdd2() {
			return strSAdd2;
		}

		public void setStrSAdd2(String strSAdd2) {
			this.strSAdd2 = strSAdd2;
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

		public String getStrSCtry() {
			return strSCtry;
		}

		public void setStrSCtry(String strSCtry) {
			this.strSCtry = strSCtry;
		}

		public String getStrSPin() {
			return strSPin;
		}

		public void setStrSPin(String strSPin) {
			this.strSPin = strSPin;
		}

	

		public String getStrReaCode() {
			return strReaCode;
		}

		public void setStrReaCode(String strReaCode) {
			this.strReaCode = strReaCode;
		}

		public String getStrSerialNo() {
			return strSerialNo;
		}

		public void setStrSerialNo(String strSerialNo) {
			this.strSerialNo = strSerialNo;
		}

		public String getStrWarrPeriod() {
			return strWarrPeriod;
		}

		public void setStrWarrPeriod(String strWarrPeriod) {
			this.strWarrPeriod = strWarrPeriod;
		}

		public String getStrWarraValidity() {
			return strWarraValidity;
		}

		public void setStrWarraValidity(String strWarraValidity) {
			this.strWarraValidity = strWarraValidity;
		}

		public String getStrClientCode() {
			return strClientCode;
		}

		public void setStrClientCode(String strClientCode) {
			this.strClientCode = strClientCode;
		}

		public String getStrSCountry() {
			return strSCountry;
		}

		public void setStrSCountry(String strSCountry) {
			this.strSCountry = strSCountry;
		}

		public String getStrProdType() {
			return strProdType;
		}

		public void setStrProdType(String strProdType) {
			this.strProdType = strProdType;
		}

		public String getStrLocName() {
			return strLocName;
		}

		public void setStrLocName(String strLocName) {
			this.strLocName = strLocName;
		}

		public String getStrCustName() {
			return strCustName;
		}

		public void setStrCustName(String strCustName) {
			this.strCustName = strCustName;
		}


		public String getStrInvCode() {
			return strInvCode;
		}

		public void setStrInvCode(String strInvCode) {
			this.strInvCode = strInvCode;
		}

		public String getDteInvDate() {
			return dteInvDate;
		}

		public void setDteInvDate(String dteInvDate) {
			this.dteInvDate = dteInvDate;
		}

		public String getStrInvNo() {
			return strInvNo;
		}

		public void setStrInvNo(String strInvNo) {
			this.strInvNo = strInvNo;
		}

		public Double getDblTaxAmt() {
			return dblTaxAmt;
		}

		public void setDblTaxAmt(Double dblTaxAmt) {
			this.dblTaxAmt = dblTaxAmt;
		}

		public Double getDblTotalAmt() {
			return dblTotalAmt;
		}

		public void setDblTotalAmt(Double dblTotalAmt) {
			this.dblTotalAmt = dblTotalAmt;
		}

		public Double getDblSubTotalAmt() {
			return dblSubTotalAmt;
		}

		public void setDblSubTotalAmt(Double dblSubTotalAmt) {
			this.dblSubTotalAmt = dblSubTotalAmt;
		}

		public Double getDblUnitPrice() {
			return dblUnitPrice;
		}

		public void setDblUnitPrice(Double dblUnitPrice) {
			this.dblUnitPrice = dblUnitPrice;
		}

		public Double getDblDiscount() {
			return dblDiscount;
		}

		public void setDblDiscount(Double dblDiscount) {
			this.dblDiscount = dblDiscount;
		}

		public Double getDblDiscountAmt() {
			return dblDiscountAmt;
		}

		public void setDblDiscountAmt(Double dblDiscountAmt) {
			this.dblDiscountAmt = dblDiscountAmt;
		}

		public List<clsInvoiceDtlBean> getListclsInvoiceModelDtl() {
			return listclsInvoiceModelDtl;
		}

		public void setListclsInvoiceModelDtl(List<clsInvoiceDtlBean> listclsInvoiceModelDtl) {
			this.listclsInvoiceModelDtl = listclsInvoiceModelDtl;
		}

		public List<clsInvoiceTaxDtlBean> getListInvoiceTaxDtl() {
			return listInvoiceTaxDtl;
		}

		public void setListInvoiceTaxDtl(List<clsInvoiceTaxDtlBean> listInvoiceTaxDtl) {
			this.listInvoiceTaxDtl = listInvoiceTaxDtl;
		}

		public String getStrExciseable() {
			return strExciseable;
		}

		public void setStrExciseable(String strExciseable) {
			this.strExciseable = strExciseable;
		}

		public String getStrSettlementCode() {
			return strSettlementCode;
		}

		public void setStrSettlementCode(String strSettlementCode) {
			this.strSettlementCode = strSettlementCode;
		}

		public List<clsInvSettlementdtlModel> getListInvsettlementdtlModel() {
			return listInvsettlementdtlModel;
		}

		public void setListInvsettlementdtlModel(
				List<clsInvSettlementdtlModel> listInvsettlementdtlModel) {
			this.listInvsettlementdtlModel = listInvsettlementdtlModel;
		}

		
		
		

}
