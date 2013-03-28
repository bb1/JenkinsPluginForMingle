package mingleplugin;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts string dates in ISO 8601 format to Java and back.
 */
public class MingleDateConverter implements Converter {
  private static final Logger LOGGER = Logger.getLogger(MingleDateConverter.class.getName());

  // mingle uses the ISO 8601 date format:
  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

  public boolean canConvert(Class clazz) {
    // This converter is only for Date fields.
    return Date.class.isAssignableFrom(clazz);
  }

  public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
    Date date = (Date) value;
    writer.setValue(formatter.format(date));
  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    // This Log was NEVER displayed. Therefore xstream ignores the DateConverter settings!
    LOGGER.log(Level.INFO, "======== MingleDateConverter started ========");
    Date date;
    try {
      date = formatter.parse(reader.getValue());
    } catch (ParseException e) {
      throw new ConversionException(e.getMessage(), e);
    }
    return date;
  }
}