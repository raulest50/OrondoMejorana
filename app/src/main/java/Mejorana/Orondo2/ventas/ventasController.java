package Mejorana.Orondo2.ventas;

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

public class ventasController {
    
    @FXML
    public AnchorPane AnchorPane_bp;
    
    @FXML
    public BorderPane BorderPane_bp;
    
    @FXML
    public VBox VBox_Atras;
    
    @FXML
    public VBox VBox_HacerVentas;
    
    @FXML
    public VBox VBox_Monitorear;
    
    public Pane pane_vender;
    public Pane pane_monitorear;
    public Pane pane_reg_ventas;
    
    public mainController maincont;
    
    public hacerVentasController hacer_ventas_cont = new hacerVentasController();
    public monitorear_retailController monitorear_cont = new monitorear_retailController();
    
    public ventasController() throws IOException{
        this.pane_vender = (Pane) FXMLLoader.load(this.getClass().getResource(Locations.hacer_ventas_fxml));
        this.pane_monitorear = (Pane) FXMLLoader.load(this.getClass().getResource(Locations.monitorear_ventas_fxml));
        this.pane_reg_ventas = (Pane) FXMLLoader.load(this.getClass().getResource(Locations.reg_ventas_fxml));
    }
    
    public void initialize() {
        Styler.AddFadingAnimation_onHover(VBox_Atras);
        Styler.AddFadingAnimation_onHover(VBox_HacerVentas);
        Styler.AddFadingAnimation_onHover(VBox_Monitorear);
    }
    
    @FXML
    public void onClick_VBox_Atras(MouseEvent event) throws IOException{
        maincont.mainstage.setScene(maincont.scn_main);
        maincont.Maximizar();
    }
    
    @FXML
    public void onClick_VBox_HacerVentas(MouseEvent event) throws IOException{
        BorderPane_bp.setCenter(pane_vender);
    }
    
    @FXML
    public void onClick_VBox_Monitorear(MouseEvent event){
        BorderPane_bp.setCenter(pane_monitorear);
    }
    
    @FXML
    public void onClick_VBox_RegVentas(){
        BorderPane_bp.setCenter(pane_reg_ventas);
    }
    
    
}
