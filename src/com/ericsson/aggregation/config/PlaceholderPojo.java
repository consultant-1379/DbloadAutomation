package com.ericsson.aggregation.config;

import java.util.ArrayList;

public class PlaceholderPojo {
	private String bhlevel =null;
	private String bhtype =null;
	private String criteria =null;
	private String formula =null;
	private String whereCondition =null;
	private String description=null;

	private ArrayList<String> sources =null;
	private ArrayList<String> keys=null;
	private String id=null;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBhlevel() {
		return bhlevel;
	}
	public void setBhlevel(String bhlevel) {
		this.bhlevel = bhlevel;
	}
	public String getBhtype() {
		return bhtype;
	}
	public void setBhtype(String bhtype) {
		this.bhtype = bhtype;
	}
	public String getCriteria() {
		return criteria;
	}
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getWhereCondition() {
		return whereCondition;
	}
	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}
	public ArrayList<String> getSources() {
		return sources;
	}
	public void setSources(ArrayList<String> sources) {
		this.sources = sources;
	}
	public ArrayList<String> getKeys() {
		return keys;
	}
	public void setKeys(ArrayList<String> keys) {
		this.keys = keys;
	}

}
