package com.sanguine.webpms.dao;

import com.sanguine.webpms.model.clsReservationHdModel;

public interface clsReservationDao{

	public void funAddUpdateReservationHd(clsReservationHdModel objHdModel, String bookingType);
	
	public clsReservationHdModel funGetReservationList(String reservationNo,String clientCode,String propertyCode);

}
