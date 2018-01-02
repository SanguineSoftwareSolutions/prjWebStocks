package com.sanguine.webpms.dao;

import com.sanguine.webpms.model.clsCheckInHdModel;

public interface clsCheckInDao{

	public void funAddUpdateCheckInHd(clsCheckInHdModel objHdModel);

	public clsCheckInHdModel funGetCheckInData(String checkInNo, String clientCode);
}
