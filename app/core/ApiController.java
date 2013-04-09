package core;

import helpers.Common;
import helpers.Server;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Authenticator;
import views.xml.api.messages.operationFailed;
import views.xml.api.messages.operationSuccess;
import views.xml.api.messages.unauthorized;

public abstract class ApiController extends Controller
{
	public static boolean isAuthorized(long userId)
	{
		Authenticator authenticator = new Authenticator();
		String authToken = Server.getHeaderValue("AuthToken");

		if (!Common.isNullOrEmpty(authToken) && authenticator.validateAuthToken(userId, false, authToken))
		{
			return true;
		}
		return false;
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result unAuthorized()
	{
		return unauthorized(unauthorized.render());
	}
	
	public static Result operationSuccess()
	{
		return ok(operationSuccess.render());
	}
	
	public static Result operationFailed()
	{
		return badRequest(operationFailed.render());
	}
}
