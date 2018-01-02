package com.sanguine.webpms.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sanguine.webpms.model.clsReservationDtlModel;
import com.sanguine.webpms.model.clsReservationHdModel;

@Repository("clsReservationDao")
public class clsReservationDaoImpl implements clsReservationDao{

	@Autowired
	private SessionFactory webPMSSessionFactory;

	@Override
	@Transactional(value = "WebPMSTransactionManager")
	public void funAddUpdateReservationHd(clsReservationHdModel objHdModel, String bookingType){
		
		webPMSSessionFactory.getCurrentSession().saveOrUpdate(objHdModel);
		
		for(clsReservationDtlModel objResDtlModel:objHdModel.getListReservationDtlModel())
		{
			String sql="update tblroom set strStatus='"+bookingType+"' "
				+ " where strRoomCode='"+objResDtlModel.getStrRoomNo()+"' and strClientCode='"+objHdModel.getStrClientCode()+"'";
			webPMSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
		}
	}

	@Override
	@Transactional(value = "WebPMSTransactionManager")
	public clsReservationHdModel funGetReservationList(String reservationNo,String clientCode,String propertyCode)
	{
		Criteria cr=webPMSSessionFactory.getCurrentSession().createCriteria(clsReservationHdModel.class);
		cr.add(Restrictions.eq("strReservationNo", reservationNo));
		cr.add(Restrictions.eq("strClientCode", clientCode));
		cr.add(Restrictions.eq("strPropertyCode", propertyCode));
		List list=cr.list();
		
		clsReservationHdModel objModel=null;
		if(list.size()>0)
		{
			objModel=(clsReservationHdModel)list.get(0);
			objModel.getListReservationDtlModel().size();
		}
		
		return objModel;
	}

}
