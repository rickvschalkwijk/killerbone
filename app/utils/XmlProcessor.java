package utils;

import java.io.*;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import org.w3c.dom.*;
import play.libs.XPath;

import helpers.Common;

public class XmlProcessor
{
	public boolean validateXmlAgainstXsd(Document xmlDocument, String pathToXsd)
	{
		// Open xsd file
		String pathToXsdFile = Common.resolvePath(pathToXsd);
		File xsdFile = new File(pathToXsdFile);
		
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
	
	private InputStream documentToInputStream(Document xmlDocument) throws Exception
	{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(xmlDocument);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);

		return new ByteArrayInputStream(outputStream.toByteArray());	
	}
	
	public String composeXmlMessage(String pathToTemplate, Map<String, String> templateValues)
	{
		// open xml template
		pathToTemplate = Common.resolvePath(pathToTemplate);
		File xmlFile = new File(pathToTemplate);
		String templateString = null;
		
		try
		{
			// Parse xml message template
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document xmlMessage = dBuilder.parse(xmlFile);	
			xmlMessage.setXmlStandalone(true);			
			
			// Output xml message
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(xmlMessage);
			transformer.transform(source, result);
	
			templateString = result.getWriter().toString();				
		}
		catch (Exception e)
		{
			return null;
		}
		
		// Fill in template placeholders
		if (!Common.isNullOrEmpty(templateString))
		{
			String tempKey = null;
			String tempValue = null;
			
			for (Map.Entry<String,String> entry : templateValues.entrySet()) 
			{
				tempKey = entry.getKey();
				tempValue = entry.getValue();
				
				// Add key-value pair as xml node
				if (!Common.isNullOrEmpty(tempKey) && !Common.isNullOrEmpty(tempValue))
				{
					tempKey = "{" + tempKey.trim() + "}";
					templateString = templateString.replace(tempKey, tempValue);
				}
			}
		}
		return templateString; 
	}
	
	public String composeXmlMessage(String code, String content, Map<String, String> extraContent)
	{
		// Open xml message template
		String pathToMessageTemplate = Common.resolvePath("~/app/assets/xml/message.xml");
		File xmlFile = new File(pathToMessageTemplate);
		
		try
		{
			// Parse xml message template
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document xmlMessage = dBuilder.parse(xmlFile);	
			xmlMessage.setXmlStandalone(true);			
			
			// Modify xml message
			Node codeNode = XPath.selectNode("/message/code", xmlMessage);
			codeNode.setTextContent(code);
			
			Node contentNode = XPath.selectNode("/message/content", xmlMessage);
			contentNode.setTextContent(content);
			
			// Add extra content to message
			if (extraContent != null)
			{
				String tempKey = null;
				String tempValue = null;
				
				for (Map.Entry<String,String> entry : extraContent.entrySet()) 
				{
					NodeList messageNode = xmlMessage.getElementsByTagName("content");
					tempKey = entry.getKey();
					tempValue = entry.getValue();
					
					// Add key-value pair as xml node
					if (!Common.isNullOrEmpty(tempKey) && !Common.isNullOrEmpty(tempValue))
					{
						Element node = xmlMessage.createElement(tempKey);
						Text textNode = xmlMessage.createTextNode(tempValue);
						node.appendChild(textNode);

						messageNode.item(0).getParentNode().insertBefore(node, messageNode.item(0));
					}
				}
			}
	
			// Output xml message
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(xmlMessage);
			transformer.transform(source, result);
	
			return result.getWriter().toString();	
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
