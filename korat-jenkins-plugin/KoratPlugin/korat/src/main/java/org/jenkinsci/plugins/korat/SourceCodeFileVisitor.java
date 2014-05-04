package org.jenkinsci.plugins.korat;

import hudson.model.BuildListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceCodeFileVisitor extends SimpleFileVisitor<Path> {
	
	private BuildListener listener;
	
	private Map<String,String> classInvariantMap;
	
	private Boolean forceKoratRun = Boolean.FALSE;
	
	private Map<String,String> finitizationMap = new HashMap<String,String>();
	
	private String finitizationParams;
	
	public SourceCodeFileVisitor(BuildListener listener, Map<String,String> classInvariantMap, String finitizationParams, Boolean forceKoratRun) {
		this.listener = listener;
		this.classInvariantMap = classInvariantMap;
		this.finitizationParams = finitizationParams;
		this.forceKoratRun = forceKoratRun == null ? false : forceKoratRun;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
		
		String fileName = file.getFileName().toString();
		
		if (!fileName.endsWith(".java")) {
			return FileVisitResult.CONTINUE;
		}
		
		listener.getLogger().println("\nScanning for repOK in " + file);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file.toString()));

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String sourceCode = sb.toString();

			Pattern pattern = Pattern.compile("\\brepOK\\(\\).+",
					Pattern.DOTALL);
			Matcher m = pattern.matcher(sourceCode);
			if (m.find()) {
				listener.getLogger().println("Found repOK!");
				String repOkStr = m.group();
				int startIndex = repOkStr.indexOf('{');
				repOkStr = repOkStr.substring(startIndex);

				int braceBalance = 0;

				char[] repOkArr = repOkStr.toCharArray();

				for (int i = 0; i < repOkArr.length; i++) {
					if (repOkArr[i] == '{') {
						braceBalance = braceBalance + 1;
					} else if (repOkArr[i] == '}') {
						braceBalance = braceBalance - 1;
					}
					if (braceBalance == 0) {
						repOkStr = repOkStr.substring(1, i);
						break;
					}
				}
				
				repOkStr = repOkStr.replaceAll("\\p{Space}", "");
				
				//listener.getLogger().println("\nRepOk=" + repOkStr + "\n");
				//listener.getLogger().println("Class Invariant Map = " + this.classInvariantMap + "\n");
				
				if (hasRepOkChanged(fileName, repOkStr) || this.forceKoratRun || hasFinitizationChanged(fileName) ) {
					this.classInvariantMap = getSavedMap();
					this.classInvariantMap.put(fileName, repOkStr);
					serializeClassInvariantMap();
					serializeFinitizationParams(fileName);
					createRunKoratScript();
				} else {
					listener.getLogger().println("RepOk has not changed. Moving on...\n");
				}
/*				try {
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					byte[] digest = md.digest(repOkStr.getBytes());
					String sha256sum = Base64.encode(digest);
					System.out.println("SHA256 digest=" + sha256sum);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
*/			} else {
				listener.getLogger().println("No repOK found");
			}
		} catch (FileNotFoundException e) {
			listener.getLogger().println("ERROR:" + e);
		} catch (IOException e) {
			listener.getLogger().println("ERROR:" + e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				listener.getLogger().println("ERROR:" + e);
			}
		}
		return FileVisitResult.CONTINUE;
	}
	
	private void serializeClassInvariantMap() {
	   try {
				FileOutputStream fout = new FileOutputStream("/var/lib/jenkins/userContent/ClassInvariantMap.ser");
				ObjectOutputStream oos = new ObjectOutputStream(fout);   
				oos.writeObject(this.classInvariantMap);
				oos.close();
				fout.close();
		   } catch(Exception e){
				listener.getLogger().println("ERROR: Error while serializing class invariant map " + this.classInvariantMap);
				listener.getLogger().println("ERROR:" + e);
		   }
	   }	
	
	private void createRunKoratScript() {
		Writer writer = null;
		try {
			//listener.getLogger().println("Using finitization parameters " + this.finitizationParams);
		    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/var/lib/jenkins/userContent/runKorat.sh"), "utf-8"));
		    writer.write("java -cp \"/var/lib/jenkins/userContent/korat.jar:/var/lib/jenkins/jobs/sortedlist/workspace/sortedlist/target/sortedlist-0.0.1-SNAPSHOT.jar\" korat.Korat --printCandVects --serialize /var/lib/jenkins/userContent/candidates.ser --class com.vandv.SortedList --args " + this.finitizationParams);
		} catch (IOException ex) {
			listener.getLogger().println("ERROR:" + ex);
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {
				listener.getLogger().println("ERROR:" + ex);
		   }
		}
	}	

	private Map<String, String> getSavedMap() {
		FileInputStream fin = null;
		ObjectInputStream oin = null;

		Map<String,String> savedMap = null;
	   
		try 
			 {
			   fin = new FileInputStream("/var/lib/jenkins/userContent/ClassInvariantMap.ser");
			   oin = new ObjectInputStream(fin);
			   savedMap = (Map<String, String>) oin.readObject();
			 } 
	   
	   catch(FileNotFoundException e) {
			listener.getLogger().println("WARN: No /var/lib/jenkins/userContent/ClassInvariantMap.ser found!");
	   } 
	   catch(Exception e) {
				listener.getLogger().println("ERROR: Error while deserializing class invariant map");
				listener.getLogger().println("ERROR:" + e);
		} 
	   finally {
			try {
				if (oin != null) {
					oin.close();
				}
				if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				listener.getLogger().println("ERROR: Error while closing stream " + e);
			}
	   }
		return savedMap;
	}	

	private Map<String, String> getSavedFinitizationMap() {
		FileInputStream fin = null;
		ObjectInputStream oin = null;

		Map<String,String> savedMap = null;
	   
		try 
			 {
			   fin = new FileInputStream("/var/lib/jenkins/userContent/finitization.ser");
			   oin = new ObjectInputStream(fin);
			   savedMap = (Map<String, String>) oin.readObject();
			 } 
	   
	   catch(FileNotFoundException e) {
			listener.getLogger().println("WARN: No /var/lib/jenkins/userContent/finitization.ser found!");
	   } 
	   catch(Exception e) {
				listener.getLogger().println("ERROR: Error while deserializing finitization map");
				listener.getLogger().println("ERROR:" + e);
		} 
	   finally {
			try {
				if (oin != null) {
					oin.close();
				}
				if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				listener.getLogger().println("ERROR: Error while closing stream " + e);
			}
	   }
		return savedMap;
	}	

	private boolean hasRepOkChanged(String fileName, String repOkStr) {
		FileInputStream fin = null;
		ObjectInputStream oin = null;
		
	   try 
			 {
			   fin = new FileInputStream("/var/lib/jenkins/userContent/ClassInvariantMap.ser");
			   oin = new ObjectInputStream(fin);
			   
				Map<String,String> savedMap = (Map<String, String>) oin.readObject();
				//listener.getLogger().println("INFO: savedMap= " + savedMap);
				
				if (savedMap != null) {
					String savedRepOk = savedMap.get(fileName);
					if (savedRepOk == null) {
						listener.getLogger().println("repOK for " + fileName + " is new." );
						return true;
					}
					if (savedRepOk != null && !savedRepOk.equals(repOkStr)) {
						listener.getLogger().println("repOK for " + fileName + " has changed." );
						return true;
					}
				} else {
					listener.getLogger().println("ERROR: savedMap is null");
				}
		} 
	   
	   catch(FileNotFoundException e) {
			listener.getLogger().println("WARN: No /var/lib/jenkins/userContent/ClassInvariantMap.ser found!");
			return true;
	   } 
	   catch(Exception e) {
				listener.getLogger().println("ERROR: Error while deserializing class invariant map");
				listener.getLogger().println("ERROR:" + e);
		} 
	   finally {
			try {
				if (oin != null) {
					oin.close();
				}
				if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				listener.getLogger().println("ERROR: Error while closing stream " + e);
			}
	   }
		return false;
		}	
	
	
	private boolean hasFinitizationChanged(String fileName) {
		
		FileInputStream fin = null;
		ObjectInputStream oin = null;
		
	   try 
			 {
			   fin = new FileInputStream("/var/lib/jenkins/userContent/finitization.ser");
			   oin = new ObjectInputStream(fin);
			   
			   
				Map<String,String> savedMap = (Map<String, String>) oin.readObject();
				//listener.getLogger().println("INFO: savedMap= " + savedMap);
				
				if (savedMap != null) {
					String savedFinitization = savedMap.get(fileName);
					if (savedFinitization == null) {
						listener.getLogger().println("Finitization for " + fileName + " is new." );
						return true;
					}
					if (savedFinitization != null && !savedFinitization.equals(this.finitizationParams)) {
						listener.getLogger().println("Finitization for " + fileName + " has changed." );
						return true;
					}
				} else {
					listener.getLogger().println("ERROR: saved finitization map is null");
				}
		} 
	   
	   catch(FileNotFoundException e) {
			listener.getLogger().println("WARN: No /var/lib/jenkins/userContent/finitization.ser found!");
			return true;
	   } 
	   catch(Exception e) {
				listener.getLogger().println("ERROR: Error while deserializing class finitization");
				listener.getLogger().println("ERROR:" + e);
		} 
	   finally {
			try {
				if (oin != null) {
					oin.close();
				}
				if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				listener.getLogger().println("ERROR: Error while closing stream " + e);
			}
	   }
		return false;
	}	
	
	 private void serializeFinitizationParams(String fileName) {
 	   try {
 				FileOutputStream fout = new FileOutputStream("/var/lib/jenkins/userContent/finitization.ser");
 				ObjectOutputStream oos = new ObjectOutputStream(fout);
 				
 				this.finitizationMap.put(fileName, this.finitizationParams);
 				
 				oos.writeObject(this.finitizationMap);
 				oos.close();
 				fout.close();
 		   } catch(Exception e){
 				listener.getLogger().println("ERROR: Error while serializing class invariant map " + this.finitizationParams);
 				listener.getLogger().println("ERROR:" + e);
 		   }
 	   }	    
}


