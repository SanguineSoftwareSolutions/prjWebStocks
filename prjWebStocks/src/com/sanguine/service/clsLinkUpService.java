package com.sanguine.service;

import com.sanguine.model.clsLinkUpHdModel;

public interface clsLinkUpService{

	public void funAddUpdateARLinkUp(clsLinkUpHdModel objMaster);

	public clsLinkUpHdModel funGetARLinkUp(String docCode,String clientCode,String propCode);
	
	public int funExecute(String sql);

}
