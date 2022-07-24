package Mejorana.Orondo2.OrondoDb;



import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.TransactionBody;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import java.util.Iterator;


import org.bson.Document;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;



/**
 *
 * @author Raul Alzate
 * 
 * Inicialmente se implemento con morphia pero no dio buena impresion ya que se
 * seguian los pasos documentacion para la ultima vercion y mostraba el uso de
 * funciones ya deprecated y para acabar de completar no funcionaban tal cual.
 * la incongruencia sumado a lo complejo de morphia llevo al uso de jongo que
 * es notablemente mas simple. jongo parecia ser perfecto ya que las operaciones
 * se parecen mucho a como se hacen en la shell, incluso jongo para mi merece 5
 * estrellas. sin embargo sebe usarse el conector de mongo para java version 3
 * y por tanto no se puede hacer uso de transacciones. este pequeño detalle
 * lo arruino todo. las transacciones en resumen es cuando se modifican multiples
 * documentos se requiere que se modifiquen todos o no se modifique ninguno para
 * garantizar la consistencia de los datos. Ademas despues de la version 4 el
 * conector de mongo para java ya soporta el uso directo de pojos. aunque seria
 * un condigo mucho mas hermoso con jongo, por la imperiosa necesidad se debe 
 * usar el conector directamente por las multitransactions. Esta caracteristica
 * es critica en la generacion de la facturacion para garantizar la altisima
 * robustez del programa.
 */
public class dbMapper {
    
    // CONSTANTES
    private final String DBNAME = "Retail";
    
    private final String CLN_PRODUCTOS = "productos";
    
    private final String CLN_VENTAS = "ventas";
    
    private final String CLN_CLIENTES = "clientes";
    
    private final String CLN_RANGOS_FAC = "rangofacturacion";
    
    /**
     * en rangos de facturacion el doc con _id consecutivo, guarda en valor
     * el numero de consecutivo actual, para la generacion de un documento de
     * venta se debe usar ese numero +1.
     */
    public final String consecutivoKey = "consecutivo"; 
    public final String valorKey = "valor";
    
    public final String Key_id = "_id"; 
    
    
    // MOGODB CONECTOR OBJECTS
    private final MongoDatabase db;
    
    private final MongoClient mongo_client;
    
    private final MongoClientSettings settings;
    
    private final CodecRegistry pojoCodecRegistry;
    
    /**
     * Hay 2 caracteristicas muy importantes como se explica arriba.
     * 1. transactions
     * 2. el uso de pojos
     * para 2. se requiere pojoCodecRegistry y settings, entonces hay que
     * garantizar la inicializacion de las variables para que se puedan usar 
     * pojos.
     */
    public dbMapper(){
        this.pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        
        this.settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .build();
        
        this.mongo_client = MongoClients.create(settings);
        
        
        this.db = this.mongo_client.getDatabase(this.DBNAME);
    }
    
    
    private MongoCollection<Producto> getProductosCollection(){
        MongoCollection<Producto> cln = db.getCollection(this.CLN_PRODUCTOS, Producto.class);
        return cln;
    }
    
    private MongoCollection<Venta> getVentaCollection(){
        MongoCollection<Venta> cln = db.getCollection(this.CLN_VENTAS, Venta.class);
        return cln;
    }
    
    private MongoCollection getRangoFCollection(){
        return db.getCollection(this.CLN_RANGOS_FAC);
        
    }
    
    
    // ################ Productos  ################
    
    public void SaveProduct(Producto p){
        getProductosCollection().insertOne(p);
    }
    
    /**
     * para usar principalmente en importacion de productos.json
     * @param lp 
     */
    public void InsertManyProductos(ArrayList<Producto> lp){
        getProductosCollection().insertMany(lp);
    }
    
    /**
     * si al producto no se modifico el atributo _id o tambien llamado codigo,
     * entonces el codigo anterior y actual es el mismo, es decir, que en
     * el argumento de esta funcion se podria colocar (Producto p, String p._id).
     * En caso contrario el metodo eliminara el record con el codigo anterior
     * y hara un save del producto (que tiene el codigo nuevo encapsulado).
     * @param p producto que se desea actualizar
     * @param _id codigo anterior
     */
    public void UpdateProducto(Producto p, String _id){
        
        MongoCollection<Producto> cln = getProductosCollection();
        // _id anterior y nuevo iguales
        if(_id.equals(p._id)) cln.replaceOne(eq("_id", _id), p);
        else{ // _id anterior y nuevo diferentes
            InsertOneResult ior = cln.insertOne(p); // se inserta el p con el nuevo codigo
            cln.deleteOne(eq("_id", _id)); // se borra el anterior p con el vijo codigo
        }
    }
    
    public void EliminarProducto(String _id){
        MongoCollection cln = getProductosCollection();
        cln.deleteOne(eq("_id", _id));
    }
    
    /**
     * trae todos los productos de la base de datos.
     * @return 
     */
    public ArrayList<Producto> GetAllProducts(){
        Iterator<Producto> it = getProductosCollection().find().iterator();
        ArrayList<Producto> li = new ArrayList();
        it.forEachRemaining( (x)-> { li.add(x); });
        return li;
    }
    
    
    /**
     * la busqueda por codigo exacto solo puede retornar un producto o ninguno.
     * sin embargo se retorna en un arraylist para usar el size, si es 0
     * es porque el producto no se encontro.
     * https://beginnersbook.com/2017/10/java-string-format-method/
     * https://dzone.com/articles/java-string-format-examples
     * @param _id
     * @return 
     */
    public ArrayList<Producto> GetProductById(String _id){
        Iterator<Producto> it = getProductosCollection().find(eq("_id", _id)).iterator();
        ArrayList<Producto> li = new ArrayList();
        it.forEachRemaining((x)->{ li.add(x); });
        return li;
    }
    
    
    /**
     * from https://docs.mongodb.com/manual/reference/operator/query/all/
     * The $all operator selects the documents where the value of a field is an 
     * array that contains all the specified elements.
     * 
     * metodo que devuelve los documentos que su descripcion contienen todas las
     * palabras separadas por whitespace " " en el String descri
     * 
     * mejoro mucho el codigo al usar directamente el driver en lugar de jongo
     * 
     * @param descri
     * @return 
     */
    public ArrayList<Producto> GetByDescripcion(String descri){
        ArrayList<Bson> lf = new ArrayList(); // lista de filtros
        String[] kw = descri.split(" "); // para cada elemento del split se crea un filtro
        for(String x : kw){
            lf.add(regex("descripcion", ".*${x}.*", "i")); // el filtro es un elemento regex
        } // que comia el "like % %" del sql
        Iterator<Producto> it = getProductosCollection().find(and(lf)).iterator();
        ArrayList<Producto> lp = new ArrayList();
        it.forEachRemaining((x)->{ lp.add(x); }); // se pasan los elemntos del iterador a un arraylist
        return lp;
    }
    
    /**
     * encuentra los productos cuyo codigo de barras termina en
     * los digitos especificados por el sting b
     * @return 
     */
    public ArrayList<Producto> getPrdByLastCod(String b){
        Iterator<Producto> it = getProductosCollection().find(regex("_id",".*${b}$","i")).iterator();
        //String q = "{_id:{$regex:'.*%1s$'}}";//q = String.format(q, b);
        ArrayList<Producto> lp = new ArrayList();
        it.forEachRemaining((x)-> { lp.add(x); } );
        return lp;
    }
    
    /**
     * aun por implementar.trae de la base de datos las ventas entre 2 momentos de tiempo
     * especificado. 
     * En find() debe ir una implementacion de BSon interface. En este caso
     * fue mas comodo y rapido usar BasicDBObject pero, aunque no deprecated,
     * no se recomienda para nuevas implementaciones.
     * 
     * 
     * Bson filter = and(gte("fecha", d1), lte("fecha", d1));
     * 
     * db.getCollection('ventas').find(
     * {"$and":[{"fecha":{"$gte":"2021-08-24 00:00:00"}}, {"fecha":{"$lt":"2021-08-24 23:59:59"}}]}
     * )
     * 
     * @param d
     * @return 
     */
    public ArrayList<Venta> getVentas(LocalDateTime[] d){
        ArrayList<Venta> lv = new ArrayList<>();
        
        //Bson filter = and(gte("fecha", d1), lte("fecha", d1));
        BasicDBObject query = new BasicDBObject("fecha", //
                      new BasicDBObject("$gte", d[0]).append("$lt", d[1]));
        
        //Iterator<Venta> it = getVentaCollection().find(query).iterator();
        //it.forEachRemaining((x) -> { lv.add(x); });
        
        System.out.println("**********************");
        System.out.println(lv.size());
        
        return lv;
    }
    
    //eq("fecha", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
    
    /**
     * crea un respaldo de la base de datos como archivo .json
     */
    public void BackupJson(String dirfol){
        
        ArrayList<Producto> all = this.GetAllProducts();
    }
    
    /**
     * timestamp truncado
     * @return 
     */
    public String now(){
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString().replace("T", " ");
    }
    
    public String nowDay(){
        return LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).toString().split("T")[0];
    }
    
    // agregar un arrancador de la base de datos mongo y un ping para mongodb
    // si ocurre un ConectionRefusedException que orondo arranque mongod
    // de manera automatica
    
    
    // ################ Ventas
    
    
    /**
     * debe asignar el id a la venta ya que este debe ser un concecutivo
     * segun lo dispuesto por la dian para facturacion POS.al hacer las operaciones dentro de una transacion nos aseguramos
     * de que el consecutivo se avance +1 solo si realmente se inserto
     * el documento de venta, asegurando consistencia de los datos.
     * @param v
     * @return 
     */
    public boolean InsertVenta(Venta v){
        boolean op_exitosa = false;
        final ClientSession clientSession = mongo_client.startSession();
        
        TransactionOptions txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        
        /* Step 3: Define the sequence of operations to perform inside the transactions. */
        TransactionBody txnBody = new TransactionBody<String>() {
            @Override
            public String execute() {
                String r = "false";
                MongoCollection ventas = getVentaCollection();
                MongoCollection rangos = getRangoFCollection();
                
                // se lee el consecutivo actual, se incrementa +1 y se asigna al doc de venta
                Document cstvo = (Document) rangos.find(eq("_id", consecutivoKey)).first();
                String next_ctvo = Integer.toString(1 + Integer.parseInt(cstvo.getString("valor")));
                v._id = next_ctvo;
                // se guarda el doc de venta y se actualiza el doc consecutivo
                InsertOneResult ior = ventas.insertOne(clientSession, v);
                
                UpdateResult upr = rangos.updateOne(clientSession, eq("_id", consecutivoKey),
                        new Document("$set", new Document(valorKey, next_ctvo)));
                
                if(ior.wasAcknowledged() && upr.wasAcknowledged()) r = "true";
                
                return r;
            }
        };
        try {
            /*
            Step 4: Use .withTransaction() to start a transaction,
            execute the callback, and commit (or abort on error).
            */
            String op_result = (String) clientSession.withTransaction(txnBody, txnOptions);
            op_exitosa = Boolean.parseBoolean(op_result);
        } catch (RuntimeException e) {
            // some error handling
            System.out.println("##############"+e.getLocalizedMessage());
        } finally {
            clientSession.close();
        }
        return op_exitosa;
    }
    
    /**
     * verifica que existan las colecciones en mongo db que esta aplicacion usa
     */
    public void InitMongo(){
        
        MongoCollection<Document> cln_f = getRangoFCollection();
        
        Iterator<Document> it = cln_f.find(eq(Key_id, consecutivoKey)).iterator();
        
        if(!it.hasNext()){
            System.out.println("Creando colleccion de rangos de facturacion...");
            Document consecutivo = new Document(Key_id, consecutivoKey)
                    .append("valor", "0");
            cln_f.insertOne(consecutivo);
            System.out.println("Rango de facturacion inicializado");
        } else{
            System.out.println("El concecutivo de facturacion ya se inicializo");
        }
        
    }
}

/**
 * Mongodb ejemplos de comandos.
 * db.productos.find({$and:[{descripcion:{$regex:/darnel/, $options:"i"}}, {descripcion:{$regex:/vaso/, $options:"i"}}]})
 * db.productos.countDocuments({})
 * show dbs
 * use Retail
 */
