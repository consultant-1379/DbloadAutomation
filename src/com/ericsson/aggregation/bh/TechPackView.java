package com.ericsson.aggregation.bh;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.aggregation.Reaggregation;
import com.ericsson.aggregation.config.PlaceholderJsonReader;
import com.ericsson.eniq.busyhourcfg.config.BusyhourConfigurationFactory;
import com.ericsson.eniq.busyhourcfg.config.BusyhourEnvironment;
import com.ericsson.eniq.busyhourcfg.config.BusyhourEnvironmentFactory;
import com.ericsson.eniq.busyhourcfg.config.BusyhourProperties;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.DatabaseConnector;
import com.ericsson.eniq.busyhourcfg.database.DatabaseConnectorFactory;
import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.database.TechPackReader;
import com.ericsson.eniq.busyhourcfg.database.TechPackReaderFactory;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeActionFactory;
import com.ericsson.eniq.busyhourcfg.exceptions.BusyhourConfigurationException;
import com.ericsson.eniq.busyhourcfg.exceptions.DWHManagerException;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;
import com.ericsson.eniq.busyhourcfg.licensing.LicenseChecker;
import com.ericsson.eniq.busyhourcfg.licensing.LicenseCheckerFactory;
import com.ericsson.eniq.repository.ETLCServerProperties;

public class TechPackView {
	
	private static final String CXC_NUMBER = "CXC4010932";	


public void PostImitation(RockSession session) throws IOException{
          ETLCServerProperties etlcServerProperties = null;
          BusyhourProperties busyhourProperties = null;
          final BusyhourEnvironment busyhourEnvironment = BusyhourEnvironmentFactory.getBusyhourEnvironment();
          final String folder = System.getProperty("CONF_DIR");
		final String filename = "ETLCServer.properties";
		final String filepath = File.separator + folder + File.separator + filename;
		etlcServerProperties = new ETLCServerProperties(filepath);
		busyhourProperties = new BusyhourProperties("/eniq/sw/runtime/tomcat/webapps/adminui/busyhourcfg/conf/busyhourcfg.properties");
		System.out.println("Configurations read successfully");
          
          final LicenseChecker lc = LicenseCheckerFactory.getLicenseChecker(etlcServerProperties);
          if (lc.isLicenseValid(CXC_NUMBER)) {
            DatabaseSession databaseSession = null;
            DWHStorageTimeAction dwhStorageTimeAction = null;
            try {
              if (busyhourProperties != null) {
                final DatabaseConnector databaseConnector = DatabaseConnectorFactory.getDatabaseConnector(etlcServerProperties, busyhourProperties);
                try{
	                databaseSession = databaseConnector.createDatabaseSession(DatabaseConnectorFactory.getApplicationName());
	                System.out.println("[INFO]::Database session created successfully");
	                try {
	                  dwhStorageTimeAction = DWHStorageTimeActionFactory.getDWHStorageTimeAction(databaseSession);
	                } catch (DWHManagerException e) {
	                  System.out.println(e.getMessage());
	                }
                }finally{
                	DatabaseConnectorFactory.setDatabaseConnector(null);
                }
              }
            } catch (DatabaseException e) {
            	System.out.println(e.getMessage());
            }
            session.setDatabaseSession(databaseSession);
            System.out.println("[INFO]::session  databasesession saved");
            session.setDwhStorageTimeAction(dwhStorageTimeAction);
            System.out.println("[INFO]::session dwhstoragetimeaction saved");
          } else {
            // do not allow the user to log in since the license is invalid!
            System.out.println("[INFO]::License validation failed!");
           
          }
        } 
    public void selectTechPacks(RockSession session,String techpack,boolean bhflag) throws Exception {
    	final DatabaseSession databaseSession = (DatabaseSession) session.getDatabaseSession();
       // final TechPack selectedTechPack = (TechPack) session.getAttribute("sourcetp");
    	String versionid=null;
        final TechPackReader reader = TechPackReaderFactory.getTechPackReader(databaseSession);
        session.setTechpackreader(reader);
        Pattern pattern=Pattern.compile("[Dd]{1}[Cc]{1}_[a-zA-Z0-9]+_[a-zA-Z0-9]+:*\\(*\\(*[0-9]*\\)*\\)*");
        Matcher match=pattern.matcher(techpack);
        List<String> versionids = reader.getAllActivatedPMTypeVersionIds();
        for ( String versionidd : versionids) {
        	System.out.println("[INFO]::Inside loop "+techpack+" "+versionidd);
        	if(match.matches()) {
        		if(versionidd.contains(techpack.toUpperCase())) {
        			System.out.println("[INFO]::Pattern matched "+techpack+" "+versionidd);
        			versionid=versionidd;
        			break;
        			//System.out.println("<option value=" + versionid + " selected>" + versionid + "</option>");
        		}
        	}
        }
        session.setVersionId(versionid);
        if(bhflag) {
        	BusyHourView bhv=new BusyHourView();
        	bhv.populateconfig(session);
        }
        PlaceholderJsonReader phjr=new PlaceholderJsonReader();
        phjr.populateAggregationdate(session);
        Reaggregation reg=new Reaggregation();
        reg.HandleRequest(session);
        
    }
    
}


