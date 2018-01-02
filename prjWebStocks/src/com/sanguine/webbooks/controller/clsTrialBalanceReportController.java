package com.sanguine.webbooks.controller;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.Connection;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.model.clsPropertySetupModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsSetupMasterService;
import com.sanguine.webbooks.bean.clsCreditorOutStandingReportBean;
import com.sanguine.webbooks.model.clsCurrentAccountBalMaodel;

@Controller
public class clsTrialBalanceReportController {

	private clsGlobalFunctions objGlobal = null;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private clsSetupMasterService objSetupMasterService;

	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;

	// Open Buget Form
	@RequestMapping(value = "/frmTrialBalanceReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,
			HttpServletRequest request) {
		String urlHits = "1";
		try {
			urlHits = request.getParameter("saddr").toString();
		} catch (NullPointerException e) {
			urlHits = "1";
		}
		model.put("urlHits", urlHits);
		if ("2".equalsIgnoreCase(urlHits)) {
			return new ModelAndView("frmTrialBalanceReport_1",
					"command", new clsCreditorOutStandingReportBean());
		} else if ("1".equalsIgnoreCase(urlHits)) {
			return new ModelAndView("frmTrialBalanceReport", "command",
					new clsCreditorOutStandingReportBean());
		} else {
			return null;
		}

	}

	@RequestMapping(value = "/rptTrialBalanceReport", method = RequestMethod.GET)
	private void funReport(
			@ModelAttribute("command") clsCreditorOutStandingReportBean objBean,
			HttpServletResponse resp, HttpServletRequest req) {
		objGlobal = new clsGlobalFunctions();
		Connection con = objGlobal.funGetConnection(req);
		try {

			String clientCode = req.getSession().getAttribute("clientCode").toString();
			String companyName = req.getSession().getAttribute("companyName").toString();
			String userCode = req.getSession().getAttribute("usercode").toString();
			String propertyCode = req.getSession().getAttribute("propertyCode").toString();
			clsPropertySetupModel objSetup = objSetupMasterService.funGetObjectPropertySetup(propertyCode, clientCode);
			if (objSetup == null) {
				objSetup = new clsPropertySetupModel();
			}
			String type="PDF";
			String fromDate = objBean.getDteFromDate();
			String toDate = objBean.getDteToDate();

			String fd = fromDate.split("-")[0];
			String fm = fromDate.split("-")[1];
			String fy = fromDate.split("-")[2];

			String td = toDate.split("-")[0];
			String tm = toDate.split("-")[1];
			String ty = toDate.split("-")[2];

			String dteFromDate = fy + "-" + fm + "-" + fd;
			String dteToDate = ty + "-" + tm + "-" + td;
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webbooks/rptTrialBalanceReport.jrxml");
			String imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");

			ArrayList fieldList = new ArrayList();
			String startDate=req.getSession().getAttribute("startDate").toString();
			
			String[] sp=startDate.split(" ");
			String[] spDate=sp[0].split("/");
			startDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
			HashMap<String , Double> hmMap=new HashMap<String,Double>();
			
			
			String sql = " select b.strGroupCode,b.strGroupName,a.strAccountCode,a.strAccountName "
						+ " from tblacmaster a,tblacgroupmaster b "
						+ " where a.strGroupCode=b.strGroupCode and  a.strClientCode='"+clientCode+"' ";
			
			List listAc = objGlobalFunctionsService.funGetListModuleWise(sql,"sql");
			{
				 if(listAc.size()>0)
		         {
		      	   for( int i = 0 ; i<listAc.size() ;i++)
		      	   {
		      		   Object [] objArr=  (Object[]) listAc.get(i);
		      		   String acCode=objArr[2].toString();
		      		   double openingbal =0.00;
		      		   if(!startDate.equals(dteFromDate))
			 			{
			      			String tempFromDate=dteFromDate.split("-")[2]+"-"+dteFromDate.split("-")[1]+"-"+dteFromDate.split("-")[0];
			    			SimpleDateFormat obj=new SimpleDateFormat("dd-MM-yyyy");
			    			Date dt1;
			    			try 
			    			{
			    				dt1 = obj.parse(tempFromDate);
			    				GregorianCalendar cal = new GregorianCalendar();
			    	            cal.setTime(dt1);
			    	            cal.add(Calendar.DATE, -1);
			    	            String newToDate = (cal.getTime().getYear() + 1900) + "-" + (cal.getTime().getMonth() + 1) + "-" + (cal.getTime().getDate());            
			    	            
			    	            funInsertCurrentAccountBal(acCode,startDate, newToDate ,userCode,propertyCode,clientCode);	
			    	            
			    	            sql = " select sum(a.dblDrAmt-a.dblCrAmt)  "
			    	            		+ " from tblcurrentaccountbal a "
			    	            		+ " where a.strAccountCode='"+acCode+"' and a.strUserCode='"+userCode+"' "
			    	            		+ " and a.strPropertyCode='"+propertyCode+"' and a.strClientCode='"+clientCode+"' group by a.strAccountCode ";
			    	            
			    	            List listOpBalOld = objGlobalFunctionsService.funGetListModuleWise(sql,"sql");	
			    	            {
			    	            	if(listOpBalOld.size()>0)
			    			         {
			    	            		openingbal =  Double.parseDouble(listOpBalOld.get(0).toString());
			    			         }
			    	            }
			    	            funInsertCurrentAccountBal(acCode,dteFromDate, dteToDate ,userCode,propertyCode,clientCode);  
			    	            
			    	            sql = " SELECT c.strGroupCode,c.strGroupName,a.strAccountCode,a.strAccountName, "
			    	            		+ " ifnull(SUM(b.dblCrAmt),0.00), ifnull(SUM(b.dblDrAmt),0.00) "
			    	            		+ " FROM tblacmaster a "
			    	            		+ " left outer join tblcurrentaccountbal b on a.strAccountCode=b.strAccountCode AND b.strUserCode='"+userCode+"' AND b.strPropertyCode='"+propertyCode+"' AND b.strClientCode='"+clientCode+"' "
			    	            		+ " left outer join tblacgroupmaster c on  a.strGroupCode=c.strGroupCode "
			    	            		+ " WHERE   a.strAccountCode='"+acCode+"' GROUP BY a.strAccountCode; ";
			    	            
			    	            List listMainBal = objGlobalFunctionsService.funGetListModuleWise(sql,"sql");	
			    	            {
			    	            	if(listMainBal.size()>0)
			    			         {
			    	            		for (int j = 0; j < listMainBal.size(); j++) 
			    	            		{
				    	            		clsCreditorOutStandingReportBean objRtpBean = new clsCreditorOutStandingReportBean();
				    	            		Object[] balArr = (Object[]) listMainBal.get(j);
				    	            	
				    	    				objRtpBean.setDblOpnAmt(openingbal);	
				    	    				objRtpBean.setStrGroupCode(balArr[0].toString());
				    	    				objRtpBean.setStrGroupName(balArr[1].toString());
				    	    				objRtpBean.setStrAccountCode(balArr[2].toString());
				    	    				objRtpBean.setStrAccountName(balArr[3].toString());
				    	    				objRtpBean.setDblCrAmt(Double.parseDouble(balArr[4].toString()));
				    	    				objRtpBean.setDblDrAmt(Double.parseDouble(balArr[5].toString()));
				    	    				fieldList.add(objRtpBean);
			    	            		
			    	            		}
			    			         }
			    	            }
			    	            
			    			}catch(Exception ex)
			    			{
			    				ex.printStackTrace();
			    			}
			 			}else
			 			{
			 				
			 				funInsertCurrentAccountBal(acCode,dteFromDate, dteToDate ,userCode,propertyCode,clientCode);  
		    	            
		    	            sql = " select c.strGroupCode,c.strGroupName,a.strAccountCode,a.strAccountName,sum(a.dblCrAmt),sum(a.dblDrAmt) "
		    	            		+ " from tblcurrentaccountbal a,tblacmaster b,tblacgroupmaster c "
		    	            		+ " where a.strAccountCode=b.strAccountCode  and b.strGroupCode=c.strGroupCode "
		    	            		+ " and a.strAccountCode='"+acCode+"' and a.strUserCode='"+userCode+"' "
		    	            		+ " and a.strPropertyCode='"+propertyCode+"' and a.strClientCode='"+clientCode+"' group by a.strAccountCode "; 
		    	            
			    	            List listMainBal = objGlobalFunctionsService.funGetListModuleWise(sql,"sql");	
			    	            {
			    	            	if(listMainBal.size()>0)
			    			         {
			    	            		for (int j = 0; j < listMainBal.size(); j++) 
			    	            		{
				    	            		clsCreditorOutStandingReportBean objRtpBean = new clsCreditorOutStandingReportBean();
				    	            		Object[] balArr = (Object[]) listMainBal.get(j);
				    	            	
				    	    				objRtpBean.setDblOpnAmt(openingbal);	
				    	    				objRtpBean.setStrGroupCode(balArr[0].toString());
				    	    				objRtpBean.setStrGroupName(balArr[1].toString());
				    	    				objRtpBean.setStrAccountCode(balArr[2].toString());
				    	    				objRtpBean.setStrAccountName(balArr[3].toString());
				    	    				objRtpBean.setDblCrAmt(Double.parseDouble(balArr[4].toString()));
				    	    				objRtpBean.setDblDrAmt(Double.parseDouble(balArr[5].toString()));
				    	    				fieldList.add(objRtpBean);
			    	            		
			    	            		}
			    			         }
			    	            }
			 				}
		      		   
		      	   }
				
		         }
			}
			
			
			
			/*if(!startDate.equals(dteFromDate))
			{
				String tempFromDate=dteFromDate.split("-")[2]+"-"+dteFromDate.split("-")[1]+"-"+dteFromDate.split("-")[0];
				SimpleDateFormat obj=new SimpleDateFormat("dd-MM-yyyy");
				Date dt1;
				
				try 
				{
					Date dt2=obj.parse(startDate);
					dt1 = obj.parse(tempFromDate);
					
					if(dt2.before(dt1))
					{
					
					GregorianCalendar cal = new GregorianCalendar();
		            cal.setTime(dt1);
		            cal.add(Calendar.DATE, -1);
		            String newToDate = (cal.getTime().getYear() + 1900) + "-" + (cal.getTime().getMonth() + 1) + "-" + (cal.getTime().getDate());            
		            double balance=0.0;
		         
		            
		            String sql="select b.strGroupCode,b.strGroupName,a.strAccountCode,a.strAccountName,d.dblCrAmt,d.dblDrAmt "
							+" from tblacmaster a,tblacgroupmaster b ,tbljvhd c,tbljvdtl d "
							+" where c.dteVouchDate between '"+startDate+"' and '"+newToDate+"'  and a.strAccountCode=d.strAccountCode and a.strGroupCode=b.strGroupCode "
							+" and d.strVouchNo=c.strVouchNo group by a.strAccountCode  order by b.strGroupCode";
		            objGlobalFunctionsService.funUpdate(sql,"sql");
		            
                       
					List list = objGlobalFunctionsService.funGetDataList(sql, "sql");
					
					for (int j = 0; j < list.size(); j++) {
						Object[] objData = (Object[]) list.get(j);
						balance=(Double.parseDouble(objData[4].toString()))-(Double.parseDouble(objData[5].toString()));
						hmMap.put(objData[2].toString(), balance);
					}
				}
				}
				catch (ParseException e) 
				{
					e.printStackTrace();
				}
			}
    	

			
			
			String sqlQuery ="select b.strGroupCode,b.strGroupName,a.strAccountCode,a.strAccountName,d.dblCrAmt,d.dblDrAmt "
							+" from tblacmaster a,tblacgroupmaster b ,tbljvhd c,tbljvdtl d "
							+" where c.dteVouchDate between '"+dteFromDate+"' and '"+dteToDate+"'  and a.strAccountCode=d.strAccountCode and a.strGroupCode=b.strGroupCode "
							+" and d.strVouchNo=c.strVouchNo group by a.strAccountCode  ";
					

			List listProdDtl = objGlobalFunctionsService.funGetDataList(sqlQuery, "sql");

			for (int j = 0; j < listProdDtl.size(); j++) {
				clsCreditorOutStandingReportBean objProdBean = new clsCreditorOutStandingReportBean();
				Object[] prodArr = (Object[]) listProdDtl.get(j);
				if(hmMap.containsKey(prodArr[2].toString()))
				{
					objProdBean.setDblOpnAmt(hmMap.get(prodArr[2].toString()));	
				}else{
					objProdBean.setDblOpnAmt(0.0);
				}
				objProdBean.setStrGroupCode(prodArr[0].toString());
				objProdBean.setStrGroupName(prodArr[1].toString());
				objProdBean.setStrAccountCode(prodArr[2].toString());
				objProdBean.setStrAccountName(prodArr[3].toString());
				objProdBean.setDblCrAmt(Double.parseDouble(prodArr[4].toString()));
				objProdBean.setDblDrAmt(Double.parseDouble(prodArr[5].toString()));
				fieldList.add(objProdBean);

			}*/

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
			hm.put("dteFromDate", dteFromDate);
			hm.put("dteToDate", dteToDate);

			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
			

			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(
					fieldList);
			JasperPrint print = JasperFillManager.fillReport(jr, hm,
					beanCollectionDataSource);
			jprintlist.add(print);
			ServletOutputStream servletOutputStream = resp.getOutputStream();
			if (jprintlist.size() > 0) {
				// if(objBean.getStrDocType().equals("PDF"))
				// {
				JRExporter exporter = new JRPdfExporter();
				resp.setContentType("application/pdf");
				exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST,jprintlist);
				exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM,servletOutputStream);
				exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS,Boolean.TRUE);
				// resp.setHeader("Content-Disposition",
				// "inline;filename=rptProductWiseGRNReport_"+dteFromDate+"_To_"+dteToDate+"_"+userCode+".pdf");
				exporter.exportReport();
				servletOutputStream.flush();
				servletOutputStream.close();

				 }else
				 {
				 JRExporter exporter = new JRXlsExporter();
				resp.setContentType("application/xlsx");
				exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST,jprintlist);
				exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM,servletOutputStream);
				exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS,Boolean.TRUE);
				// resp.setHeader("Content-Disposition",
				// "inline;filename=rptProductWiseGRNReport_"+dteFromDate+"_To_"+dteToDate+"_"+userCode+".xls");
				exporter.exportReport();
				servletOutputStream.flush();
				servletOutputStream.close();
				// }

			}
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

	}
	
	public int funInsertCurrentAccountBal(String acCode,String fromDate,String toDate ,String userCode,String propertyCode,String clientCode)
	{
		String sql = "";
		
		sql = " Delete from tblcurrentaccountbal where a.strUserCode='"+userCode+"' and a.strPropertyCode='"+propertyCode+"' AND a.strClientCode='"+clientCode+"' ";
		objGlobalFunctionsService.funDeleteWebBookCurrentAccountBal(clientCode, userCode, propertyCode);
		
		sql = " INSERT INTO tblcurrentaccountbal (strAccountCode, strAccountName,  dteBalDate,strDrCrCode, strTransecType, dblDrAmt,dblCrAmt,dblBalAmt,strUserCode, strPropertyCode, strClientCode)  "
					+ " (SELECT b.strAccountCode,b.strAccountName,DATE_FORMAT(DATE(a.dteVouchDate),'%d-%m-%Y'),'','JV', sum(b.dblDrAmt),sum(b.dblCrAmt),sum(b.dblDrAmt - b.dblCrAmt),'"+userCode+"','"+propertyCode+"','"+clientCode+"' "
					+ " FROM tbljvhd a, tbljvdtl b "
					+ " WHERE a.strVouchNo=b.strVouchNo AND DATE(a.dteVouchDate) BETWEEN '"+fromDate+"' AND '"+toDate+"' "
					+ " AND b.strAccountCode='"+acCode+"'  AND a.strPropertyCode=b.strPropertyCode "
					+ " AND a.strPropertyCode='"+propertyCode+"' AND a.strClientCode='"+clientCode+"' group by b.strAccountCode ) ";
		 
			objGlobalFunctionsService.funUpdateAllModule(sql,"sql");	
			
			sql = " INSERT INTO tblcurrentaccountbal (strAccountCode, strAccountName,  dteBalDate,strDrCrCode, strTransecType, dblDrAmt,dblCrAmt,dblBalAmt,strUserCode, strPropertyCode, strClientCode)  "
					+" (SELECT b.strAccountCode,b.strAccountName,DATE_FORMAT(DATE(a.dteVouchDate),'%d-%m-%Y'),'','Payment', sum(b.dblDrAmt),sum(b.dblCrAmt),sum(b.dblDrAmt - b.dblCrAmt),'super','01','189.001' "
					+ "  FROM tblpaymenthd a, tblpaymentdtl b "
					+ " WHERE a.strVouchNo=b.strVouchNo AND DATE(a.dteVouchDate) BETWEEN '"+fromDate+"' AND '"+toDate+"' "
					+ " AND b.strAccountCode='"+acCode+"'  AND a.strPropertyCode=b.strPropertyCode "
					+ " AND a.strPropertyCode='"+propertyCode+"' AND a.strClientCode='"+clientCode+"' group by b.strAccountCode ) ";
			
			objGlobalFunctionsService.funUpdateAllModule(sql,"sql");	
			
			sql = " INSERT INTO tblcurrentaccountbal (strAccountCode, strAccountName,  dteBalDate,strDrCrCode, strTransecType, dblDrAmt,dblCrAmt,dblBalAmt,strUserCode, strPropertyCode, strClientCode)  "
					+ " (SELECT b.strAccountCode,b.strAccountName,DATE_FORMAT(DATE(a.dteVouchDate),'%d-%m-%Y'),'','Receipt', sum(b.dblDrAmt),sum(b.dblCrAmt),sum(b.dblDrAmt - b.dblCrAmt),'super','01','189.001' "
					+ " FROM tblreceipthd a, tblreceiptdtl b  "
					+ " WHERE a.strVouchNo=b.strVouchNo AND DATE(a.dteVouchDate) BETWEEN '"+fromDate+"' AND '"+toDate+"' "
					+ " AND b.strAccountCode='"+acCode+"'  AND a.strPropertyCode=b.strPropertyCode "
					+ " AND a.strPropertyCode='"+propertyCode+"' AND a.strClientCode='"+clientCode+"' group by b.strAccountCode ) ";
			
			objGlobalFunctionsService.funUpdateAllModule(sql,"sql");
		
		return 1;
		
	}
	
}
