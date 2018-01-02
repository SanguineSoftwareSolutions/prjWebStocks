package com.sanguine.webpms.service;

import com.sanguine.webpms.model.clsCheckInHdModel;

public interface clsCheckInService{

	public void funAddUpdateCheckInHd(clsCheckInHdModel objHdModel);
	
	public clsCheckInHdModel funGetCheckInData(String checkInNo, String clientCode);
	
}
