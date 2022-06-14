package org.languagetool.tools;

import java.util.HashMap;
import java.util.Map;

public class ArabicConstantsMaps {
  protected static final Map<String, String> isolatedToAttachedPronoun = new HashMap<>();
  protected ArabicConstantsMaps() {
    // restrict instantiation
    // Isolated pronoun to attached
    isolatedToAttachedPronoun.put("أنا","ني");
    isolatedToAttachedPronoun.put("نحن","نا");
    isolatedToAttachedPronoun.put("هو","ه");
    isolatedToAttachedPronoun.put("هي","ها");
    isolatedToAttachedPronoun.put("هم","هم");
    isolatedToAttachedPronoun.put("هن","هن");
    isolatedToAttachedPronoun.put("أنتما","كما");
    isolatedToAttachedPronoun.put("أنتم","كم");
    isolatedToAttachedPronoun.put("أنتن","كن");
  }



}
