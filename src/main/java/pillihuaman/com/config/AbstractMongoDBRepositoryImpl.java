package pillihuaman.com.config;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

public abstract class AbstractMongoDBRepositoryImpl<T> implements BaseMongoRepository<T> {

	protected static String DS_WRITE;
	@Autowired
	private MongoDBConfig mongoDBConfig;
	// protected static String DS_LECTURA;
	protected static String COLLECTION;

	protected final Log log = LogFactory.getLog(getClass());
	ObjectMapper mapper = new ObjectMapper();

	private static final String DS_GRABACION = "dg";

	private static final String DS_CONSULTA = "dc";

	/**
	 * Nombre de la coleccion
	 */
	protected String collectionName;

	/**
	 * Conexion a la base de datos de Escritura
	 */
	private MongoDatabase mongoDatabaseWrite;

	/**
	 * Conexion a la base de datos de Lectura
	 */
	// private MongoDatabase mongoDatabaseRead;

	/**
	 * Nombre de la coleccion para operaciones de Lectura
	 */
	MongoCollection<T> mongoCollectionRead;

	/**
	 * Nombre de la coleccion para operaciones de Escritura
	 */
	// MongoCollection<T> mongoCollectionWrite;

	/**
	 * Entity Class para las colecciones de MongoDB
	 */
	protected Class<T> entityClass;

	/**
	 * Se debe indicar la clase a devolver o procesar
	 */
	public abstract Class<T> provideEntityClass();

	@PostConstruct
	public void init() {
		mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		this.entityClass = provideEntityClass();
		// List<MongoDBConexion> ls = yamlFooProperties.conexiones;

		// con.getConexion();
		// this.mongoDatabaseWrite =
		// configMain.getDataBases(appProperties.getDatabase(),
		// configMain.getConexion());
		MongoClient mongoClient = new MongoClient(mongoDBConfig.getConexiones().get(0).getNodos().get(0).getHost(),
				mongoDBConfig.getConexiones().get(0).getNodos().get(0).getPort());
		// MongoDatabase database = mongoClient.getDatabase("");

		this.mongoDatabaseWrite = mongoClient.getDatabase(DS_WRITE);// MongoDBClient.getDataBaseByDataSource(DS_WRITE);

		this.collectionName = COLLECTION;
		this.mongoCollectionRead = mongoDatabaseWrite.getCollection(collectionName, entityClass);
	}

	public List<T> findAll() {
		List<T> objetos = (List<T>) mongoCollectionRead.find().into(new ArrayList<T>());
		return objetos;
	}

	/**
	 * Devuelve una lista de elementos de una coleccion por un filtro especifico
	 * 
	 * @param query
	 * @return
	 */
	public List<T> findAllByQuery(Bson query) {
		log.debug("query==>" + query.toString());
		List<T> objetos = (List<T>) mongoCollectionRead.find(query).into(new ArrayList<T>());
		return objetos;
	}

	/**
	 * @deprecated Es una mala pr&aacute;ctica forzar al Motor de Base de Datos a
	 *             utilizar un determinado &iacute;ndice. Devuelve una lista de
	 *             elementos de una coleccion por un filtro e indice especifico
	 * @param query
	 * @param index
	 * @return
	 */
	@Deprecated
	public List<T> findAllWithHintByQuery(Bson query, Bson index) {
		log.debug("query==>" + query.toString());
		log.debug("hint==>" + index.toString());
		List<T> objetos = (List<T>) mongoCollectionRead.find(query).hint(index).into(new ArrayList<T>());
		return objetos;
	};

	/**
	 * Devuelve un elemento de una coleccion por un filtro especifico
	 * 
	 * @param query
	 * @return
	 */
	public T findOneById(Bson query) {
		log.debug("query==>" + query.toString());
		final Optional<T> objeto = Optional.ofNullable((T) mongoCollectionRead.find(query).first());
		try {
			if (objeto.isPresent()) {
				return objeto.get();
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return null;
	}

	/**
	 * @deprecated Es una mala pr&aacute;ctica forzar al Motor de Base de Datos a
	 *             utilizar un determinado &iacute;ndice. Busca un document por el
	 *             query especificado e indice especificado
	 * @param query
	 * @param index
	 * @return
	 */
	@Deprecated
	public T findOneWithHintByQuery(Bson query, Bson index) {
		log.debug("query==>" + query.toString());
		log.debug("hint==>" + index.toString());
		final Optional<T> objeto = Optional.ofNullable((T) mongoCollectionRead.find(query).hint(index).first());
		try {
			if (objeto.isPresent()) {
				return objeto.get();
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return null;
	}

	/**
	 * Grabar el Document enviado Utiliza la base de datos definida para Escritura
	 * 
	 * @param document
	 */
	public T save(T document) {
		mongoCollectionRead.insertOne(document);
		return document;
	}

	/**
	 * Grabar el documento en la coleccion del repository Utiliza la base de datos
	 * definida para Escritura
	 * 
	 * @param document
	 * @return
	 */
	public Document save(Document document) {
		mongoDatabaseWrite.getCollection(collectionName).insertOne(document);
		return document;
	}

	/**
	 * Obtiene una Coleccion de la Base de datos; Debe indicarse el tipo datasource
	 * que se desea utilizar: Lectura o Escritura datasourceType: dg: Grabacion /
	 * dc: Consulta por defecto se va por el de grabacion.
	 */
	public MongoCollection<Document> getCollection(String dsType, String collectionName) {
		MongoCollection<Document> collection;
		switch (dsType) {
		case DS_CONSULTA:
			collection = mongoDatabaseWrite.getCollection(collectionName);
		case DS_GRABACION:
			collection = mongoDatabaseWrite.getCollection(collectionName);
		default:
			collection = mongoDatabaseWrite.getCollection(collectionName);
		}
		return collection;
	}

	/**
	 * Obtiene una Coleccion de la Base de datos; Por defecto se esta yendo a
	 * consultar por la conexión de Escritura
	 * 
	 */
	public MongoCollection<Document> getCollection(String colname) {

		MongoCollection<Document> collection = mongoDatabaseWrite.getCollection(colname);
		return collection;
	}

	/**
	 * Inserta un document en la coleccion
	 * 
	 * @param colname
	 * @param document
	 */
	public void insertCollection(T document) {
		// mongoDatabase.getCollection(collectionName).insertOne(document);
		mongoCollectionRead.insertOne(document);
	}

	/**
	 * Inserta un document en la coleccion
	 * 
	 * @param colname
	 * @param document
	 */
	public void insertOne(T document) {
//		mongoDatabase.getCollection(collectionName).insertOne(document);
		MongoCollection<T> tcCollection = mongoDatabaseWrite.getCollection(this.collectionName, this.entityClass);
		tcCollection.insertOne(document);
	}

	/**
	 * Inserta un documento en la coleccion y devuelve el resultado
	 * 
	 * @param document
	 * @return
	 */
	public T insert(T document) {
		mongoCollectionRead.insertOne(document);
		return document;
	}

	/**
	 * Actualiza un document en la coleccion
	 * 
	 * @param filter
	 * @param document
	 */
	public void updateOne(Bson filter, T document) {
		MongoCollection<T> tcCollection = mongoDatabaseWrite.getCollection(this.collectionName, this.entityClass);
		tcCollection.updateOne(filter, new Document("$set", document));
	}

	/**
	 * Obtiene una coleccion con un tipo especifico Obtiene la conexion de Base de
	 * datos : de escritura
	 * 
	 * @param <T>
	 * @param colname
	 * @param classType
	 * @return
	 */
	public <T> MongoCollection<T> getCollection(String colname, Class<T> classType) {
		CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		MongoCollection<T> collection = mongoDatabaseWrite.getCollection(colname, classType)
				.withCodecRegistry(codecRegistry);
		return collection;
	}

	public GridFSBucket getGridFSBucket(String bucketName) {
		return GridFSBuckets.create(mongoDatabaseWrite, bucketName)
				.withReadPreference(mongoDatabaseWrite.getReadPreference())
				.withWriteConcern(mongoDatabaseWrite.getWriteConcern());
	}

	/**
	 * Inserta una lista de objetos en la coleccion
	 * 
	 * @param <T>
	 * @param colname
	 * @param classType
	 * @param listObjects
	 * @return
	 */
	public <T> MongoCollection<T> insertCollection(String colname, Class<T> classType, List<T> listObjects) {

		MongoCollection<T> collection = mongoDatabaseWrite.getCollection(colname, classType);
		collection.insertMany(listObjects);
		collection = mongoDatabaseWrite.getCollection(colname, classType);
		return collection;
	}

	/**
	 * Actualiza elementos de una coleccion tipada
	 * 
	 * @param <T>
	 * @param colname
	 * @param classType
	 * @param filter
	 * @param query
	 * @return
	 */
	public <T> MongoCollection<T> updateCollection(String colname, Class<T> classType, Bson filter, Bson query) {

		MongoCollection<T> collection = mongoDatabaseWrite.getCollection(colname, classType);
		collection.updateOne(filter, query);
		collection = mongoDatabaseWrite.getCollection(colname, classType);
		return collection;
	}

	/**
	 * Actualiza elementos de una coleccion sin tipo (document)
	 * 
	 * @param colname
	 * @param filter
	 * @param query
	 * @return
	 */
	public T updateCollection(String colname, Bson filter, Bson query) {
		mongoCollectionRead.updateOne(filter, query);
		mongoCollectionRead = mongoDatabaseWrite.getCollection(collectionName, entityClass);
		return mongoCollectionRead.find(filter).first();
	}

	public <T> T insertOneCollection(String colname, Class<T> t, T entity) {

		MongoCollection<T> collection = mongoDatabaseWrite.getCollection(colname, t);
		collection.insertOne(entity);
		return entity;
	}

	private void createSequencesCollection(String collectionName, String sequenceName) {
		BasicDBObject newSequence = new BasicDBObject();
		newSequence.append("_id", sequenceName);
		newSequence.append("seq", 0);
		Document documen = Document.parse(newSequence.toJson());
		getCollection(collectionName).insertOne(documen);
	}

	/**
	 * Obtiene un correlativo desde una coleccion de sequences en mongodb
	 * 
	 * @param collectionName
	 * @param sequenceName
	 * @return
	 */
	public String getNextSequence(String collectionName, String sequenceName) {
		if (getCollection(collectionName).countDocuments() == 0) {
			createSequencesCollection(collectionName, sequenceName);
		}
		Document searchQuery = new Document("_id", sequenceName);
		Document increase = new Document("seq", 1);
		Document updateQuery = new Document("$inc", increase);
		Document result = getCollection(collectionName).findOneAndUpdate(searchQuery, updateQuery);
		return String.valueOf(result.get("seq"));
	}

}