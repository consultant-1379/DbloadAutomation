package com.ericsson.main;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.RowSetMetaData;

import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.aggregation.bh.RockSession;
import com.ericsson.aggregation.bh.TechPackView;
import com.ericsson.aggregation.config.Placeholderconsoleintegrator;
import com.ericsson.common.FileHelper;
import com.ericsson.common.LogHandler;
import com.ericsson.config.Readcounterconfig;
import com.ericsson.config.StatusConfigPOJO;
import com.ericsson.db.repoint.IntfDbCache;
import com.ericsson.db.repoint.LogsessionAdapter;
import com.ericsson.db.repoint.LogsessionAdapterFactory;
import com.ericsson.db.repoint.MetaTransferActionsProps;
import com.ericsson.db.repoint.Queries;
import com.ericsson.engine.InterfaceThread;
import com.ericsson.engine.connectEngine;
import com.ericsson.eniq.common.DatabaseConnections;
import com.ericsson.eniq.scheduler.exception.SchedulerException;
import com.ericsson.report.ConsoleReporter;

import ssc.rockfactory.RockFactory;

public class Main {
	public static LogHandler log=LogHandler.getInstance();
	//public static Logger log=Logger.getLogger(Main.class.getName());
	public static HashMap<String,RockFactory> dbconmap =new HashMap<String,RockFactory>();
	public static Object lock=new Object();
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE = "\u001B[34m";
	private final static String classname=Main.class.getName();
	private static Set<String> activeInterface=new HashSet<String>();
	private static HashMap<String,Properties> parserconfs=null;
	private static ArrayList<String> g_tables=null;
	private static HashMap<String, StatusConfigPOJO> counterstatusMap=null;
	private static HashMap<String, StatusConfigPOJO> counterstatusMap1=null;
	private static HashMap<String, Boolean> notnullcounters=null;
	public static HashMap<String,ArrayList<File>> intffiles=new HashMap<String,ArrayList<File>>();
	public static HashMap<String,Callable> intfThreads= new HashMap<String,Callable> ();
	public static HashMap<String,MetaTransferActionsProps> setidtometacachemap=new HashMap<String,MetaTransferActionsProps>();
	
	private static void filldbconmap() {
		// TODO Auto-generated method stub
		synchronized(lock){
			if(dbconmap !=null) {
				dbconmap.clear();
				RockFactory etlrock = DatabaseConnections.getETLRepConnection();
				RockFactory dwhreprock = DatabaseConnections.getDwhRepConnection();
				RockFactory dwhdb = DatabaseConnections.getDwhDBConnection();
				dbconmap.put("etlrock",etlrock);
				dbconmap.put("dwhreprock",dwhreprock);
				dbconmap.put("dwhdb",dwhdb);
				validatedbconmap();
			
			}else {
				RockFactory etlrock = DatabaseConnections.getETLRepConnection();
				RockFactory dwhreprock = DatabaseConnections.getDwhRepConnection();
				RockFactory dwhdb = DatabaseConnections.getDwhDBConnection();
				dbconmap.put("etlrock",etlrock);
				dbconmap.put("dwhreprock",dwhreprock);
				dbconmap.put("dwhdb",dwhdb);
				validatedbconmap();
			}
		}
		
	}
	private static void validatedbconmap() {
		// TODO Auto-generated method stub
		if((dbconmap !=null) && (dbconmap.keySet().size()==3)) {
			log.log(Level.INFO, "db connection successfully validated");
			for(String s:dbconmap.keySet()){
				log.log(Level.FINEST, "The db connections in pool are "+s);
			}
		}else {
			log.log(Level.INFO, "db connections failed to validated");
		}
	}
	public static ArrayList<String> getTablesforInterface(String interfaceName) {
		ArrayList<String> tables= new ArrayList<String>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Pattern pattern=Pattern.compile(".*(:\\(\\([0-9]{1,2}\\)\\):).*:(.*)");
		int tablesno=0;
		String sql=Queries._get_interfaces_sql("dwhrep.",interfaceName);
		//System.out.println("sql  being executed :-"+sql);
		con=dbconmap.get("dwhreprock").getConnection();
		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				//System.out.println(rs.getNString(4));
				String dataformatid=rs.getNString(3);
				String versionid=null;
				String parsertype=null;
				String foldername=rs.getNString(4);
				if(dataformatid!= null){
					Matcher match=pattern.matcher(dataformatid);
					if(match.find())
						versionid=match.group(1);
						parsertype=match.group(2);
						log.log(Level.FINEST, classname+": versionid found is "+versionid+" for foldername "+foldername+"");
				}else {
					log.log(Level.WARNING, classname+": Dataformatid was not found "+foldername);
				}
				System.out.println("[INFO]::tablename added "+versionid+","+parsertype+","+foldername+"_RAW");
				tables.add(versionid+","+parsertype+","+foldername+"_RAW");
				tablesno++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			con=null;
			log.log(Level.WARNING, classname+": tables fetching query thrown error"+e.getMessage());
			
			
		}
		if(tablesno < 0) {
			System.out.println("[ERROR]::No data found for the TechPack. Hence skipping and please check");
			System.exit(400);
		}
		con=null;
		return tables;
	}
	public static void main(String[] args) throws IOException {
		System.out.println("[INFO]::Start123456789");
		//System.out.println("Start");
		 //Commenting the below code to test BH_automation
		String argone="";
		 
		String interfacename=null;
		if(args.length>0) {
			//interfacename=;
			interfacename=validateTechPackName(args[0]);
			System.out.println("TechPackName "+interfacename);
		}else {
			System.out.println("[ERROR]::TechPack Name is needed.Exiting");
        	System.exit(300);
		}
		ITransferEngineRMI enginermi=null;
		if(args.length>1)argone=args[1];
		if(!argone.equalsIgnoreCase("Config")) {
		
		ConsoleReporter cr=new ConsoleReporter();
		loadvalidationconfig();
		
		FileHelper fh=new FileHelper();
		
        filldbconmap();
       
       // disabled
        g_tables=getTablesforInterface(interfacename);
        
        LoaddbCache(g_tables,interfacename,false);
       
		
        setidtometacachemap=getPropsforParseAction(interfacename);
        loadProperties();
        if(parserconfs.keySet().size()<=0) {
        	System.out.println("[ERROR]::No valid parser actions found for TechPack.Hence Exiting");
        	System.exit(300);
        }
        System.out.println("[INFO]::iterating through intf vector");
        activeInterface.iterator();
        ExecutorService executorService=Executors.newFixedThreadPool(parserconfs.keySet().size()+1);
        HashMap<String,Future<Boolean>> futuremap=new HashMap<String,Future<Boolean>>();
        log.log(Level.WARNING, classname+": -connectengine ");
        
        connectEngine connectengine=new connectEngine(log);
			try {
				enginermi=connectengine.Engineconnect();
			} catch (SchedulerException e) {
				System.out.println("[ERROR]::Engine connect failure");
				log.log(Level.SEVERE,"Engine connect failure", e);
				System.exit(200);
			}
		
        for(String intfname:parserconfs.keySet()) {
        	Properties prop=parserconfs.get(intfname);
        	System.out.println("[INFO]::iterating through intf vector inside "+intfname);
        	ArrayList<File> files=new ArrayList<File>();
        	files=(ArrayList<File>) fh.validateFile(intfname);
        	if(files.size()>0) {
        		InterfaceThread intfThread=new InterfaceThread(intfname,prop,files);
        		intfThread.setEnginermi(enginermi);
        		intfThreads.put(intfname,intfThread);
        		intffiles.put(intfname, files);
        	}
        	
        }
        for(String intfname:intffiles.keySet()) {
        	Future<Boolean> future=executorService.submit(intfThreads.get(intfname));
        	futuremap.put(intfname, future);
        }
        for(String Threadname:futuremap.keySet()) {
        	System.out.println("[INFO]::Retriving the future");
        	Future<Boolean> future=futuremap.get(Threadname);
        	try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
        }
        
        //Verify the files are loaded
        Verifydbload(intffiles,enginermi);
        /* disabling as it was fetched from the log session
        LoaddbCache(tables,interfacename,true);
        
        System.out.println("Start");
        cr.readCache();
        */
		
        cr.printCounterstatus(counterstatusMap1);
        cr.printnonnullcolumns(notnullcounters);
        System.out.println("[INFO]:: End of execution");
        executorService.shutdown();
        //exit(0);
       
        //commented for BH automation testing
		}
		if(args.length>1) {
			if(argone.equalsIgnoreCase("Config")) {
				Placeholderconsoleintegrator phci=new Placeholderconsoleintegrator();
				TechPackView tpv=new TechPackView();
				RockSession rs=new RockSession();
				tpv.PostImitation(rs);
				phci.config(rs);
				System.exit(0);
			}
		
			TechPackView tpv=new TechPackView();
			RockSession rs=new RockSession();
			tpv.PostImitation(rs);
			RockFactory etlrep=dbconmap.get("etlrock");
			try {
				if(argone.equalsIgnoreCase("BusyHour")) {
					System.out.println("[INFO]::BusyHour Configurations triggered");
					tpv.selectTechPacks(rs,interfacename,true);
					//enginermi.execute(etlrep.getDbURL(), etlrep.getUserName(), etlrep.getPassword(), etlrep.getDriverName(), 
							//"DWH_MONITOR","AutomaticREAggregation", null);
				}else {
					tpv.selectTechPacks(rs,interfacename,false);
					//enginermi.execute(etlrep.getDbURL(), etlrep.getUserName(), etlrep.getPassword(), etlrep.getDriverName(), 
						//	"DWH_MONITOR","AutomaticREAggregation", null);
				}
			} catch (Exception e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("[INFO]::End of BH Configuration");
			
			
			try {
				System.out.println("[INFO]::Sleeping for 30 seconds");
				Thread.sleep(30000);
				System.out.println("[INFO]::Slept and awake to trigger");
				System.out.println("[INFO]::Triggering automatic reaggregation");
				enginermi.execute(etlrep.getDbURL(), etlrep.getUserName(), etlrep.getPassword(), etlrep.getDriverName(), 
						"DWH_MONITOR","AutomaticREAggregation", null);
				System.out.println("[INFO]::Successfully triggering automatic reaggregation");
				validateAggregation(rs,args[0]);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		}
		
    private static String validateTechPackName(String TechPackName) {
    	if(TechPackName.toUpperCase().startsWith("INTF")) {
    		Pattern pattern=Pattern.compile("(INTF_)([Dd]{1}[Cc]{1}_[a-zA-Z0-9]+_[a-zA-Z0-9]+.*)");
    		Matcher match=pattern.matcher(TechPackName);
    		if(match.find()) {
    			return match.group(2);
    		}
    	}
    	return TechPackName;
    }
	private static void validateAggregation(RockSession rs, String techpack) {
		String startdate=rs.getStartYear()+"-"+rs.getStartMonth()+"-"+rs.getStartdate();
		String enddate=rs.getEndYear()+"-"+rs.getEndMonth()+"-"+rs.getEnddate();
		Pattern pattern=Pattern.compile("([Dd]{1}[Cc]{1}_[a-zA-Z0-9]+_[a-zA-Z0-9]+):*\\(*\\(*[0-9]*\\)*\\)*");
        Matcher match=pattern.matcher(techpack);
        String Aggregationtype=null;
        if(match.find()) {
        	Aggregationtype=match.group(1);
        }
		int Numberofruns=-1;
		int Manualcount=0;
		do {
			Numberofruns++;
			filldbconmap();
			RockFactory dwhdb=dbconmap.get("dwhdb");
			Connection con = null;
   			PreparedStatement ps = null;
   			ResultSet rs1 = null;
   			String sql=Queries._get_log_aggregationStatus(Aggregationtype, "MANUAL", startdate, enddate);
   			log.log(Level.INFO, classname+":sql query for Log_Aggregation status query and status MANUAL:"+sql);
   			con=dwhdb.getConnection();
   			FileHelper fh=new FileHelper();
   			try {
   				ps = con.prepareStatement(sql);
   				rs1 = ps.executeQuery();
   				while(rs1.next()) {
   					Manualcount++;
   					break;
   				}
   				rs1.close();
   				ps.close();
   				if(Manualcount>0&&Numberofruns<21) {
   					System.out.println("[INFO]:Aggregation on going. Waiting");
   					System.out.println("[INFO]:Pending Aggregation. "+Manualcount);
   					Manualcount=0;
   					Thread.sleep(3000);
   				}else if(Numberofruns>21){
   					System.out.println("[INFO]:Aggregation on going. TimedOut");
   					System.exit(201);
   				}else {
   					try {
   						fh.createFile("csvextraction/AggregationStatus.csv");
   					} catch (IOException e) {
   						// TODO Auto-generated catch block
   						e.printStackTrace();
   					}
   					Thread.sleep(10000);
   					sql=Queries._get_log_aggregationStatus(Aggregationtype, "AGGREGATED", startdate, enddate);
   					log.log(Level.INFO, classname+":sql query for Log_Aggregation status query and status AGGREGATED:"+sql);
   					ps = con.prepareStatement(sql);
   	   				rs1 = ps.executeQuery();
   	   				fh.writetextline("Aggregation,Timelevel,Date,Status");
   	   				System.out.println(ANSI_YELLOW_BACKGROUND+ANSI_BLUE+"[HEADER]::Aggregation   Timelevel   Date  Status"+ANSI_RESET);
   	   				while(rs1.next()) {
   	   					String aggregation=rs1.getString(1);
   	   					String timelevel=rs1.getString(3);
   	   					String date=rs1.getDate(4).toString();
   	   					String status=rs1.getString(7);
   	   					fh.writetextline(aggregation+","+timelevel+","+date+","+status);
   	   					System.out.println(ANSI_YELLOW_BACKGROUND+ANSI_BLUE+"[DATA]::"+aggregation+"   "+timelevel+"   "+date+"  "+status+ANSI_RESET);
   	   				}
   	   				fh.closeFile();
   	   				break;
   				}
   			}catch (SQLException e) {
   				
   			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(true);
	}
	private static void loadvalidationconfig() {
		Readcounterconfig rcc=new Readcounterconfig();
		rcc.loadconfig();
		HashMap<String, Set<String>> hsm=rcc.getCountertovalueMap();
		Set<String> set=rcc.getNonnulls();
		System.out.println("[TEST2]::SET "+set);
		counterstatusMap=new HashMap<String,StatusConfigPOJO>();
		counterstatusMap1=new HashMap<String,StatusConfigPOJO>();
		notnullcounters=new HashMap<String,Boolean>();
		for(String countername:hsm.keySet()) {
			System.out.println("[TEST2]::countername "+countername);
			Set<String> values=hsm.get(countername);
			counterstatusMap.put(countername, new StatusConfigPOJO(values));	
		}
		for(String countername:set) {
			System.out.println("[TEST2]::counternameset "+countername);
			notnullcounters.put(countername+"::tmp123", false);
		}
	}
	private static void verifycounterdata(String columnname, String outstring) {
		if(counterstatusMap1!=null) {
			for(String countername:counterstatusMap1.keySet()) {
			
				StatusConfigPOJO statusconfig=counterstatusMap1.get(countername);
				//System.out.println("[TEST]  :"+(statusconfig != null));
				if(statusconfig != null) {
					if(statusconfig.getColumnname().equalsIgnoreCase(columnname)) {
						//System.out.println("[TEST]::countername "+countername);
						//System.out.println("[TEST]::columnname "+columnname);
						//System.out.println("[TEST]::outstring "+outstring);
						//System.out.println("[TEST]::equals "+statusconfig.getValues().contains(outstring));
						if(statusconfig.getValues().contains(outstring)) {
							statusconfig.setValuestatus(outstring,true);
						}
					}
						counterstatusMap1.put(countername,statusconfig);
					}
			}
		}
		if(notnullcounters!=null) {
			if(notnullcounters.containsKey(columnname)) {
				if(outstring.equalsIgnoreCase("")) {
					notnullcounters.put(columnname, true);
				}
			}
		}
		
	}
	private static void Verifydbload(HashMap<String, ArrayList<File>> intffiles, ITransferEngineRMI enginermi) {
		LogsessionAdapterFactory logsessionFactory=null;
		int Numberofruns=-1;
		boolean flag=false;
		do {
			Numberofruns++;
			System.out.println("[INFO]::Refilling the db rocketFactories");
			filldbconmap();
			RockFactory etlrep=dbconmap.get("etlrock");
			System.out.println("[INFO]::Triggering the Logsession Adapter to get the Loadstatus");
			try {
				enginermi.execute(etlrep.getDbURL(), etlrep.getUserName(), etlrep.getPassword(), etlrep.getDriverName(), 
						"DWH_MONITOR","SessionLogLoader_Adapter", null);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			logsessionFactory=new LogsessionAdapterFactory(intffiles,dbconmap.get("dwhdb"));
			HashMap<String,HashMap<String,ArrayList<LogsessionAdapter>>>logsessionFactoryList=logsessionFactory.getLogsessionCache();
			if(logsessionFactoryList != null ) {
				for(String intfname:intffiles.keySet()) {
					int filessize=intffiles.get(intfname).size();
					HashMap<String,ArrayList<LogsessionAdapter>> fileMap=logsessionFactoryList.get(intfname);
					int filemapsize=-1;
					if(fileMap != null)
					filemapsize=fileMap.size();
					if(filessize == filemapsize) {
						System.out.println("[INFO]:: Below will provide the load of the interface grouping filename");
						System.out.println(ANSI_YELLOW_BACKGROUND+ANSI_BLUE+"[HEADER]::InterfaceName   FileName   TableName   Noofrows   Noofcounters"+ANSI_RESET);
						flag=true;
						for(String filename:fileMap.keySet()) {
							ArrayList<LogsessionAdapter> logsessionAdapterList=fileMap.get(filename);
							for(LogsessionAdapter logadapter:logsessionAdapterList) {
								String BACKGROUND_COLOR=null;
								if((logadapter.getStatus()!=null)&&(logadapter.getStatus().equalsIgnoreCase("ok"))&&(logadapter.getTypename()!=null)&&
										(!logadapter.getTypename().equalsIgnoreCase(""))) {
									FileHelper fh=new FileHelper();
									try {
										//System.out.println("[INFO]::The csv extraction");
										fh.createFile("csvextraction/"+logadapter.getTypename()+logadapter.getDatestring()+".csv");
									} catch (IOException e) {
										e.printStackTrace();
									}
									fetchdataitem(logadapter.getTypename(),fh);
									fetchdata(logadapter.getTypename(),logadapter.getNo_of_rows(),logadapter.getDatestring(),fh);
									fh.closeFile();
								}else {
								}
							}

							for(LogsessionAdapter logadapter:logsessionAdapterList) {
								String BACKGROUND_COLOR=null;
								if((logadapter.getStatus()!=null)&&(logadapter.getStatus().equalsIgnoreCase("ok"))&&(logadapter.getTypename()!=null)&&
										(!logadapter.getTypename().equalsIgnoreCase(""))) {
									BACKGROUND_COLOR = "\u001B[42m";
								}else {
									BACKGROUND_COLOR = "\u001B[41m";
								}
								
								System.out.println(BACKGROUND_COLOR+ANSI_BLUE+"[DATA]::"+intfname+"  "+filename+"  "+logadapter.getTypename()+"  "+logadapter.getNo_of_rows()+
										"  "+logadapter.getNo_of_counters()+ANSI_RESET+ANSI_RESET);
							}
						}
						//System.out.println("[INFO]::file to map size matched .hence exiting");
					}else {
						//System.out.println("[INFO]::file to map size not matched .hence waiting");
						if(Numberofruns<=20){
							System.out.println("[INFO]::file to map size not matched .hence waiting");
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}else {
							System.out.println("[INFO]:: file to map size not matched .TimedOut");
							System.exit(121);
						}
					}
				}
				
			}else {
				if(Numberofruns<=20){
					System.out.println("[INFO]:: Loadstatus not found. hence waiting");
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else {
					System.out.println("[INFO]:: Loadstatus not found Please check sessionloader Adapter logs. TimedOut");
					System.exit(121);
				}
			}
		}while(!flag);
			
	}
	private static void fetchdata(String typename, int no_of_rows, String datestring, FileHelper fh) {
		//System.out.println("[TEST2]:: DateString being pushed to query "+datestring);
		HashMap<Integer,String> columnnames=new HashMap<Integer,String>();
		HashMap<Integer,Integer> columntypes=new HashMap<Integer,Integer>();
		boolean dataflag=false;
		int counter=0;
		do {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql=Queries._get_datatablerows(typename, no_of_rows, datestring);
		con=dbconmap.get("dwhdb").getConnection();
		log.log(Level.INFO, classname+":sql query for retriving the data:"+sql);
		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData rsm=rs.getMetaData();
			int columnscount=rsm.getColumnCount();
			for (int i=1;columnscount>=i;i++) {
				columnnames.put(i,rsm.getColumnName(i));
				columntypes.put(i,rsm.getColumnType(i));
			}
			
			while(rs.next()) {
				dataflag=true;
				String datastr="";
				for(Integer columnno:columnnames.keySet()) {
					String columnname=columnnames.get(columnno);
					Integer columntype=columntypes.get(columnno);
					//System.out.println("[TEST2]:: column type"+columntype);
					//System.out.println("[TEST2]:: column name"+columnname);
					String outstring="";
					switch(columntype) {
						case(Types.DATE):Date date=rs.getDate(columnname);
										 outstring="";
										 if(date!=null) {
											 outstring=date.toString();
										 }
										 verifycounterdata(columnname,outstring);
										 datastr=datastr+outstring+",";
										 break;
						case(Types.NVARCHAR):
											outstring=rs.getNString(columnname);
						 					if(outstring==null) {
						 						outstring="";
						 					}
						 					verifycounterdata(columnname,outstring);
						 					datastr=datastr+outstring+",";
						 					break;
						case(Types.VARCHAR):outstring=rs.getNString(columnname);
	 										if(outstring==null) {
	 											outstring="";
	 										}
	 										verifycounterdata(columnname,outstring);
	 										datastr=datastr+outstring+",";
	 										break;
						case(Types.NUMERIC):BigDecimal number=rs.getBigDecimal(columnname);
											
											if(number==null) {
												outstring="";
											}else {
												outstring=number.toString();
											}
											verifycounterdata(columnname,outstring);
											datastr=datastr+outstring+",";
											break;
		 				default:Object object=rs.getObject(columnname);
		 							if(object==null) {
		 								outstring="";
		 							}else {
		 								outstring=object.toString();
		 							}
		 							verifycounterdata(columnname,outstring);
		 							datastr=datastr+outstring+",";
		 							break;
					}
				}
				fh.writetextline(datastr.substring(0, datastr.lastIndexOf(",")));
			}
			if(!dataflag&&counter<=3) {
				try {
					System.out.println("[INFO]:: No data found for sql "+sql+" waiting for 30 seconds");
							Thread.sleep(30000);
							counter++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(counter==3){
				break;
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			con=null;
			log.log(Level.WARNING, classname+": failed to run the query for fetching the data"+e.getMessage());
		
		}
		}while(!dataflag);
	}

	private static void fetchdataitem(String tablename,FileHelper fh) {
		//System.out.println("[TEST2]:: tablename name got"+tablename);
		String likedataformatid=null;
		for(String dataformatid:g_tables) {
			if(dataformatid.contains(tablename)) {
				//System.out.println("[TEST2]:: dateformatid name got"+dataformatid);
				String[] dataformatsegments=dataformatid.split(",");
				String versionid=dataformatsegments[0];
				String ParserType=dataformatsegments[1];
				likedataformatid=versionid+tablename+":"+ParserType;
				break;
				}
			}
	
		
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			String sql=Queries._get_dataitems(likedataformatid);
			con=dbconmap.get("dwhreprock").getConnection();
			log.log(Level.INFO, classname+":sql fetch data:"+sql);
			try {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				String columnheader="";
				String fileheader="";
				while(rs.next()) {
					
					columnheader=columnheader+rs.getNString("DATANAME")+",";
					fileheader=fileheader+rs.getNString("DATAID").replace(",", "|")+",";
					log.log(Level.INFO, classname+":"+rs.getNString("DATAID")+"columnameName "+rs.getNString("DATANAME"));
					for(String countername:counterstatusMap.keySet()) {
					//System.out.println("[TEST2]::"+rs.getNString("DATAID")+"columnameName "+countername);
						StatusConfigPOJO tmpstatusobject=counterstatusMap.get(countername);
						
						//counterstatusMap1.put(countername+":"+tablename,statusobject);
						if(countername.contains(rs.getNString("DATAID"))) {
						//System.out.println("[TEST2]::"+rs.getNString("DATAID")+"columnameMap"+rs.getNString("DATANAME"));
							//StatusConfigPOJO statusobject=counterstatusMap.get(countername).;
							StatusConfigPOJO statusobject=new StatusConfigPOJO(tmpstatusobject.getValues());
							statusobject.setTablename(tablename);
							statusobject.setColumnno(rs.getInt("COLNUMBER"));
							statusobject.setColumnname(rs.getNString("DATANAME"));
							counterstatusMap1.put(countername+":"+tablename,statusobject);
						}
					
				}
				Set<String> nullcounternames=new HashSet<String>();
				nullcounternames.addAll(notnullcounters.keySet())	;
				
				for(String nullcountername:nullcounternames) {
					//System.out.println("[TEST2]::"+rs.getNString("DATAID")+"columnameName "+nullcountername);
					if(nullcountername.split("::")[0].equals(rs.getNString("DATAID"))) {
						//System.out.println("[TEST2]::"+rs.getNString("DATAID")+"columnameMap"+rs.getNString("DATANAME"));
						notnullcounters.remove(nullcountername);
						notnullcounters.put(rs.getNString("DATANAME"), false);
					}
				}
				}
			
			if(columnheader.contains(",")&&fileheader.contains(",")) {
				//System.out.println("[TEST]"+columnheader.substring(0, columnheader.lastIndexOf(",")));
				//System.out.println("[TEST]"+fileheader.substring(0, fileheader.lastIndexOf(",")));
				
				fh.writetextline(fileheader.substring(0, fileheader.lastIndexOf(",")));
				fh.writetextline(columnheader.substring(0, columnheader.lastIndexOf(",")));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			con=null;
			log.log(Level.WARNING, classname+": failed to run the query for getting dataitem"+e.getMessage());
		
		}
		
		
	}
	private static void exit(int i) {
		// TODO Auto-generated method stub
		System.out.println("Shutting down");
		System.exit(30);
		
	}
	private static void LoaddbCache(ArrayList<String> atables, String interfacename, boolean isloaded) {
		ArrayList<String> tables=atables;
		if(isloaded) {
			filldbconmap();
			int beforetablestorow=IntfDbCache.getBeforecallAdapter().get(interfacename).size();
			HashMap<String,Integer> tablestorow=fetchtablerowcountdata(tables);
			while(tablestorow.size() != beforetablestorow) {
				System.out.println("Not all the views are available waiting +"+beforetablestorow+"-->"+tablestorow.size());
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tablestorow=fetchtablerowcountdata(tables);
			}
			IntfDbCache.setAftercallAdapter(interfacename, tablestorow);
		}else {
			HashMap<String,Integer> tablestorow=fetchtablerowcountdata(tables);
			IntfDbCache.setBeforecallAdapter(interfacename, tablestorow);
		}
		
	}
	
	private static void loadProperties() {
		parserconfs=new HashMap<String,Properties>();
		for(String InterfaceName:setidtometacachemap.keySet()) {
        	MetaTransferActionsProps mtp=setidtometacachemap.get(InterfaceName);
        		if(mtp.isActionfound()) {
        			System.out.println("[INFO]::Iterating the metaActions");
        			HashMap<Long, Vector<Meta_transfer_actions>> mcamap=mtp.getMeta_collecActionsCache();
        			for(Long keyCollec:mcamap.keySet()){
        				Vector<Meta_transfer_actions> vc=mcamap.get(keyCollec);
        				Iterator<Meta_transfer_actions> itmp=vc.iterator();
        				while(itmp.hasNext()) {
        					//System.out.println("CollectionName "+itmp.next().getAction_contents());
        					Properties prop=getPropeties(itmp.next().getAction_contents());
        					parserconfs.put(InterfaceName,prop);
        					//System.out.println("indir for "+ prop.get("inDir"));
        				}
        			}
        		}
		}
		
        		
	}
	
	private static Properties getPropeties(String action_contents) {
		// TODO Auto-generated method stub
		Properties parserconf=new Properties();
		try {
			parserconf.load(new ByteArrayInputStream(action_contents.getBytes()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parserconf;
		
	}
	public static HashMap<String,MetaTransferActionsProps> getPropsforParseAction(String interfaceName){
		HashMap<String,MetaTransferActionsProps> tmp_setidtometacachemap=new HashMap<String,MetaTransferActionsProps>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql=Queries._get_meta_collection_sets(interfaceName,true);
		//System.out.println("sql  being executed :-"+sql);
		con=dbconmap.get("etlrock").getConnection();
		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				String collection_setName=rs.getNString(2);
				activeInterface.add(collection_setName);
				//populategetMetaColletions(collection_set_id,version_number);
				//System.out.println("Value for the collectionset or TP Name================"+ collection_setName);
				tmp_setidtometacachemap.put(collection_setName,new MetaTransferActionsProps(dbconmap.get("etlrock"),log,collection_setName));
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			con=null;
			log.log(Level.WARNING, classname+": failed to run the query get the meta_colletion_sets"+e.getMessage());
			
			
		}
		con=null;
		//return tables;
		return tmp_setidtometacachemap;
		}
	@SuppressWarnings("unused")
	private static HashMap<String,Integer> fetchtablerowcountdata(ArrayList<String> tables) {
		// TODO Auto-generated method stub
		
		HashMap<String,Integer> tabletorowcount=new HashMap<String,Integer>();
		tabletorowcount.clear();
		for(String table:tables) {
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			String sql=Queries._get_rowcount_sql(table);
			
			//String sql="select TOP 1 DATETIME_ID from DC_E_OCC_POLICY_CONTROL_ESY_RAW";
			con=dbconmap.get("dwhdb").getConnection();
			try {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				/*final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				while(rs.next()) {
				Timestamp date=rs.getTimestamp(1);
				System.out.println("printing the dateString"+rs.getTime(1));
				//System.out.println(date.toString()+date.+date.getMinutes());
				String dateString = dateFormat.format(date);
				System.out.println("printing the dateString");
				System.out.println(dateString);
				}
				break;*/
				while(rs.next()) {
					Integer rowcount=rs.getInt(1);
					//if(rowcount>0) {
						tabletorowcount.put(table,rowcount);
						//System.out.println("rowcount for the table "+table+" is "+rowcount);
					//}
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				con=null;
				log.log(Level.WARNING, classname+": tables fecting queru thrown error"+e.getMessage());
				
				
			}
			
		}
		return tabletorowcount;
	}

}
