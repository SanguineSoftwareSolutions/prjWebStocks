package com.sanguine.webpms.dao;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.sanguine.webpms.model.clsWalkinDtl;
import com.sanguine.webpms.model.clsWalkinHdModel;

@Repository("clsWalkinDao")
public class clsWalkinDaoImpl implements clsWalkinDao{

	@Autowired
	private SessionFactory webPMSSessionFactory;
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void funAddUpdateWalkinHd(clsWalkinHdModel objHdModel)
	{
		webPMSSessionFactory.getCurrentSession().saveOrUpdate(objHdModel);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void funAddUpdateWalkinDtl(clsWalkinDtl objDtlModel) 
	{
		webPMSSessionFactory.getCurrentSession().saveOrUpdate(objDtlModel);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List funGetWalkinDataDtl(String walkinNo, String clientCode)
	{
		List list=null;
		try
		{
			Criteria cr = webPMSSessionFactory.getCurrentSession().createCriteria(clsWalkinHdModel.class);
			cr.add(Restrictions.eq("strWalkinNo",walkinNo));
			cr.add(Restrictions.eq("strClientCode",clientCode));
			list = cr.list();
			
			if(list.size()>0)
			{
				clsWalkinHdModel objWalkinHdModel=(clsWalkinHdModel)list.get(0);
				objWalkinHdModel.getListWalkinDtlModel().size();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
}
