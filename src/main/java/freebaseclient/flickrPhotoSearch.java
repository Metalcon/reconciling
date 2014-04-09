package freebaseclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * An experimental implementation of Flickr Queries via XML-RPC. Aiming to
 * request data relevant to our Metalcon interests from Flickr.
 * 
 * @author Christian Schowalter
 * 
 */
public class flickrPhotoSearch {
	public static Properties properties = new Properties();

	public static void main(String[] args) throws IOException {
		try {
			properties.load(new FileInputStream("flickr.properties"));
		} catch (FileNotFoundException fnfe) {
			System.err.println("missing flickr properties");
		}

		GenericUrl url = new GenericUrl(
				"https://api.flickr.com/services/xmlrpc/");
		HttpTransport httpTransport = new NetHttpTransport();
		HttpRequestFactory requestFactory = httpTransport
				.createRequestFactory();

		try {

			// Creating an empty XML Document
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element methodCall = doc.createElement("methodCall");
			doc.appendChild(methodCall);

			Element methodName = doc.createElement("methodName");
			// methodName.setAttribute("name", "value");
			methodCall.appendChild(methodName);

			// add a text element to the child
			Text echo = doc.createTextNode("flickr.test.echo");
			methodName.appendChild(echo);

			Element params = doc.createElement("params");
			methodCall.appendChild(params);

			Element param = doc.createElement("param");
			params.appendChild(param);

			Element value = doc.createElement("value");
			param.appendChild(value);

			Element struct = doc.createElement("struct");
			value.appendChild(struct);

			Element member = doc.createElement("member");
			struct.appendChild(member);

			Element name1 = doc.createElement("name");
			member.appendChild(name1);
			Text name1text = doc.createTextNode("this is my first name");
			name1.appendChild(name1text);

			Element value1 = doc.createElement("value");
			member.appendChild(value1);

			Element string = doc.createElement("string");
			value1.appendChild(string);

			Element api_key = doc.createElement("api_key");
			string.appendChild(api_key);

			// FIXME: this is not the right way to tell flickr the api-key.
			Text api_keyText = doc.createTextNode(properties.get("API_KEY")
					.toString());
			api_key.appendChild(api_keyText);

			// Text value1text = doc.createTextNode("this is my first value");
			// string.appendChild(value1text);

			// Output the XML to a string

			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			String xmlString = sw.toString();

			// print xml
			System.out.println("xml output :\n\n" + xmlString);

			HttpRequest request = requestFactory.buildPostRequest(url,
					ByteArrayContent.fromString("application/json", xmlString));

			HttpResponse httpResponse = request.execute();
			System.out.println(httpResponse.parseAsString());

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
