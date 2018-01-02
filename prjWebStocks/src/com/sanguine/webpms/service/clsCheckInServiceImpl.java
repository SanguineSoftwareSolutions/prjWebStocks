package com.sanguine.webpms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.sanguine.webpms.dao.clsCheckInDao;
import com.sanguine.webpms.model.clsCheckInHdModel;

@Service("clsCheckInService")

public class clsCheckInServiceImpl implements clsCheckInService{
	@Autowired
	private clsCheckInDao objCheckInDao;

	@Override
	public void funAddUpdateCheckInHd(clsCheckInHdModel objHdModel){
		objCheckInDao.funAddUpdateCheckInHd(objHdModel);
	}

	@Override
	public clsCheckInHdModel funGetCheckInData(String checkInNo, String clientCode)
	{
		return objCheckInDao.funGetCheckInData(checkInNo, clientCode);
	}

}
