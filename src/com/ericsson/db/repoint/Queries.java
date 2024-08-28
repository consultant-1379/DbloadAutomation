package com.ericsson.db.repoint;

public class Queries {
	
	public static String _get_interfaces_sql(final String __schema, String interfacename) {
		return "select di.interfacename, im.tagid, im.dataformatid, df.foldername, im.transformerid"
				+ " from datainterface di, interfacemeasurement im, dataformat df"
				+ " where di.interfacename = im.interfacename and im.dataformatid = df.dataformatid"
				+ " and di.status = 1 "+"and di.interfacename like '%"+interfacename+"%' and im.status = 1" + " and df.versionid in (select versionid from " + __schema
				+ "tpactivation where status = 'ACTIVE')" + " ORDER BY im.dataformatid";
	}
	public static String _get_rowcount_sql(String tablename) {
		return "select count(*) from "+tablename;
	}
	public static String _get_meta_collection_sets(String collectionsetname, boolean likeflag) {
		if(likeflag) {
			return "select * from meta_collection_sets where collection_set_name like '%"+collectionsetname+"%' and ENABLED_FLAG='Y'";
		}else {
			return "select * from meta_collection_sets where collection_set_name ='"+collectionsetname+"' and ENABLED_FLAG='Y'";
		}
	}
	public static String _get_log_session_adapter(String filename) {
		return "select * from log_session_adapter where filename like '%"+filename+"%' order by sessionstarttime desc";
	}
	public static String _get_dataitems(String tablename) {
		return "select * from dataitem where dataformatid like '%"+tablename+"%' order by colnumber;";
	}
	public static String _get_datatablerows(String tablename,int rows,String datestring) {
		if(tablename.startsWith("DIM")) {
			return "select * from "+tablename;
		}
		return "select * from "+tablename+"_RAW where DATETIME_ID='"+datestring+"'";
	}
	public static String _get_log_aggregationStatus(String Aggregationtype,String status,String startdate,String enddate) {
		return "select * from LOG_AggregationStatus sta where Aggregation like '%"+Aggregationtype+"%' and TIMELEVEL in ('RANKBH','DAY','DAYBH','WEEK','WEEKBH','MONTH') and status='"+status+"' and datadate between '"+startdate+"' and '"+enddate+"'";
	}

}
