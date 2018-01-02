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
import com.sanguine.webbooks.model.clsLedgerSummaryModel;

@Controller
public class clsCreditorTrialBalanceController {

	
	private clsGlobalFunctions objGlobal = null;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private clsSetupMasterService objSetupMasterService;

	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@RequestMapping(value = "/frmCreditorTrialBalanceReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String,Object> model,HttpServletRequest request)
	{
		String urlHits = "1";
		try {
			urlHits = request.getParameter("saddr").toString();
		} catch (NullPointerException e) {
			urlHits = "1";
		}
		model.put("urlHits", urlHits);
		if ("2".equalsIgnoreCase(urlHits)) {
			return new ModelAndView("frmCreditorTrialBalanceReport_1",
					"command", new clsCreditorOutStandingReportBean());
		} else if ("1".equalsIgnoreCase(urlHits)) {
			return new ModelAndView("frmCreditorTrialBalanceReport", "command",
					new clsCreditorOutStandingReportBean());
		} else {
			return null;
		}
		
	}
	
	@RequestMapping(value = "/rptCreditorTrialBalanceReport", method = RequestMethod.GET)
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
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webbooks/rptCreditorTrialBalanceReport.jrxml");
			String imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");

			ArrayList fieldList = new ArrayList();
			String startDate=req.getSession().getAttribute("startDate").toString();
			
			String[] sp=startDate.split(" ");
			String[] spDate=sp[0].split("/");
			startDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
			HashMap<String , Double> hmMap=new HashMap<String,Double>();
			
			 String sunCrAccountCode="";
			 String sunCrAccountName = "";
			String sqlCrAc = " select a.strAccountCode,a.strAccountName "
					+ " from tblacmaster a where a.strType='Gl Code' and a.strOperational='Yes' "
					+ " and a.strCreditor='Yes' and a.strClientCode='"+clientCode+"' and a.strPropertyCode='"+propertyCode+"'  ";
			
			List listSunCrAc = objGlobalFunctionsService.funGetListModuleWise(sqlCrAc,"sql");
			{
				if(listSunCrAc.size()>0)
		         {
		      		   Object [] objArr=  (Object[]) listSunCrAc.get(0);
		      		   sunCrAccountCode=objArr[0].toString();
		      		   sunCrAccountName=objArr[1].toString();
		         }	   
			}
			
			String sql = " select a.strCreditorCode,a.strCreditorFullName  "
					+ " from tblsundarycreditormaster a where a.strClientCode='"+clientCode+"' and a.strPropertyCode='"+propertyCode+"' ";
		
			List listSunCr = objGlobalFunctionsService.funGetListModuleWise(sql,"sql");
			{
			 if(listSunCr.size()>0)
	         {
	      	   for( int i = 0 ; i<listSunCr.size() ;i++)
	      	   {
	      		   Object [] objArr=  (Object[]) listSunCr.get(i);
	      		   String sunCrCode=objArr[0].toString();
	      		   String sunCrName=objArr[1].toString();
	      		   double openingbal =0.00;
	      		
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
		    	            
		    	            funProcessWebBookCreditorTrailBal(sunCrAccountCode,sunCrCode,startDate,newToDate,clientCode,userCode,propertyCode, req, resp );
		    	            
		    	            /*sql = " select sum(a.dblDebitAmt-a.dblCreditAmt) from tblledgersummary a "
		    	            		+ " where  a.strUserCode='"+userCode+"' "
		    	            		+ " and a.strPropertyCode='"+propertyCode+"' and a.strClientCode='"+clientCode+"' group by a.strUserCode,a.strPropertyCode,strClientCode ";*/
		    	            sql=" select sum(bal) from  (select (IF(a.strCrDr='Dr',a.dblOpeningbal,0) - IF(a.strCrDr='Cr',a.dblOpeningbal,0)) bal "
		    	            		+ " from tblsundarycreditoropeningbalance a "
		    	            		+ " where a.strCreditorCode='"+sunCrCode+"' and a.strAccountCode='"+sunCrAccountCode+"' AND a.strClientCode='"+clientCode+"' "
		    	            		+ " UNION All "
		    	            		+ " SELECT SUM(a.dblDebitAmt-a.dblCreditAmt) bal "
		    	            		+ " FROM tblledgersummary a "
		    	            		+ " WHERE a.strUserCode='super' AND a.strPropertyCode='"+propertyCode+"' "
		    	            		+ " AND a.strClientCode='"+clientCode+"' "
		    	            		+ " GROUP BY a.strUserCode,a.strPropertyCode,strClientCode ) opening "; 
		    	            
		    	            
		    	            List listOpBalOld = objGlobalFunctionsService.funGetListModuleWise(sql,"sql");	
		    	            {
		    	            	if(listOpBalOld.size()>0)
		    			         {
		    	            		openingbal =  Double.parseDouble(listOpBalOld.get(0).toString());
		    			         }
		    	            }
		    	            
		    	            funProcessWebBookCreditorTrailBal(sunCrAccountCode,sunCrCode,dteFromDate,dteToDate,clientCode,userCode,propertyCode, req, resp );
		    	            
		    	            sql = " select sum(a.dblCreditAmt),sum(a.dblDebitAmt) "
		    	            		+ " from tblledgersummary a "
		    	            		+ " where a.strUserCode='"+userCode+"' "
		    	            		+ " and a.strPropertyCode='"+propertyCode+"' and a.strClientCode='"+clientCode+"' "
		    	            		+ " group by a.strUserCode,a.strPropertyCode,a.strClientCode  "; 
		    	            
		    	            List listMainBal = objGlobalFunctionsService.funGetListModuleWise(sql,"sql");	
		    	            for (int j = 0; j < listMainBal.size(); j++) {
		    					clsCreditorOutStandingReportBean objRptBean = new clsCreditorOutStandingReportBean();
		    					Object[] obArr = (Object[]) listMainBal.get(j);
		    					objRptBean.setDblOpnAmt(openingbal);	
		    					objRptBean.setStrDebtorCode(sunCrCode);
		    					objRptBean.setStrDebtorName(sunCrName);
		    					objRptBean.setDblCrAmt(Double.parseDouble(obArr[0].toString()));
		    					objRptBean.setDblDrAmt(Double.parseDouble(obArr[1].toString()));
		    					fieldList.add(objRptBean);
		    	            }
		    	            
		    			}catch (Exception e) {
						// TODO: handle exception
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

			            String sql="select a.strVouchNo, sum(b.dblCrAmt),sum(b.dblDrAmt) ,c.strDebtorCode from tbljvhd a,tbljvdtl b ,tbljvdebtordtl c "
			            		  +" where a.strVouchNo=b.strVouchNo  and a.strVouchNo=c.strVouchNo and a.strModuleType='AP' and  "
			            		  +" a.dteVouchDate between '"+startDate+"' and '"+newToDate+"'  and a.strClientCode='"+clientCode+"' "
			            		  +" group by c.strDebtorCode  ";
			            objGlobalFunctionsService.funUpdate(sql,"sql");
						
			            List list = objGlobalFunctionsService.funGetDataList(sql, "sql");
						
						for (int j = 0; j < list.size(); j++) {
							Object[] objData = (Object[]) list.get(j);
							balance=(Double.parseDouble(objData[1].toString()))-(Double.parseDouble(objData[2].toString()));
							hmMap.put(objData[3].toString(), balance);
						}
					}
				}
				catch (ParseException e) 
				{
					e.printStackTrace();
				}
			}
			String sqlQuery ="select c.strDebtorCode,c.strDebtorName, sum(b.dblCrAmt),sum(b.dblDrAmt)  from tbljvhd a,tbljvdtl b ,tbljvdebtordtl c "
          		  +" where a.strVouchNo=b.strVouchNo  and a.strVouchNo=c.strVouchNo and a.strModuleType='AP' and  "
          		  +" a.dteVouchDate between '"+dteFromDate+"' and '"+dteToDate+"'  and a.strClientCode='"+clientCode+"' "
          		  +" group by c.strDebtorCode  ";

			List listProdDtl = objGlobalFunctionsService.funGetDataList(sqlQuery, "sql");

			for (int j = 0; j < listProdDtl.size(); j++) {
				clsCreditorOutStandingReportBean objProdBean = new clsCreditorOutStandingReportBean();
				Object[] prodArr = (Object[]) listProdDtl.get(j);
				if(hmMap.containsKey(prodArr[0].toString()))
				{
					objProdBean.setDblOpnAmt(hmMap.get(prodArr[0].toString()));	
				}else{
					objProdBean.setDblOpnAmt(0.0);
				}
				objProdBean.setStrDebtorCode(prodArr[0].toString());
				objProdBean.setStrDebtorName(prodArr[1].toString());
				objProdBean.setDblCrAmt(Double.parseDouble(prodArr[2].toString()));
				objProdBean.setDblDrAmt(Double.parseDouble(prodArr[3].toString()));
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
		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

	}
	
	
	@SuppressWarnings("rawtypes")
	public int funProcessWebBookCreditorTrailBal(String acCode,String detorCretditorCode,String fromDate,String toDate,String clientCode,String userCode,String propertyCode,HttpServletRequest req,HttpServletResponse resp )
	{
		objGlobalFunctions.funDeleteAndInsertWebBookLedgerTable(clientCode, userCode, propertyCode);
		// jv		
		
		String sql= " SELECT DATE_FORMAT(DATE(a.dteVouchDate),'%d-%m-%Y') ,a.strVouchNo,'JV'  ,c.strBillNo , DATE_FORMAT(DATE(c.dteInvoiceDate),'%d-%m-%Y') , "
				+ " b.dblDrAmt ,b.dblCrAmt ,b.dblDrAmt - b.dblCrAmt ,'Cr','','1','"+userCode+"','"+propertyCode+"','"+clientCode+"' "
				+ " FROM tbljvhd a, tbljvdtl b,tbljvdebtordtl c "
				+ " WHERE a.strVouchNo=b.strVouchNo AND a.strVouchNo=c.strVouchNo "
				+ " AND DATE(a.dteVouchDate) BETWEEN '"+fromDate+"' AND '"+toDate+"' "
				+ " AND c.strDebtorCode='"+detorCretditorCode+"'  and b.strCrDr='Cr' "
				+ " AND a.strPropertyCode=b.strPropertyCode AND a.strPropertyCode='"+propertyCode+"' AND a.strClientCode='"+clientCode+"' ";
		List listjv = objGlobalFunctionsService.funGetListModuleWise(sql,"sql");
		 if(listjv.size()>0)
         {
      	   for( int i = 0 ; i<listjv.size() ;i++)
      	   {
      		   clsLedgerSummaryModel objSummaryledger = new clsLedgerSummaryModel();
      		   Object [] objArr=  (Object[]) listjv.get(i);
      		   objSummaryledger.setDteVochDate(objArr[0].toString());
      		   objSummaryledger.setStrVoucherNo(objArr[1].toString());
      		   objSummaryledger.setStrTransType(objArr[2].toString());
      		   objSummaryledger.setStrChequeBillNo(objArr[3].toString());
      		   objSummaryledger.setDteBillDate(objArr[4].toString());
      		   objSummaryledger.setDblDebitAmt(Double.parseDouble(objArr[5].toString()));
    		   objSummaryledger.setDblCreditAmt(Double.parseDouble(objArr[6].toString()));;
    		   objSummaryledger.setDblBalanceAmt(Double.parseDouble(objArr[7].toString()));
    		   objSummaryledger.setStrBalCrDr(objArr[8].toString());
    		   objSummaryledger.setStrNarration(objArr[9].toString());
      		   objSummaryledger.setStrTransTypeForOrderBy(objArr[10].toString());
      		   objSummaryledger.setStrClientCode(clientCode);
      		   objSummaryledger.setStrUserCode(userCode);
      		   objSummaryledger.setStrPropertyCode(propertyCode);
      		
      		 objGlobalFunctionsService.funAddUpdateLedgerSummary(objSummaryledger);
      	   }
		
         }
		
      	    sql= " SELECT DATE_FORMAT(DATE(a.dteVouchDate),'%d-%m-%Y') ,a.strVouchNo,'Payment', a.strChequeNo,  DATE_FORMAT(DATE(a.dteChequeDate),'%d-%m-%Y') ,b.dblDrAmt , b.dblCrAmt ,b.dblDrAmt - b.dblCrAmt ,'Dr','','2','"+userCode+"','"+propertyCode+"','"+clientCode+"' "
		 		+ " FROM tblpaymenthd a, tblpaymentdtl b "
		 		+ " WHERE a.strVouchNo=b.strVouchNo "
		 		+ " AND DATE(a.dteVouchDate) BETWEEN '"+fromDate+"' AND '"+toDate+"' "
		 		+ " AND b.strDebtorCode='"+detorCretditorCode+"' AND a.strPropertyCode=b.strPropertyCode "
		 		+ " AND a.strPropertyCode='"+propertyCode+"' AND a.strClientCode='"+clientCode+"'  ";
      	    
      	 List listPay = objGlobalFunctionsService.funGetListModuleWise(sql,"sql");
 		 if(listPay.size()>0)
          {
       	   for( int i = 0 ; i<listPay.size() ;i++)
       	   {
       		   clsLedgerSummaryModel objSummaryledger = new clsLedgerSummaryModel();
       		   Object [] objArr=  (Object[]) listPay.get(i);
       		   objSummaryledger.setDteVochDate(objArr[0].toString());
       		   objSummaryledger.setStrVoucherNo(objArr[1].toString());
       		   objSummaryledger.setStrTransType(objArr[2].toString());
       		   objSummaryledger.setStrChequeBillNo(objArr[3].toString());
       		   objSummaryledger.setDteBillDate(objArr[4].toString());
       		   objSummaryledger.setDblDebitAmt(Double.parseDouble(objArr[5].toString()));
     		   objSummaryledger.setDblCreditAmt(Double.parseDouble(objArr[6].toString()));;
     		   objSummaryledger.setDblBalanceAmt(Double.parseDouble(objArr[7].toString()));
     		   objSummaryledger.setStrBalCrDr(objArr[8].toString());
     		   objSummaryledger.setStrNarration(objArr[9].toString());
       		   objSummaryledger.setStrTransTypeForOrderBy(objArr[10].toString());
       		   objSummaryledger.setStrClientCode(clientCode);
       		   objSummaryledger.setStrUserCode(userCode);
       		   objSummaryledger.setStrPropertyCode(propertyCode);
       		
       		 objGlobalFunctionsService.funAddUpdateLedgerSummary(objSummaryledger);
       	   }
          }
 		
 		 //receipt
 		 sql = "  SELECT DATE_FORMAT(DATE(a.dteVouchDate),'%d-%m-%Y') ,a.strVouchNo,'Recepit', a.strChequeNo, "
 		 		+ " DATE_FORMAT(DATE(a.dteChequeDate),'%d-%m-%Y') ,  b.dblDrAmt , b.dblCrAmt ,b.dblDrAmt - b.dblCrAmt ,b.strCrDr,'','3','"+userCode+"','"+propertyCode+"','"+clientCode+"' "
 		 		+ " FROM tblreceipthd a, tblreceiptdtl b ,tblreceiptdebtordtl c  WHERE a.strVouchNo=b.strVouchNo  and b.strVouchNo=c.strVouchNo "
 		 		+ " AND DATE(a.dteVouchDate) BETWEEN '"+fromDate+"' AND '"+toDate+"' "
 		 		+ " and  c.strDebtorCode='"+detorCretditorCode+"'  "
 		 		+ " AND a.strPropertyCode=b.strPropertyCode "
 		 		+ " AND a.strPropertyCode='"+propertyCode+"' AND a.strClientCode='"+clientCode+"'  "; 
      	  
 		 List listRecept = objGlobalFunctionsService.funGetListModuleWise(sql,"sql");
		 if(listRecept.size()>0)
         {
      	   for( int i = 0 ; i<listRecept.size() ;i++)
      	   {
      		   clsLedgerSummaryModel objSummaryledger = new clsLedgerSummaryModel();
      		   Object [] objArr=  (Object[]) listRecept.get(i);
      		   objSummaryledger.setDteVochDate(objArr[0].toString());
      		   objSummaryledger.setStrVoucherNo(objArr[1].toString());
      		   objSummaryledger.setStrTransType(objArr[2].toString());
      		   objSummaryledger.setStrChequeBillNo(objArr[3].toString());
      		   objSummaryledger.setDteBillDate(objArr[4].toString());
      		   objSummaryledger.setDblDebitAmt(Double.parseDouble(objArr[5].toString()));
    		   objSummaryledger.setDblCreditAmt(Double.parseDouble(objArr[6].toString()));;
    		   objSummaryledger.setDblBalanceAmt(Double.parseDouble(objArr[7].toString()));
    		   objSummaryledger.setStrBalCrDr(objArr[8].toString());
    		   objSummaryledger.setStrNarration(objArr[9].toString());
      		   objSummaryledger.setStrTransTypeForOrderBy(objArr[10].toString());
      		   objSummaryledger.setStrClientCode(clientCode);
      		   objSummaryledger.setStrUserCode(userCode);
      		   objSummaryledger.setStrPropertyCode(propertyCode);
      		
      		 objGlobalFunctionsService.funAddUpdateLedgerSummary(objSummaryledger);
      	   }
         }
 		 
 		 
		return 1;
	
	}

	
	
}
