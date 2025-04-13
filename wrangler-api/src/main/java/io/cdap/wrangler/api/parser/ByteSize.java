package io.cdap.wrangler.api.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

/**
 * Token implementation representing a byte size value.
 */
public class ByteSize implements Token {

  private final String value;

  public ByteSize(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", type().name());
    object.addProperty("value", value);
    return object;
  }

  /**
   * Converts the value of this ByteSize token to its byte representation.
   *
   * @return the number of bytes
   */
  public long getBytes() {
    // Assuming the value is in a simple string format like "1024", "1MB", etc.
    // Implement conversion logic here based on your requirements.
    // For example, "1KB" = 1024 bytes, "1MB" = 1024*1024 bytes.

    String lowerValue = value.toLowerCase();
    if (lowerValue.endsWith("kb")) {
      return Long.parseLong(value.replace("KB", "").replace("kb", "").trim()) * 1024;
    } else if (lowerValue.endsWith("mb")) {
      return Long.parseLong(value.replace("MB", "").replace("mb", "").trim()) * 1024 * 1024;
    } else if (lowerValue.endsWith("gb")) {
      return Long.parseLong(value.replace("GB", "").replace("gb", "").trim()) * 1024 * 1024 * 1024;
    } else {
      // If no units provided, assume it's a number of bytes
      return Long.parseLong(value);
    }
  }
}
