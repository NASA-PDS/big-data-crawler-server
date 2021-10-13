package gov.nasa.pds.crawler.util.xml;

import java.io.FileReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;

import gov.nasa.pds.crawler.util.CloseUtils;


public class PdsLabelInfoParser
{
    private XMLInputFactory factory;
    
    public PdsLabelInfoParser()
    {
        factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    }
    
    
    public PdsLabelInfo getBasicInfo(String path) throws Exception
    {
        XMLEventReader reader = null;
        
        try
        {
            reader = factory.createXMLEventReader(new FileReader(path));
            PdsLabelInfo info = new PdsLabelInfo();
            
            // Root element
            info.productClass = getNextElementName(reader);
            if(info.productClass == null) return null;
            
            String lid = trim(getElementText(reader, "logical_identifier"));
            String vid = trim(getElementText(reader, "version_id"));
            
            if(lid != null && vid != null)
            {
                info.lidvid = lid + "::" + vid;
                return info;
            }
            
            return null;
        }
        finally
        {
            CloseUtils.close(reader);
        }
    }
    
    
    private String getNextElementName(XMLEventReader reader) throws Exception
    {
        while(reader.hasNext())
        {
            XMLEvent event = reader.nextEvent();
            if(event.isStartElement())
            {
                return event.asStartElement().getName().getLocalPart();
            }
        }

        return null;
    }
    
    
    private String getElementText(XMLEventReader reader, String findName) throws Exception
    {
        String name;
        while((name = getNextElementName(reader)) != null)
        {
            if(name.equals(findName)) 
            {
                return reader.getElementText();
            }
        }
        
        return null;
    }
    
    
    private String trim(String str)
    {
        if(str == null) return null;
        
        str = str.trim();
        if(str.isEmpty()) return null;
        
        return str;
    }
}
