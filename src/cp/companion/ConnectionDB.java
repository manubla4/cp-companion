package cp.companion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class ConnectionDB {

    public Statement st = null;
    public ResultSet rs = null;
    public Connection con = null;
    private String url;

    
    
    public boolean testConnection(String server, String databaseName, String user, String password, boolean instance) {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");       
            url = "jdbc:sqlserver://" + server + ";databaseName=" + databaseName + ";user=" + user + ";password=" + password;
            if (instance) 
                url = "jdbc:sqlserver://" + server + "\\SQLEXPRESS;databaseName=" + databaseName + ";user=" + user + ";password=" + password;
            System.out.println("PROBANDO CONEXIÓN CON URL = " + url);
            con = DriverManager.getConnection(url);
            disconnect();
            return true;
        }catch (SQLException ex) {
            System.out.println("ERROR PROBANDO CONEXIÓN: " + ex);
            ex.printStackTrace();
            return false;
        }catch (ClassNotFoundException ex) {
            System.out.println("ERROR PROBANDO CONEXIÓN: " + ex);
            ex.printStackTrace();
            return false;
        }catch (Exception ex) {
            System.out.println("ERROR PROBANDO CONEXIÓN: " + ex);
            ex.printStackTrace();
            return false;
        }
    }
    
    
    public boolean testConnectionSavedPrefs() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");       
            url = "jdbc:sqlserver://" + Preferences.GetInstance().ip +":"+Preferences.GetInstance().tcp+ ";databaseName=" + Preferences.GetInstance().databaseName + ";user=" + Preferences.GetInstance().user + ";password=" + Preferences.GetInstance().password;
            if (Preferences.GetInstance().instance) 
                url = "jdbc:sqlserver://" + Preferences.GetInstance().ip +":"+Preferences.GetInstance().tcp + "\\SQLEXPRESS;databaseName=" + Preferences.GetInstance().databaseName + ";user=" + Preferences.GetInstance().user + ";password=" + Preferences.GetInstance().password;
            System.out.println("PROBANDO CONEXIÓN CON URL = " + url);
            con = DriverManager.getConnection(url);
            disconnect();
            return true;
        }catch (SQLException ex) {
            System.out.println("ERROR PROBANDO CONEXIÓN GUARDADA: " + ex);
            ex.printStackTrace();
            return false;
        }catch (ClassNotFoundException ex) {
            System.out.println("ERROR PROBANDO CONEXIÓN GUARDADA: " + ex);
            ex.printStackTrace();
            return false;
        }catch (Exception ex) {
            System.out.println("ERROR PROBANDO CONEXIÓN GUARDADA: " + ex);
            ex.printStackTrace();
            return false;
        }
        
    }
    

    public void connect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            url = "jdbc:sqlserver://" + Preferences.GetInstance().ip +":"+Preferences.GetInstance().tcp+ ";databaseName=" + Preferences.GetInstance().databaseName + ";user=" + Preferences.GetInstance().user + ";password=" + Preferences.GetInstance().password;
            if (Preferences.GetInstance().instance) 
                url = "jdbc:sqlserver://" + Preferences.GetInstance().ip +":"+Preferences.GetInstance().tcp + "\\SQLEXPRESS;databaseName=" + Preferences.GetInstance().databaseName + ";user=" + Preferences.GetInstance().user + ";password=" + Preferences.GetInstance().password;
            con = DriverManager.getConnection(url);
            System.out.println("..................CONEXIÓN ESTABLECIDA..................");
        }catch (SQLException ex) {
            System.out.println("ERROR AL CONECTAR: " + ex);
            ex.printStackTrace();
        }catch (ClassNotFoundException ex) {
            System.out.println("ERROR AL CONECTAR: " + ex);
            ex.printStackTrace();
        }catch (Exception ex) {
            System.out.println("ERROR AL CONECTAR: " + ex);    
            ex.printStackTrace();
        }  
   }


    public void disconnect() {
        try{
            if (con != null) {
                con.commit();
                con.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
        }catch (SQLException ex) {
            System.out.println("ERROR AL DESCONECTAR: " + ex); 
            ex.printStackTrace();
        }
    }
    
    
    
    public void createAndExecuteQueryStocks() {
        try {
            st = con.createStatement();
            rs = st.executeQuery(Daemon.queryStocks);
        }catch (SQLException ex) {
            System.out.println("ERROR AL EJECUTAR QUERY STOCKS: " + ex);  
            ex.printStackTrace();
        }catch (Exception ex) {
            System.out.println("ERROR AL EJECUTAR QUERY STOCKS: " + ex);  
            ex.printStackTrace();
        }   
   }
    
    public void createAndExecuteQueryVenc() {
        try {
            st = con.createStatement();
            rs = st.executeQuery(Daemon.queryVencimientos);
        }catch (SQLException ex) {
            System.out.println("ERROR AL EJECUTAR QUERY STOCKS: " + ex);  
            ex.printStackTrace();
        }catch (Exception ex) {
            System.out.println("ERROR AL EJECUTAR QUERY STOCKS: " + ex);  
            ex.printStackTrace();
        }   
   }
    
    public boolean RSgetNext() {
        try {
            return rs.next();
        }catch (SQLException ex) {
            System.out.println("ERROR AL EJECUTAR rs.next(): " + ex);  
            ex.printStackTrace();
            return false;
        }catch (Exception ex) {
            System.out.println("ERROR AL EJECUTAR rs.next(): " + ex);  
            ex.printStackTrace();
            return false;
        }   
   }
    
    public String RSgetString(String key) {
        try {
            return rs.getString(key);
        }catch (SQLException ex) {
            System.out.println("ERROR AL EJECUTAR rs.getString("+key+"): " + ex);  
            ex.printStackTrace();
            return "";
        }catch (Exception ex) {
            System.out.println("ERROR AL EJECUTAR rs.getString("+key+"): " + ex);   
            ex.printStackTrace();
            return "";
        }   
   }
    
    public int RSgetInt(String key) {
        try {
            return rs.getInt(key);
        }catch (SQLException ex) {
            System.out.println("ERROR AL EJECUTAR rs.getInt("+key+"): " + ex); 
            ex.printStackTrace();
            return 0;
        }catch (Exception ex) {
            System.out.println("ERROR AL EJECUTAR rs.getInt("+key+"): " + ex);   
            ex.printStackTrace();
            return 0;
        }   
   }
}
