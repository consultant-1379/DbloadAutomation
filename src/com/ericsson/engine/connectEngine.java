package com.ericsson.engine;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import com.distocraft.dc5000.common.RmiUrlFactory;
import com.distocraft.dc5000.common.ServicenamesHelper;
import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;
import com.ericsson.common.LogHandler;
import com.ericsson.eniq.repository.ETLCServerProperties;
import com.ericsson.eniq.scheduler.exception.SchedulerException;

public class connectEngine {

	  public static final String STATUS_EXECUTED = "Executed";

	  public static final String STATUS_FAILED = "Exec failed!";

	  private  LogHandler log = null;

	  private String serverRefName;

	  private String url = "";

	  private static final String SCHEDULER_RMI_PROCESS_PORT = "SCHEDULER_RMI_PROCESS_PORT";
	  
	  private static final String SCHEDULER_RMI_PROCESS_PORT_DEFAULT = "60002";
	  
	  // Time to connect again to the database if it fails
	  private long reConnectTime;

	  private String engineURL;
	  
	  Lock lock = new ReentrantLock(true);
	public connectEngine(LogHandler log2) {
		// TODO Auto-generated constructor stub
		log=log2;
		try {
			loadProperties();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void loadProperties() throws SchedulerException {
		  
		  
		// Create static properties.
	try {
			StaticProperties.reload();
		} catch (IOException e1) {

			log.log(Level.SEVERE, "IOException when loading StaticProperties",
					e1);
		}
		
    String sysPropDC5000 = System.getProperty("dc5000.config.directory");
    if (sysPropDC5000 == null) {
      log.severe("System property dc5000.config.directory not defined");
      throw new SchedulerException("System property dc5000.config.directory not defined");
    }

    if (!sysPropDC5000.endsWith(File.separator)) {
      sysPropDC5000 += File.separator;
    }
   Properties appProps = null ;
    try{	
    	appProps = new ETLCServerProperties(sysPropDC5000 + "ETLCServer.properties");
    }catch(IOException e){
    	throw new SchedulerException("failed to read ETLCServer.Properties",e);
    }
   // Properties appProps = new ETLCServerProperties();
    // Reading DB connection properties

    this.url = appProps.getProperty("ENGINE_DB_URL");
    log.config("Using DB @ " + this.url);

    appProps.getProperty("ENGINE_DB_USERNAME");
    appProps.getProperty("ENGINE_DB_PASSWORD");
    appProps.getProperty("ENGINE_DB_DRIVERNAME");
    Integer.parseInt(appProps.getProperty(SCHEDULER_RMI_PROCESS_PORT,SCHEDULER_RMI_PROCESS_PORT_DEFAULT));
    

    
    // get the hostname by service name and default to localhost.
    String hostNameByServiceName = null ;
    try{
  	  hostNameByServiceName = ServicenamesHelper.getServiceHost("scheduler", "localhost");
    }catch(final Exception e){
  	  hostNameByServiceName = "localhost" ;
    }
	appProps.getProperty("SCHEDULER_HOSTNAME", hostNameByServiceName);

    // Reading engine connection properties
	
    // get the engine hostname by service name and default to localhost.
    String engHostNameByServiceName = null ;
    try{
    	engHostNameByServiceName = ServicenamesHelper.getServiceHost("engine", "localhost");
    }catch(final Exception e){
    	engHostNameByServiceName = "localhost" ;
    }
    String engineServerHostName = appProps.getProperty("ENGINE_HOSTNAME", engHostNameByServiceName);

    int engineServerPort = 1200;
    final String engineSporttmp = appProps.getProperty("ENGINE_PORT", "1200");
    try {
      engineServerPort = Integer.parseInt(engineSporttmp);
    } catch (NumberFormatException nfe) {
      log.config("Using default ENGINE_PORT 1200.");
    }

    String engineServerRefName = appProps.getProperty("ENGINE_REFNAME", null);
    if (engineServerRefName == null) {
      log.config("Using default ENGINE_REFNAME \"TransferEngine\"");
      engineServerRefName = "TransferEngine";
    }

    this.engineURL = "rmi://" + engineServerHostName + ":" + engineServerPort + "/" + engineServerRefName;

    log.config("Engine RMI Reference is: " + engineURL);

    final String sporttmp = appProps.getProperty("SCHEDULER_PORT", "1200");
    try {
      Integer.parseInt(sporttmp);
    } catch (NumberFormatException nfe) {
      log.config("Using default SCHEDULER_PORT 1200.");
    }

    this.serverRefName = appProps.getProperty("SCHEDULER_REFNAME", null);
    if (this.serverRefName == null) {
      log.config("Using default SCHEDULER_REFNAME \"Scheduler\"");
      this.serverRefName = "Scheduler";
    }

    final String pollIntervall = appProps.getProperty("SCHEDULER_POLL_INTERVALL");
    if (pollIntervall != null) {
      Long.parseLong(pollIntervall);
    }
    log.config("Using pollInterval " + pollIntervall);

    final String penaltyWait = appProps.getProperty("SCHEDULER_PENALTY_WAIT", "30");
    if (penaltyWait != null) {
      Integer.parseInt(penaltyWait);
    }
    log.config("Using penaltyWait " + penaltyWait);

    try {
      this.reConnectTime = new Long(appProps.getProperty("SERVER_RECONNECT_TIME")).longValue();
    } catch (Exception e) {
      this.reConnectTime = 60000;
      log.config("Using default reconnect time " + this.reConnectTime);
    }

    log.log(Level.CONFIG, "Properties loaded");

	}

	public ITransferEngineRMI Engineconnect() throws SchedulerException {
			log.info("Pulling instance of engine");
			ITransferEngineRMI engine = null;
			try {
			
			engine = (ITransferEngineRMI) Naming.lookup(RmiUrlFactory.getInstance().getEngineRmiUrl());

			// Wait until engine has been initialized.
			
				while (!engine.isInitialized() || (!engine.isCacheRefreshed())) {
					engine = (ITransferEngineRMI) Naming.lookup(RmiUrlFactory.getInstance().getEngineRmiUrl());
					log.fine("Waiting for the engine to initialize before starting scheduling.");
					Thread.sleep(1000);
				}
			} catch (RemoteException | MalformedURLException | NotBoundException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
			log.info("Connected to ETLC engine");
			return engine;
		}
}
