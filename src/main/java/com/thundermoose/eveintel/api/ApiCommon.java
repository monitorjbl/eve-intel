package com.thundermoose.eveintel.api;

import com.thundermoose.eveintel.exceptions.RemoteReadException;
import com.thundermoose.eveintel.exceptions.XmlParsingException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class ApiCommon {
  public static final String CONTACT_INFO = "monitorjbl@gmail.com";

  public InputStream httpGet(String uri) throws IOException {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(uri);
    httpGet.addHeader(new BasicHeader("Accept-Encoding", "gzip"));
    httpGet.addHeader(new BasicHeader("User-Agent", "eve-intel;" + CONTACT_INFO));
    CloseableHttpResponse response = httpclient.execute(httpGet);

    int code = response.getStatusLine().getStatusCode();
    if (code == 200) {
      return response.getEntity().getContent();
    } else {
      throw new IOException("Got [" + code + "] response from GET request to [" + uri + "]");
    }
  }

  public Document readXml(String uri) {
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

}
