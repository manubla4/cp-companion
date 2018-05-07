/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.companion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.Period;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author manub
 */
class Daemon implements Runnable {
    
    public static final String queryDepartamentos = "SELECT NUMDPTO FROM DEPARTAMENTO";
    public static final String queryStocks = "SELECT st.CODARTICULO, ar.DESCRIPCION, st.CODALMACEN, al.NOMBREALMACEN, "
            + "st.STOCK, st.MINIMO, st.MAXIMO FROM STOCKS st JOIN ARTICULOS ar ON ar.CODARTICULO = st.CODARTICULO "
            + "JOIN ALMACEN al ON al.CODALMACEN = st.CODALMACEN";
    public static final String queryVencimientos = "SELECT ar.CODARTICULO, ar.DESCRIPCION, cl.VENCIMIENTO_LOTE1, cl.VENCIMIENTO_LOTE2, "
            + "cl.VENCIMIENTO_LOTE3, cl.VENCIMIENTO_LOTE4, cl.VENCIMIENTO_LOTE5 FROM ARTICULOS ar "
            + "JOIN ARTICULOSCAMPOSLIBRES cl ON ar.CODARTICULO = cl.CODARTICULO";
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
            //LocalDate lastDateCheck = LocalDate.now().minus(Period.ofDays(1));
            String loteString;
            LocalDate localDate;
            int articulosSinStock = 0;
            int articulosPorAcabarse = 0;
            int lotesVencidos = 0;                   
            File f = new File("./anticipation.properties");
            Properties props = new Properties();
            if (!f.exists())
                ConfigMenu.GetInstance().createDefaultPreferences();                     
            FileInputStream in = new FileInputStream(f);
            props.load(in);
            in.close();
            
            while (true){
                conDB.connect();
                conDB.createAndExecuteQueryStocks();
                while (conDB.RSgetNext()){
                    String articulo = conDB.RSgetString("CODARTICULO");
                    float minimo = conDB.RSgetFloat("MINIMO");
                    float stock = conDB.RSgetFloat("STOCK");                    
                    float anticipation = Float.parseFloat(props.getProperty("STOCK_"+articulo, Integer.toString(ConfigMenu.DEFAULT_ANTICIPATION)));
                    
                    if (stock <= minimo){
                        articulosSinStock += 1;
                        if (MainMenu.GetInstance().activeFrame){
                            JOptionPane.showMessageDialog(MainMenu.GetInstance(),
                                        "STOCK MINIMO ALCANZADO !\nCÓDIGO ARTÍCULO: "+articulo,
                                        "  Advertencia",
                                        JOptionPane.WARNING_MESSAGE);
                        }
                        else if (ConfigMenu.GetInstance().activeFrame){
                            JOptionPane.showMessageDialog(ConfigMenu.GetInstance(),
                                        "STOCK MINIMO ALCANZADO !\nCÓDIGO ARTÍCULO: "+articulo,
                                        "  Advertencia",
                                        JOptionPane.WARNING_MESSAGE);
                        }
                    }      
                    
                    else if (stock <= (minimo + anticipation)){
                       articulosPorAcabarse += 1;
                       if (MainMenu.GetInstance().activeFrame){
                            JOptionPane.showMessageDialog(MainMenu.GetInstance(),
                                        "STOCK A SOLO "+ Math.round(stock-minimo) +" UNIDADES DE ALCANZAR EL MINIMO !\nCÓDIGO ARTÍCULO: "+articulo,
                                        "  Advertencia",
                                        JOptionPane.WARNING_MESSAGE);
                        }
                        else if (ConfigMenu.GetInstance().activeFrame){
                            JOptionPane.showMessageDialog(ConfigMenu.GetInstance(),
                                        "STOCK A SOLO "+ Math.round(stock-minimo) +" UNIDADES DE ALCANZAR EL MINIMO !\nCÓDIGO ARTÍCULO: "+articulo,
                                        "  Advertencia",
                                        JOptionPane.WARNING_MESSAGE);
                        } 
                        props.setProperty("STOCK_"+articulo, Integer.toString(Math.round(anticipation-1)));
                        OutputStream out = new FileOutputStream(f);
                        props.store(out, "ANTICIPATION PROPERTIES");
                        out.close();
                    }    
                }
                
                MainMenu.GetInstance().setLabelStocks("EN STOCK MINIMO: "+ articulosSinStock);
                MainMenu.GetInstance().setLabelStocksAnt("POR ACABARSE: "+ articulosPorAcabarse);
                articulosSinStock = 0;
                articulosPorAcabarse = 0;
                    
                localDate = LocalDate.now();
                //daysBetween =  DAYS.between(lastDateCheck, localDate);
                conDB.createAndExecuteQueryVenc();
                while (conDB.RSgetNext()){

                    loteString = "";
                    LocalDate lote1 = conDB.RSgetDate("VENCIMIENTO_LOTE1");
                    LocalDate lote2 = conDB.RSgetDate("VENCIMIENTO_LOTE2");
                    LocalDate lote3 = conDB.RSgetDate("VENCIMIENTO_LOTE3");
                    LocalDate lote4 = conDB.RSgetDate("VENCIMIENTO_LOTE4");
                    LocalDate lote5 = conDB.RSgetDate("VENCIMIENTO_LOTE5");

                    if (lote1 != null && lote1.compareTo(localDate) <= 0){
                        loteString += " - LOTE 1";
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

                MainMenu.GetInstance().setLabelVencidos("VENCIDOS: "+lotesVencidos);
                lotesVencidos = 0;
                conDB.disconnect();
//                System.out.println("Me duermo!");
                Thread.sleep(Preferences.GetInstance().time); 
//                System.out.println("Desperte!");
                
            }
        } 
        
        catch (InterruptedException ex) {
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
    
    
}
