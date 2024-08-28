package com.ericsson.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
//Creating DWH busyhour mappings
public class LogHandler {
	public static Logger log = Logger.getLogger("testingsuite.main");
	
	public static LogHandler instance;
	private LogHandler() {
		/*disabling the logic
		log.setLevel(Level.ALL);
		FileHandler handler=null;
		String logpath=System.getProperty("logPath","");
		if(logpath.equalsIgnoreCase("")) {
			File file=new File("log");
			if(file.exists()) {
				try {
					handler = new FileHandler(file.toString()+"/testsuite_db.log");
					//log.addHandler(handler);
					//log.setLevel(Level.ALL);
					//System.setErr(new PrintStream(new FileOutputStream(file.toString()+"/testsuite_err.log")));
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(file.mkdir()) {
				try {
					handler = new FileHandler(file.toString()+"/testsuite_db.log");
					//log.addHandler(handler);
					//System.setErr(new PrintStream(new FileOutputStream(file.toString()+"/testsuite_err.log")));
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				log.log(Level.SEVERE, "Not able to create the log directory");
			}
				
		}else {
			File file=new File(logpath);
			if(file.exists()) {
				try {
					handler = new FileHandler(file.toString()+"/testsuite_db.log");
					//log.addHandler(handler);
					//System.setErr(new PrintStream(new FileOutputStream(file.toString()+"/testsuite_err.log")));
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(file.mkdir()) {
					try {
						handler = new FileHandler(file.toString()+"/testsuite_db.log");
						//log.addHandler(handler);
						//System.setErr(new PrintStream(new FileOutputStream(file.toString()+"/testsuite_err.log")));
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}else {
					log.log(Level.SEVERE, "Not able to create the log directory");
				}
			}*/
	}
	public static  LogHandler getInstance() {
		if(instance != null) {
			return instance;
		}
		instance=new LogHandler();
		return instance;
	}
	public void log(Level level,String string) {
		log.log(level, string);
		}
	public void error(Level level,String string) {
		}
	public void config(String string) {
		// TODO Auto-generated method stub
		log.log(Level.CONFIG, string);
	}
	public void log(Level severe, String string, Exception e1) {
		// TODO Auto-generated method stub
		log.log(severe,string,e1);
	}
	public void severe(String msg) {
		// TODO Auto-generated method stub
		log.log(Level.SEVERE, msg);
	}
	public void info(String msg) {
		// TODO Auto-generated method stub
		log.log(Level.INFO, msg);
	}
	public void fine(String msg) {
		// TODO Auto-generated method stub
		log.log(Level.FINEST,msg);
	}
	
}
