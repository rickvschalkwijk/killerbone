package controllers;

import helpers.Common;
import models.User;
import org.w3c.dom.Document;
import play.libs.XPath;
import play.mvc.*;
import utils.Authenticator;
import utils.XmlProcessor;

import java.util.HashMap;
import java.util.Map;

public class Authentication extends Controller
{
	public static Result createAuthToken()
	{
		XmlProcessor xml = new XmlProcessor();
		Authenticator authenticator = new Authenticator();
		Document xmlDocument = request().body().asXml();
		
		// Validate xml document
		String pathToXsd = Common.resolvePath("~/app/assets/xsd/createAuthToken.xsd");
		boolean isValidXml = xml.validateXmlAgainstXsd(xmlDocument, pathToXsd);
		
		if (!isValidXml)
		{
			String xmlResponse = xml.composeXmlMessage("INVALID_XML_FORMAT", "", null);
			return badRequest(xmlResponse).as("text/xml");
		}

		// Gather required user information
		String email = XPath.selectText("/user/email", xmlDocument).trim();
		String password = XPath.selectText("/user/password", xmlDocument).trim();

		// Validate user credentials
		User user = User.find.where().eq("email", email).eq("password", password).findUnique();

		if (user != null)
		{
			Map<String, String> extraContent = new HashMap<String, String>();
			extraContent.put("userid", String.valueOf(user.userId));
			
			String authToken = authenticator.generateAuthToken(user.userId);
			String xmlResponse = xml.composeXmlMessage("USER_LOGIN_SUCCESS", authToken, extraContent);
			return ok(xmlResponse).as("text/xml");
		}
		else
		{
			String xmlResponse = xml.composeXmlMessage("USER_LOGIN_FAILED", "", null);
			return ok(xmlResponse).as("text/xml");
		}		
	}
}
