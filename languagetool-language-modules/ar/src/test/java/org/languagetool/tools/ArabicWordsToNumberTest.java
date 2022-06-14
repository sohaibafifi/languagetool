/* LanguageTool, a natural language style checker
 * Copyright (C) 2021 Sohaib Afifi, Taha Zerrouki
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
package org.languagetool.tools;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**

   * @author Taha Zerrouki
 */




public class ArabicWordsToNumberTest {
  final boolean debug= false;
//  final boolean debug= true;

  /*
  Extract number from text
   */
  @Test
  public void testNumberPhrase() {
   String text ="تسعمئة وثلاث وعشرون ألفا وتسعمئة وواحد";
   Integer x = ArabicNumbersWords.textToNumber(text);
//   System.out.println("Phrase to Number text: "+ text +" "+x);
  assertEquals("testNumberPhrase"+ text, 923901,  x.intValue());
  }

  /* Test converting number to phrase*/
  @Test
  public void testNumeber2Phrase() {

    Map<Integer, String> phraseMap = new HashMap<>();

    phraseMap.put(0,"صفر");
    phraseMap.put(1,"واحد");
    phraseMap.put(2,"اثنين");
    phraseMap.put(3,"ثلاثة");
    phraseMap.put(11,"أحد عشر");
    phraseMap.put(12,"اثني عشر");
    phraseMap.put(14,"أربعة عشر");
    phraseMap.put(34,"أربعة وثلاثين");
    phraseMap.put(100,"مائة");
    phraseMap.put(125,"مائة وخمسة وعشرين");
    phraseMap.put(134,"مائة وأربعة وثلاثين");
    phraseMap.put(1922,"ألف وتسعمائة واثنين وعشرين");
    phraseMap.put(1245701,"مليون ومئتين وخمسة وأربعين ألفاً وسبعمائة وواحد");
    phraseMap.put(102,"مائة واثنين");
    phraseMap.put(10000,"عشرة آلاف");

    //
    String inflection = "jar";
    boolean feminin = false;
    boolean attached = false;
    for( Integer n: phraseMap.keySet())
    {
      assertEquals(phraseMap.get(n),ArabicNumbersWords.numberToArabicWords(String.valueOf(n), feminin,attached, inflection));
    }
  }
  /* test extracting number from text, and convert the same number to text
  */

  @Test
  public void testBidiNumberPhrase() {
//    String text ="خمسمئة وثلاث وعشرون ألفا وتسعمئة وواحد";
    String text ="ثمانية وتسعين ألفاً وتسعمائة وخمس وثمانين";
    Integer x =  ArabicNumbersWords.textToNumber(text);
    String text2 = ArabicNumbersWords.numberToArabicWords(Integer.toString(x),true,false,"jar");
    if(debug)
    System.out.println("text: "+ text +" detected "+x+ " |"+text2);
    else
      assertEquals(text, text2);

  }

  /* test bidirictional convertion from text to number  on a range */
  @Test
  public void testNumberPhraseRandom() {

    for (Integer i = 1000; i < 99000; i++) {
      String text = ArabicNumbersWords.numberToArabicWords(Integer.toString(i));
      Integer x = ArabicNumbersWords.textToNumber(text);
      if(debug) {
        if (!i.equals(x)) {
          System.out.println("text: " + text + " detected " + Integer.toString(x) + " != " + Integer.toString(i));
        }
      }
      else {
        assertEquals(x, i);
      }

    }
  }
  /* test UnitHelper , to get number words specific to a case */
@Test
  public void testUnitsHelper() {
 // test gender
    assertEquals(ArabicUnitsHelper.isFeminin("ليرة"), true);
    assertEquals(ArabicUnitsHelper.isFeminin("دينار"), false);
    assertEquals(ArabicUnitsHelper.isFeminin("فلس"), false);
// test forms
  assertEquals(ArabicUnitsHelper.getOneForm("ليرة" ,"jar"), "ليرةٍ");
  assertEquals(ArabicUnitsHelper.getPluralForm("ليرة" ,"jar"), "ليراتٍ");
  assertEquals(ArabicUnitsHelper.getTwoForm("دولار" ,"jar"), "دولارين");
  assertEquals(ArabicUnitsHelper.getTwoForm("أوقية" ,"jar"), "[[أوقية]]");
    }

    /* used to generate examples in multiple format */
//    @Test
  public void generateExamples() {

    String unit = "دينار";
    String inflection = "jar";
    Integer[] numbers = new Integer[] {0,1,2,3,11,12,14,34,100,125,134,1922, 1245701,102, 10000};
    for( Integer n: numbers)
    {
      String phrase = ArabicNumbersWords.numberToWordsWithUnits(n, unit, inflection);
//      System.out.println("N: "+ Integer.toString(n)+ " phrase:"+phrase);
      System.out.println("phraseMap.put("+String.valueOf(n)+",\""+phrase+"\");");
    }
    }


/* Test converting numbers into phrases with specific units */
  @Test
  public void testNumberToWordsWithUnits()
  {
    String unit = "دينار";
    String inflection = "jar";
    Map<Integer, String> phraseMap = new HashMap<>();

    phraseMap.put(0,"لا دنانيرَ");
    phraseMap.put(1,"دينارٍ واحد");
    phraseMap.put(2,"دينارين");
    phraseMap.put(3,"ثلاثة دنانيرَ");
    phraseMap.put(11,"أحد عشر دينارًا");
    phraseMap.put(12,"اثني عشر دينارًا");
    phraseMap.put(14,"أربعة عشر دينارًا");
    phraseMap.put(34,"أربعة وثلاثين دينارًا");
    phraseMap.put(100,"مائة دينارٍ");
    phraseMap.put(125,"مائة وخمسة وعشرين دينارًا");
    phraseMap.put(134,"مائة وأربعة وثلاثين دينارًا");
    phraseMap.put(1922,"ألف وتسعمائة واثنين وعشرين دينارًا");
    phraseMap.put(1245701,"مليون ومئتين وخمسة وأربعين ألفاً وسبعمائة دينارٍ ودينارٍ");
    phraseMap.put(102,"مائة دينارٍ ودينارين");
    phraseMap.put(10000,"عشرة آلاف دينارٍ");

    Integer[] numbers = new Integer[] {0,1,2,3,11,12,14,34,100,125,134,1922, 1245701,102, 10000};
    for( Integer n: phraseMap.keySet())
    {
      assertEquals(phraseMap.get(n),ArabicNumbersWords.numberToWordsWithUnits(n, unit, inflection));
    }
  }
  /* Test Checking phrase with units if its spelled correctelly*/
//  @Test
//  public void testCheckNumericPhraseWithUnits() {
//
// List<String> phraseList = new ArrayList<>();
//    phraseList.add("لا دنانيرَ");
//    phraseList.add("دينارٍ واحد");
//    phraseList.add("دينارين");
//    phraseList.add("ثلاثة دنانيرَ");
//    phraseList.add("أحد عشر دينارًا");
//    phraseList.add("اثني عشر دينارًا");
//    phraseList.add("أربعة عشر دينارًا");
//    phraseList.add("أربعة وثلاثون دينارًا");
//    phraseList.add("مائة دينارٍ");
//    phraseList.add("مائة وخمسة وعشرون دينارًا");
//    phraseList.add("مائة وأربعة وثلاثون دينارًا");
//    phraseList.add("ألف وتسعمائة واثنان وعشرون دينارًا");
//    phraseList.add("مليون ومئتان وخمسة وأربعون ألفاً وسبعمائة دينارٍ ودينارٍ");
//    phraseList.add("مائة دينارٍ ودينارين");
//    phraseList.add("عشرة آلاف دينارٍ");
//
//    //
//    String unit = "دينار";
//    String inflection = "jar";
//    boolean feminin = false;
//    boolean attached = false;
//    for(String phrase: phraseList) {
//      checkNumericPhraseWithUnits(phrase, unit, feminin,  attached, inflection);
//    }
//  }
//  /* Test Checking phrase without units if its spelled correctelly*/
//  @Test
//  public void testCheckNumericPhrase() {
//
// List<String> phraseList = new ArrayList<>();
//    phraseList.add("صفر");
//    phraseList.add("واحد");
//    phraseList.add("اثنين");
//    phraseList.add("ثلاثة");
//    phraseList.add("أحد عشر");
//    phraseList.add("اثني عشر");
//    phraseList.add("أربعة عشر");
//    phraseList.add("أربعة وثلاثين");
//    phraseList.add("مائةٍ");
//    phraseList.add("مائة وخمسة وعشرين");
//    phraseList.add("مائة وأربعة وثلاثين");
//    phraseList.add("ألف وتسعمائة واثنين وعشرين");
//    phraseList.add("مليون ومئتين وخمسة وأربعين ألفاً وسبعمائة وواحد");
//    phraseList.add("مائة واثنين");
//    phraseList.add("عشرة آلاف");
//
//    //
//    String unit = "دينار";
//    String inflection = "jar";
//    boolean feminin = false;
//    boolean attached = false;
//    if(debug)
//    System.out.println("---------testCheckNumericPhrase---------------");
//    for(String phrase: phraseList) {
//      boolean test = ArabicNumbersWords.checkNumericPhrase(phrase, feminin,  attached, inflection);
//      if(debug)
//      {
//        if(!test) {
//          System.out.println("testCheckNumericPhrase::Input: " + phrase + " Inccorrect");
//        }
//      }
//      else {
//        assertTrue(test);
//      }
//    }
//    if(debug)
//    System.out.println("---------testCheckNumericPhrase---------------");
//  }

  /* Test Checking phrase without units if its spelled correctelly*/
//  @Test
  public void generateExamplesSuggestions() {

 List<String> phraseList = new ArrayList<>();
    phraseList.add("صفر");
    phraseList.add("واحد");
    phraseList.add("اثنان");
    phraseList.add("ثلاثة");
    phraseList.add("إحدى عشر");
    phraseList.add("اثنتي عشر");
    phraseList.add("أربعة عشر");
    phraseList.add("أربعة وثلاثون");
    phraseList.add("مائةٍ");
    phraseList.add("مائة وخمسة وعشرون");
    phraseList.add("مائة وأربعة وثلاثين");
    phraseList.add("ألف وتسعمائة واثنان وعشرين");
    phraseList.add("مليون ومئتان وخمسة وأربعين ألفاً وسبعمائة وواحد");
    phraseList.add("مائة واثنين");
    phraseList.add("عشرة وآلاف");

    //
    String unit = "دينار";
    String inflection = "jar";
    boolean feminin = false;
    boolean attached = false;
    for(String phrase: phraseList) {
      List<String> suggestions = ArabicNumbersWords.getSuggestionsNumericPhrase(phrase, feminin,  attached, inflection);
      if(debug)
      {
//          System.out.println("testGetSuggestionsNumberPhrase::Input: " + phrase + " Suggestions:"+suggestions.toString());
          System.out.println("assertSuggestions(\""+phrase+"\", \""+String.join("|",suggestions)+"\", feminin,  attached, inflection);");
      }
      else {
        assertEquals(suggestions.size(),1);
      }
    }
  } @Test
  public void testGetSuggestionsNumberPhrase2() {
    String unit = "دينار";
    String inflection = "jar";
    boolean feminin = false;
    boolean attached = false;
    assertSuggestions("صفر", "", feminin,  attached, inflection);
    assertSuggestions("واحد", "", feminin,  attached, inflection);
    assertSuggestions("اثنان", "اثنين", feminin,  attached, inflection);
    assertSuggestions("ثلاثة", "", feminin,  attached, inflection);
    assertSuggestions("إحدى عشر", "أحد عشر", feminin,  attached, inflection);
    assertSuggestions("اثنتي عشر", "اثني عشر", feminin,  attached, inflection);
    assertSuggestions("أربعة عشر", "", feminin,  attached, inflection);
    assertSuggestions("أربعة وثلاثون", "أربعة وثلاثين", feminin,  attached, inflection);
    assertSuggestions("مائةٍ", "", feminin,  attached, inflection);
    assertSuggestions("مائة وخمسة وعشرون", "مائة وخمسة وعشرين", feminin,  attached, inflection);
    assertSuggestions("مائة وأربعة وثلاثين", "", feminin,  attached, inflection);
    assertSuggestions("ألف وتسعمائة واثنان وعشرين", "ألف وتسعمائة واثنين وعشرين", feminin,  attached, inflection);
    assertSuggestions("مليون ومئتان وخمسة وأربعين ألفاً وسبعمائة وواحد", "مليون ومئتين وخمسة وأربعين ألفا وسبعمائة وواحد", feminin,  attached, inflection);
    assertSuggestions("مائة واثنين", "", feminin,  attached, inflection);
    assertSuggestions("عشرة وآلاف", "عشرة آلاف", feminin,  attached, inflection);

  }

  /* Assert suggestions */
  private void assertSuggestions(String phrase, String expectedSuggestions, boolean feminin,  boolean attached, String inflection)
    {
      List<String> actualSuggestionsList = ArabicNumbersWords.getSuggestionsNumericPhrase(phrase, feminin,  attached, inflection);
      String phrase_unvocalized = ArabicStringTools.removeTashkeel(phrase);
      String actualSuggestions_unvocalized = ArabicStringTools.removeTashkeel(String.join("|",actualSuggestionsList));
      if(debug)
      {
        if(!expectedSuggestions.equals(actualSuggestions_unvocalized))
        System.out.println("assertSuggestions::Input: " + phrase + " Suggestions Expected:'"+expectedSuggestions+"' Actual Suggestions: '"+actualSuggestions_unvocalized+
          "' Incorrect");
      }
      else {
        assertEquals(expectedSuggestions, actualSuggestions_unvocalized);
      }

    }




  public boolean checkNumericPhraseWithUnits(String phrase, String unit, boolean feminin,  boolean attached, String inflection) {

    Integer x = ArabicNumbersWords.textToNumber(phrase);
    String autoPhrase = ArabicNumbersWords.numberToArabicWords(String.valueOf(x), feminin, attached, inflection);
    if(!autoPhrase.equals(phrase))
    {
      if(debug)
            System.out.println("Input: "+ phrase + " Output: X: "+ String.valueOf(x) + " String:"+ autoPhrase);
    }
    return autoPhrase.equals(phrase);
  }
}
