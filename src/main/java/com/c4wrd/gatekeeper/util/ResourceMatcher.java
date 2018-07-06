package com.c4wrd.gatekeeper.util;

import dk.brics.automaton.Automaton;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to determine if a given resource pattern
 * matches the supplied input, based on a simple DFA implementation
 * of the research pattern.
 */
public class ResourceMatcher {

  /**
   * Local cache of automatons for resource patterns
   *
   * TODO replace with LRU cache from Guava, as many many resources may be
   * cached but never used again
   */
  private static final Map<String, Automaton> AUTOMATON_CACHE = new HashMap<>();

  private static Automaton getAutomaton(String pattern) {
    if (AUTOMATON_CACHE.containsKey(pattern)) {
      return AUTOMATON_CACHE.get(pattern);
    }

    Automaton automaton;

    if (pattern.contains("*")) {
      automaton = Automaton.makeEmptyString();
      for (int i = 0; i < pattern.length(); i++) {
        char c = pattern.charAt(i);
        if (c == '*') {
          automaton = automaton.concatenate(Automaton.makeAnyString());
        } else {
          automaton = automaton.concatenate(Automaton.makeChar(c));
        }
      }
    } else {
      automaton = Automaton.makeString(pattern);
    }

    automaton.minimize();
    AUTOMATON_CACHE.put(pattern, automaton);
    return automaton;
  }

  public static boolean matches(String pattern, String source) {
    if (source == null || pattern == null) {
      return false;
    }

    if (source.length() == 0 || pattern.length() == 0) {
      return false;
    }

    // obtain the DFA for this input pattern
    Automaton automaton = getAutomaton(pattern);
    return automaton.run(source);
  }
}
