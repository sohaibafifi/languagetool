/* LanguageTool, a natural language style checker
 * Copyright (C) 2020 Sohaib Afifi, Taha Zerrouki
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
package org.languagetool.tagging.ar;

import java.util.List;
import java.util.HashMap;
/**
 * @author Taha Zerrouki
 * @since 5.0
 */
public class ArabicTagManager {

  //@deprecated variables, it must be used as Hashmap
  // CONSTANT for noun flags position
  private static final int NOUN_TAG_LENGTH = 12;
  private static final int NOUN_FLAG_POS_WORDTYPE = 0;
  private static final int NOUN_FLAG_POS_CATEGORY = 1;

  private static final int NOUN_FLAG_POS_GENDER = 4;
  private static final int NOUN_FLAG_POS_NUMBER = 5;
  private static final int NOUN_FLAG_POS_CASE = 6;
  private static final int NOUN_FLAG_POS_INFLECT_MARK = 7;

  private static final int NOUN_FLAG_POS_CONJ = 9;
  private static final int NOUN_FLAG_POS_JAR = 10;
  private static final int NOUN_FLAG_POS_PRONOUN = 11;

  // CONSTANT for Verb flags position
  private static final int VERB_TAG_LENGTH = 15;
  private static final int VERB_FLAG_POS_WORDTYPE = 0;
  private static final int VERB_FLAG_POS_CATEGORY = 1;
  private static final int VERB_FLAG_POS_TRANS = 2;

  private static final int VERB_FLAG_POS_GENDER = 4;
  private static final int VERB_FLAG_POS_NUMBER = 5;
  private static final int VERB_FLAG_POS_PERSON = 6;
  private static final int VERB_FLAG_POS_INFLECT_MARK = 7;
  private static final int VERB_FLAG_POS_TENSE = 8;
  private static final int VERB_FLAG_POS_VOICE = 9;
  private static final int VERB_FLAG_POS_CASE = 10;

  private static final int VERB_FLAG_POS_CONJ = 12;
  private static final int VERB_FLAG_POS_ISTIQBAL = 13;
  private static final int VERB_FLAG_POS_PRONOUN = 14;

  // CONSTANT for particle flags position
  private static final int PARTICLE_TAG_LENGTH = 12;
  private static final int PARTICLE_FLAG_POS_WORDTYPE = 0;
  private static final int PARTICLE_FLAG_POS_CATEGORY = 1;

  private static final int PARTICLE_FLAG_POS_GENDER = 4;
  private static final int PARTICLE_FLAG_POS_NUMBER = 5;
  private static final int PARTICLE_FLAG_POS_CASE = 6;
  private static final int PARTICLE_FLAG_POS_INFLECT_MARK = 7;

  private static final int PARTICLE_FLAG_POS_CONJ = 9;
  private static final int PARTICLE_FLAG_POS_JAR = 10;
  private static final int PARTICLE_FLAG_POS_PRONOUN = 11;

  private final HashMap<String,Integer> mapFlagPos =new HashMap<>();//Creating HashMap

  public ArabicTagManager() {
    loadHashmap();
  }

  public String modifyPosTag(String postag, List<String> tags) {
    // if one of tags are incompatible return null
    for (String tg : tags) {
      postag = addTag(postag, tg);
      if (postag == null)
        return null;
    }
    return postag;
  }
  public String addTag(String postag, String flagString) {
//    StringBuilder tmp = new StringBuilder(postag);
    // split flag into flag and flagtype
    String[] flagTuple = flagString.split(";");
    String flag = "";
    String flagType = "";
    if (flagTuple.length == 1)
      flag = flagTuple[0];
    if (flagTuple.length >= 2) {
      flagType = flagTuple[0];
      flag = flagTuple[1];
    }
   // System.out.printf("addTag: %s (%s,%s) %d\n",flagString, flag, flagType,flagTuple.length );

    return addTag(postag, flagType, flag);
  }
  public String addTag(String postag, String flagType, String flag) {
//    StringBuilder tmp = new StringBuilder(postag);

    switch (flag) {
      case "W":
//        tmp.setCharAt(postag.length() - 3, 'W');
        postag = setFlag(postag, "CONJ", 'W');
        break;
      case "K":
        if (isNoun(postag)) {
          // the noun must be majrour
          if (isMajrour(postag))
            postag = setFlag(postag, "JAR", 'K');
//            tmp.setCharAt(postag.length() - 2, 'K');
            // a prefix K but non majrour
          else return null;

        } else return null;
        break;
      case "B":
        if (isNoun(postag)) {
          // the noun must be majrour
          if (isMajrour(postag))
//            tmp.setCharAt(postag.length() - 2, 'B');
          postag = setFlag(postag, "JAR", 'B');

          // a prefix B but non majrour
          else return null;

        } else return null;
        break;
      case "L":
        if (isNoun(postag)) {
          // the noun must be majrour
          if (isMajrour(postag))
            postag = setFlag(postag, "JAR", 'L');

//          tmp.setCharAt(postag.length() - 2, 'L');
            // a prefix Lam but non majrour
          else return null;

        } else {// verb case
          postag = setFlag(postag, "ISTIQBAL", 'L');

//          tmp.setCharAt(postag.length() - 2, 'L');
        }
        break;
      case "D":
        // the noun must be not attached
        if (isUnAttachedNoun(postag))
          postag = setFlag(postag, "PRONOUN", 'L');

//          tmp.setCharAt(postag.length() - 1, 'L');
          // a prefix Lam but non majrour
        else return null;
        break;
      case "S":
        // َAdd S flag
        // if postag contains a future tag, TODO with regex
        if (isFutureTense(postag)) {
//          tmp.setCharAt(postag.length() - 2, 'S');
          postag = setFlag(postag, "ISTIQBAL", 'S');

        } else
          // a prefix Seen but non verb or future
          return null;
        break;
    }
    // case of Pronoun
    if (flagType.equals("PRONOUN")
//        && flag.equals("H")
      && ! flag.isEmpty()
      && ! flag.equals("D")
//      && ! flag.equals("-")
    )
    {
//      System.out.printf("Add tag %s [%s, %s]\n",postag, flagType,flag );
//      postag = setFlag(postag, flagType, 'H');
      postag = setFlag(postag, flagType, flag.charAt(0));
//      System.out.printf("Add tag %s [%s, %s]\n",postag, flagType,flag );

   }
    return postag;
//    return tmp.toString();
  }

 /**
   * @return true if have flag majrour
   */
  public boolean isMajrour(String postag) {
    char flag = getFlag(postag, "CASE");
    return (flag == 'I') || (flag == '-');
  }

  /**
   * @return add jar flag to noun
   */
  public String setJar(String postag, String jar) {

    char myflag = 0;
    if (isMajrour(postag)) {
      if (jar.equals("ب") || jar.equals("B"))
        myflag = 'B';
      else if (jar.equals("ل") || jar.equals("L"))
        myflag = 'L';
      else if (jar.equals("ك") || jar.equals("K"))
        myflag = 'K';
      else if (jar.equals("-") || jar.isEmpty())
        myflag = '-';
      if (myflag != 0)
        postag = setFlag(postag, "JAR", myflag);
    }
    return postag;
  }

  /**
   * @return add definite flag to noun
   */
  public String setDefinite(String postag, String flag) {
    char myflag = 0;
    if (isNoun(postag) && isUnAttachedNoun(postag)) {
      if (flag.equals("ال")
        || flag.equals("L")
        || flag.equals("لل")
        || flag.equals("D")
      )
        myflag = 'L';
      else if (flag.equals("-") || flag.isEmpty())
        myflag = '-';
      if (myflag != 0)
        postag = setFlag(postag, "PRONOUN", myflag);
    }
    return postag;
  }

  /**
   * @return add conjuction flag to noun
   */
  public String setConjunction(String postag, String flag) {
    char myflag = 0;
    if (flag.equals("و")
      || flag.equals("W")
      || flag.equals("ف")
      || flag.equals("F")
    )
      myflag = 'W';
    else if (flag.equals("-") || flag.isEmpty())
      myflag = '-';
    if (myflag != 0) {
      if (isNoun(postag) || isVerb(postag) )
        postag = setFlag(postag, "CONJ", myflag);
    }
    return postag;
  }

  /**
   * @return add conjunction flag to noun
   */
  public String setPronoun(String postag, String flag) {

    char myflag = 0;
    if (flag.equals("ه")
      || flag.equals("H")
    )
      myflag = 'H';
    if (myflag != 0) {
      if (isNoun(postag) || isVerb(postag) ) {
        postag = setFlag(postag, "PRONOUN", myflag);
      }

    }
    return postag;
  }

  /**
   * @return true if have flag future
   */
  public boolean isFutureTense(String postag) {
    return isVerb(postag) && (getFlag(postag, "TENSE") == 'f');
  }

  /**
   * @return true if have flag is noun and has attached pronoun
   */
  public boolean isUnAttachedNoun(String postag) {
    return isNoun(postag) && !(getFlag(postag, "PRONOUN") == 'H')&& !postag.endsWith("X");
  }

  /**
   * @return true if have flag is noun/verb and has attached pronoun
   */
  public boolean isAttached(String postag) {
    return ((isNoun(postag) || (isVerb(postag))) && (getFlag(postag, "PRONOUN") == 'H'));
  }

  /**
   * @return test if word has stopword tagging
   */
  public boolean isStopWord(String postag) {
    return postag.startsWith("P");
  }

  /**
   * @return true if have flag noun
   */
  public boolean isNoun(String postag) {
    return postag.startsWith("N");
  }

  /**
   * @return true if have flag verb
   */
  public boolean isVerb(String postag) {
    return postag.startsWith("V");
  }

  /**
   * @return true if have flag is noun and definite
   */
  public boolean isDefinite(String postag) {
    return isNoun(postag) && (getFlag(postag, "PRONOUN") == 'L');
  }

  /**
   * @return true if the postag has a Jar
   */
  public boolean hasJar(String postag) {
    return isNoun(postag) && (getFlag(postag, "JAR") != '-');
  }

  /**
   * @return true if the postag has a conjuction
   */
  public boolean hasConjunction(String postag) {
    char flag = getFlag(postag, "CONJ");
    return (isNoun(postag) && ( flag != '-'))
      || (isVerb(postag) && (flag != '-'));
  }

  /**
   * @return if have a flag which is a noun and definite, return the prefix letter for this case
   */
  public String getDefinitePrefix(String postag) {
    if (postag.isEmpty())
      return "";
    if (isNoun(postag) && (getFlag(postag, "PRONOUN") == 'L')) {
      if (hasJar(postag) && getJarPrefix(postag).equals("ل"))
        return "ل";
      else return "ال";
    }
    return "";
  }

  /**
   * @return the Jar prefix letter
   */
  public String getJarPrefix(String postag) {
    if (postag.isEmpty())
      return "";
    if (isNoun(postag)) {
      char flag = getFlag(postag, "JAR");
      if ( flag == 'L')
        return "ل";
      else if ( flag == 'K')
        return "ك";
      else if (flag  == 'B')
        return "ب";
    }
      return "";
  }

  /**
   * @return the Conjunction prefix letter
   */
  public String getConjunctionPrefix(String postag) {

    if (getFlag(postag, "CONJ") == 'F')
      return "ف";
    else if (getFlag(postag, "CONJ") == 'W')
      return "و";

    return "";
  }

  private int getFlagPos(String postag, String flagType) {
    return  getFlagPos2(postag, flagType);
  }

 //@depcrecated 
  private int getFlagPos1(String postag, String flagType)
  {
   /*
   return position of flag in the tag string accorging to word_type and tagstring
    */
    int pos = 0;
    String key = "";
    if(isNoun(postag))
      key = "NOUN_FLAG_POS_"+flagType;
    else if(isVerb(postag))
      key = "VERB_FLAG_POS_"+flagType;
    if (key.equals("NOUN_TAG_LENGTH")) pos = NOUN_TAG_LENGTH;
    else if (key.equals("NOUN_FLAG_POS_WORDTYPE")) pos = NOUN_FLAG_POS_WORDTYPE;
    else if (key.equals("NOUN_FLAG_POS_CATEGORY")) pos = NOUN_FLAG_POS_CATEGORY;

    else if (key.equals("NOUN_FLAG_POS_GENDER")) pos = NOUN_FLAG_POS_GENDER;
    else if (key.equals("NOUN_FLAG_POS_NUMBER")) pos = NOUN_FLAG_POS_NUMBER;
    else if (key.equals("NOUN_FLAG_POS_CASE")) pos = NOUN_FLAG_POS_CASE;
    else if (key.equals("NOUN_FLAG_POS_INFLECT_MARK")) pos = NOUN_FLAG_POS_INFLECT_MARK;

    else if (key.equals("NOUN_FLAG_POS_CONJ")) pos = NOUN_FLAG_POS_CONJ;
    else if (key.equals("NOUN_FLAG_POS_JAR")) pos = NOUN_FLAG_POS_JAR;
    else if (key.equals("NOUN_FLAG_POS_PRONOUN")) pos = NOUN_FLAG_POS_PRONOUN;

    else if (key.equals("VERB_TAG_LENGTH")) pos = VERB_TAG_LENGTH;
    else if (key.equals("VERB_FLAG_POS_WORDTYPE")) pos = VERB_FLAG_POS_WORDTYPE;
    else if (key.equals("VERB_FLAG_POS_CATEGORY")) pos = VERB_FLAG_POS_CATEGORY;
    else if (key.equals("VERB_FLAG_POS_TRANS")) pos = VERB_FLAG_POS_TRANS;

    else if (key.equals("VERB_FLAG_POS_GENDER")) pos = VERB_FLAG_POS_GENDER;
    else if (key.equals("VERB_FLAG_POS_NUMBER")) pos = VERB_FLAG_POS_NUMBER;
    else if (key.equals("VERB_FLAG_POS_PERSON")) pos = VERB_FLAG_POS_PERSON;
    else if (key.equals("VERB_FLAG_POS_INFLECT_MARK")) pos = VERB_FLAG_POS_INFLECT_MARK;
    else if (key.equals("VERB_FLAG_POS_TENSE")) pos = VERB_FLAG_POS_TENSE;
    else if (key.equals("VERB_FLAG_POS_VOICE")) pos = VERB_FLAG_POS_VOICE;
    else if (key.equals("VERB_FLAG_POS_CASE")) pos = VERB_FLAG_POS_CASE;

    else if (key.equals("VERB_FLAG_POS_CONJ")) pos = VERB_FLAG_POS_CONJ;
    else if (key.equals("VERB_FLAG_POS_ISTIQBAL")) pos = VERB_FLAG_POS_ISTIQBAL;
    else if (key.equals("VERB_FLAG_POS_PRONOUN")) pos = VERB_FLAG_POS_PRONOUN;

    else if (key.equals("PARTICLE_TAG_LENGTH")) pos = PARTICLE_TAG_LENGTH;
    else if (key.equals("PARTICLE_FLAG_POS_WORDTYPE")) pos = PARTICLE_FLAG_POS_WORDTYPE;
    else if (key.equals("PARTICLE_FLAG_POS_CATEGORY")) pos = PARTICLE_FLAG_POS_CATEGORY;

    else if (key.equals("PARTICLE_FLAG_POS_GENDER")) pos = PARTICLE_FLAG_POS_GENDER;
    else if (key.equals("PARTICLE_FLAG_POS_NUMBER")) pos = PARTICLE_FLAG_POS_NUMBER;
    else if (key.equals("PARTICLE_FLAG_POS_CASE")) pos = PARTICLE_FLAG_POS_CASE;
    else if (key.equals("PARTICLE_FLAG_POS_INFLECT_MARK")) pos = PARTICLE_FLAG_POS_INFLECT_MARK;

    else if (key.equals("PARTICLE_FLAG_POS_CONJ")) pos = PARTICLE_FLAG_POS_CONJ;
    else if (key.equals("PARTICLE_FLAG_POS_JAR")) pos = PARTICLE_FLAG_POS_JAR;
    else if (key.equals("PARTICLE_FLAG_POS_PRONOUN")) pos = PARTICLE_FLAG_POS_PRONOUN;

    /* test new function */
    int pos2 = getFlagPos2(postag, flagType);
//    if(pos != pos2)
//      System.out.printf("Pos1: %s %d %d\n",postag, pos,pos2);
    return pos;
  }

  private char getFlag(String postag, String flagType)
  {
    /* a flag value for flagtype from postag */
    return postag.charAt(getFlagPos(postag, flagType));

  }
  private String setFlag(String postag, String flagType, char flag)
  {
    /* a flag value for flagtype from postag */
    StringBuilder tmp = new StringBuilder(postag);
    tmp.setCharAt(getFlagPos(postag, flagType), flag);
    return tmp.toString();

  }


  private void loadHashmap()
  {
    mapFlagPos.put("None", 0);
    mapFlagPos.put("NOUN_TAG_LENGTH", 12);
    mapFlagPos.put("NOUN_WORDTYPE", 0);
    mapFlagPos.put("NOUN_CATEGORY", 1);

    mapFlagPos.put("NOUN_GENDER", 4);
    mapFlagPos.put("NOUN_NUMBER", 5);
    mapFlagPos.put("NOUN_CASE", 6);
    mapFlagPos.put("NOUN_INFLECT_MARK", 7);

    mapFlagPos.put("NOUN_CONJ", 9);
    mapFlagPos.put("NOUN_JAR", 10);
    mapFlagPos.put("NOUN_PRONOUN", 11);

    // CONSTANT for Verb flags position
    mapFlagPos.put("VERB_TAG_LENGTH", 15);
    mapFlagPos.put("VERB_WORDTYPE", 0);
    mapFlagPos.put("VERB_CATEGORY", 1);
    mapFlagPos.put("VERB_TRANS", 2);

    mapFlagPos.put("VERB_GENDER", 4);
    mapFlagPos.put("VERB_NUMBER", 5);
    mapFlagPos.put("VERB_PERSON", 6);
    mapFlagPos.put("VERB_INFLECT_MARK", 7);
    mapFlagPos.put("VERB_TENSE", 8);
    mapFlagPos.put("VERB_VOICE", 9);
    mapFlagPos.put("VERB_CASE", 10);

    mapFlagPos.put("VERB_CONJ", 12);
    mapFlagPos.put("VERB_ISTIQBAL", 13);
    mapFlagPos.put("VERB_PRONOUN", 14);

    // CONSTANT for particle flags position
    mapFlagPos.put("PARTICLE_TAG_LENGTH", 12);
    mapFlagPos.put("PARTICLE_WORDTYPE", 0);
    mapFlagPos.put("PARTICLE_CATEGORY", 1);
// FIXME: Add partical
//    mapFlagPos.put("PARTICLE_GENDER", 4);
//    mapFlagPos.put("PARTICLE_NUMBER", 5);
//    mapFlagPos.put("PARTICLE_CASE", 6);
//    mapFlagPos.put("PARTICLE_INFLECT_MARK", 7);
//
//    mapFlagPos.put("PARTICLE_CONJ", 9);
//    mapFlagPos.put("PARTICLE_JAR", 10);
//    mapFlagPos.put("PARTICLE_PRONOUN", 11);
  }

  private int getFlagPos2(String tagString, String flagType)
  {
   /*
   return position of flag in the tag string accorging to word_type and tagstring
    */
//   String key = "";
    int pos = 0;
    String key = "";
    if(isNoun(tagString))
      key = "NOUN_"+flagType;
    else if(isVerb(tagString))
      key = "VERB_"+flagType;
//   else if(isStopWord(tagString))
//     key = "PARTICAL_"+flagType;
    try {
      pos = mapFlagPos.get(key);
    }
    catch (NullPointerException e) {
     // System.out.printf("POS2: %s %s %s\n", tagString, flagType, key);
      pos = 0;
    }
    return pos;
  }

}

