package javalabs.Lab5;

import java.util.*;

public class Task12 {
    static class Repo<ID, T> {
        private final Map<ID, T> store = new LinkedHashMap<>();

        void save(ID id, T entity) {
            if (id == null) throw new IllegalArgumentException("id == null");
            if (entity == null) throw new IllegalArgumentException("entity == null");
            store.put(id, entity);   
        }

        T findById(ID id) {
            return store.get(id);
        }

        List<T> findAll() {
            return new ArrayList<>(store.values()); 
        }

        boolean deleteById(ID id) {
            return store.remove(id) != null;
        }
    }

    public static void main(String[] args) {
        Repo<String, String> repo = new Repo<>();
        repo.save("CS101", "Intro");
        repo.save("CS101", "Intro v2");  
        System.out.println(repo.findById("CS101"));   
        System.out.println(repo.findAll());           
        System.out.println(repo.deleteById("CS101")); 
        System.out.println(repo.findAll());           
    }
}