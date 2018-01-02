package com.sanguine.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.bean.clsDeleteTransactionBean;
import com.sanguine.crm.controller.clsInvoiceController;
import com.sanguine.model.clsDeleteTransModel;
import com.sanguine.model.clsProductionHdModel;
import com.sanguine.model.clsProductionOrderHdModel;
import com.sanguine.model.clsStkAdjustmentHdModel;
import com.sanguine.model.clsStkPostingHdModel;
import com.sanguine.model.clsStkTransferHdModel;
import com.sanguine.model.clsWorkOrderHdModel;
import com.sanguine.service.clsDelTransService;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsProductionOrderService;
import com.sanguine.service.clsProductionService;
import com.sanguine.service.clsStkAdjustmentService;
import com.sanguine.service.clsStkPostingService;
import com.sanguine.service.clsStkTransferService;
import com.sanguine.service.clsWorkOrderService;

@Controller
public class clsDeleteTransController
{	
	@Autowired
	clsDelTransService objDelTransService;
	@Autowired
	clsGlobalFunctionsService objGlobalFunService;
	
	@Autowired
	clsMISController objMIS;
	
	@Autowired
	clsGRNController objGRN;
	
	@Autowired
	clsMaterialReturnController objMatReturn;
	
	@Autowired
	clsStkAdjustmentController objStkAdj;
	
	@Autowired
	clsMaterialReqController objMatReq;
	
	@Autowired
	clsStkTransferController objStkTransfer;
	
	@Autowired
	clsProductionController objProduction;
	
	@Autowired
	clsPurchaseReturnController objPurRet;
	
	@Autowired
	clsStockController objOpStk;
	
	@Autowired
	clsPurchaseOrderController objPurOrder;
		
	@Autowired
	clsStkPostingController objPhyStk;
	
	@Autowired
	clsPurchaseIndentHdController objPurchaseIndent;
		
	@Autowired
	clsBillPassingController objBillPassing;
	
	@Autowired
	clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsStkPostingService objStkPostService;
	
	@Autowired
	private clsProductionService objPDService;
	
	@Autowired
	private clsWorkOrderService objWorkOrderService;
	
	@Autowired
	clsProductionOrderService objProductionOrderService;
	
	@Autowired
	private clsStkTransferService objStkTransService;
	
	@Autowired
	private clsStkAdjustmentService objStkAdjService;
	
	@Autowired
	private clsInvoiceController objInvController;
	
	@Autowired
	private clsProductionOrderController objProductionOrderController;
	
	@Autowired
	private clsWorkOrderController objWorkOrderController;
	
	@Autowired
	private clsProductionController onjProductionController;
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/frmDeleteTransaction", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") clsDeleteTransactionBean objBean,BindingResult result,Map<String,Object> model,HttpServletRequest req)
	{
		String strModule="1";
		Map<String,String> mapTransForms=new HashMap<String, String>();
		String sql="select strFormName,strFormDesc from clsTreeMasterModel "
				+ "where strType='T' and strModule='"+strModule+"' "
				+ "order by strFormName";
		List list=objGlobalFunService.funGetList(sql,"hql");
		for(int cnt=0;cnt<list.size();cnt++)
		{
			Object[] arrObj=(Object[])list.get(cnt);
			mapTransForms.put(arrObj[0].toString(),arrObj[1].toString());
		}
		mapTransForms.put("frmInvoice","Invoice");
		ModelAndView objModelAndView=new ModelAndView();
		objModelAndView.addObject("listFormName",mapTransForms);
		return objModelAndView;
	}
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/deleteTransaction", method = RequestMethod.POST)
	public ModelAndView funDeleteTransaction(@ModelAttribute("command") clsDeleteTransactionBean objBean,BindingResult result,Map<String,Object> model,HttpServletRequest req)
	{
		objGlobal=new clsGlobalFunctions();
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String userCode=req.getSession().getAttribute("usercode").toString();
		Map<String,String> mapTransForms=new HashMap<String, String>();
		/*String sql="select strFormName,strFormDesc from clsTreeMasterModel "
				+ "where strType='T' and strClientCode='"+clientCode+"' "
				+ "order by strFormName";*/
		
		String sql="select strFormName,strFormDesc from clsTreeMasterModel "
				+ "where strType='T' "
				+ "order by strFormName";
		
		List list=objGlobalFunService.funGetList(sql,"hql");
		for(int cnt=0;cnt<list.size();cnt++)
		{
			Object[] arrObj=(Object[])list.get(cnt);
			mapTransForms.put(arrObj[0].toString(),arrObj[1].toString());
		}
		mapTransForms.put("frmInvoice","Invoice");
		String formName=mapTransForms.get(objBean.getStrFormName());
		clsDeleteTransModel objModel=new clsDeleteTransModel();
		objModel.setStrFormName(formName);
		objModel.setStrTransCode(objBean.getStrTransCode());
		objModel.setStrReasonCode(objGlobal.funIfNull(objBean.getStrReasonCode(), "NA", objBean.getStrReasonCode()));
		objModel.setStrNarration(objGlobal.funIfNull(objBean.getStrNarration(),"",objBean.getStrNarration()));
		objModel.setDtDeleteDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		objModel.setStrUserCode(userCode);
		objModel.setStrClientCode(clientCode);
		objModel.setStrDataPostFlag("N");
		clsStkPostingHdModel objPhyModel=objStkPostService.funGetModelObject(objBean.getStrTransCode(), clientCode);
		
		String sql_DeleteHd="delete from ";
		String sql_DeleteDtl="delete from ";
		clsDeleteTransModel objDeleteModelForSA=new clsDeleteTransModel();
		if(objPhyModel!=null)
		{
			objDeleteModelForSA.setStrFormName(formName);
			objDeleteModelForSA.setStrTransCode(objBean.getStrTransCode());
			objDeleteModelForSA.setStrReasonCode(objGlobal.funIfNull(objBean.getStrReasonCode(), "NA", objBean.getStrReasonCode()));
			objDeleteModelForSA.setStrNarration(objGlobal.funIfNull(objBean.getStrNarration(),"",objBean.getStrNarration()));
			objDeleteModelForSA.setDtDeleteDate(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objDeleteModelForSA.setStrUserCode(userCode);
			objDeleteModelForSA.setStrClientCode(clientCode);
			objDeleteModelForSA.setStrDataPostFlag("N");
		}
		
		String sql_StckAdjDeleteHd="delete from ";
		String sql_StckAdjDeleteDtl="delete from ";
		
		boolean flgTrans=false;
		String queryType="hql";
		String type="Deleted";
		switch(formName)
		{
			case "GRN(Good Receiving Note)":
				flgTrans=true;
				objGRN.funSaveAudit(objBean.getStrTransCode(),type, req);
				sql_DeleteHd+="clsGRNHdModel where strGRNCode=";
				sql_DeleteDtl+="clsGRNDtlModel where strGRNCode=";
				queryType="hql";
				break;
				
			case "Opening Stock":
				flgTrans=true;
				objOpStk.funSaveAudit(objBean.getStrTransCode(),type, req);
				sql_DeleteHd+="clsInitialInventoryModel where strOpStkCode=";
				sql_DeleteDtl+="clsOpeningStkDtl where strOpStkCode=";
				queryType="hql";
				break;
				
			case "Physical Stk Posting":
				flgTrans=true;
				if(objPhyModel!=null)
				{
					objStkAdj.funSaveAudit(objPhyModel.getStrSACode(),type, req);
					sql_StckAdjDeleteHd+="clsStkAdjustmentHdModel where strSACode=";
					sql_StckAdjDeleteDtl+="clsStkAdjustmentDtlModel where strSACode=";
				}
				objPhyStk.funSaveAudit(objBean.getStrTransCode(),type, req);
				sql_DeleteHd+="clsStkPostingHdModel where strPSCode=";
				sql_DeleteDtl+="clsStkPostingDtlModel where strPSCode=";
				queryType="hql";
				break;
				
			case "Production Order":
				flgTrans=true;
				objProductionOrderController.funSaveAudit(objBean.getStrTransCode(), type, req);
				sql_DeleteHd+="clsProductionOrderHdModel where strOPCode=";
				sql_DeleteDtl+="clsProductionOrderDtlModel where strOPCode=";
				queryType="hql";
				break;
				
			case "Purchase Indent":
				flgTrans=true;
				objPurchaseIndent.funSaveAudit(objBean.getStrTransCode(),type, req);
				sql_DeleteHd+="tblpurchaseindendhd where strPICode=";
				sql_DeleteDtl+="tblpurchaseindenddtl where strPICode=";
				queryType="sql";
				break;
				
			case "Purchase Order":
				flgTrans=true;
				objPurOrder.funSaveAudit(objBean.getStrTransCode(),type, req);
				sql_DeleteHd+="clsPurchaseOrderHdModel where strPOCode=";
				sql_DeleteDtl+="clsPurchaseOrderDtlModel where strPOCode=";
				queryType="hql";
				break;
				
			case "Purchase Return":
				flgTrans=true;
				objPurRet.funSaveAudit(objBean.getStrTransCode(),type, req);
				sql_DeleteHd+="clsPurchaseReturnHdModel where strPRCode=";
				sql_DeleteDtl+="clsPurchaseReturnDtlModel where strPRCode=";
				queryType="hql";
				break;
				
			case "Stock Adjustment":
				flgTrans=true;
				objStkAdj.funSaveAudit(objBean.getStrTransCode(),type, req);
				sql_DeleteHd+="clsStkAdjustmentHdModel where strSACode=";
				sql_DeleteDtl+="clsStkAdjustmentDtlModel where strSACode=";
				queryType="hql";
				break;
				
			case "Stock Transfer":
				flgTrans=true;
				objStkTransfer.funSaveAudit(objBean.getStrTransCode(),type, req);
				sql_DeleteHd+="clsStkTransferHdModel where strSTCode=";
				sql_DeleteDtl+="clsStkTransferDtlModel where strSTCode=";
				queryType="hql";
				break;
				
			case "Work Order":
				flgTrans=true;
				objWorkOrderController.funSaveAudit(objBean.getStrTransCode(), type, req);
				sql_DeleteHd+="clsWorkOrderHdModel where strWOCode=";
				sql_DeleteDtl+="clsWorkOrderDtlModel where strWOCode=";
				queryType="hql";
				break;
			
			case "Material Return":
				flgTrans=true;
				objMatReturn.funSaveAudit(objBean.getStrTransCode(),type, req);
				sql_DeleteHd+="clsMaterialReturnHdModel where strMRetCode=";
				sql_DeleteDtl+="clsMaterialReturnDtlModel where strMRetCode=";
				queryType="hql";
				break;
				
			case "Material Issue Slip":
				flgTrans=true;
				objMIS.funSaveAudit(objBean.getStrTransCode(),type, req);
				sql_DeleteHd+="tblmishd where strMISCode=";
				sql_DeleteDtl+="tblmisdtl where strMISCode=";
				queryType="sql";
				break;
				
			case "Material Requisition":
				flgTrans=true;
				objMatReq.funSaveAudit(objBean.getStrTransCode(),type, req);
				sql_DeleteHd+="tblreqhd where strReqCode=";
				sql_DeleteDtl+="tblreqdtl where strReqCode=";
				queryType="sql";
				break;
		
			case "Material Production":
			//	flgTrans=true;
				onjProductionController.funSaveAudit(objBean.getStrTransCode(),type, req);
				clsProductionHdModel objPDModel= objPDService.funGetObject(objBean.getStrTransCode(),clientCode);
				String pdCode= objBean.getStrTransCode();
				objDelTransService.funDeleteRecord("delete from clsProductionHdModel where strPDCode='"+pdCode+"'  and strClientCode='"+clientCode+"'","hql");
				objDelTransService.funDeleteRecord("delete from clsProductionDtlModel where strPDCode='"+pdCode+"'  and strClientCode='"+clientCode+"'","hql");
				String woCode= objPDModel.getStrWOCode();
				clsWorkOrderHdModel objWO =objWorkOrderService.funGetWOHd(woCode,clientCode);
				if(objWO.getStrWOCode()!=null)
				{
					List woList =objWorkOrderService.funGetGeneratedWOAgainstOPData(objWO.getStrSOCode(), clientCode);
					if(woList.size()==1)
					{
						objWorkOrderController.funSaveAudit(woCode, type, req);
						objDelTransService.funDeleteRecord("delete from clsWorkOrderHdModel where strWOCode='"+woCode+"'  and strClientCode='"+clientCode+"'","hql");
						objDelTransService.funDeleteRecord("delete from tblworkorderdtl where strWOCode='"+woCode+"'  and strClientCode='"+clientCode+"'","sql");
						
						clsProductionOrderHdModel objPOModel =  objProductionOrderService.funGetObject(objWO.getStrSOCode(), clientCode);
						objProductionOrderController.funSaveAudit(objWO.getStrSOCode(), type, req);
						objDelTransService.funDeleteRecord("delete from clsProductionOrderHdModel where strOPCode='"+objPOModel.getStrOPCode()+"'  and strClientCode='"+clientCode+"'","hql");
						objDelTransService.funDeleteRecord("delete from clsProductionOrderDtlModel where strOPCode='"+objPOModel.getStrOPCode()+"'  and strClientCode='"+clientCode+"'","hql");
						
						clsStkTransferHdModel objSTModel =  objStkTransService.funGetModel(pdCode,clientCode);
						if(objSTModel.getStrSTCode()!=null)
						{
							objStkTransfer.funSaveAudit(objSTModel.getStrSTCode(),type, req);
							objDelTransService.funDeleteRecord("delete from clsStkTransferHdModel where strSTCode='"+objSTModel.getStrSTCode()+"'  and strClientCode='"+clientCode+"'","hql");
							objDelTransService.funDeleteRecord("delete from clsStkTransferDtlModel where strSTCode='"+objSTModel.getStrSTCode()+"'  and strClientCode='"+clientCode+"'","hql");
						}
					
					}
					
				}
				
				break;	
				
			case "Invoice":
				
				objInvController.funSaveAudit(objBean.getStrTransCode(),type, req);
				objDelTransService.funDeleteRecord("delete from tblinvoicehd where strInvCode='"+objBean.getStrTransCode()+"' and strClientCode='"+clientCode+"'","sql");
				objDelTransService.funDeleteRecord("delete from tblinvoicehd where strInvCode='"+objBean.getStrTransCode()+"' and strClientCode='"+clientCode+"'","sql");
				objDelTransService.funDeleteRecord("delete from tblinvprodtaxdtl where strInvCode='"+objBean.getStrTransCode()+"' and strClientCode='"+clientCode+"'","sql");
				objDelTransService.funDeleteRecord("delete from tblinvsalesorderdtl where strInvCode='"+objBean.getStrTransCode()+"' and strClientCode='"+clientCode+"'","sql");
				objDelTransService.funDeleteRecord("delete from tblinvsettlementdtl where strInvCode='"+objBean.getStrTransCode()+"' and strClientCode='"+clientCode+"'","sql");
				objDelTransService.funDeleteRecord("delete from tblinvtaxdtl where strInvCode='"+objBean.getStrTransCode()+"' and strClientCode='"+clientCode+"'","sql");
				clsStkAdjustmentHdModel objStk =  objStkAdjService.funGetDataModelByInvCode(objBean.getStrTransCode(),clientCode);
				if(objStk.getStrSACode()!=null)
				{
					objStkAdj.funSaveAudit(objBean.getStrTransCode(),type, req);
					objDelTransService.funDeleteRecord("delete from tblinvoicehd where strInvCode='"+objBean.getStrTransCode()+"' and strClientCode='"+clientCode+"'","sql");
					objDelTransService.funDeleteRecord("delete from tblinvoicehd where strInvCode='"+objBean.getStrTransCode()+"' and strClientCode='"+clientCode+"'","sql");
					
				}
				
				break;
				
				
				
		}
		
		if(flgTrans)
		{
			if(formName.equals("Physical Stk Posting"))
			{
				objDelTransService.funInsertRecord(objDeleteModelForSA);
				sql_StckAdjDeleteHd+="'"+objPhyModel.getStrSACode()+"' and strClientCode='"+clientCode+"'";
				sql_StckAdjDeleteDtl+="'"+objPhyModel.getStrSACode()+"' and strClientCode='"+clientCode+"'";
				objDelTransService.funDeleteRecord(sql_StckAdjDeleteHd,queryType);
				objDelTransService.funDeleteRecord(sql_StckAdjDeleteDtl,queryType);
			}
			objDelTransService.funInsertRecord(objModel);
			sql_DeleteHd+="'"+objBean.getStrTransCode()+"' and strClientCode='"+clientCode+"'";
			sql_DeleteDtl+="'"+objBean.getStrTransCode()+"' and strClientCode='"+clientCode+"'";
			objDelTransService.funDeleteRecord(sql_DeleteHd,queryType);
			objDelTransService.funDeleteRecord(sql_DeleteDtl,queryType);
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage","Transaction Code : ".concat(objBean.getStrTransCode()));
		}
		return new ModelAndView("redirect:/frmDeleteTransaction.html");
	}
	
	@RequestMapping(value = "/setReportFormName", method = RequestMethod.GET)	
	private void funCallJsperReport(@RequestParam("docCode")  String docCode1, HttpServletResponse resp,HttpServletRequest req)
	{
		String[] sp=docCode1.split(",");
		String docCode=sp[0];
		switch (sp[1]) 
		{
		   case "frmGRN":
			   objGRN.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmMaterialReturn":
			   objMatReturn.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmOpeningStock":
			   objOpStk.funCallOpeningReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmPhysicalStkPosting":
			   objPhyStk.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmProductionOrder":
			   //objProduction.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmPurchaseIndent":
			   objPurchaseIndent.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmPurchaseOrder":
			   objPurOrder.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmPurchaseReturn":
			   objPurRet.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmStockAdjustment":
			   objStkAdj.funCallReportBeanWise(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmWorkOrder":
			   break;
		   
		   case "frmStockTransfer":
			   objStkTransfer.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmMIS":
			   objMIS.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmMaterialReq":
			   objMatReq.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmProduction":
			   //objProduction.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmBillPassing":
			   //objBillPassing.funCallReport(docCode,"pdf",resp,req);
		       break;
		       
		   case "frmInvoice":
			  String invoiceformat=req.getAttribute("invoieFormat").toString();
				if(invoiceformat=="Format 1")
					{
					objInvController.funOpenInvoiceRetailReport(docCode,"pdf",resp,req);
					
					}else if(invoiceformat=="Format 2") {
					
						objInvController.funOpenInvoiceRetailReport(docCode,"pdf",resp,req);
				}else
				{
					objInvController.funOpenInvoiceRetailReport(docCode,"pdf",resp,req);
				}
			   
		       break;    
		}
		
	}	
	
}
