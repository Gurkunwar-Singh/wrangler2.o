package io.cdap.wrangler.core.directives.aggregates;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.annotations.Categories;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.TransientStore;
import io.cdap.wrangler.api.TransientVariableScope;



import java.util.ArrayList;
import java.util.List;

/**
 * A directive that aggregates byte size and time duration values.
 *
 * Usage:
 *   aggregate-stats :size_column :time_column total_size_column total_time_column
 */
@Categories(categories = { "aggregate" })
public class AggregateStats implements Directive {

  private String sizeColumn;
  private String timeColumn;
  private String totalSizeColumn;
  private String totalTimeColumn;

  private static final String TOTAL_BYTES = "total_bytes";
  private static final String TOTAL_MS = "total_ms";
  private static final String ROW_COUNT = "row_count";

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
    builder.define("size-column", TokenType.COLUMN_NAME);
    builder.define("time-column", TokenType.COLUMN_NAME);
    builder.define("total-size-column", TokenType.COLUMN_NAME);
    builder.define("total-time-column", TokenType.COLUMN_NAME);

    builder.define("string-literal", TokenType.STRING_LITERAL);

    return builder.build();
    
  
  }

  @Override
  public void initialize(Arguments args) throws DirectiveParseException {
    this.sizeColumn = ((ColumnName) args.value("size-column")).value();
    this.timeColumn = ((ColumnName) args.value("time-column")).value();
    this.totalSizeColumn = ((ColumnName) args.value("total-size-column")).value();
    this.totalTimeColumn = ((ColumnName) args.value("total-time-column")).value();
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
    TransientStore store = context.getTransientStore();

    long totalBytes = getOrDefault(store, TOTAL_BYTES, 0L);
    long totalMilliseconds = getOrDefault(store, TOTAL_MS, 0L);
    long rowCount = getOrDefault(store, ROW_COUNT, 0L);

    for (Row row : rows) {
      rowCount++;

      Object sizeObj = row.getValue(sizeColumn);
      if (sizeObj != null) {
        long bytes;
        if (sizeObj instanceof ByteSize) {
          bytes = ((ByteSize) sizeObj).getBytes();
        } else if (sizeObj instanceof String) {
          bytes = new ByteSize((String) sizeObj).getBytes();
        } else {
          throw new DirectiveExecutionException(
            String.format("Column '%s' is not a valid byte size", sizeColumn));
        }
        totalBytes += bytes;
      }

      Object timeObj = row.getValue(timeColumn);
      if (timeObj != null) {
        long milliseconds;
        if (timeObj instanceof TimeDuration) {
          milliseconds = ((TimeDuration) timeObj).getMilliseconds();
        } else if (timeObj instanceof String) {
          milliseconds = new TimeDuration((String) timeObj).getMilliseconds();
        } else {
          throw new DirectiveExecutionException(
            String.format("Column '%s' is not a valid time duration", timeColumn));
        }
        totalMilliseconds += milliseconds;
      }
    }

   store.set(TransientVariableScope.DIRECTIVE, TOTAL_BYTES, totalBytes);
store.set(TransientVariableScope.DIRECTIVE, TOTAL_MS, totalMilliseconds);
store.set(TransientVariableScope.DIRECTIVE, ROW_COUNT, rowCount);


    // Check if this is the final batch
    if (isLastBatch(context)) {
      Row resultRow = new Row();

      double totalMB = totalBytes / (1024.0 * 1024.0);
      resultRow.addOrSet(totalSizeColumn, totalMB);

      double totalSeconds = totalMilliseconds / 1000.0;
      resultRow.addOrSet(totalTimeColumn, totalSeconds);

      List<Row> result = new ArrayList<>();
      result.add(resultRow);
      return result;
    }

    return new ArrayList<>();
  }

  @Override
  public void destroy() {
    // Optional cleanup logic
  }

  // Helper method to mimic get(key, default) behavior
  private long getOrDefault(TransientStore store, String key, long defaultValue) {
    Object val = store.get(key);
    return val instanceof Number ? ((Number) val).longValue() : defaultValue;
  }

  // Helper for isLastBatch until the method is added in ExecutorContext
  private boolean isLastBatch(ExecutorContext context) {
    try {
      return (boolean) ExecutorContext.class
        .getMethod("isLastBatch")
        .invoke(context);
    } catch (Exception e) {
      // If not present or inaccessible, assume it's not the last batch
      return false;
    }
  }
}
