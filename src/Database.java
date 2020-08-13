import java.util.*;
public class Database
{
private String name;
private LinkedList<Table> tables;
public Database()
{
tables=new LinkedList<Table>();
}
public String toString()
{
return name;
}
public void setDatabaseName(String name)
{
this.name=name;
}
public String getDatabaseName()
{
return name;
}
public void addTable(Table table)
{
tables.add(table);
}
public LinkedList<Table> getTableList()
{
return tables;
}
}