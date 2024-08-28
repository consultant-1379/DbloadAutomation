package com.ericsson.aggregation.config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.ericsson.aggregation.bh.RockSession;
public class PlaceholderJsonReader {
	private static List<PlaceholderPojo> list=null;
	private static PlaceholderJsonReader phsr=null;
	public String bhlevel=null;
	public String cp_No=null;
	public String description=null;
	public String wherecondition=null;
	public String formula=null;
	public ArrayList<String> Source_tables=null; 
	public ArrayList<String> Keys=null;
	public static PlaceholderJsonReader getInstance() {
		if(phsr==null) {
			phsr=new PlaceholderJsonReader();
			return phsr;
		}
		return phsr;
	}
	public static List<PlaceholderPojo> getList() {
		if(list==null) {
			PlaceholderJsonReader phsrobj=PlaceholderJsonReader.getInstance();
			phsrobj.mains();
			return list;
		}
		return list;
	}
	/*
	public ArrayList<String> getKeys() {
		return Keys;
	}

	public void setKeys(ArrayList<String> keys) {
		Keys = keys;
	}

	public ArrayList<String> getSource_tables() {
		return Source_tables;
	}

	public void setSource_tables(ArrayList<String> source_tables) {
		Source_tables = source_tables;
	}

	public String getBhlevel() {
		return bhlevel;
	}

	public void setBhlevel(String bhlevel) {
		this.bhlevel = bhlevel;
	}
	public String getCp_No() {
		return cp_No;
	}

	public void setCp_No(String cp_No) {
		this.cp_No = cp_No;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWherecondition() {
		return wherecondition;
	}

	public void setWherecondition(String wherecondition) {
		this.wherecondition = wherecondition;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PlaceholderJsonReader phsr=new PlaceholderJsonReader();
		phsr.mains();
		for(String table:phsr.getSource_tables()) {
			System.out.println("tables  "+table);
		}
		for(String key:phsr.getKeys()) {
			System.out.println("Keys  "+key);
		}
	}*/
	 
	    public  void mains(){
	    	list=new ArrayList<PlaceholderPojo>();
	        String basedir=System.getProperty("java.basedir");
	        File jsonInputdirectory = new File(basedir+"Placeholderconfig");
	        System.out.println("Outside the file  loop "+jsonInputdirectory.toString());
	        if(jsonInputdirectory.isDirectory()&&jsonInputdirectory.exists()) {
	        	System.out.println("if condition ");
	        	for(File file:jsonInputdirectory.listFiles((File f, String name)->{Pattern pattern=Pattern.compile(".*_CP[0-5]{1}.json");
	        	Matcher match=pattern.matcher(name);
	        	return match.matches();
	        	})) {
	        		System.out.println("Inside the file  loop "+file.toString());
	        	InputStream is;
	        	//ArrayList<String> temparray=null;
	        	try {
	        		is = new FileInputStream(file);
	        		JsonReader reader = Json.createReader(is);
	        		JsonObject placeholderObj = reader.readObject();
	        		reader.close();
	        		Iterator it=placeholderObj.getJsonArray("source_tables").iterator();
	        		Iterator it1=placeholderObj.getJsonArray("Keys").iterator();
	        		MapList m1=(a)-> {List<String> sources=new ArrayList<String>();
	        		while(a.hasNext()){sources.add(a.next().toString());} return sources;};
	        		PlaceholderPojo placeholderpojo=new PlaceholderPojo();
	        		
	        		placeholderpojo.setId(placeholderObj.getString("BHLevel")+"_"+placeholderObj.getString("BHType"));
	        		placeholderpojo.setDescription(placeholderObj.getString("Description"));
	        		placeholderpojo.setBhlevel(placeholderObj.getString("BHLevel"));
	        		placeholderpojo.setBhtype(placeholderObj.getString("BHType"));
	        		placeholderpojo.setWhereCondition(placeholderObj.getString("whereCondition"));
	        		placeholderpojo.setFormula(placeholderObj.getString("formula"));
	        		placeholderpojo.setSources((ArrayList<String>) m1.mapfunc(it));
	        		placeholderpojo.setKeys((ArrayList<String>) m1.mapfunc(it1));
	        		list.add(placeholderpojo);
	        		
	        		/*System.out.println("Emp Name: " + empObj.getString("CP_No"));
	        		this.setBhlevel(empObj.getString("BHLevel"));
	        		this.setCp_No(empObj.getString("CP_No"));
	        		this.setDescription(empObj.getString("Description"));
	        		this.setWherecondition(empObj.getString("whereCondition"));
	        		this.setFormula(empObj.getString("Formula"));
	        		System.out.println("Emp Id: " + empObj.getString("Description"));
	        		JsonArray jsonarray=empObj.getJsonArray("Source_tables");
	        		Iterator<JsonValue> tables=jsonarray.iterator();
	        		while(tables.hasNext()) {
	        			if(temparray == null) {
	        				temparray= new ArrayList<String>();
	        				temparray.add(tables.next().toString());
	        			}else {
	        				temparray.add(tables.next().toString());
	        			}
	        		}
	            this.setSource_tables(temparray);
	            jsonarray=empObj.getJsonArray("Keys");
	            Iterator<JsonValue> keys=jsonarray.iterator();
	            temparray = null;
	            while(keys.hasNext()) {
	            	if(temparray == null) {
	            		temparray= new ArrayList<String>();
	            		temparray.add(keys.next().toString());
	            	}else {
	            		temparray.add(keys.next().toString());
	            	}
	            }
	            this.setKeys(temparray);
	            */
	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        }
	        }
	     }
	    public void populateAggregationdate(RockSession session) {
	    	String basedir=System.getProperty("java.basedir");
	    	InputStream is;
        	//ArrayList<String> temparray=null;
        	
        		try {
					is = new FileInputStream(new File(basedir+"/etc/Aggregation.json"));
					JsonReader reader = Json.createReader(is);
	        		JsonObject dateobj = reader.readObject();
	        		reader.close();
	        		System.out.println(dateobj.getString("Timelevel"));
	        		session.setTimelevel(dateobj.getString("Timelevel"));
	        		System.out.println(dateobj.getString("startdate"));
	        		setdate(dateobj.getString("startdate"),"start",session);
	        		System.out.println(dateobj.getString("enddate"));
	        		setdate(dateobj.getString("enddate"),"end",session);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	
	    }
	    private void setdate(String datestring, String datetype, RockSession session) {
	    	Pattern pattern1=Pattern.compile("[0-3]{0,1}[0-9]{1}([_\\-/]){1}[0-1]{0,1}[0-9]{1}[_\\-/]{1}[0-9]{4}");
		     	Matcher matchs =pattern1.matcher(datestring);
		     	if(matchs.matches()) {
		     		if(datetype.equals("start")) {
		     			String[] _parts=datestring.split(matchs.group(1));
		     			//System.out.println(matchs.group(1));
		     			//System.out.println(_parts.length);
		     			//System.out.println(_parts[0]);
		     			session.setStartdate(_parts[0]);
		     			session.setStartMonth(_parts[1]);
		     			session.setStartYear(_parts[2]);
		     		}else {
		     			String[] _parts=datestring.split(matchs.group(1));
		     			session.setEnddate(_parts[0]);
		     			session.setEndMonth(_parts[1]);
		     			session.setEndYear(_parts[2]);
		     			
		     		}
		     	}else {
		     		System.out.println("Invalid date format. Hence Exiting");
		     		System.exit(201);
		     	}
			
		}
		public static void main(String args[]) {
	    	PlaceholderJsonReader phssr=PlaceholderJsonReader.getInstance();
	    	//phssr.populateAggregationdate();
	    }
	}

interface MapList{
	public List<String> mapfunc(Iterator iterator);
}
