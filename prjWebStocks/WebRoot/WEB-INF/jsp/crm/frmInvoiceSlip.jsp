<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Invoice Slip</title>
<script type="text/javascript">



$(document).ready(function() 
{

	$("#txtInvDate").datepicker({ dateFormat: 'yy-mm-dd' });
	$("#txtInvDate" ).datepicker('setDate', 'today');
	$("#txtInvDate").datepicker();
});


function funHelp()
{
	var transactionName='';
	var inv  = $('#cmbPrintFormat').val();
	if(inv=='A4')
		{
			transactionName='invoice';
		}else{
			transactionName='invoiceRetail';
		}
	fieldName = transactionName;
//	window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;top=500,left=500")
}

 	function funSetData(code)
	{ 
 		$('#txtInvCode').val(code);
	
	}
 	
 	function funCallFormAction() 
	{	
		 var code=$('#txtInvCode').val();
	     var invDate=$('#txtInvDate').val();
	     
	     var prinFormat=$('#cmbPrintFormat').val();
	     
	     var invoiceformat='<%=session.getAttribute("invoieFormat").toString()%>';
	
	if(prinFormat=='A4')
		{
			
		if(invoiceformat=="Format 1")
		{
		window.open(getContextPath()+"/openRptInvoiceSlip.html?rptInvCode="+code,'_blank');
		window.open(getContextPath()+"/openRptInvoiceProductSlip.html?rptInvCode="+code+"&rptInvDate="+invDate,'_blank');
		window.open(getContextPath()+"/rptTradingInvoiceSlip.html?rptInvCode="+code+"&rptInvDate="+invDate,'_blank');
		}
		else if(invoiceformat=="Format 2")
		{
		window.open(getContextPath()+"/rptInvoiceSlipFromat2.html?rptInvCode="+code+"&rptInvDate="+invDate,'_blank');
		window.open(getContextPath()+"/rptInvoiceSlipNonExcisableFromat2.html?rptInvCode="+code+"&rptInvDate="+invDate,'_blank');
		window.open(getContextPath()+"/rptDeliveryChallanInvoiceSlip.html?strDocCode="+dccode,'_blank');
		}
	
		else if(invoiceformat == 'Format 3 Inv Ret'){
		window.open(getContextPath()+"/openRptInvoiceRetailReport.html?rptInvCode="+code,'_blank');
	    }
		
		} 
	if(prinFormat=='40 Col')
	{
 		window.open(getContextPath()+"/opentxtInvoice.html?rptInvCode="+code,'_blank');
 	}
		
		
		
		
		
		
		
		
	else if(invoiceformat == 'Format 4 Inv Ret'){
		
	}
	/* else{
		
		window.open(getContextPath()+"/rptInvoiceSlipFromat2.html?rptInvCode="+code+"&rptInvDate="+invDate,'_blank');
		window.open(getContextPath()+"/rptInvoiceSlipNonExcisableFromat2.html?rptInvCode="+code+"&rptInvDate="+invDate,'_blank');
		
	} */
	}

</script>
<body onload="funOnLoad();">
	<div id="formHeading">
		<label>Invoice Slip</label>
	</div>

 <br/><br><br>
	<s:form name="InvoiceSlipForm" method="GET" action="">
	<div>
			<table class="masterTable">
			<tr>
	<td width="100px"><label>Invoice Code</label></td>
									<td  colspan="3"><s:input path="strInvCode" id="txtInvCode"
											ondblclick="funHelp()"
											cssClass="searchTextBox" /></td>
											
											<td width="100px"><label>Invoice Date</label>
									<td><s:input path="dteInvDate" id="txtInvDate"
											required="required" cssClass="calenderTextBox" /></td>
											
								<td width="100px"><label>Print Format</label>
									<td><select id="cmbPrintFormat" name="cmbPrintFormat"
											 Class="BoxW124px">
											<option value="A4">Invoice</option>
											<option value="40 Col">Retail</option>
										</select></td>			
</tr></table></div>
    <div align="center">
			<input type="submit" value="Submit"
				onclick="return funCallFormAction()"
				class="form_button" /> &nbsp; &nbsp; &nbsp; <input type="button"
				id="reset" name="reset" value="Reset" class="form_button" />
		</div>

</s:form>
</body>
</html>