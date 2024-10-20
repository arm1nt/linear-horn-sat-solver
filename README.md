# linear-horn-sat-solver

Horn formulas are a specific subset of propositional logic formulas where a formula is a conjunction of horn clauses.
A horn clause is an implication in which the left side is a conjunction of atoms, and the right side is a single atom.

An example for a Horn Formula is: $(a \land b \land c \implies d) \land (b \land d \land \top \implies a) \land (c \implies \bot)$

This SAT solver takes a horn formula of arbitrary size as input and determines its satisfiability with linear time complexity. If the formula is satisfiable, a satisfying configuration is provided.

### Input format

Each horn clause of the horn formula to be tested for satisfiability must be given on a new line and can contain the following symbols:

| logical symbol | equivalent encoding |
| -------------- | ------------------- |
| $\top$         | 1                   |
| $\bot$         | 0                   |
| $\land$        | &                   |
| $\implies$     | ->                  |

To improve readability, the conjuncts can be grouped using parentheses.

### Example

In this example, the formula introduced earlier is encoded using the symbols understood by the SAT solver:

``````
a & (b & c) -> d
b & d & 1 -> a
c -> 0
``````

The SAT solver's output is:

``````
Verdict: SAT
e.g. (a: false, b: false, c: false, d: false, ⊥: false, ⊤: true)
``````
