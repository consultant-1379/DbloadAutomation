package com.ericsson.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Callable;

import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.ericsson.common.FileHelper;
import com.ericsson.db.repoint.MetaTransferActionsProps;
import com.ericsson.main.Main;
import com.ericsson.main.Utility;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.main.EngineThread;
public class InterfaceThread implements Callable<Boolean>{
	private String name="";
	private Properties prop=null;
	private ArrayList<File> files=null;
	private Utility util=null;
	private HashMap<String,MetaTransferActionsProps> setidtometacachemap=null;
	ITransferEngineRMI enginermi=null;
	private FileHelper fh=null;
	public InterfaceThread(String threadname,Properties prop) {
		this.name=threadname;
		this.prop=prop;
		this.setidtometacachemap=Main.setidtometacachemap;
		util =new Utility();
		fh=new FileHelper();
	}
	public InterfaceThread(String intfname, Properties prop, ArrayList<File> files) {
		this(intfname,prop);
		this.setidtometacachemap=Main.setidtometacachemap;
		this.files=files;
		util =new Utility();
		this.files=files;
		fh=new FileHelper();
	}
	public void setEnginermi(ITransferEngineRMI enginermi) {
		this.enginermi = enginermi;
	}
	public Boolean call() throws Exception {

		RockFactory etlrep=Main.dbconmap.get("etlrock");
		MetaTransferActionsProps mtap=setidtometacachemap.get(name);
		Vector<Meta_collections> Meta_collecCache=mtap.getMeta_collecCache();
		Iterator<Meta_collections> collectionIteratror=Meta_collecCache.iterator();
		while(collectionIteratror.hasNext()) {
			Meta_collections metacollec= collectionIteratror.next();
			if(metacollec.getCollection_name().contains("Adapter")){
				//files = (ArrayList<File>) fh.validateFile(name);
				String destdir=util.resolveFilePath(prop.getProperty("inDir"));
				System.out.println("[INFO]::Copying files to parser In directory "+destdir+"/testdir");
				File indir=null;
				if(!fh.createDirectory(destdir+"/testdir")) {
					System.out.println("[ERROR]::Failed to create In directory, Exiting");
					System.exit(600);
				}else {
					indir=new File(destdir+"/testdir");
				}
				
				for(File file:files) {
					fh.copyFileUsingStream(file, new File(indir.getAbsolutePath()+"/"+file.getName()));
				}
				System.out.println("[INFO]::Parsertype found is "+ prop.getProperty("parserType")+" condition is "+(prop.getProperty("parserType").contains("mdc")||prop.getProperty("parserType").contains("ascii")) );
				if(prop.getProperty("parserType").contains("mdc")||prop.getProperty("parserType").contains("ascii")) {
					System.out.println("[INFO]:: "+prop.getProperty("parserType")+" parser found hence waiting for 1 minutes before triggering adapter");
					Thread.sleep(60000);
				}
				System.out.println("[INFO]::Transfering the request to engine for Adapater execution");
			}else {
				System.out.println("[INFO]::Transfering the execution request to engine for collection :"+metacollec.getCollection_name());
			}
			enginermi.execute(etlrep.getDbURL(), etlrep.getUserName(), etlrep.getPassword(), etlrep.getDriverName(), 
					name,metacollec.getCollection_name(), null);
		}
		//String indir=util.resolveFilePath(prop.getProperty("inDir"));
		
		return true;
	}

}
