package mingleplugin;

import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.util.Date;
import java.lang.Integer;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

class MingleCard extends MingleObject implements Comparable<MingleCard> {

  public String mingleObjectType = "MingleCard";

  public String name;
  public String description;
  public String card_type;
  
  public MingleProject project;
  public MingleCardProperty[] properties;
  public String tags; // comma-delimited list of tags
  public URL rendered_description; // Resource; Link to rendered card description as HTML.
  // read only, must be set inside the constructor and can't be changed:
  public int id;
  public int number;
  public int version;
  public MingleUser created_by;
  public int project_card_rank;
  // the 2 Date-fields are Strings here to make XML parsing possible:
  public String created_on;
  private transient Date created_on_parsed;
  public String modified_on;
  private transient Date modified_on_parsed;

  // getter and setter:
  public String getName()
  {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  String getCardtype() {
    return card_type;
  }
  void setCardtype(String arg) {
    this.card_type = arg;
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
    if (created_on_parsed == null)
      this.created_on_parsed = parseDateFromString(created_on);
    return created_on_parsed;
  }

  public Date getModifiedOn() {
    if (modified_on_parsed == null)
      this.modified_on_parsed = parseDateFromString(modified_on);
    return modified_on_parsed;
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

  void setProjectCardRank(int rank) {
    this.project_card_rank = rank;
  }

  int getProjectCardRank() {
    return project_card_rank;
  }

  public int compareTo(MingleCard that) {
    return Integer.valueOf(this.number).compareTo(Integer.valueOf(that.number));
  }

  // Constructor only with essential constants
  public MingleCard (int id, int number, int version, String created_on, String modified_on, MingleUser created_by) {
    this.id = id;
    this.number = number;
    this.version = version;
    this.created_on = created_on;
    this.modified_on = modified_on;
    this.created_by = created_by;
  }

  // Constructor with all possible stuff
  public MingleCard (int id, int number, int version, String created_on, String modified_on, MingleUser created_by,
                     String name, String description, String cardtype, MingleProject project, MingleCardProperty[] properties, 
                     String tags, URL rendered_description, int project_card_rank) {
    this.id = id;
    this.number = number;
    this.version = version;
    this.created_on = created_on;
    this.modified_on = modified_on;
    this.created_by = created_by;
    this.name = name;
    this.description = description;
    this.card_type = cardtype;
    this.project = project;
    this.properties = properties;
    this.tags = tags;
    this.rendered_description = rendered_description;
    this.project_card_rank = project_card_rank;
  }

  // Constructor with all possible stuff
  public MingleCard (int id, int number, int version, Date created_on, Date modified_on, MingleUser created_by,
                     String name, String description, String cardtype, MingleProject project, MingleCardProperty[] properties, 
                     String tags, URL rendered_description, int project_card_rank) {
    this.id = id;
    this.number = number;
    this.version = version;
    this.created_on_parsed = created_on;
    this.modified_on = getStringFromDate(created_on);
    this.modified_on_parsed = modified_on;
    this.modified_on = getStringFromDate(modified_on);
    this.created_by = created_by;
    this.name = name;
    this.description = description;
    this.card_type = cardtype;
    this.project = project;
    this.properties = properties;
    this.tags = tags;
    this.rendered_description = rendered_description;
    this.project_card_rank = project_card_rank;
  }

  private Date parseDateFromString(String dateString) {
    // mingle uses the ISO 8601 date format:
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    Date date;
    try {
      date = formatter.parse(dateString);
    } catch (ParseException e) {
      return null;
    }
    return date;
  }
  
  private String getStringFromDate(Date date) {
    // mingle uses the ISO 8601 date format:
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    return formatter.format(date);
  }
}