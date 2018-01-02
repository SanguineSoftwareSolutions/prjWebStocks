package com.sanguine.webpos.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsBillDtl;
import com.sanguine.webpos.bean.clsGroupSubGroupItemBean;
import com.sanguine.webpos.bean.clsPOSItemWiseSalesReportBean;
import com.sanguine.webpos.bean.clsWebPOSReportBean;
import com.sanguine.webpos.util.clsCreditBillReportComparator;

@Controller
public class clsPOSCreditBillOutstandingReport {

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController; 
	 @Autowired
	 private ServletContext servletContext;
	
	 Map<String,String> hmPOSData;
	 
	 @RequestMapping(value = "/frmPOSCreditBillOutstandingReport", method = RequestMethod.GET)
		public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
		{
			String strClientCode=request.getSession().getAttribute("clientCode").toString();	
			String urlHits="1";
			try{
				urlHits=request.getParameter("saddr").toString();
			}catch(NullPointerException e){
				urlHits="1";
			}
			model.put("urlHits",urlHits);
			List poslist = new ArrayList();
			poslist.add("All");
			
			hmPOSData=new HashMap<String, String>();
			JSONArray jArryPosList =objPOSGlobalFunctionsController.funGetAllPOSForMaster(strClientCode);
			for(int i =0 ;i<jArryPosList.size();i++)
			{
				JSONObject josnObjRet = (JSONObject) jArryPosList.get(i);
				poslist.add(josnObjRet.get("strPosName"));
				hmPOSData.put(josnObjRet.get("strPosName").toString(),josnObjRet.get("strPosCode").toString());
			}
			model.put("posList",poslist);
			
			if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSCreditBillOutstandingReport_1","command", new clsWebPOSReportBean());
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSCreditBillOutstandingReport","command", new clsWebPOSReportBean());
			}else {
				return null;
			}
			 
		}
	 
	 
		

		
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/rptPOSCreditBillOutstandingReport", method = RequestMethod.POST)	
		private void funReport(@ModelAttribute("command") clsWebPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
		{
			//objGlobal=new clsGlobalFunctions();
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String userCode=req.getSession().getAttribute("usercode").toString();
			String companyName=req.getSession().getAttribute("companyName").toString();
			//String posCode=req.getSession().getAttribute("loginPOS").toString();
			List listLive = null;
			List listQFile = null;
			List listModLive = null;
			List listModQFile =null;
			try
			{
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCreditBillReport.jrxml");
			String imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");//
			
			 List<JasperPrint> jprintlist =new ArrayList<JasperPrint>();
			
			String strFromdate=objBean.getFromDate().split("-")[2]+"-"+objBean.getFromDate().split("-")[1]+"-"+objBean.getFromDate().split("-")[0];
			
			String strToDate=objBean.getToDate().split("-")[2]+"-"+objBean.getToDate().split("-")[1]+"-"+objBean.getToDate().split("-")[0]; 
			
			String type=objBean.getStrDocType();
			
			String strPOSName =objBean.getStrPOSName();
			String posCode= "All";
			
			if(!strPOSName.equalsIgnoreCase("All"))
			{
				posCode= hmPOSData.get(strPOSName);
			}
			
			JSONObject jObjFillter = new JSONObject();
			jObjFillter.put("strFromdate", strFromdate);
			jObjFillter.put("strToDate", strToDate);
			jObjFillter.put("posCode", posCode);
			jObjFillter.put("strShiftNo", "1");
			jObjFillter.put("userCode", userCode);
			
		
			 HashMap hm = new HashMap();
	         hm.put("posCode", posCode);
	         hm.put("posName", strPOSName);
	         hm.put("imagePath", imagePath);
	         hm.put("clientName", companyName);
	         hm.put("fromDateToDisplay", strFromdate);
	         hm.put("toDateToDisplay", strToDate);
	         hm.put("shiftNo", "1");
	         hm.put("userName", userCode);
	         
			JSONObject jObj = objGlobalFunctions.funPOSTMethodUrlJosnObjectData("http://localhost:8080/prjSanguineWebService/WebPOSReport/funCreditBillOutstandingReport",jObjFillter);
			List<clsBillDtl> listOfCreditBillOutsatndingData =new ArrayList<clsBillDtl>();
			List<clsBillDtl> list =new ArrayList<clsBillDtl>();
			JSONArray jarr = (JSONArray) jObj.get("jArr");
			for(int i=0;i<jarr.size();i++)
			{
				JSONObject jObjtemp =(JSONObject) jarr.get(i);
				

				
				clsBillDtl objBillDtlBean=new clsBillDtl();
				objBillDtlBean.setStrCustomerName(jObjtemp.get("strCustomerName").toString());
				objBillDtlBean.setStrCustomerCode(jObjtemp.get("strCustomerCode").toString());
			
				objBillDtlBean.setStrBillNo(jObjtemp.get("strBillNo").toString()); 
				objBillDtlBean.setDteBillDate(jObjtemp.get("dteBillDate").toString());
				objBillDtlBean.setDblBillAmt(Double.parseDouble(jObjtemp.get("dblBillAmt").toString()));
				objBillDtlBean.setDblBalanceAmt(Double.parseDouble(jObjtemp.get("dblBalanceAmt").toString()));
				objBillDtlBean.setStrReceiptNo(jObjtemp.get("strReceiptNo").toString());
				objBillDtlBean.setDteReceiptDate(jObjtemp.get("dteReceiptDate").toString());
				objBillDtlBean.setDblAmount(Double.parseDouble(jObjtemp.get("dblAmount").toString()));
				objBillDtlBean.setStrSettlementName(jObjtemp.get("strSettlementName").toString());
				objBillDtlBean.setStrChequeNo(jObjtemp.get("strChequeNo").toString());
				objBillDtlBean.setStrBankName(jObjtemp.get("strBankName").toString());
				objBillDtlBean.setStrRemark(jObjtemp.get("strRemark").toString());
			
				
				
				listOfCreditBillOutsatndingData.add(objBillDtlBean);
			}
			
			Comparator<clsBillDtl> customerComparator = new Comparator<clsBillDtl>()
		            {

		                @Override
		                public int compare(clsBillDtl o1, clsBillDtl o2)
		                {
		                    return o1.getStrCustomerCode().compareToIgnoreCase(o2.getStrCustomerCode());
		                }
		            };
		            Comparator<clsBillDtl> billComparator = new Comparator<clsBillDtl>()
		            {

		                @Override
		                public int compare(clsBillDtl o1, clsBillDtl o2)
		                {
		                    return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
		                }
		            };

		            Comparator<clsBillDtl> receiptComparator = new Comparator<clsBillDtl>()
		            {

		                @Override
		                public int compare(clsBillDtl o1, clsBillDtl o2)
		                {
		                    return o1.getStrReceiptNo().compareToIgnoreCase(o2.getStrReceiptNo());
		                }
		            };

		            Collections.sort(listOfCreditBillOutsatndingData, new clsCreditBillReportComparator(
		                    customerComparator,
		                    billComparator,
		                    receiptComparator)
		            );
	           
	            
	            
		    	    JasperDesign jd = JRXmlLoader.load(reportName);
		    	    JasperReport jr = JasperCompileManager.compileReport(jd);
		    	   
		    	   //jp =  JasperFillManager.fillReport(jr, hm,  new JREmptyDataSource());
		            
		         
		            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfCreditBillOutsatndingData);
		            JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
		            jprintlist.add(print);
						
		            
		            if (jprintlist.size()>0)
				    {
				    	ServletOutputStream servletOutputStream = resp.getOutputStream();
				    	if(objBean.getStrDocType().equals("PDF"))
				    	{
						JRExporter exporter = new JRPdfExporter();
						resp.setContentType("application/pdf");
						exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
						exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
						exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
						resp.setHeader("Content-Disposition", "inline;filename=CreditBillOutstandingReport_"+strFromdate+"_To_"+strToDate+"_"+userCode+".pdf");
					    exporter.exportReport();
						servletOutputStream.flush();
						servletOutputStream.close();
				    	}else
				    	{
				    		JRExporter exporter = new JRXlsExporter();
				    		resp.setContentType("application/xlsx");
							exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
							exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
							exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
							resp.setHeader("Content-Disposition", "inline;filename=CreditBillOutstandingReport_"+strFromdate+"_To_"+strToDate+"_"+userCode+".xls");
						    exporter.exportReport();
							servletOutputStream.flush();
							servletOutputStream.close();
				    	}
				    }else
				    {
				    	 resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				    	resp.getWriter().append("No Record Found");
				 		
				    }
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		    
			System.out.println("Hi");
				
		}
		
		
		
	 
}
