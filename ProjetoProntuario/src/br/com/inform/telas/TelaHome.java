package br.com.inform.telas;

import br.com.inform.auxiliar.Auxiliar;
import br.com.inform.auxiliar.Impressao;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.proteanit.sql.DbUtils;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Raimundo
 */
public class TelaHome extends javax.swing.JFrame {
     private BufferedImage imagem;
    /**
     *
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    public static String perfil;
    public static String usuario;
    //botao imprimirr imagem 

//============================================================================================
    
    
//===========================================================================================    
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public TelaHome() {
      
        
        initComponents();
        conexao = br.com.inform.dal.ModuloConexao.conector();
        setarIcon();
        //exibUsuario.setText(br.com.inform.telas.TelaLogin.usuario);       
        exibPerfil.setText(br.com.inform.telas.TelaLogin.perfil);
        
        String p = exibPerfil.getText();
        if (!p.equals("admin")) {
            MenCadUs.setEnabled(false);
        }
        
   
        
        limparCampos();
        excluirRegistros();
        relatoriododia();
        txtIdR.setVisible(false);
        exibPerfil.setVisible(false);
        //CODIGO ROLAR A TABELA RELATORIO DIA  
        int ultimaLinha = tblResultUsuDia.getRowCount() - 1;
        tblResultUsuDia.scrollRectToVisible(tblResultUsuDia.getCellRect(ultimaLinha, 0, true));

        // Adiciona um ouvinte de eventos ao campo de texto da data de nascimento
        txtDataNasc.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarIdade();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarIdade();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarIdade();
            }
            
        });
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // Adicionando um WindowListener para detectar o evento de fechamento
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    confirmarFechamento();
                } catch (SQLException ex) {
                    Logger.getLogger(TelaHome.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Método para exibir um diálogo de confirmação ao fechar a janela
    private void confirmarFechamento() throws SQLException {
        
        sair();

        // Se o usuário escolher "Não" ou fechar o diálogo, a janela permanece aberta
    }
    
    public void setarIcon() {
        URL caminhoIcone = getClass().getResource("/br/com/inform/icon/iconSis.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(caminhoIcone);
        this.setIconImage(iconeTitulo);
    }
    
    private void limparCampos() {
        txtNome.setText(null);
        txtCpf.setText("000.000.000-00");
        txtPesquisar.setText(null);
        txtPesquisarCns.setText(null);
        txtPesquisarCPF.setText(null);
        txtRg.setText(null);
        txtNaturalidade.setText(null);
        cboUfNaturalidade.setSelectedIndex(0);
        cboEstCivil.setSelectedIndex(0);
        cboUfRg.setSelectedIndex(0);
        cboOrgRg.setSelectedIndex(0);
        cboRaCor.setSelectedIndex(0);
        txtDataNasc.setText("00/00/0000");
        
        txtDataNasc.setEnabled(true);
        cboSexo.setSelectedIndex(0);
        dataEmi.setText("00/00/0000");
        txtOcupacao.setText(null);
        txtTelefone.setText("(00)00000-0000");
        txtNomeMae.setText(null);
        txtNomePai.setText(null);
        txtEndereco.setText(null);
        txtNumero.setText(null);
        txtBairro.setText(null);
        txtMunicipio.setText(null);
        txtCadSus.setText("000.0000.0000.0000");
        txtIdade.setText(null);
        txtId.setText(null);
        pesquisar_registro();
        btnEditar.setEnabled(false);
        btnImprimir.setEnabled(true);
        
    }
    
    private void atualizarIdade() {
        try {
            // Obtém a data do campo de texto
            String inputData = txtDataNasc.getText();
            // LocalDate dataNascimento = LocalDate.parse(inputData);
            LocalDate dataNascimento = LocalDate.parse(inputData, formatoData);

            // Calcula a idade
            //int idade = calcularIdade(dataNascimento);
            Period periodo = calcularDiferenca(dataNascimento, LocalDate.now());

            // Atualiza o rótulo com a idade
            //Result.setText("Idade: " + idade + " anos");
            if (periodo.getYears()<2) {
               txtIdade.setText(periodo.getYears() + " ano"); 
            } else {
               txtIdade.setText(periodo.getYears() + " anos");
            }
           //dias
            if (periodo.getDays()<2) {
              txtDias.setText(periodo.getDays() + " dia");  
            } else {
              txtDias.setText(periodo.getDays() + " dias");
            }
            //meses
            if (periodo.toTotalMonths()<2) {
              txtMes.setText(periodo.toTotalMonths() + " mes"); 
            } else {
              txtMes.setText(periodo.toTotalMonths() + " meses");
            }
            
          
        } catch (Exception ex) {
            // Em caso de formato de data inválido, exibe uma mensagem de erro
            txtIdade.setText("inválido");
            txtDias.setText("");
            txtMes.setText("");
        }
    }
    
    private int calcularIdade(LocalDate dataNascimento) {
        // Obtém a data atual
        LocalDate dataAtual = LocalDate.now();

        // Calcula a diferença entre as datas
        Period periodo = Period.between(dataNascimento, dataAtual);

        // Retorna a diferença no campo de anos
        return periodo.getYears();
    }
    
    private Period calcularDiferenca(LocalDate dataNascimento, LocalDate dataAtual) {
        // Calcula a diferença entre as datas
        return Period.between(dataNascimento, dataAtual);
    }
    
    private void adicionar() {
        
        String sql = "insert into tbpacientes(nome,cpf,rg,naturalidade,ufnaturalidade,orgao_rg,uf_rg,dataemissao_rg,data_nascimento,sexo,estado_civil,ocupacao,telefone,nome_mae,nome_pai,endereco,numero,bairro,municipio,cad_sus,cor)value(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        try {
            pst = conexao.prepareStatement(sql);
            
            pst.setString(1, txtNome.getText());
            pst.setString(2, txtCpf.getText());
            pst.setString(3, txtRg.getText());
            pst.setString(4, txtNaturalidade.getText());
            pst.setString(5, cboUfNaturalidade.getSelectedItem().toString());
            pst.setString(6, cboOrgRg.getSelectedItem().toString());
            pst.setString(7, cboUfRg.getSelectedItem().toString());
            pst.setString(8, dataEmi.getText());
            pst.setString(9, txtDataNasc.getText());
            pst.setString(10, cboSexo.getSelectedItem().toString());
            pst.setString(11, cboEstCivil.getSelectedItem().toString());
            pst.setString(12, txtOcupacao.getText());
            pst.setString(13, txtTelefone.getText());
            pst.setString(14, txtNomeMae.getText());
            pst.setString(15, txtNomePai.getText());
            pst.setString(16, txtEndereco.getText());
            pst.setString(17, txtNumero.getText());
            pst.setString(18, txtBairro.getText());
            pst.setString(19, txtMunicipio.getText());
            pst.setString(20, txtCadSus.getText());
            pst.setString(21, cboRaCor.getSelectedItem().toString());
            
            if (txtNome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "O Nome é obrigatório!");
            } else if (txtNaturalidade.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "A naturalidade é importante");
            } else if (cboUfNaturalidade.getSelectedItem().equals("Não Informado")) {
                JOptionPane.showMessageDialog(null, "Selecione uma Sigla de estado no campo UF");
            } else if (txtNomeMae.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "O nome da mãe é obrigatório!");
            } else if (txtEndereco.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o campo endereço!");
            } else if (txtMunicipio.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha o campo municipio!");
            } else {

                //=======================================
                if (txtRg.getText().equals("")) {
                    
                    if (!cboOrgRg.getSelectedItem().equals("Não Informado")) {
                        JOptionPane.showMessageDialog(null, "Para informar o orgão o campo Rg precisa ser preenchido");
                    } else if (!cboUfRg.getSelectedItem().equals("Não informado")) {
                        JOptionPane.showMessageDialog(null, "Para informar a unidade federativa do RG o campo Rg precisa ser preenchido");
                    } else if (!dataEmi.getText().equals("00/00/0000")) {
                        JOptionPane.showMessageDialog(null, "Para informar a data de emissão do RG o campo Rg precisa ser preenchido");
                    } else {
                        //salvar no banco de dados
                        int adicionado = pst.executeUpdate();
                        if (adicionado > 0) {
                            JOptionPane.showMessageDialog(null, "Cadastro efetuado com sucesso");
                            
                            limparCampos();
                            pesquisar_registro();
                        }
                    }
                    
                } else {
                    
                    if (dataEmi.getText().equals("00/00/0000")) {
                        JOptionPane.showMessageDialog(null, "Insira a Data da emissão do Rg");
                    } else if (cboOrgRg.getSelectedItem().equals("Não Informado")) {
                        JOptionPane.showMessageDialog(null, "Selecione o orgão emissor do Rg");
                    } else if (cboUfRg.getSelectedItem().equals("Não informado")) {
                        JOptionPane.showMessageDialog(null, "Selecione a unidade federativa da emissão do Rg");
                    } else {

                        //executa a quelry para salvar no banco de dados
                        int adicionado = pst.executeUpdate();
                        if (adicionado > 0) {
                            JOptionPane.showMessageDialog(null, "Cadastro efetuado com sucesso");
                            limparCampos();
                            pesquisar_registro();
                        }
                    }
                }
                
            }
            
        } catch (HeadlessException | SQLException e) {
            
            JOptionPane.showMessageDialog(null, e);
        }
        
    }
    
    private void adicionarbtnsair() {
        
        String sql = "insert into tbpacientes(nome,cpf,rg,naturalidade,ufnaturalidade,orgao_rg,uf_rg,dataemissao_rg,data_nascimento,sexo,estado_civil,ocupacao,telefone,nome_mae,nome_pai,endereco,numero,bairro,municipio,cad_sus,cor)value(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        try {
            
            int response = JOptionPane.showConfirmDialog(null,
                    "Deseja salvar os dados antes de fechar?",
                    "Confirmação",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                pst = conexao.prepareStatement(sql);
                
                pst.setString(1, txtNome.getText());
                pst.setString(2, txtCpf.getText());
                pst.setString(3, txtRg.getText());
                pst.setString(4, txtNaturalidade.getText());
                pst.setString(5, cboUfNaturalidade.getSelectedItem().toString());
                pst.setString(6, cboOrgRg.getSelectedItem().toString());
                pst.setString(7, cboUfRg.getSelectedItem().toString());
                pst.setString(8, dataEmi.getText());
                pst.setString(9, txtDataNasc.getText());
                pst.setString(10, cboSexo.getSelectedItem().toString());
                pst.setString(11, cboEstCivil.getSelectedItem().toString());
                pst.setString(12, txtOcupacao.getText());
                pst.setString(13, txtTelefone.getText());
                pst.setString(14, txtNomeMae.getText());
                pst.setString(15, txtNomePai.getText());
                pst.setString(16, txtEndereco.getText());
                pst.setString(17, txtNumero.getText());
                pst.setString(18, txtBairro.getText());
                pst.setString(19, txtMunicipio.getText());
                pst.setString(20, txtCadSus.getText());
                pst.setString(21, cboRaCor.getSelectedItem().toString());
                
                if (txtNome.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "O Nome é obrigatório!");
                } else if (txtNaturalidade.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "A naturalidade é importante");
                } else if (cboUfNaturalidade.getSelectedItem().equals("Não Informado")) {
                    JOptionPane.showMessageDialog(null, "Selecione uma Sigla de estado no campo UF");
                } else if (txtNomeMae.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "O nome da mãe é obrigatório!");
                } else if (txtEndereco.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Preencha o campo endereço!");
                } else if (txtMunicipio.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Preencha o campo municipio!");
                } else {

                    //=======================================
                    if (txtRg.getText().equals("")) {
                        
                        if (!cboOrgRg.getSelectedItem().equals("Não Informado")) {
                            JOptionPane.showMessageDialog(null, "Para informar o orgão o campo Rg precisa ser preenchido");
                        } else if (!cboUfRg.getSelectedItem().equals("Não informado")) {
                            JOptionPane.showMessageDialog(null, "Para informar a unidade federativa do RG o campo Rg precisa ser preenchido");
                        } else if (!dataEmi.getText().equals("00/00/0000")) {
                            JOptionPane.showMessageDialog(null, "Para informar a data de emissão do RG o campo Rg precisa ser preenchido");
                        } else {
                            //salvar no banco de dados
                            int adicionado = pst.executeUpdate();
                            if (adicionado > 0) {
                                JOptionPane.showMessageDialog(null, "Cadastro efetuado com sucesso");
                                
                                limparCampos();
                                pesquisar_registro();
                                this.dispose();
                            }
                        }
                        
                    } else {
                        
                        if (dataEmi.getText().equals("00/00/0000")) {
                            JOptionPane.showMessageDialog(null, "Insira a Data da emissão do Rg");
                        } else if (cboOrgRg.getSelectedItem().equals("Não Informado")) {
                            JOptionPane.showMessageDialog(null, "Selecione o orgão emissor do Rg");
                        } else if (cboUfRg.getSelectedItem().equals("Não informado")) {
                            JOptionPane.showMessageDialog(null, "Selecione a unidade federativa da emissão do Rg");
                        } else {

                            //executa a quelry para salvar no banco de dados
                            int adicionado = pst.executeUpdate();
                            if (adicionado > 0) {
                                JOptionPane.showMessageDialog(null, "Cadastro efetuado com sucesso");
                                limparCampos();
                                pesquisar_registro();
                                
                                this.dispose();
                            }
                        }
                    }
                    
                }
                //=====================================

            } else if (response == JOptionPane.NO_OPTION) {
                this.dispose();
                
            }
            
        } catch (HeadlessException | SQLException e) {
            
            JOptionPane.showMessageDialog(null, e);
        }
        
    }
    
    public void pesquisar_registro() {

        //pesquisa avançada clicar na caixa de texto
        String sql = "select nome as Nome,cpf as CPF, rg as Rg,data_nascimento as DN, cad_sus as CadSus, nome_mae as Mãe, nome_pai as Pai, naturalidade as Naturalidade, endereco as End,numero as Nm, municipio as Mun, ocupacao as OC, bairro as Bairro,telefone as Fn,uf_rg as UfRg, ufnaturalidade as UfN, estado_civil as EC, cor as Cor, orgao_rg as Org, dataemissao_rg as Erg, sexo as Sx,idp from tbpacientes where nome like? ORDER BY Nome ASC";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisar.getText() + "%");
            rs = pst.executeQuery();
            tblPacientes.setModel(DbUtils.resultSetToTableModel(rs));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

//ok
    //Medoto pesquisar pelo cartao do sus   
    public void pesquisar_cns() {

        //pesquisa avançada clicar na caixa de texto
        String sql = "select nome as Nome, cpf as CPF,rg as Rg,data_nascimento as DN, cad_sus as CadSus, nome_mae as Mãe, nome_pai as Pai, naturalidade as Naturalidade, endereco as End,numero as Nm, municipio as Mun, ocupacao as OC, bairro as Bairro,telefone as Fn,uf_rg as UfRg, ufnaturalidade as UfN, estado_civil as EC, cor as Cor, orgao_rg as Org, dataemissao_rg as Erg, sexo as Sx,idp from tbpacientes where cad_sus like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisarCns.getText() + "%");
            rs = pst.executeQuery();
            tblPacientes.setModel(DbUtils.resultSetToTableModel(rs));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    public void pesquisar_cpf() {

        //pesquisa avançada clicar na caixa de texto
        String sql = "select nome as Nome, cpf as CPf,rg as Rg,data_nascimento as DN, cad_sus as CadSus, nome_mae as Mãe, nome_pai as Pai, naturalidade as Naturalidade, endereco as End,numero as Nm, municipio as Mun, ocupacao as OC, bairro as Bairro,telefone as Fn,uf_rg as UfRg, ufnaturalidade as UfN, estado_civil as EC, cor as Cor, orgao_rg as Org, dataemissao_rg as Erg, sexo as Sx,idp from tbpacientes where cpf like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisarCPF.getText() + "%");
            rs = pst.executeQuery();
            tblPacientes.setModel(DbUtils.resultSetToTableModel(rs));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void setacampos() {
        
        int setar = tblPacientes.getSelectedRow();
        txtNome.setText(tblPacientes.getModel().getValueAt(setar, 0).toString());
        txtCpf.setText(tblPacientes.getModel().getValueAt(setar, 1).toString());
        txtRg.setText(tblPacientes.getModel().getValueAt(setar, 2).toString());
        txtDataNasc.setText(tblPacientes.getModel().getValueAt(setar, 3).toString());
        txtCadSus.setText(tblPacientes.getModel().getValueAt(setar, 4).toString());
        txtNomeMae.setText(tblPacientes.getModel().getValueAt(setar, 5).toString());
        txtNomePai.setText(tblPacientes.getModel().getValueAt(setar, 6).toString());
        txtNaturalidade.setText(tblPacientes.getModel().getValueAt(setar, 7).toString());
        txtEndereco.setText(tblPacientes.getModel().getValueAt(setar, 8).toString());
        txtNumero.setText(tblPacientes.getModel().getValueAt(setar, 9).toString());
        txtMunicipio.setText(tblPacientes.getModel().getValueAt(setar, 10).toString());
        txtOcupacao.setText(tblPacientes.getModel().getValueAt(setar, 11).toString());
        txtBairro.setText(tblPacientes.getModel().getValueAt(setar, 12).toString());
        txtTelefone.setText(tblPacientes.getModel().getValueAt(setar, 13).toString());
        cboUfRg.setSelectedItem(tblPacientes.getModel().getValueAt(setar, 14).toString());
        cboUfNaturalidade.setSelectedItem(tblPacientes.getModel().getValueAt(setar, 15).toString());
        cboEstCivil.setSelectedItem(tblPacientes.getModel().getValueAt(setar, 16).toString());
        cboRaCor.setSelectedItem(tblPacientes.getModel().getValueAt(setar, 17).toString());
        cboOrgRg.setSelectedItem(tblPacientes.getModel().getValueAt(setar, 18).toString());
        dataEmi.setText(tblPacientes.getModel().getValueAt(setar, 19).toString());
        cboSexo.setSelectedItem(tblPacientes.getModel().getValueAt(setar, 20).toString());
        txtId.setText(tblPacientes.getModel().getValueAt(setar, 21).toString());
        
    }
    
    private void alterar() throws SQLException {
        
        String texto = "Não houve alteração de dados \n";
//        texto += "Não aceitamos dados duplicados \n";
//        texto += "Use a área de busca e pesquise por esse cadastro\n";
//        texto += "Ou revise os dados que estão sendo inseridos!";

        try {

            //=======================================
            String sqll = "select * from tbpacientes where nome = ? and cpf=? and rg=? and naturalidade=? and ufnaturalidade=? and orgao_rg=? and uf_rg=? and dataemissao_rg=? and data_nascimento=? and sexo=? and estado_civil=? and ocupacao=? and telefone=? and nome_mae=? and nome_pai=? and endereco=? and numero=? and bairro=? and municipio=? and cad_sus=? and cor=?";
            
            pst = conexao.prepareStatement(sqll);
            pst.setString(1, txtNome.getText());
            pst.setString(2, txtCpf.getText());
            pst.setString(3, txtRg.getText());
            pst.setString(4, txtNaturalidade.getText());
            pst.setString(5, cboUfNaturalidade.getSelectedItem().toString());
            pst.setString(6, cboOrgRg.getSelectedItem().toString());
            pst.setString(7, cboUfRg.getSelectedItem().toString());
            pst.setString(8, dataEmi.getText());
            pst.setString(9, txtDataNasc.getText());
            pst.setString(10, cboSexo.getSelectedItem().toString());
            pst.setString(11, cboEstCivil.getSelectedItem().toString());
            pst.setString(12, txtOcupacao.getText());
            pst.setString(13, txtTelefone.getText());
            pst.setString(14, txtNomeMae.getText());
            pst.setString(15, txtNomePai.getText());
            pst.setString(16, txtEndereco.getText());
            pst.setString(17, txtNumero.getText());
            pst.setString(18, txtBairro.getText());
            pst.setString(19, txtMunicipio.getText());
            pst.setString(20, txtCadSus.getText());
            pst.setString(21, cboRaCor.getSelectedItem().toString());
            
            rs = pst.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, texto);
                
            } else {
                
                String sql = "update tbpacientes set nome=?,cpf=?,rg=?,naturalidade=?,ufnaturalidade=?,orgao_rg=?,uf_rg=?,dataemissao_rg=?,data_nascimento=?,sexo=?,estado_civil=?,ocupacao=?,telefone=?,nome_mae=?,nome_pai=?,endereco=?,numero=?,bairro=?,municipio=?,cad_sus=?,cor=? where idp=?";
                
                try {
                    
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtNome.getText());
                    pst.setString(2, txtCpf.getText());
                    pst.setString(3, txtRg.getText());
                    pst.setString(4, txtNaturalidade.getText());
                    pst.setString(5, cboUfNaturalidade.getSelectedItem().toString());
                    pst.setString(6, cboOrgRg.getSelectedItem().toString());
                    pst.setString(7, cboUfRg.getSelectedItem().toString());
                    pst.setString(8, dataEmi.getText());
                    pst.setString(9, txtDataNasc.getText());
                    pst.setString(10, cboSexo.getSelectedItem().toString());
                    pst.setString(11, cboEstCivil.getSelectedItem().toString());
                    pst.setString(12, txtOcupacao.getText());
                    pst.setString(13, txtTelefone.getText());
                    pst.setString(14, txtNomeMae.getText());
                    pst.setString(15, txtNomePai.getText());
                    pst.setString(16, txtEndereco.getText());
                    pst.setString(17, txtNumero.getText());
                    pst.setString(18, txtBairro.getText());
                    pst.setString(19, txtMunicipio.getText());
                    pst.setString(20, txtCadSus.getText());
                    pst.setString(21, cboRaCor.getSelectedItem().toString());
                    pst.setString(22, txtId.getText());
                    
                    if (txtNome.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "O Nome é obrigatório!");
                    } else if (txtNaturalidade.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "A naturalidade é importante");
                    } else if (cboUfNaturalidade.getSelectedItem().equals("Não Informado")) {
                        JOptionPane.showMessageDialog(null, "Selecione uma Sigla de estado no campo UF");
                        
                    } else if (txtNomeMae.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "O nome da mãe é obrigatório!");
                    } else if (txtEndereco.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Preencha o campo endereço!");
                    } else if (txtMunicipio.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Preencha o campo municipio!");
                    } else if (cboRaCor.getSelectedItem().equals("Não informado")) {
                        JOptionPane.showMessageDialog(null, "Selecione uma opção em Raça/cor");
                        
                    } else {
                        
                        if (txtRg.getText().equals("")) {
                            
                            if (!cboOrgRg.getSelectedItem().equals("Não Informado")) {
                                cboOrgRg.setSelectedIndex(0);
                                JOptionPane.showMessageDialog(null, "Para informar o orgão o campo Rg precisa ser preenchido");
                                
                            } else if (!cboUfRg.getSelectedItem().equals("Não informado")) {
                                cboUfRg.setSelectedIndex(0);
                                JOptionPane.showMessageDialog(null, "Para informar a unidade federativa do RG o campo Rg precisa ser preenchido");
                            } else if (!dataEmi.getText().equals("00/00/0000")) {
                                dataEmi.setText("00/00/0000");
                                JOptionPane.showMessageDialog(null, "Para informar a data de emissão do RG o campo Rg precisa ser preenchido");
                            } else {
                                //salvar no banco de dados 
                                int adicionado = pst.executeUpdate();
                                if (adicionado > 0) {
                                    JOptionPane.showMessageDialog(null, "Cadastro alterado com sucesso");
                                    limparCampos();
                                    pesquisar_registro();
                                }
                            }
                            
                        } else {
                            
                            if (dataEmi.getText().equals("00/00/0000")) {
                                JOptionPane.showMessageDialog(null, "Data da emissão do Rg é importante já que está sendo definido um numero de RG");
                            } else if (cboOrgRg.getSelectedItem().equals("Não Informado")) {
                                JOptionPane.showMessageDialog(null, "Selecione o orgão emissor do Rg");
                            } else if (cboUfRg.getSelectedItem().equals("Não informado")) {
                                JOptionPane.showMessageDialog(null, "Selecione a unidade federativa da emissão do Rg");
                            } else {
                                //executa a quelry para salvar no banco de dados 
                                int adicionado = pst.executeUpdate();
                                if (adicionado > 0) {
                                    JOptionPane.showMessageDialog(null, "Cadastro alterado com sucesso");
                                    limparCampos();
                                    
                                    pesquisar_registro();
                                }
                            }
                        }
                        
                    }
                    btnSalvar.setEnabled(true);
                } catch (HeadlessException | SQLException e) {
                    
                    JOptionPane.showMessageDialog(null, e);
                }
                //}
            }
            //---------------------------------------
        } catch (SQLException ex) {
            
            Logger.getLogger(TelaHome.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void excluir() {
        
        if (txtId.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Tenha um registro selecionado para poder excluir!");
        } else {
            int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover esse registro", "Atençao", JOptionPane.YES_NO_OPTION);
            if (confirma == JOptionPane.YES_OPTION) {
                String sql = "delete from tbpacientes where idp=?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtId.getText());
                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        JOptionPane.showMessageDialog(null, "Registro removido com sucesso");
                    } else {
                        JOptionPane.showMessageDialog(null, "Registro não removido");
                    }
                    
                } catch (HeadlessException | SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        }
    }
//===============================================================================
//configuração da tabela relatorio dia
    private void relatoriodia() {
        
        String nm = txtNome.getText();
        String dataNascim = txtDataNasc.getText();
        
        String sql = "insert into tbrelatoriodia(nome, dataNascimento)values(?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, nm);
            pst.setString(2, dataNascim);
            pst.executeUpdate();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.println(e);
        }
        
    }
    
    public void relatoriododia() {

        //pesquisa avançada clicar na caixa de texto
        String sql = "select nome as Nome,dataNascimento, data as Data from tbrelatoriodia where nome like?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisaUsuDia.getText() + "%");
            rs = pst.executeQuery();
            tblResultUsuDia.setModel(DbUtils.resultSetToTableModel(rs));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    public void excluirRegistros() {
        
        String sql = "DELETE FROM tbrelatoriodia WHERE data < DATE_SUB(NOW(), INTERVAL 7 DAY)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.executeUpdate(sql);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
    }
    
   

    
    private void setacampostbrelatdia() {
        
        int setar = tblResultUsuDia.getSelectedRow();
        
        txtIdR.setText(tblResultUsuDia.getModel().getValueAt(setar, 0).toString());
        
    }
    
    private void imprimir() {
        
        if (txtNome.getText().isEmpty()) {
                       
            txtDataNasc.setText("");
            txtCadSus.setText("");
            txtCpf.setText("");
            txtTelefone.setText("");
            dataEmi.setText("");
            cboOrgRg.setSelectedIndex(3);
            cboUfNaturalidade.setSelectedIndex(28);
            txtIdade.setText("");
            cboSexo.setSelectedIndex(3);
            cboEstCivil.setSelectedIndex(5);
            cboUfRg.setSelectedIndex(28);
            cboRaCor.setSelectedIndex(7);
            
            Impressao imprime = new Impressao();
            ArrayList listda_de_dados = GetDados();
            imprime.Imprime_Relatorio(listda_de_dados);
            limparCampos();
            
            
        } else {
            Impressao imprime = new Impressao();
            ArrayList listda_de_dados = GetDados();
            imprime.Imprime_Relatorio(listda_de_dados);
            relatoriodia();
            
            
            
        }
        
    }
    
    private void sair() throws SQLException {
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja mesmo Sair", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            verificarasntesdesair();
            //this.dispose();

        } else {
            
        }
        
    }

    //======================================================
    private void verificarasntesdesair() throws SQLException {
        
        try {

            //=======================================
            String sqll = "select * from tbpacientes where nome = ? and cpf=? and rg=? and naturalidade=? and ufnaturalidade=? and orgao_rg=? and uf_rg=? and dataemissao_rg=? and data_nascimento=? and sexo=? and estado_civil=? and ocupacao=? and telefone=? and nome_mae=? and nome_pai=? and endereco=? and numero=? and bairro=? and municipio=? and cad_sus=? and cor=?";
            
            pst = conexao.prepareStatement(sqll);
            pst.setString(1, txtNome.getText());
            pst.setString(2, txtCpf.getText());
            pst.setString(3, txtRg.getText());
            pst.setString(4, txtNaturalidade.getText());
            pst.setString(5, cboUfNaturalidade.getSelectedItem().toString());
            pst.setString(6, cboOrgRg.getSelectedItem().toString());
            pst.setString(7, cboUfRg.getSelectedItem().toString());
            pst.setString(8, dataEmi.getText());
            pst.setString(9, txtDataNasc.getText());
            pst.setString(10, cboSexo.getSelectedItem().toString());
            pst.setString(11, cboEstCivil.getSelectedItem().toString());
            pst.setString(12, txtOcupacao.getText());
            pst.setString(13, txtTelefone.getText());
            pst.setString(14, txtNomeMae.getText());
            pst.setString(15, txtNomePai.getText());
            pst.setString(16, txtEndereco.getText());
            pst.setString(17, txtNumero.getText());
            pst.setString(18, txtBairro.getText());
            pst.setString(19, txtMunicipio.getText());
            pst.setString(20, txtCadSus.getText());
            pst.setString(21, cboRaCor.getSelectedItem().toString());
            
            rs = pst.executeQuery();
            
            if (rs.next()) {
                //JOptionPane.showMessageDialog(null, texto);
                this.dispose();
                
            } else {
                
                String sql = "update tbpacientes set nome=?,cpf=?,rg=?,naturalidade=?,ufnaturalidade=?,orgao_rg=?,uf_rg=?,dataemissao_rg=?,data_nascimento=?,sexo=?,estado_civil=?,ocupacao=?,telefone=?,nome_mae=?,nome_pai=?,endereco=?,numero=?,bairro=?,municipio=?,cad_sus=?,cor=? where idp=?";
                
                try {
                    
                    if (!txtId.getText().isEmpty()) {
                        
                        pst = conexao.prepareStatement(sql);
                        pst.setString(1, txtNome.getText());
                        pst.setString(2, txtCpf.getText());
                        pst.setString(3, txtRg.getText());
                        pst.setString(4, txtNaturalidade.getText());
                        pst.setString(5, cboUfNaturalidade.getSelectedItem().toString());
                        pst.setString(6, cboOrgRg.getSelectedItem().toString());
                        pst.setString(7, cboUfRg.getSelectedItem().toString());
                        pst.setString(8, dataEmi.getText());
                        pst.setString(9, txtDataNasc.getText());
                        pst.setString(10, cboSexo.getSelectedItem().toString());
                        pst.setString(11, cboEstCivil.getSelectedItem().toString());
                        pst.setString(12, txtOcupacao.getText());
                        pst.setString(13, txtTelefone.getText());
                        pst.setString(14, txtNomeMae.getText());
                        pst.setString(15, txtNomePai.getText());
                        pst.setString(16, txtEndereco.getText());
                        pst.setString(17, txtNumero.getText());
                        pst.setString(18, txtBairro.getText());
                        pst.setString(19, txtMunicipio.getText());
                        pst.setString(20, txtCadSus.getText());
                        pst.setString(21, cboRaCor.getSelectedItem().toString());
                        pst.setString(22, txtId.getText());
                        
                        int response = JOptionPane.showConfirmDialog(null,
                                "Deseja salvar os dados antes de fechar?",
                                "Confirmação",
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        if (response == JOptionPane.YES_OPTION) {
                            
                            if (txtNome.getText().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "O Nome é obrigatório!");
                            } else if (txtNomeMae.getText().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "O nome da mãe é obrigatório!");
                            } else if (txtEndereco.getText().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "Preencha o campo endereço!");
                            } else if (txtMunicipio.getText().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "Preencha o campo municipio!");
                            } else {
                                
                                if (txtRg.getText().equals("")) {
                                    
                                    if (!cboOrgRg.getSelectedItem().equals("Não Informado")) {
                                        cboOrgRg.setSelectedIndex(0);
                                        JOptionPane.showMessageDialog(null, "Para informar o orgão o campo Rg precisa ser preenchido");
                                        
                                    } else if (!cboUfRg.getSelectedItem().equals("Não informado")) {
                                        cboUfRg.setSelectedIndex(0);
                                        JOptionPane.showMessageDialog(null, "Para informar a unidade federativa do RG o campo Rg precisa ser preenchido");
                                    } else if (!dataEmi.getText().equals("00/00/0000")) {
                                        dataEmi.setText("00/00/0000");
                                        JOptionPane.showMessageDialog(null, "Para informar a data de emissão do RG o campo Rg precisa ser preenchido");
                                    } else {
                                        //salvar no banco de dados 
                                        int adicionado = pst.executeUpdate();
                                        if (adicionado > 0) {
                                            JOptionPane.showMessageDialog(null, "Cadastro alterado com sucesso");
                                            limparCampos();
                                            pesquisar_registro();
                                            this.dispose();
                                        }
                                    }
                                    
                                } else {
                                    
                                    if (dataEmi.getText().equals("00/00/0000")) {
                                        JOptionPane.showMessageDialog(null, "Data da emissão do Rg é importante já que está sendo definido um numero de RG");
                                    } else if (cboOrgRg.getSelectedItem().equals("Não Informado")) {
                                        JOptionPane.showMessageDialog(null, "Selecione o orgão emissor do Rg");
                                    } else if (cboUfRg.getSelectedItem().equals("Não informado")) {
                                        JOptionPane.showMessageDialog(null, "Selecione a unidade federativa da emissão do Rg");
                                    } else {
                                        //executa a quelry para salvar no banco de dados 
                                        int adicionado = pst.executeUpdate();
                                        if (adicionado > 0) {
                                            JOptionPane.showMessageDialog(null, "Cadastro alterado com sucesso");
                                            limparCampos();
                                            
                                            pesquisar_registro();
                                            this.dispose();
                                        }
                                    }
                                }
                                
                            }
                            
                        } else if (response == JOptionPane.NO_OPTION) {
                            this.dispose();
                            
                        }
                        
                    } else if (!txtNome.getText().isEmpty()) {
                        
                        adicionarbtnsair();
                        
                    } else if (txtNome.getText().isEmpty()) {
                        this.dispose();
                    }
                    
                } catch (HeadlessException | SQLException e) {
                    
                    JOptionPane.showMessageDialog(null, e);
                }
                //}
            }
            //---------------------------------------
        } catch (SQLException ex) {
            
            Logger.getLogger(TelaHome.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    //============================================================================
     
    
    
    
    
    
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtPesquisar = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtPesquisarCns = new javax.swing.JFormattedTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtDataNasc = new javax.swing.JFormattedTextField();
        txtIdade = new javax.swing.JTextField();
        txtNaturalidade = new javax.swing.JTextField();
        txtMes = new javax.swing.JTextField();
        txtDias = new javax.swing.JTextField();
        cboUfNaturalidade = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtRg = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cboOrgRg = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cboUfRg = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        dataEmi = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cboSexo = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        cboEstCivil = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        txtCadSus = new javax.swing.JFormattedTextField();
        txtCpf = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        cboRaCor = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txtEndereco = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtBairro = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtMunicipio = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtNomePai = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtNomeMae = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        txtPesquisarCPF = new javax.swing.JFormattedTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        txtOcupacao = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPacientes = new javax.swing.JTable();
        txtId = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        txtTelefone = new javax.swing.JFormattedTextField();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblResultUsuDia = new javax.swing.JTable();
        txtPesquisaUsuDia = new javax.swing.JTextField();
        btnAtualizar = new javax.swing.JButton();
        txtIdR = new javax.swing.JTextField();
        btnLimparrelatorio = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnEditar = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        exibPerfil = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        MenCadUs = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RMSysCadastro");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Área de Pesquisa de Cadastro por Nome", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        txtPesquisar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarActionPerformed(evt);
            }
        });
        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyReleased(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Agency FB", 1, 12)); // NOI18N
        jLabel22.setText("Buscar nome");

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setText("X");
        jLabel25.setToolTipText("limpar");
        jLabel25.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel25)
                .addGap(232, 232, 232))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Pesquisar numero CNS"));

        try {
            txtPesquisarCns.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.####.####.####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtPesquisarCns.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPesquisarCns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarCnsActionPerformed(evt);
            }
        });
        txtPesquisarCns.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarCnsKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtPesquisarCns, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(txtPesquisarCns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados pessoais"));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("NOME");

        txtNome.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtNome.setToolTipText("Nome completo");
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });
        txtNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNomeKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("DATA NASCIMENTO");

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel34.setText("IDADE");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("NATURALIDADE");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("UF/NASCIMENTO");

        try {
            txtDataNasc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtDataNasc.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDataNasc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDataNascActionPerformed(evt);
            }
        });

        txtIdade.setEditable(false);
        txtIdade.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtIdade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdadeActionPerformed(evt);
            }
        });

        txtNaturalidade.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtNaturalidade.setToolTipText("A cidade aonde nasceu");
        txtNaturalidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNaturalidadeActionPerformed(evt);
            }
        });

        txtMes.setEditable(false);
        txtMes.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMesActionPerformed(evt);
            }
        });

        txtDias.setEditable(false);
        txtDias.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiasActionPerformed(evt);
            }
        });

        cboUfNaturalidade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Não Informado", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO", " " }));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("N° RG");

        txtRg.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtRg.setToolTipText("Numero do RG");
        txtRg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRgActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("ORGÃO");

        cboOrgRg.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Não Informado", "SSP", "DPC", " " }));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("UF: RG");

        cboUfRg.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Não informado", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO", " " }));
        cboUfRg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUfRgActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("DATA DA EMISSÃO RG");

        try {
            dataEmi.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        dataEmi.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("CPF");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("SEXO");

        cboSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Não Informado", "M", "F", " " }));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("ESTADO CIVIL");

        cboEstCivil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Não Informado", "Solteiro(a)", "Casado(a)", "Viuvo(a)", "Divorciado(a)", " " }));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("CAD SUS");

        try {
            txtCadSus.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.####.####.####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtCadSus.setToolTipText("Numero do cartão do sus");
        txtCadSus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        try {
            txtCpf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtCpf.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCpf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCpfActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("RAÇA/ETINIA/COR: ");

        cboRaCor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Não informado", "Branca", "Preta", "Parda", "Amarela", "Indigena", "Ignorado", " " }));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtNome))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNaturalidade, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDataNasc)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(txtIdade, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtMes, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtDias, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(cboSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboEstCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19)
                                    .addComponent(txtCadSus, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10)))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(195, 195, 195)
                                        .addComponent(jLabel5)
                                        .addGap(75, 75, 75)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(49, 49, 49))
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(txtRg)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cboOrgRg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cboUfRg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)))
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(dataEmi))
                                .addGap(18, 18, 18)
                                .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(cboRaCor, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboUfNaturalidade, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(11, 11, 11))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel8))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNaturalidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboUfNaturalidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel34)
                        .addComponent(jLabel14)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboEstCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCadSus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataNasc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboRaCor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel7)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboOrgRg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboUfRg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataEmi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Endereço pessoal"));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("ENDEREÇO");

        txtEndereco.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtEndereco.setToolTipText("Endereço");
        txtEndereco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEnderecoActionPerformed(evt);
            }
        });
        txtEndereco.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEnderecoKeyReleased(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("N°");

        txtNumero.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtNumero.setToolTipText("Número da residencia");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("BAIRRO");

        txtBairro.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtBairro.setToolTipText("Bairro");
        txtBairro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBairroActionPerformed(evt);
            }
        });
        txtBairro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBairroKeyReleased(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("MUNICIPIO");

        txtMunicipio.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtMunicipio.setToolTipText("Municipio");
        txtMunicipio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMunicipioKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtEndereco))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addComponent(jLabel18)
                    .addGap(27, 27, 27))
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addComponent(jLabel17)
                    .addGap(29, 29, 29)))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addComponent(jLabel20)
                            .addGap(27, 27, 27))
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Filiação"));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("NOME DO PAI");

        txtNomePai.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtNomePai.setToolTipText("Nome do pai");
        txtNomePai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomePaiActionPerformed(evt);
            }
        });
        txtNomePai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNomePaiKeyReleased(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("NOME DA MÃE");

        txtNomeMae.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtNomeMae.setToolTipText("Nome da mãe");
        txtNomeMae.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeMaeActionPerformed(evt);
            }
        });
        txtNomeMae.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNomeMaeKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addContainerGap(552, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11)
                    .addComponent(txtNomePai, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                    .addComponent(txtNomeMae))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtNomePai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(txtNomeMae, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Pesquisar CPF"));
        jPanel9.setName(""); // NOI18N

        try {
            txtPesquisarCPF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtPesquisarCPF.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPesquisarCPF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarCPFKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtPesquisarCPF, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtPesquisarCPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Ocupação"));
        jPanel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtOcupacao.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtOcupacao.setToolTipText("Ocupação");
        txtOcupacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOcupacaoActionPerformed(evt);
            }
        });
        txtOcupacao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOcupacaoKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(txtOcupacao, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(166, 166, 166)
                .addComponent(jLabel24))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(txtOcupacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tblPacientes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblPacientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblPacientes.getTableHeader().setReorderingAllowed(false);
        tblPacientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblPacientesMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblPacientesMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPacientesMouseClicked(evt);
            }
        });
        tblPacientes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblPacientesKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblPacientes);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1045, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 162, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        txtId.setEnabled(false);
        txtId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Telefone"));

        try {
            txtTelefone.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)#####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtTelefone.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTelefone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefoneActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtTelefone, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 479, Short.MAX_VALUE))))
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel2);

        jSplitPane1.setRightComponent(jScrollPane1);

        jPanel13.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 294, Short.MAX_VALUE)
        );

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Relatório Dia"));

        tblResultUsuDia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(tblResultUsuDia);

        txtPesquisaUsuDia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisaUsuDiaActionPerformed(evt);
            }
        });
        txtPesquisaUsuDia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisaUsuDiaKeyReleased(evt);
            }
        });

        btnAtualizar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnAtualizar.setText("Atualizar Tabela");
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        txtIdR.setEnabled(false);

        btnLimparrelatorio.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnLimparrelatorio.setText("Limpar");
        btnLimparrelatorio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnLimparrelatorioMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesquisaUsuDia, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(btnAtualizar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLimparrelatorio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdR, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 10, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPesquisaUsuDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAtualizar)
                    .addComponent(txtIdR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimparrelatorio))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(jPanel14);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel12);

        btnEditar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/inform/icon/edit.png"))); // NOI18N
        btnEditar.setToolTipText("Editar cadastro");
        btnEditar.setBorder(null);
        btnEditar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnSalvar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/inform/icon/diskette.png"))); // NOI18N
        btnSalvar.setToolTipText("Salvar cadastro");
        btnSalvar.setBorder(null);
        btnSalvar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnImprimir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/inform/icon/printer.png"))); // NOI18N
        btnImprimir.setToolTipText("Imprimir");
        btnImprimir.setBorder(null);
        btnImprimir.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnImprimir.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });
        btnImprimir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnImprimirKeyPressed(evt);
            }
        });

        btnLimpar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/inform/icon/clean.png"))); // NOI18N
        btnLimpar.setToolTipText("Limpar campos");
        btnLimpar.setBorder(null);
        btnLimpar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        btnExcluir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/inform/icon/delete-folder.png"))); // NOI18N
        btnExcluir.setToolTipText("Excluir registros");
        btnExcluir.setBorder(null);
        btnExcluir.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnExcluir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnExcluirMouseClicked(evt);
            }
        });

        btnSair.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/inform/icon/close.png"))); // NOI18N
        btnSair.setToolTipText("Sair");
        btnSair.setBorder(null);
        btnSair.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        exibPerfil.setText("jLabel12");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSalvar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnImprimir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLimpar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExcluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSair)
                .addGap(281, 281, 281)
                .addComponent(exibPerfil)
                .addContainerGap(664, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnEditar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImprimir, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSair, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLimpar, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exibPerfil, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu1.setText("Cadastro");

        MenCadUs.setText("Usuário");
        MenCadUs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenCadUsActionPerformed(evt);
            }
        });
        jMenu1.add(MenCadUs);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Sobre");
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu2MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Sair");
        jMenu3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenu3MousePressed(evt);
            }
        });
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1296, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtMunicipioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMunicipioKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMunicipioKeyReleased

    private void txtBairroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBairroKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBairroKeyReleased

    private void txtBairroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBairroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBairroActionPerformed

    private void txtEnderecoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEnderecoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEnderecoKeyReleased

    private void txtEnderecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEnderecoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEnderecoActionPerformed

    private void txtNomeMaeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomeMaeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeMaeKeyReleased

    private void txtNomeMaeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeMaeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeMaeActionPerformed

    private void txtNomePaiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomePaiKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomePaiKeyReleased

    private void txtPesquisarCnsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarCnsKeyReleased
        // TODO add your handling code here:
        pesquisar_cns();
    }//GEN-LAST:event_txtPesquisarCnsKeyReleased

    private void txtPesquisarCnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarCnsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesquisarCnsActionPerformed

    private void jLabel25MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseClicked
        // TODO add your handling code here:
        txtPesquisar.setText("");
    }//GEN-LAST:event_jLabel25MouseClicked

    private void txtPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyReleased
        pesquisar_registro();
    }//GEN-LAST:event_txtPesquisarKeyReleased

    private void txtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesquisarActionPerformed

    private void cboUfRgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUfRgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboUfRgActionPerformed

    private void txtDiasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDiasActionPerformed

    private void txtMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMesActionPerformed

    private void txtNaturalidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNaturalidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNaturalidadeActionPerformed

    private void txtIdadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdadeActionPerformed

    private void txtDataNascActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDataNascActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDataNascActionPerformed

    private void txtNomeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeKeyReleased

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeActionPerformed

    private void txtTelefoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefoneActionPerformed

    private void txtOcupacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOcupacaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOcupacaoActionPerformed

    private void txtOcupacaoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOcupacaoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOcupacaoKeyReleased

    private void tblPacientesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPacientesMouseReleased
        // TODO add your handling code here:
        
    }//GEN-LAST:event_tblPacientesMouseReleased

    private void tblPacientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPacientesMouseClicked
        // TODO add your handling code here:
        //quando clicado na celula da tabela preencher os campos do formulario
        
        setacampos();
        
        btnSalvar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnImprimir.setEnabled(true);
    }//GEN-LAST:event_tblPacientesMouseClicked

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        try {
            alterar();
            txtDataNasc.setEnabled(true);
        } catch (SQLException ex) {
            Logger.getLogger(TelaHome.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        //metodo salvar
        adicionar();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        imprimir();
        
        conexao = br.com.inform.dal.ModuloConexao.conector();
        excluirRegistros();
        relatoriododia();
        // Rola a tabela para a última linha adicionada
        int ultimaLinha = tblResultUsuDia.getRowCount() - 1;
        tblResultUsuDia.scrollRectToVisible(tblResultUsuDia.getCellRect(ultimaLinha, 0, true));
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnImprimirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnImprimirKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_btnImprimirKeyPressed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        limparCampos();
        btnSalvar.setEnabled(true);
        btnEditar.setEnabled(false);

    }//GEN-LAST:event_btnLimparActionPerformed

    private void btnExcluirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnExcluirMouseClicked
        // TODO add your handling code here:
        excluir();
        limparCampos();
        btnSalvar.setEnabled(true);

    }//GEN-LAST:event_btnExcluirMouseClicked

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        
        try {
            sair();
        } catch (SQLException ex) {
            Logger.getLogger(TelaHome.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSairActionPerformed

    private void txtPesquisarCPFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarCPFKeyReleased
        // TODO add your handling code here:
        pesquisar_cpf();
    }//GEN-LAST:event_txtPesquisarCPFKeyReleased

    private void txtPesquisaUsuDiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisaUsuDiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesquisaUsuDiaActionPerformed

    private void txtPesquisaUsuDiaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaUsuDiaKeyReleased
        
        relatoriododia();
    }//GEN-LAST:event_txtPesquisaUsuDiaKeyReleased

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        
        conexao = br.com.inform.dal.ModuloConexao.conector();
        excluirRegistros();
        relatoriododia();
        //RELOAD TABELA RELATORIO DIA
        int ultimaLinha = tblResultUsuDia.getRowCount() - 1;
        tblResultUsuDia.scrollRectToVisible(tblResultUsuDia.getCellRect(ultimaLinha, 0, true));

    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnLimparrelatorioMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLimparrelatorioMousePressed
        // TODO add your handling code here:
        txtPesquisaUsuDia.setText("");
        relatoriododia();
    }//GEN-LAST:event_btnLimparrelatorioMousePressed

    private void jMenu3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu3MousePressed
        try {
            // TODO add your handling code here:
            sair();
        } catch (SQLException ex) {
            Logger.getLogger(TelaHome.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenu3MousePressed

    private void jMenu2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseClicked
        // TODO add your handling code here:
        Copy sobre = new Copy(this, rootPaneCheckingEnabled);
        
        sobre.setVisible(true);

    }//GEN-LAST:event_jMenu2MouseClicked

    private void MenCadUsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenCadUsActionPerformed
        // TODO add your handling code here:
        CadastroUser usuario = new CadastroUser(this, rootPaneCheckingEnabled);
        
        usuario.setVisible(true);
        

    }//GEN-LAST:event_MenCadUsActionPerformed

    private void tblPacientesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPacientesKeyReleased
        // TODO add your handling code here:
        setacampos();        
        
        btnSalvar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnImprimir.setEnabled(true);
    }//GEN-LAST:event_tblPacientesKeyReleased

    private void tblPacientesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPacientesMousePressed
        // TODO add your handling code here:
        setacampos();        
        
        btnSalvar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnImprimir.setEnabled(true);
    }//GEN-LAST:event_tblPacientesMousePressed

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdActionPerformed

    private void txtCpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCpfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCpfActionPerformed

    private void txtRgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRgActionPerformed

    private void txtNomePaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomePaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomePaiActionPerformed

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
            java.util.logging.Logger.getLogger(TelaHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaHome().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem MenCadUs;
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnLimparrelatorio;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JComboBox<String> cboEstCivil;
    private javax.swing.JComboBox<String> cboOrgRg;
    private javax.swing.JComboBox<String> cboRaCor;
    private javax.swing.JComboBox<String> cboSexo;
    private javax.swing.JComboBox<String> cboUfNaturalidade;
    private javax.swing.JComboBox<String> cboUfRg;
    private javax.swing.JFormattedTextField dataEmi;
    private javax.swing.JLabel exibPerfil;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable tblPacientes;
    private javax.swing.JTable tblResultUsuDia;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JFormattedTextField txtCadSus;
    private javax.swing.JFormattedTextField txtCpf;
    private javax.swing.JFormattedTextField txtDataNasc;
    private javax.swing.JTextField txtDias;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtIdR;
    private javax.swing.JTextField txtIdade;
    private javax.swing.JTextField txtMes;
    private javax.swing.JTextField txtMunicipio;
    private javax.swing.JTextField txtNaturalidade;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtNomeMae;
    private javax.swing.JTextField txtNomePai;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtOcupacao;
    private javax.swing.JTextField txtPesquisaUsuDia;
    private javax.swing.JTextField txtPesquisar;
    private javax.swing.JFormattedTextField txtPesquisarCPF;
    private javax.swing.JFormattedTextField txtPesquisarCns;
    private javax.swing.JTextField txtRg;
    private javax.swing.JFormattedTextField txtTelefone;
    // End of variables declaration//GEN-END:variables

    //CODIGO RESPONSAVEL PARA ENVIAR OS DADOS PARA O FORMULARIO DE IMPRESSAO SEM O BANCO DE DADOS
    public ArrayList GetDados() {
        ArrayList lista = new ArrayList();
        Auxiliar print = new Auxiliar();
        
        print.setNome(txtNome.getText());
        print.setNaturalidade(txtNaturalidade.getText());
        print.setUfNascimento(cboUfNaturalidade.getSelectedItem().toString());
        print.setDataNascimento(txtDataNasc.getText());
        print.setAno(txtIdade.getText());
        print.setMes(txtMes.getText());
        print.setDias(txtDias.getText());
        print.setSexo(cboSexo.getSelectedItem().toString());
        print.setRg(txtRg.getText());
        print.setOrgao(cboOrgRg.getSelectedItem().toString());
        print.setUfrg(cboUfRg.getSelectedItem().toString());
        print.setDataemissao(dataEmi.getText());
        print.setPai(txtNomePai.getText());
        print.setMae(txtNomeMae.getText());
        print.setEstadocivil(cboEstCivil.getSelectedItem().toString());
        print.setCor(cboRaCor.getSelectedItem().toString());
        print.setEndereco(txtEndereco.getText());
        print.setNumero(txtNumero.getText());
        print.setBairro(txtBairro.getText());
        print.setMunicipio(txtMunicipio.getText());
        print.setOcupacao(txtOcupacao.getText());
        print.setTelefone(txtTelefone.getText());
        print.setCpf(txtCpf.getText());
        print.setCns(txtCadSus.getText());
        lista.add(print);
        
        return lista;
        
    }
    
}
