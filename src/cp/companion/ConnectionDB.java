package cp.companion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class ConnectionDB {

    private static ConnectionDB selfInstance;
    public Statement st = null;
    public ResultSet rs = null;
    public Connection con = null;
    private String url;

    
    static ConnectionDB GetInstance(){
        if (selfInstance == null){
            selfInstance = new ConnectionDB();
        }
        return selfInstance;
    }
    
    
    public boolean testConnection(String server, String databaseName, String user, String password, boolean instance) throws ClassNotFoundException, Exception {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");       
            url = "jdbc:sqlserver://" + server + ";databaseName=" + databaseName + ";user=" + user + ";password=" + password;
            if (instance) 
                url = "jdbc:sqlserver://" + server + "\\SQLEXPRESS;databaseName=" + databaseName + ";user=" + user + ";password=" + password;
            System.out.println("PROBANDO CONEXIÓN CON URL = " + url);
            con = DriverManager.getConnection(url);
            disconnect();
            return true;
        } catch (SQLException ex) {
            System.out.println("ERROR PROBANDO CONEXIÓN: " + ex);
            return false;
        }
    }
    
    
    public boolean testConnectionSavedPrefs() throws ClassNotFoundException, Exception {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");       
            url = "jdbc:sqlserver://" + Preferences.GetInstance().ip +":"+Preferences.GetInstance().tcp+ ";databaseName=" + Preferences.GetInstance().databaseName + ";user=" + Preferences.GetInstance().user + ";password=" + Preferences.GetInstance().password;
            if (Preferences.GetInstance().instance) 
                url = "jdbc:sqlserver://" + Preferences.GetInstance().ip +":"+Preferences.GetInstance().tcp + "\\SQLEXPRESS;databaseName=" + Preferences.GetInstance().databaseName + ";user=" + Preferences.GetInstance().user + ";password=" + Preferences.GetInstance().password;
            System.out.println("PROBANDO CONEXIÓN CON URL = " + url);
            con = DriverManager.getConnection(url);
            disconnect();
            return true;
        } catch (SQLException ex) {
            System.out.println("ERROR PROBANDO CONEXIÓN: " + ex);
            return false;
        }
    }
    

    public void connect() throws ClassNotFoundException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            url = "jdbc:sqlserver://" + Preferences.GetInstance().ip +":"+Preferences.GetInstance().tcp+ ";databaseName=" + Preferences.GetInstance().databaseName + ";user=" + Preferences.GetInstance().user + ";password=" + Preferences.GetInstance().password;
            if (Preferences.GetInstance().instance) 
                url = "jdbc:sqlserver://" + Preferences.GetInstance().ip +":"+Preferences.GetInstance().tcp + "\\SQLEXPRESS;databaseName=" + Preferences.GetInstance().databaseName + ";user=" + Preferences.GetInstance().user + ";password=" + Preferences.GetInstance().password;
            con = DriverManager.getConnection(url);
            System.out.println("..................CONEXIÓN ESTABLECIDA..................");
        } catch (SQLException ex) {
            System.out.println("ERROR AL CONECTAR: " + ex);        
        }
    }


    public void disconnect() throws SQLException {
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

    }
}
