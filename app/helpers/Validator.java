package helpers;

public class Validator
{
	private final String NUMERIC_REGEX = "[0-9]{1,10}";
	private final String NAME_REGEX = ".{0,50}";
	private final String EMAIL_REGEX = ".+\\@.+\\..+";
	private final String PASSWORD_REGEX = "^[a-zA-Z]\\w{3,14}$";
	
	//-----------------------------------------------------------------------//
	
	/**
	 * Validates that a string contains numeric data.
	 * @param String
	 * @return boolean
	 */
	public boolean validateNumeric(String numeric)
	{
		return (!Common.isNullOrEmpty(numeric) && numeric.matches(NUMERIC_REGEX));
	}
	
	/**
	 * Validates that a string contains name compatible data.
	 * @param String
	 * @return boolean
	 */
	public boolean validateName(String name)
	{
		return (!Common.isNullOrEmpty(name) && name.matches(NAME_REGEX));
	}
	
	/**
	 * Validates that a string contains data in a email format.
	 * @param String
	 * @return boolean
	 */
	public boolean validateEmail(String email)
	{
		return (!Common.isNullOrEmpty(email) && email.matches(EMAIL_REGEX));
	}
	
	/**
	 * Validates that a string contains password compatible data.
	 * @param String
	 * @return boolean
	 */
	public boolean validatePassword(String password)
	{
		return (!Common.isNullOrEmpty(password) && password.matches(PASSWORD_REGEX));
	}
}
