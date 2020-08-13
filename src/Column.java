public class Column
{
private String name;
private String dataType;
public Column()
{
name="";
dataType="";
}
public String toString()
{
return name+"("+dataType+")";
}
public void setColumnName(String name)
{
this.name=name;
}
public String getColumnName()
{
return name;
}
public void setDataType(String dataType)
{
this.dataType=dataType;
}
public String getDataType()
{
return dataType;
}
}