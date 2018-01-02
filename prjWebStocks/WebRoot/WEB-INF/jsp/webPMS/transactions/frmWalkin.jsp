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
	var strDemo;	
	
//Initialize tab Index or which tab is Active
	$(document).ready(function() 
	{		
		$(".tab_content").hide();
		$(".tab_content:first").show();

		$("ul.tabs li").click(function() {
			$("ul.tabs li").removeClass("active");
			$(this).addClass("active");
			$(".tab_content").hide();
			var activeTab = $(this).attr("data-state");
			$("#" + activeTab).fadeIn();
		});
			
		$(document).ajaxStart(function(){
		    $("#wait").css("display","block");
		});
		$(document).ajaxComplete(function(){
		   	$("#wait").css("display","none");
		});
		
		 var pmsDate='<%=session.getAttribute("PMSDate").toString()%>';
		  var dte=pmsDate.split("-");
		  $("#txtPMSDate").val(dte[2]+"-"+dte[1]+"-"+dte[0]);
		  
		  
	});
	
	

	$(function()
	{
		$("#txtWalkinDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtWalkinDate").datepicker('setDate', 'today');
		
		$("#txtCheckOutDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtCheckOutDate").datepicker('setDate', 'today');
		
		$("#txtWalkinTime").timepicker();
		$("#txtCheckOutTime").timepicker();
		
		$("#txtDOB").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtDOB").datepicker('setDate', 'today');
				
		$("#txtPassportExpiryDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtPassportExpiryDate").datepicker('setDate', 'today');
		
		$("#txtPassportIssueDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtPassportIssueDate").datepicker('setDate', 'today');
		
		
		$('a#baseUrl').click(function() 
		{
			if($("#txtWalkinNo").val().trim()=="")
			{
				alert("Please Select Wakin No ");
				return false;
			}
			
			
		   	window.open('attachDoc.html?transName=frmWalkin.jsp&formName=Walkin &code='+$('#txtWalkinNo').val(),"mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
		});		
	});
	

	function funSetData(code){

		switch(fieldName){

			case 'WalkinNo' : 
				funSetWalkinNo(code);
				//funSetWalkinInformation(code);unHelp
				
				break;
				
			case 'CorporateCode' : 
				funSetCorporateCode(code);
				break;
				
			case 'BookerCode' : 
				funSetBookerCode(code);
				break;
				
			case 'AgentCode' : 
				funSetAgentCode(code);
				break;
				
			case 'guestCode' : 
				funSetGuestCode(code);
				break;
				
			case 'roomType' : 
				funSetRoomTypeData(code);
				break;
			
			case 'mobileNo'	:
				funSetMobileData(code);
				break;
				
			case 'roomCode'	:
				funSetRoomNo(code);
				break;
				
			case 'extraBed' : 
				funSetExtraBed(code);
				break;
			
		}
	}

	
	function funSetRoomNo(roomCode)
	{
		var searchUrl=getContextPath()+"/loadRoomInformation.html?roomCode="+roomCode;
		$.ajax({
			url:searchUrl,
			type :"GET",
			dataType: "json",
	        success: function(response)
	        {
	        	if(response.strRoomCode=='Invalid Code')
	        	{
	        		alert("Invalid Room Code");
	        		$("#txtRoomNo").val('');
	        	}
	        	else
	        	{
	        		$("#txtRoomNo").val(response[0][0]);
	        		$("#lblRoomDesc").text(response[0][1]);
	        		$("#lblRoomType").text(response[0][2]);
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
	

	function funSetWalkinNo(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadWalkinData.html?docCode=" + code,
			dataType : "json",
			success : function(response)
			{
				if(response.strWalkinNo=='Invalid Code')
	        	{
	        		alert("Invalid Walkin No");
	        		$("#txtWalkinNo").val('');
	        	}
	        	else
	        	{
	        		funFillWalkinHdData(response);
	        	}
			},
			error : function(e)
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

	
	function funSetWalkinInformation(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadWalkinInformation.html?docCode=" + code,
			dataType : "json",
			success : function(response)
			{ 
				if(response.strWalkinNo=='Invalid Code')
	        	{
	        		alert("Invalid Walkin No");
	        		$("#txtWalkinNo").val('');
	        	}
	        	else
	        	{
	        		funFillWalkinHdData(response);
	        	}
			},
			error : function(e)
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

	
	
	function funFillWalkinHdData(response)
	{
		$("#txtWalkinNo").val(response.strWalkinNo);
    	$("#txtWalkinTime").val(response.tmeWalkinTime);
		$("#txtWalkinDate").val(response.dteWalkinDate);
		$("#txtCheckOutDate").val(response.dteCheckOutDate);
		$("#txtCheckOutTime").val(response.tmeCheckOutTime);
		$("#txtCorporateCode").val(response.strCorporateCode);
		$("#txtBookerCode").val(response.strBookerCode);
		$("#txtAgentCode").val(response.strAgentCode);
		$("#txtNoOfNights").val(response.intNoOfNights);
		$("#txtRemarks").val(response.strRemarks);
		$("#txtRoomNo").val(response.strRoomNo);
		$("#lblRoomDesc").text(response.strRoomDesc);
	    $("#txtExtraBed").val(response.strExtraBedCode);
	    $("#lblExtraBed").text(response.strExtraBedDesc);
	    $("#txtNoOfAdults").val(response.intNoOfAdults);
	    $("#txtNoOfChild").val(response.intNoOfChild);
		
		funFillWalkinDtlGrid(response.listWalkinDetailsBean);
	}
	
	
	/*
	function funFillWalkinHdData(response)
	{
		$("#txtWalkinNo").val(response[0][0]);
		$("#txtWalkinDate").val(response[0][1]);
		$("#txtWalkinTime").val(response[0][2]);
		$("#txtCheckOutDate").val(response[0][3]);
		$("#txtCheckOutTime").val(response[0][4]);
		$("#txtCorporateCode").val(response[0][5]);
		$("#txtBookerCode").val(response[0][6]);
		$("#txtAgentCode").val(response[0][7]);
	    $("#txtNoOfNights").val(response[0][8]);
	
		funFillWalkinDtlGrid(response.listWalkinDetailsBean);
		funFillGuestMasterDetails(response.strGuestCode);
	}
	*/
	
	function funFillWalkinDtlGrid(resListWalkinDetailsBean)
	{
		funRemoveProductRows();
		$.each(resListWalkinDetailsBean, function(i,item)
        {
			funAddDetailsRow(resListWalkinDetailsBean[i].strRoomNo,resListWalkinDetailsBean[i].strRoomDesc,resListWalkinDetailsBean[i].strRoomType,
				resListWalkinDetailsBean[i].strGuestCode,resListWalkinDetailsBean[i].strGuestFirstName,resListWalkinDetailsBean[i].strGuestMiddleName,
				resListWalkinDetailsBean[i].strGuestLastName,resListWalkinDetailsBean[i].lngMobileNo,resListWalkinDetailsBean[i].intNoOfAdults
				,resListWalkinDetailsBean[i].intNoOfChild,resListWalkinDetailsBean[i].strExtraBedCode,resListWalkinDetailsBean[i].strExtraBedDesc);
        });
	}
	
	
	function funFillGuestMasterDetails(guestCode)
	{
		funSetGuestCode(guestCode);
	}
	
	
	/**
	* Get and Set data from help file and load data Based on Selection Passing Value(Room Master Code)
	**/
	
	function funSetRoomTypeData(code)
	{
		var searchurl=getContextPath()+"/loadRoomTypeMasterData.html?roomCode="+code;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strRoomTypeCode=='Invalid Code')
			        	{
			        		alert("Invalid Walikn No");
			        		$("#txtRoomType").val('');
			        	}
			        	else
			        	{
				        	$("#txtRoomType").val(response.strRoomTypeDesc);
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





	function funSetCorporateCode(code)
	{
		$("#txtCorporateCode").val(code);
		
		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadCorporateCode.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 

			},
			error : function(e){

			}
		});
	}

	function funSetBookerCode(code)
	{
		$("#txtBookerCode").val(code);

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadBookerCode.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 

			},
			error : function(e){

			}
		});
	}

	function funSetAgentCode(code)
	{
		$("#txtAgentCode").val(code);
		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadAgentCode.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 

			},
			error : function(e){

			}
		});
	}

	function funSetGuestCode(code)
	{
		var searchurl=getContextPath()+"/loadGuestInformation.html?guestCode="+code;
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
		        		$("#txtGuestCode").val(response[0][0]);
		        		$("#txtGuestFirstName").val(response[0][1]);
		        		 $("#txtGuestMiddleName").val(response[0][2]);
		        		 $("#txtGuestLastName").val(response[0][3]);
		        		 $("#txtMobileNo").val(response[0][4]);
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
	* Get and Set data from help file and load data Based on Selection Passing Value(Mobile No)
	**/
	
	function funSetMobileData(code)
	{
		var searchurl=getContextPath()+"/loadMobileNo.html?mobileNo="+code;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strRoomTypeCode=='Invalid Code')
			        	{
			        		alert("Invalid Mobile No");
			        		$("#txtMobileNo").val('');
			        	}
			        	else
			        	{
				        	$("#txtGuestCode").val(response.strGuestCode);
				        	
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
	
	
	
	function funHelp(transactionName)
	{	
		fieldName=transactionName;
		window.open("searchform.html?formname="+transactionName+"&searchText=","mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
		//window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
	
	
	//Function to Delete Selected Row From Grid
	function funDeleteRow(obj)
	{
	    var index = obj.parentNode.parentNode.rowIndex;
	    var table = document.getElementById("tblwalkindtl");
	    table.deleteRow(index);
	}

	
	
	//Delete a All record from a grid
	function funRemoveProductRows()
	{
		var table = document.getElementById("tblwalkindtl");
		var rowCount = table.rows.length;
		while(rowCount>0)
		{
			table.deleteRow(0);
			rowCount--;
		}
	}
	
	/**
	* On Blur Event on mobileNo Textfield
	**/
	$('#txtMobileNo').blur(function() 
	{
			var code = $('#txtMobileNo').val();
			/*if (code.trim().length > 0 && code !="?" && code !="/")
			{
				funSetData(code);
			}
			*/
	});
	
	
	
// Get Detail Info From detail fields and pass them to function to add into detail grid
	function funGetDetailsRow() 
	{
		var roomNo =$("#txtRoomNo").val().trim();
		var roomDesc=($("#lblRoomDesc").text());
		var roomType=($("#lblRoomType").text());
		var guestCode=$("#txtGuestCode").val().trim();
		var guestFirstName=$("#txtGuestFirstName").val().trim();
		var guestMiddleName=$("#txtGuestMiddleName").val().trim();
		var guestLastName=$("#txtGuestLastName").val().trim();
		var mobileNo=$("#txtMobileNo").val().trim();
		var extraBedCode=$("#txtExtraBed").val();
		var extraBedDesc=$("#lblExtraBed").text();
	    var noOfAdults=$("#txtNoOfAdults").val();
	    var noOfChild=$("#txtNoOfChild").val();
	    
	    
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
		
		if($("#txtGuestFirstName").val().trim()=='')
		{
			alert('Enter Guest First Name!!!');
			$("#txtGuestFirstName").focus();
			return;
		}
		
		if(roomNo=='')
		{
			alert('Select Room No!!!');
			$("#txtRoomNo").focus();
			return;
		}
		
		if(noOfAdults=='')
		{
			alert('Enter No of Adults!!!');
			$("#txtNoOfAdults").focus();
			return;
		}
		
		if(noOfChild=='')
		{
			alert('Enter No of Child!!!');
			$("#txtNoOfChild").focus();
			return;
		}
	    	 
	    funAddDetailsRow(roomNo,roomDesc,roomType,guestCode,guestFirstName,guestMiddleName,guestLastName,mobileNo,noOfAdults,noOfChild,extraBedCode,extraBedDesc);	   
	}
	
	// Function to add detail grid rows	
	function funAddDetailsRow(roomNo,roomDesc,roomType,guestCode,guestFirstName,guestMiddleName,guestLastName,mobileNo,noOfAdults,noOfChild,extraBedCode,extraBedDesc) 
	{
	    var table = document.getElementById("tblwalkindtl");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    	    
	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"12%\" name=\"listWalkinDetailsBean["+(rowCount)+"].strRoomDesc\" id=\"strRoomDesc."+(rowCount)+"\" value='"+roomDesc+"' />";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"12%\" id=\"strExtraBedDesc."+(rowCount)+"\" value='"+extraBedDesc+"' />";
	    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"12%\" name=\"listWalkinDetailsBean["+(rowCount)+"].strRoomType\" id=\"strRoomType."+(rowCount)+"\" value='"+roomType+"' />";	    
	    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"12%\" name=\"listWalkinDetailsBean["+(rowCount)+"].strGuestFirstName\" id=\"strGuestFirstName."+(rowCount)+"\" value='"+guestFirstName+"' />";
	    row.insertCell(4).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"12%\" name=\"listWalkinDetailsBean["+(rowCount)+"].strGuestMiddleName\" id=\"strGuestMiddleName."+(rowCount)+"\" value='"+guestMiddleName+"' />";
	    row.insertCell(5).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"10%\" name=\"listWalkinDetailsBean["+(rowCount)+"].strGuestLastName\" id=\"strGuestLastName."+(rowCount)+"\" value='"+guestLastName+"' />";
	    row.insertCell(6).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"7%\" name=\"listWalkinDetailsBean["+(rowCount)+"].lngMobileNo\" id=\"lngMobileNo."+(rowCount)+"\" value='"+mobileNo+"' />";
	    row.insertCell(7).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"2%\" name=\"listWalkinDetailsBean["+(rowCount)+"].intNoOfAdults\" id=\"intNoOfAdults."+(rowCount)+"\" value='"+noOfAdults+"' />";
	    row.insertCell(8).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"2%\" name=\"listWalkinDetailsBean["+(rowCount)+"].intNoOfChild\" id=\"intNoOfChild."+(rowCount)+"\" value='"+noOfChild+"' />";
	    
	    row.insertCell(9).innerHTML= "<input type=\"button\" class=\"deletebutton\" size=\"5%\" value = \"Delete\" onClick=\"Javacsript:funDeleteRow(this)\"/>";
	    row.insertCell(10).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"2%\" name=\"listWalkinDetailsBean["+(rowCount)+"].strGuestCode\" id=\"strGuestCode."+(rowCount)+"\" value='"+guestCode+"' type='hidden' />";
	    row.insertCell(11).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"2%\" name=\"listWalkinDetailsBean["+(rowCount)+"].strRoomNo\" id=\"strRoomNo."+(rowCount)+"\" value='"+roomNo+"' type='hidden' />";
	    row.insertCell(12).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"2%\" name=\"listWalkinDetailsBean["+(rowCount)+"].strExtraBedCode\" id=\"strExtraBedCode."+(rowCount)+"\" value='"+extraBedCode+"' type='hidden' />";
	    
	    funResetWalkinDetailFields();
	}
	
	// Reset Walkin Detail Fields
	function funResetWalkinDetailFields()
	{		
	    $("#txtGuestCode").val('');
	    $("#txtGuestFirstName").val('');
	    $("#txtGuestMiddleName").val('');
	    $("#txtGuestLastName").val('');
	    $("#txtMobileNo").val('');
	}
	
	function fun1()
	{
		//alert("saved");
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
			
			var AdvAmount='';
			var isOk=confirm("Do You Want to pay Advance Amount ?");
			if(isOk)
				{
				var checkAgainst="Walkin";
				AdvAmount='<%=session.getAttribute("AdvanceAmount").toString()%>';
				window.location.href=getContextPath()+"/frmPMSPaymentAdvanceAmount.html?AdvAmount="+AdvAmount ;
    			session.removeAttribute("AdvanceAmount");
//					session.removeAttribute("Against");
			
				}
		<%
		}}%>

	});
</script>



</head>
<body>

	<div id="formHeading">
	<label>Walk In</label>
	</div>

<br/>


	<s:form name="Walkin" method="GET" action="saveWalkin.html">
	
<!-- 		<table style="border: 0px solid black; width: 100%; height: 100%; margin-left: auto; margin-right: auto; background-color: #C0E4FF;"> -->
		<table class="transTable">
			<tr>
				<th ><label >PMS Date</label>&nbsp; &nbsp;<input id="txtPMSDate" style="width: 90px;font-weight: bold;" readonly="readonly" class="longTextBox"/></th>
				<th align="right"><a id="baseUrl" href="#"> Attach Documents</a>&nbsp; &nbsp; &nbsp;&nbsp;</th>
			   
			</tr>
			
			<tr>
				<td>
				
				<div id="tab_container" style="height: 580px">
				<ul class="tabs">
					<li data-state="tab1" style="width: 6%; padding-left: 2%">General</li>
					<li data-state="tab2" style="width: 8%; padding-left: 1%">Guest Details</li>
				</ul>
							
				<!-- General Tab Start -->
				<div id="tab1" class="tab_content" style="height: 400px">
					<br>
			
					<table class="transTable">
					<tr>
						<td>
							<label>Walkin No</label>
						</td>
						<td>
							<s:input colspan="3" type="text" id="txtWalkinNo" path="strWalkinNo" cssClass="searchTextBox" ondblclick="funHelp('WalkinNo');"/>
						</td><td colspan="3"></td>
					</tr>
					<tr>
						<td>
							<label>Walkin Date</label>
						</td>
						<td>
							<s:input colspan="3" type="text" id="txtWalkinDate" path="dteWalkinDate"  cssClass="calenderTextBox" />
						</td>
						<td>
							<label>Walkin Time</label>
						</td>
						<td>
						<s:input colspan="3" type="text" id="txtWalkinTime" path="tmeWalkinTime"  cssClass="calenderTextBox" />
							
						</td>
					</tr>
					
					<tr>
						<td>
							<label>Check-Out Date</label>
						</td>
						<td>
							<s:input colspan="3" type="text" id="txtCheckOutDate" path="dteCheckOutDate" cssClass="calenderTextBox" />
						</td>
						
						<td>
							<label>Check-Out Time</label>
						</td>
						<td>
							<s:input colspan="3" type="text" id="txtCheckOutTime" path="tmeCheckOutTime"  cssClass="calenderTextBox" />				
						</td>
					</tr>
					
					<tr>
						<td>
							<label>Corporate Code</label>
						</td>
						<td>
							<s:input colspan="3" type="text" id="txtCorporateCode" path="strCorporateCode" cssClass="searchTextBox" ondblclick="funHelp('CorporateCode');"/>
						</td>
						
						<td>
							<label>Booker Code</label>
						</td>
						<td>
							<s:input colspan="3" type="text" id="txtBookerCode" path="strBookerCode" cssClass="searchTextBox" ondblclick="funHelp('BookerCode');"/>
						</td>
					</tr>
					
					<tr>
						<td>
							<label>Agent Code</label>
						</td>
						<td>
							<s:input colspan="3" type="text" id="txtAgentCode" path="strAgentCode" cssClass="searchTextBox" ondblclick="funHelp('AgentCode');"/>
						</td>
						<td colspan="3"></td>
					</tr>
					
					<tr>
						<td width="10%"><label>Room No</label></td>
						<td><s:input colspan="1" type="text" id="txtRoomNo" name="txtRoomNo" ondblclick="funHelp('roomCode')" path="strRoomNo" cssClass="searchTextBox"/>
						<td width="10%"><label id="lblRoomDesc"></label></td>
						<td width="10%"><label id="lblRoomType"></label></td>
					</tr>
		
					<tr>
						<td width="10%"><label>Extra Bed</label></td>
						<td><s:input type="text" id="txtExtraBed" name="txtExtraBed" path="strExtraBedCode" cssClass="searchTextBox" ondblclick="funHelp('extraBed')" /></td>
						<td width="10%"><label id="lblExtraBed"></label></td>
					</tr>
					
					<tr>
						<td width="10%"><label>#Adult</label></td>
						<td><s:input id="txtNoOfAdults" name="txtNoOfAdults" path="intNoOfAdults" type="number" min="0" step="1" class="longTextBox" style="text-align: right;width: 117px;"/></td>
						<td width="10%"><label>#Child</label></td>
						<td width="5%"><s:input id="txtNoOfChild" path="intNoOfChild" type="number" min="0" step="1" name="txtNoOfChild" class="longTextBox" style="text-align: right;width: 117px;"/></td>				
					</tr>
					
					
					<tr>
						<td>
							<label>No Of Nights</label>
						</td>
						<td>
							<s:input colspan="3" type="text" class="numeric" id="txtNoOfNights" path="intNoOfNights" cssClass="longTextBox" style="text-align: right;width: 117px;" />
						</td>
						<td>
							<label>Remarks</label>
						</td>
						<td>
							<s:input colspan="3" type="text" id="txtRemarks" path="strRemarks" cssClass="longTextBox" />
						</td>						
					</tr>
				</table>
				
				<br>
			
				<div >
				<table class="transTable">				
				  	<tr>
					    <td width="45%"><label>Guest Code</label></td>
					   	<td width="60%"><input id="txtGuestCode"  ondblclick="funHelp('guestCode')" class="searchTextBox" /></td>
						
						<td width="5%"><label>Mobile No</label></td>
						<td width="15%"><input id="txtMobileNo"  name="txtMobileNo"  class="remarkTextBox" /></td>					
				  		<td colspan="2"></td>
				  	</tr>
						
				 	<tr>
				    	<td width="15%"><label>Guest Name</label></td>
					 	<td width="10%"><input id="txtGuestFirstName"  name="txtGuestFirstName"  class="remarkTextBox"/></td>
				     	<td width="10%"><input id="txtGuestMiddleName"  name="txtGuestMiddleName"  class="remarkTextBox" /></td>
					 	<td width="10%"><input id="txtGuestLastName"  name="txtGuestLastName"  class="remarkTextBox" /></td>						
				 		<td colspan="3"></td>
				 	</tr>
						
					<tr>						
						<td colspan="3">
							<input type="Button" value="Add" onclick="return funGetDetailsRow()" class="smallButton" />
						</td>						
					</tr>
				
				</table>
			</div>
			<br>
			
			<div class="dynamicTableContainer" style="height: 200px;">
				<table
					style="height: 28px; border: #0F0; width: 100%; font-size: 11px; font-weight: bold;">
					<tr bgcolor="#72BEFC">
						<td style="width:12%;">Room Desc</td>
						<td style="width:12%;">Extra Bed</td>
						<td style="width:12%;">Room Type</td>
						<td style="width:12%;">First Name</td>
						<td style="width:12%;">Middle Name</td>
						<td style="width:10%;">Last Name</td>
						<td style="width:7%;">Mobile No</td>
						<td style="width:2%;">No Of Adults</td>
						<td style="width:2%;">No Of Child</td>
						<td style="width:5%;">Delete</td>
					</tr>
				</table>
				
				<div style="background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
					<table id="tblwalkindtl"
						style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll"
						class="transTablex col8-center">
						<tbody>					
							
							<col style="width:12%">
							<col style="width:12%">
							<col style="width:12%">
							<col style="width:12%">
							<col style="width:12%">
							<col style="width:10%">
							<col style="width:7%">
							<col style="width:2%">
							<col style="width:2%">
							<col style="width:5%">
							<col style="display:none;">
						</tbody>
					</table>
				</div>
			</div>	
			</div>
						<!--General Tab End  -->
						
						
			<!-- Guest Details Tab Start -->
			<div id="tab2" class="tab_content" style="height: 400px">
			<br> <br>
			 <table class="transTable">
						
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
				
				
				</div>
			<!--Personal Tab End  -->			
				</div>	
						
							
				</td>
			</tr>
		</table>
		<br>
		<br>
		
		<p align="center">
			<input type="submit" value="Submit" tabindex="3" class="form_button" />
			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/>
		</p>	
		
		<br>
		<br>

	</s:form>
	
		
</body>
</html>
