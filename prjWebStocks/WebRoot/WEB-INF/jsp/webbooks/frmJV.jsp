<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
<script type="text/javascript">
	
	var fieldName;
	var debtorYN='N';
	var debtorName='';
	var oneLineAccDesc='';

	$(function() 
	{
		$("#txtVouchDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtVouchDate").datepicker('setDate', 'today');
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
				%> alert("Data Save successfully\n\n"+message);<%
			}
		}%>
	});
	
	

	function funSetData(code){

		switch(fieldName){

			case 'JVNo' : 
				funSetVouchNo(code);
				break;
				
			case 'accountCode' : 
				funSetAccountDetails(code);
				break;
				
			case 'oneLineAcc' : 
				funSetOneLineAccDetails(code);
				break;
			
			case 'debtorCode' : 
			//	funSetMemberDetails(code);
				funSetDebtorMasterData(code)
				break;
		}
	}

// Function to set JV Data from help	
	function funSetVouchNo(code){
		
		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadJV.html?docCode=" + code,
			dataType : "json",
			success : function(response){

				if(response.strVouchNo!="Invalid")
		    	{
		    		funFillHdData(response);
		    	}
		    	else
			    {
			    	alert("Invalid JV No");
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
	
	
	function funFillHdData(response)
	{
		$("#txtVouchNo").val(response.strVouchNo);
		$("#txtVouchDate").val(response.dteVouchDate);
		$("#txtNarration").val(response.strNarration);
		$("#txtUserCreated").val(response.strUserCreated);
		$("#txtUserEdited").val(response.strUserEdited);
		$("#txtCreatedDate").val(response.dteDateCreated);
		$("#txtEditedDate").val(response.dteDateEdited);
		funFillDtlGrid(response.listJVDtlBean);
	}
	
	
	function funFillDtlGrid(resListJVDtlBean)
	{
		funRemoveProductRows();
		$.each(resListJVDtlBean, function(i,item)
        {
			debtorYN=resListJVDtlBean[i].strDebtorYN;
			debtorName=resListJVDtlBean[i].strDebtorName;
			funAddDetailsRow(resListJVDtlBean[i].strAccountCode,resListJVDtlBean[i].strDebtorCode
				,resListJVDtlBean[i].strDescription,resListJVDtlBean[i].strDC,''
				,resListJVDtlBean[i].strOneLineAcc,resListJVDtlBean[i].strNarration
				,resListJVDtlBean[i].dblDebitAmt,resListJVDtlBean[i].dblCreditAmt);
        });
	}
	
	
// Function to set account details from help	
	function funSetAccountDetails(code){
		
		 $.ajax({
		        type: "GET",
		        url: getContextPath()+ "/loadAccontCodeAndName.html?accountCode=" + code,
		        dataType: "json",
		        success: function(response)
		        {
		        	if(response.strAccountCode=='Invalid Code')
		        	{
		        		alert("Invalid Account Code");
		        		$("#txtAccCode").val('');
		        	}
		        	else
		        	{
		        		$("#txtAccCode").val(response.strAccountCode);
			        	$("#txtDescription").val(response.strAccountName);
			        	if(response.strDebtor=='Yes')
			        	{
			        //		$("#txtDebtorCode").prop('disabled', false);
			        	}
			        	else
			        	{
			        		$("#txtDebtorCode").val('');
			       // 		$("#txtDebtorCode").prop('disabled', true);
			        	}
			        	$("#txtDebtorCode").focus();
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
	
	
	
// Function to set account details from help
	function funSetOneLineAccDetails(code){
	
		var searchurl=getContextPath()+ "/loadAccForNonDebtor.html?acCode=" + code
	
		$.ajax({
			type : "GET",
			url : searchurl,
			dataType : "json",
			success : function(response){ 
				if(response.strAccountCode=='Invalid Code')
	        	{
	        		alert("Invalid Account Code");
	        		$("#txtOneLineAcc").val('');
	        	}
				if($("#txtAccCode").val()==response.strAccountCode)
				{
					alert("This account in not valid!!!");
				}
	        	else
	        	{
	        		$("#txtOneLineAcc").val(response.strAccountCode);
	        		oneLineAccDesc=response.strAccountName;
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
	


// Function to set debtor details from help	
	function funSetMemberDetails(code){
		//alert(getContextPath()+ "/loadMemberDetails.html?memberCode=" + code);
		
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
	        		$("#cmbDrCr").focus();
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
	    var description=$("#txtDescription").val();
	    var transType=$("#cmbDrCr").val();
	    var dimension=$("#cmbDimesion").val();
	    var oneLineAcc=$("#txtOneLineAcc").val();
	    var narration=$("#txtNarrationDtl").val();
	    
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
	    
	    funAddDetailsRow(accountCode,debtorCode,description,transType,dimension,oneLineAcc,narration,debitAmt,creditAmt);
	    
	    if(oneLineAcc.trim()!='')
	    {
	    	var transTypeForOneLineAcc=transType;
	    	if(transType=='Dr')
		    {
	    		transTypeForOneLineAcc='Cr';
		    }
		    else
		    {
		    	transTypeForOneLineAcc='Dr';
		    }
	    	funAddDetailsRow(oneLineAcc,'',oneLineAccDesc,transTypeForOneLineAcc,dimension,'',narration,creditAmt,debitAmt);
	    }
	}
	
	
// Function to add detail grid rows	
	function funAddDetailsRow(accountCode,debtorCode,description,transType,dimension,oneLineAcc,narration,debitAmt,creditAmt) 
	{
	    var table = document.getElementById("tblJVDetails");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"12%\" name=\"listJVDtlBean["+(rowCount)+"].strAccountCode\" id=\"strAccountCode."+(rowCount)+"\" value='"+accountCode+"' />";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"12%\" name=\"listJVDtlBean["+(rowCount)+"].strDebtorCode\" id=\"strDebtorCode."+(rowCount)+"\" value='"+debtorCode+"' />";
	    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"20%\" name=\"listJVDtlBean["+(rowCount)+"].strDescription\" id=\"strDescription."+(rowCount)+"\" value='"+description+"' />";
	    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"5%\" name=\"listJVDtlBean["+(rowCount)+"].strDC\" id=\"strDC."+(rowCount)+"\" value='"+transType+"' />";
	    row.insertCell(4).innerHTML= "<input readonly=\"readonly\" class=\"Box debitAmt\" size=\"7%\" name=\"listJVDtlBean["+(rowCount)+"].dblDebitAmt\" id=\"dblDebitAmt."+(rowCount)+"\" value='"+debitAmt+"'/>";
	    row.insertCell(5).innerHTML= "<input readonly=\"readonly\" class=\"Box creditAmt\" size=\"7%\" name=\"listJVDtlBean["+(rowCount)+"].dblCreditAmt\" id=\"dblCreditAmt."+(rowCount)+"\" value='"+creditAmt+"'/>";
	    row.insertCell(6).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"8%\" name=\"listJVDtlBean["+(rowCount)+"].strDimension\" id=\"strDimension."+(rowCount)+"\" value='"+dimension+"'/>";
	    row.insertCell(7).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"8%\" name=\"listJVDtlBean["+(rowCount)+"].strOneLineAcc\" id=\"strOneLineAcc."+(rowCount)+"\" value='"+oneLineAcc+"'/>";
	    row.insertCell(8).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"15%\" name=\"listJVDtlBean["+(rowCount)+"].strNarration\" id=\"strNarration."+(rowCount)+"\" value='"+narration+"'/>";
	    row.insertCell(9).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"2%\" name=\"listJVDtlBean["+(rowCount)+"].strDebtorYN\" id=\"strDebtorYN."+(rowCount)+"\" value='"+debtorYN+"'/>";
	    row.insertCell(10).innerHTML= "<input type=\"button\" class=\"deletebutton\" size=\"2%\" value = \"Delete\" onClick=\"Javacsript:funDeleteRow(this)\"/>";
	    row.insertCell(11).innerHTML= "<input type=\"hidden\" readonly=\"readonly\" size=\"1%\" class=\"Box\" name=\"listJVDtlBean["+(rowCount)+"].strDebtorName\" id=\"strDebtorName."+(rowCount)+"\" value='"+debtorName+"' />";
	    
	    debtorYN='N';
	    debtorName='';
	    oneLineAccDesc='';
//	    $("#txtDebtorCode").prop('disabled', true);
	    funCalculateTotalAmt();
	    funResetDetailFields();
	}
	

//Delete a All record from a grid
	function funRemoveProductRows()
	{
		var table = document.getElementById("tblJVDetails");
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
	    var table = document.getElementById("tblJVDetails");
	    table.deleteRow(index);
	    funCalculateTotalAmt();
	}

	
//Calculating total amount
	function funCalculateTotalAmt()
	{
		var totalAmt=0.00;
		var totalDebitAmt=0.00;
		var totalCreditAmt=0.00;
		
		$('#tblJVDetails tr').each(function() 
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
	    $("#txtOneLineAcc").val('');
	    $("#txtNarrationDtl").val('');
	}
	
	
// Reset Header Fields
	function funResetHeaderFields()
	{
		$("#txtVouchNo").val('');
		$("#txtVouchDate").val('');
		$("#txtNarration").val('');
		$("#txtUserCreated").val('');
		$("#txtUserEdited").val('');
		$("#txtCreatedDate").val('');
		$("#txtEditedDate").val('');
	}


// Function to Validate Header Fields
	function funValidateHeaderFields(object)
	{
		if($("#txtVouchDate").val()=='')
		{
			alert('Please Select Vouch Date!!!');
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
			        	    $("#txtDebtorCode").val(debtorCode);
			        	
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
</script>

</head>
<body>

	<div id="formHeading">
		<label>JV</label>
	</div>

<br/>
<br/>

	<s:form name="JV" method="POST" action="saveJV.html">

		<table class="transTable">
			<tr>
				<td>
					<label>VouchNo</label>
				</td>
				<td >
					<s:input  type="text" id="txtVouchNo" path="strVouchNo" cssClass="searchTextBox" ondblclick="funHelp('JVNo');"/>
				</td>
				
				<td>
					<label>VouchDate</label>
				</td>
				<td colspan="5">
					<s:input  type="text" id="txtVouchDate" path="dteVouchDate" cssClass="calenderTextBox" />
				</td>
			</tr>
			<tr>
				<td>
					<label>Narration</label>
				</td>
				<td colspan="7" >
					<s:input type="text" id="txtNarration" path="strNarration" cssClass="remarkTextBox" />
				</td>
			</tr>
			
			<tr>
				<td><label>User Created</label></td>
				<td>
					<s:input type="text" id="txtUserCreated" path="strUserCreated" cssClass="longTextBox" />
				</td>
				
				<td><label>User Edited</label></td>
				<td>
					<s:input  type="text" id="txtUserEdited" path="strUserEdited" cssClass="longTextBox" />
				</td>
				
				<td><label>Created Date</label></td>
				<td>
					<s:input  type="text" id="txtCreatedDate" path="dteDateCreated" cssClass="longTextBox" />
				</td>
				
				<td><label>Edited Date</label></td>
				<td>
					<s:input  type="text" id="txtEditedDate" path="dteDateEdited" cssClass="longTextBox" />
				</td>
			</tr>
			<tr></tr>
			<tr></tr>
		</table>

		<br>
		<br>
		
		<table class="transTable">
		</table>

	<div >
		<table class="transTable">
				
			<tr>
				<td width="10%"><label>Details</label></td>
			</tr>
				
			<tr>
				<td width="10%"><label>Account Code</label></td>
				<td width="20%"><input id="txtAccCode" name="txtAccCode" ondblclick="funHelp('accountCode')" class="searchTextBox" /></td>
				
				<td width="10%">Debtor Code</td>
				<td width="65%"><input id="txtDebtorCode"  name="txtDebtorCode" ondblclick="funHelp('debtorCode')" class="searchTextBox" /></td>
			
				<td width="10%">Description</td>
				<td width="65%"><input id="txtDescription" name="txtDescription" class="remarkTextBox" /></td>
				
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
						  <option value=""></option>
						  <option value=""></option>
					</select>
				</td>
			</tr>
			
			<tr>
				<td><label>One Line Account</label></td>
				<td><input id="txtOneLineAcc" name="txtOneLineAcc" ondblclick="funHelp('oneLineAcc')" class="searchTextBox" /></td>
			
				<td><label>Narration</label></td>
				<td>
					<input type="text" id="txtNarrationDtl" class="remarkTextBox" />
				</td>
				
				<td colspan="3">
					<input type="Button" value="Add" onclick="return funGetDetailsRow()" class="smallButton" />
				</td>
			</tr>
		</table>
	</div>


	<div class="dynamicTableContainer" style="height: 300px;">
		<table
			style="height: 28px; border: #0F0; width: 100%; font-size: 11px; font-weight: bold;">
			<tr bgcolor="#72BEFC">
				<td style="width:12%;">Account Code</td>
				<td style="width:12%;">Debtor Code</td>
				<td style="width:20%;">Description</td>
				<td style="width:5%;">D/C</td>
				<td style="width:7%;">Debit Amt</td>
				<td style="width:7%;">Credit Amt</td>
				<td style="width:8%;">Dimension</td>
				<td style="width:8%;">One Line Account</td>
				<td style="width:13%;">Narration</td>
				<td style="width:5%;">DebtorYN</td>
				<td style="width:2%;"></td>
			</tr>
		</table>
		
		<div style="background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
			<table id="tblJVDetails"
				style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll"
				class="transTablex col8-center">
				<tbody>
					<col style="width=20%">
					<col style="width:12%">
					<col style="width:20%">
					<col style="width:5%">
					<col style="width:7%">
					<col style="width:7%">
					<col style="width:8%">
					<col style="width:8%">
					<col style="width:13%">
					<col style="width:2%">
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

	<p align="center">
		<input type="submit" value="Submit" onclick="return funValidateHeaderFields(this)" tabindex="3" class="form_button" />
		<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/>
	</p>
	
<br />
	<br /><br />
	<br />
	</s:form>
</body>
</html>
