package data;

import java.sql.Connection;
import java.sql.DriverManager;

public class Userutil {

    private static String JDBC_Driver = "com.mysql.cj.jdbc.Driver";

    private static String DB_Url = "jdbc:mysql://localhost:3306/hospitalsystem?user=root&password=";

    private  static Connection cn = null;
    public static  Connection connectToDB(){
        Connection cn =null;
        try{
            cn = DriverManager.getConnection(DB_Url);
            System.out.println("connected to database");
        }
        catch (Exception e){
            System.out.println("error connecting to database : ");
        }
        return cn ;
    }
}
