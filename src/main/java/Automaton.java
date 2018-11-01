import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Automaton {

    static Scanner scanner = new Scanner(System.in);
    LinkedHashSet<Character> alphabet;
    Set<String> states;
    Set<String> finalStates;
    String initialState;

    // ∂(qo, a) = {q1}
    // ∂(qo, €) = {q1, q2}
    Map<String, Map<Character, Set<String>>> transitions;

    public Automaton() {
        alphabet = new LinkedHashSet<>();
        states = new HashSet<>();
        finalStates = new HashSet<>();
        initialState = "";
        transitions = new HashMap<>();
    }


    public void parseAlphabet(String input) {
        alphabet = Arrays.stream(input.split("\\s"))
                .map(c -> c.charAt(0))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // a b c d e f g
    // *q1 q0 q1 q2 q3 q4 q5 q6
    // q1 {q0 q1 q2 q3} q1 q2 q3 q4 q5 q6
    public void parseTransitionTable(List<String> lines) {
        for (String line : lines) {

        }
    }


    public void parseTransitionFunctionsRegex(List<String> lines) {
        Pattern pattern = Pattern.compile("(->|\\*)?\\s*(\\w*)\\s*(\\w*)\\s*(\\w*)");
        for (String c : lines) {
            Matcher matcher = pattern.matcher(c);
            if (!matcher.matches()) continue;
            String fromState = matcher.group(2);
            char symbol = matcher.group(3).charAt(0);
            String toState = matcher.group(4);
            if (matcher.group(1) != null) {
                switch (matcher.group(1)) {
                    case "->":
                        this.initialState = fromState;
                        break;
                    case "*":
                        this.finalStates.add(fromState);
                        break;
                }
            }
            states.add(fromState);
            alphabet.add(symbol);
            transitions.putIfAbsent(fromState, new HashMap<>());
            transitions.get(fromState).putIfAbsent(symbol, new HashSet<>());
            transitions.get(fromState).get(symbol).add(toState);
        }
    }

    public Automaton toDFA() {
        return null;
    }

    public static void main(String[] args) {
        Automaton test = new Automaton();
        test.parseAlphabet("a b c d e f g");
        String f1 = "->q0 a q1";
        String f3 = "q0 a q2";
        String f2 = "*q2 b q1";
        String f4 = "q1 b q1";
        List<String> fs = new ArrayList<>();
        fs.add(f1);
        fs.add(f2);
        fs.add(f3);
        fs.add(f4);
        //test.parseTransitionFunctions(fs);
        test.parseTransitionFunctionsRegex(fs);
        System.out.println("Done");
    }

}
