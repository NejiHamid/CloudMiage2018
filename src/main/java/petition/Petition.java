package petition;



import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class Petition extends HttpServlet {
	
	DatastoreService datastore;
	
	@Override
	public void init() throws ServletException
	{
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	
	// poste des petitions
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		
		
		// Create a map of the httpParameters that we want and run it through jSoup
		//jsoup est une bibliothèque Java open source conçue pour analyser, extraire et manipuler les données stockées dans des documents HTML
		  Map<String, String> petitionContent =
		      req.getParameterMap()
		          .entrySet()
		          .stream()
		          .filter(a -> a.getKey().startsWith("petitionContent_"))
		          .collect(
		              Collectors.toMap(
		                  p -> p.getKey(), p -> Jsoup.clean(p.getValue()[0], Whitelist.basic())));
		
		Entity petition = new Entity("Petition");
		petition.setProperty("sender",petitionContent.get("petitionContent_sender"));
		petition.setProperty("title",petitionContent.get("petitionContent_title"));
		petition.setProperty("body",petitionContent.get("petitionContent_body"));
		petition.setProperty("category",petitionContent.get("petitionContent_category"));
		petition.setProperty("score",petitionContent.get("petitionContent_score"));
		petition.setProperty("pour",petitionContent.get("petitionContent_pour"));
		petition.setProperty("contre",petitionContent.get("petitionContent_contre"));
		
		try {
			// store the fully populated entity in the cloud datastore
			datastore.put(petition);
			String confirmation = "Petition with title " + petitionContent.get("petitionContent_title") + " created.";
			
			req.setAttribute("confirmation", confirmation);
		  
		}
		catch (DatastoreFailureException e) {
		    throw new ServletException("Datastore error", e);
		  }
		
		
	}
	
}

