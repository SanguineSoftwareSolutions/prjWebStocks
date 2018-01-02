package com.sanguine.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.sanguine.bean.Person;
import com.sanguine.bean.clsGRNBean;
import com.sanguine.bean.clsTransectionProdCharBean;
import com.sanguine.crm.model.clsInvoiceHdModel;
import com.sanguine.crm.model.clsInvoiceModelDtl;
import com.sanguine.crm.model.clsInvoiceTaxDtlModel;
import com.sanguine.model.clsAuditDtlModel;
import com.sanguine.model.clsAuditGRNTaxDtlModel;
import com.sanguine.model.clsAuditHdModel;
import com.sanguine.model.clsCompanyMasterModel;
import com.sanguine.model.clsGRNDtlModel;
import com.sanguine.model.clsGRNHdModel;
import com.sanguine.model.clsGRNTaxDtlModel;
import com.sanguine.model.clsLinkUpHdModel;
import com.sanguine.model.clsLocationMasterModel;
import com.sanguine.model.clsMISDtlModel;
import com.sanguine.model.clsMISHdModel;
import com.sanguine.model.clsMISHdModel_ID;
import com.sanguine.model.clsPOTaxDtlModel;
import com.sanguine.model.clsProdSuppMasterModel;
import com.sanguine.model.clsProductMasterModel;
import com.sanguine.model.clsPropertySetupModel;
import com.sanguine.model.clsPurchaseOrderHdModel;
import com.sanguine.model.clsPurchaseReturnDtlModel;
import com.sanguine.model.clsSupplierMasterModel;
import com.sanguine.model.clsTransectionProdCharModel;
import com.sanguine.service.clsGRNService;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsGroupMasterService;
import com.sanguine.service.clsLinkUpService;
import com.sanguine.service.clsLocationMasterService;
import com.sanguine.service.clsMISService;
import com.sanguine.service.clsProductMasterService;
import com.sanguine.service.clsPropertyMasterService;
import com.sanguine.service.clsPurchaseOrderService;
import com.sanguine.service.clsSetupMasterService;
import com.sanguine.service.clsUOMService;
import com.sanguine.util.clsGRNFixedAmtTax;
import com.sanguine.util.clsReportBean;

@Controller
public class clsGRNController
{
	final static Logger logger=LoggerFactory.getLogger(clsGRNController.class);
		@Autowired
		private ServletContext servletContext;
		@Autowired
		private clsGRNService objGRNService;
		@Autowired
		private clsProductMasterService objProdSuppService;
		@Autowired
		private clsGlobalFunctionsService objGlobalFunctionsService;
		@Autowired
		private clsProductMasterService objProductMasterService;
		@Autowired
		private clsSetupMasterService objSetupMasterService;
		
		@Autowired
		private clsPropertyMasterService objPropertyMasterService;
		
		private clsGlobalFunctions objGlobal=null;
		@Autowired
		private clsGlobalFunctions objGlobalFunctions;
		
		@Autowired
		private clsUOMService objclsUOMService;
		
		@Autowired
		private clsPurchaseOrderService objPurchaseOrderService;
		
		@Autowired
		private clsMISService objMISService;
		
		@Autowired
    	private clsLocationMasterService objLocationMasterService;
		
		@Autowired
		private clsGlobalFunctionsService objGlobalService;
		
		@Autowired 
		private clsLinkUpService objLinkupService;
		
		
		@SuppressWarnings("rawtypes")
		private List listPOInGRN=null;
		
		/**
		 * Open GRN form
		 * @param model
		 * @param request
		 * @return
		 */
		@SuppressWarnings("unused")
		@RequestMapping(value = "/frmGRN", method = RequestMethod.GET)
		public ModelAndView funOpenForm(Map<String,Object> model, HttpServletRequest request)
		{
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			String userCode=request.getSession().getAttribute("usercode").toString();
			String propCode=request.getSession().getAttribute("propertyCode").toString();
			request.getSession().setAttribute("formName","frmWebStockHelpGRN");
			String urlHits="1";
			try{
				urlHits=request.getParameter("saddr").toString();
			}catch(NullPointerException e){
				urlHits="1";
			}
			String authorizationGRNCode="";
			boolean flagOpenFromAuthorization=true;
			try {
				authorizationGRNCode=request.getParameter("authorizationGRNCode").toString();
			} catch (NullPointerException e) {
				flagOpenFromAuthorization=false;
			}
			model.put("flagOpenFromAuthorization",flagOpenFromAuthorization);
			if(flagOpenFromAuthorization){
				model.put("authorizationGRNCode",authorizationGRNCode);
			}
			model.put("urlHits",urlHits);
			List<clsGRNFixedAmtTax> listFixedAmtTax=new ArrayList<clsGRNFixedAmtTax>();
			
			clsGRNBean objBean=new clsGRNBean();			
			objBean.setStrLocCode(request.getSession().getAttribute("locationCode").toString());
			
			/*
			 * Set Process
			 */
			List<String> list=objGlobalFunctions.funGetSetUpProcess("frmGRN",propCode,clientCode);
			if(list.size()>0)
			{
				model.put("strProcessList", list);
			}
			else
			{
				list=new ArrayList<String>();
				model.put("strProcessList",list);
			}
			
			/*
			 * Set UOM List
			 */
			List<String> uomList=new ArrayList<String>();
			uomList=objclsUOMService.funGetUOMList(clientCode);
			model.put("uomList", uomList);
			
			if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmGRN_1","command", objBean);
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmGRN","command", objBean);	
			}else {
				return null;
			}
			
		}
		/**
		 * Open Pending PO form
		 * @return
		 */
		@RequestMapping(value = "/frmGRNPODetails", method = RequestMethod.GET)
		public ModelAndView funOpenPOforMR(){		
			return new ModelAndView("frmGRNPODetails");
			
		}
		/**
		 * Load Pending PO data in Pending PO form
		 * @param request
		 * @return
		 */
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/loadPOforGRN", method = RequestMethod.GET)
		public @ResponseBody List funLoadPOforGRN(HttpServletRequest request)
		{		
			String strSuppCode=request.getParameter("strSuppCode").toString();
			String propCode=request.getSession().getAttribute("propertyCode").toString();
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			List POHelpList=objGRNService.funLoadPOforGRN(strSuppCode,propCode,clientCode);
			return POHelpList;
			
		}
		
		
		@SuppressWarnings({ "rawtypes", "unused" })
		@RequestMapping(value = "/frmGRN1", method = RequestMethod.POST)
		public ModelAndView funOpenFormWithGRNCode(Map<String,Object> model, HttpServletRequest request)
		{
			String urlHits="1";
			try{
				urlHits=request.getParameter("saddr").toString();
			}catch(NullPointerException e){
				urlHits="1";
			}
			
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			String userCode=request.getSession().getAttribute("usercode").toString();
			clsGRNBean bean=new clsGRNBean();
	        String grnCode=request.getParameter("GRNCode").toString();
	        List listGRNHd=objGRNService.funGetObject(grnCode,clientCode);
			bean = funPrepareBean(listGRNHd);
			if(null==listGRNHd)
			{
			}
			else
			{
				request.getSession().setAttribute("transname","frmGRN.jsp");
				request.getSession().setAttribute("formname","GRN");
				request.getSession().setAttribute("code",bean.getStrGRNCode());
			}
	        List listTempDtl=objGRNService.funGetDtlList(grnCode,clientCode);
	        List<clsGRNDtlModel> listGRNDtl=new ArrayList<clsGRNDtlModel>();
	        
	        for(int i=0;i<listTempDtl.size();i++)
	        {
	        	Object[] ob = (Object[])listTempDtl.get(i);
	        	clsGRNDtlModel grnDtl=(clsGRNDtlModel)ob[0];
	        	clsProductMasterModel prodMaster=(clsProductMasterModel)ob[1];
	        	clsGRNDtlModel objGRNDtl=new clsGRNDtlModel();
	        	double totalWt=prodMaster.getDblWeight()*objGRNDtl.getDblQty();
	        	objGRNDtl.setStrGRNCode(grnDtl.getStrGRNCode());
	        	objGRNDtl.setStrProdCode(grnDtl.getStrProdCode());
	        	objGRNDtl.setStrClientCode(grnDtl.getStrClientCode());
	        	objGRNDtl.setStrProdChar(grnDtl.getStrProdChar());
	        	objGRNDtl.setStrProdName(prodMaster.getStrProdName());
	        	objGRNDtl.setStrTaxType(grnDtl.getStrTaxType());
	        	objGRNDtl.setDblUnitPrice(prodMaster.getDblCostRM());
	        	objGRNDtl.setDblWeight(prodMaster.getDblWeight());
	        	objGRNDtl.setDblQty(grnDtl.getDblQty());
	        	objGRNDtl.setDblRejected(grnDtl.getDblRejected());
	        	objGRNDtl.setDblDiscount(grnDtl.getDblDiscount());
	        	objGRNDtl.setDblTax(grnDtl.getDblTax());
	        	objGRNDtl.setDblTaxableAmt(grnDtl.getDblTaxableAmt());
	        	objGRNDtl.setDblTaxAmt(grnDtl.getDblTaxAmt());
	        	objGRNDtl.setDblWeight(grnDtl.getDblWeight());
	        	objGRNDtl.setDblDCQty(grnDtl.getDblDCQty());
	        	objGRNDtl.setDblDCWt(grnDtl.getDblDCWt());
	        	objGRNDtl.setStrRemarks(grnDtl.getStrRemarks());
	        	objGRNDtl.setDblQtyRbl(grnDtl.getDblQtyRbl());
	        	objGRNDtl.setStrGRNProdChar(grnDtl.getStrGRNProdChar());
	        	objGRNDtl.setDblPOWeight(grnDtl.getDblPOWeight());
	        	objGRNDtl.setStrCode(grnDtl.getStrCode());
	        	objGRNDtl.setDblPackForw(grnDtl.getDblPackForw());
	        	double totalPrice=objGRNDtl.getDblUnitPrice()*objGRNDtl.getDblQty();
	        	objGRNDtl.setDblTotalWt(totalWt);
	        	objGRNDtl.setDblTotalPrice(totalPrice);
	        	objGRNDtl.setStrUOM(prodMaster.getStrUOM());
	        	listGRNDtl.add(objGRNDtl);
	        }
			bean.setListGRNDtl(listGRNDtl);
			if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmGRN_1","command", bean);
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmGRN","command", bean);	
			}else {
				return null;
			}
			
		}
		
		
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/loadGRNHd", method = RequestMethod.GET)
		public @ResponseBody clsGRNHdModel funLoadGRNHd(Map<String,Object> model, HttpServletRequest request)
		{
			objGlobal=new clsGlobalFunctions();
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			//String userCode=request.getSession().getAttribute("usercode").toString();
			clsGRNBean bean=new clsGRNBean();
	        String grnCode=request.getParameter("GRNCode").toString();
	        List listGRNHd=objGRNService.funGetObject(grnCode,clientCode);
	        if(listGRNHd.size() == 0){
	        	clsGRNHdModel tempGRNHd= new clsGRNHdModel();
	        	tempGRNHd.setStrGRNCode("Invalid Code");
	        	return tempGRNHd;
	        }else{
	        	 Object[] ob = (Object[])listGRNHd.get(0);
	 			clsGRNHdModel tempGRNHd=(clsGRNHdModel)ob[0];
	 			clsSupplierMasterModel supplierMaster=(clsSupplierMasterModel)ob[1];
	 			clsLocationMasterModel locMaster=(clsLocationMasterModel)ob[2];
	 			
	 			tempGRNHd.setDtGRNDate(objGlobal.funGetDate("yyyy/MM/dd",tempGRNHd.getDtGRNDate()));
	 			tempGRNHd.setDtDueDate(objGlobal.funGetDate("yyyy/MM/dd",tempGRNHd.getDtDueDate()));
	 			tempGRNHd.setDtRefDate(objGlobal.funGetDate("yyyy/MM/dd",tempGRNHd.getDtRefDate()));
	 			tempGRNHd.setDtBillDate(objGlobal.funGetDate("yyyy/MM/dd",tempGRNHd.getDtBillDate()));
	 			tempGRNHd.setStrSuppCode(supplierMaster.getStrPCode());
	 			tempGRNHd.setStrSuppName(supplierMaster.getStrPName());
	 			tempGRNHd.setStrLocName(locMaster.getStrLocName());
	 			request.getSession().setAttribute("transname","frmGRN.jsp");
 				request.getSession().setAttribute("formname","GRN");
 				request.getSession().setAttribute("code",bean.getStrGRNCode());
	 			return tempGRNHd;
	        }	       
		}
		
		
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/loadGRNTaxDtl", method = RequestMethod.GET)
		public @ResponseBody List<clsGRNTaxDtlModel> funLoadGRNDtl(Map<String,Object> model, HttpServletRequest request)
		{
			objGlobal=new clsGlobalFunctions();
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			//String userCode=request.getSession().getAttribute("usercode").toString();
			String grnCode=request.getParameter("GRNCode").toString();
	        
			String sql="select strTaxCode,strTaxDesc,strTaxableAmt,strTaxAmt from clsGRNTaxDtlModel "
					+ "where strGRNCode='"+grnCode+"' and strClientCode='"+clientCode+"'";
			List list=objGlobalFunctionsService.funGetList(sql,"hql");
			List<clsGRNTaxDtlModel> listGRNTaxDtl=new ArrayList<clsGRNTaxDtlModel>();
			for(int cnt=0;cnt<list.size();cnt++)
			{
				clsGRNTaxDtlModel objTaxDtl=new clsGRNTaxDtlModel();
				Object[] arrObj=(Object[])list.get(cnt);
				objTaxDtl.setStrTaxCode(arrObj[0].toString());
				objTaxDtl.setStrTaxDesc(arrObj[1].toString());
				objTaxDtl.setStrTaxableAmt(Double.parseDouble(arrObj[2].toString()));
				objTaxDtl.setStrTaxAmt(Double.parseDouble(arrObj[3].toString()));
				listGRNTaxDtl.add(objTaxDtl);
			}
			
			return listGRNTaxDtl;
		}
		
		
		
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/loadPOTaxDtlonGRN", method = RequestMethod.GET)
		public @ResponseBody List<clsPOTaxDtlModel> funLoadPOTaxDtlonGRN(Map<String,Object> model, HttpServletRequest request)
		{
			objGlobal=new clsGlobalFunctions();
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			//String userCode=request.getSession().getAttribute("usercode").toString();
			String poCode=request.getParameter("POCode").toString();
	        
			String sql="select strTaxCode,strTaxDesc,strTaxableAmt,strTaxAmt from clsPOTaxDtlModel "
					+ "where strPOCode='"+poCode+"' and strClientCode='"+clientCode+"'";
			List list=objGlobalFunctionsService.funGetList(sql,"hql");
			List<clsPOTaxDtlModel> listPOTaxDtl=new ArrayList<clsPOTaxDtlModel>();
			for(int cnt=0;cnt<list.size();cnt++)
			{
				clsPOTaxDtlModel objTaxDtl=new clsPOTaxDtlModel();
				Object[] arrObj=(Object[])list.get(cnt);
				objTaxDtl.setStrTaxCode(arrObj[0].toString());
				objTaxDtl.setStrTaxDesc(arrObj[1].toString());
				objTaxDtl.setStrTaxableAmt(Double.parseDouble(arrObj[2].toString()));
				objTaxDtl.setStrTaxAmt(Double.parseDouble(arrObj[3].toString()));
				listPOTaxDtl.add(objTaxDtl);
			}
			
			return listPOTaxDtl;
		}
		
		
		
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/loadGRNDtl", method = RequestMethod.GET)
		public @ResponseBody List funLoadGRNTaxDtl(HttpServletRequest request)
		{
			objGlobal=new clsGlobalFunctions();
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			String grnCode=request.getParameter("GRNCode").toString();
	        
	        List listTempDtl=objGRNService.funGetDtlList(grnCode,clientCode);
	        List<clsGRNDtlModel> listGRNDtl=new ArrayList<clsGRNDtlModel>();
	        
	        for(int i=0;i<listTempDtl.size();i++)
	        {
	        	Object[] ob = (Object[])listTempDtl.get(i);
	        	clsGRNDtlModel grnDtl=(clsGRNDtlModel)ob[0];
	        	clsProductMasterModel prodMaster=(clsProductMasterModel)ob[1];
	        	clsGRNDtlModel objGRNDtl=new clsGRNDtlModel();
	        	double totalWt=prodMaster.getDblWeight()*objGRNDtl.getDblQty();
	        	objGRNDtl.setStrGRNCode(grnDtl.getStrGRNCode());
	        	objGRNDtl.setStrProdCode(grnDtl.getStrProdCode());
	        	objGRNDtl.setStrClientCode(grnDtl.getStrClientCode());
	        	objGRNDtl.setStrProdChar(grnDtl.getStrProdChar());
	        	objGRNDtl.setStrProdName(prodMaster.getStrProdName());
	        	objGRNDtl.setStrTaxType(grnDtl.getStrTaxType());
	        	objGRNDtl.setDblUnitPrice(grnDtl.getDblUnitPrice());
	        	objGRNDtl.setDblWeight(prodMaster.getDblWeight());
	        	objGRNDtl.setDblQty(grnDtl.getDblQty());
	        	objGRNDtl.setDblRejected(grnDtl.getDblRejected());
	        	objGRNDtl.setDblDiscount(grnDtl.getDblDiscount());
	        	objGRNDtl.setDblTax(grnDtl.getDblTax());
	        	objGRNDtl.setDblTaxableAmt(grnDtl.getDblTaxableAmt());
	        	objGRNDtl.setDblTaxAmt(grnDtl.getDblTaxAmt());
	        	objGRNDtl.setDblWeight(grnDtl.getDblWeight());
	        	objGRNDtl.setDblDCQty(grnDtl.getDblDCQty());
	        	objGRNDtl.setDblDCWt(grnDtl.getDblDCWt());
	        	objGRNDtl.setStrRemarks(grnDtl.getStrRemarks());
	        	objGRNDtl.setDblQtyRbl(grnDtl.getDblQtyRbl());
	        	objGRNDtl.setStrGRNProdChar(grnDtl.getStrGRNProdChar());
	        	objGRNDtl.setDblPOWeight(grnDtl.getDblPOWeight());
	        	objGRNDtl.setStrCode(grnDtl.getStrCode());
	        	objGRNDtl.setDblPackForw(grnDtl.getDblPackForw());
	        	objGRNDtl.setDblTotalWt(totalWt);
	        	objGRNDtl.setDblTotalPrice(grnDtl.getDblTotalPrice());
	        	objGRNDtl.setStrUOM(prodMaster.getStrUOM());
	        	objGRNDtl.setStrExpiry(prodMaster.getStrExpDate());
	        	objGRNDtl.setStrMISCode(grnDtl.getStrMISCode());
	        	objGRNDtl.setStrIssueLocation(grnDtl.getStrIssueLocation());
	        	clsLocationMasterModel objLocModel=objLocationMasterService.funGetObject(grnDtl.getStrIssueLocation(),clientCode);
	        	if(objLocModel!=null)
	        	{
	        		objGRNDtl.setStrIsueLocName(objLocModel.getStrLocName());
	        	}
	        	else
	        	{
	        		objGRNDtl.setStrIsueLocName("");
	        	}
	        	objGRNDtl.setStrStkble(prodMaster.getStrNonStockableItem());
	        	String sql="select IfNULL(b.strReqCode,'') from tblpurchaseorderdtl a "
 						+ " inner join tblmrpidtl b on a.strPIcode =b.strPIcode and b.strClientCode='"+clientCode+"'"
 						+ " where a.strPOCode='"+grnDtl.getStrCode()+"'  and a.strClientCode='"+clientCode+"' group by b.strReqCode";
				List list=objGlobalFunctionsService.funGetList(sql,"sql");
				if(!list.isEmpty() && list!=null)
				{
					objGRNDtl.setStrReqCode(list.get(0).toString());
				}
				else
				{
					objGRNDtl.setStrReqCode("");
				}
	        	listGRNDtl.add(objGRNDtl);
	        	
	        }
			
			return listGRNDtl;
		}
		
		/**
		 * GRN Save Function
		 * @param objBean
		 * @param result
		 * @param request
		 * @return
		 */
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/saveGRN", method = RequestMethod.POST)
		public String funAddUpdate(@ModelAttribute("command") @Valid clsGRNBean objBean,BindingResult result,HttpServletRequest request)
		{
			String urlHits="1";
			String strmsg="Update";
			DecimalFormat df = new DecimalFormat("#.##");
			try{
				urlHits=request.getParameter("saddr").toString();
			}catch(NullPointerException e){
				urlHits="1";
			}
			
			if(objBean.getStrGRNCode().trim().length()==0)
			{
				strmsg="Inserted";
			}
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			String userCode=request.getSession().getAttribute("usercode").toString();
			String startDate=request.getSession().getAttribute("startDate").toString();
			String propCode=request.getSession().getAttribute("propertyCode").toString();
			Double stock,weightedAvg,weightedStk,weigthedvalue=0.00;
			clsGRNHdModel objHdModel=funPrepareModel(objBean,request);
			objGlobal=new clsGlobalFunctions();
			String grnCode="";
			if(!result.hasErrors())
			{
				
							List<clsGRNDtlModel> listGRNDtl = objBean.getListGRNDtl();
							if(null != listGRNDtl && listGRNDtl.size() > 0)
					        {
								grnCode=objHdModel.getStrGRNCode();
								objGRNService.funDeleteDtl(grnCode,clientCode);
								double totalValue=0.00;
								boolean flagDtlDataInserted = false;
								List<clsGRNDtlModel>  batchList=new ArrayList<clsGRNDtlModel>();
					            for (clsGRNDtlModel ob : listGRNDtl)
					            {
					            	if(null!=ob.getStrProdCode())
					            	{
						            	String binNo=objGlobal.funIfNull(objBean.getStrBillNo(),"",ob.getStrBinNo());
						            	List listProdSupp=objGRNService.funGetProdSupp(objHdModel.getStrSuppCode(),ob.getStrProdCode(),clientCode);
						            	funAddProdSuppMaster(listProdSupp,clientCode,ob.getDblUnitPrice(),objHdModel.getStrSuppCode()
						            		,ob.getStrProdCode(),userCode,binNo,grnCode);
						            	ob.setStrGRNCode(grnCode);
						            	ob.setStrProdChar(" ");
						            	if(ob.getStrMISCode()==null)
						            	{
						            		ob.setStrMISCode("");
						            	}
						            	ob.setStrClientCode(clientCode);
						            	objGRNService.funAddUpdateDtl(ob);
						            	clsProductMasterModel objModel=objProductMasterService.funGetObject(ob.getStrProdCode(),clientCode);
						            	if(ob.getDblUnitPrice()!=objModel.getDblCostRM())
						            	{
						            		//Weighted average Calculating Logic and Update In Product Master
						            		 stock=objGlobalFunctions.funGetCurrentStockForProduct(ob.getStrProdCode(), objHdModel.getStrLocCode(), clientCode, userCode, startDate, objGlobalFunctions.funGetCurrentDate("yyyy-MM-dd"));
							            		 weigthedvalue=stock*objModel.getDblCostRM();
							            		 double tempval=ob.getDblQty()*ob.getDblUnitPrice();
							            		 weightedStk=stock+ob.getDblQty();
							            		 if(weightedStk==0.0)
							            		 {
							            			 weightedStk=1.0;
							            		 }
							            		 double temp=weigthedvalue+tempval;
							            		 weightedAvg=temp/weightedStk;
							            		 weightedAvg=Double.parseDouble(df.format(temp/weightedStk).toString());
							            		 //weightedAvg=Math.rint(weightedAvg);
							            		 objProductMasterService.funProductUpdateCostRM(weightedAvg, ob.getStrProdCode(), clientCode);
						            	}
						            	if(ob.getStrExpiry().equalsIgnoreCase("y") && ob.getStrExpiry()!=null)
					            		{
					            			clsGRNDtlModel BatchModel=new clsGRNDtlModel();
					            			BatchModel.setStrGRNCode(grnCode);
					            			BatchModel.setStrProdCode(ob.getStrProdCode());
					            			BatchModel.setStrProdName(ob.getStrProdName());
					            			BatchModel.setDblQty(ob.getDblQty());
					            			batchList.add(BatchModel);
					            		}
						            	
						            	flagDtlDataInserted=true;
					            	}
					            }
					            
					            List<clsGRNTaxDtlModel> listGRNTaxDtlModel=objBean.getListGRNTaxDtl();
				    			if(null!=listGRNTaxDtlModel)
				    			{
				    				objGRNService.funDeleteGRNTaxDtl(objBean.getStrGRNCode(), clientCode);
				    				for(clsGRNTaxDtlModel obTaxDtl:listGRNTaxDtlModel)
				    				{
				    					if(null!=obTaxDtl.getStrTaxCode())
				    					{
					    					obTaxDtl.setStrGRNCode(grnCode);
					    					obTaxDtl.setStrClientCode(clientCode);
					    					objGRNService.funAddUpdateGRNTaxDtl(obTaxDtl);
				    					}
				    				}
				    			}
					            
					            objHdModel.setDblValueTotal(totalValue);
					            objGRNService.funAddUpdate(objHdModel);
					            if(flagDtlDataInserted)
					            {
					            	String strGeneratedMISCode=funSaveNONStkMIS(objBean,objHdModel.getStrGRNCode(),request);
					            	request.getSession().setAttribute("success", true);
					            	request.getSession().setAttribute("successMessage","GRN Code : ".concat(objHdModel.getStrGRNCode()));
					            	if(strGeneratedMISCode!="")
					            	{
					            		request.getSession().setAttribute("successMessageMIS","MIS Code : ".concat(strGeneratedMISCode));
					            	}
					            	request.getSession().setAttribute("rptGRNCode",objHdModel.getStrGRNCode());
					            	if(!batchList.isEmpty())
					            	{
					            		request.getSession().setAttribute("BatchProcessList",batchList);			            		
					            	}
					            	else
					            	{
					            		request.getSession().setAttribute("BatchProcessList",null);
					            	}			            	
					            	request.getSession().setAttribute("strmsg",strmsg);
					            }
					        }
						
					clsCompanyMasterModel objCompModel = objSetupMasterService.funGetObject(clientCode);
					if( objCompModel.getStrWebBookModule().equals("Yes") )
						{
						   funGenrateJVforGRN( objHdModel,listGRNDtl,objBean.getListGRNTaxDtl(),clientCode,userCode,propCode,request);
						}
							
							
					
					return ("redirect:/frmGRN.html?saddr="+urlHits);
				}
				else
				{
					return ("redirect:/frmGRN.html?saddr="+urlHits);
				}
		}
		
		/**
		 * NonStockable Issue Function Logic
		 * @param objBean
		 * @param strGRNCode
		 * @param req
		 * @return
		 */
		@SuppressWarnings("rawtypes")
		private String funSaveNONStkMIS(clsGRNBean objBean,String strGRNCode,HttpServletRequest req)
		{
			objGlobal=new clsGlobalFunctions();
			String userCode=req.getSession().getAttribute("usercode").toString();
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String propCode=req.getSession().getAttribute("propertyCode").toString();
			String startDate=req.getSession().getAttribute("startDate").toString();
			clsMISHdModel objMISHd = null;
			String strGeneratedMISCode="";
			String strMISCode="";
			String grnDate=objBean.getDtGRNDate();
			
			
			
			for(clsGRNDtlModel objGRNDtl : objBean.getListGRNDtl())
			{
				if(null!=objGRNDtl.getStrProdCode())
				{
					if(objGRNDtl.getStrStkble().equalsIgnoreCase("Yes") && !objGRNDtl.getStrIssueLocation().equals("") && null!=objGRNDtl.getStrStkble())
					{
						String reqCode="";
						String against="Direct";
						String sql="select IfNULL(b.strReqCode,'') from tblpurchaseorderdtl a "
							+ " inner join tblmrpidtl b on a.strPIcode =b.strPIcode and b.strClientCode='"+clientCode+"'"
				 			+ " where a.strPOCode='"+objGRNDtl.getStrCode()+"' and a.strClientCode='"+clientCode+"' "
		 					+ "group by b.strReqCode";
						List list=objGlobalFunctionsService.funGetList(sql,"sql");
						if(!list.isEmpty() && list!=null)
						{
							against="Requisition";
							reqCode=list.get(0).toString();
						}						
						if(objGRNDtl.getStrMISCode().isEmpty())
						{
							strMISCode=objGlobalFunctions.funGenerateDocumentCode("frmMIS", grnDate, req);							
							objMISHd=new clsMISHdModel(new clsMISHdModel_ID(strMISCode,clientCode));
							objMISHd.setIntid(0);
							objMISHd.setStrUserCreated(userCode);
							objMISHd.setDtCreatedDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
						}
						else
						{
							strMISCode=objGRNDtl.getStrMISCode();
							objMISHd=new clsMISHdModel(new clsMISHdModel_ID(objGRNDtl.getStrMISCode().trim(),clientCode));
						}
						
						objMISHd.setStrMISCode(strMISCode);
						objMISHd.setStrLocFrom(objBean.getStrLocCode());
				    	objMISHd.setStrLocTo(objGRNDtl.getStrIssueLocation());
				    	objMISHd.setDtMISDate(objGlobal.funGetDate("yyyy-MM-dd", grnDate));
				    	objMISHd.setStrUserModified(userCode);
				    	objMISHd.setStrNarration("Generated GRN Code:-"+strGRNCode);
				    	objMISHd.setDtLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				    	objMISHd.setStrAuthorise("Yes");
						objMISHd.setDblTotalAmt(objGRNDtl.getDblTotalPrice());
						objMISHd.setStrAgainst(against);
						objMISHd.setStrReqCode(reqCode);
						
						clsMISDtlModel objMISDtlModel=new clsMISDtlModel();
						objMISDtlModel.setStrProdCode(objGRNDtl.getStrProdCode());
						objMISDtlModel.setDblQty(objGRNDtl.getDblQty());
						objMISDtlModel.setDblUnitPrice(objGRNDtl.getDblUnitPrice());
						objMISDtlModel.setDblTotalPrice(objGRNDtl.getDblTotalPrice());
						objMISDtlModel.setStrRemarks(objGRNDtl.getStrRemarks());
						objMISDtlModel.setStrReqCode(reqCode);
						
						List<clsMISDtlModel> listMISDtlModel=new ArrayList<clsMISDtlModel>();
						listMISDtlModel.add(objMISDtlModel);
						objMISHd.setClsMISDtlModel(listMISDtlModel);
						
						objMISService.funAddMISHd(objMISHd);
						
						String updateQuery="update tblgrndtl set strMISCode='"+strMISCode+"' "
						   	+ "	where strgrnCode = '"+objGRNDtl.getStrGRNCode()+"' and strprodcode = '"+objMISDtlModel.getStrProdCode()+"' "
				    		+ " and strIssueLocation='"+objGRNDtl.getStrIssueLocation()+"' and strClientCode='"+clientCode+"'" ;
						objMISService.funInsertNonStkItemDirect(updateQuery);
						
				    	if(strGeneratedMISCode.length()>0)
						{
							strGeneratedMISCode= strGeneratedMISCode+","+strMISCode;
						}
						else
						{
							strGeneratedMISCode= strMISCode;
						}						
					}
				}
			}
			
			
			
		/*	
		// Collect all non stockable items in one map (according to issue location) 	
			Map<String,List<clsMISDtlModel>> hmIssueLocMISDtl=new HashMap<String,List<clsMISDtlModel>>();
			Map<String,List<String>> hmIssueLocMISCode=new HashMap<String,List<String>>();
			
			for(clsGRNDtlModel objGRNDtl : objBean.getListGRNDtl())
			{
				if(null!=objGRNDtl.getStrProdCode())
				{
					if(objGRNDtl.getStrStkble().equalsIgnoreCase("Yes") && !objGRNDtl.getStrIssueLocation().equals("") && null!=objGRNDtl.getStrStkble())
					{
						String reqCode="";
						String sql="select IfNULL(b.strReqCode,'') from tblpurchaseorderdtl a "
							+ " inner join tblmrpidtl b on a.strPIcode =b.strPIcode and b.strClientCode='"+clientCode+"'"
				 			+ " where a.strPOCode='"+objGRNDtl.getStrCode()+"' and a.strClientCode='"+clientCode+"' "
		 					+ "group by b.strReqCode";
						List list=objGlobalFunctionsService.funGetList(sql,"sql");
						if(!list.isEmpty() && list!=null)
						{
							reqCode=list.get(0).toString();
						}
						
						clsMISDtlModel objMISDtlModel=new clsMISDtlModel();
						
						objMISDtlModel.setStrProdCode(objGRNDtl.getStrProdCode());
						objMISDtlModel.setDblQty(objGRNDtl.getDblQty());
						objMISDtlModel.setDblUnitPrice(objGRNDtl.getDblUnitPrice());
						objMISDtlModel.setDblTotalPrice(objGRNDtl.getDblTotalPrice());
						objMISDtlModel.setStrRemarks(objGRNDtl.getStrRemarks());
						objMISDtlModel.setStrReqCode(reqCode);
						
						if(null==hmIssueLocMISDtl.get(objGRNDtl.getStrIssueLocation()))
						{
							List<clsMISDtlModel> listMISDtl=new ArrayList<clsMISDtlModel>();
							listMISDtl.add(objMISDtlModel);
							hmIssueLocMISDtl.put(objGRNDtl.getStrIssueLocation(),listMISDtl);
							
							List<String> listDocCodes=new ArrayList<String>();
							listDocCodes.add(objGRNDtl.getStrMISCode());
							listDocCodes.add(objGRNDtl.getStrGRNCode());
							hmIssueLocMISCode.put(objGRNDtl.getStrIssueLocation(),listDocCodes);
						}
						else
						{
							List<clsMISDtlModel> listMISDtl=hmIssueLocMISDtl.get(objGRNDtl.getStrIssueLocation());
							listMISDtl.add(objMISDtlModel);
							//hmIssueLocMISDtl.remove(objGRNDtl.getStrIssueLocation());
							hmIssueLocMISDtl.put(objGRNDtl.getStrIssueLocation(),listMISDtl);
							
							List<String> listDocCodes=new ArrayList<String>();
							listDocCodes.add(objGRNDtl.getStrMISCode());
							listDocCodes.add(objGRNDtl.getStrGRNCode());
							hmIssueLocMISCode.put(objGRNDtl.getStrIssueLocation(),listDocCodes);
						}						
					}
				}
			}
			
			
		// Iterate non stockable items and generate MIS hd and dtl models to save into db	
			for (Map.Entry<String, List<clsMISDtlModel>> entry : hmIssueLocMISDtl.entrySet()) {
				//System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
				
				String issueLocCode=entry.getKey();
				List<String> listDocCodes=hmIssueLocMISCode.get(issueLocCode);
				String misCode=listDocCodes.get(0);
				String grnCode=listDocCodes.get(1);
				
				if(misCode.length()==0)
				{	
					strMISCode=objGlobalFunctions.funGenerateDocumentCode("frmMIS", grnDate, req);
					
					objMISHd=new clsMISHdModel(new clsMISHdModel_ID(strMISCode,clientCode));
					objMISHd.setIntid(0);
					objMISHd.setStrUserCreated(userCode);
					objMISHd.setDtCreatedDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				}
				else
				{
					objMISHd=new clsMISHdModel(new clsMISHdModel_ID(misCode,clientCode));
				}
				String strReqCode="";
				String strAgainst="Direct";
		    	objMISHd.setStrLocFrom(objBean.getStrLocCode());
		    	objMISHd.setStrLocTo(issueLocCode);
		    	objMISHd.setDtMISDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    	objMISHd.setStrUserModified(userCode);
		    	objMISHd.setStrNarration("Generated GRN Code:-"+strGRNCode);
		    	objMISHd.setDtLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    	objMISHd.setStrAuthorise("Yes");
				
		    	double totalAmt=0;
		    	List<clsMISDtlModel> listMISDtl=entry.getValue();
		    	for(clsMISDtlModel objMISDtl:listMISDtl)
		    	{
		    		if(!objMISDtl.getStrReqCode().isEmpty())
		    		{
		    			strReqCode=objMISDtl.getStrReqCode();
		    			strAgainst="Requisition";
		    		}
		    		totalAmt+=objMISDtl.getDblTotalPrice();
		    		
		    		String updateQuery="update tblgrndtl set strMISCode='"+strMISCode+"' "
				    	+ "	where strgrnCode = '"+grnCode+"' and strprodcode = '"+objMISDtl.getStrProdCode()+"' "
		    			+ " and strIssueLocation='"+issueLocCode+"' and strClientCode='"+clientCode+"'" ;
		    		objMISService.funInsertNonStkItemDirect(updateQuery);
		    	}
		    	objMISHd.setDblTotalAmt(totalAmt);
		    	objMISHd.setStrReqCode(strReqCode);
		    	objMISHd.setStrAgainst(strAgainst);
		    	objMISHd.setClsMISDtlModel(listMISDtl);
		    	
		    	objMISService.funAddMISHd(objMISHd);
		    	
		    	if(strGeneratedMISCode.length()>0)
				{
					strGeneratedMISCode= strGeneratedMISCode+","+strMISCode;
				}
				else
				{
					strGeneratedMISCode= strMISCode;
				}
			}
			*/
			
			
			
			
			/*
			List<clsGRNDtlModel> grndtl=objBean.getListGRNDtl();
			for(clsGRNDtlModel GRNDtlModel :grndtl)
			{
				if(GRNDtlModel.getStrProdCode()!=null)
				{
					if(GRNDtlModel.getStrStkble().equalsIgnoreCase("Yes") && !GRNDtlModel.getStrIssueLocation().equals("") && GRNDtlModel.getStrStkble()!=null )
					{
						if(GRNDtlModel.getStrMISCode().length()==0)
						{
							long lastNo=objGlobalFunctionsService.funGetLastNo("tblmishd","MaterailReq","intid", clientCode);
							String year=objGlobal.funGetSplitedDate(startDate)[2];
							String cd=objGlobal.funGetTransactionCode("MI",propCode,year);
							strMISCode = cd + String.format("%06d", lastNo);
							objMISHd=new clsMISHdModel(new clsMISHdModel_ID(strMISCode,clientCode));
							objMISHd.setIntid(lastNo);
							objMISHd.setStrUserCreated(userCode);
							objMISHd.setDtCreatedDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
						}
						else
						{
							objMISHd=new clsMISHdModel(new clsMISHdModel_ID(GRNDtlModel.getStrMISCode(),clientCode));
							strMISCode=GRNDtlModel.getStrMISCode();
						}
						String strReqCode="";
						String strAgainst="Direct";
				    	objMISHd.setStrLocFrom(objBean.getStrLocCode());
				    	objMISHd.setStrLocTo(GRNDtlModel.getStrIssueLocation());
				    	objMISHd.setDtMISDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				    	objMISHd.setStrUserModified(userCode);
				    	objMISHd.setStrNarration("Generated GRN Code:-"+strGRNCode);
				    	objMISHd.setDtLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));	
				    	objMISHd.setStrAuthorise("Yes");
			    		
			            List<clsMISDtlModel> MISLIST=new ArrayList<clsMISDtlModel>();
			            String sql="select IfNULL(b.strReqCode,'') from tblpurchaseorderdtl a "
			 				+ " inner join tblmrpidtl b on a.strPIcode =b.strPIcode and b.strClientCode='"+clientCode+"'"
			 				+ " where a.strPOCode='"+GRNDtlModel.getStrCode()+"' and a.strClientCode='"+clientCode+"' group by b.strReqCode";
						List list=objGlobalFunctionsService.funGetList(sql,"sql");
						if(!list.isEmpty() && list!=null)
						{
							strAgainst="Requisition";
							strReqCode=list.get(0).toString();
						}
						objMISHd.setStrAgainst(strAgainst);
						objMISHd.setStrReqCode(strReqCode);
						
						clsMISDtlModel MISModel=new clsMISDtlModel();
						if(null!=GRNDtlModel.getStrProdCode())
						{
				    		MISModel.setStrProdCode(GRNDtlModel.getStrProdCode());
				    		MISModel.setDblQty(GRNDtlModel.getDblQty());
				    		MISModel.setDblUnitPrice(GRNDtlModel.getDblUnitPrice());
				    		MISModel.setDblTotalPrice(GRNDtlModel.getDblTotalPrice());
				    		MISModel.setStrRemarks(GRNDtlModel.getStrRemarks());
				    		MISModel.setStrReqCode(strReqCode);
				    		MISLIST.add(MISModel);
				    		objMISHd.setClsMISDtlModel(MISLIST);
						}
			    	    objMISService.funAddMISHd(objMISHd);
			    		    
			    	    if(strGeneratedMISCode.length()>0)
						{
							strGeneratedMISCode= strGeneratedMISCode+","+strMISCode;
						}
						else
						{
							strGeneratedMISCode= strMISCode;
						}
			    	    String updateQuery="update tblgrndtl set strMISCode='"+strMISCode+"' "
			    	    	+ "	where strgrnCode = '"+strGRNCode+"' and strprodcode = '"+GRNDtlModel.getStrProdCode()+"' and strIssueLocation='"+GRNDtlModel.getStrIssueLocation()+"' and strClientCode='"+clientCode+"'" ;
						objMISService.funInsertNonStkItemDirect(updateQuery);
					}
				}
			}*/

			return strGeneratedMISCode;
		}
		
		@SuppressWarnings("finally")
		@RequestMapping(value = "/loadProductDataWithTax", method = RequestMethod.GET)
		public @ResponseBody clsProductMasterModel funAssignFields(@RequestParam("prodCode")  String prodCode,HttpServletRequest request)
		{
			clsProductMasterModel objProdMasterModel=null;
			try
			{
				String clientCode=request.getSession().getAttribute("clientCode").toString();
				objGlobal = new clsGlobalFunctions();
				//For Accoding to Bar Code checking length
				if(prodCode.length()>8)
				{
					objProdMasterModel=objProductMasterService.funGetBarCodeProductObject(prodCode,clientCode);
				}else
				{
					objProdMasterModel=objProductMasterService.funGetObject(prodCode,clientCode);
				}
				
				//objProdMasterModel=objProductMasterService.funGetObject(prodCode,clientCode);
				if(null == objProdMasterModel)
				{
					objProdMasterModel=new clsProductMasterModel();
					objProdMasterModel.setStrProdCode("Invalid Code");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				return objProdMasterModel;
			}
		}
		
	
		@RequestMapping(value = "/loadProductDataWithSuppWiseRate", method = RequestMethod.GET)
		public @ResponseBody clsProductMasterModel funGetSupplierWiseObject(@RequestParam("prodCode")  String prodCode,@RequestParam("suppCode")  String strSuppCode,HttpServletRequest request)
		{
			String clientCode=request.getSession().getAttribute("clientCode").toString();						
			clsProductMasterModel objProdMasterModel=objProductMasterService.funGetSupplierWiseObject(strSuppCode,prodCode,clientCode);
			return objProdMasterModel;
		}
		
		@RequestMapping(value = "/loadMultiPODiscount", method = RequestMethod.GET)
		private @ResponseBody clsPurchaseOrderHdModel funGetPOHdData(@RequestParam("POCodes")  String POCodes,HttpServletRequest request)
		{
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			clsPurchaseOrderHdModel POModel=new clsPurchaseOrderHdModel();
			double disCountAmt=0;
			double extraCharge=0;
			String strDNCodes[] = POCodes.split(",");
			for(int i=0;i<strDNCodes.length;i++)
			{
				clsPurchaseOrderHdModel objPurchaseOrderHdModel=objPurchaseOrderService.funGetObject(strDNCodes[i],clientCode);
				extraCharge=extraCharge+objPurchaseOrderHdModel.getDblExtra();
				disCountAmt=disCountAmt+objPurchaseOrderHdModel.getDblDiscount();
			}
			POModel.setDblDiscount(disCountAmt);
			POModel.setDblExtra(extraCharge);
			return POModel;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value = "/loadProductDataForPOTrans", method = RequestMethod.GET)
		public @ResponseBody List funLoadProductForPO(@RequestParam("prodCode") String prodCode,@RequestParam("POCode") String POCode
				,HttpServletRequest req)
		{		
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String sql="";
			
			sql="select a.strProdCode,a.strProdName,e.dblPrice,a.dblWeight,a.strBinNo,a.strReceivedUOM,"
					+ " a.strExpDate,a.strNonStockableItem,"
					+ " e.dblOrdQty-ifnull(sum(f.dblQty),0) as dblOrdQty,e.strPOCode"
					+ "	from tblproductmaster a,tblpurchaseorderdtl e"
					+ " left outer join tblgrndtl f  on f.strCode=e.strPOCode and f.strCode='"+POCode+"' "
					+ "	and f.strClientCode='"+clientCode+"' "
					+ " where  e.strProdCode=a.strProdCode and  e.strProdCode='"+prodCode+"' and e.strPOCode='"+POCode+"'  "
					+ " and a.strClientCode='"+clientCode+"' and e.strClientCode='"+clientCode+"'"
					+ "	group by a.strProdCode";
			List list=objGlobalFunctionsService.funGetList(sql, "sql");
			
			List ProdList=new ArrayList();
			if(list.size()>0)
			{
				Object[] ob=(Object[])list.get(0);
				
				ProdList.add(ob[0].toString());
				ProdList.add(ob[1].toString());
				ProdList.add(Double.parseDouble(ob[2].toString()));
				ProdList.add(ob[3].toString());
				ProdList.add(ob[4].toString());
				ProdList.add(ob[5].toString());
				ProdList.add(ob[6].toString());
				ProdList.add(ob[7].toString());
				ProdList.add(Double.parseDouble(ob[8].toString()));
				ProdList.add(ob[9].toString());
			}
			else
			{
				ProdList=new ArrayList();
				ProdList.add("Invalid Code ");
			}
			return ProdList;
		}
		
		
		@RequestMapping(value = "/loadAgainstPO", method = RequestMethod.GET)
		public @ResponseBody List<clsGRNDtlModel> funAssignFields1(@RequestParam("POCode")  String POCode,HttpServletRequest request) 
		{
			objGlobal = new clsGlobalFunctions();
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			listPOInGRN=objGRNService.funGetDtlListAgainst(POCode, clientCode , "clsPurchaseOrderDtl");
			
			List<clsGRNDtlModel> listGRNDtlModel=new  ArrayList<clsGRNDtlModel>();
			for(int i=0;i<listPOInGRN.size();i++)
	        {
				clsGRNDtlModel objGRNDtlModel=new clsGRNDtlModel();
	        	Object[] ob = (Object[])listPOInGRN.get(i);
	        	objGRNDtlModel.setStrCode(ob[1].toString());
	        	objGRNDtlModel.setStrProdCode(ob[0].toString());
	        	double pendingQty=Double.parseDouble(ob[4].toString());
	        	objGRNDtlModel.setDblQty(pendingQty);
	        	double totalPrice=pendingQty*(Double.parseDouble(ob[5].toString()));
	        	objGRNDtlModel.setDblTotalPrice(totalPrice-Double.parseDouble(ob[10].toString()));
	        	double totalweight=pendingQty*(Double.parseDouble(ob[6].toString()));
	        	objGRNDtlModel.setDblTotalWt(totalweight);
	        	objGRNDtlModel.setStrUOM(ob[7].toString());
	        	objGRNDtlModel.setStrProdName(ob[8].toString());
	        	objGRNDtlModel.setDblRate(Double.parseDouble(ob[5].toString()));
	        	objGRNDtlModel.setDblWeight(Double.parseDouble(ob[6].toString()));
	        	objGRNDtlModel.setStrExpiry(ob[9].toString());
	        	objGRNDtlModel.setDblDiscount(Double.parseDouble(ob[10].toString()));
	        	objGRNDtlModel.setStrIssueLocation(ob[11].toString());
	        	objGRNDtlModel.setStrIsueLocName(ob[12].toString());
	        	objGRNDtlModel.setStrStkble(ob[13].toString());
	        	objGRNDtlModel.setStrReqCode(ob[14].toString());
	        	listGRNDtlModel.add(objGRNDtlModel);
	        }
			return listGRNDtlModel;
		}
		@RequestMapping(value = "/loadAgainstShortSupply", method = RequestMethod.GET)
		public @ResponseBody List<clsGRNDtlModel> funAssignFields4(@RequestParam("POCode")  String POCode,HttpServletRequest request) 
		{
			objGlobal = new clsGlobalFunctions();
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			listPOInGRN=objGRNService.funGetDtlListAgainst(POCode, clientCode, "clsPurchaseOrderDtl");
			List<clsGRNDtlModel> listGRNDtlModel=new  ArrayList<clsGRNDtlModel>();
			for(int i=0;i<listPOInGRN.size();i++)
	        {
				clsGRNDtlModel objGRNDtlModel=new clsGRNDtlModel();
	        	Object[] ob = (Object[])listPOInGRN.get(i);
	        	objGRNDtlModel.setStrCode(ob[1].toString());
	        	objGRNDtlModel.setStrProdCode(ob[0].toString());
	        	double pendingQty=Double.parseDouble(ob[4].toString());
	        	objGRNDtlModel.setDblQty(pendingQty);
	        	double totalPrice=pendingQty*(Double.parseDouble(ob[5].toString()));
	        	objGRNDtlModel.setDblTotalPrice(totalPrice-Double.parseDouble(ob[10].toString()));
	        	double totalweight=pendingQty*(Double.parseDouble(ob[6].toString()));
	        	objGRNDtlModel.setDblTotalWt(totalweight);
	        	objGRNDtlModel.setStrUOM(ob[7].toString());
	        	objGRNDtlModel.setStrProdName(ob[8].toString());
	        	objGRNDtlModel.setDblRate(Double.parseDouble(ob[5].toString()));
	        	objGRNDtlModel.setDblWeight(Double.parseDouble(ob[6].toString()));
	        	objGRNDtlModel.setStrExpiry(ob[9].toString());
	        	objGRNDtlModel.setDblDiscount(Double.parseDouble(ob[10].toString()));
	        	objGRNDtlModel.setStrIssueLocation(ob[11].toString());
	        	objGRNDtlModel.setStrIsueLocName(ob[12].toString());
	        	objGRNDtlModel.setStrStkble(ob[13].toString());
	        	listGRNDtlModel.add(objGRNDtlModel);
	        }
			return listGRNDtlModel;
		}
		
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/loadAgainstPR", method = RequestMethod.GET)
		public @ResponseBody List funAssignFields2(@RequestParam("PRCode")  String PRCode,HttpServletRequest request)
		{
			objGlobal = new clsGlobalFunctions();
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			List<clsPurchaseReturnDtlModel> listPRDtlModel=new  ArrayList<clsPurchaseReturnDtlModel>();
			List tempPRList=objGlobalFunctionsService.funGetPurchaseReturnList(PRCode, clientCode);
			for(int i=0;i<tempPRList.size();i++)
	        {
				clsPurchaseReturnDtlModel objPRDtlModel=new clsPurchaseReturnDtlModel();
	        	Object[] ob = (Object[])tempPRList.get(i);
	        	clsPurchaseReturnDtlModel prDtl=(clsPurchaseReturnDtlModel)ob[0];
	        	clsProductMasterModel prodMaster=(clsProductMasterModel)ob[1];
	        	objPRDtlModel.setStrPRCode(prDtl.getStrPRCode());
	        	objPRDtlModel.setStrProdCode(prDtl.getStrProdCode());
	        	objPRDtlModel.setDblQty(prDtl.getDblQty());
	        	objPRDtlModel.setStrProdName(prodMaster.getStrProdName());
	        	objPRDtlModel.setDblWeight(prodMaster.getDblWeight());
	        	objPRDtlModel.setDblTotalPrice(prodMaster.getDblCostRM()*prDtl.getDblQty());
	        	objPRDtlModel.setDblTotalWt(prodMaster.getDblWeight()*prDtl.getDblQty());
	        	objPRDtlModel.setStrUOM(prodMaster.getStrUOM());
	        	objPRDtlModel.setDblUnitPrice(prodMaster.getDblCostRM());
	        	objPRDtlModel.setStrExpiry(prodMaster.getStrExpDate());
	        	listPRDtlModel.add(objPRDtlModel);
	        }
			return listPRDtlModel;
		}
		
		
		@SuppressWarnings("rawtypes")
		private boolean funAddProdSuppMaster(List listProdSupp,String clientCode,double lastPrice,String suppCode,String prodCode,String userCode,String binNo,String strGRNCode)
		{
			boolean flgInsert=false;
			objGlobal = new clsGlobalFunctions();
			if(listProdSupp.size()>0)
			{
			
				clsProdSuppMasterModel objProdSupp=(clsProdSuppMasterModel)listProdSupp.get(0);
				objGRNService.funDeleteProdSupp(objProdSupp.getStrSuppCode(),objProdSupp.getStrProdCode(), clientCode);
				objProdSupp.setDblLastCost(lastPrice);
				objProdSupp.setDtLastDate(objGlobal.funGetCurrentDate("yyyy-MM-dd"));
				objProdSuppService.funAddUpdateProdSupplier(objProdSupp);
			}
			else
			{
				clsProdSuppMasterModel objProdSupp=new clsProdSuppMasterModel();
				objGRNService.funDeleteProdSupp(objProdSupp.getStrSuppCode(),objProdSupp.getStrProdCode(), clientCode);
				objProdSupp.setStrSuppCode(suppCode);
				objProdSupp.setStrProdCode(prodCode);
				objProdSupp.setStrClientCode(clientCode);
				objProdSupp.setDblMaxQty(0.00);
				objProdSupp.setStrDefault("Y");
				objProdSupp.setStrLeadTime("");
				objProdSupp.setStrSuppPartDesc("");
				objProdSupp.setStrSuppPartNo("");
				objProdSupp.setStrSuppUOM("");
				objProdSupp.setDblLastCost(lastPrice);
				objProdSupp.setDtLastDate(objGlobal.funGetCurrentDate("yyyy-MM-dd"));
				String sql="update tblprodsuppmaster set strDefault='N' where  strProdCode='"+prodCode+"' and strClientCode='"+clientCode+"'";
				objGlobalFunctionsService.funUpdate(sql,"sql");
				objProdSuppService.funAddUpdateProdSupplier(objProdSupp);
				
			}
			
			
			return flgInsert;
		}
		
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private clsGRNHdModel funPrepareModel(clsGRNBean objBean,HttpServletRequest request)
		{
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			String userCode=request.getSession().getAttribute("usercode").toString();
			boolean res=false;
			if(null!=request.getSession().getAttribute("hmAuthorization"))
			{
				HashMap<String,Boolean> hmAuthorization=(HashMap)request.getSession().getAttribute("hmAuthorization");
				if(hmAuthorization.get("GRN"))
				{
					res=true;
				}
			}
			
			objGlobal=new clsGlobalFunctions();
			long lastNo=0;
			String strDocNo="";
			clsGRNHdModel objHdModel = new clsGRNHdModel();
			if(objBean.getStrGRNCode().trim().length()==0)
			{
				/*lastNo=objGlobalFunctionsService.funGetLastNo("tblgrnhd","GRNCode","intId", clientCode);
				String year=objGlobal.funGetSplitedDate(startDate)[2];
				String cd=objGlobal.funGetTransactionCode("GR",propCode,year);
				String code = cd + String.format("%06d", lastNo);*/
				strDocNo=objGlobalFunctions.funGenerateDocumentCode("frmGRN", objBean.getDtGRNDate(), request);
				objHdModel.setStrGRNCode(strDocNo);
				objHdModel.setIntId(lastNo);
				objHdModel.setStrUserCreated(userCode);
				objHdModel.setDtDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				
				if(res)
				{
					objHdModel.setStrAuthorise("No");					
				}
				else
				{
					objHdModel.setStrAuthorise("Yes");
				}
				
			}
			else
			{
				 List listGRNHd=objGRNService.funGetObject(objBean.getStrGRNCode(),clientCode);
			        if(listGRNHd.size() == 0){
			        	/*lastNo=objGlobalFunctionsService.funGetLastNo("tblgrnhd","GRNCode","intId", clientCode);
						String year=objGlobal.funGetSplitedDate(startDate)[2];
						String cd=objGlobal.funGetTransactionCode("GR",propCode,year);
						String code = cd + String.format("%06d", lastNo);*/
			        	strDocNo=objGlobalFunctions.funGenerateDocumentCode("frmGRN", objBean.getDtGRNDate(), request);
						objHdModel.setStrGRNCode(strDocNo);
						objHdModel.setIntId(lastNo);
						objHdModel.setStrUserCreated(userCode);
						objHdModel.setDtDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
						if(res)
						{
							objHdModel.setStrAuthorise("No");
							
						}
						else
						{
							objHdModel.setStrAuthorise("Yes");
						}
			        }
			        else
			        {
			        	
			        	if(objGlobalFunctions.funCheckAuditFrom("frmGRN", request))
						{
								funSaveAudit(objBean.getStrGRNCode(),"Edit",request);
						}
			        	objHdModel.setStrGRNCode(objBean.getStrGRNCode());
			        }
			}
			
			objHdModel.setStrPONo(objBean.getStrPONo());
			objHdModel.setStrNo(objBean.getStrGRNNo());
			objHdModel.setDtGRNDate(objGlobal.funGetDate("yyyy-MM-dd", objBean.getDtGRNDate()));
			objHdModel.setStrSuppCode(objBean.getStrSuppCode());
			objHdModel.setStrLocCode(objBean.getStrLocCode());
			objHdModel.setStrBillNo(objBean.getStrBillNo());
			objHdModel.setDtRefDate(objGlobal.funGetDate("yyyy-MM-dd",objBean.getDtRefDate()));
			objHdModel.setDtBillDate(objGlobal.funGetDate("yyyy-MM-dd",objBean.getDtBillDate()));
			objHdModel.setDtDueDate(objGlobal.funGetDate("yyyy-MM-dd",objBean.getDtDueDate()));
			objHdModel.setStrRefNo(objBean.getStrRefNo());
			objHdModel.setStrAgainst(objBean.getStrAgainst());
			objHdModel.setStrPONo(objGlobal.funIfNull(objBean.getStrPONo()," ",objBean.getStrPONo()));
			objHdModel.setStrPayMode(objBean.getStrPayMode());
			objHdModel.setStrTimeInOut(objBean.getStrTimeInOut());
			objHdModel.setDblSubTotal(objBean.getDblSubTotal());
			String conversion=objGlobal.funIfNull(String.valueOf(objBean.getDblConversion()),"1.0000",String.valueOf(objBean.getDblConversion()));
			objHdModel.setDblConversion(Double.parseDouble(conversion));
			objHdModel.setDblDisRate(objBean.getDblDisRate());
			objHdModel.setDblDisAmt(objBean.getDblDisAmt());
			objHdModel.setDblTaxAmt(objBean.getDblTaxAmt());
			objHdModel.setDblExtra(objBean.getDblExtra());
			double totalAmt=objBean.getDblTotal();
			objHdModel.setDblTotal(totalAmt);
			objHdModel.setDblLessAmt(objBean.getDblLessAmt());
			objHdModel.setStrVehNo(objGlobal.funIfNull(objBean.getStrVehNo()," ",objBean.getStrVehNo()));
			objHdModel.setStrMInBy(objGlobal.funIfNull(objBean.getStrMInBy()," ",objBean.getStrMInBy()));
			objHdModel.setStrNarration(objGlobal.funIfNull(objBean.getStrNarration()," ",objBean.getStrNarration()));
			objHdModel.setStrCurrency(objGlobal.funIfNull(objBean.getStrCurrency()," ",objBean.getStrCurrency()));
			objHdModel.setStrShipmentMode(objGlobal.funIfNull(objBean.getStrShipmentMode()," ",objBean.getStrShipmentMode()));
			objHdModel.setStrUserModified(userCode);
			objHdModel.setDtLastModified(objGlobal.funGetCurrentDate("yyyy-MM-dd"));
			objHdModel.setStrClientCode(clientCode);
			return objHdModel;
		}

		
		@SuppressWarnings("rawtypes")
		private clsGRNBean funPrepareBean(List listGRNHd)
		{
			objGlobal=new clsGlobalFunctions();
			clsGRNBean objBean=new clsGRNBean();
			Object[] ob = (Object[])listGRNHd.get(0);
			clsGRNHdModel tempGRNHd=(clsGRNHdModel)ob[0];
			clsSupplierMasterModel supplierMaster=(clsSupplierMasterModel)ob[1];
			clsLocationMasterModel locMaster=(clsLocationMasterModel)ob[2];
			
			objBean.setStrGRNCode(tempGRNHd.getStrGRNCode());
			objBean.setStrGRNNo(tempGRNHd.getStrNo());
			objBean.setDtGRNDate(objGlobal.funGetDate("yyyy/MM/dd",tempGRNHd.getDtGRNDate()));
			objBean.setDtDueDate(objGlobal.funGetDate("yyyy/MM/dd",tempGRNHd.getDtDueDate()));
			objBean.setDtRefDate(objGlobal.funGetDate("yyyy/MM/dd",tempGRNHd.getDtRefDate()));
			objBean.setDtBillDate(objGlobal.funGetDate("yyyy/MM/dd",tempGRNHd.getDtBillDate()));
			objBean.setStrSuppCode(supplierMaster.getStrPCode());
			objBean.setStrSuppName(supplierMaster.getStrPName());
			objBean.setStrAgainst(tempGRNHd.getStrAgainst());
			objBean.setStrPayMode(tempGRNHd.getStrPayMode());
			objBean.setStrRefNo(tempGRNHd.getStrRefNo());
			objBean.setStrLocCode(tempGRNHd.getStrLocCode());
			objBean.setStrLocName(locMaster.getStrLocName());
			objBean.setStrNarration(tempGRNHd.getStrNarration());
			objBean.setDblDisRate(tempGRNHd.getDblDisRate());
			objBean.setDblDisAmt(tempGRNHd.getDblDisAmt());
			objBean.setDblConversion(tempGRNHd.getDblConversion());
			objBean.setDblExtra(tempGRNHd.getDblExtra());
			objBean.setDblLessAmt(tempGRNHd.getDblLessAmt());
			objBean.setDblSubTotal(tempGRNHd.getDblSubTotal());
			objBean.setDblTaxAmt(tempGRNHd.getDblTaxAmt());
			objBean.setDblTotal(tempGRNHd.getDblTotal());
			objBean.setStrConsignedCountry(tempGRNHd.getStrConsignedCountry());
			objBean.setStrCountryofOrigin(tempGRNHd.getStrCountryofOrigin());
			objBean.setStrCurrency(tempGRNHd.getStrCurrency());			
			objBean.setStrMInBy(tempGRNHd.getStrMInBy());
			objBean.setStrTimeInOut(tempGRNHd.getStrTimeInOut());
			objBean.setStrVehNo(tempGRNHd.getStrVehNo());
			
			return objBean;
		}
		
		/**
		 * Checking Non Stockable Item
		 */
		@SuppressWarnings({"rawtypes"})
		@RequestMapping(value = "/CheckNonStkItem", method = RequestMethod.GET)
		public @ResponseBody String funCheckNonStkItem(@RequestParam("GRNCode")  String GRNCode,HttpServletRequest request) 
		{
			String flagNonStkItem="false";
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			
			List listTempDtl=objGRNService.funGetDtlList(GRNCode,clientCode);
			 for(int i=0;i<listTempDtl.size();i++)
		        {
		        	Object[] ob = (Object[])listTempDtl.get(i);
		        	clsGRNDtlModel grnDtl=(clsGRNDtlModel)ob[0];
		        	clsProductMasterModel objModel=objProductMasterService.funGetObject(grnDtl.getStrProdCode(),clientCode);
		        	if(objModel.getStrNonStockableItem().equalsIgnoreCase("Yes"))
	            	{
		        		flagNonStkItem="true";
	            	}
		        }
			 
			return flagNonStkItem;
		}
		
		/**
		 * Auditing Function Start
		 * @param POModel
		 * @param req
		 */
		@SuppressWarnings("rawtypes")
		public void funSaveAudit(String strGRNCode,String strTransMode,HttpServletRequest  req)
		{
			try
			{
				String clientCode=req.getSession().getAttribute("clientCode").toString();
				String userCode=req.getSession().getAttribute("usercode").toString();
				List listGRNHd=objGRNService.funGetObject(strGRNCode,clientCode);
		        if(!listGRNHd.isEmpty())
		        {
		        	Object[] obHD=(Object[])listGRNHd.get(0);
		        	clsGRNHdModel GRNModel =(clsGRNHdModel)obHD[0];
		       
					List listTempDtl=objGRNService.funGetDtlList(GRNModel.getStrGRNCode(),clientCode);
					if(null != listTempDtl && listTempDtl.size() > 0)
			        {
						String sql="select count(*)+1 from tblaudithd where left(strTransCode,12)='"+GRNModel.getStrGRNCode()+"' and strClientCode='"+clientCode+"'";
						List list=objGlobalFunctionsService.funGetList(sql,"sql");
						if(!list.isEmpty())
						{
							String count=list.get(0).toString();
							clsAuditHdModel model=funPrepairAuditHdModel(GRNModel);
							if(strTransMode.equalsIgnoreCase("Deleted"))
							{
								model.setStrTransCode(GRNModel.getStrGRNCode());
							}
							else
							{
								model.setStrTransCode(GRNModel.getStrGRNCode()+"-"+count);
							}
							model.setStrClientCode(clientCode);
							model.setStrTransMode(strTransMode);
							model.setStrUserAmed(userCode);
							model.setDtLastModified(objGlobal.funGetCurrentDate("yyyy-MM-dd"));
							model.setStrUserModified(userCode);
							objGlobalFunctionsService.funSaveAuditHd(model);
							List<clsAuditGRNTaxDtlModel> GrnTaxList=funPrepareGRNTaxDtl(GRNModel.getStrGRNCode(), req);
							for(clsAuditGRNTaxDtlModel Taxlist:GrnTaxList)
							{
								clsAuditGRNTaxDtlModel taxModel=new clsAuditGRNTaxDtlModel();
								taxModel.setStrGRNCode(model.getStrTransCode());
								taxModel.setStrClientCode(Taxlist.getStrClientCode());
								taxModel.setStrTaxableAmt(Taxlist.getStrTaxableAmt());
								taxModel.setStrTaxAmt(Taxlist.getStrTaxAmt());
								taxModel.setStrTaxCode(Taxlist.getStrTaxCode());
								taxModel.setStrTaxDesc(Taxlist.getStrTaxCode());
								objGlobalFunctionsService.funSaveAuditTaxDtl(taxModel);
							}
							
							for(int i=0;i<listTempDtl.size();i++)
						    {
						        Object[] ob = (Object[])listTempDtl.get(i);
						        clsGRNDtlModel grnDtl=(clsGRNDtlModel)ob[0];
								clsAuditDtlModel AuditMode=new clsAuditDtlModel();
								AuditMode.setStrTransCode(model.getStrTransCode());
								AuditMode.setStrProdCode(grnDtl.getStrProdCode());
								AuditMode.setStrPICode("");
								AuditMode.setStrUOM("");
								AuditMode.setStrAgainst("");
								AuditMode.setStrRemarks(grnDtl.getStrRemarks());
								AuditMode.setDblDiscount(grnDtl.getDblDiscount());
								AuditMode.setDblQty(grnDtl.getDblQty());
								AuditMode.setDblUnitPrice(grnDtl.getDblUnitPrice());
								AuditMode.setStrTaxType(grnDtl.getStrTaxType());
								AuditMode.setDblTax(grnDtl.getDblTax());
								AuditMode.setDblTaxableAmt(grnDtl.getDblTaxableAmt());
								AuditMode.setDblTaxAmt(grnDtl.getDblTaxAmt());
								AuditMode.setDblTotalPrice(grnDtl.getDblTotalPrice());
								AuditMode.setDblRejected(grnDtl.getDblRejected());
								AuditMode.setDblDCQty(grnDtl.getDblDCQty());
								AuditMode.setDblDCWt(grnDtl.getDblDCWt());
								AuditMode.setDblQtyRbl(grnDtl.getDblQtyRbl());
								AuditMode.setDblPackForw(grnDtl.getDblPackForw());
								AuditMode.setDblPOWeight(grnDtl.getDblPOWeight());
								AuditMode.setStrCode(grnDtl.getStrCode());
								AuditMode.setStrClientCode(grnDtl.getStrClientCode());
								AuditMode.setStrProdChar(grnDtl.getStrGRNProdChar());
								AuditMode.setDblRate(grnDtl.getDblRate());
								AuditMode.setDblValue(grnDtl.getDblValue());
								objGlobalFunctionsService.funSaveAuditDtl(AuditMode);
				            }
						}
						
			        }
		        }
			}
			catch(Exception e)
			{
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		/**
		 * Preparing AuditHd Model
		 * @param GRNModel
		 * @return
		 */
		private clsAuditHdModel funPrepairAuditHdModel(clsGRNHdModel GRNModel)
		{
			clsAuditHdModel AuditHdModel=new clsAuditHdModel();
			if(GRNModel!=null)
			{
				AuditHdModel.setStrTransCode(GRNModel.getStrGRNCode());
				AuditHdModel.setDtTransDate(GRNModel.getDtGRNDate());
				AuditHdModel.setStrTransType("GRN(Good Receiving Note)");
				//AuditHdModel.setStrTransMode("Edit");
				AuditHdModel.setStrAgainst(GRNModel.getStrAgainst());
				AuditHdModel.setStrLocCode(GRNModel.getStrLocCode());
				AuditHdModel.setStrSuppCode(GRNModel.getStrSuppCode());
				AuditHdModel.setStrNarration(GRNModel.getStrNarration());
				AuditHdModel.setStrPayMode(GRNModel.getStrPayMode());
				AuditHdModel.setDblDisRate(GRNModel.getDblDisRate());
				AuditHdModel.setDblDiscount(GRNModel.getDblDisAmt());
				AuditHdModel.setDblTaxAmt(GRNModel.getDblTaxAmt());
				AuditHdModel.setDblExtra(GRNModel.getDblExtra());
				AuditHdModel.setDblSubTotal(GRNModel.getDblSubTotal());
				AuditHdModel.setDblTotalAmt(GRNModel.getDblTotal());
				AuditHdModel.setStrBillNo(GRNModel.getStrBillNo());
				AuditHdModel.setDtBillDate(GRNModel.getDtBillDate());
				AuditHdModel.setDtDueDate(GRNModel.getDtDueDate());
				AuditHdModel.setDtRefDate(GRNModel.getDtRefDate());
				AuditHdModel.setStrMInBy(GRNModel.getStrMInBy());
				AuditHdModel.setStrNo(GRNModel.getStrNo());
				AuditHdModel.setStrRefNo(GRNModel.getStrRefNo());
				AuditHdModel.setStrShipmentMode(GRNModel.getStrShipmentMode());
				AuditHdModel.setStrTimeInOut(GRNModel.getStrTimeInOut());
				AuditHdModel.setStrVehNo(GRNModel.getStrVehNo());
				AuditHdModel.setDtRefDate(GRNModel.getDtRefDate());
				AuditHdModel.setStrAuthorise(GRNModel.getStrAuthorise());
				AuditHdModel.setStrPONo(GRNModel.getStrPONo());
				AuditHdModel.setDtDateCreated(GRNModel.getDtDateCreated());
				AuditHdModel.setStrUserCreated(GRNModel.getStrUserCreated());
				AuditHdModel.setStrLocBy("");
				AuditHdModel.setStrLocOn("");
				AuditHdModel.setDblWOQty(0);
				AuditHdModel.setStrExcise("");
				AuditHdModel.setStrWoCode("");
				AuditHdModel.setStrCloseReq("");
				AuditHdModel.setStrClosePO("");
				AuditHdModel.setStrGRNCode("");
				AuditHdModel.setStrCode("");
				
			}
			return AuditHdModel;
			
		}
		
		@SuppressWarnings({ "rawtypes"})
		public List<clsAuditGRNTaxDtlModel> funPrepareGRNTaxDtl(String grnCode,HttpServletRequest request)
		{
			objGlobal=new clsGlobalFunctions();
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			//String userCode=request.getSession().getAttribute("usercode").toString();
	        
			String sql="select strTaxCode,strTaxDesc,strTaxableAmt,strTaxAmt,strClientCode from clsGRNTaxDtlModel "
					+ " where strGRNCode='"+grnCode+"' and strClientCode='"+clientCode+"' ";
			List list=objGlobalFunctionsService.funGetList(sql,"hql");
			List<clsAuditGRNTaxDtlModel> listGRNTaxDtl=new ArrayList<clsAuditGRNTaxDtlModel>();
			for(int cnt=0;cnt<list.size();cnt++)
			{
				clsAuditGRNTaxDtlModel objTaxDtl=new clsAuditGRNTaxDtlModel();
				Object[] arrObj=(Object[])list.get(cnt);
				objTaxDtl.setStrTaxCode(arrObj[0].toString());
				objTaxDtl.setStrTaxDesc(arrObj[1].toString());
				objTaxDtl.setStrTaxableAmt(Double.parseDouble(arrObj[2].toString()));
				objTaxDtl.setStrTaxAmt(Double.parseDouble(arrObj[3].toString()));
				objTaxDtl.setStrClientCode(arrObj[4].toString());
				listGRNTaxDtl.add(objTaxDtl);
			}
			
			return listGRNTaxDtl;
		}
		
		
		/**
		 * Open GRN Slip After Saving GRN
		 * @param resp
		 * @param req
		 */
		@RequestMapping(value = "/openRptGrnSlip", method = RequestMethod.GET)
		 private void funCallReportOnSubmit( HttpServletResponse resp,HttpServletRequest req)
			{
				String GRNCode=req.getParameter("rptGRNCode").toString();
				//req.getSession().removeAttribute("rptGRNCode");
				String type="pdf";
				funCallReport(GRNCode,type,resp,req);
				 
			}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void funCallReport(String GRNCode,String type,HttpServletResponse resp,HttpServletRequest req)
		{
			try {
				objGlobal=new clsGlobalFunctions();
	            Connection con=objGlobal.funGetConnection(req);
	            String clientCode=req.getSession().getAttribute("clientCode").toString();
	            String companyName=req.getSession().getAttribute("companyName").toString();
	            String propertyCode=req.getSession().getAttribute("propertyCode").toString();
				String userCode=req.getSession().getAttribute("usercode").toString();
				String strFinYear=req.getSession().getAttribute("financialYear").toString();
				clsPropertySetupModel objSetup=objSetupMasterService.funGetObjectPropertySetup(propertyCode, clientCode);
				if(objSetup==null)
				{
					objSetup=new clsPropertySetupModel();
				}
				String reportName=servletContext.getRealPath("/WEB-INF/reports/rptGrnDtlSlip.jrxml"); 
				String imagePath=servletContext.getRealPath("/resources/images/company_Logo.png");
	            String sql=" SELECT g.strGRNCode,  DATE_FORMAT(g.dtGRNDate,'%d-%m-%Y')as dtGRNDate, g.strSuppCode, g.strAgainst, g.strPONo, g.strBillNo, DATE_FORMAT(g.dtBillDate,'%d-%m-%Y') as dtBillDate, "
	            		+ "  DATE_FORMAT(g.dtDueDate,'%d-%m-%Y') as dtDueDate, g.strPayMode, g.dblSubTotal, g.dblDisRate, g.dblDisAmt, g.dblTaxAmt, g.dblExtra, g.dblTotal, "
	            		+ " g.strNarration, g.strLocCode, s.strPCode, s.strPName, s.strBAdd1, s.strBAdd2, s.strBCity, s.strBPin, "
	            		+ " s.strBState, s.strBCountry, g.strNo,g.strRefNo, DATE_FORMAT(g.dtRefDate,'%d-%m-%Y') as dtRefDate,g.dblLessAmt,dblTaxAmt ,g.dblDisRate,g.strNarration ,g.strVehNo "
	            		+ " FROM tblgrnhd AS g INNER JOIN tblpartymaster AS s ON g.strSuppCode = s.strPCode and s.strClientCode='"+clientCode+"'"
	            		+ "	Left outer join tblgrntaxdtl as t on t.strGRNCode=g.strGRNCode and t.strClientCode='"+clientCode+"'"
	            		+ " WHERE g.strGRNCode = '"+GRNCode+"' and g.strClientCode='"+clientCode+"' group by g.strGRNCode ";
	          
	            JasperDesign jd=JRXmlLoader.load(reportName);
	            JRDesignQuery newQuery= new JRDesignQuery();
	            newQuery.setText(sql);
	            jd.setQuery(newQuery);  
	            String sql2="select g.strGRNCode,g.strProdCode,p.strProdName,p.strReceivedUOM,g.dblQty,g.dblRejected,g.dblDiscount,"
	            		+ " g.strTaxType,g.dblTaxableAmt,g.dblTax,dblTaxAmt, g.dblUnitPrice,g.dblWeight,g.strProdChar,g.dblDCQty,"
	            		+ " g.dblDCWt,g.strRemarks,g.dblQtyRbl,g.strGRNProdChar,g.dblPOWeight,g.strCode, g.dblRework,g.dblPackForw,"
	            		+ " g.dblRate,g.dblValue,p.strPartNo,p.dblUnitPrice as stdRate,p.dblUnitPrice*(g.dblQty-g.dblRejected) as stdAmt from tblgrndtl g,tblproductmaster p "
	            		+ " where g.strProdCode=p.strProdCode and g.strGRNCode='"+GRNCode+"' and g.strClientCode='"+clientCode+"' and p.strClientCode='"+clientCode+"' order by p.strProdName ";
	            JRDesignQuery subQuery = new JRDesignQuery();
	            subQuery.setText(sql2);
	            Map<String, JRDataset> datasetMap = jd.getDatasetMap();
	            JRDesignDataset subDataset = (JRDesignDataset) datasetMap.get("dsGrnDtl");
	            subDataset.setQuery(subQuery);
	             
				String Sql3="select a.strTaxDesc,a.strTaxAmt from tblgrntaxdtl a where strGRNCode='"+GRNCode+"' and strClientCode='"+clientCode+"'";
	            
	            JRDesignQuery taxQuery=new JRDesignQuery();
	            taxQuery.setText(Sql3);
	            JRDesignDataset taxDataset = (JRDesignDataset) datasetMap.get("dsTaxDtl");
	            taxDataset.setQuery(taxQuery);
	            JasperReport jr=JasperCompileManager.compileReport(jd);            
	            HashMap hm = new HashMap();
	            hm.put("strCompanyName",companyName);
	            hm.put("strUserCode",userCode);
	            hm.put("strImagePath",imagePath );
	            hm.put("strGRN Code",GRNCode );
	            hm.put("strAddr1",objSetup.getStrAdd1() );
	            hm.put("strAddr2",objSetup.getStrAdd2());            
	            hm.put("strCity", objSetup.getStrCity());
	            hm.put("strState",objSetup.getStrState());
	            hm.put("strCountry", objSetup.getStrCountry());
	            hm.put("strPin", objSetup.getStrPin());
	            hm.put("strFinYear",strFinYear);
	            JasperPrint p=JasperFillManager.fillReport(jr, hm,con);
	            if(type.trim().equalsIgnoreCase("pdf"))
				{
	            	 ServletOutputStream servletOutputStream = resp.getOutputStream();
	                 byte[] bytes = null;
	                 bytes = JasperRunManager.runReportToPdf(jr,hm, con);
	                     resp.setContentType("application/pdf");
	                     resp.setHeader( "Content-Disposition", "inline;filename=" + "rptGRNSlip."+type.trim());
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
		            resp.setHeader( "Content-Disposition", "attachment;filename=" + "rptGRNSlip."+type.trim() );                        
		            exporterXLS.exportReport();  
		            resp.setContentType("application/xlsx");	
		            
				}
	            
	        } catch (Exception e) {
	        e.printStackTrace();
	        logger.error(e.getMessage());
	        }
		}

		/**
		 * Open Report From
		 */
		@RequestMapping(value = "/frmGrnSlip", method = RequestMethod.GET)
		public ModelAndView funOpenMISSlipForm(
				Map<String, Object> model,
				HttpServletRequest request)
	    {
			
			request.getSession().setAttribute("formName","frmWebStockHelpGRNSlip");
			String urlHits = "1";
			try {
				urlHits = request.getParameter("saddr").toString();
			} catch (NullPointerException e) {
				urlHits = "1";
			}
			model.put("urlHits", urlHits);

			if ("2".equalsIgnoreCase(urlHits)) {
				return new ModelAndView("frmGrnSlip_1", "command",
						new clsReportBean());
			} else {
				return new ModelAndView("frmGrnSlip", "command",
						new clsReportBean());
			}
			
	    }
		
		/**
		 * GRN Range Printing Report
		 * @param objBean
		 * @param resp
		 * @param req
		 */
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/rptGrnSlip", method = RequestMethod.GET)	
		private void funReport(@ModelAttribute("command") clsReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
		{
			objGlobal=new clsGlobalFunctions();
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String fromDate=objGlobal.funGetDate("yyyy-MM-dd",objBean.getDtFromDate());
			String toDate=objGlobal.funGetDate("yyyy-MM-dd",objBean.getDtToDate());
			
			String strFromGRNCode=objBean.getStrFromDocCode();
			String strToGRNCode=objBean.getStrToDocCode();
			
			String type=objBean.getStrDocType();
			String tempSupp[] = objBean.getStrSuppCode().split(",");
			String strSuppCodes = "";
			try
			{
				 String sql="select a.strGRNCode from tblgrnhd a where date(a.dtGRNDate) between '"+fromDate+"' and '"+toDate+"' and a.strClientCode='"+clientCode+"' ";
				
				 if(objBean.getStrSuppCode().trim().length()>0)
				 {
					 for (int i = 0; i < tempSupp.length; i++) {
							if (strSuppCodes.length() > 0) {
								strSuppCodes = strSuppCodes + " or a.strSuppCode='" + tempSupp[i]
										+ "' ";
							} else {
								strSuppCodes = " a.strSuppCode='" + tempSupp[i] + "' ";

							}
						}
					 sql = sql	+ " and "+ "("+ strSuppCodes+ ") ";
				 }
				 
				 if(strFromGRNCode.trim().length()>0 && strToGRNCode.trim().length()>0)
				 {
					 sql=sql+" and a.strGRNCode between '"+strFromGRNCode+"' and '"+strToGRNCode+"' ";
				 }
				 
				List list=objGlobalFunctionsService.funGetList(sql,"sql");
				if(list!=null && !list.isEmpty())
				{
					List<JasperPrint> jprintlist =new ArrayList<JasperPrint>();
					 ServletOutputStream servletOutputStream = resp.getOutputStream();
					for(int i=0;i<list.size();i++)
					{
							JasperPrint p=funCallRangePrintReport(list.get(i).toString(),resp,req);
							jprintlist.add(p);
					}
				
					if(type.trim().equalsIgnoreCase("pdf"))
					{
			        	     JRExporter exporter = new JRPdfExporter();
			        	     resp.setContentType("application/pdf");
			                 exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
			                 exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream );
			                 resp.setHeader( "Content-Disposition", "inline;filename=" + "rptGRNSlip."+type.trim());                        
			                 exporter.exportReport();  
			                 servletOutputStream.flush();
		                     servletOutputStream.close();
					}
					else if(type.trim().equalsIgnoreCase("xls"))
					{
						JRExporter exporterXLS = new JRXlsExporter();	 
						exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);  
						exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, resp.getOutputStream() );
			            resp.setHeader( "Content-Disposition", "attachment;filename=" + "rptGRNSlip."+type.trim());                        
			            exporterXLS.exportReport();  
			            resp.setContentType("application/xlsx");	
					}
				}
				else
				{
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					resp.getWriter().append("No Record Found");
				}
			}
			catch(JRException e)
			{
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			catch (IOException e)
			{
				e.printStackTrace();
				logger.error(e.getMessage());
			} 
				
		}
		@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
		public JasperPrint funCallRangePrintReport(String GRNCode,HttpServletResponse resp,HttpServletRequest req)
		{
			objGlobal=new clsGlobalFunctions();
			  Connection con=objGlobal.funGetConnection(req);
			  JasperPrint p=null;
			try {
				String clientCode=req.getSession().getAttribute("clientCode").toString();
	            String companyName=req.getSession().getAttribute("companyName").toString();
	            String propertyCode=req.getSession().getAttribute("propertyCode").toString();
				String userCode=req.getSession().getAttribute("usercode").toString();
				clsPropertySetupModel objSetup=objSetupMasterService.funGetObjectPropertySetup(propertyCode, clientCode);
				if(objSetup==null)
				{
					objSetup=new clsPropertySetupModel();
				}
				String reportName=servletContext.getRealPath("/WEB-INF/reports/rptGrnDtlSlip.jrxml"); 
				String imagePath=servletContext.getRealPath("/resources/images/company_Logo.png");
	               String sql=" SELECT g.strGRNCode, DATE_FORMAT(g.dtGRNDate,'%d-%m-%Y') as dtGRNDate, g.strSuppCode, g.strAgainst, g.strPONo, g.strBillNo, DATE_FORMAT(g.dtBillDate,'%d-%m-%Y') as dtBillDate, DATE_FORMAT(g.dtDueDate,'%d-%m-%Y') "
	               		+ " as dtDueDate, g.strPayMode, g.dblSubTotal, g.dblDisRate, g.dblDisAmt, g.dblTaxAmt, g.dblExtra, g.dblTotal, "
	            		+ " g.strNarration, g.strLocCode, s.strPCode, s.strPName, s.strBAdd1, s.strBAdd2, s.strBCity, s.strBPin, "
	            		+ " s.strBState, s.strBCountry, g.strNo,g.strRefNo, DATE_FORMAT(g.dtRefDate,'%d-%m-%Y') as dtRefDate,g.dblLessAmt,dblTaxAmt ,g.dblDisRate,g.strNarration ,g.strVehNo "
	            		+ " FROM tblgrnhd AS g INNER JOIN tblpartymaster AS s ON g.strSuppCode = s.strPCode and s.strClientCode='"+clientCode+"'"
	            		+ "	Left outer join tblgrntaxdtl as t on t.strGRNCode=g.strGRNCode and t.strClientCode='"+clientCode+"'"
	            		+ " WHERE g.strGRNCode = '"+GRNCode+"' and g.strClientCode='"+clientCode+"'";
	          
	            JasperDesign jd=JRXmlLoader.load(reportName);
	            JRDesignQuery newQuery= new JRDesignQuery();
	            newQuery.setText(sql);
	            jd.setQuery(newQuery);  
	            String sql2="select g.strGRNCode,g.strProdCode,p.strProdName,p.strReceivedUOM,g.dblQty,g.dblRejected,g.dblDiscount,"
	            		+ " g.strTaxType,g.dblTaxableAmt,g.dblTax,dblTaxAmt, g.dblUnitPrice,g.dblWeight,g.strProdChar,g.dblDCQty,"
	            		+ " g.dblDCWt,g.strRemarks,g.dblQtyRbl,g.strGRNProdChar,g.dblPOWeight,g.strCode, g.dblRework,g.dblPackForw,"
	            		+ " g.dblRate,g.dblValue,p.strPartNo ,p.dblUnitPrice as stdRate,p.dblUnitPrice*(g.dblQty-g.dblRejected) as stdAmt  from tblgrndtl g,tblproductmaster p "
	            		+ " where g.strProdCode=p.strProdCode and g.strGRNCode='"+GRNCode+"' and g.strClientCode='"+clientCode+"' and p.strClientCode='"+clientCode+"' order by p.strProdName ";
	            JRDesignQuery subQuery = new JRDesignQuery();
	            subQuery.setText(sql2);
	            Map<String, JRDataset> datasetMap = jd.getDatasetMap();
	            JRDesignDataset subDataset = (JRDesignDataset) datasetMap.get("dsGrnDtl");
	            subDataset.setQuery(subQuery);
				String Sql3="select a.strTaxDesc,a.strTaxAmt from tblgrntaxdtl a where strGRNCode='"+GRNCode+"' and strClientCode='"+clientCode+"'";
	            
	            JRDesignQuery taxQuery=new JRDesignQuery();
	            taxQuery.setText(Sql3);
	            JRDesignDataset taxDataset = (JRDesignDataset) datasetMap.get("dsTaxDtl");
	            taxDataset.setQuery(taxQuery);
	            JasperReport jr=JasperCompileManager.compileReport(jd);            
	            HashMap hm = new HashMap();
	            hm.put("strCompanyName",companyName);
	            hm.put("strUserCode",userCode);
	            hm.put("strImagePath",imagePath );
	            hm.put("strGRN Code",GRNCode );
	            hm.put("strAddr1",objSetup.getStrAdd1() );
	            hm.put("strAddr2",objSetup.getStrAdd2());            
	            hm.put("strCity", objSetup.getStrCity());
	            hm.put("strState",objSetup.getStrState());
	            hm.put("strCountry", objSetup.getStrCountry());
	            hm.put("strPin", objSetup.getStrPin());
	            p=JasperFillManager.fillReport(jr, hm,con);
	            
	      } catch (Exception e) {
	      e.printStackTrace();
	      logger.error(e.getMessage());
	      }finally{
	      	try {
					con.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
	      	return p;
	      }
		}
		
		
		
		@RequestMapping(value = "/frmGRNRegisterReport", method = RequestMethod.GET)
		public ModelAndView funOpenReportForm(Map<String, Object> model,HttpServletRequest request){
						
			String urlHits="1";
			try{
				urlHits=request.getParameter("saddr").toString();
			}catch(NullPointerException e){
				urlHits="1";
			}
			model.put("urlHits",urlHits);
			
			String propertyCode=request.getSession().getAttribute("propertyCode").toString();
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			
			HashMap<String, String> mapLocation= objGlobalService.funGetLocationList(propertyCode,clientCode);
			if(mapLocation.isEmpty())
			{
				mapLocation.put("", "");
			}	
			else
			{
				mapLocation.put("All", "All");
			}
			mapLocation=clsGlobalFunctions.funSortByValues(mapLocation);
			model.put("listLocation",mapLocation);
			
			HashMap<String, String> mapSupplier= objGlobalService.funGetSupplierList(propertyCode,clientCode);
			if(mapSupplier.isEmpty())
			{
				mapSupplier.put("", "");
			}
			else
			{
				mapSupplier.put("All", "All");
			}
			
			mapSupplier=clsGlobalFunctions.funSortByValues(mapSupplier);
			model.put("listSupplier",mapSupplier);
			
			
			if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmGRNRegisterReport_1","command", new clsReportBean());
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmGRNRegisterReport","command", new clsReportBean());
			}else {
				return null;
			}
			//return new ModelAndView("frmJV","command", new clsJVHdModel());
		}
		
		
//		@RequestMapping(value = "/rptGRNRegisterReport", method = RequestMethod.GET)
//		private void funReciptReport(@ModelAttribute("command") clsReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
//		{
//			String ProductCode=objBean.getStrDocCode();
//			String type=objBean.getStrDocType();
////			if(type.equals("Excel"))
////			{
////				 funCallGRNRegisterReport(ProductCode,type,resp,req);
//			//}
////			else
////			{
////				
////			}
//	      
//		}
		
		
		

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@RequestMapping(value = "/rptGRNRegisterReport", method = RequestMethod.POST)
		private ModelAndView funCallGRNRegisterReport(@ModelAttribute("command") clsReportBean objBean,HttpServletResponse resp,HttpServletRequest req)
		{
//			try {
//				
//				
//				objGlobal=new clsGlobalFunctions();
//				Connection con=objGlobal.funGetConnection();
//	            String clientCode=req.getSession().getAttribute("clientCode").toString();
//				String companyName=req.getSession().getAttribute("companyName").toString();
				
//				String propertyCode=req.getSession().getAttribute("propertyCode").toString();
//				clsPropertySetupModel objSetup=objSetupMasterService.funGetObjectPropertySetup(propertyCode, clientCode);
//				if(objSetup==null)
//				{
//					objSetup=new clsPropertySetupModel();
//				}
//				String reportName=servletContext.getRealPath("/WEB-INF/reports/rptGRNRegisterReport.jrxml"); 
//				String imagePath=servletContext.getRealPath("/resources/images/company_Logo.png");
//				
//				
//				 String sqlDtl="select a.strGRNCode as GRNCode ,date(a.dtGRNDate) As GRNDate,a.strSuppCode as SuppCode,d.strPName as SuppName,d.strPType as PIndicator, "
//					 	  +"a.strLocCode as LocCode, "
//					 	  +"e.strLocName as LocName,b.strProdCode,c.strProdName as ProdName,f.strGCode as GroupCode,g.strGName as GroupName, "
//					 	  +"f.strSGCode,f.strSGName as SubGroupName,c.strClass as Class, "
//					 	  +" ifnull(h.strTaxCode,'') as TaxCode,ifnull(i.strTaxDesc,'') as TaxName,ifnull(i.dblPercent,0.00) as TaxPer, "
//					 	  +"b.dblUnitPrice as Rate,(b.dblQty-b.dblRejected) as AcceptQty, "
//					 	  +"b.dblRejected as RejQty,b.dblUnitPrice*(b.dblQty-b.dblRejected) as Amount " 
//					 	  +"from tblgrnhd a left outer join tblgrndtl b on a.strGRNCode=b.strGRNCode  "
//						  +"left outer join tblproductmaster c on b.strProdCode=c.strProdCode  "
//						  +"left outer join tblpartymaster d on a.strSuppCode=d.strPCode "
//							+"left outer join tbllocationmaster e on a.strLocCode=e.strLocCode "
//							+"left outer join tblsubgroupmaster f on c.strSGCode=f.strSGCode  "
//							+"left outer join tblgroupmaster g on f.strGCode = g.strGCode  "
//							+"left outer join tblgrntaxdtl h on a.strGRNCode=h.strGRNCode "
//							+"left outer join tbltaxhd i on h.strTaxCode=i.strTaxCode  "
//							+"where a.strClientCode='"+clientCode+"' and b.strProdCode='"+ProductCode+"' "
//							+"and a.strClientCode=b.strClientCode "
//							+"and b.strClientCode=c.strClientCode "
//							+"and c.strClientCode=d.strClientCode "
//							+"and d.strClientCode=e.strClientCode "
//							+"and e.strClientCode=f.strClientCode "
//							+"and f.strClientCode=g.strClientCode "
//							+"order by a.strGRNCode" ; 
//			
//				  JasperDesign jd=JRXmlLoader.load(reportName);
//				  JRDesignQuery subQuery = new JRDesignQuery();
//				  subQuery.setText(sqlDtl);
//				  Map<String, JRDataset> datasetMap = jd.getDatasetMap();
//				  JRDesignDataset subDataset;
//				  subDataset = (JRDesignDataset) datasetMap.get("dsGRNRegister");
//				  subDataset.setQuery(subQuery);
//		          JasperReport jr=JasperCompileManager.compileReport(jd);
//	          
//	          HashMap hm = new HashMap();
//	          hm.put("strCompanyName",companyName );
//	          hm.put("strUserCode",userCode);
//	          hm.put("strImagePath",imagePath );
//	          hm.put("strAddr1",objSetup.getStrAdd1() );
//	          hm.put("strAddr2",objSetup.getStrAdd2());            
//	          hm.put("strCity", objSetup.getStrCity());
//	          hm.put("strState",objSetup.getStrState());
//	          hm.put("strCountry", objSetup.getStrCountry());
//	          hm.put("strPin", objSetup.getStrPin());
//	       
//	          
//	          JasperPrint p=JasperFillManager.fillReport(jr, hm,con);
//	          
//	        
//	           if(type.trim().equalsIgnoreCase("xls"))
//				{
//					JRExporter exporterXLS = new JRXlsExporter();	 
//					exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, p);  
//					exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, resp.getOutputStream() );  
//		            resp.setHeader( "Content-Disposition", "attachment;filename=" + "rptBillPassingReport."+type.trim() );                        
//		            exporterXLS.exportReport();  
//		            resp.setContentType("application/xlsx");	
//				}
//
//	        } catch (Exception e) {
//	        e.printStackTrace();
//	        }
//		}

			//String ProductCode=objBean.getStrDocCode();
			String fromDate=objGlobalFunctions.funGetDate("yyyy-MM-dd", objBean.getDtFromDate());
			String toDate=objGlobalFunctions.funGetDate("yyyy-MM-dd",objBean.getDtToDate());
			
			String supplierCode=objBean.getStrSuppCode();
			String locationCode=objBean.getStrLocationCode();
			String propertyCode=req.getSession().getAttribute("propertyCode").toString();
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String userCode=req.getSession().getAttribute("usercode").toString();
			String tempLoc[] = objBean.getStrLocationCode().split(",");
			String strLocCodes = "";
			
//			for (int i = 0; i < tempLoc.length; i++) 
//			{
//				if(tempLoc.length >=i)
//				{
//					strLocCodes+=" '"+tempLoc[i]+"'";
//				}else
//				{
//					strLocCodes+=" '"+tempLoc[i]+"',";
//				}
//			}
			String locNames="";
			for (int i = 0; i < tempLoc.length; i++) {
				if (strLocCodes.length() > 0) {
					clsLocationMasterModel objLocModel= objLocationMasterService.funGetObject(tempLoc[i], clientCode);
					locNames+=objLocModel.getStrLocName()+",";
					strLocCodes = strLocCodes + " or a.strLocCode='"
							+ tempLoc[i] + "' ";
				} else {
					clsLocationMasterModel objLocModel= objLocationMasterService.funGetObject(tempLoc[i], clientCode);
					locNames+=objLocModel.getStrLocName()+",";
					strLocCodes = "a.strLocCode='" + tempLoc[i] + "' ";

				}
			}
			
			String tempSupp[] = objBean.getStrSuppCode().split(",");
			String strSuppCodes = "";
//			
//			for (int i = 0; i < tempSupp.length; i++) 
//			{
//				if(tempSupp.length >=i)
//				{
//					strSuppCodes+=" '"+tempSupp[i]+"',";
//				}else
//				{
//					strSuppCodes+=" '"+tempSupp[i]+"'";
//				}
//			}
			
			for (int i = 0; i < tempSupp.length; i++) {
				if (strSuppCodes.length() > 0) {
					strSuppCodes = strSuppCodes + " or a.strSuppCode='"
							+ tempSupp[i] + "' ";
				} else {
					strSuppCodes = "a.strSuppCode='" + tempSupp[i] + "' ";

				}
			}
			
			
			
			
		
//		String header="GRNCode ,GRNDate,SuppCode,SuppName,PIndicator,LocCode,LocName,ProdCode,  ProdName,  GroupCode, GroupName, "
//						+"SGCode, SubGroupName, class,TaxCode,TaxName,TaxPer, TaxAmt,TotalAmt,Rate,AcceptQty,RejQty, Amount ";
//		
		
		List ExportList=new ArrayList();		
		
		ExportList.add("rptGRNRegisterReport_"+fromDate+"to"+toDate+"_"+userCode);
		
		List titleData=new ArrayList<>();
		titleData.add("GRN Register Report");
		ExportList.add(titleData);
				
		List filterData=new ArrayList<>();
		titleData.add("Locations");
		titleData.add(locNames);
		filterData.add("From Date");
		filterData.add(fromDate);
		filterData.add("To Date");
		filterData.add(toDate);
		
		ExportList.add(filterData);
		
		String header="GRNCode ,GRNDate,SuppCode,SuppName,PIndicator,LocCode,LocName,ProdCode,  ProdName,  GroupCode, GroupName, "
				+"SGCode, SubGroupName, class,TaxCode,TaxName,TaxPer,RejQty,AcceptQty,Rate,Amount, TaxAmt,TotalAmt, ";

		
			
		String[] ExcelHeader=header.split(",");
		ExportList.add(ExcelHeader);
//		String sqlDtl="select a.strGRNCode as GRNCode ,date(a.dtGRNDate) As GRNDate,a.strSuppCode as SuppCode,d.strPName as SuppName,d.strPType as PIndicator, "
//			 	  +"a.strLocCode as LocCode, "
//			 	  +"e.strLocName as LocName,b.strProdCode,c.strProdName as ProdName,f.strGCode as GroupCode,g.strGName as GroupName, "
//			 	  +"f.strSGCode,f.strSGName as SubGroupName,"
//			 	  +" case c.strClass "
//				  +" WHEN '' THEN 'NA' "
//				  +" else c.strClass "
//				  +" end  as strClass, "
//			 	  +" ifnull(h.strTaxCode,'') as TaxCode,ifnull(i.strTaxDesc,'') as TaxName,ifnull(i.dblPercent,0.00) as TaxPer, "
//			 	  
//				  + " b.dblRejected as RejQty,"
//				  + " (b.dblQty-b.dblRejected) as AcceptQty, "
//			 	  + " b.dblUnitPrice as Rate,"
//			 	  + " b.dblUnitPrice*(b.dblQty-b.dblRejected) as Amount, "
//			 	  
//			 	  
//			 	  + " ifnull(h.strTaxAmt,'') as TaxAmt,"
//			 	 + " ifnull(a.dblTotal,'') as TotalAmt "
//			 	  
//			 	 // + " ifnull((h.strTaxableAmt+h.strTaxAmt),'') as TotalAmt "
//			 	  
////				  +" (b.dblUnitPrice*(b.dblQty-b.dblRejected) /100)*IFNULL(i.dblPercent,0.00) as TaxAmt, "
////				  +" ((b.dblUnitPrice*(b.dblQty-b.dblRejected) ) + ((b.dblUnitPrice*(b.dblQty-b.dblRejected) /100)*IFNULL(i.dblPercent,0.00)) - ((b.dblUnitPrice*(b.dblQty-b.dblRejected)/100) * a.dblDisRate) ) as TotalAmt "
//
//			 	  
//			 	  +"from tblgrnhd a left outer join tblgrndtl b on a.strGRNCode=b.strGRNCode  "
//				  +"left outer join tblproductmaster c on b.strProdCode=c.strProdCode  "
//				  +"left outer join tblpartymaster d on a.strSuppCode=d.strPCode "
//					+"left outer join tbllocationmaster e on a.strLocCode=e.strLocCode "
//					+"left outer join tblsubgroupmaster f on c.strSGCode=f.strSGCode  "
//					+"left outer join tblgroupmaster g on f.strGCode = g.strGCode  "
//					+"left outer join tblgrntaxdtl h on a.strGRNCode=h.strGRNCode "
//					+"left outer join tbltaxhd i on h.strTaxCode=i.strTaxCode  "
//					+"where a.strClientCode='"+clientCode+"' and "
//				    + " date(a.dtGRNDate) between '"+fromDate+"' and '"+toDate+"' ";
//					
//					sqlDtl = sqlDtl
//					+ " and "
//					+ "("
//					+ strLocCodes
//					+ ")"
//					+ "and "
//					+ "("
//					+ strSuppCodes
//					+ ") ";
//				
//				sqlDtl+=" and a.strClientCode=b.strClientCode "
//					+" and b.strClientCode=c.strClientCode "
//					+" and c.strClientCode=d.strClientCode "
//					+" and d.strClientCode=e.strClientCode "
//					+" and e.strClientCode=f.strClientCode "
//					+" and f.strClientCode=g.strClientCode and e.strPropertyCode='"+propertyCode+"' "
//					+" order by a.strGRNCode,d.strPName,c.strProdName " ;
		
		String sqlDtl="  select a.strGRNCode as GRNCode ,DATE_FORMAT(DATE(a.dtGRNDate),'%d-%m-%Y')  AS GRNDate,a.strSuppCode as SuppCode, d.strPName as SuppName,"
				+ "  d.strPType as PIndicator, a.strLocCode as LocCode,  e.strLocName as LocName,b.strProdCode, "
				+ "  c.strProdName as ProdName,f.strGCode as GroupCode, g.strGName as GroupName, f.strSGCode, "
				+ "  f.strSGName as SubGroupName, case c.strClass  WHEN '' THEN 'NA'  else c.strClass  end  as strClass,"
				+ "  'TaxCode','TaxName','TaxPer',  b.dblRejected as RejQty,"
				+ "  (b.dblQty-b.dblRejected) as AcceptQty,b.dblUnitPrice as Rate, "
				+ "  b.dblUnitPrice*(b.dblQty-b.dblRejected) as Amount, 'TaxAmt',"
				+ "  ifnull(a.dblTotal,'') as TotalAmt "
				+ "  from tblgrnhd a ,tblgrndtl b ,tblproductmaster c,tblpartymaster d,tbllocationmaster e,tblsubgroupmaster f,tblgroupmaster g  "
				+ "	 where a.strGRNCode=b.strGRNCode and b.strProdCode=c.strProdCode "
				+ "  and a.strSuppCode=d.strPCode and a.strLocCode=e.strLocCode and c.strSGCode=f.strSGCode "
				+ "  and f.strGCode = g.strGCode and a.strClientCode='"+clientCode+"' "
				+ "  and date(a.dtGRNDate) between '"+fromDate+"' and '"+toDate+"' ";
		
		sqlDtl = sqlDtl
				+ " and "
				+ "("
				+ strLocCodes
				+ ")"
				+ "and "
				+ "("
				+ strSuppCodes
				+ ") ";
		
		sqlDtl+=" and a.strClientCode=b.strClientCode "
				+" and b.strClientCode=c.strClientCode "
				+" and c.strClientCode=d.strClientCode "
				+" and d.strClientCode=e.strClientCode "
				+" and e.strClientCode=f.strClientCode "
				+" and f.strClientCode=g.strClientCode and e.strPropertyCode='"+propertyCode+"' "
				+" order by a.strGRNCode,d.strPName,c.strProdName " ;
		
 
		List list=objGlobalFunctionsService.funGetList(sqlDtl.toString(),"sql");		
		List OpeningStklist=new ArrayList();
		
		double subToltal =0.00;
		
		double grandToltal =0.00;
		HashMap<String,Double> hmTaxTotalGrid = new HashMap<String, Double>(); 
		for(int i=0;i<list.size();i++)
		{
			Object[] ob=(Object[])list.get(i);
			String prodCode=ob[7].toString();
			String unitPrice =ob[19].toString();
			String suppCode=ob[2].toString();
			String qty =ob[18].toString();
			String disAmt="0";
			String dteGRN=ob[1].toString();
			String taxableAmt="0";
			String taxCode="";
			String taxDesc=""; 
			String taxPer1="0";
			String taxAmt="0";
			double dblRowAmtTotal=0.00;
			
			
			String prdDetailForTax =prodCode+","+unitPrice+","+suppCode+","+qty+","+disAmt;
			Map<String,String> hmProdTax = objGlobalFunctions.funCalculateTax(prdDetailForTax,"Purchase",objGlobalFunctions.funGetDate("yyyy-MM-dd", dteGRN), req);
			
			
			
			if(hmProdTax.size()>0)
			{
				for (Map.Entry<String, String> entry : hmProdTax.entrySet())
				{
					String taxdetails =entry.getValue();
					String[] spItem = taxdetails.split("#");
					 taxableAmt=spItem[0];
					 taxCode=spItem[1];
					 taxDesc=spItem[2];
					 taxPer1=spItem[4];
					 taxAmt=spItem[5];
					 if(hmTaxTotalGrid.containsKey(taxDesc))
					 {
						 double dbltax =hmTaxTotalGrid.get(taxDesc);
						 hmTaxTotalGrid.put(taxDesc, dbltax+Double.parseDouble(taxAmt));
					 }else
					 {
						 hmTaxTotalGrid.put(taxDesc,Double.parseDouble(taxAmt));
					 }
					
					
				    System.out.println(entry.getKey() + "/" + entry.getValue());
				}
			}
			List DataList=new ArrayList<>();
			DataList.add(ob[0].toString()); // GRNCode
			DataList.add(ob[1].toString()); // GRNDate
			DataList.add(ob[2].toString()); // SuppCode
			DataList.add(ob[3].toString()); // SuppName
			DataList.add(ob[4].toString()); // PIndicator
			DataList.add(ob[5].toString()); // LocCode
			DataList.add(ob[6].toString()); // LocName
		
			DataList.add(ob[7].toString()); // strProdCode
			DataList.add(ob[8].toString()); // ProdName
			DataList.add(ob[9].toString()); // GroupCode
			DataList.add(ob[10].toString()); // GroupName
			DataList.add(ob[11].toString()); // SGCode
			DataList.add(ob[12].toString()); // SGName
			DataList.add(ob[13].toString()); // strClass
			
			DataList.add(taxCode); //TaxCode
//			Double acceptQty= (GRNDtlModel.getDblQty()-GRNDtlModel.getDblRejected());
			DataList.add(taxDesc); //TaxNAme
			DataList.add(taxPer1); //taxper
//			Double amount=GRNDtlModel.getDblUnitPrice()*acceptQty;
			DataList.add(ob[17].toString()); //RejQty
			DataList.add(ob[18].toString()); //AcceptQty
			DataList.add(ob[19].toString()); //Rate
			DataList.add(ob[20].toString()); //Amount
			DataList.add(taxAmt);  //TaxAmt
			dblRowAmtTotal = Double.parseDouble(ob[20].toString())+Double.parseDouble(taxAmt);
			DataList.add(dblRowAmtTotal); //TotalAmt = Amt + taxAmt
			grandToltal += dblRowAmtTotal;
			
			subToltal+= Double.parseDouble(ob[20].toString());
			
			
			OpeningStklist.add(DataList);
			
		}
		
		
		
		
		List DataListblank=new ArrayList<>();
		for(int i=0;i<23;i++)
		{
			DataListblank.add(""); 
		}
			
		OpeningStklist.add(DataListblank);
		
		
		if(subToltal>0)
		{
			List DataListSubtotal=new ArrayList<>();
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			
			DataListSubtotal.add(""); 
			DataListSubtotal.add("Sub Total"); 
			DataListSubtotal.add(subToltal); 
			
			OpeningStklist.add(DataListSubtotal);
		}
		
		String sqldisAmt = " SELECT sum(a.dblDisAmt ) FROM tblgrnhd a "
				+ " WHERE DATE(a.dtGRNDate) between '"+fromDate+"' and '"+toDate+"' and a.strClientCode='"+clientCode+"' and a.strGRNCode like '"+propertyCode+"%' ";
		List listDis=objGlobalFunctionsService.funGetList(sqldisAmt,"sql");	
		double totDisAmt = 0.0;
		if(listDis.size()>0)
		{
			totDisAmt= Double.parseDouble(listDis.get(0).toString());
		}
		
			List DataListSubtotal=new ArrayList<>();
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			DataListSubtotal.add(""); 
			
			DataListSubtotal.add(""); 
			DataListSubtotal.add("Total Discount"); 
			DataListSubtotal.add(totDisAmt); 
			
			OpeningStklist.add(DataListSubtotal);
		
		
		for (Map.Entry<String, Double> entry : hmTaxTotalGrid.entrySet())
		{
			List DataListTax=new ArrayList<>();
			DataListTax.add(""); 
			DataListTax.add(""); 
			DataListTax.add(""); 
			DataListTax.add(""); 
			
			DataListTax.add(""); 
			DataListTax.add(""); 
			DataListTax.add(""); 
			DataListTax.add(""); 
			
			DataListTax.add(""); 
			DataListTax.add(""); 
			DataListTax.add(""); 
			DataListTax.add(""); 
			
			DataListTax.add(""); 
			DataListTax.add(""); 
			DataListTax.add(""); 
			DataListTax.add(""); 
			
			DataListTax.add(""); 
			DataListTax.add(""); 
			DataListTax.add(""); 
			DataListTax.add("");
			
			DataListTax.add("");
			DataListTax.add(entry.getKey());
			DataListTax.add(entry.getValue());
			
			OpeningStklist.add(DataListTax);
		}
		
		if(grandToltal>0)
		{
			List DataListgrandtotal=new ArrayList<>();
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add(""); 
			
			DataListgrandtotal.add(""); 
			DataListgrandtotal.add("Grand Total"); 
			DataListgrandtotal.add(grandToltal); 
			
			OpeningStklist.add(DataListgrandtotal);
		}
		
		ExportList.add(OpeningStklist);
		
		return new ModelAndView("excelViewFromDateTodateWithReportName", "listFromDateTodateWithReportName", ExportList);
	//	return new ModelAndView("excelView", "stocklist", ExportList);	
	}
		
		
//		@SuppressWarnings("unchecked")
//		@RequestMapping(value = "/loadAllSupplier", method = RequestMethod.GET)
//		public @ResponseBody List<clsSupplierMasterModel> funLoadAllSupplier(HttpServletRequest request)
//		{
//			String clientCode=request.getSession().getAttribute("clientCode").toString();
//			String sql="from clsSupplierMasterModel where strClientCode='"+clientCode+"' ";
//			List<clsSupplierMasterModel> list=objGlobalFunctionsService.funGetList(sql, "hql");
//			return list;
//		}
			
		
		@RequestMapping(value = "/frmGRNSummaryReport", method = RequestMethod.GET)
		public ModelAndView funOpenGRNSummaryReportForm(
				Map<String, Object> model,
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
				return new ModelAndView("frmGRNSummaryReport_1", "command",
						new clsReportBean());
			} else {
				return new ModelAndView("frmGRNSummaryReport", "command",
						new clsReportBean());
			}

	    }	
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@RequestMapping(value = "/rptGRNSummaryReport", method = RequestMethod.POST)
		private ModelAndView funCallGRNSummaryReportt(@ModelAttribute("command") clsReportBean objBean,HttpServletResponse resp,HttpServletRequest req)
		{
			String fromDate=objGlobalFunctions.funGetDate("yyyy-MM-dd", objBean.getDtFromDate());
			String toDate=objGlobalFunctions.funGetDate("yyyy-MM-dd",objBean.getDtToDate());
			String supplierCode = objBean.getStrSuppCode();
			String[] tempSupplierCode = objBean.getStrSuppCode().split(",");
			String strSuppCode="";
			String gruopcode=objBean.getStrGCode();		
			String[] tempgroupCode=objBean.getStrGCode().split(",");
		    String strGCode="";
		    String subGroupCode=objBean.getStrSGCode();
			String[] tempsubGroupCode=objBean.getStrSGCode().split(",");
			String strSGCode="";
			String tempLoc[] = objBean.getStrLocationCode().split(",");
			String strLocCodes = "";
			
			String type=objBean.getStrDocType();
			String propertyCode=req.getSession().getAttribute("propertyCode").toString();
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String userCode=req.getSession().getAttribute("usercode").toString();
			
			if(tempSupplierCode[0].length()>0)
			{
				for (int i = 0; i < tempSupplierCode.length; i++) {
					if (strSuppCode.length() > 0) {
						strSuppCode = strSuppCode + " or a.strSuppCode='"
								+ tempSupplierCode[i] + "' ";
					} else {
						strSuppCode = "a.strSuppCode='" + tempSupplierCode[i] + "' ";
	
					}
				}
			}
			
			for (int i = 0; i < tempgroupCode.length; i++) {
				if (strGCode.length() > 0) {
					strGCode = strGCode + " or f.strGCode='"
							+ tempgroupCode[i] + "' ";
				} else {
					strGCode = "f.strGCode='" + tempgroupCode[i] + "' ";

				}
			}
			for (int i = 0; i < tempsubGroupCode.length; i++) {
				if (strSGCode.length() > 0) {
					strSGCode = strSGCode + " or c.strSGCode='"
							+ tempsubGroupCode[i] + "' ";
				} else {
					strSGCode = "c.strSGCode='" + tempsubGroupCode[i] + "' ";

				}
			}
			String locNames="";
			for (int i = 0; i < tempLoc.length; i++) {
				if (strLocCodes.length() > 0) {
					clsLocationMasterModel objLocModel= objLocationMasterService.funGetObject(tempLoc[i], clientCode);
					locNames+=objLocModel.getStrLocName()+",";
					strLocCodes = strLocCodes + " or a.strLocCode='"
							+ tempLoc[i] + "' ";
				} else {
					clsLocationMasterModel objLocModel= objLocationMasterService.funGetObject(tempLoc[i], clientCode);
					locNames+=objLocModel.getStrLocName()+",";
					strLocCodes = "a.strLocCode='" + tempLoc[i] + "' ";

				}
			}
			
			List ExportList=new ArrayList();		
			
			ExportList.add("rptGRNSummaryReport_"+fromDate+"to"+toDate+"_"+userCode);
			
			List titleData=new ArrayList<>();
			titleData.add("GRN Summary Report");
			ExportList.add(titleData);
					
			List filterData=new ArrayList<>();
			titleData.add("Locations");
			titleData.add(locNames);
			filterData.add("From Date");
			filterData.add(fromDate);
			filterData.add("To Date");
			filterData.add(toDate);
			ExportList.add(filterData);
			
			String header="Group ,SubGroup,Location,Party Indicator,NonTaxableAmount,TaxableAmount,TaxAmount,TaxBillAmount, TotalAmount ";
			String[] ExcelHeader=header.split(",");
			ExportList.add(ExcelHeader);
			String sqlDtl="SELECT  g.strGName AS GroupName,f.strSGName AS SubGroupName,  e.strLocName AS LocName,d.strPType AS PIndicator, "
					+" IFNULL((a.dblTotal-(h.strTaxableAmt+h.strTaxAmt)),'') as NONTaxAmt, "
					+" IFNULL(h.strTaxableAmt,'') AS TaxableAmt,IFNULL(h.strTaxAmt,'') AS TaxAmt, IFNULL((h.strTaxableAmt+h.strTaxAmt),'') AS TaxBillAmt, "
					+" IFNULL((a.dblSubTotal+a.dblDisAmt+a.dblTaxAmt+dblExtra), '') as TotalAmt "
					+" FROM tblgrnhd a "
					+" LEFT OUTER "
					+" JOIN tblgrndtl b ON a.strGRNCode=b.strGRNCode "
					+" LEFT OUTER "
					+" JOIN tblproductmaster c ON b.strProdCode=c.strProdCode "
					+" LEFT OUTER "
					+" JOIN tblpartymaster d ON a.strSuppCode=d.strPCode "
					+" LEFT OUTER "
					+" JOIN tbllocationmaster e ON a.strLocCode=e.strLocCode "
					+" LEFT OUTER "
					+" JOIN tblsubgroupmaster f ON c.strSGCode=f.strSGCode "
					+" LEFT OUTER "
					+" JOIN tblgroupmaster g ON f.strGCode = g.strGCode "
					+" LEFT OUTER "
					+" JOIN tblgrntaxdtl h ON a.strGRNCode=h.strGRNCode "
					+" LEFT OUTER "
					+" JOIN tbltaxhd i ON h.strTaxCode=i.strTaxCode "
					+" WHERE a.strClientCode='"+clientCode+"' AND DATE(a.dtGRNDate) BETWEEN '"+fromDate+"' AND '"+toDate+"'  ";

					sqlDtl = sqlDtl
						+ " and "
						+ "("
						+ strGCode
						+ ")"
						+ "and "
						+ "("
						+ strSGCode
						+ ") "
						+ "and "
						+ "("
						+ strLocCodes
						+ ") ";
						if(strSuppCode.length()>0)
						{
							sqlDtl = sqlDtl + " and "
									+ "("
									+ strSuppCode
									+ ") ";
						}
						
			
			sqlDtl = sqlDtl + " AND a.strClientCode=b.strClientCode "
					+" AND b.strClientCode=c.strClientCode AND c.strClientCode=d.strClientCode " 
					+" AND d.strClientCode=e.strClientCode AND e.strClientCode=f.strClientCode  "
	  				+" AND f.strClientCode=g.strClientCode AND e.strPropertyCode='"+propertyCode+"' "
	  				+" GROUP BY a.strGRNCode  order by e.strLocName,g.strGName,f.strSGName ";
				
					List list=objGlobalFunctionsService.funGetList(sqlDtl.toString(),"sql");		
					List GRNSummarylist=new ArrayList();
					double totNonTaxAmt=0.00;
					double totTaxableAmt=0.00;
					double totTaxAmt=0.00;
					double totTaxbillAmt=0.00;
					double totAmt=0.00;
					for(int i=0;i<list.size();i++)
					{
						Object[] ob=(Object[])list.get(i);
					
						List DataList=new ArrayList<>();
						DataList.add(ob[0].toString());
						DataList.add(ob[1].toString());
						DataList.add(ob[2].toString());
						DataList.add(ob[3].toString());
						
						DataList.add(ob[4].toString());
						totNonTaxAmt=totNonTaxAmt+Double.parseDouble(ob[4].toString().isEmpty() ? "0.0" : ob[4].toString());
						
						DataList.add(ob[5].toString());
						totTaxableAmt=totTaxableAmt+Double.parseDouble(ob[5].toString().isEmpty() ? "0.0" : ob[5].toString());;
						
						DataList.add(ob[6].toString());
						totTaxAmt=totTaxAmt+Double.parseDouble(ob[6].toString().isEmpty() ? "0.0" : ob[6].toString());;
						
						DataList.add(ob[7].toString());
						totTaxbillAmt=totTaxbillAmt+Double.parseDouble(ob[7].toString().isEmpty() ? "0.0" : ob[7].toString());;
						
						DataList.add(ob[8].toString());
						totAmt=totAmt+Double.parseDouble(ob[8].toString().isEmpty() ? "0.0" : ob[8].toString());;
					
						
						GRNSummarylist.add(DataList);
					}
					
					List totDataList=new ArrayList<>();
					totDataList.add("");
					totDataList.add("");
					totDataList.add("");
					totDataList.add("Total");
					totDataList.add(totNonTaxAmt);
					totDataList.add(totTaxableAmt);
					totDataList.add(totTaxAmt);
					totDataList.add(totTaxbillAmt);
					totDataList.add(totAmt);
				
					
					GRNSummarylist.add(totDataList);
					
					ExportList.add(GRNSummarylist);
					
				//	return new ModelAndView("excelView", "stocklist", ExportList);	
					return new ModelAndView("excelViewFromDateTodateWithReportName", "listFromDateTodateWithReportName", ExportList);
		}

//
//		 @RequestMapping(value="/person", method = RequestMethod.GET)
//		 public @ResponseBody Person funGetModifyChar(@RequestParam("Chaar")  String person,HttpServletRequest request)
//		 // public @ResponseBody Person post( @RequestBody final  Person person) 
//		 {    
//		 
//		     // System.out.println(person.getId() + " " + person.getName());
//		      return null;
//		  }
		
		 @SuppressWarnings("rawtypes")
			@RequestMapping(value = "/charSalesData", method = RequestMethod.POST,headers = {"Content-type=application/json"})
			public @ResponseBody String funLoadCharBean(@RequestBody Object obj, HttpServletRequest request)
			{
			 	@SuppressWarnings("unused")
				List<clsTransectionProdCharModel> listTransProdChar =new ArrayList<clsTransectionProdCharModel>();
			 
			 
			 return null;
			}
		 
		 
		 
		 
		 
		 
		 private String funGenrateJVforGRN(clsGRNHdModel objModel,List<clsGRNDtlModel> listDtlModel,List<clsGRNTaxDtlModel> listTaxDtl,String clientCode,String userCode,String propCode,HttpServletRequest req)
			{
				JSONObject jObjJVData =new JSONObject();
				
				JSONArray jArrJVdtl = new JSONArray();
				JSONArray jArrJVDebtordtl = new JSONArray();
				String jvCode ="";
				String custCode = objModel.getStrSuppCode();
				double debitAmt = objModel.getDblTotal();
				clsLinkUpHdModel objLinkCust = objLinkupService.funGetARLinkUp(custCode,clientCode, propCode);
				if(objLinkCust!=null)
				{
					if(objModel.getStrRefNo().equals(""))
					{
						jObjJVData.put("strVouchNo", "");
						jObjJVData.put("strNarration", "JV Genrated by GRN:"+objModel.getStrGRNCode());
						jObjJVData.put("strSancCode", "");
						jObjJVData.put("strType", "");
						jObjJVData.put("dteVouchDate", objModel.getDtGRNDate());
						jObjJVData.put("intVouchMonth", 1);
						jObjJVData.put("dblAmt", debitAmt);
						jObjJVData.put("strTransType", "R");
						jObjJVData.put("strTransMode", "A");
						jObjJVData.put("strModuleType", "AP");
						jObjJVData.put("strMasterPOS", "WEBSTOCK");
						jObjJVData.put("strUserCreated", userCode);
						jObjJVData.put("strUserEdited", userCode);
						jObjJVData.put("dteDateCreated", objGlobalFunctions.funGetCurrentDateTime("yyyy-MM-dd"));
						jObjJVData.put("dteDateEdited", objGlobalFunctions.funGetCurrentDateTime("yyyy-MM-dd"));
						jObjJVData.put("strClientCode", clientCode);
						jObjJVData.put("strPropertyCode", propCode);
						
					}else
					{
						jObjJVData.put("strVouchNo", objModel.getStrRefNo());
						jObjJVData.put("strNarration", "JV Genrated by GRN:"+objModel.getStrGRNCode());
						jObjJVData.put("strSancCode", "");
						jObjJVData.put("strType", "");
						jObjJVData.put("dteVouchDate", objModel.getDtGRNDate());
						jObjJVData.put("intVouchMonth", 1);
						jObjJVData.put("dblAmt", debitAmt);
						jObjJVData.put("strTransType", "R");
						jObjJVData.put("strTransMode", "A");
						jObjJVData.put("strModuleType", "AP");
						jObjJVData.put("strMasterPOS", "WEBSTOCK");
						jObjJVData.put("strUserCreated", userCode);
						jObjJVData.put("strUserEdited", userCode);
						jObjJVData.put("dteDateCreated", objGlobalFunctions.funGetCurrentDateTime("yyyy-MM-dd"));
						jObjJVData.put("dteDateEdited", objGlobalFunctions.funGetCurrentDateTime("yyyy-MM-dd"));
						jObjJVData.put("strClientCode", clientCode);
						jObjJVData.put("strPropertyCode", propCode);
						
						
					}
					// jvhd entry end
					
					// jvdtl entry Start
					for(clsGRNDtlModel objDtl : listDtlModel)
					{
						
						JSONObject jObjDtl =new JSONObject();
						
						clsProductMasterModel objProdModle=objProductMasterService.funGetObject(objDtl.getStrProdCode(),clientCode);
						clsLinkUpHdModel objLinkSubGroup = objLinkupService.funGetARLinkUp(objProdModle.getStrSGCode(),clientCode,propCode);
					if(objProdModle!=null && objLinkSubGroup!=null)
						{
							jObjDtl.put("strVouchNo", "");
							jObjDtl.put("strAccountCode", objLinkSubGroup.getStrAccountCode());
							jObjDtl.put("strAccountName", objLinkSubGroup.getStrGDes());
							jObjDtl.put("strCrDr", "Dr");
							jObjDtl.put("dblDrAmt",  objDtl.getDblTotalPrice());
							jObjDtl.put("dblCrAmt",0.00);
							jObjDtl.put("strNarration", "WS Product code :"+objDtl.getStrProdCode());
							jObjDtl.put("strOneLine", "R");
							jObjDtl.put("strClientCode", clientCode);
							jObjDtl.put("strPropertyCode", propCode);
							jArrJVdtl.add(jObjDtl);
						}	
					}
					
					if(listTaxDtl!=null)
					{
						for(clsGRNTaxDtlModel objTaxDtl : listTaxDtl)
						{
							JSONObject jObjTaxDtl =new JSONObject();
							clsLinkUpHdModel objLinkTax = objLinkupService.funGetARLinkUp(objTaxDtl.getStrTaxCode(),clientCode,propCode);
							if(objLinkTax!=null )
							{
								jObjTaxDtl.put("strVouchNo", "");
								jObjTaxDtl.put("strAccountCode", objLinkTax.getStrAccountCode());
								jObjTaxDtl.put("strAccountName", objLinkTax.getStrGDes());
								jObjTaxDtl.put("strCrDr", "Dr");
								jObjTaxDtl.put("dblDrAmt", objTaxDtl.getStrTaxAmt());
								jObjTaxDtl.put("dblCrAmt", 0.00);
								jObjTaxDtl.put("strNarration", "WS Tax Desc :"+objTaxDtl.getStrTaxDesc());
								jObjTaxDtl.put("strOneLine", "R");
								jObjTaxDtl.put("strClientCode", clientCode);
								jObjTaxDtl.put("strPropertyCode", propCode);
								jArrJVdtl.add(jObjTaxDtl);
							}
						}
					}
					JSONObject jObjCustDtl =new JSONObject();
					jObjCustDtl.put("strVouchNo", "");
					jObjCustDtl.put("strAccountCode", "");
					jObjCustDtl.put("strAccountName", "");
					jObjCustDtl.put("strCrDr", "Cr");
					jObjCustDtl.put("dblDrAmt", 0.00);
					jObjCustDtl.put("dblCrAmt", objModel.getDblTotal());
					jObjCustDtl.put("strNarration", "GRN Supplier");
					jObjCustDtl.put("strOneLine", "R");
					jObjCustDtl.put("strClientCode", clientCode);
					jObjCustDtl.put("strPropertyCode", propCode);
					jArrJVdtl.add(jObjCustDtl);
					
					jObjJVData.put("ArrJVDtl", jArrJVdtl);
					
					// jvdtl entry end
					
					// jvDebtor detail entry start
					String sql = " select a.strGRNCode,a.dblTotal,b.strDebtorCode,b.strPName,date(a.dtGRNDate),"
							+ " a.strNarration ,date(a.dtDueDate),a.strBillNo"
							+ " from dbwebmms.tblgrnhd a,dbwebmms.tblpartymaster b  "
							+ " where a.strSuppCode =b.strPCode  "
							+ " and a.strGRNCode='"+objModel.getStrGRNCode()+"' "
							+ " and a.strClientCode='"+objModel.getStrClientCode()+"'   " ; 
					List listTax=objGlobalFunctionsService.funGetList(sql,"sql");		
					for(int i=0;i<listTax.size();i++)
					{
							JSONObject jObjDtl =new JSONObject();
							Object[] ob=(Object[])listTax.get(i);
							jObjDtl.put("strVouchNo", "");
							jObjDtl.put("strDebtorCode", ob[2].toString());
							jObjDtl.put("strDebtorName", ob[3].toString());
							jObjDtl.put("strCrDr", "Cr");
							jObjDtl.put("dblAmt", ob[1].toString());
							jObjDtl.put("strBillNo", ob[7].toString());
							jObjDtl.put("strInvoiceNo",  ob[0].toString());
							jObjDtl.put("strGuest", "");
							jObjDtl.put("strAccountCode", objLinkCust.getStrAccountCode());
							jObjDtl.put("strCreditNo", "");
							jObjDtl.put("dteBillDate", ob[4].toString());
							jObjDtl.put("dteInvoiceDate", ob[4].toString());
							jObjDtl.put("strNarration", ob[5].toString());
							jObjDtl.put("dteDueDate", ob[6].toString());
							jObjDtl.put("strClientCode", clientCode);
							jObjDtl.put("strPropertyCode", propCode);
							jObjDtl.put("strPOSCode", "");
							jObjDtl.put("strPOSName", "");
							jObjDtl.put("strRegistrationNo", "");
						
						jArrJVDebtordtl.add(jObjDtl);
						}	
					
					jObjJVData.put("ArrJVDebtordtl", jArrJVDebtordtl);
					// jvDebtor detail entry end
					
					JSONObject jObj = objGlobalFunctions.funPOSTMethodUrlJosnObjectData("http://localhost:8080/prjSanguineWebService/WebBooksIntegration/funGenrateJVforGRN",jObjJVData);
					jvCode = jObj.get("strJVCode").toString();
				}	
				return jvCode;
			}
		 
		 
		 
		 @SuppressWarnings({ "rawtypes", "unchecked" })
			@RequestMapping(value = "/loadGRNProductRate", method = RequestMethod.GET)
			public @ResponseBody List funLatestGRNProductRate(@RequestParam("prodCode") String prodCode ,HttpServletRequest req)
			{		
				String clientCode=req.getSession().getAttribute("clientCode").toString();
				String sql="";
				
				sql=" select max(date(a.dtGRNDate)),b.dblUnitPrice from tblgrnhd a,tblgrndtl b  "
						+ " where a.strGRNCode=b.strGRNCode and b.strProdCode='"+prodCode+"' "
						+ " and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"';  ";
				List list=objGlobalFunctionsService.funGetList(sql, "sql");
				
				List ProdList=new ArrayList();
				if(list.size()>0)
				{
					Object[] ob=(Object[])list.get(0);
					
					ProdList.add(ob[0].toString());
					ProdList.add(Double.parseDouble(ob[1].toString()));
					
				}
				else
				{
					ProdList=new ArrayList();
					ProdList.add("Invalid Code ");
				}
				return ProdList;
			}
		 
		 
		 
		 
		 
		 
		 
}
		
		



