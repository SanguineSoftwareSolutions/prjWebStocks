package com.sanguine.webpms.service;

import com.sanguine.webpms.model.clsReservationHdModel;

public interface clsReservationService{

	public void funAddUpdateReservationHd(clsReservationHdModel objHdModel, String bookingType);
	
	public clsReservationHdModel funGetReservationList(String reservationNo,String clientCode,String propertyCode);

}
