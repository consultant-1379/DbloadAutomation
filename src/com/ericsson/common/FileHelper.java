package com.ericsson.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper {
	private File Outputfile=null;
	private BufferedWriter br=null;
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public FileHelper() {
		
	}
	
	public boolean copyFiles(String src,String dst) {
		File srcfile = new File(src);
		if(srcfile.isDirectory()) {
			File[] srcfiles=srcfile.listFiles();
			for(int i=0;i<srcfiles.length;i++) {
				try {
					copyFileUsingStream(srcfiles[i],new File(dst+srcfiles[i].getName()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
				}
			}
		}else {
			try {
				copyFileUsingStream(srcfile,new File(dst+srcfile.getName()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	public void copyFileUsingStream(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[65536];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}

	public  List<File> validateFile(String intf) {
		ArrayList<File> filelist=null;
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String sb=null;
		while(true) {
			System.out.print("[INPUT]::Please give the PM files name with absolute path for Interface "+ANSI_YELLOW_BACKGROUND+ANSI_BLUE+intf+ANSI_RESET+":");
			try {
				sb=br.readLine();
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Pattern pattern = Pattern.compile("\\s*(.+)");
			Matcher match=pattern.matcher(sb);
			if(!match.matches()) {
				System.out.println();
				System.out.print("[INVALID]::Not a valid file name");
			}else {
					filelist=new ArrayList<File>();
					File file=new File(match.group(1));
					if(file.exists()) {
						if(file.isFile()) {
							System.out.println("[INFO]::File found proceeding to copy");
							filelist.add(file);
							return filelist;
						}else if(file.isDirectory()) {
							String regex="";
							System.out.println("[INFO]::Given path found is a directory");
							System.out.print("[INPUT]::(Optional)Please give regex to match files from the directory.else all files from direcotry will be copied:");
							try {
								regex=br.readLine();
								pattern = Pattern.compile("\\s+");
							
								if(pattern.matcher(regex).matches()||regex == null||regex.equalsIgnoreCase("")) {
									regex=".*";
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							filelist.addAll(getFiles(file,regex));
							//System.out.println(filelist);
							return filelist;
						}
					
					}else {
						System.out.println("[ERROR]:: File doesnot Exist");
					}	
					
				}
			}
		
		}
		
		
	
	public List<File> getFiles(File dir, String regex) {
		FilenameFilter filefilter=new FilenameFilter(){
			public boolean accept(File dir, String name) {
				Pattern pattern=Pattern.compile(regex);
				Matcher matcher=pattern.matcher(name);
				return matcher.find();
			}
		};
		File[] files=dir.listFiles(filefilter);
		ArrayList<File> list=new ArrayList<File>();
		for(File file:files) {
			
			if(file.isFile()&&!file.isHidden()) {
				list.add(file);
			}
		}
		
		return list;
		
	}
	public File createFile(String filename) throws IOException {
		boolean newfile=false;
		if(Outputfile==null) {
			Outputfile=new File(filename);
			newfile=true;
		}
		if(newfile&&Outputfile.exists()&&Outputfile.isFile()) {
			//System.out.println("[INFO]:: File already exist. deleting the file");
			Outputfile.delete();
		}else if(newfile&&Outputfile.isDirectory()){
			//System.out.println("[INFO]:: Not valid Filename");
			return null;
		}
		if(newfile&&(filename.contains("\\")||filename.contains("/"))){
			if(filename.contains("\\")) {
				File newdir=new File(filename.substring(0,filename.lastIndexOf("\\")+1));
				if(newdir.exists()) {
					//System.out.println("[INFO]:: diectory already present."+newdir.toString());
				}else {
					if(newdir.mkdir()) {
						//System.out.println("[INFO]:: Successfully created the directory"+newdir.toString());
					}else {
						//System.out.println("[Error]:: Failed created the directory "+newdir.toString());
					}
				}
			}else {
				File newdir=new File(filename.substring(0,filename.lastIndexOf("/")+1));
				if(newdir.exists()) {
					//System.out.println("[INFO]:: diectory already present."+newdir.toString());
				}else {
					if(newdir.mkdir()) {
						//System.out.println("[INFO]:: Successfully created the directory "+newdir.toString());
					}else {
						//System.out.println("[Error]:: Failed created the directory "+newdir.toString());
					}
				}
			}
		}
		if(newfile=true) {
				br=new BufferedWriter(new FileWriter(Outputfile));
		}
		return Outputfile;
	}
	public void writetextline(String line) {
		
		char[] chars=line.toCharArray();
		try {
			br.write(chars);
			br.newLine();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	public int closeFile() {
		if(Outputfile!=null) {
			Outputfile=null;
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 0;
			
		}
		return 1;
	}
	public boolean createDirectory(String indirstr) {
		File indir=new File(indirstr);
		if(indir.exists()) {
			System.out.println("[INFO]::In directory already exist "+indir.getName());
			return true;
		}else {
			if(indir.mkdir()) {
				System.out.println("[INFO]::Created In directory successfully "+indir.getName());
				return true;
			}else {
				System.out.println("[ERROR]::Failed to create a in directory.Trying to create Main directory");
				if(createDirectory(indirstr.substring(0, indirstr.lastIndexOf("/")))) {
					return createDirectory(indirstr);
				}else {
					return false;
				}
				
			}
		}
	}
  
	//Testing
	public static void main(String args[]) throws IOException {
		FileHelper fh=new FileHelper();
		//File file=new File("H:\\OSS-Documents\\JIRA\\Source\\OneDrive\\");
		//System.out.println(file.getPath());
		//fh.validateFile("test");
		fh.createFile("csvout\\text.csv");
		/*
		Long start=System.currentTimeMillis();
		System.out.println(start);
		fh.copyFileUsingStream(new File("H:\\OSS-Documents\\JIRA\\Source\\OneDrive\\SDG_OUTPUT_REPORT_20210907T160637Z.zip"),new File("H:\\OSS-Documents\\JIRA\\dest\\SDG_OUTPUT_REPORT_20210907T160637Z.zip"));
		Long end=System.currentTimeMillis();
		System.out.println("run difference "+ (end - start));
		*/
		
	}


}
