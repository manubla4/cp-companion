/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.companion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author manub
 */
public class MainMenu extends javax.swing.JFrame {


    private static MainMenu selfInstance = null;
    public Thread daemon = null;    
    private Properties prop = new Properties();
    private InputStream input = null;
    private File file;
    private ConnectionDB conDB = new ConnectionDB();
    private DefaultTableModel modelStocks;
    private DefaultTableModel modelVenc;
    private boolean configLoaded = false;
    
    /**
     * Creates new form MainMenu
     */
    public MainMenu() {
        initComponents();        
        this.setLocationRelativeTo(null);
        
        tableStocks.getTableHeader().setReorderingAllowed(false);
        tableVencimientos.getTableHeader().setReorderingAllowed(false);
        alignCellsToCenter(tableStocks);
        alignCellsToCenter(tableVencimientos);
        ((DefaultTableCellRenderer)tableStocks.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((DefaultTableCellRenderer)tableVencimientos.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        tableStocks.getColumnModel().getColumn(0).setPreferredWidth(10);
        tableStocks.getColumnModel().getColumn(3).setPreferredWidth(10);
        tableStocks.getColumnModel().getColumn(4).setPreferredWidth(10);
        tableStocks.getColumnModel().getColumn(5).setPreferredWidth(10);
        tableStocks.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 15));
        tableVencimientos.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 15));
        
        URL url = this.getClass().getResource("resources/logo.png");  
        ImageIcon icon = new ImageIcon(url);  
        Image img = icon.getImage();
        Image img2 = img.getScaledInstance(490, 98,  java.awt.Image.SCALE_SMOOTH);
        ImageIcon icon2 = new ImageIcon(img2);
        labelLogo.setIcon(icon2);    
        
        labelConnection.setText("DESCONECTADO");
        labelConnection.setOpaque(true);
        labelConnection.setBackground(Color.red);   
      
        modelStocks = (DefaultTableModel) tableStocks.getModel();
        modelVenc = (DefaultTableModel) tableVencimientos.getModel();
        
        file = new File("config.properties");
        if(file.exists() && !file.isDirectory()){
            
            try{
                input = new FileInputStream("config.properties");
                prop.load(input);   
                     
                if (checkConfigFile()){
                    String myKey = TextEncryptor.encrypt(TextEncryptor.SECRET_KEY, ConfigMenu.configKey);
                    String decryptedPassword = TextEncryptor.decrypt(prop.getProperty("Password"), myKey); 
                    Preferences.GetInstance().ip = prop.getProperty("IP");
                    Preferences.GetInstance().tcp = prop.getProperty("TCP");
                    Preferences.GetInstance().databaseName = prop.getProperty("Database");
                    Preferences.GetInstance().user = prop.getProperty("User");
                    Preferences.GetInstance().password = decryptedPassword;
                    Preferences.GetInstance().instance = Boolean.valueOf(prop.getProperty("Instance"));
                    Preferences.GetInstance().DBconfigured = Boolean.valueOf(prop.getProperty("DBconfigured"));
                    if (Preferences.GetInstance().ip.compareTo("") == 0)
                        Preferences.GetInstance().ip = "localhost";
                    if (Preferences.GetInstance().tcp.compareTo("") == 0)
                        Preferences.GetInstance().tcp = "1433";
                    configLoaded = true;
                    
                    if (Preferences.GetInstance().DBconfigured && conDB.testConnectionSavedPrefs()){ 
                        labelConnection.setText("CONECTADO");
                        labelConnection.setOpaque(true);
                        labelConnection.setBackground(Color.green);  
                        Preferences.GetInstance().DBConnected = true;
                        refreshTables();
                        int counter = 5;
//                        conDB.connect();
//                        conDB.createAndExecuteQueryFields();
//                        while (conDB.RSgetNext()) {counter-=1;}
//                        System.out.println(counter);
//                        if (counter > 0){
//                            //solicitar creacion de campos libres
//                        }
//                        conDB.disconnect();
                        if (daemon == null)
                            daemon = new Thread(new Daemon(), "Hilo daemon");                  
                        if (!daemon.isAlive())
                            daemon.start();    
                    }

                }
                else {
                    configLoaded = false;
                }
                
            }catch (IOException ex){
                ex.printStackTrace();
            }catch (Exception ex){
                ex.printStackTrace();
            }finally{
                if (input != null){
                    try {
                        input.close();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }  
        }
                      
    }

    public JLabel getLabelCon() {return labelConnection;}
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnConfig = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableStocks = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableVencimientos = new javax.swing.JTable();
        labelLogo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        labelConnection = new javax.swing.JLabel();
        btnConnect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CP Companion");
        setResizable(false);

        btnConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cp/companion/resources/config.png"))); // NOI18N
        btnConfig.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfigActionPerformed(evt);
            }
        });

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N

        tableStocks.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        tableStocks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Artículo", "Descripción", "Almacen", "Stock", "Mínimo", "Máximo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableStocks.setRowHeight(25);
        jScrollPane1.setViewportView(tableStocks);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel3.setText("POR ENCIMA DEL MAXIMO: 0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 717, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel3)
                .addGap(25, 25, 25)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Stocks", jPanel1);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("VENCIDOS: 0");

        tableVencimientos.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        tableVencimientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Artículo", "Descripción", "Lote 1", "Lote 2", "Lote 3", "Lote 4", "Lote 5"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableVencimientos.setRowHeight(25);
        jScrollPane2.setViewportView(tableVencimientos);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 717, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel4)
                .addGap(25, 25, 25)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Vencimientos", jPanel2);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("v 1.0");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Manuel Blanco");

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cp/companion/resources/refresh.png"))); // NOI18N
        btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        labelConnection.setFont(new java.awt.Font("Tahoma", 3, 15)); // NOI18N
        labelConnection.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelConnection.setText("CONECTADO");

        btnConnect.setText("Conectar / Desconectar");
        btnConnect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 786, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(labelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 595, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnConnect, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelConnection, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(33, Short.MAX_VALUE)
                        .addComponent(labelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnConfig, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                            .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(labelConnection, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void alignCellsToCenter(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }     
    }
   
    
    public void cleanTables(){
        while (modelStocks.getRowCount() != 0){
            modelStocks.removeRow(0);
        }
        while (modelVenc.getRowCount() != 0){
            modelVenc.removeRow(0);
        }
    }
    
    public void refreshTables(){          
        if (Preferences.GetInstance().DBconfigured && Preferences.GetInstance().DBConnected && conDB.testConnectionSavedPrefs()){ 
            cleanTables();   
            conDB.connect();
            conDB.createAndExecuteQueryStocks();
            while (conDB.RSgetNext()){
                modelStocks.addRow(new Object[]{conDB.RSgetString("CODARTICULO"), conDB.RSgetString("DESCRIPCION"), conDB.RSgetString("NOMBREALMACEN"), conDB.RSgetInt("STOCK"), conDB.RSgetInt("MINIMO"), conDB.RSgetInt("MAXIMO")});           
            }
//            conDB.createAndExecuteQueryVenc();
//            while (conDB.RSgetNext()){
//                modelVenc.addRow(new Object[]{conDB.RSgetString("CODARTICULO"), conDB.RSgetString("DESCRIPCION"), conDB.RSgetString("VENCIMIENTO")});           
//            }
            conDB.disconnect();
        }                  
    }
    
    private void btnConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfigActionPerformed
        this.setEnabled(false);
        ConfigMenu.GetInstance().setLocationRelativeTo(null);
        ConfigMenu.GetInstance().setVisible(true);
    }//GEN-LAST:event_btnConfigActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        refreshTables();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        if (Preferences.GetInstance().DBconfigured && conDB.testConnectionSavedPrefs()){
            if (Preferences.GetInstance().DBConnected){
                labelConnection.setText("DESCONECTADO");
                labelConnection.setOpaque(true);
                labelConnection.setBackground(Color.red);
                Preferences.GetInstance().DBConnected = false;
                cleanTables();
                daemon.interrupt();
                daemon = null;
            }
            else{
                labelConnection.setText("CONECTADO");
                labelConnection.setOpaque(true);
                labelConnection.setBackground(Color.green);
                Preferences.GetInstance().DBConnected = true;
                refreshTables();
                if (daemon == null)
                    daemon = new Thread(new Daemon(), "Hilo daemon");
                if (!daemon.isAlive())
                    daemon.start();
            }
        }
    }//GEN-LAST:event_btnConnectActionPerformed
    
    static MainMenu GetInstance(){
        if (selfInstance == null){
            selfInstance = new MainMenu();
        }
        return selfInstance;
    }
    
    private boolean checkConfigFile(){
        if (prop.getProperty("IP") != null && prop.getProperty("TCP") != null && prop.getProperty("Database") != null && prop.getProperty("User") != null && prop.getProperty("Password") != null){
            if ((prop.getProperty("Instance") != null && (prop.getProperty("Instance").compareTo("true") == 0 || prop.getProperty("Instance").compareTo("false") == 0)) && (prop.getProperty("DBconfigured") != null && (prop.getProperty("DBconfigured").compareTo("true") == 0 || prop.getProperty("DBconfigured").compareTo("false") == 0))){
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainMenu.GetInstance().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfig;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelConnection;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JTable tableStocks;
    private javax.swing.JTable tableVencimientos;
    // End of variables declaration//GEN-END:variables
}
