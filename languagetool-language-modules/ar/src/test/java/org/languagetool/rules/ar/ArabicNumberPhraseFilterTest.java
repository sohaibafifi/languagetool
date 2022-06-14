/* LanguageTool, a natural language style checker
 * Copyright (C) 2019 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules.ar;

import org.junit.Test;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.rules.FakeRule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.patterns.RuleFilter;
import org.languagetool.tagging.ar.ArabicTagger;
import org.languagetool.tokenizers.ArabicWordTokenizer;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ArabicNumberPhraseFilterTest {

  private final RuleMatch match = new RuleMatch(new FakeRule(), null, 0, 10, "message");
  private final RuleFilter filter = new NumberPhraseFilter();
  private final ArabicTagger tagger = new ArabicTagger();
  private final ArabicWordTokenizer tokenizer = new ArabicWordTokenizer();
  boolean debug = false;
//  boolean  debug = true;
  boolean run_example = false;
//  boolean run_example = true;

  //  run_example = true;

  @Test
  public void testFilter() throws IOException {
//    String unit = "دينار";
    String inflection = "";
    String previous ="في";
    String next ="";
//    boolean debug = true;
    boolean debug = false;

    assertSuggestion("صفر", "", previous,  next, inflection, debug);
    assertSuggestion("واحد", "", previous,  next, inflection, debug);
    assertSuggestion("اثنان", "اثنين", previous,  next, inflection, debug);
    assertSuggestion("ثلاثة", "", previous,  next, inflection, debug);
    assertSuggestion("إحدى عشر", "أحد عشر", previous,  next, inflection, debug);
    assertSuggestion("اثنتي عشر", "اثني عشر", previous,  next, inflection, debug);
    assertSuggestion("أربعة عشر", "", previous,  next, inflection, debug);
    assertSuggestion("أربعة وثلاثون", "أربعة وثلاثين", previous,  next, inflection, debug);
    assertSuggestion("مائةٍ", "", previous,  next, inflection, debug);
    assertSuggestion("مائة وخمسة وعشرون", "مائة وخمسة وعشرين", previous,  next, inflection, debug);
    assertSuggestion("مائة وأربعة وثلاثين", "", previous,  next, inflection, debug);
    assertSuggestion("ألف وتسعمائة واثنان وعشرين", "ألف وتسعمائة واثنين وعشرين", previous,  next, inflection, debug);
    assertSuggestion("مليون ومئتان وخمسة وأربعين ألفاً وسبعمائة وواحد", "مليون ومئتين وخمسة وأربعين ألفا وسبعمائة وواحد", previous,  next, inflection, debug);
    assertSuggestion("مائة واثنين", "", previous,  next, inflection, debug);
    assertSuggestion("عشرة وآلاف", "عشرة آلاف", previous,  next, inflection, debug);


  } @Test
  public void testUnitFilter() throws IOException {
    String unit = "دينار";
//    String unit = "دينار";
    String inflection = "";
    String previous ="في";
    String next = unit;
//    boolean debug = true;
    boolean debug = false;
//    assertSuggestion("صفر", "في صفر دينار", previous,  next, inflection, debug);
    assertSuggestion("واحد", "في دينار واحد", previous,  next, inflection, debug);
//    assertSuggestion("اثنان", "في دينارين", previous,  next, inflection, debug);
    assertSuggestion("ثلاثة", "في ثلاثة دنانير", previous,  next, inflection, debug);
    assertSuggestion("إحدى عشر", "في أحد عشر دينارا", previous,  next, inflection, debug);
    assertSuggestion("اثنتي عشر", "في اثني عشر دينارا", previous,  next, inflection, debug);
    assertSuggestion("أربعة عشر", "في أربعة عشر دينارا", previous,  next, inflection, debug);
    assertSuggestion("أربعة وثلاثون", "في أربعة وثلاثين دينارا", previous,  next, inflection, debug);
    assertSuggestion("مائةٍ", "في مائة دينار", previous,  next, inflection, debug);
    assertSuggestion("مائة وخمسة وعشرون", "في مائة وخمسة وعشرين دينارا", previous,  next, inflection, debug);
    assertSuggestion("مائة وأربعة وثلاثين", "في مائة وأربعة وثلاثين دينارا", previous,  next, inflection, debug);
    assertSuggestion("ألف وتسعمائة واثنان وعشرين", "في ألف وتسعمائة واثنين وعشرين دينارا", previous,  next, inflection, debug);
    assertSuggestion("مليون ومئتان وخمسة وأربعين ألفاً وسبعمائة وواحد", "في مليون ومئتين وخمسة وأربعين ألفا وسبعمائة وواحد دينار", previous,  next, inflection, debug);
//    assertSuggestion("مائة واثنين", "في مائة ودينارين", previous,  next, inflection, debug);
    assertSuggestion("عشرة وآلاف", "في عشرة آلاف دينار", previous,  next, inflection, debug);
  }

  private void assertSuggestion(String phrase, String expectedSuggestion, String previousWord, String nextWord, String inflection, boolean debug) throws IOException {

    Map<String,String> args = new HashMap<>();
    args.put("previous", previousWord);
    args.put("previousPos", "1");
    args.put("next", nextWord);
    args.put("inflect", inflection);
      // the value -1, used to say that the last word is the given nex word
      int nextPos = -1 ;
//    int nextPos = tokenizer.tokenize(phrase).size() +1 ;
      args.put("nextPos", String.valueOf(nextPos));


    // tokenlize phrase
    String fullPhrase = previousWord+" "+ phrase+ " "+ nextWord;
//    String fullPhrase =  phrase;
    List<String> tokens = tokenizer.tokenize(fullPhrase);
//    System.out.println("Tokens: "+tokens.toString());
    tokens.removeAll(Collections.singleton(" "));
//    System.out.println("Tokens clean: "+tokens.toString());

    List<AnalyzedTokenReadings> patternTokens = tagger.tag(tokens);

    AnalyzedTokenReadings[] patternTokensArray = patternTokens.stream().toArray(AnalyzedTokenReadings[]::new);
    RuleMatch ruleMatch = filter.acceptRuleMatch(match, args, -1, patternTokensArray);
    if(!debug) {
      int expectedSize = expectedSuggestion.split("\\|").length;
    assertThat(ruleMatch.getSuggestedReplacements().size(), is(expectedSize));
//    List<String> expectedList = asList(expectedSuggestion.split("\\|"));
//    assertThat(ruleMatch.getSuggestedReplacements().get(0), is(expectedList.get(0)));
//    assertThat(ruleMatch.getSuggestedReplacements().get(1), is(expectedList.get(1)));
    }
    else { //  debug is true
      String suggestion = "";
      if (!ruleMatch.getSuggestedReplacements().isEmpty()) {
        suggestion = ruleMatch.getSuggestedReplacements().toString();
      }
      // show only no suggestion cases
//      if(suggestion.isEmpty() || !suggestion.startsWith("[ي"))
      if(expectedSuggestion.isEmpty()) {
        System.out.println("مثال: " + fullPhrase + " مقترح:" + suggestion);
      }
      else {
        System.out.println("مثال: " + fullPhrase + " مقترح:" + suggestion + " متوقع: " + expectedSuggestion);
      }

    }
    }




}
