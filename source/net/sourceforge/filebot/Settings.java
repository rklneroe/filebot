
package net.sourceforge.filebot;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


public class Settings {
	
	public static final String NAME = "FileBot";
	public static final String VERSION = "2.0";
	
	public static final String ROOT = NAME.toLowerCase();
	
	public static final String SELECTED_PANEL = "panel";
	public static final String SEARCH_HISTORY = "search/history";
	public static final String SUBTITLE_HISTORY = "subtitle/history";
	public static final String SUBTITLE_LANGUAGE = "subtitle/language";
	
	private static final Settings settings = new Settings();
	
	
	public static Settings getSettings() {
		return settings;
	}
	
	private final Preferences prefs;
	
	
	private Settings() {
		this.prefs = Preferences.userRoot().node(ROOT);
	}
	

	public void putString(String key, String value) {
		prefs.put(key, value);
	}
	

	public String getString(String key, String def) {
		return prefs.get(key, def);
	}
	

	public void putInt(String key, int value) {
		prefs.putInt(key, value);
	}
	

	public int getInt(String key, int def) {
		return prefs.getInt(key, def);
	}
	

	public void putBoolean(String key, boolean value) {
		prefs.putBoolean(key, value);
	}
	

	public boolean getBoolean(String key, boolean def) {
		return prefs.getBoolean(key, def);
	}
	

	public Collection<String> getStringList(String key) {
		Preferences listNode = prefs.node(key);
		
		List<String> list = new ArrayList<String>();
		
		try {
			for (String nodeKey : listNode.keys()) {
				list.add(listNode.get(nodeKey, null));
			}
		} catch (BackingStoreException e) {
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, e.toString(), e);
		}
		
		return list;
	}
	

	public void putStringList(String key, Collection<String> list) {
		Preferences listNode = getClearNode(key);
		
		int i = 0;
		
		for (String entry : list) {
			listNode.put(Integer.toString(i), entry);
			i++;
		}
	}
	

	public Map<String, String> getStringMap(String key) {
		Preferences mapNode = prefs.node(key);
		
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			for (String mapNodeKey : mapNode.keys()) {
				map.put(mapNodeKey, mapNode.get(mapNodeKey, null));
			}
		} catch (BackingStoreException e) {
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, e.toString(), e);
		}
		
		return map;
	}
	

	public void putStringMap(String key, Map<String, String> map) {
		Preferences mapNode = getClearNode(key);
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			mapNode.put(entry.getKey(), entry.getValue());
		}
	}
	

	public Map<String, Integer> getIntegerMap(String key) {
		Map<String, String> entries = getStringMap(key);
		
		Map<String, Integer> map = new HashMap<String, Integer>(entries.size());
		
		for (Entry<String, String> entry : entries.entrySet()) {
			try {
				map.put(entry.getKey(), Integer.valueOf(entry.getValue()));
			} catch (NumberFormatException e) {
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, e.toString(), e);
			}
		}
		
		return map;
	}
	

	public void putIntegerMap(String key, Map<String, Integer> map) {
		Map<String, String> entries = new HashMap<String, String>();
		
		for (Entry<String, Integer> entry : map.entrySet()) {
			entries.put(entry.getKey(), entry.getValue().toString());
		}
		
		putStringMap(key, entries);
	}
	

	public Map<String, Boolean> getBooleanMap(String key) {
		Map<String, String> entries = getStringMap(key);
		
		Map<String, Boolean> map = new HashMap<String, Boolean>(entries.size());
		
		for (Entry<String, String> entry : entries.entrySet()) {
			map.put(entry.getKey(), Boolean.valueOf(entry.getValue()));
		}
		
		return map;
	}
	

	public void putBooleanMap(String key, Map<String, Boolean> map) {
		Map<String, String> entries = new HashMap<String, String>();
		
		for (Entry<String, Boolean> entry : map.entrySet()) {
			entries.put(entry.getKey(), entry.getValue().toString());
		}
		
		putStringMap(key, entries);
	}
	

	public void putBooleanMapEntry(String nodeKey, String mapKey, Boolean value) {
		prefs.node(nodeKey).put(mapKey, value.toString());
	}
	

	public void clear() {
		try {
			for (String child : prefs.childrenNames()) {
				prefs.node(child).removeNode();
			}
		} catch (BackingStoreException e) {
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, e.toString(), e);
		}
	}
	

	private Preferences getClearNode(String nodeName) {
		Preferences node = prefs.node(nodeName);
		
		try {
			node.clear();
		} catch (BackingStoreException e) {
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, e.toString(), e);
		}
		
		return node;
	}
}
