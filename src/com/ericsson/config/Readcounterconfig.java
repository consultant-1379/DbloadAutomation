package com.ericsson.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Readcounterconfig {
	private File inputfile=null;
	private BufferedReader br=null;
	private enum configproperties{counterName,value,nonnull};
	private String configFile="./etc/counter.config";
	HashMap<String,Set<String>> countertovalueMap=new HashMap<String,Set<String>>();

	Set<String> nonnulls=new HashSet<String>();
	private String[] headers= new String[3];
	public Readcounterconfig() {
		
	}
	public Readcounterconfig(String configfile) {
		configFile=configfile;
	}
	public void loadconfig() {
		createstream();
		Pattern commentpattern=Pattern.compile("^#.*");
		String line="";
		int header=0;
		while((line=readline())!=null) {
			Matcher matcher=commentpattern.matcher(line);
			if(!matcher.matches()) {
				if(header==0) {
					String[] tmpheader=line.split(",");
					if(tmpheader.length>=3) {
						headers[0]=tmpheader[0];
						headers[1]=tmpheader[1];
						headers[2]=tmpheader[2];
						header++;
					}
				}else {
					String[] tmpdata=line.split(",");
					if(tmpdata.length>=2) {
						String counterName="";
						String value="";
						String nonnull="";
						for(int i=0;i<tmpdata.length;i++) {
							switch(headers[i]) {
								case("counterName"):counterName=tmpdata[i];
													break;
								case("value"):value=tmpdata[i];
												break;
								case("nonnull"):nonnull=tmpdata[i];
												break;
									default:break;
							}
						}
						if(value!=null&&!value.equals("")) {
							Set<String> tmpvalueslist=null;
							if(countertovalueMap.containsKey(counterName)) {
								tmpvalueslist=countertovalueMap.get(counterName);
							}else {
								tmpvalueslist=new HashSet<String>();
							}
							tmpvalueslist.add(value);
							countertovalueMap.put(counterName,tmpvalueslist);
						}
						if(nonnull!=null&&!nonnull.equals("")) {
							nonnulls.add(nonnull);
						}
					}
				}
			}
		}
		
	}
	private void createstream() {
		try {
			br=new BufferedReader(new FileReader(new File(configFile)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String readline() {
		String line="";
		try {
			if((line=br.readLine())==null)
				return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return line;
	}
	public HashMap<String, Set<String>> getCountertovalueMap() {
		return countertovalueMap;
	}
	public Set<String> getNonnulls() {
		return nonnulls;
	}
	//Testing
	public static void main(String args[]) {
		Readcounterconfig rcc=new Readcounterconfig();
		rcc.loadconfig();
		HashMap<String, Set<String>> hsm=rcc.getCountertovalueMap();
		Set<String> set=rcc.getNonnulls();
		System.out.println(set);
		
	}
}
