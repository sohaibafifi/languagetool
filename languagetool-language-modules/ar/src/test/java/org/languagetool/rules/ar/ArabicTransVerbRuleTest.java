/* LanguageTool, a natural language style checker
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
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

import org.junit.Before;
import org.junit.Test;
import org.languagetool.*;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
//import static org.languagetool.rules.ar.ArabicTransVerbRule.Determiner;

public class ArabicTransVerbRuleTest {

  private ArabicTransVerbRule rule;
  private JLanguageTool langTool;

  @Before
  public void setUp() throws IOException {
    rule = new ArabicTransVerbRule(TestTools.getEnglishMessages());
    langTool = new JLanguageTool(Languages.getLanguageForShortCode("ar"));
  }

  @Test
  public void testRule() throws IOException {

  // correct sentences:
    assertCorrect("كان أَفَاضَ في الحديث");

// errors:
    assertIncorrect("كان أفاض من الحديث");
    assertIncorrect("لقد أفاضت من الحديث");
    assertIncorrect("لقد أفاضت الحديث");
    assertIncorrect("كان أفاضها الحديث");
    assertIncorrect("إذ استعجل الأمر");
  }

  private void assertCorrect(String sentence) throws IOException {
    RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence(sentence));
    assertEquals(0, matches.length);
  }

  private void assertIncorrect(String sentence) throws IOException {
    RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence(sentence));
    assertEquals(1, matches.length);
  }

  @Test
  public void testSuggestions() throws IOException {
//    assertEquals("a string", rule.suggestAorAn("string"));
//    assertEquals("a university", rule.suggestAorAn("university"));
//    assertEquals("an hour", rule.suggestAorAn("hour"));
//    assertEquals("an all-terrain", rule.suggestAorAn("all-terrain"));
//    assertEquals("a UNESCO", rule.suggestAorAn("UNESCO"));
//    assertEquals("a historical", rule.suggestAorAn("historical"));
  }

  @Test
  public void testGetCorrectDeterminerFor() throws IOException {
//    assertEquals(Determiner.A, getDeterminerFor("string"));
//    assertEquals(Determiner.A, getDeterminerFor("university"));
//    assertEquals(Determiner.A, getDeterminerFor("UNESCO"));
//    assertEquals(Determiner.A, getDeterminerFor("one-way"));
//    assertEquals(Determiner.AN, getDeterminerFor("interesting"));
//    assertEquals(Determiner.AN, getDeterminerFor("hour"));
//    assertEquals(Determiner.AN, getDeterminerFor("all-terrain"));
//    assertEquals(Determiner.A_OR_AN, getDeterminerFor("historical"));
//    assertEquals(Determiner.UNKNOWN, getDeterminerFor(""));
//    assertEquals(Determiner.UNKNOWN, getDeterminerFor("-way"));
//    assertEquals(Determiner.UNKNOWN, getDeterminerFor("camelCase"));
  }

//  private Determiner getDeterminerFor(String word) {
//    AnalyzedTokenReadings token = new AnalyzedTokenReadings(new AnalyzedToken(word, "fake-postag", "fake-lemma"), 0);
//    return rule.getCorrectDeterminerFor(token);
//  }

//  @Test
//  public void testGetCorrectDeterminerForException() throws IOException {
//    try {
//      rule.getCorrectDeterminerFor(null);
//      fail();
//    } catch (NullPointerException ignored) {}
//  }

  @Test
  public void testPositions() throws IOException {
    RuleMatch[] matches;
    JLanguageTool langTool = new JLanguageTool(Languages.getLanguageForShortCode("ar"));
    // no quotes etc.:
//    matches = rule.match(langTool.getAnalyzedSentence("a industry standard."));
//    assertEquals(0, matches[0].getFromPos());
//    assertEquals(1, matches[0].getToPos());
//
//    // quotes..
//    matches = rule.match(langTool.getAnalyzedSentence("a \"industry standard\"."));
//    assertEquals(0, matches[0].getFromPos());
//    assertEquals(1, matches[0].getToPos());
//
//    matches = rule.match(langTool.getAnalyzedSentence("a - industry standard\"."));
//    assertEquals(0, matches[0].getFromPos());
//    assertEquals(1, matches[0].getToPos());
//
//    matches = rule.match(langTool.getAnalyzedSentence("This is a \"industry standard\"."));
//    assertEquals(8, matches[0].getFromPos());
//    assertEquals(9, matches[0].getToPos());
//
//    matches = rule.match(langTool.getAnalyzedSentence("\"a industry standard\"."));
//    assertEquals(1, matches[0].getFromPos());
//    assertEquals(2, matches[0].getToPos());
//
//    matches = rule.match(langTool.getAnalyzedSentence("\"Many say this is a industry standard\"."));
//    assertEquals(18, matches[0].getFromPos());
//    assertEquals(19, matches[0].getToPos());
//
//    matches = rule.match(langTool.getAnalyzedSentence("Like many \"an desperado\" before him, Bart headed south into Mexico."));
//    assertEquals(11, matches[0].getFromPos());
//    assertEquals(13, matches[0].getToPos());
  }
}
