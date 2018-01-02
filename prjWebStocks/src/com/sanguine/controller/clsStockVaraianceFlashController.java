package com.sanguine.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.sanguine.model.clsStkAdjustmentHdModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsStkAdjustmentService;
import com.sanguine.util.clsReportBean;

@Controller
public class clsStockVaraianceFlashController  extends AbstractExcelView{

	@Autowired
	clsGlobalFunctionsService objGlobalFunctionsService;
	
	@Autowired
	clsStkAdjustmentService objStkAdjustmentService;
	private clsGlobalFunctions objGlobal=null;
	/**
	 * Open Stock variance Flash
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/frmStkVarianceFlash",method=RequestMethod.GET)
	private ModelAndView funOpenStkVarianceFlash(HttpServletRequest req)
	{
		return new ModelAndView("frmStkVarianceFlash","command",new clsReportBean());
	}
	/**
	 * Load Stock Variance Flash Data
	 * @param param1
	 * @param req
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/loadStkVarianceFlashData",method=RequestMethod.GET)
	private @ResponseBody List funGetStkVarianceFlashData(@RequestParam(value="param1") String param1,HttpServletRequest req)
	{
		objGlobal=new clsGlobalFunctions();
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String[] spParam1=param1.split(",");
		String strLocCode=spParam1[0];
		String fromDate=objGlobal.funGetDate("yyyy-MM-dd",spParam1[1]);
		String toDate=objGlobal.funGetDate("yyyy-MM-dd",spParam1[2]);
		
		
		String sql="select b.strProdCode,c.strProdName,sum(b.dblCStock),sum(b.dblPStock),sum(b.dblVariance),c.dblCostRM,(c.dblCostRM *sum(b.dblVariance)) as value  "
				+ " from clsStkPostingHdModel a,clsStkPostingDtlModel b,clsProductMasterModel c,clsStkAdjustmentHdModel d "
				+ " where a.strPSCode=b.strPSCode and b.strProdCode=c.strProdCode and a.strSACode=d.strSACode  "
				+ " and a.dtPSDate between '"+fromDate+"' and '"+toDate+"' "
				+ " and a.strClientCode='"+clientCode+"' and  b.strClientCode='"+clientCode+"' "
				+ " and c.strClientCode='"+clientCode+"'";
		if(strLocCode.trim().length()>0)
		{
			sql=sql+"and a.strLocCode='"+strLocCode+"' ";
		}
		sql=sql+"group by b.strProdCode";
		List list=objGlobalFunctionsService.funGetList(sql, "hql");
		return list;
	}

		/**
		 * Stock variance Excel Export  
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		@RequestMapping(value = "/ExportExcelStkVariance", method = RequestMethod.GET)	 
	    protected void buildExcelDocument(Map<String, Object> model,
	            HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
	            throws Exception {
			objGlobal=new clsGlobalFunctions();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader( "Content-Disposition", "inline;filename=stkVarianceReport.xls");
			
			String clientCode=request.getSession().getAttribute("clientCode").toString();
			String param1=request.getParameter("param1");
			String[] spParam1=param1.split(",");
			String strLocCode=spParam1[0];
			String fromDate=objGlobal.funGetDate("yyyy-MM-dd",spParam1[1]);
			String toDate=objGlobal.funGetDate("yyyy-MM-dd",spParam1[2]);
			
			double value=0;
				
			String sql="select b.strProdCode,c.strProdName,sum(b.dblCStock),sum(b.dblPStock),sum(b.dblVariance),c.dblCostRM,(c.dblCostRM *sum(b.dblVariance)) as value "
					+ " from clsStkPostingHdModel a,clsStkPostingDtlModel b,clsProductMasterModel c ,clsStkAdjustmentHdModel d "
					+ " where a.strPSCode=b.strPSCode and b.strProdCode=c.strProdCode  and a.strSACode=d.strSACode  "
					+ " and a.dtPSDate between '"+fromDate+"' and '"+toDate+"' "
					+ " and a.strClientCode='"+clientCode+"' and  b.strClientCode='"+clientCode+"' "
					+ " and c.strClientCode='"+clientCode+"'";
			if(strLocCode.trim().length()>0)
			{
				sql=sql+"and a.strLocCode='"+strLocCode+"' ";
			}
			sql=sql+"group by b.strProdCode";
			List list=objGlobalFunctionsService.funGetList(sql, "hql");
			
			List listStockFlashModel=new ArrayList();
			
			for(int cnt=0;cnt<list.size();cnt++)
			{
				Object[] arrObj=(Object[])list.get(cnt);
				List DataList=new ArrayList<>();
				DataList.add(arrObj[0].toString());
				DataList.add(arrObj[1].toString());
				DataList.add(Double.parseDouble(arrObj[2].toString()));
				DataList.add(Double.parseDouble(arrObj[3].toString()));
				DataList.add(Double.parseDouble(arrObj[4].toString()));
				DataList.add(Double.parseDouble(arrObj[5].toString()));
				DataList.add(Double.parseDouble(arrObj[6].toString()));
				value=value+Double.parseDouble(arrObj[6].toString());
				listStockFlashModel.add(DataList);
			}
	        // create a new Excel sheet
	        HSSFSheet sheet = workbook.createSheet("Sheet");
	        sheet.setDefaultColumnWidth(20);
	         
	        // create style for header cells
	        CellStyle style = workbook.createCellStyle();
	        Font font = workbook.createFont();
	        font.setFontName("Arial");
	        style.setFillForegroundColor(HSSFColor.BLUE.index);
	        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        font.setColor(HSSFColor.WHITE.index);
	        style.setFont(font);
	        
	        HSSFRow header = sheet.createRow(0);
		    header.createCell(0).setCellValue("Product Code");
		    header.getCell(0).setCellStyle(style);
		    header.createCell(1).setCellValue("Product Name");
		    header.getCell(1).setCellStyle(style);
		    header.createCell(2).setCellValue("Computer Stk");
		    header.getCell(2).setCellStyle(style);
		    header.createCell(3).setCellValue("Phy Stk");
		    header.getCell(3).setCellStyle(style);
		    header.createCell(4).setCellValue("Variance");
		    header.getCell(4).setCellStyle(style);
		    header.createCell(5).setCellValue("Unit Price");
		    header.getCell(5).setCellStyle(style);
		    header.createCell(6).setCellValue("Value");
		    header.getCell(6).setCellStyle(style);
		    
	        // create style for Data cells
	        CellStyle Datastyle = workbook.createCellStyle();
	        Datastyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
	        // create header row
	       
		    
	        // create data rows
	        int ColrowCount = 1  ;
	        for(int rowCount=0;rowCount<listStockFlashModel.size();rowCount++)
				{
	            	HSSFRow aRow = sheet.createRow(ColrowCount++);
					List arrObj=(List) listStockFlashModel.get(rowCount);
					for(int Count=0;Count<arrObj.size();Count++)
					{
						if(arrObj.get(Count).toString().length()>0)
						{
							if(isNumeric(arrObj.get(Count).toString()))
							{
								aRow.createCell(Count).setCellValue(Double.parseDouble(arrObj.get(Count).toString()));
								aRow.getCell(Count).setCellStyle(Datastyle);
							}
							else
							{
								aRow.createCell(Count).setCellValue(arrObj.get(Count).toString());
							}
						}
						else
						{
							aRow.createCell(Count).setCellValue("");
						}
					}					
	           
	        }
	       
	        // create style for Footer cells
	        CellStyle sellStyle = workbook.createCellStyle();
	        Font cellfont = workbook.createFont();
	        cellfont.setFontName("Arial");
	        cellfont.setColor(HSSFColor.BLACK.index);
	        cellfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        sellStyle.setFont(cellfont);
	        HSSFRow aRow1 = sheet.createRow(ColrowCount+1);
	        aRow1.createCell(5).setCellValue("Total Variance Value");
	        aRow1.getCell(5).setCellStyle(sellStyle);
	        aRow1.createCell(6).setCellValue(value);
	        aRow1.getCell(6).setCellStyle(sellStyle);
	        
		    workbook.write(response.getOutputStream());
	       
	}   
   /**
	* Checking Value is Number
	* @param str
	* @return
	*/
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
}