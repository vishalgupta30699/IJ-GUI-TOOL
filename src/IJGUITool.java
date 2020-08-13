import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.sql.*;
import java.io.*;


class GUITool extends JFrame implements ActionListener
{
    private JMenuBar menuBar;
    private JMenu rdbms,RAD;
    private JLabel homeLabel1,homeLabel2,homeLabel3,spaceLabel,tableHeadingLabel,sqlLabel;
    private JTable table;
    public JMenuItem connect,disConnect,quit,radSetting;
    public JButton runButton,generateDLButton;
    public JTextArea outputTextArea,sqlTextArea;
    private JPanel tablePanel,sqlPanel,homePanel;
    private JList tableList;
    public DefaultListModel tableModel;
    private Connection connection;
    private Font sqlFont;
    private Dimension screenSize;
    private JScrollPane jsp,sqlJSP,tableJSP;
    public boolean TB=false;
    private Table singleSelectedTable;
    private ConnectionFrame connectionFrame;
    private RapidApplicationDevelopment rapidApplicationDevelopment;
    GUITool()
    {
        sqlFont=new Font("arial",Font.PLAIN,16);
        screenSize=Toolkit.getDefaultToolkit().getScreenSize();
        setTitle("IJ GUI TOOL");
        setSize((screenSize.width),26*(screenSize.height)/27);
        connect=new JMenuItem("Connect");
        disConnect=new JMenuItem("DisConnect");
        quit=new JMenuItem("Quit");
        connect.addActionListener(this);
        disConnect.addActionListener(this);
        quit.addActionListener(this);
        disConnect.setEnabled(false);
        rdbms=new JMenu("RDBMS");
        rdbms.add(connect);
        rdbms.add(disConnect);
        rdbms.add(quit);
        radSetting=new JMenuItem("RAD Setting");
        radSetting.addActionListener(this);
        RAD=new JMenu("RAD");
        RAD.add(radSetting);
        menuBar=new JMenuBar();
        menuBar.add(rdbms);
        menuBar.add(RAD);
        setJMenuBar(menuBar);
        homeLabel1=new JLabel("WELCOME TO");
        homeLabel2=new JLabel("IJ GUI TOOL");
        homeLabel3=new JLabel("Create Your Database Here!");
        Font font1=new Font("Book Antiqua",Font.BOLD,50);
        Font font2=new Font("Calligrapher ",Font.PLAIN,60);
        homeLabel1.setFont(font1);
        homeLabel2.setFont(font1);
        homeLabel3.setFont(font1);

        homeLabel1.setBounds(100,100,600,100);
        homeLabel2.setBounds(100,200,600,150);
        homeLabel3.setBounds(100,350,600,100);

        homePanel=new JPanel();
        homePanel.setLayout(null);
        homePanel.setBackground(Color.white);
        homePanel.add(homeLabel1);
        homePanel.add(homeLabel2);
        homePanel.add(homeLabel3);
        
        
        runButton=new JButton("Run");
        runButton.setBounds(3*(screenSize.width)/5+100+20,25,100,35);
        runButton.setEnabled(false);
        runButton.addActionListener(this);
        sqlTextArea=new JTextArea();
        sqlTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        sqlTextArea.setLineWrap(true);
        sqlJSP = new JScrollPane(sqlTextArea);
        sqlJSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sqlJSP.setBounds(140,25,3*(screenSize.width)/5-30,40);
        sqlTextArea.setBounds(140,25,3*(screenSize.width)/5-45,40);
        sqlTextArea.setFont(sqlFont);
        sqlLabel=new JLabel("SQL statement:");
        sqlLabel.setBounds(10,25,130,35);
        sqlLabel.setFont(sqlFont);
        
        outputTextArea=new JTextArea();
        outputTextArea.setFont(sqlFont);
        outputTextArea.setBounds(10,95+4*(screenSize.height)/7-20,3*(screenSize.width)/4-15,2*(screenSize.height)/7-50);
        outputTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        outputTextArea.setEditable(false);
        spaceLabel=new JLabel("Table will be displayed here", SwingConstants.CENTER);
        spaceLabel.setFont(font2);
        spaceLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        spaceLabel.setBounds(10,80,3*(screenSize.width)/4-15, 4*(screenSize.height)/7-15); 
        tableHeadingLabel=new JLabel("List of Tables", SwingConstants.CENTER);
        tableHeadingLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tableHeadingLabel.setBounds(3*(screenSize.width)/4+15,80,(screenSize.width)/4-65,35);
        Font tableFont=new Font("arial",Font.BOLD,20);
        tableHeadingLabel.setFont(tableFont);

        tableModel=new DefaultListModel();
        tableList=new JList(tableModel);
        tableList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tableList.setBounds(3*(screenSize.width)/4+15,120,(screenSize.width)/4-90,6*(screenSize.height)/7-170);
        tableList.setBackground(Color.white);
        tableList.setFont(sqlFont);
        tableJSP=new JScrollPane(tableList);
        tableJSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        tableJSP.setBounds(3*(screenSize.width)/4+15,120,(screenSize.width)/4-65,6*(screenSize.height)/7-170);
        
        tableList.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent evt)
            {
                if(evt.getClickCount()==2)
                {
                    Table selectedTable=(Table)tableList.getSelectedValue();
                    String tableName=selectedTable.getTableName();
                    Object[][] data={{}};
                    String [] heading={};
                    try 
                    {
                        heading=connectionFrame.connectionClass.getTableHeading("select * from "+tableName);
                        data=connectionFrame.connectionClass.getTableData("select * from "+tableName);  
                    } catch (Exception e) 
                    {
                       outputTextArea.setText(e.getMessage());
                    }
                    sqlTextArea.setText("select * from "+tableName);
                    Font tableFont=new Font("arial",Font.PLAIN,16);
                    table=new JTable(data,heading);
                    table.setFont(tableFont);
                    table.setRowHeight(18);
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                    jsp=new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                    jsp.setBounds(0,0,3*(screenSize.width)/4, 4*(screenSize.height)/7); 
                    
                    remove(tablePanel);
                    tablePanel=new JPanel();
                    tablePanel.setLayout(new GridLayout(1,1));
                    tablePanel.add(jsp);
                    tablePanel.setBounds(10,80,3*(screenSize.width)/4+15, 4*(screenSize.height)/7+15);    //.....
                    add(tablePanel,BorderLayout.CENTER);
                    tablePanel.setVisible(true);
                    TB=true;
                }
                if(evt.getClickCount()==1)
                {
                    singleSelectedTable=(Table)tableList.getSelectedValue();
	                generateDLButton.setEnabled(true);
                }
            }
        });
        generateDLButton=new JButton("Generate DL");
        generateDLButton.setBounds(3*(screenSize.width)/4+15,6*(screenSize.height)/7-20,(screenSize.width)/4-65,30);
        generateDLButton.addActionListener(this);
        generateDLButton.setEnabled(false);

        sqlPanel=new JPanel();
        sqlPanel.setLayout(null);
        sqlPanel.add(runButton);
        sqlPanel.add(sqlTextArea);
        sqlPanel.add(sqlJSP);
        sqlPanel.add(outputTextArea);
        sqlPanel.add(spaceLabel);
        sqlPanel.add(tableHeadingLabel);
        sqlPanel.add(tableList);
        sqlPanel.add(tableJSP);
        sqlPanel.add(generateDLButton);
        sqlPanel.add(sqlLabel);

        tablePanel=new JPanel();
        tablePanel.setLayout(new BorderLayout());
        add(tablePanel,BorderLayout.CENTER);
        tablePanel.setVisible(false);

        setLayout(new BorderLayout());
        add(homePanel,BorderLayout.CENTER);
        setLocation(10,10);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }
    public void actionPerformed(ActionEvent ev)
    {
        if(ev.getSource()==connect)
        {
            connectionFrame=new ConnectionFrame(this);
            if(TB) tablePanel.setVisible(false);
            homePanel.setVisible(false);
            add(sqlPanel,BorderLayout.CENTER);
            outputTextArea.setForeground(Color.red);
            outputTextArea.setText("Please Connect to server first..");
        }
        if(ev.getSource()==disConnect)
        {
            try
            {
                connectionFrame.connectionClass.disconnect();
                if(TB) tablePanel.setVisible(false);
                runButton.setEnabled(false);
                tableModel.removeAllElements();
                generateDLButton.setEnabled(false);
                disConnect.setEnabled(false);
                connect.setEnabled(true);                      
                outputTextArea.setForeground(Color.red);

                outputTextArea.setText("PLEASE CONNECT TO SERVER......");
            }
            catch(SQLException sqlException)
            {
                if(TB) tablePanel.setVisible(false);
                outputTextArea.setForeground(Color.red);
                outputTextArea.setText(sqlException.getMessage());
            }
            catch(ClassNotFoundException cnfe)
            {
                if(TB) tablePanel.setVisible(false);
                outputTextArea.setForeground(Color.red);
                outputTextArea.setText(cnfe.getMessage());
            }
            catch(Exception e)
            {
                if(TB) tablePanel.setVisible(false);
                outputTextArea.setForeground(Color.red);
                outputTextArea.setText(e.getMessage());  
            }
        }
        if(ev.getSource()==quit)
        {
            System.exit(0);
        }
        if(ev.getSource()==runButton)
        {
                Color greenColor = new Color(0, 153, 0);
                outputTextArea.setText("");
                try
                {
                    String sql=sqlTextArea.getText();
                    String[] sqlArray=sql.split(" ",4);
                    if(sqlArray[0].equals("select")) 
                    {
                        Object[][] data;
                        String[] heading;
                        
                        heading=connectionFrame.connectionClass.getTableHeading(sql);
                        data=connectionFrame.connectionClass.getTableData(sql);
                        Font tableFont=new Font("arial",Font.PLAIN,16);
                        table=new JTable(data,heading); 
                        table.setFont(tableFont);
                        table.setRowHeight(20);
                        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                        jsp=new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                        jsp.setBounds(0,0,3*(screenSize.width)/4, 4*(screenSize.height)/7); 
                        
                        remove(tablePanel);
                        tablePanel=new JPanel();
                        tablePanel.setLayout(new GridLayout(1,1));
                        tablePanel.add(jsp);
                        tablePanel.setBounds(10,80,3*(screenSize.width)/4+15, 4*(screenSize.height)/7+15);    //.....
                        add(tablePanel,BorderLayout.CENTER);
                        tablePanel.setVisible(true);
                        TB=true;
                    }
                    else 
                    {
                        if(TB) tablePanel.setVisible(false);
                        
                        if(sqlArray[0].equals("update"))
                        {
                            connectionFrame.connectionClass.changeStatement(sql); 
                            outputTextArea.setForeground(greenColor);
                            outputTextArea.setText("Table updated successfully");
                        }
                        else if(sqlArray[0].equals("delete"))
                        {
                            connectionFrame.connectionClass.changeStatement(sql); 
                            outputTextArea.setForeground(greenColor);
                            outputTextArea.setText("Data from table deleted successfully :)");
                        }
                        else if(sqlArray[0].equals("insert"))
                        {
                            connectionFrame.connectionClass.changeStatement(sql); 
                            outputTextArea.setForeground(greenColor);
                            outputTextArea.setText("Data inserted successfully :)");
                        }
                    
                        else if(sqlArray[0].equals("create"))
                        {
                            connectionFrame.connectionClass.changeStatement(sql); 
                            outputTextArea.setForeground(greenColor);
                            outputTextArea.setText("New table has been created successfully :)");
                            Table table=connectionFrame.connectionClass.createTable(sqlArray[2]);
                            tableModel.addElement(table);
                        }
                        else if(sqlArray[0].equals("drop"))
                        {
                            connectionFrame.connectionClass.changeStatement(sql); 
                            outputTextArea.setForeground(greenColor);
                            int c=tableModel.getSize();
                            for(int i=0;i<c;i++)
                            {
                                Table name=(Table)tableModel.get(i);
                                if(name.toString().equalsIgnoreCase(sqlArray[2]))
                                {
                                    tableModel.removeElementAt(i);
                                    break;
                                }
                            }
                            outputTextArea.setText("table "+sqlArray[2]+" has been deleted successfully :)");
                        }
                        else
                        {
                            connectionFrame.connectionClass.changeStatement(sql);
                            outputTextArea.setText("Job done :)"); 
                        }
                    }
                }
                catch(SQLException sqlException)
                {
                    if(TB) tablePanel.setVisible(false);
                    outputTextArea.setForeground(Color.red);
                    outputTextArea.setText(sqlException.getMessage());
                }
                catch(ClassNotFoundException cnfe)
                {
                    if(TB) tablePanel.setVisible(false);
                    outputTextArea.setForeground(Color.red);
                    outputTextArea.setText(cnfe.getMessage());
                }
                catch(Exception e)
                {
                    if(TB) tablePanel.setVisible(false);
                    e.printStackTrace();
                        
                    outputTextArea.setForeground(Color.red);
                    outputTextArea.setText("error");
                }
        }
        if(ev.getSource()==radSetting)
        {
            rapidApplicationDevelopment=new RapidApplicationDevelopment();
        }
        if(ev.getSource()==generateDLButton)
        {
            JFrame f;
            try
            {
                String  primaryKeyName=connectionFrame.connectionClass.getPrimaryKey(singleSelectedTable.getTableName());
                DTOGenerator dtoGenerator=new DTOGenerator(singleSelectedTable);
                DAOGenerator daoGenerator=new DAOGenerator(singleSelectedTable,primaryKeyName);
            }
            catch(IOException ioe)
            {
                f=new JFrame();  
                JOptionPane.showMessageDialog(f,ioe.getMessage());  
            }
            catch(SQLException sqle)
            {
                f=new JFrame();  
                JOptionPane.showMessageDialog(f,sqle.getMessage());  
            }
            f=new JFrame();  
            JOptionPane.showMessageDialog(f,"Data layer for database of "+singleSelectedTable+" has been created successfully ");  
        }      
    }    
}






class IJGUITool 
{
    public static void main(String gg[])
    {
        GUITool g=new GUITool();
    }
}