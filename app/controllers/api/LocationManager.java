package controllers.api;

import java.util.List;

import com.avaje.ebean.Expr;

import models.*;
import play.mvc.*;
import views.xml.api.locationList;
import core.ApiController;

public class LocationManager extends ApiController 
{
	public static Result getAllLocations()
	{
		List<Location> locations = Location.find.all();
		return ok(locationList.render(locations).body().trim()).as("text/xml");
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result getNewLocations(long timestamp)
	{
		List<Location> locations =  Location.find.where()
										    .or(Expr.ge("creationTimestamp", timestamp), 
										        Expr.ge("modificationTimestamp", timestamp)).findList();
		
		return ok(locationList.render(locations).body().trim()).as("text/xml");
	}
}