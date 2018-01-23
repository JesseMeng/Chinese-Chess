package chinesechess;

import javax.swing.*;
import java.util.*;
import java.io.*;

public class AgentclientThread extends Thread{
	//the main client who assigned this agent
	ChineseChess client;
	boolean connected = true;
	DataInputStream input;
	DataOutputStream output;
	String challenger=null;
	//Constructor
	public AgentclientThread(ChineseChess client){
		this.client = client;
		try{
			input = new DataInputStream(client.socket.getInputStream());
			output = new DataOutputStream(client.socket.getOutputStream());
			String name = client.nickNameT.getText().trim();
			output.writeUTF("<#NEW__USER#>"+name);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(connected){
			try{
				String msg = input.readUTF().trim();
				if(msg.startsWith("<#NAMEEXISTALREA#>")){
					this.nameExists();
				}
				else if(msg.startsWith("<#NICK_LIST#>")){
					this.nickList(msg);
				}
				else if(msg.startsWith("<#SERVER_DOWN#>")){
					this.serverDown();
				}
				else if(msg.startsWith("<#CHALLENGE#>")){
					this.challenge(msg);
				}
				else if(msg.startsWith("<#CHALACC#>")){
					this.accept();	
				}
				else if(msg.startsWith("<#CHAREJECT#>")){
					this.decline();
				}
				else if(msg.startsWith("<#BUSY#>")){
					this.busy();
				}
				else if(msg.startsWith("<#MOVE#>")){
					this.movePiece(msg);
				}
				else if(msg.startsWith("<#GIVEUP#>")){
					this.surrender();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void nameExists(){
		try{
			JOptionPane.showMessageDialog(this.client,"Name already exists!", "Error!",JOptionPane.ERROR_MESSAGE);
			input.close();
			output.close();
			this.client.hostT.setEnabled(!false);
			this.client.portT.setEnabled(!false);
			this.client.nickNameT.setEnabled(!false);
			this.client.connect.setEnabled(!false);
			this.client.disconnect.setEnabled(!true);
			this.client.challenge.setEnabled(!true);
			this.client.acceptChallenge.setEnabled(false);
			this.client.declineChallenge.setEnabled(false);
			this.client.surrender.setEnabled(false);
			client.socket.close();
			client.socket = null;
			client.act = null;
			connected = false;
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void nickList(String msg){
		String s = msg.substring(13);
		String[] na = s.split("\\|");
		Vector v = new Vector();
		//process the user-list string
		for(int i = 0; i < na.length; ++i){
			if((na[i].trim().length() != 0) && (!na[i].trim().equals(client.nickNameT.getText().trim()))){
				v.add(na[i]);
			}
		}
		client.otherUsersList.setModel(new DefaultComboBoxModel(v));
	}
	
	public void serverDown(){
		this.client.hostT.setEnabled(!false);
		this.client.portT.setEnabled(!false);
		this.client.nickNameT.setEnabled(!false);
		this.client.connect.setEnabled(!false);
		this.client.disconnect.setEnabled(!true);
		this.client.challenge.setEnabled(!true);
		this.client.acceptChallenge.setEnabled(false);
		this.client.declineChallenge.setEnabled(false);
		this.client.surrender.setEnabled(false);
		this.connected = false;
		client.act = null;
		JOptionPane.showMessageDialog(this.client,"Server stopped!","Message", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void challenge(String msg){
		try{
			String name = msg.substring(13);
			if(this.challenger == null){//the player is not currently playing anyone
				challenger = msg.substring(13);
				this.client.hostT.setEnabled(false);
				this.client.portT.setEnabled(false);
				this.client.nickNameT.setEnabled(false);
				this.client.connect.setEnabled(false);
				this.client.disconnect.setEnabled(!true);
				this.client.challenge.setEnabled(!true);
				this.client.acceptChallenge.setEnabled(!false);
				this.client.declineChallenge.setEnabled(!false);
				this.client.surrender.setEnabled(false);
				JOptionPane.showMessageDialog(this.client,challenger+"challenges you!", "Message",JOptionPane.INFORMATION_MESSAGE);
			}
			else{//if the player is busy
				this.output.writeUTF("<#BUSY#>"+name);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void accept(){
		this.client.hostT.setEnabled(false);
		this.client.portT.setEnabled(false);
		this.client.nickNameT.setEnabled(false);
		this.client.connect.setEnabled(false);
		this.client.disconnect.setEnabled(!true);
		this.client.challenge.setEnabled(!true);
		this.client.acceptChallenge.setEnabled(false);
		this.client.declineChallenge.setEnabled(false);
		this.client.surrender.setEnabled(!false);
		JOptionPane.showMessageDialog(this.client,"Your challenge was accepted! You(red) start!", "Message",JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void decline(){
		this.client.myTurn = false;
		this.client.myColor = 0;
		this.client.hostT.setEnabled(false);
		this.client.portT.setEnabled(false);
		this.client.nickNameT.setEnabled(false);
		this.client.connect.setEnabled(false);
		this.client.disconnect.setEnabled(true);
		this.client.challenge.setEnabled(true);
		this.client.acceptChallenge.setEnabled(false);
		this.client.declineChallenge.setEnabled(false);
		this.client.surrender.setEnabled(false);
		JOptionPane.showMessageDialog(this.client,"Your challenge was declined!","Message", JOptionPane.INFORMATION_MESSAGE);
		this.challenger = null;
	}
	
	public void busy(){
		this.client.myTurn = false;
		this.client.myColor = 0;
		this.client.hostT.setEnabled(false);
		this.client.portT.setEnabled(false);
		this.client.nickNameT.setEnabled(false);
		this.client.connect.setEnabled(false);
		this.client.disconnect.setEnabled(true);
		this.client.challenge.setEnabled(true);
		this.client.acceptChallenge.setEnabled(false);
		this.client.declineChallenge.setEnabled(false);
		this.client.surrender.setEnabled(false);
		JOptionPane.showMessageDialog(this.client,"He/she is busy atm!","Message", JOptionPane.INFORMATION_MESSAGE);
		this.challenger = null;
	}
	
	public void movePiece(String msg){
		int length = msg.length();
		int startI = Integer.parseInt(msg.substring(length-4,length-3));
		int startJ = Integer.parseInt(msg.substring(length-3,length-2));
		int endI = Integer.parseInt(msg.substring(length-2,length-1));
		int endJ = Integer.parseInt(msg.substring(length-1));
		this.client.board.move(startI,startJ,endI,endJ);
		this.client.myTurn = true;
	}
	
	public void surrender(){
		JOptionPane.showMessageDialog(this.client,"Congrats! Your opponent surrendered.","Message", JOptionPane.INFORMATION_MESSAGE);
		this.challenger = null;
		this.client.myColor = 0;
		this.client.myTurn = false;
		this.client.next();
		this.client.hostT.setEnabled(false);
		this.client.portT.setEnabled(false);
		this.client.nickNameT.setEnabled(false);
		this.client.connect.setEnabled(false);
		this.client.disconnect.setEnabled(true);
		this.client.challenge.setEnabled(true);
		this.client.acceptChallenge.setEnabled(false);
		this.client.declineChallenge.setEnabled(false);
		this.client.surrender.setEnabled(false);
	}
	
	
}
