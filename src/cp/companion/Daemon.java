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
    
    public static final String queryStocks = "SELECT st.CODARTICULO, ar.DESCRIPCION, st.CODALMACEN, al.NOMBREALMACEN, "
            + "st.STOCK, st.MINIMO, st.MAXIMO FROM STOCKS st JOIN ARTICULOS ar ON ar.CODARTICULO = st.CODARTICULO "
            + "JOIN ALMACEN al ON al.CODALMACEN = st.CODALMACEN";
    public static final String queryVencimientos = "SELECT ar.CODARTICULO, ar.DESCRIPCION, cl.VENCIMIENTO_LOTE1, cl.VENCIMIENTO_LOTE2, "
            + "cl.VENCIMIENTO_LOTE3, cl.VENCIMIENTO_LOTE4, cl.VENCIMIENTO_LOTE5 FROM ARTICULOS ar "
            + "JOIN ARTICULOSCAMPOSLIBRES cl ON ar.CODARTICULO = cl.CODARTICULO;";
    public static final String queryFieldsExist = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'ARTICULOSCAMPOSLIBRES' "
            + "AND (COLUMN_NAME = 'VENCIMIENTO_LOTE1' OR COLUMN_NAME = 'VENCIMIENTO_LOTE2' OR "
            + "COLUMN_NAME = 'VENCIMIENTO_LOTE3' OR COLUMN_NAME = 'VENCIMIENTO_LOTE4' OR COLUMN_NAME = 'VENCIMIENTO_LOTE5')";
    private ConnectionDB conDB = new ConnectionDB();
    
    public void run() {
        try {
            while (true){
                conDB.connect();
                conDB.createAndExecuteQueryStocks();
                while (conDB.RSgetNext()){
//                    System.out.println(conDB.rs.getFloat("STOCK") + " >= " +conDB.rs.getFloat("MAXIMO") + " ?");
                    if (conDB.RSgetFloat("STOCK") >= conDB.RSgetFloat("MAXIMO")){
//                        System.out.println("JACKPOT!");
                        InfoDialog.GetInstance().setLabelText("STOCK MINIMO ALCANZADO!");
//                        InfoDialog.GetInstance().setLabelImage(false);
                        InfoDialog.GetInstance().setVisible(true);
                    }               
                    
                }
                conDB.disconnect();
//                System.out.println("Me duermo!");
                Thread.sleep(Preferences.GetInstance().time); 
//                System.out.println("Desperte!");
                
            }
        } 
        
        catch (InterruptedException ex) {
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
    
    
}
