import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.sql.*;

public class ConnectionFrame extends JFrame implements ActionListener
{ 
    private GUITool g;
    private JLabel serverLabel,portLabel,directoryLabel,serverError,portError,directoryError;
    private JTextField serverTF,portTF,directoryTF;
    private JButton connectButton;
    public ConnectionClass connectionClass;
    public static LinkedList<Table> linkedListTable;
    ConnectionFrame(GUITool g)
    {
        this.g=g;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        directoryError=new JLabel("");
        portError=new JLabel("");
        serverError=new JLabel("");
        setTitle("Connect to DB");
        Font font=new Font("arial",Font.PLAIN,18);
        Font font2=new Font("arial",Font.PLAIN,18);
        serverLabel=new JLabel("Server name");
        serverLabel.setFont(font);
        serverLabel.setBounds(10,20,120,50);
        serverTF=new JTextField(30);
        serverTF.setFont(font2);
        serverTF.setBounds(150,25,280,30);

        portLabel=new JLabel("Port number");
        portLabel.setFont(font);
        portLabel.setBounds(10,70,120,50);
        portTF=new JTextField(30);
        portTF.setFont(font2);
        portTF.setBounds(150,75,280,30);

        directoryLabel=new JLabel("Path to DB");
        directoryLabel.setFont(font);
        directoryLabel.setBounds(10,120,120,50);
        directoryTF=new JTextField(30);
        directoryTF.setFont(font2);
        directoryTF.setBounds(150,125,280,30);

        connectButton=new JButton("connect");
        connectButton.addActionListener(this);
        connectButton.setBounds(185,180,120,30);
        add(serverLabel);
        add(serverTF);
        add(portLabel);
        add(portTF);
        add(directoryLabel);
        add(directoryTF);
        add(connectButton);
        add(portError);
        add(serverError);
        add(directoryError);
        setLayout(null);
        setLocation((screenSize.width/2)-250,(screenSize.height/2)-135);
        setSize(500,270);
        //addWindowListener(this);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent ev)
    {
        if(ev.getSource()==connectButton)
        {
            try 
            {
                int port=Integer.parseInt(portTF.getText());
                String server=serverTF.getText();
                String directory=directoryTF.getText();
                try
                {
                    connectionClass=ConnectionClass.getInstance();
                    connectionClass.createConnection(server,port,directory);
                    linkedListTable=connectionClass.getTableList();
                    int i=0;
                    for(Table t:linkedListTable)
                    {
                        g.tableModel.add(i,t);
                        i++;
                    }
                    g.disConnect.setEnabled(true);
                    g.connect.setEnabled(false);
                    g.runButton.setEnabled(true);
                    g.generateDLButton.setEnabled(false);
                    Color greenColor = new Color(0, 153, 0);
                    g.outputTextArea.setForeground(greenColor);
                    g.outputTextArea.setText("Start your Job");
                    setVisible(false);
                }
                catch(SQLException sqlException)
                {
                    JFrame f=new JFrame();
                    JOptionPane.showMessageDialog(f,sqlException.getMessage());
                }
                catch(ClassNotFoundException cnfe)
                {
                    JFrame f=new JFrame();
                    JOptionPane.showMessageDialog(f,cnfe.getMessage());  
                    directoryError.setBounds(150,155,280,15);
                    directoryError.setForeground(Color.red);
                }
            
            } 
            catch (Exception e) 
            {
                JFrame f;
                f=new JFrame();  
                JOptionPane.showMessageDialog(f,e.getMessage()); 
            }
        }
    }

}