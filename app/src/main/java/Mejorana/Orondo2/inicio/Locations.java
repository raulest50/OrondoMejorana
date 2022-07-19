package Mejorana.Orondo2.inicio;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Para guardar de forma estatica los nombres de los resources y otros archivos
 * y asi poder cambiarlos mas facilmente en el codigo en caso de ser necesario.
 * importantes.
 * 
 * se debe recordar que el metodo que se esta usando para cargar los resources
 * es mediante el uso de los metodos getClass.getResource los cuales permiten
 * acceder directamente al respectivo folder de un .classes dentro de la carpeta
 * build. Se accede al folder en una linea, sin usar rutas absolutas.
 * para que funcione es importante que se copien los resources junto con los
 * respectivos .classes en cada una de las carpetas. en esta caso se usa gradle 
 * el cual se le adiciono una directiva para asegurar que sean copiados.
 * @author Raul Alzate
 */
public class Locations {
    
    /**
     * FXML view de inicio de la aplicacion. Tiene botones con los diferentes 
     * modulos del programa
     */
    public static String main_fxml = "main.fxml";
    
    /**
     * archivo de estilizado del view principal
     */
    public static String main_css = "main.css";
    
    /**
     * view de productos
     */
    public static String productos_fxml = "productos.fxml";
    
    /**
     * Se pone en el centro del borderpane de productos cuando se da click
     * al boton codificar
     */
    public static String codificar_fxml = "codificar.fxml";
    
    /**
     * lo mismo que codificar pero para buscar y modificar productos
     */
    public static String modificar_fxml = "modificar.fxml";
    
    public static String modificar_dialog_fxml = "modificarDialog.fxml";
    
    public static String reg_ventas_fxml = "reg_ventas.fxml";
    
    public static String ventas_fxml = "ventas.fxml";
    
    public static String hacer_ventas_fxml = "hacer_ventas.fxml";
    
    public static String monitorear_ventas_fxml = "monitorear_retail.fxml";
    
    public static String pop_up_negociar_fxml = "PopUp_Negociar.fxml";
    
    
}
