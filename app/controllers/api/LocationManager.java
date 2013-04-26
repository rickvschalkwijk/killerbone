package controllers.api;

import java.util.ArrayList;
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
	
	//-----------------------------------------------------------------------//
	
	public static Result getAllLocationsFromCategory(String categoryName)
	{
		LocationCategory category = LocationCategory.find.where().eq("systemName", categoryName).findUnique();
		
		if (category != null)
		{
			return ok(locationList.render(category.locations).body().trim()).as("text/xml");
		}
		return operationFailed();
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result getNewLocationsFromCategory(String categoryName, long timestamp)
	{
		LocationCategory category = LocationCategory.find.where().eq("systemName", categoryName).findUnique();
		
		if (category != null)
		{
			List<Location> locations = category.locations;
			List<Location> newLocations = new ArrayList<Location>();
			
			// Filter new events
			for (Location location : locations)
			{
				if (location.creationTimestamp >= timestamp || location.modificationTimestamp >= timestamp)
				{
					newLocations.add(location);
				}
			}
			return ok(locationList.render(newLocations).body().trim()).as("text/xml");
		}
		return operationFailed();
	}
}