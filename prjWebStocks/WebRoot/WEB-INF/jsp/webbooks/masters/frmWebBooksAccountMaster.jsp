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

	/**
	* Success Message After Saving Record
	**/
	 $(document).ready(function()
				{
		var message='';
		<%if (session.getAttribute("success") != null) {
			            if(session.getAttribute("successMessage") != null){%>
			            message='<%=session.getAttribute("successMessage").toString()%>';
			            <%
			            session.removeAttribute("successMessage");
			            }
						boolean test = ((Boolean) session.getAttribute("success")).booleanValue();
						session.removeAttribute("success");
						if (test) {
						%>	
			alert("Data Save successfully\n\n"+message);
		<%
		}}%>

	});
	
	 document.onkeypress = function(key_dtl) {
		 key_dtl = key_dtl || window.event; 
		 var uni_code = key_dtl.keyCode || key_dtl.which; 
		 var key_name = String.fromCharCode(uni_code); 
		
			var accno=$("#txtAccountCode").val();
 			
 			if(accno.length=='4')
 				{
 					funSetAccountGroupDetails(accno);	
 					$("#txtAccountCode").focus();
 				}
		 }
		 		
		 
	
	function funSetAccountDetails(accountCode)
	{
	    $("#txtAccountCode").val(accountCode);
		var searchurl=getContextPath()+"/loadAccountMasterData.html?accountCode="+accountCode;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strAccountCode=='Invalid Code')
			        	{
			        		alert("Invalid Account Code");
			        		$("#txtAccountCode").val('');
			        	}
			        	else
			        	{
				        	$("#txtAccountName").val(response.strAccountName);
				        	$("#txtAccountName").focus();
				        	
				        	if(response.strType=="GLCode" || response.strType=="GL Code")
				        	{
				        		$("#cmbAccountType").val("GL Code");
				        	}
				        	else				        		
				        	{
				        		$("#cmbAccountType").val(response.strType);
				        	}
				        	$("#cmbOperational").val(response.strOperational);
				        	$("#cmbDebtor").val(response.strDebtor);
				        	$("#cmbCreditor").val(response.strCreditor);
				        	$("#txtGroupCode").val(response.strGroupCode);
				        	$("#txtGroupName").val(response.strGroupName);
				        	$("#txtBranch").val(response.strBranch);
				        	$("#txtOpeningBal").val(response.intOpeningBal);
				        	$("#cmbOpeningBal").val(response.strCreditor);
				        	
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
	
	function funSetAccountGroupDetails(groupCode)
	{
	   
		var searchurl=getContextPath()+"/loadACGroupMasterData.html?acGroupCode="+groupCode;
		 $.ajax({
	        type: "GET",
	        url: searchurl,
	        dataType: "json",
	        success: function(response)
	        {
	        	if(response.strGroupCode=='Invalid Code')
	        	{
	        		alert("Invalid Group Code");
	        		$("#txtGroupCode").val('');
	        	}
	        	else
	        	{
	        		$("#txtGroupCode").val(response.strGroupCode);
		        	$("#txtGroupName").val(response.strGroupName);
		        					        	
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

	function funSetData(code){

		switch(fieldName)
		{		
			case "accountCode": 
			     funSetAccountDetails(code);
				 break;
				 
            case "acGroupCode":
                 funSetAccountGroupDetails(code);			    					   
				 break; 
		}
	}

	function funHelp(transactionName)
	{
		fieldName=transactionName;
	//	window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
		window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
	
	/* function funChangeDrYN()
	{
		var dr =$("#cmbDebtor").val();
		
		if(dr=='No')
			{
				$("#cmbCreditor").val('Yes');
			}
		if(dr=='Yes')
			{
			$("#cmbCreditor").val('No');
			}
		
		
	}
	
	function funChangeCrYN()
	{
		var cr =$("#cmbCreditor").val();
		if(cr=='Yes')
			{
				$("#cmbDebtor").val('No');
			}
		if(cr=='No')
			{
			$("#cmbDebtor").val('Yes');
			}
		
	} */
	
	
</script>

</head>
<body>

	<div id="formHeading">
	<label> Account Master</label>
	</div>

<br/>
<br/>

	<s:form name="WebBooksAccountMaster" method="POST" action="saveWebBooksAccountMaster.html">

		<table class="masterTable">
			<tr>
			    <td><label >Account Code</label></td>
			    <td><s:input id="txtAccountCode" path="strAccountCode"  ondblclick="funHelp('accountCode')" cssClass="searchTextBox"/></td>			        			        
			    <td colspan="2"><s:input id="txtAccountName" path="strAccountName" required="true" cssClass="longTextBox" style="width: 340px"/></td>			    		        			   
			</tr>
			<tr>
				<td><label>Account Type</label> </td>
				<td><s:select id="cmbAccountType" path="strType" items="${listAccountType}" cssClass="BoxW124px"/></td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td><label>Operational</label> </td>
				<td colspan="3"><s:select id="cmbOperational" path="strOperational" items="${listOperational}" cssClass="BoxW124px"/></td>
				
			</tr>
			<tr>
			<td style="width: 100px;"><label>Debtor</label> </td>
				<td><s:select id="cmbDebtor" path="strDebtor" items="${listDebtor}" cssClass="BoxW124px" /></td>
			<td style="width: 100px;"><label>Creditor</label> </td>
				<td><s:select id="cmbCreditor" path="strCreditor" items="${listCreditor}" cssClass="BoxW124px" /></td>	
			
			</tr>
			<tr>
			    <td><label >Group Code</label></td>
			    <td><s:input id="txtGroupCode" path="strGroupCode" readonly="true" ondblclick="funHelp('acGroupCode')" cssClass="searchTextBox"/></td>			        			        
			    <td colspan="2"><s:input id="txtGroupName" path="strGroupName" required="true" readonly="true" cssClass="longTextBox"/></td>			    			        			   
			</tr>
			<tr>
			    <td><label >Branch</label></td>			   			        			       
			    <td ><s:input id="txtBranch" path="strBranch" cssClass="longTextBox"/></td>
			    <td></td>
			    <td></td>			        			    
			</tr>
			<tr>
			    <td><label>Opening Balance</label></td>			   			        			       
			    <td  ><s:input id="txtOpeningBal" path="intOpeningBal" readonly="true" required="true" cssClass="longTextBox"/></td>
<%-- 			    <td><s:select id="cmbOpeningBal" path="strCreditor" items="${listOpeningBalance}" cssClass="BoxW124px" /></td> --%>
			    <td></td>
			     <td></td>			        			    
			</tr>
		</table>

		<br />
		<br />
		<p align="center">
			<input type="submit" value="Submit" tabindex="3" class="form_button" "/>
			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/>
		</p>

	</s:form>
</body>
</html>
