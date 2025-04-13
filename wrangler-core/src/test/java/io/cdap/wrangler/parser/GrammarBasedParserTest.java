/*
 *  Copyright © 2017-2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

 package io.cdap.wrangler.parser;

 import io.cdap.wrangler.TestingRig;
 import io.cdap.wrangler.api.Directive;
 import io.cdap.wrangler.api.RecipeParser;
 import io.cdap.wrangler.api.DirectiveParseException;
 import org.junit.Assert;
 import org.junit.Test;
 
 import java.util.List;
 
 /**
  * Tests {@link GrammarBasedParser}
  */
 public class GrammarBasedParserTest {
 
   // ... existing test methods ...
 
   @Test
   public void testByteSizeParsing() throws Exception {
     String[] recipe = new String[] {
       "set-column :total_bytes byte-size:'10MB'",
       "set-column :file_size '5.5KB'"
     };
 
     RecipeParser parser = TestingRig.parse(recipe);
     List<Directive> directives = parser.parse();
     Assert.assertEquals(2, directives.size());
     Assert.assertTrue(directives.get(0).toString().contains("10MB"));
     Assert.assertTrue(directives.get(1).toString().contains("5.5KB"));
   }
 
   @Test
   public void testTimeDurationParsing() throws Exception {
     String[] recipe = new String[] {
       "delay time-duration:'5s'",
       "set-timeout '500ms'"
     };
 
     RecipeParser parser = TestingRig.parse(recipe);
     List<Directive> directives = parser.parse();
     Assert.assertEquals(2, directives.size());
     Assert.assertTrue(directives.get(0).toString().contains("5s"));
     Assert.assertTrue(directives.get(1).toString().contains("500ms"));
   }
 
   @Test
   public void testAggregateStatsDirective() throws Exception {
     String[] recipe = new String[] {
       "aggregate-stats :input_size :input_time :total_size :total_time 'MB' 's'"
     };
 
     RecipeParser parser = TestingRig.parse(recipe);
     List<Directive> directives = parser.parse();
     Assert.assertEquals(1, directives.size());
     
     String directiveStr = directives.get(0).toString();
     Assert.assertTrue(directiveStr.contains("input_size"));
     Assert.assertTrue(directiveStr.contains("input_time"));
     Assert.assertTrue(directiveStr.contains("MB"));
     Assert.assertTrue(directiveStr.contains("s"));
   }
 
   @Test(expected = DirectiveParseException.class)
   public void testInvalidByteSizeSyntax() throws Exception {
     String[] recipe = new String[] {
       "set-column :bytes '10XB'" // Invalid unit
     };
     TestingRig.parse(recipe).parse();
   }
 
   @Test(expected = DirectiveParseException.class)
   public void testInvalidTimeDurationSyntax() throws Exception {
     String[] recipe = new String[] {
       "delay '5minutes'" // Invalid unit
     };
     TestingRig.parse(recipe).parse();
   }
 
   @Test(expected = DirectiveParseException.class)
   public void testInvalidAggregateStatsSyntax() throws Exception {
     String[] recipe = new String[] {
       "aggregate-stats :size :time" // Missing required arguments
     };
     TestingRig.parse(recipe).parse();
   }
 }