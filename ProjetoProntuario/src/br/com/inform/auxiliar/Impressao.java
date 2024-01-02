
package br.com.inform.auxiliar;


import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class Impressao {
    
       public void Imprime_Relatorio(List lista){
        String caminhoRealJasper = "/br/com/inform/auxiliar/cadastro.jasper";
        
        
        InputStream realJasper = getClass().getResourceAsStream(caminhoRealJasper);
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(lista);
        
        Map parametros = new HashMap();
        JasperPrint impressao = null;
        
        try {
            impressao = JasperFillManager.fillReport(realJasper, parametros, ds);
            
            JasperViewer viewer = new JasperViewer(impressao, false);
            viewer.setTitle("Formulario");
            viewer.setVisible(true);
        } catch (JRException e) {
            
        }
    
        
    }
    
    
    
}
