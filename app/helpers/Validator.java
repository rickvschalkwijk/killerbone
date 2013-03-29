package helpers;

public class Validator
{
	private final String NUMERIC_REGEX = ".{0,50}";
	private final String NAME_REGEX = ".{0,50}";
	private final String EMAIL_REGEX = ".+\\@.+\\..+";
	private final String PASSWORD_REGEX = "^[a-zA-Z]\\w{3,14}$";
	
	//-----------------------------------------------------------------------//
	
	public boolean validateNumeric(String numeric)
	{
		return (!Common.isNullOrEmpty(numeric) && numeric.matches(NUMERIC_REGEX));
	}
	
	public boolean validateName(String name)
	{
		return (!Common.isNullOrEmpty(name) && name.matches(NAME_REGEX));
	}
	
	public boolean validateEmail(String email)
	{
		return (!Common.isNullOrEmpty(email) && email.matches(EMAIL_REGEX));
	}
	
	public boolean validatePassword(String password)
	{
		return (!Common.isNullOrEmpty(password) && password.matches(PASSWORD_REGEX));
	}
}
