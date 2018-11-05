import java.util.*;
import java.util.stream.Collectors;

public class NFAToDFA {
    static Queue<String> toVisit;
    static Set<String> visited;
    static Map<String, Map<Character, Set<String>>> transitionMap;
    static Automaton NFA;
    static Automaton DFA;

    public static Automaton convert(Automaton anNFA) {
        reset();
        NFA = anNFA;
        DFA.initialState = NFA.initialState;
        toVisit.add(NFA.initialState);
        visited.add(NFA.initialState);
        while (!toVisit.isEmpty()) {
            exploreUnion();
        }
        System.out.println("Stop");
        return DFA;
    }

    // Queue: a,b,c,d,e TODO Set?
    public static Map<Character, Set<String>> mergeCharMap(Map<Character, Set<String>> a, Map<Character, Set<String>> b) {
        Map<Character, Set<String>> c = new HashMap<>();
        Set<Character> keys = new HashSet<>();
        if (a != null)
            keys.addAll(a.keySet());
        if (b != null)
            keys.addAll(b.keySet());
        for (Character key : keys) {
            c.put(key, new HashSet<>());
            if (a.keySet().size() > 0)
                c.get(key).addAll(a.getOrDefault(key, null));
            if (b != null && b.get(key) != null)
                c.get(key).addAll(b.getOrDefault(key, null));
        }
        System.out.println(a);
        return c;
    }

    public static void exploreUnion() {
        String stateString = toVisit.poll();
        Map<Character, Set<String>> transitionMap = new HashMap<>();
        List<String> states = Arrays.stream(stateString.split(",")).collect(Collectors.toList());
        transitionMap = states.stream()
                .map(c -> NFA.transitions.get("" + c))
                .reduce(transitionMap,
                        (a, b) -> {
                            return mergeCharMap(a, b);
                        });
        Set<String> keySet = new HashSet<>();
        keySet.addAll(states);
        keySet.retainAll(NFA.finalStates);
        if (!keySet.isEmpty()) {
            DFA.finalStates.add(stateString);
        }
        DFA.transitions.putIfAbsent(stateString, new HashMap<>());
        for (Character key : transitionMap.keySet()) {
            Set<String> tmp = transitionMap.get(key);
            String tmpString = packStates(tmp);
            DFA.transitions.get(stateString).put(key, Collections.singleton(tmpString));
            if (visited.contains(tmpString) || tmpString.isEmpty()) continue;
            else {
                visited.add(tmpString);
                toVisit.add(tmpString);
            }
        }
        System.out.println(transitionMap);
        System.out.println("Done");
    }


    private static String packStates(Set<String> states) {
        String test = states.stream().sorted().filter(c -> !c.equals("_")).reduce("", (a, b) -> {
            return a.equals("") ? b : a + "," + b;
        });
        return test;
    }


    public static void reset() {
        toVisit = new LinkedList<>();
        transitionMap = new HashMap<>();
        visited = new HashSet<>();
        NFA = null;
        DFA = new Automaton();
    }
}
