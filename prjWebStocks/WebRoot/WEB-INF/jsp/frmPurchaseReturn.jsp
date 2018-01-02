<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="sp"%>
<!DOCTYPE html>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>Purchase Return</title>
<script type="text/javascript">

	/* On form Load It Reset form :Ritesh 22 Nov 2014
	 * Ready Function for Ajax Waiting
	**/
	 $(document).ready(function () {
	  
	    $(document).ajaxStart(function(){
		    $("#wait").css("display","block");
		  });
		  $(document).ajaxComplete(function(){
		    $("#wait").css("display","none");
		  });
	}); 

</script>
<script type="text/javascript">
/**
 * Global variable
 */
	var fieldName="";
	var strUOM="";
	var gTaxType="";
	var gTaxCal=0;
	var gTaxPer=0;
	var gTaxOnGD=0;
	var listRow=0;
	
		/**
		 * Ready Function for Initialize textField with default value
		 * And Set date in date picker 
		 **/
		$(function() {
			$("#txtPurchDate").datepicker({ dateFormat: 'dd-mm-yy' });
			$('#txtPurchDate').datepicker('setDate', 'today');
		});
			
		/**
		 * Open Help Form 
		 */
		function funHelp(transactionName)
		{
			fieldName=transactionName;		
		//	window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:1000px;dialogLeft:200px;")
			window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:1000px;dialogLeft:200px;")
		}
		
		/**
		 * Set Data after selecting form Help windows
		 */
		function funSetData(code)
		{
			switch(fieldName)
			{
				case 'locby':
					funSetLocation(code);
					break;
				case 'suppcode':
					funSetSupplier(code);
					break;
				case 'productInUse':
					funSetProduct(code);
					break;
				case 'PurchaseReturn':
					funSetPurchaseReturnData(code);
					break;
				case 'grncode':
					funSetGRNData(code);
					break;
			}			
		}
		
		/**
		 * Get and set Location Data Based on passing Location Code
		 */
		function funSetLocation(code)
		{
			var searchUrl="";					
			searchUrl=getContextPath()+"/loadLocationMasterData.html?locCode="+code;	
			
			$.ajax({
			        type: "GET",
			        url: searchUrl,
				    dataType: "json",
				    success: function(response)
				    {if(response.strLocCode=='Invalid Code')
			       	{
			       		alert("Invalid Location Code");
			       		$("#txtLocCode").val('');
			       		$("#spLocName").text("");
			       		$("#txtLocCode").focus();
			       	}
			       	else
			       	{
				    	$("#txtLocCode").val(code);
				    	$("#spLocName").text(response.strLocName); 
				    	$("#txtProdCode").focus();
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
		 * Get and set supplier Data
		 */
		function funSetSupplier(code)
		{
			var searchUrl="";			
			searchUrl=getContextPath()+"/loadSupplierMasterData.html?partyCode="+code;		
			$.ajax({
			        type: "GET",
			        url: searchUrl,
				    dataType: "json",
				    success: function(response)
				    {
				    	if('Invalid Code' == response.strPCode){
				    		alert('Invalid Code');	
					    	$("#txtSuppCode").val('');
					    	$("#spSuppName").text('');
					    	$("#txtSuppCode").focus();
				    	}else{
				    		$("#txtSuppCode").val(code);
					    	$("#spSuppName").text(response.strPName);
					    	$("#txtLocCode").focus();
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
		 * Get and set Product Data 
		 */
		function funSetProduct(code)
		{
			var searchUrl="";
			$("#txtProdCode").val(code);
			searchUrl=getContextPath()+"/loadProductDataWithTax.html?prodCode="+code;
			$.ajax({
			        type: "GET",
			        url: searchUrl,
				    dataType: "json",
				    success: function(response)
				    {
				    	$("#spProdName").text(response.strProdName);	
						$("#txtPrice").val(response.dblCostRM);
						$("#txtWt").val(response.dblWeight);
						strUOM=response.strUOM;
			        	$("#txtQty").focus();
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
		 * Get and set purchse return Data
		 */
		function funSetPurchaseReturnData(code)
		{
			var searchUrl="";
			searchUrl=getContextPath()+"/loadPurchaseReturnData.html?PRCode="+code;	
		
			$.ajax({
			        type: "GET",
			        url: searchUrl,
				    dataType: "json",
				    success: function(response)
				    {	
				    	if('Invalid Code' == response.strPRCode)
				    	{
				    		alert('Invalid Code');		
					    	$("#txtPRCode").val('');
					    	$("#txtPRCode").focus();	
				    	}
				    	else
				    	{
					    	$("#txtPRCode").val(response.strPRCode);
					    	$("#txtPurchCode").val(response.strPurCode);
					    	$("#txtPurchDate").val(response.dtPRDate);
					    	$("#txtCode").val(response.strGRNCode);
					    	$("#cmbAgainst").val(response.strAgainst);
					    	funOnChange();
					    	$("#txtSuppCode").val(response.strSuppCode);
					    	$("#spSuppName").text(response.strSupplierName);
							$("#txtLocCode").val(response.strLocCode);
							$("#spLocName").text(response.strLocName);
							$("#txtVehNo").val(response.strVehNo);
							$("#txtTimeInOut").val(response.strTimeInOut);
							$("#txtMInBy").val(response.strMInBy);
							$("#txtNarration").val(response.strNarration);
							$("#txtExtra").val(response.dblExtra);
							$("#txtFinalAmt").val(response.dblTotal);						
							$("#txtDisAmt").val(response.dblDisAmt);
							$("#txtDisRate").val(response.dblDisRate);
							$("#txtSubTotal").val(response.dblSubTotal);
							$("#btnAdd").focus();							
							funSetPurchaseReturnDtlData(code);
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
		 * Get and set purchse return Detail Data
		 */
		function funSetPurchaseReturnDtlData(code)
		{
			var searchUrl="";
			searchUrl=getContextPath()+"/loadPurchaseReturnDtlData.html?PRCode="+code;
			$.ajax({
			        type: "GET",
			        url: searchUrl,
				    dataType: "json",
				    success: function(response)
				    {
				    	var count=0;
				    	funRemoveProductRows();
			        	$.each(response, function(i,item)
			       	    {
			        		count=i;
			       	        funfillProdRow(response[i].strProdCode,response[i].strProdName,response[i].dblQty,response[i].strUOM,response[i].dblWeight,response[i].dblTotalWt,response[i].dblUnitPrice,response[i].dblTotalPrice,response[i].dblDiscount);
			       	    });
			        	listRow=count+1;
			        	//funGetTotal();
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
		 * Get and set GRN Dtl Data
		 */
		function funSetGRNDtlData(code)
		{
			var searchUrl="";
			searchUrl=getContextPath()+"/loadGRNDtl.html?GRNCode="+code;	
			//alert(searchUrl);
			$("#txtCode").val(code);
			$.ajax({
			        type: "GET",
			        url: searchUrl,
				    dataType: "json",
				    success: function(response)
				    {
				    	var count=0;
				    	funRemoveProductRows();
			        	$.each(response, function(i,item)
			       	    {
			        		count=i;
			       	        funfillProdRow(response[i].strProdCode,response[i].strProdName,response[i].dblQty,response[i].strUOM,response[i].dblWeight,response[i].dblTotalWt,response[i].dblUnitPrice,response[i].dblTotalPrice,response[i].dblDiscount);
			       	    });
			        	listRow=count+1;
			        	funGetTotal();
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
		 * Get and set GRN Hd Data
		 */
		function funSetGRNData(code)
		{
			var searchUrl="";
			searchUrl=getContextPath()+"/loadGRNHd.html?GRNCode="+code;	
			//alert(searchUrl);
			$.ajax({
			        type: "GET",
			        url: searchUrl,
				    dataType: "json",
				    success: function(response)
				    {
				    	if ('Invalid Code' == response.strGRNCode) {
							alert('Invalid Code');
							$("#txtGRNCode").val('');
							$("#txtGRNCode").focus();

						} else {
							funSetGRNHd(response);
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
		 * set GRN Hd Data in textfield
		 */
		function funSetGRNHd(response) {
			$("#txtCode").val(response.strGRNCode);
			$("#txtMInBy").val(response.strBillNo);
			$("#txtSuppCode").val(response.strSuppCode);
			$("#spSuppName").text(response.strSuppName);
			$("#txtSubTotal").val(parseFloat(response.dblSubTotal).toFixed(maxAmountDecimalPlaceLimit));
			$("#txtDisRate").val(parseFloat(response.dblDisRate).toFixed(maxAmountDecimalPlaceLimit));
			$("#txtDisAmt").val(parseFloat(response.dblDisAmt).toFixed(maxAmountDecimalPlaceLimit));
			$("#txtExtra").val(parseFloat(response.dblExtra).toFixed(maxAmountDecimalPlaceLimit));
			$("#txtFinalAmt").val(parseFloat(response.dblTotal).toFixed(maxAmountDecimalPlaceLimit));
			funSetGRNDtlData(response.strGRNCode);
			document.getElementById("txtLocCode").readOnly =true;
		}
		
		/**
		 * Remove all product form row
		 */
		function funRemoveProductRows()
		{
			var table = document.getElementById("tblProdDet");
			var rowCount = table.rows.length;
			while(rowCount>0)
			{
				table.deleteRow(0);
				rowCount--;
			}
		}
		/**
		 * filling Product Data in grid
		 */
		function funfillProdRow(strProdCode,strProdName,dblQty,strUOM,dblwt,dtlTotWt,dblPrice,dblTotalPrice,dblDiscount)
		{	
		    var table = document.getElementById("tblProdDet");
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    dblQty=parseFloat(dblQty).toFixed(maxQuantityDecimalPlaceLimit);
		    dblwt=parseFloat(dblwt).toFixed(maxQuantityDecimalPlaceLimit);
		    dtlTotWt=parseFloat(dtlTotWt).toFixed(maxQuantityDecimalPlaceLimit);
		    dblPrice=parseFloat(dblPrice).toFixed(maxAmountDecimalPlaceLimit);
		    dblTotalPrice=parseFloat(dblTotalPrice).toFixed(maxAmountDecimalPlaceLimit);
		    dblDiscount=parseFloat(dblDiscount).toFixed(maxAmountDecimalPlaceLimit);
		
		    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"10%\" name=\"listPurchaseReturnDtl["+(rowCount)+"].strProdCode\" id=\"txtProdCode."+(rowCount)+"\" value='"+strProdCode+"' />";   
		    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"100%\" id=\"txtProdCode."+(rowCount)+"\" value='"+strProdName+"' />";
		    row.insertCell(2).innerHTML= "<input type=\"text\"  required = \"required\" style=\"text-align: right;\" size=\"6%\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dblQty\" id=\"dblQty."+(rowCount)+"\" value="+dblQty+" class=\"decimal-places inputText-Auto\" onblur=\"funUpdatePrice(this);\">";
		    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"6%\" name=\"listPurchaseReturnDtl["+(rowCount)+"].strUOM\" id=\"strUOM."+(rowCount)+"\" value="+strUOM+">";		   
		    row.insertCell(4).innerHTML= "<input type=\"text\"  required = \"required\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dblWeight\" id=\"txtWt."+(rowCount)+"\" value="+dblwt+" class=\"decimal-places inputText-Auto\">";
		    row.insertCell(5).innerHTML= "<input readonly=\"readonly\" class=\"Box\" style=\"text-align: right;\" size=\"6%\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dtlTotWt\" id=\"dtlTotWt."+(rowCount)+"\" value="+dtlTotWt+" class=\"decimal-places inputText-Auto\">";
		    row.insertCell(6).innerHTML= "<input readonly=\"readonly\" type=\"text\"  required = \"required\" style=\"text-align: right;\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dblUnitPrice\"  id=\"dblUnitPrice."+(rowCount)+"\" value="+dblPrice+" class=\"decimal-places-amt inputText-Auto\">";
		    row.insertCell(7).innerHTML= "<input type=\"text\"  required = \"required\" style=\"text-align: right;\" size=\"6%\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dblDiscount\"  id=\"dblDiscount."+(rowCount)+"\" value="+dblDiscount+" class=\"decimal-places-amt inputText-Auto discount\" onblur=\"funUpdatePrice(this);\">";
		    row.insertCell(8).innerHTML= "<input readonly=\"readonly\"  style=\"text-align: right;\" size=\"6%\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dblTotalPrice\"  id=\"dblTotalPrice."+(rowCount)+"\" value="+dblTotalPrice+" class=\"decimal-places-amt inputText-Auto totalPriceCell Box\">" ;
		    row.insertCell(9).innerHTML= '<input type="button" value = "Delete" class="deletebutton" onClick="Javacsript:funDeleteRow(this)">';
		    funApplyNumberValidation();
		}

		/**
		 * Check validation before adding product data in grid
		 */
		function btnAdd_onclick() 
	    {
			if($("#txtProdCode").val()!="")
			{
				if($("#txtQty").val()!="" && $("#txtQty").val()!= 0 )
				{
					var strProdCode=$("#txtProdCode").val();
					if(funDuplicateProduct(strProdCode))
						{
							funAddRow();
						}
			    } 
				else
			    {
			    	alert("Please Enter Quantity");
			        $("#txtQty").focus();
			        return false;
			    }
			}
			else
		    {
		    	alert("Please Enter Product Code Or Search");
		        $("#txtProdCode").focus() ; 
		        return false;
		    }
		}
		/**
		 * Check duplicate record in grid
		 */
		function funDuplicateProduct(strProdCode)
		{
		    var table = document.getElementById("tblProdDet");
		    var rowCount = table.rows.length;		   
		    var flag=true;
		    if(rowCount > 0)
		    	{
				    $('#tblProdDet tr').each(function()
				    {
					    if(strProdCode==$(this).find('input').val())// `this` is TR DOM element
	    				{
					    	alert("Already added "+ strProdCode);
					    	 funClearProduct();
		    				flag=false;
	    				}
					});
				    
		    	}
		    return flag;
		  
		}
		
		/**
		 * Adding Product Data in grid 
		 */
		function funAddRow() 
		{
			var strProdCode =$("#txtProdCode").val();
			var strProdName=$("#spProdName").text();			
		    var dblQty = $("#txtQty").val();
		    dblQty=parseFloat(dblQty).toFixed(maxQuantityDecimalPlaceLimit);
		    var dblwt = $("#txtWt").val();
		    dblwt=parseFloat(dblwt).toFixed(maxQuantityDecimalPlaceLimit);
		    var dblPrice=$("#txtPrice").val();  
		    dblPrice=parseFloat(dblPrice).toFixed(maxAmountDecimalPlaceLimit);
		    var dblDiscount;
		   
		    if($("#txtDiscount").val()!="")
		   	{
		   		dblDiscount=$("#txtDiscount").val();			    	
		   		dblDiscount=parseFloat(dblDiscount).toFixed(maxAmountDecimalPlaceLimit);
		   	}
		    else
			{
		    	dblDiscount=0;
		    	dblDiscount=parseFloat(dblDiscount).toFixed(maxAmountDecimalPlaceLimit);
		    }
		    var totalPrice=parseFloat(dblPrice)*parseFloat(dblQty);
		    totalPrice=parseFloat(totalPrice).toFixed(maxAmountDecimalPlaceLimit);
		    totalPrice=totalPrice-dblDiscount;
		    
		    var dtlTotWt=dblPrice*dblwt;
		    dtlTotWt=parseFloat(dtlTotWt).toFixed(maxAmountDecimalPlaceLimit);
		    
		    var table = document.getElementById("tblProdDet");
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    rowCount=listRow;
		    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"10%\" name=\"listPurchaseReturnDtl["+(rowCount)+"].strProdCode\" id=\"txtProdCode."+(rowCount)+"\" value='"+strProdCode+"' />";   
		    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"100%\" id=\"txtProdCode."+(rowCount)+"\" value='"+strProdName+"' />";
		    row.insertCell(2).innerHTML= "<input type=\"text\"  required = \"required\" style=\"text-align: right;\" size=\"6%\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dblQty\" id=\"dblQty."+(rowCount)+"\" value="+dblQty+" class=\"decimal-places inputText-Auto\" onblur=\"funUpdatePrice(this);\">";
		    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"6%\" name=\"listPurchaseReturnDtl["+(rowCount)+"].strUOM\" id=\"strUOM."+(rowCount)+"\" value="+strUOM+">";		   
		    row.insertCell(4).innerHTML= "<input type=\"text\" required = \"required\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dblWeight\" id=\"txtWt."+(rowCount)+"\" value="+dblwt+" class=\"decimal-places inputText-Auto\">";
		    row.insertCell(5).innerHTML= "<input readonly=\"readonly\" class=\"Box\" style=\"text-align: right;\" size=\"6%\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dtlTotWt\" id=\"dtlTotWt."+(rowCount)+"\" value="+dtlTotWt+" class=\"decimal-places inputText-Auto\">";
		    row.insertCell(6).innerHTML= "<input readonly=\"readonly\" type=\"text\" required = \"required\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dblUnitPrice\"  id=\"dblUnitPrice."+(rowCount)+"\" value="+dblPrice+" class=\"decimal-places-amt inputText-Auto\">";
		    row.insertCell(7).innerHTML= "<input type=\"text\"  required = \"required\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dblDiscount\"  id=\"dblDiscount."+(rowCount)+"\" value="+dblDiscount+" class=\"decimal-places-amt inputText-Auto discount\" onblur=\"funUpdatePrice(this);\">";
		    row.insertCell(8).innerHTML= "<input style=\"text-align: right;\" size=\"6%\" name=\"listPurchaseReturnDtl["+(rowCount)+"].dblTotalPrice\"  id=\"dblTotalPrice."+(rowCount)+"\" value="+totalPrice+" class=\"inputText-Auto totalPriceCell Box\">" ;		    		    	    
		    row.insertCell(9).innerHTML= '<input type="button" value = "Delete" class="deletebutton" onClick="Javacsript:funDeleteRow(this)">';	    
		    listRow++;
		    funApplyNumberValidation();
		    funClearProduct();
		    funGetTotal();
		    $("#txtProdCode").focus() ; 
		    return false;
		}
		 
		/**
		 * Delete a particular record from a grid
		 */
		function funDeleteRow(obj) 
		{
		    var index = obj.parentNode.parentNode.rowIndex;
		    var table = document.getElementById("tblProdDet");
		    table.deleteRow(index);
		    funGetTotal();
		}
		/**
		 * Clear textfiled after adding data in textfield
		 */
		function funClearProduct()
		{
			$("#txtProdCode").val("");
			$("#spProdName").text("");
			$("#txtQty").val("");
			$("#txtWt").val("");
			$("#txtPrice").val("");
			$("#txtDiscount").val("");
			$("#txtProdCode").focus();
		}
		
		/**
		 * Combo box change event
		 */
		function funOnChange()
		{
			if($("#cmbAgainst").val()=='GRN')
			{
				$("#txtProdCode").attr("disabled", true);	
				$("#txtCode").css('visibility', 'visible');
			}
			else
			{
				$("#txtProdCode").attr("disabled", false);	
				$("#txtCode").css('visibility', 'hidden'); 
			}			
		}
		/**
		 * Open help form with criteria
		 */
		function funOpenSearch()
		{
			if($("#txtLocCode").val()!='')
			{
				funHelp('productInUse');
			}
			else
			{
				alert("Please Select Location or Search");
				$("#txtLocCode").focus();
				return false;
			 }
		}
		
		/**
		 * Update total price when user change the qty 
		 */
		function funUpdatePrice(object)
		{
			var index=object.parentNode.parentNode.rowIndex;
			var price=parseFloat(document.getElementById("dblUnitPrice."+index).value)*parseFloat(document.getElementById("dblQty."+index).value);
			price=parseFloat(price).toFixed(maxAmountDecimalPlaceLimit);
			var discount= document.all("dblDiscount."+index).value;	
			document.getElementById("dblTotalPrice."+index).value=parseFloat(price)-parseFloat(discount);
			funGetTotal();
		}
			
		/**
		 * Calcutating subtotal
		 */
		function funGetTotal()
	    {
	       var totalPrice=0;
	       var subtot=0;
		    $('#tblProdDet tr').each(function() {
			    var totalPriceCell = $(this).find(".totalPriceCell").val();	
				totalPrice=(parseFloat(totalPriceCell)+parseFloat(totalPrice));
			 });
		   
			subtot=parseFloat(totalPrice).toFixed(maxAmountDecimalPlaceLimit);
			$("#txtSubTotal").val(subtot);
			funCalculateTotal();
	    }
		
		/**
		 * Calcutating Total Amount
		 */
		function funCalculateTotal()
		{
			var finalamt=0;
			if($("#txtSubTotal").val()!=""){
		        finalamt=parseFloat(finalamt)+parseFloat($("#txtSubTotal").val());
			}
		    if($("#txtDisAmt").val()!=""){
		        finalamt=parseFloat(finalamt)-parseFloat($("#txtDisAmt").val());
		    }
		    if($("#txtExtra").val()!="")
		    {
		        finalamt=parseFloat(finalamt)+parseFloat($("#txtExtra").val());    
		    }
		    finalamt=parseFloat(finalamt).toFixed(maxAmountDecimalPlaceLimit);
		    $("#txtFinalAmt").val(finalamt);
		}
		
		/**
		 * Calcutating Discount
		 */
		function funCalDiscount()
		{
			if($("#txtDisRate").val()!="")
		    {
		    	var subtotal=parseFloat($("#txtSubTotal").val());
		        var disCount=parseFloat($("#txtDisRate").val());
		        subtotal=parseFloat(subtotal).toFixed(maxAmountDecimalPlaceLimit);
		        disCount=parseFloat(disCount).toFixed(maxAmountDecimalPlaceLimit);
		        $("#txtDisAmt").val(disCount * subtotal/100);
		        funCalculateTotal();
		        $("#txtExtra").focus();
		    }
		}
		
		/**
		 * Checking Validation before submiting the data
		 */
		function funCallFormAction(actionName,object) 
		{
			if(!fun_isDate($("#txtPurchDate").val()))
			{
				alert('Invalid Date');
		        $("#txtPurchDate").focus();
		        return false;
			}
			var table = document.getElementById("tblProdDet");
		    var rowCount = table.rows.length;
			   
			if($("#txtLocCode").val()=="")
			{
				alert("Please Enter Location Or Search");
				return false;
			}			 
			else if($("#txtSuppCode").val()=="")
			{
			 	alert("Please Enter Supplier Or Search");
				return false;
			}
			else if(rowCount==0)
			{
			  	alert("Please fill the Product");
			  	return false;
			}
			

			if(  $("#cmbAgainst").val() == null || $("#cmbAgainst").val().trim().length == 0 )
			 {
			 		alert("Please Select Against");
					return false;
			 }
			
			else
			{
				return true;
			}
		}
		
		
		/**
		 * Ready function for Textfield on blur event
		 */
		$(function()
		{
			$('#txtPRCode').blur(function () 
			{
				var code=$('#txtPRCode').val();   
				if (code.trim().length > 0 && code !="?" && code !="/")
				{
					funSetPurchaseReturnData(code);
				}
			});
			
			$('#txtSuppCode').blur(function () 
			{
				var code=$('#txtSuppCode').val();   
				if (code.trim().length > 0 && code !="?" && code !="/"){
				   funSetSupplier(code);
				}
			});
						
			$('#txtLocCode').blur(function () {
			  	var code=$('#txtLocCode').val();   
				if (code.trim().length > 0 && code !="?" && code !="/"){
					funSetLocation(code);
				}
			});
						
			$('#txtProdCode').blur(function () {
				var code=$('#txtProdCode').val();   
				if (code.trim().length > 0 && code !="?" && code !="/")
				{
			  		funSetProduct(code);
				}
			});
		});
		 
		/**
		 * Number field Validation
		 */
		function funApplyNumberValidation(){
				$(".numeric").numeric();
				$(".integer").numeric(false, function() { alert("Integers only"); this.value = ""; this.focus(); });
				$(".positive").numeric({ negative: false }, function() { alert("No negative values"); this.value = ""; this.focus(); });
				$(".positive-integer").numeric({ decimal: false, negative: false }, function() { alert("Positive integers only"); this.value = ""; this.focus(); });
			    $(".decimal-places").numeric({ decimalPlaces: maxQuantityDecimalPlaceLimit, negative: false });
			    $(".decimal-places-amt").numeric({ decimalPlaces: maxAmountDecimalPlaceLimit, negative: false });
		}
		
		/**
		 * Ready Function for Initialize textField with default value
		 * And Getting session Value
		 * Success Message after Submit the Form
		 * Open slip
		 */
		$(document).ready(function(){
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
				
				var code='';
				<%if(null!=session.getAttribute("rptPRCode")){%>
				code='<%=session.getAttribute("rptPRCode").toString()%>';
				 '<%session.removeAttribute("rptPRCode");%>'
				
				var isOk=confirm("Do You Want to Generate Slip?");
				if(isOk){
				window.open(getContextPath()+"/openRptPRSlip.html?rptPRCode="+code,'_blank');
				}
				<%}%>
				
				var flagOpenFromAuthorization="${flagOpenFromAuthorization}";
				if(flagOpenFromAuthorization == 'true')
				{
					funSetPurchaseReturnData("${authorizationPRCode}");
				}
		});
		
		/**
		 * Reset function
		 */
		function funResetFields()
		{
			location.reload(true); 
		}
		
		function funCheckReadonly(byloc)
		{
			if ($('#txtLocCode').attr("readonly") != "readonly")
			{ 
				funHelp(byloc);
			}
			
		}
		
</script>
</head>
<body onload="funOnLoad()">
<div id="formHeading">
		<label>Purchase Return</label>
</div>
<s:form name="purchaseReturn" action="savePR.html?saddr=${urlHits}" method="POST">
   <br>
		<table class="transTable">
		<tr><th colspan="5"></th></tr>
			<tr>
				<td width="140px">PR Code</td>
				<td  width="120px"><s:input id="txtPRCode" onblur="funGetPRDetails()"
						ondblclick="funHelp('PurchaseReturn')" type="text" path="strPRCode" cssClass="searchTextBox"></s:input></td>
				<td width="180px"><span>PR No</span> 
<%-- 				<s:input id="txtPurchCode" type="text"  --%>
<%-- 						path="strPRNo" value="" cssClass="BoxW116px"/> --%>
						</td>
				<td  width="140px">PR Date</td>
				<td><s:input type="text" id="txtPurchDate" path="dtPRDate" required="required"  pattern="\d{1,2}-\d{1,2}-\d{4}"
						value="${command.dtPRDate}"  cssClass="calenderTextBox"/></td>
			</tr>
			<tr>
				<td>Against</td>
				<td><s:select id="cmbAgainst" onchange="funOnChange()"
						items="${strProcessList}" path="strAgainst" cssClass="BoxW124px" >
					</s:select></td>
				<td><s:input id="txtCode" type="text" path="strGRNCode" cssClass="BoxW116px" ondblclick="funHelp('grncode');"/></td>
				<td>Supplier</td>
				<td><s:input id="txtSuppCode" type="text" required="required"  path="strSuppCode"
						value="${command.strSuppCode}" ondblclick="funHelp('suppcode')"  cssClass="searchTextBox"/>
					<span id="spSuppName"></span></td>
			</tr>
			<tr>
				<td>Location</td>
				<td><s:input id="txtLocCode" type="text" required="required"  path="strLocCode"
						value="${locationCode}" ondblclick="funCheckReadonly('locby')" cssClass="searchTextBox"/></td>
				<td><span id="spLocName" >${locationName}</span></td>  
				<td>Mode Of Transport</td>
				<td><s:input id="txtVehNo" type="text" path="strVehNo"
						value="${command.strVehNo}" cssClass="BoxW116px"/></td>
			</tr>
			<tr>
				<td>Your Bill / DC Reference</td>
				<td><s:input id="txtMInBy"  type="text" path="strMInBy"
						value="${command.strMInBy}" cssClass="BoxW116px"/></td>
				<td></td>
				<td>Time &amp; Date Of Removal</td>
				<td><s:input id="txtTimeInOut" type="text"  path="strTimeInOut"
						value="${command.strTimeInOut}" cssClass="BoxW116px"/></td>
			</tr>
		</table>
		<table class="transTableMiddle1">
			<tr>
			<td width="140px">Product</td>
				<td  width="120px"><input id="txtProdCode" type="text"
					ondblclick="funOpenSearch();" class="searchTextBox"/></td>
				<td colspan="4"><span id="spProdName"></span></td>
			</tr>
			<tr>
				<td>Unit Price</td>
				<td><input id="txtPrice" type="text"  class="decimal-places-amt numberField"/></td>
				<td>Wt/Unit</td>
				<td><input id="txtWt" type="text"   class="decimal-places numberField"/></td>
				<td>Quantity</td>
				<td><input id="txtQty" type="text"  class="decimal-places numberField" /></td>
				</tr><tr>
				<td>Discount</td>
				<td><input id="txtDiscount" type="text"class="decimal-places-amt numberField" />
				<td>Stock </td><td><label id="spStock"></label></td>
				<td colspan="2"><input id="btnAdd" type="button" value="Add"
					onclick="return btnAdd_onclick()" class="smallButton" /></td>
				
			</tr>
		</table>

		<div class="dynamicTableContainer"  style="height:300px;">
			<table style="height: 20px; border: #0F0; width: 100%;font-size:11px;
			font-weight: bold;">

				<tr bgcolor="#72BEFC">
					<td width="6%">Prod Code</td>
					<!--  COl1   -->
					<td width="24%">Prod Name</td>
					<!--  COl2   -->
					<td width="5%">Qty</td>
					<!--  COl3   -->
					<td width="5%">UOM</td>
					<!--  COl3   -->
					<td width="5%">Wt/ Unit</td>
					<!--  COl4   -->
					<td width="5%">Total Wt</td>
					<!--  COl5   -->
					<td width="5%">Unit Price</td>
					<!--  COl6   -->
					<td width="5%">Discount</td>
					<!--  COl7   -->
					<td width="5%">Total Price</td>
					<!--  COl8   -->
					<td style="width:0%;display: none"></td>
					<td style="width:0%;display: none"></td>					
					<!--  COl9   -->
					<td width="5%">Delete</td>
					<!--  COl10   -->
				</tr>
			</table>
			
<div style="background-color:  	#a4d7ff;
    border: 1px solid #ccc;
    display: block;
    height: 260px;
    margin: auto;
    overflow-x: hidden;
    overflow-y: scroll;
    width: 100%;">
    
		<table id="tblProdDet" style="width:100%;border:
			#0F0;table-layout:fixed;overflow:scroll" class="transTablex col10-center">
				<tbody>
					<col style="width:7%"><!--  COl1   -->
					<col style="width:29%"><!-- COl2   -->
					<col style="width:5.5%"><!--COl3   -->
					<col style="width:6%"><!--  COl4   -->
					<col style="width:6%"><!--  COl5   -->
					<col style="width:6%"><!--  COl6   -->
					<col style="width:6%"><!--  COl7   -->
					<col style="width:6%"><!--  COl8   -->
					<col style="width:6%"><!--  COl9   -->					
					<col style="width:5%"><!--COl10  -->
				</tbody>
		</table>
		</div>
</div>

	<table class="transTable">
				<tr>
					<td></td>
					<td></td>
					<td> Sub Total</td>
					<td><s:input id="txtSubTotal" path="dblSubTotal" readonly="true" cssClass="decimal-places-amt numberField" value="${command.dblSubTotal}"></s:input></td>				   
				</tr>
				<tr>
				
				    <td> Discount %</td>
				    <td><s:input id="txtDisRate" type="text"  onblur="funCalDiscount();" cssClass="decimal-places-amt numberField" path="dblDisRate" value="${command.dblDisRate}"/></td>
				    <td > Discount Amount</td>
				    <td> <s:input id="txtDisAmt" type="text"  onblur="funCalculateTotal();" cssClass="decimal-places-amt numberField" path="dblDisAmt" value="${command.dblDisAmt}" />
				    </td>				    
				</tr>
				
				<tr>				
				    <td >Narration</td>
				    <td><s:textarea id="txtNarration" style="width: 90%" path="strNarration" value="${command.strNarration }"></s:textarea>
				    </td>
				    <td > Extra Charges</td>
				    <td><s:input id="txtExtra"  type="text"  cssClass="decimal-places-amt numberField" path="dblExtra" value="${command.dblExtra}" onblur="funCalculateTotal();" /></td>				   
				</tr>
				
				<tr >
					<td></td>
					<td></td>
					<td>Final Amount</td>
					<td><s:input id="txtFinalAmt" type="text" readonly="true" cssClass="decimal-places-amt numberField" path="dblTotal" value="0"></s:input></td>				    
				</tr>
				<tr><td colspan="4"></td></tr>

		</table>

	<br />
		<p align="center">
			<input type="submit" value="Submit"
				onclick="return funCallFormAction('submit',this)"
				class="form_button" />
				 <input type="button" value="Reset" onclick="funResetFields();" class="form_button" />
		</p>
	<br /><br />
	
	<div id="wait" style="display:none;width:60px;height:60px;border:0px solid black;position:absolute;top:60%;left:55%;padding:2px;">
				<img src="../${pageContext.request.contextPath}/resources/images/ajax-loader-light.gif" width="60px" height="60px" />
	</div>
	</s:form>
	
	<script type="text/javascript">
	
	funApplyNumberValidation();
	funOnChange();
	
	</script>
</body>
</html>