package com.ericsson.aggregation.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.aggregation.bh.RockSession;
import com.ericsson.aggregation.bh.TechPackView;
import com.ericsson.common.FileHelper;
import com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultKey;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultSource;
import com.ericsson.eniq.busyhourcfg.data.vo.Key;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.Source;
import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.database.TechPackReader;
import com.ericsson.eniq.busyhourcfg.database.TechPackReaderFactory;

public class Placeholderconsoleintegrator {
	public static HashMap<String,List<Placeholder>> bhlevels=new HashMap<String,List<Placeholder>>();
	public static String TechPack=null;
	TechPackReader factory =null;
	String selectedversion_id=null;
	public void config(RockSession session) throws IOException {
		TechPackView tpv=new TechPackView();
		tpv.PostImitation(session);
		final DatabaseSession databaseSession = (DatabaseSession) session.getDatabaseSession();
	       // final TechPack selectedTechPack = (TechPack) session.getAttribute("sourcetp");
	    	
	    factory = TechPackReaderFactory.getTechPackReader(databaseSession);
	    List<String> versionids = factory.getAllActivatedPMTypeVersionIds();
	    BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String sb=null;
		selectedversion_id=null;
		while(true) {
			System.out.println("[INPUT]::Please choose the TechPack from below list ");
			for(int i=0;i<versionids.size();i++) {
				System.out.println("[INPUT]::"+(i+1)+") "+FileHelper.ANSI_YELLOW_BACKGROUND+FileHelper.ANSI_BLUE+versionids.get(i)+FileHelper.ANSI_RESET);
			}
			System.out.print("[INPUT]::Please enter the number of  respective techpack.Example 1 or 2 or N :");
			try {
				sb=br.readLine();
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Pattern pattern = Pattern.compile("[0-9]+");
			Matcher match=pattern.matcher(sb);
			if(match.matches()) {
				if(versionids.size()>=Integer.valueOf(sb)) {
					selectedversion_id=versionids.get(Integer.valueOf(sb)-1);
					System.out.println("[INFO]::selected version id "+selectedversion_id);
					break;
				}else {
					System.out.print("[INVALID]::Not in valid number range, please choose number from 1  to "+versionids.size());
				}
			}else {
				System.out.print("[INVALID]::Not a valid number, Please choose only number from 1 to "+versionids.size());
			}
		}
		//Busy hour choosing
		TechPack currentTechPack = factory.getTechPackByVersionId(selectedversion_id);
		List<TargetTechPack> ttplist=currentTechPack.getTargetTechPacks();
		String bhlevel=null;
		Placeholder placeholder=null;
			for(TargetTechPack ttp:ttplist) {
				while(true) {
				System.out.println("[INPUT]::Please choose the Busy hour level from the below");
				List<BusyhourSupport>bhslist=ttp.getBusyhourSupports();
				for(int i=0;i<bhslist.size();i++) {
					System.out.println("[INPUT]::"+(i+1)+") "+FileHelper.ANSI_YELLOW_BACKGROUND+FileHelper.ANSI_BLUE+bhslist.get(i).getBhlevel()+FileHelper.ANSI_RESET);
				}
				System.out.print("[INPUT]::Please enter the number of  respective blevel.Example 1 or 1,2,..,N :");
				try {
					sb=br.readLine();
				} catch (IOException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Pattern pattern = Pattern.compile("[0-9]+(,{1}[0-9]*)*");
				Matcher match=pattern.matcher(sb);
				boolean flag0=false;
				if(match.matches()) {
					String[] strs=sb.split(",");
			    	 for(int i=0;i<strs.length;i++) {
			    		 System.out.println(strs[i]);
					if(versionids.size()>=Integer.valueOf(strs[i])) {
						BusyhourSupport BHS=bhslist.get(Integer.valueOf(strs[i])-1);
						bhlevel=BHS.getBhlevel();
						System.out.println("[INFO]::selected bhlevel "+bhlevel);
						ArrayList<Placeholder> placeholders=null;
						if(bhlevels.get(BHS.getBhlevel()) == null) {
							bhlevels.put(BHS.getBhlevel(), new ArrayList<Placeholder>());
							placeholders=(ArrayList<Placeholder>) bhlevels.get(BHS.getBhlevel());
						}else {
							placeholders=(ArrayList<Placeholder>) bhlevels.get(BHS.getBhlevel());
						}
						List<Placeholder> phlist=BHS.getPlaceholders();
						while(true) {
							System.out.println("[INPUT]::Please choose the PlaceHolder level from the below");
							for(int j=0;j<phlist.size();j++) {
								System.out.println("[INPUT]::"+(j+1)+") "+FileHelper.ANSI_YELLOW_BACKGROUND+FileHelper.ANSI_BLUE+phlist.get(j).getBhtype()+FileHelper.ANSI_RESET);
							}
							System.out.print("[INPUT]::Please enter the number of  respective placeholder.Example 1 or 1,2,..,N :");
							try {
								sb=br.readLine();
							} catch (IOException e) {
							// TODO Auto-generated catch block
								e.printStackTrace();
							}
							pattern = Pattern.compile("[0-9]+(,{1}[0-9]*)*");
							match=pattern.matcher(sb);
							boolean flag1=false;
							if(match.matches()) {
								String[] strs1=sb.split(",");
						    	 for(int k=0;k<strs1.length;k++) {
						    		 System.out.println(strs1[k]);
								if(phlist.size()>=Integer.valueOf(strs1[k])) {
									placeholder=phlist.get(Integer.valueOf(strs1[k])-1);
									placeholders.add(placeholder);
									System.out.println("[INFO]::selected version id "+placeholder.getBhtype());
									flag1=true;
								}else {
									System.out.print("[INVALID]::Not in valid number range, please choose number from 1  to "+phlist.size());
								}
							}
						    	 if(flag1)break;
							}else {
								System.out.print("[INVALID]::Not a valid number, Please choose only number from 1 to "+phlist.size());
							}
						}
						flag0=true;
					}else {
						System.out.print("[INVALID]::Not in valid number range, please choose number from 1  to "+bhslist.size());
					}
				}
			    	 if(flag0)break;
				}else {
					System.out.print("[INVALID]::Not a valid number, Please choose only number from 1 to "+bhslist.size());
				
				}
			}
			break;
		}
			//testing part
			System.out.println("Sample configuration has been loaded");
			for(String bhlevell:bhlevels.keySet()) {
				ArrayList<Placeholder> testplaceholder=(ArrayList<Placeholder>) bhlevels.get(bhlevell);
				for(Placeholder placeholderr:testplaceholder) {
					System.out.println("BHlevel "+bhlevell+" "+placeholderr.getBhtype());
					updateView(placeholderr,bhlevell);
					new PlaceholderJsonWriter().writer(placeholderr, bhlevell);
				}
				//System.out.print(n);
			}
			//break;
		}

	//Testing
	public Placeholder updateView(Placeholder placeholder, String bhlevel) {
		String description=null;
		System.out.print("[INPUT]::Please enter the desription for BHLevel "+bhlevel+" BHType "+placeholder.getBhtype()+":");
		//String sb=null;
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		try {
			description=br.readLine();
			placeholder.setDescription(description);
			placeholder.setSources(getTables(br));
			getKeys(placeholder,br);
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return placeholder;
		
	}
	private List<Source> getTables(BufferedReader br) {
		List<Source> Sources=new ArrayList<Source>();
		List<String> basetables=factory.getAllBusyhourBasetables(selectedversion_id);
		while(true) {
			System.out.println("[INPUT]::Please choose the basetable level from the below");
			for(int j=0;j<basetables.size();j++) {
				System.out.println("[INPUT]::"+(j+1)+") "+FileHelper.ANSI_YELLOW_BACKGROUND+FileHelper.ANSI_BLUE+basetables.get(j)+FileHelper.ANSI_RESET);
			}
			System.out.print("[INPUT]::Please enter the number of  respective placeholder.Example 1 or 1,2,..,N :");
			String sb=null;
			try {
				sb=br.readLine();
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Pattern pattern = Pattern.compile("[0-9]+(,{1}[0-9]*)*");
			Matcher match=pattern.matcher(sb);
			boolean flag1=false;
			if(match.matches()) {
				String[] strs1=sb.split(",");
		    	 for(int k=0;k<strs1.length;k++) {
		    		 System.out.println(strs1[k]);
		    		 String tablename=null;
				if(basetables.size()>=Integer.valueOf(strs1[k])) {
					tablename=basetables.get(Integer.valueOf(strs1[k])-1);
					Sources.add(new DefaultSource(tablename));
					//placeholders.add(placeholder);
					System.out.println("[INFO]::selected version id "+tablename);
					flag1=true;
				}else {
					System.out.print("[INVALID]::Not in valid number range, please choose number from 1  to "+basetables.size());
				}
			}
		    	 if(flag1)break;
			}else {
				System.out.print("[INVALID]::Not a valid number, Please choose only number from 1 to "+basetables.size());
			}
		}
		return Sources;
	}
	public void getKeys(Placeholder placeholder, BufferedReader br){
		List<Source> source=placeholder.getSources();
		List<Key> keyslist=new ArrayList<Key>();
		while(true) {

			System.out.println("[INPUT]::Please choose the tablename from the below to add keys or columnName");
			for(int j=0;j<source.size();j++) {
				System.out.println("[INPUT]::"+(j+1)+") "+FileHelper.ANSI_YELLOW_BACKGROUND+FileHelper.ANSI_BLUE+source.get(j).getTypename()+FileHelper.ANSI_RESET);
			}
			System.out.print("[INPUT]::Please enter the numbers of  respective tablename to add keys.Example 1 or 1,2 or 1,2..,N :");
			String sb=null;
			try {
				sb=br.readLine();
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Pattern pattern = Pattern.compile("[0-9]+(,{1}[0-9]*)*");
			Matcher match=pattern.matcher(sb);
			boolean flag1=false;
			if(match.matches()) {
				String[] tablenames=sb.split(",");
		    	 for(int k=0;k<tablenames.length;k++) {
		    		 //System.out.println(tablenames[k]);
		    		 String tablename=null;
				if(source.size()>=Integer.valueOf(tablenames[k])) {
					tablename=source.get(Integer.valueOf(tablenames[k])-1).getTypename();
						System.out.print("[INFO]::Please enter the keys or columnName for tablename "+tablename+" in comma separated format. Example OSSID,DATETIME:");
						try {
							sb=br.readLine();
							String[] keys=sb.split(",");
							for(int x=0; x<keys.length;x++) {
								keyslist.add(new DefaultKey(keys[x], tablename, keys[x]));
							}
							placeholder.setKeys(keyslist);
							//break;
						} catch (IOException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						}
					//System.out.println("[INFO]::selected ta "+tablename);
					flag1=true;
				}else {
					System.out.print("[INVALID]::Not in valid number range, please choose number from 1  to "+source.size());
				}
			}
		    	 if(flag1)break;
			}else {
				System.out.print("[INVALID]::Not a valid number, Please choose only number from 1 to "+source.size());
			}
		
		}
	}

	public static void main(String args[]) {
		
	}
	


}
