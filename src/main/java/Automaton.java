import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.nidi.graphviz.model.Factory.*;


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

    public void viewAutomaton() throws IOException {
        Graph graph = createGraph();
        File tmpImage = File.createTempFile("tmp", ".png", new File("images/"));
        Graphviz.fromGraph(graph).height(1000).render(Format.PNG).toFile(tmpImage);
        Desktop desktop = Desktop.getDesktop();
        desktop.open(tmpImage);
        tmpImage.deleteOnExit();
    }

    //node("a").link(to(node("b")).with(Label.of("Test"))),
    public Graph createGraph() {
        //getNodes(this.initialState);
        List<Node> nodes = getNodes();
        Graph g = graph("example2").directed()
                .graphAttr().with(RankDir.LEFT_TO_RIGHT);
        for (Node node : nodes) {
            g = g.with(node);
        }
        return g;
    }

    public List<Node> getNodes() {
        return transitions.entrySet().stream().flatMap(c -> {
            return getNodes(c.getKey());
        }).collect(Collectors.toList());
    }

    public Stream<Node> getNodes(String state) {
        return transitions.get(state).entrySet().stream()
                .flatMap(c -> {
//                    System.out.printf("State: %s Key: %s Value %s\n", state,c.getKey(), c.getValue());
                    return getNodes(state, c.getKey(), c.getValue());
                });
    }

    //node("a").link(to(node("b")).with(Label.of("Test"))),
    public Stream<Node> getNodes(String from, Character input, Set<String> states) {
        return states.stream().filter(c -> !c.equals(emptySymbol)).map(c -> {
            System.out.printf("State: %s Key: %s Value %s\n", from, input, c);
            return node(from).link(to(node(c)).with(Label.of("" + input)));
        });
    }

    public static void main(String[] args) throws IOException {
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
        //System.out.println("Done");
        test.viewAutomaton();
        //test.toDFA();
    }

}
