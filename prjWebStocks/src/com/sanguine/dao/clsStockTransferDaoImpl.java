package com.sanguine.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sanguine.model.clsStkTransferDtlModel;
import com.sanguine.model.clsStkTransferHdModel;

@Repository("clsStockTransferDao")
public class clsStockTransferDaoImpl implements clsStockTransferDao
{
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void funAddUpdate(clsStkTransferHdModel object)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(object);
	}
	
	@Override
	public void funAddUpdateDtl(clsStkTransferDtlModel object)
	{
		sessionFactory.getCurrentSession().save(object);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List funGetList(String clientCode)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from clsStkTransferDtlModel a,clsProductMasterModel b "
				+ "where a.strProdCode=b.strProdCode and a.strClientCode= :clientCode and b.strClientCode= :clientCode "
				+ "order by a.strSTCode");
		query.setParameter("clientCode", clientCode);
		List list = query.list();
		return list;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List funGetObject(String STCode,String clientCode)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from clsStkTransferHdModel a,clsLocationMasterModel b "
				+ "where a.strSTCode= :stCode and a.strFromLocCode=b.strLocCode and a.strClientCode= :clientCode and b.strClientCode= :clientCode");
		query.setParameter("stCode", STCode);
		query.setParameter("clientCode", clientCode);
		return query.list();
	}
	
	@SuppressWarnings({"rawtypes" })
	@Override
	public List funGetDtlList(String STCode,String clientCode) 
	{ 
		Query query = sessionFactory.getCurrentSession().createQuery("from clsStkTransferDtlModel a,clsProductMasterModel b "
				+ "where a.strSTCode = :stCode and a.strProdCode=b.strProdCode and a.strClientCode= :clientCode and b.strClientCode= :clientCode");
		query.setParameter("stCode", STCode);
		query.setParameter("clientCode", clientCode);
		List list = query.list();
		return list;
	}
		
	@SuppressWarnings("unused")
	public void funDeleteDtl(String SACode,String clientCode)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("delete clsStkTransferDtlModel where strSTCode = :stCode and strClientCode= :clientCode");
		query.setParameter("stCode", SACode);
		query.setParameter("clientCode", clientCode);
		int result = query.executeUpdate();
	}
	
	@SuppressWarnings("finally")
	public List funGetProdAgainstActualProduction(String strOPCode,String clientCode)
	{
		List list=null;
		try
		{
		
		String sql=" select c.strProdCode,c.dblQtyProd,c.dblWeight,c.dblPrice ,b.strWOCode "
				+ " from tblworkorderhd a ,tblproductionhd b ,tblproductiondtl c "
				+ " where a.strSOCode='"+strOPCode+"' "
				+ " and a.strWOCode=b.strWOCode "
				+ " and b.strPDCode=c.strPDCode "
				+ " and a.strClientCode='"+clientCode+"' "
				+ " and b.strClientCode='"+clientCode+"' "
				+ " and c.strClientCode='"+clientCode+"'  ";
		
		list = sessionFactory.getCurrentSession().createSQLQuery(sql).list();
		
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return list;
		}
		
	}
	
	
	public clsStkTransferHdModel funGetModel(String strPDCode,String clientCode)
	{
		clsStkTransferHdModel objSTModel = new clsStkTransferHdModel();
		Query query = sessionFactory.getCurrentSession().createQuery("from clsStkTransferHdModel a "
				+ "where a.strNarration like  :pdCode and a.strClientCode= :clientCode ");
		query.setParameter("pdCode", "%"+strPDCode+"%");
		query.setParameter("clientCode", clientCode);
		List list = query.list();
		if(!list.isEmpty())
		{
			objSTModel = (clsStkTransferHdModel) list.get(0);
		}
		return objSTModel;
	}
	
	
	
}
