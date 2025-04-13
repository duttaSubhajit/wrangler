<<<<<<< HEAD
/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;

/**
 * Represents a byte size value with units (e.g., 10KB, 5MB).
 */
@PublicEvolving
public class ByteSize implements Token {
=======
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
>>>>>>> 4ce7097ca8008f60b27e01c9c52d9a50cc025319
  private final long bytes;
  private final String original;

  public ByteSize(String value) {
<<<<<<< HEAD
    this.original = value.trim();
    this.bytes = parseByteSize(this.original);
  }

  private long parseByteSize(String value) {
    String numStr = value.replaceAll("[^0-9.]", "");
    String unitStr = value.replaceAll("[0-9.\\s]", "").toUpperCase();
    
    double num = Double.parseDouble(numStr);
    
    switch (unitStr) {
      case "B":
        return (long) num;
      case "KB":
        return (long) (num * 1024);
      case "MB":
        return (long) (num * 1024 * 1024);
      case "GB":
        return (long) (num * 1024 * 1024 * 1024);
      case "TB":
        return (long) (num * 1024 * 1024 * 1024 * 1024);
      default:
        throw new IllegalArgumentException(
          String.format("Invalid byte size unit '%s'. Supported units are B, KB, MB, GB, TB.", unitStr));
    }
=======
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
>>>>>>> 4ce7097ca8008f60b27e01c9c52d9a50cc025319
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
<<<<<<< HEAD
    JsonObject object = new JsonObject();
    object.addProperty("type", type().name());
    object.addProperty("value", bytes);
    object.addProperty("original", original);
    return object;
  }

  public long getBytes() {
    return bytes;
  }
}
=======
    JsonObject obj = new JsonObject();
    obj.addProperty("type", "BYTE_SIZE");
    obj.addProperty("value", original);
    obj.addProperty("bytes", bytes);
    return obj;
  }
}
>>>>>>> 4ce7097ca8008f60b27e01c9c52d9a50cc025319
