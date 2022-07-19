/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mejorana.Orondo2.ventas;

import Mejorana.Orondo2.OrondoDb.ItemVenta;
import Mejorana.Orondo2.Styling.Styler;
import Mejorana.Orondo2.inicio.GenericDialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 *
 * @author Raul Alzate
 */
public class PopUp_NegociarController {
    
    @FXML
    public Label L_Descripcion;
    
    @FXML
    public Label L_Costo;
    
    @FXML
    public Label L_PV_Publico;
    
    @FXML
    public Label L_PV_mayorista;
    
    @FXML
    public TextField TF_Subtotal;
    
    @FXML
    public TextField TF_PrecioVenta;
    
    @FXML
    public TextField TF_Cantidad;
    
    @FXML
    public CheckBox CH_Box_Fraccionar;
    
    @FXML
    public Label L_fracUnits;
    
    public hacerVentasController hv;
    
    public ItemVenta iv;
    
    //private ItemVenta ivcp;
    
    public void initialize(){
        
        // la funcionalidad de cambiar el subtotal aun tiene que analizarse bien
        // por tanto se deshabilita la edicion del textfield mientras
        //TF_Subtotal.setEditable(false);
        
        Styler.SetTextFieldAsNumericInt(TF_Cantidad);
        Styler.SetTextFieldAsNumericInt(TF_PrecioVenta);
        Styler.SetTextFieldAsNumericInt(TF_Subtotal);
        
        // 3 listeners a cambios en los textfield para actualizar de manera 
        // automatica los demas valores del itemventa
        TF_PrecioVenta.textProperty().addListener((obs, old, neu)->{
            try{
                int temp = Integer.parseInt(neu);
                if(temp >= iv.p.costo){
                    iv.setUnitPrecio(Integer.parseInt(neu));
                    RefreshTextFields();
                }
            } catch(NumberFormatException ex){ 
                // se agrega el catch solo para que no salga error en el output
                // de la consola en el caso que el campo de texto precio venta
                // este vacio. logica similar en los otros textfield
            }
        });
        
        TF_Cantidad.textProperty().addListener((obs, old, neu)->{
            try{
                System.out.println(" old: ${old}   nuevo: ${neu}");
                int temp = Integer.parseInt(neu);
                if(temp == 0) iv.setCantidad(1);
                else iv.setCantidad(temp);
                RefreshTextFields();
            } catch(NumberFormatException ex){
                
            }
        });
        
        TF_Subtotal.textProperty().addListener((obs, old, neu)->{
            try{
                int temp = Integer.parseInt(neu);
                if(temp >= iv.p.costo*iv.getCantidad()){
                    iv.setSubTotal(temp);
                    RefreshTextFields();
                }
            } catch(NumberFormatException ex){
                
            }
        });
        
        TF_PrecioVenta.focusedProperty().addListener((obs, old, neu)->{
            if(old && !neu){ // si se pasa de focus a no focus entonces
                try{
                    int temp = Integer.parseInt(TF_PrecioVenta.getText());
                    if(temp < iv.p.costo){
                        iv.setSubTotal(temp);
                        RefreshTextFields();
                    }
                } catch(NumberFormatException e){
                    
                }
            }
        });
        
        TF_Subtotal.focusedProperty().addListener((obs, old, neu)->{
            if(old && !neu){ // si se pasa de focus a no focus entonces
                try{
                    int temp = Integer.parseInt(TF_Subtotal.getText());
                    if(temp < (iv.p.costo*iv.getCantidad())){
                        iv.setUnitPrecio(iv.p.costo);
                        RefreshTextFields();
                    }
                } catch(NumberFormatException e){
                    
                }
            }
        });
        
        L_fracUnits.setVisible(false);
        CH_Box_Fraccionar.selectedProperty().addListener((obs, old, neu)->{
            if(neu){
                L_fracUnits.setVisible(true);
            } else {
                L_fracUnits.setVisible(false);
            }
            iv.setFraccionado(neu);
            RefreshTextFields();
        });
        
        // se queria adicionar la posibilidad de modificar el subtotal tambien
        // y que esto modificara dinamicamente el precio de venta.
        // pero para enfocar esfuerzos en caracteristicas mas urgentes se
        // bloquean cambios en este campo de texto ya que el comportamiento
        // aun es algo buggi. Mas adelante con mas tiempo lo retomare
        TF_Subtotal.setEditable(false);
        
        
    }
    
    @FXML
    public void onAction_B_Modificar(ActionEvent event){
        if(CambioValido()){
            hv.UpdateItemVenta(iv);
            cerrar();
        } else{
            GenericDialogs.Info("Porfavor Verifique los datos", "", "El precio negociado no puede ser inferior al costo\n"
                    + "del producto, verifique porfavor los valores ingresados.");
        }
    }
    
    @FXML
    public void onAction_B_Cancelar(ActionEvent event){
        cerrar();
    }
            

    public void setItemVenta(ItemVenta iv, hacerVentasController hv){
        this.iv = new ItemVenta(iv.p, iv.getCantidad(), iv.getUnitPrecio(), iv.isFraccionado());
        this.hv = hv;
        
        this.L_Descripcion.setText(iv.p.descripcion);
        this.L_Costo.setText(Integer.toString(iv.p.costo));
        this.L_PV_Publico.setText(Integer.toString(iv.p.pv_publico));
        this.L_PV_mayorista.setText(Integer.toString(iv.p.pv_mayor));
        RefreshTextFields();
        
        if(!iv.p.fraccionable){
            CH_Box_Fraccionar.setDisable(true);
        } else{
            if(iv.isFraccionado()) CH_Box_Fraccionar.setSelected(true);
        }
        
    }
    
    public void RefreshTextFields(){
        this.TF_Cantidad.setText(Integer.toString(iv.getCantidad()));
        this.TF_PrecioVenta.setText(Integer.toString(iv.getPVenta()));        
        this.TF_Subtotal.setText(Integer.toString(iv.getSubTotal()));
    }
    
    public boolean CambioValido(){
        boolean r=true;
        if(iv.getPVenta() < iv.p.costo) r=false;
        
        return r;
    }
    
    
    public void cerrar(){ // metodo para cerrar la ventana
        // se obtiene un handle de la ventana actual mediante un boton de la  misma ventana
        //en este caso el mismo asociado a la operacion de cancelar
        Stage stage = (Stage) TF_PrecioVenta.getScene().getWindow();
        // se cierra la ventana de modificacion
        stage.close();
        // actualizar los cambios en la pantalla de venta
    }
    
}
