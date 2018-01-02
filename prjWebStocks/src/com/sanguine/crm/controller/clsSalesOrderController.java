package com.sanguine.crm.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.Connection;
import com.sanguine.bean.clsTransectionProdCharBean;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.crm.bean.clsSalesOrderBean;
import com.sanguine.crm.model.clsPartyMasterModel;
import com.sanguine.crm.model.clsSalesCharModel;
import com.sanguine.crm.model.clsSalesOrderBOMModel;
import com.sanguine.crm.model.clsSalesOrderDtl;
import com.sanguine.crm.model.clsSalesOrderHdModel;
import com.sanguine.crm.model.clsSalesOrderHdModel_ID;
import com.sanguine.crm.service.clsCRMSettlementMasterService;
import com.sanguine.crm.service.clsPartyMasterService;
import com.sanguine.crm.service.clsSalesOrderBOMService;
import com.sanguine.crm.service.clsSalesOrderService;
import com.sanguine.model.clsBomDtlModel;
import com.sanguine.model.clsBomHdModel;
import com.sanguine.model.clsLocationMasterModel;
import com.sanguine.model.clsProdCharMasterModel;
import com.sanguine.model.clsProdProcessModel;
import com.sanguine.model.clsProductMasterModel;
import com.sanguine.model.clsPropertySetupModel;
import com.sanguine.model.clsPurchaseIndentDtlModel;
import com.sanguine.model.clsSubGroupMasterModel;
import com.sanguine.model.clsTransectionProdCharModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsProductMasterService;
import com.sanguine.service.clsRecipeMasterService;
import com.sanguine.service.clsSetupMasterService;
import com.sanguine.service.clsSubGroupMasterService;
import com.sanguine.service.clsUOMService;
import com.sanguine.util.clsReportBean;

@Controller
public class clsSalesOrderController{

	@Autowired
	private clsSalesOrderService objSalesOrderService;
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsUOMService objclsUOMService;
	
	@Autowired
	private clsSetupMasterService objSetupMasterService;
	
	@Autowired
	private clsSalesOrderBOMService objSOBOMService;
	
	@Autowired
	private clsRecipeMasterService objRecipeMasterService;
	
	@Autowired
	private clsProductMasterService objProductMasterService;
	
	private Map<String,ArrayList<clsInnerSalesBom>> mapSOBOMcls;
	
	@Autowired
	private clsPartyMasterService objPartyMasterService;
	
	@Autowired
	private clsSalesOrderBOMService objSoBomService;

	@Autowired
	private clsSubGroupMasterService objSubGroupService;
	
	@Autowired
	private clsCRMSettlementMasterService objSettlementService;
	
	List<clsSubGroupMasterModel> dataSG = new ArrayList<clsSubGroupMasterModel>();
	
	@RequestMapping(value = "/AutoCompletGetSubgroupNameForSO", method = RequestMethod.POST)
	 public @ResponseBody Set<clsSubGroupMasterModel> getSubgroupNames(@RequestParam String term, HttpServletResponse response) {
	 return simulateSubgroupNameSearchResult(term);
	 
	}
	 
	/* @param empName
	 * @return
	 */
	private Set<clsSubGroupMasterModel> simulateSubgroupNameSearchResult(String sgName)
	{
		Set<clsSubGroupMasterModel> result = new HashSet<clsSubGroupMasterModel>();
		// iterate a list and filter by ProductName
		 for (clsSubGroupMasterModel sg : dataSG) 
		 {
			 if (sg.getStrSGName().contains((sgName.toUpperCase())))
			 {
				 result.add(sg);
			 }
		 }
	
		return result;
	 }
	
	//Open SalesOrderHd
	@RequestMapping(value = "/frmSalesOrder", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String,Object> model, HttpServletRequest req){
		
		dataSG = new ArrayList<clsSubGroupMasterModel>();
		@SuppressWarnings("rawtypes")
		List list=objSubGroupService.funGetList();
		 for (Object objSG : list) 
		{
			 clsSubGroupMasterModel sgModel =(clsSubGroupMasterModel) objSG;
			dataSG.add(sgModel);
		}
		 
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String userCode = req.getSession().getAttribute("usercode").toString();
//		String locCode = req.getSession().getAttribute("locationCode").toString();
//		String startDate=req.getSession().getAttribute("startDate").toString();
//		String[] sp=startDate.split(" ");
//		String[] spDate=sp[0].split("/");
//		startDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		

		List<String> strCurrency = new ArrayList<>();
		strCurrency.add("RS");
		strCurrency.add("USD");
		strCurrency.add("EUR");
		strCurrency.add("GBP");
        model.put("currencyList", strCurrency);
        
        List<String> strAgainst = new ArrayList<>();
        strAgainst.add("Direct");
        strAgainst.add("Project");
        strAgainst.add("Sales Projection");
        model.put("againstList", strAgainst);
        
        List<String> strPaymentMode = new ArrayList<>();
        strPaymentMode.add("Cheque");
        strPaymentMode.add("Cash");
        strPaymentMode.add("Credit");
        strPaymentMode.add("Part payment");
        strPaymentMode.add("Bank Transfer");
        model.put("paymentMode", strPaymentMode);
        
        List<String> uomList=new ArrayList<String>();
		uomList=objclsUOMService.funGetUOMList(clientCode);
		uomList.add("");
		model.put("uomList", uomList);
		
		 Map<String, String> settlementList= objSettlementService.funGetSettlementComboBox(clientCode);
	        model.put("settlementList", settlementList);
//		if(null==req.getSession().getAttribute("CalStock"))
//		{
//			objGlobal.funInvokeStockFlash(startDate,locCode,dteCurrDate[0],dteCurrDate[0],clientCode,userCode, "Both");
//		}
//		else
//		{
//			req.getSession().removeAttribute("CalStock");
//		}
        
		 if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmSalesOrder_1","command", new clsSalesOrderBean());
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmSalesOrder","command", new clsSalesOrderBean());
			}else {
				return null;
			}   
		
	}
	
	@RequestMapping(value = "/saveSOData", method = RequestMethod.POST)
	public ModelAndView funSaveSO(@ModelAttribute("command") @Valid clsSalesOrderBean objSOBean,BindingResult result,HttpServletRequest request)
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		String userCode = request.getSession().getAttribute("usercode").toString();
		
		String loginglocCode = request.getSession().getAttribute("locationCode").toString();
		String startDate=request.getSession().getAttribute("startDate").toString();
		String[] sp=startDate.split(" ");
		String[] spDate=sp[0].split("/");
		startDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
		String dteCurrDateTime =objGlobal.funGetCurrentDateTime("yyyy-MM-dd");
		String[] dteCurrDate =dteCurrDateTime.split(" ");
		String propertyCode=request.getSession().getAttribute("propertyCode").toString();
		clsPropertySetupModel objSetup = objSetupMasterService.funGetObjectPropertySetup(propertyCode, clientCode);
//		if(objSetup.getStrShowStockInSO().equals("Y"))
//		{
//			objGlobal.funInvokeStockFlash(startDate,loginglocCode,dteCurrDate[0],dteCurrDate[0],clientCode,userCode, "Both");
//		}
		if(!result.hasErrors())
		{
			clsSalesOrderHdModel SOModel=funPrepardHDModel(objSOBean,request);
			boolean saveFlg=objSalesOrderService.funAddUpdate(SOModel);
			String strSOCode=SOModel.getStrSOCode();
					
			List<clsSalesOrderDtl> listSODtlModel=objSOBean.getListSODtl();
			if(saveFlg)
			{
				if(null!=listSODtlModel)
				{
					objSalesOrderService.funDeleteDtl(strSOCode,clientCode);
					int intindex=1;
					//objSalesOrderService.funDeleteGRNTaxDtl(objSOBean.getStrSOCode(), clientCode);
					for(clsSalesOrderDtl obSODtl:listSODtlModel)
					{
						if(null!=obSODtl.getStrProdCode())
						{
							
							obSODtl.setStrSOCode(strSOCode);
							obSODtl.setStrProdCode(obSODtl.getStrProdCode());
							obSODtl.setDblQty(obSODtl.getDblQty());
							obSODtl.setDblAcceptQty(obSODtl.getDblAcceptQty());
							obSODtl.setDblDiscount(obSODtl.getDblDiscount());
							obSODtl.setStrTaxType("");
							obSODtl.setDblTaxableAmt(obSODtl.getDblTaxableAmt());
							obSODtl.setDblTax(obSODtl.getDblTax());
							obSODtl.setDblTaxAmt(obSODtl.getDblTaxAmt());
							obSODtl.setDblUnitPrice(obSODtl.getDblUnitPrice());
							obSODtl.setDblWeight(obSODtl.getDblWeight());
							obSODtl.setStrProdType("");
							obSODtl.setStrRemarks(obSODtl.getStrRemarks());
							obSODtl.setIntindex(intindex);
							obSODtl.setStrProdChar("");
							//obSODtl.setDblTotalPrice();
							obSODtl.setStrClientCode(clientCode);
							if(obSODtl.getDblAcceptQty()!=0)
							{
							objSalesOrderService.funAddUpdateDtl(obSODtl);
							funPrepardBOMData(SOModel,obSODtl,clientCode,userCode);
							}
							intindex++;
							
							
							if(null!=objSOBean.getListSalesChar())
							{
								objSalesOrderService.funDeleteSalesChar(strSOCode,obSODtl.getStrProdCode());
								List<clsSalesCharModel> listSaleCharModel=objSOBean.getListSalesChar();
								for(clsSalesCharModel objSaleChar : listSaleCharModel)
								{
									if(objSaleChar.getStrProdCode().equals(obSODtl.getStrProdCode()))
						 			{
										objSaleChar.setStrSOCode(strSOCode);
//										objSaleChar.setStrProdCode(obSODtl.getStrProdCode());
										objSalesOrderService.funAddUpdateSaleChar(objSaleChar);
									}
								}
							}
							
						}
					}
					
					
					
					// so bom
					
					
					
					request.getSession().setAttribute("success", true);
					request.getSession().setAttribute("successMessage","SO Code : ".concat(strSOCode));
					request.getSession().setAttribute("rptSOCode",strSOCode);
					request.getSession().setAttribute("CalStock","No");
					return new ModelAndView("redirect:/frmSalesOrder.html?saddr="+urlHits);
					
				}
			}
			return new ModelAndView ("frmSalesOrder?saddr="+urlHits,"command",new clsSalesOrderBean());
				
		}else
		{
			
			return new ModelAndView ("frmSalesOrder?saddr="+urlHits,"command",new clsSalesOrderBean());
		}
		
	}
	
	private clsSalesOrderHdModel funPrepardHDModel(clsSalesOrderBean objBean,HttpServletRequest req)
	{
		long lastNo=0;
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String userCode=req.getSession().getAttribute("usercode").toString();
		String propCode=req.getSession().getAttribute("propertyCode").toString();
		String startDate=req.getSession().getAttribute("startDate").toString();
		clsSalesOrderHdModel SOHDModel;
		objGlobal=new clsGlobalFunctions();
		if(objBean.getStrSOCode().trim().length()==0)
		{
			
			lastNo=objGlobalFunctionsService.funGetLastNo("tblsalesorderhd","SOCode","intId", clientCode);
			
			String year=objGlobal.funGetSplitedDate(startDate)[2];
			String cd=objGlobal.funGetTransactionCode("SO",propCode,year);			
			String strSOCode = cd + String.format("%06d", lastNo);
			
			SOHDModel=new clsSalesOrderHdModel(new clsSalesOrderHdModel_ID(strSOCode, clientCode));
			SOHDModel.setStrSOCode(strSOCode);
			SOHDModel.setStrUserCreated(userCode);
			SOHDModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			SOHDModel.setIntId(lastNo);
		}
		
		else
		{	
			clsSalesOrderHdModel objSalesModel=objSalesOrderService.funGetSalesOrderHd(objBean.getStrSOCode(),clientCode);
			if(null == objSalesModel){
				lastNo=objGlobalFunctionsService.funGetLastNo("tblsalesorderhd","SOCode","intId", clientCode);
				String year=objGlobal.funGetSplitedDate(startDate)[2];
				String cd=objGlobal.funGetTransactionCode("SO",propCode,year);			
				String strSOCode = cd + String.format("%06d", lastNo);
				SOHDModel=new clsSalesOrderHdModel(new clsSalesOrderHdModel_ID(strSOCode, clientCode));
				SOHDModel.setIntId(lastNo);
				SOHDModel.setStrUserCreated(userCode);
				//SOHDModel.setStrPropertyCode(propCode);
				SOHDModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			}else{
				SOHDModel=new clsSalesOrderHdModel(new clsSalesOrderHdModel_ID(objBean.getStrSOCode(),clientCode));
				//objModel.setStrPropertyCode(propCode);
			}
		}
		SOHDModel.setStrUserModified(userCode);
		SOHDModel.setDteLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		SOHDModel.setDteSODate(objBean.getDteSODate());
		SOHDModel.setStrCustCode(objBean.getStrCustCode());
		SOHDModel.setStrCustPONo(objBean.getStrCustPONo());
		SOHDModel.setDteCPODate(objBean.getDteCPODate());
		SOHDModel.setStrLocCode(objBean.getStrLocCode());
		SOHDModel.setDteFulmtDate(objBean.getDteFulmtDate());
		SOHDModel.setStrAgainst(objBean.getStrAgainst());
		SOHDModel.setStrCode(objBean.getStrCode());
		SOHDModel.setStrCurrency(objBean.getStrCurrency());
		SOHDModel.setIntwarmonth(objBean.getIntwarmonth());
		SOHDModel.setStrPayMode(objBean.getStrPayMode());
		SOHDModel.setDblSubTotal(objBean.getDblSubTotal());
		SOHDModel.setDblDisRate(objBean.getDblDisRate());
		SOHDModel.setDblDisAmt(objBean.getDblDisAmt());
		SOHDModel.setStrNarration(objBean.getStrNarration());
		SOHDModel.setDblExtra(objBean.getDblExtra());
		SOHDModel.setDblTotal(objBean.getDblTotal());
		
		
		SOHDModel.setStrBAdd1(objBean.getStrBAdd1());
		SOHDModel.setStrBAdd2(objBean.getStrBAdd2());
		SOHDModel.setStrBCity(objBean.getStrBCity());
		SOHDModel.setStrBState(objBean.getStrBState());
		SOHDModel.setStrBCountry(objBean.getStrBCountry());
		SOHDModel.setStrBPin(objBean.getStrBPin());
		
		SOHDModel.setStrSAdd1(objBean.getStrSAdd1());
		SOHDModel.setStrSAdd2(objBean.getStrSAdd2());
		SOHDModel.setStrSCity(objBean.getStrSCity());
		SOHDModel.setStrSState(objBean.getStrSState());
		SOHDModel.setStrSCountry(objBean.getStrSCountry());
		SOHDModel.setStrSPin(objBean.getStrSPin());
		SOHDModel.setStrSettlementCode(objBean.getStrSettlementCode());
		SOHDModel.setStrAuthorise("N");
		SOHDModel.setStrBOMFlag("N");
		SOHDModel.setStrBoomLen("");
		SOHDModel.setStrCranenModel("");
		SOHDModel.setStrImgName("");
		SOHDModel.setStrMaxCap("");
		SOHDModel.setStrNoFall("");
		SOHDModel.setStrReaCode("");
		if(null!=objBean.getListSalesChar())
		{
			SOHDModel.setStrStatus("Advance Order");
		}else
		{
			SOHDModel.setStrStatus("Normal Order");
		}
		
		
		SOHDModel.setStrSuppVolt("");
		SOHDModel.setStrSysModel("");
		SOHDModel.setStrWipeRopeDia("");;
		SOHDModel.setStrWarraValidity("");
		SOHDModel.setStrWarrPeriod("");
		if(objBean.getStrCloseSO()==null)
		{
			SOHDModel.setStrCloseSO("N");
		}else
		{
			SOHDModel.setStrCloseSO(objBean.getStrCloseSO());
		}
		
		return SOHDModel;
	}
	
	
	//Assign filed function to set data onto form for edit transaction.
		@RequestMapping(value = "/SalesOrderHdData", method = RequestMethod.GET)
		public @ResponseBody clsSalesOrderBean funAssignFields(@RequestParam("soCode") String soCode,HttpServletRequest req)
		{
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String userCode = req.getSession().getAttribute("usercode").toString();
			String startDate=req.getSession().getAttribute("startDate").toString();
			String propCode=req.getSession().getAttribute("propertyCode").toString();
			clsPropertySetupModel objSetup = objSetupMasterService.funGetObjectPropertySetup(propCode, clientCode);
		  	   
			
			String[] sp=startDate.split(" ");
			String[] spDate=sp[0].split("/");
			startDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
			clsSalesOrderBean objBeanSale = new clsSalesOrderBean();
			
			List <Object> objSales=objSalesOrderService.funGetSalesOrder(soCode,clientCode);
			clsSalesOrderHdModel objSalesOrderHdModel = null;
			clsLocationMasterModel objLocationMasterModel = null;
			clsPartyMasterModel objPartyMasterModel = null;
			for(int i=0;i<objSales.size();i++)
			{
				Object[] ob = (Object[])objSales.get(i);
				objSalesOrderHdModel = (clsSalesOrderHdModel) ob[0];
				objLocationMasterModel = (clsLocationMasterModel) ob[1];
				objPartyMasterModel = (clsPartyMasterModel) ob[2];
			}
			objBeanSale=funPrepardHdBean(objSalesOrderHdModel,objLocationMasterModel,objPartyMasterModel);
			
			List<clsSalesOrderDtl> listSaleDtl = new ArrayList<clsSalesOrderDtl>();
			List<clsSalesCharModel> listSalesChar =new ArrayList<clsSalesCharModel>();
			List<Object> objSalesDtlModelList=objSalesOrderService.funGetSalesOrderDtl(soCode,clientCode);	
			for(int i=0;i<objSalesDtlModelList.size();i++)
			{
				Object[] ob = (Object[])objSalesDtlModelList.get(i);
				clsSalesOrderDtl saleDtl = (clsSalesOrderDtl) ob[0];
				clsProductMasterModel prodmast =(clsProductMasterModel) ob[1];
				
				listSalesChar=objSalesOrderService.funGetSalesChar(objBeanSale.getStrSOCode(),saleDtl.getStrProdCode());
				
				saleDtl.setDblAvgQty(funGetCustAvg( objBeanSale.getStrCustCode(), saleDtl.getStrProdCode(), objBeanSale.getDteSODate(), req));
				saleDtl.setStrProdName(prodmast.getStrProdName());
				saleDtl.setStrProdType(prodmast.getStrProdType());
				
				if(!(objSetup==null))
				{
				  if(objSetup.getStrShowAvgQtyInSO().equals("Y"))
				  {
					  double avgQty = funGetCustAvg(objBeanSale.getStrCustCode(),saleDtl.getStrProdCode(),objBeanSale.getDteSODate(),req);
						saleDtl.setDblAvgQty(avgQty);
				  }
				  if(objSetup.getStrShowStockInSO().equals("Y"))
				  {
					  String sqlStk =" select a.dblClosingStk "
				 				+ " from tblcurrentstock a where a.strLocCode='"+objBeanSale.getStrLocCode()+"' "
				 				+ " and a.strProdCode='"+saleDtl.getStrProdCode()+"' "
				 				+ " and a.strClientCode='"+clientCode+"'  ";
					 	List<BigDecimal> listSTK = objGlobalFunctionsService.funGetDataList(sqlStk,"sql");
					 	double dblClosingStk=0;
					 	if(!(listSTK.isEmpty()))
					 	{
					 		BigDecimal objStk =  listSTK.get(0);
					 		dblClosingStk=objStk.doubleValue();
					 	}
				 		saleDtl.setDblAvalaibleStk(dblClosingStk);
				  }
					
					
					
				}
				
				
				
				listSaleDtl.add(saleDtl);
			}
			if(listSalesChar!=null)
			{
				objBeanSale.setListSalesChar(listSalesChar);
			}
			objBeanSale.setListSODtl(listSaleDtl);
			return objBeanSale;
		}
		
		
		//Assign filed function to set data onto form for edit transaction.
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@RequestMapping(value = "/SalesOrderDtlData", method = RequestMethod.GET)
				public @ResponseBody List funAssignDetialsData(@RequestParam("soCode") String soCode,HttpServletRequest req)
				{
					String clientCode=req.getSession().getAttribute("clientCode").toString();
					
					List<Object> objSalesDtlModelList=objSalesOrderService.funGetSalesOrderDtl(soCode,clientCode);
					
					if(null==objSalesDtlModelList)
					{
						objSalesDtlModelList=new ArrayList();
						clsSalesOrderDtl objModelDtl = new clsSalesOrderDtl();
						objModelDtl.setStrSOCode("Invalid Code");
						objSalesDtlModelList.add(objModelDtl);
					}					
					return objSalesDtlModelList;
				}
				
				
			private clsSalesOrderBean funPrepardHdBean(clsSalesOrderHdModel objSalesOrderHdModel,clsLocationMasterModel objLocationMasterModel,clsPartyMasterModel objPartyMasterModel)
			{
				clsSalesOrderBean objBeanSale = new clsSalesOrderBean();
				objBeanSale.setStrSOCode(objSalesOrderHdModel.getStrSOCode());
				objBeanSale.setDteSODate(objSalesOrderHdModel.getDteSODate());
				objBeanSale.setDteCPODate(objSalesOrderHdModel.getDteCPODate());
				objBeanSale.setStrCustCode(objSalesOrderHdModel.getStrCustCode());
				objBeanSale.setStrLocCode(objSalesOrderHdModel.getStrLocCode());
				objBeanSale.setStrAgainst(objSalesOrderHdModel.getStrAgainst());
				objBeanSale.setStrCurrency(objSalesOrderHdModel.getStrCurrency());
				objBeanSale.setStrPayMode(objSalesOrderHdModel.getStrPayMode());
				objBeanSale.setDblDisAmt(objSalesOrderHdModel.getDblDisAmt());
				objBeanSale.setStrNarration(objSalesOrderHdModel.getStrNarration());
				objBeanSale.setStrCloseSO(objSalesOrderHdModel.getStrCloseSO());
				objBeanSale.setDblSubTotal(objSalesOrderHdModel.getDblSubTotal());
				objBeanSale.setDblDisAmt(objSalesOrderHdModel.getDblDisAmt());
				objBeanSale.setDblDisRate(objSalesOrderHdModel.getDblDisRate());
				objBeanSale.setDblExtra(objSalesOrderHdModel.getDblExtra());
				objBeanSale.setDblTotal(objSalesOrderHdModel.getDblTotal());
				objBeanSale.setStrLocName(objLocationMasterModel.getStrLocName());
				objBeanSale.setStrcustName(objPartyMasterModel.getStrPName());
				objBeanSale.setDteFulmtDate(objSalesOrderHdModel.getDteFulmtDate());
				objBeanSale.setStrCustPONo(objSalesOrderHdModel.getStrCustPONo());
				objBeanSale.setStrCode(objSalesOrderHdModel.getStrCode());
				objBeanSale.setStrCloseSO(objSalesOrderHdModel.getStrCloseSO());
				
				
				objBeanSale.setStrBAdd1(objSalesOrderHdModel.getStrBAdd1());
				objBeanSale.setStrBAdd2(objSalesOrderHdModel.getStrBAdd2());
				objBeanSale.setStrBCity(objSalesOrderHdModel.getStrBCity());
				objBeanSale.setStrBCountry(objSalesOrderHdModel.getStrBCountry());
				objBeanSale.setStrBPin(objSalesOrderHdModel.getStrBPin());
				objBeanSale.setStrBState(objSalesOrderHdModel.getStrBState());
				
				objBeanSale.setStrSAdd1(objSalesOrderHdModel.getStrSAdd1());
				objBeanSale.setStrSAdd2(objSalesOrderHdModel.getStrSAdd2());
				objBeanSale.setStrSCity(objSalesOrderHdModel.getStrSCity());
				objBeanSale.setStrSCountry(objSalesOrderHdModel.getStrSCountry());
				objBeanSale.setStrSPin(objSalesOrderHdModel.getStrSPin());
				objBeanSale.setStrSState(objSalesOrderHdModel.getStrSState());
				objBeanSale.setStrSettlementCode(objSalesOrderHdModel.getStrSettlementCode());
				return objBeanSale;
			}
			
			
			@RequestMapping(value = "/frmSalesOrderSlip", method = RequestMethod.GET)
			public ModelAndView funOpenSalesOrderSlipForm(Map<String, Object> model,
					HttpServletRequest request)
		    {
				String urlHits = "1";
				try {
					urlHits = request.getParameter("saddr").toString();
				} catch (NullPointerException e) {
					urlHits = "1";
				}
				model.put("urlHits", urlHits);

				if ("2".equalsIgnoreCase(urlHits)) {
					return new ModelAndView("frmSalesOrderSlip_1", "command",
							new clsReportBean());
				} else {
					return new ModelAndView("frmSalesOrderSlip", "command",
							new clsReportBean());
				}
		    }
			
			
			@RequestMapping(value = "/rptSalesOrderSlip", method = RequestMethod.GET)
			private void funReportSalesOrderReport(@ModelAttribute("command") clsReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
			{
				String SOCode=objBean.getStrDocCode();
				String type=objBean.getStrDocType();
//				String showBOM = objBean.getStrShowBOM();
				
				funCallReportSalesOrderReport(SOCode,type,resp,req);
			}
			
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			private void funCallReportSalesOrderReport(String SOCode,String type,HttpServletResponse resp,HttpServletRequest req)
			{
				try {
					
					String strSOCode="";
					String dteSODate="";
					String strPName="";
					String strBAdd1="";
					String strBAdd2="";
					String strBCity="";
					String strBPin="";
					String strBState="";
					String strBCountry="";
					String strNarration="";
					objGlobal=new clsGlobalFunctions();
					Connection con=objGlobal.funGetConnection(req);
		            String clientCode=req.getSession().getAttribute("clientCode").toString();
					String companyName=req.getSession().getAttribute("companyName").toString();
					String userCode=req.getSession().getAttribute("usercode").toString();
					String propertyCode=req.getSession().getAttribute("propertyCode").toString();
					clsPropertySetupModel objSetup=objSetupMasterService.funGetObjectPropertySetup(propertyCode, clientCode);
					if(objSetup==null)
					{
						objSetup=new clsPropertySetupModel();
					}
					String reportName=servletContext.getRealPath("/WEB-INF/reports/rptSalesOrderSlip.jrxml"); 
					String imagePath=servletContext.getRealPath("/resources/images/company_Logo.png");
					
					String sqlHd="select a.strSOCode, date(a.dteSODate), b.strPName ,b.strBAdd1,b.strBAdd2 ,b.strBCity,b.strBState,b.strBPin,b.strBCountry,a.strNarration " 
								+ "	from tblsalesorderhd a , tblpartymaster b " 
								+ "	where a.strSOCode = '"+SOCode+"' " 
								+ "	and a.strCustCode=b.strPCode and a.strClientCode='"+clientCode+"' and a.strClientCode=b.strClientCode ";
					List list=objGlobalFunctionsService.funGetList(sqlHd,"sql");
					if(!list.isEmpty())
					{
						Object[] arrObj=(Object[])list.get(0);
						strSOCode=arrObj[0].toString();
						dteSODate=arrObj[1].toString();
						strPName=arrObj[2].toString();
						strBAdd1=arrObj[3].toString();
						strBAdd2=arrObj[4].toString();
						strBCity=arrObj[5].toString();
						strBState=arrObj[6].toString();
						strBPin=arrObj[7].toString();
						strBCountry=arrObj[8].toString();
						strNarration=arrObj[9].toString(); 
					}
					

					
		            String sqlDtl="select a.strSOCode,b.strProdName,b.strPartNo,a.dblQty,b.strUOM,a.strRemarks "
		            	+	"	from tblsalesorderdtl a ,tblproductmaster b "
		            	+   "	where a.strSOCode='"+SOCode+"' and a.strProdCode=b.strProdCode "
		            	+	"	and a.strClientCode='"+clientCode+"' and a.strClientCode = b.strClientCode ";
		            
		            JasperDesign jd=JRXmlLoader.load(reportName);
		            JRDesignQuery subQuery = new JRDesignQuery();
		            subQuery.setText(sqlDtl);
		            Map<String, JRDataset> datasetMap = jd.getDatasetMap();
		            JRDesignDataset subDataset = (JRDesignDataset) datasetMap.get("dsSalesReportDtl");
		            subDataset.setQuery(subQuery);
		            JasperReport jr=JasperCompileManager.compileReport(jd);
		        
		          
		          HashMap hm = new HashMap();
		          hm.put("strCompanyName",companyName );
		          hm.put("strUserCode",userCode);
		          hm.put("strImagePath",imagePath );
		          
		          hm.put("strAddr1",objSetup.getStrAdd1() );
		          hm.put("strAddr2",objSetup.getStrAdd2());            
		          hm.put("strCity", objSetup.getStrCity());
		          hm.put("strState",objSetup.getStrState());
		          hm.put("strCountry", objSetup.getStrCountry());
		          hm.put("strPin", objSetup.getStrPin());
		          hm.put("strPName", strPName);
		          hm.put("strBAdd1",strBAdd1 );
		          hm.put("strBAdd2",strBAdd2);            
		          hm.put("strBCity",strBCity );
		          hm.put("strBState",strBState);
		          hm.put("strBCountry",strBCountry );
		          hm.put("strBPin",strBPin );
		          hm.put("strNarration", strNarration);
		          hm.put("strSOCode",strSOCode );
		          hm.put("dteSODate",dteSODate );
		         
		                 
		          
		          JasperPrint p=JasperFillManager.fillReport(jr, hm,con);
		          if(type.trim().equalsIgnoreCase("pdf"))
					{
		        	 ServletOutputStream servletOutputStream = resp.getOutputStream();
		             byte[] bytes = null;
		             bytes = JasperRunManager.runReportToPdf(jr,hm, con);
		                 resp.setContentType("application/pdf");
		                 resp.setContentLength(bytes.length);                     
		                 servletOutputStream.write(bytes, 0, bytes.length);
		                 servletOutputStream.flush();
		                 servletOutputStream.close();
					}
					else if(type.trim().equalsIgnoreCase("xls"))
					{
						JRExporter exporterXLS = new JRXlsExporter();	 
						exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, p);  
						exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, resp.getOutputStream() );  
			            resp.setHeader( "Content-Disposition", "attachment;filename=" + "rptSalesOrderSlip."+type.trim() );                        
			            exporterXLS.exportReport();  
			            resp.setContentType("application/xlsx");	
					}
		          
		        } catch (Exception e) {
		        e.printStackTrace();
		        }
			}	
			
		
			@RequestMapping(value = "/openRptSalesOrderSlip", method = RequestMethod.GET)	
			public void funOpenReport(HttpServletResponse resp,HttpServletRequest req)
			{
				 
					String SOCode=req.getParameter("rptSOCode").toString();
					req.getSession().removeAttribute("rptSOCode");			 
					String type="pdf";
					funCallReportSalesOrderReport(SOCode,type,resp,req);
		  
			}	
			
			
			
			@RequestMapping(value = "/frmSalesOrderList", method = RequestMethod.GET)
			public ModelAndView funSalesOrderListForm(Map<String, Object> model,
					HttpServletRequest request)
		    {
				String urlHits = "1";
				try {
					urlHits = request.getParameter("saddr").toString();
				} catch (NullPointerException e) {
					urlHits = "1";
				}
				model.put("urlHits", urlHits);

				if ("2".equalsIgnoreCase(urlHits)) {
					return new ModelAndView("frmSalesOrderList_1", "command",
							new clsReportBean());
				} else {
					return new ModelAndView("frmSalesOrderList", "command",
							new clsReportBean());
				}
		    }
			

			@RequestMapping(value = "/rptSalesOrderList", method = RequestMethod.GET)
			private void funDeliveryNoteReport(@ModelAttribute("command") clsReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
			{
				String custCode=objBean.getStrDocCode();
				String type=objBean.getStrDocType();
				String fromDate=objBean.getDtFromDate();
				String toDate=objBean.getDtToDate();
				String fromFulfillment=objBean.getDteFromFulfillment();
				String toFulfillment=objBean.getDteToFulfillment();
				String reportType=objBean.getStrReportType();
				funSalesOrderListReport(custCode,type,fromDate,toDate,fromFulfillment,toFulfillment,reportType,resp,req);
			}
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			private void funSalesOrderListReport(String custCode,String type,String fromDate,String toDate,String fromFulfillment,String toFulfillment,String reportType,HttpServletResponse resp,HttpServletRequest req )
			{
				
				
				try{
					String dteSODate="";
					String dteFulmtDate="";
					String strPName="";
					
					objGlobal=new clsGlobalFunctions();
					Connection con=objGlobal.funGetConnection(req);
					String clientCode=req.getSession().getAttribute("clientCode").toString();
					String companyName=req.getSession().getAttribute("companyName").toString();
					String userCode=req.getSession().getAttribute("usercode").toString();
					String propertyCode=req.getSession().getAttribute("propertyCode").toString();
					clsPropertySetupModel objSetup=objSetupMasterService.funGetObjectPropertySetup(propertyCode, clientCode);
					if(objSetup==null)
					{
						objSetup=new clsPropertySetupModel();
					}
					String reportName;
					if(reportType.equals("Summary"))
					{
					 reportName=servletContext.getRealPath("/WEB-INF/reports/rptSalesOrderListSummary.jrxml");
					}
					else
					{
					 reportName=servletContext.getRealPath("/WEB-INF/reports/rptSalesOrderListDetail.jrxml");
						
					}
					String imagePath=servletContext.getRealPath("/resources/images/company_Logo.png");
					
					
					String sqlHd="select Date(a.dteSODate),Date(a.dteFulmtDate),b.strPName from tblsalesorderhd a, tblpartymaster b  "
							    +"where a.strCustCode='"+custCode+"' and a.strClientCode='"+clientCode+"' and a.strClientCode=b.strClientCode and a.strCustCode=b.strPCode "
							    +"and a.dteSODate between '"+fromDate+"' and '"+toDate+"' and a.dteFulmtDate between '"+fromFulfillment+"' and '"+toFulfillment+"' ";
					
				
					
					List list=objGlobalFunctionsService.funGetList(sqlHd,"sql");
					if(!list.isEmpty())
					{
						Object[] arrObj=(Object[])list.get(0);
						dteSODate=arrObj[0].toString();
						dteFulmtDate=arrObj[1].toString();
						strPName=arrObj[2].toString();
						

					}
					 String sqlDtl;
					if(reportType.equals("Summary"))
					{
					  sqlDtl="select Date(a.dteSODate),Date(a.dteFulmtDate),b.strPName,a.strSOCode,a.strCustCode, "
                            +"b.strPName,a.strCustPONo,a.dteFulmtDate,a.strBAdd1,a.strBAdd2,a.strBCity,a.strBState,a.strBCountry, "
                            +"a.strSAdd1,a.strSAdd2,a.strSCity,a.strSState,a.strSCountry,a.dblTaxAmt,a.dblTotal "
                            +"from tblsalesorderhd a, tblpartymaster b   "
                            +"where a.strCustCode='"+custCode+"' and a.strClientCode='"+clientCode+"' " 
                            +"and a.strClientCode=b.strClientCode and a.strCustCode=b.strPCode "
                            +"and a.dteSODate between '"+fromDate+"' and '"+toDate+"' "
                            + "and a.dteFulmtDate between '"+fromFulfillment+"' and '"+toFulfillment+"' ";
					}
					else{
					 sqlDtl="select a.strSOCode,a.strProdCode ,b.strProdName,a.dblQty,a.strRemarks "
                           +" from tblsalesorderdtl a,tblproductmaster b,tblsalesorderhd c  "
                           +" where c.strCustCode='"+custCode+"' and a.strSOCode=c.strSOCode  and  c.strClientCode='"+clientCode+"'  "
                           +" and c.strClientCode=b.strClientCode and a.strProdCode=b.strProdCode "
                           +" and c.dteSODate between '"+fromDate+"' and '"+toDate+"' and  "
                           +" c.dteFulmtDate between '"+fromFulfillment+"' and '"+toFulfillment+"' ";	
						
						
					}
				  JasperDesign jd=JRXmlLoader.load(reportName);
				  JRDesignQuery subQuery = new JRDesignQuery();
				  subQuery.setText(sqlDtl);
				  Map<String, JRDataset> datasetMap = jd.getDatasetMap();
				  JRDesignDataset subDataset;
				  if(reportType.equals("Summary"))
				  {
				  subDataset = (JRDesignDataset) datasetMap.get("dsSalesOrderListSummary");
				  }
				  else{
					  subDataset = (JRDesignDataset) datasetMap.get("dsSalesOrderListDetail");
				  }
				  subDataset.setQuery(subQuery);
		          JasperReport jr=JasperCompileManager.compileReport(jd);
		          HashMap hm=new HashMap();
		          hm.put("strCompanyName",companyName );
		          hm.put("strUserCode",userCode);
		          hm.put("strImagePath",imagePath );
		          hm.put("strAddr1",objSetup.getStrAdd1() );
		          hm.put("strAddr2",objSetup.getStrAdd2());            
		          hm.put("strCity", objSetup.getStrCity());
		          hm.put("strState",objSetup.getStrState());
		          hm.put("strCountry", objSetup.getStrCountry());
		          hm.put("strPin", objSetup.getStrPin());
		          hm.put("strPin", objSetup.getStrPin());
		          hm.put("dteSODate", dteSODate);
		          hm.put("dteFulmtDate", dteFulmtDate);
		          hm.put("strPName", strPName);
		          hm.put("fromDate", fromDate);
		          hm.put("toDate", toDate);
		          
		          
		          JasperPrint p=JasperFillManager.fillReport(jr, hm,con);
		          if(type.trim().equalsIgnoreCase("pdf"))
					{
		      	 ServletOutputStream servletOutputStream = resp.getOutputStream();
		           byte[] bytes = null;
		           bytes = JasperRunManager.runReportToPdf(jr,hm, con);
		               resp.setContentType("application/pdf");
		               resp.setContentLength(bytes.length);                     
		               servletOutputStream.write(bytes, 0, bytes.length);
		               servletOutputStream.flush();
		               servletOutputStream.close();
					}
					else if(type.trim().equalsIgnoreCase("xls"))
					{
						JRExporter exporterXLS = new JRXlsExporter();	 
						exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, p);  
						exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, resp.getOutputStream() );  
			            resp.setHeader( "Content-Disposition", "attachment;filename=" + "rptSalesOrderList."+type.trim() );                        
			            exporterXLS.exportReport();  
			            resp.setContentType("application/xlsx");	
					}
				  
				}
				catch(Exception e){
					
					e.printStackTrace();
					
					
					
				}
			}
			
	
			@SuppressWarnings("rawtypes")
			private boolean funPrepardBOMData(clsSalesOrderHdModel SOModel,clsSalesOrderDtl obSODtl ,String clientCode,String userCode )
			{
				
				mapSOBOMcls=new HashMap<String,ArrayList<clsInnerSalesBom>> ();
				
				String bomCode="";
				//clsSalesOrderBOMModel SOBOMModel = new clsSalesOrderBOMModel();
				double soQty = obSODtl.getDblQty();
				double soWt = obSODtl.getDblWeight();
				String strSOCode = SOModel.getStrSOCode();
			
				// delete of existing salesOrder of Bom
				objSOBOMService.funDeleteSalesOrderBom(strSOCode,obSODtl.getStrProdCode(),clientCode);
				funGetChild(soQty, soWt, obSODtl.getStrProdCode(), clientCode);
				
				// itearate map and its inner class form saveing
				for (Map.Entry<String,ArrayList<clsInnerSalesBom>> entry : mapSOBOMcls.entrySet()) {
					//System.out.println(entry.getKey());
					/* from getting process Code */
					List listProdObject =objProductMasterService.funGetProdProcessList(obSODtl.getStrProdCode(), clientCode);
					String strProdProcess ="";
					if(listProdObject.size()!=0)
					{
						Object[] ob = (Object[])listProdObject.get(0);
						clsProdProcessModel objProdProcess=(clsProdProcessModel)ob[0];
						strProdProcess = objProdProcess.getStrProcessCode();
					}
					/////////////////////////////////////
					
					
					if(mapSOBOMcls.containsKey(entry.getKey()))
					{
						List<clsInnerSalesBom> listSOBOM=entry.getValue();
						long i=0;
						for(clsInnerSalesBom objInn :listSOBOM)
						{
							/* from getting process Code */
							List listParentProdObject =objProductMasterService.funGetProdProcessList(entry.getKey(), clientCode);
							String strParentProdProcess ="";
							if(listParentProdObject.size()!=0)
							{
							Object[] ob = (Object[])listParentProdObject.get(0);
							clsProdProcessModel objParentProdProcess=(clsProdProcessModel)ob[0];
							strParentProdProcess = objParentProdProcess.getStrProcessCode();
							}
							/////////////////////////////////////
							
							clsSalesOrderBOMModel SOBOMModel = new clsSalesOrderBOMModel();
							
							SOBOMModel.setStrSOCode(strSOCode);
							SOBOMModel.setStrProdCode(obSODtl.getStrProdCode());
							SOBOMModel.setStrProcessCode(strProdProcess);
							SOBOMModel.setStrParentCode(entry.getKey());
							SOBOMModel.setStrParentProcessCode(strParentProdProcess);
							SOBOMModel.setStrChildCode(objInn.getProdCode());
							SOBOMModel.setDblQty(objInn.getQty());
							SOBOMModel.setDblWeight(objInn.getQty()*objInn.getWt());
							SOBOMModel.setIntindex(new Long(i));
							SOBOMModel.setStrRemarks(obSODtl.getStrRemarks());
							SOBOMModel.setStrUserCreated(userCode);
							SOBOMModel.setStrUserModified(userCode);
							SOBOMModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
							SOBOMModel.setDteLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
							SOBOMModel.setStrClientCode(clientCode);
							i++;
							objSOBOMService.funAddUpdateSoBomHd(SOBOMModel);
							
							
							//System.out.println("\t"+objInn.getProdCode()+"\t"+objInn.getQty()+"\t"+objInn.getWt());
						}
//						
					}
					
				}
				if(mapSOBOMcls.isEmpty())
				{
					long i=0;
					List listParentProdObject =objProductMasterService.funGetProdProcessList(obSODtl.getStrProdCode(), clientCode);
					String strParentProdProcess ="";
					if(listParentProdObject.size()!=0)
					{
					Object[] ob = (Object[])listParentProdObject.get(0);
					clsProdProcessModel objParentProdProcess=(clsProdProcessModel)ob[0];
					strParentProdProcess = objParentProdProcess.getStrProcessCode();
					}
					/////////////////////////////////////
					
					clsSalesOrderBOMModel SOBOMModel = new clsSalesOrderBOMModel();
					
					SOBOMModel.setStrSOCode(strSOCode);
					SOBOMModel.setStrProdCode(obSODtl.getStrProdCode());
					SOBOMModel.setStrProcessCode(strParentProdProcess);
					SOBOMModel.setStrParentCode(obSODtl.getStrProdCode());
					SOBOMModel.setStrParentProcessCode(strParentProdProcess);
					SOBOMModel.setStrChildCode(obSODtl.getStrProdCode());
					SOBOMModel.setDblQty(obSODtl.getDblQty());
					SOBOMModel.setDblWeight(obSODtl.getDblQty()*obSODtl.getDblWeight());
					SOBOMModel.setIntindex(new Long(i));
					SOBOMModel.setStrRemarks(obSODtl.getStrRemarks());
					SOBOMModel.setStrUserCreated(userCode);
					SOBOMModel.setStrUserModified(userCode);
					SOBOMModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
					SOBOMModel.setDteLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
					SOBOMModel.setStrClientCode(clientCode);
					objSOBOMService.funAddUpdateSoBomHd(SOBOMModel);
					
				}
				

				return false;
			 }
			
			
			// for putting in map each product details have child or not
			@SuppressWarnings({ "unchecked", "unused" })
			private int funGetChild(double soQty,double soWt,String ProdCode,String clientCode)
			{
				int retVal=0;
				List listBomCode=objRecipeMasterService.funGetBOMCode(ProdCode, clientCode);
				if(listBomCode.size()!=0)
				{
					clsBomHdModel objBomModel= (clsBomHdModel) listBomCode.get(0);
					
					List listBOMDtl=objRecipeMasterService.funGetBOMDtl(clientCode,objBomModel.getStrBOMCode());
					
					ArrayList arrListChildNodes=new ArrayList<String>();
					ArrayList objInnerSalesBom = new ArrayList<clsInnerSalesBom>();;
					for(int i=0;i<listBOMDtl.size();i++)
					{
						retVal=1;
						
						clsBomDtlModel objBOMDtl=(clsBomDtlModel) listBOMDtl.get(i);
						
						clsInnerSalesBom objSalesOrderBom = new clsInnerSalesBom();
						String childProdCode=objBOMDtl.getStrChildCode();
						double childProdQty=objBOMDtl.getDblQty();
						double childProdWeight=objBOMDtl.getDblWeight();
						objSalesOrderBom.setProdCode(childProdCode);
						objSalesOrderBom.setQty(1);
						objSalesOrderBom.setWt(childProdWeight);
						
						objInnerSalesBom.add(objSalesOrderBom);
						arrListChildNodes.add(childProdCode);
							
						funGetChild(childProdQty, childProdWeight, childProdCode,clientCode);
						
					}
					mapSOBOMcls.put(ProdCode,objInnerSalesBom);
				
				}
				return retVal;
			}
			
			private class clsInnerSalesBom
			{
				private String ProdCode;
				
				private double Qty;
				
				private double Wt;

				public String getProdCode() {
					return ProdCode;
				}

				public void setProdCode(String prodCode) {
					ProdCode = prodCode;
				}

				public double getQty() {
					return Qty;
				}

				public void setQty(double qty) {
					Qty = qty;
				}

				public double getWt() {
					return Wt;
				}

				public void setWt(double wt) {
					Wt = wt;
				}
				
			}
			
			
			
			
			
			@RequestMapping(value = "/frmSalesProdChar", method = RequestMethod.GET)
			public ModelAndView funOpenSalesChar(Map<String, Object> model,HttpServletRequest req)
			{
				String urlHits="1";
				String prodCode=req.getParameter("prodCode").toString();
				String clientCode=req.getSession().getAttribute("clientCode").toString();
				try{
					urlHits=req.getParameter("saddr").toString();
				}catch(NullPointerException e){
					urlHits="1";
				}
				@SuppressWarnings("rawtypes")
				List listProdChar= objGlobalFunctionsService.funGetList("select a.strCharCode,b.strCharName from tblprodchar a,tblcharacteristics b where a.strCharCode=b.strCharCode and a.strProdCode='"+prodCode+"' and a.strClientCode='"+clientCode+"'");
				ArrayList <clsProdCharMasterModel> list =new ArrayList<clsProdCharMasterModel>();
				for (int count=0;count<listProdChar.size();count++) {
					clsProdCharMasterModel objModel= new clsProdCharMasterModel();
					Object[] obj = (Object[]) listProdChar.get(count);
					objModel.setStrProdCode(prodCode);
					objModel.setStrCharCode(obj[0].toString());
					objModel.setStrCharName(obj[1].toString());
//					objModel.setStrSpecf(obj[1].toString());
					list.add(objModel);
				}
				model.put("listProdCharData",list);
				
				if("2".equalsIgnoreCase(urlHits)){
					return new ModelAndView("frmTransectionProdChar","command", new clsTransectionProdCharBean());
				}else if("1".equalsIgnoreCase(urlHits)){
					return new ModelAndView("frmTransectionProdChar","command", new clsTransectionProdCharBean());	
				}else {
					return null;
				}
				 
			}
			
			
			
			 @SuppressWarnings("rawtypes")
				@RequestMapping(value = "/charTransectionData", method = RequestMethod.POST,headers = {"Content-type=application/json"})
				public @ResponseBody String funLoadCharBean(@RequestBody Object obj, HttpServletRequest request)
				{
				 	@SuppressWarnings("unused")
					List<clsTransectionProdCharModel> listTransProdChar =new ArrayList<clsTransectionProdCharModel>();
				 
				 
				 return null;
				}
			
	
	private String funBeforeSevenDayDate(String dte,String pattern)
	{
		int day = 0;
		int month = 0;
		int year = 0;
		if(pattern.equals("yyyy-mm-dd"))
		{
			String[] date =dte.split("-");
			 day = Integer.parseInt(date[2].toString());
			 month = Integer.parseInt(date[1].toString());
			 year = Integer.parseInt(date[0].toString());
		}else
		{
			String[] date =dte.split("-");
			 day = Integer.parseInt(date[0].toString());
			 month = Integer.parseInt(date[1].toString());
			 year = Integer.parseInt(date[2].toString());
		}
		
		int day1=0;
		int day2=0;
		int day3=0;
		int day4=0;
		
		int month1=0;
		int month2=0;
		int month3=0;
		int month4=0;
		
		int year1=0;
		int year2=0;
		int year3=0;
		int year4=0;
		

		day1=day-7;
		month1=month;
		year1=year;
		if(day1<0)
		{
			day1=30-(-day1);
			
			month1 =month1-1;
			if(month1==0)
			{
				month1=12;
				year1=year1-1;
			}
			
		}
		if(day1==0)
		{
			day1=30;
			
		}

		//System.out.println("Day1="+day1+"-"+month1+"-"+year1);
		String date1=year1+"-"+month1+"-"+day1;
		day2=day1-7;
		month2=month1;
		year2=year1;
		if(day2<0)
		{
			day2=30-(-day2);
			month2 =month2-1;
			if(month2==0)
			{
				month2=12;
				year2=year2-1;
			}
			
		}
		if(day2==0)
		{
			day2=30;
			
		}
		//System.out.println("Day2="+day2+"-"+month2+"-"+year2);
		String date2=year2+"-"+month2+"-"+day2;
		
		day3=day2-7;
		month3=month2;
		year3=year2;
		if(day3<0)
		{
			day3=30-(-day3);
			month3=month3-1;
			
			if(month3==0)
			{
				month3=12;
				year3=year3-1;
			}
			
			
		}
		if(day3==0)
		{
			day3=30;
			
		}
		//System.out.println("Day3="+day3+"-"+month3+"-"+year3);
		String date3=year3+"-"+month3+"-"+day3;
		
		day4=day3-7;
		month4=month3;
		year4=year3;
		if(day4<0)
		{
			day4=30-(-day4);
			month4 =month4-1;
			if(month4==0)
			{
				month4=12;
				year4=year4-1;
			}
		}
		if(day4==0)
		{
			day4=30;
			
		}
		//System.out.println("Day4="+day4+"-"+month4+"-"+year4);
		String date4=year4+"-"+month4+"-"+day4;

		return date1+"#"+date2+"#"+date3+"#"+date4;
	}
	
	//@RequestMapping(value = "/SalesOrderCustAvg", method = RequestMethod.GET)
	//public @ResponseBody double funGetCustAvg(@RequestParam("custCode") String custCode,@RequestParam("prodCode") String prodCode,@RequestParam("dteSODate") String dteSODate,HttpServletRequest req)
	
	public  double funGetCustAvg(String custCode,String prodCode,String dteSODate,HttpServletRequest req)
	{
		double retAvgValue=0.00;
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String invoDates = funBeforeSevenDayDate(dteSODate,"yyyy-mm-dd");
		String invoDate[] = invoDates.split("#");
		//According to Innvoce avg		
//		String sqlAvgCustProd= " select sum(b.dblQty)/4 from tblinvoicehd a ,tblinvoicedtl b "
//				+ " where a.strCustCode='"+custCode+"' "
//				+ " and b.strProdCode='"+prodCode+"' "
//				+ " and a.strInvCode=b.strInvCode "
//				+ " and (date(a.dteInvDate) ='"+invoDate[0]+"' "
//				+ " or date(a.dteInvDate)='"+invoDate[1]+"' "
//				+ " or date(a.dteInvDate)= '"+invoDate[2]+"' "
//				+ " or date(a.dteInvDate)= '"+invoDate[3]+"' )"
//				+ " and a.strClientCode='"+clientCode+"' "
//				+ " and b.strClientCode='"+clientCode+"' ; ";
		
	//According to sales order avg
		String sqlAvgCustProd= " select sum(b.dblQty)/4 from tblsalesorderhd a ,tblsalesorderdtl b  "
				+ " where  a.strSOCode=b.strSOCode " 
				+ "  and a.strCustCode='"+custCode+"' "  
				+ "  and b.strProdCode='"+prodCode+"' "
				+ " and (date(a.dteSODate) ='"+invoDate[0]+"' "
				+ " or date(a.dteSODate)='"+invoDate[1]+"' "
				+ " or date(a.dteSODate)= '"+invoDate[2]+"' "
				+ " or date(a.dteSODate)= '"+invoDate[3]+"' )"
				+ " and a.strClientCode='"+clientCode+"' "
				+ " and b.strClientCode='"+clientCode+"' ; ";
		
		
		
		List list=objGlobalFunctionsService.funGetList(sqlAvgCustProd,"sql");
		
		if(list.get(0)!=null)
		{
			retAvgValue=Double.parseDouble(list.get(0).toString());
		}
		
		System.out.println("retvalue===="+retAvgValue);
		return retAvgValue;
	}
	
	@RequestMapping(value = "/SalesOrderCustAvg", method = RequestMethod.GET)
	public @ResponseBody String funGetCustAvgAddRow(@RequestParam("custCode") String custCode,@RequestParam("prodCode") String prodCode,@RequestParam("dteSODate") String dteSODate,HttpServletRequest req)
	{	
		String retVal="NV";
		double retAvgValue=0.00;
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String userCode=req.getSession().getAttribute("usercode").toString();
		String propCode=req.getSession().getAttribute("propertyCode").toString();
		clsPropertySetupModel objSetup = objSetupMasterService.funGetObjectPropertySetup(propCode, clientCode);
		if(objSetup.getStrShowAvgQtyInSO().equals("Y"))
		{
			String invoDates = funBeforeSevenDayDate(dteSODate,"yyyy-mm-dd");
			String invoDate[] = invoDates.split("#");
			//According to Innvoce avg		
	//		String sqlAvgCustProd= " select sum(b.dblQty)/4 from tblinvoicehd a ,tblinvoicedtl b "
	//				+ " where a.strCustCode='"+custCode+"' "
	//				+ " and b.strProdCode='"+prodCode+"' "
	//				+ " and a.strInvCode=b.strInvCode "
	//				+ " and (date(a.dteInvDate) ='"+invoDate[0]+"' "
	//				+ " or date(a.dteInvDate)='"+invoDate[1]+"' "
	//				+ " or date(a.dteInvDate)= '"+invoDate[2]+"' "
	//				+ " or date(a.dteInvDate)= '"+invoDate[3]+"' )"
	//				+ " and a.strClientCode='"+clientCode+"' "
	//				+ " and b.strClientCode='"+clientCode+"' ; ";
		
		//According to sales order avg
			String sqlAvgCustProd= " select sum(b.dblQty)/4 from tblsalesorderhd a ,tblsalesorderdtl b  "
					+ " where  a.strSOCode=b.strSOCode " 
					+ "  and a.strCustCode='"+custCode+"' "  
					+ "  and b.strProdCode='"+prodCode+"' "
					+ " and (date(a.dteSODate) ='"+invoDate[0]+"' "
					+ " or date(a.dteSODate)='"+invoDate[1]+"' "
					+ " or date(a.dteSODate)= '"+invoDate[2]+"' "
					+ " or date(a.dteSODate)= '"+invoDate[3]+"' )"
					+ " and a.strClientCode='"+clientCode+"' "
					+ " and b.strClientCode='"+clientCode+"' ; ";
			List list=objGlobalFunctionsService.funGetList(sqlAvgCustProd,"sql");
			
			if(list.get(0)!=null)
			{
				retAvgValue=Double.parseDouble(list.get(0).toString());
				retVal=Double.toString(retAvgValue);
			}
		
		}
		
		System.out.println("retvalue===="+retVal);
		return retVal;
	}
	
	@RequestMapping(value = "/frmLoadMultiCustomer", method = RequestMethod.GET)
	public ModelAndView funOpenPOforMR(Map<String,Object> model,HttpServletRequest req){	
		return new ModelAndView("frmLoadMultiCustomer","command",new clsReportBean());
	}
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/loadCustomerDetail", method = RequestMethod.GET)
	public @ResponseBody List funLoadCustomerDetail(HttpServletRequest request)
	{		
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		List custHelpList=objPartyMasterService.funGetListCustomer(clientCode);
		return custHelpList;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/populateAllProductDataOfAllCustomer", method = RequestMethod.GET)
	public @ResponseBody List funPopulateAllProductDataOfAllCustomerl(@RequestParam("custCodes") String custCodes,
			@RequestParam("stdOrder") String strStdOrder,
			@RequestParam("dteSODate") String dteSODate,
			@RequestParam("locCode") String locCode,
			HttpServletRequest req)
	{	
		
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String propertyCode=req.getSession().getAttribute("propertyCode").toString();
		ListIterator<clsProductMasterModel> litr = null;
		
		
		
		String startDate=req.getSession().getAttribute("startDate").toString();
		String[] sp=startDate.split(" ");
		String[] spDate=sp[0].split("/");
		startDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
	
		String userCode=req.getSession().getAttribute("usercode").toString();
//		String locCode=req.getParameter("locCode").toString();
		String strEndDate=objGlobal.funGetCurrentDate("yyyy-MM-dd");
		if(req.getParameter("strMISDate")!=null)
		{
			strEndDate=req.getParameter("strMISDate").toString();
			strEndDate=objGlobal.funGetDate("yyyy-MM-dd",strEndDate);
		}
		clsPropertySetupModel objSetup=objSetupMasterService.funGetObjectPropertySetup(propertyCode, clientCode);
	
		List<clsProductMasterModel> prodList=objProductMasterService.funGetALLProducedlProduct(clientCode);
	  	    String[] tempstrCustCodes = custCodes.split(",");
	  	    String strCustCode = "(";
	  	    for (int i = 0; i < tempstrCustCodes.length; i++)
	  	    {
	  		if (i == 0)
	  		{
	  			strCustCode = strCustCode + "'" + tempstrCustCodes[i] + "'";
	  		}
	  		else
	  		{
	  			strCustCode = strCustCode + ",'" + tempstrCustCodes[i] + "'";
	  		}
	  	    }
	  	  strCustCode += ")";
		 
		
				 
		if(strStdOrder.equals("Y"))   // for both single and mutiple customer consolidate Standard vaule of qty
		{
			
				String sqlSTDProdQty= " select a.strProdCode,sum(a.dblStandardValue) from tblprodsuppmaster a where a.strSuppCode IN  "+strCustCode+"  "
								+ " and a.strClientCode='"+clientCode+"' "
								+ " group BY a.strProdCode ";
				List listStd=objGlobalFunctionsService.funGetList(sqlSTDProdQty,"sql");
				
				if(null!=listStd)
				{
					for(int i=0;i<listStd.size();i++)
					{
						double value=0.00;
						Object[] obj = (Object[]) listStd.get(i);
						String pCode=obj[0].toString();
						double dblOverQty=Double.parseDouble(obj[1].toString());
						
						 litr=prodList.listIterator();
						 while(litr.hasNext())
						 {
							 clsProductMasterModel objLtIT=litr.next();
							 if(objLtIT.getStrProdCode().equals(pCode))
									{
								 		objLtIT.setDblOverQty(dblOverQty);
								 		
									
								 		if(objSetup.getStrShowStockInSO()!=null && objSetup.getStrShowStockInSO().equals("Y") )
								 		{
								 			double dblStk=objGlobal.funGetCurrentStockForProduct(pCode, locCode, clientCode, userCode, startDate, dteSODate);
								 			objLtIT.setDblStock(dblStk);
								 		}
								 		litr.set(objLtIT);
								 		
									}
						 }
						 
					}
				}
				
		}else  // for both single and mutiple customer consolidate Avg qty   and if you are not select any customer then show all product 
		{
			for(String cCode : tempstrCustCodes)
			{
				
				 litr=prodList.listIterator();
				 while(litr.hasNext())
				 {
					 clsProductMasterModel objLtIT=litr.next();
					 String pCode=objLtIT.getStrProdCode();
					 
					 double avgValue= Double.parseDouble(funGetCustAvgAddRow(cCode,pCode,dteSODate,req));
					 avgValue=Math.round(avgValue);
					 Double dAvg=new Double(avgValue);
					 int intValue =dAvg.intValue();
					 avgValue =(double) intValue ;
					 objLtIT.setDblAcceptQty(objLtIT.getDblAcceptQty()+avgValue);
					 if(objSetup.getStrShowStockInSO()!=null && objSetup.getStrShowStockInSO().equals("Y"))
				 		{
						 double dblStk=objGlobal.funGetCurrentStockForProduct(pCode, locCode, clientCode, userCode, startDate, dteSODate);
						 objLtIT.setDblStock(dblStk);
				 		}
					
					 litr.set(objLtIT);
				 }
				
			}
		}
		
		return prodList;
		
	}
	
	
	@RequestMapping(value = "/getdbProdStk", method = RequestMethod.GET)
	public @ResponseBody String funGetProdStk(@RequestParam("prodCode") String prodCode,@RequestParam("locCode") String locCode,HttpServletRequest req)
	{	double dblClosingStk=0.00;
		String val="0";
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String userCode=req.getSession().getAttribute("usercode").toString();
		String propCode=req.getSession().getAttribute("propertyCode").toString();
		clsPropertySetupModel objSetup = objSetupMasterService.funGetObjectPropertySetup(propCode, clientCode);
		if(objSetup.getStrShowStockInSO().equals("Y"))
		{
			String sqlStk =" select a.dblClosingStk "
	 				+ " from tblcurrentstock a where a.strLocCode='"+locCode+"' "
	 				+ " and a.strProdCode='"+prodCode+"' "
	 				+ " and a.strClientCode='"+clientCode+"'  ";
		 	List<BigDecimal> listSTK = objGlobalFunctionsService.funGetDataList(sqlStk,"sql");
		 	BigDecimal objStk =  listSTK.get(0);
	 		dblClosingStk=objStk.doubleValue();
	 		val=Double.toString(dblClosingStk);
		}
		
		System.out.println("retvalueStock===="+val);
		return val;
	}
	
	
	
	//Assign filed function to set data onto form for edit transaction.
			@RequestMapping(value = "/loadAutoSalesOrderForPI", method = RequestMethod.GET)
			public @ResponseBody List funLoadAutoSalesOrderForPI(@RequestParam("soCode") String soCode,HttpServletRequest req)
			{
				String clientCode=req.getSession().getAttribute("clientCode").toString();
				String userCode = req.getSession().getAttribute("usercode").toString();
				String startDate=req.getSession().getAttribute("startDate").toString();
				String propCode=req.getSession().getAttribute("propertyCode").toString();
				clsPropertySetupModel objSetup = objSetupMasterService.funGetObjectPropertySetup(propCode, clientCode);
				List<clsSalesOrderBOMModel> listsoBOMModel =new ArrayList<clsSalesOrderBOMModel>();
				List<clsPurchaseIndentDtlModel>  listPIModel =new ArrayList<clsPurchaseIndentDtlModel>();
				List listModel = objSoBomService.funGetListOfSOBom(soCode, clientCode);
				List<Object> objSalesDtlModelList=objSalesOrderService.funGetSalesOrderDtl(soCode,clientCode);	
				if(listModel.size()>0)
				{
					
					for(Object obj :listModel)
					{
						Object[] objArr = (Object[]) obj;
						
						for(Object objSO :objSalesDtlModelList)
						{
							Object[] objArrSO = (Object[]) objSO;
							clsSalesOrderDtl soDtl=(clsSalesOrderDtl) objArrSO[0];
							
							clsSalesOrderBOMModel soBom=(clsSalesOrderBOMModel)objArr[0];
							clsProductMasterModel prodModel=(clsProductMasterModel) objArr[1];
							clsPurchaseIndentDtlModel objPI = new clsPurchaseIndentDtlModel();
							if(soDtl.getStrProdCode().equals(soBom.getStrParentCode()))
							{
								objPI.setDblQty(soBom.getDblQty()*soDtl.getDblAcceptQty());
								objPI.setDblRate(prodModel.getDblCostRM());
								objPI.setStrProdCode(soBom.getStrChildCode());
								objPI.setStrProdName(prodModel.getStrProdName());
								objPI.setStrChecked("false");
								objPI.setStrDocCode(soCode);
								objPI.setStrDocType("Sales Order");
								objPI.setDtReqDate("");
								listPIModel.add(objPI);

								/*
								soBom.setStrProdName(prodModel.getStrProdName());
								soBom.setDblRate(prodModel.getDblCostRM());
								soBom.setStrChecked("false");
								listsoBOMModel.add(soBom);*/
							}
							
						}
						
					}
					
				}
				req.getSession().setAttribute("AutoPIData", listPIModel);
				
				return listPIModel;
			}
						 
			
			
}
