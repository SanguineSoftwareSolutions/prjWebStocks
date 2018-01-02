package com.sanguine.webbooks.controller;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.Connection;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.model.clsPropertySetupModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsSetupMasterService;
import com.sanguine.util.clsReportBean;
import com.sanguine.webbooks.bean.clsJVBean;
import com.sanguine.webbooks.bean.clsJVDetailsBean;
import com.sanguine.webbooks.model.clsJVDebtorDtlModel;
import com.sanguine.webbooks.model.clsJVDtlModel;
import com.sanguine.webbooks.model.clsJVHdModel;
import com.sanguine.webbooks.model.clsWebBooksAccountMasterModel;
import com.sanguine.webbooks.service.clsJVService;
import com.sanguine.webbooks.service.clsWebBooksAccountMasterService;

@Controller
public class clsJVController{

	@Autowired
	private clsWebBooksAccountMasterService objWebBooksAccountMasterService;
	
	@Autowired
	private clsJVService objJVService;
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired
	private clsSetupMasterService objSetupMasterService;
	
	@Autowired
	private ServletContext servletContext;
	
//Open JV
	@RequestMapping(value = "/frmJV", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request){
		
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmJV_1","command", new clsJVHdModel());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmJV","command", new clsJVHdModel());
		}else {
			return null;
		}
		//return new ModelAndView("frmJV","command", new clsJVHdModel());
	}
	
//Load Header Table Data On Form
	@RequestMapping(value = "/loadJV", method = RequestMethod.GET)
	public @ResponseBody clsJVBean funLoadHdData(HttpServletRequest request){
		
		String sql="";
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		String userCode=request.getSession().getAttribute("usercode").toString();
		String docCode=request.getParameter("docCode").toString();
		String propertyCode=request.getSession().getAttribute("propertyCode").toString();
		clsJVHdModel objJV=objJVService.funGetJVList(docCode, clientCode, propertyCode);
		
		clsJVBean objJVBean=null;
		if(null==objJV)
		{
			objJVBean = new clsJVBean();
			objJVBean.setStrVouchNo("Invalid");
		}
		else
		{
			Map<String,clsJVDetailsBean> hmJVDtlBean=new HashMap<String,clsJVDetailsBean>();
			
			objJVBean = new clsJVBean();
			objJVBean.setStrVouchNo(objJV.getStrVouchNo());
			objJVBean.setStrNarration(objJV.getStrNarration());
			objJVBean.setDteVouchDate(objGlobal.funGetDate("yyyy/MM/dd",objJV.getDteVouchDate()));
			objJVBean.setStrUserCreated(objJV.getStrUserCreated());
			objJVBean.setStrUserEdited(objJV.getStrUserEdited());
			objJVBean.setDteDateCreated(objGlobal.funGetDate("yyyy/MM/dd",objJV.getDteDateCreated()));
			objJVBean.setDteDateEdited(objGlobal.funGetDate("yyyy/MM/dd",objJV.getDteDateEdited()));
			
			List<clsJVDetailsBean> listJVDtlBean=new ArrayList<clsJVDetailsBean>();
			
			for(clsJVDtlModel objJVDtlModel:objJV.getListJVDtlModel())
			{
				clsJVDetailsBean objJVDtlBean=new clsJVDetailsBean();
				objJVDtlBean.setStrAccountCode(objJVDtlModel.getStrAccountCode());
				objJVDtlBean.setStrDescription(objJVDtlModel.getStrAccountName());
				objJVDtlBean.setStrDC(objJVDtlModel.getStrCrDr());
				objJVDtlBean.setDblCreditAmt(objJVDtlModel.getDblCrAmt());
				objJVDtlBean.setDblDebitAmt(objJVDtlModel.getDblDrAmt());
				objJVDtlBean.setStrDimension("");
				objJVDtlBean.setStrNarration(objJVDtlModel.getStrNarration());
				objJVDtlBean.setStrOneLineAcc(objJVDtlModel.getStrOneLine());
				//listJVDtlBean.add(objJVDtlBean);
				
				hmJVDtlBean.put(objJVDtlModel.getStrAccountCode(),objJVDtlBean);
			}
			
			for(clsJVDebtorDtlModel objJVDebtorDtl:objJV.getListJVDebtorDtlModel())
			{
				clsJVDetailsBean objJVDtlBean=hmJVDtlBean.get(objJVDebtorDtl.getStrAccountCode());
				
				clsJVDetailsBean objJVDtlNew=new clsJVDetailsBean();
				objJVDtlNew.setStrAccountCode(objJVDtlBean.getStrAccountCode());
				objJVDtlNew.setStrDescription(objJVDtlBean.getStrDescription());
				objJVDtlNew.setStrDC(objJVDtlBean.getStrDC());
				objJVDtlNew.setStrDimension("");
				objJVDtlNew.setStrNarration(objJVDtlBean.getStrNarration());
				objJVDtlNew.setStrOneLineAcc(objJVDtlBean.getStrOneLineAcc());
				
				objJVDtlNew.setStrDebtorCode(objJVDebtorDtl.getStrDebtorCode());
				objJVDtlNew.setStrDebtorName(objJVDebtorDtl.getStrDebtorName());
				objJVDtlNew.setStrDebtorYN("Y");
				
				if(objJVDebtorDtl.getStrCrDr().equalsIgnoreCase("Cr"))
				{
					objJVDtlNew.setDblCreditAmt(objJVDebtorDtl.getDblAmt());
					objJVDtlNew.setDblDebitAmt(0.0000);
				}
				else
				{
					objJVDtlNew.setDblCreditAmt(0.0000);
					objJVDtlNew.setDblDebitAmt(objJVDebtorDtl.getDblAmt());
				}
				listJVDtlBean.add(objJVDtlNew);
				
				/*
				for(clsJVDetailsBean objJVDtlBean:listJVDtlBean)
				{
					if(objJVDtlBean.getStrAccountCode().equals(objJVDebtorDtl.getStrAccountCode()))
					{
						objJVDtlBean.setStrDebtorCode(objJVDebtorDtl.getStrDebtorCode());
						objJVDtlBean.setStrDebtorName(objJVDebtorDtl.getStrDebtorName());
						objJVDtlBean.setStrDebtorYN("Y");
					}
				}*/
			}
			objJVBean.setListJVDtlBean(listJVDtlBean);			
			hmJVDtlBean=null;
		}
		return objJVBean;
	}

	
//Load Dtl Table Data On Form
	@RequestMapping(value = "/loadJVDtl", method = RequestMethod.POST)
	public @ResponseBody clsJVDtlModel funLoadDtlData(HttpServletRequest request){
		
		String sql="";
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		String userCode=request.getSession().getAttribute("userCode").toString();
		clsJVBean objBean=new clsJVBean();
		String docCode=request.getParameter("docCode").toString();
		List listDtlModel=objGlobalFunctionsService.funGetList(sql);
		clsJVDtlModel objJVDtl = new clsJVDtlModel();
		return objJVDtl;
	}

	
//Save or Update JV
	@RequestMapping(value = "/saveJV", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsJVBean objBean ,BindingResult result,HttpServletRequest req){
		if(!result.hasErrors()){
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String userCode=req.getSession().getAttribute("usercode").toString();
			String propCode=req.getSession().getAttribute("propertyCode").toString();
			String startDate=req.getSession().getAttribute("startDate").toString();
			clsJVHdModel objHdModel = funPrepareHdModel(objBean,userCode,clientCode,req);
			objJVService.funAddUpdateJVHd(objHdModel);
			
			req.getSession().setAttribute("success", true);
        	req.getSession().setAttribute("successMessage","JV No : ".concat(objHdModel.getStrVouchNo()));
			
			return new ModelAndView("redirect:/frmJV.html");
		}
		else{
			return new ModelAndView("frmJV");
		}
	}

//Convert bean to model function
	private clsJVHdModel funPrepareHdModel(clsJVBean objBean,String userCode,String clientCode,HttpServletRequest request){
		
		String propertyCode=request.getSession().getAttribute("propertyCode").toString();
		clsJVHdModel objModel=new clsJVHdModel();
		
		Map<String,clsJVDtlModel> hmJVDtl=new HashMap<String,clsJVDtlModel>();
		
		if(objBean.getStrVouchNo().isEmpty()) // New Entry
		{
			String documentNo=objGlobal.funGenerateDocumentCodeWebBook("frmJV",objBean.getDteVouchDate(),request);
			objModel.setStrVouchNo(documentNo);
//			long lastNo=objGlobal.funGenerateDocumentCodeLastNo("frmJV",objBean.getDteVouchDate(),request);
			long lastNo=0;
			objModel.setIntVouchNum(String.valueOf(lastNo));
			objModel.setStrUserCreated(userCode);
			objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		}
		else // Update
		{
			objModel.setStrUserCreated(userCode);
			objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objModel.setStrVouchNo(objBean.getStrVouchNo());
		}
				
		objModel.setIntId(0);
		objModel.setStrNarration(objGlobal.funIfNull(objBean.getStrNarration(), "NA", objBean.getStrNarration()));
		objModel.setStrType(objGlobal.funIfNull(objBean.getStrType(), "None", objBean.getStrType()));
		objModel.setStrSancCode(objGlobal.funIfNull(objBean.getStrSancCode(), "NA", objBean.getStrSancCode()));
		objModel.setDteVouchDate(objGlobal.funGetDate("yyyy-MM-dd",objBean.getDteVouchDate()));
		objModel.setIntVouchMonth(objGlobal.funGetMonth());
		objModel.setDblAmt(objBean.getDblAmt());
		objModel.setStrUserEdited(userCode);
		objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		objModel.setStrMasterPOS("");
		if (request.getSession().getAttribute("selectedModuleName").toString().equalsIgnoreCase("8-WebBookAPGL")) {
		objModel.setStrModuleType("APGL");
		}else
		{
			objModel.setStrModuleType("AR");
		}
		objModel.setStrTransMode("R");
		objModel.setStrTransType("A");
		objModel.setStrPropertyCode(propertyCode);
		objModel.setStrClientCode(clientCode);
		
		for(clsJVDetailsBean objJVDetails:objBean.getListJVDtlBean())
		{
			if(null!=hmJVDtl.get(objJVDetails.getStrAccountCode()))
			{
				clsJVDtlModel objJVDtlModel=hmJVDtl.get(objJVDetails.getStrAccountCode());
				objJVDtlModel.setDblCrAmt(objJVDtlModel.getDblCrAmt()+objJVDetails.getDblCreditAmt());
				objJVDtlModel.setDblDrAmt(objJVDtlModel.getDblDrAmt()+objJVDetails.getDblDebitAmt());
				hmJVDtl.put(objJVDetails.getStrAccountCode(),objJVDtlModel);
			}
			else
			{
				clsJVDtlModel objJVDtlModel=new clsJVDtlModel();
				objJVDtlModel.setStrAccountCode(objJVDetails.getStrAccountCode());
				objJVDtlModel.setStrAccountName(objJVDetails.getStrDescription());
				objJVDtlModel.setStrCrDr(objJVDetails.getStrDC());
				objJVDtlModel.setStrNarration(objJVDetails.getStrNarration());
				objJVDtlModel.setStrOneLine(objJVDetails.getStrOneLineAcc());
				objJVDtlModel.setDblCrAmt(objJVDetails.getDblCreditAmt());
				objJVDtlModel.setDblDrAmt(objJVDetails.getDblDebitAmt());
				objJVDtlModel.setStrPropertyCode(propertyCode);
				//listJVDtlModel.add(objJVDtlModel);
				
				hmJVDtl.put(objJVDetails.getStrAccountCode(),objJVDtlModel);
			}
		}
		
		List<clsJVDtlModel> listJVDtlModel=new ArrayList<clsJVDtlModel>();
		for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtl.entrySet()) {
			listJVDtlModel.add(entry.getValue());
		}
		objModel.setListJVDtlModel(listJVDtlModel);
		
		List<clsJVDebtorDtlModel> listJVDebtorDtlModel=new ArrayList<clsJVDebtorDtlModel>();
		for(clsJVDetailsBean objJVDetails:objBean.getListJVDtlBean())
		{
			clsJVDebtorDtlModel objJVDebtorDtlModel=new clsJVDebtorDtlModel();
			
			objJVDebtorDtlModel.setStrDebtorCode(objJVDetails.getStrDebtorCode());
			objJVDebtorDtlModel.setStrDebtorName(objJVDetails.getStrDebtorName());
			objJVDebtorDtlModel.setStrAccountCode(objJVDetails.getStrAccountCode());			
			objJVDebtorDtlModel.setStrNarration(objJVDetails.getStrNarration());
			objJVDebtorDtlModel.setStrPropertyCode(propertyCode);
			objJVDebtorDtlModel.setStrCrDr(objJVDetails.getStrDC());			
			if(objJVDetails.getStrDC().equals("Cr"))
			{
				objJVDebtorDtlModel.setDblAmt(objJVDetails.getDblCreditAmt());
			}
			else
			{
				objJVDebtorDtlModel.setDblAmt(objJVDetails.getDblDebitAmt());
			}
			
			objJVDebtorDtlModel.setStrBillNo("BBB");
			objJVDebtorDtlModel.setStrInvoiceNo("BBB");
			objJVDebtorDtlModel.setStrGuest("BBB");
			objJVDebtorDtlModel.setStrPOSCode("P01");
			objJVDebtorDtlModel.setStrPOSName("BAR");
			objJVDebtorDtlModel.setStrRegistrationNo("R9999");
			objJVDebtorDtlModel.setStrCreditNo("C9999");
			objJVDebtorDtlModel.setDteBillDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objJVDebtorDtlModel.setDteInvoiceDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objJVDebtorDtlModel.setDteDueDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			
			listJVDebtorDtlModel.add(objJVDebtorDtlModel);
		}
		
		objModel.setListJVDebtorDtlModel(listJVDebtorDtlModel);
		
		return objModel;
	}
	
	
	@RequestMapping(value = "/loadMemberDetails", method = RequestMethod.GET)
	public @ResponseBody clsJVDebtorDtlModel funLoadMemberDetails(@RequestParam("debtorCode") String debtorCode,HttpServletRequest req)
	{
		clsJVDebtorDtlModel objJVDebtorDtlModel=new clsJVDebtorDtlModel();
		
		String sql="select strMemberCode,strFirstName,strMiddleName,strLastName "
			+ "from vMemberData where strMemberCode='"+debtorCode+"'";
		List list=objGlobalFunctionsService.funGetListModuleWise(sql,"sql");
		
		Object[] arrObj=(Object[]) list.get(0);
		objJVDebtorDtlModel.setStrDebtorCode(arrObj[0].toString());
		objJVDebtorDtlModel.setStrDebtorName(arrObj[1].toString());
		
		return objJVDebtorDtlModel;
	}

	
// Get Non Debtor Account Code And Name
	@RequestMapping(value = "/loadAccForNonDebtor", method = RequestMethod.GET)
	public @ResponseBody clsWebBooksAccountMasterModel funGetAccountCodeAndName(@RequestParam("acCode") String acCode,HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		clsWebBooksAccountMasterModel objModel=objWebBooksAccountMasterService.funGetAccountForNonDebtor(acCode, clientCode);
		if(null==objModel)
		{
			objModel=new clsWebBooksAccountMasterModel();
			objModel.setStrAccountCode("Invalid Code");
		}

		return objModel;
	}	
	
	
	@RequestMapping(value = "/frmJVReport", method = RequestMethod.GET)
	public ModelAndView funOpenReportForm(Map<String, Object> model,HttpServletRequest request){
		
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmJVReport_1","command", new clsReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmJVReport","command", new clsReportBean());
		}else {
			return null;
		}
		//return new ModelAndView("frmJV","command", new clsJVHdModel());
	}
	
	
	@RequestMapping(value = "/rptJVReport", method = RequestMethod.GET)
	private void funJobOrderReport(@ModelAttribute("command") clsReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
	{
		String VoucherNo=objBean.getStrDocCode();
		String type=objBean.getStrDocType();
       funCallJVdtlReport(VoucherNo,type,resp,req);
	}
	
	
	@SuppressWarnings(
		    { "unchecked", "rawtypes" })
		    public void funCallJVdtlReport(String VoucherNo, String type, HttpServletResponse resp, HttpServletRequest req)
		    {
			try
			{
			    
			    String strVouchNo = "";
			    String dteVouchDate = "";
			    String strNarration = "";
			    
			    Connection con = objGlobal.funGetConnection(req);
			    String clientCode = req.getSession().getAttribute("clientCode").toString();
			    String companyName = req.getSession().getAttribute("companyName").toString();
			    String userCode = req.getSession().getAttribute("usercode").toString();
			    String propertyCode = req.getSession().getAttribute("propertyCode").toString();
			    clsPropertySetupModel objSetup = objSetupMasterService.funGetObjectPropertySetup(propertyCode, clientCode);
			    if (objSetup == null)
			    {
				objSetup = new clsPropertySetupModel();
			    }
			    String reportName = servletContext.getRealPath("/WEB-INF/reports/webbooks/rptJVReport.jrxml");
			    String imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
			    
			    String sqlHd = "select strVouchNo ,Date(dteVouchDate),strNarration from tbljvhd where strVouchNo='" + VoucherNo + "'";
			    
			    List list = objGlobalFunctionsService.funGetListModuleWise(sqlHd, "sql");
			    if (!list.isEmpty())
			    {
				Object[] arrObj = (Object[]) list.get(0);
				strVouchNo = arrObj[0].toString();
				dteVouchDate = arrObj[1].toString();
				strNarration = arrObj[2].toString();
			    }
			    String sqlDtl = "select * from tbljvdtl where strVouchNo='" + VoucherNo + "' order by strAccountCode;";
			    JasperDesign jd = JRXmlLoader.load(reportName);
			    JRDesignQuery subQuery = new JRDesignQuery();
			    subQuery.setText(sqlDtl);
			    Map<String, JRDataset> datasetMap = jd.getDatasetMap();
			    JRDesignDataset subDataset = (JRDesignDataset) datasetMap.get("dsJVdtl");
			    subDataset.setQuery(subQuery);
			    
			    // String trunTemp="truncate table tbltempjvorderdtl";
			    //
			    // objJVService.funInsertJV(trunTemp);
			    //
			    // String
			    // sqltempDtlDr="insert into tbltempjvorderdtl (strVouchNo,strDebtorCode,strDebtorName,"
			    // +
			    // "dblAmt,strCrDr,strBillNo,dteBillDate,strInvoiceNo,dteInvoiceDate,strNarration ) "
			    // +
			    // "select strVouchNo, strDebtorCode,strDebtorName,sum(dblAmt),strCrDr,strBillNo,"
			    // +
			    // "date(dteBillDate),strInvoiceNo,date(dteInvoiceDate),strNarration "
			    // +"from tbljvdebtordtl where strVouchNo='"+VoucherNo+"' and strCrDr='Dr'  "
			    // +"group by strDebtorCode order by strDebtorCode ";
			    //
			    //
			    // objJVService.funInsertJV(sqltempDtlDr);
			    //
			    //
			    // String
			    // sqltempDtlCr="insert into tbltempjvorderdtl (strVouchNo,strDebtorCode,strDebtorName,"
			    // +
			    // "dblAmt,strCrDr,strBillNo,dteBillDate,strInvoiceNo,dteInvoiceDate,strNarration ) "
			    // +
			    // "select strVouchNo, strDebtorCode,strDebtorName,sum(dblAmt),strCrDr,strBillNo,"
			    // +
			    // "date(dteBillDate),strInvoiceNo,date(dteInvoiceDate),strNarration "
			    // +"from tbljvdebtordtl where strVouchNo='"+VoucherNo+"' and strCrDr='Cr'  "
			    // +"group by strDebtorCode order by strDebtorCode ";
			    //
			    // objJVService.funInsertJV(sqltempDtlCr);
			    
			    String sqldetordtl = "select strVouchNo, strDebtorCode,strDebtorName,sum(dblAmt) as dblAmt,strCrDr,strBillNo,"  
			    + " date(dteBillDate) as dteBillDate,strInvoiceNo,date(dteInvoiceDate) as dteInvoiceDate,strNarration " 
			    + " from tbljvdebtordtl  where strVouchNo='" + VoucherNo + "'"
			    + " group by strDebtorCode ,strCrDr order by strDebtorCode;";
			    
			    JRDesignQuery detorDtl = new JRDesignQuery();
			    detorDtl.setText(sqldetordtl);
			    JRDesignDataset detorDataset = (JRDesignDataset) datasetMap.get("dsJvdtltemp");
			    detorDataset.setQuery(detorDtl);
			    JasperReport jr = JasperCompileManager.compileReport(jd);
			    
			    HashMap hm = new HashMap();
			    hm.put("strCompanyName", companyName);
			    hm.put("strUserCode", userCode);
			    hm.put("strImagePath", imagePath);
			    hm.put("strAddr1", objSetup.getStrAdd1());
			    hm.put("strAddr2", objSetup.getStrAdd2());
			    hm.put("strCity", objSetup.getStrCity());
			    hm.put("strState", objSetup.getStrState());
			    hm.put("strCountry", objSetup.getStrCountry());
			    hm.put("strPin", objSetup.getStrPin());
			    hm.put("strVouchNo", strVouchNo);
			    hm.put("dteVouchDate", dteVouchDate);
			    hm.put("strNarration", strNarration);
			    
			    JasperPrint p = JasperFillManager.fillReport(jr, hm, con);
			    
			    if (type.trim().equalsIgnoreCase("pdf"))
			    {
				ServletOutputStream servletOutputStream = resp.getOutputStream();
				byte[] bytes = null;
				bytes = JasperRunManager.runReportToPdf(jr, hm, con);
				resp.setContentType("application/pdf");
				resp.setContentLength(bytes.length);
				servletOutputStream.write(bytes, 0, bytes.length);
				servletOutputStream.flush();
				servletOutputStream.close();
			    }
			    else if (type.trim().equalsIgnoreCase("xls"))
			    {
				JRExporter exporterXLS = new JRXlsExporter();
				exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, p);
				exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, resp.getOutputStream());
				resp.setHeader("Content-Disposition", "attachment;filename=" + "rptJVReport." + type.trim());
				exporterXLS.exportReport();
				resp.setContentType("application/xlsx");
			    }
			    
			}
			catch (Exception e)
			{
			    e.printStackTrace();
			}
		    }
	
}
