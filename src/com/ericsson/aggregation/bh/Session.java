package com.ericsson.aggregation.bh;

import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;

public interface Session {
	DatabaseSession databaseSession=null;
	DWHStorageTimeAction dwhStorageTimeAction = null;
	TechPack sourcetp=null;
	TechPack targettp=null;
	public String getServletContext();

}
