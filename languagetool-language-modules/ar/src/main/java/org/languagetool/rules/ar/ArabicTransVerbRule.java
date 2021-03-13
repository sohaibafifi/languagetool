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

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.language.Arabic;
import org.languagetool.rules.*;
import org.languagetool.synthesis.ar.ArabicSynthesizer;
import org.languagetool.tagging.ar.ArabicTagManager;
import org.languagetool.tagging.ar.ArabicTagger;
import org.languagetool.tokenizers.ArabicWordTokenizer;
import org.languagetool.tools.StringTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static org.languagetool.rules.ar.ArabicTransVerbData.getWordsRequiringA;
import static org.languagetool.rules.ar.ArabicTransVerbData.getWordsRequiringAn;

/**
 * Check if the determiner (if any) preceding a word is:
 * <ul>
 *   <li><i>an</i> if the next word starts with a vowel
 *   <li><i>a</i> if the next word does not start with a vowel
 * </ul>
 *  This rule loads some exceptions from external files {@code det_a.txt} and
 *  {@code det_an.txt} (e.g. for <i>an hour</i>).
 * 
 * @author Daniel Naber
 */
public class ArabicTransVerbRule extends Rule {
  private ArabicTagger tagger;
  private ArabicTagManager tagmanager;
  private ArabicWordTokenizer tokenizer;
  private ArabicSynthesizer synthesizer;

  enum Determiner {
    A, AN, A_OR_AN, UNKNOWN
  }

  private static final Pattern cleanupPattern = Pattern.compile("[^αa-zA-Z0-9.;,:']");

  public ArabicTransVerbRule(ResourceBundle messages) {
    tagger = new ArabicTagger();
    tokenizer = new ArabicWordTokenizer();
    tagmanager = new ArabicTagManager();
    synthesizer = new ArabicSynthesizer(new Arabic());

    super.setCategory(Categories.MISC.getCategory(messages));
    setLocQualityIssueType(ITSIssueType.Misspelling);
    addExamplePair(Example.wrong("The train arrived <marker>a hour</marker> ago."),
                   Example.fixed("The train arrived <marker>an hour</marker> ago."));
  }

  @Override
  public String getId() {
    return "Arabic_Verb_Transitive_to_Untransitive";
  }

  @Override
  public String getDescription() {
    return "Use of 'a' vs. 'an'";
  }

  @Override
  public int estimateContextForSureMatch() {
    return 1;
  }

  @Override
  public RuleMatch[] match(AnalyzedSentence sentence) {
    List<RuleMatch> ruleMatches = new ArrayList<>();
    AnalyzedTokenReadings[] tokens = sentence.getTokensWithoutWhitespace();
    int prevTokenIndex = 0;
    boolean isSentenceStart;
    boolean equalsA;
    boolean equalsAn;
    for (int i = 1; i < tokens.length; i++) {  // ignoring token 0, i.e., SENT_START
      AnalyzedTokenReadings token = tokens[i];
      AnalyzedTokenReadings prevToken = prevTokenIndex > 0 ? tokens[prevTokenIndex] : null;
      String prevTokenStr = prevTokenIndex > 0 ? tokens[prevTokenIndex].getToken() : null;
      String tokenStr = token.getToken();

      isSentenceStart = prevTokenIndex == 1;
      System.out.printf("ArabicTransVerbRule: verb %s prepostion %s\n", prevTokenStr,
        tokenStr);
      if(prevTokenStr != null) {
        // test if the first token is a verb
        boolean is_attached_verb_transitive = isAttachedTransitiveVerb(prevToken);
        // test if the preposition token is suitable for verb token (previous)
        boolean is_right_preposition = isRightPrepostion(prevTokenStr, tokenStr);

        System.out.printf("ArabicTransVerbRule: verb %b prepostion %b\n", is_attached_verb_transitive,
          is_right_preposition);
        // the verb is attached and the next token is not the suitable preposition
        // we give the coorect new form
        if (is_attached_verb_transitive && !is_right_preposition) {
//      if( is_attached_verb_transitive && ! is_right_preposition) {
          String verb = getCorrectVerbForm(tokens[prevTokenIndex]);
          String preposition = getCorrectPrepositionForm(token, prevToken);

          System.out.printf("ArabicTransVerbRule: verb %s prepostion %s =>  verb %s prepostion %s\n", prevTokenStr,
            tokenStr, verb, preposition);
          String replacement = verb + " " + preposition;
          String msg = "Tetststs Use <suggestion>" + replacement + "</suggestion> instead of '" + prevTokenStr + "' if the following " +
            "word starts with a vowel sound, e.g. 'an article', 'an hour'.";
          RuleMatch match = new RuleMatch(
            this, sentence, tokens[prevTokenIndex].getStartPos(), tokens[prevTokenIndex].getEndPos(),
            tokens[prevTokenIndex].getStartPos(), token.getEndPos(), msg, "Wrong article");
          ruleMatches.add(match);

        }
      }

     /* if (!isSentenceStart) {
        equalsA = "a".equals(prevTokenStr);
        equalsAn = "an".equals(prevTokenStr);
      } else {
      	equalsA = "a".equalsIgnoreCase(prevTokenStr);
        equalsAn = "an".equalsIgnoreCase(prevTokenStr);
      }

      if (equalsA || equalsAn) {
        Determiner determiner = getCorrectDeterminerFor(token);
        String msg = null;
        if (equalsA && determiner == Determiner.AN) {
          String replacement = StringTools.startsWithUppercase(prevTokenStr) ? "An" : "an";
          msg = "Use <suggestion>" + replacement + "</suggestion> instead of '" + prevTokenStr + "' if the following "+
                  "word starts with a vowel sound, e.g. 'an article', 'an hour'.";
        } else if (equalsAn && determiner == Determiner.A) {
          String replacement = StringTools.startsWithUppercase(prevTokenStr) ? "A" : "a";
          msg = "Use <suggestion>" + replacement + "</suggestion> instead of '" + prevTokenStr + "' if the following "+
                  "word doesn't start with a vowel sound, e.g. 'a sentence', 'a university'.";
        }
        if (msg != null) {
          RuleMatch match = new RuleMatch(
              this, sentence, tokens[prevTokenIndex].getStartPos(), tokens[prevTokenIndex].getEndPos(),
                  tokens[prevTokenIndex].getStartPos(), token.getEndPos(), msg, "Wrong article");
          ruleMatches.add(match);
        }
      }
      String nextToken = "";
      if (i + 1 < tokens.length) {
        nextToken = tokens[i + 1].getToken();
      }
      if (token.hasPosTag("DT")) {
        prevTokenIndex = i;
      }else
        */
      if (isAttachedTransitiveVerb(token)) {
          prevTokenIndex = i;
      }
      else {
        prevTokenIndex = 0;
      }
    }
    return toRuleMatchArray(ruleMatches);
  }

  /**
   * Adds "a" or "an" to the English noun. Used for suggesting the proper form of the indefinite article.
   * For the rare cases where both "a" and "an" are considered okay (e.g. for "historical"), "a" is returned.
   * @param origWord Word that needs an article.
   * @return String containing the word with a determiner, or just the word if the word is an abbreviation.
   */
  public String suggestAorAn(String origWord) {
    AnalyzedTokenReadings token = new AnalyzedTokenReadings(new AnalyzedToken(origWord, null, null), 0);
    Determiner determiner = getCorrectDeterminerFor(token);
    if (determiner == Determiner.A || determiner == Determiner.A_OR_AN) {
      return "a " + StringTools.lowercaseFirstCharIfCapitalized(origWord);
    } else if (determiner == Determiner.AN) {
      return "an " + StringTools.lowercaseFirstCharIfCapitalized(origWord);
    } else {
      return origWord;
    }
  }

  static Determiner getCorrectDeterminerFor(AnalyzedTokenReadings token) {
    String word = token.getToken();
    Determiner determiner = Determiner.UNKNOWN;
    String[] parts = word.split("[-']");  // for example, in "one-way" only "one" is relevant
    if (parts.length >= 1 && !parts[0].equalsIgnoreCase("a")) {  // avoid false alarm on "A-levels are..."
      word = parts[0];
    }
    if (token.isWhitespaceBefore() || !"-".equals(word)) { // e.g., 'a- or anti- are prefixes'
      word = cleanupPattern.matcher(word).replaceAll("");         // e.g. >>an "industry party"<<
      if (StringTools.isEmpty(word)) {
        return Determiner.UNKNOWN;
      }
    }
    if (getWordsRequiringA().contains(word.toLowerCase()) || getWordsRequiringA().contains(word)) {
      determiner = Determiner.A;
    }
    if (getWordsRequiringAn().contains(word.toLowerCase()) || getWordsRequiringAn().contains(word)) {
      if (determiner == Determiner.A) {
        determiner = Determiner.A_OR_AN;   // e.g. for 'historical'
      } else {
        determiner = Determiner.AN;
      }
    }
    if (determiner == Determiner.UNKNOWN) {
      char tokenFirstChar = word.charAt(0);
      if (StringTools.isAllUppercase(word) || StringTools.isMixedCase(word)) {
        // we don't know how all-uppercase words (often abbreviations) are pronounced,
        // so never complain about these
        determiner = Determiner.UNKNOWN;
      } else if (isVowel(tokenFirstChar)) {
        determiner = Determiner.AN;
      } else {
        determiner = Determiner.A;
      }
    }
    return determiner;
  }

  private static boolean isVowel(char c) {
    char lc = Character.toLowerCase(c);
    return lc == 'a' || lc == 'e' || lc == 'i' || lc == 'o' || lc == 'u';
  }


  private static boolean isAttachedTransitiveVerb(AnalyzedTokenReadings mytoken)
  {
    String word =  mytoken.getToken();
   List<AnalyzedToken> verbTokenList = mytoken.getReadings();

    // keep the suitable postags
    List<String> rightPostags = new  ArrayList<String>();

    for(AnalyzedToken verbTok: verbTokenList)
    {
      String verbLemma = verbTok.getLemma();
      String verbPostag = verbTok.getPOSTag();
      // if postag is attached
      // test if verb is in the verb list
      if (verbPostag != null)// && verbPostag.endsWith("H"))
      {
        if(getWordsRequiringA().contains(verbLemma)) {
          rightPostags.add(verbPostag);

          System.out.printf("ArabicTransVerbRule:(isAttachedTransitiveVerb) verb lemma %s, postag %s\n", verbLemma, verbPostag);
          return true;
        }
      }

    }
    return false;
//    return (getWordsRequiringA().contains(word));

  }

  private static boolean isRightPrepostion(String verbToken, String nextToken)
  {
    // test if the next token  is the suitable preposition for the previous token as verbtoken
    return (nextToken.equals("في") ||  nextToken.equals("in"));
  }
  private  String getCorrectVerbForm(AnalyzedTokenReadings token)
  {
//    return "verben";
    return generateUnattachedNewForm(token);
  }
  private String getCorrectPrepositionForm(AnalyzedTokenReadings token, AnalyzedTokenReadings prevtoken)
  {

    return generateAttachedNewForm(token, prevtoken);
  };

  /* generate a new form according to a specific postag*/
  private String generateNewForm(String word, String posTag, char flag)
  {
    //      // generate new from word form
    String newposTag = tagmanager.setFlag(posTag, "PRONOUN", flag);
    // FIXME: remove the specific flag for option D
    if (flag != '-')
      newposTag = tagmanager.setFlag(newposTag, "OPTION", 'D');
    // generate the new preposition according to modified postag
    AnalyzedToken prepAToken = new AnalyzedToken(word, newposTag, word);
    String newWord = Arrays.toString(synthesizer.synthesize(prepAToken, newposTag));

    return newWord;

  }
  /* generate a new form according to a specific postag, this form is Attached*/
  private String generateAttachedNewForm(String word, String posTag, char flag)
  {
    return generateNewForm(word, posTag,flag);

  }
  /* generate a new form according to a specific postag, this form is Un-Attached*/
  private String generateUnattachedNewForm(String word, String posTag)
  {
    return generateNewForm(word, posTag,'-');
  }
  /* generate a new form according to a specific postag, this form is Un-Attached*/
  private String generateUnattachedNewForm(AnalyzedTokenReadings token)
  {
    String lemma = token.getReadings().get(0).getLemma();
    String postag = token.getReadings().get(0).getPOSTag();
    return generateNewForm(lemma, postag,'-');
  }

  /* generate a new form according to a specific postag, this form is Attached*/
  private String generateAttachedNewForm(AnalyzedTokenReadings token, AnalyzedTokenReadings prevtoken)
  {
    //FIXME ; generate multiple cases
//    String lemma = token.getReadings().get(0).getLemma();
//    String postag = token.getReadings().get(0).getPOSTag();
    String lemma = "في";
    String postag = "PR-;---;---";
    String prevpostag = prevtoken.getReadings().get(0).getPOSTag();
    char flag = tagmanager.getFlag(prevpostag,"PRONOUN");
    System.out.printf("ArabicTransVerbRule:(generateAttachedNewForm) %s %s %c\n",lemma, postag, flag );
    return generateNewForm(lemma, postag,flag);
  }
}

