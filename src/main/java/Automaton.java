import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Transition {
    String state;
    Character symbol;

    public Transition(String state, Character symbol) {
        this.state = state;
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transition that = (Transition) o;
        return Objects.equals(state, that.state) &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, symbol);
    }
}

public class Automaton {

    static Scanner scanner = new Scanner(System.in);
    LinkedHashSet<Character> alphabet;
    Set<String> states;
    Set<String> finalStates;
    String initialState;

    // ∂(qo, a) = {q1}
    // ∂(qo, €) = {q1, q2}
    Map<Transition, Set<String>> transitions;

    public Automaton() {
        alphabet = new LinkedHashSet<>();
        states = new HashSet<>();
        finalStates = new HashSet<>();
        initialState = "";
        transitions = new HashMap<>();
    }

    public static void main(String[] args) {
        Automaton test = new Automaton();
        test.parseAlphabet("a b c d e f g");
        String f1 = "->q0 a q1";
        String f2 = "q0 b q1";
        String f3 = "*q0 a q2";
        List<String> fs = new ArrayList<>();
        fs.add(f1);
        fs.add(f2);
        fs.add(f3);
        //test.parseTransitionFunctions(fs);
        test.parseTransitionFunctionsRegex(fs);
        System.out.println("Done");
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

    // q1 a q2
    public void parseTransitionFunctions(List<String> lines) {
        lines.stream()
                .map(c -> c.split("\\s"))
                .forEach(c -> {
                            String startState = c[0];
                            // Check if is startState
                            if (startState.substring(0, 2).equals("->")) {
                                System.out.println(startState.substring(0, 2));
                                startState = startState.substring(2);
                                System.out.println("Start");
                            }
                            char symbol = c[1].charAt(0);
                            String endState = c[2];
                            states.add(startState);
                            alphabet.add(symbol);
                            Transition transition = new Transition(startState, symbol);
                            transitions.putIfAbsent(transition, new HashSet<>());
                            transitions.get(transition).add(endState);
//                            if (transitions.containsKey(transition)) {
//                                transitions.get(transition).add(endState);
//                            } else {
//                                transitions.get(transition).add(endState);
//                            }
                        }
                );
    }

    public void parseTransitionFunctionsRegex(List<String> lines) {
        Pattern pattern = Pattern.compile("(->|\\*)?\\s*(\\w*)\\s*(\\w*)\\s*(\\w*)");
        for (String c : lines) {
            Matcher matcher = pattern.matcher(c);
            if (!matcher.matches()) continue;
            System.out.println("Test");
            String fromState = matcher.group(2);
            char symbol = matcher.group(3).charAt(0);
            String toState = matcher.group(4);
            if (matcher.group(1) != null) {
                switch (matcher.group(1)) {
                    case "->":
                        this.finalStates.add(fromState);
                    case "*":
                        System.out.println("Is end");
                        this.initialState = fromState;
                        break;
                }
            }
            states.add(fromState);
            alphabet.add(symbol);
            Transition transition = new Transition(fromState, symbol);
            transitions.putIfAbsent(transition, new HashSet<>());
            transitions.get(transition).add(toState);
            System.out.println("Matched");
        }
    }

    public Automaton toDFA() {
        return null;
    }

}
