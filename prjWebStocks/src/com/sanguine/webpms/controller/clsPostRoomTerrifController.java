package com.sanguine.webpms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webpms.bean.clsPostRoomTerrifBean;
import com.sanguine.webpms.bean.clsTaxCalculation;
import com.sanguine.webpms.bean.clsTaxProductDtl;
import com.sanguine.webpms.dao.clsExtraBedMasterDao;
import com.sanguine.webpms.model.clsExtraBedMasterModel;
import com.sanguine.webpms.model.clsFolioDtlModel;
import com.sanguine.webpms.model.clsFolioHdModel;
import com.sanguine.webpms.model.clsFolioTaxDtl;
import com.sanguine.webpms.service.clsFolioService;

@Controller
public class clsPostRoomTerrifController {

	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	
	@Autowired
	clsPMSUtilityFunctions objPMSUtility;
	
	@Autowired
	clsFolioService objFolioService;
	
	@Autowired
	clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsExtraBedMasterDao objExtraBedMasterDao;
	
// Open Post Room Terrif
    @RequestMapping(value = "/frmPostRoomTerrif", method = RequestMethod.GET)
    public ModelAndView funOpenForm()
    {
    	clsPostRoomTerrifBean objBean = new clsPostRoomTerrifBean();
    	return new ModelAndView("frmPostRoomTerrif", "command", objBean);
    }
    
    
  //Load Header Table Data On Form
  	@RequestMapping(value = "/loadRoomDetails", method = RequestMethod.GET)
  	public @ResponseBody List funLoadRoomDetails(HttpServletRequest request){
  		
  		String clientCode=request.getSession().getAttribute("clientCode").toString();
  		String propCode=request.getSession().getAttribute("propertyCode").toString();
  		String roomNo=request.getParameter("roomNo").toString();
  		
  		/*String sql="select e.strRoomCode,e.strRoomDesc,c.strRegistrationNo,d.strFirstName,d.strMiddleName,d.strLastName"
  			+ ",f.dblRoomTerrif "
  			+ " from tblreservationhd a,tblreservationdtl b,tblcheckinhd c,tblguestmaster d,tblroom e,tblroomtypemaster f "
  			+ " where a.strReservationNo=b.strReservationNo and a.strReservationNo=c.strReservationNo and b.strRoomNo=e.strRoomCode "
  			+ " and e.strRoomTypeCode=f.strRoomTypeCode and b.strGuestCode=d.strGuestCode and b.strRoomNo='"+roomNo+"' "
  			+ " and c.strRegistrationNo not in (select strRegistrationNo from tblbillhd) ";*/
  		
  		String sql=" select d.strRoomCode,d.strRoomDesc,a.strRegistrationNo,c.strFirstName,c.strMiddleName,c.strLastName,e.dblRoomTerrif "
  				+ " from tblcheckinhd a,tblcheckindtl b,tblguestmaster c,tblroom d,tblroomtypemaster e "
  				+ "  where a.strCheckInNo=b.strCheckInNo and b.strGuestCode=c.strGuestCode and b.strRoomNo=d.strRoomCode  "
  				+ "  and d.strRoomTypeCode=e.strRoomTypeCode and b.strRoomNo='"+roomNo+"' "
  				+ " and a.strCheckInNo not in (select strCheckInNo from tblbillhd)  ";
  		
  		
  		List listRoomDtl=objGlobalFunctionsService.funGetListModuleWise(sql, "sql");
  		return listRoomDtl;
  	}
  	
  	
  //Save or Update Reservation
  	@RequestMapping(value = "/postRoomTerrif", method = RequestMethod.POST)
  	public ModelAndView funPostRoomTerrif(@ModelAttribute("command") @Valid clsPostRoomTerrifBean objBean ,BindingResult result,HttpServletRequest req){
  		if(!result.hasErrors()){
  			String clientCode=req.getSession().getAttribute("clientCode").toString();
  			String userCode=req.getSession().getAttribute("usercode").toString();
  			String propCode=req.getSession().getAttribute("propertyCode").toString();
  			String PMSDate=objGlobal.funGetDate("yyyy-MM-dd",req.getSession().getAttribute("PMSDate").toString());
  			
  			String sql="select strFolioNo,strExtraBedCode "
  				+ " from tblfoliohd "
  				+ " where strRoomNo='"+objBean.getStrRoomNo()+"' and strRegistrationNo='"+objBean.getStrRegistrationNo()+"' "
  				+ " and strClientCode='"+clientCode+"'";
  			List listFolio=objGlobalFunctionsService.funGetListModuleWise(sql, "sql");
  			Object[] arrObjFolioDtl = (Object[]) listFolio.get(0);
  			
  			String folioNo=arrObjFolioDtl[0].toString();
  			String extraBedCode=arrObjFolioDtl[1].toString();
  			
  			String docNo=funInsertFolioRecords(folioNo,clientCode,propCode,objBean,PMSDate,extraBedCode);
  			
  			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage","Terrif Posted Successfully. "+docNo);
  			
  			return new ModelAndView("redirect:/frmPostRoomTerrif.html");
  		}
  		else{
  			return new ModelAndView("frmPostRoomTerrif");
  		}
  	}
  	
  	
  	public String funInsertFolioRecords(String folioNo, String clientCode, String propCode, clsPostRoomTerrifBean objBean, String PMSDate, String extraBedCode)
  	{
  		clsFolioHdModel objFolioHd=objFolioService.funGetFolioList(folioNo, clientCode, propCode);
		System.out.println(objFolioHd.getListFolioDtlModel().size());
		//long nextDocNo=objGlobalFunctionsService.funGetNextNo("tblfoliodtl", "FolioPosting", "strDocNo", clientCode, "and left(strDocNo,2)='RM'");
		//docNo="RM"+String.format("%06d", nextDocNo);
		
		long doc=objPMSUtility.funGenerateFolioDocForRoom("RoomFolio");
    	String docNo="RM"+String.format("%06d", doc);
		double roomTerrif=objBean.getDblRoomTerrif();
		
  		clsTaxProductDtl objTaxProductDtl=new clsTaxProductDtl();
		objTaxProductDtl.setStrTaxProdCode(objBean.getStrRoomNo());
		objTaxProductDtl.setStrTaxProdName("");
		objTaxProductDtl.setDblTaxProdAmt(roomTerrif);
		List<clsTaxProductDtl> listTaxProdDtl = new ArrayList<clsTaxProductDtl>();
		listTaxProdDtl.add(objTaxProductDtl);
		Map<String,List<clsTaxCalculation>> hmTaxCalDtl = objPMSUtility.funCalculatePMSTax(listTaxProdDtl,"Room Night");
			
		List<clsFolioDtlModel> listFolioDtl=new ArrayList<clsFolioDtlModel>();
		List<clsFolioTaxDtl> listFolioTaxDtl=new ArrayList<clsFolioTaxDtl>();
		List<String> listDocNo=new ArrayList<String>();
		
		/*
		if(objFolioHd.getListFolioDtlModel().size()>0)
		{
			for(clsFolioDtlModel objFolioDtlModel:objFolioHd.getListFolioDtlModel())
			{
				String folioDate=objFolioDtlModel.getDteDocDate().split(" ")[0];
				if(objFolioDtlModel.getStrRevenueType().equals("Room") || objFolioDtlModel.getStrRevenueType().equals("ExtraBed"))
				{
					if(!PMSDate.equals(folioDate))
					{
						listFolioDtl.add(objFolioDtlModel);
						listDocNo.add(objFolioDtlModel.getStrDocNo());
					}
				}
				else
				{
					listFolioDtl.add(objFolioDtlModel);
					listDocNo.add(objFolioDtlModel.getStrDocNo());
				}
			}
		}
		
		if(objFolioHd.getListFolioTaxDtlModel().size()>0)
		{
			for(clsFolioTaxDtl objFolioTaxDtlModel:objFolioHd.getListFolioTaxDtlModel())
			{
				if(listDocNo.contains(objFolioTaxDtlModel.getStrDocNo()))
				{
					listFolioTaxDtl.add(objFolioTaxDtlModel);
				}
			}
		}
		*/
		
		clsFolioDtlModel objFolioDtl=new clsFolioDtlModel();
		objFolioDtl.setStrDocNo(docNo);
		objFolioDtl.setDteDocDate(PMSDate);
		objFolioDtl.setDblDebitAmt(roomTerrif);
		objFolioDtl.setDblBalanceAmt(0);
		objFolioDtl.setDblCreditAmt(0);
		objFolioDtl.setStrPerticulars("Room Revenue ");
		objFolioDtl.setStrRevenueType("Room");
		objFolioDtl.setStrRevenueCode(objBean.getStrRoomNo());
		listFolioDtl.add(objFolioDtl);
			
		List<clsTaxCalculation> listTaxCal=hmTaxCalDtl.get(objBean.getStrRoomNo());
		for(clsTaxCalculation objTaxCal:listTaxCal)
		{  			
  			clsFolioTaxDtl objFolioTaxDtl=new clsFolioTaxDtl();
  			objFolioTaxDtl.setStrDocNo(docNo);
  			objFolioTaxDtl.setStrTaxCode(objTaxCal.getStrTaxCode());
  			objFolioTaxDtl.setStrTaxDesc(objTaxCal.getStrTaxDesc());
  			objFolioTaxDtl.setDblTaxableAmt(objTaxCal.getDblTaxableAmt());
  			objFolioTaxDtl.setDblTaxAmt(objTaxCal.getDblTaxAmt());
  			listFolioTaxDtl.add(objFolioTaxDtl);
		}
		
		if(!extraBedCode.isEmpty())
		{
			List listExtraBed = objExtraBedMasterDao.funGetExtraBedMaster(extraBedCode, clientCode);
			clsExtraBedMasterModel objExtraBedMaster = (clsExtraBedMasterModel) listExtraBed.get(0);
			
			doc=objPMSUtility.funGenerateFolioDocForRoom("RoomFolio");
	    	docNo="RM"+String.format("%06d", doc);
	    	
			objFolioDtl=new clsFolioDtlModel();
			objFolioDtl.setStrDocNo(docNo);
			objFolioDtl.setDteDocDate(PMSDate);
			objFolioDtl.setDblDebitAmt(objExtraBedMaster.getDblChargePerBed());
			objFolioDtl.setDblBalanceAmt(0);
			objFolioDtl.setDblCreditAmt(0);
			objFolioDtl.setStrPerticulars("Extra Bed Charges");
			objFolioDtl.setStrRevenueType("ExtraBed");
			objFolioDtl.setStrRevenueCode(objExtraBedMaster.getStrExtraBedTypeCode());
			listFolioDtl.add(objFolioDtl);
						
			objTaxProductDtl=new clsTaxProductDtl();
			objTaxProductDtl.setStrTaxProdCode(objExtraBedMaster.getStrExtraBedTypeCode());
			objTaxProductDtl.setStrTaxProdName("");
			objTaxProductDtl.setDblTaxProdAmt(objExtraBedMaster.getDblChargePerBed());
			List<clsTaxProductDtl> listTaxProdDtlForExtraBed = new ArrayList<clsTaxProductDtl>();
			listTaxProdDtlForExtraBed.add(objTaxProductDtl);
			Map<String,List<clsTaxCalculation>> hmTaxCalDtlForExtraBed = objPMSUtility.funCalculatePMSTax(listTaxProdDtlForExtraBed,"Room Night");			
			List<clsTaxCalculation> listTaxCalForExtraBed=hmTaxCalDtlForExtraBed.get(objExtraBedMaster.getStrExtraBedTypeCode());
			for(clsTaxCalculation objTaxCal:listTaxCalForExtraBed)
			{
	  			clsFolioTaxDtl objFolioTaxDtl=new clsFolioTaxDtl();
	  			objFolioTaxDtl.setStrDocNo(docNo);
	  			objFolioTaxDtl.setStrTaxCode(objTaxCal.getStrTaxCode());
	  			objFolioTaxDtl.setStrTaxDesc(objTaxCal.getStrTaxDesc());
	  			objFolioTaxDtl.setDblTaxableAmt(objTaxCal.getDblTaxableAmt());
	  			objFolioTaxDtl.setDblTaxAmt(objTaxCal.getDblTaxAmt());
	  			listFolioTaxDtl.add(objFolioTaxDtl);
			}
		}
		
		
		objFolioHd.setListFolioDtlModel(listFolioDtl);
		objFolioHd.setListFolioTaxDtlModel(listFolioTaxDtl);
		objFolioService.funAddUpdateFolioHd(objFolioHd);
			
		return docNo;
  	}
  	
    
}
