import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;

/**
 * Utilities for our simple implementation of JSON.
 *
 * @author Samuel Rebelsky, Reed Colloton, Noah Mendola
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

  /**
   * Parse JSON from a reader, keeping track of the current position
   */
  static JSONValue parseKernel(Reader source) throws ParseException, IOException {
    int ch = skipWhitespace(source);
    if (-1 == ch) {
      throw new ParseException("Unexpected end of file", pos);
    } // if
    return switch (ch) {
      case '\"' -> parseString(source);
      case '[' -> parseArray(source);
      case '{' -> parseHash(source);
      case '}', ']' -> null;
      default -> parseNumberOrConstant(ch, source);
    }; // switch
  } // parseKernel

  /**
   * Parse a JSONInteger, JSONReal, or JSONConstant
   */
  static JSONValue parseNumberOrConstant(int ch, Reader source) throws IOException, ParseException {
    StringBuilder chars = new StringBuilder();
    chars.append((char) ch);
    do {
      ch = source.read();
      chars.append((char) ch);
    } while (!isWhitespace(ch) && ch != ',');
    int lastChar = chars.length() - 1;
    chars.deleteCharAt(lastChar);
    String str = chars.toString();
    try {
      int n = Integer.parseInt(str);
      return new JSONInteger(n);
    } catch (NumberFormatException ignored) {
    } // try/catch
    try {
      BigDecimal n = new BigDecimal(str);
      return new JSONReal(n);
    } catch (NumberFormatException ignored) {
    } // try/catch
    return switch (str) {
      case "true" -> JSONConstant.TRUE;
      case "false" -> JSONConstant.FALSE;
      case "null" -> JSONConstant.NULL;
      default -> throw new ParseException("Invalid number or constant", pos);
    };
  } // parseNumberOrConstant

  /**
   * Parse a JSONString
   */
  private static JSONString parseString(Reader source) throws IOException {
    int ch;
    boolean escaped = false;
    StringBuilder str = new StringBuilder();
    do {
      ch = source.read();
      if (ch == '\\') escaped = !escaped;
      str.append((char) ch);
    } while (ch != '"' || escaped);
    str.deleteCharAt(str.length() - 1);
    return new JSONString(str.toString());
  } // parseString

  /**
   * Parse a JSONArray
   */
  private static JSONArray parseArray(Reader source) throws IOException, ParseException {
    JSONArray jsonArray = new JSONArray();
    JSONValue value = parseKernel(source);
    while (value != null) {
      jsonArray.add(value);
      value = parseKernel(source);
    } // while
    return jsonArray;
  } // parseArray()

  /**
   * Parse JSONHash
   */
  private static JSONValue parseHash(Reader source) throws ParseException, IOException {
    JSONHash jsonHash = new JSONHash();
    int ch;
    while (skipWhitespace(source) != '}') {
      JSONString key = parseString(source);
      if (skipWhitespace(source) != ':') {
        throw new ParseException("Expected ':'", pos);
      } // if
      JSONValue value = parseKernel(source);
      jsonHash.set(key, value);
    } // while
    return jsonHash;
  } // parseHash()

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
