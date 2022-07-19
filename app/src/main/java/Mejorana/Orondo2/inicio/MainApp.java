package Mejorana.Orondo2.inicio;

import Mejorana.Orondo2.Styling.Styler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApp extends Application {    

    @Override
    public void start(Stage stage) throws Exception {
        
        // Cuando se cerraba la ventana, la app seguia corriendo.
        // en https://stackoverflow.com/questions/14938279/javafx-application-still-running-after-close
        // se muestra esta solucion. Se supone que esto sucede cuando queda
        // un thread vivo, pero con esto se cierra por la fuerza.
        Platform.setImplicitExit(true);
        stage.setOnCloseRequest((ae) -> {
            Platform.exit();
            System.exit(0);
        });
        
        System.out.println( getClass() );
        System.out.println( getClass().getResource("") );
        System.out.println( getClass().getResource("main.fxml") );
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Locations.main_fxml));
        Parent root = loader.load();
        mainController maincont = loader.getController();
        
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        //Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(new Styler().getClass().getResource(Locations.main_css).toExternalForm());
        
        maincont.mainstage = stage;
        maincont.scn_main = scene;
        
        
        stage.setTitle("ORONDO");
        stage.setScene(scene);
        stage.setX(0);
        stage.setY(0);
        stage.show();
        
        maincont.Maximizar();
    }

    public static void main(String[] args) {
        launch(args);
    }


}


// facturacion POS requerimientos y cuando hacer facturacion electronica.
// https://www.gerencie.com/facturacion-pos.html