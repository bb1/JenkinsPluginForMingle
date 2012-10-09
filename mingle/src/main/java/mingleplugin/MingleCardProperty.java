package mingleplugin;

// Mingle api doc says:
// property: Resource; name and a current value for each card property defined for current
// card's card type are listed; Data type will depend on the property while property name 
// is always String. The property also includes attributes about the property type_description
// and whether or not it is hidden.

class MingleCardProperty {
  private String name;
  private Object value;
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

  public Object getValue() {
    return value;
  }

  public void setValue( Object obj ) {
    if (obj instanceof String || obj instanceof int || obj instanceof Array) { // checks for valid data types. incomplete!
      this.value = obj;
    }
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

  // TODO: Constructors

}