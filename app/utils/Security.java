package utils;

import helpers.Common;
import helpers.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

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
		public Result call(Http.Context ctx) throws Throwable
		{
			Authenticator authenticator = new Authenticator();
			boolean isAuthorized = false;
			
			String userId = ctx.session().get("UserId");
			String authToken = ctx.session().get("AuthToken");
			
			if (!Common.isNullOrEmpty(userId) && Validator.validateNumeric(userId) && !Common.isNullOrEmpty(authToken))
			{
				isAuthorized = authenticator.validateAuthToken(Long.valueOf(userId), true, authToken);
				if (isAuthorized)
				{
					return delegate.call(ctx);
				}
			}
			if (configuration.redirectToLogin())
			{
				return redirect(controllers.routes.Application.login());
			}
			return forbidden();
		}
	}
}
