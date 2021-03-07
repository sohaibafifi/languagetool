package org.languagetool.rules.ar;
import org.languagetool.rules.Categories;
import org.languagetool.rules.ITSIssueType;
import org.languagetool.rules.WhitespaceBeforePunctuationRule;

import java.util.ResourceBundle;

public class ArabicWhitespaceBeforePunctuationRule extends WhitespaceBeforePunctuationRule{

  public ArabicWhitespaceBeforePunctuationRule(ResourceBundle messages) {
    super(messages);
    super.setCategory(Categories.TYPOGRAPHY.getCategory(messages));
    setLocQualityIssueType(ITSIssueType.Whitespace);
  }

  @Override
  public final String getId() {
    return "ARABIC_WHITESPACE_PUNCTUATION";
  }

  public String getSemicolonCharacter() {
    return "؛";
  }
}
