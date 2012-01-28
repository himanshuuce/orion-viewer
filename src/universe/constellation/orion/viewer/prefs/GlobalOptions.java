package universe.constellation.orion.viewer.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.code.orion_viewer.Common;

import java.io.Serializable;
import java.util.*;

/**
 * User: mike
 * Date: 26.11.11
 * Time: 16:18
 */
public class GlobalOptions implements Serializable {

    public static final int MAX_RECENT_ENTRIES = 10;

    public static final String NEXT_KEY = "next_key_keycode";

    public static final String PREV_KEY = "prev_key_keycode";

    private static final String RECENT_PREFIX = "recent_";

    public final static String SWAP_KEYS = "SWAP_KEYS";

    public final static String DEFAULT_ORIENTATION = "BOOK_ORIENTATION";

    public final static String APPLY_AND_CLOSE = "APPLY_AND_CLOSE";

    public final static String FULL_SCREEN = "FULL_SCREEN";

    public final static String TAP_ZONE = "TAP_ZONE";

    public final static String SCREEN_ORIENTATION = "SCREEN_ORIENTATION";

    public final static String EINK_OPTIMIZATION = "EINK_OPTIMIZATION";

    public final static String EINK_TOTAL_AFTER = "EINK_TOTAL_AFTER";

    public final static String DICTIONARY = "DICTIONARY";

    public final static String LONG_CROP_VALUE = "LONG_CROP_VALUE";
    public final static String SCREEN_OVERLAPPING_HORIZONTAL = "SCREEN_OVERLAPPING_HORIZONTAL";
    public final static String SCREEN_OVERLAPPING_VERTICAL = "SCREEN_OVERLAPPING_VERTICAL";

    public final static String BRIGHTNESS = "BRIGHTNESS";

    public final static String CUSTOM_BRIGHTNESS = "CUSTOM_BRIGHTNESS";

    public final static String APPLICATION_THEME = "APPLICATION_THEME";

    public final static String OPEN_RECENT_BOOK = "OPEN_RECENT_BOOK";

    private String lastOpenedDirectory;

    private LinkedList<RecentEntry> recentFiles;

    private SharedPreferences prefs;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;

    private Map<String, Object> prefValues = new HashMap<String, Object>();

    private List<PrefListener> prefListener = new ArrayList<PrefListener>();

    GlobalOptions(Context applicationContext) {
        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        lastOpenedDirectory = prefs.getString(Common.LAST_OPENED_DIRECTORY, null);

        recentFiles = new LinkedList<RecentEntry>();
        for (int i = 0; i < MAX_RECENT_ENTRIES; i++) {
            String entry = prefs.getString(RECENT_PREFIX + i, null);
            if (entry == null) {
                break;
            } else {
                recentFiles.add(new RecentEntry(entry));
            }
        }

        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences preferences, String name) {
                Common.d("onSharedPreferenceChanged " + name);
                Object oldValue = prefValues.remove(name);
                pushChangePropertyEvent(name, oldValue);
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public String getLastOpenedDirectory() {
        return lastOpenedDirectory;
    }

    public void addRecentEntry(RecentEntry newEntry) {
        for (Iterator<RecentEntry> iterator = recentFiles.iterator(); iterator.hasNext(); ) {
            RecentEntry recentEntry =  iterator.next();
            if (recentEntry.getPath().equals(newEntry.getPath())) {
                iterator.remove();
                break;
            }
        }

        recentFiles.add(0, newEntry);

        if (recentFiles.size() > MAX_RECENT_ENTRIES) {
            recentFiles.removeLast();
        }
    }

    public void saveDirectory() {
        //TODO
    }

    public void saveRecents() {
        int i = 0;
        SharedPreferences.Editor editor = prefs.edit();
        for (Iterator<RecentEntry> iterator = recentFiles.iterator(); iterator.hasNext(); i++) {
            RecentEntry next =  iterator.next();
            editor.putString(RECENT_PREFIX  + i, next.getPath());
        }
        editor.commit();
    }

    public static class RecentEntry implements Serializable{

        private String path;

        public RecentEntry(String path) {
            this.path = path;
        }


        public String getPath() {
            return path;
        }

        public String getLastPathElement() {
            return path.substring(path.lastIndexOf("/") + 1);
        }

        @Override
        public String toString() {
            return getLastPathElement();
        }
    }

    public LinkedList<RecentEntry> getRecentFiles() {
        return recentFiles;
    }

    public int getPrevKey() {
        return getIntFromStringProperty(PREV_KEY, -1);
    }

    public int getNextKey() {
        return getIntFromStringProperty(NEXT_KEY, -1);
    }

//    public void onDestroy(Context applicationContext) {
//        if (onSharedPreferenceChangeListener != null) {
//            PreferenceManager.getDefaultSharedPreferences(applicationContext).unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
//            onSharedPreferenceChangeListener = null;
//        }
//    }

    public boolean isSwapKeys() {
        return getBooleanProperty(SWAP_KEYS, false);
    }

//    public boolean isUseNookKeys() {
//        return useNookKeys;
//    }

    public int getDefaultOrientation() {
        return getIntFromStringProperty(DEFAULT_ORIENTATION, 0);
    }

    public boolean isApplyAndClose() {
        return getBooleanProperty(APPLY_AND_CLOSE, false);
    }

    public boolean isFullScreen() {
        return getBooleanProperty(FULL_SCREEN, false);
    }

    public int getActionCode(int i, int j, boolean isLong) {
        String key = OrionTapActivity.getKey(i, j, isLong);
        int code = getInt(key, -1);
        if (code == -1) {
            prefValues.remove(key);
            code = getInt(key, OrionTapActivity.getDefaultAction(i, j, isLong));
        }
//        tapCodes[i * 3 + j][isLong ? 1 : 0] = code;
        return code;
    }


//    private void resetTapCodes() {
//        for (int i = 0; i < tapCodes.length; i++) {
//            int[] tapCode = tapCodes[i];
//            for (int j = 0; j < tapCode.length; j++) {
//                tapCode[j] = -1;
//            }
//        }
//    }

    public String getDictionary() {
        return getStringProperty(DICTIONARY, "FORA");
    }

    public int getEinkRefreshAfter() {
        return getIntFromStringProperty(EINK_TOTAL_AFTER, 10);
    }

    public boolean isEinkOptimization() {
        return getBooleanProperty(EINK_OPTIMIZATION, false);
    }

//    public Integer getInteger(String key) {
//        if (!prefValues.containsKey(key)) {
//            String value = prefs.getString(key, null);
//            Integer newIntValue = null;
//            if (value == null || "".equals(value)) {
//                return null;
//            } else {
//                newIntValue = Integer.valueOf(value);
//            }
//            prefValues.put(key, newIntValue);
//        }
//        return (Integer) prefValues.get(key);
//    }

    public int getIntFromStringProperty(String key, int defaultValue) {
        if (!prefValues.containsKey(key)) {
            String value = prefs.getString(key, null);
            Integer newIntValue = null;
            if (value == null || "".equals(value)) {
                newIntValue = defaultValue;
            } else {
                newIntValue = Integer.valueOf(value);
            }
            prefValues.put(key, newIntValue);
        }
        return (Integer) prefValues.get(key);
    }

    public int getInt(String key, int defaultValue) {
        if (!prefValues.containsKey(key)) {
            int value = prefs.getInt(key, defaultValue);
            prefValues.put(key, value);
        }
        return (Integer) prefValues.get(key);
    }

    public String getStringProperty(String key, String defaultValue) {
        if (!prefValues.containsKey(key)) {
            String value = prefs.getString(key, defaultValue);
            prefValues.put(key, value);
        }
        return (String) prefValues.get(key);
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        if (!prefValues.containsKey(key)) {
            boolean value = prefs.getBoolean(key, defaultValue);
            prefValues.put(key, value);
        }
        return (Boolean) prefValues.get(key);
    }


    public int getLongCrop() {
        return getIntFromStringProperty(LONG_CROP_VALUE, 10);
    }

    public int getVerticalOverlapping() {
        return getIntFromStringProperty(SCREEN_OVERLAPPING_VERTICAL, 3);
    }

    public int getHorizontalOverlapping() {
        return getIntFromStringProperty(SCREEN_OVERLAPPING_HORIZONTAL, 3);
    }

    public int getBrightness() {
        return getIntFromStringProperty(BRIGHTNESS, 100);
    }

    public boolean isCustomBrightness() {
        return getBooleanProperty(CUSTOM_BRIGHTNESS, false);
    }

    public boolean isOpenRecentBook() {
        return getBooleanProperty(OPEN_RECENT_BOOK, false);
    }


    public String getApplicationTheme() {
        return getStringProperty(APPLICATION_THEME, "DEFAULT");
    }

    public void subscribe(PrefListener listener) {
        prefListener.add(listener);
    }

    private void pushChangePropertyEvent(String key, Object oldValue) {
        for (int i = 0; i < prefListener.size(); i++) {
            PrefListener prefListener1 =  prefListener.get(i);
            try {
                prefListener1.onPreferenceChanged(this, key, oldValue);
            } catch (Exception e) {
                Common.d(e);
                //TODO show error
            }
        }
    }


    public void unsubscribe(PrefListener listener) {
        prefListener.remove(listener);
    }

}