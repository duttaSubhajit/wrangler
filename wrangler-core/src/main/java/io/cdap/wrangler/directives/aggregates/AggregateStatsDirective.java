// /*
//  * Copyright © 2023 Cask Data, Inc.
//  *
//  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
//  * use this file except in compliance with the License. You may obtain a copy of
//  * the License at
//  *
//  * http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
//  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
//  * License for the specific language governing permissions and limitations under
//  * the License.
//  */



package io.cdap.wrangler.directives.aggregates;

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.*;
import com.google.gson.JsonElement;

import java.util.Collections;
import java.util.List;

@Plugin(type = Directive.Type)
@Name("aggregate-stats")
@Description("Aggregates byte sizes and time durations across rows")
public final class AggregateStatsDirective implements Directive {
    private static final String BYTE_SIZE_COL = "sizeColumn";
    private static final String TIME_COL = "timeColumn";
    private static final String TOTAL_SIZE_COL = "totalSizeColumn";
    private static final String TOTAL_TIME_COL = "totalTimeColumn";
    private static final String OUTPUT_SIZE_UNIT = "sizeUnit";
    private static final String OUTPUT_TIME_UNIT = "timeUnit";

    private String sizeColumn;
    private String timeColumn;
    private String totalSizeColumn;
    private String totalTimeColumn;
    private String sizeUnit = "bytes";
    private String timeUnit = "millis";

    // @Override
    // public UsageDefinition define() {
    //     return UsageDefinition.builder("aggregate-stats")
    //         .setDescription("Aggregates byte sizes and time durations")  // Fixed method name
    //         .withArguments(
    //             new Argument(BYTE_SIZE_COL, TokenType.COLUMN_NAME, "Source column with byte sizes"),
    //             new Argument(TIME_COL, TokenType.COLUMN_NAME, "Source column with time durations"),
    //             new Argument(TOTAL_SIZE_COL, TokenType.COLUMN_NAME, "Target column for size results"),
    //             new Argument(TOTAL_TIME_COL, TokenType.COLUMN_NAME, "Target column for time results"),
    //             new Argument(OUTPUT_SIZE_UNIT, TokenType.TEXT, "Output size unit (B,KB,MB,GB)", true),
    //             new Argument(OUTPUT_TIME_UNIT, TokenType.TEXT, "Output time unit (ms,s,m,h)", true)
    //         )
    //         .build();
    // }

    @Override
    public UsageDefinition define() {
        return UsageDefinition.builder("aggregate-stats")
            .setDescription("Aggregates byte sizes and time durations")
            .withArguments(
                Arguments.of(BYTE_SIZE_COL, TokenType.COLUMN_NAME, "Source column with byte sizes"),
                Arguments.of(TIME_COL, TokenType.COLUMN_NAME, "Source column with time durations"),
                Arguments.of(TOTAL_SIZE_COL, TokenType.COLUMN_NAME, "Target column for size results"),
                Arguments.of(TOTAL_TIME_COL, TokenType.COLUMN_NAME, "Target column for time results"),
                Arguments.of(OUTPUT_SIZE_UNIT, TokenType.TEXT, "Output size unit (B,KB,MB,GB)", true),
                Arguments.of(OUTPUT_TIME_UNIT, TokenType.TEXT, "Output time unit (ms,s,m,h)", true)
            )
            .build();
    }

    @Override
    public void initialize(Arguments args) throws DirectiveParseException {
        this.sizeColumn = ((ColumnName) args.value(BYTE_SIZE_COL)).value();
        this.timeColumn = ((ColumnName) args.value(TIME_COL)).value();
        this.totalSizeColumn = ((ColumnName) args.value(TOTAL_SIZE_COL)).value();
        this.totalTimeColumn = ((ColumnName) args.value(TOTAL_TIME_COL)).value();

        if (args.contains(OUTPUT_SIZE_UNIT)) {
            this.sizeUnit = ((Text) args.value(OUTPUT_SIZE_UNIT)).value();
        }
        if (args.contains(OUTPUT_TIME_UNIT)) {
            this.timeUnit = ((Text) args.value(OUTPUT_TIME_UNIT)).value();
        }
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) 
        throws DirectiveExecutionException {
        
        AggregationStore store = new AggregationStore();
        
        for (Row row : rows) {
            try {
                Object sizeValue = row.getValue(sizeColumn);
                if (sizeValue != null) {
                    ByteSize size = new ByteSize(sizeValue.toString());
                    store.totalBytes += size.getBytes();
                }

                Object timeValue = row.getValue(timeColumn);
                if (timeValue != null) {
                    TimeDuration duration = new TimeDuration(timeValue.toString());
                    store.totalMillis += duration.getMilliseconds();
                }
            } catch (Exception e) {
                throw new DirectiveExecutionException(
                    String.format("Error processing row %s: %s", row, e.getMessage()), e);
            }
        }

        Row result = new Row();
        result.add(totalSizeColumn, convertSize(store.totalBytes, sizeUnit));
        result.add(totalTimeColumn, convertTime(store.totalMillis, timeUnit));
        
        return Collections.singletonList(result);
    }

    private double convertSize(long bytes, String unit) {
        switch (unit.toUpperCase()) {
            case "KB": return bytes / 1024.0;
            case "MB": return bytes / (1024.0 * 1024);
            case "GB": return bytes / (1024.0 * 1024 * 1024);
            default: return bytes;
        }
    }

    private double convertTime(long millis, String unit) {
        switch (unit.toLowerCase()) {
            case "s": return millis / 1000.0;
            case "m": return millis / (1000.0 * 60);
            case "h": return millis / (1000.0 * 60 * 60);
            default: return millis;
        }
    }

    private static class AggregationStore {
        long totalBytes = 0;
        long totalMillis = 0;
    }
}