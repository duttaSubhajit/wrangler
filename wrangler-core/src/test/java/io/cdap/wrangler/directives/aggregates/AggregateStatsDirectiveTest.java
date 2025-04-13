package io.cdap.wrangler.directives.aggregates;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.parser.*;
import io.cdap.wrangler.executor.ErrorRecordCollector;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class AggregateStatsDirectiveTest {

    @Test
    public void testAggregation() throws Exception {
        // Test data
        List<Row> rows = List.of(
            new Row().add("size", "1KB").add("time", "1s"),
            new Row().add("size", "2MB").add("time", "500ms")
        );

        // Create test arguments
        Map<String, Token> argsMap = new HashMap<>();
        argsMap.put("sizeColumn", new ColumnName("size"));
        argsMap.put("timeColumn", new ColumnName("time"));
        argsMap.put("totalSizeColumn", new ColumnName("total_size"));
        argsMap.put("totalTimeColumn", new ColumnName("total_time"));
        argsMap.put("sizeUnit", new Text("KB"));
        argsMap.put("timeUnit", new Text("s"));
        
        Arguments args = new TestArguments(argsMap);

        // Execute directive
        AggregateStatsDirective directive = new AggregateStatsDirective();
        directive.initialize(args);
        
        // Test execution
        List<Row> results = directive.execute(rows, new TestContext(true));
        
        assertEquals(1, results.size());
        assertEquals(2048.0, results.get(0).getValue("total_size")); // 1KB + 2MB = 2048KB
        assertEquals(1.5, results.get(0).getValue("total_time"));    // 1s + 0.5s
    }

    // Test Arguments implementation
    private static class TestArguments implements Arguments {
        private final Map<String, Token> args;
    
        TestArguments(Map<String, Token> args) {
            this.args = args;
        }
    
        @Override
        public boolean contains(String name) {
            return args.containsKey(name);
        }
    
        @Override
        public Token value(String name) throws DirectiveParseException {
            if (!contains(name)) {
                throw new DirectiveParseException("Argument '" + name + "' not found");
            }
            return args.get(name);
        }
    
        @Override
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            for (Map.Entry<String, Token> entry : args.entrySet()) {
                json.add(entry.getKey(), entry.getValue().toJson());
            }
            return json;
        }
    }

    // Simplified TestContext without PipelineContext/ErrorRecordCollector
    private static class TestContext implements ExecutorContext {
        private final boolean finalize;
        
        public TestContext(boolean finalize) {
            this.finalize = finalize;
        }
        
        @Override
        public boolean isFinalize() {
            return finalize;
        }
    
        @Override
        public String getContextName() {
            return "test";
        }
    
        @Override
        public <T> T getStore(String namespace, Supplier<T> supplier) {
            return supplier.get();
        }
    
        // If these methods exist in your ExecutorContext interface:
        @Override
        public Object getPipelineContext() {
            return null;
        }
    
        @Override
        public Object getErrorRecordCollector() {
            return null;
        }
    
        // Add any other required methods from ExecutorContext here
        // @Override
        // public OtherType someOtherMethod() { ... }
    }
}