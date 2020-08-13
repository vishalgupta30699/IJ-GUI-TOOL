import java.io.*;
import java.util.*;
public class DTOGenerator
{

    public DTOGenerator(Table table) throws IOException
    {
        int i;
        File pathFile,interfacePathFile,interfaceFile;
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
        fullPath=location+"\\"+bpn2+"\\dl\\dto";
        pathFile=new File(fullPath);
        interfacePathFile=new File(location+"\\"+bpn2+"\\dl\\interfaces");
        if(pathFile.exists()==false || interfacePathFile.exists()==false)
        {
            pathFile.mkdirs();
            interfacePathFile.mkdirs();
        }
        String className=table.getTableName();
        className=className.substring(0,1).toUpperCase()+className.substring(1).toLowerCase();
        while(className.indexOf('_')>=0)
        {
            i=className.indexOf('_');
            String s1=className.substring(0,i);
            String s2=className.substring(i+1,i+2).toUpperCase();
            String s3=className.substring(i+2);
            className=s1+s2+s3;
        }
        String fileName=fullPath+"\\"+className+"DTO.java";
        File dtoFile=new File(fileName);
        if(dtoFile.exists()) dtoFile.delete();
        randomAccessFile=new RandomAccessFile(dtoFile,"rw");
        randomAccessFile.writeBytes("package "+bpn+".dl.dto;\r\n");
        randomAccessFile.writeBytes("import "+bpn+".dl.interfaces.*;\r\n");
        randomAccessFile.writeBytes("public class "+className+"DTO implements "+className+"DTOInterface\r\n");
        randomAccessFile.writeBytes("{\r\n");
        LinkedList<Column> columnList=table.getColumnList();
        int size=columnList.size();
        String[] propertyNameArray=new String[size];
        String[] javaTypeArray=new String[size];

        int j=0;
        for(Column cc:columnList)
        {
            propertyName=cc.getColumnName();
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
            javaType="";
            javaType=TypeMappings.getJavaType(propertyType);
            javaTypeArray[j]=javaType;
            randomAccessFile.writeBytes("private "+javaType+" "+propertyName+";\r\n");
            j++;
        }
        randomAccessFile.writeBytes("public "+className+"DTO()\r\n");
        randomAccessFile.writeBytes("{\r\n");
        for(i=0;i<size;i++)
        {
            if(javaTypeArray[i].equals("int") || javaTypeArray[i].equals("long"))
            {
            randomAccessFile.writeBytes("this."+propertyNameArray[i]+"=0;\r\n");
            }
            if( javaTypeArray[i].equals("double") || javaTypeArray[i].equals("float"))
            {
            randomAccessFile.writeBytes("this."+propertyNameArray[i]+"=0.0;\r\n");
            }
            if(javaTypeArray[i].equals("String"))
            {
            randomAccessFile.writeBytes("this."+propertyNameArray[i]+"=\"\";\r\n");
            }
        }
        randomAccessFile.writeBytes("}\r\n");

        for(i=0;i<size;i++)
        {
            randomAccessFile.writeBytes("public void set"+propertyNameArray[i].substring(0,1).toUpperCase()+propertyNameArray[i].substring(1)+"("+javaTypeArray[i]+" "+propertyNameArray[i]+")\r\n");
            randomAccessFile.writeBytes("{\r\n");
            randomAccessFile.writeBytes("this."+propertyNameArray[i]+"="+propertyNameArray[i]+";\r\n");
            randomAccessFile.writeBytes("}\r\n");
            randomAccessFile.writeBytes("public "+javaTypeArray[i]+" get"+propertyNameArray[i].substring(0,1).toUpperCase()+propertyNameArray[i].substring(1)+"()\r\n");
            randomAccessFile.writeBytes("{\r\n");
            randomAccessFile.writeBytes("return this."+propertyNameArray[i]+";\r\n");
            randomAccessFile.writeBytes("}\r\n");
        }

        randomAccessFile.writeBytes("public boolean equals(Object object)\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("if(object==null) return false;\r\n");
        randomAccessFile.writeBytes("if(!(object instanceof "+className+"DTOInterface)) return false;\r\n");
        randomAccessFile.writeBytes(className+"DTOInterface other=("+className+"DTOInterface)object;\r\n");
        if(javaTypeArray[0].equals("int")|| javaTypeArray[0].equals("float") || javaTypeArray[0].equals("double"))
        {
            randomAccessFile.writeBytes("return this."+propertyNameArray[0]+"==other.get"+propertyNameArray[0].substring(0,1).toUpperCase()+propertyNameArray[0].substring(1)+"();\r\n");
        }
        if(javaTypeArray[0].equals("String")|| javaTypeArray[0].equals("char"))
        {
            randomAccessFile.writeBytes("return this."+propertyNameArray[0]+".equals(other.get"+propertyNameArray[0].substring(0,1).toUpperCase()+propertyNameArray[0].substring(1)+"());\r\n");
        }
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("public int compareTo("+className+"DTOInterface other)\r\n");
        randomAccessFile.writeBytes("{\r\n");
        if(javaTypeArray[0].equals("int")|| javaTypeArray[0].equals("float") || javaTypeArray[0].equals("double"))
        {
            randomAccessFile.writeBytes("return this."+propertyNameArray[0]+"-other.get"+propertyNameArray[0].substring(0,1).toUpperCase()+propertyNameArray[0].substring(1)+"();\r\n");
        }
        if(javaTypeArray[0].equals("String")|| javaTypeArray[0].equals("char"))
        {
            randomAccessFile.writeBytes("return this."+propertyNameArray[0]+"compareTo(other.get"+propertyNameArray[0].substring(0,1).toUpperCase()+propertyNameArray[0].substring(1)+"());\r\n");
        }
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.writeBytes("public "+javaTypeArray[0]+" hashCode()\r\n");
        randomAccessFile.writeBytes("{\r\n");
        randomAccessFile.writeBytes("return this."+propertyNameArray[0]+";\r\n");
        randomAccessFile.writeBytes("}\r\n");

        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.close();

        //CODE OF DTO INTERFACE
        interfaceFile=new File(location+"\\"+bpn2+"\\dl\\interfaces\\"+className+"DTOInterface.java");
        if(interfaceFile.exists()) interfaceFile.delete();
        randomAccessFile=new RandomAccessFile(interfaceFile,"rw");
        randomAccessFile.writeBytes("package "+bpn+".dl.interfaces;\r\n");
        randomAccessFile.writeBytes("public interface "+className+"DTOInterface extends java.io.Serializable,Comparable<"+className+"DTOInterface>\r\n");
        randomAccessFile.writeBytes("{\r\n");
        for(i=0;i<size;i++)
        {
            randomAccessFile.writeBytes("public void set"+propertyNameArray[i].substring(0,1).toUpperCase()+propertyNameArray[i].substring(1)+"("+javaTypeArray[i]+" "+propertyNameArray[i]+");\r\n");
            randomAccessFile.writeBytes("public "+javaTypeArray[i]+" get"+propertyNameArray[i].substring(0,1).toUpperCase()+propertyNameArray[i].substring(1)+"();\r\n");
        }
        randomAccessFile.writeBytes("}\r\n");
        randomAccessFile.close();
    }

}







