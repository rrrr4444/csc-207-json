import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;

/**
 * JSON hashes/objects.
 * Hashmap functions based on <a href="https://github.com/rrrr4444/csc207-hash-tables/"/>
 *
 * @author Samuel Rebelsky, Reed Colloton, Noah Mendola
 */
public class JSONHash implements JSONValue {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  private final double PROBE_OFFSET = 17;
  private final double LOAD_FACTOR = 0.5;
  private int size = 32;
  KVPair<JSONString, JSONValue>[] hashmap = new KVPair[size];
  private int values = 0;
  Iterator<KVPair<JSONString, JSONValue>> iterator = this.iterator();

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    if (this.values == 0) {
      return "{ }";
    } // if
    StringBuilder chars = new StringBuilder("{ ");
    while (this.iterator.hasNext()) {
      KVPair<JSONString, JSONValue> pair = this.iterator.next();
      chars.append(pair.key()).append(" : ");
      chars.append(pair.value()).append(", ");
    } // for
    int length = chars.length();
    chars.delete(length - 2, length);
    chars.append(" }");
    return chars.toString();
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    return other instanceof JSONHash
            && this.toString().equals(other.toString());
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    if (this.hashmap == null)
      return 0;
    else
      return Arrays.hashCode(this.hashmap);
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
    pen.println(this);
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public Iterator<KVPair<JSONString, JSONValue>> getValue() {
    return this.iterator;
  } // getValue()

  // +-------------------+-------------------------------------------
  // | Hashtable methods |
  // +-------------------+

  /**
   * Get the value associated with a key.
   */
  public Object get(JSONString key) {
    return this.hashmap[find(key)].value();
  } // get(JSONString)

  /**
   * Get all the key/value pairs.
   */
  public Iterator<KVPair<JSONString, JSONValue>> iterator() {
    return new Iterator<>() {
      private int cursor = 0;

      public boolean hasNext() {
        return values != 0;
      } // hasNext()

      public KVPair<JSONString, JSONValue> next() {
        for (int i = cursor; values != 0; i++) {
          if (hashmap[i] != null) {
            KVPair<JSONString, JSONValue> pair = hashmap[i];
            hashmap[i] = null;
            values--;
            this.cursor = i;
            return pair;
          } // if
        } // for
        return null;
      } // next()
    }; // return
  } // iterator()

  /**
   * Set the value associated with a key.
   */
  public void set(JSONString key, JSONValue value) {
    if (this.values > (this.size * LOAD_FACTOR)) expand();
    int index = this.find(key);
    if (this.hashmap[index] == null) {
      ++values;
    } // if
    this.hashmap[index] = new KVPair<>(key, value);
  } // set(JSONString, JSONValue)

  private void expand() {
    KVPair<JSONString, JSONValue>[] old = this.hashmap;
    this.size = this.size * 2;
    this.hashmap = new KVPair[this.size];
    for (KVPair<JSONString, JSONValue> pair : old) {
      set(pair.key(), pair.value());
    } // for
  } // expand()

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return this.size;
  } // size()

  /**
   * Find the index of the entry with a given key. If there is no such entry,
   * return the index of an entry we can use to store that key.
   */
  private int find(JSONString key) {
    int hashCode = Math.abs(key.hashCode()) % this.size;
    while (this.hashmap[hashCode] != null
            || (this.hashmap[hashCode]) != null
            && !this.hashmap[hashCode].key().equals(key)) {
      hashCode += (int) (this.PROBE_OFFSET % this.size);
    } // while
    return hashCode;
  } // find(K)


} // class JSONHash
