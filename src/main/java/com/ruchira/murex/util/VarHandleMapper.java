package com.ruchira.murex.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VarHandleMapper<S, T> {

    private static final Map<Class<?>, Map<String, VarHandle>> classVarHandles = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, FieldMapping>> fieldMappingsBySourceTarget = new ConcurrentHashMap<>();

    private static class FieldMapping {
        final VarHandle sourceHandle;
        final VarHandle targetHandle;

        FieldMapping(VarHandle s, VarHandle t) {
            this.sourceHandle = s;
            this.targetHandle = t;
        }
    }

    private final Map<String, FieldMapping> fieldMappings;

    public VarHandleMapper(Class<S> sourceClass, Class<T> targetClass) {
        String key = sourceClass.getName().concat("->").concat(targetClass.getName());
        this.fieldMappings = fieldMappingsBySourceTarget.computeIfAbsent(
                key, k -> buildFieldMappings(sourceClass, targetClass)
        );
    }

    public static Object getField(Object obj, String fieldName) {
        Map<String, VarHandle> handles = getVarHandles(obj.getClass());
        VarHandle vh = handles.get(fieldName);
        if (vh != null) return vh.get(obj);
        return null;
    }

    public static void setField(Object obj, String fieldName, Object value) {
        Map<String, VarHandle> handles = getVarHandles(obj.getClass());
        VarHandle vh = handles.get(fieldName);
        if (vh != null) vh.set(obj, value);
    }

    public static Map<String, VarHandle> getVarHandles(Class<?> clazz) {
        return classVarHandles.computeIfAbsent(clazz, VarHandleMapper::buildVarHandlesForClass);
    }

    public T copy(S source, Class<T> targetClass, Set<String> includedFields) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            for (String field : includedFields) {
                FieldMapping fm = fieldMappings.get(field);
                if (fm != null) {
                    Object value = fm.sourceHandle.get(source);
                    fm.targetHandle.set(target, value);
                }
            }
            return target;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, VarHandle> buildVarHandlesForClass(Class<?> clazz) {
        Map<String, VarHandle> map = new HashMap<>();
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
            for (Field f : clazz.getDeclaredFields()) {
                try {
                    map.put(f.getName(), lookup.unreflectVarHandle(f));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    private static Map<String, FieldMapping> buildFieldMappings(Class<?> sourceClass, Class<?> targetClass) {
        Map<String, VarHandle> sourceHandles = classVarHandles.computeIfAbsent(sourceClass, VarHandleMapper::buildVarHandlesForClass);
        Map<String, VarHandle> targetHandles = classVarHandles.computeIfAbsent(targetClass, VarHandleMapper::buildVarHandlesForClass);
        Map<String, FieldMapping> map = new HashMap<>();

        for (String field : targetHandles.keySet()) {
            VarHandle s = sourceHandles.get(field);
            VarHandle t = targetHandles.get(field);
            if (s != null) map.put(field, new FieldMapping(s, t));
        }
        return map;
    }
}

