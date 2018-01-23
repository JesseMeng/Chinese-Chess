package chinesechess;

import java.util.*;
import java.net.*;
import java.io.*;

public class AgentServerThread extends Thread{
	//the main server who assigned this agent
	Server father;
	Socket socket;
	// data input and output streams
	DataInputStream input;
	DataOutputStream output;
	//thread indicator
	boolean connected = true;
	//Constructor
	public AgentServerThread(Server father,Socket sc){
		this.father = father;
		this.socket = sc;
		try{
			input = new DataInputStream(sc.getInputStream());
			output = new DataOutputStream(sc.getOutputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(connected){
			try{
				String msg = input.readUTF().trim();//take in message from client
				if(msg.startsWith("<#NEW__USER#>")){//new user
					this.newUser(msg);
				}
				else if(msg.startsWith("<#CLIENT_LEAVE#>")){//user leaves
					this.clientLeave(msg);
				}
				else if(msg.startsWith("<#CHALLENGE#>")){//challenge
					this.challenge(msg);
				}
				else if(msg.startsWith("<#CHALACC#>")){//challenge accepted
					this.acceptChallenge(msg);
				}
				else if(msg.startsWith("<#CHAREJECT#>")){//challenge rejected
					this.declineChallenge(msg);
				}
				else if(msg.startsWith("<#BUSY#>")){//other player is busy
					this.busy(msg);
				}
				else if(msg.startsWith("<#MOVE#>")){//a player's move
					this.move(msg);
				}
				else if(msg.startsWith("<#GIVEUP#>")){//surrender
					this.surrender(msg);
				}	
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void newUser(String msg){
		try{
			String name = msg.substring(13);
			this.setName(name);
			Vector v = father.users;
			boolean nameExists = false;
			int size = v.size();
			//search to see if name already exists
			for(int i = 0;i < size; ++i){
				AgentServerThread tempSat = (AgentServerThread)v.get(i);
				if(tempSat.getName().equals(name)){
					nameExists = true;
					break;
				}
			}
			if(nameExists){
				output.writeUTF("<#NAMEEXISTALREA#>");
				input.close();
				output.close();
				socket.close();
				connected = false;
			}
			else{
				v.add(this);
				father.refreshUsers();
				String nickListMsg = "";
				size=v.size();
				//format the concatenated string
				for(int i = 0;i < size; ++i){
					AgentServerThread tempSat = (AgentServerThread)v.get(i);
					nickListMsg = nickListMsg+"|"+tempSat.getName();
				}
				nickListMsg = "<#NICK_LIST#>"+nickListMsg;
				Vector tempv = father.users;
				size = tempv.size();
				//send new user list and online message to everyone
				for(int i = 0; i < size; ++i){
					AgentServerThread satTemp = (AgentServerThread)tempv.get(i);
					satTemp.output.writeUTF(nickListMsg);
					if(satTemp != this){
						satTemp.output.writeUTF("<#MSG#>"+this.getName()+" is online!");
					}
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void clientLeave(String msg){
		try{
			Vector tempv = father.users;
			tempv.remove(this);
			int size = tempv.size();
			String nl = "<#NICK_LIST#>";
			//send offline message and get refreshed list of users
			
			for(int i = 0; i < size; ++i){
				AgentServerThread satTemp = (AgentServerThread)tempv.get(i);
				satTemp.output.writeUTF("<#MSG#>"+this.getName()+" is offline!");
				nl = nl+"|"+satTemp.getName();
			}
			for(int i = 0; i < size; ++i){
				AgentServerThread satTemp = (AgentServerThread)tempv.get(i);
				satTemp.output.writeUTF(nl);
			}
			this.connected = false;
			father.refreshUsers();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void challenge(String msg){
		try{
			String name1 = this.getName();//challenger's name
			String name2 = msg.substring(13);//challenged player's name
			Vector v = father.users;
			int size = v.size();
			//search for the user who is being challenged
			for(int i = 0;i < size; ++i){
				AgentServerThread satTemp = (AgentServerThread)v.get(i);
				if(satTemp.getName().equals(name2)){
					satTemp.output.writeUTF("<#CHALLENGE#>"+name1);
					break;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void acceptChallenge(String msg){
		try{
			String name = msg.substring(11);//challenger's name
			Vector v = father.users;
			int size = v.size();
			//locate the user
			for(int i = 0; i < size; ++i){
				AgentServerThread satTemp = (AgentServerThread)v.get(i);
				if(satTemp.getName().equals(name)){
					satTemp.output.writeUTF("<#CHALACC#>");
					break;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void declineChallenge(String msg){
		try{
			String name = msg.substring(13);//challenger's name
			Vector v = father.users;
			int size = v.size();
			//locate the user
			for(int i = 0; i < size; ++i){
				AgentServerThread satTemp = (AgentServerThread)v.get(i);
				if(satTemp.getName().equals(name)){
					satTemp.output.writeUTF("<#CHAREJECT#>");
					break;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void busy(String msg){
		try{
			String name = msg.substring(8);//challenger's name
			Vector v = father.users;
			int size = v.size();
			//locate the user
			for(int i = 0; i < size; ++i){
				AgentServerThread satTemp = (AgentServerThread)v.get(i);
				if(satTemp.getName().equals(name)){
					satTemp.output.writeUTF("<#BUSY#>");
					break;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void move(String msg){
		try{
			String name = msg.substring(8,msg.length()-4);//opponent's name
			Vector v = father.users;
			int size = v.size();
		    //locate opponent
			for(int i = 0; i < size; ++i){
				AgentServerThread satTemp = (AgentServerThread)v.get(i);
				if(satTemp.getName().equals(name)){
					satTemp.output.writeUTF(msg);
					break;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void surrender(String msg){
		try{
			String name = msg.substring(10);//opponent's name
			Vector v = father.users;
			int size = v.size();
			//locate the user
			for(int i = 0; i < size; ++i){
				AgentServerThread satTemp = (AgentServerThread)v.get(i);
				if(satTemp.getName().equals(name)){
					satTemp.output.writeUTF(msg);
					break;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}