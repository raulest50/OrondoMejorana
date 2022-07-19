# Orondo - Adaptacion para Mejorana

## SetUp For Development

Guia de instalacion para desarrollador.

### Windows 10

install openJDK 17

se copia la carpeta a Archivos de programa/ Java
en el path se agrega la ruta al bin
y el JAVA_HOME se deja antes del bin

usar la ultima de java 17.

install Netbeans using msi installer
https://netbeans.apache.org/download/index.html

una vez esta instalado el openJDK y el path esta correctamente configurado
entonces se deja instalar netbeans.

install Scene Builder from Gluon Mobile
https://gluonhq.com/products/scene-builder/

![SceneBuilder setup](/readme_images/SceneBuilder_libs.png?raw=true "Scene Builder SetUp")

Se deben instalar las librerias:
    'org.kordamp.ikonli:ikonli-core:11.3.4'
    'org.kordamp.ikonli:ikonli-javafx:11.3.4'
    'org.kordamp.ikonli:ikonli-fontawesome5-pack:11.3.4'
    'org.kordamp.ikonli:ikonli-metrizeicons-pack:11.3.4'
    'org.kordamp.ikonli:ikonli-entypo-pack:11.3.4'
    'org.kordamp.ikonli:ikonli-openiconic-pack:11.5.0'

si no aparecen como busqueda automatica entonces se agregan manualmente.
para que scene builder pueda abrir los FXML del proyecto.

Se instala mongodb community con un .msi y se instala como servicio.

### Ubuntu 



# App Deployment Guide

guia de instalacion para usuario final:

1. se instala mongodb community 5.0 como un servicio de windows usando
el .msi de la pagina oficial.

2. se copia el app-image generado por jlink y se crea el acceso directo en el 
escritorio al app launcher que se encuentra en la carpta bin.
