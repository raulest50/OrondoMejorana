package Mejorana.Orondo2.ventas;

import Mejorana.Orondo2.OrondoDb.ItemVenta;
import Mejorana.Orondo2.OrondoDb.Producto;
import Mejorana.Orondo2.OrondoDb.Venta;
import Mejorana.Orondo2.OrondoDb.dbMapper;
import Mejorana.Orondo2.ThermalPrint.RPT008_3nstar;
import Mejorana.Orondo2.inicio.GenericDialogs;
import Mejorana.Orondo2.inicio.Locations;
import Mejorana.Orondo2.utils.Toast;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.print.PrintException;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * @author Raul Alzate
 */
public class hacerVentasController {
    
    
    @FXML
    TextField TF_BuscarProducto;
    
    @FXML
    Label L_Total;
    
    
    @FXML
    TableView<Producto> TV_Busqueda;
    
    @FXML
    TableColumn TCBuscar_Codigo;
    
    @FXML
    TableColumn TCBuscar_Descripcion;
    
    @FXML
    TableColumn TCBuscar_Costo;
    
    @FXML
    TableColumn TCBuscar_PVmayor;
    
    @FXML
    TableColumn TCBuscar_PVPublico;
    
    
    
    @FXML
    TableView<ItemVenta> TV_Ventas;
    
    @FXML
    TableColumn TCVentas_Descripcion;
    
    @FXML
    TableColumn TCVentas_Pventa;
    
    @FXML
    TableColumn TCVentas_Cantidad;
    
    @FXML
    TableColumn TCVentas_SubTotal;
    
    
    @FXML
    public ComboBox CB_OpcionesB;
    
    
    @FXML
    public ToggleButton ToggButton_TipoVenta;
    
    @FXML
    public CheckBox CHBox_Unificar;
    
    
    ObservableList<String> itemsComboB = FXCollections.observableArrayList();
    
    ArrayList<ItemVenta> ListaVenta;
    
    //para busqueda por codigo exacto
    public final String B_CODIGO_EXACT = "Codigo Exacto";
    
    // busqueda por coincidencias parciaes en la descripcion
    public final String B_DESCRI = "Descripcion";
    
    //
    public final String B_LAST_CODE = "Ultimos del Codigo";
    
    
    public final String STR_VENTA_MAYOR="MAYORISTA";
    public final String STR_VENTA_PUBLICO="DETAL";
    
    //suma de los productos que se han registrado
    public int Suma=0;
    
    public void initialize(){
        
        // Table view busqeuda productos para luego adicionar a table view ventas
        TCBuscar_Codigo.setCellValueFactory(new PropertyValueFactory<>("id"));
        TCBuscar_Descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        TCBuscar_Costo.setCellValueFactory(new PropertyValueFactory<>("costo"));
        TCBuscar_PVmayor.setCellValueFactory(new PropertyValueFactory<>("pv_mayor"));
        TCBuscar_PVPublico.setCellValueFactory(new PropertyValueFactory<>("pv_publico"));
        
        //Table view ventas
        TCVentas_Descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        TCVentas_Cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad_str"));
        TCVentas_Pventa.setCellValueFactory(new PropertyValueFactory<>("pVenta"));
        TCVentas_SubTotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        
        // se inicializa las opciones del ComboBox
        itemsComboB.addAll(this.B_DESCRI, this.B_CODIGO_EXACT, this.B_LAST_CODE);
        CB_OpcionesB.setItems(itemsComboB);
        CB_OpcionesB.getSelectionModel().selectFirst();
        
        ToggButton_TipoVenta.setText(this.STR_VENTA_PUBLICO);
        ToggButton_TipoVenta.selectedProperty().addListener((Obs, oldv, newv)->{
            if(newv){
                ToggButton_TipoVenta.setText(this.STR_VENTA_MAYOR);
            } else{
                ToggButton_TipoVenta.setText(this.STR_VENTA_PUBLICO);
            }
        });
        
        //double click listener para tabla de busqueda
        TV_Busqueda.setRowFactory( tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    TVBuscar2TV_Venta();
                }
            });
            return row ;
        });
        
        //double click listener para tabla de ventas
        TV_Ventas.setRowFactory( tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) && !TV_Ventas.getSelectionModel().isEmpty()) {
                    try{
                        abrirNegociarDialog(event, TV_Ventas.getSelectionModel().getSelectedItem());
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }
            });
            return row ;
        });
        
    }
    
    /**
     * cuando se hace enter en el tableview de busqueda de productos,
     * si hay un producto seleccionado, este se debe agregar al tableview
     * de venta.
     * @param event 
     */
    @FXML
    private void onKeyPressed_TVBuscar(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)){
            TVBuscar2TV_Venta();
        }
        if(event.getCode().equals(KeyCode.ALT_GRAPH)){
            TV_Ventas.requestFocus();
            TV_Ventas.getSelectionModel().selectFirst();
        }
    }
    
    /**
     * shortcuts para la tabla de ventas
     * @param event 
     */
    @FXML
    private void onKeyPressed_TV_Ventas(KeyEvent event){
        if(!TV_Ventas.getSelectionModel().isEmpty()){
            if(event.getCode().equals(KeyCode.DELETE)){
                RemoverItemVenta(TV_Ventas.getSelectionModel().getSelectedItem());
            }
            
            if(event.getCode().equals(KeyCode.PLUS)){
                TV_Ventas.getSelectionModel().getSelectedItem().Add2Cantidad(+1);
                //System.out.println(TV_Ventas.getSelectionModel().getSelectedItem().Cantidad);
                TV_Ventas.refresh();
                RefreshSuma();
            }
            
            if(event.getCode().equals(KeyCode.MINUS)){
                int temp = TV_Ventas.getSelectionModel().getSelectedItem().getCantidad();
                if(temp>1){
                    TV_Ventas.getSelectionModel().getSelectedItem().Add2Cantidad(-1);
                    TV_Ventas.refresh();
                    RefreshSuma();
                }
            }
            
            if(event.getCode().equals(KeyCode.ALT_GRAPH)){
                TF_BuscarProducto.requestFocus();
            }
        }
    }
    
    
    @FXML
    private void onKeyPressed_BorderPane(KeyEvent event){
       
    }
    
    /**
     * se configuran teclas de acceso rapido para agiliar.
     * si el campo de texto de busqeuda tiene focus, presionar la
     * tecla alt cambia el criterio de busqueda. presionar alt graph
     * permite pasar el focus al la tabla de busqueda
     * @param event 
     */
    @FXML
    private void onKeyPressed_TF_Busqueda(KeyEvent event){
        if(event.getCode().equals(KeyCode.ALT)){
            if(CB_OpcionesB.getSelectionModel().getSelectedIndex()==2) {
                CB_OpcionesB.getSelectionModel().selectFirst();
            } else{
                CB_OpcionesB.getSelectionModel().selectNext();
            }
        }
        
        if(event.getCode().equals(KeyCode.ALT_GRAPH)){
            TV_Busqueda.requestFocus();
            TV_Busqueda.getSelectionModel().selectFirst();
        }
    }
    
    
    /**
     * se realiza la busqueda de productos deacuerdo al texto ingresado en
     * el textfield de busqueda y la opcion seleccionada en el combobox
     * @param event 
     */
    @FXML
    public void onAction_BuscarProducto(ActionEvent event){
        BuscarProducto();
        if(!TV_Busqueda.getItems().isEmpty() && !CB_OpcionesB.getSelectionModel().getSelectedItem().equals(this.B_CODIGO_EXACT)){            
            TV_Busqueda.requestFocus();
            TV_Busqueda.getSelectionModel().select(0);
        }
        
        if(CB_OpcionesB.getSelectionModel().getSelectedItem().equals(this.B_CODIGO_EXACT)){
            TF_BuscarProducto.selectAll();
        }
    }
    
    @FXML
    public void onAction_ToggButton(ActionEvent event){
        this.TF_BuscarProducto.requestFocus();
    }
    
    @FXML
    public void onAction_B_Registrar(ActionEvent event){
        RegistrarVenta();
    }
    
    @FXML
    public void onAction_B_Cancelar(ActionEvent event){
        TV_Ventas.getItems().clear();
        //TV_Ventas.refresh();
    }
    
    @FXML // cuando se hace click en el boton de imprimir 
    public void onAction_B_Imprimir(ActionEvent event){
        System.out.println("imprimir_remi");
        //HttpPost_Imprimir();
        UsbImprimir();
    }
    
    
    public void UsbImprimir(){
        RPT008_3nstar printer = new RPT008_3nstar();
        
        ObservableList<ItemVenta> ol = this.TV_Ventas.getItems();
        if(ol.size()>= 1){ // si la lista no de venta no esta vacia.
            LinkedList<ItemVenta> lit = new LinkedList<>();
            ol.iterator().forEachRemaining((x) -> { lit.add(x); });
            
            try {
                printer.ImprimirRemision(lit);
                
            } catch (NullPointerException ex) {
                Logger.getLogger(hacerVentasController.class.getName()).log(Level.SEVERE, null, ex);
                GenericDialogs.Error("NullPointerException", "", "GetPrint Service retorna null");
            } catch (PrintException ex) {
                Logger.getLogger(hacerVentasController.class.getName()).log(Level.SEVERE, null, ex);
                GenericDialogs.Error("PrintException", ex.toString(), ex.getMessage());
            }
            
        }
    }
    
    
    public void HttpPost_Imprimir(){
        
        ObservableList<ItemVenta> ol = this.TV_Ventas.getItems();
        if(ol.size()>= 1){ // si la lista no de venta no esta vacia.
            LinkedList<ItemVenta> lit = new LinkedList<>();
            ol.iterator().forEachRemaining((x) -> { lit.add(x); });
            
            String lv_json = new Gson().toJson(lit);
            System.out.println(lv_json);
            String post_ans;
            try {
                RequestBody reqBody = new FormBody.Builder().add("lista_compra", lv_json).build();
                Request req = new Request.Builder().url("http://localhost:3000/imprimir_remi").post(reqBody).build();
                OkHttpClient client = new OkHttpClient();
                Call call = client.newCall(req);
                try (Response res = call.execute()) { post_ans = res.body().string();}
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public void RegistrarVenta(){
        ObservableList<ItemVenta> lit = TV_Ventas.getItems();
        if(!lit.isEmpty()){
            ArrayList<ItemVenta> alit = new ArrayList<>();
            lit.iterator().forEachRemaining((x) -> { alit.add(x); });
            dbMapper dbm = new dbMapper();
            Venta v = new Venta(alit, dbm.now(), "123");
            boolean registro_exitoso = dbm.InsertVenta(v);
            if(registro_exitoso){
                TV_Ventas.getItems().clear();
                String toastMsg = "Venta Registrada";
                int toastMsgTime = 2000; //3.5 seconds
                int fadeInTime = 500; //0.5 seconds
                int fadeOutTime= 500; //0.5 seconds
                Stage st = (Stage) TF_BuscarProducto.getScene().getWindow();
                RefreshSuma(); // debe quedar 0
                Toast.makeText(st, toastMsg, toastMsgTime, fadeInTime, fadeOutTime);
            }
        }
    }
    
    public void BuscarProducto(){
        dbMapper dbm = new dbMapper();
        ArrayList<Producto> lista = new ArrayList<>();
        
        String selected = (String) CB_OpcionesB.getSelectionModel().getSelectedItem();
        String b = TF_BuscarProducto.getText();
        
        switch(selected){
            case B_CODIGO_EXACT:
                lista = dbm.GetProductById(b);
                if(!lista.isEmpty()) AddItemVenta(lista.get(0));
                break;
            case B_DESCRI:
                lista = dbm.GetByDescripcion(b);
                break;
            case B_LAST_CODE:
                lista = dbm.getPrdByLastCod(b);
                break;
        }
        
        TV_Busqueda.getItems().setAll(lista);
    }
    
    /**
     * coloca el precio del producto en la tabla de ventas
     * segun el estado del toggleButton.
     * @param p 
     */
    public void AddItemVenta(Producto p){
        if(ToggButton_TipoVenta.getText().equals(this.STR_VENTA_MAYOR)){
            TV_Ventas.getItems().add(new ItemVenta(p, 1, p.pv_mayor, false));
        }
        else{
            TV_Ventas.getItems().add(new ItemVenta(p, 1, p.pv_publico, false));
        }
        // si el checkbox esta seleccionado se unifican los items con el mismo _id
        if(CHBox_Unificar.isSelected()) UnificarItemsVenta();
        RefreshSuma();
    }
    
    public void RemoverItemVenta(ItemVenta it2rem){
        TV_Ventas.getItems().remove(it2rem);
        RefreshSuma();
    }
    
    
    /**
     * para usar cuando se va a pasar el producto seleccionado de la tabla de 
     * busqueda a la tabla de venta.
     */
    public void TVBuscar2TV_Venta(){
        if(!TV_Busqueda.getSelectionModel().isEmpty()){
            AddItemVenta(TV_Busqueda.getSelectionModel().getSelectedItem());            
        }
    }
    
    public void RefreshSuma(){
        this.Suma = 0;
        for(ItemVenta x: TV_Ventas.getItems()){
            Suma += x.getSubTotal();
        }
        L_Total.setText(Integer.toString(this.Suma));
    }
    
    
    /**
     * se abre la ventana modal para modificar un item venta
     * @param event
     * @param p
     * @throws IOException 
     */
    public void abrirNegociarDialog(MouseEvent event, ItemVenta iv) throws IOException{
        // getClass.GetResource va a apuntar a la carpeta de resources/producto
        FXMLLoader cargador = new FXMLLoader(getClass().getResource(Locations.pop_up_negociar_fxml));
        Parent root = cargador.load(); // se usa el fxml para cargar tanto la gui como el controlador del dialogo de
        Stage st = new Stage();// modificacion
        st.setScene(new Scene(root));
        st.initModality(Modality.WINDOW_MODAL); // se fuerza el focus al dialogo de modificacion
        st.initOwner( ((Node) event.getSource()).getScene().getWindow());
        PopUp_NegociarController npcont = cargador.<PopUp_NegociarController>getController(); // se obtiene el controlador
        npcont.setItemVenta(iv, this);// se usa esta instancia del controlador para enviar el producto seleccionado
        st.show(); // se muestra la ventana
    }
    
    /**
     * debe ser usado por el Dialogo de negociacion o modificacion de itemVenta
     * para pasar los cambios devuelta a esta pantalla.
     * @param iv 
     */
    public void UpdateItemVenta(ItemVenta iv){
        int idx = TV_Ventas.getSelectionModel().getSelectedIndex();
        TV_Ventas.getItems().set(idx, iv);
        RefreshSuma();
        TV_Ventas.refresh();
    }
    
    /**
     * metodo que barre toda los items de la tabla de venta para buscar 
     * productos con codigo repetido para juntarlos en un mismo item sumando
     * las cantidades.
     */
    public void UnificarItemsVenta(){
        
        ObservableList<ItemVenta> ol = TV_Ventas.getItems(); // old list: ol
        ObservableList<ItemVenta> ul = FXCollections.observableArrayList(); // unified list: ul
        
        for(ItemVenta x : ol){
            boolean in_list = false;
            for(ItemVenta y : ul){
                if(y.p._id.equals(x.p._id)){
                    in_list=true;
                    y.Add2Cantidad(x.getCantidad());
                }
            }
            if(!in_list){
                ul.add(x);
            }
        }
        TV_Ventas.setItems(ul);
        TV_Ventas.refresh();
    }
    
}
