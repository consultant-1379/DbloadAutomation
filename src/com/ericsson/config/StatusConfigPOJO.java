package com.ericsson.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class StatusConfigPOJO implements Cloneable {
	private boolean status=false;
	private String columnname="";
	private Set<String> values=null;
	private HashMap<String,Boolean> valuestatus=null;
	
	private int columnno=-1;
	private String tablename=null;
	//private HashMap<String,Boolean> tablestatus=null;
	private String value=null;

	public StatusConfigPOJO(Set<String> values2) {
		values=new HashSet<String>();
		valuestatus=new HashMap<String,Boolean>();
		this.values=values2;
	}
	public HashMap<String, Boolean> getValuestatus() {
		return valuestatus;
	}
	public void setValuestatus(HashMap<String, Boolean> valuestatus) {
		this.valuestatus = valuestatus;
	}
	public void setValuestatus(String value,Boolean status) {
		if(values.contains(value)) {
			valuestatus.put(value, status);
		}
	}
	public boolean getValuestatus(String value) {
		if(values.contains(value)) {
			return valuestatus.get(value);
			}
		return false;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getColumnno() {
		return columnno;
	}
	public void setColumnno(int columnno) {
		this.columnno = columnno;
	}
	public  String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename=tablename;
	}
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getColumnname() {
		return columnname;
	}
	public void setColumnname(String columnname) {
		this.columnname = columnname;
	}
	public Set<String> getValues() {
		return values;
	}
	public void setValues(HashSet<String> values) {
		this.values = values;
	}

}
