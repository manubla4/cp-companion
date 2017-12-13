/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.companion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author manub
 */
class Daemon implements Runnable {
    
    String sqlQuery1 = "SELECT * FROM ARTICULOS";
    String sqlQuery2 = "SELECT * FROM STOCKS";
    
    
    public void run() {
        try {
            while (true){
                System.out.println("Me duermo!");
                Thread.sleep(Preferences.GetInstance().time); 
                System.out.println("Desperte!");
                ConnectionDB.GetInstance().connect();
                ConnectionDB.GetInstance().st = ConnectionDB.GetInstance().con.createStatement();
                ConnectionDB.GetInstance().rs = ConnectionDB.GetInstance().st.executeQuery(sqlQuery1);
                while (ConnectionDB.GetInstance().rs.next()){
                    System.out.println(ConnectionDB.GetInstance().rs.getString("DESCRIPCION"));
                }
                ConnectionDB.GetInstance().disconnect();
            }
        } 
        
        catch (InterruptedException ex) {
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    
}
