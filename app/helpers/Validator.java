package helpers;

public class Validator
{
	final private static String NUMERIC_REGEX = "[0-9]{1,10}";
	final private static String NAME_REGEX = ".{0,50}";
	final private static String EMAIL_REGEX = ".+\\@.+\\..+";
	final private static String PASSWORD_REGEX = "^[a-zA-Z]\\w{3,14}$";
	
	//-----------------------------------------------------------------------//
	
	/**
	 * Validates that a string contains numeric data.
	 * @param string
	 * @return boolean
	 */
	public static boolean validateNumeric(String numeric)
	{
		return (!Common.isNullOrEmpty(numeric) && numeric.matches(NUMERIC_REGEX));
	}
	
	/**
	 * Validates that a string contains name compatible data.
	 * @param string
	 * @return boolean
	 */
	public static boolean validateName(String name)
	{
		return (!Common.isNullOrEmpty(name) && name.matches(NAME_REGEX));
	}
	
	/**
	 * Validates that a string contains data in a email format.
	 * @param string
	 * @return boolean
	 */
	public static boolean validateEmail(String email)
	{
		return (!Common.isNullOrEmpty(email) && email.matches(EMAIL_REGEX));
	}
	
	/**
	 * Validates that a string contains password compatible data.
	 * @param string
	 * @return boolean
	 */
	public static boolean validatePassword(String password)
	{
		return (!Common.isNullOrEmpty(password) && password.matches(PASSWORD_REGEX));
	}
}
