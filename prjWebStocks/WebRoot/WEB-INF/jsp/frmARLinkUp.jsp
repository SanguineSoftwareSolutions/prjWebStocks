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
	var rowNo;
	var acBrandrow;

	$(function() 
	{
		funLinkupData("SubGroup");
	});

	function funSetData(code){

		switch(fieldName){
				
			case 'WSItemCode' : 
				funSetWSItemCode(code);
				break;
				
			case 'productmaster':
		    	funSetProduct(code);
		        break;
		        
			case 'BrandMasterWeb-Service':
		    	funSetBrand(acBrandrow,code);
		        break; 
		        
			case 'exciseSupplierWeb-Service':
		    	funSetExciseSupplier(acBrandrow,code);
		        break;      
		        
			case 'SundryCreditorWeb-Service':
		    	funSetSundryCreditor(acBrandrow,code);
		        break;    
		        
			case 'AccountMasterGLOnlyWeb-Service':
		    	funSetAccounts(acBrandrow,code);
		        break;
		        
			case 'SundryDebtorWeb-Service':
		    	funSetSundryDetor(acBrandrow,code);
		        break;    
		        
		        
		        
		}
	}
	
	 function funSetBrand(acBrandrow,code)
		{	
		 
		 $.ajax({
				type : "GET",
				url : getContextPath()+ "/loadBrandDataFormWebService.html?strBrandCode=" + code,
				dataType : "json",
				success : function(response){ 

					document.getElementById("txtBrandCode."+acBrandrow).value=response.strBrandCode;						
	    			document.getElementById("txtBrandName."+acBrandrow).value=response.strBrandName; 
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
	 
	 function funSetExciseSupplier(acBrandrow,code)
		{	
		 
		 $.ajax({
				type : "GET",
				url : getContextPath()+ "/loadExciseSuppMaster.html?strSupplierCode=" + code,
				dataType : "json",
				success : function(response){ 

					document.getElementById("txtExSuppCode."+acBrandrow).value=response.strSupplierCode;						
	    			document.getElementById("txtExSuppName."+acBrandrow).value=response.strSupplierName; 
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
	 
	 function funSetSundryCreditor(acBrandrow,code)
		{	
		var transType =  $('#cmbLinkup').val();
		 $.ajax({
				type : "GET",
				url : getContextPath()+ "/loadSundryCreditorOrDebtorLinkupDataFormWebService.html?strDocCode=" + code,
				dataType : "json",
				success : function(response){ 
					
					if(transType=="Supplier")
					{
						document.getElementById("txtSuppAcCode."+acBrandrow).value=response.strDebtorCode;
						document.getElementById("txtSuppAcName."+acBrandrow).value=response.strFirstName;
					}
					if(transType=="Customer")
					{
						document.getElementById("txtCustAcCode."+acBrandrow).value=response.strDebtorCode;
						document.getElementById("txtCustAcName."+acBrandrow).value=response.strFirstName;
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
	 
	 
	 function funSetSundryDetor(acBrandrow,code)
		{	
		var transType =  $('#cmbLinkup').val();
		 $.ajax({
				type : "GET",
				url : getContextPath()+ "/loadSundryDebtorLinkupDataFormWebService.html?strDocCode=" + code,
				dataType : "json",
				success : function(response){ 
					
					if(transType=="Supplier")
					{
						document.getElementById("txtSuppAcCode."+acBrandrow).value=response.strDebtorCode;
						document.getElementById("txtSuppAcName."+acBrandrow).value=response.strFirstName;
					}
					if(transType=="Customer")
					{
						document.getElementById("txtCustAcCode."+acBrandrow).value=response.strDebtorCode;
						document.getElementById("txtCustAcName."+acBrandrow).value=response.strFirstName;
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
	 
	 function funSetAccounts(acBrandrow,code)
		{	
		var transType =  $('#cmbLinkup').val();
		 $.ajax({
				type : "GET",
				url : getContextPath()+ "/loadLinkupDataFormWebService.html?strAccountCode=" + code,
				dataType : "json",
				success : function(response){ 
					if(transType=="SubGroup")
					{
						document.getElementById("txtSGAcCode."+acBrandrow).value=response.strDebtorCode;
						document.getElementById("txtSGAcName."+acBrandrow).value=response.strFirstName;
					}
					if(transType=="Tax")
					{
						document.getElementById("txtTaxAcCode."+acBrandrow).value=response.strDebtorCode;
						document.getElementById("txtTaxAcName."+acBrandrow).value=response.strFirstName;
					}
					if(transType=="Supplier")
					{
						document.getElementById("txtSuppAcCode."+acBrandrow).value=response.strDebtorCode;
						document.getElementById("txtSuppAcName."+acBrandrow).value=response.strFirstName;
					}
					if(transType=="Customer")
					{
						document.getElementById("txtCustAcCode."+acBrandrow).value=response.strDebtorCode;
						document.getElementById("txtCustAcName."+acBrandrow).value=response.strFirstName;
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


	function funSetWSItemCode(code){

		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadSundryDataFormWebService.html?strAccountCode=" + code,
			dataType : "json",
			success : function(response){ 

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

	
	function funSetProduct(code)
	{
		var searchUrl="";
		searchUrl=getContextPath()+"/loadProductMasterData.html?prodCode="+code;
		$.ajax
		({
	        type: "GET",
	        url: searchUrl,
		    dataType: "json",
		    success: function(response)
		    {
		    	document.getElementById("strWSItemCode."+rowNo).value=response.strProdCode;
		    	document.getElementById("strWSItemName."+rowNo).value=response.strProdName;
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
	//	window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
		window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
	
	function funHelp1(row,transactionName)
	{
		acBrandrow=row;
		fieldName = transactionName;
		 window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:1000px;top=500,left=500")
	}
	

	
	function funAddRowSubgroupLinkUpData(rowData)  
	{ 
		//alert(qty);
		$('#hidLinkup').val("");
		$('#hidLinkup').val("subGroupLinkup");
		var table = document.getElementById("tblSubGroup");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    var strSubgroupCode = rowData.strSGCode;
    	var strSubgroupName = rowData.strSGName;
    	var strAcCode = rowData.strAccountCode;
    	var strAcName = rowData.strGDes;
		
	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" name=\"listSubGroupLinkUp["+(rowCount)+"].strSGCode\"   id=\"txtSubgroupCode."+(rowCount)+"\" value='"+strSubgroupCode+"' />";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" name=\"listSubGroupLinkUp["+(rowCount)+"].strSGName\"   id=\"txtSubgroupName."+(rowCount)+"\" value='"+strSubgroupName+"' />";
	    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"searchTextBox\" name=\"listSubGroupLinkUp["+(rowCount)+"].strAccountCode\"    id=\"txtSGAcCode."+(rowCount)+"\" value='"+strAcCode+"' ondblclick=\"funHelp1("+(rowCount)+",'AccountMasterGLOnlyWeb-Service')\"/>";
	    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\"  name=\"listSubGroupLinkUp["+(rowCount)+"].strGDes\"   id=\"txtSGAcName."+(rowCount)+"\" value='"+strAcName+"' />";
	}
	
	function funAddRowTaxLinkUpData(rowData)
	{
		//alert(qty);
		$('#hidLinkup').val("");
		$('#hidLinkup').val("taxLinkup");
		var table = document.getElementById("tblTax");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    var strTaxCode = rowData.strSGCode;
    	var strDesc = rowData.strSGName;
    	var strAcCode = rowData.strAccountCode;
    	var strAcName = rowData.strGDes;
		
	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" name=\"listTaxLinkUp["+(rowCount)+"].strSGCode\"  id=\"txtTaxCode."+(rowCount)+"\" value='"+strTaxCode+"' />";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" name=\"listTaxLinkUp["+(rowCount)+"].strSGName\"  id=\"txtTaxDesc."+(rowCount)+"\" value='"+strDesc+"' />";
	    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"searchTextBox\"  name=\"listTaxLinkUp["+(rowCount)+"].strAccountCode\"   id=\"txtTaxAcCode."+(rowCount)+"\" value='"+strAcCode+"' ondblclick=\"funHelp1("+(rowCount)+",'AccountMasterWeb-Service')\" />";
	    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\"  name=\"listTaxLinkUp["+(rowCount)+"].strGDes\"   id=\"txtTaxAcName."+(rowCount)+"\" value='"+strAcName+"' />";
	}
	
	function funAddRowSupplierLinkUpData(rowData)
	{
		//alert(qty);
		$('#hidLinkup').val("");
		$('#hidLinkup').val("suppLinkup");
		var table = document.getElementById("tblSupplier");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    var strSuppCode = rowData.strSGCode;
    	var strSuppName = rowData.strSGName;
    	var strAcCode = rowData.strAccountCode;
    	var strAcName = rowData.strGDes;
    	var strExSuppCode = rowData.strExSuppCode;
    	var strExSuppName = rowData.strExSuppName;
		
	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\"  name=\"listSupplierLinkUp["+(rowCount)+"].strSGCode\"   id=\"txtSuppcode."+(rowCount)+"\" value='"+strSuppCode+"' />";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\"  name=\"listSupplierLinkUp["+(rowCount)+"].strSGName\"   id=\"txtSuppName."+(rowCount)+"\" value='"+strSuppName+"' />";
	    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"searchTextBox\"  name=\"listSupplierLinkUp["+(rowCount)+"].strAccountCode\"    id=\"txtSuppAcCode."+(rowCount)+"\" value='"+strAcCode+"' ondblclick=\"funHelp1("+(rowCount)+",'SundryCreditorWeb-Service')\"/>";
	    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\"  name=\"listSupplierLinkUp["+(rowCount)+"].strGDes\"   id=\"txtSuppAcName."+(rowCount)+"\" value='"+strAcName+"' />";
	    row.insertCell(4).innerHTML= "<input readonly=\"readonly\" class=\"searchTextBox\"   name=\"listSupplierLinkUp["+(rowCount)+"].strExSuppCode\" id=\"txtExSuppCode."+(rowCount)+"\" value='"+strExSuppCode+"' ondblclick=\"funHelp1("+(rowCount)+",'exciseSupplierWeb-Service')\" />";
	    row.insertCell(5).innerHTML= "<input readonly=\"readonly\" class=\"Box\" name=\"listSupplierLinkUp["+(rowCount)+"].strExSuppName\"  id=\"txtExSuppName."+(rowCount)+"\" value='"+strExSuppName+"' />";
	}
	
	
	
	function funAddRowProductLinkUpData(rowData)
	{
		//alert(qty);
		$('#hidLinkup').val("");
		$('#hidLinkup').val("productLinkup");
		var table = document.getElementById("tblProduct");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    var strProdCode = rowData.strSGCode;
    	var strProdName = rowData.strSGName;
    	var strBrandCode = rowData.strExSuppCode;
    	var strBrandName = rowData.strExSuppName;
		
	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\"  name=\"listProductLinkUp["+(rowCount)+"].strSGCode\"   id=\"txtProdcode."+(rowCount)+"\" value='"+strProdCode+"'  />";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\"  name=\"listProductLinkUp["+(rowCount)+"].strSGName\"  id=\"txtProdName."+(rowCount)+"\" value='"+strProdName+"' />";
	    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"searchTextBox\"  name=\"listProductLinkUp["+(rowCount)+"].strExSuppCode\"   id=\"txtBrandCode."+(rowCount)+"\" value='"+strBrandCode+"' ondblclick=\" funHelp1("+(rowCount)+",'BrandMasterWeb-Service') \" />";
	    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\"  name=\"listProductLinkUp["+(rowCount)+"].strExSuppName\"   id=\"txtBrandName."+(rowCount)+"\" value='"+strBrandName+"' />";
	}
	
	function funAddRowCustomerLinkUpData(rowData)
	{
		//alert(qty);
		$('#hidLinkup').val("");
		$('#hidLinkup').val("customerLinkup");
		var table = document.getElementById("tblCustomer");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    var strProdCode = rowData.strSGCode;
    	var strProdName = rowData.strSGName;
    	var strAcCode = rowData.strAccountCode;
    	var strAcName = rowData.strGDes;
		
	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\"  name=\"listCustomerLinkUp["+(rowCount)+"].strSGCode\"   id=\"txtProdcode."+(rowCount)+"\" value='"+strProdCode+"'  />";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\"  name=\"listCustomerLinkUp["+(rowCount)+"].strSGName\"  id=\"txtProdName."+(rowCount)+"\" value='"+strProdName+"' />";
	    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"searchTextBox\"  name=\"listCustomerLinkUp["+(rowCount)+"].strAccountCode\"   id=\"txtCustAcCode."+(rowCount)+"\" value='"+strAcCode+"' ondblclick=\" funHelp1("+(rowCount)+",'SundryDebtorWeb-Service') \" />";
	    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\"  name=\"listCustomerLinkUp["+(rowCount)+"].strGDes\"   id=\"txtCustAcName."+(rowCount)+"\" value='"+strAcName+"' />";
	}
	
	function funSupplierLinkUpData(code)
	{
		var searchUrl="";
		searchUrl=getContextPath()+"/loadARLinkUpData.html?strDoc="+code;
		$.ajax
		({
	        type: "POST",
	        url: searchUrl,
		    dataType: "json",
		    success: function(response)
		    {
		    	funDeleteTableAllRowsOfParticulorTable(code);
		    	$.each(response, function(i,item)
						{
// 				    		var arr = jQuery.makeArray( response[i] );
				    		funAddRowSupplierLinkUpData(item);
				    		
						}); 
		    	
		    	
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
	
	function funSubGroupLinkUpData(code)
	{
		searchUrl=getContextPath()+"/loadARLinkUpData.html?strDoc="+code;
		$.ajax
		({
	        type: "POST",
	        url: searchUrl,
		    dataType: "json",
		    success: function(response)
		    {
		    	funDeleteTableAllRowsOfParticulorTable(code);
		    	$.each(response, function(i,item)
						{
// 				    		var arr = jQuery.makeArray( response[i] );
				    		funAddRowSubgroupLinkUpData(item);
				    		
						}); 
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
	
	
	function funTaxLinkUpData(code)
	{
		searchUrl=getContextPath()+"/loadARLinkUpData.html?strDoc="+code;
		$.ajax
		({
	        type: "POST",
	        url: searchUrl,
		    dataType: "json",
		    success: function(response)
		    {
		    	funDeleteTableAllRowsOfParticulorTable(code);
		    	$.each(response, function(i,item)
						{
				    		//var arr = jQuery.makeArray( response[i] );
				    		funAddRowTaxLinkUpData(item);
				    		
						}); 
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
	
	
	function funProductLinkUpData(code)
	{
		searchUrl=getContextPath()+"/loadARLinkUpData.html?strDoc="+code;
		$.ajax
		({
	        type: "POST",
	        url: searchUrl,
		    dataType: "json",
		    success: function(response)
		    {
		    	funDeleteTableAllRowsOfParticulorTable(code);
		    	$.each(response, function(i,item)
						{
// 				    		var arr = jQuery.makeArray( response[i] );
				    		funAddRowProductLinkUpData(item);
				    		
						}); 
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
	
	function funCustomerLinkUpData(code)
	{
		searchUrl=getContextPath()+"/loadARLinkUpData.html?strDoc="+code;
		$.ajax
		({
	        type: "POST",
	        url: searchUrl,
		    dataType: "json",
		    success: function(response)
		    {
		    	funDeleteTableAllRowsOfParticulorTable(code);
		    	$.each(response, function(i,item)
						{
				    		funAddRowCustomerLinkUpData(item);
				    		
						}); 
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
	
	function funLinkupData(code) {
		
		if(code=="SubGroup")
		{
// 			document.all[ 'trSubGroup' ].style.display = 'block';
			document.all[ 'divSubGroup' ].style.display = 'block';
			
			funSubGroupLinkUpData(code);
			document.all[ 'divTax' ].style.display = 'none';
			document.all[ 'divSupplier' ].style.display = 'none';
			document.all[ 'divProduct' ].style.display = 'none';
			document.all[ 'divCustomer' ].style.display = 'none';
			
// 			document.all[ 'trTax' ].style.display = 'none';
// 			document.all[ 'tblTax' ].style.display = 'none';
// 			document.all[ 'trSupplier' ].style.display = 'none';
// 			document.all[ 'tblSupplier' ].style.display = 'none';
// 			document.all[ 'trProduct' ].style.display = 'none';
// 			document.all[ 'tblProduct' ].style.display = 'none';
			
		}
	
	
		if(code=="Tax")
		{
// 			document.all[ 'trTax' ].style.display = 'block';
			document.all[ 'divTax' ].style.display = 'block';
	
			funTaxLinkUpData(code);
			document.all[ 'divSubGroup' ].style.display = 'none';
			document.all[ 'divSupplier' ].style.display = 'none';
			document.all[ 'divProduct' ].style.display = 'none';
			document.all[ 'divCustomer' ].style.display = 'none';
			
// 			document.all[ 'trSubGroup' ].style.display = 'none';
// 			document.all[ 'tblSubGroup' ].style.display = 'none';
// 			document.all[ 'trSupplier' ].style.display = 'none';
// 			document.all[ 'tblSupplier' ].style.display = 'none';
// 			document.all[ 'trProduct' ].style.display = 'none';
// 			document.all[ 'tblProduct' ].style.display = 'none';
		}
		
		
		if(code=="Supplier")
		{
// 			document.all[ 'trSupplier' ].style.display = 'block';
			document.all[ 'divSupplier' ].style.display = 'block';
			
			funSupplierLinkUpData(code);
			document.all[ 'divSubGroup' ].style.display = 'none';
			document.all[ 'divTax' ].style.display = 'none';
			document.all[ 'divProduct' ].style.display = 'none';
			document.all[ 'divCustomer' ].style.display = 'none';
			
// 			document.all[ 'trSubGroup' ].style.display = 'none';
// 			document.all[ 'tblSubGroup' ].style.display = 'none';
// 			document.all[ 'trTax' ].style.display = 'none';
// 			document.all[ 'tblTax' ].style.display = 'none';
// 			document.all[ 'trProduct' ].style.display = 'none';
// 			document.all[ 'tblProduct' ].style.display = 'none';
		}
		
		if(code=="Product")
		{
// 			document.all[ 'trProduct' ].style.display = 'block';
			document.all[ 'divProduct' ].style.display = 'block';
			
			funProductLinkUpData(code);
			document.all[ 'divSubGroup' ].style.display = 'none';
			document.all[ 'divTax' ].style.display = 'none';
			document.all[ 'divSupplier' ].style.display = 'none';
			document.all[ 'divCustomer' ].style.display = 'none';
			
// 			document.all[ 'trSubGroup' ].style.display = 'none';
// 			document.all[ 'tblSubGroup' ].style.display = 'none';
// 			document.all[ 'trTax' ].style.display = 'none';
// 			document.all[ 'tblTax' ].style.display = 'none';
// 			document.all[ 'trSupplier' ].style.display = 'none';
// 			document.all[ 'tblSupplier' ].style.display = 'none';
			
		}
		
		
		if(code=="Customer")
		{
// 			document.all[ 'trProduct' ].style.display = 'block';
			document.all[ 'divCustomer' ].style.display = 'block';
			
			funCustomerLinkUpData(code);
			document.all[ 'divSubGroup' ].style.display = 'none';
			document.all[ 'divTax' ].style.display = 'none';
			document.all[ 'divSupplier' ].style.display = 'none';
			document.all[ 'divProduct' ].style.display = 'none';
			
// 			document.all[ 'trSubGroup' ].style.display = 'none';
// 			document.all[ 'tblSubGroup' ].style.display = 'none';
// 			document.all[ 'trTax' ].style.display = 'none';
// 			document.all[ 'tblTax' ].style.display = 'none';
// 			document.all[ 'trSupplier' ].style.display = 'none';
// 			document.all[ 'tblSupplier' ].style.display = 'none';
			
		}
		
	}

	function funDeleteTableAllRowsOfParticulorTable(tableName)
	{
		switch(tableName)
		{
		
			case "SubGroup" :
			{
					$("#tbl"+tableName+ " tr").remove();
					break;
			}
			case "Tax" :
			{
					$("#tbl"+tableName+ " tr").remove();
					break;
			}
			
			case "Supplier" :
			{
					$("#tbl"+tableName+ " tr").remove();
					break;
			}
			case "Product" :
			{
					$("#tbl"+tableName+ " tr").remove();
					break;
			}
			case "Customer" :
			{
					$("#tbl"+tableName+ " tr").remove();
					break;
			}
			
						
		}
	}	
	
	
</script>

</head>
<body>

	<div id="formHeading">
	<label>AR Link Up</label>
	</div>

<br/>
<br/>

	<s:form name="ARLinkUp" method="POST" action="saveARLinkUp.html">
	
	
				
		<table  id="tblTermsAndCond" style="height:100px;border:#0F0;width:100%;font-size:11px;font-weight: bold; width: 99%; border: #0F0; table-layout: fixed; overflow: scroll">	
		
		<tr>
			<td><lable>Link up</lable> </td>
			<td>	<s:select id="cmbLinkup" path="strLinkup" cssClass="BoxW124px" onchange="funLinkupData(this.value)">
				    		<s:option value="SubGroup">SubGroup</s:option>
				    		<s:option value="Tax">Tax</s:option>
				    		<s:option value="Supplier">Supplier</s:option>
				    		<s:option value="Product">Product</s:option>
				    		<s:option value="Customer">Customer</s:option>
				    	</s:select>
					</td>
		</tr>

			
		</table>

	<div id="divSubGroup" class="dynamicTableContainer" style="height:293px; width :1000px!important; margin: 0px 2px 4px 4px !important; display:none; ">
			<table
				style="height: 28px; border: #0F0; width: 100%; font-size: 11px; font-weight: bold;">
				<tr bgcolor="#72BEFC">
				<td style="width:10%;">Subgroup Code</td>
				<td style="width:20%;">Subgroup Name</td>
				<td style="width:20%;">Account Code</td>
				<td style="width:20%;">Account Name</td>
			</tr>
		</table>
			<div
				style="background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
					<table id="tblSubGroup"
					style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll"
					class="transTablex col8-center">
					<tbody>
					<col style="width:10%">
					<col style="width:20%">					
					<col style="width:20%">
					<col style="width:20%">
					</tbody>
				</table>
			</div>
		</div>
		
		<div id="divTax" class="dynamicTableContainer" style="height:293px; width :1000px!important; margin: 0px 2px 4px 4px !important; display:none; ">
			<table
				style="height: 28px; border: #0F0; width: 100%; font-size: 11px; font-weight: bold;">
				<tr bgcolor="#72BEFC">
				<td style="width:10%;">Tax Code</td>
				<td style="width:20%;">Tax Desc</td>
				<td style="width:20%;">Account Code</td>
				<td style="width:20%;">Account Name</td>
			</tr>
		</table>
			<div
				style="background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
					<table id="tblTax"
					style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll"
					class="transTablex col8-center">
					<tbody>
					<col style="width:10%">
					<col style="width:22%">					
					<col style="width:20%">
					<col style="width:20%">
					</tbody>
				</table>
			</div>
		</div>

		<div id="divSupplier" class="dynamicTableContainer" style="height:293px; width :1000px!important; margin: 0px 2px 4px 4px !important; display:none; ">
			<table
				style="height: 28px; border: #0F0; width: 100%; font-size: 11px; font-weight: bold;">
				<tr bgcolor="#72BEFC">
				<td style="width:16%;">Supplier Code</td>
				<td style="width:20%;">Supplier Name</td>
				<td style="width:15%;">Account Code</td>
				<td style="width:15%;">Account Name</td>
				<td style="width:15%;">Ex Supplier Code</td>
				<td style="width:20%;">Ex Supplier Name</td>
			</tr>
		</table>
			<div
				style="background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
					<table id="tblSupplier"
					style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll"
					class="transTablex col8-center">
					<tbody>
					<col style="width:16%">
					<col style="width:20%">					
					<col style="width:15%">
					<col style="width:15%">
					<col style="width:15%">
					<col style="width:20%">	
					</tbody>
				</table>
			</div>
		</div>
		
		
		<div id="divProduct" class="dynamicTableContainer" style="height:293px; width :1000px!important; margin: 0px 2px 4px 4px !important; display:none; ">
			<table
				style="height: 28px; border: #0F0; width: 100%; font-size: 11px; font-weight: bold;">
				<tr bgcolor="#72BEFC">
				<td style="width:10%;">Product Code</td>
				<td style="width:20%;">Product Name</td>
				<td style="width:10%;">Brand Code</td>
				<td style="width:20%;">Brand Name</td>
			</tr>
		</table>
			<div
				style="background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
					<table id="tblProduct"
					style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll"
					class="transTablex col8-center">
					<tbody>
					<col style="width:10%">
					<col style="width:26%">					
					<col style="width:10%">
					<col style="width:26%">	
					</tbody>
				</table>
			</div>
		</div>
		<div id="divCustomer" class="dynamicTableContainer" style="height:293px; width :1000px!important; margin: 0px 2px 4px 4px !important; display:none; ">
			<table
				style="height: 28px; border: #0F0; width: 100%; font-size: 11px; font-weight: bold;">
				<tr bgcolor="#72BEFC">
				<td style="width:10%;">Customer Code</td>
				<td style="width:15%;">Customer Name</td>
				<td style="width:10%;">Account Code</td>
				<td style="width:15%;">Account Name</td>
			</tr>
		</table>
			<div
				style="background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
					<table id="tblCustomer"
					style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll"
					class="transTablex col8-center">
					<tbody>
					<col style="width:10%">
					<col style="width:16%">					
					<col style="width:10%">
					<col style="width:15%">
					</tbody>
				</table>
			</div>
		</div>







		<br />
		<br />
		<p align="center">
			<input type="submit" value="Submit" tabindex="3" class="form_button" />
			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/>
		</p>
		
	
	
		<br />
		<br />
		<br />
		<br />
	</s:form>
</body>
</html>
