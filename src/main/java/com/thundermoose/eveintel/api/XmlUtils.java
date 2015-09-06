package com.thundermoose.eveintel.api;

import com.thundermoose.eveintel.exceptions.XmlParsingException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {
  public static String sanitize(String str) {
    try {
      return URLEncoder.encode(str, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      //this should never happen at runtime
      throw new RuntimeException(e);
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
