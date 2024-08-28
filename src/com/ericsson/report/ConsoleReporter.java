package com.ericsson.report;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.ericsson.common.FileHelper;
import com.ericsson.config.StatusConfigPOJO;
import com.ericsson.db.repoint.IntfDbCache;

public class ConsoleReporter {
	private HashMap<String,Integer> differencerecorder=null;
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public void readCache() {
		int drysize=IntfDbCache.getbeforecachesize();
		int loadedsize=IntfDbCache.getaftercachesize();
		System.out.println("Tables Rows After before "+ loadedsize);
		if(drysize == loadedsize) {
			printEven();
		}else {
			System.out.println("Table count is mismatch");
		}
		displayDiff();
	}

	private void printEven() {
		HashMap<String,HashMap<String,Integer>> dry=IntfDbCache.getBeforecallAdapter();
		HashMap<String,HashMap<String,Integer>> load=IntfDbCache.getAftercallAdapter();
		System.out.println("Tables Rows before                                               Tables Rows After");
		for(String intf:dry.keySet()) {
			HashMap<String,Integer> drytablesrows=dry.get(intf);
			HashMap<String,Integer> loadtablesrows=load.get(intf);
			for(String tablename:drytablesrows.keySet()) {
				System.out.println(tablename+"  "+drytablesrows.get(tablename)+"                     "+tablename+" "+loadtablesrows.get(tablename));
				if(drytablesrows.get(tablename)<loadtablesrows.get(tablename)) {
					if(differencerecorder==null) {
						differencerecorder=new HashMap<String,Integer>();
					}
					differencerecorder.put(tablename,loadtablesrows.get(tablename)-drytablesrows.get(tablename));
				}
			}	
		}
		//displayDiff();
	}

	private void displayDiff() {
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("Tables Modified and the number of Rows updated");
		if(differencerecorder != null) {
			if(differencerecorder.keySet().size()>0) {
				for(String tablename:differencerecorder.keySet()) {
					System.out.println(tablename+"        "+differencerecorder.get(tablename));
				}
			}
		}
		System.out.println();
		System.out.println("End");
	}
	public void printCounterstatus(HashMap<String, StatusConfigPOJO> counterstatusMap) {
		FileHelper fh=new FileHelper();
		if(counterstatusMap.size()>0) {
			System.out.println("[INFO]:: Below will provide the validation results for provided counters");
			System.out.println(ANSI_YELLOW_BACKGROUND+ANSI_BLUE+"[HEADER]:: counterName   columnName   Tablename   value   status"+ANSI_RESET);
			try {
				fh.createFile("csvextraction/status.csv");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fh.writetextline("counterName,columnName,Tablename,value,status");
		}
		for(String countername:counterstatusMap.keySet()) {
			
			StatusConfigPOJO statuscon=counterstatusMap.get(countername);
			countername=countername.split(":")[0];
			HashMap<String,Boolean> map=statuscon.getValuestatus();
			Set<String> set=statuscon.getValues();
			for(String value:set) {
				String BACKGROUND_COLOR="\u001B[41m";
				String status="FAILED";
				if(map.containsKey(value)) {
					if(map.get(value)) {
						//System.out.println("[Getmap value]:: "+value+" "+map.get(value));
						status="SUCCESS";
						BACKGROUND_COLOR = "\u001B[42m";
					}else {
						status="FAILED";
					}
				}
				System.out.println("[Verification]:: "+countername+"  "+statuscon.getColumnname()+"  "+statuscon.getTablename()+"  "+value+"  "+BACKGROUND_COLOR+ANSI_BLUE+status+ANSI_RESET);
				fh.writetextline(countername+","+statuscon.getColumnname()+","+statuscon.getTablename()+","+value+","+status);
			}
		}
		fh.closeFile();
	}

	public void printnonnullcolumns(HashMap<String, Boolean> notnullcounters) {
		FileHelper fh=new FileHelper();
		if(notnullcounters.size()>0) {
			System.out.println("[INFO]:: Below will provide the validation for not to be null columns");
			System.out.println(ANSI_YELLOW_BACKGROUND+ANSI_BLUE+"[HEADER]:: columnName   status"+ANSI_RESET);
			try {
				fh.createFile("csvextraction/nonnullcolumns.csv");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fh.writetextline("columnName,status");
		}
		for(String columnName:notnullcounters.keySet()) {
			boolean status=notnullcounters.get(columnName);
			String BACKGROUND_COLOR="\u001B[42m";
			String statusstring="SUCCESS";
			if(status||columnName.contains("::tmp123")) {
				BACKGROUND_COLOR = "\u001B[41m";
				statusstring="FAILED";
				if(columnName.contains("::tmp123")) {
					System.out.println(columnName.split("::")[0]+"  "+BACKGROUND_COLOR+ANSI_BLUE+statusstring+ANSI_RESET);
					fh.writetextline(columnName.split("::")[0]+","+statusstring);
				}else {
					System.out.println(columnName+"  "+BACKGROUND_COLOR+ANSI_BLUE+statusstring+ANSI_RESET);
					fh.writetextline(columnName+","+statusstring);
				}
			}else {
				System.out.println(columnName+"  "+BACKGROUND_COLOR+ANSI_BLUE+statusstring+ANSI_RESET);
				fh.writetextline(columnName+","+statusstring);
			}
		}
		fh.closeFile();
	}

}
