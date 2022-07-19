package Mejorana.Orondo2.Styling;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * 
 * @author Raul Alzate
 */
public class Styler {
    
    /**
     * Agrega una transicion de opacidad a un VBox.
     * Meramente visual, no afecta la logica.
     * @param v 
     */
    public static void AddFadingAnimation_onHover(VBox v){
        FadeTransition ft = new FadeTransition();
        ft.setNode(v);
        ft.setDuration(new Duration(100));
        ft.setFromValue(1.0);
        ft.setToValue(0.8);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        v.setOnMouseEntered((MouseEvent me) -> ft.play());
    }
    
    /**
     * configura un textfield para que acepte solo numeros y un punto
     * osea solo numeros de hasta 7 digitos la parte entera y hasta 4 
     * digitos la parte decimal. ver mas sobre java regex en:
     * https://www.w3schools.com/java/java_regex.asp
     * @param textField 
     */
    public static void SetTextFieldAsNumericRational(TextField textField){
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    textField.setText(oldValue);
                }
            }
        });
    }
    
    /**
     * Configura un textfield para que solo acepte hasta 7 digitos de 0-9 sin
     * punto decimal
     * @param textField 
     */
    public static void SetTextFieldAsNumericInt(TextField textField){
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,10}")) {
                    textField.setText(oldValue);
                }
            }
        });
    }
}

/*
TextField_Costo.lengthProperty().addListener(new ChangeListener<Number>(){
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		  if (newValue.intValue() > oldValue.intValue()) {
			  char ch = TextField_Costo.getText().charAt(oldValue.intValue());
			  // Check if the new character is the number or other's
			  if (!(ch >= '0' && ch <= '9' )) {
				   // if it's not number then just setText to previous one
				   TextField_Costo.setText(TextField_Costo.getText().substring(0,TextField_Costo.getText().length()-1)); 
			  }
                  }
        }
        });
    }
*/

