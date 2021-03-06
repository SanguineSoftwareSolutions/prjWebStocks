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
		var startDate="${startDate}";
		var arr = startDate.split("/");
		Dat=arr[0]+"-"+arr[1]+"-"+arr[2];
		$( "#txtFromDate" ).datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtFromDate" ).datepicker('setDate', Dat);
		$("#txtToDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtToDate").datepicker('setDate', 'today');
		
		$("#btnExecute").click(function( event )
				{
			 		var propCode='<%=session.getAttribute("propertyCode").toString()%>';
					if($("#txtGLCode").val()=='')
					{
						alert("Enter GL Code!");
						return false;
					}
					if($("#txtFromDebtorCode").val()=='')
					{
						alert("Enter Creditor Code!");
						return false;
					}
					
					var fromDate=$("#txtFromDate").val();
					var toDate=$("#txtToDate").val();
					var table = document.getElementById("tblCreditorLedgerBill");
					var rowCount = table.rows.length;
					while(rowCount>0)
					{
						table.deleteRow(0);
						rowCount--;
					}
					
					var table1 = document.getElementById("tblCreditorLedgerBillTot");
					var rowCount1 = table1.rows.length;
					while(rowCount1>0)
					{
						table1.deleteRow(0);
						rowCount1--;
					}
					var glCode = $("#txtGLCode").val();
					var creditorCode =$("#txtFromDebtorCode").val()
					funGetCreditorLedger(fromDate,toDate,glCode,creditorCode,propCode);
					
				});
		
		$("#btnExport").click(
				function() {
					
					var fromDate = $("#txtFromDate").val();
					var toDate = $("#txtToDate").val();
					var glCode = $("#txtGLCode").val();
					var creditorCode = $("#txtFromDebtorCode").val();
					var prodCode = $("#txtProdCode").val();
					var qtyWithUOM = $("#cmbQtyWithUOM").val();
					var param1=glCode+","+creditorCode;
					window.location.href = getContextPath()
							+ "/frmExportLedger.html?param1="
							+ param1 + "&fDate=" + fromDate
							+ "&tDate=" + toDate;
				});
		
	});
	
	function funGetCreditorLedger(fromDate,toDate,glCode,creditorCode,propCode)
	{
		
		var param1=glCode+","+creditorCode+","+propCode;
		var searchUrl=getContextPath()+"/getCreditorLedger.html?param1="+param1+"&fDate="+fromDate+"&tDate="+toDate;
		
		$.ajax({
		        type: "GET",
		        url: searchUrl,
			    dataType: "json",
			    success: function(response)
			    {
			    	funShowCreditorLedger(response);
			    },
				error: function(e)
			    {
			       	alert('Error:=' + e);
			    }
		      });
	}
	
	
	function funShowCreditorLedger(response)
	{
		var table = document.getElementById("tblCreditorLedgerBill");
		var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
		row.insertCell(0).innerHTML= "<label>Transaction Date</label>";
		row.insertCell(1).innerHTML= "<label>Transaction Type</label>";
		row.insertCell(2).innerHTML= "<label>Ref No</label>";
		row.insertCell(3).innerHTML= "<label>Chq/BillNo</label>";
		row.insertCell(4).innerHTML= "<label>BillDate</label>";
		row.insertCell(5).innerHTML= "<label>Cr</label>";
		row.insertCell(6).innerHTML= "<label>Dr</label>";
		row.insertCell(7).innerHTML= "<label>Bal</label>";
		
		rowCount=rowCount+1;
		//var records = [];
		var bal=0.00;
		var cr=0.00;
		var dr=0.00;
		$.each(response, function(i,item)
		{
			var row1 = table.insertRow(rowCount);
			if(item[2]!='')
			{
				/* cr=cr+parseFloat(item[5]);
				dr=dr+parseFloat(item[6]);
				bal=bal+parseFloat(item[5])-parseFloat(item[6]);
				
				row1.insertCell(0).innerHTML= "<label>"+item[0]+"</label>";
				row1.insertCell(1).innerHTML= "<label>"+item[1]+"</label>";
				row1.insertCell(2).innerHTML= "<a id=\"urlDocCode\" href=\"openSlipLedger.html?docCode="+item[2]+","+item[1]+"\" target=\"_blank\" >"+item[2]+"</a>";
				row1.insertCell(3).innerHTML= "<label>"+item[3]+"</label>";
				row1.insertCell(4).innerHTML= "<label>"+item[4]+"</label>";
				row1.insertCell(5).innerHTML= "<label>"+item[5].toFixed(maxQuantityDecimalPlaceLimit)+"</label>";
				row1.insertCell(6).innerHTML= "<label>"+item[6].toFixed(maxQuantityDecimalPlaceLimit)+"</label>";
				row1.insertCell(7).innerHTML= "<label>"+bal.toFixed(maxQuantityDecimalPlaceLimit)+"</label>"; */
				
				
				cr=cr+parseFloat(item.dblCreditAmt);
				dr=dr+parseFloat(item.dblDebitAmt);
				bal=bal+parseFloat(item.dblCreditAmt)-parseFloat(item.dblDebitAmt);
				
				row1.insertCell(0).innerHTML= "<label>"+item.dteVochDate+"</label>";
				row1.insertCell(1).innerHTML= "<label>"+item.strTransType+"</label>";
				row1.insertCell(2).innerHTML= "<a id=\"urlDocCode\" href=\"openSlipLedger.html?docCode="+item.strVoucherNo+","+item.strTransType+"\" target=\"_blank\" >"+item.strVoucherNo+"</a>";
				row1.insertCell(3).innerHTML= "<label>"+item.strChequeBillNo+"</label>";
				row1.insertCell(4).innerHTML= "<label>"+item.dteBillDate+"</label>";
				row1.insertCell(5).innerHTML= "<label>"+(item.dblDebitAmt).toFixed(maxQuantityDecimalPlaceLimit)+"</label>";
				row1.insertCell(6).innerHTML= "<label>"+(item.dblCreditAmt).toFixed(maxQuantityDecimalPlaceLimit)+"</label>";
				row1.insertCell(7).innerHTML= "<label>"+bal.toFixed(maxQuantityDecimalPlaceLimit)+"</label>";
				
				
			}	
		});
		
		var table = document.getElementById("tblCreditorLedgerBillTot");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		row.insertCell(0).innerHTML = "<label>Transaction Type</label>";
		row.insertCell(1).innerHTML = "<label>Amount</label>";
		rowCount = rowCount + 1;
	
		var row1 = table.insertRow(rowCount);
		
		row1 = table.insertRow(rowCount);
		row1.insertCell(0).innerHTML = "<label>Total Credit</label>";
		row1.insertCell(1).innerHTML = "<label>"+ parseFloat(cr).toFixed(maxQuantityDecimalPlaceLimit)+ "</label>";

		rowCount = rowCount + 1;
		row1 = table.insertRow(rowCount);
		row1.insertCell(0).innerHTML = "<label>Total Debit</label>";
		row1.insertCell(1).innerHTML = "<label>"+ parseFloat(dr).toFixed(maxQuantityDecimalPlaceLimit) + "</label>";

		rowCount = rowCount + 1;
		row1 = table.insertRow(rowCount);
		row1.insertCell(0).innerHTML = "<label>Closing Balance</label>";
		row1.insertCell(1).innerHTML = "<label>"+ parseFloat(bal).toFixed(maxQuantityDecimalPlaceLimit) + "</label>";
	
		
		

	}
	function funSetData(code){

		switch(fieldName){

			case 'creditorAccountCode' : 
				funSetGLCode(code);
				break;
				
			case 'creditorCode' : 
				funSetCreditoMasterData(code);
				break;
				
			case 'ToDebtorCode' : 
				funSetToDebtorCode(code);
				break;
		}
	}
	
	function funSetCreditoMasterData(creditor)
	{
	   
		var searchurl=getContextPath()+"/loadSundryCreditorMasterData.html?creditorCode="+creditor;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strCreditorCode=='Invalid Code')
			        	{
			        		alert("Invalid creditor Code");
			        		$("#txtDebtorCode").val('');
			        	}
			        	else
			        	{					        	    			        	    
			        	   
			        	    $("#txtFromDebtorCode").val(creditor);
			        	    $("#lblFromDebtorName").text(response.strCreditorFullName);
			        	
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



	function funSetGLCode(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadAccontCodeAndName.html?accountCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strVouchNo!="Invalid")
		    	{
					$("#txtGLCode").val(response.strAccountCode);
					$("#lblGLCode").text(response.strAccountName);
					$("#txtFromDebtorCode").focus();					
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
	
	
// Function to set debtor details from help	
	function funSetMemberDetails(code){
		
		$.ajax({
			type : "GET",
			url : getContextPath()+ "/getDebtorDetails.html?debtorCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strAccountCode=='Invalid Code')
	        	{
	        		alert("Invalid Debtor Code");
	        		$("#txtFromDebtorCode").val('');
	        	}
	        	else
	        	{
	        		$("#txtFromDebtorCode").val(response.strDebtorCode);
	        		$("#lblFromDebtorName").text(response.strDebtorName);
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


// Function to add detail grid rows	
	function funAddDetailsRow(accountCode,debtorCode,description,transType,dimension,debitAmt,creditAmt) 
	{
	    var table = document.getElementById("tblReceiptDetails");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"12%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strAccountCode\" id=\"strAccountCode."+(rowCount)+"\" value='"+accountCode+"' />";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"12%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strDebtorCode\" id=\"strDebtorCode."+(rowCount)+"\" value='"+debtorCode+"' />";
	    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"20%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strDescription\" id=\"strDescription."+(rowCount)+"\" value='"+description+"' />";
	    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"5%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strDC\" id=\"strDC."+(rowCount)+"\" value='"+transType+"' />";
	    row.insertCell(4).innerHTML= "<input readonly=\"readonly\" class=\"Box debitAmt\" size=\"7%\" name=\"listReceiptBeanDtl["+(rowCount)+"].dblDebitAmt\" id=\"dblDebitAmt."+(rowCount)+"\" value='"+debitAmt+"'/>";
	    row.insertCell(5).innerHTML= "<input readonly=\"readonly\" class=\"Box creditAmt\" size=\"7%\" name=\"listReceiptBeanDtl["+(rowCount)+"].dblCreditAmt\" id=\"dblCreditAmt."+(rowCount)+"\" value='"+creditAmt+"'/>";
	    row.insertCell(6).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"8%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strDimension\" id=\"strDimension."+(rowCount)+"\" value='"+dimension+"'/>";	        
	    row.insertCell(7).innerHTML= "<input type=\"button\" class=\"deletebutton\" size=\"2%\" value = \"Delete\" onClick=\"Javacsript:funDeleteRow(this)\"/>";
	    row.insertCell(8).innerHTML= "<input type=\"hidden\" readonly=\"readonly\" size=\"1%\" name=\"listReceiptBeanDtl["+(rowCount)+"].strDebtorName\" id=\"strDebtorName."+(rowCount)+"\" value='"+debtorName+"' />";
	    
	    debtorName='';
	    $("#txtDebtorCode").prop('disabled', true);
	    funCalculateTotalAmt();
	    funResetDetailFields();
	}


//Delete All records from a grid
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
	    $("#txtDebtorCode").val('');
	    $("#txtDescription").val('');
	    $("#cmbDrCr").val('Dr');
	    $("#txtAmount").val('0.00');
	    $("#cmbDimesion").val('No');	    
	}
	
	
// Reset Header Fields
	function funResetHeaderFields()
	{
		$("#txtVouchNo").val('');		
		$("#txtNarration").val('');
		$("#txtVouchDate").datepicker('setDate', 'today');
		$("#txtChequeDate").datepicker('setDate', 'today');
		$("#txtDebtorCode").prop('disabled', true);
	}


	
// Function to Validate Header Fields
	function funValidateHeaderFields()
	{
		/* if($("#txtVouchDate").val()=='')
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
		} */
	}


	function funHelp(transactionName)
	{
		fieldName=transactionName;
	//	window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
		window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
	
</script>

</head>
<body>

	<div id="formHeading">
		<label>Creditor Ledger</label>
	</div>

	<br/>
	<br/>

	<s:form name="CreditorLedger" method="POST" action="showCreditorLedger.html">

		<table class="masterTable">
			<tr>
				<td>
					<label>GL Code</label>
				</td>
				<td>
					<s:input type="text" id="txtGLCode" path="strGLCode" class="searchTextBox" ondblclick="funHelp('creditorAccountCode');"/>
				</td>
				<td  colspan="3"><label id="lblGLCode"></label></td>
			</tr>
			
			<tr>
				<td>
					<label>Creditor Code</label>
				</td>
				<td>
					<s:input type="text" id="txtFromDebtorCode" path="strFromDebtorCode" class="searchTextBox" ondblclick="funHelp('creditorCode');"/>
				</td>
				<td >
					<label id="lblFromDebtorName"></label>
				</td>
				<td>
					<label>Type</label>
				</td>
				<td>
					<s:select id="cmbType" path="strType" class="BoxW124px" >
						<option value="By Account">By Account</option>
						<option value="Bill By Bill">Bill By Bill</option>
					</s:select>
				</td>
			</tr>

<!-- 		<!--  -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>To Debtor Code</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<!-- 					<s:input type="text" id="txtToDebtorCode" path="strToDebtorCode" class="searchTextBox" ondblclick="funHelp('DebtorCode');"/> -->
<!-- 				</td> -->
<!-- 				<td> -->
<!-- 					<label id="lblToDebtorCode"></label> -->
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 		 --> 

<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>Creditor Name</label> -->
<!-- 				</td> -->
<%-- 				<td colspan="4"><s:input id="txtCreditorName" path="strGuestName" type="text" style="width: 28%;" class="longTextBox"/></td> --%>
<!-- 			</tr> -->
			
			<tr>
				<td>
					<label>Status</label>
				</td>
				<td>
					<label id="lblStatus"></label>
				</td>
				
				<td>
					<label>Stop Credit Supply</label>
				</td>
				<td>
					<label id="lblStopCreditSupply"></label>
				</td>
				<td>
					<s:select id="cmbChangeYear" path="strChangeYear" class="BoxW124px">
						<s:options items="${listChangeYear}"/>
					</s:select>
				</td>
			</tr>
			
			<tr>
				<td>
					<label>From Date</label>
				</td>
				<td>
					<s:input type="text" id="txtFromDate" path="dteFromDate" cssClass="calenderTextBox" />
				</td>
				
				<td>
					<label>To Date</label>
				</td>
				<td colspan="2">
					<s:input  type="text" id="txtToDate" path="dteToDate" cssClass="calenderTextBox" />
				</td>
			</tr>
			<tr>
			<td ><p align="center">
			<input type="button" value="Execute" id="btnExecute"   class="form_button" /></p><td>
			<td ><p align="center"><input type="button" value="Export" id="btnExport" class="form_button" onclick=""/></p></td>
			
			<td colspan="4"></td>
			</tr>
			
		</table>

		<br>
		<br>
			
			<div id="dvCreditorLedgerBill" style="width: 100% ;height: 100% ;">
<!-- 				<table id="tblCreditorLedgerBill"  class="transTable col2-right col3-right">					 -->
		<table id="tblCreditorLedgerBill"  class="transTable col5-right col6-right col7-right col8-right col9-right"></table>
		</div>
		<br> <br>
		<div id="dvCreditorLedgerBillTot" style="width: 30% ;height: 100% ;">
				<table id="tblCreditorLedgerBillTot" class="transTable col2-right"></table>
			</div>
		
		

	</s:form>
</body>
</html>
