package com.ericsson.db.repoint;

import java.sql.Date;

public class LogsessionAdapter {
	private int session_id=0;
	private String filename=null;
	private String status=null;
	private Date sessionstarttime=null;
	private String source=null;
	private String typename=null;
	private int no_of_rows=0;
	private int no_of_counters=0;
	private String datestring=null;
	
	
	
	public LogsessionAdapter(int session_id2, String filename2, String status2, String source2, String typename2,
			Date sessionstarttime2, int no_of_rows2, int no_of_counters2, String datestring2) {
		this.session_id=session_id2;
		this.filename=filename2;
		this.status=status2;
		this.source=source2;
		this.typename=typename2;
		this.sessionstarttime=sessionstarttime2;
		this.no_of_rows=no_of_rows2;
		this.no_of_counters=no_of_counters2;
		this.datestring=datestring2;
	}
	public String getTypename() {
		return typename;
	}
	public void setTypename(String typename) {
		this.typename = typename;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getSession_id() {
		return session_id;
	}
	public void setSession_id(int session_id) {
		this.session_id = session_id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getSessionstarttime() {
		return sessionstarttime;
	}
	public void setSessionstarttime(Date sessionstarttime) {
		this.sessionstarttime = sessionstarttime;
	}
	public int getNo_of_rows() {
		return no_of_rows;
	}
	public void setNo_of_rows(int no_of_rows) {
		this.no_of_rows = no_of_rows;
	}
	public int getNo_of_counters() {
		return no_of_counters;
	}
	public void setNo_of_counters(int no_of_counters) {
		this.no_of_counters = no_of_counters;
	}
	public String getDatestring() {
		return datestring;
	}
	public void setDatestring(String datestring) {
		this.datestring = datestring;
	}
}
