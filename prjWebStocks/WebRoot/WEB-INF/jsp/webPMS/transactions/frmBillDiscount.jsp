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

	$(function() 
	{
		$("#txtBillDate").timepicker();
		$("#txtBillDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtBillDate").datepicker('setDate', 'today');
	});

	function funSetData(code){

		switch(fieldName){

			case 'billNo' : 
				funSetBillNo(code);
				break;
			case 'CheckInNo' : 
				funSetCheckInNo(code);
				break;
			case 'FolioNo' : 
				funSetFolioNo(code);
				break;
		}
	}


	function funSetBillNo(code){

		$.ajax({
			type : "POST",
			url : getContextPath()+ "/getBillData.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 

				if(response.strBillNo=="Invalid Bill No")
		    	{
					alert("Invalid Bill No");
			    	$("#txtBillNo").val("");
			    	$("#txtBillNo").focus();
			    	return false;
		    	}
		    	else
			    {
		    		$("#txtBillNo").val(response.strBillNo);
		    		$("#txtBillDate").val(response.dteBillDate);
		    		$("#txtCheckInNo").val(response.strCheckInNo);
		    		$("#txtFolioNo").val(response.strFolioNo);
		    		
		    		funSetRoomMasterData(response.strRoomNo)
		    		$("#txtDiscAmt").val(response.dblDiscAmt);
		    		$("#txtGrandTotal").val(response.dblGrandTotal);
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


	function funSetCheckInNo(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadCheckInNo.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 

			},
			error : function(e){

			}
		});
	}

	function funSetFolioNo(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadFolioNo.html?docCode=" + code,
			dataType : "json",
			success : function(response){ 

			},
			error : function(e){

			}
		});
	}

	function funSetRoomMasterData(roomCode)
	{
		var searchUrl=getContextPath()+"/loadRoomMasterData.html?roomCode="+roomCode;
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
	        		$("#txtRoomNo").val(response.strRoomCode);
	        		$("#lblRoomDesc").text(response.strRoomDesc);
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






	function funHelp(transactionName)
	{
		fieldName=transactionName;
		window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
</script>

</head>
<body>

	<div id="formHeading">
	<label>Bill Discount</label>
	</div>

<br/>
<br/>

	<s:form name="BillDiscount" method="POST" action="saveBillDiscount.html">

		<table class="masterTable">
			<tr>
				<td>
					<label>Bill No</label>
				</td>
				<td>
					<s:input colspan="3" type="text" path="strBillNo" id="txtBillNo" cssClass="searchTextBox" ondblclick="funHelp('billNo')" />
				</td>
			</tr>
			<tr>
				<td>
					<label>Bill Date</label>
				</td>
				<td>
					<s:input colspan="3" type="text" readonly="true" path="dteBillDate" id="txtBillDate" cssClass="calenderTextBox" />
				</td>
			</tr>
			<tr>
				<td>
					<label>CheckIn No</label>
				</td>
				<td>
					<s:input colspan="3" type="text" readonly="true"  path="strCheckInNo" id="txtCheckInNo" cssClass="BoxW124px" />
				</td>
			</tr>
			<tr>
				<td>
					<label>Folio No</label>
				</td>
				<td>
					<s:input colspan="3" type="text" readonly="true"  path="strFolioNo"  id="txtFolioNo" cssClass="BoxW124px" />
				</td>
			</tr>
			<tr>
				<td>
					<label>Room No</label>
				</td>
				<td>
					<s:input type="text" readonly="true"  path="strRoomNo"  id="txtRoomNo" cssClass="BoxW124px" />
				&nbsp;&nbsp;&nbsp;<label id="lblRoomDesc"></label>
				</td>
			</tr>
			<tr>
				<td>
					<label>Grand Total</label>
				</td>
				<td>
					<s:input colspan="3" type="number" step="0.01" readonly="true"   path="dblGrandTotal"  id="txtGrandTotal" cssClass="BoxW124px" />
				</td>
			</tr>
			<tr>
				<td>
					<label>Disc Amt</label>
				</td>
				<td>
					<s:input colspan="3" type="number" step="0.01"  path="dblDiscAmt"  id="txtDiscAmt" cssClass="BoxW124px" />
				</td>
			</tr>
			
		</table>

		<br />
		<br />
		<p align="center">
			<input type="submit" value="Submit" tabindex="3" class="form_button" />
			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/>
		</p>

	</s:form>
</body>
</html>
