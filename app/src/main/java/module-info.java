/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/module-info.java to edit this template
 */

module Orondo2App {
    
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    
    
    // ikonli. para usar fontawesome en javaFX
    //requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.metrizeicons;
    requires org.kordamp.ikonli.entypo;
    requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.openiconic;
    
    // CONECTOR MONGO DB
    requires json.simple;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    
    requires com.google.gson; // best suited than simplejson for certain tasks
    
    //The standard Java libraries fail to provide enough methods for manipulation of its core classes.
    //Apache Commons Lang provides these extra methods.
    requires org.apache.commons.lang3;
    
    requires java.prefs; // java preferences
    requires java.logging;
    
    
    //requires org.eclipse.paho.client.mqttv3; // mqtt client only
    //uses org.eclipse.paho.client.mqttv3.spi.NetworkModuleFactory;
    
    requires nv.websocket.client; // websocket server-client
    
    
    requires jdk.net;
    
    requires java.net.http;
    requires okhttp3;
    
    //requires better.strings; // para poder hacer string interpolation en java
    
    
    
    
    // para evitar el erorr
    //accessible: module Orondo2App does not "opens Mejorana.Orondo2.inicio" to module javafx.fxml
    opens Mejorana.Orondo2.inicio to javafx.fxml;
    opens Mejorana.Orondo2.productos to javafx.fxml;
    opens Mejorana.Orondo2.ventas to javafx.fxml;
    
    opens Mejorana.Orondo2.OrondoDb to com.google.gson;
    
    exports Mejorana.Orondo2.inicio;
    
    // si no se colocan estos exports sale el error:
    // org.bson.codecs.configuration.CodecConfigurationException: Can't find a codec for class ... producto
    exports Mejorana.Orondo2.productos;
    exports Mejorana.Orondo2.OrondoDb;
    exports Mejorana.Orondo2.Styling;
    
}
