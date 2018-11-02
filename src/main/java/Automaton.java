import javafx.collections.transformation.SortedList;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Automaton {

    static Scanner scanner = new Scanner(System.in);
    static String emptySymbol = "_";
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

            // Has special character for empty set
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
        return NFAToDFA.convert(this);
    }

    public void viewAutomaton(){
        //Desktop.getDesktop().open();
    }



    public static void main(String[] args) {
        Automaton test = new Automaton();
        //test.parseAlphabet("a b c d e f g");
        String f1 = "->a 0 a";
        String f2 = "->a 0 b";
        String f3 = "->a 0 c";
        String f4 = "->a 0 d";
        String f5 = "->a 0 e";
        String f6 = "->a 1 d";
        String f7 = "->a 1 e";

        String f8 = "b 0 c";
        String f9 = "b 1 e";

        String f10 = "c 0 _";
        String f11 = "c 1 b";

        String f12 = "d 0 e";
        String f13 = "d 1 _";

        String f14 = "*e 0 _";
        String f15 = "*e 1 _";
        String[] tmp = new String[]{f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15};
        List<String> fs = new ArrayList<>(Arrays.asList(tmp));
        //test.parseTransitionFunctions(fs);
        test.parseTransitionFunctionsRegex(fs);
        System.out.println("Done");
        test.toDFA();
    }

}
