package net.spark;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

public class PropertiesHandler extends Properties {
    private final Map<String, String> properties;

    public PropertiesHandler(LinkedHashMap<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public Object get(Object key) {
        return properties.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        return properties.put((String) key, (String) value);
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public Enumeration<Object> keys() {
        return new Vector<Object>(properties.keySet()).elements();
    }

    @Override
    public Set<Object> keySet() {
        return new LinkedHashSet<>(properties.keySet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Map.Entry<Object, Object>> entrySet() {
        Set<?> entrySet = properties.entrySet();
        return (Set<Map.Entry<Object, Object>>) entrySet;
    }
}
