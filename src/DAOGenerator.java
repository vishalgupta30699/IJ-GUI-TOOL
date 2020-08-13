import java.io.*;
import java.util.*;
import java.sql.*;
public class DAOGenerator
{
    public DAOGenerator(Table table,String primaryKeyName) throws IOException
    {
        int i;
        File pathFile,interfacePathFile,interfaceFile,exceptionFile,exceptionPathFile;
        String bpn,location,fullPath,bpn2;
        String propertyName;
        String propertyType;
        String javaType;
        pathFile=new File("rapidDevelopment.config");
        if(!pathFile.exists() || pathFile.length()==0)
        {
            throw new IOException("please create RAD first in RAD settings");
        }
        RandomAccessFile randomAccessFile=new RandomAccessFile(pathFile,"rw");
        bpn=randomAccessFile.readLine();
        location=randomAccessFile.readLine();
        randomAccessFile.close();
        bpn2=bpn.replaceAll("\\.","\\\\");
        fullPath=location+"\\"+bpn2+"\\dl\\dao";
        pathFile=new File(fullPath);
        interfacePathFile=new File(location+"\\"+bpn2+"\\dl\\interfaces");
        exceptionPathFile=new File(location+"\\"+bpn2+"\\dl\\exceptions");
        
        if(pathFile.exists()==false) 
        {
            pathFile.mkdirs();
        }
        if(interfacePathFile.exists()==false)
        {
            interfacePathFile.mkdirs();
        }
        if(exceptionPathFile.exists()==false)
        {
            exceptionPathFile.mkdirs();
        }
        
        String className=table.getTableName();
        String sqlClassName=className;
        className=className.substring(0,1).toUpperCase()+className.substring(1).toLowerCase();
        while(className.indexOf('_')>=0)
        {
            i=className.indexOf('_');
            String s1=className.substring(0,i);
            String s2=className.substring(i+1,i+2).toUpperCase();
            String s3=className.substring(i+2);
            className=s1+s2+s3;
        }
        String fileName=fullPath+"\\"+className+"DAO.java";
        File daoFile=new File(fileName);
        if(daoFile.exists()) daoFile.delete();
        randomAccessFile=new RandomAccessFile(daoFile,"rw");
        randomAccessFile.writeBytes("package "+bpn+".dl.dao;\r\n");
        randomAccessFile.writeBytes("import "+bpn+".dl.interfaces.*;\r\n");
        randomAccessFile.writeBytes("import "+bpn+".dl.exceptions.*;\r\n");
        randomAccessFile.writeBytes("import "+bpn+".dl.dto.*;\r\n");
        randomAccessFile.writeBytes("import java.sql.*;\r\n");
        randomAccessFile.writeBytes("import java.util.*;\r\n");
        randomAccessFile.writeBytes("import java.io.*;\r\n"); 
        randomAccessFile.writeBytes("public class "+className+"DAO implements "+className+"DAOInterface\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("private Connection c;\r\n");
        
        //DATA TYPES AND ENTITIES NAMES STORED IN COLUMNLIST (START)
        
        LinkedList<Column> columnList=table.getColumnList();
        int size=columnList.size();
        String[] propertyNameArray=new String[size];
        String[] sqlPropertyNameArray=new String[size];
        String[] javaTypeArray=new String[size];
        String[] sqlPropertyTypeArray=new String[size];
        int j=0;
        for(Column cc:columnList)
        {
            propertyName=cc.getColumnName();
            sqlPropertyNameArray[j]=propertyName;
            propertyName=propertyName.substring(0,1).toLowerCase()+propertyName.substring(1).toLowerCase();
            while(propertyName.indexOf('_')>=0)
            {
                i=propertyName.indexOf('_');
                String s1=propertyName.substring(0,i);
                String s2=propertyName.substring(i+1,i+2).toUpperCase();
                String s3=propertyName.substring(i+2);
                propertyName=s1+s2+s3;
            }
            propertyNameArray[j]=propertyName;
            propertyType=cc.getDataType();
            sqlPropertyTypeArray[j]=propertyType;
            javaType="";
            javaType=TypeMappings.getJavaType(propertyType);
            javaTypeArray[j]=javaType;
            j++;
        }
        //ENTITIES WITH DATATYPE STORED (END)
        
        
        //ADD METHOD STARTS (START)
        randomAccessFile.writeBytes("public void add("+className+"DTOInterface "+className.substring(0,1).toLowerCase()+className.substring(1)+"DTOInterface) throws DAOException\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("try\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("c=DAOConnection.getConnection();\r\n");
        
        //VALIDATION FOR UNIQUE KEY LEFT (e.g. panNumber)
        
        String sn="",sq="";
        for(j=1;j<size;j++)
        {
            if(j==size-1)
            {
                sn=sn+sqlPropertyNameArray[j];
                sq=sq+"?";
            }
            else
            {
                sn=sn+sqlPropertyNameArray[j]+",";
                sq=sq+"?,";
            }
        }
        randomAccessFile.writeBytes("PreparedStatement ps=c.prepareStatement(\"insert into "+sqlClassName+" ("+sn+") values ("+sq+")\",Statement.RETURN_GENERATED_KEYS);\r\n");
        for(j=1;j<size;j++)
        {
            randomAccessFile.writeBytes("ps.set"+javaTypeArray[j].substring(0,1).toUpperCase()+javaTypeArray[j].substring(1)+"("+j+","+className.substring(0,1).toLowerCase()+className.substring(1)+"DTOInterface.get"+propertyNameArray[j].substring(0,1).toUpperCase()+propertyNameArray[j].substring(1)+"());\r\n");
        }
        randomAccessFile.writeBytes("ps.executeUpdate();\r\n");
        randomAccessFile.writeBytes("ResultSet r;\r\n");
        randomAccessFile.writeBytes("r=ps.getGeneratedKeys();\r\n");
        randomAccessFile.writeBytes("if(r.next())\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("int key=r.get"+javaTypeArray[0].substring(0,1).toUpperCase()+javaTypeArray[0].substring(1)+"(1);\r\n");
        randomAccessFile.writeBytes(className.substring(0,1).toLowerCase()+className.substring(1)+"DTOInterface.set"+propertyNameArray[0].substring(0,1).toUpperCase()+propertyNameArray[0].substring(1)+"(key);\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("r.close();\r\n");
        randomAccessFile.writeBytes("ps.close();\r\n");
        randomAccessFile.writeBytes("c.close();\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("catch(SQLException sqlException)\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("throw new DAOException(sqlException.getMessage());\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("}\r\n");
        
        //ADD METHOD (ENDS)
        
        //UPDATE METHOD (STARTS)	
        randomAccessFile.writeBytes("public void update("+className+"DTOInterface "+className.substring(0,1).toLowerCase()+className.substring(1)+"DTOInterface) throws DAOException\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("try\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("c=DAOConnection.getConnection();\r\n");
        
        randomAccessFile.writeBytes("PreparedStatement ps=c.prepareStatement(\"select "+primaryKeyName+" from "+sqlClassName+" where "+primaryKeyName+"=?\");\r\n");
        randomAccessFile.writeBytes("ps.setInt(1,"+className.substring(0,1).toLowerCase()+className.substring(1)+"DTOInterface.get"+propertyNameArray[0].substring(0,1).toUpperCase()+propertyNameArray[0].substring(1)+"());\r\n");
        randomAccessFile.writeBytes("ResultSet r;\r\n");
        randomAccessFile.writeBytes("r=ps.executeQuery();\r\n");
        randomAccessFile.writeBytes("boolean b=r.next();\r\n");
        randomAccessFile.writeBytes("if(!b)\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("r.close();\r\n");
        randomAccessFile.writeBytes("ps.close();\r\n");
        randomAccessFile.writeBytes("c.close();\r\n");
        randomAccessFile.writeBytes("throw new DAOException(\"invalid "+primaryKeyName+"\");\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("r.close();\r\n");
        randomAccessFile.writeBytes("ps.close();\r\n");
        
        //UNIQUE KEY CHECK NOT DONE
        
        
        sn="";
        for(j=1;j<size;j++)
        {
            if(j==size-1)
            {
                sn=sn+sqlPropertyNameArray[j]+"=?";
            }
            else
            {
                sn=sn+sqlPropertyNameArray[j]+"=?,";
            }
        }
        randomAccessFile.writeBytes("ps=c.prepareStatement(\"update "+sqlClassName+" set "+sn+" where "+primaryKeyName+"=?\");\r\n");
        for(j=1;j<size;j++)
        {
            randomAccessFile.writeBytes("ps.set"+javaTypeArray[j].substring(0,1).toUpperCase()+javaTypeArray[j].substring(1)+"("+j+","+className.substring(0,1).toLowerCase()+className.substring(1)+"DTOInterface.get"+propertyNameArray[j].substring(0,1).toUpperCase()+propertyNameArray[j].substring(1)+"());\r\n");
        }
        randomAccessFile.writeBytes("ps.set"+javaTypeArray[0].substring(0,1).toUpperCase()+javaTypeArray[0].substring(1)+"("+j+","+className.substring(0,1).toLowerCase()+className.substring(1)+"DTOInterface.get"+propertyNameArray[0].substring(0,1).toUpperCase()+propertyNameArray[0].substring(1)+"());\r\n");
        randomAccessFile.writeBytes("ps.executeUpdate();\r\n");
        randomAccessFile.writeBytes("ps.close();\r\n");
        randomAccessFile.writeBytes("c.close();\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("catch(SQLException sqlException)\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("throw new DAOException(sqlException.getMessage());\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("}\r\n");
        
        //UPDATE METHOD (END)
         
        //DELETE METHOD (START)
        
        randomAccessFile.writeBytes("public void delete("+javaTypeArray[0]+" "+propertyNameArray[0]+") throws DAOException\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("try\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("c=DAOConnection.getConnection();\r\n");
        
        randomAccessFile.writeBytes("PreparedStatement ps=c.prepareStatement(\"select "+primaryKeyName+" from "+sqlClassName+" where "+primaryKeyName+"=?\");\r\n");
        randomAccessFile.writeBytes("ps.set"+javaTypeArray[0].substring(0,1).toUpperCase()+javaTypeArray[0].substring(1)+"(1,"+propertyNameArray[0]+");\r\n");
        randomAccessFile.writeBytes("ResultSet r;\r\n");
        randomAccessFile.writeBytes("r=ps.executeQuery();\r\n");
        randomAccessFile.writeBytes("boolean b=r.next();\r\n");
        randomAccessFile.writeBytes("if(!b)\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("r.close();\r\n");
        randomAccessFile.writeBytes("ps.close();\r\n");
        randomAccessFile.writeBytes("c.close();\r\n");
        randomAccessFile.writeBytes("throw new DAOException(\"invalid "+primaryKeyName+"\");\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("r.close();\r\n");
        randomAccessFile.writeBytes("ps.close();\r\n");
        
        randomAccessFile.writeBytes("ps=c.prepareStatement(\"delete from "+sqlClassName+" where "+primaryKeyName+"=?\");\r\n");
        
        randomAccessFile.writeBytes("ps.set"+javaTypeArray[0].substring(0,1).toUpperCase()+javaTypeArray[0].substring(1)+"("+1+","+propertyNameArray[0]+");\r\n");
        randomAccessFile.writeBytes("ps.executeUpdate();\r\n");
        randomAccessFile.writeBytes("ps.close();\r\n");
        randomAccessFile.writeBytes("c.close();\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("catch(SQLException sqlException)\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("throw new DAOException(sqlException.getMessage());\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("}\r\n");
        
        //DELETE METHOD (END)
        
        //GETALL METHOD (START)
         
        randomAccessFile.writeBytes("public List<"+className+"DTOInterface> getAll() throws DAOException\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes(className+"DTOInterface temp;\r\n");
        randomAccessFile.writeBytes("List<"+className+"DTOInterface> list=new LinkedList<"+className+"DTOInterface>();\r\n");
        randomAccessFile.writeBytes("try\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("c=DAOConnection.getConnection();\r\n");
        randomAccessFile.writeBytes("Statement s=c.createStatement();\r\n");
        randomAccessFile.writeBytes("ResultSet r;\r\n");
        randomAccessFile.writeBytes("r=s.executeQuery(\"select * from "+sqlClassName+"\");\r\n");
        randomAccessFile.writeBytes("while(r.next())\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("temp=new "+className+"DTO();\r\n");
        for(j=0;j<size;j++)
        {
            randomAccessFile.writeBytes("temp.set"+propertyNameArray[j].substring(0,1).toUpperCase()+propertyNameArray[j].substring(1)+"(r.get"+javaTypeArray[j].substring(0,1).toUpperCase()+javaTypeArray[j].substring(1)+"(\""+sqlPropertyNameArray[j]+"\"));\r\n");
        }
        randomAccessFile.writeBytes("list.add(temp);\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("r.close();\r\n");
        randomAccessFile.writeBytes("s.close();\r\n");
        randomAccessFile.writeBytes("c.close();\r\n");
        randomAccessFile.writeBytes("return list;\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("catch(SQLException sqlException)\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("throw new DAOException(sqlException.getMessage());\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("}\r\n");
        
        //GET ALL (END)
        
        
        //GET COUNT (START)
        
        randomAccessFile.writeBytes("public int getCount() throws DAOException\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("int count=0;\r\n");
        randomAccessFile.writeBytes("try\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("c=DAOConnection.getConnection();\r\n");
        randomAccessFile.writeBytes("PreparedStatement ps=c.prepareStatement(\"select count(*) as cnt from "+sqlClassName+"\");\r\n");
        randomAccessFile.writeBytes("ResultSet r;\r\n");
        randomAccessFile.writeBytes("r=ps.executeQuery();\r\n");
        randomAccessFile.writeBytes("if(r.next())\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("count=r.getInt(\"cnt\");\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("r.close();\r\n");
        randomAccessFile.writeBytes("ps.close();\r\n");
        randomAccessFile.writeBytes("c.close();\r\n");
        randomAccessFile.writeBytes("return count;\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("catch(SQLException sqlException)\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("throw new DAOException(sqlException.getMessage());");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("}\r\n");
        
        //GET COUNT (END)
        
        //GET BY PRIMARY KEY (STARTS)
        
        randomAccessFile.writeBytes("public "+className+"DTOInterface getBy"+propertyNameArray[0].substring(0,1).toUpperCase()+propertyNameArray[0].substring(1)+"("+javaTypeArray[0]+" "+propertyNameArray[0]+") throws DAOException\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes(className+"DTOInterface temp;\r\n");
        randomAccessFile.writeBytes("try\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("c=DAOConnection.getConnection();\r\n");
        randomAccessFile.writeBytes("PreparedStatement ps=c.prepareStatement(\"select * from "+sqlClassName+" where "+sqlPropertyNameArray[0]+"=?\");\r\n");
        randomAccessFile.writeBytes("ps.set"+javaTypeArray[0].substring(0,1).toUpperCase()+javaTypeArray[0].substring(1)+"(1,"+propertyNameArray[0]+");\r\n");
        randomAccessFile.writeBytes("ResultSet r;\r\n");
        randomAccessFile.writeBytes("r=ps.executeQuery();\r\n");
        randomAccessFile.writeBytes("if(r.next())\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("temp=new "+className+"DTO();\r\n");
        for(j=0;j<size;j++)
        {
            randomAccessFile.writeBytes("temp.set"+propertyNameArray[j].substring(0,1).toUpperCase()+propertyNameArray[j].substring(1)+"(r.get"+javaTypeArray[j].substring(0,1).toUpperCase()+javaTypeArray[j].substring(1)+"(\""+sqlPropertyNameArray[j]+"\"));\r\n");
        }
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("else \r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("r.close();\r\n");
        randomAccessFile.writeBytes("ps.close();\r\n");
        randomAccessFile.writeBytes("c.close();\r\n");
        randomAccessFile.writeBytes("throw new DAOException(\"Invalid "+propertyNameArray[0]+"\");\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("r.close();\r\n");
        randomAccessFile.writeBytes("ps.close();\r\n");
        randomAccessFile.writeBytes("c.close();\r\n");
        randomAccessFile.writeBytes("return temp;\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("catch(SQLException sqlException)\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("throw new DAOException(sqlException.getMessage());\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("}\r\n");
        
        //GET BY PRIMARY KEY (ENDS)
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.close();
        
        // METHOD FORMATION END CLASS END
        
        
        
        
        //DAO INTERFACE (START)
        
        interfaceFile=new File(location+"\\"+bpn2+"\\dl\\interfaces\\"+className+"DAOInterface.java");
        if(interfaceFile.exists()) interfaceFile.delete();
        randomAccessFile=new RandomAccessFile(interfaceFile,"rw");
        randomAccessFile.writeBytes("package "+bpn+".dl.interfaces;\r\n");
        randomAccessFile.writeBytes("import "+bpn+".dl.exceptions.*;\r\n");
        randomAccessFile.writeBytes("import java.util.*;\r\n");
        randomAccessFile.writeBytes("public interface "+className+"DAOInterface \r\n");
        randomAccessFile.writeBytes("{\r\n");
        
        randomAccessFile.writeBytes("public void add("+className+"DTOInterface "+className.substring(0,1).toLowerCase()+className.substring(1)+"DTOInterface) throws DAOException;\r\n");
        randomAccessFile.writeBytes("public void update("+className+"DTOInterface "+className.substring(0,1).toLowerCase()+className.substring(1)+"DTOInterface) throws DAOException;\r\n");
        randomAccessFile.writeBytes("public void delete("+javaTypeArray[0]+" "+propertyNameArray[0]+") throws DAOException;\r\n");
        randomAccessFile.writeBytes("public List<"+className+"DTOInterface> getAll() throws DAOException;\r\n");
        randomAccessFile.writeBytes("public int getCount() throws DAOException;\r\n");
        randomAccessFile.writeBytes("public "+className+"DTOInterface getBy"+propertyNameArray[0].substring(0,1).toUpperCase()+propertyNameArray[0].substring(1)+"("+javaTypeArray[0]+" "+propertyNameArray[0]+") throws DAOException;\r\n");
        
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.close();
        // DAO INTERFACE (END)
        
        //DAO EXCEPTION (START)
        exceptionFile=new File(location+"\\"+bpn2+"\\dl\\exceptions\\DAOException.java");
        if(!exceptionFile.exists()) exceptionFile.delete();
        
        randomAccessFile=new RandomAccessFile(exceptionFile,"rw");
        randomAccessFile.writeBytes("package "+bpn+".dl.exceptions;\r\n");
        randomAccessFile.writeBytes("public class DAOException extends Exception implements java.io.Serializable \r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("public DAOException(String message)\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("super(message);\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.close();         
    }
}