package controllers.admin;

import annotations.Security.Authorized;

import com.avaje.ebean.Ebean;

import helpers.Common;
import helpers.Pagination;
import helpers.Pagination.Page;
import models.Event;
import play.data.DynamicForm;
import play.mvc.Result;
import views.html.admin.eventOverview;
import views.html.admin.eventsOverview;
import core.AdminController;

public class EventManagement extends AdminController
{
	@Authorized
	public static Result index(int page, String orderBy, String filter)
	{
		Page<Event> events = Pagination.getEventPage(page, 15, orderBy, filter);
		return ok(eventsOverview.render(events, orderBy, filter));
	}
	
	@Authorized
	public static Result displayEvent(long eventId)
	{
		Event event = Event.find.byId(eventId);
		if (event != null)
		{
			return ok(eventOverview.render(event));
		}
		return redirect(routes.EventManagement.index(1, "", ""));
	}
	
	@Authorized(redirectToLogin = false)
	public static Result updateEvent(long eventId)
	{
		DynamicForm requestData = DynamicForm.form().bindFromRequest();
		Event event = Event.find.byId(eventId);
		
		String title = requestData.get("title");
		String price = requestData.get("price");
		String description = requestData.get("description");
		
		// Update event
		if (event != null)
		{
			event.title = Common.ensureNotNull(title).trim();
			event.price = Common.ensureNotNull(price).trim();
			event.description = Common.ensureNotNull(description).trim();
			event.modifiedTimestamp = Common.getTimestamp();
			
			Ebean.save(event);
			flash("event.updated", "");
		}
		return redirect(routes.EventManagement.displayEvent(eventId));
	}	
}
