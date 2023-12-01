import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

/**
 * Utilities for our simple implementation of JSON.
 */
public class JSON {
  // +---------------+-----------------------------------------------
  // | Static fields |
  // +---------------+

  /**
   * The current position in the input.
   */
  static int pos;

  // +----------------+----------------------------------------------
  // | Static methods |
  // +----------------+

  /**
   * Parse a string into JSON.
   */
  public static JSONValue parse(String source) throws ParseException, IOException {
    return parse(new StringReader(source));
  } // parse(String)

  /**
   * Parse a file into JSON.
   */
  public static JSONValue parseFile(String filename) throws ParseException, IOException {
    FileReader reader = new FileReader(filename);
    JSONValue result = parse(reader);
    reader.close();
    return result;
  } // parseFile(String)

  /**
   * Parse JSON from a reader.
   */
  public static JSONValue parse(Reader source) throws ParseException, IOException {
    pos = 0;
    JSONValue result = parseKernel(source);
    if (-1 != skipWhitespace(source)) {
      throw new ParseException("Characters remain at end", pos);
    }
    return result;
  } // parse(Reader)

  // +---------------+-----------------------------------------------
  // | Local helpers |
  // +---------------+

  static JSONValue parseNumber(Reader source) {
    int pos = 0;
    int ch;
    do {
      ch = source.read(); 
    } while (ch)

  }

  static JSONValue parseConstant(int ch, Reader source) {
    StringBuilder jsonVal = new StringBuilder(ch);
    do {
      ch = source.read();
      jsonVal.append(ch);
    } while (isWhitespace(ch) == false);
    String JVstring = jsonVal.toString();
    if (JVstring == null) {
        return 
    }
    try {
        int I = Integer.parseInt(strNum);
        return new JSONInteger(I);
    } catch (NumberFormatException nfe) {}
    return 
 
    };

  /**
   * Parse JSON from a reader, keeping track of the current position
   */
  static JSONValue parseKernel(Reader source) throws ParseException, IOException {
    int ch;
    ch = skipWhitespace(source);
    if (-1 == ch) {
      throw new ParseException("Unexpected end of file", pos);
    } // if
    switch (ch) {
      case '{' -> parseHash(source);
      case '\"' -> parseString(source);
      case '[' -> parseArray(source);
      case 'T', 'F', 'N' -> parseConstant(source);
      default -> {
        if (ch <= '9' && ch >= '0')
          parseNumber(ch, source);
        else
          throw new ParseException("Unexpected character: " + ch, pos);
        }
    } // switch
    throw new ParseException("Unimplemented", pos);
  } // parseKernel

  /**
   * Get the next character from source, skipping over whitespace.
   */
  static int skipWhitespace(Reader source) throws IOException {
    int ch;
    do {
      ch = source.read();
      ++pos;
    } while (isWhitespace(ch));
    return ch;
  } // skipWhitespace(Reader)

  /**
   * Determine if a character is JSON whitespace (newline, carriage return,
   * space, or tab).
   */
  static boolean isWhitespace(int ch) {
    return (' ' == ch) || ('\n' == ch) || ('\r' == ch) || ('\t' == ch);
  } // isWhiteSpace(int)

} // class JSON
