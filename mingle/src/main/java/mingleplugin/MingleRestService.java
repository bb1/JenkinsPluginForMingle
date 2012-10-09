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

  public URL generateRestUrl(String action) {
    return new URL("http",userName+":"+password+"@"+url.getHost(),url.getPort(),url.getFile()+action);
  }


  public MingleObject doMingleCall(URL url, String method, MingleObject obj) {
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
    Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
    if (scanner.hasNext()) {
      try {
        resultString = scanner.next();
      } catch (java.util.NoSuchElementException e) {
        resultString = "";
      }
    }
    // TODO: parse String to XML
    // TODO: convert XML to some kind of useful MingleCart or MingleSomething-object using XStream!
  }

}