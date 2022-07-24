package Mejorana.Orondo2.productos;

import Mejorana.Orondo2.OrondoDb.Producto;
import Mejorana.Orondo2.OrondoDb.Validador;
import Mejorana.Orondo2.OrondoDb.dbMapper;
import Mejorana.Orondo2.Styling.Styler;
import Mejorana.Orondo2.inicio.GenericDialogs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author Raul Alzate
 */
public class codificarController {
    
    @FXML
    public TextField TextField_Codigo;
    
    @FXML
    public ComboBox CBox_iva;
    
    @FXML
    public TextField TextField_Descripcion;

    @FXML
    public TextField TextField_StockInit;

    @FXML
    public TextField TextField_Costo;

    @FXML
    public TextField TextField_PvMayor;

    @FXML
    public TextField TextField_PvPublico;
    
    @FXML 
    public TextArea TextArea_keywords;

    @FXML
    public Button Button_Codificar;

    @FXML
    public Button Button_Borrar;
    
    @FXML 
    public CheckBox CheckB_fraccionable;
    
    @FXML
    public CheckBox CHBox_Hold;
    
    @FXML
    public TextField TextField_PesoUnitario;
    
    /**
     * para importar productos desde un .json file. el boton incia con
     * visible = false. para hacerlo visible se debe escribir importar en
     * el text area keywords la palabra "importar" y luego hacer click derecho
     * en el boton codificar. ver metodo onClickButtonCodificar.
     */
    @FXML
    public Button Button_Importar;
    
    
    private ObservableList<String> itemsCMBox_grupo = FXCollections.observableArrayList();
    
    @FXML
    public ComboBox<String> CMBox_grupo;
    
    public final String IVA_0="0%", IVA_5="5%", IVA_19="19%";
    
    
    ObservableList<String> itemsCBox_iva = FXCollections.observableArrayList();
    
    
    public void initialize() {
        // se configuran estos textFields para que solo reciban numeros
        Styler.SetTextFieldAsNumericInt(TextField_Costo);
        Styler.SetTextFieldAsNumericInt(TextField_PvMayor);
        Styler.SetTextFieldAsNumericInt(TextField_PvPublico);
        Styler.SetTextFieldAsNumericInt(TextField_StockInit);
        
        Styler.SetTextFieldAsNumericInt(TextField_PesoUnitario);
        
        itemsCBox_iva.addAll(IVA_0, IVA_5, IVA_19);
        CBox_iva.setItems(itemsCBox_iva);
        CBox_iva.getSelectionModel().selectFirst();
        
        TextField_PesoUnitario.setDisable(true);
        // cada que hay un cambio en el boolean selected del checkbox, se activa este metodo.
        CheckB_fraccionable.selectedProperty().addListener((Observable, oldValue, newValue)->{
            if(newValue){
                TextField_PesoUnitario.setDisable(false);
                TextField_PesoUnitario.requestFocus();
            } else{
                TextField_PesoUnitario.setDisable(true);
                TextField_PesoUnitario.setText("");
            }
        });
        itemsCMBox_grupo.addAll(Producto.NORMAL, Producto.STK_PRIOR);
        CMBox_grupo.setItems(itemsCMBox_grupo);
        CMBox_grupo.getSelectionModel().selectFirst();
        CMBox_grupo.setVisible(false);
    }
    
    
    
    @FXML
    void onClickButtonCodificar(MouseEvent event) {
        // funcion secretea para importar productos a la base de datos
        if(event.getButton() == MouseButton.SECONDARY){
            if(TextArea_keywords.getText().equals("importar")){
                Button_Importar.setVisible(true);
                TextArea_keywords.setText("");
            }
            if(TextArea_keywords.getText().equals("agrupar")){
                CMBox_grupo.setVisible(true);
                TextArea_keywords.setText("");
            }
        } else{ // en el caso normal se lee los text fields
            try{ // y se ingresa el producto a mongo si pasa las validaciones
                CodificarProducto();// se paso el codigo a una funcion 
            } catch(NumberFormatException e){ // para mayor legibilidad
                GenericDialogs.Info("No se Puede codificar", "", "los campos numericos de costo, precio por mayor\n"
                        + "y precio de venta al publico no pueden quedar vacios. Stock inicial si puede quedar vacio\n"
                        + "ya que se asume como cero en ese caso.");
            }
        }
    }
    
    @FXML
    void onClickButtonImportar(MouseEvent event){
        // se lee el archivo .json
        FileChooser fileChooser = new FileChooser();
        File jsonFile = fileChooser.showOpenDialog((Stage) Button_Importar.getScene().getWindow());
        
        // dentro del try se toma cada uno de los records del archivo .json y
        // se insertan en la base de datos de productos
        
        JSONParser jsonParser = new JSONParser();
        
        // try with resources. el resourse es reader, y esto indica que se debe 
        // de cerrar despues de terminar el bloque try catch
        try (FileReader reader = new FileReader(jsonFile)){
            
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONArray listaProductosJson = (JSONArray) obj;
            
            ArrayList<Producto> lp = new ArrayList<>();
            //Iterate over employee array
            
            /**
             * este codigo es especifico para importar de la bd de sql (anterior
             * varsion del programa) a la nueva version con mongo. los key del 
             * json no son los mismos, este codigo toca dejarlo quieto o hacer una
             * version mas compleja y generalizada de importacion en la que se use
             * interfaz grafica para definir los keys y asi lograr una importacion
             * generalizada a la bd. por lo pronto dejar quieto.
             */
            listaProductosJson.forEach(jsonObj -> {
                JSONObject pjson = (JSONObject) jsonObj; // producto en formato json
                
                String codigo = (String) pjson.get("codigo");
                String descripcion = (String) pjson.get("descripcion");
                int costo = ((Double) pjson.get("costo")).intValue();
                int pvmayor = ((Double) pjson.get("pvtienda")).intValue();
                int pvpublico = ((Double) pjson.get("pvpublico")).intValue();
                Double iva = (Double) pjson.get("iva");
                String last_updt = (String) pjson.get("ultima_actualizacion");
                String keywords = (String) pjson.get("Familia");
                
                Producto p = new Producto(codigo, descripcion, costo, pvmayor, pvpublico, iva, last_updt, keywords);
                lp.add(p);
                });
            
            dbMapper db = new dbMapper();
            db.InsertManyProductos(lp);
            
 
        } catch (FileNotFoundException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        } catch (IOException | ParseException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
    }
    
    //los siguientes metodos permiten pasar entre los textfield al pulsar enter
    @FXML
    void onActionTF_Codigo(ActionEvent event){
        TextField_Descripcion.requestFocus();
    }
    @FXML
    void onActionTF_Descripcion(ActionEvent event){
        TextField_Costo.requestFocus();
    }
    @FXML
    void onActionTF_Costo(ActionEvent event){
        TextField_PvMayor.requestFocus();
    }
    @FXML
    void onActionTF_PVxMayor(ActionEvent event){
        TextField_PvPublico.requestFocus();
    }
    @FXML
    void onActionTF_PVPublico(ActionEvent event){
        CBox_iva.requestFocus();
    }
    @FXML
    void onKeyPress_CBox_Iva(KeyEvent event){
        if(event.getCode().equals(KeyCode.ENTER)) TextField_StockInit.requestFocus();
    }
    @FXML
    void onActionTF_StockInit(ActionEvent event){
        TextArea_keywords.requestFocus();
    }
    
    @FXML
    void onAction_TF_PesoUnitario(ActionEvent event){
        TextField_StockInit.requestFocus();
    }
    
    @FXML
    void onActionB_Borrar(ActionEvent event){
        ClearTextFields();
    }
    
    public void CodificarProducto(){
        dbMapper db = new dbMapper();
        
        String codigo = TextField_Codigo.getText();
        String descripcion = TextField_Descripcion.getText();
        int costo = Integer.parseInt(TextField_Costo.getText());
        int pvmayor = Integer.parseInt(TextField_PvMayor.getText());
        int pvpublico = Integer.parseInt(TextField_PvPublico.getText());
        
        Double stock;// si stock se deja vacio se asume 0
        String strStock = TextField_StockInit.getText();
        if(strStock.isBlank() || strStock.isEmpty()) stock = 0.0;
        else stock = Double.parseDouble(TextField_StockInit.getText());
        
        String iva_s = CBox_iva.getSelectionModel().getSelectedItem().toString();
        iva_s = iva_s.split("%")[0];// se remueve el porcentaje del string para hacer parsing directamente
        
        //if(iva_s.equals("")) iva_s = "0"; // Texfield iva en blanco se asume como 0%
        Double iva = Double.parseDouble(iva_s);
        
        String last_updt = db.now();
        String keywords = TextArea_keywords.getText();
        
        boolean fraccionable = CheckB_fraccionable.isSelected();
        
        int PesoUnitario;
        String PesoUnitario_s = TextField_PesoUnitario.getText();
        if(fraccionable) PesoUnitario = Integer.parseInt(PesoUnitario_s);
        else PesoUnitario = 1; // 1 seria equivalente a no fraccionable
        
        String grupo = CMBox_grupo.getSelectionModel().getSelectedItem();
        
        Producto p = new Producto(codigo, descripcion, costo, pvmayor, pvpublico, iva, last_updt, keywords,
                fraccionable, PesoUnitario, grupo);
        
        // se verifica la validez de los datos y se procede a insertar en bd
        // o indicar al usuario que campos se deben corregir
        ArrayList valrel = Validador.CodificacionValida(p);
        if((boolean) valrel.get(0)){
            db.SaveProduct(p);
            GenericDialogs.Info("Codificacion", "Operacion Exitosa :)",
                    "El producto ha sido ingresado exitosamente\n"
                            + "recuerde que en caso de dejar el campo de iva y/o stock vacios, \n"
                            + "el sistema asume 0 en ambos casos");
            if(CHBox_Hold.isSelected()){
                TextField_Codigo.setText("");
                TextField_Codigo.requestFocus();
            }
            else ClearTextFields();
        }
        else GenericDialogs.Info("No se puede codificar",
                "Hay que hacer correcciones a los datos introducidos,\n"
                        + " a continuacion se muestra que se debe corregir",
                (String) valrel.get(1));
    }
    
    public void ClearTextFields(){
        TextField_Codigo.setText("");
        TextField_Descripcion.setText("");
        TextField_Costo.setText("");
        TextField_PvMayor.setText("");
        TextField_PvPublico.setText("");
        
        TextField_StockInit.setText("");
        CMBox_grupo.setVisible(false);
        Button_Importar.setVisible(false);
    }
}
