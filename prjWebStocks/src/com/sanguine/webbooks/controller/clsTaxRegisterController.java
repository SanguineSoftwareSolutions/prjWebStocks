package com.sanguine.webbooks.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignQuery;
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

@Controller
public class clsTaxRegisterController {

	private clsGlobalFunctions objGlobal = null;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private clsSetupMasterService objSetupMasterService;

	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;

	// Open Buget Form
	@RequestMapping(value = "/frmTaxRegister", method = RequestMethod.GET)
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
			return new ModelAndView("frmTaxRegister_1",
					"command", new clsCreditorOutStandingReportBean());
		} else if ("1".equalsIgnoreCase(urlHits)) {
			return new ModelAndView("frmTaxRegister", "command",
					new clsCreditorOutStandingReportBean());
		} else {
			return null;
		}

	}

	@RequestMapping(value = "/rptTaxRegister", method = RequestMethod.GET)
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

			String fromDate = objBean.getDteFromDate();
			String toDate = objBean.getDteToDate();
			String type="PDF";
			String fd = fromDate.split("-")[0];
			String fm = fromDate.split("-")[1];
			String fy = fromDate.split("-")[2];

			String td = toDate.split("-")[0];
			String tm = toDate.split("-")[1];
			String ty = toDate.split("-")[2];

			String dteFromDate = fy + "-" + fm + "-" + fd;
			String dteToDate = ty + "-" + tm + "-" + td;
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webbooks/rptTaxRegister.jrxml");
			String imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");

			ArrayList fieldList = new ArrayList();

//			String sqlQuery = "select a.dteVouchDate,a.strVouchNo ,DATE_FORMAT( a.dteVouchDate,'%d-%m-%Y'),ifNull(a.strNarration,'') ,sum(b.dblDrAmt) "
//							+ " from tbljvhd a,tbljvdtl b,tbljvdebtordtl c  "
//							+ " where a.strVouchNo= b.strVouchNo and a.strVouchNo=c.strVouchNo  and a.strModuleType='AP' AND a.dteVouchDate between '"+dteFromDate+"' and '"+dteToDate+"' "
//							+" group by a.strVouchNo";
			
			
			String sqlQuery = "select b.strInvCode,DATE_FORMAT(date(c.dteInvDate),'%d-%m-%Y') as dteInvDate ,b.dblTaxableAmt,b.dblTaxAmt,a.strTaxDesc,d.strSettlementDesc, c.strUserCreated " 
							 +" from  dbwebmms.tbltaxhd a,dbwebmms.tblinvtaxdtl b,dbwebmms.tblinvoicehd c ,dbwebmms.tblsettlementmaster d "
							 +" where a.strTaxCode=b.strTaxCode and b.strInvCode=c.strInvCode and c.strSettlementCode=d.strSettlementCode  "
							 +" and c.dteInvDate between '"+dteFromDate+"' and '"+dteToDate+"' and c.strClientCode='"+clientCode+"' and a.strTaxOnSP='Sales'  ";

			  
			  JasperDesign jd=JRXmlLoader.load(reportName);
	          JRDesignQuery taxQuery = new JRDesignQuery();
	          taxQuery.setText(sqlQuery);
	          Map<String, JRDataset> datasetMap = jd.getDatasetMap();
	          JRDesignDataset taxDataset = (JRDesignDataset) datasetMap.get("dsTax");
	          taxDataset.setQuery(taxQuery);
			
			
		   String taxSummary="select a.strTaxCode,a.strTaxDesc,b.dblTaxableAmt,b.dblTaxAmt from  dbwebmms.tbltaxhd a left outer join dbwebmms.tblinvtaxdtl b "
					        +" on a.strTaxCode=b.strTaxCode  where a.strTaxOnSP='Sales' ";

			   JRDesignQuery taxSummQuery = new JRDesignQuery();
			   taxSummQuery.setText(taxSummary);
	           JRDesignDataset taxSumDataset = (JRDesignDataset) datasetMap.get("dsTaxSummary");
	           taxSumDataset.setQuery(taxSummQuery);
	

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

			
			
			 JasperReport jr=JasperCompileManager.compileReport(jd);
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
		            resp.setHeader( "Content-Disposition", "attachment;filename=" + "rptTaxRegister."+type.trim() );                        
		            exporterXLS.exportReport();  
		            resp.setContentType("application/xlsx");	
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
}
