package controllers.admin;

import java.util.List;

import javax.persistence.PersistenceException;

import annotations.Security.Authorized;

import com.avaje.ebean.Ebean;

import helpers.Common;
import helpers.Pagination;
import helpers.Validator;
import helpers.Pagination.Page;
import models.Location;
import models.LocationCategory;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Result;
import views.html.admin.locationOverview;
import views.html.admin.locationsOverview;
import core.AdminController;


public class LocationManagement extends AdminController 
{
	@Authorized
	public static Result index(int page, String orderBy, String filter)
	{
		Page<Location> locations = Pagination.getLocationPage(page, 15, orderBy, filter);
		List<LocationCategory> categories = LocationCategory.find.all();
		
		return ok(locationsOverview.render(locations, categories, orderBy, filter));
	}
	
	@Authorized
	public static Result displayLocation(long locationId)
	{
		Location location = Location.find.byId(locationId);
		if (location != null)
		{
			List<LocationCategory> categories = LocationCategory.find.all();
			
			return ok(locationOverview.render(location, categories));
		}
		return redirect(routes.LocationManagement.index(1, "", ""));
	}
	
	@Authorized(redirectToLogin = false)
	public static Result createLocation()
	{
		boolean operationSucceeded = false;
		DynamicForm requestData = DynamicForm.form().bindFromRequest();
		
		// Gather required information
		String title = requestData.get("title");
		String categoryId = requestData.get("categoryId");
		String description = requestData.get("description");
		String latitude = requestData.get("latitude");
		String longitude = requestData.get("longitude");
		
		LocationCategory category = LocationCategory.find.where().eq("location_category_id", categoryId).findUnique(); 
		
		if (!Common.isNullOrEmpty(title) && !Common.isNullOrEmpty(description) && category != null &&
			Validator.validateLatitudeOrLongitude(latitude) && Validator.validateLatitudeOrLongitude(longitude))
		{
			Location newLocation = new Location();
			newLocation.title = title;
			newLocation.category = category;
			newLocation.description = description;
			newLocation.latitude = Double.parseDouble(latitude);
			newLocation.longitude = Double.parseDouble(longitude);
			newLocation.creationTimestamp = Common.getTimestamp();
			
			try
			{
				Ebean.save(newLocation);	
				
				operationSucceeded = true;
				flash("location.create.name", newLocation.title);				
			}
			catch(PersistenceException e)
			{
				Logger.error("An error occured while creating location: " + e.getMessage());
			}
		}

		flash(operationSucceeded ? "location.create.success" : "location.create.failed", "");
		return redirect(routes.LocationManagement.index(1, "", ""));
	}
	
	@Authorized(redirectToLogin = false)
	public static Result updateLocation(long locationId)
	{
		DynamicForm requestData = DynamicForm.form().bindFromRequest();
		Location location = Location.find.byId(locationId);
		
		String title = requestData.get("title");
		String categoryId = requestData.get("categoryId");
		String description = requestData.get("description");
		String imageUrl = requestData.get("imageUrl");
		String latitude = requestData.get("latitude");
		String longitude = requestData.get("longitude");
		
		LocationCategory category = LocationCategory.find.where().eq("location_category_id", categoryId).findUnique();
		
		if (location != null && !Common.isNullOrEmpty(title) && !Common.isNullOrEmpty(description) && category != null &&
			Validator.validateLatitudeOrLongitude(latitude) && Validator.validateLatitudeOrLongitude(longitude))
		{
			location.title = Common.ensureNotNull(title).trim();
			location.category = category;
			location.description = Common.ensureNotNull(description).trim();
			location.imageUrl = Common.ensureNotNull(imageUrl).trim();
			location.latitude = Double.parseDouble(latitude);
			location.longitude = Double.parseDouble(longitude);			
			location.modificationTimestamp = Common.getTimestamp();
			
			Ebean.save(location);
			flash("location.updated", "");
		}
		return redirect(routes.LocationManagement.displayLocation(locationId));
	}
	
	@Authorized(redirectToLogin = false)
	public static Result deleteLocation(long locationId)
	{
		Location location = Location.find.byId(locationId);
		if (location != null)
		{
			Ebean.delete(location);
			flash("location.deleted", location.title);
		}
		return redirect(routes.LocationManagement.index(1, "", ""));
	}
}