package com.ericsson.aggregation.bh;

import com.ericsson.eniq.busyhourcfg.config.BusyhourProperties;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.database.TechPackReader;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;
import com.ericsson.eniq.repository.ETLCServerProperties;

public class RockSession implements Session {
	DatabaseSession databaseSession=null;
	DWHStorageTimeAction dwhStorageTimeAction = null;
	public static RockSession rocksession=null;
	TechPack sourcetp=null;
	TechPack targettp=null;
	String versionId=null;
	ETLCServerProperties etlcserverProperties=null;
	BusyhourProperties busyhourProperties=null;
	TechPackReader techpackreader=null;
	String startdate=null;
	String startMonth=null;
	String startYear=null;
	String startWeek=null;
	String enddate=null;
	String endMonth=null;
	String endYear=null;
	String endWeek=null;
	String Timelevel=null;
	public String getStartWeek() {
		return startWeek;
	}
	public void setStartWeek(String startWeek) {
		this.startWeek = startWeek;
	}
	public String getEndWeek() {
		return endWeek;
	}
	public void setEndWeek(String endWeek) {
		this.endWeek = endWeek;
	}

	public String getTimelevel() {
		return Timelevel;
	}
	public void setTimelevel(String timelevel) {
		Timelevel = timelevel;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}
	public String getStartYear() {
		return startYear;
	}
	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getEndMonth() {
		return endMonth;
	}
	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}
	public String getEndYear() {
		return endYear;
	}
	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}
	public TechPackReader getTechpackreader() {
		return techpackreader;
	}
	public void setTechpackreader(TechPackReader techpackreader) {
		this.techpackreader = techpackreader;
	}
	public static RockSession getInstance() {
		if(rocksession==null) {
			rocksession=new RockSession();
			return rocksession;
		}
		return rocksession;
	}
	public ETLCServerProperties getEtlcserverProperties() {
		return etlcserverProperties;
	}

	public void setEtlcserverProperties(ETLCServerProperties etlcserverProperties) {
		this.etlcserverProperties = etlcserverProperties;
	}

	public BusyhourProperties getBusyhourProperties() {
		return busyhourProperties;
	}

	public void setBusyhourProperties(BusyhourProperties busyhourProperties) {
		this.busyhourProperties = busyhourProperties;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public TechPack getSourcetp() {
		return sourcetp;
	}

	public void setSourcetp(TechPack sourcetp) {
		this.sourcetp = sourcetp;
	}

	public TechPack getTargettp() {
		return targettp;
	}

	public void setTargettp(TechPack targettp) {
		this.targettp = targettp;
	}

	public DatabaseSession getDatabaseSession() {
		return databaseSession;
	}

	public void setDatabaseSession(DatabaseSession databaseSession) {
		this.databaseSession = databaseSession;
	}

	public DWHStorageTimeAction getDwhStorageTimeAction() {
		return dwhStorageTimeAction;
	}

	public void setDwhStorageTimeAction(DWHStorageTimeAction dwhStorageTimeAction) {
		this.dwhStorageTimeAction = dwhStorageTimeAction;
	}

	public String getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
