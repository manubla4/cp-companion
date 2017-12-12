package cp.companion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class ConnectionDB {

    static ConnectionDB selfInstance;
    Statement st = null;
    ResultSet rs = null;
    Connection con = null;
    String url;

    
    static ConnectionDB GetInstance(){
        if (selfInstance == null){
            selfInstance = new ConnectionDB();
        }
        return selfInstance;
    }
    
    
    public boolean testConnection(String server, String databaseName, String user, String password, boolean instance) throws ClassNotFoundException, Exception {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");       
            url = "jdbc:sqlserver://" + server + ";databaseName=" + databaseName + ";user=" + user + ";password=" + password + ";integratedSecurity=true;";
            if (instance) 
                url = "jdbc:sqlserver://" + server + "\\SQLEXPRESS;databaseName=" + databaseName + ";user=" + user + ";password=" + password + ";integratedSecurity=true;";
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
            url = "jdbc:sqlserver://" + Preferencias.GetInstance().ip +":"+Preferencias.GetInstance().tcp+ ";databaseName=" + Preferencias.GetInstance().databaseName + ";user=" + Preferencias.GetInstance().user + ";password=" + Preferencias.GetInstance().password + ";integratedSecurity=true;";
            if (Preferencias.GetInstance().instance) 
                url = "jdbc:sqlserver://" + Preferencias.GetInstance().ip +":"+Preferencias.GetInstance().tcp + "\\SQLEXPRESS;databaseName=" + Preferencias.GetInstance().databaseName + ";user=" + Preferencias.GetInstance().user + ";password=" + Preferencias.GetInstance().password + ";integratedSecurity=true;";
            con = DriverManager.getConnection(url);
            System.out.println("..................CONEXIÓN ESTABLECIDA..................");
        } catch (SQLException ex) {
            System.out.println("ERROR AL CONECTAR: " + ex);
        }
    }


    public void disconnect() throws Exception {
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
