package com.lhavanguane.tisimu.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lhavanguane.tisimu.models.DailyVerse;
import com.lhavanguane.tisimu.utils.LanguageManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DailyVerseManager {
    private static final String TAG = "DailyVerseManager";
    private static final String VERSE_FILE = "daily_verses.json";

    private static DailyVerseManager instance;
    private Context context;
    private Gson gson;
    private Map<String, Map<String, DailyVerse>> versesMap;
    private LanguageManager languageManager;

    private DailyVerseManager(Context context) {
        this.context = context.getApplicationContext();
        this.gson = new Gson();
        this.languageManager = LanguageManager.getInstance(context);
        loadVerses();
    }

    public static synchronized DailyVerseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DailyVerseManager(context);
        }
        return instance;
    }

    private void loadVerses() {
        try {
            InputStream inputStream = context.getAssets().open(VERSE_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            JSONObject root = new JSONObject(jsonBuilder.toString());
            JSONObject versesObj = root.getJSONObject("verses");

            versesMap = new HashMap<>();

            // Parse each language
            for (String lang : LanguageManager.SUPPORTED_LANGUAGES) {
                if (versesObj.has(lang)) {
                    JSONObject langVerses = versesObj.getJSONObject(lang);
                    Type type = new TypeToken<Map<String, DailyVerse>>(){}.getType();
                    Map<String, DailyVerse> langMap = gson.fromJson(langVerses.toString(), type);
                    versesMap.put(lang, langMap);
                }
            }

            Log.d(TAG, "Loaded verses for languages: " + versesMap.keySet());

        } catch (Exception e) {
            Log.e(TAG, "Error loading verses", e);
            versesMap = new HashMap<>();
        }
    }

    public DailyVerse getTodaysVerse() {
        return getVerseForDate(new Date());
    }

    public DailyVerse getVerseForDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateString = dateFormat.format(date);

        String currentLang = languageManager.getCurrentLanguage();

        // Try to get verse in current language
        DailyVerse verse = getVerseForDateAndLanguage(dateString, currentLang);

        // Fallback to English if not available
        if (verse == null && !currentLang.equals("en")) {
            verse = getVerseForDateAndLanguage(dateString, "en");
        }

        // Fallback to any available verse if still null
        if (verse == null) {
            verse = getFallbackVerse(currentLang);
        }

        return verse;
    }

    private DailyVerse getVerseForDateAndLanguage(String dateString, String language) {
        if (versesMap.containsKey(language)) {
            Map<String, DailyVerse> langVerses = versesMap.get(language);
            return langVerses.get(dateString);
        }
        return null;
    }

    private DailyVerse getFallbackVerse(String language) {
        if (versesMap.containsKey(language) && !versesMap.get(language).isEmpty()) {
            return versesMap.get(language).values().iterator().next();
        }
        if (versesMap.containsKey("en") && !versesMap.get("en").isEmpty()) {
            return versesMap.get("en").values().iterator().next();
        }
        return createDefaultVerse(language);
    }

    private DailyVerse createDefaultVerse(String language) {
        DailyVerse defaultVerse = new DailyVerse();

        if ("pt".equals(language)) {
            defaultVerse.setVerse("Porque Deus amou o mundo de tal maneira...");
            defaultVerse.setReference("João 3:16");
            defaultVerse.setDevotionalTitle("O Amor de Deus");
            defaultVerse.setDevotionalBody("Deus te ama incondicionalmente.");
            defaultVerse.setApplication("Compartilhe esse amor hoje.");
            defaultVerse.setReflection("Como você tem experimentado o amor de Deus?");
            defaultVerse.setPrayer("Obrigado, Senhor, pelo Teu amor.");
        } else if ("es".equals(language)) {
            defaultVerse.setVerse("Porque de tal manera amó Dios al mundo...");
            defaultVerse.setReference("Juan 3:16");
            defaultVerse.setDevotionalTitle("El Amor de Dios");
            defaultVerse.setDevotionalBody("Dios te ama incondicionalmente.");
            defaultVerse.setApplication("Comparte este amor hoy.");
            defaultVerse.setReflection("¿Cómo has experimentado el amor de Dios?");
            defaultVerse.setPrayer("Gracias, Señor, por Tu amor.");
        } else if ("ts".equals(language)) {
            defaultVerse.setVerse("Hikuva Xikwembu xi rhandzile misava swonghasi...");
            defaultVerse.setReference("Yohane 3:16");
            defaultVerse.setDevotionalTitle("Rirhandzu ra Xikwembu");
            defaultVerse.setDevotionalBody("Xikwembu xa ku rhandza swinene.");
            defaultVerse.setApplication("Avelana rirhandzu leri namuntlha.");
            defaultVerse.setReflection("U ri vonise ku yini rirhandzu ra Xikwembu?");
            defaultVerse.setPrayer("Ndzi nkhensa rirhandzu ra Wena, Yehovha.");
        } else {
            defaultVerse.setVerse("For God so loved the world...");
            defaultVerse.setReference("John 3:16");
            defaultVerse.setDevotionalTitle("God's Love");
            defaultVerse.setDevotionalBody("God loves you unconditionally.");
            defaultVerse.setApplication("Share this love today.");
            defaultVerse.setReflection("How have you experienced God's love?");
            defaultVerse.setPrayer("Thank You, Lord, for Your love.");
        }

        return defaultVerse;
    }

    public void refreshVerses() {
        loadVerses();
    }
}