package io.cdap.wrangler.api.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a token of ByteSize like 10KB, 20MB, etc.
 */
public class ByteSize implements Token {
  private static final Pattern PATTERN = Pattern.compile("(?i)(\\d+)([KMGT]?B)");
  private final long bytes;
  private final String original;

  public ByteSize(String value) {
    this.original = value.trim().toUpperCase();
    Matcher matcher = PATTERN.matcher(this.original);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid byte size format: " + value);
    }

    long number = Long.parseLong(matcher.group(1));
    String unit = matcher.group(2);

    switch (unit) {
      case "KB":
        this.bytes = number * 1024;
        break;
      case "MB":
        this.bytes = number * 1024 * 1024;
        break;
      case "GB":
        this.bytes = number * 1024 * 1024 * 1024;
        break;
      case "TB":
        this.bytes = number * 1024L * 1024L * 1024L * 1024L;
        break;
      case "B":
        this.bytes = number;
        break;
      default:
        throw new IllegalArgumentException("Unknown unit: " + unit);
    }
  }

  public long getBytes() {
    return bytes;
  }

  @Override
  public Object value() {
    return bytes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    JsonObject obj = new JsonObject();
    obj.addProperty("type", "BYTE_SIZE");
    obj.addProperty("value", original);
    obj.addProperty("bytes", bytes);
    return obj;
  }
}
