package com.sanguine.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.Connection;
import com.sanguine.bean.clsPurchaseReturnBean;
import com.sanguine.model.clsAuditDtlModel;
import com.sanguine.model.clsAuditHdModel;
import com.sanguine.model.clsCompanyMasterModel;
import com.sanguine.model.clsGRNDtlModel;
import com.sanguine.model.clsGRNHdModel;
import com.sanguine.model.clsGRNTaxDtlModel;
import com.sanguine.model.clsLinkUpHdModel;
import com.sanguine.model.clsLocationMasterModel;
import com.sanguine.model.clsProductMasterModel;
import com.sanguine.model.clsPropertySetupModel;
import com.sanguine.model.clsPurchaseReturnDtlModel;
import com.sanguine.model.clsPurchaseReturnHdModel;
import com.sanguine.model.clsPurchaseReturnHdModel_ID;
import com.sanguine.model.clsSupplierMasterModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsLinkUpService;
import com.sanguine.service.clsLocationMasterService;
import com.sanguine.service.clsProductMasterService;
import com.sanguine.service.clsPurchaseReturnService;
import com.sanguine.service.clsSetupMasterService;
import com.sanguine.service.clsSupplierMasterService;
import com.sanguine.util.clsReportBean;

@Controller
public class clsPurchaseReturnController {

	final static Logger logger=Logger.getLogger(clsPurchaseReturnController.class);
	@Autowired
	ServletContext servletContext;
	
	@Autowired
	private clsSetupMasterService objSetupMasterService;
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	
	@Autowired
	private clsPurchaseReturnService objPurchaseReturnService;
	
	@Autowired
	private clsLocationMasterService objLocService;
	
	@Autowired
	private clsSupplierMasterService objSupplierMasterService;
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	clsGlobalFunctions objGlobal=null;
	
	@Autowired 
	private clsLinkUpService objLinkupService;
	
	@Autowired
	private clsProductMasterService objProductMasterService;
	
	/**
	 * Open Purchase Return Form
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/frmPurchaseReturn", method = RequestMethod.GET)
	public ModelAndView funOpenPurchaseReturnForm(Map<String, Object> model,HttpServletRequest request)
	{
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		String propCode=request.getSession().getAttribute("propertyCode").toString();
		request.getSession().setAttribute("formName","frmWebStockHelpPurchaseReturn");
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		/**
		 * Checking Authorization
		 */
		String docCode="";
		boolean flagOpenFromAuthorization=true;
		try {
			docCode=request.getParameter("authorizationPRCode").toString();
		} catch (NullPointerException e) {
			flagOpenFromAuthorization=false;
		}
		model.put("flagOpenFromAuthorization",flagOpenFromAuthorization);
		if(flagOpenFromAuthorization){
			model.put("authorizationPRCode",docCode);
		}
		model.put("urlHits",urlHits);
		
		/**
		 * Set Process
		 */
		List<String> list=objGlobalFunctions.funGetSetUpProcess("frmPurchaseReturn",propCode,clientCode);
		if(list.size()>0)
		{
			model.put("strProcessList", list);
		}
		else
		{
			list=new ArrayList<String>();
			model.put("strProcessList",list);
		}
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPurchaseReturn_1","command", new clsPurchaseReturnBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPurchaseReturn","command", new clsPurchaseReturnBean());
		}else {
			return null;
		}
		
	}	
	/**
	 * Save Purchase Return Form
	 * @param PRBean
	 * @param result
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/savePR", method = RequestMethod.POST)
	public ModelAndView funSavePurchaseReturn(@ModelAttribute("command") clsPurchaseReturnBean PRBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		objGlobal=new clsGlobalFunctions();
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String userCode=req.getSession().getAttribute("usercode").toString();
		String propCode=req.getSession().getAttribute("propertyCode").toString();
		if(!result.hasErrors())
		{
			clsPurchaseReturnHdModel objPRModel=null;
			List<clsPurchaseReturnDtlModel> listonPRDtl=PRBean.getListPurchaseReturnDtl();
			if(null != listonPRDtl && listonPRDtl.size() > 0)
	        {
				objPRModel=funPrepareModelHd(PRBean,req);
				objPurchaseReturnService.funAddPRHd(objPRModel);
				String strPrCode=objPRModel.getStrPRCode();
				objPurchaseReturnService.funDeleteDtl(strPrCode,clientCode);
	            for (clsPurchaseReturnDtlModel ob : listonPRDtl)
	            {
	            	ob.setStrPRCode(strPrCode);
	            	ob.setStrProdChar(objGlobal.funIfNull(ob.getStrProdChar(),"NA",ob.getStrProdChar()));
	            	ob.setStrClientCode(clientCode);
	            	objPurchaseReturnService.funAddUpdatePRDtl(ob);
	            }
	            req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage","PR Code : ".concat(objPRModel.getStrPRCode()));
				req.getSession().setAttribute("rptPRCode", objPRModel.getStrPRCode());
	        }
			
			clsCompanyMasterModel objCompModel = objSetupMasterService.funGetObject(clientCode);
			if( objCompModel.getStrWebBookModule().equals("Yes") )
				{
					funGenrateJVforPurchaseReturn( objPRModel,listonPRDtl,clientCode,userCode,propCode,req);
				}
			
			return new ModelAndView("redirect:/frmPurchaseReturn.html?saddr="+urlHits);	
		
		}
		else
		{
			return new ModelAndView("redirect:/frmPurchaseReturn.html?saddr="+urlHits);
		}
		
	}
	/**
	 * Prepare Purchase Return hdModel
	 * @param PRBean
	 * @param req
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private clsPurchaseReturnHdModel funPrepareModelHd(clsPurchaseReturnBean PRBean,HttpServletRequest req)
	{
		String userCode=req.getSession().getAttribute("usercode").toString();
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		/*String propCode=req.getSession().getAttribute("propertyCode").toString();
		String startDate=req.getSession().getAttribute("startDate").toString();
		String res=req.getSession().getAttribute("PRAuthorization").toString();*/
		boolean res=false;
		if(null!=req.getSession().getAttribute("hmAuthorization"))
		{
			HashMap<String,Boolean> hmAuthorization=(HashMap)req.getSession().getAttribute("hmAuthorization");
			if(hmAuthorization.get("Purchase Return"))
			{
				res=true;
			}
		}
		long lastNo=0;
		clsPurchaseReturnHdModel objPRHd ;
		
		if(PRBean.getStrPRCode().trim().length()==0)
		{		
			String strPRCode=objGlobalFunctions.funGenerateDocumentCode("frmPurchaseReturn", PRBean.getDtPRDate(), req);
			objPRHd=new clsPurchaseReturnHdModel(new clsPurchaseReturnHdModel_ID(strPRCode,clientCode));
			
			objPRHd.setIntid(lastNo);
			objPRHd.setStrUserCreated(userCode);
			objPRHd.setDtDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		}
		else
		{				
			clsPurchaseReturnHdModel prModel=objPurchaseReturnService.funGetObject(PRBean.getStrPRCode(), clientCode);
			if(null == prModel){
				String strPRCode=objGlobalFunctions.funGenerateDocumentCode("frmPurchaseReturn", PRBean.getDtPRDate(), req);
				objPRHd=new clsPurchaseReturnHdModel(new clsPurchaseReturnHdModel_ID(strPRCode,clientCode));
				objPRHd.setIntid(lastNo);
				objPRHd.setStrUserCreated(userCode);
				objPRHd.setDtDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			}
			else
			{
				if(objGlobalFunctions.funCheckAuditFrom("frmPurchaseReturn", req))
				{
					funSaveAudit(PRBean.getStrPRCode(),"Edit",req);
				}
				
				objPRHd=new clsPurchaseReturnHdModel(new clsPurchaseReturnHdModel_ID(PRBean.getStrPRCode(),clientCode));
			}
		}
		
		objPRHd.setStrLocCode(PRBean.getStrLocCode());		
		objPRHd.setDtPRDate(objGlobal.funGetDate("yyyy-MM-dd",PRBean.getDtPRDate()));
		objPRHd.setStrSuppCode(PRBean.getStrSuppCode());
		
		if(res)
		{
			objPRHd.setStrAuthorise("No");
			
		}
		else
		{
			objPRHd.setStrAuthorise("Yes");
		}
		objPRHd.setStrPRNo(objGlobal.funIfNull(PRBean.getStrPRNo(),"",PRBean.getStrPRNo())  );
		objPRHd.setStrMInBy(PRBean.getStrMInBy());
		objPRHd.setStrVehNo(PRBean.getStrVehNo());
		objPRHd.setStrTimeInOut(PRBean.getStrTimeInOut());
		objPRHd.setStrAgainst(PRBean.getStrAgainst());
		objPRHd.setStrGRNCode(PRBean.getStrGRNCode());
		objPRHd.setDblSubTotal(PRBean.getDblSubTotal());
		objPRHd.setDblDisRate(PRBean.getDblDisRate());
		objPRHd.setDblDisAmt(PRBean.getDblDisAmt());		
		objPRHd.setDblExtra(PRBean.getDblExtra());
		objPRHd.setDblTotal(PRBean.getDblTotal());
		objPRHd.setStrUserModified(userCode);		
		objPRHd.setStrNarration(PRBean.getStrNarration());
		objPRHd.setDtLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		
		return objPRHd;
	}
	/**
	 * Load Purchase Return HdData
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/loadPurchaseReturnData", method = RequestMethod.GET)
	public @ResponseBody clsPurchaseReturnHdModel funOpenFormWithPRCode(HttpServletRequest request)
	{
		objGlobal=new clsGlobalFunctions();
		String PRCode=request.getParameter("PRCode").toString();
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		clsPurchaseReturnHdModel prModel=objPurchaseReturnService.funGetObject(PRCode, clientCode);
		if(null == prModel)
		{
			prModel=new clsPurchaseReturnHdModel();
			prModel.setStrPRCode("Invalid Code");
			return prModel;
		}
		else
		{
			
		clsLocationMasterModel objLocationFrom= objLocService.funGetObject(prModel.getStrLocCode(),clientCode);
		prModel.setStrLocName(objLocationFrom.getStrLocName());
		prModel.setDtPRDate(objGlobal.funGetDate("yyyy/MM/dd",prModel.getDtPRDate()));
		clsSupplierMasterModel objSupplier=objSupplierMasterService.funGetObject(prModel.getStrSuppCode(), clientCode);
		prModel.setStrSupplierName(objSupplier.getStrPName());
		return prModel;
		}
	}
	/**
	 * Load Purchase Return Dtl Data
	 * @param request
	 * @return
	 */
	@SuppressWarnings({"rawtypes" })
	@RequestMapping(value = "/loadPurchaseReturnDtlData", method = RequestMethod.GET)
	public @ResponseBody List funLoadPRDtlData(HttpServletRequest request)
	{
		objGlobal=new clsGlobalFunctions();
		String PRCode=request.getParameter("PRCode").toString();
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		List prDtlModel=objPurchaseReturnService.funGetDtlList(PRCode, clientCode);
		List<clsPurchaseReturnDtlModel> listPrDtl=new ArrayList<clsPurchaseReturnDtlModel>();
		for(int i=0;i<prDtlModel.size();i++)
        {
        	Object[] ob = (Object[])prDtlModel.get(i);
        	clsPurchaseReturnDtlModel PRProdDtl=(clsPurchaseReturnDtlModel)ob[0];
        	clsProductMasterModel prodMaster=(clsProductMasterModel)ob[1];  
        	clsPurchaseReturnDtlModel objPRDtl=new clsPurchaseReturnDtlModel();
        	objPRDtl.setStrProdCode(PRProdDtl.getStrProdCode());
        	objPRDtl.setStrPRCode(PRProdDtl.getStrPRCode());
        	objPRDtl.setStrProdName(prodMaster.getStrProdName());
        	objPRDtl.setStrUOM(PRProdDtl.getStrUOM());
        	objPRDtl.setDblDiscount(PRProdDtl.getDblDiscount());
        	objPRDtl.setDblQty(PRProdDtl.getDblQty());        	
        	objPRDtl.setDblUnitPrice(PRProdDtl.getDblUnitPrice());
        	objPRDtl.setDblTotalPrice(PRProdDtl.getDblTotalPrice());
        	objPRDtl.setDblWeight(PRProdDtl.getDblWeight());
        	objPRDtl.setDblTotalWt(PRProdDtl.getDblTotalWt());
        	
        	listPrDtl.add(objPRDtl);
        	
        }
		return listPrDtl;
	}
	
	/**
	 *  Auditing Function Start
	 * @param PRCode
	 * @param req
	 */
	@SuppressWarnings("rawtypes")
	public void funSaveAudit(String PRCode,String strTransMode,HttpServletRequest  req)
	{
		objGlobal=new clsGlobalFunctions();
		try
		{
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String userCode=req.getSession().getAttribute("usercode").toString();
			clsPurchaseReturnHdModel prModel=objPurchaseReturnService.funGetObject(PRCode, clientCode);
			if(null != prModel){
			//String userCode=req.getSession().getAttribute("usercode").toString();
				List prDtlModel=objPurchaseReturnService.funGetDtlList(PRCode, clientCode);
			if(null != prDtlModel && prDtlModel.size() > 0)
	        {
				String sql="select count(*)+1 from tblaudithd where left(strTransCode,12)='"+prModel.getStrPRCode()+"'";
				List list=objGlobalFunctionsService.funGetList(sql,"sql");
				if(!list.isEmpty())
				{
					String count=list.get(0).toString();
					clsAuditHdModel model=funPrepairAuditHdModel(prModel);
					if(strTransMode.equalsIgnoreCase("Deleted"))
					{
						model.setStrTransCode(prModel.getStrPRCode());
					}
					else
					{
						model.setStrTransCode(prModel.getStrPRCode()+"-"+count);
					}
					model.setStrClientCode(clientCode);
					model.setStrTransMode(strTransMode);
					model.setStrUserAmed(userCode);
					model.setDtLastModified(objGlobal.funGetCurrentDate("yyyy-MM-dd"));
					model.setStrUserModified(userCode);
					objGlobalFunctionsService.funSaveAuditHd(model);
					for(int i=0;i<prDtlModel.size();i++)
					{
						Object[] ob=(Object[])prDtlModel.get(i);
						clsPurchaseReturnDtlModel PRDtlModel=(clsPurchaseReturnDtlModel)ob[0];
						clsAuditDtlModel AuditMode=new clsAuditDtlModel();
						AuditMode.setStrTransCode(model.getStrTransCode());
						AuditMode.setStrProdCode(PRDtlModel.getStrProdCode());
						AuditMode.setStrUOM("");
						AuditMode.setStrAgainst("");
						AuditMode.setStrCode("");
						AuditMode.setStrRemarks("");
						AuditMode.setDblDiscount(PRDtlModel.getDblDiscount());
						AuditMode.setDblQty(PRDtlModel.getDblQty());
						AuditMode.setDblUnitPrice(PRDtlModel.getDblUnitPrice());
						AuditMode.setDblTax(0.00);
						AuditMode.setDblTaxableAmt(0.00);
						AuditMode.setDblTaxAmt(0.00);
						AuditMode.setStrTaxType("");
						AuditMode.setDblTotalPrice(PRDtlModel.getDblTotalPrice());
						AuditMode.setStrPICode("");
						AuditMode.setStrClientCode(PRDtlModel.getStrClientCode());
						objGlobalFunctionsService.funSaveAuditDtl(AuditMode);
		            }
				}
				
	          }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
	}
	/**
	 * Prepare Audit HdModel
	 * @param PRModel
	 * @return
	 */
	private clsAuditHdModel funPrepairAuditHdModel(clsPurchaseReturnHdModel PRModel)
	{
		clsAuditHdModel AuditHdModel=new clsAuditHdModel();
		if(PRModel!=null)
		{
			AuditHdModel.setStrTransCode(PRModel.getStrPRCode());
			AuditHdModel.setDtTransDate(PRModel.getDtPRDate());
			AuditHdModel.setStrTransType("Purchase Return");
			AuditHdModel.setStrSuppCode(PRModel.getStrSuppCode());
			AuditHdModel.setStrAgainst(PRModel.getStrAgainst());
			AuditHdModel.setDblExtra(PRModel.getDblExtra());
			AuditHdModel.setDblDisRate(PRModel.getDblDisRate());
			AuditHdModel.setDblDiscount(PRModel.getDblDisAmt());
			AuditHdModel.setDblSubTotal(PRModel.getDblSubTotal());
			AuditHdModel.setDblTotalAmt(PRModel.getDblTotal());
			AuditHdModel.setStrAuthorise(PRModel.getStrAuthorise());
			AuditHdModel.setStrMInBy(PRModel.getStrMInBy());
			AuditHdModel.setStrTimeInOut(PRModel.getStrTimeInOut());
			AuditHdModel.setStrVehNo(PRModel.getStrVehNo());
			AuditHdModel.setStrGRNCode(PRModel.getStrGRNCode());
			AuditHdModel.setStrCode(PRModel.getStrPurCode());
			AuditHdModel.setStrLocCode(PRModel.getStrLocCode());
			AuditHdModel.setStrNarration(PRModel.getStrNarration());
			AuditHdModel.setStrNo(PRModel.getStrPRNo());
			AuditHdModel.setDtDateCreated(PRModel.getDtDateCreated());
			AuditHdModel.setStrUserCreated(PRModel.getStrUserCreated());
			AuditHdModel.setStrClosePO("");
			AuditHdModel.setStrExcise("");
			AuditHdModel.setStrLocBy("");
			AuditHdModel.setStrLocOn("");
			AuditHdModel.setStrCloseReq("");
			AuditHdModel.setStrWoCode("");
			AuditHdModel.setStrBillNo("");
			AuditHdModel.setDblWOQty(0);
			AuditHdModel.setStrNo("");
			AuditHdModel.setStrRefNo("");
			AuditHdModel.setStrShipmentMode("");
			AuditHdModel.setStrPayMode("");
		}
		return AuditHdModel;
	}
	/**
	 * End Function Audit
	 */
	
	/**	 
	 * Purchase Return Slip
	 * @return
	 * @throws JRException
	 */
	@RequestMapping(value = "/frmPurchaseReturnSlip", method = RequestMethod.GET)
	public ModelAndView funOpenPurchaseReturnSlipForm(Map<String, Object> model,
			HttpServletRequest request)
    {
		request.getSession().setAttribute("formName", "frmWebStockHelpPurchaseReturnSlip");
		String urlHits = "1";
		try {
			urlHits = request.getParameter("saddr").toString();
		} catch (NullPointerException e) {
			urlHits = "1";
		}
		model.put("urlHits", urlHits);

		if ("2".equalsIgnoreCase(urlHits)) {
			return new ModelAndView("frmPurchaseReturnSlip_1", "command",
					new clsReportBean());
		} else {
			return new ModelAndView("frmPurchaseReturnSlip", "command",
					new clsReportBean());
		}
    }

	public void funCallReport(String PRCode,String type,HttpServletResponse resp,HttpServletRequest req) 
	{
		try
		{
		List<JasperPrint> jprintlist =new ArrayList<JasperPrint>();
		ServletOutputStream servletOutputStream = resp.getOutputStream();
		
		JasperPrint p=funCallRangePrintReport(PRCode,resp,req);
		jprintlist.add(p);
		
		if(type.trim().equalsIgnoreCase("pdf"))
		{
			
        	     JRExporter exporter = new JRPdfExporter();
        	     resp.setContentType("application/pdf");
                 exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
                 exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream );
                 resp.setHeader( "Content-Disposition", "inline;filename=" + "rptPurchaseReturnSlip."+type.trim());                        
                 exporter.exportReport();  
                
                 servletOutputStream.flush();
                 servletOutputStream.close();
		}
		else if(type.trim().equalsIgnoreCase("xls"))
		{
			JRExporter exporterXLS = new JRXlsExporter();	 
			exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);  
			exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, resp.getOutputStream() );
            resp.setHeader( "Content-Disposition", "attachment;filename=" + "rptPurchaseReturnSlip."+type.trim());                        
            exporterXLS.exportReport();  
            resp.setContentType("application/xlsx");	
           
		}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		catch(JRException e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
	@RequestMapping(value = "/openRptPRSlip", method = RequestMethod.GET)	
	private void funCallReportOnSubmit( HttpServletResponse resp,HttpServletRequest req) throws JRException, IOException
	{
		String PRCode=req.getParameter("rptPRCode").toString();
		req.getSession().removeAttribute("rptPRCode");
		String type="pdf";
		
		List<JasperPrint> jprintlist =new ArrayList<JasperPrint>();
		ServletOutputStream servletOutputStream = resp.getOutputStream();
		
		JasperPrint p=funCallRangePrintReport(PRCode,resp,req);
		jprintlist.add(p);
		
		if(type.trim().equalsIgnoreCase("pdf"))
		{
			
        	     JRExporter exporter = new JRPdfExporter();
        	     resp.setContentType("application/pdf");
                 exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
                 exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream );
                 resp.setHeader( "Content-Disposition", "inline;filename=" + "rptPurchaseReturnSlip."+type.trim());                        
                 exporter.exportReport();  
                
                 servletOutputStream.flush();
                 servletOutputStream.close();
		}
		else if(type.trim().equalsIgnoreCase("xls"))
		{
			JRExporter exporterXLS = new JRXlsExporter();	 
			exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);  
			exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, resp.getOutputStream() );
            resp.setHeader( "Content-Disposition", "attachment;filename=" + "rptPurchaseReturnSlip."+type.trim());                        
            exporterXLS.exportReport();  
            resp.setContentType("application/xlsx");	
           
		}
	}
	
	/**
	 * Purchase Return Range Printing Report
	 * @param objBean
	 * @param resp
	 * @param req
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPurchaseReturnSlip", method = RequestMethod.POST)	
	private @ResponseBody void funReport(@ModelAttribute("command") clsReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
	{
		objGlobal=new clsGlobalFunctions();
		String dtFromDate=objGlobal.funGetDate("yyyy-MM-dd",objBean.getDtFromDate());
		String dtToDate=objGlobal.funGetDate("yyyy-MM-dd",objBean.getDtToDate());
		String strFromPRCode=objBean.getStrFromDocCode();
		String strToPRCode=objBean.getStrToDocCode();
		String type=objBean.getStrDocType();
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String tempLoc[] = objBean.getStrLocationCode().split(",");
		String tempSupp[] = objBean.getStrSuppCode().split(",");
		String strLocCodes = "";
		String strSuppCodes = "";
		
		for (int i = 0; i < tempLoc.length; i++) {
			if (strLocCodes.length() > 0) {
				strLocCodes = strLocCodes + " or a.strLocCode='"
						+ tempLoc[i] + "' ";
			} else {
				strLocCodes = " a.strLocCode='" + tempLoc[i] + "' ";

			}
		}

		for (int i = 0; i < tempSupp.length; i++) {
			if (strSuppCodes.length() > 0) {
				strSuppCodes = strSuppCodes + " or a.strSuppCode='" + tempSupp[i]
						+ "' ";
			} else {
				strSuppCodes = " a.strSuppCode='" + tempSupp[i] + "' ";

			}
		}
		try
		{
			 String sql="select strPRCode from tblpurchasereturnhd a where date(a.dtPRDate) between '"+dtFromDate+"' and '"+dtToDate+"' and a.strClientCode='"+clientCode+"'";
			 if(objBean.getStrLocationCode().length()>0 )
			 {
				 sql=sql+" and ("+strLocCodes+")  ";
			 }
			 if(objBean.getStrSuppCode().length()>0 )
			 {
				 sql=sql+" and ("+strSuppCodes+")  ";
			 }
			 if(strFromPRCode.trim().length()>0 && strToPRCode.trim().length()>0)
			 {
				 sql=sql+" and a.strPRCode between '"+strFromPRCode+"' and '"+strToPRCode+"' ";
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
		                 resp.setHeader( "Content-Disposition", "inline;filename=" + "rptPurchaseReturnSlip."+type.trim());                        
		                 exporter.exportReport();  
		                
		                 servletOutputStream.flush();
	                     servletOutputStream.close();
				}
				else if(type.trim().equalsIgnoreCase("xls"))
				{
					JRExporter exporterXLS = new JRXlsExporter();	 
					exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);  
					exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, resp.getOutputStream() );
		            resp.setHeader( "Content-Disposition", "attachment;filename=" + "rptPurchaseReturnSlip."+type.trim());                        
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
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	public JasperPrint funCallRangePrintReport(String PRCode,HttpServletResponse resp,HttpServletRequest req)
	{
		objGlobal=new clsGlobalFunctions();
		  Connection con=objGlobal.funGetConnection(req);
		  JasperPrint p=null;
		try {
            String clientCode=req.getSession().getAttribute("clientCode").toString();
			String companyName=req.getSession().getAttribute("companyName").toString();
			String userCode=req.getSession().getAttribute("usercode").toString();
			String propertyCode=req.getSession().getAttribute("propertyCode").toString();
						
			clsPropertySetupModel objSetup=objSetupMasterService.funGetObjectPropertySetup(propertyCode, clientCode);
			if(objSetup==null)
			{
				objSetup=new clsPropertySetupModel();
			}
			String sql="select a.strPRCode,a.dtPRDate,b.strLocName,c.strPName,ifnull(a.strAgainst,'') as strAgainst,"
					+ " ifnull(a.strGRNCode,'') as strGRNCode,a.strNarration,a.strMInBy,a.strTimeInOut,a.strVehNo,"
					+ " e.strProdCode,e.strProdName,d.strUOM as UOM,d.dblQty,d.dblUnitPrice,d.dblDiscount, "
					+ " d.dblTotalPrice,ifnull(d.dblTax,0) as dblTax,ifnull(d.dblTaxAmt,0) as dblTaxAmt, "
					+ " ifnull(d.dblTaxPercentage,'') as dblTaxPercentage,ifnull(d.dblTaxableAmt,'') as dblTaxableAmt, "
					+ " a.dblDisAmt,a.dblDisRate,a.dblExtra,a.dblSubTotal,ifnull(a.dblTaxAmt,0) as dblTaxAmt,ifnull(a.dblTotal,0) as dblTotal"
					+ " from tblpurchasereturnhd a,tbllocationmaster b,tblpartymaster c,tblpurchasereturndtl d,"
					+ " tblproductmaster e where d.strProdCode=e.strProdCode and a.strLocCode=b.strLocCode and"
					+ " a.strSuppCode=c.strPCode and a.strPRCode='"+PRCode+" ' "
					+ "and a.strClientCode='"+clientCode+"' and a.strClientCode='"+clientCode+"' "
					+ "and c.strClientCode='"+clientCode+"' and d.strClientCode='"+clientCode+"' ";
			String reportName=servletContext.getRealPath("/WEB-INF/reports/rptPurchaseReturnSlip.jrxml"); 
			String imagePath=servletContext.getRealPath("/resources/images/company_Logo.png");
           
        //  getting multi copy of small data in table in Detail thats why we add in subDataset   
           JasperDesign jd=JRXmlLoader.load(reportName);
           JRDesignQuery newQuery= new JRDesignQuery();
	        newQuery.setText(sql);
	        jd.setQuery(newQuery); 
          
           JasperReport jr=JasperCompileManager.compileReport(jd);
               
          HashMap hm = new HashMap();
          hm.put("strCompanyName",companyName);
          hm.put("strUserCode",userCode.toUpperCase());
          hm.put("strImagePath",imagePath);
          hm.put("strAddr1",objSetup.getStrAdd1() );
          hm.put("strAddr2",objSetup.getStrAdd2());            
          hm.put("strCity", objSetup.getStrCity());
          hm.put("strState",objSetup.getStrState());
          hm.put("strCountry", objSetup.getStrCountry());
          hm.put("strPin", objSetup.getStrPin());
          hm.put("strPhoneNo", objSetup.getStrPhone());
          hm.put("strEmailAddress", objSetup.getStrEmail());
          hm.put("strWebSite", objSetup.getStrWebsite());
          p=JasperFillManager.fillReport(jr, hm,con);
          
      } catch (Exception e) {
      e.printStackTrace();
      }finally{
      	try {
				con.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
      	return p;
      }
	}
	
	
	 private String funGenrateJVforPurchaseReturn(clsPurchaseReturnHdModel objModel,List<clsPurchaseReturnDtlModel> listDtlModel,String clientCode,String userCode,String propCode,HttpServletRequest req)
		{
			JSONObject jObjJVData =new JSONObject();
			
			JSONArray jArrJVdtl = new JSONArray();
			JSONArray jArrJVDebtordtl = new JSONArray();
			String jvCode ="";
			String custCode = objModel.getStrSuppCode();
			double debitAmt = objModel.getDblTotal();
			clsLinkUpHdModel objLinkCust = objLinkupService.funGetARLinkUp(custCode,clientCode,propCode);
			if(objLinkCust!=null)
			{
				if(objModel.getStrPRNo().equals(""))
				{
					jObjJVData.put("strVouchNo", "");
					jObjJVData.put("strNarration", "JV Genrated by Pur-Ret:"+objModel.getStrPRCode());
					jObjJVData.put("strSancCode", "");
					jObjJVData.put("strType", "");
					jObjJVData.put("dteVouchDate", objModel.getDtPRDate());
					jObjJVData.put("intVouchMonth", 1);
					jObjJVData.put("dblAmt", debitAmt);
					jObjJVData.put("strTransType", "R");
					jObjJVData.put("strTransMode", "A");
					jObjJVData.put("strModuleType", "AP");
					jObjJVData.put("strMasterPOS", "CRM");
					jObjJVData.put("strUserCreated", userCode);
					jObjJVData.put("strUserEdited", userCode);
					jObjJVData.put("dteDateCreated", objGlobalFunctions.funGetCurrentDateTime("yyyy-MM-dd"));
					jObjJVData.put("dteDateEdited", objGlobalFunctions.funGetCurrentDateTime("yyyy-MM-dd"));
					jObjJVData.put("strClientCode", clientCode);
					jObjJVData.put("strPropertyCode", propCode);
					
				}else
				{
					jObjJVData.put("strVouchNo", objModel.getStrPRNo());
					jObjJVData.put("strNarration", "JV Genrated by Pur-Ret:"+objModel.getStrGRNCode());
					jObjJVData.put("strSancCode", "");
					jObjJVData.put("strType", "");
					jObjJVData.put("dteVouchDate", objModel.getDtPRDate());
					jObjJVData.put("intVouchMonth", 1);
					jObjJVData.put("dblAmt", debitAmt);
					jObjJVData.put("strTransType", "R");
					jObjJVData.put("strTransMode", "A");
					jObjJVData.put("strModuleType", "AP");
					jObjJVData.put("strMasterPOS", "CRM");
					jObjJVData.put("strUserCreated", userCode);
					jObjJVData.put("strUserEdited", userCode);
					jObjJVData.put("dteDateCreated", objGlobalFunctions.funGetCurrentDateTime("yyyy-MM-dd"));
					jObjJVData.put("dteDateEdited", objGlobalFunctions.funGetCurrentDateTime("yyyy-MM-dd"));
					jObjJVData.put("strClientCode", clientCode);
					jObjJVData.put("strPropertyCode", propCode);
					
					
				}
				// jvhd entry end
				
				// jvdtl entry Start
				for(clsPurchaseReturnDtlModel objDtl : listDtlModel)
				{
					
					JSONObject jObjDtl =new JSONObject();
					
					clsProductMasterModel objProdModle=objProductMasterService.funGetObject(objDtl.getStrProdCode(),clientCode);
					clsLinkUpHdModel objLinkSubGroup = objLinkupService.funGetARLinkUp(objProdModle.getStrSGCode(),clientCode,propCode);
				if(objProdModle!=null && objLinkSubGroup!=null)
					{
						jObjDtl.put("strVouchNo", "");
						jObjDtl.put("strAccountCode", objLinkSubGroup.getStrAccountCode());
						jObjDtl.put("strAccountName", objLinkSubGroup.getStrGDes());
						jObjDtl.put("strCrDr", "Cr");
						jObjDtl.put("dblDrAmt",  0.00);
						jObjDtl.put("dblCrAmt",objDtl.getDblTotalPrice());
						jObjDtl.put("strNarration", "WS Product code :"+objDtl.getStrProdCode());
						jObjDtl.put("strOneLine", "R");
						jObjDtl.put("strClientCode", clientCode);
						jObjDtl.put("strPropertyCode", propCode);
						jArrJVdtl.add(jObjDtl);
					}	
				}
				
				/*if(listTaxDtl!=null)
				{
					for(clsGRNTaxDtlModel objTaxDtl : listTaxDtl)
					{
						JSONObject jObjTaxDtl =new JSONObject();
						clsLinkUpHdModel objLinkTax = objLinkupService.funGetARLinkUp(objTaxDtl.getStrTaxCode(),clientCode);
						if(objLinkTax!=null )
						{
							jObjTaxDtl.put("strVouchNo", "");
							jObjTaxDtl.put("strAccountCode", objLinkTax.getStrAccountCode());
							jObjTaxDtl.put("strAccountName", objLinkTax.getStrGDes());
							jObjTaxDtl.put("strCrDr", "Cr");
							jObjTaxDtl.put("dblDrAmt", 0.00);
							jObjTaxDtl.put("dblCrAmt", objTaxDtl.getStrTaxAmt());
							jObjTaxDtl.put("strNarration", "WS Tax Desc :"+objTaxDtl.getStrTaxDesc());
							jObjTaxDtl.put("strOneLine", "R");
							jObjTaxDtl.put("strClientCode", clientCode);
							jObjTaxDtl.put("strPropertyCode", propCode);
							jArrJVdtl.add(jObjTaxDtl);
						}
					}
				}*/
				JSONObject jObjCustDtl =new JSONObject();
				jObjCustDtl.put("strVouchNo", "");
				jObjCustDtl.put("strAccountCode", "");
				jObjCustDtl.put("strAccountName", "");
				jObjCustDtl.put("strCrDr", "Dr");
				jObjCustDtl.put("dblDrAmt", objModel.getDblTotal());
				jObjCustDtl.put("dblCrAmt", 0.00);
				jObjCustDtl.put("strNarration", "GRN Supplier Return");
				jObjCustDtl.put("strOneLine", "R");
				jObjCustDtl.put("strClientCode", clientCode);
				jObjCustDtl.put("strPropertyCode", propCode);
				jArrJVdtl.add(jObjCustDtl);
				
				jObjJVData.put("ArrJVDtl", jArrJVdtl);
				
				// jvdtl entry end
				
				// jvDebtor detail entry start
				String sql = " select a.strPRCode,a.dblTotal,b.strDebtorCode,b.strPName,date(a.dtPRDate),  a.strNarration ,date(a.dtPRDate),'' "
						+ " from dbwebmms.tblpurchasereturnhd a,dbwebmms.tblpartymaster b   "
						+ " where a.strSuppCode =b.strPCode   "
						+ " and a.strPRCode='"+objModel.getStrPRCode()+"' "
						+ " and a.strClientCode='"+objModel.getStrClientCode()+"'   " ; 
				List listTax=objGlobalFunctionsService.funGetList(sql,"sql");		
				for(int i=0;i<listTax.size();i++)
				{
						JSONObject jObjDtl =new JSONObject();
						Object[] ob=(Object[])listTax.get(i);
						jObjDtl.put("strVouchNo", "");
						jObjDtl.put("strDebtorCode", ob[2].toString());
						jObjDtl.put("strDebtorName", ob[3].toString());
						jObjDtl.put("strCrDr", "Dr");
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
	
}
