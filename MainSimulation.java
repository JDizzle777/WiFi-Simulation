/*
 *  WiFi LAN Simulation
 *  Justin Cartlidge
 *  Fall 2015
 */

import java.io.IOException;
import java.util.Scanner;

public class MainSimulation {
	public static int numCollisions = 0;
	public static int totalRunTime = 0;
	public static double lightspeed = 3 * Math.pow(10, 8);
	public static boolean simulationRunning = false;
	
	////////////// __MAIN__ ///////////////////////////////////////////////////////
	public static void main(String[] args) {
		// variables
		boolean ProgramRun = true;
		String userSelection;
		long programRunTime = 60000;
		Scanner userInput = new Scanner(System.in);
		
		// create our LAN WiFi Access Point (AP)
		AccessPoint WiFiAP = new AccessPoint("MyCharterWiFi24-2G", "192.168.1.1", "24.182.30.120");
		
		// create the homeowner wireless devices
		WirelessDevice myIPhone = new WirelessDevice("Justin's iPhone");
		WirelessDevice andrewIPhone = new WirelessDevice("Andrew's Android S4");
		WirelessDevice laptop = new WirelessDevice("Mitch's Macbook");
		
		// connect the homeowner devices to the wifi
		myIPhone.connectToAP(WiFiAP);
		andrewIPhone.connectToAP(WiFiAP);
		laptop.connectToAP(WiFiAP);
		
		// display main menu
		do{
			// get user input from menu
			userSelection = DisplayMenu(userInput, programRunTime);
			
			// do what user selected
			switch(userSelection){
			case "1":
				totalRunTime += programRunTime / 1000;
				StartSimulation(WiFiAP, WiFiAP.getHosts(), programRunTime);
				try {
					System.in.read();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "2":
				showDeviceAttributes(WiFiAP.getHosts());
				try {
					System.in.read();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "3":
				showWifiAttributes(WiFiAP);
				try {
					System.in.read();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "4":
				addUserToNetwork(WiFiAP, userInput);
				try {
					System.in.read();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "5":
				programRunTime = setRunTime(userInput);
				try {
					System.in.read();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "6":
			case "Q":
			case "q":
				ProgramRun = false;
				for(int i = 0; i < 10; i++){
					System.out.println();
				}
				userInput.close();
				
			}
			
			
		}
		while(ProgramRun);
	}


	//////////////////////////// SIMULATION //////////////////////////////////////////////////
	private static void StartSimulation(AccessPoint AP, WirelessDevice[] devices, long time){
		// variables
		simulationRunning = true;
		Thread threads[] = new Thread[devices.length];
		int length = AP.getNumHosts();
		
		//start threads for each wireless device
		for(int i = 0; i < length; i++){
			threads[i] = new Thread(devices[i]);
			devices[i].setRunTime(time);
			threads[i].start();
		}
	
		// wait for them to finish
		for(int i = 0; i < length; i++){
			while(devices[i].running);
		}
		// display results
		displayResults(devices);
		System.out.println("Press <ENTER> to return to main menu. . .");
		System.out.println();
		System.out.println();
	}


	///////////////////////////////////////////////////////////////////////////
	private static void displayResults(WirelessDevice[] devices) {
		// variables
		int length = devices[0].getAP().getNumHosts();
		
		// clear screen
		for(int i = 0; i < 7; i++){
			System.out.println();
		}
		// print results
		System.out.println("----------------------Simulation Results---------------------");
		System.out.println("Total simulation run time = " + totalRunTime + " seconds.");
		System.out.println("Total number of collisions in CSMA/CA = " + numCollisions + '.');
		
		// print device stats
		for(int i = 0; i < length; i++){
			System.out.println(devices[i].Name + ": ");
			System.out.println("     Average throughput = " + devices[i].getThroughput()/1000 + " Mbps.");
			System.out.println("     Distance away from access point = " + devices[i].getDistance() + " meters with " + devices[i].obstacles + " obstacle(s).");
			System.out.println("     Signal Strength = " + devices[i].getSignalStrength() + "%.");
		}	
		System.out.println();
	}


	///////////////////////////////////////////////////////////////////////////
	public static String DisplayMenu(Scanner userInput, long runTime){
		// variables
		String retVal;
		
		// display
		System.out.println();
		System.out.println("1.) Start (or Continue) Simulation (simulation time = " + runTime/1000 + " seconds)");
		System.out.println("2.) Show device attributes");
		System.out.println("3.) Show WiFi attributes");
		System.out.println("4.) Add a wireless device to the network");
		System.out.println("5.) Set simulation run time");
		System.out.println("6.) <Q>uit");
		
		// get input
		retVal = userInput.next();
		
		// return
		return retVal;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public static void showWifiAttributes(AccessPoint AP){
		// clear screen
		for(int i = 0; i < 7; i++){
			System.out.println();
		}
		
		// display wifi info
		System.out.println("--------------------WiFi Information------------------");
		System.out.println("SSID: " + outputSpaces(6, 30) + AP.getSSID());
		System.out.println("Physical Address: " + outputSpaces(17, 30) + AP.getMAC());
		System.out.println("Local IPv4 Address: " + outputSpaces(20, 30) + AP.getLocalIP());
		System.out.println("Public IPv4 Address: " + outputSpaces(21, 30) + AP.getPublicIP());
		System.out.println("Subnet Mask: " + outputSpaces(13, 30) + "255.255.255.0");
		System.out.println();
		System.out.println("Press <ENTER> to return to main menu. . .");
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public static void showDeviceAttributes(WirelessDevice []devices){
		// clear screen
		int length = devices[0].getAP().getNumHosts();
		for(int i = 0; i < 7; i++){
			System.out.println();
		}
		
		// display device info
		for(int i = 0; i < length; i++){
			System.out.println("-----------------Wireless Device Information---------------");
			System.out.println("Name: " + devices[i].Name);
			System.out.println("    Signal Strength: " + outputSpaces(21, 30) + devices[i].getSignalStrength() + '%');
			System.out.println("    Access point: " + outputSpaces(18, 30) + devices[i].getAP().getSSID());
			System.out.println("    Physical Address: " + outputSpaces(22, 30) + devices[i].getMAC());
			System.out.println("    Local IPv4 Address: " + outputSpaces(24, 30) + devices[i].getLocalIP());
			System.out.println("    Public IPv4 Address: " + outputSpaces(25, 30) + devices[i].getPublicIP());
			System.out.println("    Distance (m) from AP: " + outputSpaces(26, 30) + devices[i].getDistance() + "");
			System.out.println("    Average Throughput: " + outputSpaces(24, 30) + devices[i].getThroughput() + " Mbps");
			System.out.println();	
		}
		System.out.println();
		System.out.println("Press <ENTER> to return to main menu. . .");
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	private static long setRunTime(Scanner userInput) {
		// variables
		String retVal;
		long newTime;
		
		// clear screen
		for(int i = 0; i < 7; i++){
			System.out.println();
		}
		
		System.out.print("Enter new simulation run time value (in seconds): ");
		retVal = userInput.next();
		newTime = (long)Integer.parseInt(retVal) * 1000;
		System.out.println();
		System.out.println("New simulation run time is " + newTime/1000 + " seconds.");
		
		// clear screen
		System.out.println();
		System.out.println("Press <ENTER> to return to main menu. . .");
		return newTime;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	public static void addUserToNetwork(AccessPoint AP, Scanner userInput){
		// vars
		String name = "";
		
		// clear screen
		for(int i = 0; i < 7; i++){
			System.out.println();
		}
		System.out.println("Give the name of the device which you would like to add to the WiFi LAN: ");
		while(userInput.nextLine() == "");
		name = userInput.nextLine();
		WirelessDevice newDevice = new WirelessDevice(name);
		newDevice.connectToAP(AP);
		System.out.println(name + " successfully added to " + AP.getSSID() + " WiFi LAN.");
		System.out.println();
		System.out.println("Press <ENTER> to return to main menu. . .");
	}
	
	
	public static String outputSpaces(int length, int numSpaces){
		String retVal = "";
		for(int i = 0; i < (numSpaces-length); i++){
			retVal += ".";
		}
		return retVal;
	}
}
