package com.sanguine.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sanguine.model.clsStkAdjustmentDtlModel;
import com.sanguine.model.clsStkAdjustmentHdModel;
import com.sanguine.model.clsStkAdjustmentHdModel_ID;
import com.sanguine.webclub.model.clsWebClubGroupMasterModel;
import com.sanguine.webclub.model.clsWebClubGroupMasterModel_ID;

@Repository("clsStkAdjustmentDao")
public class clsStkAdjustmentDaoImpl implements clsStkAdjustmentDao
{
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void funAddUpdate(clsStkAdjustmentHdModel object)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(object);
	}
	
	@Override
	public void funAddUpdateDtl(clsStkAdjustmentDtlModel object)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(object);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<clsStkAdjustmentHdModel> funGetList(String clientCode)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from clsStkAdjustmentHdModel where strClientCode= :clientCode");
		query.setParameter("clientCode", clientCode);
		List list = query.list();
		return (List<clsStkAdjustmentHdModel>) list;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List funGetObject(String SACode,String clientCode)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from clsStkAdjustmentHdModel a,clsLocationMasterModel b "
				+ "where a.strSACode= :saCode and a.strLocCode=b.strLocCode and a.strClientCode= :clientCode and b.strClientCode= :clientCode");
		query.setParameter("saCode", SACode);
		query.setParameter("clientCode", clientCode);
		return query.list();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List funGetDtlList(String SACode,String clientCode) 
	{ 
		Query query = sessionFactory.getCurrentSession().createQuery("from clsStkAdjustmentDtlModel a,clsProductMasterModel b "
				+ "where a.strSACode = :saCode and a.strProdCode=b.strProdCode and a.strClientCode= :clientCode and b.strClientCode= :clientCode order by b.strProdName ");
		query.setParameter("saCode", SACode);
		query.setParameter("clientCode", clientCode);
		List list = query.list();
		return list;
	}
		
	public void funDeleteDtl(String SACode,String clientCode)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("delete clsStkAdjustmentDtlModel where strSACode = :saCode and strClientCode= :clientCode");
		query.setParameter("saCode", SACode);
		query.setParameter("clientCode", clientCode);
		int exe=query.executeUpdate();
	}
	
	public void funDeleteHd(String SACode,String clientCode)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("delete clsStkAdjustmentHdModel where strSACode = :saCode and strClientCode= :clientCode");
		query.setParameter("saCode", SACode);
		query.setParameter("clientCode", clientCode);
		int exe=query.executeUpdate();
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public List funGetStkAdjListReport(String fromDate,String toDate,String clientCode)
	{
		Query query =sessionFactory.getCurrentSession().createQuery("Select a.strSACode, a.dtSADate, a.strLocCode, b.strLocName, "
				+ " a.strNarration, a.strAuthorise "
				+ "from clsStkAdjustmentHdModel a ,clsLocationMasterModel b "
				+" where a.strLocCode=b.strLocCode and a.strClientCode=:clientCode and b.strClientCode= :clientCode and date(a.dtSADate) between '"+fromDate+"' and '"+toDate+"'");
		//System.out.println(query);
		query .setParameter("clientCode", clientCode);
	
		List list=query.list();
		return list;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List funGetStockAdjFlashData(String sql, String clientCode,
			String userCode) {
		Query query=sessionFactory.getCurrentSession().createQuery(sql);
		List list=query.list();
		return list;
	}
	
	@Override
	public clsStkAdjustmentHdModel funGetDataOfModel(String SACode,String clientCode)
	{
		return (clsStkAdjustmentHdModel) sessionFactory.getCurrentSession().get(clsStkAdjustmentHdModel.class,new clsStkAdjustmentHdModel_ID(SACode,clientCode));
	}
	
	@Override
	public List<clsStkAdjustmentDtlModel> funGetDatewiseDtlList(String fromDate,String toDate,String clientCode)
	{
		/*Criteria cr = sessionFactory.getCurrentSession().createCriteria(" from clsStkAdjustmentHdModel a ,clsStkAdjustmentDtlModel b "
				+ " where a.strSACode=b.strSACode and a.dtSADate>=:fromDate and  a.dtSADate<=:toDate and a.strClientCode=:clientCode ");
		cr.add(Restrictions.ge("fromDate", fromDate)); 
		cr.add(Restrictions.lt("toDate", toDate));
		cr.add(Restrictions.eq("clientCode", clientCode));*/
		List<clsStkAdjustmentDtlModel> listDtl = new ArrayList<clsStkAdjustmentDtlModel>();
		Query query = sessionFactory.getCurrentSession().createQuery(" from clsStkAdjustmentHdModel a ,clsStkAdjustmentDtlModel b "
				+ " where a.strSACode=b.strSACode and b.strType='Out' "
				+ " and a.strNarration like '%Sales Data%' and a.dtSADate between :fromDate and  :toDate and a.strClientCode=:clientCode ");
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		query.setParameter("clientCode", clientCode);
		List resultDtl = query.list();
		if(resultDtl!=null)
		{
			for(int i=0 ; i<resultDtl.size(); i++)
			{
				Object[] arrOb =  (Object[]) resultDtl.get(i);
				clsStkAdjustmentDtlModel objDtl = (clsStkAdjustmentDtlModel) arrOb[1];
				listDtl.add(objDtl);
			}
			
		}
		
		
		return listDtl;
		
	}
	
	
	@Override
	public List<clsStkAdjustmentDtlModel> funGetReportMonthlyDatewiseDtlList(String fromDate,String toDate,String clientCode)
	{
		
		List<clsStkAdjustmentDtlModel> listDtl = new ArrayList<clsStkAdjustmentDtlModel>();
		Query query = sessionFactory.getCurrentSession().createQuery(" from clsStkAdjustmentHdModel a ,clsStkAdjustmentDtlModel b "
				+ " where a.strSACode=b.strSACode and b.strType='Out' "
				+ " and a.strNarration like '%Sales Data%' and a.dtSADate between :fromDate and  :toDate "
				+ " and a.strClientCode=:clientCode group by b.strRemark,b.strWSLinkedProdCode,b.strSACode ");
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		query.setParameter("clientCode", clientCode);
		List resultDtl = query.list();
		if(resultDtl!=null)
		{
			for(int i=0 ; i<resultDtl.size(); i++)
			{
				Object[] arrOb =  (Object[]) resultDtl.get(i);
				clsStkAdjustmentDtlModel objDtl = (clsStkAdjustmentDtlModel) arrOb[1];
				listDtl.add(objDtl);
			}
			
		}
		return listDtl;
		
	}
	
	public clsStkAdjustmentHdModel funGetDataModelByInvCode(String invCode,String clientCode)
	{
		clsStkAdjustmentHdModel objModel = new clsStkAdjustmentHdModel();
		Query query = sessionFactory.getCurrentSession().createQuery(" from clsStkAdjustmentHdModel a  "
				+ " where   a.strNarration like :invCode and a.strClientCode=:clientCode ");
		query.setParameter("invCode", "%"+invCode+"%");
		query.setParameter("clientCode", clientCode);
		List resultDtl = query.list();
		if(!resultDtl.isEmpty())
		{
			objModel = (clsStkAdjustmentHdModel) resultDtl.get(0);
		}
		return objModel;
		
	}
	
	
	
	
}
