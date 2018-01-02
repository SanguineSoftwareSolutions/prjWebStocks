<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
<script type="text/javascript">
	
	var fieldName,gridHelpRow;

	$(function() 
	{
		$("#txtArrivalTime").timepicker();
		$("#txtDepartureTime").timepicker();
		
		$("#txtArrivalDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtArrivalDate").datepicker('setDate', 'today');
		
		$("#txtDepartureDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtDepartureDate").datepicker('setDate', 'today');
		
		//$("#txtArrivalTime").prop('disabled',true);
		//$("#txtDepartureTime").prop('disabled',true);
		//$("#txtArrivalDate").prop('disabled',true);
		//$("#txtDepartureDate").prop('disabled',true);
		 
		$('a#baseUrl').click(function() 
		{
			if($("#txtCheckInNo").val().trim()=="")
			{
				alert("Please Select CheckIn No ");
				return false;
			}
			window.open('attachDoc.html?transName=frmCheckIN.jsp&formName=CheckIn &code='+$('#txtCheckInNo').val(),"mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
		});
				
		var checkInNo='';
		<%if (session.getAttribute("checkInNo") != null) 
		{
			%>
			checkInNo='<%=session.getAttribute("checkInNo").toString()%>';
			funSetCheckInData(checkInNo);
		    <%
   		 	session.removeAttribute("checkInNo");
		}%>
		
		 var pmsDate='<%=session.getAttribute("PMSDate").toString()%>';
		  var dte=pmsDate.split("-");
		  $("#txtPMSDate").val(dte[2]+"-"+dte[1]+"-"+dte[0]);
	});

	
	function funSetData(code){

		switch(fieldName){

			case 'RegistrationNo' : 
				funSetRegistrationNo(code);
				break;
				
			case 'ReservationNo' : 
				funSetReservationNo(code);
				break;
				
			case 'WalkinNo' : 
				funSetWalkinNo(code);
				break;
				
			case 'checkIn' : 
				funSetCheckInData(code);
				break;
				
			case 'roomCode' : 
				funSetRoomNo(code,gridHelpRow);
				break;
			
			case 'extraBed' : 
				funSetExtraBed(code,gridHelpRow);
				break;
				
				
		}
	}

	
	
	function funSetCheckInData(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadCheckInData.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strCheckInNo!="Invalid")
		    	{
					funFillCheckInHdData(response);
		    	}
		    	else
			    {
			    	alert("Invalid Check In No");
			    	$("#txtCheckInNo").val("");
			    	$("#txtCheckInNo").focus();
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
	
	
	function funFillCheckInHdData(response)
	{
		$("#txtCheckInNo").val(response.strCheckInNo);
		$("#txtRegistrationNo").val(response.strRegistrationNo);
		$("#txtDocNo").val(response.strAgainstDocNo);
		$("#cmbAgainst").val(response.strType);
		if(response.strType=="Reservation")
		{
			$("#lblAgainst").text("Reservation");
		}
		else
		{
			$("#lblAgainst").text("Walk In No");
		}
				
		$("#txtArrivalDate").val(response.dteArrivalDate);
		$("#txtDepartureDate").val(response.dteDepartureDate);

		$("#txtArrivalTime").val(response.tmeArrivalTime);
		$("#txtDepartureTime").val(response.tmeDepartureTime);
		
		$("#txtRoomNo").val(response.strRoomNo);
		$("#lblRoomNo").text(response.strRoomDesc);
	    $("#txtExtraBed").val(response.strExtraBedCode);
	    $("#lblExtraBed").text(response.strExtraBedDesc);	    
	    $("#txtNoOfAdults").val(response.intNoOfAdults);
	    $("#txtNoOfChild").val(response.intNoOfChild);
		
		funFillDtlGrid(response.listCheckInDetailsBean);
	}
		
	
	function funFillDtlGrid(resListDtlBean)
	{
		funRemoveProductRows();
		$.each(resListDtlBean, function(i,item)
		{
			funAddDetailsRow(resListDtlBean[i].strGuestName,resListDtlBean[i].strGuestCode,resListDtlBean[i].lngMobileNo
				,resListDtlBean[i].strRoomNo,resListDtlBean[i].strRoomDesc,resListDtlBean[i].strExtraBedCode
				,resListDtlBean[i].strExtraBedDesc,resListDtlBean[i].strPayee);
		});
	}
	
	
	

	function funSetReservationNo(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadReservation.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strReservationNo!="Invalid")
		    	{
					funFillHdDataAgainstRes(response);
		    	}
		    	else
			    {
			    	alert("Invalid Reservation No");
			    	$("#txtDocNo").val("");
			    	$("#txtDocNo").focus();
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
	
	
	function funFillHdDataAgainstRes(response)
	{
		$("#txtDocNo").val(response.strReservationNo);
		
		$("#txtArrivalDate").val(response.dteArrivalDate);
		$("#txtDepartureDate").val(response.dteDepartureDate);

		$("#txtArrivalTime").val(response.tmeArrivalTime);
		$("#txtDepartureTime").val(response.tmeDepartureTime);
		
		$("#txtRoomNo").val(response.strRoomNo);
		$("#lblRoomNo").text(response.strRoomDesc);
	    $("#txtExtraBed").val(response.strExtraBedCode);
	    $("#lblExtraBed").text(response.strExtraBedDesc);
	    $("#txtNoOfAdults").val(response.intNoOfAdults);
	    $("#txtNoOfChild").val(response.intNoOfChild);

		funFillDtlGridAgainstRes(response.listReservationDetailsBean);
	}
		
	
	function funFillDtlGridAgainstRes(resListResDtlBean)
	{
		funRemoveProductRows();
		$.each(resListResDtlBean, function(i,item)
		{
			funAddDetailsRow(resListResDtlBean[i].strGuestName,resListResDtlBean[i].strGuestCode,resListResDtlBean[i].lngMobileNo
				,resListResDtlBean[i].strRoomNo,resListResDtlBean[i].strRoomDesc
				,resListResDtlBean[i].strExtraBedCode,resListResDtlBean[i].strExtraBedDesc,resListResDtlBean[i].strPayee);
		});
	}
	
	
	
	
	function funSetWalkinNo(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadWalkinData.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strWalkinNo!="Invalid")
		    	{
					funFillHdDataAgainstWalkIn(response);
		    	}
		    	else
			    {
			    	alert("Invalid Walk In No");
			    	$("#txtDocNo").val("");
			    	$("#txtDocNo").focus();
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
	
	
	function funFillHdDataAgainstWalkIn(response)
	{
		$("#txtDocNo").val(response.strWalkinNo);		
		$("#txtArrivalDate").val(response.dteWalkinDate);
		$("#txtDepartureDate").val(response.dteCheckOutDate);
		$("#txtArrivalTime").val(response.tmeWalkinTime);
		$("#txtDepartureTime").val(response.tmeCheckOutTime);
		$("#txtRoomNo").val(response.strRoomNo);
		$("#lblRoomNo").text(response.strRoomDesc);
	    $("#txtExtraBed").val(response.strExtraBedCode);
	    $("#lblExtraBed").text(response.strExtraBedDesc);
	    $("#txtNoOfAdults").val(response.intNoOfAdults);
	    $("#txtNoOfChild").val(response.intNoOfChild);

		funFillDtlGridAgainstWalkIn(response.listWalkinDetailsBean);
	}
		
	
	function funFillDtlGridAgainstWalkIn(resListWalkInDtlBean)
	{
		funRemoveProductRows();
		$.each(resListWalkInDtlBean, function(i,item)
		{
			var geustName=resListWalkInDtlBean[i].strGuestFirstName+' '+resListWalkInDtlBean[i].strGuestMiddleName+' '+resListWalkInDtlBean[i].strGuestLastName
			funAddDetailsRow(geustName,resListWalkInDtlBean[i].strGuestCode,resListWalkInDtlBean[i].lngMobileNo
				,resListWalkInDtlBean[i].strRoomNo,resListWalkInDtlBean[i].strRoomDesc,resListWalkInDtlBean[i].strExtraBedCode
				,resListWalkInDtlBean[i].strExtraBedDesc,"Y");
		});
	}
	
	

	// Get Detail Info From detail fields and pass them to function to add into detail grid
		function funGetDetailsRow() 
		{
			var guestCode=$("#txtGuestCode").val().trim();
			var mobileNo=$("#txtMobileNo").val().trim();
			var guestName=$("#txtGFirstName").val().trim()+" "+$("#txtGMiddleName").val().trim()+" "+$("#txtGLastName").val().trim();
			
			var roomNo =$("#txtRoomNo").val().trim();
			var roomDesc =$("#lblRoomNo").text().trim();
			var extraBedCode=$("#txtExtraBed").val();
			var extraBedDesc=$("#lblExtraBed").text();
			
		    funAddDetailsRow(guestName,guestCode,mobileNo,roomNo,roomDesc,extraBedCode,extraBedDesc,"Y");
		}
	
	
	//Function to add detail grid rows	
		function funAddDetailsRow(guestName,guestCode,mobileNo,roomNo,roomDesc,extraBedCode,extraBedDesc,payee) 
		{
		    var table = document.getElementById("tblCheckInDetails");
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);

		    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"60%\" name=\"listCheckInDetailsBean["+(rowCount)+"].strGuestName\" id=\"strGuestName."+(rowCount)+"\" value='"+guestName+"' />";	    
		    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"10%\" name=\"listCheckInDetailsBean["+(rowCount)+"].lngMobileNo\" id=\"lngMobileNo."+(rowCount)+"\" value='"+mobileNo+"' />";	   
		    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"searchTextBox\" size=\"1%\" name=\"listCheckInDetailsBean["+(rowCount)+"].strRoomNo\" id=\"strRoomNo."+(rowCount)+"\" value='"+roomNo+"'  ondblclick=\"Javacsript:funHelp1('roomCode',"+(rowCount)+")\"/>";
		    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"searchTextBox\" size=\"1%\" name=\"listCheckInDetailsBean["+(rowCount)+"].strExtraBedCode\" id=\"strExtraBedCode."+(rowCount)+"\" value='"+extraBedCode+"' ondblclick=\"Javacsript:funHelp1('extraBed',"+(rowCount)+")\" />";
		    
		     
		    if(payee=='Y')
		    {
		    	row.insertCell(4).innerHTML= "<input id=\"cbItemCodeSel."+(rowCount)+"\" type=\"checkbox\" checked=\"checked\" class=\"Box payeeSel\" name=\"listCheckInDetailsBean["+(rowCount)+"].strPayee\" size=\"2%\" value=\"Y\" onClick=\"Javacsript:funCheckBoxRow(this)\"  />";
		    }
		    else
		    {
		    	row.insertCell(4).innerHTML= "<input id=\"cbItemCodeSel."+(rowCount)+"\" type=\"checkbox\" class=\"Box payeeSel\" name=\"listCheckInDetailsBean["+(rowCount)+"].strPayee\" size=\"2%\" value=\"N\" onClick=\"Javacsript:funCheckBoxRow(this)\" />";	
		    } 
		    
		    
		  /*   if(payee=='Y')
		    {
		    	row.insertCell(4).innerHTML= "<input id=\"cbItemCodeSel."+(rowCount)+"\" type=\"radio\" checked=\"checked\" class=\"Box payeeSel\" name=\"strPayeeRoomNo."+roomNo+"\" size=\"2%\" value='"+roomNo+"'  onClick=\"Javacsript:funRadioRow(this)\" />";
		    	row.insertCell(5).innerHTML= "<input readonly=\"readonly\"  style=\"display:none\" class=\"searchTextBox\" size=\"1%\" name=\"listCheckInDetailsBean["+(rowCount)+"].strPayee\" id=\"strPayee."+(rowCount)+"\" value=\"Y\" />";
		    }
		    else
		    {
		    	row.insertCell(4).innerHTML= "<input id=\"cbItemCodeSel."+(rowCount)+"\" type=\"radio\" class=\"Box payeeSel\" name=\"strPayeeRoomNo."+roomNo+"\" size=\"2%\" value='"+roomNo+"'  />";	
		    	row.insertCell(5).innerHTML= "<input readonly=\"readonly\"  style=\"display:none\" class=\"searchTextBox\" size=\"1%\" name=\"listCheckInDetailsBean["+(rowCount)+"].strPayee\" id=\"strPayee."+(rowCount)+"\" value=\"N\" />";	
		    } */
		    
		    row.insertCell(5).innerHTML= "<input class=\"Box\" size=\"5%\" name=\"listCheckInDetailsBean["+(rowCount)+"].intNoOfFolios\" id=\"intNoOfFolios."+(rowCount)+"\" value='1' />";
		    row.insertCell(6).innerHTML= "<input type=\"button\" class=\"deletebutton\" size=\"2%\" value = \"Delete\" onClick=\"Javacsript:funDeleteRow(this)\"/>";
		    
		    row.insertCell(7).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"1%\" name=\"listCheckInDetailsBean["+(rowCount)+"].strGuestCode\" id=\"strGuestCode."+(rowCount)+"\" value='"+guestCode+"' type='hidden' />";
		    row.insertCell(8).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"5%\" id=\"strRoomDesc."+(rowCount)+"\" value='"+roomDesc+"' type='hidden' />";
		    row.insertCell(9).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"5%\" id=\"strExtraBedDesc."+(rowCount)+"\" value='"+extraBedDesc+"' />";
		    
		    funResetDetailFields();
		    
		    if(payee=='Y')
		    {
		    	$("#hidPayee").val(guestCode);
		    }
		}


	// Reset Detail Fields
		function funResetDetailFields()
		{
			$("#txtGuestCode").val('');
			$("#txtMobileNo").val('');
			$("#txtGFirstName").val('');
			$("#txtGMiddleName").val('');
			$("#txtGLastName").val('');
		}
	
		function funCheckBoxRow(rowObj)
		{
			/* $( "input[type='radio']" ).prop({
				checked: false
				}); */
				var no=0;
				$('#tblCheckInDetails tr').each(function() {
					
					if(document.getElementById("cbItemCodeSel."+no).checked == true)
						{
						  document.getElementById("cbItemCodeSel."+no).value='Y';
						}else
							{
								document.getElementById("cbItemCodeSel."+no).value='N';
							}
					no++;
				});
		}


	//Delete a All record from a grid
		function funRemoveProductRows()
		{
			var table = document.getElementById("tblCheckInDetails");
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
		    var table = document.getElementById("tblCheckInDetails");
		    table.deleteRow(index);
		}


		function funHelp(transactionName)
		{
			fieldName=transactionName;
			window.open("searchform.html?formname="+transactionName+"&searchText=","mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
			//window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
		}
		
		function funHelp1(transactionName,row)
		{
			gridHelpRow=row;
			fieldName=transactionName;
			window.open("searchform.html?formname="+transactionName+"&searchText=","mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
			//window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
		}
	
		
		function funHelpAgainst()
		{
			if ($("#cmbAgainst").val() == "Reservation")
			{
				fieldName="ReservationNo";
			}
			else if ($("#cmbAgainst").val() == "Walk In")
			{
				fieldName="WalkinNo";
			}
			
			window.open("searchform.html?formname="+fieldName+"&searchText=","mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
			//window.showModalDialog("searchform.html?formname="+fieldName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
		}
		
	
		$(document).ready(function()
		{
			var message='';
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
					%>
					alert("Data Save successfully\n\n"+message);
					var AdvAmount='';
					var isOk=confirm("Do You Want to pay Advance Amount ?");
					if(isOk)
						{
						var checkAgainst="checkIN";
						AdvAmount='<%=session.getAttribute("AdvanceAmount").toString()%>';
						window.location.href=getContextPath()+"/frmPMSPaymentAdvanceAmount.html?AdvAmount="+AdvAmount ;
		    			session.removeAttribute("AdvanceAmount");
//							session.removeAttribute("Against");
					
						}
					
					
					<%
				}
			}%>
		});
		
		
		function funOnChange() {
			if ($("#cmbAgainst").val() == "Reservation")
			{
				$("#lblAgainst").text("Reservation");
			}
			else if ($("#cmbAgainst").val() == "Walk In")
			{
				$("#lblAgainst").text("Walk In No");
			}
		}
		

		 /**
			*   Attached document Link
			**/
			$(function()
			{
			
				$('a#baseUrl').click(function() 
				{
					if($("#txtCheckInNo").val().trim()=="")
					{
						alert("Please Select CheckIN No ");
						return false;
					}
				   window.open('attachDoc.html?transName=frmCheckIN.jsp&formName=CheckIn &code='+$('#txtCheckInNo').val(),"mywindow","directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=600,left=400px");
				});
			});
		 
		 
		 function funValidateForm()
		 {
				var table = document.getElementById("tblCheckInDetails");
			    var rowCount = table.rows.length;
				if(rowCount==0)
				{
					alert("Please Select Guest.");
					return false;
				}
				
				if($("#hidPayee").val()=='')
				{
					alert("Please Select One Payee");
					return false;
				}
				
				
				return true;	 
		 }
		 
		 
		 function funSetRoomNo(code,gridHelpRow){

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
			        		document.getElementById("strRoomNo."+gridHelpRow).value=response.strRoomCode;						
			    			document.getElementById("strRoomDesc."+gridHelpRow).value=response.strRoomDesc;
			    			 $( "#tblCheckInDetails" ).load( "your-current-page.html #tblCheckInDetails" );
			    		//	document.getElementById("strPayee."+gridHelpRow).value=response.strRoomDesc; 
			    			  
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
			
		 function funSetExtraBed(code,gridHelpRow)
			{
			//	$("#txtExtraBed").val(code);
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
					        	document.getElementById("strExtraBedCode."+gridHelpRow).value=code;						
				    			document.getElementById("strExtraBedDesc."+gridHelpRow).value=response.strExtraBedTypeDesc; 
						       
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
	<label>CheckIn</label>
	</div>

	<br/>
	<br/>

	<s:form name="frmCheckIn" method="POST" action="saveCheckIn.html">

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
				<td>
					<label>Check In No</label>
				</td>
				<td>
					<s:input  type="text" id="txtCheckInNo" path="strCheckInNo" cssClass="searchTextBox" ondblclick="funHelp('checkIn');"/>
				</td>
							
				<td>
					<label>Registration No</label>
				</td>
				<td>
					<s:input type="text" id="txtRegistrationNo" path="strRegistrationNo" cssClass="searchTextBox" ondblclick="funHelp('RegistrationNo');"/>
				</td>
			</tr>
			
		 	<tr>
				<td><label>Type</label></td>
				<td>
					<s:select id="cmbAgainst" path="strType" cssClass="BoxW124px" onchange="funOnChange();">
					    <option selected="selected" value="Reservation">Reservation</option>
				        <option value="Walk In">Walk In</option>
			         </s:select>
				</td>
							
				<td>
					<label id="lblAgainst">Reservation No</label>
				</td>
				<td>
					<s:input  type="text" id="txtDocNo" path="strAgainstDocNo" cssClass="searchTextBox" ondblclick="funHelpAgainst();"/>
				</td>
			</tr>
			
			<tr>
				<td><label>Arrival Date</label></td>
				<td><s:input  type="text" id="txtArrivalDate" path="dteArrivalDate" cssClass="calenderTextBox" /></td>
			
				<td><label>Departure Date</label></td>
				<td><s:input  type="text" id="txtDepartureDate" path="dteDepartureDate" cssClass="calenderTextBox" /></td>
			</tr>
		
			<tr>
				<td><label>Arrival Time</label></td>
				<td><s:input  type="text" id="txtArrivalTime" path="tmeArrivalTime" cssClass="calenderTextBox" /></td>
			
				<td><label>Departure Time</label></td>
				<td><s:input  type="text" id="txtDepartureTime" path="tmeDepartureTime" cssClass="calenderTextBox" /></td>
			</tr>
			
			
				
			<tr>
				<td ><label>#Adult</label></td>
				<td><s:input id="txtNoOfAdults" name="txtNoOfAdults" path="intNoOfAdults" type="number" min="0" step="1" class="longTextBox" style="width: 38%;text-align: right;"/></td>
				<td><label>#Child</label></td>
				<td><s:input id="txtNoOfChild" path="intNoOfChild" type="number" min="0" step="1" name="txtNoOfChild" class="longTextBox" style="width: 38%;text-align: right;"/></td>				
			</tr> 
			
		</table>
		
		<br>
		<br>
		
	<!-- 	<div>
			<table class="transTable">
				
				<tr>
					<td><label>Mobile No</label></td>
					<td width="10%"><input type="text" id="txtMobileNo" class="longTextBox" /></td>
					
					<td><label>Guest Code</label></td>
					<td width="10%"><input id="txtGuestCode" ondblclick="funHelp('guestCode');" class="searchTextBox" /></td>
				<td colspan="3"></td>
				</tr>
				
				<tr>
					<td width="5%"><label id="lblGFirstName">First Name</label></td>
					<td width="15%"><input type="text" id="txtGFirstName" class="longTextBox" /></td>
					
					<td width="5%"><label id="lblGMiddleName">Middle Name</label></td>
					<td width="15%"><input type="text" id="txtGMiddleName" class="longTextBox" /></td>
					
					<td width="1%"><label id="lblGLastName">Last Name</label></td>
					<td width="15%" colspan="2"><input type="text" id="txtGLastName" class="longTextBox" /></td>
				</tr>
				<tr>
				<td width="10%"><label>Room No</label></td>
				<td><input id="txtRoomNo" name="txtRoomNo" ondblclick="funHelp('roomCode')"  Class="searchTextBox" /></td>
				<td width="10%"><label id="lblRoomNo"></label></td>
				<td width="10%"><label>Extra Bed</label></td>
				<td><input type="text" id="txtExtraBed" name="txtExtraBed"  Class="searchTextBox" ondblclick="funHelp('extraBed')" /></td>
				<td width="10%"><label id="lblExtraBed"></label></td>
			   
			</tr>
			
				<tr>
					<td colspan="6"><input type="Button" value="Add" onclick="return funGetDetailsRow()" class="smallButton" /></td>
				</tr>
			</table>
		</div> -->

	
		<div class="dynamicTableContainer" style="height: 300px;">
			<table style="height: 28px; border: #0F0; width: 100%; font-size: 11px; font-weight: bold;">
				<tr bgcolor="#72BEFC">
				
					<td style="width:15%;">Name</td>
					<td style="width:10%;">Mb No</td>
					<td style="width:10%;">Room No</td>
					<td style="width:10%;">Extra Bed</td>					
					<td style="width:2%;">Payee</td>
					<td style="width:2%;">No Of Folios</td>
					<td style="width:5%;"></td>
					<td style="width:5%;"></td>
					<td style="width:5%;"></td>
				</tr>
			</table>
		
			<div style="background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
				<table id="tblCheckInDetails"
					style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll"
					class="transTablex col8-center">
					<tbody>
					
						<col style="width: 210px;">
						<col style="width: 140px;">
						<col style="width: 140px;">
						<col style="width: 140px;">
						<col style="width: 40px;">
						<col style="width: 50px;">
						<col style="width: 50px;">
						<col style="width: 50px;">
						<col style="width: 50px;">
						<col style="width: 50px;">
					
					<!-- 
						<col style="width=40%">
						<col style="width=10%">
						<col style="width=10%">
						<col style="width=10%">
						<col style="width:10%">
						<col style="width:5%">
						<col style="width:5%">
						<col style="width:5%">
						<col style="width:5%">
						<col style="width:5%">-->
						
						<col style="display:none;">
					</tbody>
				</table>
			</div>
		</div>
		
		

		<br>
		<br>
		<p align="center">
			<input type="submit" value="Submit" tabindex="3" class="form_button" onclick="return funValidateForm();"/>
			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/>
		</p>
<%-- 		<s:input type="hidden" id="hidPayee" path="strPayeeGuestCode"></s:input> --%>
		<br><br>

	</s:form>
</body>
</html>
