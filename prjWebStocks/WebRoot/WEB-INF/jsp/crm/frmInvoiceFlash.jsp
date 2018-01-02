<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<script type="text/javascript">
		var invoceFlashData;
		
		$(document).ready(function() 
				{		
				
					$("#txtFromDate").datepicker({ dateFormat: 'yy-mm-dd' });
					$("#txtFromDate").datepicker('setDate','today');
					$("#txtFromDate").datepicker();
					$("#txtToDate").datepicker({ dateFormat: 'yy-mm-dd' });
					$("#txtToDate" ).datepicker('setDate', 'today');
					$("#txtToDate").datepicker();
					
					var code='<%=session.getAttribute("locationCode").toString()%>';
					funSetLocation(code);
					funOnClckBillWiseBtn('divBillWise');
					
					$(document).ajaxStart(function()
							{
							    $("#wait").css("display","block");
							});
							$(document).ajaxComplete(function()
							{
								$("#wait").css("display","none");
							});
		
				});
		function funHelp(transactionName)
		{
			fieldName = transactionName;
		//	window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
			window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;top=500,left=500")
		}

		 	function funSetData(code)
			{
		 		
		 		
		 		switch (fieldName)
				{
				
				 case 'locationmaster':
				    	funSetLocation(code);
				        break;
				        
				 case 'custMaster' :
			 			funSetCuster(code);
			 			break;
				}		
			}

		 	function funSetCuster(code)
			{
				var gurl=getContextPath()+"/loadPartyMasterData.html?partyCode=";
				$.ajax({
			        type: "GET",
			        url: gurl+code,
			        dataType: "json",
			        success: function(response)
			        {		        	
			        		if('Invalid Code' == response.strPCode){
			        			alert("Invalid Customer Code");
			        			$("#txtCustCode").val('');
			        			$("#txtCustCode").focus();
			        		}else{			   
			        			$("#txtCustCode").val(response.strPCode);
								$("#lblCustomerName").text(response.strPName);
					
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
			
		 	function funSetLocation(code) {
		 		var searchUrl = "";
		 		searchUrl = getContextPath()
		 				+ "/loadLocationMasterData.html?locCode=" + code;

		 		$.ajax({
		 			type : "GET",
		 			url : searchUrl,
		 			dataType : "json",
		 			success : function(response) {
		 				if (response.strLocCode == 'Invalid Code') {
		 					alert("Invalid Location Code");
		 					$("#txtLocCode").val('');
		 					$("#lblLocName").text("");
		 					$("#txtLocCode").focus();
		 				} else {
		 					$("#txtLocCode").val(response.strLocCode);						
		 					$("#lblLocName").text(response.strLocName);
		 					$("#txtProdCode").focus();
		 				}
		 			},
		 			error : function(jqXHR, exception) {
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
		 		
			$(document).ready(function() 
			{
				$("#btnExecute").click(function( event )
				{
// 					funCalculateInvoiceFlash();
					
				});
					
				$(document).ajaxStart(function()
				{
				    $("#wait").css("display","block");
				});
				$(document).ajaxComplete(function()
				{
					$("#wait").css("display","none");
				});
			});
		 	
			function funCalculateInvoiceFlash()
			{	
				var Code=$('#cmbSettlement').val();	 
				if(Code=="")
				{
					Code="All";
				}
				var frmDte1=$('#txtFromDate').val();
				var toDte1=$('#txtToDate').val();
				var locCode1=$('#txtLocCode').val();
				var searchUrl=getContextPath()+"/loadInvoiceFlash.html?settlementcode="+Code+"&frmDte="+frmDte1+"&toDte="+toDte1+"&locCode="+locCode1;
			
				$.ajax({
				        type: "GET",
				        url: searchUrl,
					    dataType: "json",
					    success: function(response)
					    {
					    	funShowProductDetail(response)
					    	invoceFlashData=response[0];
 					    	showTable();
					    	funGetTotalValue(response[1],response[2],response[3]);
					    	
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
			
			
			function funShowProductDetail(invoiceData)
			{
				var table = document.getElementById("tblProdDet");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			 
		       var strSOCode="";
			    row.insertCell(0).innerHTML= "<input name=\"strInvCode.["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box txtProdCode\" size=\"8%\" id=\"txtProdCode."+(rowCount)+"\" value='"+strProdCode+"' />";		  		   	  
			    row.insertCell(1).innerHTML= "<input name=\"dteInv.["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"40%\" id=\"txtProdName."+(rowCount)+"\" value='"+strProdName+"'/>";
			    row.insertCell(2).innerHTML= "<input name=\"strProdType.["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"0%\" id=\"txtProdTpye."+(rowCount)+"\" value='"+strProdType+"'/>";
			    row.insertCell(3).innerHTML= "<input name=\"dblQty.["+(rowCount)+"]\" type=\"text\"  required = \"required\" style=\"text-align: right;\" size=\"3.9%\"  class=\"decimal-places inputText-Auto  txtQty\" id=\"txtQty."+(rowCount)+"\" value="+dblQty+" onblur=\"Javacsript:funUpdatePrice(this)\">";
			    row.insertCell(4).innerHTML= "<input name=\"dblWeight.["+(rowCount)+"]\" type=\"text\"  required = \"required\" style=\"text-align: right;\" size=\"3.9%\" class=\"decimal-places inputText-Auto\" id=\"txtWeight."+(rowCount)+"\" value="+dblWeight+" >";
			    row.insertCell(5).innerHTML= "<input name=\"dblTotalWeight.["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;\" size=\"3.9%\" id=\"dblTotalWeight."+(rowCount)+"\"   value='"+dblTotalWeight+"'/>";
			    row.insertCell(6).innerHTML= "<input name=\"dblUnitPrice.["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box txtUnitprice\" style=\"text-align: right;\" \size=\"3.9%\" id=\"unitprice."+(rowCount)+"\"   value='"+unitprice+"'/>";
			
			
			
			   // funGetTotal();
			    return false;
			}

			function showTable()
			{
				var optInit = getOptionsFromForm();
			    $("#Pagination").pagination(invoceFlashData.length, optInit);	
			    $("#divValueTotal").show();
			    
			}
		
			var items_per_page = 10;
			function getOptionsFromForm()
			{
			    var opt = {callback: pageselectCallback};
				opt['items_per_page'] = items_per_page;
				opt['num_display_entries'] = 10;
				opt['num_edge_entries'] = 3;
				opt['prev_text'] = "Prev";
				opt['next_text'] = "Next";
			    return opt;
			}
			
			
			
			function pageselectCallback(page_index, jq)
			{
			    // Get number of elements per pagionation page from form
			    var max_elem = Math.min((page_index+1) * items_per_page, invoceFlashData.length);
			    var newcontent="";
			  		    	
				   	newcontent = '<table id="tblInvoiceFlash" class="transTablex" style="width: 100%;font-size:11px;font-weight: bold;"><tr bgcolor="#75c0ff"><td id="labld1" size="10%">Invoice Code</td><td id="labld2"> Date</td><td id="labld3"> Customer Name</td>	<td id="labld4"> Against </td> <td id="labld5"> Vehicle No </td>	<td id="labld6"> Excisable </td> <td id="labld7"> SubTotal</td><td id="labld8"> Tax Amount</td><td id="labld9"> Grand Toal</td></tr>';
				   	// Iterate through a selection of the content and build an HTML string
				    for(var i=page_index*items_per_page;i<max_elem;i++)
				    {
				        newcontent += '<tr><td><a id="stkLedgerUrl.'+i+'" href="#" onclick="funClick(this);">'+invoceFlashData[i].strInvCode+'</a></td>';
				        newcontent += '<td>'+invoceFlashData[i].dteInvDate+'</td>';
				        newcontent += '<td>'+invoceFlashData[i].strCustName+'</td>';
				        newcontent += '<td>'+invoceFlashData[i].strAgainst+'</td>';
				        newcontent += '<td>'+invoceFlashData[i].strVehNo+'</td>';
				        newcontent += '<td align="Center">'+invoceFlashData[i].strExciseable+'</td>';
				        newcontent += '<td align="right">'+parseFloat(invoceFlashData[i].dblSubTotalAmt).toFixed(maxQuantityDecimalPlaceLimit)+'</td>';
				        newcontent += '<td align="right">'+parseFloat(invoceFlashData[i].dblTaxAmt).toFixed(maxQuantityDecimalPlaceLimit)+'</td>';
				        newcontent += '<td align="right">'+parseFloat(invoceFlashData[i].dblTotalAmt).toFixed(maxQuantityDecimalPlaceLimit)+'</td>';
				    }
			    
			    newcontent += '</table>';
			    // Replace old content with new content
			   
			    $('#Searchresult').html(newcontent);
			   
			    // Prevent click eventpropagation
			    return false;
			}
			
			function funClick(obj,dteObj)
			{
				var code=document.getElementById(""+obj.id+"").innerHTML;
				window.open(getContextPath()+"/rptInvoiceSlipFromat2.html?rptInvCode="+code,'_blank');
				window.open(getContextPath()+"/rptInvoiceSlipNonExcisableFromat2.html?rptInvCode="+code,'_blank');
			}
			
			
			
			$(document).ready(function () 
					{			 
						$("#btnExport").click(function (e)
						{
							var Code=$('#txtCustCode').val();	 
							if(Code=="")
							{
								Code="All";
							}
							var frmDte1=$('#txtFromDate').val();
							var toDte1=$('#txtToDate').val();
							var locCode1=$('#txtLocCode').val();

		
						window.location.href=getContextPath()+"/exportInvoiceExcel.html?custcode="+Code+"&frmDte="+frmDte1+"&toDte="+toDte1+"&locCode="+locCode1;
									
									});
					});
			
			
			
			
			
			
			
			function funShowTableGUI(divID)
			{
				// for hide Table GUI
				document.all["divTenderWise"].style.display = 'none';
 				document.all["divOperatorWise"].style.display = 'none';
 				document.all["divBillWise"].style.display = 'none';
				document.all["divCustomerWise"].style.display = 'none';
				document.all["divSKUWise"].style.display = 'none';
				document.all["divCategoryWise"].style.display = 'none';
				document.all["divManufactureWise"].style.display = 'none';
				document.all["divDepartmentWise"].style.display = 'none';
// 				document.all["divProfessionTable"].style.display = 'none';
// 				document.all["divMaritalTable"].style.display = 'none';
// 				document.all["divEducationTable"].style.display = 'none';
// 				document.all["divCommitteeMemberRoleTable"].style.display = 'none';
// 				document.all[ 'divRelationTable' ].style.display = 'none';
				

// 		 		document.all["divStaffTable"].style.display = 'none';
// 		 		document.all["divCurrencyDetailsTable"].style.display = 'none';
// 		 		document.all["divInvitedByTable"].style.display = 'none';
// 		 		document.all["divItemCategoryTable"].style.display = 'none';
// 		 		document.all["divProfileTable"].style.display = 'none';
// 		 		document.all["divSalutationTable"].style.display = 'none';
// 		 		document.all["divTitleTable"].style.display = 'none';
				
				
				// for show Table GUI
				document.all[divID].style.display = 'block';
				
			}
			
			
			
			function funOnClckBillWiseBtn( divId)
			{
				funShowTableGUI(divId);
				var settCode=$('#cmbSettlement').val();
				var locCode=$('#txtLocCode').val();
				
			
				var frmDte1=$('#txtFromDate').val();
				var toDte1=$('#txtToDate').val();
				var locCode1=$('#txtLocCode').val();
				var searchUrl=getContextPath()+"/loadInvoiceFlash.html?settlementcode="+settCode+"&frmDte="+frmDte1+"&toDte="+toDte1+"&locCode="+locCode;
			
				$.ajax({
				        type: "GET",
				        url: searchUrl,
					    dataType: "json",
					    success: function(response)
					    {
					    	funBillWiseProductDetail(response[0])
					    	funGetTotalValue(response[1],response[2],response[3]);
					    	
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
			
			function funGetTotalValue(dblTotalValue,dblSubTotalValue,dblTaxTotalValue)
			{
				$("#txtTotValue").val(parseFloat(dblTotalValue).toFixed(maxQuantityDecimalPlaceLimit));
				$("#txtSubTotValue").val(parseFloat(dblSubTotalValue).toFixed(maxQuantityDecimalPlaceLimit));
				$("#txtTaxTotValue").val(parseFloat(dblTaxTotalValue).toFixed(maxQuantityDecimalPlaceLimit));
			}
			
			function funBillWiseProductDetail(ProdDtl)
			{
				$('#tblProdDet tbody').empty();
				for(var i=0;i<ProdDtl.length;i++)
				{
				 var data=ProdDtl[i];
				
			    var table = document.getElementById("tblProdDet");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    row.insertCell(0).innerHTML= "<input name=\"StrInvCode["+(rowCount)+"]\" readonly=\"readonly\"  class=\"Box\" size=\"20%\" id=\"StrInvCode."+(rowCount)+"\" value='"+data.strInvCode+"'>";		    
			    row.insertCell(1).innerHTML= "<input name=\"DteInvDate["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"25%\" id=\"DteInvDate."+(rowCount)+"\" value='"+data.dteInvDate+"'>";
			    row.insertCell(2).innerHTML= "<input name=\"StrCustName["+(rowCount)+"]\" id=\"StrCustName."+(rowCount)+"\" readonly=\"readonly\"   size=\"14%\" class=\"Box\" value="+data.strCustName+">";
			    row.insertCell(3).innerHTML= "<input name=\"StrAgainst["+(rowCount)+"]\" id=\"StrAgainst."+(rowCount)+"\" readonly=\"readonly\"   size=\"14%\" class=\"Box\"  value="+data.strAgainst+">";
			    row.insertCell(4).innerHTML= "<input name=\"StrVehNo["+(rowCount)+"]\" id=\"StrVehNo."+(rowCount)+"\" readonly=\"readonly\" size=\"14%\" class=\"Box\" value="+data.strVehNo+">";
			    row.insertCell(5).innerHTML= "<input name=\"DblSubTotalAmt["+(rowCount)+"]\" id=\"DblSubTotalAmt."+(rowCount)+"\" readonly=\"readonly\" style=\"text-align: right;\" size=\"15%\" class=\"Box\" value="+data.dblSubTotalAmt+">";
			    row.insertCell(6).innerHTML= "<input name=\"DblTaxAmt["+(rowCount)+"]\" id=\"DblTaxAmt."+(rowCount)+"\" readonly=\"readonly\" style=\"text-align: right;\" size=\"15%\" class=\"Box\" value="+data.dblTaxAmt+">";
			    row.insertCell(7).innerHTML= "<input name=\"DblTotalAmt["+(rowCount)+"]\" id=\"DblTotalAmt."+(rowCount)+"\" readonly=\"readonly\" style=\"text-align: right;\" size=\"15%\" class=\"Box\" value="+data.dblTotalAmt+">";
			   
			    
			    funApplyNumberValidation();
				}
			}
			
			function funApplyNumberValidation(){
				$(".numeric").numeric();
				$(".integer").numeric(false, function() { alert("Integers only"); this.value = ""; this.focus(); });
				$(".positive").numeric({ negative: false }, function() { alert("No negative values"); this.value = ""; this.focus(); });
				$(".positive-integer").numeric({ decimal: false, negative: false }, function() { alert("Positive integers only"); this.value = ""; this.focus(); });
			    $(".decimal-places").numeric({ decimalPlaces: maxQuantityDecimalPlaceLimit, negative: false });
			    $(".decimal-places-amt").numeric({ decimalPlaces: maxAmountDecimalPlaceLimit, negative: false });
			}
			
			
			function funOnClckTenderWiseBtn( divId)
			{
				funShowTableGUI(divId)
				var settCode=$('#cmbSettlement').val();
				var locCode=$('#txtLocCode').val();
				
			
				var frmDte1=$('#txtFromDate').val();
				var toDte1=$('#txtToDate').val();
				var locCode1=$('#txtLocCode').val();
				var searchUrl=getContextPath()+"/loadTenderWiseDtl.html?settlementcode="+settCode+"&frmDte="+frmDte1+"&toDte="+toDte1+"&locCode="+locCode;
			
				$.ajax({
				        type: "GET",
				        url: searchUrl,
					    dataType: "json",
					    success: function(response)
					    {
					    	funTenderWiseProductDetail(response[0]);
					    	$("#txtTotValue").val(parseFloat(response[1]).toFixed(maxQuantityDecimalPlaceLimit));
					    	
					    	
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
			
			function funTenderWiseProductDetail(ProdDtl)
			{
				$('#tblTenderProdDet tbody').empty();
				for(var i=0;i<ProdDtl.length;i++)
				{
				 var data=ProdDtl[i];
				
			    var table = document.getElementById("tblTenderProdDet");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    row.insertCell(0).innerHTML= "<input name=\"StrInvCode["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"20%\" id=\"StrInvCode."+(rowCount)+"\" value='"+data[0]+"'>";		    
			    row.insertCell(1).innerHTML= "<input name=\"DteInvDate["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"25%\" id=\"DteInvDate."+(rowCount)+"\" value='"+data[1]+"'>";
			    row.insertCell(2).innerHTML= "<input name=\"StrCustName["+(rowCount)+"]\" id=\"StrCustName."+(rowCount)+"\" readonly=\"readonly\"  size=\"14%\" class=\"Box\" value="+data[2]+">";
			    row.insertCell(3).innerHTML= "<input name=\"StrAgainst["+(rowCount)+"]\" id=\"StrAgainst."+(rowCount)+"\"readonly=\"readonly\"  style=\"text-align: right;\"  size=\"25%\" class=\"Box\"  value="+data[3]+">";
			    
			    funApplyNumberValidation();
				}
			}
			
			
			
			function funOnClckoperatorWiseBtn( divId)
			{
				funShowTableGUI(divId)
				var settCode=$('#cmbSettlement').val();
				var locCode=$('#txtLocCode').val();
				
			
				var frmDte1=$('#txtFromDate').val();
				var toDte1=$('#txtToDate').val();
				var locCode1=$('#txtLocCode').val();
				var searchUrl=getContextPath()+"/loadOpertorWiseDtl.html?settlementcode="+settCode+"&frmDte="+frmDte1+"&toDte="+toDte1+"&locCode="+locCode;
			
				$.ajax({
				        type: "GET",
				        url: searchUrl,
					    dataType: "json",
					    success: function(response)
					    {
					    	funOperatorrWiseProductDetail(response[0],response[2]);
					    	$("#txtTotValue").val(parseFloat(response[1]).toFixed(maxQuantityDecimalPlaceLimit));
					    	
					    	
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
			
			function funOperatorrWiseProductDetail(ProdDtl,userWiseSalesAmt)
			{
				var userName="";
				$('#tblOpertorProdDet tbody').empty();
				var rowCount = "";
			    var row ="";
				var amt='';
				for(var i=0;i<ProdDtl.length;i++)
				{
				 var data=ProdDtl[i];
				 var nextProdDtl="";
				 var j=i+1;
				 if(j<ProdDtl.length)
					 {
					 var j=i+1;
					 nextProdDtl=ProdDtl[j];					 
					 }
				 else{
					 nextProdDtl=ProdDtl[i];
				 }
				var usercode="";
			
				$.each(userWiseSalesAmt, function(j,item)
						{
							
							if(j==data[0])
								{
								amt=item;
								}
						});
			
			    var table = document.getElementById("tblOpertorProdDet");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    row.insertCell(0).innerHTML= "<input name=\"OperatorCode["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"20%\" id=\"OperatorCode."+(rowCount)+"\" value='"+data[0]+"'>";		    
			    row.insertCell(1).innerHTML= "<input name=\"OperatorName["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"25%\" id=\"OperatorName."+(rowCount)+"\" value='"+data[1]+"'>";
			    row.insertCell(2).innerHTML= "<input name=\"SalesAmt["+(rowCount)+"]\" id=\"SalesAmt."+(rowCount)+"\"readonly=\"readonly\" style=\"text-align: right;\"  size=\"14%\" class=\"Box\" value="+data[2]+">";
			    row.insertCell(3).innerHTML= "<input name=\"DiscAmt["+(rowCount)+"]\" id=\"DiscAmt."+(rowCount)+"\" readonly=\"readonly\"  style=\"text-align: right;\"  size=\"14%\" class=\"Box\"  value="+data[3]+">";
			    row.insertCell(4).innerHTML= "<input name=\"PaymentMode["+(rowCount)+"]\" id=\"PaymentMode."+(rowCount)+"\" readonly=\"readonly\"  style=\"text-align: right;\"  size=\"14%\" class=\"Box\"  value="+data[4]+">";
			   
			    if(data[0]==nextProdDtl[0])
			    {    
				if(i==(ProdDtl.length-1))
					{
					 row.insertCell(5).innerHTML= "<input name=\"operatorTotl["+(rowCount)+"]\" id=\"operatorTotl."+(rowCount)+"\" readonly=\"readonly\"  style=\"text-align: right;\"  size=\"14%\" class=\"Box\"  value="+amt+">";
					}else{
			    	row.insertCell(5).innerHTML= "<input name=\"operatorTotl["+(rowCount)+"]\" id=\"operatorTotl."+(rowCount)+"\" readonly=\"readonly\"  style=\"text-align: right;\"  size=\"14%\" class=\"Box\"  value="+usercode+">";
			    }}
			    else{
 			    row.insertCell(5).innerHTML= "<input name=\"operatorTotl["+(rowCount)+"]\" id=\"operatorTotl."+(rowCount)+"\" readonly=\"readonly\"  style=\"text-align: right;\"  size=\"14%\" class=\"Box\"  value="+amt+">";
			    }
					}
				
				  funApplyNumberValidation();
			}
			
			
			
			function funOnClckCuctomerWiseBtn( divId)
			{
				funShowTableGUI(divId)
				var settCode=$('#cmbSettlement').val();
				var locCode=$('#txtLocCode').val();
				
			
				var frmDte1=$('#txtFromDate').val();
				var toDte1=$('#txtToDate').val();
				var locCode1=$('#txtLocCode').val();
				var searchUrl=getContextPath()+"/loadCustomerWiseDtl.html?settlementcode="+settCode+"&frmDte="+frmDte1+"&toDte="+toDte1+"&locCode="+locCode;
			
				$.ajax({
				        type: "GET",
				        url: searchUrl,
					    dataType: "json",
					    success: function(response)
					    {
					    	funcustomerWiseProductDetail(response[0]);
					    	$("#txtTotValue").val(parseFloat(response[1]).toFixed(maxQuantityDecimalPlaceLimit));
					    	
					    	
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
			
			function funcustomerWiseProductDetail(ProdDtl)
			{
				var userName="";
				$('#tblCustomerProdDet tbody').empty();
				var rowCount = "";
			    var row ="";
				var amt='';
				for(var i=0;i<ProdDtl.length;i++)
				{
				 var data=ProdDtl[i];
				
			    var table = document.getElementById("tblCustomerProdDet");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    row.insertCell(0).innerHTML= "<input name=\"CustomerCode["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"20%\" id=\"CustomerCode."+(rowCount)+"\" value='"+data[0]+"'>";		    
			    row.insertCell(1).innerHTML= "<input name=\"CustomerName["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"25%\" id=\"CustomerName."+(rowCount)+"\" value='"+data[1]+"'>";
			    row.insertCell(2).innerHTML= "<input name=\"CustomerType["+(rowCount)+"]\" id=\"CustomerType."+(rowCount)+"\" readonly=\"readonly\"  size=\"14%\" class=\"Box\" value="+data[2]+">";
			    row.insertCell(3).innerHTML= "<input name=\"NoOfBills["+(rowCount)+"]\" id=\"NoOfBills."+(rowCount)+"\" readonly=\"readonly\"  style=\"text-align: right;\"  size=\"14%\" class=\"Box\"  value="+data[3]+">";
			    row.insertCell(4).innerHTML= "<input name=\"SalesAmt["+(rowCount)+"]\" id=\"SalesAmt."+(rowCount)+"\" readonly=\"readonly\"  style=\"text-align: right;\"  size=\"14%\" class=\"Box\"  value="+data[4]+">";
			   
					}
				
				  funApplyNumberValidation();
			}
			
			
			
			function funOnClckSKUWiseBtn( divId)
			{
				funShowTableGUI(divId)
				var settCode=$('#cmbSettlement').val();
				var locCode=$('#txtLocCode').val();
				
			
				var frmDte1=$('#txtFromDate').val();
				var toDte1=$('#txtToDate').val();
				var locCode1=$('#txtLocCode').val();
				var searchUrl=getContextPath()+"/loadSKUWiseDtl.html?settlementcode="+settCode+"&frmDte="+frmDte1+"&toDte="+toDte1+"&locCode="+locCode;
			
				$.ajax({
				        type: "GET",
				        url: searchUrl,
					    dataType: "json",
					    success: function(response)
					    {
					    	funSKUWiseProductDetail(response[0]);
					    	$("#txtTotValue").val(parseFloat(response[1]).toFixed(maxQuantityDecimalPlaceLimit));
					    	
					    	
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
			
			function funSKUWiseProductDetail(ProdDtl)
			{
				var userName="";
				$('#tblSKUProdDet tbody').empty();
				var rowCount = "";
			    var row ="";
				var amt='';
				for(var i=0;i<ProdDtl.length;i++)
				{
				 var data=ProdDtl[i];
				
			    var table = document.getElementById("tblSKUProdDet");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    row.insertCell(0).innerHTML= "<input name=\"SKUCode["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"20%\" id=\"SKUCode."+(rowCount)+"\" value='"+data[0]+"'>";		    
			    row.insertCell(1).innerHTML= "<input name=\"SKUName["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"25%\" id=\"SKUName."+(rowCount)+"\" value='"+data[1]+"'>";
			    row.insertCell(2).innerHTML= "<input name=\"SKUQty["+(rowCount)+"]\" id=\"SKUQty."+(rowCount)+"\"readonly=\"readonly\" style=\"text-align: right;\" size=\"18%\" class=\"Box\" value="+data[2]+">";
			    row.insertCell(3).innerHTML= "<input name=\"SKUAmount["+(rowCount)+"]\" id=\"SKUAmount."+(rowCount)+"\" readonly=\"readonly\"  style=\"text-align: right;\"  size=\"14%\" class=\"Box\"  value="+data[3]+">";
			    
			   
					}
				
				  funApplyNumberValidation();
			}
			
			
		
			function funOnClckCategoryWiseBtn( divId)
			{
				funShowTableGUI(divId)
				var settCode=$('#cmbSettlement').val();
				var locCode=$('#txtLocCode').val();
				
			
				var frmDte1=$('#txtFromDate').val();
				var toDte1=$('#txtToDate').val();
				var locCode1=$('#txtLocCode').val();
				var searchUrl=getContextPath()+"/loadCategoryWiseDtl.html?settlementcode="+settCode+"&frmDte="+frmDte1+"&toDte="+toDte1+"&locCode="+locCode;
			
				$.ajax({
				        type: "GET",
				        url: searchUrl,
					    dataType: "json",
					    success: function(response)
					    {
					    	funCategoryWiseProductDetail(response[0]);
					    	$("#txtTotValue").val(parseFloat(response[1]).toFixed(maxQuantityDecimalPlaceLimit));
					    	
					    	
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
			
			function funCategoryWiseProductDetail(ProdDtl)
			{
				var userName="";
				$('#tblCategoryProdDet tbody').empty();
				var rowCount = "";
			    var row ="";
				var amt='';
				for(var i=0;i<ProdDtl.length;i++)
				{
				 var data=ProdDtl[i];
				
			    var table = document.getElementById("tblCategoryProdDet");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    row.insertCell(0).innerHTML= "<input name=\"CategoryCode["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"20%\" id=\"CategoryCode."+(rowCount)+"\" value='"+data[0]+"'>";		    
			    row.insertCell(1).innerHTML= "<input name=\"CategoryName["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"25%\" id=\"CategoryName."+(rowCount)+"\" value='"+data[1]+"'>";
			    row.insertCell(2).innerHTML= "<input name=\"CategoryQty["+(rowCount)+"]\" id=\"CategoryQty."+(rowCount)+"\" readonly=\"readonly\" size=\"18%\" style=\"text-align: right;\" class=\"Box\" value="+data[2]+">";
			    row.insertCell(3).innerHTML= "<input name=\"CategoryAmount["+(rowCount)+"]\" id=\"CategoryAmount."+(rowCount)+"\" readonly=\"readonly\"  style=\"text-align: right;\"  size=\"14%\" class=\"Box\"  value="+data[3]+">";
			    
			   
					}
				
				  funApplyNumberValidation();
			}
			
		
			function funOnClckManufactureWiseBtn( divId)
			{
				funShowTableGUI(divId)
				var settCode=$('#cmbSettlement').val();
				var locCode=$('#txtLocCode').val();
				
			
				var frmDte1=$('#txtFromDate').val();
				var toDte1=$('#txtToDate').val();
				var locCode1=$('#txtLocCode').val();
				var searchUrl=getContextPath()+"/loadManufactureWiseDtl.html?settlementcode="+settCode+"&frmDte="+frmDte1+"&toDte="+toDte1+"&locCode="+locCode;
			
				$.ajax({
				        type: "GET",
				        url: searchUrl,
					    dataType: "json",
					    success: function(response)
					    {
					    	funManufactureWiseProductDetail(response[0]);
					    	$("#txtTotValue").val(parseFloat(response[1]).toFixed(maxQuantityDecimalPlaceLimit));
					    	
					    	
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
			
			function funManufactureWiseProductDetail(ProdDtl)
			{
				var userName="";
				$('#tblManufactureProdDet tbody').empty();
				var rowCount = "";
			    var row ="";
				var amt='';
				for(var i=0;i<ProdDtl.length;i++)
				{
				 var data=ProdDtl[i];
				
			    var table = document.getElementById("tblManufactureProdDet");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    row.insertCell(0).innerHTML= "<input name=\"ManufactureCode["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"20%\" id=\"ManufactureCode."+(rowCount)+"\" value='"+data[0]+"'>";		    
			    row.insertCell(1).innerHTML= "<input name=\"ManufactureName["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"25%\" id=\"ManufactureName."+(rowCount)+"\" value='"+data[1]+"'>";
			    row.insertCell(2).innerHTML= "<input name=\"ManufactureQty["+(rowCount)+"]\" id=\"ManufactureQty."+(rowCount)+"\" readonly=\"readonly\"  size=\"18%\" style=\"text-align: right;\" class=\"Box\" value="+data[2]+">";
			    row.insertCell(3).innerHTML= "<input name=\"ManufactureAmount["+(rowCount)+"]\" id=\"ManufactureAmount."+(rowCount)+"\" readonly=\"readonly\"  style=\"text-align: right;\"  size=\"14%\" class=\"Box\"  value="+data[3]+">";
			    
			   
					}
				
				  funApplyNumberValidation();
			}
			
			
			function funOnClckDepartmentWiseBtn( divId)
			{
				funShowTableGUI(divId)
				var settCode=$('#cmbSettlement').val();
				var locCode=$('#txtLocCode').val();
				if(locCode=="")
					{
					locCode="All";
					}
				
			
				var frmDte1=$('#txtFromDate').val();
				var toDte1=$('#txtToDate').val();
				var locCode1=$('#txtLocCode').val();
				var searchUrl=getContextPath()+"/loadDepartmentWiseDtl.html?settlementcode="+settCode+"&frmDte="+frmDte1+"&toDte="+toDte1+"&locCode="+locCode;
			
				$.ajax({
				        type: "GET",
				        url: searchUrl,
					    dataType: "json",
					    success: function(response)
					    {
					    	funDepartmentWiseProductDetail(response[0]);
					    	$("#txtTotValue").val(parseFloat(response[1]).toFixed(maxQuantityDecimalPlaceLimit));
					    	
					    	
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
			
			function funDepartmentWiseProductDetail(ProdDtl)
			{
				var userName="";
				$('#tblDepartmentProdDet tbody').empty();
				var rowCount = "";
			    var row ="";
				var amt='';
				for(var i=0;i<ProdDtl.length;i++)
				{
				 var data=ProdDtl[i];
				 
				
			    var table = document.getElementById("tblDepartmentProdDet");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    row.insertCell(0).innerHTML= "<input name=\"locationCode["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"20%\" id=\"locationCode."+(rowCount)+"\" value='"+data[0]+"'>";		    
			    row.insertCell(1).innerHTML= "<input name=\"locationName["+(rowCount)+"]\" readonly=\"readonly\" class=\"Box\" size=\"25%\" id=\"locationName."+(rowCount)+"\" value='"+data[1]+"'>";
			    row.insertCell(2).innerHTML= "<input name=\"locationQty["+(rowCount)+"]\" id=\"locationQty."+(rowCount)+"\" readonly=\"readonly\"  size=\"18%\" style=\"text-align: right;\" class=\"Box\" value="+data[2]+">";
			    row.insertCell(3).innerHTML= "<input name=\"locationAmount["+(rowCount)+"]\" id=\"locationAmount."+(rowCount)+"\" readonly=\"readonly\" style=\"text-align: right;\"  size=\"14%\" class=\"Box\"  value="+data[3]+">";
			    
			   
					}
				
				  funApplyNumberValidation();
			}
			
		 	</script>
</head>

<body>
	<div id="formHeading">
		<label>Invoice Flash</label>
	</div>
	<s:form name="Form" method="GET" action="">
		<br />

		<table class="transTable">
			<tr>
				<%-- <td width="10%">Property Code</td>
					<td width="20%">
						<s:select id="cmbProperty" name="propCode" path="" cssClass="longTextBox" cssStyle="width:100%" onchange="funChangeLocationCombo();">
			    			<s:options items="${listProperty}"/>
			    		</s:select>
					</td> --%>

				<td><label id="lblFromDate">From Date</label></td>
				<td><s:input id="txtFromDate" required="required" path=""
						name="fromDate" cssClass="calenderTextBox" /></td>

				<!-- 			      	<td><label>Customer Code</label></td> -->
				<%-- 									<td><s:input path="strCustCode" id="txtCustCode" --%>
				<%-- 											ondblclick="funHelp('custMaster')" cssClass="searchTextBox" /></td> --%>
				<!-- 									<td ><label id="lblCustomerName" -->
				<!-- 										class="namelabel">All Customer</label></td> -->
				<!-- 						 <td colspan="4"></td> -->



				<td><label id="lblToDate">To Date</label></td>
				<td colspan="3"><s:input id="txtToDate" name="toDate" path=""
						cssClass="calenderTextBox" /></td>
			</tr>
			<tr>
				<td><label>Location Code</label></td>
				<td><s:input type="text" id="txtLocCode" path="strLocCode"
						cssClass="searchTextBox" ondblclick="funHelp('locationmaster');" /></td>
				<td colspan="2"><label id="lblLocName"></label></td>
				<td width="100px"><label>Settlement</label>
				<td><s:select id="cmbSettlement" path="strSettlementCode"
						items="${settlementList}" cssClass="BoxW124px" /></td>
			</tr>
			
<!-- 			<tr> -->
<!-- 				<td><input id="btnExecute" type="button" class="form_button1" -->
<!-- 					value="EXECUTE" /></td> -->
<!-- 				<td><input id="btnExport" type="button" value="EXPORT" -->
<!-- 					class="form_button1" /></td> -->
<!-- 				<td colspan="5"></td> -->

<!-- 			</tr> -->


		</table>



		<!-- 			<dl id="Searchresult" style="width: 95%; margin-left: 26px; overflow:auto;"></dl> -->
		<!-- 		<div id="Pagination" class="pagination" style="width: 80%;margin-left: 26px;"> -->

		<!-- 		</div> -->
		<!-- 		<br/> -->
		<!-- 		<div id="divValueTotal"> -->
		<!-- 		<table id="tblTotalFlash" class="transTablex" style="width: 95%;font-size:11px;font-weight: bold;"> -->
		<!-- 		<tr style="margin-left: 28px"> -->
		<!-- 			<td id="labld26" width="70%" align="right">Total Value</td> -->
		<!-- 			<td id="tdSubTotValue" width="10%" align="right"> -->
		<!-- 			<input id="txtSubTotValue" style="width: 100%;text-align: right;" class="Box"></input></td> -->
		<!-- 			<td id="tdTaxTotValue" width="10%" align="right"> -->
		<!-- 			<input id="txtTaxTotValue" style="width: 100%;text-align: right;" class="Box"></input></td> -->
		<!-- 			<td id="tdTotValue" width="10%" align="right"> -->
		<!-- 			<input id="txtTotValue" style="width: 100%;text-align: right;" class="Box"></input></td> -->

		<!-- 			</tr> -->
		<!-- 		</table> -->
		<!-- 		</div> -->


<br/><br/>
		<div id="divBillWise" class="dynamicTableContainer"
			style="height: 400px;">
			<table style="width: 100%; border: #0F0; table-layout: fixed;"
				class="transTablex col15-center">
				<tr bgcolor="#72BEFC">
					<td width="8%">Invoice Code</td>
					<!--  COl1   -->
					<td width="8%">Date</td>
					<!--  COl2   -->

					<td width="5%">Customer Name</td>
					<!--  COl4   -->
					<td width="5%">Against</td>
					<!-- COl5   -->
					<td width="3%">Vehicle No</td>
					<!-- COl6   -->
					<!--  										<td width="5%">Excisable</td>  -->
					<!--  COl7   -->
					<td width="6%">SubTotal</td>
					<!--  COl8   -->
					<td width="5%">Tax Amount</td>
					<!--  COl9   -->
					<td width="5%">Grand Total</td>
					<!--COl10   -->

				</tr>
			</table>
			<div
				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 330px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 100%;">
				<table id="tblProdDet"
					style="width: 100%; border: #0F0; table-layout: fixed;"
					class="transTablex col15-center">
					<tbody>
					<col style="width: 7.5%">
					<!--  COl1   -->
					<col style="width: 7.5%">
					<!--  COl2   -->

					<col style="width: 4.5%">
					<!--  COl4   -->
					<col style="width: 4.5%">
					<!--COl5   -->
					<%-- 										<col style="width: 3%">  --%>
					<!--COl6   -->
					<col style="width: 3%">
					<!-- COl7   -->
					<col style="width: 5.5%">
					<!--  COl8   -->
					<col style="width: 4.5%">
					<!--  COl9   -->
					<col style="width: 4.5%">
					<!--  COl10  -->


					</tbody>

				</table>
			</div>

		</div>




		<div id="divTenderWise" class="dynamicTableContainer"
			style="height: 400px;">
			<table style="width: 100%; border: #0F0; table-layout: fixed;"
				class="transTablex col15-center">
				<tr bgcolor="#72BEFC">
					<td width="8%">Settlement Code</td>
					<!--  COl1   -->
					<td width="8%">Settlement Name</td>
					<!--  COl2   -->

					<td width="5%">Settlement Type</td>
					<!--  COl3   -->
					<td width="5%">Sales Amount</td>
					<!-- COl4   -->

				</tr>
			</table>
			<div
				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 330px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 100%;">
				<table id="tblTenderProdDet"
					style="width: 100%; border: #0F0; table-layout: fixed;"
					class="transTablex col15-center">
					<tbody>
					<col style="width: 7.5%">
					<!--  COl1   -->
					<col style="width: 7.5%">
					<!--  COl2   -->

					<col style="width: 4.5%">
					<!--  COl3   -->
					<col style="width: 4.5%">
					<!--  COl4   -->



					</tbody>

				</table>
			</div>

		</div>




		<div id="divOperatorWise" class="dynamicTableContainer"
			style="height: 400px;">
			<table style="width: 100%; border: #0F0; table-layout: fixed;"
				class="transTablex col15-center">
				<tr bgcolor="#72BEFC">
					<td width="8%">User Code</td>
					<!--  COl1   -->
					<td width="8%">User Name</td>
					<!--  COl2   -->

					<td width="5%">Sales Amount</td>
					<!-- COl3  -->

					<td width="5%">Discount Amt</td>
					<!--  COl4   -->
					<td width="5%">PaymentMode</td>
					<!--  COl5   -->
					<td width="5%">Operator total</td>
					<!--  COl6   -->

				</tr>
			</table>
			<div
				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 330px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 100%;">
				<table id="tblOpertorProdDet"
					style="width: 100%; border: #0F0; table-layout: fixed;"
					class="transTablex col15-center">
					<tbody>
					<col style="width: 7.5%">
					<!--  COl1   -->
					<col style="width: 7.5%">
					<!--  COl2   -->

					<col style="width: 4.5%">
					<!--  COl3   -->
					<col style="width: 4.5%">
					<!--  COl4   -->
					<col style="width: 4.5%">
					<!--  COl5   -->
					<col style="width: 4.5%">
					<!--  COl6   -->



					</tbody>

				</table>
			</div>

		</div>



	<div id="divCustomerWise" class="dynamicTableContainer"
			style="height: 400px;">
			<table style="width: 100%; border: #0F0; table-layout: fixed;"
				class="transTablex col15-center">
				<tr bgcolor="#72BEFC">
					<td width="8%">Customer Code</td>
					<!--  COl1   -->
					<td width="8%">Customer Name</td>
					<!--  COl2   -->
					<td width="5%">Customer Type</td>
					<!-- COl3  -->
					<td width="5%">No Of Bills</td>
					<!--  COl4   -->
					<td width="5%">Sales Amount</td>
					<!--  COl5   -->

				</tr>
			</table>
			<div
				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 330px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 100%;">
				<table id="tblCustomerProdDet"
					style="width: 100%; border: #0F0; table-layout: fixed;"
					class="transTablex col15-center">
					<tbody>
					<col style="width: 7.5%">
					<!--  COl1   -->
					<col style="width: 7.5%">
					<!--  COl2   -->
					<col style="width: 4.5%">
					<!--  COl3   -->
					<col style="width: 4.5%">
					<!--  COl4   -->
					<col style="width: 4.5%">
					<!--  COl5   -->
				
					</tbody>

				</table>
			</div>

		</div>


	<div id="divSKUWise" class="dynamicTableContainer"
			style="height: 400px;">
			<table style="width: 100%; border: #0F0; table-layout: fixed;"
				class="transTablex col15-center">
				<tr bgcolor="#72BEFC">
					<td width="8%">SKU Code</td>
					<!--  COl1   -->
					<td width="8%">SKU Name</td>
					<!--  COl2   -->
					<td width="5%">Quantity</td>
					<!-- COl3  -->
					<td width="5%">Sales Amount</td>
					<!--  COl4   -->
					

				</tr>
			</table>
			<div
				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 330px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 100%;">
				<table id="tblSKUProdDet"
					style="width: 100%; border: #0F0; table-layout: fixed;"
					class="transTablex col15-center">
					<tbody>
					<col style="width: 7.5%">
					<!--  COl1   -->
					<col style="width: 7.5%">
					<!--  COl2   -->
					<col style="width: 4.5%">
					<!--  COl3   -->
					<col style="width: 4.5%">
					<!--  COl4   -->
				
				
					</tbody>

				</table>
			</div>

		</div>


	<div id="divCategoryWise" class="dynamicTableContainer"
			style="height: 400px;">
			<table style="width: 100%; border: #0F0; table-layout: fixed;"
				class="transTablex col15-center">
				<tr bgcolor="#72BEFC">
					<td width="8%">Category Code</td>
					<!--  COl1   -->
					<td width="8%">Category Name</td>
					<!--  COl2   -->
					<td width="5%">Quantity</td>
					<!-- COl3  -->
					<td width="5%">Sales Amount</td>
					<!--  COl4   -->
					

				</tr>
			</table>
			<div
				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 330px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 100%;">
				<table id="tblCategoryProdDet"
					style="width: 100%; border: #0F0; table-layout: fixed;"
					class="transTablex col15-center">
					<tbody>
					<col style="width: 7.5%">
					<!--  COl1   -->
					<col style="width: 7.5%">
					<!--  COl2   -->
					<col style="width: 4.5%">
					<!--  COl3   -->
					<col style="width: 4.5%">
					<!--  COl4   -->
				
				
					</tbody>

				</table>
			</div>

		</div>


	<div id="divManufactureWise" class="dynamicTableContainer"
			style="height: 400px;">
			<table style="width: 100%; border: #0F0; table-layout: fixed;"
				class="transTablex col15-center">
				<tr bgcolor="#72BEFC">
					<td width="8%">Category Code</td>
					<!--  COl1   -->
					<td width="8%">Category Name</td>
					<!--  COl2   -->
					<td width="5%">Quantity</td>
					<!-- COl3  -->
					<td width="5%">Sales Amount</td>
					<!--  COl4   -->
					

				</tr>
			</table>
			<div
				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 330px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 100%;">
				<table id="tblManufactureProdDet"
					style="width: 100%; border: #0F0; table-layout: fixed;"
					class="transTablex col15-center">
					<tbody>
					<col style="width: 7.5%">
					<!--  COl1   -->
					<col style="width: 7.5%">
					<!--  COl2   -->
					<col style="width: 4.5%">
					<!--  COl3   -->
					<col style="width: 4.5%">
					<!--  COl4   -->
				
				
					</tbody>

				</table>
			</div>

		</div>
		
		<div id="divDepartmentWise" class="dynamicTableContainer"
			style="height: 400px;">
			<table style="width: 100%; border: #0F0; table-layout: fixed;"
				class="transTablex col15-center">
				<tr bgcolor="#72BEFC">
					<td width="8%">Department Code</td>
					<!--  COl1   -->
					<td width="8%">Department Name</td>
					<!--  COl2   -->
					<td width="5%">Quantity</td>
					<!-- COl3  -->
					<td width="5%">Sales Amount</td>
					<!--  COl4   -->
					

				</tr>
			</table>
			<div
				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 330px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 100%;">
				<table id="tblDepartmentProdDet"
					style="width: 100%; border: #0F0; table-layout: fixed;"
					class="transTablex col15-center">
					<tbody>
					<col style="width: 7.5%">
					<!--  COl1   -->
					<col style="width: 7.5%">
					<!--  COl2   -->
					<col style="width: 4.5%">
					<!--  COl3   -->
					<col style="width: 4.5%">
					<!--  COl4   -->
				
				
					</tbody>

				</table>
			</div>

		</div>
		
		
		<div id="divValueTotal"
			style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 50px; margin: auto; overflow-x: hidden; overflow-y: hidden; width: 100%;">
			<table id="tblTotalFlash" class="transTablex"
				style="width: 100%; font-size: 11px; font-weight: bold;">
				<tr style="margin-left: 28px">
					<td id="labld26" width="70%" align="right">Total Value</td>
					<td id="tdSubTotValue" width="10%" align="right"><input
						id="txtSubTotValue" style="width: 100%; text-align: right;"
						class="Box"></input></td>
					<td id="tdTaxTotValue" width="10%" align="right"><input
						id="txtTaxTotValue" style="width: 100%; text-align: right;"
						class="Box"></input></td>
					<td id="tdTotValue" width="10%" align="right"><input
						id="txtTotValue" style="width: 100%; text-align: right;"
						class="Box"></input></td>

				</tr>
			</table>
		</div>


		<div
			style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 50px; margin: auto;  width: 100%;">

			<td><input id="btnBillWise" type="button" class="form_button"
				value="Bill Wise" onclick="funOnClckBillWiseBtn('divBillWise')" /></td>
			<td><input id="btnTenderWise" type="button" class="form_button"
				value="Tender Wise"
				onclick="funOnClckTenderWiseBtn('divTenderWise')" /></td>
			<td><input id="btnOperatorWise" type="button"
				class="form_button" value="Operator Wise"
				onclick="funOnClckoperatorWiseBtn('divOperatorWise')" /></td>
			<td><input id="btnCustomerWise" type="button"
				class="form_button" value="Customer Wise"  onclick="funOnClckCuctomerWiseBtn('divCustomerWise')" /></td>
			<td><input id="btnSkuWise" type="button" class="form_button"
				value="SKU Wise"  onclick="funOnClckSKUWiseBtn('divSKUWise')" /></td>
			<td><input id="btnCategoryWise" type="button"
				class="form_button" value="Category Wise"  onclick="funOnClckCategoryWiseBtn('divCategoryWise')"  /></td>
			<td><input id="btnManufacturerWise" type="button"
				class="form_button" value="Manufacturer Wise" onclick="funOnClckManufactureWiseBtn('divManufactureWise')"  /></td>
			<td><input id="btnAttributeWise" type="button"
				class="form_button" value="Attribute Wise" /></td>
			<td><input id="btnDepartmentWise" type="button" 
				 class="form_button" value="Department Wise" onclick="funOnClckDepartmentWiseBtn('divDepartmentWise')"   /></td>
		</div>
		<!-- 		<table id="tblButton" class="transTablex" style="width: 100%;font-size:11px;font-weight: bold;"> -->
		<!-- 		<tr style="margin-left: 28px"> -->
		<!-- 			<td id="labld26" width="70%" align="right">Total Value</td> -->
		<!-- 			<td id="tdSubTotValue" width="10%" align="right"> -->
		<!-- 			<input id="txtSubTotValue" style="width: 100%;text-align: right;" class="Box"></input></td> -->
		<!-- 			<td id="tdTaxTotValue" width="10%" align="right"> -->
		<!-- 			<input id="txtTaxTotValue" style="width: 100%;text-align: right;" class="Box"></input></td> -->
		<!-- 			<td id="tdTotValue" width="10%" align="right"> -->
		<!-- 			<input id="txtTotValue" style="width: 100%;text-align: right;" class="Box"></input></td> -->

		<!-- 			</tr> -->
		<!-- 		</table> -->

<div id="wait" style="display:none;width:60px;height:60px;border:0px solid black;position:absolute;top:60%;left:55%;padding:2px;">
				<img src="../${pageContext.request.contextPath}/resources/images/ajax-loader-light.gif" width="60px" height="60px" />
			</div>
	</s:form>
</body>
</html>