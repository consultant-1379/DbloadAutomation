package com.ericsson.db.repoint;

import java.util.HashMap;

public class IntfDbCache {
	
	private static HashMap<String,HashMap<String,Integer>> beforecallAdapter=null;
	private static HashMap<String,HashMap<String,Integer>> aftercallAdapter=null;
	public static HashMap<String, HashMap<String, Integer>> getBeforecallAdapter() {
		return beforecallAdapter;
	}
	public static void setBeforecallAdapter(HashMap<String, HashMap<String, Integer>> beforecallAdapter) {
		IntfDbCache.beforecallAdapter = beforecallAdapter;
	}
	public static void setBeforecallAdapter(String intfName,HashMap<String, Integer> tablesrows) {
		if(beforecallAdapter==null) {
			beforecallAdapter = new HashMap<String,HashMap<String,Integer>>();
		}
		beforecallAdapter.put(intfName, tablesrows);
	}
	public static HashMap<String, HashMap<String, Integer>> getAftercallAdapter() {
		return aftercallAdapter;
	}
	public static void setAftercallAdapter(HashMap<String, HashMap<String, Integer>> aftercallAdapter) {
		
		IntfDbCache.aftercallAdapter = aftercallAdapter;
	}
	public static void setAftercallAdapter(String intfName,HashMap<String, Integer> tablesrows) {
		if(aftercallAdapter==null) {
			aftercallAdapter = new HashMap<String,HashMap<String,Integer>>();
		}
		aftercallAdapter.put(intfName, tablesrows);
	}
	
	public static int getbeforecachesize() {
		return beforecallAdapter.size();
	}
	public static int getaftercachesize() {
		return aftercallAdapter.size();
	}
	

}
