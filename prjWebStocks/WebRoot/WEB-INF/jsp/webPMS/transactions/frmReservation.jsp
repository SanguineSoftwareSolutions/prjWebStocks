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
	
	  $(document).ready(function(){
		    
		  $("#txtNoOfAdults").val(1);
		  $("#txtNoOfBookingRoom").val(1);
		  
		  var pmsDate='<%=session.getAttribute("PMSDate").toString()%>';
		  var dte=pmsDate.split("-");
		  $("#txtPMSDate").val(dte[2]+"-"+dte[1]+"-"+dte[0]);
	   });

	$(function() 
	{
		//var arrivalTime=session.getAttribute("PMSCheckInTime");
		//var departureTime=session.getAttribute("PMSCheckOutTime");
		
		
		$('#txtArrivalTime').timepicker();
		$('#txtDepartureTime').timepicker();
		
		$('#txtArrivalTime').timepicker({
		        'timeFormat':'H:i:s'
		});
		$('#txtDepartureTime').timepicker({
		        'timeFormat':'H:i:s'
		});
		
		$('#txtArrivalTime').timepicker('setTime', new Date());
		$('#txtDepartureTime').timepicker('setTime', new Date());
		
		$("#txtArrivalDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtArrivalDate").datepicker('setDate', 'today');
		
		$("#txtDepartureDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtDepartureDate").datepicker('setDate', 'today');
		
		$("#txtCancelDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtCancelDate").datepicker('setDate', 'today');
		
		$("#txtConfirmDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtConfirmDate").datepicker('setDate', 'today');
		
		
		$('a#baseUrl').click(function() 
		{
			if($("#txtReservationNo").val().trim()=="")
			{
				alert("Please Select Reservation No ");
				return false;
			}
			window.open('attachDoc.html?transName=frmReservation.jsp&formName=Reservation &code='+$('#txtReservationNo').val(),"mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
		});		
		
		
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
				%> alert("Data Save successfully\n\n"+message);
				var AdvAmount='';
				var isOk=confirm("Do You Want to pay Advance Amount ?");
				if(isOk)
 				{
					var checkAgainst="Reservation";
					AdvAmount='<%=session.getAttribute("AdvanceAmount").toString()%>';
	    			window.location.href=getContextPath()+"/frmPMSPaymentAdvanceAmount.html?AdvAmount="+AdvAmount ;
	    			session.removeAttribute("AdvanceAmount");
	    			
 				}<%
			}
		}%>
		
		var resNo='';
		<%if (session.getAttribute("ResNo") != null) 
		{
			%>
			resNo='<%=session.getAttribute("ResNo").toString()%>';
			funSetReservationNo(resNo);
		    <%
		    session.removeAttribute("ResNo");
		}%>
	});

	
	function funSetData(code){

		switch(fieldName){

			case 'ReservationNo' : 
				funSetReservationNo(code);
				break;
				
			case 'PropertyCode' : 
				funSetPropertyCode(code);
				break;
				
			case 'guestCode' : 
				funSetGuestCode(code);
				break;
				
			case 'CorporateCode' : 
				funSetCorporateCode(code);
				break;
				
			case 'BookingTypeCode' : 
				funSetBookingTypeCode(code);
				break;
				
			case 'BillingInstCode' : 
				funSetBillingInstCode(code);
				break;
				
			case 'BookerCode' : 
				funSetBookerCode(code);
				break;
				
			case 'business' : 
				funSetBusinessSourceCode(code);
				break;
				
			case 'AgentCode' : 
				funSetAgentCode(code);
				break;
				
			case 'roomCode' : 
				funSetRoomNo(code);
				break;
			
			case 'extraBed' : 
				funSetExtraBed(code);
				break;
		}
	}


	function funSetReservationNo(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadReservation.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strReservationNo!="Invalid")
		    	{
		    		funFillHdData(response);
		    	}
		    	else
			    {
			    	alert("Invalid Reservation No");
			    	$("#txtReservationNo").val("");
			    	$("#txtReservationNo").focus();
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

	
	function funSetPropertyCode(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadPropertyCode.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 
				
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

	
	function funSetGuestCode(code){
			
		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadGuestCode.html?guestCode=" + code,
			dataType : "json",
			success : function(response){ 
				funSetGuestInfo(response);
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

	
	function funSetCorporateCode(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadCorporateCode.html?corpcode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strCorporateCode=='Invalid Code')
	        	{
	        		alert("Invalid Agent Code");
	        		$("#txtCorporateCode").val('');
	        	}
	        	else
	        	{
	        		$("#txtCorporateCode").val(response.strCorporateCode);
	        		$("#lblCorporateDesc").text(response.strCorporateDesc);
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

	
	function funSetBookingTypeCode(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadBookingTypeCode.html?bookingType=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strBookingTypeCode=='Invalid Code')
	        	{
	        		alert("Invalid Agent Code");
	        		$("#txtBookingTypeCode").val('');
	        	}
	        	else
	        	{
	        		$("#txtBookingTypeCode").val(response.strBookingTypeCode);
	        	    $("#lblBookingTypeDesc").text(response.strBookingTypeDesc);
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


	function funSetBillingInstCode(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadBillingInstCode.html?billingInstructions=" + code,
			dataType : "json",
			success : function(response){
				$("#txtBillingInstCode").val(response.strBillingInstCode);
        	    $("#lblBillingInstDesc").text(response.strBillingInstDesc);
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

	
	function funSetBookerCode(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadBookerCode.html?bookerCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strBookerCode=='Invalid Code')
	        	{
	        		alert("Invalid Agent Code");
	        		$("#txtBookerCode").val('');
	        	}
	        	else
	        	{
	        		$("#txtBookerCode").val(response.strBookerCode);
	        		$("#lblBookerName").text(response.strBookerName);
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

	
	function funSetBusinessSourceCode(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadBusinessMasterData.html?businessCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strBusinessSourceCode=='Invalid Code')
	        	{
	        		alert("Invalid Business Code");
	        		$("#txtBusinessSourceCode").val('');
	        	}
	        	else
	        	{
	        		$("#txtBusinessSourceCode").val(response.strBusinessSourceCode);
		        	$("#lblBusinessSourceName").text(response.strDescription);
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

	
	function funSetAgentCode(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadAgentCode.html?agentCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strAgentCode=='Invalid Code')
	        	{
	        		alert("Invalid Agent Code");
	        		$("#txtAgentCode").val('');
	        	}
	        	else
	        	{
	        		$("#txtAgentCode").val(response.strAgentCode);
	        		$("#lblAgentName").text(response.strDescription);
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

	
	
	function funSetRoomType(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadRoomTypeMasterData.html?roomCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strAgentCode=='Invalid Code')
	        	{
	        		alert("Invalid Room Type");
	        		$("#lblRoomType").text('');
	        	}
	        	else
	        	{					        	    	        		
	        		$("#lblRoomType").text(response.strRoomTypeDesc);
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
	
	
	
	function funSetRoomNo(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadRoomMasterData.html?roomCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strAgentCode=='Invalid Code')
	        	{
	        		alert("Invalid Room No");
	        		$("#txtRoomNo").val('');
	        	}
	        	else
	        	{
	        		$("#txtRoomNo").val(response.strRoomCode);
	        		$("#lblRoomNo").text(response.strRoomDesc);
	        		funSetRoomType(response.strRoomTypeCode);
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
	
	
	
	function funSetExtraBed(code)
	{
		$("#txtExtraBed").val(code);
		var searchurl=getContextPath()+"/loadExtraBedMasterData.html?extraBedCode="+code;
		 $.ajax({
			    type: "GET",
			    url: searchurl,
			    dataType: "json",
			    success: function(response)
			    {
			        if(response.strExtraBedTypeCode=='Invalid Code')
			        {
			        	alert("Invalid ExtraBed Code");
			        	$("#txtExtraBed").val('');
			        }
			        else
			        {
				        $("#lblExtraBed").text(response.strExtraBedTypeDesc);
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
	
	
	
	function funSetGuestInfo(obj)
	{
		$("#txtGuestCode").val(obj.strGuestCode);
		$("#txtMobileNo").val(obj.lngMobileNo);
		$("#txtGFirstName").val(obj.strFirstName);
		$("#txtGMiddleName").val(obj.strMiddleName);
		$("#txtGLastName").val(obj.strLastName);
	}
	
	
	
	function funFillHdData(response)
	{
		$("#txtReservationNo").val(response.strReservationNo);
		$("#txtPropertyCode").val(response.strPropertyCode);
		
		$("#txtGuestCode").val(response.strGuestCode);
		$("#txtGuestPrefix").val(response.strGuestPrefix);
		$("#txtGFirstName").val(response.strFirstName);
		$("#txtGMiddleName").val(response.strMiddleName);
		$("#txtGLastName").val(response.strLastName);
		
		if(response.strCorporateCode!='')
		{
			$("#txtCorporateCode").val(response.strCorporateCode);
			funSetCorporateCode(response.strCorporateCode);
		}
		
		if(response.strBookingTypeCode!='')
		{
			$("#txtBookingTypeCode").val(response.strBookingTypeCode);
			funSetBookingTypeCode(response.strBookingTypeCode);
		}
		
		if(response.strBillingInstCode!='')
		{
			$("#txtBillingInstCode").val(response.strBillingInstCode);
			funSetBillingInstCode(response.strBillingInstCode);
		}
		
		if(response.strBookerCode!='')
		{
			$("#txtBookerCode").val(response.strBookerCode);
			funSetBookerCode(response.strBookerCode);
		}
		
		if(response.strBusinessSourceCode!='')
		{
			$("#txtBusinessSourceCode").val(response.strBusinessSourceCode);
			funSetBusinessSourceCode(response.strBusinessSourceCode);
		}
		
		if(response.strAgentCode!='')
		{
			$("#txtAgentCode").val(response.strAgentCode);
			funSetAgentCode(response.strAgentCode);
		}
		
		$("#txtRoomNo").val(response.strRoomNo);
		$("#lblRoomNo").text(response.strRoomDesc);
	    $("#txtExtraBed").val(response.strExtraBedCode);
	    $("#lblExtraBed").text(response.strExtraBedDesc);	    
	    $("#txtNoOfAdults").val(response.intNoOfAdults);
	    $("#txtNoOfChild").val(response.intNoOfChild);
				
		$("#txtArrivalDate").val(response.dteArrivalDate);
		$("#txtDepartureDate").val(response.dteDepartureDate);

		$("#txtArrivalTime").val(response.tmeArrivalTime);
		$("#txtDepartureTime").val(response.tmeDepartureTime);
		$("#txtNoOfNights").val(response.intNoOfNights);
		$("#txtNoOfBookingRoom").val(response.intNoRoomsBooked);
		$("#txtContactPerson").val(response.strContactPerson);
		$("#txtEmailId").val(response.strEmailId);
		$("#txtMobileNo").val(response.lngMobileNo);
		
		$("#txtRemarks").val(response.strRemarks);
		$("#txtCancelDate").val(response.dteCancelDate);
		$("#txtConfirmDate").val(response.dteConfirmDate);

		funFillDtlGrid(response.listReservationDetailsBean);
	}
	
	
	
	function funFillDtlGrid(resListResDtlBean)
	{
		funRemoveProductRows();
		$.each(resListResDtlBean, function(i,item)
		{
			funAddDetailsRow(resListResDtlBean[i].strGuestName,resListResDtlBean[i].strGuestCode,resListResDtlBean[i].lngMobileNo
				,resListResDtlBean[i].strRoomType,resListResDtlBean[i].strRoomNo,resListResDtlBean[i].strRoomDesc
				,resListResDtlBean[i].strExtraBedCode,resListResDtlBean[i].strExtraBedDesc,resListResDtlBean[i].strPayee);
		});
	}
	
	
	
//Delete a All record from a grid
	function funRemoveProductRows()
	{
		var table = document.getElementById("tblResDetails");
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
	    var table = document.getElementById("tblResDetails");
	    table.deleteRow(index);
	}
	
	
	
// Reset Detail Fields
	function funResetDetailFields()
	{
		$("#txtGuestCode").val('');
		$("#txtMobileNo").val('');
		$("#txtGFirstName").val('');
		$("#txtGMiddleName").val('');
		$("#txtGLastName").val('');
		
		$("#lblRoomType").text('');
	    $("#txtRoomNo").val('');
	    $("#lblRoomNo").text('');
	    $("#txtExtraBed").val('');
	    $("#lblExtraBed").text('');	    
	}
	
	

// Get Detail Info From detail fields and pass them to function to add into detail grid
	function funGetDetailsRow() 
	{
	/*
		var table = document.getElementById("tblResDetails");
		var rowCount = table.rows.length;
		if(rowCount>0)
		{
			alert('Only One Guest is Allowed at a time.');
			return;
		}*/
	
		var guestCode=$("#txtGuestCode").val().trim();
		var mobileNo=$("#txtMobileNo").val().trim();
		var guestName=$("#txtGFirstName").val().trim()+" "+$("#txtGMiddleName").val().trim()+" "+$("#txtGLastName").val().trim();
		var roomType =$("#lblRoomType").text().trim();
		var roomNo =$("#txtRoomNo").val().trim();
		var roomDesc =$("#lblRoomNo").text().trim();
		var extraBedCode=$("#txtExtraBed").val();
		var extraBedDesc=$("#lblExtraBed").text();
		
		if(mobileNo=='')
		{
			alert('Enter Mobile No!!!');
			$("#txtMobileNo").focus();
			return;
		}
		else
		{
			var phoneno = /^\d{10}$/;
			if((mobileNo.match(phoneno)))
			{
			}
			else
			{
				alert("Invalid Mobile No");
			    return;
			}
		}
		
		if($("#txtGFirstName").val().trim()=='')
		{
			alert('Enter Guest First Name!!!');
			$("#txtGFirstName").focus();
			return;
		}		
		
	    funAddDetailsRow(guestName,guestCode,mobileNo,roomType,roomNo,roomDesc,extraBedCode,extraBedDesc,"N");
	}
	
	
// Function to add detail grid rows	
	function funAddDetailsRow(guestName,guestCode,mobileNo,roomType,roomNo,roomDesc,extraBedCode,extraBedDesc,payee)
	{
	    var table = document.getElementById("tblResDetails");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);

	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"60%\" name=\"listReservationDetailsBean["+(rowCount)+"].strGuestName\" id=\"strGuestName."+(rowCount)+"\" value='"+guestName+"' />";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"10%\" name=\"listReservationDetailsBean["+(rowCount)+"].lngMobileNo\" id=\"lngMobileNo."+(rowCount)+"\" value='"+mobileNo+"' />";
	    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"5%\" id=\"strRoomDesc."+(rowCount)+"\" value='"+roomDesc+"' />";
	    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"5%\" id=\"strExtraBedDesc."+(rowCount)+"\" value='"+extraBedDesc+"' />";
	    row.insertCell(4).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"10%\" name=\"listReservationDetailsBean["+(rowCount)+"].strRoomType\" id=\"strRoomType."+(rowCount)+"\" value='"+roomType+"' />";
	    if(payee=='Y')
	    	{
	    		row.insertCell(5).innerHTML= "<input id=\"cbItemCodeSel."+(rowCount)+"\" type=\"radio\" checked=\"checked\" class=\"Box payeeSel\" name=\"listReservationDetailsBean.strPayee\" size=\"2%\"   value=\"Y\" onClick=\"Javacsript:funRadioRow(this)\"  />";
	    	}else
	    	{
	    		row.insertCell(5).innerHTML= "<input id=\"cbItemCodeSel."+(rowCount)+"\" type=\"radio\" class=\"Box payeeSel\" name=\"listReservationDetailsBean.strPayee\" size=\"2%\" value=\"N\" onClick=\"Javacsript:funRadioRow(this)\"  />";
	    	}
	    
	    row.insertCell(6).innerHTML= "<input type=\"button\" class=\"deletebutton\" size=\"2%\" value = \"Delete\" onClick=\"Javacsript:funDeleteRow(this)\"/>";
	    
	    row.insertCell(7).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"1%\" name=\"listReservationDetailsBean["+(rowCount)+"].strGuestCode\" id=\"strGuestCode."+(rowCount)+"\" value='"+guestCode+"' type='hidden' />";
	    row.insertCell(8).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"1%\" name=\"listReservationDetailsBean["+(rowCount)+"].strRoomNo\" id=\"strRoomNo."+(rowCount)+"\" value='"+roomNo+"' type='hidden' />";
	    row.insertCell(9).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"1%\" name=\"listReservationDetailsBean["+(rowCount)+"].strExtraBedCode\" id=\"strExtraBedCode."+(rowCount)+"\" value='"+extraBedCode+"' type='hidden' />";
	    
	    funResetDetailFields();
	    
	    if(payee=='Y')
	    	{
	    	 	$("#hidPayee").val(guestCode);
	    	}
	}

	function funRadioRow(rowObj)
	{
		/* $( "input[type='radio']" ).prop({
			checked: false
			}); */
			var no=0;
			$('#tblResDetails tr').each(function() {
				
				if(document.getElementById("cbItemCodeSel."+no).checked == true)
					{
					  var gustcode = document.getElementById("strGuestCode."+no).value;
					  $("#hidPayee").val(gustcode);
					}
				no++;
			});
	}

	function funHelp(transactionName)
	{
		fieldName=transactionName;
		window.open("searchform.html?formname="+transactionName+"&searchText=","mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
		//window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
		
	
	function funValidateForm()
	{
		if($("#txtBookingTypeCode").val()=='')
		{
			alert("Please Select Booking Type");
			return false;
		}
		
		if($("#txtArrivalTime").val()=='')
		{
			alert("Please Enter Arrival Time");
			return false;
		}
		
		if($("#txtDepartureTime").val()=='')
		{
			alert("Please Enter Departure Time");
			return false;
		}
		
		if($("#hidPayee").val()=='')
		{
			alert("Please Select One Payee");
			return false;
		}
	/* 	if($("#txtRoomNo").val()=='')
		{
			alert('Select Room No!!!');
			$("#txtRoomNo").focus();
			return false;
		}
		 */
		if($("#txtNoOfAdults").val()=='')
		{
			alert('Enter No of Adults!!!');
			$("#txtNoOfAdults").focus();
			return false;
		}
		
		if($("#txtNoOfChild").val()=='')
		{
			alert('Enter No of Child!!!');
			$("#txtNoOfChild").focus();
			return false;
		}
		
		var table = document.getElementById("tblResDetails");
	    var rowCount = table.rows.length;
		if(rowCount==0)
		{
			alert("Please Enter Guest For Reservation");
			return false;
		}
		return true;
	}
	
</script>

</head>
<body>

	<div id="formHeading">
	<label>Reservation</label>
	</div>

	<br/>
	<br/>

	<s:form name="Reservation" method="POST" action="saveReservation.html">

		<table class="transTable">
		
			<tr>
				<th align="right" colspan="2"><label >PMS Date</label>&nbsp; &nbsp;<input id="txtPMSDate" style="width: 90px;font-weight: bold;" readonly="readonly" class="longTextBox"/></th>
				<th align="right" colspan="3"><a id="baseUrl" href="#"> Attach Documents</a>&nbsp; &nbsp; &nbsp;&nbsp;</th>
			    <th colspan="1">
			</tr>
			<tr>
			<th colspan="6">
			</tr>
		
			<tr>
				<td><label>Reservation No</label></td>
				<td>
					<s:input colspan="1" type="text" id="txtReservationNo" path="strReservationNo" cssClass="searchTextBox" ondblclick="funHelp('ReservationNo');"/>
				</td>
				
				<td><label>Property</label></td>
				<td colspan="2"><s:select id="strPropertyCode" path="strPropertyCode" items="${listOfProperty}" required="true" cssClass="BoxW200px"></s:select></td>
				
				<td><label id="lblPropName"></label></td>
			</tr>
			
			<tr>
				<td><label>Corporate</label></td>
				<td>
					<s:input colspan="1" type="text" id="txtCorporateCode" path="strCorporateCode" cssClass="searchTextBox" ondblclick="funHelp('CorporateCode');"/>
				</td>
				<td><label id="lblCorporateDesc"></label></td>
			
				<td ><label>Booking Type</label></td>
				<td>
					<s:input  type="text" id="txtBookingTypeCode" path="strBookingTypeCode" cssClass="searchTextBox" ondblclick="funHelp('BookingTypeCode');"/>
				</td>
				<td><label id="lblBookingTypeDesc"></label></td>
			</tr>
			
			<tr>
				<td><label>Arrival Date</label></td>
				<td><s:input colspan="1" type="text" id="txtArrivalDate" path="dteArrivalDate" cssClass="calenderTextBox" /></td>
			
				<td><label>Departure Date</label></td>
				<td><s:input colspan="1" type="text" id="txtDepartureDate" path="dteDepartureDate" cssClass="calenderTextBox" /></td>
			    <td colspan="2"></td>
			</tr>
			
			<tr>
				<td><label>Arrival Time</label></td>
				<td><s:input colspan="1" type="text" id="txtArrivalTime" path="tmeArrivalTime" cssClass="calenderTextBox" /></td>
			
				<td><label>Departure Time</label></td>
				<td><s:input colspan="1" type="text" id="txtDepartureTime" path="tmeDepartureTime" cssClass="calenderTextBox"  /></td>
				<td colspan="2"></td>
			</tr>
						
			<%-- <tr>
				<td width="10%"><label>Room No</label></td>
				<td><s:input colspan="1" type="text" id="txtRoomNo" name="txtRoomNo" ondblclick="funHelp('roomCode')" path="strRoomNo" cssClass="searchTextBox"/>
				<td width="10%"><label id="lblRoomNo"></label></td>
				<td width="10%"><label id="lblRoomType"></label></td>
			</tr>

			<tr>
				<td width="10%"><label>Extra Bed</label></td>
				<td><s:input type="text" id="txtExtraBed" name="txtExtraBed" path="strExtraBedCode" cssClass="searchTextBox" ondblclick="funHelp('extraBed')" /></td>
				<td width="10%"><label id="lblExtraBed"></label></td>
			</tr> --%>
			
			<tr>
				<td width="10%"><label>#Adult</label></td>
				<td><s:input id="txtNoOfAdults" name="txtNoOfAdults" path="intNoOfAdults" style="width: 38%;text-align: right;" type="number" min="1" step="1"   class="longTextBox" /></td>
				<td width="10%"><label>#Child</label></td>
				<td width="5%"><s:input id="txtNoOfChild" path="intNoOfChild" style="text-align: right;" type="number" min="0" step="1" name="txtNoOfChild" class="longTextBox" /></td>				
			</tr>
			
			<tr>
				<td><label>No Of Nights</label></td>
				<td>
					<s:input colspan="1" type="text" class="numeric" id="txtNoOfNights" path="intNoOfNights" cssClass="longTextBox" style="width: 38%;text-align: right;"/>
				</td>
				
				<td><label>Booking Rooms</label></td>
				<td>
					<s:input colspan="1" type="number" min="1" step="1" class="longTextBox" id="txtNoOfBookingRoom" path="intNoRoomsBooked" cssClass="longTextBox" style="text-align: right;"/>
				</td>
				
			
				
			</tr>
			
			<tr>
				<td><label>Email Id</label></td>
				<td><s:input colspan="1" type="text" id="txtEmailId" path="strEmailId" cssClass="longTextBox" /></td>
				<td><label>Contact Person</label></td>
				<td><s:input type="text" id="txtContactPerson" path="strContactPerson" cssClass="longTextBox" /></td>
				<td colspan="2"></td>
			</tr>
			
			<tr>
				<td><label>Billing Instructions</label></td>
				<td>
					<s:input colspan="1" type="text" id="txtBillingInstCode" path="strBillingInstCode" cssClass="searchTextBox" ondblclick="funHelp('BillingInstCode');"/>
				</td>
				<td><label id="lblBillingInstDesc"></label></td>
			
				<td><label>Booker</label></td>
				<td>
					<s:input colspan="1" type="text" id="txtBookerCode" path="strBookerCode" cssClass="searchTextBox" ondblclick="funHelp('BookerCode');"/>
				</td>
				<td><label id="lblBookerName"></label></td>
			</tr>
			
			<tr>
				<td><label>Business Source</label></td>
				<td>
					<s:input colspan="1" type="text" id="txtBusinessSourceCode" path="strBusinessSourceCode" cssClass="searchTextBox" ondblclick="funHelp('business');"/>
				</td>
				<td><label id="lblBusinessSourceName"></label></td>
			
				<td><label>Agent</label></td>
				<td>
					<s:input colspan="1" type="text" id="txtAgentCode" path="strAgentCode" cssClass="searchTextBox" ondblclick="funHelp('AgentCode');"/>
				</td>
				<td><label id="lblAgentName"></label></td>
			</tr>
			
			<tr>
				<td><label>Remarks</label></td>
				<td><s:input colspan="1" type="text" id="txtRemarks" path="strRemarks" cssClass="longTextBox" /></td>
				<td colspan="4"></td>
			</tr>
			
			<tr>
				<td><label>Cancel Date</label></td>
				<td><s:input colspan="1" type="text" id="txtCancelDate" path="dteCancelDate" cssClass="calenderTextBox" /></td>
			
				<td><label>Confirm Date</label></td>
				<td><s:input colspan="1" type="text" id="txtConfirmDate" path="dteConfirmDate" cssClass="calenderTextBox" /></td>
				<td colspan="2"></td>
			</tr>
		</table>

			<br>
			<br>
		
			<div >
			<table class="transTable">
				
				<tr>
					<td><label>Mobile No</label></td>
					<td width="10%"><input type="text" id="txtMobileNo" class="longTextBox" /></td>
					
					<td><label>Guest Code</label></td>
					<td width="10%"><input id="txtGuestCode" ondblclick="funHelp('guestCode');" class="searchTextBox" /></td>
					<td colspan="2"></td>
				</tr>
				
				<tr>
					<td width="5%"><label id="lblGFirstName">First Name</label></td>
					<td width="15%"><input type="text" id="txtGFirstName" class="longTextBox" /></td>
					
					<td width="5%"><label id="lblGMiddleName">Middle Name</label></td>
					<td width="15%"><input type="text" id="txtGMiddleName" class="longTextBox" /></td>
					
					<td width="1%"><label id="lblGLastName">Last Name</label></td>
					<td width="15%"><input type="text" id="txtGLastName" class="longTextBox" /></td>
				</tr>
				<tr>
				<td width="10%"><label>Room No</label></td>
				<td><input type="text" id="txtRoomNo" name="txtRoomNo" ondblclick="funHelp('roomCode')" Class="searchTextBox"/>
				<td width="10%"><label id="lblRoomNo"></label></td>
				<td width="10%"><label id="lblRoomType"></label></td>
				
				<td width="10%"><label>Extra Bed</label></td>
				<td><input type="text" id="txtExtraBed" name="txtExtraBed" Class="searchTextBox" ondblclick="funHelp('extraBed')" /></td>
				<td width="10%"><label id="lblExtraBed"></label></td>
				</tr>
				
				<tr>
					<td></td>
					<td>
						<input type="Button" value="Add" onclick="return funGetDetailsRow()" class="smallButton" />
					</td>
				</tr>
			
			</table>
		</div>

	
		<div class="dynamicTableContainer" style="height: 300px;">
			<table style="height: 28px; border: #0F0; width: 100%; font-size: 11px; font-weight: bold;">
				<tr bgcolor="#72BEFC">				
					<td style="width:25%;">Name</td>
					<td style="width:15%;">Mb No</td>
					<td style="width:10%;">Room No</td>
					<td style="width:5%;">Extra Bed</td>
					<td style="width:10%;">Room Type</td>
					<td style="width:2%;">Payee</td>
					<td style="width:0%;">Delete</td>
				</tr>
			</table>
		
			<div style="background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
				<table id="tblResDetails"
					style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll"
					class="transTablex col8-center">
					<tbody>
						<col style="width: 250px;">
						<col style="width: 140px;">
						<col style="width: 100px;">
						<col style="width: 50px;">
						<col style="width: 100px;">
						<col style="width: 50px;">
						
						<col style="display:none;">
						
					</tbody>
				</table>
			</div>
		</div>

		<br />
		<br />
		<p align="center">
			<input type="submit" value="Submit" tabindex="3" class="form_button" onclick="return funValidateForm();" />
			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/>
		</p>
		<s:input type="hidden" id="hidPayee" path="strPayeeGuestCode"></s:input>
		<br />
		<br />

	</s:form>
</body>
</html>
