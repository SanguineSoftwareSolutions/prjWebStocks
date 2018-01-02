package com.sanguine.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.bean.clsLinkUpBean;
import com.sanguine.excise.bean.clsBrandMasterBean;
import com.sanguine.excise.bean.clsExciseSupplierMasterBean;
import com.sanguine.model.clsLinkUpHdModel;
import com.sanguine.model.clsGroupMasterModel;
import com.sanguine.service.clsLinkUpService;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webbooks.bean.clsSundryDebtorMasterBean;
import com.sanguine.webpos.controller.clsPOSGlobalFunctionsController;

@Controller
public class clsARLinkUpController {
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	
	private clsGlobalFunctions objGlobal=null;
	
	@Autowired
	private clsLinkUpService objARLinkUpService;
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/frmARLinkUp", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command")clsLinkUpBean objBean,Map<String,Object> model,HttpServletRequest request){
		
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		String propertyCode = request.getSession().getAttribute("propertyCode").toString();
		String urlHits="1";
		try{ 
			urlHits=request.getParameter("saddr").toString();
			String sql= "";

				 sql= " select a.strSGCode,a.strSGName,ifnull(b.strGDes,''),ifnull(b.strAccountCode,'') "
				 		+ " from tblsubgroupmaster a "
				 		+ " left outer join tbllinkup b on a.strSGCode=b.strSGCode and b.strPropertyCode='"+propertyCode+"'  "
				 		+ " where a.strClientCode='"+clientCode+"'  "
				 		+ " order by a.strSGName "; 

			ArrayList list=(ArrayList) objGlobalFunctionsService.funGetDataList(sql,"sql");
			List listARLinkUp=new ArrayList<clsLinkUpHdModel> ();
			for(int cnt=0;cnt<list.size();cnt++)
			{
				clsLinkUpHdModel objModel=new clsLinkUpHdModel();
				Object[] arrObj=(Object[])list.get(cnt);
				objModel.setStrSGCode(arrObj[0].toString());
				objModel.setStrSGName(arrObj[1].toString());
				objModel.setStrGDes(arrObj[2].toString());
				objModel.setStrAccountCode(arrObj[3].toString());
				listARLinkUp.add(objModel);
			}
			
			objBean.setListSubGroupLinkUp(listARLinkUp);
			model.put("ARLinkUpList", objBean);
			
			
			
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmARLinkUp_1","command", objBean);
			
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmARLinkUp","command", objBean);
		}else {
			return null;
		}
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadARLinkUpData", method = RequestMethod.POST)
		public @ResponseBody List funLoadLinkupData(@RequestParam("strDoc") String strDoc,HttpServletRequest request,HttpServletResponse response){	
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		String propertyCode = request.getSession().getAttribute("propertyCode").toString();
		String urlHits="1";
		
			//urlHits=request.getParameter("saddr").toString();
			String sql= "";
			if(strDoc.equals("SubGroup"))
			{
				 sql= " select a.strSGCode,a.strSGName,ifnull(b.strAccountCode,''),ifnull(b.strGDes,'') "
					 		+ " from tblsubgroupmaster a "
					 		+ " left outer join tbllinkup b on a.strSGCode=b.strSGCode and b.strPropertyCode='"+propertyCode+"' "
					 		+ " where a.strClientCode='"+clientCode+"'   "
					 		+ " order by a.strSGName "; 
			}
			
			if(strDoc.equals("Tax"))
			{
				 sql= " select a.strTaxCode,a.strTaxDesc,ifnull(b.strAccountCode,''),ifnull(b.strGDes,'') "
				 		+ " from tbltaxhd a left outer join tbllinkup b on a.strTaxCode=b.strSGCode and b.strPropertyCode='"+propertyCode+"' "
				 		+ " where  a.strClientCode='"+clientCode+"' and a.strPropCode='"+propertyCode+"' order by a.strTaxCode  "; 
			}
			
			if(strDoc.equals("Supplier"))
			{
				 sql= " select a.strPCode,a.strPName,ifnull(b.strGDes,''),ifnull(b.strAccountCode,''), ifnull(b.strExSuppCode,''), "
				 		+ " ifnull(b.strExSuppName,'')"
						+ " from tblpartymaster a left outer join tbllinkup b on a.strPCode=b.strSGCode and b.strPropertyCode='"+propertyCode+"' "
				 		+ " where  a.strPType='supp' and a.strClientCode='"+clientCode+"' and a.strPropCode='"+propertyCode+"' order by a.strPCode "; 
			}
			
			if(strDoc.equals("Product"))
			{
				 sql= " select a.strProdCode,a.strProdName,a.strSGCode,ifnull(b.strExSuppCode,''),  "
				 		+ " ifnull(b.strExSuppName,'')  "
				 		+ " from tblproductmaster a  left outer join tbllinkup b on a.strProdCode=b.strSGCode and b.strPropertyCode='"+propertyCode+"' "
				 		+ " where  a.strClientCode='"+clientCode+"' order by a.strProdCode  ";  
			}
			if(strDoc.equals("Customer"))
			{
				 sql= " select a.strPCode,a.strPName,ifnull(b.strGDes,''),ifnull(b.strAccountCode,''), ifnull(b.strExSuppCode,''), "
				 		+ " ifnull(b.strExSuppName,'')"
						+ " from tblpartymaster a left outer join tbllinkup b on a.strPCode=b.strSGCode and b.strPropertyCode='"+propertyCode+"' "
				 		+ " where  a.strPType='cust' and a.strClientCode='"+clientCode+"' and a.strPropCode='"+propertyCode+"' order by a.strPCode "; 
			}
			
			ArrayList list=(ArrayList) objGlobalFunctionsService.funGetDataList(sql,"sql");
			List listARLinkUp=new ArrayList<clsLinkUpHdModel> ();
			
			if(strDoc.equals("SubGroup"))
			{
				for(int cnt=0;cnt<list.size();cnt++)
				{
					clsLinkUpHdModel objModel=new clsLinkUpHdModel();
					Object[] arrObj=(Object[])list.get(cnt);
					objModel.setStrSGCode(arrObj[0].toString());
					objModel.setStrSGName(arrObj[1].toString());
					objModel.setStrAccountCode(arrObj[2].toString());
					objModel.setStrGDes(arrObj[3].toString());
					listARLinkUp.add(objModel);
				}	
			}
			
			if(strDoc.equals("Tax"))
			{
				for(int cnt=0;cnt<list.size();cnt++)
				{
					clsLinkUpHdModel objModel=new clsLinkUpHdModel();
					Object[] arrObj=(Object[])list.get(cnt);
					objModel.setStrSGCode(arrObj[0].toString());
					objModel.setStrSGName(arrObj[1].toString());
					objModel.setStrAccountCode(arrObj[2].toString());
					objModel.setStrGDes(arrObj[3].toString());
					listARLinkUp.add(objModel);
				}
			}
			
			
			if(strDoc.equals("Supplier"))
			{
				for(int cnt=0;cnt<list.size();cnt++)
				{
					clsLinkUpHdModel objModel=new clsLinkUpHdModel();
					Object[] arrObj=(Object[])list.get(cnt);
					objModel.setStrSGCode(arrObj[0].toString());
					objModel.setStrSGName(arrObj[1].toString());
					objModel.setStrGDes(arrObj[2].toString());
					objModel.setStrAccountCode(arrObj[3].toString());
					objModel.setStrExSuppCode(arrObj[4].toString());
					objModel.setStrExSuppName(arrObj[5].toString());
					listARLinkUp.add(objModel);
				}
			}
			
			if(strDoc.equals("Product"))
			{
					for(int cnt=0;cnt<list.size();cnt++)
					{
						clsLinkUpHdModel objModel=new clsLinkUpHdModel();
						Object[] arrObj=(Object[])list.get(cnt);
						objModel.setStrSGCode(arrObj[0].toString());
						objModel.setStrSGName(arrObj[1].toString());
						objModel.setStrGDes(arrObj[2].toString());
						objModel.setStrExSuppCode(arrObj[3].toString());
						objModel.setStrExSuppName(arrObj[4].toString());
						
						
						listARLinkUp.add(objModel);
					}
			}
			
			if(strDoc.equals("Customer"))
			{
				for(int cnt=0;cnt<list.size();cnt++)
				{
					clsLinkUpHdModel objModel=new clsLinkUpHdModel();
					Object[] arrObj=(Object[])list.get(cnt);
					objModel.setStrSGCode(arrObj[0].toString());
					objModel.setStrSGName(arrObj[1].toString());
					objModel.setStrGDes(arrObj[2].toString());
					objModel.setStrAccountCode(arrObj[3].toString());
					objModel.setStrExSuppCode(arrObj[4].toString());
					objModel.setStrExSuppName(arrObj[5].toString());
					listARLinkUp.add(objModel);
				}
			}
			return listARLinkUp;
		
	}
	
	
	//Save or Update LinkUp
	@RequestMapping(value = "/saveARLinkUp", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsLinkUpBean objBean ,BindingResult result,HttpServletRequest req){
		if(!result.hasErrors()){
		
			objGlobal=new clsGlobalFunctions();
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String userCode=req.getSession().getAttribute("usercode").toString();
			String propertyCode = req.getSession().getAttribute("propertyCode").toString();
			
				
				switch(objBean.strLinkup)
				{
				
					case "SubGroup":
						
						List<clsLinkUpHdModel> listSubLinkUp=objBean.getListSubGroupLinkUp();
						for(int cnt=0;cnt<listSubLinkUp.size();cnt++)
						{
							clsLinkUpHdModel objModel=listSubLinkUp.get(cnt);
							
							if(objModel.getStrAccountCode().length()>0)
							{
								String delete=" delete from tbllinkup where strSGCode='"+objModel.getStrSGCode()+"' and strClientCode='"+clientCode+"' ";
								objARLinkUpService.funExecute(delete);
//								objModel.setStrGDes("");
								objModel.setStrExSuppCode("");
								objModel.setStrExSuppName("");
								objModel.setStrClientCode(clientCode);
								objModel.setDteCreatedDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));;
								objModel.setDteLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
								objModel.setStrUserCreated(userCode);
								objModel.setStrUserEdited(userCode);
								objModel.setStrPropertyCode(propertyCode);
								objARLinkUpService.funAddUpdateARLinkUp(objModel);
							}
						}	
							
						break;
							
					case "Tax" :
						
						List<clsLinkUpHdModel> listTaxLinkUp=objBean.getListTaxLinkUp();
						for(int cnt=0;cnt<listTaxLinkUp.size();cnt++)
						{
							clsLinkUpHdModel objModel=listTaxLinkUp.get(cnt);
							
							if(objModel.getStrAccountCode().length()>0)
							{
								String delete=" delete from tbllinkup where strSGCode='"+objModel.getStrSGCode()+"' and strClientCode='"+clientCode+"' ";
								objARLinkUpService.funExecute(delete);
//								objModel.setStrGDes("");
								objModel.setStrExSuppCode("");
								objModel.setStrExSuppName("");
								objModel.setStrClientCode(clientCode);
								objModel.setDteCreatedDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));;
								objModel.setDteLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
								objModel.setStrUserCreated(userCode);
								objModel.setStrUserEdited(userCode);
								objModel.setStrPropertyCode(propertyCode);
								
								objARLinkUpService.funAddUpdateARLinkUp(objModel);
							}
						}	
						break;
						
					case "Supplier" : 
						
						List<clsLinkUpHdModel> listSupplierLinkUp=objBean.getListSupplierLinkUp();
						for(int cnt=0;cnt<listSupplierLinkUp.size();cnt++)
						{
							clsLinkUpHdModel objModel=listSupplierLinkUp.get(cnt);
							if(objModel.getStrAccountCode().length()>0)
							{
								String delete=" delete from tbllinkup where strSGCode='"+objModel.getStrSGCode()+"' and strClientCode='"+clientCode+"' ";
								objARLinkUpService.funExecute(delete);
//								objModel.setStrGDes("");
								objModel.setStrClientCode(clientCode);
								objModel.setDteCreatedDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));;
								objModel.setDteLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
								objModel.setStrUserCreated(userCode);
								objModel.setStrUserEdited(userCode);
								objModel.setStrPropertyCode(propertyCode);
								
								objARLinkUpService.funAddUpdateARLinkUp(objModel);
							}
						}
						
						break;
						
					case "Product" : 
						List<clsLinkUpHdModel> listProductLinkUp=objBean.getListProductLinkUp();
						for(int cnt=0;cnt<listProductLinkUp.size();cnt++)
						{
							clsLinkUpHdModel objModel=listProductLinkUp.get(cnt);
							
							if(objModel.getStrExSuppCode().length()>0)
							{
								String delete=" delete from tbllinkup where strSGCode='"+objModel.getStrSGCode()+"' and strClientCode='"+clientCode+"' ";
								objARLinkUpService.funExecute(delete);
								objModel.setStrGDes("");
								objModel.setStrAccountCode("");
								objModel.setStrClientCode(clientCode);
								objModel.setDteCreatedDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));;
								objModel.setDteLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
								objModel.setStrUserCreated(userCode);
								objModel.setStrUserEdited(userCode);	
								objModel.setStrPropertyCode(propertyCode);
							
								objARLinkUpService.funAddUpdateARLinkUp(objModel);
							}
						}
	
						break;	
						
					case "Customer" : 
						
						List<clsLinkUpHdModel> listCustomerLinkUp=objBean.getListCustomerLinkUp();
						for(int cnt=0;cnt<listCustomerLinkUp.size();cnt++)
						{
							clsLinkUpHdModel objModel=listCustomerLinkUp.get(cnt);
							if(objModel.getStrAccountCode().length()>0)
							{
								String delete=" delete from tbllinkup where strSGCode='"+objModel.getStrSGCode()+"' and strClientCode='"+clientCode+"' ";
								objARLinkUpService.funExecute(delete);
								objModel.setStrExSuppCode("");
								objModel.setStrExSuppName("");
								objModel.setStrClientCode(clientCode);
								objModel.setDteCreatedDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));;
								objModel.setDteLastModified(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
								objModel.setStrUserCreated(userCode);
								objModel.setStrUserEdited(userCode);
								objModel.setStrPropertyCode(propertyCode);
								
								objARLinkUpService.funAddUpdateARLinkUp(objModel);
							}
						}
						
						break;	
						
				}
				return new ModelAndView("redirect:/frmARLinkUp.html");
			}else{
				
		return new ModelAndView("frmARLinkUp");
	}
}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadBrandDataFormWebService", method = RequestMethod.GET)
		public @ResponseBody clsBrandMasterBean funLoadBrandDataFormWebService(@RequestParam("strBrandCode") String strBrandCode,HttpServletRequest request,HttpServletResponse response){	
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		
		String exciseUrl = clsPOSGlobalFunctionsController.POSWSURL+"/ExciseIntegration/funGetExciseBrandMasterData?strBrandCode="+strBrandCode+"&clientCode="+clientCode;
		System.out.println(exciseUrl);
		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(exciseUrl);
		clsBrandMasterBean objBrand = new clsBrandMasterBean();
		
		objBrand.setStrBrandCode(jObj.get("strBrandCode").toString());
		objBrand.setStrBrandName(jObj.get("strBrandName").toString());
		
		if(null==objBrand)
		{
			objBrand=new clsBrandMasterBean();
			objBrand.setStrBrandCode("Invalid Code");
		}
		
		return objBrand;
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadLinkupDataFormWebService", method = RequestMethod.GET)
		public @ResponseBody clsSundryDebtorMasterBean funLoadSundryDataFormWebService(@RequestParam("strAccountCode") String strAccountCode,HttpServletRequest request,HttpServletResponse response){	
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		
		String webbookUrl = clsPOSGlobalFunctionsController.POSWSURL+"/WebBooksIntegration/funGetWebBooksData?strAccountCode="+strAccountCode+"&clientCode="+clientCode;
		System.out.println(webbookUrl);
		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(webbookUrl);
		clsSundryDebtorMasterBean objDebtor = new clsSundryDebtorMasterBean();
		
		objDebtor.setStrDebtorCode(jObj.get("strAccountCode").toString());
		objDebtor.setStrFirstName(jObj.get("strAccountName").toString());
		
		if(null==objDebtor)
		{
			objDebtor=new clsSundryDebtorMasterBean();
			objDebtor.setStrDebtorCode("Invalid Code");
		}
		
		return objDebtor;
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadSundryCreditorOrDebtorLinkupDataFormWebService", method = RequestMethod.GET)
		public @ResponseBody clsSundryDebtorMasterBean funLoadSundryCreditorOrDebtorLinkupDataFormWebService(@RequestParam("strDocCode") String strDocCode,HttpServletRequest request,HttpServletResponse response){	
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		
		String webbookUrl = clsPOSGlobalFunctionsController.POSWSURL+"/WebBooksIntegration/funGetWebBooksSundryCreditorData?strDocCode="+strDocCode+"&clientCode="+clientCode;
		System.out.println(webbookUrl);
		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(webbookUrl);
		clsSundryDebtorMasterBean objDebtor = new clsSundryDebtorMasterBean();
		
		objDebtor.setStrDebtorCode(jObj.get("strCreditorCode").toString());
		objDebtor.setStrFirstName(jObj.get("strFirstName").toString());
		
		if(null==objDebtor)
		{
			objDebtor=new clsSundryDebtorMasterBean();
			objDebtor.setStrDebtorCode("Invalid Code");
		}
		
		return objDebtor;
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadExciseSuppMaster", method = RequestMethod.GET)
		public @ResponseBody clsExciseSupplierMasterBean funLoadExciseSupplierMasterData(@RequestParam("strSupplierCode") String strSupplierCode,HttpServletRequest request,HttpServletResponse response){	
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		
		String exciseUrl = clsPOSGlobalFunctionsController.POSWSURL+"/ExciseIntegration/funGetExciseSupplierMasterData?strSupplierCode="+strSupplierCode+"&clientCode="+clientCode;
		System.out.println(exciseUrl);
		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(exciseUrl);
		clsExciseSupplierMasterBean objBrand = new clsExciseSupplierMasterBean();
		
		objBrand.setStrSupplierCode(jObj.get("strSupplierCode").toString());
		objBrand.setStrSupplierName(jObj.get("strSupplierName").toString());
		
		if(null==objBrand)
		{
			objBrand=new clsExciseSupplierMasterBean();
			objBrand.setStrSupplierCode("Invalid Code");
		}
		
		return objBrand;
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/loadSundryDebtorLinkupDataFormWebService", method = RequestMethod.GET)
		public @ResponseBody clsSundryDebtorMasterBean funLoadSundryDebtorLinkupDataFormWebService(@RequestParam("strDocCode") String strDocCode,HttpServletRequest request,HttpServletResponse response){	
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		
		String webbookUrl = clsPOSGlobalFunctionsController.POSWSURL+"/WebBooksIntegration/funGetWebBooksSundryDebtorData?strDocCode="+strDocCode+"&clientCode="+clientCode;
		System.out.println(webbookUrl);
		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(webbookUrl);
		clsSundryDebtorMasterBean objDebtor = new clsSundryDebtorMasterBean();
		
		objDebtor.setStrDebtorCode(jObj.get("strDetorCode").toString());
		objDebtor.setStrFirstName(jObj.get("strFirstName").toString());
		
		if(null==objDebtor)
		{
			objDebtor=new clsSundryDebtorMasterBean();
			objDebtor.setStrDebtorCode("Invalid Code");
		}
		
		return objDebtor;
		
	}
	
	
	
	
	
	

}
