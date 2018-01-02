package com.sanguine.webbooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webbooks.bean.clsDebtorLedgerBean;
import com.sanguine.webbooks.model.clsDebtorMaster;
import com.sanguine.webbooks.model.clsJVDebtorDtlModel;
import com.sanguine.webbooks.service.clsDebtorLedgerService;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class clsCreditorLedgerController{
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	clsDebtorLedgerService objDebtorLedgerService;
	
	@Autowired
	clsPaymentController objPaymentController;
	
	@Autowired
	clsJVController objJVController;
	
	@Autowired
	private clsGlobalFunctions objclsGlobalFunctions;
	
	@Autowired
	private clsReceiptController objclsReceiptController;
	
	
	
//Open Debtor Ledger
	@RequestMapping(value = "/frmCreditorLedger", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") clsDebtorLedgerBean objBean,BindingResult result,Map<String,Object> model,HttpServletRequest req)
	{
		objDebtorLedgerService.funGetDebtorDetails("M000002", "060.001","02");
		ModelAndView objModelAndView=new ModelAndView();
		return objModelAndView;
	}
	

// Process and Show Creditor Ledger
	@RequestMapping(value = "/showCreditorLedger", method = RequestMethod.POST)
	public ModelAndView funShowDebtorLedger(@ModelAttribute("command") @Valid clsDebtorLedgerBean objBean ,BindingResult result,HttpServletRequest req){
		if(!result.hasErrors()){
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String userCode=req.getSession().getAttribute("usercode").toString();
			String propertyCode=req.getSession().getAttribute("propertyCode").toString();
			return new ModelAndView("redirect:/frmDebtorLedger.html");
		}
		else{
			return new ModelAndView("frmCreditorLedger");
		}
	}
	
	
// Get Creditor Details 	
	@RequestMapping(value = "/getCreditorLedger", method = RequestMethod.GET)
	public @ResponseBody List funLoadMemberDetails(@RequestParam(value="param1") String param1,
			@RequestParam(value="fDate") String fDate,@RequestParam(value="tDate") String tDate,
			HttpServletRequest req,HttpServletResponse resp)
	{
		List retList= new ArrayList();
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String propertyCode=req.getSession().getAttribute("propertyCode").toString();
		String startDate=req.getSession().getAttribute("startDate").toString();
		objGlobal=new clsGlobalFunctions();
		String userCode=req.getSession().getAttribute("usercode").toString();
		String[] spParam1=param1.split(",");
		String glCode=spParam1[0];
		String creditorCode=spParam1[1];
		String prodCode=spParam1[2];
		String fromDate=objGlobal.funGetDate("yyyy-MM-dd",fDate);
		String toDate=objGlobal.funGetDate("yyyy-MM-dd",tDate);
		
		startDate=startDate.split("/")[2]+"-"+startDate.split("/")[1]+"-"+startDate.split("/")[0];
		
		
		objclsGlobalFunctions.funInvokeWebBookLedger(glCode, creditorCode, startDate, propertyCode, fromDate, toDate, clientCode, userCode, req, resp,"Creditor");
		
		/*String sql=" select DATE_FORMAT(DATE(TransDate),'%d-%m-%Y'),TransName,RefNo,Chq_BillNo,BillDate,Dr,Cr from ( ";
			   sql+="  select a.dteVouchDate TransDate,'1' TransType,'JV' TransName,a.strVouchNo RefNo,c.strInvoiceNo Chq_BillNo,DATE_FORMAT(DATE(c.dteInvoiceDate),'%d-%m-%Y') BillDate,b.dblDrAmt Dr,b.dblCrAmt Cr "
			   		+ " from tbljvhd a, tbljvdtl b,tbljvdebtordtl c "
			   		+ " where a.strVouchNo=b.strVouchNo and a.strVouchNo=c.strVouchNo "
			   		+ " and date(a.dteVouchDate) between '"+fromDate+"' and '"+toDate+"' "
			   		+ " and b.strAccountCode='"+glCode+"'and c.strDebtorCode='"+creditorCode+"' and a.strPropertyCode=b.strPropertyCode "
			   		+ " and a.strPropertyCode='"+propertyCode+"' and a.strClientCode='"+clientCode+"' "
			   	+  " Union all "
			   	+ " select a.dteVouchDate TransDate,'2' TransType,'Payment' TransName,a.strVouchNo RefNo,a.strChequeNo Chq_BillNo,DATE_FORMAT(DATE(a.dteChequeDate),'%d-%m-%Y')  BillDate,b.dblDrAmt Dr, b.dblCrAmt Cr "
			   	+ " from tblpaymenthd a, tblpaymentdtl b "
			   	+ " where a.strVouchNo=b.strVouchNo "
			   	+ " and date(a.dteVouchDate) between '"+fromDate+"' and '"+toDate+"' "
			   	+ " AND b.strAccountCode ='"+glCode+"' and b.strDebtorCode='"+creditorCode+"' and a.strPropertyCode=b.strPropertyCode "
			   	+ " and a.strPropertyCode='"+propertyCode+"' and a.strClientCode='"+clientCode+"' "
			   	+ " ) ledger "
			   	+ " order by TransDate, TransType "; */
		
		String hql= "  from clsLedgerSummaryModel a where a.strUserCode='"+userCode+"' and a.strPropertyCode='"+propertyCode+"' and a.strClientCode='"+clientCode+"' order by a.strTransTypeForOrderBy desc ";
		
		List listBillLedger=objGlobalFunctionsService.funGetListModuleWise(hql,"hql");	   
		
		return listBillLedger;
	}
	
	@RequestMapping(value = "/openSlipLedger", method = RequestMethod.GET)	
	private void funCallJsperReport(@RequestParam("docCode")  String docCode, HttpServletResponse resp,HttpServletRequest req)
	{		
		String[] sp=docCode.split(",");
		docCode=sp[0];
		
		switch(sp[1])
		{
			case "Payment":
				objPaymentController.funCallPaymentdtlReport(docCode,"pdf",resp,req);
				break;
				
			case "JV":
				objJVController.funCallJVdtlReport(docCode,"pdf",resp,req);
				break;	
				
			case "Recepit":
				objclsReceiptController.funCallReciptdtlReport(docCode,"pdf",resp,req);
				break;	
				
		}
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/frmExportLedger", method = RequestMethod.GET)	
	private ModelAndView funExportLedger(@RequestParam(value="param1") String param1,
			@RequestParam(value="fDate") String fDate,@RequestParam(value="tDate") String tDate,
			HttpServletRequest req,HttpServletResponse resp)
	{
		
		
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String userCode=req.getSession().getAttribute("usercode").toString();
		String[] spParam1=param1.split(",");
		String glCode=spParam1[0];
		String creditorCode=spParam1[1];
		String propertyCode=req.getSession().getAttribute("propertyCode").toString();
		
		String fromDate=objGlobal.funGetDate("yyyy-MM-dd",fDate);
		String toDate=objGlobal.funGetDate("yyyy-MM-dd",tDate);
		List listLedger=new ArrayList();
		listLedger.add("Ledger_"+fromDate+"to"+toDate+"_"+userCode);
		String[] ExcelHeader={"Transaction Date","Transaction Type","Ref No","Chq/BillNo","Bill Date","Cr","Dr","Balance"};
		listLedger.add(ExcelHeader);
		
		String sql = " SELECT dteVochDate,strTransType,strVoucherNo,strChequeBillNo,dteBillDate,dblDebitAmt,dblCreditAmt,dblBalanceAmt "
        		+ " from tblledgersummary where strUserCode='"+userCode+"' and strPropertyCode='"+propertyCode+"' AND strClientCode='"+clientCode+"'  "
        		+ " order by strTransTypeForOrderBy desc ";
		
		/*String sql=" select DATE_FORMAT(DATE(TransDate),'%d-%m-%Y'),TransName,RefNo,Chq_BillNo,BillDate,Dr,Cr from ( ";
		 sql+="  select a.dteVouchDate TransDate,'1' TransType,'JV' TransName,a.strVouchNo RefNo,c.strInvoiceNo Chq_BillNo,DATE_FORMAT(DATE(c.dteInvoiceDate),'%d-%m-%Y') BillDate,b.dblDrAmt Dr,b.dblCrAmt Cr "
			   		+ " from tbljvhd a, tbljvdtl b,tbljvdebtordtl c "
			   		+ " where a.strVouchNo=b.strVouchNo and a.strVouchNo=c.strVouchNo "
			   		+ " and date(a.dteVouchDate) between '"+fromDate+"' and '"+toDate+"' "
			   		+ " and b.strAccountCode='"+glCode+"'and c.strDebtorCode='"+creditorCode+"' and a.strPropertyCode=b.strPropertyCode "
			   		+ " and a.strPropertyCode='"+propertyCode+"' and a.strClientCode='"+clientCode+"' "
			   	+  " Union all "
			   	+ " select a.dteVouchDate TransDate,'2' TransType,'Payment' TransName,a.strVouchNo RefNo,a.strChequeNo Chq_BillNo,DATE_FORMAT(DATE(a.dteChequeDate),'%d-%m-%Y')  BillDate,b.dblDrAmt Dr, b.dblCrAmt Cr "
			   	+ " from tblpaymenthd a, tblpaymentdtl b "
			   	+ " where a.strVouchNo=b.strVouchNo "
			   	+ " and date(a.dteVouchDate) between '"+fromDate+"' and '"+toDate+"' "
			   	+ " AND b.strAccountCode ='"+glCode+"' and b.strDebtorCode='"+creditorCode+"' and a.strPropertyCode=b.strPropertyCode "
			   	+ " and a.strPropertyCode='"+propertyCode+"' and a.strClientCode='"+clientCode+"' "
			   	+ " ) ledger "
			   	+ " order by TransDate, TransType "; */
	
		   List listBillLedger=objGlobalFunctionsService.funGetListModuleWise(sql,"sql");
		   List objList = new ArrayList();
		   double bal=0.00;
		   for(int i=0;i<listBillLedger.size();i++)
		   {
			   Object[] obj=(Object[])listBillLedger.get(i);
			   bal=bal+Double.parseDouble(obj[5].toString())-Double.parseDouble(obj[6].toString());
			   List listemp = new ArrayList();;
			   listemp.add(obj[0]);
			   listemp.add(obj[1]);
			   listemp.add(obj[2]);
			   listemp.add(obj[3]);
			   listemp.add(obj[4]);
			   listemp.add(obj[5]);
			   listemp.add(obj[6]);
			   listemp.add(bal);
			   objList.add(listemp);
		   }
		  listLedger.add(objList);
		
		return new ModelAndView("excelViewWithReportName", "listWithReportName", listLedger);
	}
	
	
}
