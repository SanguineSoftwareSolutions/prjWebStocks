package com.sanguine.webpms.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sanguine.webpms.model.clsBillDtlModel;
import com.sanguine.webpms.model.clsCheckInDtl;
import com.sanguine.webpms.model.clsCheckInHdModel;
import com.sanguine.webpms.model.clsReservationDtlModel;
import com.sanguine.webpms.model.clsReservationHdModel;

@Repository("clsCheckInDao")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false,value = "WebPMSTransactionManager")
public class clsCheckInDaoImpl implements clsCheckInDao{

	@Autowired
	private SessionFactory webPMSSessionFactory;

	@Override	
	public void funAddUpdateCheckInHd(clsCheckInHdModel objHdModel){
		webPMSSessionFactory.getCurrentSession().saveOrUpdate(objHdModel);
		
		for(clsCheckInDtl objCheckInDtlModel:objHdModel.getListCheckInDtl())
		{
			String sql="update tblroom set strStatus='Occupied' "
				+ " where strRoomCode='"+objCheckInDtlModel.getStrRoomNo()+"' and strClientCode='"+objHdModel.getStrClientCode()+"'";
			webPMSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
		}
		
	}

	@Override
	public clsCheckInHdModel funGetCheckInData(String checkInNo, String clientCode)
	{
		Criteria cr=webPMSSessionFactory.getCurrentSession().createCriteria(clsCheckInHdModel.class);
		cr.add(Restrictions.eq("strCheckInNo", checkInNo));
		cr.add(Restrictions.eq("strClientCode", clientCode));
		List list=cr.list();
		
		clsCheckInHdModel objModel=null;
		if(list.size()>0)
		{
			objModel=(clsCheckInHdModel)list.get(0);
			objModel.getListCheckInDtl().size();
		}
		
		return objModel;
	}

}
