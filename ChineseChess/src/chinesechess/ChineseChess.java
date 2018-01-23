package chinesechess;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;

public class ChineseChess extends JFrame implements ActionListener{
	
	public static final Color backGround = new Color(245,250,160);
	public static final Color selectedBackground = new Color(242,242,242);
	public static final Color selectedTextBackground = new Color(96,95,91);
	public static final Color redColor = new Color(249,183,172);
	public static final Color whiteColor= Color.white;
	//graphical user interface setup
	JLabel hostLabel = new JLabel("HostIP");
	JLabel portLabel = new JLabel("Port");
	JLabel nickName = new JLabel("Name");
	JTextField hostT = new JTextField("127.0.0.1");//default
	JTextField portT = new JTextField("1111");//default
	JTextField nickNameT = new JTextField("Player1");//default
	JButton connect = new JButton("Connect");
	JButton disconnect = new JButton("Disconnect");
	JButton surrender = new JButton("surrender");
	JButton challenge = new JButton("challenge");
	JComboBox otherUsersList = new JComboBox();
	JButton acceptChallenge = new JButton("Accept");
	JButton declineChallenge = new JButton("Decline");
	int width = 60;//distance between lines
	ChessPiece[][] chessPieces = new ChessPiece[9][10];
	Board board = new Board(chessPieces, width, this);
	JPanel jpRight = new JPanel();
	JSplitPane spane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, board, jpRight);
	
	boolean myTurn = false;
	int myColor = 0;//0 is red, 1 is white
	Socket socket;
	AgentclientThread act;
	
	public ChineseChess(){
		this.initialComponent();//add components
		this.addComponentListener();//add listeners
		this.intialState();
		this.initialPieces();
		this.initialFrame();
	}
	
	public void initialComponent(){
		jpRight.setLayout(null);
		this.hostLabel.setBounds(10,10,50,20);
		jpRight.add(this.hostLabel);
		this.hostT.setBounds(70,10,80,20);
		jpRight.add(this.hostT);
		this.portLabel.setBounds(10,40,50,20);
		jpRight.add(this.portLabel);
		this.portT.setBounds(70,40,80,20);
		jpRight.add(this.portT);
		this.nickName.setBounds(10,70,50,20);
		jpRight.add(this.nickName);
		this.nickNameT.setBounds(70,70,80,20);
		jpRight.add(this.nickNameT);
		this.connect.setBounds(10,100,80,20);
		jpRight.add(this.connect);
		this.disconnect.setBounds(100,100,80,20);
		jpRight.add(this.disconnect);
		this.otherUsersList.setBounds(20,130,130,20);
		jpRight.add(this.otherUsersList);
		this.challenge.setBounds(10,160,80,20);
		jpRight.add(this.challenge);
		this.surrender.setBounds(100,160,80,20);
		jpRight.add(this.surrender);
		this.acceptChallenge.setBounds(5,190,86,20);
		jpRight.add(this.acceptChallenge);
		this.declineChallenge.setBounds(100,190,86,20);
		jpRight.add(this.declineChallenge);
		board.setLayout(null);
		board.setBounds(0,0,700,700);
	}
	
	public void addComponentListener(){
		this.connect.addActionListener(this);
		this.disconnect.addActionListener(this);
		this.challenge.addActionListener(this);
		this.surrender.addActionListener(this);
		this.acceptChallenge.addActionListener(this);
		this.declineChallenge.addActionListener(this);
	}
	
	public void intialState(){
		this.disconnect.setEnabled(false);
		this.challenge.setEnabled(false);
		this.acceptChallenge.setEnabled(false);
		this.declineChallenge.setEnabled(false);
		this.surrender.setEnabled(false);
	}
	
	public void initialPieces(){
		chessPieces[0][0] = new ChessPiece(redColor,"Ü‡",0,0);
		chessPieces[1][0] = new ChessPiece(redColor,"ñR",1,0);
		chessPieces[2][0] = new ChessPiece(redColor,"Ïà",2,0);
		chessPieces[3][0] = new ChessPiece(redColor,"ÊË",3,0);
		chessPieces[4][0] = new ChessPiece(redColor,"Ž›",4,0);
		chessPieces[5][0] = new ChessPiece(redColor,"ÊË",5,0);
		chessPieces[6][0] = new ChessPiece(redColor,"Ïà",6,0);
		chessPieces[7][0] = new ChessPiece(redColor,"ñR",7,0);
		chessPieces[8][0] = new ChessPiece(redColor,"Ü‡",8,0);
		chessPieces[1][2] = new ChessPiece(redColor,"³h",1,2);
		chessPieces[7][2] = new ChessPiece(redColor,"³h",7,2);
		chessPieces[0][3] = new ChessPiece(redColor,"±ø",0,3);
		chessPieces[2][3] = new ChessPiece(redColor,"±ø",2,3);
		chessPieces[4][3] = new ChessPiece(redColor,"±ø",4,3);
		chessPieces[6][3] = new ChessPiece(redColor,"±ø",6,3);
		chessPieces[8][3] = new ChessPiece(redColor,"±ø",8,3);
		chessPieces[0][9] = new ChessPiece(whiteColor,"Ü‡",0,9);
		chessPieces[1][9] = new ChessPiece(whiteColor,"ñR",1,9);
		chessPieces[2][9] = new ChessPiece(whiteColor,"Ïó",2,9);
		chessPieces[3][9] = new ChessPiece(whiteColor,"Ê¿",3,9);
		chessPieces[4][9] = new ChessPiece(whiteColor,"Œ¢",4,9);
		chessPieces[5][9] = new ChessPiece(whiteColor,"Ê¿",5,9);
		chessPieces[6][9] = new ChessPiece(whiteColor,"Ïó",6,9);
		chessPieces[7][9] = new ChessPiece(whiteColor,"ñR",7,9);
		chessPieces[8][9] = new ChessPiece(whiteColor,"Ü‡",8,9);
		chessPieces[1][7] = new ChessPiece(whiteColor,"ÅÚ",1,7);
		chessPieces[7][7] = new ChessPiece(whiteColor,"ÅÚ",7,7);
		chessPieces[0][6] = new ChessPiece(whiteColor,"×ä",0,6);
		chessPieces[2][6] = new ChessPiece(whiteColor,"×ä",2,6);
		chessPieces[4][6] = new ChessPiece(whiteColor,"×ä",4,6);
		chessPieces[6][6] = new ChessPiece(whiteColor,"×ä",6,6);
		chessPieces[8][6] = new ChessPiece(whiteColor,"×ä",8,6);
	}
	
	public void initialFrame(){
		this.setTitle("Chinese chess!");
		this.add(this.spane);
		spane.setDividerLocation(730);
		spane.setDividerSize(4);
		this.setBounds(30,30,930,730);
		this.setVisible(true);
		
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					if(act == null){
						System.exit(0);
						return;
					}
					try{
						if(act.challenger != null){//playing with someone
							try{
								//surrender message
								act.output.writeUTF("<#GIVEUP#>"+act.challenger);
							}
							catch(Exception ee){
								ee.printStackTrace();
							}
						}
						act.output.writeUTF("<#CLIENT_LEAVE#>");//leave message
						act.connected = false;//end client
						act = null;	
					}
					catch(Exception ee){
						ee.printStackTrace();
					}
					System.exit(0);
				}
				
			});
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == this.connect){
			this.connectEvent();
		}else if(e.getSource() == this.disconnect){
			this.disconnectEvent();
		}else if(e.getSource() == this.challenge){
			this.challengeEvent();
		}else if(e.getSource() == this.acceptChallenge){
			this.acceptChallengeEvent();
		}else if(e.getSource() == this.declineChallenge){
			this.declineChallengeEvent();
		}else if(e.getSource() == this.surrender){
			this.surrenderEvent();
		}
	}
	
	public void connectEvent(){
		int port = 0;
		try{//get port id
			port=Integer.parseInt(this.portT.getText().trim());
		}catch(Exception ee){//not whole number
			JOptionPane.showMessageDialog(this,"Whole numbers only","Error!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(port > 65535 || port < 0){
			JOptionPane.showMessageDialog(this,"Port id should be from 0 to 65535","Error!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String name = this.nickNameT.getText().trim();//get nick name
		if(name.length() == 0){//empty nick name
			JOptionPane.showMessageDialog(this,"Name cannot be empty!","Error!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			//adjusting setup
			socket=new Socket(this.hostT.getText().trim(),port);
			act=new AgentclientThread(this);
			act.start();	
			this.hostT.setEnabled(false);
			this.portT.setEnabled(false);
			this.nickNameT.setEnabled(false);
			this.connect.setEnabled(false);
			this.disconnect.setEnabled(true);
			this.challenge.setEnabled(true);
			this.acceptChallenge.setEnabled(false);
			this.declineChallenge.setEnabled(false);
			this.surrender.setEnabled(false);
			JOptionPane.showMessageDialog(this,"Connected to server!","Message", JOptionPane.INFORMATION_MESSAGE);
		}
		catch(Exception ee){
			JOptionPane.showMessageDialog(this,"Fssil to connect!","Error!", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	public void disconnectEvent(){
		try{
			//changing setup
			this.act.output.writeUTF("<#CLIENT_LEAVE#>");//send offline message
			this.act.connected = false;//end client agent
			this.act = null;
			this.hostT.setEnabled(!false);
			this.portT.setEnabled(!false);
			this.nickNameT.setEnabled(!false);
			this.connect.setEnabled(!false);
			this.disconnect.setEnabled(!true);
			this.challenge.setEnabled(!true);
			this.acceptChallenge.setEnabled(false);
			this.declineChallenge.setEnabled(false);
			this.surrender.setEnabled(false);
		}
		catch(Exception ee){
			ee.printStackTrace();
		}
	}
	
	public void challengeEvent(){
		//receive the selected opponent
		Object o = this.otherUsersList.getSelectedItem();
		if(o == null || ((String)o).equals("")) {
			JOptionPane.showMessageDialog(this,"Please choose the opponent's name","Error!", JOptionPane.ERROR_MESSAGE);
		}
		else{
			String opponent=(String)this.otherUsersList.getSelectedItem();
			try{
				this.hostT.setEnabled(false);
				this.portT.setEnabled(false);
				this.nickNameT.setEnabled(false);
				this.connect.setEnabled(false);
				this.disconnect.setEnabled(!true);
				this.challenge.setEnabled(!true);
				this.acceptChallenge.setEnabled(false);
				this.declineChallenge.setEnabled(false);
				this.surrender.setEnabled(false);
				this.act.challenger = opponent;
				this.myTurn = true;
				this.myColor = 0;
				this.act.output.writeUTF("<#CHALLENGE#>"+opponent);
				JOptionPane.showMessageDialog(this,"Challenge sent","message", JOptionPane.INFORMATION_MESSAGE);
			}
			catch(Exception ee){
				ee.printStackTrace();
			}
		}
	}
	
	public void acceptChallengeEvent(){
		try{	//deliver accept message
			this.act.output.writeUTF("<#CHALACC#>"+this.act.challenger);
			this.myTurn = false;
			this.myColor=1;
			this.hostT.setEnabled(false);
			this.portT.setEnabled(false);
			this.nickNameT.setEnabled(false);
			this.connect.setEnabled(false);
			this.disconnect.setEnabled(!true);
			this.challenge.setEnabled(!true);
			this.acceptChallenge.setEnabled(false);
			this.declineChallenge.setEnabled(false);
			this.surrender.setEnabled(!false);
		}
		catch(Exception ee){
			ee.printStackTrace();
		}
	}
	
	public void declineChallengeEvent(){
		try{//deliver decline message
			this.act.output.writeUTF("<#CHAREJECT#>"+this.act.challenger);
			this.act.challenger = null;
			this.hostT.setEnabled(false);
			this.portT.setEnabled(false);
			this.nickNameT.setEnabled(false);
			this.connect.setEnabled(false);
			this.disconnect.setEnabled(true);
			this.challenge.setEnabled(true);
			this.acceptChallenge.setEnabled(false);
			this.declineChallenge.setEnabled(false);
			this.surrender.setEnabled(false);
		}
		catch(Exception ee){
			ee.printStackTrace();
		}
	}
	
	public void surrenderEvent(){
		try{   //deliver surrender message
			this.act.output.writeUTF("<#GIVEUP#>"+this.act.challenger);
			this.act.challenger = null;
			this.myColor = 0;
			this.myTurn = false;
			this.next();//prepare for next round
			this.hostT.setEnabled(false);
			this.portT.setEnabled(false);
			this.nickNameT.setEnabled(false);
			this.connect.setEnabled(false);
			this.disconnect.setEnabled(true);
			this.challenge.setEnabled(true);
			this.acceptChallenge.setEnabled(false);
			this.declineChallenge.setEnabled(false);
			this.surrender.setEnabled(false);
		}
		catch(Exception ee){
			ee.printStackTrace();
		}	
	}
	
	public void next(){
		for(int i = 0;i < 9; ++i){//empty the chess pieces
			for(int j = 0;j < 10; ++j){
				this.chessPieces[i][j] = null;
			}
		}
		this.myTurn = false;
		this.initialPieces();
		this.repaint();
	}
	
	public static void main(String args[]){
		new ChineseChess();
	}
}
