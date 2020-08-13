import java.util.*;
import java.sql.*;
public class TypeMappings
{
    private static HashMap<String,String> mappings;
    static
    {
        mappings=new HashMap<>();
        mappings.put("TINYINT","int");
        mappings.put("SMALLINT","int");
        mappings.put("INTEGER","int");
        mappings.put("BIGINT","long");
        mappings.put("FLOAT","float");
        mappings.put("DOUBLE","double");
        mappings.put("CHAR","String");
        mappings.put("VARCHAR","String");
        mappings.put("LONGVARCHAR","String");
    }
    public static String getJavaType(String dataType)
    {
        return mappings.get(dataType); 
    }
}


