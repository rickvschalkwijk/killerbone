package helpers;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import play.Logger;

import com.ning.http.util.Base64;
import com.typesafe.config.ConfigFactory;

public class Cryptography
{
	final private static String SECRET_KEY_FACTORY = "PBKDF2WithHmacSHA1";
	final private static String SECRET_KEY_ALGORITHM = "AES";
	final private static String CRYPTO_PASS_VARIABLE = "crypto.password";
	final private static String CRYPTO_SALT_VARIABLE = "crypto.salt";
	final private static String CRYPTO_CIPHER_INSTANCE = "AES";

	private static SecretKey cryptographyKey;
	
	//-----------------------------------------------------------------------//
	
	public static String encrypt(String data)
	{
		SecretKey secret = getCryptographyKey();
		
		try
		{
			// Get and initialize cipher instance
			Cipher cipher = Cipher.getInstance(CRYPTO_CIPHER_INSTANCE);
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			
			// Perform ecryption
			byte[] encryptedData = cipher.doFinal(data.getBytes());
			String encodedData = Base64.encode(encryptedData);
		
			return encodedData;
		}
		catch(Exception e)
		{
			Logger.error("An error occured while encrypting data.");
		}
		
		// Operation failed, return null
		return null;
	}
	
	public static String decrypt(String data)
	{
		SecretKey secret = getCryptographyKey();
		
		try
		{
			// Get and initialize cipher instance
			Cipher cipher = Cipher.getInstance(CRYPTO_CIPHER_INSTANCE);
			cipher.init(Cipher.DECRYPT_MODE, secret);
			
			// Perform decryption
			byte[] decodedData = Base64.decode(data);
			byte[] decryptedData = cipher.doFinal(decodedData);
			
			return new String(decryptedData);
		}
		catch(Exception e)
		{
			Logger.error("An error occured while decrypting data.");
		}
		
		// Operation failed, return null
		return null;
	}
	
	private static SecretKey compileSecretKey(String password, String salt, int keyLength)
	{
		try
		{
			SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY);
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, keyLength);
			SecretKey tmp = factory.generateSecret(keySpec);
			
			return new SecretKeySpec(tmp.getEncoded(), SECRET_KEY_ALGORITHM);			
		}
		catch (Exception e)
		{
			Logger.error("An error occured while compiling cryptography key.");
		}
		
		// Operation failed, return null
		return null;
	}
	
	//-----------------------------------------------------------------------//
	
	public static SecretKey getCryptographyKey()
	{
		if (cryptographyKey == null)
		{
			String password = ConfigFactory.load().getString(CRYPTO_PASS_VARIABLE);
			String salt = ConfigFactory.load().getString(CRYPTO_SALT_VARIABLE);
			cryptographyKey = compileSecretKey(password, salt, 128);			
			
			Logger.info("Cryptography key compiled.");
		}
		return cryptographyKey;
	}	
}
