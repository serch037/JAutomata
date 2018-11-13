import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;
import java.awt.event.*;  

import static guru.nidi.graphviz.model.Factory.*;


public class Automaton{

    static Scanner scanner = new Scanner(System.in);
    String emptySymbol = "_";
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
        Pattern pattern = Pattern.compile("(->|\\*|->\\*|\\*->)?\\s*(\\w*)\\s*(\\w*)\\s*(\\w*)");
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
                    case "->*":
                    case "*->":
                        this.initialState = fromState;
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
        //tmpImage.deleteOnExit();
    }

    //node("a").link(to(node("b")).with(Label.of("Test"))),
    public Graph createGraph() {
        //getNodes(this.initialState);
        List<MutableNode> nodes = getNodes();
        Graph g = graph("example2").directed()
                .graphAttr().with(RankDir.LEFT_TO_RIGHT);
        for (MutableNode node : nodes) {
            g = g.with(node);
        }
        return g;
    }


    public List<MutableNode> getNodes() {
        Map<String, MutableNode> nodes = new HashMap<>();
        transitions.entrySet().stream().forEach(c -> {
//            System.out.println("1: "+c);
            getNodes(c.getKey(), nodes);
        });
        return new ArrayList<>(nodes.values());
    }

    public void getNodes(String state, Map<String, MutableNode> nodes) {
        transitions.get(state).entrySet().stream()
                .forEach(c -> {
                    System.out.println("2: "+c);
                    getNodes(state, c.getKey(), c.getValue(), nodes);
                });
    }

    //node("a").link(to(node("b")).with(Label.of("Test"))),
    public void getNodes(String from, Character input, Set<String> states, Map<String, MutableNode> nodes) {
        System.out.printf("3: %s %s %s\n", from, input, states);
        nodes.putIfAbsent(from, mutNode(from));
        if (from.equals(this.initialState)){
            MutableNode init = mutNode("").add(Shape.POINT).addLink(nodes.get(from));
            nodes.putIfAbsent("", init);
        }
        if (this.finalStates.contains(from)) {
            nodes.get(from).add(Shape.DOUBLE_CIRCLE);
        }
        states.stream().filter(to -> !to.equals(emptySymbol)).forEach(to -> {
            // Find before inserting
            Optional<Link> tmp = nodes.get(from).links().stream().filter(l -> {
                Boolean p = l.asLinkSource().toString().equals(to);
                return p;
            }).findAny();
            if (tmp.isPresent()) {
                String label = tmp.get().attrs().get("label").toString();
                tmp.get().attrs().add("label",label+","+input );
            }else {
                nodes.get(from).addLink(to(node(to)).with(Label.of("" + input)));
            }
            System.out.println("4: "+to);
        });
//        nodes.get(from).addLink(states.stream().filter(c -> !c.equals(emptySymbol)).toArray(String[]::new));
    }



    public static void main(String[] args) throws IOException {
        Automaton test = new Automaton();

        //test 3 https://er.yuvayana.org/nfa-to-dfa-conversion-algorithm-with-solved-example/
        /*
        String f1 = "->a 0 c";
        String f2 = "b 1 c";
        String f3 = "b 1 a";
        String f4 = "c 0 a";
        String f5 = "c 1 a";
        String f6 = "c 0 b";
        String f7 = "*c 0 _";
        String f8 = "*c 1 _";
        String[] tmp = new String[]{f1, f2, f3, f4, f5, f6, f7, f8};
        List<String> fs = new ArrayList<>(Arrays.asList(tmp));
        */

        //test 4 https://www.cs.odu.edu/~toida/nerzic/390teched/regular/fa/nfa-2-dfa.html

        /*
        String f1 = "->*0 a 1";
        String f2 = "0 a 2";

        String f3 = "*1 a 1";
        String f4 = "*1 a 2";

        String f5 = "2 b 1";
        String f6 = "2 b 3";

        String f7 = "3 a 1";
        String f8 = "3 a 2";

        String[] tmp = new String[]{f1, f2, f3, f4, f5, f6, f7, f8};
        //String[] tmp = new String[]{f1};
        List<String> fs = new ArrayList<>(Arrays.asList(tmp));
        */

        String f1 = "->*q0 a q0";
        String f2 = "->*q0 a q1";
        String f3 = "->*q0 a q2";
        String f4 = "->*q0 a q3";
        String f5 = "->*q0 b q1";
        String f6 = "q1 a q1";
        String f7 = "q1 a q2";
        String f8 = "*q2 a q2";
        String f9 = "*q2 a q2";
        String f10 = "*q2 a q2";
        String f11 = "*q2 a q2";
        String f12 = "*q2 a q2";
        String f13 = "*q2 a q2";
        String[] tmp = new String[]{f1, f2, f3, f4, f5, f6, f7, f8};
        List<String> fs = new ArrayList<>(Arrays.asList(tmp));


        test.parseTransitionFunctionsRegex(fs);
        System.out.println("Done");
        test.viewAutomaton();
        Automaton DFA = test.toDFA();
        DFA.viewAutomaton();
    }
}

