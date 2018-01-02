package com.sanguine.webbooks.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sanguine.webbooks.model.clsChargeProcessingHDModel;

@Repository("clsChargeProcessingDao")
public class clsChargeProcessingDaoImpl implements clsChargeProcessingDao{

	@Autowired
	private SessionFactory webBooksSessionFactory;

	@Override
	public void funAddUpdateChargeProcessing(clsChargeProcessingHDModel objMaster){
		webBooksSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	}

	@Override
	public clsChargeProcessingHDModel funGetChargeProcessing(String docCode,String clientCode){
		return new clsChargeProcessingHDModel() ;
	}

	@Override
	public void funClearTblChargeGenerationTemp(String strMemberCode) 
	{
		Query query;
		if(strMemberCode.equalsIgnoreCase("All"))
		{
			query=webBooksSessionFactory.getCurrentSession().createSQLQuery("TRUNCATE `tblChargeGenerationTemp` ");			
		}
		else
		{
			query=webBooksSessionFactory.getCurrentSession().createSQLQuery("delete from tblChargeGenerationTemp where strMemberCode='"+strMemberCode+"' ");
		}
		
		int affectedRows=query.executeUpdate();
		System.out.println("affectedRows="+affectedRows);
	}

	@Override
	public List funGetAllMembers(String clientCode,String propertyCode) 
	{	
		List<?> list=null;
		list=webBooksSessionFactory.getCurrentSession().createSQLQuery("select strDebtorCode,CONCAT_WS(' ',strPrefix,strFirstName,strMiddleName,strLastName) as strFullName from tblsundarydebtormaster ").list();
		return list;
	}

	@Override
	public List funCalculateOutstanding(String accountCode, String DrDate,String CrDate, String memberCode) 
	{	
				
		StringBuilder sqlJVDrAmt=new StringBuilder();
		StringBuilder sqlJVCrAmt=new StringBuilder();
		StringBuilder sqlReceiptDrAmt=new StringBuilder();
		StringBuilder sqlReceiptCrAmt=new StringBuilder();
		StringBuilder sqlPaymentDrAmt=new StringBuilder();
		StringBuilder sqlPaymentCrAmt=new StringBuilder();
		
		//JV debit
		sqlJVDrAmt.append("select a.strDebtorCode,a.strDebtorName,a.strCrDr,ifnull(sum(a.dblAmt),'0.00') as debitAmount	"
						 +"from tbljvdebtordtl a ,tbljvhd b	"
						 +"where a.strVouchNo=b.strVouchNo 	"
						 +"and ( date(b.dteVouchDate) <= '"+DrDate+"' and a.strCrDr='Dr' )	"
						 );
		//jv credit
		sqlJVCrAmt.append("select a.strDebtorCode,a.strDebtorName,a.strCrDr,ifnull(sum(a.dblAmt),'0.00') as creditAmount	"		
						 +"from tbljvdebtordtl a ,tbljvhd b "
						 +"where a.strVouchNo=b.strVouchNo "
						 +"and ( (date(b.dteVouchDate) between DATE_ADD('"+DrDate+"', INTERVAL 1 DAY) AND '"+CrDate+"') AND  a.strCrDr='Cr' ) " 
						 );
		//receipt debit
		sqlReceiptDrAmt.append("select a.strDebtorCode,a.strDebtorName,a.strCrDr,ifnull(sum(a.dblAmt),'0.00') as debitAmount	"
						 +"from tblreceiptdebtordtl a ,tbljvhd b	"
						 +"where a.strVouchNo=b.strVouchNo 	"
						 +"and ( date(b.dteVouchDate) <= '"+DrDate+"' and a.strCrDr='Dr' )	" 			
						 );
		//receipt credit
		sqlReceiptCrAmt.append("select a.strDebtorCode,a.strDebtorName,a.strCrDr,ifnull(sum(a.dblAmt),'0.00') as creditAmount	"		
						 +"from tblreceiptdebtordtl a ,tbljvhd b "
						 +"where a.strVouchNo=b.strVouchNo "
						 +"and ( (date(b.dteVouchDate) between DATE_ADD('"+DrDate+"', INTERVAL 1 DAY) AND '"+CrDate+"') AND  a.strCrDr='Cr' ) " 
						 );
		//payment debit
		sqlPaymentDrAmt.append("select a.strDebtorCode,a.strDebtorName,a.strCrDr,ifnull(sum(a.dblAmt),'0.00') as debitAmount	"
						 +"from tblpaymentdebtordtl a ,tbljvhd b	"
						 +"where a.strVouchNo=b.strVouchNo 	"
						 +"and ( date(b.dteVouchDate) <= '"+DrDate+"' and a.strCrDr='Dr' )	" 			
						 );
		//payment credit
		sqlPaymentCrAmt.append("select a.strDebtorCode,a.strDebtorName,a.strCrDr,ifnull(sum(a.dblAmt),'0.00') as creditAmount	"		
						 +"from tblpaymentdebtordtl a ,tbljvhd b "
						 +"where a.strVouchNo=b.strVouchNo "
						 +"and ( (date(b.dteVouchDate) between DATE_ADD('"+DrDate+"', INTERVAL 1 DAY) AND '"+CrDate+"') AND  a.strCrDr='Cr' ) " 
						 );
		
		//filter member  code
		if(!memberCode.equalsIgnoreCase("All"))
		{
			sqlJVDrAmt.append("and a.strDebtorCode='"+memberCode+"' ");
			sqlJVCrAmt.append("and a.strDebtorCode='"+memberCode+"' ");			
			sqlReceiptDrAmt.append("and a.strDebtorCode='"+memberCode+"' ");
			sqlReceiptCrAmt.append("and a.strDebtorCode='"+memberCode+"' ");			
			sqlPaymentDrAmt.append("and a.strDebtorCode='"+memberCode+"' ");
			sqlPaymentCrAmt.append("and a.strDebtorCode='"+memberCode+"' ");
			
		}
		
		//grouping
		sqlJVDrAmt.append("group by a.strDebtorCode ");
		sqlJVCrAmt.append("group by a.strDebtorCode ");		
		sqlReceiptDrAmt.append("group by a.strDebtorCode ");
		sqlReceiptCrAmt.append("group by a.strDebtorCode ");		
		sqlPaymentDrAmt.append("group by a.strDebtorCode ");
		sqlPaymentCrAmt.append("group by a.strDebtorCode ");
		
		//ordering
		sqlJVDrAmt.append("order by a.strDebtorCode ");
		sqlJVCrAmt.append("order by a.strDebtorCode ");		
		sqlReceiptDrAmt.append("order by a.strDebtorCode ");
		sqlReceiptCrAmt.append("order by a.strDebtorCode ");		
		sqlPaymentDrAmt.append("order by a.strDebtorCode ");
		sqlPaymentCrAmt.append("order by a.strDebtorCode ");							
		
		List listJVDrBal=webBooksSessionFactory.getCurrentSession().createSQLQuery(sqlJVDrAmt.toString()).list();
		List listJVCrBal=webBooksSessionFactory.getCurrentSession().createSQLQuery(sqlJVCrAmt.toString()).list();
		List listReceiptDrBal=webBooksSessionFactory.getCurrentSession().createSQLQuery(sqlReceiptDrAmt.toString()).list();
		List listReceiptCrBal=webBooksSessionFactory.getCurrentSession().createSQLQuery(sqlReceiptCrAmt.toString()).list();
		List listPaymentDrBal=webBooksSessionFactory.getCurrentSession().createSQLQuery(sqlPaymentDrAmt.toString()).list();
		List listPaymentCrBal=webBooksSessionFactory.getCurrentSession().createSQLQuery(sqlPaymentCrAmt.toString()).list();
		
		List<Map<String,Double>> listMemberOutstanding=new ArrayList<Map<String,Double>>();
		
		Map<String,Double>mapMemberDrAmount=new HashMap<String, Double>();
		Map<String,Double>mapMemberCrAmount=new HashMap<String, Double>();
		
		//JV Dr
		for(int i=0;i<listJVDrBal.size();i++)
		{
			Object[]objDr=(Object[])listJVDrBal.get(i);
			String memCode=objDr[0].toString();
			String drAmt=objDr[3].toString();
			
			if(mapMemberDrAmount.containsKey(memCode))
			{
				double drAmt1=mapMemberDrAmount.get(memCode);
				double drAmt2=Double.parseDouble(drAmt);
				
				mapMemberDrAmount.remove(memCode);
				mapMemberDrAmount.put(memCode,(drAmt1+drAmt2));
			}
			else
			{
				mapMemberDrAmount.put(memCode, Double.parseDouble(drAmt));
			}
		}
		//JV Cr
		for(int i=0;i<listJVCrBal.size();i++)
		{
			Object[]objCr=(Object[])listJVCrBal.get(i);
			String memCode=objCr[0].toString();
			String crAmt=objCr[3].toString();
			
			if(mapMemberCrAmount.containsKey(memCode))
			{
				double crAmt1=mapMemberCrAmount.get(memCode);
				double crAmt2=Double.parseDouble(crAmt);
				
				mapMemberCrAmount.remove(memCode);
				mapMemberCrAmount.put(memCode,(crAmt1+crAmt2));
			}
			else
			{
				mapMemberCrAmount.put(memCode, Double.parseDouble(crAmt));
			}
		}
		
		//Receipt Dr
		for(int i=0;i<listReceiptDrBal.size();i++)
		{
			Object[]objDr=(Object[])listReceiptDrBal.get(i);
			String memCode=objDr[0].toString();
			String drAmt=objDr[3].toString();
			
			if(mapMemberDrAmount.containsKey(memCode))
			{
				double drAmt1=mapMemberDrAmount.get(memCode);
				double drAmt2=Double.parseDouble(drAmt);
				
				mapMemberDrAmount.remove(memCode);
				mapMemberDrAmount.put(memCode,(drAmt1+drAmt2));
			}
			else
			{
				mapMemberDrAmount.put(memCode, Double.parseDouble(drAmt));
			}
		}
		//Receipt Cr
		for(int i=0;i<listReceiptCrBal.size();i++)
		{
			Object[]objCr=(Object[])listReceiptCrBal.get(i);
			String memCode=objCr[0].toString();
			String crAmt=objCr[3].toString();
			
			if(mapMemberCrAmount.containsKey(memCode))
			{
				double crAmt1=mapMemberCrAmount.get(memCode);
				double crAmt2=Double.parseDouble(crAmt);
				
				mapMemberCrAmount.remove(memCode);
				mapMemberCrAmount.put(memCode,(crAmt1+crAmt2));
			}
			else
			{
				mapMemberCrAmount.put(memCode, Double.parseDouble(crAmt));
			}
		}
		//Payment Dr
		for(int i=0;i<listPaymentDrBal.size();i++)
		{
			Object[]objDr=(Object[])listPaymentDrBal.get(i);
			String memCode=objDr[0].toString();
			String drAmt=objDr[3].toString();
			
			if(mapMemberDrAmount.containsKey(memCode))
			{
				double drAmt1=mapMemberDrAmount.get(memCode);
				double drAmt2=Double.parseDouble(drAmt);
				
				mapMemberDrAmount.remove(memCode);
				mapMemberDrAmount.put(memCode,(drAmt1+drAmt2));
			}
			else
			{
				mapMemberDrAmount.put(memCode, Double.parseDouble(drAmt));
			}
		}
		//Payment Cr
		for(int i=0;i<listPaymentCrBal.size();i++)
		{
			Object[]objCr=(Object[])listPaymentCrBal.get(i);
			String memCode=objCr[0].toString();
			String crAmt=objCr[3].toString();
			
			if(mapMemberCrAmount.containsKey(memCode))
			{
				double crAmt1=mapMemberCrAmount.get(memCode);
				double crAmt2=Double.parseDouble(crAmt);
				
				mapMemberCrAmount.remove(memCode);
				mapMemberCrAmount.put(memCode,(crAmt1+crAmt2));
			}
			else
			{
				mapMemberCrAmount.put(memCode, Double.parseDouble(crAmt));
			}
		}		
					
		
		Iterator<Entry<String, Double>>drIt=mapMemberDrAmount.entrySet().iterator();
		Iterator<Entry<String, Double>>crIt=mapMemberCrAmount.entrySet().iterator();
		
		if(drIt.hasNext())
		{
			while(drIt.hasNext())
			{
				Entry<String, Double>drEntry=drIt.next();
				String code=drEntry.getKey();
				double drAmt=drEntry.getValue();
				double crAmt=0.00;				
				if(mapMemberCrAmount.get(code)!=null)
				{
					crAmt=mapMemberCrAmount.get(code);
				}
				double outstanding=drAmt-crAmt;
				
				Map<String,Double>memOutstanding=new HashMap<String,Double>();
				memOutstanding.put(code, outstanding);
				
				listMemberOutstanding.add(memOutstanding);
			}
		}
		else if(crIt.hasNext())
		{
			while(crIt.hasNext())
			{
				Entry<String, Double>crEntry=crIt.next();
				String code=crEntry.getKey();
				double crAmt=crEntry.getValue();
				double drAmt=0.00;				
				if(mapMemberDrAmount.get(code)!=null)
				{
					drAmt=mapMemberDrAmount.get(code);
				}
				double outstanding=drAmt-crAmt;
				
				Map<String,Double>memOutstanding=new HashMap<String,Double>();
				memOutstanding.put(code, outstanding);
				
				listMemberOutstanding.add(memOutstanding);
			}
		}
			
		return listMemberOutstanding;
	}

	@Override
	public void funUpdateMemberOutstanding(String memberCode,double dblOutstanding, String clientCode, String propertyCode) 
	{	
		Query sqlUpdateOutstanding=webBooksSessionFactory.getCurrentSession().createSQLQuery("update tblsundarydebtormaster a set a.dblOutstanding='"+dblOutstanding+"' "
																 +"where a.strDebtorCode='"+memberCode+"' and a.strClientCode='"+clientCode+"' and a.strPropertyCode='"+propertyCode+"' ");
		int affectedRows=sqlUpdateOutstanding.executeUpdate();
	}

	@Override
	public List funIsChargeApplicable(String memberCode,String chargeCode, String clientCode,String propertyCode) 
	{		
		
		Query sqlChargeCondition=webBooksSessionFactory.getCurrentSession().createSQLQuery("select strSql from tblchargemaster where strChargeCode='"+chargeCode+"' and strClientCode='"+clientCode+"' ");
		List listCondition=sqlChargeCondition.list();
		
		String sqlCondition=listCondition.get(0).toString();
			
		Query sqlIsChargeApplicable=webBooksSessionFactory.getCurrentSession().createSQLQuery("select  Debtor_Code,Member_Full_Name,Outstanding  from dbwebmms.vwdebtormemberdtl "
								+"where  "+sqlCondition+"  "
								+"and Customer_Code ='"+memberCode+"' and Client_Code='"+clientCode+"' "
								+"and Property_Code='"+propertyCode+"' "
								+"");
		
		return sqlIsChargeApplicable.list();
	}
	
	
}
