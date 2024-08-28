package com.ericsson.aggregation.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonWriter;

import com.ericsson.eniq.busyhourcfg.data.vo.Key;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.Source;

public class PlaceholderJsonWriter {
	String jsondirectory=System.getProperty("java.basedir");
	public void writer(Placeholder placeholder,String bhlevel) {
		File file= new File(jsondirectory+"Placeholderconfig");
		while(true) {
			if(file.exists()) {
				if(file.isDirectory()) {
					break;
				}else if(file.isFile()) {
					System.out.println("File found for the same name. Hence deleting");
					if(!file.delete()) {
						System.out.println("Error while deleting a file.Hence Exiting");
						System.exit(200);
					}
				}
			}else {
				if(!file.mkdir()) {
					System.out.println("Error while creating a directory.Hence Exiting");
					System.exit(201);
				}else {
					break;
				}
			}
		}
		File filejson=new File(file.getAbsolutePath()+"/"+bhlevel+"_"+placeholder.getBhtype()+".json");
		FileOutputStream wout;
		try {
			JsonArrayBuilder source_tables=Json.createArrayBuilder();
			for(Source source:placeholder.getSources()) {
				source_tables.add(source.getTypename());
			}
			//source_tables.build();
			JsonArrayBuilder Keys=Json.createArrayBuilder();
			for(Key key:placeholder.getKeys()) {
				Keys.add(key.getSource()+"."+key.getKeyName());
			}
			
			wout = new FileOutputStream(filejson);
			JsonWriter jsonobj=Json.createWriter(wout);
			JsonObjectBuilder structure = Json.createObjectBuilder()
		            .add("BHLevel", bhlevel)
		            .add("BHType", placeholder.getBhtype())
		            .add("Description", placeholder.getDescription())
					.add("whereCondition",placeholder.getWhere())
					.add("formula", placeholder.getCriteria())
					.add("source_tables", source_tables)
					.add("Keys",Keys);
					//.add("source_tables", Json.createArrayBuilder(placeholder.getSources().stream().map(Source::getTypename).collect(Collectors.toList())))
					//.add("Keys",Json.createArrayBuilder(placeholder.getKeys().stream().map(Key-> Key.getSource()+"."+Key.getKeyValue()).collect(Collectors.toList())));
			jsonobj.writeObject(structure.build());
			jsonobj.close();
			try {
				wout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//structure.
		    /*fromStructure = jsonbBuilder.toJson(structure);

		    JsonPointer pointer = Json.createPointer("/profiles");
		    JsonValue value = pointer.getValue(structure);
		    fromJpointer = value.toString();
			jsonobj.write(arg0);*/
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
