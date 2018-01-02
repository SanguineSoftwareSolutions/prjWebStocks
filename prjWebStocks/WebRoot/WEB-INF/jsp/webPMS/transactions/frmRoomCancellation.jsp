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
	
// Validate form before submit
	
	function funValidateData()
	{
		var isValid=false;
		if($("#txtReservationNo").val().trim().length<1)
		{
			alert("Please Select Reservation.");
			isValid=false;
		}
		else
		{
			isValid=true;
		}		
		return isValid;
	}

	
//set Reservation Data
	function funSetReservationData(reservationNo)
	{
		var arrivalFromDate=$("#txtArrivalFromDate").val();
		var arrivalToDate=$("#txtArrivalToDate").val();
		var searchUrl=getContextPath()+"/loadReservationDataForCheckIn.html?reservationNo="+reservationNo+"&arrivalFromDate="+arrivalFromDate+"&arrivalToDate="+arrivalToDate;
		$.ajax({
			
			url:searchUrl,
			type :"GET",
			dataType: "json",
	        success: function(response)
	        {
	        	if(response.strReservationNo=='Invalid Code')
	        	{
	        		alert("Invalid Reservation No.");
	        		$("#txtReservationNo").val('');
	        	}
	        	else
	        	{
	        		$("#txtReservationNo").val(response[0][0]);
	        		$("#txtCorporateCode").val(response[0][1]);
	        		$("#lblCorporateName").text(response[0][2]);
	        		$("#txtGuestName").val(response[0][3]);
	        		$("#lblRoomDesc").text(response[0][4]);
	        		$("#txtGuestCode").val(response[0][5]);
	        	}
			},
			error: function(jqXHR, exception) 
			{
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


	/* set date values */
	function funSetDate(id,responseValue)
	{
		var id=id;
		var value=responseValue;
		var date=responseValue.split(" ")[0];
		
		var y=date.split("-")[0];
		var m=date.split("-")[1];
		var d=date.split("-")[2];
		
		$(id).val(d+"-"+m+"-"+y);
	}	

	
//set date
	$(document).ready(function(){
		$("#txtArrivalFromDate").datepicker({dateFormat : 'dd-mm-yy'});
		$("#txtArrivalFromDate").datepicker('setDate', 'today');
		
		$("#txtArrivalToDate").datepicker({dateFormat : 'dd-mm-yy'});
		$("#txtArrivalToDate").datepicker('setDate', 'today');
		
		 var pmsDate='<%=session.getAttribute("PMSDate").toString()%>';
		  var dte=pmsDate.split("-");
		  $("#txtPMSDate").val(dte[2]+"-"+dte[1]+"-"+dte[0]);
	});
	
	/**
	* Success Message After Saving Record
	**/
	$(document).ready(function()
	{
		var message='';
		<%if (session.getAttribute("success") != null) 
		{
			if(session.getAttribute("successMessage") != null){%>
				message='<%=session.getAttribute("successMessage").toString()%>';
			    <%
			    session.removeAttribute("successMessage");
			}
			boolean test = ((Boolean) session.getAttribute("success")).booleanValue();
			session.removeAttribute("success");
			if (test)
			{
				%>	
				alert("Data Save successfully\n\n"+message);
			<%
			}
		}%>
	});
	
	
	/**
		* Success Message After Saving Record
	**/
	function funSetData(code)
	{
		switch(fieldName)
		{
			case "ReservationNo":
				funSetReservationData(code);
			break;
		}
	}
	
	function funHelp(transactionName)
	{
		fieldName=transactionName;
		window.open("searchform.html?formname="+transactionName+"&searchText=","mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
		//window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
</script>

</head>
<body>

	<div id="formHeading">
	<label>Cancellation</label>
	</div>

<br/>
<br/>

	<s:form name="RoomCancellation" method="POST" action="saveRoomCancellation.html">

		<table class="masterTable">
		<tr>
				<th align="right" colspan="3"><label >PMS Date</label>&nbsp; &nbsp;<input id="txtPMSDate" style="width: 90px;font-weight: bold;" readonly="readonly" class="longTextBox"/></th>
				<th align="right" colspan="3"><a id="baseUrl" href="#"> Attach Documents</a>&nbsp; &nbsp; &nbsp;&nbsp;</th>
			    <th colspan="1">
			</tr>
			<tr>
			<th colspan="7">
			</tr>
			<tr>
			    <td  style="width: 100px;"><label>Property</label></td>
				<td colspan="5"><s:select id="strPropertyCode" path="strPropertyCode" items="${listOfProperty}" required="true" cssClass="BoxW200px"></s:select></td>
			</tr>
			
			<tr>
				<td><label >Arrival From Date</label></td>
				<td><s:input type="text" id="txtArrivalFromDate" path="dteArrivalFromDate" required="true" class="calenderTextBox" /></td>
				<td><label >Arrival To Date</label></td>
				<td><s:input type="text" id="txtArrivalToDate" path="dteArrivalToDate" required="true" class="calenderTextBox" /></td>
				<td style="width: 100px;"><label>Reservation No.</label></td>
			    <td><s:input id="txtReservationNo" path="strReservationNo" readonly="true"  ondblclick="funHelp('ReservationNo')" cssClass="searchTextBox"/></td>
			</tr>
			
			<tr>
			    <td colspan="2"><label>Room No</label></td>
				<td><label id="lblRoomDesc"></label></td>
				<td colspan="3"></td>
			</tr>
			
			<tr>
			    <td><label>Guest Name</label></td>
				<td><s:input id="txtGuestName" path="strGuestName"  readonly="true" cssClass="longTextBox"  placeholder="last"  /></td>
				<td><s:input id="txtGuestCode" path="strGuestCode" type="hidden" readonly="true" cssClass="longTextBox" placeholder="last"  /></td>
				<td colspan="5"></td>
			<td colspan="5"></td>
			</tr>
			
			<tr>
			    <td><label>Corporate</label></td>
				<td><s:input id="txtCorporateCode" path="strCorporate" readonly="true" cssClass="longTextBox"  /></td>
				<td><label id="lblCorporateName"></label></td>
			<td colspan="5"></td>
			</tr>
			
		</table>

		<br />
		<br />
		<p align="center">
			<input type="submit" value="Submit" tabindex="3" class="form_button" onclick="return funValidateData()"/>
			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/>
		</p>

	</s:form>
</body>
</html>
