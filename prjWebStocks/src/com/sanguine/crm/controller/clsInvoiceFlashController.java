package com.sanguine.crm.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ibm.icu.math.BigDecimal;
import com.sanguine.bean.clsStockFlashBean;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.crm.bean.clsInvoiceBean;
import com.sanguine.crm.model.clsInvoiceHdModel;
import com.sanguine.crm.service.clsCRMSettlementMasterService;
import com.sanguine.crm.service.clsInvoiceHdService;
import com.sanguine.model.clsLocationMasterModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsLocationMasterService;

@Controller
public class clsInvoiceFlashController {
	
	@Autowired
	private clsInvoiceHdService objInvoiceHdService;
	
	@Autowired
	private clsGlobalFunctionsService objGlobalService;
	@Autowired
	private clsLocationMasterService objLocationMasterService;
	
	@Autowired
	private clsCRMSettlementMasterService objSettlementService;
	
	@RequestMapping(value = "/frmInvoiceFlash", method = RequestMethod.GET)
	public ModelAndView funInvoice(@ModelAttribute("command") clsInvoiceBean objBean,BindingResult result,HttpServletRequest req,Map<String,Object> model)
	{
		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		return funGetModelAndView(req);
	}
	
	
	@RequestMapping(value = "/loadInvoiceFlash", method = RequestMethod.GET)
	public @ResponseBody List funLoadInvoiceFlash(@RequestParam("settlementcode") String settlementcode,HttpServletRequest request)
	{
		String fromDate=request.getParameter("frmDte").toString();
		String toDate=request.getParameter("toDte").toString();
		String locCode=request.getParameter("locCode").toString();
		BigDecimal dblTotalValue=new BigDecimal(0);
		BigDecimal dblSubTotalValue=new BigDecimal(0);
		BigDecimal dblTaxTotalValue=new BigDecimal(0);
		DecimalFormat df = new DecimalFormat("#.##");
		
		List<clsInvoiceBean> listofInvFlash=new ArrayList<clsInvoiceBean>();
		List listofInvoiveTotal=new ArrayList<>();
		String strClientCode=request.getSession().getAttribute("clientCode").toString();
		String sqlInvoiceFlash="select a.strInvCode ,DATE_FORMAT(a.dteInvDate,'%d-%m-%Y'),b.strPName,a.strAgainst,a.strVehNo,a.dblSubTotalAmt,a.dblTaxAmt,a.dblTotalAmt,a.strExciseable  from tblinvoicehd a ,tblpartymaster b ,tblsettlementmaster c where   date(a.dteInvDate) between '"+fromDate+"' and '"+toDate+"' "  
                              +" and a.strLocCode='"+locCode+"' and a.strCustCode=b.strPCode and  a.strClientCode='"+strClientCode+"'" ;
							 if(!settlementcode.equals("All"))
					         {
                              sqlInvoiceFlash=sqlInvoiceFlash+ " and  a.strSettlementCode='"+settlementcode+"' "; 
					         }
		
							 sqlInvoiceFlash=sqlInvoiceFlash+ "and a.strSettlementCode=c.strSettlementCode ";
        List listOfInvoice=objGlobalService.funGetList(sqlInvoiceFlash, "sql");
	
		if(!listOfInvoice.isEmpty())
		{
		for(int i=0;i<listOfInvoice.size();i++)
		{
			Object[] objInvoice=(Object[] )listOfInvoice.get(i);	
			clsInvoiceBean objBean=new clsInvoiceBean();
			objBean.setStrInvCode(objInvoice[0].toString());
			objBean.setDteInvDate(objInvoice[1].toString());
			objBean.setStrCustName(objInvoice[2].toString());
			objBean.setStrAgainst(objInvoice[3].toString());
			objBean.setStrVehNo(objInvoice[4].toString());
			objBean.setDblSubTotalAmt(Double.parseDouble(objInvoice[5].toString()));
			objBean.setDblTaxAmt(Double.parseDouble(objInvoice[6].toString()));
			objBean.setDblTotalAmt(Double.parseDouble(objInvoice[7].toString()));
			objBean.setStrExciseable(objInvoice[8].toString());
			listofInvFlash.add(objBean);
			BigDecimal value=new BigDecimal(Double.parseDouble(objInvoice[7].toString()));
			dblTotalValue=dblTotalValue.add(value);
			value=new BigDecimal(Double.parseDouble(objInvoice[5].toString()));
			dblSubTotalValue=dblSubTotalValue.add(value);
			
			value=new BigDecimal(Double.parseDouble(objInvoice[6].toString()));
			dblTaxTotalValue=dblTaxTotalValue.add(value);
		}
		}
		listofInvoiveTotal.add(listofInvFlash);
		listofInvoiveTotal.add(dblTotalValue);
		listofInvoiveTotal.add(dblSubTotalValue);
		listofInvoiveTotal.add(dblTaxTotalValue);
		System.out.print(dblTaxTotalValue+"ttoalsubtotal"+dblTaxTotalValue);
		return listofInvoiveTotal;
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/exportInvoiceExcel", method = RequestMethod.GET)
	public  ModelAndView funExportInvoiceFlash(@RequestParam("custcode") String strCustCode,HttpServletRequest request)
	{
		String fromDate=request.getParameter("frmDte").toString();
		String toDate=request.getParameter("toDte").toString();
		String locCode=request.getParameter("locCode").toString();
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		 String userCode = request.getSession().getAttribute("usercode").toString();
		List listInvoice=new ArrayList();
		
		clsLocationMasterModel objLocCode=objLocationMasterService.funGetObject(locCode,clientCode);
    	
    	String repeortfileName="InvoiceFlashReport"+"_"+objLocCode.getStrLocName()+"_"+fromDate+"_To_"+toDate+"_"+userCode;
    	repeortfileName=repeortfileName.replaceAll(" ", "");
    	listInvoice.add(repeortfileName);
    	
		BigDecimal dblTotalValue=new BigDecimal(0);
		BigDecimal dblSubTotalValue=new BigDecimal(0);
		BigDecimal dblTaxTotalValue=new BigDecimal(0);
		String[] ExcelHeader={"Invoice Code","Date","Customer Name","Against","Vehicle No","Excisable","Sub Total","Tax Amount","Total Amount"};
		listInvoice.add(ExcelHeader);	
		
		List listofInvFlash=new ArrayList();
		
		String strClientCode=request.getSession().getAttribute("clientCode").toString();
		String sqlInvoiceFlash="select a.strInvCode ,a.dteInvDate,b.strPName,a.strAgainst,a.strVehNo,a.dblSubTotalAmt,a.dblTaxAmt,a.dblTotalAmt,a.strExciseable from tblinvoicehd a ,tblpartymaster b  where   date(a.dteInvDate) between '"+fromDate+"' and '"+toDate+"' "  
                              +" and a.strLocCode='"+locCode+"' and a.strCustCode=b.strPCode and  a.strClientCode='"+strClientCode+"'" ;
                              if(!strCustCode.equals("All"))
                              {
                            	  sqlInvoiceFlash=sqlInvoiceFlash+ " and a.strCustCode='"+strCustCode+"' "; 
                              }
		
         DecimalFormat df = new DecimalFormat("#.##");
     	double floatingPoint=0.0;
        List listOfInvoice=objGlobalService.funGetList(sqlInvoiceFlash, "sql");
        if(!listOfInvoice.isEmpty())
		{
		for(int i=0;i<listOfInvoice.size();i++)
		{
			Object[] objInvoice=(Object[] )listOfInvoice.get(i);	
			List DataList=new ArrayList<>();
			DataList.add(objInvoice[0].toString());
			DataList.add(objInvoice[1].toString());
			DataList.add(objInvoice[2].toString());
			DataList.add(objInvoice[3].toString());
			DataList.add(objInvoice[4].toString());
			DataList.add(objInvoice[8].toString());
			floatingPoint=Double.parseDouble(objInvoice[5].toString());
			floatingPoint=Double.parseDouble(df.format(floatingPoint).toString());
			DataList.add(floatingPoint);
			
			floatingPoint=Double.parseDouble(objInvoice[6].toString());
			floatingPoint=Double.parseDouble(df.format(floatingPoint).toString());
			DataList.add(floatingPoint);
			
			floatingPoint=Double.parseDouble(objInvoice[7].toString());
			floatingPoint=Double.parseDouble(df.format(floatingPoint).toString());
			DataList.add(floatingPoint);
			
			BigDecimal value=new BigDecimal(Double.parseDouble(objInvoice[7].toString()));
			dblTotalValue=dblTotalValue.add(value);
			value=new BigDecimal(Double.parseDouble(objInvoice[5].toString()));
			dblSubTotalValue=dblSubTotalValue.add(value);
			
			value=new BigDecimal(Double.parseDouble(objInvoice[6].toString()));
			dblTaxTotalValue=dblTaxTotalValue.add(value);
			listofInvFlash.add(DataList);
		
			if(i==listOfInvoice.size()-1)
			{
				DataList=new ArrayList<>();
				DataList.add("");
				DataList.add("");
				DataList.add("");
				DataList.add("");
				DataList.add("");
				DataList.add("Total");
				String a=df.format(dblSubTotalValue);
				DataList.add(df.format(dblSubTotalValue));
				a=df.format(dblTaxTotalValue);
				DataList.add(df.format(dblTaxTotalValue));
				DataList.add(df.format(dblTotalValue));
				listofInvFlash.add(DataList);
			}
		}
		}
		listInvoice.add(listofInvFlash);
//		return new ModelAndView("excelView", "stocklist", listInvoice);
		return new ModelAndView("excelViewWithReportName", "listWithReportName", listInvoice);
	}
	
	
	
	private ModelAndView funGetModelAndView(HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("clientCode").toString();

		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		ModelAndView objModelView=null;
		if("2".equalsIgnoreCase(urlHits)){
			objModelView=new ModelAndView("frmInvoiceFlash_1");
		}else if("1".equalsIgnoreCase(urlHits)){
			objModelView=new ModelAndView("frmInvoiceFlash");
		}
		Map<String, String> mapProperty= objGlobalService.funGetPropertyList(clientCode);
		if(mapProperty.isEmpty())
		{
			mapProperty.put("", "");
		}
		String propertyCode=req.getSession().getAttribute("propertyCode").toString();
		String locationCode=req.getSession().getAttribute("locationCode").toString();
		 Map<String, String> settlementList= objSettlementService.funGetSettlementComboBox(clientCode);
		 settlementList.put("All","All");
		 objModelView.addObject("settlementList", settlementList);
		
		
		objModelView.addObject("listProperty",mapProperty);
		objModelView.addObject("LoggedInProp",propertyCode);
		objModelView.addObject("LoggedInLoc",locationCode);
		
		return objModelView;
	}
	
	
	
	
	
	
	

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value = "/loadTenderWiseDtl", method = RequestMethod.GET)
		public @ResponseBody List funTenderWiseInvoiceFlash(@RequestParam("settlementcode") String settlementcode,HttpServletRequest request)
		{
		String fromDate=request.getParameter("frmDte").toString();
		String toDate=request.getParameter("toDte").toString();
		String locCode=request.getParameter("locCode").toString();
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		 String userCode = request.getSession().getAttribute("usercode").toString();
		List listInvoice=new ArrayList();
		List listofInvoiveTotal=new ArrayList();
		
		clsLocationMasterModel objLocCode=objLocationMasterService.funGetObject(locCode,clientCode);
    	
    	String repeortfileName="InvoiceFlashReport"+"_"+objLocCode.getStrLocName()+"_"+fromDate+"_To_"+toDate+"_"+userCode;
    	repeortfileName=repeortfileName.replaceAll(" ", "");
    	listInvoice.add(repeortfileName);
    	
		BigDecimal dblTotalValue=new BigDecimal(0);
		String[] ExcelHeader={"Invoice Code","Date","Customer Name","Against","Vehicle No","Excisable","Sub Total","Tax Amount","Total Amount"};
		listInvoice.add(ExcelHeader);	
		
		List listofInvFlash=new ArrayList();
		
		String strClientCode=request.getSession().getAttribute("clientCode").toString();
		String sqlInvoiceFlash="select c.strSettlementCode,ifnull(c.strSettlementDesc,''),ifnull(c.strSettlementType,''),sum(a.dblGrandTotal) from tblinvoicehd a,tblsettlementmaster c "
							   +" where  a.strSettlementCode=c.strSettlementCode "
							   + " and a.strLocCode='"+locCode+"' and date(a.dteInvDate) between '"+fromDate+"' and '"+toDate+"' and  a.strClientCode='"+strClientCode+"'" ;  
							   if(!settlementcode.equals("All"))
						         {
	                              sqlInvoiceFlash=sqlInvoiceFlash+ " and  a.strSettlementCode='"+settlementcode+"' "; 
						         } 
							   
							   sqlInvoiceFlash+="group by a.strSettlementCode ";
                            
		
         DecimalFormat df = new DecimalFormat("#.##");
     	double floatingPoint=0.0;
        List listOfInvoice=objGlobalService.funGetList(sqlInvoiceFlash, "sql");
        if(!listOfInvoice.isEmpty())
		{
		for(int i=0;i<listOfInvoice.size();i++)
		{
			Object[] objInvoice=(Object[] )listOfInvoice.get(i);	
			List DataList=new ArrayList<>();
			DataList.add(objInvoice[0].toString());
			DataList.add(objInvoice[1].toString());
			DataList.add(objInvoice[2].toString());
			
			floatingPoint=Double.parseDouble(objInvoice[3].toString());
			floatingPoint=Double.parseDouble(df.format(floatingPoint).toString());
			DataList.add(floatingPoint);
			listofInvFlash.add(DataList);
			BigDecimal value=new BigDecimal(Double.parseDouble(objInvoice[3].toString()));
			dblTotalValue=dblTotalValue.add(value);
		
		}
		}
		
		listofInvoiveTotal.add(listofInvFlash);
		listofInvoiveTotal.add(dblTotalValue);
		return listofInvoiveTotal;
	}

	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadOpertorWiseDtl", method = RequestMethod.GET)
	public @ResponseBody List funOperatorWiseInvoiceFlash(@RequestParam("settlementcode") String settlementcode,HttpServletRequest request)
	{
	
		String fromDate=request.getParameter("frmDte").toString();
		String toDate=request.getParameter("toDte").toString();
		String locCode=request.getParameter("locCode").toString();
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		 String userCode = request.getSession().getAttribute("usercode").toString();
		List listInvoice=new ArrayList();
		List listofInvoiveTotal=new ArrayList();
		
		clsLocationMasterModel objLocCode=objLocationMasterService.funGetObject(locCode,clientCode);
    	
    	String repeortfileName="InvoiceFlashReport"+"_"+objLocCode.getStrLocName()+"_"+fromDate+"_To_"+toDate+"_"+userCode;
    	repeortfileName=repeortfileName.replaceAll(" ", "");
    	listInvoice.add(repeortfileName);
    	
		BigDecimal dblTotalValue=new BigDecimal(0);
		String[] ExcelHeader={"Invoice Code","Date","Customer Name","Against","Vehicle No","Excisable","Sub Total","Tax Amount","Total Amount"};
		listInvoice.add(ExcelHeader);	
		
		List listofInvFlash=new ArrayList();
		
		String strClientCode=request.getSession().getAttribute("clientCode").toString();
		String sqlInvoiceFlash="SELECT b.strUserCode,b.strUserName,  sum(a.dblGrandTotal),sum(a.dblDiscountAmt),ifnull(c.strSettlementDesc,'')  from tblinvoicehd a,tbluserhd b,tblsettlementmaster c "
							  +" WHERE a.strUserCreated=b.strUserCode and a.strSettlementCode=c.strSettlementCode "
							  + " and a.strLocCode='"+locCode+"' and date(a.dteInvDate) between '"+fromDate+"' and '"+toDate+"' and  a.strClientCode='"+strClientCode+"'" ;  
							   if(!settlementcode.equals("All"))
						         {
	                              sqlInvoiceFlash=sqlInvoiceFlash+ " and  a.strSettlementCode='"+settlementcode+"' "; 
						         } 
							   sqlInvoiceFlash+="group by a.strUserCreated,c.strSettlementCode order by a.strUserCreated ";
                            
		
         DecimalFormat df = new DecimalFormat("#.##");
     	double floatingPoint=0.0;
     	String strUserCode="";
     	double userSalesTtl=0.0;
     	Map <String,Double>hmUserSalesDtl=new HashMap<String,Double>();
        List listOfInvoice=objGlobalService.funGetList(sqlInvoiceFlash, "sql");
        if(!listOfInvoice.isEmpty())
		{
		for(int i=0;i<listOfInvoice.size();i++)
		{
			Object[] objInvoice=(Object[] )listOfInvoice.get(i);	
			List DataList=new ArrayList<>();
			DataList.add(objInvoice[0].toString());
			DataList.add(objInvoice[1].toString());
			floatingPoint=Double.parseDouble(objInvoice[2].toString());
			floatingPoint=Double.parseDouble(df.format(floatingPoint).toString());
			DataList.add(floatingPoint);
			DataList.add(objInvoice[3].toString());
			DataList.add(objInvoice[4].toString());
			
			if(!(objInvoice[0].toString().equals(strUserCode)))
			{
//				DataList.add("");
			if(i==0)
			{
				hmUserSalesDtl.put(objInvoice[0].toString(),Double.parseDouble(objInvoice[2].toString()));
			}else{
			hmUserSalesDtl.put(strUserCode,userSalesTtl);
			}
				
				userSalesTtl=0.0;
			}else{
//				DataList.add(userSalesTtl);		
			}
			userSalesTtl+=Double.parseDouble(objInvoice[2].toString());
			strUserCode=objInvoice[0].toString();
			listofInvFlash.add(DataList);
			BigDecimal value=new BigDecimal(Double.parseDouble(objInvoice[2].toString()));
			dblTotalValue=dblTotalValue.add(value);
		
		}
		hmUserSalesDtl.put(strUserCode,userSalesTtl);
		}
		
		listofInvoiveTotal.add(listofInvFlash);
		listofInvoiveTotal.add(dblTotalValue);
		listofInvoiveTotal.add(hmUserSalesDtl);
		return listofInvoiveTotal;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadCustomerWiseDtl", method = RequestMethod.GET)
	public @ResponseBody List funCustomerWiseInvoiceFlash(@RequestParam("settlementcode") String settlementcode,HttpServletRequest request)
	{
	
		String fromDate=request.getParameter("frmDte").toString();
		String toDate=request.getParameter("toDte").toString();
		String locCode=request.getParameter("locCode").toString();
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		 String userCode = request.getSession().getAttribute("usercode").toString();
		List listInvoice=new ArrayList();
		List listofInvoiveTotal=new ArrayList();
		
		clsLocationMasterModel objLocCode=objLocationMasterService.funGetObject(locCode,clientCode);
    	
    	String repeortfileName="InvoiceFlashReport"+"_"+objLocCode.getStrLocName()+"_"+fromDate+"_To_"+toDate+"_"+userCode;
    	repeortfileName=repeortfileName.replaceAll(" ", "");
    	listInvoice.add(repeortfileName);
    	
		BigDecimal dblTotalValue=new BigDecimal(0);
		String[] ExcelHeader={"Invoice Code","Date","Customer Name","Against","Vehicle No","Excisable","Sub Total","Tax Amount","Total Amount"};
		listInvoice.add(ExcelHeader);	
		
		List listofInvFlash=new ArrayList();
		
		String strClientCode=request.getSession().getAttribute("clientCode").toString();
		String sqlInvoiceFlash="select b.strCustCode,a.strPName,a.strPType,count(b.strInvCode),sum(b.dblGrandTotal) "
							 + " from tblpartymaster a,tblinvoicehd b where b.strCustCode=a.strPCode  and b.strLocCode='"+locCode+"' "
							 + " and date(b.dteInvDate) between '"+fromDate+"' and '"+toDate+"' and  b.strClientCode='"+strClientCode+"'" ;  
							   if(!settlementcode.equals("All"))
						         {
	                              sqlInvoiceFlash=sqlInvoiceFlash+ " and  b.strSettlementCode='"+settlementcode+"' "; 
						         } 
							   sqlInvoiceFlash+="group by b.strCustCode ";
                            
		
         DecimalFormat df = new DecimalFormat("#.##");
     	
        List listOfInvoice=objGlobalService.funGetList(sqlInvoiceFlash, "sql");
        if(!listOfInvoice.isEmpty())
		{
		for(int i=0;i<listOfInvoice.size();i++)
		{
			Object[] objInvoice=(Object[] )listOfInvoice.get(i);	
			List DataList=new ArrayList<>();
			DataList.add(objInvoice[0].toString());
			DataList.add(objInvoice[1].toString());
			DataList.add(objInvoice[2].toString());
			DataList.add(objInvoice[3].toString());
			DataList.add(objInvoice[4].toString());
			
			listofInvFlash.add(DataList);
			BigDecimal value=new BigDecimal(Double.parseDouble(objInvoice[4].toString()));
			dblTotalValue=dblTotalValue.add(value);
		
		}

		}
		
		listofInvoiveTotal.add(listofInvFlash);
		listofInvoiveTotal.add(dblTotalValue);
		
		return listofInvoiveTotal;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadSKUWiseDtl", method = RequestMethod.GET)
	public @ResponseBody List funProductWiseInvoiceFlash(@RequestParam("settlementcode") String settlementcode,HttpServletRequest request)
	{
	
		String fromDate=request.getParameter("frmDte").toString();
		String toDate=request.getParameter("toDte").toString();
		String locCode=request.getParameter("locCode").toString();
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		 String userCode = request.getSession().getAttribute("usercode").toString();
		List listInvoice=new ArrayList();
		List listofInvoiveTotal=new ArrayList();
		BigDecimal dblTotalValue=new BigDecimal(0);
		String[] ExcelHeader={"Invoice Code","Date","Customer Name","Against","Vehicle No","Excisable","Sub Total","Tax Amount","Total Amount"};
		listInvoice.add(ExcelHeader);	
		
		List listofInvFlash=new ArrayList();
		
		String strClientCode=request.getSession().getAttribute("clientCode").toString();
		String sqlInvoiceFlash="SELECT  b.strProdCode,c.strProdName,sum(b.dblQty), SUM(b.dblQty*b.dblPrice) FROM tblinvoicedtl b, tblproductmaster c,tblinvoicehd a "
							 +" WHERE a.strInvCode=b.strInvCode  and b.strProdCode=c.strProdCode  and a.strLocCode='"+locCode+"' "
							 + " and date(a.dteInvDate) between '"+fromDate+"' and '"+toDate+"' and  a.strClientCode='"+strClientCode+"'" ;  
							   if(!settlementcode.equals("All"))
						         {
	                              sqlInvoiceFlash=sqlInvoiceFlash+ " and  a.strSettlementCode='"+settlementcode+"' "; 
						         } 
							   sqlInvoiceFlash+="group by b.strProdCode ";
                            
		
         DecimalFormat df = new DecimalFormat("#.##");
   
        List listOfInvoice=objGlobalService.funGetList(sqlInvoiceFlash, "sql");
        if(!listOfInvoice.isEmpty())
		{
		for(int i=0;i<listOfInvoice.size();i++)
		{
			Object[] objInvoice=(Object[] )listOfInvoice.get(i);	
			List DataList=new ArrayList<>();
			DataList.add(objInvoice[0].toString());
			DataList.add(objInvoice[1].toString());
			DataList.add(objInvoice[2].toString());
			DataList.add(objInvoice[3].toString());
			
			
			listofInvFlash.add(DataList);
			BigDecimal value=new BigDecimal(Double.parseDouble(objInvoice[3].toString()));
			dblTotalValue=dblTotalValue.add(value);
		
		}
	
		}
		
		listofInvoiveTotal.add(listofInvFlash);
		listofInvoiveTotal.add(dblTotalValue);
	
		return listofInvoiveTotal;
	}
	
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadCategoryWiseDtl", method = RequestMethod.GET)
	public @ResponseBody List funCategoryWiseInvoiceFlash(@RequestParam("settlementcode") String settlementcode,HttpServletRequest request)
	{
	
		String fromDate=request.getParameter("frmDte").toString();
		String toDate=request.getParameter("toDte").toString();
		String locCode=request.getParameter("locCode").toString();
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		 String userCode = request.getSession().getAttribute("usercode").toString();
		List listInvoice=new ArrayList();
		List listofInvoiveTotal=new ArrayList();
		BigDecimal dblTotalValue=new BigDecimal(0);
		String[] ExcelHeader={"Invoice Code","Date","Customer Name","Against","Vehicle No","Excisable","Sub Total","Tax Amount","Total Amount"};
		listInvoice.add(ExcelHeader);	
		
		List listofInvFlash=new ArrayList();
		
		String strClientCode=request.getSession().getAttribute("clientCode").toString();
		String sqlInvoiceFlash="select b.strSGCode,c.strSGName,sum(d.dblQty),sum(d.dblQty*d.dblUnitPrice) from tblinvoicehd a,tblproductmaster b,tblsubgroupmaster c,tblinvoicedtl d "
							  +" where a.strInvCode=d.strInvCode  and d.strProdCode=b.strProdCode and b.strSGCode=c.strSGCode   and a.strLocCode='"+locCode+"' " 
							 + " and date(a.dteInvDate) between '"+fromDate+"' and '"+toDate+"' and  a.strClientCode='"+strClientCode+"'" ;  
							   if(!settlementcode.equals("All"))
						         {
	                              sqlInvoiceFlash=sqlInvoiceFlash+ " and  a.strSettlementCode='"+settlementcode+"' "; 
						         } 
							   sqlInvoiceFlash+="  group by  c.strSGCode ";
                            
		
         DecimalFormat df = new DecimalFormat("#.##");
   
        List listOfInvoice=objGlobalService.funGetList(sqlInvoiceFlash, "sql");
        if(!listOfInvoice.isEmpty())
		{
		for(int i=0;i<listOfInvoice.size();i++)
		{
			Object[] objInvoice=(Object[] )listOfInvoice.get(i);	
			List DataList=new ArrayList<>();
			DataList.add(objInvoice[0].toString());
			DataList.add(objInvoice[1].toString());
			DataList.add(objInvoice[2].toString());
			DataList.add(objInvoice[3].toString());
			
			
			listofInvFlash.add(DataList);
			BigDecimal value=new BigDecimal(Double.parseDouble(objInvoice[3].toString()));
			dblTotalValue=dblTotalValue.add(value);
		
		}
	
		}
		
		listofInvoiveTotal.add(listofInvFlash);
		listofInvoiveTotal.add(dblTotalValue);
	
		return listofInvoiveTotal;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadManufactureWiseDtl", method = RequestMethod.GET)
	public @ResponseBody List funManufactureWiseInvoiceFlash(@RequestParam("settlementcode") String settlementcode,HttpServletRequest request)
	{
	
		String fromDate=request.getParameter("frmDte").toString();
		String toDate=request.getParameter("toDte").toString();
		String locCode=request.getParameter("locCode").toString();
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		 String userCode = request.getSession().getAttribute("usercode").toString();
		List listInvoice=new ArrayList();
		List listofInvoiveTotal=new ArrayList();
		BigDecimal dblTotalValue=new BigDecimal(0);
		String[] ExcelHeader={"Invoice Code","Date","Customer Name","Against","Vehicle No","Excisable","Sub Total","Tax Amount","Total Amount"};
		listInvoice.add(ExcelHeader);	
		
		List listofInvFlash=new ArrayList();
		
		String strClientCode=request.getSession().getAttribute("clientCode").toString();
		String sqlInvoiceFlash="  select b.strManufacturerCode,c.strManufacturerName,sum(d.dblQty),sum(d.dblQty*d.dblUnitPrice) from tblinvoicehd a,tblproductmaster b,tblmanufacturemaster c,tblinvoicedtl d "
							  +" where a.strInvCode=d.strInvCode  and d.strProdCode=b.strProdCode and b.strManufacturerCode=c.strManufacturerCode   and a.strLocCode='"+locCode+"' " 
							 + " and date(a.dteInvDate) between '"+fromDate+"' and '"+toDate+"' and  a.strClientCode='"+strClientCode+"'" ;  
							   if(!settlementcode.equals("All"))
						         {
	                              sqlInvoiceFlash=sqlInvoiceFlash+ " and  a.strSettlementCode='"+settlementcode+"' "; 
						         } 
							   sqlInvoiceFlash+=" group by  c.strManufacturerCode ";
                            
		
         DecimalFormat df = new DecimalFormat("#.##");
   
        List listOfInvoice=objGlobalService.funGetList(sqlInvoiceFlash, "sql");
        if(!listOfInvoice.isEmpty())
		{
		for(int i=0;i<listOfInvoice.size();i++)
		{
			Object[] objInvoice=(Object[] )listOfInvoice.get(i);	
			List DataList=new ArrayList<>();
			DataList.add(objInvoice[0].toString());
			DataList.add(objInvoice[1].toString());
			DataList.add(objInvoice[2].toString());
			DataList.add(objInvoice[3].toString());
			
			
			listofInvFlash.add(DataList);
			BigDecimal value=new BigDecimal(Double.parseDouble(objInvoice[3].toString()));
			dblTotalValue=dblTotalValue.add(value);
		
		}
	
		}
		
		listofInvoiveTotal.add(listofInvFlash);
		listofInvoiveTotal.add(dblTotalValue);
	
		return listofInvoiveTotal;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadDepartmentWiseDtl", method = RequestMethod.GET)
	public @ResponseBody List funDepartmentWiseInvoiceFlash(@RequestParam("settlementcode") String settlementcode,HttpServletRequest request)
	{
	
		String fromDate=request.getParameter("frmDte").toString();
		String toDate=request.getParameter("toDte").toString();
		String locCode=request.getParameter("locCode").toString();
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		 String userCode = request.getSession().getAttribute("usercode").toString();
		List listInvoice=new ArrayList();
		List listofInvoiveTotal=new ArrayList();
		BigDecimal dblTotalValue=new BigDecimal(0);
		String[] ExcelHeader={"Invoice Code","Date","Customer Name","Against","Vehicle No","Excisable","Sub Total","Tax Amount","Total Amount"};
		listInvoice.add(ExcelHeader);	
		
		List listofInvFlash=new ArrayList();
		
		String strClientCode=request.getSession().getAttribute("clientCode").toString();
		String sqlInvoiceFlash=" select b.strLocCode,b.strLocName ,sum(d.dblQty),sum(d.dblQty*d.dblUnitPrice) from tblinvoicehd a,tbllocationmaster b,tblinvoicedtl d "
							  +"   where a.strInvCode=d.strInvCode  and a.strLocCode=b.strLocCode    " 
							  +" and date(a.dteInvDate) between '"+fromDate+"' and '"+toDate+"' and  a.strClientCode='"+strClientCode+"'" ;  
							   if(!settlementcode.equals("All"))
						         {
	                              sqlInvoiceFlash=sqlInvoiceFlash+ " and  a.strSettlementCode='"+settlementcode+"' "; 
						         } 
							   if(!locCode.equals("All"))
						         {
	                              sqlInvoiceFlash=sqlInvoiceFlash+ "and a.strLocCode='"+locCode+"' "; 
						         } 
							   sqlInvoiceFlash+=" group by  a.strLocCode ";
                            
		
         DecimalFormat df = new DecimalFormat("#.##");
   
        List listOfInvoice=objGlobalService.funGetList(sqlInvoiceFlash, "sql");
        if(!listOfInvoice.isEmpty())
		{
		for(int i=0;i<listOfInvoice.size();i++)
		{
			Object[] objInvoice=(Object[] )listOfInvoice.get(i);	
			List DataList=new ArrayList<>();
			DataList.add(objInvoice[0].toString());
			DataList.add(objInvoice[1].toString());
			DataList.add(objInvoice[2].toString());
			DataList.add(objInvoice[3].toString());
			
			
			listofInvFlash.add(DataList);
			BigDecimal value=new BigDecimal(Double.parseDouble(objInvoice[3].toString()));
			dblTotalValue=dblTotalValue.add(value);
		
		}
	
		}
		
		listofInvoiveTotal.add(listofInvFlash);
		listofInvoiveTotal.add(dblTotalValue);
	
		return listofInvoiveTotal;
	}
	
}
