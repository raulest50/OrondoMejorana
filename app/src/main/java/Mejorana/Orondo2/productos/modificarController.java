package Mejorana.Orondo2.productos;

import Mejorana.Orondo2.OrondoDb.Producto;
import Mejorana.Orondo2.OrondoDb.dbMapper;
import Mejorana.Orondo2.inicio.GenericDialogs;
import Mejorana.Orondo2.inicio.Locations;
import java.io.IOException;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Raul Alzate
 */
public class modificarController {
    
    
    @FXML
    Button Button_Buscar;
    
    @FXML
    TextField TF_Buscar;
    
    @FXML
    ComboBox CB_OpcionesB;
    
    
    @FXML
    TableView<Producto> TV_Productos;
    
    @FXML
    TableColumn<String, Producto> TC_Codigo;
    
    @FXML
    TableColumn<String, Producto> TC_Descripcion;
    
    @FXML
    TableColumn<Integer, Producto> TC_Costo;
    
    @FXML
    TableColumn<Integer, Producto> TC_xMayor;
    
    @FXML
    TableColumn<Integer, Producto> TC_pvPublico;
    
    @FXML
    TableColumn<String, Producto> TC_LastUp;
    
    @FXML
    TableColumn<Double, Producto> TC_Iva;
    
    ObservableList<String> itemsComboB = FXCollections.observableArrayList();
    
    //para busqueda por codigo exacto
    public final String B_CODIGO_EXACT = "Codigo Exacto";
    
    // busqueda por coincidencias parciaes en la descripcion
    public final String B_DESCRI = "Descripcion";
    
    // ultimos del codigo. muy util para buscar codigos largos cuando no se tiene 
    // lector de codigo de barras
    public final String B_LAST_CODE = "Ultimos del Codigo";
    
    public final String B_KEYWORDS = "Palabras Clave";
    
    public void initialize() {
        
        TC_Codigo.setCellValueFactory(new PropertyValueFactory<>("id"));
        TC_Descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        TC_Costo.setCellValueFactory(new PropertyValueFactory<>("costo"));
        TC_pvPublico.setCellValueFactory(new PropertyValueFactory<>("pv_publico"));
        TC_xMayor.setCellValueFactory(new PropertyValueFactory<>("pv_mayor"));
        TC_LastUp.setCellValueFactory(new PropertyValueFactory<>("last_updt"));
        TC_Iva.setCellValueFactory(new PropertyValueFactory<>("iva"));
        
        
        itemsComboB.addAll(this.B_DESCRI, this.B_CODIGO_EXACT, this.B_LAST_CODE, this.B_KEYWORDS);
        CB_OpcionesB.setItems(itemsComboB);
        CB_OpcionesB.getSelectionModel().selectFirst();
        
        
        // cuando se hace doble click sobre un producto, sale un cuadro
        // informativo con los keywords. Los keywords deberian contener
        // para que sirve el producto como se debe consumir y si tiene
        // contraindicaciones etc, cualquier informacion relevante del producto
        // tomado de https://stackoverflow.com/questions/26563390/detect-doubleclick-on-row-of-tableview-javafx
        TV_Productos.setRowFactory(tv ->{
            TableRow<Producto> row = new TableRow<>();
            row.setOnMouseClicked(event ->{
                if(event.getClickCount() == 2 && !row.isEmpty() ){
                    Producto p = row.getItem();
                    GenericDialogs.Info(p.descripcion,
                            "ultima actulizacion :" + p.getLast_updt(),
                            p.keywords);
                }
            });
            return row;
        });
    }
    
    
    @FXML
    public void onAction_Button_Buscar(ActionEvent event){
        this.BuscarProducto();
    }
    
    @FXML
    public void onAction_TF_Buscar(ActionEvent event){
        this.BuscarProducto();
    }
    
    public void BuscarProducto(){
        dbMapper dbm = new dbMapper();
        ArrayList<Producto> lista = new ArrayList<>();
        
        String selected = (String) CB_OpcionesB.getSelectionModel().getSelectedItem();
        String b = TF_Buscar.getText();
        
        switch(selected){
            case B_CODIGO_EXACT -> lista = dbm.GetProductById(b);
            case B_DESCRI -> lista = dbm.GetByDescripcion(b);
            case B_LAST_CODE -> lista = dbm.getPrdByLastCod(b);
            case B_KEYWORDS -> lista = dbm.getPrdByKeyWords(b);
        }
        
        TV_Productos.getItems().setAll(lista);
        TF_Buscar.requestFocus();
        if(selected.equals(B_CODIGO_EXACT)) TF_Buscar.selectAll();
    }
    
    @FXML // se abre una ventana modal para modifcar el producto seleccionado
    public void onAction_ButtonModificar(ActionEvent event) throws IOException{
        Producto sp = TV_Productos.getSelectionModel().getSelectedItem(); // se obtiene el item seleccionado
        
        if(sp == null) GenericDialogs.Info("Informacion",
                "No ha seleccionado ningun producto", 
                """
                Primero debe seleccionar un producto de la tabla.
                Cuando un producto esta seleccionado toda su fila se resalta""");
        
        else abrirModDialog(event, sp);
    }
    
    public void abrirModDialog(ActionEvent event,  Producto p) throws IOException{
        // getClass.GetResource va a apuntar a la carpeta de resources/producto
        FXMLLoader cargador = new FXMLLoader(getClass().getResource(Locations.modificar_dialog_fxml));
        Parent root = cargador.load(); // se usa el fxml para cargar tanto la gui como el controlador del dialogo de
        Stage st = new Stage();// modificacion
        st.setScene(new Scene(root));
        st.initModality(Modality.WINDOW_MODAL); // se fuerza el focus al dialogo de modificacion
        st.initOwner( ((Node) event.getSource()).getScene().getWindow());
        modificarDialogController mdc = cargador.<modificarDialogController>getController(); // se obtiene el controlador
        mdc.setProducto(p, this);// se usa esta instancia del controlador para enviar el producto seleccionado
        st.show(); // se muestra la ventana
    }
}
