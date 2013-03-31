package controllers;

import models.User;
import org.w3c.dom.Document;

import play.api.Play;
import play.libs.XPath;
import play.mvc.*;
import views.xml.*;
import utils.Authenticator;
import utils.XmlProcessor;

import java.io.File;

public class Authentication extends Controller
{
	public static Result createAuthToken()
	{
		XmlProcessor xml = new XmlProcessor();
		Authenticator authenticator = new Authenticator();
		Document xmlDocument = request().body().asXml();
		
		// Validate xml document
		File xsdFile = Play.current().getFile("/public/xsd/createAuthToken.xsd");
		boolean isValidXml = xml.validateXmlAgainstXsd(xmlDocument, xsdFile.getAbsolutePath());
		
		if (!isValidXml)
		{
			return ok(message.render("XML_INVALID", ""));
		}

		// Gather required user information
		String email = XPath.selectText("/user/email", xmlDocument).trim();
		String password = XPath.selectText("/user/password", xmlDocument).trim();
		
		// Validate user credentials
		User user = User.find.where().eq("email", email).eq("password", password).findUnique();

		if (user != null)
		{
			String authToken = authenticator.generateAuthToken(user.userId);
			return ok(authtoken.render(authToken, user.userId));
		}
		else
		{
			return ok(message.render("AUTHTOKEN_CREATE_FAILED", ""));
		}		
	}
}
