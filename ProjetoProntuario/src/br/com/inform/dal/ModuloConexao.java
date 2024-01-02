package br.com.inform.dal;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ModuloConexao {
    public static Connection conector() {

        Connection conexao = null;
        InputStream input = null;

        try {
            // Carregar o arquivo de propriedades
            input = ModuloConexao.class.getClassLoader().getResourceAsStream("resources/database.properties");

            if (input == null) {
                System.out.println("Desculpe, o arquivo de propriedades 'database.properties' não foi encontrado.");
                return null;
            }

            Properties prop = new Properties();
            // Carregar as propriedades do arquivo
            prop.load(input);

            // Obter valores das propriedades
            String driver = "com.mysql.jdbc.Driver";
            String url = prop.getProperty("jdbc.url");
            String user = prop.getProperty("jdbc.usuario");
            String password = prop.getProperty("jdbc.senha");

            // Estabelecer a conexão com o banco de dados
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);

            return conexao;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
