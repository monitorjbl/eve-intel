package com.thundermoose.eveintel.api;

import com.thundermoose.eveintel.exceptions.RemoteReadException;
import com.thundermoose.eveintel.exceptions.XmlParsingException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thundermoose on 11/24/14.
 */
public class ApiUtils {
  public static InputStream httpGet(String uri) throws IOException {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(uri);
    CloseableHttpResponse response = httpclient.execute(httpGet);

    int code = response.getStatusLine().getStatusCode();
    if (code == 200) {
      return response.getEntity().getContent();
    } else {
      throw new IOException("Got [" + code + "] response from GET request to [" + uri + "]");
    }
  }

  public static Document readXml(String uri) {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      return dBuilder.parse(httpGet(uri));
    } catch (ParserConfigurationException | SAXException e) {
      throw new XmlParsingException("Could not parse XML from [" + uri + "]", e);
    } catch (IOException e) {
      throw new RemoteReadException("Could not read from [" + uri + "]", e);
    }
  }

  public static String attribute(Node node, String attrName) {
    Node n = node.getAttributes().getNamedItem(attrName);
    return n.getTextContent();
  }

  @SuppressWarnings("unchecked")
  public static Node xmlNode(Element ele, String xpath) {
    XPath xPath = XPathFactory.newInstance().newXPath();
    try {
      NodeList list = (NodeList) xPath.evaluate(xpath, ele, XPathConstants.NODESET);
      for (int i = 0; i < list.getLength(); i++) {
        return list.item(i);
      }
      return null;
    } catch (XPathExpressionException e) {
      throw new XmlParsingException("Could not execute XPath expression [" + xpath + "]", e);
    }
  }

  @SuppressWarnings("unchecked")
  public static List<Node> xmlNodes(Element ele, String xpath) {
    XPath xPath = XPathFactory.newInstance().newXPath();
    try {
      NodeList list = (NodeList) xPath.evaluate(xpath, ele, XPathConstants.NODESET);
      List<Node> l = new ArrayList<>();
      for (int i = 0; i < list.getLength(); i++) {
        l.add(list.item(i));
      }
      return l;
    } catch (XPathExpressionException e) {
      throw new XmlParsingException("Could not execute XPath expression [" + xpath + "]", e);
    }
  }
}
