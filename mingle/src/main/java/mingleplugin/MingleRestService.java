package mingleplugin;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;



/**
 * Class for the Mingle connection
 *
 * @author Birk Brauer
 * @version 0.5
 */
public class MingleRestService {
/**
 * URL of mingle, like <tt>http://mingle/api/v2/projects/scrum/</tt>.
 * Mandatory. Normalized to end with '/'
 */
  public final URL url;

/**
 * User name needed to login. Optional?
 */
  public final String userName;

/**
 * Password needed to login. Optional?
 */
  public final String password;

/**
 * True if this mingle is configured to allow Confluence-style Wiki comment. Wait? Wat?
 */
  public final boolean supportsWikiStyleComment;

// XStream set up:
  XStream xstream = new XStream(new StaxDriver());
  xstream.alias("MingleObject", MingleObject.class);
  xstream.alias("MingleCard", MingleCard.class);
  xstream.alias("MingleCardProperty", MingleCardProperty.class);


  @DataBoundConstructor
  public MingleRestService(URL url, String userName, String password, boolean supportsWikiStyleComment) {
    if(!url.toExternalForm().endsWith("/")) {
      try {
        url = new URL(url.toExternalForm()+"/");
      } catch (MalformedURLException e) {
        throw new AssertionError(e); // impossible
      }
    }
    this.url = url;
    this.userName = Util.fixEmpty(userName);
    this.password = Util.fixEmpty(password);
    this.supportsWikiStyleComment = supportsWikiStyleComment;
  }

  public String getUrlAsString() {
    return url.toExternalForm();
  }

  public URL getUrl() {
    return url;
  }

/**
 * Generates a URL for the mingle REST call.
 * 
 * @return URL returns a URL including username and password.
 */
  public URL generateRestUrl(String action) {
    return new URL(url.getProtocol()+"://"+userName+":"+password+"@"+url.getHost()+url.getPort()+"/"+url.getPath()+action);
  }

/**
 * Gets a mingle card by it's unique number.
 *
 * @param number The unique number of the requested card.
 * 
 * @return MingleCard Returns a mingle card by it's unique number.
 */
  public MingleCard getCardByNumber(int number) {
    String xml = doMingleCall(generateRestUrl("cards/"+number+".xml"), "GET" , null);
    // convert XML to some kind of useful MingleCart or MingleSomething-object using XStream:
    MingleCard card = (MingleCard)xstream.fromXML(xml);
    return card;
  }

/**
 * Overrides a card on the mingle server with the given card.
 *
 * @param number The unique number of the requested card.
 * @param card The new mingle card that should replace the version on the mingle server.
 */
  public void updateCardByNumber(int number, MingleCard card) {
    // Converts a MingleCard to a XML String.
    String xml = xstream.toXML(card);
    return card;
  }

/**
 * Performs a REST call on the mingle server and returns a XML string.
 *
 * @param url URL The url that represents the RESTful url for the mingle server.
 * @param method String method This will set the used HTTP method. Only GET, POST, PUT or DELETE are supported.
 * @param obj MingleObject
 * 
 * @return String XML object that can be parsed as a MingleObject.
 */
// TODO: doesn't accept input yet!
  public String doMingleCall(URL url, String method, String xml) {
    if (method == null) method = "GET";
    HttpURLConnection connection = new HttpURLConnection(url);
    // Checks for valid Http method. Mingle supports: GET, POST, PUT or DELETE.
    if (method == "GET" || method == "POST" || method == "PUT" || method == "DELETE") {
      connection.setRequestMethod(method);
    }
    else throw ProtocolException();
    connection.setFollowRedirects(true);
    //connection.connect();
    InputStream is = connection.getInputStream();
    resultString = "";
    // convert InputStream to String
    Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A"); // or "\\Z" ?
    if (scanner.hasNext()) {
      try {
        resultString = scanner.next();
      } catch (java.util.NoSuchElementException e) {
        resultString = "";
      }
    }
    return resultString;
  }

}