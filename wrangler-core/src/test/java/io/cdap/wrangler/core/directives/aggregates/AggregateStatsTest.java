package io.cdap.wrangler.core.directives.aggregates;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AggregateStatsTest {

  @Test
  public void testAggregateStatsDirective() throws Exception {
    // Create sample input data
    List<Row> rows = new ArrayList<>();
    
    Row row1 = new Row();
    row1.addOrSet("data_transfer_size", "10KB");
    row1.addOrSet("response_time", "100ms");
    rows.add(row1);
    
    Row row2 = new Row();
    row2.addOrSet("data_transfer_size", "5MB");
    row2.addOrSet("response_time", "2.5s");
    rows.add(row2);
    
    Row row3 = new Row();
    row3.addOrSet("data_transfer_size", "1GB");
    row3.addOrSet("response_time", "1m");
    rows.add(row3);
    
    // Define the recipe with aggregate-stats directive
    String[] recipe = new String[] {
      "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
    };
    
    // Execute the recipe
    List<Row> results = TestingRig.execute(recipe, rows);
    
    // Verify the results
    Assert.assertEquals(1, results.size());
    
    // Calculate expected values
    // 10KB = 10 * 1024 bytes
    // 5MB = 5 * 1024 * 1024 bytes
    // 1GB = 1 * 1024 * 1024 * 1024 bytes
    // Total bytes = 10 * 1024 + 5 * 1024 * 1024 + 1 * 1024 * 1024 * 1024
    // Total MB = Total bytes / (1024 * 1024)
    double expectedTotalSizeMB = 
      (10 * 1024.0 + 5 * 1024 * 1024.0 + 1 * 1024 * 1024 * 1024.0) / (1024 * 1024);
    
    // 100ms = 0.1s
    // 2.5s = 2.5s
    // 1m = 60s
    // Total seconds = 0.1 + 2.5 + 60 = 62.6
    double expectedTotalTimeSec = 0.1 + 2.5 + 60;
    
    // Verify the column values
    Object totalSizeMB = results.get(0).getValue("total_size_mb");
    Assert.assertTrue(totalSizeMB instanceof Double);
    Assert.assertEquals(expectedTotalSizeMB, (Double)totalSizeMB, 0.001);
    
    Object totalTimeSec = results.get(0).getValue("total_time_sec");
    Assert.assertTrue(totalTimeSec instanceof Double);
    Assert.assertEquals(expectedTotalTimeSec, (Double)totalTimeSec, 0.001);
  }
}