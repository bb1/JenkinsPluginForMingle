package mingleplugin;

class MingleProject extends MingleObject{
  
  private String mingleObjectType = "MingleProject";

  private String name;
  private String identifier;


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

  MingleProject(String name, String identifier) {
    this.name = name;
    this.identifier = identifier;
  }

}