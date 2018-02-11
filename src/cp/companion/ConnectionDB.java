package cp.companion;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class ConnectionDB {

    private Statement st = null;
    private PreparedStatement pSt = null;
    private ResultSet rs = null;
    private Connection con = null;
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
            onFailure();
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
            onFailure();
        }
    }
    
    
    public void createAndExecuteFieldInserts(int lote) {
        try {
            if (lote < 1 || lote > 5) {return;}
            String currentSQL = "";    
            int pos = 1;
            for (int i = 0; i < Daemon.stmntsInsertField.length; i++) {
                currentSQL = Daemon.stmntsInsertField[i];
                if (i == 0 || i == 2 || i == 3 || i == 5 || i == 6){
                    st = con.createStatement();
                    st.execute(currentSQL);          
                }
                else if (i == 1){
                    st = con.createStatement();
                    rs = st.executeQuery(currentSQL);
                }              
                else if (i == 4){   
                    st = con.createStatement();
                    rs = st.executeQuery("SELECT MAX (POSICION) AS POSICION FROM CAMPOSLIBRESCONFIG");
                    if (rs.next()){
                        pos = rs.getInt("POSICION") + 1;
                    }
                    String temp = currentSQL+"'VENCIMIENTO_LOTE"+lote+"',"+pos+",'VENCIMIENTO_LOTE"+lote+"',5,0,1)";
                    pSt = con.prepareStatement(temp);
                    pSt.executeUpdate();
                }      
            }
            
        }catch (SQLException ex) {
            System.out.println("ERROR AL EJECUTAR INSERT FIELD: " + ex);  
            ex.printStackTrace();
            onFailure();
        }catch (Exception ex) {
            System.out.println("ERROR AL EJECUTAR INSERT FIELD: " + ex);  
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
            onFailure();
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
            onFailure();
        }catch (Exception ex) {
            System.out.println("ERROR AL EJECUTAR QUERY STOCKS: " + ex);  
            ex.printStackTrace();
        }   
   }
    
    
    public void createAndExecuteQueryFields() {
        try {
            st = con.createStatement();
            rs = st.executeQuery(Daemon.queryFieldsExist);
        }catch (SQLException ex) {
            System.out.println("ERROR AL EJECUTAR QUERY FIELDS: " + ex);  
            ex.printStackTrace();
            onFailure();
        }catch (Exception ex) {
            System.out.println("ERROR AL EJECUTAR QUERY FIELDS: " + ex);  
            ex.printStackTrace();
        }   
   }
    
    
    public boolean RSgetNext() {
        try {
            return rs.next();
        }catch (SQLException ex) {
            System.out.println("ERROR AL EJECUTAR rs.next(): " + ex);  
            ex.printStackTrace();
            onFailure();
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
            onFailure();
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
            onFailure();
            return 0;
        }catch (Exception ex) {
            System.out.println("ERROR AL EJECUTAR rs.getInt("+key+"): " + ex);   
            ex.printStackTrace();
            return 0;
        }   
   }
    
    public float RSgetFloat(String key) {
        try {
            return rs.getFloat(key);
        }catch (SQLException ex) {
            System.out.println("ERROR AL EJECUTAR rs.getFloat("+key+"): " + ex); 
            ex.printStackTrace();
            onFailure();
            return 0;
        }catch (Exception ex) {
            System.out.println("ERROR AL EJECUTAR rs.getFloat("+key+"): " + ex);   
            ex.printStackTrace();
            return 0;
        }   
   }
    
   
    private void onFailure(){
        MainMenu.GetInstance().daemon.interrupt();
        MainMenu.GetInstance().daemon = null;
        MainMenu.GetInstance().getLabelCon().setText("DESCONECTADO");
        MainMenu.GetInstance().getLabelCon().setOpaque(true);
        MainMenu.GetInstance().getLabelCon().setBackground(Color.red);        
    }
}
