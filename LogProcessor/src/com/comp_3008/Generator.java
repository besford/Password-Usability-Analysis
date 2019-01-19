package com.comp_3008;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//generates csv files from input log data files
public class Generator {
	
	//variables to be populated when parsing the csv log files
	List<String> users;					//stores list of userID's
	List<String> websites;				//stores list of webID's
	List<String> data;					//stores list of data to be added to CSV files
	Map<String, Integer> passwds;		//stores the number of passwords created for each webID
	Map<String, Integer> practice;		//stores the number of times a password was practiced for each webID
	List<String> pwEntryTimes;			//stores the webID, time to enter password, and failure/success for each test
	TimeDifference td;					//used to calculate the time difference between the time stamps

	public Generator() {
		users = new ArrayList<String>();
		websites = new ArrayList<String>();
		data = new ArrayList<String>();
		passwds = new LinkedHashMap<String, Integer>();
		practice = new LinkedHashMap<String, Integer>();
		td = new TimeDifference();
		pwEntryTimes = new ArrayList<String>();
	}
	
	//generates the header for the dataByUser.csv file
	private void generateUserHeaders() {
		//File file = new File("./src/com/comp_3008/dataByUser.csv");
		File file = new File("./com/comp_3008/dataByUser.csv");
		String content = "\"uID\",\"Scheme\",\"TotalLogs\",\"SuccLogs\",\"FailLogs\", \"TotalLogTime\", \"SuccLogTime\",\"FailLogTime\", \"PasswordsCreated\", \"NumPractices\", "
				+ "\"TotalTestTimes\",\"SuccessfulTestTimes\", \"UnsuccessfulTestTimes\"\n";

		try (FileOutputStream fop = new FileOutputStream(file, true)) {

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//generate the header for the dataBySite.csv file
	private void generateSiteHeaders() {	
		//File file = new File("./src/com/comp_3008/dataBySite.csv");
		File file = new File("./com/comp_3008/dataBySite.csv");
		String content = "\"webID\",\"Scheme\",\"TotalLogs\",\"SuccLogs\",\"FailLogs\", \"TotalLogTime\", \"SuccLogTime\",\"FailLogTime\", \"PasswordsCreated\", \"NumPractices\", "
				+ "\"TotalTestTimes\",\"SuccessfulTestTimes\", \"UnsuccessfulTestTimes\"\n";

		try (FileOutputStream fop = new FileOutputStream(file, true)) {

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getDataByUser(String fileName) {
		//variables for storing local data about a user
		String mode = "create";
		String userID = null;
		String webID = "";
		String event = "";
		String action = "";
		String startTime = "";
		String endTime = "";
		String scheme = "";
		
		//set the current scheme
		if(fileName.equals("Text21.csv"))
			scheme = "testtextrandom";
		else if(fileName.equals("Imagept21.csv"))
			scheme = "testpasstiles";
		int t = 0;
		
		//loads a CSV log file
		InputStream s = Generator.class.getResourceAsStream(fileName);
	      
		try(BufferedReader br = new BufferedReader(new InputStreamReader(s, "UTF-8"))) {
			for(String line; (line = br.readLine()) != null; ) {
				//switches between password test and password creation modes
				if(line.contains("verifytest"))
					mode = "test";
				if(!line.contains(scheme))	//skips unnecessary lines in log data files
					continue;
				
				List<String> lineData = Arrays.asList(line.split(","));
				String curID = lineData.get(1).substring(1,lineData.get(1).length()-1);
				
				//if we move to a line with a new userID, generate a line in the dataByUser.csv file for the old userID
				if(!curID.equals(userID)) {
					if(userID != null) {
						generateUserLog(userID, scheme);
					}
					userID = curID;
					mode = "create";		//first section of user log data is users creating passwords/practicing them
				}
				
				//if the mode is create, get data about password creation
				//if the mode is test, get data bout password testing
				switch(mode) {
				case "create":
					webID = lineData.get(2).substring(1,lineData.get(2).length()-1);
					event = lineData.get(5).substring(1,lineData.get(5).length()-1);
					action = lineData.get(6).substring(1,lineData.get(6).length()-1);
					
					//get number of passwords created/practiced for testtextrandom
					if(scheme.equals("testtextrandom")) {
						if(event.equals("create") && action.equals("passwordSubmitted")) {	//passwords created
							if(!passwds.containsKey(webID))
								passwds.put(webID, 1);
							else {
								passwds.put(webID, passwds.get(webID)+1);
								practice.put(webID, 0);		//resets number of practices if a new password is generated
							}
						}
						else if(event.equals("enter") && action.equals("passwordSubmitted")) {	//passwords practiced
							if(!practice.containsKey(webID))
								practice.put(webID, 1);
							else
								practice.put(webID, practice.get(webID)+1);
						}
					}
					
					//get number of passwords created/practiced for testpasstiles
					else if(scheme.equals("testpasstiles")) {
						if(event.equals("create") && action.equals("picAccept")) {	//passwords created
							if(!passwds.containsKey(webID))
								passwds.put(webID, 1);
							else {
								passwds.put(webID, passwds.get(webID)+1);
								practice.put(webID, 0);		//resets number of practices if a new password is generated
							}
						}
						else if(event.equals("create") && (action.equals("goodPractice") || action.equals("badPractice"))) {	//passwords practiced
							if(!practice.containsKey(webID))
								practice.put(webID, 1);
							else
								practice.put(webID, practice.get(webID)+1);
						}
					}
					
					break;
				case "test":
					//get the webID, test login time, and success/failure for each test done by the user
					webID = lineData.get(2).substring(1,lineData.get(2).length()-1);
					event = lineData.get(5).substring(1,lineData.get(5).length()-1);
					action = lineData.get(6).substring(1,lineData.get(6).length()-1);
					if(action.equals("start")) {
						startTime = lineData.get(0).substring(1,lineData.get(0).length()-1);
					}
					else if(action.equals("passwordSubmitted") || action.equals("order inputPwd")) {
						endTime = lineData.get(0).substring(1,lineData.get(0).length()-1);
						t = td.getTimeDifference(startTime, endTime);	//calculate difference between time stamps
					}
					else if(event.equals("login")) {
						pwEntryTimes.add(webID+","+t+","+action);
					}
					break;
				}
				
				if(!users.contains(userID))
					users.add(userID);
				if(!websites.contains(webID))
					websites.add(webID);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		generateUserLog(userID, scheme);  //generates the user log for the last userID in the input CSV log file
	}
	
	//assembles data collected from CSV log files into a string that is added to the dataByUser.csv output file
	//userID, scheme, total logins, successful logins, failed logins, total # seconds successful logins, total # seconds failed logins, {pw creation data}, {pw test data}
	private void generateUserLog(String userID, String scheme) {
		Map<String, ArrayList<Integer>> pwTimesFail = new LinkedHashMap<String, ArrayList<Integer>>(); //stores pw times for failed logins
		Map<String, ArrayList<Integer>> pwTimesPass = new LinkedHashMap<String, ArrayList<Integer>>();	 //stores pw times for successful logins
		ArrayList<Integer> dataList;
		ArrayList<String> createData = new ArrayList<String>();
		ArrayList<String> testData = new ArrayList<String>();
		ArrayList<String> passTimes = new ArrayList<String>();
		ArrayList<String> failTimes = new ArrayList<String>();
		
		//populate maps with pw times for each webID
		testData.add(userID);
		testData.add(scheme);
		for(String s : pwEntryTimes) {
			testData.add(s);
			List<String> pwData = Arrays.asList(s.split(","));
			if(s.contains("success")) {
				passTimes.add(pwData.get(1));
				if(pwTimesPass.containsKey(pwData.get(0))) {
					dataList = (ArrayList<Integer>)pwTimesPass.get(pwData.get(0)).clone();
					dataList.set(0, Integer.parseInt(pwData.get(1)) + dataList.get(0));
					dataList.set(1, dataList.get(1)+1);
					pwTimesPass.put(pwData.get(0), dataList);
				}
				else {
					dataList = new ArrayList<Integer>();
					dataList.add(Integer.parseInt(pwData.get(1)));
					dataList.add(1);
					pwTimesPass.put(pwData.get(0), dataList);
				}
			}
			else if(s.contains("failure")) {
				failTimes.add(pwData.get(1));
				if(pwTimesFail.containsKey(pwData.get(0))) {
					dataList = (ArrayList<Integer>)pwTimesFail.get(pwData.get(0)).clone();
					dataList.set(0, Integer.parseInt(pwData.get(1)) + pwTimesFail.get(pwData.get(0)).get(0));
					dataList.set(1, pwTimesFail.get(pwData.get(0)).get(1)+1);
					pwTimesFail.put(pwData.get(0), dataList);
				}
				else {
					dataList = new ArrayList<Integer>();
					dataList.add(Integer.parseInt(pwData.get(1)));
					dataList.add(1);
					pwTimesFail.put(pwData.get(0), dataList);
				}
			}
		}
		
		data.add(userID);
		data.add(scheme);
		createData.add(userID);
		createData.add(scheme);
		int pass = 0;
		int passTime = 0;
		int fail = 0;
		int failTime = 0;
		for(String st : pwTimesPass.keySet()) {
			passTime += pwTimesPass.get(st).get(0);
			pass += pwTimesPass.get(st).get(1);
		}
		for(String st : pwTimesFail.keySet()) {
			failTime += pwTimesFail.get(st).get(0);
			fail += pwTimesFail.get(st).get(1);
		}
		data.add(String.valueOf(pass+fail));
		data.add(String.valueOf(pass));
		data.add(String.valueOf(fail));
		data.add(String.valueOf(passTime+failTime));
		data.add(String.valueOf(passTime));
		data.add(String.valueOf(failTime));
		
		int totalCreated = 0;
		int totalPractice = 0;
		//get data for password creation
		for(String s : passwds.keySet()) {
			createData.add(s);
			createData.add(String.valueOf(passwds.get(s)));
			totalCreated += passwds.get(s);
			if(practice.get(s) == null) {
				createData.add("0");
			}
			else {
				createData.add(String.valueOf(practice.get(s)));
				totalPractice += practice.get(s);
			}
			
		}
		data.add(String.valueOf(totalCreated));
		data.add(String.valueOf(totalPractice));
		
		//get data for password testing
		String passT = "";
		for(int i=0; i<passTimes.size(); i++) {
			if(i == passTimes.size()-1)
				passT += passTimes.get(i);
			else
				passT += (passTimes.get(i)+",");
		}
		
		String failT = "";
		for(int i=0; i<failTimes.size(); i++) {
			if(i == failTimes.size()-1)
				failT += failTimes.get(i);
			else
				failT += (failTimes.get(i)+",");
		}
		if(failT.length() == 0)
			data.add("\""+passT+"\"");
		else if(passT.length() == 0)
			data.add("\"\"");
		else
			data.add("\""+passT+","+failT+"\"");
		data.add("\""+passT+"\"");
		data.add("\""+failT+"\"");
		
		//write data to dataByUser.csv file
		//File file = new File("./src/com/comp_3008/dataByUser.csv");
		File file = new File("./com/comp_3008/dataByUser.csv");
		String content = "";
		for(int i = 0; i<data.size(); i++) {
			if(i == data.size()-1)
				content += data.get(i)+"\n";
			else
				content += data.get(i)+",";
		}

		try (FileOutputStream fop = new FileOutputStream(file, true)) {

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//file = new File("./src/com/comp_3008/tempCreateData.csv");
		file = new File("./com/comp_3008/tempCreateData.csv");
		content = "";
		for(int i = 0; i<createData.size(); i++) {
			if(i == createData.size()-1)
				content += createData.get(i)+"\n";
			else
				content += createData.get(i)+",";
		}

		try (FileOutputStream fop = new FileOutputStream(file, true)) {

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//file = new File("./src/com/comp_3008/tempTestData.csv");
		file = new File("./com/comp_3008/tempTestData.csv");
		content = "";
		for(int i = 0; i<testData.size(); i++) {
			if(i == testData.size()-1)
				content += testData.get(i)+"\n";
			else
				content += testData.get(i)+",";
		}

		try (FileOutputStream fop = new FileOutputStream(file, true)) {

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		//clear global variables for use with next userID
		data.clear();
		passwds.clear();
		practice.clear();
		pwEntryTimes.clear();
	}
	
	//webID, scheme, total logins, successful logins, failed logins, total # seconds successful logins, 
	//total # seconds failed logins, # of pw created, # of times accepted pw tested,
	private void getDataBySite(String fileName) {
		String createData = "tempCreateData.csv";
		String testData = "tempTestData.csv";
		InputStream s = Generator.class.getResourceAsStream(testData);
		InputStream s2 = Generator.class.getResourceAsStream(createData);
		
		//SiteData stores total logins, successful logins, failed logins, total # seconds successful logins, total # seconds failed logins
		Map<String, ArrayList<Integer>> textSiteData = new LinkedHashMap<String, ArrayList<Integer>>();
		Map<String, ArrayList<Integer>> tilesSiteData = new LinkedHashMap<String, ArrayList<Integer>>();
		//SiteCreates stores # of pw created, # of times accepted pw tested
		Map<String, ArrayList<Integer>> textSiteCreates = new LinkedHashMap<String, ArrayList<Integer>>();
		Map<String, ArrayList<Integer>> tilesSiteCreates = new LinkedHashMap<String, ArrayList<Integer>>();
		//SiteTests stores all of the tests performed for the webID
		Map<String, String> textSitePassTests = new LinkedHashMap<String, String>();
		Map<String, String> textSiteFailTests = new LinkedHashMap<String, String>();
		Map<String, String> tilesSitePassTests = new LinkedHashMap<String, String>();
		Map<String, String> tilesSiteFailTests = new LinkedHashMap<String, String>();
		
		ArrayList<Integer> dataList;		//stores data to be added to map for webID
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(s, "UTF-8"))) {
			for(String line; (line = br.readLine()) != null; ) {
				List<String> lineData = Arrays.asList(line.split(","));
				if(lineData.get(1).equals("testtextrandom")) {
					for(int i=2; i<lineData.size(); i+=3) {
						String webID = lineData.get(i);
						if(!textSiteData.containsKey(webID)) {
							dataList = new ArrayList<Integer>();
							dataList.add(0);
							dataList.add(0);
							dataList.add(0);
							dataList.add(0);
							if(lineData.get(i+2).equals("success")) {
								dataList.set(0, Integer.parseInt(lineData.get(i+1)));
								dataList.set(1, 1);
							}
							else if(lineData.get(i+2).equals("failure")) {
								dataList.set(2, Integer.parseInt(lineData.get(i+1)));
								dataList.set(3, 1);
							}
							textSiteData.put(webID, dataList);
						}
						else {
							dataList = (ArrayList<Integer>) textSiteData.get(webID).clone();
							
							if(lineData.get(i+2).equals("success")) {
								dataList.set(0, dataList.get(0) + Integer.parseInt(lineData.get(i+1)));
								dataList.set(1, dataList.get(1) + 1);
							}
							else if(lineData.get(i+2).equals("failure")) {
								dataList.set(2, dataList.get(2) + Integer.parseInt(lineData.get(i+1)));
								dataList.set(3, dataList.get(3) + 1);
							}
							textSiteData.put(webID, dataList);
						}
						
						if(lineData.get(i+2).equals("success")) {
							if(!textSitePassTests.containsKey(webID))
								textSitePassTests.put(webID, lineData.get(i+1));
							else
								textSitePassTests.put(webID, textSitePassTests.get(webID) + ","+lineData.get(i+1));
						}
						else if(lineData.get(i+2).equals("failure")) {
							if(!textSiteFailTests.containsKey(webID))
								textSiteFailTests.put(webID, lineData.get(i+1));
							else 
								textSiteFailTests.put(webID, textSiteFailTests.get(webID) + ","+lineData.get(i+1));
						}
					}
				}
				else if(lineData.get(1).equals("testpasstiles")) {
					for(int i=2; i<lineData.size(); i+=3) {
						String webID = lineData.get(i);
						if(!tilesSiteData.containsKey(webID)) {
							dataList = new ArrayList<Integer>();
							dataList.add(0);
							dataList.add(0);
							dataList.add(0);
							dataList.add(0);
							if(lineData.get(i+2).equals("success")) {
								dataList.set(0, Integer.parseInt(lineData.get(i+1)));
								dataList.set(1, 1);
							}
							else if(lineData.get(i+2).equals("failure")) {
								dataList.set(2, Integer.parseInt(lineData.get(i+1)));
								dataList.set(3, 1);
							}
							tilesSiteData.put(webID, dataList);
							
						}
						else {
							dataList = (ArrayList<Integer>) tilesSiteData.get(webID).clone();
							
							if(lineData.get(i+2).equals("success")) {
								dataList.set(0, dataList.get(0) + Integer.parseInt(lineData.get(i+1)));
								dataList.set(1, dataList.get(1) + 1);
							}
							else if(lineData.get(i+2).equals("failure")) {
								dataList.set(2, dataList.get(2) + Integer.parseInt(lineData.get(i+1)));
								dataList.set(3, dataList.get(3) + 1);
							}
							tilesSiteData.put(webID, dataList);
						}
						
						if(lineData.get(i+2).equals("success")) {
							if(!tilesSitePassTests.containsKey(webID))
								tilesSitePassTests.put(webID, lineData.get(i+1));
							else
								tilesSitePassTests.put(webID, tilesSitePassTests.get(webID) + ","+lineData.get(i+1));
						}
						else if(lineData.get(i+2).equals("failure")) {
							if(!tilesSiteFailTests.containsKey(webID))
								tilesSiteFailTests.put(webID, lineData.get(i+1));
							else 
								tilesSiteFailTests.put(webID, tilesSiteFailTests.get(webID) + ","+lineData.get(i+1));
						}
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(s2, "UTF-8"))) {
			for(String line; (line = br.readLine()) != null; ) {
				List<String> lineData = Arrays.asList(line.split(","));
				if(lineData.get(1).equals("testtextrandom")) {
					for(int i=2; i<lineData.size(); i+=3) {
						String webID = lineData.get(i);
						if(!textSiteCreates.containsKey(webID)) {
							dataList = new ArrayList<Integer>();
							dataList.add(Integer.parseInt(lineData.get(i+1)));
							dataList.add(Integer.parseInt(lineData.get(i+2)));
							textSiteCreates.put(webID, dataList);
							
						}
						else {
							dataList = (ArrayList<Integer>) textSiteCreates.get(webID).clone();
							dataList.set(0, dataList.get(0) + Integer.parseInt(lineData.get(i+1)));
							dataList.set(1, dataList.get(1) + Integer.parseInt(lineData.get(i+2)));
							textSiteCreates.put(webID, dataList);
						}
					}
				}
				else if(lineData.get(1).equals("testpasstiles")) {
					for(int i=2; i<lineData.size(); i+=3) {
						String webID = lineData.get(i);
						if(!tilesSiteCreates.containsKey(webID)) {
							dataList = new ArrayList<Integer>();
							dataList.add(Integer.parseInt(lineData.get(i+1)));
							dataList.add(Integer.parseInt(lineData.get(i+2)));
							tilesSiteCreates.put(webID, dataList);
							
						}
						else {
							dataList = (ArrayList<Integer>) tilesSiteCreates.get(webID).clone();
							dataList.set(0, dataList.get(0) + Integer.parseInt(lineData.get(i+1)));
							dataList.set(1, dataList.get(1) + Integer.parseInt(lineData.get(i+2)));
							tilesSiteCreates.put(webID, dataList);
						}
					}
				}
			}
			//assemble all collected data 
			generateSiteLog(textSiteData, tilesSiteData, textSiteCreates, tilesSiteCreates, textSitePassTests, textSiteFailTests, tilesSitePassTests, tilesSiteFailTests);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//takes all of the data collected and organizes/writes to dataBySite.csv
	private void generateSiteLog(Map<String, ArrayList<Integer>> textSiteData, Map<String, ArrayList<Integer>> tilesSiteData,
			Map<String, ArrayList<Integer>> textSiteCreates, Map<String, ArrayList<Integer>> tilesSiteCreates,
			Map<String, String> textSitePassTests, Map<String, String> textSiteFailTests, 
			Map<String, String> tilesSitePassTests, Map<String, String> tilesSiteFailTests) {
		
		String content = "";
		String line = "";
		for(String webID : textSiteData.keySet()) {
			line += (webID + ",");
			line += ("testtextrandom,");
			line += (textSiteData.get(webID).get(1) + textSiteData.get(webID).get(3) + ",");
			line += (textSiteData.get(webID).get(1) + ",");
			line += (textSiteData.get(webID).get(3) + ",");
			line += (textSiteData.get(webID).get(0) + textSiteData.get(webID).get(2) + ",");
			line += (textSiteData.get(webID).get(0) + ",");
			line += (textSiteData.get(webID).get(2) + ",");
			line += (textSiteCreates.get(webID).get(0) + ",");
			line += (textSiteCreates.get(webID).get(1) + ",");
			line += ("\"" + textSitePassTests.get(webID) + "," + textSiteFailTests.get(webID) + "\",");
			line += ("\"" + textSitePassTests.get(webID) + "\",");
			line += ("\"" + textSiteFailTests.get(webID) + "\",");
			line += "\n";
		}
		content += line;
		
		
		line = "";
		for(String webID : tilesSiteData.keySet()) {
			line += (webID + ",");
			line += ("testpasstiles,");
			line += (tilesSiteData.get(webID).get(1) + textSiteData.get(webID).get(3) + ",");
			line += (tilesSiteData.get(webID).get(1) + ",");
			line += (tilesSiteData.get(webID).get(3) + ",");
			line += (tilesSiteData.get(webID).get(0) + tilesSiteData.get(webID).get(2) + ",");
			line += (tilesSiteData.get(webID).get(0) + ",");
			line += (tilesSiteData.get(webID).get(2) + ",");
			line += (tilesSiteCreates.get(webID).get(0) + ",");
			line += (tilesSiteCreates.get(webID).get(1) + ",");
			line += ("\"" + tilesSitePassTests.get(webID) + "," + tilesSiteFailTests.get(webID) + "\",");
			line += ("\"" + tilesSitePassTests.get(webID) + "\",");
			line += ("\"" + tilesSiteFailTests.get(webID) + "\",");
			line += "\n";
		}
		content += line;
		
		//File file = new File("./src/com/comp_3008/dataBySite.csv");
		File file = new File("./com/comp_3008/dataBySite.csv");
		
		for(int i = 0; i<data.size(); i++) {
			if(i == data.size()-1)
				content += data.get(i)+"\n";
			else
				content += data.get(i)+",";
		}

		try (FileOutputStream fop = new FileOutputStream(file, true)) {

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Generator g = new Generator();
		
		//the CSV data files that will be generated
		//File file = new File("./src/com/comp_3008/dataByUser.csv");
		//File file2 = new File("./src/com/comp_3008/dataBySite.csv");
		//File file3 = new File("./src/com/comp_3008/tempCreateData.csv");
		//File file4 = new File("./src/com/comp_3008/tempTestData.csv");
		File file = new File("./com/comp_3008/dataByUser.csv");
		File file2 = new File("./com/comp_3008/dataBySite.csv");
		File file3 = new File("./com/comp_3008/tempCreateData.csv");
		File file4 = new File("./com/comp_3008/tempTestData.csv");
		
		//delete previous versions of the CSV files if they exist
		try {
			Files.deleteIfExists(file.toPath());
			Files.deleteIfExists(file2.toPath());
			Files.deleteIfExists(file3.toPath());
			Files.deleteIfExists(file4.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//generate dataByUser.csv from the two log files
		g.generateUserHeaders();
		g.getDataByUser("Text21.csv");
		g.getDataByUser("Imagept21.csv");
		
		//generate dataBySite.csv from the dataByUser.csv file
		g.generateSiteHeaders();
		g.getDataBySite("dataByUser.csv");
		
		try {
			Files.deleteIfExists(file3.toPath());
			Files.deleteIfExists(file4.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
