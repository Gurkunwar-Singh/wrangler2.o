package io.cdap.wrangler.api.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

/**
 * Token implementation representing a time duration value.
 */
public class TimeDuration implements Token {

  private final String value;

  public TimeDuration(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", type().name());
    object.addProperty("value", value);
    return object;
  }

  /**
   * Converts the value of this TimeDuration token to its milliseconds representation.
   *
   * @return the number of milliseconds
   */
  public long getMilliseconds() {
    // Assuming the value is in a string format like "10s", "2m", "100ms", etc.
    // Implement conversion logic here based on your requirements.
    // Example: "10s" = 10 * 1000, "2m" = 2 * 60 * 1000, "100ms" = 100

    String lowerValue = value.toLowerCase();
    if (lowerValue.endsWith("ms")) {
      return Long.parseLong(value.replace("ms", "").trim());
    } else if (lowerValue.endsWith("s")) {
      return Long.parseLong(value.replace("s", "").trim()) * 1000;
    } else if (lowerValue.endsWith("m")) {
      return Long.parseLong(value.replace("m", "").trim()) * 60 * 1000;
    } else if (lowerValue.endsWith("h")) {
      return Long.parseLong(value.replace("h", "").trim()) * 60 * 60 * 1000;
    } else {
      // Default to seconds if no unit is specified
      return Long.parseLong(value) * 1000;
    }
  }
}
