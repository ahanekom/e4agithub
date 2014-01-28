package com.excel4apps.servlet.wand.oracle.inst.context.xml;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.excel4apps.servlet.wand.oracle.inst.Installer;

/**
 * Read environment CONTEXT_FILE and set's selected values in OAContext POJO
 * 
 * @author Andries Hanekom
 * 
 */
public class OAContextParser
{

    /**
     * POJO holding selected Oracle Application Context Values
     * 
     * @author Andries Hanekom
     * 
     */
    public class OAContext
    {
        public String appsVersion;
        public String applTop;
        public String javaTop;
        public String fndTop;

        public String dbcFileName;

        public String dbhost;

        public String dbsid;

        public String dbport;

        public String getApplTop()
        {
            return applTop;
        }

        public String getAppsVersion()
        {
            return appsVersion;
        }

        public String getDbcFileName()
        {
            return dbcFileName;
        }

        public String getDBHost()
        {
            return dbhost;
        }

        public String getDBPort()
        {
            return dbport;
        }

        public String getDBSid()
        {
            return dbsid;
        }

        public String getFndTop()
        {
            return fndTop;
        }

        public String getJavaTop()
        {
            return javaTop;
        }

        public void setApplTop(String applTop)
        {
            this.applTop = applTop;
        }

        public void setAppsVersion(String appsVersion)
        {
            this.appsVersion = appsVersion;
        }

        public void setDbcFileName(String value)
        {
            dbcFileName = value;
        }

        public void setDBHost(String value)
        {
            dbhost = value;
        }

        public void setDBPort(String value)
        {
            dbport = value;
        }

        public void setDBSid(String value)
        {
            dbsid = value;
        }

        public void setFndTop(String fndTop)
        {
            this.fndTop = fndTop;
        }

        public void setJavaTop(String javaTop)
        {
            this.javaTop = javaTop;
        }
    }

    /**
     * The Handler for SAX Events.
     */
    class SAXHandler extends DefaultHandler
    {
        String content = null;

        public void characters(char[] ch, int start, int length) throws SAXException
        {
            content = String.copyValueOf(ch, start, length).trim();

            if (appsVersion)
            {
                oaContext.setAppsVersion(content);
                logger.finer("appsVersion=" + oaContext.getAppsVersion());
                appsVersion = false;
            }

            if (applTop)
            {
                oaContext.setApplTop(content);
                logger.finer("applTop=" + oaContext.getApplTop());
                applTop = false;
            }
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {

            if (qName.equalsIgnoreCase("dbc_file_name"))
            {
                oaContext.setDbcFileName(content);
                logger.finer("dbc_file_name=" + oaContext.getDbcFileName());
            }

            if (qName.equalsIgnoreCase("dbhost"))
            {
                oaContext.setDBHost(content);
                logger.finer("dbhost=" + oaContext.getDBHost());
            }

            if (qName.equalsIgnoreCase("dbsid"))
            {
                oaContext.setDBSid(content);
                logger.finer("dbsid=" + oaContext.getDBSid());
            }

            if (qName.equalsIgnoreCase("dbport"))
            {
                oaContext.setDBPort(content);
                logger.finer("dbport=" + oaContext.getDBPort());
            }

            if (qName.equalsIgnoreCase("java_top"))
            {
                oaContext.setJavaTop(content);
                logger.finer("java_top=" + oaContext.getJavaTop());
            }

            if (qName.equalsIgnoreCase("fnd_top"))
            {
                oaContext.setFndTop(content);
                logger.finer("fnd_top=" + oaContext.getFndTop());
            }
        }

        private void setApplTop(Attributes attr)
        {
            int length = attr.getLength();

            // process each attribute
            for (int i = 0; i < length; i++)
            {
                // get qualified (prefixed) name by index
                String name = attr.getQName(i);

                // get attribute's value by index.
                String value = attr.getValue(i);

                if ((name.equals("oa_var")) && (value.equals("s_at_adconfig")))
                {
                    applTop = true;
                }
            }
        }

        private void setAppsVersion(Attributes attr)
        {
            int length = attr.getLength();

            // process each attribute
            for (int i = 0; i < length; i++)
            {
                // get qualified (prefixed) name by index
                String name = attr.getQName(i);

                // get attribute's value by index.
                String value = attr.getValue(i);

                if ((name.equals("oa_var")) && (value.equals("s_apps_version")))
                {
                    appsVersion = true;
                }
            }
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes attr)
                throws SAXException
        {
            if (qName.equalsIgnoreCase("oa_context"))
            {
                oaContext = new OAContext();
            }

            if (qName.equalsIgnoreCase("APPL_TOP"))
            {
                setApplTop(attr);
            }

            if (qName.equalsIgnoreCase("config_option"))
            {
                setAppsVersion(attr);
            }
        }
    }

    public OAContext oaContext = null;
    private static final Logger logger = Logger.getLogger(Installer.class.getName());

    private boolean appsVersion = false;

    private boolean applTop = false;

    /**
     * Parse Environment OA CONTEXT_FILE
     * 
     * @param filename
     *            Environment CONTEXT_FILE
     * @throws Exception
     */
    public void parse(String filename) throws Exception
    {
        SAXParserFactory parserFactor = SAXParserFactory.newInstance();
        SAXParser parser = parserFactor.newSAXParser();
        SAXHandler handler = new SAXHandler();

        FileInputStream fis = null;
        fis = new FileInputStream(new File(filename));
        parser.parse(fis, handler);
    }
}
