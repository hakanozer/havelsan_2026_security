package xxe;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainXml {

    private static final String FILENAME = "src/main/resources/staff.xml";

    public static void main(String[] args){

        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {

            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            SAXParser saxParser = factory.newSAXParser();
            PrintAllHandlerSax handler = new PrintAllHandlerSax();
            saxParser.parse(FILENAME, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
