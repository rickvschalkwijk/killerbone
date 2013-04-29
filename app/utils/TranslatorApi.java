package utils;

import play.Logger;
import helpers.Common;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import com.typesafe.config.ConfigFactory;

public class TranslatorApi 
{
	public static void setupTranslatorApi()
	{
		Translate.setClientId(ConfigFactory.load().getString("translator.clientid")); 
		Translate.setClientSecret(ConfigFactory.load().getString("translator.clientsecret"));
	}
	
	public String toEnglish(String text)
	{
		if (!Common.isNullOrEmpty(text))
		{
			try {
				return Translate.execute(text, Language.ENGLISH);
			} catch (Exception e) {
				Logger.error("An error occured while translating text: " + text.substring(0, 10) + "..");
			}
		}
		return Common.ensureNotNull(text);
	}
}
