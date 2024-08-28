package com.ericsson.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
	public String regex="\\$\\{(.*)\\}.*";
	public String regex2="(\\$\\{)(.*)(\\})(.*)";
	public String resolveDirVariable(String Dir) {
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(Dir);
		String Variablename=null;
		if(matcher.find()) {
			Variablename=matcher.group(1);
			//System.out.println(Variablename);
		}
		
		return Variablename;
		
	}
	public String resolveRemainVariable(String Dir) {
		Pattern pattern=Pattern.compile(regex2);
		Matcher matcher=pattern.matcher(Dir);
		String Variablename=null;
		if(matcher.find()) {
			Variablename=matcher.group(4);
			//System.out.println(Variablename);
		}
		
		return Variablename;
		
	}
	
	public String resolveFilePath(String FilePath) {
		String Variablename = resolveDirVariable(FilePath);
		Variablename=System.getProperty(Variablename);
		String RemainingVariable=resolveRemainVariable(FilePath);
		
		return Variablename+RemainingVariable;
		
	}
	public String getProperty() {
		return null;
	}
	//Testing
	/*
	public static void main(String[] args) {
		new Utility().resolveDirVariable("${PMDATA_SIM_DIR}/eniq_oss_1/dc_e_occ_meters/");
		new Utility().resolveRemainVariable("${PMDATA_SIM_DIR}/eniq_oss_1/dc_e_occ_meters/");
	}
	*/
}
