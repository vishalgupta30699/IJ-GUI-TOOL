import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.filechooser.*;

public class RapidApplicationDevelopment extends JFrame implements ActionListener,WindowListener
{
    private JLabel bpnLabel,locationLabel;
    private JTextField bpnTextField,locationTextField;
    private JButton saveButton,browseButton;
    public RapidApplicationDevelopment()
    {
        setTitle("RAD settings");
        setLayout(null);
        Font font=new Font("arial",Font.PLAIN,18);

        bpnLabel=new JLabel("Base package name :");
        bpnLabel.setBounds(10,20,300,25);
        bpnLabel.setFont(font);
        bpnTextField=new JTextField();
        bpnTextField.setBounds(10,50,400,35);
        bpnTextField.setFont(font);

        locationLabel=new JLabel("Location:");
        locationLabel.setBounds(10,110,300,25);
        locationLabel.setFont(font);
        locationTextField=new JTextField();
        locationTextField.setBounds(10,140,400,35);
        locationTextField.setFont(font);

        browseButton=new JButton("Browse");
        browseButton.setBounds(300,110,100,25);

        saveButton=new JButton("save");
        saveButton.setBounds(160,180,100,30);

        saveButton.addActionListener(this);
        browseButton.addActionListener(this);

        add(locationTextField);
        add(bpnTextField);
        add(browseButton);
        add(saveButton);
        add(locationLabel);
        add(bpnLabel);
        setLocation(30,30);
        setSize(500,270);
        setVisible(true);
        addWindowListener(this);
    }

    public void actionPerformed(ActionEvent ev)
    {
        if(ev.getSource()==saveButton)
        {
            try
            {
                File file=new File("rapidDevelopment.config");
                if(file.exists()) file.delete();
                RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");
                randomAccessFile.writeBytes(bpnTextField.getText());
                randomAccessFile.writeBytes("\n");
                randomAccessFile.writeBytes(locationTextField.getText());
                randomAccessFile.writeBytes("\n");
                randomAccessFile.close();
                setVisible(false);
            }
            catch(IOException ioe)
            {
                JFrame f;
                f=new JFrame();  
                JOptionPane.showMessageDialog(f,ioe.getMessage()); 
            }   
        }
        if(ev.getSource()==browseButton)
        {
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); 
            
            int r = j.showOpenDialog(null); 
            j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
            j.setSelectedFile(null);
            if (r == JFileChooser.APPROVE_OPTION) 
            {   
                locationTextField.setText(j.getCurrentDirectory().getAbsolutePath());  
            } 
        }
    }

    public void windowClosing(WindowEvent ev)
    {
    setVisible(false);
    }
    public void windowClosed(WindowEvent e){}
    public void windowOpened(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}

}