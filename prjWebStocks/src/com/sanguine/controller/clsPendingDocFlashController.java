package com.sanguine.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.bean.clsGlobalMembersBean;
import com.sanguine.service.clsGlobalFunctionsService;

@Controller
public class clsPendingDocFlashController 
{
	@Autowired
	clsGlobalFunctionsService objGlobalService;
	
	clsGlobalFunctions objGlobal;
	/**
	 * Open Pending Document flash
	 * @param objBean
	 * @param result
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/frmPendingDocFlash", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") clsGlobalMembersBean objBean,BindingResult result,Map<String,Object> model,HttpServletRequest req)
	{
		Map<String,String> mapTransForms=new HashMap<String, String>();
		Map<String,String> mapLocation1=new HashMap<String, String>();
		Map<String,String> mapLocation2=new HashMap<String, String>();
		Map<String,String> poStatus=new HashMap<String, String>();
		
		mapTransForms.put("frmMaterialReq","Material Requisition");
		mapTransForms.put("frmPurchaseOrder","Purchase Order");
		mapTransForms.put("frmPurchaseIndent","Purchase Indent");
		
		poStatus.put("ALL", "ALL");
		poStatus.put("Pending", "Pending");
		poStatus.put("Close", "Close");
		ModelAndView objModelAndView=new ModelAndView();
		objModelAndView.addObject("listFormName",mapTransForms);
		objModelAndView.addObject("listPOStatus",poStatus);
		
		mapLocation1=funGetTransactionWiseLocation("All");
		mapLocation2=funGetTransactionWiseLocation("Cost Center");
		objModelAndView.addObject("listLocationStores",mapLocation1);
		objModelAndView.addObject("listLocationCostCenters",mapLocation2);
		
		return objModelAndView;
	}
	
	/**
	 * Load Pending Document Data
	 * @param param1
	 * @param fDate
	 * @param tDate
	 * @param req
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/loadPendingDocData", method = RequestMethod.GET)
	public @ResponseBody List funLoadPendingDocData(@RequestParam(value="param1") String param1,
			@RequestParam(value="fDate") String fDate,
			@RequestParam(value="tDate") String tDate,HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String[] spParam=param1.split(",");
		String loc1=spParam[1];
		String loc2="";
		
		if(spParam[0].equals("frmMaterialReq"))
		{
			String[] spLoc=spParam[1].split("!");
			loc1=spLoc[0];
			loc2=spLoc[1];
		}
		String[] arrDate=fDate.split("-");
		fDate=arrDate[2]+"-"+arrDate[1]+"-"+arrDate[0];
		
		arrDate=tDate.split("-");
		tDate=arrDate[2]+"-"+arrDate[1]+"-"+arrDate[0];
		
		String sql="";

		if(spParam[0].equals("frmPurchaseOrder"))
		{
			if(spParam[2].equals("Summary"))
			{			
				
				sql="select strPOCode,date(dtPODate) PO_Date,date(dtDelDate) Delivery_Date, b.strPCode Supplier_Code, "
					+ "b.strPName Supplier_Name, strAgainst, strSOCode Purchase_Indent, dblTotal PO_Sub_Total, "
					+ "ifnull(dblTaxAmt,0) Tax_Amount, dblFinalAmt Final_PO_Amount, a.strUserCreated User_Code, "
					+ "date(a.dtDateCreated) Created_Date, a.strAuthorise Authorised, a.intLevel "
					+ "from tblPurchaseOrderHd a, tblpartymaster b "
					+ "Where (strPOCode IN (select distinct a.strPOCode from tblPurchaseOrderDtl a "
					+ "left outer join (select a.strPONo POCode, b.strProdCode, sum(dblQty) POQty "
					+ "from tblgrnhd a, tblgrndtl b "
					+ "where a.strGRNCode = b.strGRNCode and strAgainst = 'Purchase Order' "
					+ "and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' Group by a.strPONo, b.strProdCode) b "
					+ "on a.strPOCode = b.POCode and a.strProdCode = b.strProdCode and a.strClientCode='"+clientCode+"'   " ;
					
					if(spParam[4].toString().equalsIgnoreCase("Close"))
					{
						sql=sql+ " where  strClosePO = 'Yes' ) ";
					}
					if(spParam[4].toString().equalsIgnoreCase("Pending"))
					{
						sql=sql+ " where a.dblOrdQty > ifnull(b.POQty,0)) and strClosePO != 'Yes' ";
					}
					if(spParam[4].toString().equalsIgnoreCase("ALL"))
					{
						sql=sql+ ") ";
					}
					sql=sql+"  ) and a.strSuppCode = b.strPCode and date(a.dtPODate) between '"+fDate+"' and '"+tDate+"' "
							+ " and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' ";
					
					if(!spParam[3].toString().equalsIgnoreCase("ALL"))
					{
						sql=sql+ " and a.strSuppCode='"+spParam[3].toString()+"' "; 
					}
			}
			else
			{
				sql="select  a.strPOCode,date(c.dtPODate) PO_Date,date(c.dtDelDate) Delivery_Date, d.strPCode Supplier_Code,d.strPName Supplier_Name, "
						+ " c.strAgainst, c.strSOCode Purchase_Indent, dblTotal PO_Sub_Total,ifnull(a.dblTaxAmt,0) Tax_Amount, dblFinalAmt Final_PO_Amount, "
						+ " e.strProdCode,e.strProdName,e.strUOM, a.dblOrdQty,a.dblPrice,left((a.dblOrdQty*a.dblPrice),7) as TotalPrice ,"
						+ " ifnull(b.POQty,0) as ReceiveQty , (a.dblOrdQty-ifnull(b.POQty,0)) as BalQty, ifnull(b.strGRNCode,'') as GRNCode, "
						+ " c.strUserCreated User_Code,date(c.dtDateCreated) Created_Date,c.strAuthorise Authorised, c.intLevel "
						+ " from tblPurchaseOrderDtl a "
						+ " left outer join (select a.strPONo POCode, b.strProdCode, sum(dblQty) POQty, b.strGRNCode "
						+ " from tblgrnhd a, tblgrndtl b where a.strGRNCode = b.strGRNCode and strAgainst = 'Purchase Order' "
						+ " and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' "
						+ " Group by a.strPONo, b.strProdCode) b on a.strPOCode = b.POCode and a.strProdCode = b.strProdCode "
						+ " left outer join tblPurchaseOrderHd c on a.strPOCode = c.strPOCode and c.strClientCode='"+clientCode+"' "
						+ " inner join tblpartymaster d on c.strSuppCode=d.strPCode and d.strClientCode='"+clientCode+"'"
						+ " inner join tblproductmaster e on a.strProdCode=e.strProdCode and e.strClientCode='"+clientCode+"' "
						+ " where  date(c.dtPODate) between '"+fDate+"' and '"+tDate+"'"
						+ " and a.strClientCode='"+clientCode+"' " ;
				
				if(spParam[4].toString().equalsIgnoreCase("Close"))
				{
					sql=sql+ " and c.strClosePO = 'Yes' ";
				}
				if(spParam[4].toString().equalsIgnoreCase("Pending"))
				{
					sql=sql+ " and a.dblOrdQty > ifnull(b.POQty,0) and  c.strClosePO != 'Yes' ";
				}
				if(!spParam[3].toString().equalsIgnoreCase("ALL"))
				{
					sql=sql+ " and c.strSuppCode='"+spParam[3].toString()+"' "; 
				}
				sql=sql+" order by a.strPOCode asc ";
			}
		}
		else if(spParam[0].equals("frmPurchaseIndent"))
		{
			if(spParam[2].equals("Summary"))
			{
				sql="select a.strPICode, date(a.dtPIDate),b.strLocName,a.strNarration,a.dblTotal "
					+ "from tblpurchaseindendhd a,tbllocationmaster b "
					+ "where strPICode IN (select a.strPICode from tblpurchaseindenddtl a left outer join "
					+ "(select b.strPICode, b.strProdCode,sum(dblOrdQty) POQty from tblpurchaseorderhd a,tblpurchaseorderdtl b "
					+ "where a.strPOCode = b.strPOCode and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' ";
					
					if(!spParam[3].toString().equalsIgnoreCase("ALL"))
					{
						sql=sql+ " and a.strSuppCode='"+spParam[3].toString()+"' "; 
					}
					
					sql=sql+ "group by b.strPICode, b.strProdCode) as b on a.strPIcode = b.strPICode  and a.strProdCode = b.strProdCode "
					+ "where a.strClientCode='"+clientCode+"' and a.dblQty > ifnull(b.POQty,0)) "
					+ "and a.strLocCode=b.strLocCode and a.strLocCode='"+loc1+"' and a.strClientCode='"+clientCode+"' "
					+ "and b.strClientCode='"+clientCode+"' and date(a.dtPIDate) between '"+fDate+"' and '"+tDate+"'";
					
					if(spParam[4].toString().equalsIgnoreCase("Close"))
					{
						sql=sql+ " and a.strClosePI = 'Yes' ";
					}
				
					if(spParam[4].toString().equalsIgnoreCase("Pending"))
					{
						sql=sql+ "and a.strClosePI != 'Yes' ";
					}
			}
			else
			{
				sql="select a.strPICode,c.strProdCode,d.strProdName,d.strUOM,c.dblQty,c.dblReOrderQty,c.strPurpose "
					+ "from tblpurchaseindendhd a,tblpurchaseindenddtl c,tbllocationmaster b,tblproductmaster d "
					+ "where a.strPIcode=c.strPIcode and c.strProdCode=d.strProdCode "
					+ "and a.strPICode IN (select a.strPICode from tblpurchaseindenddtl a left outer join "
					+ "(select b.strPICode, b.strProdCode,sum(dblOrdQty) POQty from tblpurchaseorderhd a,tblpurchaseorderdtl b "
					+ "where a.strPOCode = b.strPOCode and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' ";
					
					if(!spParam[3].toString().equalsIgnoreCase("ALL"))
					{
						sql=sql+ " and a.strSuppCode='"+spParam[3].toString()+"' "; 
					}
					
					sql=sql+ "group by b.strPICode, b.strProdCode) as b on a.strPIcode = b.strPICode  and a.strProdCode = b.strProdCode "
					+ "where a.strClientCode='"+clientCode+"' and a.dblQty > ifnull(b.POQty,0)) "
					+ "and a.strLocCode=b.strLocCode and a.strLocCode='"+loc1+"' and a.strClientCode='"+clientCode+"' "
					+ "and b.strClientCode='"+clientCode+"' and date(a.dtPIDate) between '"+fDate+"' and '"+tDate+"'";
				
					if(spParam[4].toString().equalsIgnoreCase("Close"))
					{
						sql=sql+ " and a.strClosePI = 'Yes' ";
					}
					
					if(spParam[4].toString().equalsIgnoreCase("Pending"))
					{
						sql=sql+ "and a.strClosePI != 'Yes' ";
					}
				
			}
		}
		else
		{
			if(spParam[2].equals("Summary"))
			{
				sql="select a.strReqCode Requestion_Code, date(a.dtReqDate) Requestion_Date, b.strLocName as Location_By,"
					+ " c.strLocName as Location_On,a.strNarration Remarks, a.strUserCreated User_Created "
					+ ", date(a.dtCreatedDate) Created_Date,a.strAuthorise Authorise, a.intLevel Authorise_Level "
					+ " from tblreqhd a left outer join tbllocationmaster b on  b.strLocCode=a.strLocBy and b.strClientCode='"+clientCode+"'"
					+ " left outer join tbllocationmaster c on  c.strLocCode=a.strLocOn and c.strClientCode='"+clientCode+"'"
					+ " Where a.strReqCode IN (select a.strReqCode  from tblreqdtl a "
					+ " left outer join (select b.strReqCode, b.strProdCode, SUM(dblQty) ReqQty "
					+ " from tblmishd a, tblmisdtl b "
					+ " Where a.strMISCode = b.strMISCode and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' "
					+ " group by b.strReqCode, b.strProdCode) b on a.strReqCode = b.strReqCode "
					+ " and a.strProdCode = b.strProdCode "
					+ " where  a.dblQty > ifnull(b.ReqQty,0)) "
					+ " and a.strLocby='"+loc1+"' and a.strLocon='"+loc2+"' "
					+ " and date(a.dtReqDate) between '"+fDate+"' and '"+tDate+"' and a.strClientCode='"+clientCode+"' "
					+ " order by a.dtReqDate";
			}
			else
			{
				sql="SELECT r.strReqCode Requestion_Code, date(rh.dtReqDate) Requestion_Date, b.strLocName as Location_By, "
					+ "c.strLocName as Location_On,  r.strProdCode Product_Code, p.strPartno as Part_No,p.strProdName Product_Name "
					+ ", r.dblqty as Requestion_Qty, IFNULL(mis.dblQty, 0) MIS_Qty, r.dblqty-IFNULL(mis.dblQty, 0) as Pending_Qty "
					+ ",r.dblUnitPrice Price, ((r.dblqty-IFNULL(mis.dblQty, 0)) * r.dblUnitPrice) Value,r.strRemarks as strRemarks "
					+ ",p.strIssueUOM as IssueUOM,rh.strNarration Remarks, rh.strUserCreated User_Created "
					+ ", date(rh.dtCreatedDate) Created_Date,rh.strAuthorise Authorise, rh.intLevel Authorise_Level "
					+ "FROM tblreqdtl r LEFT OUTER JOIN (select  strProdcode,sum(IFNULL(dblQty, 0)) as dblQty,strReqCode "
					+ "from  tblmisdtl  where strClientCode='"+clientCode+"' group by strProdcode) mis "
					+ "on r.strreqcode=mis.strreqcode and mis.strprodcode=r.strprodcode "
					+ "left outer join tblproductmaster p ON r.strProdCode = p.strProdCode and p.strClientCode='"+clientCode+"'"
					+ "left outer join tblreqhd rh ON r.strReqCode = rh.strReqCode and rh.strClientCode='"+clientCode+"'"
					+ "left outer join tbllocationmaster b on  b.strLocCode=rh.strLocBy and b.strClientCode='"+clientCode+"'"
					+ "left outer join tbllocationmaster c on  c.strLocCode=rh.strLocOn and c.strClientCode='"+clientCode+"'"
					+ "WHERE (r.dblqty-IFNULL(mis.dblQty,0))>0 and r.strClientCode='"+clientCode+"' "
					+ "and rh.strLocby='"+loc1+"' and rh.strLocon='"+loc2+"' "
					+ "and date(rh.dtReqDate) between '"+fDate+"' and '"+tDate+"' and r.strClientCode='"+clientCode+"' "
					+ "Order By rh.dtReqDate, r.strReqCode, p.strProdName ";
			}
		}
		
		List list=objGlobalService.funGetList(sql,"sql");
		
		return list;
	}
	
	/**
	 * Load Location for Material Requisition 
	 * @param formName
	 * @param req
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/loadLoactionFormWise1", method = RequestMethod.GET)
	public @ResponseBody List funLoadLocation1(@RequestParam(value="formName") String formName,HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String sql="";
		if(formName.equals("frmMaterialReq"))
		{
			sql="select strLocName,strLocCode from clsLocationMasterModel "
				+ "where strType='Cost Center' and strClientCode='"+clientCode+"' order by strLocName";
		}
		else
		{
			sql="select strLocName,strLocCode from clsLocationMasterModel "
				+ "where strClientCode='"+clientCode+"' order by strLocName";
		}			
		List list=objGlobalService.funGetList(sql, "hql");
		
		return list;
	}
	
	/**
	 * Load Loaction for MIS
	 * @param formName
	 * @param req
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/loadLoactionFormWise2", method = RequestMethod.GET)
	public @ResponseBody List funLoadLocation2(@RequestParam(value="formName") String formName,HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String sql="";
		sql="select strLocName,strLocCode from clsLocationMasterModel "
			+ "where strType='Stores' and strClientCode='"+clientCode+"' order by strLocName";
		List list=objGlobalService.funGetList(sql,"hql");
		
		return list;
	}
	
	/**
	 * Export Pending Document Flash
	 * @param param1
	 * @param fDate
	 * @param tDate
	 * @param req
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/exportDataForPendingDoc", method = RequestMethod.GET)	 
	public ModelAndView funExportExcel(@RequestParam(value="param") String param1,@RequestParam(value="fDate") String fDate,@RequestParam(value="tDate") String tDate,HttpServletRequest req) 
	{
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String[] spParam=param1.split(",");
		String loc1=spParam[1];
		String loc2="";
		
		if(spParam[0].equals("frmMaterialReq"))
		{
			String[] spLoc=spParam[1].split("!");
			loc1=spLoc[0];
			loc2=spLoc[1];
		}
		String[] arrDate=fDate.split("-");
		fDate=arrDate[2]+"-"+arrDate[1]+"-"+arrDate[0];		
		arrDate=tDate.split("-");
		tDate=arrDate[2]+"-"+arrDate[1]+"-"+arrDate[0];
		
		String sql="",columnNames="";

		if(spParam[0].equals("frmPurchaseOrder"))
		{
			if(spParam[2].equals("Summary"))
			{
				columnNames="POCode,PO_Date,Delivery_Date,Supplier_Code,Supplier_Name,Against,Purchase_Indent,PO_Sub_Total,"
					+ "Tax_Amount,Final_PO_Amount,User_Code,Created_Date,Authorised,Authorised_Level";
				
				sql="select strPOCode,date(dtPODate) PO_Date,date(dtDelDate) Delivery_Date, b.strPCode Supplier_Code, "
						+ "b.strPName Supplier_Name, strAgainst, strSOCode Purchase_Indent, dblTotal PO_Sub_Total, "
						+ "ifnull(dblTaxAmt,0) Tax_Amount, dblFinalAmt Final_PO_Amount, a.strUserCreated User_Code, "
						+ "date(a.dtDateCreated) Created_Date, a.strAuthorise Authorised, a.intLevel "
						+ "from tblPurchaseOrderHd a, tblpartymaster b "
						+ "Where (strPOCode IN (select distinct a.strPOCode from tblPurchaseOrderDtl a "
						+ "left outer join (select a.strPONo POCode, b.strProdCode, sum(dblQty) POQty "
						+ "from tblgrnhd a, tblgrndtl b "
						+ "where a.strGRNCode = b.strGRNCode and strAgainst = 'Purchase Order' "
						+ "and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' Group by a.strPONo, b.strProdCode) b "
						+ "on a.strPOCode = b.POCode and a.strProdCode = b.strProdCode and a.strClientCode='"+clientCode+"'   " ;
						
						if(spParam[4].toString().equalsIgnoreCase("Close"))
						{
							sql=sql+ " where  strClosePO = 'Yes' ) ";
						}
						if(spParam[4].toString().equalsIgnoreCase("Pending"))
						{
							sql=sql+ " where a.dblOrdQty > ifnull(b.POQty,0)) and strClosePO != 'Yes' ";
						}
						if(spParam[4].toString().equalsIgnoreCase("ALL"))
						{
							sql=sql+ ") ";
						}
						sql=sql+"  ) and a.strSuppCode = b.strPCode and date(a.dtPODate) between '"+fDate+"' and '"+tDate+"' "
								+ " and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' ";
						
						if(!spParam[3].toString().equalsIgnoreCase("ALL"))
						{
							sql=sql+ " and a.strSuppCode='"+spParam[3].toString()+"' "; 
						}
			}
			else
			{
				columnNames="POCode,PO_Date,Delivery_Date,Supplier_Code,Supplier_Name,Against,Purchase_Indent,PO_Sub_Total,"
						+ "Tax_Amount,Final_PO_Amount,ProdCode,ProdName,UOM,OrderQty,Price,TotalPrice,User_Code,Receive_Qty,Bal_Qty,GRNCode"
						+ ",Created_Date,Authorised,Authorised_Level";
				
				sql="select  a.strPOCode,date(c.dtPODate) PO_Date,date(c.dtDelDate) Delivery_Date, d.strPCode Supplier_Code,d.strPName Supplier_Name, "
						+ " c.strAgainst, c.strSOCode Purchase_Indent, dblTotal PO_Sub_Total,ifnull(a.dblTaxAmt,0) Tax_Amount, dblFinalAmt Final_PO_Amount, "
						+ " e.strProdCode,e.strProdName,e.strUOM, a.dblOrdQty,a.dblPrice,left((a.dblOrdQty*a.dblPrice),7) as TotalPrice ,"
						+ " ifnull(b.POQty,0) as ReceiveQty , (a.dblOrdQty-ifnull(b.POQty,0)) as BalQty, ifnull(b.strGRNCode,'') as GRNCode, "
						+ " c.strUserCreated User_Code,date(c.dtDateCreated) Created_Date,c.strAuthorise Authorised, c.intLevel "
						+ " from tblPurchaseOrderDtl a "
						+ " left outer join (select a.strPONo POCode, b.strProdCode, sum(dblQty) POQty, b.strGRNCode "
						+ " from tblgrnhd a, tblgrndtl b where a.strGRNCode = b.strGRNCode and strAgainst = 'Purchase Order' "
						+ " and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' "
						+ " Group by a.strPONo, b.strProdCode) b on a.strPOCode = b.POCode and a.strProdCode = b.strProdCode "
						+ " left outer join tblPurchaseOrderHd c on a.strPOCode = c.strPOCode and c.strClientCode='"+clientCode+"' "
						+ " inner join tblpartymaster d on c.strSuppCode=d.strPCode and d.strClientCode='"+clientCode+"'"
						+ " inner join tblproductmaster e on a.strProdCode=e.strProdCode and e.strClientCode='"+clientCode+"' "
						+ " where  date(c.dtPODate) between '"+fDate+"' and '"+tDate+"'"
						+ " and a.strClientCode='"+clientCode+"' " ;
				
				if(spParam[4].toString().equalsIgnoreCase("Close"))
				{
					sql=sql+ " and c.strClosePO = 'Yes' ";
				}
				if(spParam[4].toString().equalsIgnoreCase("Pending"))
				{
					sql=sql+ " and a.dblOrdQty > ifnull(b.POQty,0) and  c.strClosePO != 'Yes' ";
				}
				if(!spParam[3].toString().equalsIgnoreCase("ALL"))
				{
					sql=sql+ " and c.strSuppCode='"+spParam[3].toString()+"' "; 
				}
				sql=sql+" order by a.strPOCode asc ";
			}
		}
		else if(spParam[0].equals("frmPurchaseIndent"))
		{
			if(spParam[2].equals("Summary"))
			{
				columnNames="PICode,PIDate,LocationName,Narration,TotalAmt";
				sql="select a.strPICode, date(a.dtPIDate),b.strLocName,a.strNarration,a.dblTotal "
						+ "from tblpurchaseindendhd a,tbllocationmaster b "
						+ "where strPICode IN (select a.strPICode from tblpurchaseindenddtl a left outer join "
						+ "(select b.strPICode, b.strProdCode,sum(dblOrdQty) POQty from tblpurchaseorderhd a,tblpurchaseorderdtl b "
						+ "where a.strPOCode = b.strPOCode and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' "
						+ "group by b.strPICode, b.strProdCode) as b on a.strPIcode = b.strPICode  and a.strProdCode = b.strProdCode "
						+ "where a.strClientCode='"+clientCode+"' and a.dblQty > ifnull(b.POQty,0)) "
						+ "and a.strLocCode=b.strLocCode and a.strLocCode='"+loc1+"' and a.strClientCode='"+clientCode+"' "
						+ "and b.strClientCode='"+clientCode+"' and date(a.dtPIDate) between '"+fDate+"' and '"+tDate+"'";
			}
			else
			{
				columnNames="PICode,ProdCode,ProdName,UOM,Qty,ReOrderQty,Purpose";
				sql="select a.strPICode,c.strProdCode,d.strProdName,d.strUOM,c.dblQty,c.dblReOrderQty,c.strPurpose "
						+ "from tblpurchaseindendhd a,tblpurchaseindenddtl c,tbllocationmaster b,tblproductmaster d "
						+ "where a.strPIcode=c.strPIcode and c.strProdCode=d.strProdCode "
						+ "and a.strPICode IN (select a.strPICode from tblpurchaseindenddtl a left outer join "
						+ "(select b.strPICode, b.strProdCode,sum(dblOrdQty) POQty from tblpurchaseorderhd a,tblpurchaseorderdtl b "
						+ "where a.strPOCode = b.strPOCode and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' "
						+ "group by b.strPICode, b.strProdCode) as b on a.strPIcode = b.strPICode  and a.strProdCode = b.strProdCode "
						+ "where a.strClientCode='"+clientCode+"' and a.dblQty > ifnull(b.POQty,0)) "
						+ "and a.strLocCode=b.strLocCode and a.strLocCode='"+loc1+"' and a.strClientCode='"+clientCode+"' "
						+ "and b.strClientCode='"+clientCode+"' and date(a.dtPIDate) between '"+fDate+"' and '"+tDate+"'";
			}
		}
		else
		{
			if(spParam[2].equals("Summary"))
			{
				columnNames="Requestion_Code,Requestion_Date,Location_By,Location_On,Remarks,User_Created,Created_Date"
					+ ",Authorise,Authorise_Level";
				
				sql="select a.strReqCode Requestion_Code, date(a.dtReqDate) Requestion_Date, b.strLocName as Location_By,"
						+ " c.strLocName as Location_On,a.strNarration Remarks, a.strUserCreated User_Created "
						+ ", date(a.dtCreatedDate) Created_Date,a.strAuthorise Authorise, a.intLevel Authorise_Level "
						+ " from tblreqhd a left outer join tbllocationmaster b on  b.strLocCode=a.strLocBy and b.strClientCode='"+clientCode+"'"
						+ " left outer join tbllocationmaster c on  c.strLocCode=a.strLocOn and c.strClientCode='"+clientCode+"'"
						+ " Where a.strReqCode IN (select a.strReqCode  from tblreqdtl a "
						+ " left outer join (select b.strReqCode, b.strProdCode, SUM(dblQty) ReqQty "
						+ " from tblmishd a, tblmisdtl b "
						+ " Where a.strMISCode = b.strMISCode and a.strClientCode='"+clientCode+"' and b.strClientCode='"+clientCode+"' "
						+ " group by b.strReqCode, b.strProdCode) b on a.strReqCode = b.strReqCode "
						+ " and a.strProdCode = b.strProdCode "
						+ " where  a.dblQty > ifnull(b.ReqQty,0)) "
						+ " and a.strLocby='"+loc1+"' and a.strLocon='"+loc2+"' "
						+ " and date(a.dtReqDate) between '"+fDate+"' and '"+tDate+"' and a.strClientCode='"+clientCode+"' "
						+ " order by a.dtReqDate";
			}
			else
			{
				columnNames="Requestion_Code,Requestion_Date,Location_By,Location_On,Product_Code,Part_No,Product_Name "
					+ ",Requestion_Qty,MIS_Qty,Pending_Qty ,Price,Value,Remarks,IssueUOM,Narration,User_Created"
					+ ",Created_Date,Authorise,Authorise_Level";
				
				sql="SELECT r.strReqCode Requestion_Code, date(rh.dtReqDate) Requestion_Date, b.strLocName as Location_By, "
						+ "c.strLocName as Location_On,  r.strProdCode Product_Code, p.strPartno as Part_No,p.strProdName Product_Name "
						+ ", r.dblqty as Requestion_Qty, IFNULL(mis.dblQty, 0) MIS_Qty, r.dblqty-IFNULL(mis.dblQty, 0) as Pending_Qty "
						+ ",r.dblUnitPrice Price, ((r.dblqty-IFNULL(mis.dblQty, 0)) * r.dblUnitPrice) Value,r.strRemarks as strRemarks "
						+ ",p.strIssueUOM as IssueUOM,rh.strNarration Remarks, rh.strUserCreated User_Created "
						+ ", date(rh.dtCreatedDate) Created_Date,rh.strAuthorise Authorise, rh.intLevel Authorise_Level "
						+ "FROM tblreqdtl r LEFT OUTER JOIN (select  strProdcode,sum(IFNULL(dblQty, 0)) as dblQty,strReqCode "
						+ "from  tblmisdtl  where strClientCode='"+clientCode+"' group by strProdcode) mis "
						+ "on r.strreqcode=mis.strreqcode and mis.strprodcode=r.strprodcode "
						+ "left outer join tblproductmaster p ON r.strProdCode = p.strProdCode and p.strClientCode='"+clientCode+"'"
						+ "left outer join tblreqhd rh ON r.strReqCode = rh.strReqCode and rh.strClientCode='"+clientCode+"'"
						+ "left outer join tbllocationmaster b on  b.strLocCode=rh.strLocBy and b.strClientCode='"+clientCode+"'"
						+ "left outer join tbllocationmaster c on  c.strLocCode=rh.strLocOn and c.strClientCode='"+clientCode+"'"
						+ "WHERE (r.dblqty-IFNULL(mis.dblQty,0))>0 and r.strClientCode='"+clientCode+"' "
						+ "and rh.strLocby='"+loc1+"' and rh.strLocon='"+loc2+"' "
						+ "and date(rh.dtReqDate) between '"+fDate+"' and '"+tDate+"' and r.strClientCode='"+clientCode+"' "
						+ "Order By rh.dtReqDate, r.strReqCode, p.strProdName ";
			}
		}
		
		List list=objGlobalService.funGetList(sql,"sql");
		
		
		String[] arrExcelHeader=columnNames.split(",");
		List listUDReport=new ArrayList();
		listUDReport.add(arrExcelHeader);
		List arrList = new ArrayList<String>();
		List arrList1 = new ArrayList<String>();
		
		arrList1=list;
		for(int cnt=0;cnt<arrList1.size();cnt++)
		{			
			Object[] arrObj=(Object[])arrList1.get(cnt);
			List arrListTemp=new ArrayList<String>();
			for(int cnt1=0;cnt1<arrObj.length;cnt1++)
			{
				arrListTemp.add(arrObj[cnt1]);
			}
			arrList.add(arrListTemp);
		}		
		
		listUDReport.add(arrList);
		
		return new ModelAndView("excelView", "stocklist", listUDReport);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String,String> funGetTransactionWiseLocation(String transType)
	{
		String sql_Loc="";
		
		Map<String,String> hmLocation=new HashMap<String, String>();
		if(transType.equals("Stores"))
		{
			hmLocation.put(" "," ");
			sql_Loc="select strLocCode,strLocName from tbllocationmaster "
					+ "where strType='Stores' order by strLocName";
			List listLocations=objGlobalService.funGetList(sql_Loc, "sql");
			for(int cnt=0;cnt<listLocations.size();cnt++)
			{
				Object[] arrObjLoc=(Object[])listLocations.get(cnt);
				hmLocation.put(arrObjLoc[0].toString(),arrObjLoc[1].toString());
			}
		}
		else if(transType.equals("Cost Center"))
		{
			sql_Loc="select strLocCode,strLocName from tbllocationmaster "
					+ "where strType='Cost Center' order by strLocName";
			List listLocations=objGlobalService.funGetList(sql_Loc, "sql");			
			for(int cnt=0;cnt<listLocations.size();cnt++)
			{
				Object[] arrObjLoc=(Object[])listLocations.get(cnt);
				hmLocation.put(arrObjLoc[0].toString(),arrObjLoc[1].toString());
			}
		}
		else
		{
			sql_Loc="select strLocCode,strLocName from tbllocationmaster "
					+ "order by strLocName";
			List listLocations=objGlobalService.funGetList(sql_Loc, "sql");
			for(int cnt=0;cnt<listLocations.size();cnt++)
			{
				Object[] arrObjLoc=(Object[])listLocations.get(cnt);
				hmLocation.put(arrObjLoc[0].toString(),arrObjLoc[1].toString());
			}
		}
		if(hmLocation.size()>0)
		{
			HashMap hmTempMap=(HashMap)hmLocation;
			hmLocation=clsGlobalFunctions.funSortByValues(hmTempMap);
		}
		
		return hmLocation;
	}

}
