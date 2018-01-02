package com.sanguine.webpms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webpms.bean.clsCheckInBean;
import com.sanguine.webpms.bean.clsCheckInDetailsBean;
import com.sanguine.webpms.bean.clsWalkinBean;
import com.sanguine.webpms.bean.clsWalkinDtlBean;
import com.sanguine.webpms.dao.clsExtraBedMasterDao;
import com.sanguine.webpms.dao.clsWalkinDao;
import com.sanguine.webpms.model.clsExtraBedMasterModel;
import com.sanguine.webpms.model.clsRoomMasterModel;
import com.sanguine.webpms.model.clsWalkinDtl;
import com.sanguine.webpms.model.clsWalkinHdModel;
import com.sanguine.webpms.service.clsRoomMasterService;
import com.sanguine.webpms.service.clsWalkinService;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

@Controller
public class clsWalkinController{
	
	@Autowired
	private clsWalkinService objWalkinService;
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsWalkinDao objWalkinDao;
		
	@Autowired
	private clsExtraBedMasterDao objExtraBedMasterDao;
	
	@Autowired
    private clsRoomMasterService objRoomMasterService;
		
	
//Open Walkin
	@RequestMapping(value = "/frmWalkin", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request){
		String urlHits="1";
		
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		List<String> listPrefix = new ArrayList<>();
		listPrefix.add("Mr.");
		listPrefix.add("Mrs.");
		listPrefix.add("Miss");
        model.put("prefix", listPrefix);
        
        List<String> listGender = new ArrayList<>();
        listGender.add("M");
        listGender.add("F");
        model.put("gender", listGender);
        
        String sql="select strCityName from dbwebmms.tblcitymaster where strClientCode='"+clientCode+"'";
        List list=objGlobalFunctionsService.funGetList(sql, "sql");
        List<String> listCity = new ArrayList<>();
        for(int cnt=0;cnt<list.size();cnt++)
        {
        	listCity.add(list.get(cnt).toString());
        }
        model.put("listCity",listCity);
        
        sql="select strStateName from dbwebmms.tblstatemaster where strClientCode='"+clientCode+"'";
        List listStateDetails=objGlobalFunctionsService.funGetList(sql, "sql");
        List<String> listState = new ArrayList<>();
        for(int cnt=0;cnt<listStateDetails.size();cnt++)
        {
        	listState.add(listStateDetails.get(cnt).toString());
        }
        model.put("listState",listState);
        
        sql="select strCountryName from dbwebmms.tblcountrymaster where strClientCode='"+clientCode+"'";
        List listCountryDetails=objGlobalFunctionsService.funGetList(sql, "sql");
        List<String> listCountry = new ArrayList<>();
        for(int cnt=0;cnt<listCountryDetails.size();cnt++)
        {
        	listCountry.add(listCountryDetails.get(cnt).toString());
        }
        model.put("listCountry",listCountry);
        
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
	
		if("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmWalkin_1","command", new clsWalkinBean());
		}
		else if("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmWalkin","command", new clsWalkinBean());
		}
		else 
		{
			return null;
		}
	}

	
	//Load Header Table Data On Form
		@RequestMapping(value = "/loadWalkinData", method = RequestMethod.GET)
		public @ResponseBody clsWalkinBean funLoadWalkinHdData(HttpServletRequest request){
			clsWalkinHdModel objWalkinHdModel=null;
			String sql="";
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			//String userCode=request.getSession().getAttribute("usercode").toString();
			String docCode=request.getParameter("docCode").toString();
			
			List listWalkinData=objWalkinDao.funGetWalkinDataDtl(docCode, clientCode);
			objWalkinHdModel=(clsWalkinHdModel) listWalkinData.get(0);
			clsWalkinBean objWalkinBean=null;
			if(null==objWalkinHdModel)
			{
				objWalkinBean = new clsWalkinBean();
				objWalkinBean.setStrWalkinNo("Invalid");
			}
			else
			{
				Map<String,clsWalkinDtlBean> hmWalkinDtlBean=new HashMap<String,clsWalkinDtlBean>();
				
				objWalkinBean = new clsWalkinBean();
				objWalkinBean.setStrWalkinNo(objWalkinHdModel.getStrWalkinNo());
				objWalkinBean.setDteWalkinDate(objGlobal.funGetDate("yyyy/MM/dd",objWalkinHdModel.getDteWalkinDate()));
				objWalkinBean.setDteCheckOutDate(objGlobal.funGetDate("yyyy/MM/dd",objWalkinHdModel.getDteCheckOutDate()));
			    objWalkinBean.setTmeWalkinTime(objWalkinHdModel.getTmeWalkinTime());
				objWalkinBean.setTmeCheckOutTime(objWalkinHdModel.getTmeCheckOutTime());
				objWalkinBean.setStrCorporateCode(objWalkinHdModel.getStrCorporateCode());
				objWalkinBean.setStrBookerCode(objWalkinHdModel.getStrBookerCode());
				objWalkinBean.setStrAgentCode(objWalkinHdModel.getStrAgentCode());
				objWalkinBean.setIntNoOfNights(objWalkinHdModel.getIntNoOfNights());
				objWalkinBean.setStrRemarks(objWalkinHdModel.getStrRemarks());
				objWalkinBean.setStrUserCreated(objWalkinHdModel.getStrUserCreated());
				objWalkinBean.setStrUserEdited(objWalkinHdModel.getStrUserEdited());
				objWalkinBean.setDteDateCreated(objWalkinHdModel.getDteDateCreated());
				objWalkinBean.setDteDateEdited(objWalkinHdModel.getDteDateEdited());
								
				clsRoomMasterModel objRoomMasterModel=objRoomMasterService.funGetRoomMaster(objWalkinHdModel.getStrRoomNo(), clientCode);
				objWalkinBean.setStrRoomNo(objWalkinHdModel.getStrRoomNo());
				objWalkinBean.setStrRoomDesc(objRoomMasterModel.getStrRoomDesc());
							
				if(!objWalkinHdModel.getStrExtraBedCode().isEmpty())
				{
					List listExtraBedData=objExtraBedMasterDao.funGetExtraBedMaster(objWalkinHdModel.getStrExtraBedCode(), clientCode);
					clsExtraBedMasterModel objExtraBedMasterModel= (clsExtraBedMasterModel) listExtraBedData.get(0);
					objWalkinBean.setStrExtraBedCode(objWalkinHdModel.getStrExtraBedCode());
					objWalkinBean.setStrExtraBedDesc(objExtraBedMasterModel.getStrExtraBedTypeDesc());
				}
				else
				{
					objWalkinBean.setStrExtraBedCode("");
					objWalkinBean.setStrExtraBedDesc("");
				}
				objWalkinBean.setIntNoOfAdults(objWalkinHdModel.getIntNoOfAdults());
				objWalkinBean.setIntNoOfChild(objWalkinHdModel.getIntNoOfChild());
				
				
				List<clsWalkinDtlBean> listWalkinDtlBean=new ArrayList<clsWalkinDtlBean>();
				for(clsWalkinDtl objWalkinDtlModel:objWalkinHdModel.getListWalkinDtlModel())
				{
					clsWalkinDtlBean objWalkinDtlBean=new clsWalkinDtlBean();
					
					sql="select strFirstName,strMiddleName,strLastName,lngMobileNo from tblguestmaster "
						+ " where strGuestCode='"+objWalkinDtlModel.getStrGuestCode()+"' and strClientCode='"+clientCode+"' ";
					List listGuestMaster=objGlobalFunctionsService.funGetListModuleWise(sql, "sql");
					for(int cnt=0;cnt<listGuestMaster.size();cnt++)
					{
						Object[] arrObjGuest=(Object[])listGuestMaster.get(cnt);
						objWalkinDtlBean.setStrGuestFirstName(String.valueOf(arrObjGuest[0]));
						objWalkinDtlBean.setStrGuestMiddleName(String.valueOf(arrObjGuest[1]));
						objWalkinDtlBean.setStrGuestLastName(String.valueOf(arrObjGuest[2]));
						objWalkinDtlBean.setLngMobileNo(Long.parseLong(arrObjGuest[3].toString()));
					}
				    objWalkinDtlBean.setStrGuestCode(objWalkinDtlModel.getStrGuestCode());
				    
					sql="select strRoomCode,strRoomDesc from tblroom "
						+ " where strRoomCode='"+objWalkinDtlModel.getStrRoomNo()+"' and strClientCode='"+clientCode+"' ";
					List listRoomMaster=objGlobalFunctionsService.funGetListModuleWise(sql, "sql");
					for(int cnt=0;cnt<listRoomMaster.size();cnt++)
					{
						Object[] arrObjRoom=(Object[])listRoomMaster.get(cnt);
						objWalkinDtlBean.setStrRoomDesc(String.valueOf(arrObjRoom[1]));
					}
					objWalkinDtlBean.setStrRoomNo(objWalkinDtlModel.getStrRoomNo());
					objWalkinDtlBean.setStrRoomType(objWalkinDtlModel.getStrRoomType());
					objWalkinDtlBean.setIntNoOfAdults(objWalkinDtlModel.getIntNoOfAdults());
					objWalkinDtlBean.setIntNoOfChild(objWalkinDtlModel.getIntNoOfChild());
					
					List listExtraBedData=objExtraBedMasterDao.funGetExtraBedMaster(objWalkinDtlModel.getStrExtraBedCode(), clientCode);
					if(listExtraBedData.size()>0)
					{
						clsExtraBedMasterModel objExtraBedMasterModel= (clsExtraBedMasterModel) listExtraBedData.get(0);
						objWalkinDtlBean.setStrExtraBedCode(objWalkinDtlModel.getStrExtraBedCode());
						objWalkinDtlBean.setStrExtraBedDesc(objExtraBedMasterModel.getStrExtraBedTypeDesc());
					}
					else
					{
						objWalkinDtlBean.setStrExtraBedCode("");
						objWalkinDtlBean.setStrExtraBedDesc("");
					}
				
					listWalkinDtlBean.add(objWalkinDtlBean);
					//hmWalkinDtlBean.put(objWalkinDtlModel.getStrRoomNo(),objWalkinDtlBean);
				}
				
				/*
				for (Map.Entry<String, clsWalkinDtlBean> entry : hmWalkinDtlBean.entrySet()) {
					listWalkinDtlBean.add(entry.getValue());
				}*/
				
				objWalkinBean.setListWalkinDetailsBean(listWalkinDtlBean);
			}
			return objWalkinBean;
		}

	

//Save or Update Walkin
	@RequestMapping(value = "/saveWalkin", method = RequestMethod.GET)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsWalkinBean objBean ,BindingResult result,HttpServletRequest req){
		if(!result.hasErrors())
		{
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String userCode=req.getSession().getAttribute("usercode").toString();
			clsWalkinHdModel objHdModel = objWalkinService.funPrepareWalkinModel(objBean,clientCode,req,userCode);
			
			clsCheckInBean objCheckInBean = new clsCheckInBean();
			List<clsCheckInDetailsBean> listCheckinDtlBean=new ArrayList<clsCheckInDetailsBean>();
			Map<Long,String> hmGuestMbWithCode=new HashMap<Long,String>();
			
			objCheckInBean.setStrRegistrationNo("");
			objCheckInBean.setStrType("Walk In");
			objCheckInBean.setStrAgainstDocNo(objHdModel.getStrWalkinNo());
			objCheckInBean.setDteArrivalDate(objGlobal.funGetDate("yyyy/MM/dd",objHdModel.getDteWalkinDate()));
			objCheckInBean.setTmeDepartureTime(objHdModel.getTmeCheckOutTime());
			objCheckInBean.setTmeArrivalTime(objHdModel.getTmeWalkinTime());
			objCheckInBean.setDteDepartureDate(objGlobal.funGetDate("yyyy/MM/dd",objHdModel.getDteCheckOutDate()));

			objWalkinDao.funAddUpdateWalkinHd(objHdModel);
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage","Walkin Code : ".concat(objHdModel.getStrWalkinNo()));
			req.getSession().setAttribute("AdvanceAmount",objHdModel.getStrWalkinNo());
			return new ModelAndView("redirect:/frmWalkin.html");
		}
		else
		{
			return new ModelAndView("frmWalkin");
		}
	}

	
	// Load Master Data On Form
    @RequestMapping(value = "/loadRoomInformation", method = RequestMethod.GET)
    public @ResponseBody List  funLoadMasterData(@RequestParam("roomCode")String roomCode,HttpServletRequest req)
    {
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		
		String sql=" select a.strRoomCode,a.strRoomDesc,b.strRoomTypeDesc "
				  + " from tblroom a,tblroomtypemaster b  "
				  + " where a.strClientCode='"+clientCode+"' and a.strRoomCode='"+roomCode+"' "
				  + " and a.strRoomTypeCode=b.strRoomTypeCode  " ;
		List listRoom=objGlobalFunctionsService.funGetListModuleWise(sql, "sql");
		return listRoom;
		
    }
    
    
  //Load data from database to form 
  	@RequestMapping(value = "/loadGuestInformation", method = RequestMethod.GET)
  	public @ResponseBody List funFetchGuestMasterData(@RequestParam("guestCode") String guestCode,HttpServletRequest req)
  	{
  		String clientCode=req.getSession().getAttribute("clientCode").toString();
  		

		String sql=" select a.strGuestCode,a.strFirstName,a.strMiddleName,a.strLastName,a.lngMobileNo "
				  + " from tblguestmaster a "
				  + " where a.strClientCode='"+clientCode+"' and a.strGuestCode='"+guestCode+"' " ;
				  
		List listGuest=objGlobalFunctionsService.funGetListModuleWise(sql, "sql");
  		
  		return listGuest;
  	}
  	
  	
  	 //Load data from database to form 
  	@RequestMapping(value = "/loadWalkinInformation", method = RequestMethod.GET)
  	public @ResponseBody List funFetchWalkinInformation(@RequestParam("walkinNo") String walkinNo,HttpServletRequest req)
  	{
  		String clientCode=req.getSession().getAttribute("clientCode").toString();
  		

		String sql= "  select a.strWalkInNo,a.dteArrivalDate,a.tmeArrivalTime,a.dteDepartureDate,a.tmeDepartureTime,"
				  + " a.strCorporateCode,a.strBookerCode,a.strAgentCode,a.intNoOfNights"
				  + " from tblcheckinhd a where a.strWalkInNo='"+walkinNo+"' " ;
				  
		List listWalkinData=objGlobalFunctionsService.funGetListModuleWise(sql, "sql");
  		
  		return listWalkinData;
  	}

  	
}
