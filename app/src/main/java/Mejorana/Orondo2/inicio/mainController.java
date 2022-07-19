package Mejorana.Orondo2.inicio;

import java.net.URL;
import java.util.ResourceBundle;
import Mejorana.Orondo2.OrondoDb.dbMapper;
import Mejorana.Orondo2.Styling.Styler;
import Mejorana.Orondo2.productos.productosController;
import Mejorana.Orondo2.ventas.ventasController;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;




public class mainController {

    /**
    *estas variables se inyectan por defecto, las pongo como comentario
    *a manera de recordatorio por si en algun momento es util pero no he pensado
    *en un posible uso
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    */
    
    @FXML
    private VBox VBox_Productos;
    
    @FXML
    private VBox VBox_Analitica;
    
    @FXML
    private VBox VBox_Simulador;
    
    @FXML
    private VBox VBox_Configuracion;
    
    @FXML 
    private VBox VBox_Ventas;
    
    @FXML
    public AnchorPane AnchorPane_main;
    
    //controladores padre de cada seccion
    public productosController prodcont;
    public ventasController vencont;
    
    // al guardar las instacias de las scenas se logra la persistencia del 
    // estado de las UI mientras que el usuario navega entre escenas
    // scene padre de cada seccion 
    public Scene scn_productos;
    public Scene scn_ventas;
    // stage principal y scena principal
    public Stage mainstage;
    public Scene scn_main;
    
    Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();

    @FXML
    public void initialize() throws IOException {
        //se agrega animacion on hover de los botones de la ventana principal
        Styler.AddFadingAnimation_onHover(VBox_Productos);
        Styler.AddFadingAnimation_onHover(VBox_Analitica);
        Styler.AddFadingAnimation_onHover(VBox_Simulador);
        Styler.AddFadingAnimation_onHover(VBox_Configuracion);
        Styler.AddFadingAnimation_onHover(VBox_Ventas);
        
        // en productosLoader.load() se llama al constructor de 
        // productosController, alli se inicia cada uno de los panes que son
        // como sub scenas que iran en el centro del BorderPane
        FXMLLoader productosLoader = new FXMLLoader(productosController.class.getResource(Locations.productos_fxml));
        //scn_productos = new Scene(productosLoader.load(),screenSize.getWidth(), screenSize.getHeight());
        scn_productos = new Scene(productosLoader.load());
        prodcont = productosLoader.getController();
        prodcont.maincont = this; // se envia una instancia de este controlador
        // para permitir comunicacion entre scenas y facilitar regreso al 
        //main scene.
        
        FXMLLoader ventasLoader = new FXMLLoader(ventasController.class.getResource(Locations.ventas_fxml));
        scn_ventas = new Scene(ventasLoader.load());
        vencont = ventasLoader.getController();
        vencont.maincont = this;
        
        
        // se verifica si la base de datos debe incializarse.
        dbMapper dbm = new dbMapper();
        dbm.InitMongo(); // si no existen las collecciones, se crean.
    }
    
    /**
     * cuando se hace click en el VBox de producto se debe cambiar a la
     * pantalla de productos
     * @param event 
     */
    @FXML
    void onClick_VBox_Productos(MouseEvent event) throws IOException {
        mainstage.setScene(scn_productos);
        Maximizar();
    }
    
    @FXML
    void onClick_VBox_Ventas(MouseEvent event) throws IOException{
        mainstage.setScene(scn_ventas);
        Maximizar();
    }
    
    /***
     * https://stackoverflow.com/questions/38599588/javafx-stage-setmaximized-only-works-once-on-mac-osx-10-9-5/38606533
     * siempre se debe poner primero false y luego true, de lo contrario
     * solo funcionara la primera vez para maximizar la aplicacion.
     */
    public void Maximizar(){
        mainstage.setMaximized(false);
        mainstage.setMaximized(true);
    }
}
