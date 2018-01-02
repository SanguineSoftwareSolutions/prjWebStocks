<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@	taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
<script type="text/javascript">
	
	var fieldName;
	var debtorName='';
	
	$(function() 
	{
		
		$(".tab_content").hide();
		$(".tab_content:first").show();

		$("ul.tabs li").click(function() 
		{
			$("ul.tabs li").removeClass("active");
			$(this).addClass("active");
			$(".tab_content").hide();
			var activeTab = $(this).attr("data-state");
			$("#" + activeTab).fadeIn();
		});
		
		
		$("#txtVouchDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtVouchDate").datepicker('setDate', 'today');
		$("#txtChequeDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtChequeDate").datepicker('setDate', 'today');
	//	$("#txtDebtorCode").prop('disabled', true);
		
		var message='';
		var retval="";
		<%if (session.getAttribute("success") != null) 
		{
			if(session.getAttribute("successMessage") != null)
			{%>
				message='<%=session.getAttribute("successMessage").toString()%>';
			    <%
			    session.removeAttribute("successMessage");
			}
			boolean test = ((Boolean) session.getAttribute("success")).booleanValue();
			session.removeAttribute("success");
			if (test) 
			{
				%> alert("Data Save successfully\n\n"+message); <%
			}
		}%>
		
		
		var code='';
		<%
		if(null!=session.getAttribute("rptVoucherNo"))
		{%>
			code='<%=session.getAttribute("rptVoucherNo").toString()%>';
			<%session.removeAttribute("rptVoucherNo");%>
			var isOk=confirm("Do You Want to Generate Slip?");
			if(isOk)
			{
				window.open(getContextPath()+"/openRptReciptReport.html?docCode="+code,'_blank');
			}
					
			
		<%}%>
		
		
	});


	function funSetData(code){

		switch(fieldName){

			case 'ReceiptNo' : 
				funSetVouchNo(code);
				break;
				
			case 'cashBankAccNo' : 
				funSetCFCode(code);
				break;
				
			case 'accountCode' : 
				funSetAccountDetails(code);
				break;
				
			case 'debtorCode' : 
			//	funSetMemberDetails(code);
				funSetDebtorMasterData(code)
				break;
				
			case 'bankCode' : 
				funSetBankDetails(code);
				break;
		}
	}


	function funSetVouchNo(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadReceipt.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strVouchNo!="Invalid")
		    	{
		    		funFillHdData(response);
		    	}
		    	else
			    {
			    	alert("Invalid Receipt No");
			    	$("#txtVouchNo").val("");
			    	$("#txtVouchNo").focus();
			    	return false;
			    }
			},
			error : function(e){
				if (jqXHR.status === 0) {
	                alert('Not connect.n Verify Network.');
	            } else if (jqXHR.status == 404) {
	                alert('Requested page not found. [404]');
	            } else if (jqXHR.status == 500) {
	                alert('Internal Server Error [500].');
	            } else if (exception === 'parsererror') {
	                alert('Requested JSON parse failed.');
	            } else if (exception === 'timeout') {
	                alert('Time out error.');
	            } else if (exception === 'abort') {
	                alert('Ajax request aborted.');
	            } else {
	                alert('Uncaught Error.n' + jqXHR.responseText);
	            }
			}
		});
	}

	
// Fill Header Data	
	function funFillHdData(response)
	{
		$("#txtVouchNo").val(response.strVouchNo);
		$("#txtVouchDate").val(response.dteVouchDate);
		$("#txtChequeDate").val(response.dteChequeDate);
		$("#txtCFCode").val(response.strCFCode);
    	$("#lblCFDesc").text(response.strCFDesc);
    	$("#lblCFDesc").text(response.strCFDesc);
    	$("#cmbType").val(response.strType);
    	funSetTypeLabel();
    	$("#txtChequeNo").val(response.strChequeNo);
    	$("#txtAmt").val(response.dblAmt);
    	$("#txtDrawnOn").val(response.strDrawnOn);
    	$("#lblDrawnOnDesc").text(response.strBankName);
    	$("#txtBranch").val(response.strBranch);
    	$("#txtDrCr").val(response.strCrDr);
    	$("#txtReceivedFrom").val(response.strReceivedFrom);
    	$("#txtNarration").val(response.strNarration);	
    	$("#txtDebtorCode").val(response.strDebtorCode);	
    	$("#txtDebtorName").val(response.strDebtorName);	
    	
    	
		funFillDtlGrid(response.listReceiptBeanDtl,response.strDebtorCode,response.strDebtorName);
		funFillInvTable(response.listReceiptInvDtlModel)
	}
	
	
// Fill Account details and Debtor Details Grid	
	function funFillDtlGrid(resListRecDtlBean,strDebtorCode,strDebtorName)
	{
		funRemoveProductRows();
		$.each(resListRecDtlBean, function(i,item)
        {
			debtorYN=resListRecDtlBean[i].strDebtorYN;
			debtorName=strDebtorName;
			funAddDetailsRow(resListRecDtlBean[i].strAccountCode,resListRecDtlBean[i].strDebtorCode
				,resListRecDtlBean[i].strDescription,resListRecDtlBean[i].strDC,''
				,resListRecDtlBean[i].dblDebitAmt,resListRecDtlBean[i].dblCreditAmt,strDebtorName);
        });
	}
	
	// Fill Invoice details Grid	
	function funFillInvTable(resListInvDtlModel)
	{
		 funRemoveAccountRows("tblInv");
		$.each(resListInvDtlModel, function(i,item)
        {
			funLoadInvTable(resListInvDtlModel[i].strInvCode,resListInvDtlModel[i].strInvBIllNo,resListInvDtlModel[i].dblInvAmt,resListInvDtlModel[i].dteInvDate,resListInvDtlModel[i].dteBillDate,resListInvDtlModel[i].dteInvDueDate,resListInvDtlModel[i].strSelected);
        });
	}
	
	
//Delete a All record from a grid
	function funRemoveProductRows()
	{
		var table = document.getElementById("tblReceiptDetails");
		var rowCount = table.rows.length;
		while(rowCount>0)
		{
			table.deleteRow(0);
			rowCount--;
		}
	}
	
	
	
// Function to set CF Code from help	
	function funSetCFCode(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadAccontCodeAndName.html?accountCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strAccountCode=='Invalid Code')
	        	{
	        		alert("Invalid Account Code");
	        		$("#txtCFCode").val('');
	        	}
	        	else
	        	{
	        		$("#txtCFCode").val(response.strAccountCode);
		        	$("#lblCFDesc").text(response.strAccountName);
		        	$("#txtVouchDate").focus();
	        	}
			},
			error : function(e){
				if (jqXHR.status === 0) {
	                alert('Not connect.n Verify Network.');
	            } else if (jqXHR.status == 404) {
	                alert('Requested page not found. [404]');
	            } else if (jqXHR.status == 500) {
	                alert('Internal Server Error [500].');
	            } else if (exception === 'parsererror') {
	                alert('Requested JSON parse failed.');
	            } else if (exception === 'timeout') {
	                alert('Time out error.');
	            } else if (exception === 'abort') {
	                alert('Ajax request aborted.');
	            } else {
	                alert('Uncaught Error.n' + jqXHR.responseText);
	            }
			}
		});
	}


// Function to set account details from help	
	function funSetAccountDetails(code){
		
		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadAccontCodeAndName.html?accountCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strAccountCode=='Invalid Code')
	        	{
	        		alert("Invalid Account Code");
	        		$("#txtAccCode").val('');
	        	}
	        	else
	        	{
	        		$("#txtAccCode").val(response.strAccountCode);
		        	$("#txtDescription").val(response.strAccountName);
		        	if(response.strDebtor=='Yes' || response.strCreditor=='Yes')
		        	{
 		        		$("#txtDebtorCode").prop('disabled', false);
		        	}
		        	else
		        	{
		        		$("#txtDebtorCode").val('');
 		        		$("#txtDebtorCode").prop('disabled', true);
 		        		$("#txtDebtorCode").focus();
		        	}
		        //	$("#txtDebtorCode").focus();
	        	}
			},
			error : function(e){
				if (jqXHR.status === 0) {
	                alert('Not connect.n Verify Network.');
	            } else if (jqXHR.status == 404) {
	                alert('Requested page not found. [404]');
	            } else if (jqXHR.status == 500) {
	                alert('Internal Server Error [500].');
	            } else if (exception === 'parsererror') {
	                alert('Requested JSON parse failed.');
	            } else if (exception === 'timeout') {
	                alert('Time out error.');
	            } else if (exception === 'abort') {
	                alert('Ajax request aborted.');
	            } else {
	                alert('Uncaught Error.n' + jqXHR.responseText);
	            }
			}
		});
	}


// Function to set debtor details from help	
	function funSetMemberDetails(code){
		
		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadMemberDetails.html?debtorCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strAccountCode=='Invalid Code')
	        	{
	        		alert("Invalid Debtor Code");
	        		$("#txtDebtorCode").val('');
	        	}
	        	else
	        	{
	        		$("#txtDebtorCode").val(response.strDebtorCode);
	        		debtorName=response.strDebtorName;
	        	}
			},
			error : function(e){
				if (jqXHR.status === 0) {
	                alert('Not connect.n Verify Network.');
	            } else if (jqXHR.status == 404) {
	                alert('Requested page not found. [404]');
	            } else if (jqXHR.status == 500) {
	                alert('Internal Server Error [500].');
	            } else if (exception === 'parsererror') {
	                alert('Requested JSON parse failed.');
	            } else if (exception === 'timeout') {
	                alert('Time out error.');
	            } else if (exception === 'abort') {
	                alert('Ajax request aborted.');
	            } else {
	                alert('Uncaught Error.n' + jqXHR.responseText);
	            }
			}
		});
	}


// Function to set Bank details from help	
	function funSetBankDetails(code){
	
		$.ajax({
			type : "GET",
			url : getContextPath()+"/loadBankMasterData.html?bankCode="+code,
			dataType : "json",
			success : function(response){ 
				if(response.strBankCode=='Invalid Code')
	        	{
	        		alert("Invalid Bank Code");
	        		$("#txtDrawnOn").val('');
	        	}
	        	else
	        	{
	        		$("#txtDrawnOn").val(response.strBankCode);
	        		$("#lblDrawnOnDesc").text(response.strBankName);
	        		$("#txtBranch").focus();
	        	}
			},
			error : function(e){
				if (jqXHR.status === 0) {
	                alert('Not connect.n Verify Network.');
	            } else if (jqXHR.status == 404) {
	                alert('Requested page not found. [404]');
	            } else if (jqXHR.status == 500) {
	                alert('Internal Server Error [500].');
	            } else if (exception === 'parsererror') {
	                alert('Requested JSON parse failed.');
	            } else if (exception === 'timeout') {
	                alert('Time out error.');
	            } else if (exception === 'abort') {
	                alert('Ajax request aborted.');
	            } else {
	                alert('Uncaught Error.n' + jqXHR.responseText);
	            }
			}
		});
	}


// Get Detail Info From detail fields and pass them to function to add into detail grid
	function funGetDetailsRow() 
	{
		var accountCode =$("#txtAccCode").val().trim();
		var transAmt=parseFloat($("#txtAmount").val());
		
		if(accountCode=='')
		{
			alert('Select Account Code!!!');
			return;
		}
		if(transAmt<1)
		{
			alert('Enter Transaction Amt!!!');
			return;
		}
		
	    var debtorCode=$("#txtDebtorCode").val();
	    var debtorName=$("#txtDebtorName").val();
	    var description=$("#txtDescription").val();
	    var transType=$("#cmbDrCr").val();
	    var dimension=$("#cmbDimesion").val();
	    var debitAmt=0;
	    var creditAmt=0;
	    
	    if(transType=='Dr')
	    {
	    	debitAmt=parseFloat(transAmt).toFixed(maxQuantityDecimalPlaceLimit);	
	    }
	    else
	    {
	    	creditAmt=parseFloat(transAmt).toFixed(maxQuantityDecimalPlaceLimit);
	    }
	    
	    funAddDetailsRow(accountCode,debtorCode,description,transType,dimension,debitAmt,creditAmt,debtorName);
	    $("#txtDebtorCode").val('');
	}
	
	
// Function to add detail grid rows	
	function funAddDetailsRow(accountCode,debtorCode,description,transType,dimension,debitAmt,creditAmt,debtorName) 
	{
	    var table = document.getElementById("tblReceiptDetails");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"15%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strAccountCode\" id=\"strAccountCode."+(rowCount)+"\" value='"+accountCode+"' />";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"12%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strDebtorCode\" id=\"strDebtorCode."+(rowCount)+"\" value='"+debtorCode+"' />";
	    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"20%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strDescription\" id=\"strDescription."+(rowCount)+"\" value='"+description+"' />";
	    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"5%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strDC\" id=\"strDC."+(rowCount)+"\" value='"+transType+"' />";
	    row.insertCell(4).innerHTML= "<input readonly=\"readonly\" class=\"Box debitAmt\" size=\"10%\" name=\"listReceiptBeanDtl["+(rowCount)+"].dblDebitAmt\" id=\"dblDebitAmt."+(rowCount)+"\" value='"+debitAmt+"'/>";
	    row.insertCell(5).innerHTML= "<input readonly=\"readonly\" class=\"Box creditAmt\" size=\"10%\" name=\"listReceiptBeanDtl["+(rowCount)+"].dblCreditAmt\" id=\"dblCreditAmt."+(rowCount)+"\" value='"+creditAmt+"'/>";
	    row.insertCell(6).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"8%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strDimension\" id=\"strDimension."+(rowCount)+"\" value='"+dimension+"'/>";	        
	    row.insertCell(7).innerHTML= "<input type=\"button\" class=\"deletebutton\" size=\"2%\" value = \"Delete\" onClick=\"Javacsript:funDeleteRow(this)\"/>";
	    row.insertCell(8).innerHTML= "<input type=\"hidden\" readonly=\"readonly\" size=\"1%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strDebtorName\" id=\"strDebtorName."+(rowCount)+"\" value='"+debtorName+"' />";
	    
	    
	 //   $("#txtDebtorCode").prop('disabled', true);
	    funCalculateTotalAmt();
	    funResetDetailFieldsButNotAmount();
	}


//Delete a All record from a grid
	function funRemoveProductRows()
	{
		var table = document.getElementById("tblReceiptDetails");
		var rowCount = table.rows.length;
		while(rowCount>0)
		{
			table.deleteRow(0);
			rowCount--;
		}
	}
	

//Function to Delete Selected Row From Grid
	function funDeleteRow(obj)
	{
	    var index = obj.parentNode.parentNode.rowIndex;
	    var table = document.getElementById("tblReceiptDetails");
	    table.deleteRow(index);
	    funCalculateTotalAmt();
	}

	
//Calculating total amount
	function funCalculateTotalAmt()
	{
		var totalAmt=0.00;
		var totalDebitAmt=0.00;
		var totalCreditAmt=0.00;
		
		$('#tblReceiptDetails tr').each(function() 
		{
			var debitAmt=parseFloat($(this).find(".debitAmt").val());
		    totalDebitAmt = totalDebitAmt + parseFloat($(this).find(".debitAmt").val());
		    totalCreditAmt = totalCreditAmt + parseFloat($(this).find(".creditAmt").val());
		});

		totalDebitAmt=parseFloat(totalDebitAmt).toFixed(maxAmountDecimalPlaceLimit);
		totalCreditAmt=parseFloat(totalCreditAmt).toFixed(maxAmountDecimalPlaceLimit);
		
		totalAmt=totalDebitAmt-totalCreditAmt;
		totalAmt=parseFloat(totalAmt).toFixed(maxAmountDecimalPlaceLimit);
		
		$("#lblDebitAmt").text(totalDebitAmt);
		$("#lblCreditAmt").text(totalCreditAmt);
		$("#lblDiffAmt").text(totalAmt);
		$("#txtTotalAmt").val(totalAmt);
	}


// Reset Detail Fields
	function funResetDetailFields()
	{
		$("#txtAccCode").val('');
	 //   $("#txtDebtorCode").val('');
	    $("#txtDescription").val('');
	    $("#cmbDrCr").val('Dr');
	    $("#txtAmount").val('0.00');
	    $("#cmbDimesion").val('No');	    
	}
	
	function funResetDetailFieldsButNotAmount()
	{
		$("#txtAccCode").val('');
	    $("#txtDebtorCode").val('');
	    $("#txtDescription").val('');
	    $("#cmbDrCr").val('Dr');
	   
	    $("#cmbDimesion").val('No');	    
	}
	
	
// Reset Header Fields
	function funResetHeaderFields()
	{
		$("#txtVouchNo").val('');		
		$("#txtNarration").val('');
		$("#txtVouchDate").datepicker('setDate', 'today');
		$("#txtChequeDate").datepicker('setDate', 'today');
	//	$("#txtDebtorCode").prop('disabled', true);
	}


	
// Function invoked on change value event on select tag.	
	function funSetTypeLabel()
	{
		var type=$("#cmbType").val();
		switch(type)
		{
			case "Cash":
				$("#lblTypeName").text("Cash");
				break;
				
			case "Credit Card":
				$("#lblTypeName").text("Credit Card");	
				break;

			case "Cheque":
				$("#lblTypeName").text("Cheque No");
				break;
	
			case "NEFT":
				$("#lblTypeName").text("NEFT No");
				break;
		}
	}


// Function to Validate Header Fields
	function funValidateHeaderFields()
	{
		if($("#txtVouchDate").val()=='')
		{
			alert('Please Select Vouch Date!!!');
			return false;
		}
		if($("#txtCFCode").val()=='')
		{
			alert('Please Enter CF Code!!!');
			return false;
		}
		
		var amt=parseFloat($("#txtAmt").val());
		if(amt<1)
		{
			alert('Please Enter Amount!!!');
			return false;
		}
		
		var debitAmt =parseFloat($("#lblDebitAmt").text())
		var creditAmt =parseFloat($("#lblCreditAmt").text())
		if(debitAmt>0 && creditAmt>0 && (debitAmt - creditAmt)==0  )
			{
			  return true;
			}else
				{
				  alert("Balance must be Zero");
				  return false;
				}
		
		
	}


	function funHelp(transactionName)
	{
		fieldName=transactionName;
	//	window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
		window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
	
	
	function funSetDebtorMasterData(debtorCode)
	{
	   
		var searchurl=getContextPath()+"/loadSundryDebtorMasterData.html?debtorCode="+debtorCode;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strDebtorCode=='Invalid Code')
			        	{
			        		alert("Invalid Debtor Code");
			        		$("#txtDebtorCode").val('');
			        	}
			        	else
			        	{					        	    			        	    
			        	    /* Debtor Details */
			        	     funRemoveAccountRows("tblInv");
			        	    $("#txtDebtorCode").val(debtorCode);
			        	    $("#txtDebtorName").val(response.strDebtorFullName);
			        	    
			        	    $("#txtInvGetAmount").val('0.0');
			    		   $("#txtTotalInvAmount").val('0.0');
			        	    
			        	    funGetUnPayedInv(debtorCode,response.strClientCode);
			        	    
			        	
			        	}
					},
					error: function(jqXHR, exception) {
			            if (jqXHR.status === 0) {
			                alert('Not connect.n Verify Network.');
			            } else if (jqXHR.status == 404) {
			                alert('Requested page not found. [404]');
			            } else if (jqXHR.status == 500) {
			                alert('Internal Server Error [500].');
			            } else if (exception === 'parsererror') {
			                alert('Requested JSON parse failed.');
			            } else if (exception === 'timeout') {
			                alert('Time out error.');
			            } else if (exception === 'abort') {
			                alert('Ajax request aborted.');
			            } else {
			                alert('Uncaught Error.n' + jqXHR.responseText);
			            }		            
			        }
		      });
	}
	
	function funGetUnPayedInv(strDebtor,strClientCode)
	{
		var abc="";
		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadUnPayedInv.html?strDebtorCode="+strDebtor,
			dataType : "json",
			success : function(response){ 
				$.each(response, function(i, item) {
					
						funLoadInvTable(response[i].strInvCode,response[i].strInvBIllNo,response[i].dblInvAmt,response[i].dteInvDate,response[i].dteBillDate,response[i].dteInvDueDate,"No");
				});	
	        
			},
			error : function(jqXHR, exception){
				if (jqXHR.status === 0) {
	                alert('Not connect.n Verify Network.');
	            } else if (jqXHR.status == 404) {
	                alert('Requested page not found. [404]');
	            } else if (jqXHR.status == 500) {
	                alert('Internal Server Error [500].');
	            } else if (exception === 'parsererror') {
	                alert('Requested JSON parse failed.');
	            } else if (exception === 'timeout') {
	                alert('Time out error.');
	            } else if (exception === 'abort') {
	                alert('Ajax request aborted.');
	            } else {
	                alert('Uncaught Error.n' + jqXHR.responseText);
	            }
			}
		});
	}
	
	
	function funLoadInvTable(strInvCode,strInvBIllNo,dblInvAmt,dteInvDate,dteBillDate,dteInvDueDate,isSelected)
	{
		var totalInvAmt = parseFloat(dblInvAmt);
		 var table = document.getElementById("tblInv");
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    if(isSelected=="Tick")
		    	{
		    	 row.insertCell(0).innerHTML= "<input id=\"cbInvSel."+(rowCount)+"\" name=\"listReceiptInvDtlModel["+(rowCount)+"].strSelected\" type=\"checkbox\" class=\"PropCheckBoxClass\" checked=\"checked\"   value='Tick' onclick=\"funInvChkOnClick()\" />";
		    	}else
		    	{
		    		 row.insertCell(0).innerHTML= "<input id=\"cbInvSel."+(rowCount)+"\" name=\"listReceiptInvDtlModel["+(rowCount)+"].strSelected\" type=\"checkbox\" class=\"PropCheckBoxClass\"   value='Tick' onclick=\"funInvChkOnClick()\" />";	
		    	}
		   
		    row.insertCell(1).innerHTML= "<input class=\"Box\" readonly = \"readonly\" size=\"24%\" name=\"listReceiptInvDtlModel["+(rowCount)+"].strInvCode\" id=\"txtInvCode."+(rowCount)+"\" value='"+strInvCode+"' >";
		    row.insertCell(2).innerHTML= "<input class=\"Box\" readonly = \"readonly\" size=\"8%\" name=\"listReceiptInvDtlModel["+(rowCount)+"].strInvBIllNo\"  id=\"txtInvBIllNo."+(rowCount)+"\" value='"+strInvBIllNo+"'>";		    	    
		    row.insertCell(3).innerHTML= "<input class=\"Box totalinvAmtCell\" size=\"8%\" readonly = \"readonly\" style=\"text-align: right;\" size=\"8%\" name=\"listReceiptInvDtlModel["+(rowCount)+"].dblInvAmt\" id=\"txtInvAmt."+(rowCount)+"\" value='"+dblInvAmt+"'>";
		    row.insertCell(4).innerHTML= "<input class=\"Box\"  readonly = \"readonly\"  size=\"10%\" name=\"listReceiptInvDtlModel["+(rowCount)+"].dteInvDate\" id=\"txtInvDate."+(rowCount)+"\" value="+dteInvDate+">";
		    row.insertCell(5).innerHTML= "<input class=\"Box\"  readonly = \"readonly\"  size=\"10%\" name=\"listReceiptInvDtlModel["+(rowCount)+"].dteBillDate\" id=\"txtBillDate."+(rowCount)+"\" value="+dteBillDate+">";		    
		    row.insertCell(6).innerHTML= "<input class=\"Box\" readonly = \"readonly\"  size=\"10%\" name=\"listReceiptInvDtlModel["+(rowCount)+"].dteInvDueDate\" id=\"txtInvDueDate."+(rowCount)+"\" value="+dteInvDueDate+">";
		    row.insertCell(7).innerHTML= "<input class=\"text\" required = \"required\" style=\"text-align: right;\" size=\"8%\" name=\"listReceiptInvDtlModel["+(rowCount)+"].dblPayedAmt\" id=\"txtPayedAmt."+(rowCount)+"\" value='0' onblur=\"funUpdateAmount(this);\" >";
	
		    var invAmt =$("#txtTotalInvAmount").val()
		   	totalInvAmt= parseFloat(invAmt) + totalInvAmt;
		     $("#txtTotalInvAmount").val(totalInvAmt );
	
	
	}
	
	
	function funInvChkOnClick()
	{
		var table = document.getElementById("tblInv");
	    var rowCount = table.rows.length;  
	    var strInvCodes="";
	    var invAmt=0.0;
	    for(no=0;no<rowCount;no++)
	    {
	        if(document.all("cbInvSel."+no).checked==true)
	        	{
	        		if(strInvCodes.length>0)
	        			{
	        				strInvCodes=strInvCodes+","+document.all("txtInvCode."+no).value;
	        				var tempAmt = document.all("txtInvAmt."+no).value;
	        				invAmt=parseFloat(invAmt)+parseFloat(tempAmt);
	        				document.all("txtPayedAmt."+no).value=tempAmt;
	        			    
	        			}
	        		else
	        			{
	        				strInvCodes=document.all("txtInvCode."+no).value;
	        				var tempAmt = document.all("txtInvAmt."+no).value;
	        				document.all("txtPayedAmt."+no).value=tempAmt;
	        				invAmt=parseFloat(tempAmt)
	        			}
	        	}
	    }
	    $("#txtAmt").val(invAmt);
	    $("#txtAmount").val(invAmt);
	 	   
	}
	
	function funUpdateAmount(object)
	{
		var table = document.getElementById("tblInv");
	    var rowCount = table.rows.length;  
	    var strInvCodes="";
	    var invAmt=0.0;
	    var actualpayedAmt=0.0;
	    
	    for(no=0;no<rowCount;no++)
	    {
	        if(document.all("cbInvSel."+no).checked==true)
	        	{
	        		if(strInvCodes.length>0)
	        			{
	        				strInvCodes=strInvCodes+","+document.all("txtInvCode."+no).value;
	        				var tempAmt = document.all("txtPayedAmt."+no).value;
		        			actualpayedAmt=parseFloat(actualpayedAmt)+parseFloat(tempAmt);
	        			}
	        		else
	        			{
	        				strInvCodes=document.all("txtInvCode."+no).value;
	        				var tempAmt = document.all("txtPayedAmt."+no).value;
    						actualpayedAmt=parseFloat(actualpayedAmt)+parseFloat(tempAmt);
	        			}
	        	}
	    }
	    $("#txtAmt").val(actualpayedAmt);
	    $("#txtAmount").val(actualpayedAmt);
	    $("#hidInvYN").val("Y");
	   
	}
	
	function funTotalCrInv()
	{
		var finalCr=0.00;
		var table = document.getElementById("tblInv");
	    var rowCount = table.rows.length; 
		var no=0;
				$('#tblInv tr').each(function() {

					if(document.all("cbInvSel."+no).checked==true)
			    	{
				    var totalCrCell = $(this).find(".totalInvAmtCell").val();
				    finalCr+=parseFloat(totalCrCell);
			    	}
					no++;
				 });
	    	
		$("#txtAmt").val(finalCr);
		$("#txtAmount").val(finalCr);
	}
	
	function funRemoveAccountRows(tblID)
	{
		var table = document.getElementById(tblID);
		var rowCount = table.rows.length;
		while(rowCount>0)
		{
			table.deleteRow(0);
			rowCount--;
		}
	}
	
	
	 function funSetInvPayAmt()
		{
			var table = document.getElementById("tblInv");
		    var rowCount = table.rows.length; 
		    var payInvAmt = $("#txtInvGetAmount").val();
		    var totalInvAmt = $("#txtTotalInvAmount").val();
		    
		     		    
		    
		    $("#txtAmt").val(payInvAmt);
			$("#txtAmount").val(payInvAmt);
		    
		    
		    var restAmt =parseFloat(payInvAmt);
		      
		    for(no=0;no<rowCount;no++)
		    {
		    	var invCrCell = document.all("txtInvAmt."+no).value;
		    	restAmt = parseFloat(restAmt) - parseFloat(invCrCell);
		    	
		    	if(restAmt>0)
		    		{
		    		$('#tblInv tr').each(function() {
			    		document.getElementById("txtPayedAmt."+no).value=invCrCell;
			    		document.getElementById("cbInvSel."+no).checked=true;
		    		});
		    		}
		    	if(restAmt<0)
		    		{
		    		$('#tblInv tr').each(function() {
			    		document.getElementById("txtPayedAmt."+no).value=restAmt+parseFloat(invCrCell);
			    		document.getElementById("cbInvSel."+no).checked=true;
		    		});
		    		break;
		    		}
		    	
		    	
		    }
			
		}
	
	
	
</script>

</head>
<body>

	<div id="formHeading">
	<label>Receipt</label>
	</div>

<br/>
<br/>

	<s:form name="Receipt" method="POST" action="saveReceipt.html">

		<table class="transTable">
			<tr>
				<td>
					<label>Vouch No</label>
				</td>
				<td>
					<s:input  type="text" id="txtVouchNo" path="strVouchNo" cssClass="searchTextBox" ondblclick="funHelp('ReceiptNo');"/>
				</td>
				<td></td>
				<td>
					<label>Vouch Date</label>
				</td>
				<td>
					<s:input  type="text" id="txtVouchDate" path="dteVouchDate" cssClass="calenderTextBox" />
				</td>
			</tr>
			
			<tr>
				<td>
					<label>CF Code</label>
				</td>
				<td>
					<s:input   type="text" id="txtCFCode" path="strCFCode" cssClass="searchTextBox" ondblclick="funHelp('cashBankAccNo');"/>
				</td>
				<td><label id="lblCFDesc"></label></td>
				
				<td>
					<label>Cheque Date</label>
				</td>
				<td>
					<s:input  type="text" id="txtChequeDate" path="dteChequeDate" cssClass="calenderTextBox" />
				</td>				
			</tr>
			
			<tr>
				<td>
					<label>Type</label>
				</td>
				<td>
					<!-- <s:select id="txtType" path="strType" cssClass="BoxW124px"/> -->
					<s:select id="cmbType" path="strType" class="BoxW124px" onchange="funSetTypeLabel()">
						  <option value="Cash">Cash</option>
						  <option value="Cheque">Cheque</option>
						  <option value="Credit Card">Credit Card</option>
						  <option value="NEFT">NEFT</option>
					</s:select>
				</td>
			
				<td><label id="lblTypeName"></label></td>
				
				<td colspan="2">
					<s:input  type="text" id="txtChequeNo" path="strChequeNo" cssClass="remarkTextBox" />
				</td>
			</tr>
			
			<tr>
				<td>
					<label>Amt</label>
				</td>
				<td>
					<s:input  type="number" step="0.01" id="txtAmt" path="dblAmt" class="decimal-places numberField" />
				</td>
			
				<td>
					<label>Drawn On</label>
				</td>
				<td>
					<s:input type="text" id="txtDrawnOn" path="strDrawnOn" cssClass="searchTextBox" ondblclick="funHelp('bankCode');"/>
				</td>
				<td><label id="lblDrawnOnDesc"></label></td>
			</tr>
			
			<tr>
				<td>
					<label>Branch</label>
				</td>
				<td>
					<s:input  type="text" id="txtBranch" path="strBranch" cssClass="longTextBox" />
				</td>
			
				<td>
					<label>CrDr</label>
				</td>
				<td colspan="2">
					<input type="text" readonly="readonly" id="txtDrCr" value="Dr" class="longTextBox" />					
				</td>
			</tr>
			
			<tr>
				<td>
					<label>Received From</label>
				</td>
				<td>
					<s:input  type="text" id="txtReceivedFrom" path="strReceivedFrom" cssClass="remarkTextBox" />
				</td>
				
				<td>
					<label>Narration</label>
				</td>
				<td colspan="2">
					<s:textarea id="txtNarration" path="strNarration" />
				</td>
			</tr>
			
			
		</table>

		<br>
		<br>
		
		<table class="transTable">
		</table>
<div >
	<ul class="tabs">
									<li class="active" data-state="tab1"
										style="width: 100px; padding-left: 55px">Details</li>
									<li data-state="tab2" style="width: 100px; padding-left: 55px">Invoice</li>
								</ul>
		<div id="tab1" class="tab_content" style="height: 470px">


	
		<table class="transTable">
				
			<tr>
				<td width="10%"><label>Account Code</label></td>
				<td width="20%"><input id="txtAccCode" name="txtAccCode" ondblclick="funHelp('accountCode')" class="searchTextBox" /></td>
			
					<td width="10%">Debtor Code</td>
						<td ><s:input id="txtDebtorCode" readonly="readonly"  name="txtDebtorCode" ondblclick="funHelp('debtorCode')" class="searchTextBox" path="strDebtorCode"/></td>
					<td colspan="2"><s:input id="txtDebtorName" name="txtDebtorName" readonly="true" cssClass="longTextBox" path="strDebtorName"></s:input></td>
			</tr>
			<tr>
				
				<td width="10%">Description</td>
				<td colspan="5"><input id="txtDescription" name="txtDescription" class="remarkTextBox" /></td>
				
			</tr>
			
			<tr>
				<td><label>Dr/Cr</label>
				<td width="5%">
					 <select id="cmbDrCr"  class="BoxW124px">
						  <option value="Dr">Dr</option>
						  <option value="Cr">Cr</option>
					</select>
				</td>
				
				<td width="10%"><label>Amount</label></td>
				<td><input type="text" id="txtAmount" value="0.00" class="decimal-places numberField"/></td>
				
				<td width="10%"><label>Dimension</label></td>
				<td width="5%">
					 <select id="cmbDimesion" class="BoxW124px">
						  <option value="No">No</option>
						  <option value="Yes">Yes</option>
					</select>
				</td>
			</tr>
			
			<tr>
				<td colspan="6">
					<input type="Button" value="Add" onclick="return funGetDetailsRow()" class="smallButton" />
				</td>
			</tr>
		</table>
	

	<div class="dynamicTableContainer" style="height: 300px;">
		<table
			style="height: 28px; border: #0F0; width: 100%; font-size: 11px; font-weight: bold;">
			<tr bgcolor="#72BEFC">
				<td style="width:9%;">Account Code</td>
				<td style="width:12%;">Debtor Code</td>
				<td style="width:20%;">Description</td>
				<td style="width:3%;">D/C</td>
				<td style="width:5%;">Debit Amt</td>
				<td style="width:5%;">Credit Amt</td>
				<td style="width:15%;">Dimension</td>
				<td style="width:2%;"></td>
			</tr>
		</table>
		
		<div style="background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
			<table id="tblReceiptDetails"
				style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll"
				class="transTablex col8-center">
				<tbody>
					<col style="width=15%">
					<col style="width:18%">
					<col style="width:29%">
					<col style="width:5%">
					<col style="width:7%">
					<col style="width:7%">
					<col style="width:8%">
					<col style="width:2%">
					<col style="display:none;">
				</tbody>
			</table>
		</div>
	</div>

	<div >
			
		<table class="transTable">
			<tr>
				<td width="2%"><label>Difference</label></td>
				<td><label id ="lblDiffAmt">0.00</label></td>
				<td><label>Total</label></td>
				<td><label id ="lblDebitAmt">0.00</label></td>
				<td><label id ="lblCreditAmt">0.00</label></td>
			</tr>
			
			<tr>
				<td>
					<s:input type="hidden" id="txtTotalAmt" path="dblAmt"  readonly="true" cssClass="decimal-places-amt numberField"/>
				</td>
			</tr>
		</table>
	</div>	
		
	</div>	
		
		<div id="tab2" class="tab_content" style="height: 470px">
		<table class="transTable">
		<tr>
		<td><label>Total Invoice Amount</label></td>
		<td><input type="text" id="txtTotalInvAmount" readonly="readonly" value="0.00" class="decimal-places numberField"/></td>
		
		
		<td><label>Get Amount</label></td>
		<td><input type="text" id="txtInvGetAmount" value="0.00" class="decimal-places numberField"/></td>
		
		
		<td><input type="Button" value="Add" onclick="funSetInvPayAmt()" class="smallButton" /></td>
		
		</tr>
		
		
		
		<tr><td colspan="5">
		<div style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 350px; overflow-x: hidden; overflow-y: scroll;">
		
									<table id="" class="masterTable"
										style="width: 100%; border-collapse: separate;">
										<thead>
											<tr bgcolor="#72BEFC">
												<td width="5%"><input type="checkbox" checked="checked" 
												id="chkInvALL"/>Select</td>
												<td width="18%">Inv Code</td>
												<td width="8%">Bill No</td>
												<td width="8%">Amount</td>
												<td width="10%">Inv Date</td>
												<td width="10%">Bill Date</td>
												<td width="10%">Due Date</td>
												<td width="10%">Paying Amt</td>
		
											</tr>
										</thead>
									</table>
									<table id="tblInv" class="masterTable"
										style="width: 100%; border-collapse: separate;">
		
										<tr bgcolor="#72BEFC">
											
		
										</tr>
									</table>
								</div></td>
								</tr>
			</table>					
		
		</div>
		
		
		
		
	</div>
<input type="hidden" id="hidInvYN" value="N"></input>
		<br />
		<p align="center">
			<input type="submit" value="Submit" onclick="return funValidateHeaderFields()" tabindex="3" class="form_button" />
			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/>
		</p>
 
<br />
	<br /><br />
	<br />
	</s:form>
</body>
</html>
