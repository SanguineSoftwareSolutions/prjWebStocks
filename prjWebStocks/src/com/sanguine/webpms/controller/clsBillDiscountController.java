package com.sanguine.webpms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webpms.bean.clsBillDiscountBean;
import com.sanguine.webpms.model.clsBillDiscountHdModel;
import com.sanguine.webpms.model.clsBillHdModel;
import com.sanguine.webpms.model.clsRoomMasterModel;
import com.sanguine.webpms.service.clsBillDiscountService;
import com.sanguine.webpms.service.clsBillService;
import com.sanguine.webpms.service.clsRoomMasterService;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

@Controller
public class clsBillDiscountController{

	@Autowired
	private clsBillDiscountService objBillDiscountService;
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	@Autowired
	private clsGlobalFunctions objGlobal=null;
	
	@Autowired
	private clsBillService  objBillService;
	
	@Autowired 
	private  clsRoomMasterService objRoomService;

//Open BillDiscount
	@RequestMapping(value = "/frmBillDiscount", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request){
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmBillDiscount_1","command", new clsBillDiscountBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmBillDiscount","command", new clsBillDiscountBean());
		}else {
		return null;
		}
	}


//Save or Update BillDiscount
	@RequestMapping(value = "/saveBillDiscount", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsBillDiscountBean objBean ,BindingResult result,HttpServletRequest req){
		
		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		if(!result.hasErrors()){
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String userCode=req.getSession().getAttribute("usercode").toString();
			clsBillDiscountHdModel objModel = funPrepareModel(objBean,userCode,clientCode);
			if(objModel!=null)
			{
				objBillDiscountService.funAddUpdateBillDiscount(objModel);
			}
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage", "Saved");
			return new ModelAndView("redirect:/frmBillDiscount.html?saddr="+urlHits);
		}
		else{
			return new ModelAndView("redirect:/frmBillDiscount.html?saddr="+urlHits);
		}
	}

//Convert bean to model function
	private clsBillDiscountHdModel funPrepareModel(clsBillDiscountBean objBean,String userCode,String clientCode){
		objGlobal=new clsGlobalFunctions();
		long lastNo=0;
		clsBillDiscountHdModel objModel=null;
		
		boolean flgDelete = objBillDiscountService.funDeleteBillDiscount(objBean.getStrBillNo(),clientCode);
		
		if(flgDelete)
		{
			objModel=new clsBillDiscountHdModel();
			objModel.setStrBillNo(objBean.getStrBillNo());
			objModel.setStrCheckInNo(objBean.getStrCheckInNo());
			objModel.setStrFolioNo(objBean.getStrFolioNo());
			objModel.setStrRoomNo(objBean.getStrRoomNo());
			objModel.setStrUserCreated(userCode);
			objModel.setStrUserEdited(userCode);
			objModel.setStrClientCode(clientCode);
			objModel.setDblDiscAmt(objBean.getDblDiscAmt());
			objModel.setDblGrandTotal(objBean.getDblGrandTotal());
			objModel.setDteBillDate(objGlobal.funGetDate("yyyy-MM-dd", objBean.getDteBillDate()));
			objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		}
		
		
		return objModel;

	}
	
	
	
	//Load Master Data On Form
		@RequestMapping(value = "/getBillData", method = RequestMethod.POST)
		public @ResponseBody clsBillDiscountBean funLoadBillData(HttpServletRequest request){
			objGlobal=new clsGlobalFunctions();
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			String userCode=request.getSession().getAttribute("usercode").toString();
			clsBillDiscountBean objBean=null;
			String docCode=request.getParameter("docCode").toString();
			clsBillHdModel objBillhd = objBillService.funLoadBill(docCode, clientCode);
			if(objBillhd!=null)
			{
				objBean=new clsBillDiscountBean();
				objBean.setStrBillNo(objBillhd.getStrBillNo());
				objBean.setStrCheckInNo(objBillhd.getStrCheckInNo());
				objBean.setStrFolioNo(objBillhd.getStrFolioNo());
				if(objBillhd.getStrRoomNo()!="")
				{
					clsRoomMasterModel roomModel=  objRoomService.funGetRoomMaster( objBillhd.getStrRoomNo(),  clientCode);
					objBean.setStrRoomNo( objBillhd.getStrRoomNo());
				}else
				{
					objBean.setStrRoomNo("");
				}
				
				objBean.setDteBillDate(objGlobal.funGetDate("dd-MM-yyyy", objBillhd.getDteBillDate()));
				objBean.setDblDiscAmt(0.00);
				objBean.setDblGrandTotal(objBillhd.getDblGrandTotal());
			}else
			{
				objBean=new clsBillDiscountBean();
				objBean.setStrBillNo("Invalid Bill No");
			}
			return objBean;
		}
		
		
		
		//Load Master Data On Form
				@RequestMapping(value = "/loadBillData", method = RequestMethod.POST)
				public @ResponseBody clsBillDiscountHdModel funSetBillData(HttpServletRequest request){
					objGlobal=new clsGlobalFunctions();
					String sql="";
					String clientCode=request.getSession().getAttribute("clientCode").toString();
					String userCode=request.getSession().getAttribute("usercode").toString();
					clsBillDiscountBean objBean=new clsBillDiscountBean();
					String docCode=request.getParameter("docCode").toString();
					List listModel=objGlobalFunctionsService.funGetList(sql);
					clsBillDiscountHdModel objBillDiscount = new clsBillDiscountHdModel();
					return objBillDiscount;
				}

}
