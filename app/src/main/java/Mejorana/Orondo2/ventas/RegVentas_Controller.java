package Mejorana.Orondo2.ventas;

import Mejorana.Orondo2.OrondoDb.ItemVenta;
import Mejorana.Orondo2.OrondoDb.Venta;
import Mejorana.Orondo2.OrondoDb.dbMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;
import javafx.beans.property.SimpleIntegerProperty;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Esteban
 * 
 * Controlador de la seccion de registro de ventas.
 * permite visualizar las ventas entre fechas especificadas.
 * 
 * por defecto muestra las ventas del dia actual.
 */
public class RegVentas_Controller {
    
    
    /**
     * tabla de ventas o facturas
     */
    @FXML
    private TableView<Venta> TV_Ventas;

    @FXML
    private TableColumn<String, Venta> TC_TV_Consecutivo;

    @FXML
    private TableColumn<Integer, Venta> TC_TV_Valor;

    @FXML
    private TableColumn<String, Venta> TC_TV_Fecha;

    @FXML
    private TableColumn<String, Venta> TC_TV_Cliente;

    
    /**
     * Tabla de Items ventas
     */
    @FXML
    private TableView<ItemVenta> TV_ItemVenta;

    @FXML
    private TableColumn<String, ItemVenta> TC_ItemVenta_Descripcion;

    @FXML
    private TableColumn<SimpleIntegerProperty, ItemVenta> TC_ItemVenta_Precio;

    @FXML
    private TableColumn<Integer, ItemVenta> TC_ItemVenta_N;

    @FXML
    private TableColumn<Integer, ItemVenta> TC_ItemVenta_SubT;
    
    
    // date picker lim inferior
    @FXML
    private DatePicker DTP_Desde;

    // date picker lim superior
    @FXML
    private DatePicker DTP_Hasta;
    
    @FXML
    public Spinner Sp_HH_Desde;
    
    @FXML
    public Spinner Sp_HH_Hasta;
    
    @FXML
    public Spinner Sp_MM_Desde;
    
    @FXML
    public Spinner Sp_MM_Hasta;
    
    
    //Labels para motrar datos de las ventas entre las fechas especificadas
    @FXML
    public Label L_NVentas;
    
    @FXML
    public Label L_Realizo;
    
    @FXML
    public Label L_Ganancia;
    
    @FXML
    public Label L_PorcentUtilidad;
    
    // Create ContextMenu
    ContextMenu contextMenu_TVentas = new ContextMenu();
    
    
    public dbMapper dbm = new dbMapper(); // para interactura con mongo.
    
    
    // la api de javafx invoca este metodo al inicio de operacion del controlador
    public void initialize(){
        ConfigTables();
        CargarVentas(this.getLDateRange());
        InitSpinners();
    }
    
    @FXML
    public void onClick_B_Actualizar(MouseEvent e){
        // para tener los limites inferios y superior en una sola variable
        LocalDateTime[] ldtr = new LocalDateTime[2];
        LocalDateTime df; // fecha limite inferior
        LocalDateTime dto; // fecha limite superior
        
        if( Objects.isNull(DTP_Desde.getValue()) || Objects.isNull(DTP_Desde.getValue())){
            CargarVentas(this.getLDateRange());
        } else {
            df = DTP_Desde.getValue().atStartOfDay();
            dto = DTP_Hasta.getValue().atStartOfDay();
            
            df = df.plusHours((int) Sp_HH_Desde.getValue());
            df = df.plusMinutes((int) Sp_MM_Desde.getValue());
            
            dto = dto.plusHours((int) Sp_HH_Hasta.getValue());
            dto = dto.plusMinutes((int) Sp_MM_Hasta.getValue());
            ldtr[0] = df;
            ldtr[1] = dto;
            System.out.println(ldtr[0]+" - "+ldtr[1]);
            System.out.println("${ldtr[0]} - ${ldtr[1]}");
            CargarVentas(ldtr);
        }
        TV_ItemVenta.getItems().setAll( new ArrayList<>() );
    }
    
    /**
     * configura el context menu que sale con click derecho en la tabla de 
     * ventas.
     */
    public void ConfCMenuTV(){
        
        TV_Ventas.setOnMouseClicked(mevent -> {
            if (mevent.getButton() == MouseButton.SECONDARY){
                contextMenu_TVentas.show(contextMenu_TVentas);
            }
        });
    }
    
    /**
     * configura las columnas de los table view y la funcionalidad de las tablas
     */
    public void ConfigTables(){
        TC_TV_Consecutivo.setCellValueFactory(new PropertyValueFactory<>("id"));
        TC_TV_Valor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        TC_TV_Fecha.setCellValueFactory(new PropertyValueFactory<>("fecha_str"));
        TC_TV_Cliente.setCellValueFactory(new PropertyValueFactory<>("Cliente_id"));
        
        TC_ItemVenta_Descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        TC_ItemVenta_N.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        TC_ItemVenta_Precio.setCellValueFactory(new PropertyValueFactory<>("pVenta"));
        TC_ItemVenta_SubT.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        
        
        // se agrega listener al tv de ventas para mostrar la lista de items venta
        // correcpondiente cada que se cambia de factura pos seleccionada.
        TV_Ventas.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                TV_ItemVenta.getItems().setAll(TV_Ventas.getSelectionModel().getSelectedItem().items);
            }
        });
    }
    
    /**
     * Si estan vacios los datepicker entonces pone las ventas del dia actual.
     * Es importante usar LocalDateTime y no Date, la parte de tiempo es vital 
     * en el query del rango en mongo traer las ventas de 1 dia en especifico 
     * se pone en el rango el mismo date y la parte de tiempo se pone el lim
     * inferior 00:00:00 y el limite superior 23:59:59.
     * p
     * @return 
     */
    public LocalDateTime[] getLDateRange(){
        LocalDateTime[] ldtr = new LocalDateTime[2];
        LocalDateTime df;
        LocalDateTime dto;
                
        try{
            df = DTP_Desde.getValue().atStartOfDay();
            dto = DTP_Hasta.getValue().atTime(23, 59, 59);
        } 
        catch(NullPointerException e){
            df = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            dto = df.plusHours(23).plusMinutes(59).plusSeconds(59);
        }
        
        ldtr[0] = df;
        ldtr[1] = dto;
        return ldtr;
    }
    
    public void CargarVentas(LocalDateTime[] ldtr){
        ArrayList<Venta> lv = dbm.getVentas(ldtr);
        L_NVentas.setText(Integer.toString(lv.size()));
        TV_Ventas.getItems().setAll(lv);
        System.out.println(lv.size());
        System.out.println(lv);
        int Scosto = 0;
        int SVenta = 0;
        for(Venta v : lv){
            for(ItemVenta itv : v.getItems()){
                Scosto += itv.p.costo * itv.getCantidad();
                SVenta += itv.getSubTotal();
            }
        }
        
        L_Realizo.setText(Integer.toString(SVenta));
        L_Ganancia.setText(Integer.toString(SVenta-Scosto));
        double pcent = 0.0;
        if (Scosto > 0) pcent = ((double) (SVenta-Scosto)/ (double) Scosto)*100;
        L_PorcentUtilidad.setText(Double.toString(pcent));
    }
    
    /**
     * se configuran los controles para establecer hora inicial y final
     * de la fecha entre la que se desea analizar los registros de ventas
     */
    public void InitSpinners(){
        Sp_HH_Desde.setValueFactory( new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23, 0) );
        Sp_HH_Hasta.setValueFactory( new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23, 0) );
        Sp_MM_Desde.setValueFactory( new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59, 0) );
        Sp_MM_Hasta.setValueFactory( new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59, 0) );
    }
    
}
