<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
<style type="text/css">
.ui-timepicker-wrapper
{
	width: 95px;
}
</style>

<script type="text/javascript">
	var fieldName;
	$(document).ready(function() {

		$(".tab_content").hide();
		$(".tab_content:first").show();

		$("ul.tabs li").click(function() {
			$("ul.tabs li").removeClass("active");
			$(this).addClass("active");
			$(".tab_content").hide();

			var activeTab = $(this).attr("data-state");
			$("#" + activeTab).fadeIn();
		});
	});
	function funValidateFields()
	{
		var flag=false;
		if($('#tmeCheckInTime').val().trim().length==0)
		{
			 alert("Please Select Check In Time");		 	 
		}
		else if($('#tmeCheckOutTime').val().trim().length==0)
		{
			 alert("Please Select Check Out Time");
		}
		else
		{
			//checkins
			var checkin=$('#tmeCheckInTime').val();
			var inHH="00";var inMM="00";var inSS="00";
			if(checkin.contains("am"))
			{
				var checkinvalue=checkin.split("am")[0];
				var inHH=checkinvalue.split(":")[0];
				var inMM=checkinvalue.split(":")[1];				
			}
			else
			{
				var checkinvalue=checkin.split("pm")[0];
				var inHH=checkinvalue.split(":")[0];
				var inMM=checkinvalue.split(":")[1];				
			}
			
			//checkouts
			var checkout=$('#tmeCheckOutTime').val();
			var outHH="00";var outMM="00";var outSS="00";
			if(checkout.contains("am"))
			{
				var checkoutvalue=checkout.split("am")[0];
				var outHH=checkoutvalue.split(":")[0];
				var outMM=checkoutvalue.split(":")[1];				
			}
			else
			{
				var checkoutvalue=checkout.split("pm")[0];
				var outHH=checkoutvalue.split(":")[0];
				var outMM=checkoutvalue.split(":")[1];				
			}
			
			$('#tmeCheckInTime').val(inHH+":"+inMM+":"+inSS);
			$('#tmeCheckOutTime').val(outHH+":"+outMM+":"+outSS);
			
			flag=true;
		}
		
		return flag;
	}
	
	$(function() 
	{
		$('#tmeCheckInTime').timepicker();
		$('#tmeCheckOutTime').timepicker();	
		
		
		$('#tmeCheckInTime').timepicker({
		        'timeFormat':'H:i:s'
		});
		 
		$('#tmeCheckOutTime').timepicker({
		        'timeFormat':'H:i:s'
		}); 			
		/* 
		$('#tmeCheckInTime').timepicker('setTime', new Date());
		$('#tmeCheckOutTime').timepicker('setTime', new Date());*/
		
	}); 
	
	
	
	function funSetData(code)
	{

		switch(fieldName)
		{

		}
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
	/**
		* Success Message After Saving Record
	**/
	
	function funHelp(transactionName)
	{
		fieldName=transactionName;
		window.open("searchform.html?formname="+transactionName+"&searchText=","mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
		//window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
	
	
	function funCreateSMS1()
	{
	 	 	
		var field =$("#cmbReservationSMSField").val();
		var content='';
		var mainSMS =$("#txtReservationSMSContent").val();
		
		if(field=='CompanyName')
		{
			content='%%CompanyName';
		}
		if(field=='PropertyName')
		{
			content='%%PropertyName';
		}
		if(field=='RNo')
		{
			content='%%RNo';
		}
		if(field=='RDate')
		{
			content='%%RDate';
		}
	
		if(field=='GuestName')
		{
			content='%%GuestName';
		}
		
		if(field=='RoomNo')
		{
			content='%%RoomNo';
		}
		
		if(field=='NoNights')
		{
			content='%%NoNights';
		}
		
		mainSMS+=content;
		$("#txtReservationSMSContent").val(mainSMS);
	 }
		
	function funCreateSMS2()
		{
		
		   
		var field =$("#cmbCheckINSMSField").val();
		var content='';
		var mainSMS =$("#txtCheckINSMSContent").val();
		
		if(field=='CompanyName')
		{
			content='%%CompanyName';
		}
		if(field=='PropertyName')
		{
			content='%%PropertyName';
		}
		if(field=='CheckIn')
		{
			content='%%CheckIn';
		}
		if(field=='GuestName')
		{
			content='%%GuestName';
		}
		if(field=='CheckInDate')
		{
			content='%%CheckInDate';
		}
		
		if(field=='RoomNo')
		{
			content='%%RoomNo';
		}
		
		if(field=='NoNights')
		{
			content='%%NoNights';
		}
		mainSMS+=content;
		$("#txtCheckINSMSContent").val(mainSMS);
		
		}	
		
	
	function funCreateSMS3()
		{	
		
		var field =$("#cmbAdvAmtSMSField").val();
		var content='';
		var mainSMS =$("#txtAdvAmtSMSContent").val();
		
		if(field=='CompanyName')
		{
			content='%%CompanyName';
		}
		if(field=='PropertyName')
		{
			content='%%PropertyName';
		}
		if(field=='PaymentNo')
		{
			content='%%PaymentNo';
		}
		if(field=='SettlementDesc')
		{
			content='%%SettlementDesc';
		}
	
		if(field=='Amount')
		{
			content='%%Amount';
		}
		mainSMS+=content;
		$("#txtAdvAmtSMSContent").val(mainSMS);
		}	
	
	
		function funCreateSMS4()
		{
			
		var field =$("#cmbCheckOutSMSField").val();
		var content='';
		var mainSMS =$("#txtCheckOutSMSContent").val();
		
		if(field=='CompanyName')
		{
			content='%%CompanyName';
		}
		if(field=='PropertyName')
		{
			content='%%PropertyName';
		}
		if(field=='CheckOut')
		{
			content='%%CheckOut';
		}
		if(field=='GuestName')
		{
			content='%%GuestName';
		}
	
		if(field=='RoomNo')
		{
			content='%%RoomNo';
		}
		if(field=='checkOutDate')
		{
			content='%%checkOutDate';
		}
		mainSMS+=content;
		$("#txtCheckOutSMSContent").val(mainSMS);
		}		
		
		
		
	
	
	
</script>

</head>
<body>

	<div id="formHeading">
	<label>PropertySetup</label>
	</div>

<br/>
<br/>

	<s:form name="PropertySetup" method="POST" action="savePropertySetup.html">
	<table
				style="border: 0px solid black; width: 100%;height:100%; margin-left: auto; margin-right: auto; background-color: #C0E4FF;">
				<tr>
					<td>
						<div id="tab_container" style="height: 900px">
							<ul class="tabs">
								<li data-state="tab1">General</li>
								
								<li data-state="tab2">SMS Setup</li>

							</ul>
							<div id="tab1" class="tab_content" style="height: 890px">
							<br><br>
									<table class="masterTable">
							<tr>
							    <td  style="width: 100px;"><label>Property</label></td>
								<td colspan="5"><s:select id="strPropertyCode" path="strPropertyCode" items="${listOfProperty}" required="true" cssClass="BoxW200px"></s:select></td>				    						    		        			 
							</tr>
							<tr>
							    <td><label>Check In Time</label></td>
							    <td><s:input path="tmeCheckInTime"  id="tmeCheckInTime" value="${checkInTime}"  class="timePickerTextBox" /></td>	
							    <td><label>Check Out Time</label></td>
							    <td><s:input path="tmeCheckOutTime"  id="tmeCheckOutTime" value="${checkOutTime}" class="timePickerTextBox" /></td>								    						    		        			
							</tr>
						</table>
							</div>
						
							
								<div id="tab2" class="tab_content">
							<br><br><br>
							<table id="tblAudit" class="transTable">
							<tr>
							<td><label >SMS Provider</label></td>
									<td colspan="3"><s:select  id="cmbSMSProvider" path="strSMSProvider" class="BoxW48px" style="width:130px">
											<option value="SANGUINE">SANGUINE</option>
										</s:select>
									</td>
							</tr>
							
							<tr>
							<td><label >SMS API</label></td>
								<td colspan="3"><s:textarea  id="txtSMSAPI" path="strSMSAPI" cssStyle="width: 669px;" /></td>
							</tr>
						 	<tr>
							<td style="width: 130px;"><label >SMS Content For Reservation </label></td>
							<td>	
									<select  id="cmbReservationSMSField" class="BoxW48px" style="width:130px" >
										<option value="CompanyName">Company Name</option>
										<option value="PropertyName">Property Name</option>
										<option value="RNo">Reservation No</option>
										<option value="RDate">Reservation Date</option>
										<option value="GuestName">GuestName</option>
										<option value="RoomNo">Room No</option>
									<option value="NoNights">No of Nights</option>
									</select>
							 </td>
							 
							<td><input type="button" value="Add" class="smallButton" onclick="funCreateSMS1();" id=btnAddSMS1 /></td>
									<td><s:textarea cssStyle="width: 373px; height: 101px;" id="txtReservationSMSContent" path="strReservationSMSContent"  /></td>
							</tr> 
							
							
							<tr>
							<td style="width: 130px;"><label >SMS Content For Check IN </label></td>
							<td>	
									<select  id="cmbCheckINSMSField" class="BoxW48px" style="width:130px" >
										<option value="CompanyName">Company Name</option>
										<option value="PropertyName">Property Name</option>
										<option value="CheckIn">Check IN</option>
										<option value="GuestName">GuestName</option>
										<option value="RoomNo">RoomNo</option>
										<option value="NoNights">No of Nights</option>
										<option value="RDate">CheckInDate</option>
										
									</select>
							 </td>
							 
									<td><input type="button" value="Add" class="smallButton" onclick="funCreateSMS2();" id=btnAddSMS2 /></td>
									<td><s:textarea cssStyle="width: 373px; height: 101px;" id="txtCheckINSMSContent" path="strCheckInSMSContent"  /></td>
							</tr>
							
	
										<tr>
							<td style="width: 130px;"><label >SMS Content For Advance Amount </label></td>
							<td>	
									<select  id="cmbAdvAmtSMSField" class="BoxW48px" style="width:130px" >
										<option value="CompanyName">Company Name</option>
										<option value="PropertyName">Property Name</option>
										<option value="PaymentNo">Payment Recipt No</option>
										<option value="Amount">Amount</option>
										<option value="SettlementDesc">Settlement Description</option>
									</select>
							 </td>
							 
									<td><input type="button" value="Add" class="smallButton" onclick="funCreateSMS3();" id=btnAddSMS3 /></td>
									<td><s:textarea cssStyle="width: 373px; height: 101px;" id="txtAdvAmtSMSContent" path="strAdvAmtSMSContent"  /></td>
							</tr>
							
							
											<tr>
							<td style="width: 130px;"><label >SMS Content For check Out </label></td>
							<td>	
									<select  id="cmbCheckOutSMSField" class="BoxW48px" style="width:130px" >
									<option value="CompanyName">Company Name</option>
										<option value="PropertyName">Property Name</option>
										<option value="CheckOut">Check Out</option>
										<option value="GuestName">GuestName</option>
										<option value="RoomNo">RoomNo</option>
										<option value="checkOutDate">CheckOutDate</option>
									</select>
							 </td>
							 
									<td><input type="button" value="Add" class="smallButton" onclick="funCreateSMS4();" id=btnAddSMS4 /></td>
									<td><s:textarea cssStyle="width: 373px; height: 101px;" id="txtCheckOutSMSContent" path="strCheckOutSMSContent"  /></td>
							</tr>
							
							</table>							
							</div>

</div>
</td>
</tr>
</table>
		<br />
		<br />
		<p align="center">
			<input type="submit" value="Submit" tabindex="3" class="form_button" /><!-- onclick="return funValidateFields()" -->
			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/>
		</p>

	</s:form>
</body>
</html>
