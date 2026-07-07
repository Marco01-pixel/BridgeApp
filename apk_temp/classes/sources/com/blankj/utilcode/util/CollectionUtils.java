package com.blankj.utilcode.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class CollectionUtils {

    public interface Closure<E> {
        void execute(int i, E e);
    }

    public interface Predicate<E> {
        boolean evaluate(E e);
    }

    public interface Transformer<E1, E2> {
        E2 transform(E1 e1);
    }

    private CollectionUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    @SafeVarargs
    public static <E> List<E> newUnmodifiableList(E... array) {
        return Collections.unmodifiableList(newArrayList(array));
    }

    @SafeVarargs
    public static <E> List<E> newUnmodifiableListNotNull(E... array) {
        return Collections.unmodifiableList(newArrayListNotNull(array));
    }

    @SafeVarargs
    public static <E> ArrayList<E> newArrayList(E... array) {
        ArrayList<E> list = new ArrayList<>();
        if (array == null || array.length == 0) {
            return list;
        }
        for (E e : array) {
            list.add(e);
        }
        return list;
    }

    @SafeVarargs
    public static <E> ArrayList<E> newArrayListNotNull(E... array) {
        ArrayList<E> list = new ArrayList<>();
        if (array == null || array.length == 0) {
            return list;
        }
        for (E e : array) {
            if (e != null) {
                list.add(e);
            }
        }
        return list;
    }

    @SafeVarargs
    public static <E> LinkedList<E> newLinkedList(E... array) {
        LinkedList<E> list = new LinkedList<>();
        if (array == null || array.length == 0) {
            return list;
        }
        for (E e : array) {
            list.add(e);
        }
        return list;
    }

    @SafeVarargs
    public static <E> LinkedList<E> newLinkedListNotNull(E... array) {
        LinkedList<E> list = new LinkedList<>();
        if (array == null || array.length == 0) {
            return list;
        }
        for (E e : array) {
            if (e != null) {
                list.add(e);
            }
        }
        return list;
    }

    @SafeVarargs
    public static <E> HashSet<E> newHashSet(E... array) {
        HashSet<E> set = new HashSet<>();
        if (array == null || array.length == 0) {
            return set;
        }
        for (E e : array) {
            set.add(e);
        }
        return set;
    }

    @SafeVarargs
    public static <E> HashSet<E> newHashSetNotNull(E... array) {
        HashSet<E> set = new HashSet<>();
        if (array == null || array.length == 0) {
            return set;
        }
        for (E e : array) {
            if (e != null) {
                set.add(e);
            }
        }
        return set;
    }

    @SafeVarargs
    public static <E> TreeSet<E> newTreeSet(Comparator<E> comparator, E... array) {
        TreeSet<E> set = new TreeSet<>(comparator);
        if (array == null || array.length == 0) {
            return set;
        }
        for (E e : array) {
            set.add(e);
        }
        return set;
    }

    @SafeVarargs
    public static <E> TreeSet<E> newTreeSetNotNull(Comparator<E> comparator, E... array) {
        TreeSet<E> set = new TreeSet<>(comparator);
        if (array == null || array.length == 0) {
            return set;
        }
        for (E e : array) {
            if (e != null) {
                set.add(e);
            }
        }
        return set;
    }

    public static Collection newSynchronizedCollection(Collection collection) {
        return Collections.synchronizedCollection(collection);
    }

    public static Collection newUnmodifiableCollection(Collection collection) {
        return Collections.unmodifiableCollection(collection);
    }

    public static Collection union(Collection a, Collection b) {
        if (a == null && b == null) {
            return new ArrayList();
        }
        if (a == null) {
            return new ArrayList(b);
        }
        if (b == null) {
            return new ArrayList(a);
        }
        ArrayList<Object> list = new ArrayList<>();
        Map<Object, Integer> mapA = getCardinalityMap(a);
        Map<Object, Integer> mapB = getCardinalityMap(b);
        Set<Object> elts = new HashSet<>((Collection<? extends Object>) a);
        elts.addAll(b);
        for (Object obj : elts) {
            int m = Math.max(getFreq(obj, mapA), getFreq(obj, mapB));
            for (int i = 0; i < m; i++) {
                list.add(obj);
            }
        }
        return list;
    }

    public static Collection intersection(Collection a, Collection b) {
        if (a == null || b == null) {
            return new ArrayList();
        }
        ArrayList<Object> list = new ArrayList<>();
        Map<Object, Integer> cardinalityMap = getCardinalityMap(a);
        Map<Object, Integer> cardinalityMap2 = getCardinalityMap(b);
        Set<Object> elts = new HashSet<>((Collection<? extends Object>) a);
        elts.addAll(b);
        for (Object obj : elts) {
            int m = Math.min(getFreq(obj, cardinalityMap), getFreq(obj, cardinalityMap2));
            for (int i = 0; i < m; i++) {
                list.add(obj);
            }
        }
        return list;
    }

    private static int getFreq(Object obj, Map freqMap) {
        Integer count = (Integer) freqMap.get(obj);
        if (count != null) {
            return count.intValue();
        }
        return 0;
    }

    public static Collection disjunction(Collection a, Collection b) {
        if (a == null && b == null) {
            return new ArrayList();
        }
        if (a == null) {
            return new ArrayList(b);
        }
        if (b == null) {
            return new ArrayList(a);
        }
        ArrayList<Object> list = new ArrayList<>();
        Map<Object, Integer> cardinalityMap = getCardinalityMap(a);
        Map<Object, Integer> cardinalityMap2 = getCardinalityMap(b);
        Set<Object> elts = new HashSet<>((Collection<? extends Object>) a);
        elts.addAll(b);
        for (Object obj : elts) {
            int m = Math.max(getFreq(obj, cardinalityMap), getFreq(obj, cardinalityMap2)) - Math.min(getFreq(obj, cardinalityMap), getFreq(obj, cardinalityMap2));
            for (int i = 0; i < m; i++) {
                list.add(obj);
            }
        }
        return list;
    }

    public static Collection subtract(Collection a, Collection b) {
        if (a == null) {
            return new ArrayList();
        }
        if (b == null) {
            return new ArrayList(a);
        }
        ArrayList<Object> list = new ArrayList<>((Collection<? extends Object>) a);
        for (Object o : b) {
            list.remove(o);
        }
        return list;
    }

    public static boolean containsAny(Collection coll1, Collection coll2) {
        if (coll1 == null || coll2 == null) {
            return false;
        }
        if (coll1.size() < coll2.size()) {
            for (Object o : coll1) {
                if (coll2.contains(o)) {
                    return true;
                }
            }
        } else {
            for (Object o2 : coll2) {
                if (coll1.contains(o2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Map<Object, Integer> getCardinalityMap(Collection coll) {
        Map<Object, Integer> count = new HashMap<>();
        if (coll == null) {
            return count;
        }
        for (Object obj : coll) {
            Integer c = count.get(obj);
            if (c != null) {
                count.put(obj, Integer.valueOf(c.intValue() + 1));
            } else {
                count.put(obj, 1);
            }
        }
        return count;
    }

    public static boolean isSubCollection(Collection a, Collection b) {
        if (a == null || b == null) {
            return false;
        }
        Map<Object, Integer> cardinalityMap = getCardinalityMap(a);
        Map<Object, Integer> cardinalityMap2 = getCardinalityMap(b);
        for (Object obj : a) {
            if (getFreq(obj, cardinalityMap) > getFreq(obj, cardinalityMap2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isProperSubCollection(Collection a, Collection b) {
        return a != null && b != null && a.size() < b.size() && isSubCollection(a, b);
    }

    public static boolean isEqualCollection(Collection a, Collection b) {
        if (a == null || b == null || a.size() != b.size()) {
            return false;
        }
        Map<Object, Integer> cardinalityMap = getCardinalityMap(a);
        Map<Object, Integer> cardinalityMap2 = getCardinalityMap(b);
        if (cardinalityMap.size() != cardinalityMap2.size()) {
            return false;
        }
        for (Object obj : cardinalityMap.keySet()) {
            if (getFreq(obj, cardinalityMap) != getFreq(obj, cardinalityMap2)) {
                return false;
            }
        }
        return true;
    }

    public static <E> int cardinality(E e, Collection<E> collection) {
        if (collection == null) {
            return 0;
        }
        if (collection instanceof Set) {
            return collection.contains(e) ? 1 : 0;
        }
        int i = 0;
        if (e == null) {
            Iterator<E> it = collection.iterator();
            while (it.hasNext()) {
                if (it.next() == null) {
                    i++;
                }
            }
        } else {
            Iterator<E> it2 = collection.iterator();
            while (it2.hasNext()) {
                if (e.equals(it2.next())) {
                    i++;
                }
            }
        }
        return i;
    }

    public static <E> E find(Collection<E> collection, Predicate<E> predicate) {
        if (collection == null || predicate == null) {
            return null;
        }
        for (E item : collection) {
            if (predicate.evaluate(item)) {
                return item;
            }
        }
        return null;
    }

    public static <E> void forAllDo(Collection<E> collection, Closure<E> closure) {
        if (collection == null || closure == null) {
            return;
        }
        int index = 0;
        for (E e : collection) {
            closure.execute(index, e);
            index++;
        }
    }

    public static <E> void filter(Collection<E> collection, Predicate<E> predicate) {
        if (collection == null || predicate == null) {
            return;
        }
        Iterator<E> it = collection.iterator();
        while (it.hasNext()) {
            if (!predicate.evaluate(it.next())) {
                it.remove();
            }
        }
    }

    public static <E> Collection<E> select(Collection<E> inputCollection, Predicate<E> predicate) {
        if (inputCollection == null || predicate == null) {
            return new ArrayList();
        }
        ArrayList<E> answer = new ArrayList<>(inputCollection.size());
        for (E o : inputCollection) {
            if (predicate.evaluate(o)) {
                answer.add(o);
            }
        }
        return answer;
    }

    public static <E> Collection<E> selectRejected(Collection<E> inputCollection, Predicate<E> predicate) {
        if (inputCollection == null || predicate == null) {
            return new ArrayList();
        }
        ArrayList<E> answer = new ArrayList<>(inputCollection.size());
        for (E o : inputCollection) {
            if (!predicate.evaluate(o)) {
                answer.add(o);
            }
        }
        return answer;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <E1, E2> void transform(Collection<E1> collection, Transformer<E1, E2> transformer) {
        if (collection == null || transformer == 0) {
            return;
        }
        if (collection instanceof List) {
            List list = (List) collection;
            ListIterator it = list.listIterator();
            while (it.hasNext()) {
                it.set(transformer.transform(it.next()));
            }
            return;
        }
        Collection<? extends E1> collectionCollect = collect(collection, transformer);
        collection.clear();
        collection.addAll(collectionCollect);
    }

    public static <E1, E2> Collection<E2> collect(Collection<E1> inputCollection, Transformer<E1, E2> transformer) {
        List<E2> answer = new ArrayList<>();
        if (inputCollection == null || transformer == null) {
            return answer;
        }
        for (E1 e1 : inputCollection) {
            answer.add(transformer.transform(e1));
        }
        return answer;
    }

    public static <E> int countMatches(Collection<E> collection, Predicate<E> predicate) {
        if (collection == null || predicate == null) {
            return 0;
        }
        int count = 0;
        for (E o : collection) {
            if (predicate.evaluate(o)) {
                count++;
            }
        }
        return count;
    }

    public static <E> boolean exists(Collection<E> collection, Predicate<E> predicate) {
        if (collection == null || predicate == null) {
            return false;
        }
        for (E o : collection) {
            if (predicate.evaluate(o)) {
                return true;
            }
        }
        return false;
    }

    public static <E> boolean addIgnoreNull(Collection<E> collection, E object) {
        return (collection == null || object == null || !collection.add(object)) ? false : true;
    }

    public static <E> void addAll(Collection<E> collection, Iterator<E> iterator) {
        if (collection == null || iterator == null) {
            return;
        }
        while (iterator.hasNext()) {
            collection.add(iterator.next());
        }
    }

    public static <E> void addAll(Collection<E> collection, Enumeration<E> enumeration) {
        if (collection == null || enumeration == null) {
            return;
        }
        while (enumeration.hasMoreElements()) {
            collection.add(enumeration.nextElement());
        }
    }

    public static <E> void addAll(Collection<E> collection, E[] elements) {
        if (collection == null || elements == null || elements.length == 0) {
            return;
        }
        collection.addAll(Arrays.asList(elements));
    }

    public static Object get(Object object, int index) {
        if (object == null) {
            return null;
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
        }
        if (object instanceof Map) {
            Map map = (Map) object;
            Iterator iterator = map.entrySet().iterator();
            return get(iterator, index);
        }
        if (object instanceof List) {
            return ((List) object).get(index);
        }
        if (object instanceof Object[]) {
            return ((Object[]) object)[index];
        }
        if (object instanceof Iterator) {
            Iterator it = (Iterator) object;
            while (it.hasNext()) {
                index--;
                if (index == -1) {
                    return it.next();
                }
                it.next();
            }
            throw new IndexOutOfBoundsException("Entry does not exist: " + index);
        }
        if (object instanceof Collection) {
            Iterator iterator2 = ((Collection) object).iterator();
            return get(iterator2, index);
        }
        if (object instanceof Enumeration) {
            Enumeration it2 = (Enumeration) object;
            while (it2.hasMoreElements()) {
                index--;
                if (index == -1) {
                    return it2.nextElement();
                }
                it2.nextElement();
            }
            throw new IndexOutOfBoundsException("Entry does not exist: " + index);
        }
        try {
            return Array.get(object, index);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }

    public static int size(Object object) {
        if (object == null) {
            return 0;
        }
        int total = 0;
        if (object instanceof Map) {
            return ((Map) object).size();
        }
        if (object instanceof Collection) {
            return ((Collection) object).size();
        }
        if (object instanceof Object[]) {
            return ((Object[]) object).length;
        }
        if (object instanceof Iterator) {
            Iterator it = (Iterator) object;
            while (it.hasNext()) {
                total++;
                it.next();
            }
            return total;
        }
        if (object instanceof Enumeration) {
            Enumeration it2 = (Enumeration) object;
            while (it2.hasMoreElements()) {
                total++;
                it2.nextElement();
            }
            return total;
        }
        try {
            return Array.getLength(object);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }

    public static boolean sizeIsEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof Collection) {
            return ((Collection) object).isEmpty();
        }
        if (object instanceof Map) {
            return ((Map) object).isEmpty();
        }
        if (object instanceof Object[]) {
            return ((Object[]) object).length == 0;
        }
        if (object instanceof Iterator) {
            return true ^ ((Iterator) object).hasNext();
        }
        if (object instanceof Enumeration) {
            return true ^ ((Enumeration) object).hasMoreElements();
        }
        try {
            return Array.getLength(object) == 0;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }

    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.size() == 0;
    }

    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }

    public static <E> Collection<E> retainAll(Collection<E> collection, Collection<E> retain) {
        if (collection == null || retain == null) {
            return new ArrayList();
        }
        List<E> list = new ArrayList<>();
        for (E item : collection) {
            if (retain.contains(item)) {
                list.add(item);
            }
        }
        return list;
    }

    public static <E> Collection<E> removeAll(Collection<E> collection, Collection<E> remove) {
        if (collection == null) {
            return new ArrayList();
        }
        if (remove == null) {
            return new ArrayList(collection);
        }
        List<E> list = new ArrayList<>();
        for (E obj : collection) {
            if (!remove.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }

    public static <T> void shuffle(List<T> list) {
        Collections.shuffle(list);
    }

    public static String toString(Collection collection) {
        return collection == null ? "null" : collection.toString();
    }
}
