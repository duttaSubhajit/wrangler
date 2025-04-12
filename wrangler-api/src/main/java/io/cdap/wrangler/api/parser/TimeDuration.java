package io.cdap.wrangler.api.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a time duration token like 150ms, 5s, 2m, etc.
 */
public class TimeDuration implements Token {
  private static final Pattern PATTERN = Pattern.compile("(?i)(\\d+)(ms|s|m|h)");
  private final long milliseconds;
  private final String original;

  public TimeDuration(String value) {
    this.original = value.trim().toLowerCase();
    Matcher matcher = PATTERN.matcher(this.original);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid time duration format: " + value);
    }

    long number = Long.parseLong(matcher.group(1));
    String unit = matcher.group(2);

    switch (unit) {
      case "ms":
        this.milliseconds = number;
        break;
      case "s":
        this.milliseconds = number * 1000;
        break;
      case "m":
        this.milliseconds = number * 60 * 1000;
        break;
      case "h":
        this.milliseconds = number * 60 * 60 * 1000;
        break;
      default:
        throw new IllegalArgumentException("Unknown unit: " + unit);
    }
  }

  public long getMilliseconds() {
    return milliseconds;
  }

  @Override
  public Object value() {
    return milliseconds;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    JsonObject obj = new JsonObject();
    obj.addProperty("type", "TIME_DURATION");
    obj.addProperty("value", original);
    obj.addProperty("milliseconds", milliseconds);
    return obj;
  }
}
