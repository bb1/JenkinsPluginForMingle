package mingleplugin;

// Stuff that all MingleObjects have in common
class MingleObject {
  private String mingleObjectType = "MingleObject"; // can be MingleCard, MingleCardProperty, MingleObject

  public String getMingleObjectType() {
    return mingleObjectType;
  }
}