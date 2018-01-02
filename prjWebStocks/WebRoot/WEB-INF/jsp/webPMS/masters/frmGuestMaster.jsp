<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript">
var fieldName;

/**
* Open Help
**/
function funHelp(transactionName)
{
	fieldName=transactionName;
	window.open("searchform.html?formname="+transactionName+"&searchText=","mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
    //window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
}

$(function() 
		{
			
			
			$("#txtDOB").datepicker({ dateFormat: 'dd-mm-yy' });
			$("#txtDOB").datepicker('setDate', 'today');
					
			$("#txtPassportExpiryDate").datepicker({ dateFormat: 'dd-mm-yy' });
			$("#txtPassportExpiryDate").datepicker('setDate', 'today');
			
			$("#txtPassportIssueDate").datepicker({ dateFormat: 'dd-mm-yy' });
			$("#txtPassportIssueDate").datepicker('setDate', 'today');
			
		});

function funSetData(code)
{

		switch(fieldName)
		{
		   case 'guestCode' : 
			   funSetGuestCode(code);
				break;
			
		}
}



/**
*   Attached document Link
**/
$(function()
{

	$('a#baseUrl').click(function() 
	{
		if($("#txtGuestCode").val().trim()=="")
		{
			alert("Please Select Guest Code");
			return false;
		}
	   window.open('attachDoc.html?transName=frmGuestMaster.jsp&formName=Guest Master&code='+$('#txtGuestCode').val(),"mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
	});

});

function funSetGuestCode(code)
{
	var searchurl=getContextPath()+"/loadGuestCode.html?guestCode="+code;
	 $.ajax({
	        type: "GET",
	        url: searchurl,
	        dataType: "json",
	        success: function(response)
	        {
	        	if(response.strGuestCode=='Invalid Code')
	        	{
	        		alert("Invalid Walikn No");
	        		$("#txtGuestCode").val('');
	        	}
	        	else
	        	{
	        		$("#txtGuestCode").val(response.strGuestCode);
		        	$("#cmbGuestPrefix").val(response.strGuestPrefix);
		        	$("#txtFirstName").val(response.strFirstName);
		        	$("#txtMiddleName").val(response.strMiddleName);
		        	$("#txtLastName").val(response.strLastName);
		        	$("#cmbGender").val(response.strGender);
		        	$("#txtDOB").val(response.dteDOB);
		        	$("#txtDesignation").val(response.strDesignation);
		        	$("#txtAddress").val(response.strAddress);
		        	$("#cmbCity").val(response.strCity);
		        	$("#cmbState").val(response.strState);
		        	$("#cmbCountry").val(response.strCountry);
		        	$("#txtNationality").val(response.strNationality);
		        	$("#txtPinCode").val(response.intPinCode);
		        	$("#txtMobileNo").val(response.lngMobileNo);
		        	$("#txtFaxNo").val(response.lngFaxNo);
		        	$("#txtEmailId").val(response.strEmailId);
		        	$("#txtPANNo").val(response.strPANNo);
		        	$("#txtArrivalFrom").val(response.strArrivalFrom);
		        	$("#txtProceedingTo").val(response.strProceedingTo);
		        	$("#txtStatus").val(response.strStatus);
		        	$("#txtVisitingType").val(response.strVisitingType);
		        	$("#txtPassportNo").val(response.strPassportNo);
		        	$("#txtPassportIssueDate").val(response.dtePassportIssueDate);
		        	$("#txtPassportExpiryDate").val(response.dtePassportExpiryDate);
		        
		        	
		        	
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

</script>
</head>
<body>
	<div id="formHeading">
		<label>Guest Master</label>
	</div>
	
<s:form name="Guest" method="GET" action="saveGuestMaster.html?" >
	
	<br>
	<table class="masterTable">
		
		   <tr>
				<th align="right" colspan="6"><a id="baseUrl"
					href="#"> Attach Documents</a>&nbsp; &nbsp; &nbsp;
						&nbsp;</th>
			</tr>
			
			
			   <tr>
			           <td><label>Guest Code</label></td>
				       <td><s:input id="txtGuestCode" path="strGuestCode" cssClass="searchTextBox" ondblclick="funHelp('guestCode')" /></td>				
			           <td colspan="4"></td>
			      </tr>
			
		          <tr>
						 <td>
							  <label>GuestPrefix</label>
						</td>
						<td>
							 <s:select id="cmbGuestPrefix" path="strGuestPrefix" cssClass="BoxW124px">
				    		<s:options items="${prefix}"/>
				    	    </s:select>
					   </td>
						<td>
							<label>FirstName</label>
						</td>
						<td>
							 <s:input colspan="3" type="text" id="txtFirstName" path="strFirstName" cssClass="longTextBox" />
								
						</td>
							
						<td>
								<label>MiddleName</label>
						</td>
						<td>
							 <s:input colspan="3" type="text" id="txtMiddleName" path="strMiddleName" cssClass="longTextBox" />
							
						</td>
							
							
				</tr>
				
				
				<tr>
					    <td>
					       <label>LastName</label>
						</td>
						<td>
					       <s:input colspan="3" type="text" id="txtLastName" path="strLastName" cssClass="longTextBox" />
						</td>
					
						 <td>
							 <label>Gender</label>
					     </td>
						  <td>
							<s:select id="cmbGender" path="strGender" cssClass="BoxW124px">
				    		<s:options items="${gender}"/>
				    	     </s:select>
						  </td>
							<td>
								<label>DOB</label>
							</td>
							<td>
							        <s:input colspan="3" type="text" id="txtDOB" path="dteDOB" cssClass="calenderTextBox" />
							</td>
						
				</tr>
				
				<tr>
				
				      <td>
							<label>Designation</label>
						</td>
						<td>
						 <s:input colspan="3" type="text" id="txtDesignation" path="strDesignation" cssClass="longTextBox" />
							
						</td>
				
						<td>
							<label>Address</label>
						</td>
						<td>
						 <s:input colspan="3" type="text" id="txtAddress" path="strAddress" cssClass="longTextBox" />
							
						</td>
					 
					 
					     <td>
						   <label>City</label>
						</td>
						<td>
						
						<s:select id="cmbCity" path="strCity" cssClass="BoxW124px">
			    			<s:options items="${listCity}"/>
			    		</s:select>
						 	
						</td>
				</tr>
				
				  <tr>		
						<td>
							<label>State</label>
						</td>
						<td>
						 
						 <s:select id="cmbState" path="strState" cssClass="BoxW124px">
			    			<s:options items="${listState}"/>
			    		</s:select>
						
						</td>
						
						<td>
							<label>Country</label>
						</td>
						<td>
						 
						 <s:select id="cmbCountry" path="strCountry" cssClass="BoxW124px">
			    			<s:options items="${listCountry}"/>
			    		</s:select>
						
						</td>
						
						<td>
					
							<label>Nationality</label>
						</td>
						<td>
						   <s:input colspan="3" type="text" id="txtNationality" path="strNationality" cssClass="longTextBox" />
							
						</td>
				</tr>
				
				<tr>
					 <td>
						   <label>PinCode</label>
						</td>
						<td>
						<s:input colspan="3" type="text" id="txtPinCode" path="intPinCode" cssClass="longTextBox" />
							
						</td>
						<td>
							<label>MobileNo</label>
						</td>
						<td>
						      <s:input colspan="3" type="text" id="txtMobileNo" path="intMobileNo" cssClass="longTextBox" onblur="fun1(this);" />
							 
						</td>
						
						<td>
							<label>FaxNo</label>
						</td>
						<td>
						   <s:input colspan="3" type="text" id="txtFaxNo" path="intFaxNo" cssClass="longTextBox" />
							
						</td>
						
				</tr>
				
				<tr>	
						<td>
							<label>EmailId</label>
						</td>
						<td>
						   <s:input colspan="3" type="text" id="txtEmailId" path="strEmailId" cssClass="longTextBox" />
							
						</td>
						
						<td>
						   <label>PANNo</label>
						</td>
						<td>
						   <s:input colspan="3" type="text" id="txtPANNo" path="strPANNo" cssClass="longTextBox" />
						
						</td>
						<td>
							<label>ArrivalFrom</label>
						</td>
						<td>
						    <s:input colspan="3" type="text" id="txtArrivalFrom" path="strArrivalFrom" cssClass="longTextBox" />
							
						</td>
				</tr>
				
					
				<tr>
					  <td>
							<label>ProceedingTo</label>
						</td>
						<td>
						  <s:input colspan="3" type="text" id="txtProceedingTo" path="strProceedingTo" cssClass="longTextBox" />
							
						</td>
						
						<td>
							<label>Status</label>
						</td>
						<td>
						   <s:input colspan="3" type="text" id="txtStatus" path="strStatus" cssClass="longTextBox" />
							
						</td>
						
						<td>
						   <label>VisitingType</label>
						</td>
						<td>
					      <s:input colspan="3" type="text" id="txtVisitingType" path="strVisitingType" cssClass="longTextBox" />
						
						</td>
				</tr>
					
					
				<tr>
					 
						<td>
							<label>PassportNo</label>
						</td>
						<td>
						    <s:input colspan="3" type="text" id="txtPassportNo" path="strPassportNo" cssClass="longTextBox" />
							
						</td>
						
						<td>
							<label>PassportIssueDate</label>
						</td>
						<td>
						    
							 <s:input colspan="3" type="text" id="txtPassportIssueDate" path="dtePassportIssueDate" cssClass="calenderTextBox" />
						</td>
						
						<td>
							<label>PassportExpiryDate</label>
						</td>
						<td>
						      <s:input colspan="3" type="text" id="txtPassportExpiryDate" path="dtePassportExpiryDate" cssClass="calenderTextBox" />
						
						</td>
				</tr>
					
				
				
					
			
		</table>
		
		
		<br />
		<br />
		<p align="center">
			<input type="submit" value="Submit" tabindex="3" class="form_button" onclick="return funCallFormAction('submit',this);" />
            <input type="reset" value="Reset" class="form_button" />
           
            
		</p>
	</s:form>
	
</body>
</html>