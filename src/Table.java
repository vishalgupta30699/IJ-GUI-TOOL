import java.util.*;
public class Table
{
private String name;
private LinkedList<Column> columns;
public Table()
{
columns=new LinkedList<Column>();
}
public String toString()
{
return name;
}
public void setTableName(String name)
{
this.name=name;
}
public String getTableName()
{
return this.name;
}
public void addColumn(Column column)
{
columns.add(column);
}
public LinkedList<Column> getColumnList()
{
return columns;
}
}