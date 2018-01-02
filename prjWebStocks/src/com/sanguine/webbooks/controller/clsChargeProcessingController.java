package com.sanguine.webbooks.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JRViewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lowagie.text.pdf.hyphenation.TernaryTree.Iterator;
import com.mysql.jdbc.Connection;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.model.clsPropertySetupModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsSetupMasterService;
import com.sanguine.webbooks.bean.clsChargeProcessingBean;
import com.sanguine.webbooks.bean.clsParameterSetupBean;
import com.sanguine.webbooks.model.clsBankMasterModel;
import com.sanguine.webbooks.model.clsChargeMasterModel;
import com.sanguine.webbooks.model.clsChargeProcessingDtlModel;
import com.sanguine.webbooks.model.clsChargeProcessingHDModel;
import com.sanguine.webbooks.model.clsParameterSetupModel;
import com.sanguine.webbooks.model.clsWebBooksAccountMasterModel;
import com.sanguine.webbooks.service.clsChargeMasterService;
import com.sanguine.webbooks.service.clsChargeProcessingService;

@Controller
public class clsChargeProcessingController
{
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	@Autowired
	private clsChargeMasterService objChargeMasterService;
	@Autowired
	private clsChargeProcessingService objChargeProcessingService;
	@Autowired
	private clsSetupMasterService objSetupMasterService;
	@Autowired
	private ServletContext servletContext;
	
	private clsGlobalFunctions objGlobal = null;

	// Open Charge Processing
	@RequestMapping(value = "/frmChargeProcessing", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String,Object>model,HttpServletRequest request)
	{
		String urlHits = "1";
		try
		{
			urlHits = request.getParameter("saddr").toString();
		}
		catch (NullPointerException e)
		{
			urlHits = "1";
		}
		
		ArrayList<String>listOtherFunctions=new ArrayList<String>();
		listOtherFunctions.add("Process");
		listOtherFunctions.add("Charge Slip");
		
		model.put("urlHits", urlHits);
		model.put("listOtherFunctions", listOtherFunctions);		
		
		if (urlHits.equalsIgnoreCase("1"))
		{
			return new ModelAndView("frmChargeProcessing", "command",new clsChargeProcessingBean());
		}
		else
		{
			return new ModelAndView("frmChargeProcessing_1", "command",new clsChargeProcessingBean());
		}
	}
	
	//Load Master Data On Form
	@RequestMapping(value = "/loadAllChargesFromChargeMaster", method = RequestMethod.GET)
	public @ResponseBody List<clsChargeMasterModel> funAssignFields(HttpServletRequest req)
	{
		List<clsChargeMasterModel> listChargeModel=null;
		
		String clientCode = req.getSession().getAttribute("clientCode").toString();
		String userCode = req.getSession().getAttribute("usercode").toString();
		String propertyCode=req.getSession().getAttribute("propertyCode").toString();
		
		List<clsChargeMasterModel> listModel=objChargeMasterService.funGetAllChargesData(clientCode,propertyCode);
		
			
		return listModel;
	}	
	
	//Load Master Data On Form
		@RequestMapping(value = "/loadMemberData", method = RequestMethod.GET)
		public @ResponseBody Object funAssignFields(@RequestParam("memberCode")String memberCode,HttpServletRequest req)
		{
			List listmemberData=null;
			
			String clientCode = req.getSession().getAttribute("clientCode").toString();
			String userCode = req.getSession().getAttribute("usercode").toString();
			String propertyCode=req.getSession().getAttribute("propertyCode").toString();
			
			listmemberData=objGlobalFunctionsService.funGetDataList("select strDebtorCode,CONCAT_WS(' ',strPrefix,strFirstName,strMiddleName,strLastName)  as strdebtorFullName "
																	+"from tblsundarydebtormaster where strDebtorCode='"+memberCode+"' ","sql");
			
			Object []obj=(Object[])listmemberData.get(0);
				
			return obj;
		}	
		
		@RequestMapping(value = "/saveChargeProcessing", method = RequestMethod.POST)
		public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsChargeProcessingBean objBean,BindingResult result,HttpServletRequest req,HttpServletResponse response)
		{
			String urlHits="1";
			try
			{
				urlHits=req.getParameter("saddr").toString();
			}
			catch(NullPointerException e)
			{
				urlHits="1";
			}
			if (!result.hasErrors())
			{
				String clientCode = req.getSession().getAttribute("clientCode").toString();
				String userCode = req.getSession().getAttribute("usercode").toString();
				String propertyCode=req.getSession().getAttribute("propertyCode").toString();
			 	String companyName=req.getSession().getAttribute("companyName").toString();
				
				funPreparedAndSaveModel(objBean,userCode,clientCode,propertyCode,req);															
				
				req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage","Charge Processing");								
				
				return new ModelAndView("redirect:/frmChargeProcessing.html?saddr="+urlHits);
			}
			else
			{
				return new ModelAndView("redirect:/frmChargeProcessing.html?saddr="+urlHits);
			}
		}

		@SuppressWarnings({ "unchecked", "unchecked", "unchecked" })
		@RequestMapping(value="/rptChargeProcessingSlip",method=RequestMethod.GET)
		public void funGenerateJasperReport(@RequestParam("memberCode")String memberCode,@RequestParam("fromDate")String fromDate,@RequestParam("toDate")String toDate,@RequestParam("generatedOnDate")String generatedOnDate,HttpServletRequest req,HttpServletResponse resp) 
		{		
			try
			{
				String clientCode = req.getSession().getAttribute("clientCode").toString();
				String userCode = req.getSession().getAttribute("usercode").toString();
				String propertyCode=req.getSession().getAttribute("propertyCode").toString();
			 	String companyName=req.getSession().getAttribute("companyName").toString();
				
				clsPropertySetupModel objSetup=objSetupMasterService.funGetObjectPropertySetup(propertyCode, clientCode);
				if(objSetup==null)
				{
					objSetup=new clsPropertySetupModel();
				}
				String reportName=servletContext.getRealPath("/WEB-INF/reports/webbooks/rptChargeProcessingSlip.jrxml"); 
				String imagePath=servletContext.getRealPath("/resources/images/company_Logo.png");
				
				String sqlDtl="select a.strMemberCode,a.strMemberName,b.strChargeCode,b.strChargeName,d.strAccountName,a.dblAmount,a.strType,a.strCrDr, "
				+"a.strNarration,a.dteFromDate,a.dteToDate,a.dteGeneratedOn,a.strClientCode,a.strPropertyCode "
				+"from "
				+"tblchargegenerationtemp a, "
				+"tblchargemaster b, "
				+"tblsundarydebtormaster c, "
				+"tblacmaster d "
				+"where a.strChargeCode=b.strChargeCode "
				+"and a.strMemberCode=c.strDebtorCode "
				+"and a.strAccountCode=d.strAccountCode ";
				if(!memberCode.equalsIgnoreCase("All"))
				{
					sqlDtl=sqlDtl+"and a.strMemberCode='"+memberCode+"'";
				}
			
				sqlDtl=sqlDtl+"and a.dteFromDate='"+fromDate+"'";
				sqlDtl=sqlDtl+"and a.dteToDate='"+toDate+"'";
				sqlDtl=sqlDtl+"and a.dteGeneratedOn='"+generatedOnDate+"'";
						
				sqlDtl=sqlDtl+"group by a.strMemberCode ,a.strChargeCode ";
				sqlDtl=sqlDtl+"order by a.strMemberCode ,a.strChargeCode ";	       			
				
				 JasperDesign jd=JRXmlLoader.load(reportName);
				 JRDesignQuery subQuery = new JRDesignQuery();
				 subQuery.setText(sqlDtl);		
				 jd.setQuery(subQuery);
				 JasperReport jr=JasperCompileManager.compileReport(jd);
	   
	     
			     @SuppressWarnings("rawtypes")
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
			     hm.put("strclientCode",clientCode );
			   //  hm.put("dteFromDate",objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteFromDate()));
			   //  hm.put("dteToDateDate",objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteToDate()));
			   //  hm.put("dteGeneratedOn"*/,objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteGeneratedOn())); 
			    
			     Connection con=null;
				    try
				    {
				        Class.forName("com.mysql.jdbc.Driver");
				        con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/dbwebbooks?user=root&password=root");
				    }
				    catch(Exception e)
				    {
				    	System.out.println("Error in conection");
				    }
			     String type="pdf";       
			     
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
			           resp.setHeader( "Content-Disposition", "attachment;filename=" + "rptChargeProcessingSlip."+type.trim() );                        
			           exporterXLS.exportReport();  
			           resp.setContentType("application/xlsx");	
					}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		private void funPreparedAndSaveModel(clsChargeProcessingBean objBean, String userCode,String clientCode, String propertyCode,HttpServletRequest req) 
		{
			objGlobal=new clsGlobalFunctions();
			List listMemberOutstanding=null;
			
			List<clsChargeProcessingDtlModel>listOfChargesToBeProcess=new ArrayList<clsChargeProcessingDtlModel>();
			
			java.util.Iterator<clsChargeProcessingDtlModel>it=objBean.getListChargeDtl().iterator();
			//to get only selected charges
			while(it.hasNext())
			{
				clsChargeProcessingDtlModel objDtlModel=it.next();
				if(objDtlModel.getIsProcessYN()!=null && objDtlModel.getIsProcessYN().equalsIgnoreCase("Y"))
				{
					listOfChargesToBeProcess.add(objDtlModel);					
				}
			}
			
			//calculate outstanding and clear temp table  for particular member
			if(objBean.getStrMemberCode()!=null && objBean.getStrMemberCode().length()>0)
			{
				
				req.getSession().setAttribute("memberCode", objBean.getStrMemberCode());
				
				req.getSession().setAttribute("fromDate", objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteFromDate()));
				req.getSession().setAttribute("toDate", objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteToDate()));
				req.getSession().setAttribute("generatedOnDate", objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteGeneratedOn()));
				
				//Debtor Controll AC Code '0015-01-00'
				listMemberOutstanding=objChargeProcessingService.funCalculateOutstanding("0015-01-00",objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteFromDate()),objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteToDate()),objBean.getStrMemberCode());
				
				objChargeProcessingService.funClearTblChargeGenerationTemp(objBean.getStrMemberCode());
			}
			else//calculate outstanding and clear temp table  for All members
			{
				req.getSession().setAttribute("memberCode","All");
				req.getSession().setAttribute("fromDate", objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteFromDate()));
				req.getSession().setAttribute("toDate", objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteToDate()));
				req.getSession().setAttribute("generatedOnDate", objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteGeneratedOn()));
				
				//Debtor Controll AC Code '0015-01-00'
				listMemberOutstanding=objChargeProcessingService.funCalculateOutstanding("0015-01-00",objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteFromDate()),objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteToDate()),"All");
				
				objChargeProcessingService.funClearTblChargeGenerationTemp("All");
			}
				Map<String,Double>mapMemOutstanding=new HashMap<String,Double>();
				
				if(listMemberOutstanding!=null && listMemberOutstanding.size()>0)
				{
					
					for(int i=0;i<listMemberOutstanding.size();i++)
					{
						Map<String,Double>mapMemOutstand=(HashMap<String, Double>)listMemberOutstanding.get(i);
						java.util.Iterator<Entry<String, Double>>itMemOutStand =mapMemOutstand.entrySet().iterator();
						Entry<String,Double>entry=itMemOutStand.next();
						
						mapMemOutstanding.put(entry.getKey(),entry.getValue());
					}
				}
								
				java.util.Iterator<String>itOutstandingCalculatedMembers=mapMemOutstanding.keySet().iterator();
				
				while(itOutstandingCalculatedMembers.hasNext())
				{					
					String memberCode=itOutstandingCalculatedMembers.next();					
					
					for(int i=0;i<listOfChargesToBeProcess.size();i++)
					{											
						String chargeCode=listOfChargesToBeProcess.get(i).getStrChargeCode();
						
						//to check if charge applicable to this member
						List list=objChargeProcessingService.funIsChargeApplicable(memberCode,chargeCode,clientCode,propertyCode);
						//if charge is applicable to this member
						if(list!=null && list.size()>0)
						{
							Object[]objCodeNameOutStanding=(Object[])list.get(0);
							
							String memberName=objCodeNameOutStanding[1].toString();
							
							clsChargeProcessingHDModel objHDModel=new clsChargeProcessingHDModel();
							
							objHDModel.setStrMemberCode(memberCode);							
							objHDModel.setStrMemberName(memberName);								
							objHDModel.setDteFromDate(objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteFromDate()));
							objHDModel.setDteToDate(objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteToDate()));
							objHDModel.setDteGeneratedOn(objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteGeneratedOn()));
							objHDModel.setStrInstantJVYN(objBean.getStrInstantJVYN());
							objHDModel.setStrOtherFunctions(objBean.getStrOtherFunctions());
							objHDModel.setStrAnnualChargeProcessYN(objBean.getStrAnnualChargeProcessYN());																							
							
							objHDModel.setStrChargeCode(chargeCode);
							objHDModel.setStrAccountCode(listOfChargesToBeProcess.get(i).getStrAccountCode());
							objHDModel.setDblAmount(listOfChargesToBeProcess.get(i).getDblAmount());
							objHDModel.setStrType(listOfChargesToBeProcess.get(i).getStrType());
							objHDModel.setStrCrDr(listOfChargesToBeProcess.get(i).getStrCrDr());
							objHDModel.setStrNarration(listOfChargesToBeProcess.get(i).getStrNarration());
							
							objHDModel.setStrClientCode(clientCode);
							objHDModel.setStrPropertyCode(propertyCode);
							
							
							if(mapMemOutstanding.containsKey(memberCode))
							{
								double dblOutstanding=mapMemOutstanding.get(memberCode);
								
								objChargeProcessingService.funUpdateMemberOutstanding(memberCode,dblOutstanding,clientCode,propertyCode);
													
								double chargeAmt=listOfChargesToBeProcess.get(i).getDblAmount();
								String chargeType=listOfChargesToBeProcess.get(i).getStrType();
								if(chargeType.equalsIgnoreCase("Percentage"))
								{
									chargeAmt=(chargeAmt/100)*dblOutstanding;
									
									objHDModel.setDblAmount(chargeAmt);
									objHDModel.setStrType("Amount");
								}																		
							}
							
							objChargeProcessingService.funAddUpdateChargeProcessing(objHDModel);
						}											
					}
				}
		}									
}
