package edu.avans.nicknam.instagram;

import java.util.*;

/**
 * Created by snick on 19-4-2017.
 */
public class AvailableLocales {
    public final static Locale ENGLISH = new Locale("en"), DUTCH = new Locale("nl"),
            GERMAN = new Locale("de"), SPANISH = new Locale("es");

    public static ArrayList<Locale> getAvailableLocales() {
        return new ArrayList<>(Arrays.asList(ENGLISH, DUTCH, GERMAN, SPANISH));
    }

    public static ArrayList<String> getAvailableLocalesAsDisplayLanguage() {
        ArrayList<String> languages = new ArrayList<>(Arrays.asList(ENGLISH.getDisplayLanguage(ENGLISH),
                DUTCH.getDisplayLanguage(DUTCH), GERMAN.getDisplayLanguage(GERMAN),
                SPANISH.getDisplayLanguage(SPANISH)));
        Collections.sort(languages, String.CASE_INSENSITIVE_ORDER);
        return languages;
    }

    public static HashMap<String, Locale> getAvailableLocalesHashMap() {
        HashMap<String, Locale> localeMap = new HashMap<>();
        for (Locale locale : getAvailableLocales())
            localeMap.put(locale.getDisplayLanguage(locale), locale);
        return localeMap;
    }
}