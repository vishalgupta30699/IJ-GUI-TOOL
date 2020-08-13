import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.sql.*;

public class ConnectionClass
{
    private Connection c;
    private Statement s;
    private ResultSet rs1,rs2,r1,r;
    private ResultSetMetaData rsmd;
    private Object[][] data;
    private Table table;
    private Column column;
    private Database database;
    private String primaryKeyName;
    private ConnectionClass()
    {
        table=null;
        c=null;
        data=null;
        column=null;
        database=null;
        primaryKeyName=null;
    }
    public static ConnectionClass connectionClass=null;
    public static ConnectionClass getInstance()
    {
        if(connectionClass==null) connectionClass=new ConnectionClass();
        return connectionClass;
    }
    public void createConnection(String server,int portNumber,String directory) throws SQLException,ClassNotFoundException
    {
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        this.c=DriverManager.getConnection("jdbc:derby://"+server+":"+portNumber+"/"+directory+";create=true");
        database=new Database();
        String dir[]=directory.split("/",0);
        int i;
        String k="";
        for(String d:dir) k=d;
        database.setDatabaseName(k);
        DatabaseMetaData md=c.getMetaData();
        String[] types={"TABLE"};
        rs1=md.getTables(null,null,"%",types);
        while(rs1.next())
        {
            table=new Table();
            table.setTableName(rs1.getString(3));
            this.s=c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            this.rs2=s.executeQuery("select * from "+table.getTableName());
            this.rsmd=rs2.getMetaData();
            int columnCount=rsmd.getColumnCount();
            for(i=1;i<=columnCount;++i)
            {
                column=new Column();
                column.setColumnName(rsmd.getColumnLabel(i));
                column.setDataType(rsmd.getColumnTypeName(i));
                table.addColumn(column);
            }
            rs2.close();
            s.close();
            database.addTable(table);
        }
        rs1.close();
    }
    public Table createTable(String tableName) throws SQLException,ClassNotFoundException
    {
        table=new Table(); 
        table.setTableName(tableName);
        this.s=c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
        ResultSet.CONCUR_READ_ONLY);
        this.r1=s.executeQuery("select * from "+table.getTableName());
        this.rsmd = r1.getMetaData();
        int columnCount=rsmd.getColumnCount();
        for(int i=1;i<=columnCount;i++) 
        {
            column=new Column();
            column.setColumnName(rsmd.getColumnLabel(i));
            column.setDataType(rsmd.getColumnTypeName(i));
            table.addColumn(column);
        }
        r1.close();
        s.close();
        database.addTable(table);
        return table;
    }
    public Object[][] getTableData(String sqlStatement) throws SQLException,ClassNotFoundException
    {
        this.s=c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        int i,j;
        this.r=s.executeQuery(sqlStatement);
        this.rsmd = r.getMetaData();

        String[] tableHeading;
        int columnCount=rsmd.getColumnCount();
        tableHeading=new String[columnCount];
        for(i=1;i<=columnCount;i++) tableHeading[i-1]=rsmd.getColumnLabel(i);
        r.last();
        int rowsCount=r.getRow();
        r.beforeFirst();
        this.data=new Object[rowsCount][columnCount];
        i=0;
        while(r.next())
        {
        for(j=0;j<columnCount;j++) data[i][j]=r.getString(tableHeading[j]);
        i++;
        }
        r.close();
        s.close();
        return data;
    }
    public String[] getTableHeading(String sqlStatement) throws SQLException,ClassNotFoundException
    {
        this.s=c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        int i;
        this.r=s.executeQuery(sqlStatement);
        this.rsmd = r.getMetaData();
        String[] tableHeading;
        int columnCount=rsmd.getColumnCount();
        tableHeading=new String[columnCount];
        for(i=1;i<=columnCount;i++) tableHeading[i-1]=rsmd.getColumnLabel(i);
        r.close();
        s.close();
        return tableHeading;
    }
    public void changeStatement(String sqlStatement) throws SQLException,ClassNotFoundException
    {
        this.s=c.createStatement();
        this.s.executeUpdate(sqlStatement);
        s.close();
    }
    public String getPrimaryKey(String sqlClassName) throws SQLException
    {
        ResultSet r = null;
        DatabaseMetaData meta = c.getMetaData();
        r = meta.getPrimaryKeys(null, null, sqlClassName);
        while (r.next()) 
        {
        primaryKeyName = r.getString("COLUMN_NAME");      
        }
        r.close();
        return primaryKeyName;
    }
    public void disconnect() throws SQLException,ClassNotFoundException
    {
        c.close();
    }
    public LinkedList<Table> getTableList()
    {
        return database.getTableList();
    }

}