package utils;

import java.io.*;

import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import org.w3c.dom.*;

public class XmlProcessor
{
	/**
	 * Validates a xml document against a xsd.
	 * @param Document
	 * @param String
	 * @return boolean
	 */
	public boolean validateXmlAgainstXsd(Document xmlDocument, String pathToXsd)
	{
		// Open xsd file
		File xsdFile = new File(pathToXsd);
		
	    try
	    {
	        // Prepare xml schema
	    	SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = factory.newSchema(new StreamSource(xsdFile));
	        
	        // Prepare xml document
	        InputStream xmlInputStream = documentToInputStream(xmlDocument);
	        
	        // Perform validation
	        Validator validator = schema.newValidator();
	        validator.validate(new StreamSource(xmlInputStream));
	        
	        return true;
	    }
	    catch(Exception ex)
	    {
	        return false;
	    }		
	}
	
	/**
	 * Converts a xml document to an input-stream.
	 * @param Document
	 * @return InputStream
	 * @throws Exception
	 */
	private InputStream documentToInputStream(Document xmlDocument) throws Exception
	{
		// Create output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();       
        Result outputTarget = new StreamResult(outputStream);
        
        // Output xml document
        Source xmlSource = new DOMSource(xmlDocument);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);

        return new ByteArrayInputStream(outputStream.toByteArray());	
	}
}
