package mingleplugin;

class MingleUser extends MingleObject{
  
  private String mingleObjectType = "MingleUser";

  private String name;
  private String login;


  public String getName()
  {
    return name;
  }

  public void setName( String name )
  {
    this.name = name;
  }

  public String getLogin()
  {
    return login;
  }

  public void setLogin(String login)
  {
    this.login = login;
  }

  MingleCardProperty(String name, String login) {
    this.name = name;
    this.login = login;
  }

}