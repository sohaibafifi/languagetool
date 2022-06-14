/* LanguageTool, a natural language style checker
 * Copyright (C) 2014 Daniel Naber (http://www.danielnaber.de)
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

import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.rules.AbstractDateCheckFilter;
import org.languagetool.rules.RuleMatch;
import org.languagetool.tools.Tools;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 * Arabic localization of {@link AbstractDateCheckFilter}.
 * @since 2.7
 */
public class DateCheckFilter extends AbstractDateCheckFilter {

  private final DateFilterHelper dateFilterHelper = new DateFilterHelper();


  @Override
  protected Calendar getCalendar() {
    return Calendar.getInstance(Locale.forLanguageTag("ar"));
  }

  @SuppressWarnings("ControlFlowStatementWithoutBraces")
  @Override
  protected int getDayOfWeek(String dayStr) {
    return dateFilterHelper.getDayOfWeek(dayStr);

  }
  @Override
  protected String getDayOfWeek(Calendar date) {
    return date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.forLanguageTag("ar"));
//    return dateFilterHelper.getDayOfWeekName(date.get(Calendar.DAY_OF_WEEK));
  }
  protected String getDayOfWeek(int day) {
    return dateFilterHelper.getDayOfWeekName(day);
  }



  @SuppressWarnings({"ControlFlowStatementWithoutBraces", "MagicNumber"})
  @Override
  protected int getMonth(String monthStr) {
    return dateFilterHelper.getMonth(monthStr);
  }

  /**
   * @param args a map with values for {@code year}, {@code month}, {@code day} (day of month), {@code weekDay}
   */
  // debug only
//  @Override
//  public RuleMatch acceptRuleMatch(RuleMatch match, Map<String, String> args, int patternTokenPos, AnalyzedTokenReadings[] patternTokens) {
//    return  super.acceptRuleMatch(match, args,  patternTokenPos,  patternTokens);
// debug only
//        System.out.println("Ar/DateCheckFilter.java args:" + args.toString());
//    RuleMatch X =  super.acceptRuleMatch(match, args,  patternTokenPos,  patternTokens);
//    if (X!=null) {
//      X.toString();
//      System.out.println("Ar/DateCheckFilter.java rule Match:" + X.toString());
//    }
//    else
//      System.out.println("Ar/DateCheckFilter.java rule Match: NUll");
//return X;

//  }

  }
