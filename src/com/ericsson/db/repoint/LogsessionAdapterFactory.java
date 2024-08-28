package com.ericsson.db.repoint;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import ssc.rockfactory.RockFactory;

public class LogsessionAdapterFactory {
	private RockFactory dwhrock=null;
	private HashMap<String,HashMap<String,ArrayList<LogsessionAdapter>>> LogsessionCache =null;
	
	private HashMap<String, ArrayList<File>> intffiles=null;
	public LogsessionAdapterFactory(HashMap<String, ArrayList<File>> intffiles) {
		this.intffiles=intffiles;
	}

	public LogsessionAdapterFactory(HashMap<String, ArrayList<File>> intffiles, RockFactory rockFactory) {
		this(intffiles);
		this.dwhrock=rockFactory;
		validate();
	}
	public RockFactory getDwhrock() {
		return dwhrock;
	}

	public void setDwhrock(RockFactory dwhrock) {
		this.dwhrock = dwhrock;
	}
   public void validate() {
	   for(String intfname:intffiles.keySet()) {
       	System.out.println("[INFO]::Submit "+intfname);
       	ArrayList<File> filelist=intffiles.get(intfname);
       		for(File file:filelist) {
       			Connection con = null;
       			PreparedStatement ps = null;
       			ResultSet rs = null;
       			String sql=Queries._get_log_session_adapter(file.getName());
       			con=dwhrock.getConnection();
       			try {
       				ps = con.prepareStatement(sql);
       				rs = ps.executeQuery();
       				while(rs.next()) {
       					int session_id=rs.getInt("SESSION_ID");
       					String filename=rs.getNString("FILENAME");
       					String status=rs.getNString("STATUS");
       					String source=rs.getNString("SOURCE");
       					String typename=rs.getNString("TYPENAME");
       					Date sessionstarttime=rs.getDate("SESSIONSTARTTIME");
       					int no_of_rows=rs.getInt("NUM_OF_ROWS");
       					int no_of_counters=rs.getInt("NUM_OF_COUNTERS");
       					String datestring=rs.getTimestamp("ROP_STARTTIME").toString();
       					System.out.println("[TEST]:: DateString captured "+datestring);
       					if(LogsessionCache == null) {
       						LogsessionCache=new HashMap<String,HashMap<String,ArrayList<LogsessionAdapter>>>();
       					}
       					HashMap<String,ArrayList<LogsessionAdapter>> fileMap=LogsessionCache.get(intfname);
       					if(fileMap == null && (intfname.contains(source))) {
       						fileMap =new HashMap<String,ArrayList<LogsessionAdapter>>();
       						ArrayList<LogsessionAdapter> tmplogsessionAdapterList=new ArrayList<LogsessionAdapter>();
       						tmplogsessionAdapterList.add(new LogsessionAdapter(session_id,filename,
       								status,source,typename,sessionstarttime,no_of_rows,no_of_counters,datestring));
       						fileMap.put(file.getName(),tmplogsessionAdapterList);
       						LogsessionCache.put(intfname,fileMap);
       					}else if((intfname.contains(source))){
       							ArrayList<LogsessionAdapter> tmpLogsessionAdapterList=fileMap.get(file.getName());
       							if(tmpLogsessionAdapterList == null) {
       								tmpLogsessionAdapterList=new ArrayList<LogsessionAdapter> ();
       							}
       							tmpLogsessionAdapterList.add(new LogsessionAdapter(session_id,filename,
       								status,source,typename,sessionstarttime,no_of_rows,no_of_counters,datestring));
       							fileMap.put(file.getName(), tmpLogsessionAdapterList);
       							LogsessionCache.put(intfname,fileMap);
       					}
       					}
       			} catch (SQLException e) {
       				// TODO Auto-generated catch block
       				//con=null;
       				//log.log(Level.WARNING, classname+": failed to run the query get the meta_colletion_sets"+e.getMessage());
       				
       				
       			}
       		}
       }
   }
   public HashMap<String, HashMap<String, ArrayList<LogsessionAdapter>>> getLogsessionCache() {
		return LogsessionCache;
	}
	
}
