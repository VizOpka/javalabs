package javalabs.Lab5;

import java.util.*;
import java.util.function.Function;

public class Task7 {
    static <T> T firstOrThrow(List<T> list) {
        if (list == null || list.isEmpty()) throw new NoSuchElementException();
        return list.get(0);
    }

    static <T> void swap(List<T> list, int i, int j) {
        if (i < 0 || i >= list.size() || j < 0 || j >= list.size())
            throw new IndexOutOfBoundsException();
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }

    static <T, K> Map<K, T> indexBy(List<T> list, Function<T, K> keyFn) {
        Map<K, T> map = new LinkedHashMap<>();
        for (T item : list) {
            K key = keyFn.apply(item);
            if (map.containsKey(key)) {
                throw new IllegalStateException("Дубликат ключа: " + key);
            }
            map.put(key, item);
        }
        return map;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
        swap(list, 0, 2);
        System.out.println(list);                 
        System.out.println(firstOrThrow(list));   
        System.out.println(indexBy(list, new Function<String, String>() {
            @Override public String apply(String s) { return s.toUpperCase(); }
        }));
    }
}