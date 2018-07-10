package cp.companion;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ConfigMenu extends javax.swing.JFrame {

    private static ConfigMenu selfInstance = null;
    private boolean connectionSuccess = false;
    private String encryptedText;
    public static final String configKey = "asSApoS654sl518";
    private ConnectionDB conDB = new ConnectionDB();
    public boolean activeFrame = false;
    private DefaultTableModel modelStocks;
    private DefaultTableModel modelVenc;
    private ImageIcon successIcon = createImageIcon("resources/success.png");
    public static final int DEFAULT_ANTICIPATION = 5;
        
    /**
     * Creates new form ConfiguracionBD
     */
    public ConfigMenu() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE); 
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {          
                onClose();
            }
            
            public void windowIconified(WindowEvent e) {
                MainMenu.GetInstance().setState(ICONIFIED);
             }
            
             public void windowDeiconified(WindowEvent e) {
                 MainMenu.GetInstance().setState(NORMAL);
             }
        });
        ((SpinnerNumberModel) spinnerStock.getModel()).setMinimum(1);
        ((SpinnerNumberModel) spinnerVenc.getModel()).setMinimum(1);
        alignCellsToCenter(tableArticlesStock,0);
        alignCellsToCenter(tableArticlesStock,1);
        alignCellsToCenter(tableArticlesVenc,0);
        alignCellsToCenter(tableArticlesVenc,1);
        tableArticlesStock.getTableHeader().setReorderingAllowed(false);
        tableArticlesVenc.getTableHeader().setReorderingAllowed(false); 
        ((DefaultTableCellRenderer)tableArticlesStock.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((DefaultTableCellRenderer)tableArticlesVenc.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        textIP.setText(Preferences.GetInstance().ip);
        textTCP.setText(Preferences.GetInstance().tcp);
        textNameDB.setText(Preferences.GetInstance().databaseName);
        textUser.setText(Preferences.GetInstance().user);
        textPass.setText(Preferences.GetInstance().password);
        
        modelStocks = (DefaultTableModel) tableArticlesStock.getModel();
        modelVenc = (DefaultTableModel) tableArticlesVenc.getModel();
        
        //Ordena automático clickeando la cabecera de la tabla
        tableArticlesStock.setAutoCreateRowSorter(true);  
        tableArticlesVenc.setAutoCreateRowSorter(true);      
        
        if (MainMenu.GetInstance().DBIsConfiguredAndConnected()){
            conDB.connect();
            conDB.createAndExecuteQueryDptos();
            while (conDB.RSgetNext()){
                comboDepStock.addItem(conDB.RSgetString("NUMDPTO"));
                comboDepVenc.addItem(conDB.RSgetString("NUMDPTO"));
            }
            conDB.disconnect();

            loadTableStocks();
            loadTableVencs();    

            createDefaultPreferences();
        }
    }


    
    public void createDefaultPreferences (){
        try {
            File f = new File("./anticipation.properties");
            Properties props = new Properties();
            if (!f.exists()){    
                conDB.connect();
                conDB.createAndExecuteQueryStocks();
                while (conDB.RSgetNext()){
                    props.setProperty("STOCK_"+conDB.RSgetString("CODARTICULO")+"_"+conDB.RSgetString("CODALMACEN"), Integer.toString(DEFAULT_ANTICIPATION));
                }
                conDB.createAndExecuteQueryVenc();
                while (conDB.RSgetNext()){
                    props.setProperty("VENC_"+conDB.RSgetString("CODARTICULO"), Integer.toString(DEFAULT_ANTICIPATION));
                }
                OutputStream out = new FileOutputStream(f);
                props.store(out, "ANTICIPATION PROPERTIES");
                out.close();  
                conDB.disconnect();
            }   
        }catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigMenu.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfigMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ConfigMenu.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    private void loadTableStocks(){
        while (modelStocks.getRowCount() != 0){
            modelStocks.removeRow(0);
        }
        if (comboDepStock.getSelectedItem().toString().compareTo("<<TODOS LOS DEPARTAMENTOS>>") == 0 )
        {
            conDB.connect();
            conDB.createAndExecuteQueryStocks();
            while (conDB.RSgetNext()){
                modelStocks.addRow(new Object[]{conDB.RSgetString("CODARTICULO"), conDB.RSgetString("CODALMACEN"), conDB.RSgetString("DESCRIPCION")});
            }
            conDB.disconnect();
        }
        else{
            conDB.connect();
            conDB.createAndExecuteQueryStocksOfDepartment(comboDepStock.getSelectedItem().toString());
            while (conDB.RSgetNext()){
                modelStocks.addRow(new Object[]{conDB.RSgetString("CODARTICULO"), conDB.RSgetString("CODALMACEN"), conDB.RSgetString("DESCRIPCION")});
            }
            conDB.disconnect();
        }
        //********************************************************************
        //Ordenamiento de tablita
        TableRowSorter<TableModel> sorterS = new TableRowSorter<>(modelStocks);
        tableArticlesStock.setRowSorter(sorterS);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorterS.setSortKeys(sortKeys);
        sorterS.sort();
        //********************************************************************
    }
    
    
    private void loadTableVencs(){
        while (modelVenc.getRowCount() != 0){
            modelVenc.removeRow(0);
        }
        if (comboDepVenc.getSelectedItem().toString().compareTo("<<TODOS LOS DEPARTAMENTOS>>") == 0 )
        {
            conDB.connect();
            conDB.createAndExecuteQueryVenc();
            while (conDB.RSgetNext()){
                modelVenc.addRow(new Object[]{conDB.RSgetString("CODARTICULO"), conDB.RSgetString("DESCRIPCION")});
            }
            conDB.disconnect();
        }
         else{
            conDB.connect();
            conDB.createAndExecuteQueryVencOfDepartment(comboDepVenc.getSelectedItem().toString());
            while (conDB.RSgetNext()){
                modelVenc.addRow(new Object[]{conDB.RSgetString("CODARTICULO"), conDB.RSgetString("DESCRIPCION")});
            }
            conDB.disconnect();
        }       
        //********************************************************************
        //Ordenamiento de tablita
        TableRowSorter<TableModel> sorterV = new TableRowSorter<>(modelVenc);
        tableArticlesVenc.setRowSorter(sorterV);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorterV.setSortKeys(sortKeys);
        sorterV.sort();
        //********************************************************************
    }
    
    public void onClose(){
        activeFrame = false;
        MainMenu.GetInstance().activeFrame = true;
        MainMenu.GetInstance().setEnabled(true);
        this.setVisible(false);
        labelResult.setText("");
        labelResult.setOpaque(false);   
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnConnect = new javax.swing.JButton();
        textPass = new javax.swing.JPasswordField();
        textUser = new javax.swing.JTextField();
        textNameDB = new javax.swing.JTextField();
        checkInstance = new javax.swing.JCheckBox();
        textTCP = new javax.swing.JTextField();
        textIP = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelResult = new javax.swing.JLabel();
        btnCheckFields = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        spinnerStock = new javax.swing.JSpinner();
        btnAssignStock = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableArticlesStock = new javax.swing.JTable();
        checkSelectAllStock = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableArticlesVenc = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        spinnerVenc = new javax.swing.JSpinner();
        btnAssignVenc = new javax.swing.JButton();
        checkSelectAllVenc = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        comboDepStock = new javax.swing.JComboBox<>();
        comboDepVenc = new javax.swing.JComboBox<>();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Configuración");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "BASE DE DATOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 16))); // NOI18N

        btnConnect.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConnect.setText("Test Conexión");
        btnConnect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        textPass.setFont(new java.awt.Font("Consolas", 0, 15)); // NOI18N

        textUser.setFont(new java.awt.Font("Consolas", 0, 15)); // NOI18N

        textNameDB.setFont(new java.awt.Font("Consolas", 0, 15)); // NOI18N

        checkInstance.setText("Con Instancia (\\\\SQLEXPRESS)");
        checkInstance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkInstanceActionPerformed(evt);
            }
        });

        textTCP.setFont(new java.awt.Font("Consolas", 0, 15)); // NOI18N
        textTCP.setText("1433");
        textTCP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textTCPActionPerformed(evt);
            }
        });

        textIP.setFont(new java.awt.Font("Consolas", 0, 15)); // NOI18N
        textIP.setText("localhost");
        textIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textIPActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Dirección IP:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Puerto TCP:");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Base de Datos:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Usuario:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Contraseña:");

        labelResult.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        labelResult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        btnCheckFields.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCheckFields.setText("Agregar campos libres");
        btnCheckFields.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCheckFields.setEnabled(false);
        btnCheckFields.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckFieldsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCheckFields, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkInstance)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(labelResult, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnConnect))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jLabel4))
                        .addGap(40, 40, 40)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(textNameDB)
                                .addComponent(textUser)
                                .addComponent(textIP)
                                .addComponent(textPass, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(textTCP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(30, 30, 30))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textIP)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textTCP)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addComponent(checkInstance)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(textNameDB, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textUser, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textPass, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelResult, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(btnConnect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(btnCheckFields, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PREFERENCIAS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 16))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel6.setText("Anticipación alerta stock");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Unidades por encima del mínimo:");

        spinnerStock.setFont(new java.awt.Font("Consolas", 0, 15)); // NOI18N
        spinnerStock.setValue(1);

        btnAssignStock.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAssignStock.setText("Asignar");
        btnAssignStock.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAssignStock.setMaximumSize(new java.awt.Dimension(77, 32));
        btnAssignStock.setMinimumSize(new java.awt.Dimension(77, 32));
        btnAssignStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignStockActionPerformed(evt);
            }
        });

        tableArticlesStock.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tableArticlesStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Artículo", "Almacen", "Descripción", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableArticlesStock.setRowHeight(20);
        jScrollPane2.setViewportView(tableArticlesStock);

        checkSelectAllStock.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        checkSelectAllStock.setText("Seleccionar todos");
        checkSelectAllStock.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        checkSelectAllStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkSelectAllStockActionPerformed(evt);
            }
        });

        tableArticlesVenc.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tableArticlesVenc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Artículo", "Descripción", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableArticlesVenc.setRowHeight(20);
        jScrollPane3.setViewportView(tableArticlesVenc);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Días antes del vencimiento:");

        spinnerVenc.setFont(new java.awt.Font("Consolas", 0, 15)); // NOI18N
        spinnerVenc.setValue(1);

        btnAssignVenc.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAssignVenc.setText("Asignar");
        btnAssignVenc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAssignVenc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignVencActionPerformed(evt);
            }
        });

        checkSelectAllVenc.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        checkSelectAllVenc.setText("Seleccionar todos");
        checkSelectAllVenc.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        checkSelectAllVenc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkSelectAllVencActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel7.setText("Anticipación alerta vencimiento");

        comboDepStock.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<<TODOS LOS DEPARTAMENTOS>>" }));
        comboDepStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboDepStockActionPerformed(evt);
            }
        });

        comboDepVenc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<<TODOS LOS DEPARTAMENTOS>>" }));
        comboDepVenc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboDepVencActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(comboDepStock, javax.swing.GroupLayout.Alignment.LEADING, 0, 259, Short.MAX_VALUE)
                                .addComponent(checkSelectAllStock)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGap(40, 40, 40)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(spinnerStock)
                                .addComponent(btnAssignStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(comboDepVenc, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(checkSelectAllVenc))
                            .addGap(40, 40, 40)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(spinnerVenc)
                                .addComponent(btnAssignVenc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))))
                    .addComponent(jLabel7))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboDepStock, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAssignStock, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)))
                .addComponent(checkSelectAllStock)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboDepVenc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkSelectAllVenc)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerVenc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAssignVenc, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(105, 105, 105))))
        );

        checkSelectAllStock.getAccessibleContext().setAccessibleDescription("");

        btnSave.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnSave.setText("GUARDAR ");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnCancel.setText("CANCELAR");
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 59, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        if (conDB.testConnection((textIP.getText()+":"+textTCP.getText()), textNameDB.getText(), textUser.getText(), String.valueOf(textPass.getPassword()), checkInstance.isSelected())) {    
            labelResult.setOpaque(true);
            labelResult.setBackground(Color.green);
            labelResult.setText("CONEXIÓN EXITOSA!");
            connectionSuccess = true;

        } else {
            labelResult.setOpaque(true);
            labelResult.setBackground(Color.red);
            labelResult.setText("CONEXIÓN FALLIDA");
            connectionSuccess = false;
        }
    }//GEN-LAST:event_btnConnectActionPerformed

    private void checkInstanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkInstanceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkInstanceActionPerformed

    private void textIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textIPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textIPActionPerformed

    private void textTCPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textTCPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textTCPActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        onClose();
    }//GEN-LAST:event_btnCancelActionPerformed

    
    public void controlCheckBtn(){
        btnCheckFields.setEnabled(false);
        if (Preferences.GetInstance().DBConnected && conDB.testConnectionSavedPrefs()){ 
            btnCheckFields.setEnabled(true);
        }     
    }
    
    
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        
        Preferences.GetInstance().databaseName = textNameDB.getText();
        Preferences.GetInstance().user = textUser.getText();
        Preferences.GetInstance().password = String.valueOf(textPass.getPassword());
        Preferences.GetInstance().ip = textIP.getText();
        Preferences.GetInstance().tcp = textTCP.getText();
        Preferences.GetInstance().instance = checkInstance.isSelected();       
        
        if (conDB.testConnectionSavedPrefs()){
            Preferences.GetInstance().DBconfigured = true;
            MainMenu.GetInstance().getLabelCon().setText("CONECTADO");
            MainMenu.GetInstance().getLabelCon().setOpaque(true);
            MainMenu.GetInstance().getLabelCon().setBackground(Color.green);  
            Preferences.GetInstance().DBConnected = true;
            MainMenu.GetInstance().refreshTables();
            if (MainMenu.GetInstance().daemon == null)
                MainMenu.GetInstance().daemon = new Thread(new Daemon(), "Hilo daemon");                  
            if (!MainMenu.GetInstance().daemon.isAlive())
                MainMenu.GetInstance().daemon.start();         
        }
        else{
            if (MainMenu.GetInstance().daemon != null && MainMenu.GetInstance().daemon.isAlive()){
                MainMenu.GetInstance().getLabelCon().setText("DESCONECTADO");
                MainMenu.GetInstance().getLabelCon().setOpaque(true);
                MainMenu.GetInstance().getLabelCon().setBackground(Color.red);   
                Preferences.GetInstance().DBConnected = false;
                MainMenu.GetInstance().cleanTables();
                try{
                    MainMenu.GetInstance().daemon.interrupt();
                    MainMenu.GetInstance().daemon = null;
                }catch (IllegalThreadStateException ex){
                    ex.printStackTrace();
                }
            }
            Preferences.GetInstance().DBconfigured = false;
        }
        
        try {
            String myKey = TextEncryptor.encrypt(TextEncryptor.SECRET_KEY, configKey);
            encryptedText = TextEncryptor.encrypt(Preferences.GetInstance().password, myKey);
    
        }catch (Exception ex) {
            Logger.getLogger(ConfigMenu.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {            
            Properties props = new Properties();
            props.setProperty("IP", Preferences.GetInstance().ip);
            props.setProperty("TCP", Preferences.GetInstance().tcp);
            props.setProperty("Database", Preferences.GetInstance().databaseName);
            props.setProperty("User", Preferences.GetInstance().user);
            props.setProperty("Password", encryptedText);
            props.setProperty("Instance", String.valueOf(Preferences.GetInstance().instance));
            props.setProperty("DBconfigured", String.valueOf(Preferences.GetInstance().DBconfigured));
            File f = new File("./config.properties");
            OutputStream out = new FileOutputStream(f);
            props.store(out, "USER PROPERTIES");
        }
        catch (Exception e ) {
            e.printStackTrace();
        }
        
        onClose();
    }//GEN-LAST:event_btnSaveActionPerformed

    
    public void checkDBFields(){
        if (Preferences.GetInstance().DBConnected && conDB.testConnectionSavedPrefs()){         
            int counter = 5;
            ArrayList<String> array = new ArrayList<String>();
            conDB.connect();
            conDB.createAndExecuteQueryFields();
            while (conDB.RSgetNext()) {
                array.add(conDB.RSgetString("COLUMN_NAME"));
                counter-=1;
            }
            if (counter > 0){
                int n = JOptionPane.showConfirmDialog(
                    this, "No se han encontrado los campos libres de vencimientos\n"
                            + " en la base de datos, ¿desea crearlos?",
                    "  CAMPOS FALTANTES", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    try{
                        if (!array.contains("VENCIMIENTO_LOTE1")){
                            conDB.createAndExecuteFieldInserts(1);
                        }
                        if (!array.contains("VENCIMIENTO_LOTE2")){
                            conDB.createAndExecuteFieldInserts(2);
                        }
                        if (!array.contains("VENCIMIENTO_LOTE3")){
                            conDB.createAndExecuteFieldInserts(3);
                        }
                        if (!array.contains("VENCIMIENTO_LOTE4")){
                            conDB.createAndExecuteFieldInserts(4);
                        }
                        if (!array.contains("VENCIMIENTO_LOTE5")){
                            conDB.createAndExecuteFieldInserts(5);
                        }
                        JOptionPane.showMessageDialog(this,
                                    "Campos creados con éxito!",
                                    "  Información",
                                    JOptionPane.INFORMATION_MESSAGE,
                                    successIcon);
                    }catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                                    "Error de conexión al crear campos",
                                    "  Error",
                                    JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }                 
                }
            }
            else{
               JOptionPane.showMessageDialog(this,
                                    "Los campos ya se encuentran creados en la base de datos",
                                    "  Información",
                                    JOptionPane.INFORMATION_MESSAGE); 
            }
            conDB.disconnect();
        }
    }
    
    
    private void alignCellsToCenter(JTable table, int column) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(column).setCellRenderer(centerRenderer);
    }
    
    private void btnAssignStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignStockActionPerformed
        try {
            File f = new File("./anticipation.properties");
            Properties props = new Properties();
            if (f.exists() && !f.isDirectory()){    
                FileInputStream in = new FileInputStream(f);
                props.load(in);
                in.close();
            }   
            
            boolean flag = false;
            for (int i = 0; i < tableArticlesStock.getRowCount() ; i++) {
                if (tableArticlesStock.getValueAt(i, 3) != null && (boolean)tableArticlesStock.getValueAt(i, 3)){
                    flag = true;
                    props.setProperty("STOCK_"+tableArticlesStock.getValueAt(i, 0).toString()+"_"+tableArticlesStock.getValueAt(i, 1).toString(), spinnerStock.getValue().toString());
                }
            }
            if (flag){
                OutputStream out = new FileOutputStream(f);
                props.store(out, "ANTICIPATION PROPERTIES");
                out.close();
                JOptionPane.showMessageDialog(this,
                                            "Las preferencias fueron correctamente actualizadas !",
                                            " Exito",
                                            JOptionPane.INFORMATION_MESSAGE,
                                            successIcon);
            }
            else{
                JOptionPane.showMessageDialog(this,
                                            "No se seleccionó ningún artículo !",
                                            " Aviso",
                                            JOptionPane.WARNING_MESSAGE);
            }
            

        }catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigMenu.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfigMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_btnAssignStockActionPerformed

    private void btnAssignVencActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignVencActionPerformed
        try {
            File f = new File("./anticipation.properties");
            Properties props = new Properties();
            if (f.exists() && !f.isDirectory()){    
                FileInputStream in = new FileInputStream(f);
                props.load(in);
                in.close();
            }   
            
            boolean flag = false;
            for (int i = 0; i < tableArticlesVenc.getRowCount() ; i++) {
                if (tableArticlesVenc.getValueAt(i, 2) != null && (boolean)tableArticlesVenc.getValueAt(i, 2)){
                    flag = true;
                    props.setProperty("VENC_"+tableArticlesVenc.getValueAt(i, 0).toString(), spinnerVenc.getValue().toString());
                }
            }
            if (flag){
                JOptionPane.showMessageDialog(this,
                                            "Las preferencias fueron correctamente actualizadas !",
                                            " Exito",
                                            JOptionPane.INFORMATION_MESSAGE,
                                            successIcon);
                OutputStream out = new FileOutputStream(f);
                props.store(out, "ANTICIPATION PROPERTIES");
                out.close();
            }
            else{
                JOptionPane.showMessageDialog(this,
                                            "No se seleccionó ningún artículo !",
                                            " Aviso",
                                            JOptionPane.WARNING_MESSAGE);
            }
            

        }catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigMenu.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfigMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAssignVencActionPerformed

    private void checkSelectAllVencActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkSelectAllVencActionPerformed
        if (checkSelectAllVenc.isSelected()){
            for (int i = 0; i < tableArticlesVenc.getRowCount() ; i++) {
                tableArticlesVenc.setValueAt(true, i, 2);
            }
        }
        else{
            for (int i = 0; i < tableArticlesVenc.getRowCount() ; i++) {
                tableArticlesVenc.setValueAt(false, i, 2);
            }
        }
    }//GEN-LAST:event_checkSelectAllVencActionPerformed

    private void checkSelectAllStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkSelectAllStockActionPerformed
        if (checkSelectAllStock.isSelected()){
            for (int i = 0; i < tableArticlesStock.getRowCount() ; i++) {
                tableArticlesStock.setValueAt(true, i, 2);
            }
        }
        else{
            for (int i = 0; i < tableArticlesStock.getRowCount() ; i++) {
                tableArticlesStock.setValueAt(false, i, 2);
            }
        }
    }//GEN-LAST:event_checkSelectAllStockActionPerformed

    private void btnCheckFieldsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckFieldsActionPerformed
        checkDBFields();
    }//GEN-LAST:event_btnCheckFieldsActionPerformed

    private void comboDepStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboDepStockActionPerformed
        loadTableStocks();      
    }//GEN-LAST:event_comboDepStockActionPerformed

    private void comboDepVencActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboDepVencActionPerformed
        loadTableVencs();
    }//GEN-LAST:event_comboDepVencActionPerformed

    /**
     * @param args the command line arguments
     */
    public static ConfigMenu GetInstance() {
        if (selfInstance == null) {
            selfInstance = new ConfigMenu();
        }
        return selfInstance;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAssignStock;
    private javax.swing.JButton btnAssignVenc;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCheckFields;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox checkInstance;
    private javax.swing.JCheckBox checkSelectAllStock;
    private javax.swing.JCheckBox checkSelectAllVenc;
    private javax.swing.JComboBox<String> comboDepStock;
    private javax.swing.JComboBox<String> comboDepVenc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelResult;
    private javax.swing.JSpinner spinnerStock;
    private javax.swing.JSpinner spinnerVenc;
    private javax.swing.JTable tableArticlesStock;
    private javax.swing.JTable tableArticlesVenc;
    private javax.swing.JTextField textIP;
    private javax.swing.JTextField textNameDB;
    private javax.swing.JPasswordField textPass;
    private javax.swing.JTextField textTCP;
    private javax.swing.JTextField textUser;
    // End of variables declaration//GEN-END:variables
}
