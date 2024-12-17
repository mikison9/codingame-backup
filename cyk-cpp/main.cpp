#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <set>
#include <unordered_set>

using namespace std;

struct RuleHash {
    std::size_t operator()(const pair<char, string > &p) const noexcept {
        return std::hash<char>{}(p.first) + std::hash<string>{}(p.second);;
    }
};


struct Grammar {
    unordered_set<char> modulators;
    unordered_set<char> basicUnits;
    unordered_set<std::pair<char, string >, RuleHash > rules;
    char initialModulator;
};

bool cyk(const Grammar &rules, const string &command) {
    // handle empty strings
    if (command.empty()) {
        const pair<char, string > r = {rules.initialModulator, ""};
        return rules.rules.contains(r);
    }

    vector<vector<set<char> > > tab(command.size());

    for (unsigned int i = 0; i < command.size(); i++) {
        tab[i] = vector<set<char> >(command.size() - i);
    }

    // prvni uroven tabulky = ktere neterminaly se primo prepisou na tyto terminaly
    for (unsigned int i = 0; i < command.size(); i++) {
        const char s = command[i];
        for (auto nt: rules.modulators) {
            const pair<char, string > r = {nt, {s}};
            if (rules.rules.contains(r)) {
                tab[0][i].insert(r.first);
            }
        }
    }

    // loop over rows
    for (unsigned int l = 1; l < command.size(); l++) {
        // loop over this row
        for (unsigned int c = 0; c < command.size() - l; c++) {
            // try every possible permutation of lengths
            for (unsigned int first_size = 1; first_size <= l; first_size++) {
                auto perm1 = tab[first_size - 1][c];
                auto perm2 = tab[l - first_size][c + first_size];
                for (auto s1: perm1) {
                    for (auto s2: perm2) {
                        for (auto nt: rules.modulators) {
                            pair<char, string > r = {nt, {s1, s2}};
                            if (rules.rules.contains(r)) {
                                tab[l][c].insert(r.first);
                            }
                        }
                    }
                }
            }
        }
    }

    if (tab[command.size() - 1][0].contains(rules.initialModulator)) {
        return true;
    }

    return false;
}

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/

void addRule(Grammar &g, const string &line) {
    auto from = line[0];
    g.modulators.insert(from);
    auto to = line.substr(5);
    // vector<char> to;

    // pair<char, vector<char>> r = {from, to};
    g.rules.emplace(from, to);
}

int main() {
    int n;
    cin >> n;
    cin.ignore();
    string start;
    cin >> start;
    cin.ignore();
    Grammar g;
    g.initialModulator = start[0];
    for (int i = 0; i < n; i++) {
        string rule;
        getline(cin, rule);

        addRule(g, rule);
    }
    int t;
    // cerr << "start: " << g.initialModulator << endl;
    // cerr << "terms:" << endl;
    // for (auto ch : g.basicUnits) {
    //     cerr << ch << ", ";
    // }
    // cerr << endl;

    // cerr << "non terms:" << endl;
    // for (auto ch : g.modulators) {
    //     cerr << ch << ", ";
    // }
    // cerr << endl;

    //  cerr << "rules:" << endl;
    // for (auto r : g.rules) {
    //     cerr << r.first << " -> ";
    //     for (auto ch : r.second) {
    //         cerr << ch;
    //     }
    //     cerr << endl;
    // }

    cin >> t;
    cin.ignore();
    for (int i = 0; i < t; i++) {
        string word;
        cin >> word;
        cin.ignore();

        cout << (cyk(g, word) == true ? "true" : "false") << endl;
    }

    // Write an answer using cout. DON'T FORGET THE "<< endl"
    // To debug: cerr << "Debug messages..." << endl;

    // cout << "true" << endl;
}
