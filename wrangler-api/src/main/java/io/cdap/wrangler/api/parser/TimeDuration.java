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
 * Represents a time duration value with units (e.g., 100ms, 5s).
 */
@PublicEvolving
public class TimeDuration implements Token {
  private final long milliseconds;
  private final String original;

  public TimeDuration(String value) {
    this.original = value.trim();
    this.milliseconds = parseTimeDuration(this.original);
  }

  private long parseTimeDuration(String value) {
    String numStr = value.replaceAll("[^0-9.]", "");
    String unitStr = value.replaceAll("[0-9.\\s]", "").toLowerCase();
    
    double num = Double.parseDouble(numStr);
    
    switch (unitStr) {
      case "ms":
        return (long) num;
      case "s":
        return (long) (num * 1000);
      case "m":
        return (long) (num * 1000 * 60);
      case "h":
        return (long) (num * 1000 * 60 * 60);
      case "d":
        return (long) (num * 1000 * 60 * 60 * 24);
      default:
        throw new IllegalArgumentException(
            String.format("Invalid time unit '%s'. Supported units are ms, s, m, h, d.", unitStr));
    }
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
    JsonObject object = new JsonObject();
    object.addProperty("type", type().name());
    object.addProperty("value", milliseconds);
    object.addProperty("original", original);
    return object;
  }

  public long getMilliseconds() {
    return milliseconds;
  }

  public long getSeconds() {
    return milliseconds / 1000;
  }
}