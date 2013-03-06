package mingleplugin;

import java.util.Date;

class MingleProject extends MingleObject{
  
  private String mingleObjectType = "MingleProject";

  private String name;
  private String identifier;
  private String description;
  private String[] keywords;
  private Boolean template;
  // set by mingle server:
  private final Date created_at;
  private final Date updated_at;
  private final MingleUser created_by;
  private final MingleUser modified_by;
  private final String date_format;

  public String getName()
  {
    return name;
  }

  public void setName( String name )
  {
    this.name = name;
  }

  public String getIdentifier()
  {
    return identifier;
  }

  public void setIdentifier(String identifier)
  {
    this.identifier = identifier;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription( String name )
  {
    this.description = description;
  }  

  MingleProject(String name, String identifier, String description, String[] keywords,
                Boolean template, Date created_at, Date updated_at, MingleUser created_by,
                MingleUser modified_by, String date_format) {
    this.name = name;
    this.identifier = identifier;
    this.description = description;
    this.keywords = keywords;
    this.template = template;
    this.created_at = created_at;
    this.updated_at = updated_at;
    this.created_by = created_by;
    this.modified_by = modified_by;
    this.date_format = date_format;
  }

  // If created from a MingleCard
  MingleProject(String name, String identifier) {
    this.name = name;
    this.identifier = identifier;
    this.description = null;
    this.keywords = null;
    this.template = null;
    this.created_at = null;
    this.updated_at = null;
    this.created_by = null;
    this.modified_by = null;
    this.date_format = null;
  }

}