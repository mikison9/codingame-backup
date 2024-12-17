import java.util.*;
import java.io.*;
import java.math.*;

class Rule {
    char from;
    char[] to;

    Rule(char from, char[] to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public int hashCode() {
        int code = this.from;

        for (int x = 0; x < this.to.length; x++) {
            code += to[x] * x;
        }

        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rule) {
            var r = (Rule) obj;
            if (this.to.length != r.to.length) {
                return false;
            }

            for (int i = 0; i < this.to.length; i++) {
                if (this.to[i] != r.to[i]) {
                    return false;
                }
            }

            return this.from == r.from;
        } else {
            return false;
        }
    }
}

class Grammar {
    Set<Rule> rules;
    char start;
    Set<Character> nonTerms;

    Grammar(Set<Rule> rules, char start) {
        this.rules = rules;
        this.start = start;
        this.nonTerms = new HashSet<>();

        for (var r : this.rules) {
            this.nonTerms.add(r.from);
        }
    }

    boolean cyk(String str) {
        if (str.isEmpty()) {
            Rule r = new Rule(this.start, new char[0]);
            return this.rules.contains(r);
        }

        var tab = new ArrayList<ArrayList<HashSet<Character>>>(str.length());

        for (int x = 0; x < str.length(); x++) {
//            tab.add(new ArrayList<>(Collections.nCopies(str.length() - x, new HashSet<Character>())));
            tab.add(new ArrayList<>(str.length() - x));
            for (int y = 0; y < str.length() - x; y++) {
                tab.get(x).add(new HashSet<>());
            }
        }

        for (int i = 0; i < str.length(); i++) {
            var s = str.charAt(i);
            for (var r : this.rules) {
                if (r.to.length == 1 && r.to[0] == s) {
                    tab.get(0).get(i).add(r.from);
                }
            }
        }

//        var rulesArray = this.rules.toArray(new Rule[0]);
        var nonTermsArr = this.nonTerms.toArray(new Character[0]);

        for (int l = 1; l < str.length(); l++) {
            for (int c = 0; c < str.length() - l; c++) {
                var s = tab.get(l).get(c);
                for (int firstSize = 1; firstSize <= l; firstSize++) {
                    var perm1 = tab.get(firstSize - 1).get(c);
                    var perm2 = tab.get(l - firstSize).get(c + firstSize);
                    for (var s1 : perm1) {
                        for (var s2 : perm2) {
                            for (var nt : nonTermsArr) {
                                var to = new char[]{s1, s2};
                                var r = new Rule(nt, to);
                                if (this.rules.contains(r)) {
                                    s.add(r.from);
                                }
                            }
                        }
                    }
                }
            }
        }

        return tab.get(str.length() - 1).get(0).contains(this.start);
    }
}

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {

    public static Rule parseRule(String str) {
        var parts = str.split(" -> ");
        var from = parts[0].charAt(0);

        char[] to = new char[parts[1].length()];

        for (int x = 0; x < parts[1].length(); x++) {
            var ch = parts[1].charAt(x);
            to[x] = ch;
        }

        return new Rule(from, to);
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        String START = in.next();
        if (in.hasNextLine()) {
            in.nextLine();
        }

        Set<Rule> rules = new HashSet<>();

        for (int i = 0; i < N; i++) {
            String RULE = in.nextLine();
            // System.out.println(RULE);
            rules.add(Solution.parseRule(RULE));
        }

        var g = new Grammar(rules, START.charAt(0));

        int T = in.nextInt();
        var words = new ArrayList<String>();
        for (int i = 0; i < T; i++) {
            String WORD = in.next();
            // System.err.println(WORD);
            words.add(WORD);
        }

        // Write an answer using System.out.println()
        // To debug: System.err.println("Debug messages...");

        for (var word : words) {
            System.out.println(g.cyk(word));
        }

        // System.out.println("true");
    }
}