/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mejorana.Orondo2.productos;

import Mejorana.Orondo2.Styling.Styler;
import Mejorana.Orondo2.inicio.Locations;
import Mejorana.Orondo2.inicio.mainController;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


/**
 *
 * @author Raul Alzate
 */
public class productosController {
    
    /**
     * bp = bigpane. el panel padre o root panel
     */
    @FXML
    public AnchorPane AnchorPane_bp;
    
    @FXML
    public AnchorPane AnchorPane_center;
    
    @FXML
    public BorderPane BorderPane_bp;

    @FXML
    public VBox VBox_Atras;

    @FXML
    public VBox VBox_Codificar;

    @FXML
    public VBox VBox_Modificar;
    
    public modificarController modcont = new modificarController();
    public codificarController codcont = new codificarController();
    
    public Pane pane_modificar;
    public Pane pane_codificar;
    
    public mainController maincont;

    public productosController() throws IOException {
        this.pane_modificar = (Pane) FXMLLoader.load(modcont.getClass().getResource(Locations.modificar_fxml));
        this.pane_codificar = (Pane) FXMLLoader.load(modcont.getClass().getResource(Locations.codificar_fxml));
    }
    
    public void initialize() {
        Styler.AddFadingAnimation_onHover(VBox_Atras);
        Styler.AddFadingAnimation_onHover(VBox_Codificar);
        Styler.AddFadingAnimation_onHover(VBox_Modificar);
    }
    
    @FXML
    public void onClick_VBox_Atras(MouseEvent event) throws IOException {
        maincont.mainstage.setScene(maincont.scn_main);
        maincont.Maximizar();
    }

    @FXML
    public void onClick_VBox_Codificar(MouseEvent event) throws IOException {
        BorderPane_bp.setCenter(pane_codificar);
    }

    @FXML
    public void onClick_VBox_Modificar(MouseEvent event) throws IOException {
        BorderPane_bp.setCenter(pane_modificar);
    }
    
}


/**
 * MONGODB AGGREGATION 
 * ley de benford
 * 
 * 
    db.productos.aggregate([{
        $group: { _id: { $substr:[ {$toString: [ "$costo"]} , 0, 1]} ,  num:{$sum : 1} }
    }])
 * 
 * db.productos.count()
 * 
 * 
 */