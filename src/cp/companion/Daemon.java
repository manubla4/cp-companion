/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.companion;

import java.time.LocalDate;
import java.time.Period;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author manub
 */
class Daemon implements Runnable {
    
    public static final String queryDepartamentos = "SELECT NUMDPTO FROM DEPARTAMENTOS";
    public static final String queryStocks = "SELECT st.CODARTICULO, ar.DESCRIPCION, st.CODALMACEN, al.NOMBREALMACEN, "
            + "st.STOCK, st.MINIMO, st.MAXIMO FROM STOCKS st JOIN ARTICULOS ar ON ar.CODARTICULO = st.CODARTICULO "
            + "JOIN ALMACEN al ON al.CODALMACEN = st.CODALMACEN";
    public static final String queryVencimientos = "SELECT ar.CODARTICULO, ar.DESCRIPCION, cl.VENCIMIENTO_LOTE1, cl.VENCIMIENTO_LOTE2, "
            + "cl.VENCIMIENTO_LOTE3, cl.VENCIMIENTO_LOTE4, cl.VENCIMIENTO_LOTE5 FROM ARTICULOS ar "
            + "JOIN ARTICULOSCAMPOSLIBRES cl ON ar.CODARTICULO = cl.CODARTICULO;";
    public static final String queryFieldsExist = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'ARTICULOSCAMPOSLIBRES' "
            + "AND (COLUMN_NAME = 'VENCIMIENTO_LOTE1' OR COLUMN_NAME = 'VENCIMIENTO_LOTE2' OR "
            + "COLUMN_NAME = 'VENCIMIENTO_LOTE3' OR COLUMN_NAME = 'VENCIMIENTO_LOTE4' OR COLUMN_NAME = 'VENCIMIENTO_LOTE5')";
    public static final String[] stmntsInsertField = {"SET NO_BROWSETABLE ON",
                                                "SELECT TOP 0 * FROM CAMPOSLIBRESCONFIG",
                                                "SET NO_BROWSETABLE OFF",
                                                "SET FMTONLY ON select TABLA,CAMPO,POSICION,ETIQUETA,TIPO,TAMANY,TIPOVALOR from CAMPOSLIBRESCONFIG WHERE 1=2 SET FMTONLY OFF",
                                                "INSERT INTO CAMPOSLIBRESCONFIG\n" +
                                                    "(TABLA, CAMPO, POSICION, ETIQUETA, TIPO, TAMANY, TIPOVALOR)\n" +
                                                    "VALUES (1,",
                                                "ALTER TABLE ARTICULOSCAMPOSLIBRES ADD VENCIMIENTO_LOTE",
                                                "IF @@TRANCOUNT > 0 COMMIT TRAN"};
    
    private ConnectionDB conDB = new ConnectionDB();
    
    public void run() {
        try {
            LocalDate lastDateCheck = LocalDate.now().minus(Period.ofDays(1));
            long daysBetween;
            String loteString;
            LocalDate localDate;
            int articulosSinStock = 0;
            int lotesVencidos = 0;
                    
            while (true){
                conDB.connect();
                conDB.createAndExecuteQueryStocks();
                while (conDB.RSgetNext()){
                    if (conDB.RSgetFloat("STOCK") <= conDB.RSgetFloat("MINIMO")){
                        articulosSinStock += 1;
                        if (MainMenu.GetInstance().activeFrame){
                            JOptionPane.showMessageDialog(MainMenu.GetInstance(),
                                        "STOCK MINIMO ALCANZADO !\nCÓDIGO ARTÍCULO: "+conDB.RSgetString("CODARTICULO"),
                                        "  Advertencia",
                                        JOptionPane.WARNING_MESSAGE);
                        }
                        else if (ConfigMenu.GetInstance().activeFrame){
                            JOptionPane.showMessageDialog(ConfigMenu.GetInstance(),
                                        "STOCK MINIMO ALCANZADO !\nCÓDIGO ARTÍCULO: "+conDB.RSgetString("CODARTICULO"),
                                        "  Advertencia",
                                        JOptionPane.WARNING_MESSAGE);
                        }
                    }               
                    
                }
                
                MainMenu.GetInstance().setLabelStocks("EN STOCK MINIMO: "+articulosSinStock, articulosSinStock);
                articulosSinStock = 0;
                    
                localDate = LocalDate.now();
                daysBetween =  DAYS.between(lastDateCheck, localDate);
                if (daysBetween >= 1){
                    lastDateCheck = LocalDate.now();
                    conDB.createAndExecuteQueryVenc();
                    while (conDB.RSgetNext()){

                        loteString = "";
                        LocalDate lote1 = conDB.RSgetDate("VENCIMIENTO_LOTE1");
                        LocalDate lote2 = conDB.RSgetDate("VENCIMIENTO_LOTE2");
                        LocalDate lote3 = conDB.RSgetDate("VENCIMIENTO_LOTE3");
                        LocalDate lote4 = conDB.RSgetDate("VENCIMIENTO_LOTE4");
                        LocalDate lote5 = conDB.RSgetDate("VENCIMIENTO_LOTE5");

                        if (lote1 != null && lote1.compareTo(localDate) <= 0){
                            loteString = " - LOTE 1";
                            lotesVencidos += 1;
                        }
                        
                        if (lote2 != null && lote2.compareTo(localDate) <= 0){
                            loteString += " - LOTE 2";
                            lotesVencidos += 1;
                        }
                        
                        if (lote3 != null && lote3.compareTo(localDate) <= 0){
                            loteString += " - LOTE 3";
                            lotesVencidos += 1;
                        }
                            
                        if (lote4 != null && lote4.compareTo(localDate) <= 0){
                            loteString += " - LOTE 4";
                            lotesVencidos += 1;
                        }
                        
                        if (lote5 != null && lote5.compareTo(localDate) <= 0){
                            loteString += " - LOTE 5";
                            lotesVencidos += 1;
                        }

                        if (loteString != ""){
                            if (MainMenu.GetInstance().activeFrame){
                                JOptionPane.showMessageDialog(MainMenu.GetInstance(),
                                            "FECHA VENCIMIENTO ALCANZADA !\nCÓDIGO ARTÍCULO: "+conDB.RSgetString("CODARTICULO")+"\nLOTES VENCIDOS:"+loteString,
                                            "  Advertencia",
                                            JOptionPane.WARNING_MESSAGE);
                            }
                            else if (ConfigMenu.GetInstance().activeFrame){
                                JOptionPane.showMessageDialog(ConfigMenu.GetInstance(),
                                            "FECHA VENCIMIENTO ALCANZADA !\nCÓDIGO ARTÍCULO: "+conDB.RSgetString("CODARTICULO")+"\nLOTES VENCIDOS: "+loteString,
                                            "  Advertencia",
                                            JOptionPane.WARNING_MESSAGE);
                            }             
                        }
                    }
                    
                    MainMenu.GetInstance().setLabelVencidos("VENCIDOS: "+lotesVencidos, lotesVencidos);
                    lotesVencidos = 0;
                 
                    
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
