package mingleplugin;

// Mingle api doc says:
// property: Resource; name and a current value for each card property defined for current
// card's card type are listed; Data type will depend on the property while property name 
// is always String. The property also includes attributes about the property type_description
// and whether or not it is hidden.

//TODO: the value must be a Object arcording to the type_desscription. It's a bit ugly to parse but a real DATE value would give a lot of benefit.

import java.util.Arrays;

class MingleCardProperty extends MingleObject{
  
  private String mingleObjectType = "MingleCardProperty";

  private String name;
  private String value;
  //private transient Object value_object;

  // possible values: Managed text list, Allow any text, Managed number list, Allow any number, Automatically generated from the team list, Date, Formula, Card
  private String type_description;
  private boolean hidden;

  public String getName()
  {
    return name;
  }

  public void setName( String name )
  {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue( String obj ) {
    this.value = obj;
  }

  public String getTypeDescription()
  {
    return type_description;
  }

  public void setTypeDescription( String description )
  {
    this.type_description = description;
  }

 public boolean getHidden()
  {
    return hidden;
  }

  public void setHidden( boolean hidden )
  {
    this.hidden = hidden;
  }

  // The attributes hidden and type_description are optional
  MingleCardProperty(String name, String value, String type_description, boolean hidden) {
    this.name = name;
    this.value = value;
    this.type_description = type_description;
    this.hidden = hidden;
  }

  MingleCardProperty(String name, String value, boolean hidden) {
    this.name = name;
    this.value = value;
    this.hidden = hidden;
  }

  MingleCardProperty(String name, String value) {
    this.name = name;
    this.value = value;
  }

}