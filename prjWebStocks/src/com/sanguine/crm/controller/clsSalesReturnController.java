package com.sanguine.crm.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.crm.bean.clsInvoiceBean;
import com.sanguine.crm.bean.clsInvoiceDtlBean;
import com.sanguine.crm.bean.clsSalesReturnBean;
import com.sanguine.crm.model.clsInvSettlementdtlModel;
import com.sanguine.crm.model.clsInvoiceHdModel;
import com.sanguine.crm.model.clsInvoiceModelDtl;
import com.sanguine.crm.model.clsPartyMasterModel;
import com.sanguine.crm.model.clsSalesRetrunTaxModel;
import com.sanguine.crm.model.clsSalesReturnDtlModel;
import com.sanguine.crm.model.clsSalesReturnHdModel;
import com.sanguine.crm.model.clsSalesReturnHdModel_ID;
import com.sanguine.crm.service.clsInvoiceHdService;
import com.sanguine.crm.service.clsSalesReturnService;
import com.sanguine.model.clsLocationMasterModel;
import com.sanguine.model.clsProductMasterModel;
import com.sanguine.model.clsSettlementMasterModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsProductMasterService;
import com.sanguine.service.clsSetupMasterService;
import com.sanguine.service.clsUOMService;


@Controller
public class clsSalesReturnController {
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	@Autowired
	private clsGlobalFunctions objGlobalfunction;
	
	private clsGlobalFunctions objGlobal=null;
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private clsUOMService objclsUOMService;
	
	@Autowired
	private clsSetupMasterService objSetupMasterService;
	
	@Autowired
	private clsSalesReturnService objSalesReturnService;
	
	@Autowired
	private clsInvoiceHdService objInvoiceHdService;
	
	@Autowired
	private clsProductMasterService objProductMasterService;
	
	//Open Sales Return
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/frmSalesReturn", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String,Object> model, HttpServletRequest request){
		
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		ArrayList list=new ArrayList();
			list.add("Direct");
			list.add("Invoice");
			list.add("Delivery Challan");
			list.add("Retail Billing");
			model.put("againstList", list);
		
	        if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmSalesReturn_1","command", new clsSalesReturnBean());
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmSalesReturn","command", new clsSalesReturnBean());
			}else {
				return null;
			}     
	        
	}
	
	

	//Save or Update Invoice
		@SuppressWarnings("unused")
		@RequestMapping(value = "/saveSalesReturnData", method = RequestMethod.POST)
		public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsSalesReturnBean objBean ,BindingResult result,HttpServletRequest req){
			boolean flgHdSave=false;
			String urlHits="1";
			try{
				urlHits=req.getParameter("saddr").toString();
			}catch(NullPointerException e){
				urlHits="1";
			}
			if(!result.hasErrors()){
				
				
				String clientCode=req.getSession().getAttribute("clientCode").toString();
				String userCode=req.getSession().getAttribute("usercode").toString();
				String propCode=req.getSession().getAttribute("propertyCode").toString();
				String startDate=req.getSession().getAttribute("startDate").toString();
				clsSalesReturnHdModel objHdModel = funPrepareHdModel(objBean,userCode,clientCode,req);
				//String date=objHdModel.getDteInvDate().split("/");
			/*	String[] InvDate = objHdModel.getDteSRDate().split("/");
				String date = InvDate[2]+"-"+InvDate[0]+"-"+InvDate[1];*/
				objHdModel.setDteSRDate(objHdModel.getDteSRDate());
				flgHdSave=objSalesReturnService.funAddUpdateSalesReturnHd(objHdModel);
				String strSRCode=objHdModel.getStrSRCode();
				List<clsSalesReturnDtlModel> listSRDtlModel=objBean.getListSalesReturn();
				if(flgHdSave)
				{
					if(null!=listSRDtlModel)
					{
						objSalesReturnService.funDeleteDtl(strSRCode,clientCode);
						int intindex=1;
						
						for(clsSalesReturnDtlModel obSrDtl:listSRDtlModel)
						{
							if(null!=obSrDtl.getStrProdCode())
							{
								
								obSrDtl.setStrSRCode(strSRCode);
								obSrDtl.setStrClientCode(clientCode);
								if(objHdModel.getStrAgainst().equalsIgnoreCase("Delivery Challan"))
								{
									String sqlCloseDC="update tbldeliverychallanhd set strCloseDC='Y'  where strDCCode='"+objBean.getStrDCCode()+"' and strClientCode='"+clientCode+"'";
									objGlobalFunctionsService.funUpdateAllModule(sqlCloseDC,"sql");
								}else{if(objHdModel.getStrAgainst().equalsIgnoreCase("Invoice")){
									
									String sqlCloseDC="update tblinvoicehd set strCloseDC='Y'  where strInvCode='"+objBean.getStrDCCode()+"' and strClientCode='"+clientCode+"'";
									objGlobalFunctionsService.funUpdateAllModule(sqlCloseDC,"sql");
								}
									
								}
								objSalesReturnService.funAddUpdateSalesReturnDtl(obSrDtl);
								intindex++;
							}
						}
					//	    Save Data SalesReturn Tax
						if(null!=objBean.getListSalesRetrunTaxModel() )
						{
							for(clsSalesRetrunTaxModel ojInvoiceTaxDtlModel:objBean.getListSalesRetrunTaxModel())
									
							{
								objSalesReturnService.funDeleteTax(strSRCode,clientCode);
								ojInvoiceTaxDtlModel.setStrClientCode(clientCode);
								ojInvoiceTaxDtlModel.setStrSRCode(strSRCode);
								objSalesReturnService.funAddTaxDtl(ojInvoiceTaxDtlModel);
							}
				 		
						}
						req.getSession().setAttribute("success", true);
						req.getSession().setAttribute("successMessage","SalesReturn Code : ".concat(objHdModel.getStrSRCode()));
						//req.getSession().setAttribute("rptInvCode",objHdModel.getStrSRCode());
					}
				}
				
				return new ModelAndView("redirect:/frmSalesReturn.html?saddr="+urlHits);
			}
			else{
				return new ModelAndView("frmSalesReturn?saddr="+urlHits,"command",new clsSalesReturnBean());
			}
		}
		
		
		

		//Convert bean to model function
			@SuppressWarnings("unused")
			private clsSalesReturnHdModel funPrepareHdModel(clsSalesReturnBean objBean,String userCode,String clientCode,HttpServletRequest req){
				
				long lastNo=0;
				clsSalesReturnHdModel objModel;
				
				
				
				String propCode=req.getSession().getAttribute("propertyCode").toString();
				String startDate=req.getSession().getAttribute("startDate").toString();
				clsSalesReturnHdModel srHdModel;
				objGlobal=new clsGlobalFunctions();
				
				String[] date=objBean.getDteSRDate().split("-");
			    String date1 = date[2]+"-"+date[1]+"-"+date[0];
				if(objBean.getStrSRCode().trim().length()==0)
				{
					
					String strSRCode=objGlobalfunction.funGenerateDocumentCode("frmSalesReturn", date1, req);
					
					
					srHdModel=new clsSalesReturnHdModel(new clsSalesReturnHdModel_ID(strSRCode, clientCode));
					srHdModel.setStrSRCode(strSRCode);
					srHdModel.setStrUserCreated(userCode);
					srHdModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		//			srHdModel.setIntid(lastNo);
		 
				}
				
				else
				{	
					clsSalesReturnHdModel objSRModel=objSalesReturnService.funGetSalesReturnHd(objBean.getStrSRCode(),clientCode);
					if(null == objSRModel){
						String strSRCode=objGlobalfunction.funGenerateDocumentCode("frmSalesReturn", date1, req);
						
						srHdModel=new clsSalesReturnHdModel(new clsSalesReturnHdModel_ID(strSRCode, clientCode));
						//srHDModel.setIntid(lastNo);
						srHdModel.setStrUserCreated(userCode);
						
						srHdModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
					}else{
						srHdModel=new clsSalesReturnHdModel(new clsSalesReturnHdModel_ID(objBean.getStrSRCode(),clientCode));
						//objModel.setStrPropertyCode(propCode);
					}
				}
					
				    srHdModel.setStrUserEdited(userCode);
				    srHdModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				    srHdModel.setDteSRDate(objBean.getDteSRDate());
				    srHdModel.setStrAgainst(objBean.getStrAgainst());
				   
				    srHdModel.setStrCustCode(objBean.getStrCustCode());
				    
				   
				    srHdModel.setStrLocCode(objBean.getStrLocCode());
				   
				   
					if(objBean.getStrDCCode()==null)
					{
						srHdModel.setStrDCCode("");
					}else
					{
						srHdModel.setStrDCCode(objBean.getStrDCCode());
					}
					
					//srHdModel.setDblSubTotalAmt(objBean.get());
//	                double taxamt=0.0;
//	                
//					if(objBean.getDblTaxAmt()!=null)
//					{
//						taxamt=objBean.getDblTaxAmt();
//					}
//					Double amt=objBean.getDblSubTotalAmt()+taxamt;
					srHdModel.setDblTotalAmt(objBean.getDblTotalAmt());
				//	srHdModel.setDblTaxAmt(taxamt);
					
				return srHdModel;

			}
	

			//Assign filed function to set data onto form for edit transaction.
			@RequestMapping(value = "/loadSalesHdData", method = RequestMethod.GET)
			public @ResponseBody clsSalesReturnBean funAssignFields(@RequestParam("srCode") String srCode,HttpServletRequest req)
			{
				String clientCode=req.getSession().getAttribute("clientCode").toString();
				clsSalesReturnBean objBeanSalesRet = new clsSalesReturnBean();
				
				List <Object> objDC=objSalesReturnService.funGetSalesReturn(srCode,clientCode);
				clsSalesReturnHdModel objsrHdModel = null;
				clsLocationMasterModel objLocationMasterModel = null;
				clsPartyMasterModel objPartyMasterModel = null;
				
				for(int i=0;i<objDC.size();i++)
				{
					Object[] ob = (Object[])objDC.get(i);
					objsrHdModel = (clsSalesReturnHdModel) ob[0];
					objLocationMasterModel = (clsLocationMasterModel) ob[1];
					objPartyMasterModel = (clsPartyMasterModel) ob[2];
				}
				
				objBeanSalesRet=funPrepardHdBean(objsrHdModel,objLocationMasterModel,objPartyMasterModel);
				objBeanSalesRet.setStrCustName(objPartyMasterModel.getStrPName());
				objBeanSalesRet.setStrLocName(objLocationMasterModel.getStrLocName());
				List<clsSalesReturnDtlModel> listSalesDtl = new ArrayList<clsSalesReturnDtlModel>();
				List<Object> objInvDtlModelList=funGetSalesReturnDtl(srCode,clientCode);	
				for(int i=0;i<objInvDtlModelList.size();i++)
				{
					Object[] obj = (Object[])objInvDtlModelList.get(i);
					clsSalesReturnDtlModel salesRetDtl=new clsSalesReturnDtlModel();
					salesRetDtl.setStrSRCode(obj[0].toString());
					salesRetDtl.setStrProdCode(obj[1].toString());
					salesRetDtl.setDblQty(Double.valueOf(obj[2].toString()));
					salesRetDtl.setDblPrice(Double.valueOf(obj[3].toString()));
					salesRetDtl.setDblWeight(Double.valueOf(obj[4].toString()));
					salesRetDtl.setStrRemarks(obj[5].toString());
					salesRetDtl.setStrClientCode(obj[6].toString());
					salesRetDtl.setStrProdName(obj[7].toString());
					//clsSalesReturnDtlModel invDtl = (clsSalesReturnDtlModel) obj[0];
					//clsProductMasterModel prodmast =(clsProductMasterModel) obj[1];
					//clsInvoiceModelDtl invModDtl =(clsInvoiceModelDtl) obj[4];
					//prodmast.setDblUnitPrice(invModDtl.getDblPrice());
					
					//invDtl.setStrProdName(prodmast.getStrProdName());
					listSalesDtl.add(salesRetDtl);
				}
				
				objBeanSalesRet.setListSalesReturn(listSalesDtl);
				
//				String sql="select strTaxCode,strTaxDesc,strTaxableAmt,strTaxAmt from clsInvoiceTaxDtlModel "
//						+ "where strInvCode='"+srCode+"' and strClientCode='"+clientCode+"'";
				/*String sql=" ";
				
				List list=objGlobalFunctionsService.funGetList(sql,"hql");
				List<clsSalesRetrunTaxModel> listTaxDtl=new ArrayList<clsSalesRetrunTaxModel>();
				if(null!=list)
				{	
				for(int cnt=0;cnt<list.size();cnt++)
				{
					clsSalesRetrunTaxModel objTaxDtl=new clsSalesRetrunTaxModel();
					Object[] arrObj=(Object[])list.get(cnt);
					objTaxDtl.setStrTaxCode(arrObj[0].toString());
					objTaxDtl.setStrTaxDesc(arrObj[1].toString());
					objTaxDtl.setStrTaxableAmt(Double.parseDouble(arrObj[2].toString()));
					objTaxDtl.setStrTaxAmt(Double.parseDouble(arrObj[3].toString()));
					objTaxDtl.setStrSRCode(srCode);
					objTaxDtl.setStrClientCode(clientCode);
					listTaxDtl.add(objTaxDtl);
				}
				objBeanInv.setListSalesRetrunTaxModel(listTaxDtl);
				}*/
				return objBeanSalesRet;
			}
			
			
			private clsSalesReturnBean funPrepardHdBean(clsSalesReturnHdModel objsalRetHdModel,clsLocationMasterModel objLocationMasterModel,clsPartyMasterModel objPartyMasterModel)
			{
				
				clsSalesReturnBean objBean = new clsSalesReturnBean();
				
				
				
				
				objBean.setDteSRDate(objsalRetHdModel.getDteSRDate());
				objBean.setStrAgainst(objsalRetHdModel.getStrAgainst());
				objBean.setStrCustCode(objsalRetHdModel.getStrCustCode());
				objBean.setStrSRCode(objsalRetHdModel.getStrSRCode());
				objBean.setStrDCCode(objsalRetHdModel.getStrDCCode());
				
				
				objBean.setStrLocCode(objsalRetHdModel.getStrLocCode());
				
				
				//objBean.setDblTaxAmt(objInvHdModel.getDblTaxAmt());
				objBean.setDblTotalAmt(objsalRetHdModel.getDblTotalAmt());
				
				
				return objBean;
			}
			
			
			
			
			@SuppressWarnings("rawtypes")
			@RequestMapping(value = "/loadSalesReturnRetailInvoiceHdData", method = RequestMethod.GET)
			public @ResponseBody clsSalesReturnBean funAssignRetailInvoiceFields(@RequestParam("invCode") String invCode,HttpServletRequest req)
			{
				String clientCode=req.getSession().getAttribute("clientCode").toString();
				clsSalesReturnBean objSaleBeanInv = new clsSalesReturnBean();
				clsInvoiceBean objBeanInv = new clsInvoiceBean();
				
				List <Object> objDC=objInvoiceHdService.funGetInvoice(invCode,clientCode);
				clsInvoiceHdModel objInvoiceHdModel = null;
				clsLocationMasterModel objLocationMasterModel = null;
				clsPartyMasterModel objPartyMasterModel = null;
				
				for(int i=0;i<objDC.size();i++)
				{
					Object[] ob = (Object[])objDC.get(i);
					objInvoiceHdModel = (clsInvoiceHdModel) ob[0];
					objLocationMasterModel = (clsLocationMasterModel) ob[1];
					objPartyMasterModel = (clsPartyMasterModel) ob[2];
					objSaleBeanInv.setStrCustCode(objPartyMasterModel.getStrPCode());
					objSaleBeanInv.setStrCustName(objPartyMasterModel.getStrPName());
					objSaleBeanInv.setStrLocCode(objLocationMasterModel.getStrLocCode());
					objSaleBeanInv.setStrLocName(objLocationMasterModel.getStrLocName());
					objSaleBeanInv.setDteSRDate(objInvoiceHdModel.getDteInvDate().split(" ")[0]);
				}
				
				
				
				clsInvoiceHdModel objInvHDModelList=objInvoiceHdService.funGetInvoiceDtl(invCode,clientCode);
			    List<clsInvoiceModelDtl> listInvDtlModel=objInvHDModelList.getListInvDtlModel();
				List<clsInvoiceDtlBean> listInvDtlBean=new  ArrayList();
				
				List<clsSalesReturnDtlModel> listRBDtl = new ArrayList<clsSalesReturnDtlModel>();
				for(int i=0;i<listInvDtlModel.size();i++)
			    {
					clsInvoiceDtlBean objBeanInvoice=new clsInvoiceDtlBean();
					
					clsInvoiceModelDtl obj=listInvDtlModel.get(i);
					clsProductMasterModel objProdModle=objProductMasterService.funGetObject(obj.getStrProdCode(),clientCode);
					clsSalesReturnDtlModel invDtl=new clsSalesReturnDtlModel();
					invDtl.setStrSRCode(invCode);
					invDtl.setStrProdCode(obj.getStrProdCode());
					invDtl.setDblQty(obj.getDblQty());
					invDtl.setDblPrice(obj.getDblUnitPrice());
					invDtl.setDblWeight(0.0);
					invDtl.setStrRemarks(obj.getStrRemarks());
					invDtl.setStrClientCode(clientCode);
					invDtl.setStrProdName(objProdModle.getStrProdName());
					listRBDtl.add(invDtl);
					
			    }	
				objSaleBeanInv.setListSalesReturn(listRBDtl);
				return objSaleBeanInv;
			}
			
			
		
			
			public List <Object> funGetSalesReturnDtl(String srCode,String clientCode)
			{
				List<Object> objInvDtlList=null;
				
				String sql="  select a.strSRCode,a.strProdCode,a.dblQty,a.dblPrice,a.dblWeight,a.strRemarks,a.strClientCode,b.strProdName "
				+ " from tblsalesreturndtl a,tblproductmaster b ,tblsalesreturnhd c  "
				+ " where a.strSRCode='"+srCode+"' and a.strProdCode= b.strProdCode and a.strSRCode=c.strSRCode "
				+ " and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' ";

				List list=objGlobalFunctionsService.funGetList(sql,"sql");
				
				if(list.size()>0)
				{
					objInvDtlList =   list;
					
				}
				return objInvDtlList;
			}
			

			

}
