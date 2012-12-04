package mingleplugin;

import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.util.Date;
import java.lang.Integer;

class MingleCard extends MingleObject implements Comparable<MingleCard> {

  public String mingleObjectType = "MingleCard";

  public String name;
  public String description;
  public String cardtype;
  
  public MingleProject project;
  public MingleCardProperty[] properties;
  public String tags; // comma-delimited list of tags
  public URL rendered_description; // Resource; Link to rendered card description as HTML.
  // read only, must be set inside the constructor and can't be changed:
  public final int id;
  public final int number;
  public final int version;
  public final Date created_on;
  public final Date modified_on;
  public final MingleUser created_by;

  // getter and setter:
  public String getName()
  {
    return name;
  }

  public void setName( String name ) {
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

  public Date getCreatedOn() {
    return created_on;
  }

  public Date getModifiedOn() {
    return modified_on;
  }
 
  int getNumber() {
    return number;
  }

  int getId() {
    return id;
  }

  int getVersion() {
    return version;
  }

  public int compareTo(MingleCard that) {
    return Integer.valueOf(this.number).compareTo(Integer.valueOf(that.number));
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