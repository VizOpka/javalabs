package javalabs.Lab5;

import java.util.*;

public class Task15 {
    static Map<String, List<String>> graph = new LinkedHashMap<>();

    static void addVertex(String v) {
        if (!graph.containsKey(v)) graph.put(v, new ArrayList<String>());
    }
    static void addEdge(String from, String to) {
        addVertex(from);
        addVertex(to);
        graph.get(from).add(to);
    }

    static List<String> topoOrder() {
        Map<String, Integer> indeg = new HashMap<>();
        for (String v : graph.keySet()) indeg.put(v, 0);
        for (List<String> outs : graph.values()) {
            for (String to : outs) {
                indeg.put(to, indeg.get(to) + 1);
            }
        }

        TreeSet<String> ready = new TreeSet<>();
        for (Map.Entry<String, Integer> e : indeg.entrySet()) {
            if (e.getValue() == 0) ready.add(e.getKey());
        }

        List<String> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            String v = ready.pollFirst();
            order.add(v);
            for (String to : graph.get(v)) {
                int d = indeg.get(to) - 1;
                indeg.put(to, d);
                if (d == 0) ready.add(to);
            }
        }

        if (order.size() != graph.size()) {
            throw new IllegalStateException("Есть цикл");
        }
        return order;
    }

    static Map<String, Integer> state = new HashMap<>(); 
    static Deque<String> path = new ArrayDeque<>();

    static List<String> findCycle() {
        state.clear();
        path.clear();
        for (String v : graph.keySet()) {
            List<String> c = dfs(v);
            if (c != null) return c;
        }
        return null;
    }

    static List<String> dfs(String v) {
        Integer s = state.get(v);
        if (s != null && s == 1) {           
            List<String> cycle = new ArrayList<>();
            boolean started = false;
            for (String x : path) {
                if (x.equals(v)) started = true;
                if (started) cycle.add(x);
            }
            cycle.add(v);
            return cycle;
        }
        if (s != null && s == 2) return null; 

        state.put(v, 1);
        path.addLast(v);
        for (String to : graph.getOrDefault(v, Collections.<String>emptyList())) {
            List<String> c = dfs(to);
            if (c != null) return c;
        }
        path.removeLast();
        state.put(v, 2);
        return null;
    }

    public static void main(String[] args) {

        addEdge("MA101", "CS101");
        addEdge("CS101", "CS201");
        addEdge("CS201", "CS301");
        System.out.println("Порядок: " + topoOrder());  
        System.out.println("Цикл: " + findCycle());     

        graph.clear();
        addEdge("A", "B");
        addEdge("B", "C");
        addEdge("C", "A");
        System.out.println("Цикл: " + findCycle());     
    }
}