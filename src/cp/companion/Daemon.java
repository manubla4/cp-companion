/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.companion;

import java.awt.Color;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author manub
 */
class Daemon implements Runnable {
    
    private String queryStocks = "SELECT st.CODARTICULO, ar.DESCRIPCION, st.CODALMACEN, al.NOMBREALMACEN, st.STOCK, st.MINIMO, st.MAXIMO FROM STOCKS st JOIN ARTICULOS ar ON ar.CODARTICULO = st.CODARTICULO JOIN ALMACEN al ON al.CODALMACEN = st.CODALMACEN";
    private String queryVencimientos = "SELECT ar.CODARTICULO, ar.DESCRIPCION, cl.VENCIMIENTO FROM ARTICULOS ar JOIN ARTICULOSCAMPOSLIBRES cl ON ar.CODARTICULO = cl.CODARTICULO;";

    
    public void run() {
        try {
            while (true){
                ConnectionDB.GetInstance().connect();
                ConnectionDB.GetInstance().st = ConnectionDB.GetInstance().con.createStatement();
                ConnectionDB.GetInstance().rs = ConnectionDB.GetInstance().st.executeQuery(queryStocks);
                while (ConnectionDB.GetInstance().rs.next()){
//                    System.out.println(ConnectionDB.GetInstance().rs.getFloat("STOCK") + " >= " +ConnectionDB.GetInstance().rs.getFloat("MAXIMO") + " ?");
                    if (ConnectionDB.GetInstance().rs.getFloat("STOCK") >= ConnectionDB.GetInstance().rs.getFloat("MAXIMO")){
//                        System.out.println("JACKPOT!");
                        InfoDialog.GetInstance().setLabelText("STOCK MAXIMO ALCANZADO!");
//                        InfoDialog.GetInstance().setLabelImage(false);
                        InfoDialog.GetInstance().setVisible(true);
                    }               
                    
                }
                ConnectionDB.GetInstance().disconnect();
//                System.out.println("Me duermo!");
                Thread.sleep(Preferences.GetInstance().time); 
//                System.out.println("Desperte!");
                
            }
        } 
        
        catch (InterruptedException ex) {
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            MainMenu.GetInstance().daemon.interrupt();
            MainMenu.GetInstance().daemon = null;
            MainMenu.GetInstance().getLabelCon().setText("DESCONECTADO");
            MainMenu.GetInstance().getLabelCon().setOpaque(true);
            MainMenu.GetInstance().getLabelCon().setBackground(Color.red);        
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
        }      
        
    }
    
    
}
