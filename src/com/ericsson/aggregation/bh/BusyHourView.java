package com.ericsson.aggregation.bh;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.scheduler.ISchedulerRMI;
import com.distocraft.dc5000.etl.scheduler.SchedulerConnect;
import com.ericsson.aggregation.config.PlaceholderJsonReader;
import com.ericsson.aggregation.config.PlaceholderPojo;
import com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultKey;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultSource;
import com.ericsson.eniq.busyhourcfg.data.vo.Key;
import com.ericsson.eniq.busyhourcfg.data.vo.MappedType;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.Source;
import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdater;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdaterFactory;
import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.database.TechPackReader;
import com.ericsson.eniq.busyhourcfg.database.TechPackReaderFactory;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

public class BusyHourView {
	
	public void serviceImitations(RockSession session) throws Exception {
	        String selectedVersionId = (String) session.getVersionId();
	        final DatabaseSession databaseSession = (DatabaseSession) session.getDatabaseSession();
	        final TechPackReader reader = TechPackReaderFactory.getTechPackReader(databaseSession);
	        final DWHStorageTimeAction dwhStorageTimeAction=session.getDwhStorageTimeAction();
	        TechPack currentTechPack = reader.getTechPackByVersionId(selectedVersionId);
	        List<String> basetables=reader.getAllBusyhourBasetables(selectedVersionId);
	        String basehour="DC_AF_5GC_NRFBH";
	        String PlaceholderName="CP2";
	        String tablename="DC_AF_5GC_SMFCAUSESNSSAI_RAW";
	        for(String basetable:basetables) {
	        	System.out.println("BaseTable "+basetable);
	        }
	        
	        if (currentTechPack == null) {
	            throw new Exception("Can not read busyhour information from database");
	          }
	        session.setSourcetp(currentTechPack);
	     // currently target techpack is always same as  source techpack
	        session.setTargettp(currentTechPack);
	        List<TargetTechPack> ttplist=currentTechPack.getTargetTechPacks();
	        
	        for(TargetTechPack ttp:ttplist) {
	        	System.out.println("TargetTechPack is"+ ttp.getVersionId());
	        	List<BusyhourSupport>bhslist=ttp.getBusyhourSupports();
	        	for(BusyhourSupport bhs:bhslist) {
	        		System.out.println("Busyhourlvel is"+ bhs.getBhlevel());
	        		List<Placeholder> phlist=bhs.getPlaceholders();
	        	    if(bhs.getBhlevel().equals(basehour)) {
	        	    	for(Placeholder ph:phlist) {
	        	    		if(ph.getBhtype().equals(PlaceholderName)) {
	        	    			ph.setDescription("Praveen_auto_test");
	        	    			DefaultSource ds=new DefaultSource(tablename);
	        	    			List<Source> sourcelist=new ArrayList<Source>();
	        	    			sourcelist.add(ds);
	        	    			ph.setSources(sourcelist);
	        	    	         final BusyhourUpdater busyhourUpdater = BusyhourUpdaterFactory.getBusyhourUpdater(databaseSession, dwhStorageTimeAction);
	        	    	         busyhourUpdater.generateBusyhourrankkeys(ph);
	        	    	         List<MappedType>maptypelist=ph.getMappedTypes();
	        	    	         List<Key> keylist=ph.getKeys();
	        	    	         for(Key key:keylist) {
	        	    	        	 System.out.println("Keys  "+key.getKeyName()+" "+key.getKeyValue());
	        	    	         }
	        	    	         for(MappedType maps:maptypelist) {
	        	    	        	 System.out.println("MappedTypes  "+maps.getBhTargettype());
	        	    	         }
	        	    			System.out.println("PlaceHolder criteria "+ph.getDescription()+" "+ph.getSources());
	        	    			String errormessage = busyhourUpdater.validatePlaceholder(ph);
	        	    			if(errormessage.equals("")) {
	        	    				busyhourUpdater.savePlaceholder(ph);
	        	    			}else {
	        	    				System.out.println("Error  ");
	        	    			}
	        	    
	        	    			break;
	        	    		}
	        	    	}
	        	    }
	        	}
	        }
	}
	public void loadconfig(TechPack tp) {
		
	}
	public void populateconfig(RockSession session) {
		try {
			StaticProperties.reload();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String selectedVersionId = (String) session.getVersionId();
		List<PlaceholderPojo> errorpojos= new ArrayList<PlaceholderPojo>();
        final DatabaseSession databaseSession = (DatabaseSession) session.getDatabaseSession();
        final TechPackReader reader = TechPackReaderFactory.getTechPackReader(databaseSession);
        final DWHStorageTimeAction dwhStorageTimeAction=session.getDwhStorageTimeAction();
        TechPack currentTechPack = reader.getTechPackByVersionId(selectedVersionId);
        List<String> basetables=reader.getAllBusyhourBasetables(selectedVersionId);
        PlaceholderJsonReader phsr=PlaceholderJsonReader.getInstance();
        List<PlaceholderPojo> placeholderpojos=PlaceholderJsonReader.getList();
        System.out.println("[INFO]::Placeholderpojos size "+placeholderpojos.size());
        session.setSourcetp(currentTechPack);
     // currently target techpack is always same as  source techpack
        session.setTargettp(currentTechPack);
        List<TargetTechPack> ttplist=currentTechPack.getTargetTechPacks();
        final BusyhourUpdater busyhourUpdater = BusyhourUpdaterFactory.getBusyhourUpdater(databaseSession, dwhStorageTimeAction);
        
        for(TargetTechPack ttp:ttplist) {
        	System.out.println("[INFO]::TargetTechPack is"+ ttp.getVersionId());
        	BusyhourSupport bhs=null;
        	for(PlaceholderPojo placeholderpojo:placeholderpojos) {
        		bhs=ttp.getBusyhourSupportByBhlevel(placeholderpojo.getBhlevel());
        		if(bhs !=null) {
        			Placeholder ph=bhs.getPlaceholderByBhtype(placeholderpojo.getBhtype());
        			if(ph !=null) {
        				ph.setDescription(placeholderpojo.getDescription());
        				
        				try {
        					ph.setSources(getTables(placeholderpojo.getSources()));
							busyhourUpdater.generateBusyhourrankkeys(ph);
							ph.setWhere(placeholderpojo.getWhereCondition());
							ph.setCriteria(placeholderpojo.getFormula());
							ph.setKeys(getKeys(placeholderpojo.getKeys()));
							ph.setEnabled(true);
							ph.setReactivateviews(1);
							
							busyhourUpdater.validatePlaceholder(ph); 
							busyhourUpdater.savePlaceholder(ph);
							
						} catch (DatabaseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        			}else {
        				System.out.println("[INVALID]::Not valid placeholder "+placeholderpojo.getBhtype());
        				errorpojos.add(placeholderpojo);
        			}
        		}else {
        			System.out.println("[INVALID]::Not valid placeholder "+placeholderpojo.getBhlevel());
        			errorpojos.add(placeholderpojo);
        		}
        	}
        	
        }
        try {
        	System.out.println("[INFO]::Recreating views");
			busyhourUpdater.recreateViews(currentTechPack);
	         //busyhourUpdater.recreateViews(techPack);
			System.out.println("[INFO]::Recreated views");
	          ISchedulerRMI sched = SchedulerConnect.connectScheduler();
	          sched.reload();
		} catch (DatabaseException | IOException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private List<Key> getKeys(ArrayList<String> keynames) {
		List<Key> keys=new ArrayList<Key>();
		Pattern pattern=Pattern.compile(".+\\..+");
		for(String keyname:keynames) {
			Matcher matcher=pattern.matcher(keyname);
			System.out.println("[INFO]::keyname "+keyname);
			if(matcher.matches()) {
				String[] _parts=keyname.split("\\.");
				//System.out.println("_parts are size "+_parts.length);
				DefaultKey key=new DefaultKey(_parts[1],_parts[0],_parts[1]);
				keys.add(key);
			}
		}
		return keys;
	}
	private List<Source> getTables(ArrayList<String> sourceslist) {
		// TODO Auto-generated method stub 
		List<Source> sources=new ArrayList<Source>();
		for(String sourcename:sourceslist) {
			DefaultSource source=new DefaultSource(sourcename);
			sources.add(source);
		}
		return sources;
	}

}
