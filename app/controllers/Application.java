package controllers;

import play.mvc.*;

public class Application extends Controller
{
	public static Result index()
	{
		return ok("Application is running!");
	}
}
