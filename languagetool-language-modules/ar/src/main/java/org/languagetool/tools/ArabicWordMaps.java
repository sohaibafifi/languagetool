package org.languagetool.tools;

import java.util.HashMap;
import java.util.Map;

public class ArabicWordMaps {
  private  static ArabicConstantsMaps constantMap = new ArabicConstantsMaps();
  private ArabicWordMaps() {
    // restrict instantiation
  }

  // generate the attached forms from isolated
  public static String getAttachedPronoun(String word)
  {
    if(word==null)
      return "";
    return constantMap.isolatedToAttachedPronoun.getOrDefault(word, "");
  }
}
