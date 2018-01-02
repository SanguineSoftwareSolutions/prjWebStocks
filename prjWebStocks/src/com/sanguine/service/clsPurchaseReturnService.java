package com.sanguine.service;

import java.util.List;
import com.sanguine.model.clsPurchaseReturnDtlModel;
import com.sanguine.model.clsPurchaseReturnHdModel;

public interface clsPurchaseReturnService {

	public long funGetLastNo(String tableName,String masterName,String columnName);	
	public void funAddPRHd(clsPurchaseReturnHdModel PRHd);	
	public void funAddUpdatePRDtl(clsPurchaseReturnDtlModel PRDtl);
	public List<clsPurchaseReturnHdModel> funGetList();	
	public clsPurchaseReturnHdModel funGetObject(String code,String strClientCode);
	public List funGetDtlList(String PRCode,String clientCode);
	public void funDeleteDtl(String PRCode,String clientCode);
	
}
