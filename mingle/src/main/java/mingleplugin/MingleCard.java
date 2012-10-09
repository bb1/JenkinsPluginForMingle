package mingleplugin;

import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import javax.xml.bind.annotation;

@XmlRootElement
class MingleCard extends MingleObject {
  private String name;
  private String description;
  private String cardtype;
  private Map<String, String> project = new HashMap<String, String>(); // project: Resource; name and identifier of a project a card belongs to
  // not supporting everything: private Map<String, String>[] properties = new HashMap<String, String>()[]; //properties: Array; property: Resource; name and a current value for each card property defined for current card's card type are listed; Data type will depend on the property while property name is always String. The property also includes attributes about the property type_description and whether or not it is hidden.
  private MingleCardProperty[] properties;
  private String tags; // comma-delimited list of tags
  private URL rendered_description; // Resource; Link to rendered card description as HTML.
  // read only:
  private final int id;
  private final int number;
  private final int version;
  private final Date created_on;
  private final Date modified_on;
  private final Map<String, Object> created_by = new HashMap<String, Object>(); // created_by: name and login id of user who created the card


  public String getName()
  {
    return name;
  }

  public void setName( String name )
  {
    this.name = name;
  }


  public Date getCreatedOn()
  {
    return created_on;
  }

  public Date getModifiedOn()
  {
    return modified_on;
  }


  // TODO: all the setters and getters
  // TODO: constructor?
}