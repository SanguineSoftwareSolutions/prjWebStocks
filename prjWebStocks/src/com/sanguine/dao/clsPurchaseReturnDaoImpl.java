package com.sanguine.dao;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sanguine.model.clsPurchaseReturnDtlModel;
import com.sanguine.model.clsPurchaseReturnHdModel;
import com.sanguine.model.clsPurchaseReturnHdModel_ID;
import com.sanguine.model.clsSupplierMasterModel;
@Repository("clsPurchaseReturnDao")
public class clsPurchaseReturnDaoImpl implements clsPurchaseReturnDao{
	@Autowired
	private SessionFactory sessionFactory;
	@SuppressWarnings("finally")
	@Override
	public long funGetLastNo(String tableName, String masterName,
			String columnName) {
		long lastNo=0;
		try
		{
			@SuppressWarnings("rawtypes")
			List listLastNo=sessionFactory.getCurrentSession().createSQLQuery("select max("+columnName+") from "+tableName).list();						
			if(listLastNo.size()>1)
			{
				lastNo=((BigInteger)listLastNo.get(0)).longValue();
			}
			lastNo++;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return lastNo;
		}
	}

	@Override
	public void funAddPRHd(clsPurchaseReturnHdModel PRHd) {
		sessionFactory.getCurrentSession().saveOrUpdate(PRHd);
		
	}

	@Override
	public void funAddUpdatePRDtl(clsPurchaseReturnDtlModel PRDtl) {
		sessionFactory.getCurrentSession().saveOrUpdate(PRDtl);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<clsPurchaseReturnHdModel> funGetList() {
		return (List<clsPurchaseReturnHdModel>) sessionFactory.getCurrentSession().createCriteria(clsPurchaseReturnHdModel.class).list();
	}

	@Override
	public clsPurchaseReturnHdModel funGetObject(String code,String strClientCode) {
		return (clsPurchaseReturnHdModel) sessionFactory.getCurrentSession().get(clsPurchaseReturnHdModel.class, new clsPurchaseReturnHdModel_ID(code,strClientCode));
	}


	@SuppressWarnings("rawtypes")
	@Override
	public List funGetDtlList(String PRCode, String clientCode) {
		Query query = sessionFactory.getCurrentSession().createQuery("from clsPurchaseReturnDtlModel a, clsProductMasterModel b where strPRCode = :PRCode and a.strProdCode=b.strProdCode and a.strClientCode= :clientCode and b.strClientCode= :clientCode ");
		query.setParameter("PRCode", PRCode);
		query.setParameter("clientCode", clientCode);		
		List list = query.list();
		return list;
	}

	@Override
	public void funDeleteDtl(String PRCode, String clientCode) {
		Query query = sessionFactory.getCurrentSession().createQuery("delete clsPurchaseReturnDtlModel where strPRCode = :PRCode");
		query.setParameter("PRCode", PRCode);
		int result = query.executeUpdate();
		System.out.println("Result="+result);
	}

}
