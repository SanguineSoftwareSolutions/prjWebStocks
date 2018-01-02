package com.sanguine.webpms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.sanguine.webpms.dao.clsReservationDao;
import com.sanguine.webpms.model.clsReservationDtlModel;
import com.sanguine.webpms.model.clsReservationHdModel;

@Service("clsReservationService")
public class clsReservationServiceImpl implements clsReservationService{
	@Autowired
	private clsReservationDao objReservationDao;

	@Override
	public void funAddUpdateReservationHd(clsReservationHdModel objHdModel, String bookingType){
		
		objReservationDao.funAddUpdateReservationHd(objHdModel,bookingType);
	}
	
	@Override
	public clsReservationHdModel funGetReservationList(String reservationNo,String clientCode,String propertyCode)
	{
		return objReservationDao.funGetReservationList(reservationNo, clientCode, propertyCode);
	}

}
