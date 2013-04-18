package annotations;

import helpers.Common;
import helpers.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.mvc.*;
import play.mvc.Http.*;
import utils.Authenticator;

import controllers.admin.routes;

public class Security
{
	@With(value = { Security.AuthorizedAction.class })
	@Target({ ElementType.TYPE, ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Authorized
	{
		boolean redirectToLogin() default true;
	}
	
	public static class AuthorizedAction extends Action<Authorized>
	{
		public Result call(Context ctx) throws Throwable
		{
			Authenticator authenticator = new Authenticator();
			boolean isAuthorized = false;
			
			String userId = ctx.session().get("UserId");
			String authToken = ctx.session().get("AuthToken");
			
			if (Validator.validateNumeric(userId) && !Common.isNullOrEmpty(authToken))
			{
				isAuthorized = authenticator.validateAuthToken(Long.valueOf(userId), true, authToken);
				if (isAuthorized)
				{
					return delegate.call(ctx);
				}
			}
			if (configuration.redirectToLogin())
			{
				return redirect(routes.Dashboard.login());
			}
			return forbidden();
		}
	}
}
