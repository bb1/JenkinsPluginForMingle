package mingleplugin;

import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;

//import javax.servlet.ServletException;

import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Class for the Mingle connection using the API v2.
 *
 * @author Birk Brauer
 * @version 0.7
 */
public class MingleRestService extends AbstractDescribableImpl<MingleRestService> {

  private static MingleRestService instance;

  /**
   * Regexp pattern that identifies Mingle Card.
   * If this pattern changes help pages (help-issue-pattern_xy.html) must be updated 
   * <p>
   * First character must be the character #, then digits.
   * See #392 and #404
   */
  protected static Pattern DEFAULT_CARD_PATTERN = Pattern.compile("#([0-9]+)");


  /**
   * URL of mingle, like <tt>http://mingle:80/</tt>.
   * Mandatory. Normalized to end with '/'
   */
  public URL url;

  /**
   * User name needed to login.
   */
  public String userName;

  /**
   * Password needed to login.
   */
  public String password;

  /**
   * Mingle project name. e.g. "scrum".
   */
  public String project;

  /**
   * user defined pattern
   */    
  private String userPattern;
  
  private transient Pattern userPat;

  /**
   * True if this mingle is configured to allow Confluence-style Wiki comment. Wait? Wat?
   */
  public boolean supportsWikiStyleComment;

  // XStream set up:
  XStream xstream = new XStream(new StaxDriver());

  /**
   * conected mingle card... saved HERE?? Or inside the MingleBuildAction?
   * right now: saved inside the Build, but:
   * TODO: How to access the Build from here?
   */

  //@DataBoundConstructor
  MingleRestService(URL url, String userName, String password, String project, String userPattern, boolean supportsWikiStyleComment) {

  xstream.alias("card", MingleCard.class);
  xstream.alias("property", MingleCardProperty.class);
  xstream.alias("project", MingleProject.class);
  xstream.alias("user", MingleUser.class);

    if(!url.toExternalForm().endsWith("/")) {
      try {
        url = new URL(url.toExternalForm()+"/");
      } catch (MalformedURLException e) {
        throw new AssertionError(e); // impossible
      }
    }
    this.url = url;
    this.userName = (userName == "") ? null : userName;
    this.password = (password == "") ? null : password;
    this.project = (project == "") ? null : project;
    this.userPattern = (userPattern == "") ? null : userPattern;
    this.supportsWikiStyleComment = supportsWikiStyleComment;
  }

  /*/@DataBoundConstructor
  public static MingleRestService getInstance(URL url, String userName, String password, String project, String userPattern, boolean supportsWikiStyleComment) {
    if ( instance == null ) {
      instance = new MingleRestService();
      instance.initiateService(url, userName, password, project, userPattern, supportsWikiStyleComment);
    }
    else {
      // Check if the arguments match the instance. If not overwrite the instance.
      if (url != instance.url || userName.equals(instance.userName) || password.equals(instance.password) || 
          project.equals(instance.project) || userPattern.equals(instance.userPattern) || 
          supportsWikiStyleComment != instance.supportsWikiStyleComment ) {
        instance = null;
        instance = new MingleRestService();
        instance.initiateService(url, userName, password, project, userPattern, supportsWikiStyleComment);
      }
    }
    return instance;
  }

  public static MingleRestService getInstance() throws IllegalStateException {
    if ( instance == null ) {
      throw new IllegalStateException("Service is not yet initialized. To initialize more arguments are required.");
    }
    else return instance;
  }*/

  /**
   * Gets the effective {@link MingleRestService} associated with the given project.
   *
   * @return null
   *      if no such was found.
   */
  public static MingleRestService get(AbstractProject<?,?> p) {
      MingleProjectProperty jpp = p.getProperty(MingleProjectProperty.class);
      if(jpp!=null) {
          MingleRestService site = jpp.getSite();
          if(site!=null)
              return site;
      }
      // none is explicitly configured. try the default ---
      // if only one is configured, that must be it.
      MingleRestService[] sites = MingleProjectProperty.DESCRIPTOR.getSites();
      if(sites.length==1) return sites[0];
      return null;
  }

  public String getName() {
    return url.toExternalForm();
  }

  public URL getUrl() {
    return url;
  }

/**
 * Generates a URL for the mingle REST call.
 * 
 * @return URL returns a URL including username and password.
 *
 * @throws MalformedURLException thrown if there is a error inside any part of the URL.
 */
  public URL generateRestUrl(String action) throws MalformedURLException {
    String url_s;
    url_s = url.getProtocol()+"://"+userName+":"+password+"@"+url.getHost()+":"+url.getPort();
    if (! "".equals(url.getPath()) ) {
      url_s += "/"+url.getPath();
    }
    url_s += "/api/v2/projects/"+project+"/"+action;
    return new URL(url_s);
  }

/**
 * Generates a URL for link to the mingle system. The user have to be logged in to see the card.
 * 
 * @return URL returns a URL to the mingle system.
 *
 * @throws MalformedURLException thrown if there is a error inside any part of the URL.
 */
  public URL getCardUrl(int cardnumber) throws MalformedURLException {
    String url_s;
    String protocol = url.getProtocol();
    if ( !(protocol.equals("http") || protocol.equals("https")) ) protocol = "http";
    url_s = protocol+"://"+url.getHost()+":"+url.getPort();
    if (! "".equals(url.getPath()) ) {
      url_s += "/"+url.getPath();
    }
    url_s += "/projects/"+project+"/cards/"+cardnumber;
    return new URL(url_s);
  }


/**
 * Gets a mingle card by it's unique number.
 *
 * @param int number The unique number of the requested card.
 * 
 * @return MingleCard Returns a mingle card by it's unique number. Returns null if the request failed or the URL is wrong.
 */
  public MingleCard getCard(int number) {
    String xml;

    try {
      xml = doMingleCall(generateRestUrl("cards/"+number+".xml"), "GET" , null);
    } catch (MalformedURLException e) {
      return null;
    }

    // convert XML to some kind of useful MingleCart or MingleSomething-object using XStream:
    MingleCard card = (MingleCard)xstream.fromXML(xml);
    return card;
  }

/**
 * Overrides a card on the mingle server with the given card.
 *
 * @param number The unique number of the requested card.
 * @param card The new mingle card that should replace the version on the mingle server.
 *
 * @trows IllegalArgumentException throws an IllegalArgumentException if any of the passed parameters is invalid.
 */
  public void updateCardByNumber(int number, MingleCard card) throws IllegalArgumentException {
    // Converts a MingleCard to a XML String.

    String xml = xstream.toXML(card);
    try {
      URL url = new URL (doMingleCall(generateRestUrl("cards/"+number+".xml"), "PUT", xml));
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException();
    }
  }

/**
 * Creates a new empty card on the mingle server. The simple way, close to the Mingle API.
 *
 * @param String name The name of the new mingle card that should be created on the server.
 * @param String cardtype The cardtype of the new mingle card that should be created on the server.
 *
 * @return URL The URL of the new created card will be returned.
 *
 * @throws MalformedURLException thrown if the returned URL by mingle is invalid.
 */
  public URL createEmptyCard(String name, String cardtype) throws MalformedURLException {
    // generates XML-string for card creation:
    String xml = "<card><name>"+name+"</name><card_type_name>"+cardtype+"</card_type_name></card>";

    // creates a new card:
    URL url = new URL(doMingleCall(generateRestUrl("cards.xml"), "POST", xml));
    
    return url;
  }

/**
 * Creates a new card on the mingle server.
 *
 * @param MingleCard card The new mingle card that should be created on the server.
 *
 * @return int The unique card number of the new created card will be returned. Returns -1 if the creation failed.
 */
  public int createCard(MingleCard card) {

    URL url;
    try {
      // creates a new card:
      url = createEmptyCard(card.getName(), card.getCardtype());
    } catch (MalformedURLException e) {
      return -1;
    }
    
    // get cardnumber out of url:
    String urlpath = url.getPath();
    int cardnumber = Integer.parseInt(urlpath.substring(urlpath.lastIndexOf("/cards/")+7, urlpath.lastIndexOf(".xml")));
    // int cardnumber = (int)urlpath.substring(urlpath.lastIndexOf("/cards/")+7, urlpath.lastIndexOf(".xml") - (urlpath.lastIndexOf("/cards/")+7));
    
    try {
      // updates the new created card with the passed content:
      updateCardByNumber(cardnumber, card);
    } catch (IllegalArgumentException e) {
      deleteCardByNumber(cardnumber);
      return -1;
    }

    return cardnumber;
  }

  public void deleteCardByNumber(int number) {
    try {
      URL url = new URL (doMingleCall(generateRestUrl("cards/"+number+".xml"), "DELETE", null));
    } catch (MalformedURLException e) {
      // nix
    }
    //TODO: delte card in local Java cache... but there is no cache yet.
  }

  //TODO: Do we need: Method getListOfCards(view, page, filters[], sort, order, tagged_with ) and so on?


/**
 * Performs a REST call on the mingle server and returns a XML string if we requested a ressource or a link
 * if we updated or created a ressource on the Mingle system.
 *
 * @param url URL The url that represents the RESTful url for the mingle server.
 * @param method String method This will set the used HTTP method. Only GET, POST, PUT or DELETE are supported.
 * @param xml String A string that represents a MingleObject in XML form or null if just requesting data.
 * 
 * @return String XML object that can be parsed as a MingleObject if this was requested or a URL to the 
 *                ressource which has just been updated. Returns an empty string if an IO error occurs.
 */
  public String doMingleCall(URL url, String method, String xml) {
    // Default HTTP method is GET:
    if (method == null) method = "GET";

    String resultString = "";

    try {
      // Set up connection:
      //HttpURLConnection connection = new HttpURLConnection(url);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setDoInput(true);
      if (xml != null) connection.setDoOutput(true);
      else method = "GET"; // if nothing to update/delete is given it's obviously that we should use GET
    
      // Checks for valid Http method. Mingle supports: GET, POST, PUT or DELETE.
      if (method == "GET" || method == "POST" || method == "PUT" || method == "DELETE") {
        connection.setRequestMethod(method);
      }
      else throw new ProtocolException();
      connection.setFollowRedirects(true);
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // maybe with "; charset=utf-8" in the end?
      connection.connect();
      // Output stuff:
      if (xml != null) {
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(xml);
        out.close();
      }
    
      // Input stuff:
      InputStream is = connection.getInputStream();
      // convert InputStream to String
      Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A"); // or "\\Z" ?
      if (scanner.hasNext()) {
        try {
          resultString = scanner.next();
        } catch (java.util.NoSuchElementException e) {
          resultString = "";
        }
      }

      /**
       * "Result: If you were authorized to perform the operation, and the operation succeeded, 
       * you will be returned a LOCATION ATTRIBUTE in the http header of the response, which 
       * is a URL to the updated resource."
       */
  
      //get the LOCATION ATTRIBUTE from the HTTP Header:
      if (resultString == "") resultString = connection.getHeaderField("Location");

    }
    catch (IOException e) {
      return "";
    }

    return resultString;
  }

  /**
   * Gets the user-defined issue pattern if any.
   * 
   * @return the pattern or null
   */
  public Pattern getUserPattern() {
    if (userPattern == null) {
      return null;
    }
    
    if (userPat == null) {
      // We don't care about any thread race- or visibility issues here.
      // The worst thing which could happen, is that the pattern
      // is compiled multiple times.
      Pattern p = Pattern.compile(userPattern);
      userPat = p;
    }
    return userPat;
  }

  public Pattern getCardPattern() {
    if (getUserPattern() != null) {
      return getUserPattern();
    }
    
    return DEFAULT_CARD_PATTERN;
  } 


  // DESCRIPTOR:
  @Extension
  public static class DescriptorImpl extends Descriptor<MingleRestService> {

    @Override
    public String getDisplayName() {
      return "Mingle Rest Service";
    }
    

    /**
     * Checks if the content inside the URL contains the given string.
     */
    private boolean findTextInUrl(URL url, String text) throws IOException {
      // opens a http connection to url
      String resultString = "";
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setFollowRedirects(true);
      connection.setDoInput(false);
      connection.connect();
  
      // save response
      InputStream is = connection.getInputStream();
      Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A"); // or "\\Z" ?
      if (scanner.hasNext()) {
        try {
          resultString = scanner.next();
        } catch (java.util.NoSuchElementException e) {
          resultString = "";
        }
      }
      
      // actual check
      if (resultString.indexOf(text) != -1)  return true;
      return false;
    }


    /**
     * Checks if the Mingle URL is accessible and exists.
     */
    public FormValidation doUrlCheck(@QueryParameter final String value)
      throws IOException {
      // this can be used to check existence of any file in any URL, so
      // admin only
      if (!Hudson.getInstance().hasPermission(Hudson.ADMINISTER))
        return FormValidation.ok();

      return new FormValidation.URLCheck() {
        @Override
        protected FormValidation check() throws IOException {
          String url = Util.fixEmpty(value);
          if (url == null) {
            return FormValidation.error("The Mingle URL is a mandatory field!");
          }

          // normalize url with "/" at the end:
          if (url.charAt( url.length()-1) != '/') {
            url += "/";
          }
          
          // call urls to check if mingle can be reached
          try {
            URL loginURL = new URL(url + "/profile/login");
            // checks if target url contains the mingle login page
            if (!findTextInUrl(loginURL, "<title>Login Profile - Mingle</title>") )
              return FormValidation.error("This is a valid URL but it doesnt look like mingle.");
            URL restUrl = new URL(url + "/api/v2/projects.xml");
            if (!findTextInUrl(restUrl, "Incorrect username or password.") )
              return FormValidation.error("Couln't access the mingle API on the given URL. Please check if the Rest-API is activated.");
            return FormValidation.ok();
          } catch (IOException e) {
            LOGGER.log(Level.WARNING,"Unable to connect to " + url, e);
            return FormValidation.error("Unable to connect to " + url);
          }
        }
      }.check();
    }
    
    public FormValidation doCheckUserPattern(@QueryParameter String value) throws IOException {
      String userPattern = Util.fixEmpty(value);
      if (userPattern == null) {// userPattern not entered yet
        return FormValidation.ok();
      }
      try {
        Pattern.compile(userPattern);
        return FormValidation.ok();
      } catch (PatternSyntaxException e) {
        return FormValidation.error(e.getMessage());
      }
    }
    
    /**
     * Checks if the user name and password are valid.
     */
    public FormValidation doValidate( @QueryParameter String userName,
                                      @QueryParameter String url,
                                      @QueryParameter String password,
                                      @QueryParameter String project)
                throws IOException {
        url = Util.fixEmpty(url);
        if (url == null) {// URL not entered yet
          return FormValidation.error("No URL given");
        }
        MingleRestService serv = new MingleRestService(new URL(url), userName, password, null, null, false);
        try {
          // Check if project exists:
          URL url2 = serv.url;
          URL projectsURL = new URL(url2.getProtocol()+"://"+userName+":"+password+"@"+
                               url2.getHost()+":"+url2.getPort()+"/"+url2.getPath()+"api/v2/projects.xml");
          if (findTextInUrl(projectsURL, "Incorrect username or password.")) {
            LOGGER.log(Level.WARNING, "Failed to login to mingle at " + url);
            return FormValidation.error("Failed to login to mingle at " + url);
          }
          if (!findTextInUrl(projectsURL, project)) {
            LOGGER.log(Level.WARNING, "The project name \""+project+"\" can't be found on the mingle server.");
            return FormValidation.error("The project name \""+project+"\" can't be found on the mingle server.");
          }
          return FormValidation.ok("Success");
        } catch (IOException e) {
          LOGGER.log(Level.WARNING, "Failed to login to mingle at " + url, e);
          return FormValidation.error(e.getMessage());
        }

    }

  }
    
  private static final Logger LOGGER = Logger.getLogger(MingleRestService.class.getName());

}