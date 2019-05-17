package petition;

import java.util.List;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@Api(name = "myApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "helloworld.example.com", ownerName = "helloworld.example.com", packagePath = ""))

public class PetitionRest {

	// top 100 des petitions
	@ApiMethod(name = "listTopPetition", path = "/listTopPetition", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> listTopPetition() {

		// recupère les petition trié par ordre décroissant des scores
		Query q = new Query("Petition").addSort("score", Query.SortDirection.DESCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);

		// recupére uniquement les 100 premiers resultats
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
		return result;
	}

	// recupère toutes les pétitions
	@ApiMethod(name = "listAllPetition", path = "/listAllPetition", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> listAllPetition() {

		Query q = new Query("Petition");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);

		// recupére uniquement les 100 premiers resultats
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
		return result;
	}

	// recupère les petitions crée par l'utilsateur
	@ApiMethod(name = "listUserPetition", path = "/listUserPetition", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> listUserPetition(@Named("sender") String sender) {
		Query q = new Query("Petition").setFilter(new FilterPredicate("sender", FilterOperator.EQUAL, sender));

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);

		// recupére uniquement les 100 premiers resultats
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
		return result;
	}

	// poste une petition
	@ApiMethod(name="postPetition", path="/postPetition", httpMethod=ApiMethod.HttpMethod.POST)
	public Entity postPetition(@Named("title") String title, @Named("body") String body, @Named("category") String category) {
		Entity e = new Entity("Petition"); // definir la clée
		e.setProperty("title", title);
		e.setProperty("body", body);
		e.setProperty("category", category);
		e.setProperty("score", 0);
		e.setProperty("pour", null);
		e.setProperty("contre", null);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(e);
		
		return e;	
	}

	@ApiMethod(name="votePour", path="votePour", httpMethod=ApiMethod.HttpMethod.PUT)
	public Entity votePour(@Named("petition") Entity petition) {
		Query q = new Query("Petition").setFilter(new FilterPredicate("petition", FilterOperator.EQUAL, petition));
		
		return petition;
		
	}
}
	
	


