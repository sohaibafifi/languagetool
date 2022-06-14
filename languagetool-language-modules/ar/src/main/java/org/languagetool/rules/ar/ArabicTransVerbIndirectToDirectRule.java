/*
 * LanguageTool, a natural language style checker
 * Copyright (C) 2022 Sohaib Afifi, Taha Zerrouki
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

import java.util.*;

import static java.lang.Math.min;


public class ArabicTransVerbIndirectToDirectRule extends AbstractSimpleReplaceRule2 {

  public static final String AR_VERB_RANSITIVE_INDIRECT_TO_DIRECT_REPLACE = "AR_VERB_TRANSITIVE_INDIRECT_TO_DIRECT";

  private static final String FILE_NAME = "/ar/verb_trans_indirect_to_direct.txt";
  private static final Locale AR_LOCALE = new Locale("ar");
  private final int MAX_CHUNK = 4;
  private final ArabicTagger tagger;
  private final ArabicTagManager tagmanager;
  private final ArabicSynthesizer synthesizer;
  private final List<Map<String, SuggestionWithMessage>> wrongWords;


  public ArabicTransVerbIndirectToDirectRule(ResourceBundle messages) {
    super(messages, new Arabic());
    tagger = new ArabicTagger();
    tagger.enableNewStylePronounTag();
    tagmanager = new ArabicTagManager();
    synthesizer = new ArabicSynthesizer(new Arabic());

    super.setCategory(Categories.MISC.getCategory(messages));
    setLocQualityIssueType(ITSIssueType.Inconsistency);
    //FIXME: choose another example
    addExamplePair(Example.wrong("قال <marker>كشفت</marker> الأمر الخفي."),
      Example.fixed("قال <marker>كشفت عن</marker> الأمر الخفي."));

    // get wrong words from resource file
    wrongWords = getWrongWords(false);
  }

  @Override
  public String getId() {
    return AR_VERB_RANSITIVE_INDIRECT_TO_DIRECT_REPLACE;
  }

  @Override
  public String getDescription() {
    return "َIntransitive verbs corrected to direct transitive";
  }

  @Override
  public final List<String> getFileNames() {
    return Collections.singletonList(FILE_NAME);
  }

  @Override
  public String getShort() {
    return "أفعال متعدية، يخطئ في تعديتها بحرف";
  }

  @Override
  public String getMessage() {
    return "'$match' الفعل خاطئ في التعدية بحرف: $suggestions";
  }

  @Override
  public String getSuggestionsSeparator() {
    return " أو ";
  }

  @Override
  public Locale getLocale() {
    return AR_LOCALE;
  }

  @Override
  public RuleMatch[] match(AnalyzedSentence sentence) {
    List<RuleMatch> ruleMatches = new ArrayList<>();
    if (wrongWords.size() == 0) {
      return toRuleMatchArray(ruleMatches);
    }

    AnalyzedTokenReadings[] tokens = sentence.getTokensWithoutWhitespace();

    for (int i = 1; i < tokens.length; i++) {  // ignoring token 0, i.e., SENT_START
      int prevTokenIndex = i - 1;
      AnalyzedTokenReadings token = tokens[i];
      AnalyzedTokenReadings prevToken = prevTokenIndex > 0 ? tokens[prevTokenIndex] : null;
      String prevTokenStr = prevTokenIndex > 0 ? tokens[prevTokenIndex].getToken() : null;

      if (prevTokenStr != null) {
        StringBuilder replacement = new StringBuilder("");
        // browse verb forms and lemmas
        for (AnalyzedToken verbTok : prevToken.getReadings()) {
          // test if the preposition token is suitable for verb token (previous)
          boolean is_candidate_verb = isCandidateVerb(verbTok);
          if (is_candidate_verb) {
            List<String> prepositions = new ArrayList<>();
            String sug_msg = "";
            // if the verb is a candidate, we lookup for next prepostion
            // Here we lookup for wrong preposition
            // if the wrong preposition is given:
            //     - remove preposition
            //     - inflect verb by transfering the attached pronoun from prepostion into verb
            //     - if the preposition is attached to a noun, remove it from the noun
            // example:
            // التزم بالهدنة => التزم الهدنة
            // تحرى عن الأمر => تحرى الأمر
            // تحرى عنه => تحراه
            SuggestionWithMessage prepositionsWithMessage = getSuggestedPreposition(verbTok);
            if (prepositionsWithMessage != null) {
              prepositions = Arrays.asList(prepositionsWithMessage.getSuggestion().split("\\|"));
              sug_msg = prepositionsWithMessage.getMessage();
              sug_msg = sug_msg != null ? sug_msg : "";
            }
            // the current token can be a preposition or any words else
            // test if the token is in the suitable prepositions
            // browse all next  tokens to assure that proper preposition doesn't exist
            boolean is_wrong_preposition = false;
            // initial as first token
            AnalyzedTokenReadings current_token_reading = token;
            AnalyzedToken current_token = token.getReadings().get(0);

            int[] next_indexes = getNextMatch(tokens, i, prepositions);
            String skippedString = "";
            if (next_indexes[0] != -1) {

              int tokReadingPos = next_indexes[0];
              int tokPos = next_indexes[1];
              is_wrong_preposition = true;
              current_token_reading = tokens[tokReadingPos];
              current_token = current_token_reading.getReadings().get(tokPos);
              skippedString = getSkippedString(tokens, i, tokReadingPos);
            }
            // the verb is not attached and the next token is a preposition to be removed
            // we give the correct new form
            if (is_candidate_verb && is_wrong_preposition) {
              // generate suggestion according to prepositions to be removed
              // generate a new form of verb according to current token
              String verb = inflectVerb(verbTok, current_token, skippedString);
              replacement.append("<suggestion>" + verb + "</suggestion>");
              //FIXED: add the intermediate tokens to the suggestion
              // إذا كانت الكلمتان متباعدتان، إدراج لالجملة الوسيطة في الاقتراحات
              String msg = "' الفعل " + prevTokenStr + " ' متعدٍ بنفسه،" + sug_msg + ". فهل تقصد؟" + replacement.toString();
              RuleMatch match = new RuleMatch(
                this, sentence, prevToken.getStartPos(), current_token_reading.getEndPos(),
                prevToken.getStartPos(), current_token_reading.getEndPos(), msg, "خطأ في الفعل المتعدي ");
              ruleMatches.add(match);
            }
          }// if is candidate
        } // for verbTok
      }

    }
    return toRuleMatchArray(ruleMatches);
  }


  private boolean isCandidateVerb(AnalyzedToken mytoken) {
    return (getSuggestedPreposition(mytoken) != null);
  }

  private String getSkippedString(AnalyzedTokenReadings[] tokens, int start, int end) {
    StringBuilder skipped = new StringBuilder("");
    for (int i = start; i < end; i++) {
      skipped.append(tokens[i].getToken());
      skipped.append(" ");
    }

    return skipped.toString();

  }

  /* extract suggestions from Rules text file */
  private SuggestionWithMessage getSuggestedPreposition(AnalyzedToken verbTok) {

    // if postag is attached
    // test if verb is in the verb list
    if (tagmanager.isVerb(verbTok.getPOSTag())) {
      // lookup in Text file rules
      SuggestionWithMessage textRuleMatch = wrongWords.get(wrongWords.size() - 1).get(verbTok.getLemma());

      // The lemma is found in the dictionary file and return a list of suggestions
      return textRuleMatch;
    }
    return null;
  }

  private boolean isWrongPreposition(AnalyzedToken nextToken, List<String> prepositionList) {
    //test if the next token  is the wrong preposition for the previous token as verbtoken

    // test
    // isolated Jar
    if (prepositionList.contains(nextToken.getLemma()))
      return true;
    // attached Jar test if Lam or Beh jar partical
    String postag = nextToken.getPOSTag();
    if (tagmanager.hasJar(postag) && !tagmanager.hasConjunction(postag)) {
      String prefix = tagger.getJarProclitic(nextToken);
      if (prepositionList.contains(prefix)) // test if Lam or Beh jar partical
        return true;
    }

    return false;
  }

  private String inflectVerb(AnalyzedToken verbToken, AnalyzedToken prepositionToken, String skipped) {
    // extract verb postag
    // extract preposition postag
    // get pronoun flag
    // regenerate verb form with original postag and new flag to add Pronoun if exists
    String newWord = "";
// إذا كانت الكلمة الثانية حرف جر منفصلا، وله لاحقة الضمير، نعطي اللاحقة للفعل
    if (tagmanager.isStopWord(prepositionToken.getPOSTag())) {
      String suffix = tagger.getEnclitic(prepositionToken);
      newWord = synthesizer.setEnclitic(verbToken, suffix);
      newWord = verbToken.getToken() + " " + skipped;
    } else if (tagmanager.isNoun(prepositionToken.getPOSTag())) {
      // remove jar procletic if exists
      // add unattached jar and a space
      String currentWord = synthesizer.setJarProcletic(prepositionToken, "");
      newWord = verbToken.getToken() + " " + skipped + " " + currentWord;
    }
    // إذا كانت الكلمة الثانية اسما مجرورا بحرف جر كحرف تعدية
    // ننزع حرف الحر من الاسم

    return newWord;

  }

  /* Lookup for next token matched */
  public int[] getNextMatch(AnalyzedTokenReadings[] tokens, int current_index, List<String> prepositions) {
    int tokRead_index = current_index;
    int max_length = min(current_index + MAX_CHUNK, tokens.length);
    int tokIndex = 0;
    int[] indexes = {-1, -1};
    // browse all next  tokens to assure that proper preposition doesn't exist
    boolean is_wrong_preposition = false;
    // used to save skipped tokens
    // initial as first token
    AnalyzedTokenReadings current_token_reading = tokens[current_index];
    AnalyzedToken current_token = current_token_reading.getReadings().get(0);

    while (tokRead_index < max_length && !is_wrong_preposition) {
      current_token_reading = tokens[tokRead_index];
      tokIndex = 0;
      while (tokIndex < current_token_reading.getReadings().size() && !is_wrong_preposition) {
        AnalyzedToken curTok = current_token_reading.getReadings().get(tokIndex);
        is_wrong_preposition = isWrongPreposition(curTok, prepositions);
        if (is_wrong_preposition) {
          indexes[0] = tokRead_index;
          indexes[1] = tokIndex;
          return indexes;
        }
        tokIndex++;
      } // end while 2
      // increment
      tokRead_index++;
    } // end while 1

    return indexes;
  }

  /* Lookup for next token matched */
  public int[] getNextMatch_OLD(AnalyzedTokenReadings[] tokens, int current_index, List<String> prepositions) {
    int tokRead_index = current_index;
    int tokIndex = 0;
    int[] indexes = {-1, -1};
    // browse all next  tokens to assure that proper preposition doesn't exist
    boolean is_wrong_preposition = false;
    // used to save skipped tokens
    // initial as first token
    AnalyzedTokenReadings current_token_reading = tokens[current_index];
    AnalyzedToken current_token = current_token_reading.getAnalyzedToken(0);

    while (tokRead_index < tokens.length && !is_wrong_preposition) {
      current_token_reading = tokens[tokRead_index];
      tokIndex = 0;
      while (tokIndex < current_token_reading.getReadings().size() && !is_wrong_preposition) {
        AnalyzedToken curTok = current_token_reading.getReadings().get(tokIndex);
        is_wrong_preposition = isWrongPreposition(curTok, prepositions);
        if (is_wrong_preposition) {
          indexes[0] = tokRead_index;
          indexes[1] = tokIndex;
          return indexes;
        }
        tokIndex++;
      } // end while 2
      // increment
      tokRead_index++;
    } // end while 1

    return indexes;
  }
}
