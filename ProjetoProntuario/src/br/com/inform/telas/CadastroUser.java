package br.com.inform.telas;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Raimundo
 */
public class CadastroUser extends javax.swing.JDialog {

    java.sql.Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public CadastroUser(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setarIcon();
        
        conexao = br.com.inform.dal.ModuloConexao.conector();
        pesquisar_registro();
        btnDeleteUsu.setEnabled(false);
        btnEditarUsu.setEnabled(false);
    }

    
    public void setarIcon() {
        URL caminhoIcone = getClass().getResource("/br/com/inform/icon/iconSis.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(caminhoIcone);
        this.setIconImage(iconeTitulo);
    }
    
      private void limparCampos() {
        txtId.setText(null);
        txtNomeUsu.setText(null);
        txtSenhaUso.setText(null);
        txtUsuarioLog.setText(null);
        cboPerfil.setSelectedIndex(0);

    }
      
      private void adicionar() {

        String sql = "insert into tbusuarios(usuario,login,senha,perfil)value(?,?,?,?)";
        String senha = txtSenhaUso.getText();

        try {
//criptografia da senha
          

            //String senhaHex = sb.toString();

            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtNomeUsu.getText());
            pst.setString(2, txtUsuarioLog.getText());
            pst.setString(3, senha);
            pst.setString(4, cboPerfil.getSelectedItem().toString());

            if (cboPerfil.getSelectedItem().equals("Não informado")) {

                JOptionPane.showMessageDialog(null, "Selecione o perfil");
            } else {

                // a linha abaixo atualiza a tabela usuario com os dados do formualario
                int adicionado = pst.executeUpdate();

                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário salvo com sucesso");
                    limparCampos();
                }

            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Verifique seu cadastro não pode haver dois ou mais usuario com o mesmo nome!");
            // JOptionPane.showMessageDialog(null, ex);
        }
    }

    //==============================================================================
    public void pesquisar_registro() {

        //pesquisa avançada clicar na caixa de texto
        String sql = "select iduser as id, usuario as Usuario, login as Login, perfil as Perfil from tbusuarios where usuario like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisaUsu.getText() + "%");
            rs = pst.executeQuery();
            tblResultUsu.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    //=============================================================================
    private void setacampos() {

        int setar = tblResultUsu.getSelectedRow();

        txtId.setText(tblResultUsu.getModel().getValueAt(setar, 0).toString());
        txtNomeUsu.setText(tblResultUsu.getModel().getValueAt(setar, 1).toString());
        txtUsuarioLog.setText(tblResultUsu.getModel().getValueAt(setar, 2).toString());
        //txtSenhaUso.setText(tblResultUsu.getModel().getValueAt(setar, 3).toString());
        cboPerfil.setSelectedItem(tblResultUsu.getModel().getValueAt(setar, 3).toString());

    }
    
     private void update() {

        String sql = "update tbusuarios set usuario=?,login=?,senha=?,perfil=? where iduser=?";
        String senha = txtSenhaUso.getText();
        try {

           
            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtNomeUsu.getText());
            pst.setString(2, txtUsuarioLog.getText());
            pst.setString(3, senha);
            pst.setString(4, cboPerfil.getSelectedItem().toString());
            pst.setString(5, txtId.getText());
            if (cboPerfil.getSelectedItem().equals("Não informado")) {

                JOptionPane.showMessageDialog(null, "Selecione o perfil");
            } else {

                // a linha abaixo atualiza a tabela usuario com os dados do formualario
                int adicionado = pst.executeUpdate();
                System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário alterado com sucesso");
                }

                limparCampos();
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, e);
        }
    }
     
      private void excluir() {

        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover esse usuário ?", "Atençao", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbusuarios where iduser=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtId.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário removido com sucesso");
                } else {
                    JOptionPane.showMessageDialog(null, "Usuário não removido");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtPesquisaUsu = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtNomeUsu = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtUsuarioLog = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cboPerfil = new javax.swing.JComboBox<>();
        txtSenhaUso = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblResultUsu = new javax.swing.JTable();
        btnSalavar = new javax.swing.JButton();
        btnEditarUsu = new javax.swing.JButton();
        btnNovoUsu = new javax.swing.JButton();
        btnDeleteUsu = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro Usuário");

        txtPesquisaUsu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisaUsuKeyReleased(evt);
            }
        });

        jLabel1.setText("Busca avançada");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Cadastro Usuário"));

        jLabel2.setText("Id");

        txtId.setEnabled(false);

        jLabel3.setText("Nome:");

        jLabel4.setText("Usuário:");

        jLabel5.setText("Senha:");

        jLabel6.setText("Perfil:");

        cboPerfil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Não informado", "admin", "user" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtSenhaUso, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtUsuarioLog)
                    .addComponent(txtNomeUsu))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNomeUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtUsuarioLog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(cboPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSenhaUso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Usuários cadastrados"));

        tblResultUsu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblResultUsu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblResultUsuMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblResultUsu);

        btnSalavar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnSalavar.setText("Salvar");
        btnSalavar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSalavar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnSalavar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalavarActionPerformed(evt);
            }
        });

        btnEditarUsu.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnEditarUsu.setText("Editar");
        btnEditarUsu.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnEditarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarUsuActionPerformed(evt);
            }
        });

        btnNovoUsu.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnNovoUsu.setText("Novo");
        btnNovoUsu.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnNovoUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoUsuActionPerformed(evt);
            }
        });

        btnDeleteUsu.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnDeleteUsu.setText("Excluir");
        btnDeleteUsu.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnDeleteUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteUsuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnNovoUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSalavar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEditarUsu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDeleteUsu)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNovoUsu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSalavar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnEditarUsu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDeleteUsu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtPesquisaUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(txtPesquisaUsu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(660, 484));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPesquisaUsuKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaUsuKeyReleased
        // TODO add your handling code here:

        pesquisar_registro();
    }//GEN-LAST:event_txtPesquisaUsuKeyReleased

    private void tblResultUsuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblResultUsuMouseClicked
        setacampos();
        btnSalavar.setEnabled(false);
        btnDeleteUsu.setEnabled(true);
        btnEditarUsu.setEnabled(true);
    }//GEN-LAST:event_tblResultUsuMouseClicked

    private void btnSalavarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalavarActionPerformed

        adicionar();
        pesquisar_registro();
    }//GEN-LAST:event_btnSalavarActionPerformed

    private void btnEditarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarUsuActionPerformed
        update();
        pesquisar_registro();
        limparCampos();
        btnSalavar.setEnabled(true);
        btnDeleteUsu.setEnabled(false);
    }//GEN-LAST:event_btnEditarUsuActionPerformed

    private void btnNovoUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoUsuActionPerformed

        limparCampos();
        btnSalavar.setEnabled(true);
        btnDeleteUsu.setEnabled(false);
    }//GEN-LAST:event_btnNovoUsuActionPerformed

    private void btnDeleteUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteUsuActionPerformed
        excluir();
        pesquisar_registro();
        limparCampos();
        btnSalavar.setEnabled(true);
        btnEditarUsu.setEnabled(false);
        btnDeleteUsu.setEnabled(false);
    }//GEN-LAST:event_btnDeleteUsuActionPerformed

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
            java.util.logging.Logger.getLogger(CadastroUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadastroUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadastroUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadastroUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CadastroUser dialog = new CadastroUser(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteUsu;
    private javax.swing.JButton btnEditarUsu;
    private javax.swing.JButton btnNovoUsu;
    private javax.swing.JButton btnSalavar;
    private javax.swing.JComboBox<String> cboPerfil;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblResultUsu;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNomeUsu;
    private javax.swing.JTextField txtPesquisaUsu;
    private javax.swing.JPasswordField txtSenhaUso;
    private javax.swing.JTextField txtUsuarioLog;
    // End of variables declaration//GEN-END:variables
}
