package mingleplugin;

import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.util.Date;

class MingleCard extends MingleObject {

  private String mingleObjectType = "MingleCard";

  private String name;
  private String description;
  private String cardtype;
  
  private MingleProject project;
  private MingleCardProperty[] properties;
  private String tags; // comma-delimited list of tags
  private URL rendered_description; // Resource; Link to rendered card description as HTML.
  // read only, must be set inside the constructor and can't be changed:
  private final int id;
  private final int number;
  private final int version;
  private final Date created_on;
  private final Date modified_on;
  private final MingleUser created_by;

  // getter and setter:
  public String getName()
  {
    return name;
  }

  public void setName( String name )
  {
    this.name = name;
  }

  String getCardtype() {
      return cardtype;
  }
  void setCardtype(String arg) {
      this.cardtype = arg;
  }

  String getDescription() {
      return description;
  }
  void setDescription(String arg) {
      this.description = arg;
  }
  
  MingleProject getproject() {
      return project;
  }
  void setproject(MingleProject arg) {
      this.project = arg;
  }

  public Date getCreatedOn()
  {
    return created_on;
  }

  public Date getModifiedOn()
  {
    return modified_on;
  }
  
  int getId() {
    return id;
  }

  int getVersion() {
    return version;
  }

  // Constructor only with essential constants
  public MingleCard (int id, int number, int version, Date created_on, Date modified_on, MingleUser created_by) {
    this.id = id;
    this.number = number;
    this.version = version;
    this.created_on = created_on;
    this.modified_on = modified_on;
    this.created_by = created_by;
  }

  // Constructor with all possible stuff
  public MingleCard (int id, int number, int version, Date created_on, Date modified_on, MingleUser created_by,
                     String name, String description, String cardtype, MingleProject project, MingleCardProperty[] properties, 
                     String tags, URL rendered_description) {
    this.id = id;
    this.number = number;
    this.version = version;
    this.created_on = created_on;
    this.modified_on = modified_on;
    this.created_by = created_by;
    this.name = name;
    this.description = description;
    this.cardtype = cardtype;
    this.project = project;
    this.properties = properties;
    this.tags = tags;
    this.rendered_description = rendered_description;
  }
  
}