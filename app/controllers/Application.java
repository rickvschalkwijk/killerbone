package controllers;

import helpers.*;
import models.User;
import org.joda.time.DateTime;
import com.avaje.ebean.Ebean;

import play.mvc.*;

public class Application extends Controller
{
	public static Result index()
	{
		return ok("Application is online!");
	}
	
	public static Result initialize()
	{
		if (Settings.get("application.initialized") != null) 
		{
			// Create admin account
			String hashedAdminPassword = Passwords.createHash("valkering");
			User admin = new User("Onno Valkering", "onnovalkering@gmail.com", hashedAdminPassword);
			admin.creationDate = DateTime.now();
			admin.isAdmin = true;
			admin.isActivated = true;
			Ebean.save(admin);
			
			// Create audit account
			String hashedAuditPassword = Passwords.createHash("audit");
			User audit = new User("Audit AmsterGuide", "audit@amsterguide.nl", hashedAuditPassword);
			audit.creationDate = DateTime.now();
			audit.isAdmin = true;
			audit.isActivated = true;
			Ebean.save(audit);	

			// Create audit account
			String hashedPrototypePassword = Passwords.createHash("prototype");
			User prototype = new User("Prototype AmsterGuide", "prototype@amsterguide.nl", hashedPrototypePassword);
			prototype.creationDate = DateTime.now();
			prototype.isAdmin = true;
			prototype.isActivated = true;
			Ebean.save(prototype);	
			Ebean.save(prototype);
			
			Settings.set("application.initialized", "true");
		}
		return ok("Application is initialized!");
	}	
}