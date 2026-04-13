# Topic 8: Stacks & Queues

Document 8 of 20 in the FAANG DSA Prep series.

---

## Overview

Stacks and queues are the workhorses of interview problems that involve ordered processing. A stack gives you the most recent thing first (LIFO). A queue gives you the oldest thing first (FIFO). That single difference in access order drives an enormous variety of problems.

Topic 7 covered monotonic stacks, which maintain a sorted invariant during pushes. This document covers the broader stack/queue pattern family: parentheses and bracket validation, expression evaluation (infix/postfix), stack-based simulation, queue-based BFS setup, and design problems like implementing one structure using the other.

These patterns appear in some of the most frequently asked interview questions across all FAANG companies. LC 20 (Valid Parentheses) and LC 155 (Min Stack) are top-5 most-asked problems at Amazon, Google, and Meta. If you're interviewing at any major tech company, you will see at least one problem from this document.

**Top companies asking these problems:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5 Intuition](#eli5-intuition)
3. [When to Use — Recognition Signals](#when-to-use--recognition-signals)
4. [Core Templates in Java](#core-templates-in-java)
5. [Real-World Applications](#real-world-applications)
6. [Problem Categories and Solutions](#problem-categories-and-solutions)
7. [Common Mistakes and Edge Cases](#common-mistakes-and-edge-cases)
8. [Pattern Comparison](#pattern-comparison)
9. [Quick Reference Cheat Sheet](#quick-reference-cheat-sheet)
10. [Practice Roadmap](#practice-roadmap)

---

## Core Concept

### Stack: LIFO (Last In, First Out)

A stack processes elements in reverse order of insertion. The last element pushed is the first one popped. Think of it as a pile where you can only touch the top.

**Java: Use `ArrayDeque`, not `Stack`**

The `java.util.Stack` class extends `Vector`, which is synchronized. Every push and pop acquires a lock, even in single-threaded code. It's slow, and the inheritance from `Vector` exposes methods like `get(index)` that break the stack abstraction entirely.

`ArrayDeque` is the correct choice. It's backed by a resizable array, has no synchronization overhead, and is faster in practice.

```java
// WRONG — synchronized, slow, legacy
Stack<Integer> stack = new Stack<>();

// CORRECT — fast, modern, preferred
Deque<Integer> stack = new ArrayDeque<>();
stack.push(1);       // push to top
stack.pop();         // remove from top
stack.peek();        // view top without removing
stack.isEmpty();     // check if empty
```

**Stack methods on ArrayDeque:**

| Operation | Method | Throws if empty? |
|-----------|--------|-----------------|
| Push to top | `push(e)` | No |
| Pop from top | `pop()` | Yes — NoSuchElementException |
| Peek at top | `peek()` | Returns null |
| Check empty | `isEmpty()` | No |

### Queue: FIFO (First In, First Out)

A queue processes elements in insertion order. The first element added is the first one removed. Think of a line at a store.

**Java: Use `ArrayDeque` implementing `Queue` interface**

```java
// Queue backed by ArrayDeque
Queue<Integer> queue = new ArrayDeque<>();
queue.offer(1);      // add to back
queue.poll();        // remove from front (returns null if empty)
queue.peek();        // view front without removing
queue.isEmpty();     // check if empty
```

**Queue methods:**

| Operation | Method | Throws if empty? |
|-----------|--------|-----------------|
| Add to back | `offer(e)` | No (returns false) |
| Remove from front | `poll()` | Returns null |
| Peek at front | `peek()` | Returns null |
| Check empty | `isEmpty()` | No |

Avoid `add()` and `remove()` on queues — they throw exceptions on failure. Prefer `offer()` and `poll()` which return null/false instead.

### Deque: Double-Ended Queue

`ArrayDeque` implements `Deque`, which supports insertion and removal from both ends. This makes it usable as both a stack and a queue.

```java
Deque<Integer> deque = new ArrayDeque<>();

// Stack operations (front/top)
deque.push(1);       // addFirst
deque.pop();         // removeFirst
deque.peek();        // peekFirst

// Queue operations (back)
deque.offerLast(1);  // addLast
deque.pollFirst();   // removeFirst
deque.peekFirst();   // view front
```

This dual nature is what makes `ArrayDeque` the single correct choice for both stack and queue problems in Java.

### The Core Insight

> When the most recent event matters most, use a Stack.
> When the oldest event matters most, use a Queue.

Parentheses matching? The most recent unmatched open bracket is what you need to check against the current close bracket. Stack.

BFS level-order traversal? You need to process nodes in the order you discovered them. Queue.

Expression evaluation? You need to apply operators in the right order, respecting precedence. Two stacks.

### Pattern Family

1. **Matching/Validation** — brackets, HTML tags, parentheses
2. **Expression Evaluation** — infix to postfix, calculator problems
3. **Stack as History** — undo operations, backspace simulation
4. **Design Problems** — implement one structure using the other
5. **Queue for BFS** — foundation for Topics 11 (Trees) and 14 (Graphs)

---

## ELI5 Intuition

### Stack: A Stack of Plates

Imagine a stack of plates in a cafeteria. You can only add a plate to the top, and you can only take a plate from the top. The last plate you put on is the first one you take off.

That's a stack. Last in, first out.

### Queue: A Line at a Store

Imagine people waiting in line at a checkout. The first person who got in line is the first person served. New people join at the back. Nobody cuts in the middle.

That's a queue. First in, first out.

### Parentheses Matching: Promises and Fulfillment

Every opening bracket is a promise: "I will be closed later." Push that promise onto a stack.

Every closing bracket is a fulfillment: "I'm closing the most recent promise." Pop the stack and check if the closing bracket matches the opening bracket you just popped.

If the stack is empty when you try to pop (no open promise to fulfill), or if the brackets don't match, the string is invalid. If the stack isn't empty at the end (unfulfilled promises remain), also invalid.

```
Input: "{[()]}"

Process:
  '{' → push '{'. Stack: ['{']
  '[' → push '['. Stack: ['{', '[']
  '(' → push '('. Stack: ['{', '[', '(']
  ')' → pop '(' → matches ')'. Stack: ['{', '[']
  ']' → pop '[' → matches ']'. Stack: ['{']
  '}' → pop '{' → matches '}'. Stack: []

Stack empty at end → VALID
```

### Expression Evaluation: Respecting Order of Operations

When you see `3 + 4 * 2`, you can't just process left to right. Multiplication has higher precedence than addition. A stack lets you "defer" lower-precedence operations until you've handled the higher-precedence ones.

Think of it as: "I'll deal with this addition later. First let me finish this multiplication."

---

## When to Use — Recognition Signals

### Green Flags (reach for a stack or queue)

- "valid parentheses", "matching brackets", "balanced brackets"
- "evaluate expression", "basic calculator", "reverse Polish notation"
- "simplify path", "canonical path"
- "decode string", "nested encoding"
- "backspace string compare"
- "min stack", "design a stack with..."
- "implement queue using stacks", "implement stack using queues"
- "asteroid collision", "simulate collisions"
- "undo/redo", "browser history"
- "next greater element" (also monotonic stack — see Topic 7)

### Red Flags (stack/queue is wrong)

- Need random access by index → use array or list
- Need sorted order at all times → use heap or TreeMap
- Need to find elements by value → use HashMap
- Need range queries → use segment tree or prefix sum (Topic 5)

### The Bracket/Parentheses Trigger

Any problem involving nested structures — brackets, HTML tags, function calls, file paths with `..` — almost always uses a stack. The nesting creates a LIFO relationship: the innermost structure must be resolved before the outer one.

### The Expression Trigger

Any problem involving operator precedence, parenthesized expressions, or postfix/prefix notation uses a stack. The stack defers lower-priority operations while higher-priority ones execute.

---

## Core Templates in Java

### Template 1: Bracket/Parentheses Matching

The fundamental template. Push open brackets, pop on close brackets, verify match.

```java
public boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    
    for (char c : s.toCharArray()) {
        // Push all open brackets
        if (c == '(' || c == '[' || c == '{') {
            stack.push(c);
        } else {
            // Closing bracket: stack must be non-empty and top must match
            if (stack.isEmpty()) return false;
            
            char top = stack.pop();
            if (c == ')' && top != '(') return false;
            if (c == ']' && top != '[') return false;
            if (c == '}' && top != '{') return false;
        }
    }
    
    // All promises must be fulfilled
    return stack.isEmpty();
}
```

**Execution trace for `"({[]})"`:**

```
c='('  → push '('     stack: ['(']
c='{'  → push '{'     stack: ['(', '{']
c='['  → push '['     stack: ['(', '{', '[']
c=']'  → pop '[' → matches ']'   stack: ['(', '{']
c='}'  → pop '{' → matches '}'   stack: ['(']
c=')'  → pop '(' → matches ')'   stack: []

stack.isEmpty() = true → return true
```

**Execution trace for `"([)]"`:**

```
c='('  → push '('     stack: ['(']
c='['  → push '['     stack: ['(', '[']
c=')'  → pop '[' → '[' != '(' → return false
```

**Complexity:** O(n) time, O(n) space.

**Variant — using a map for cleaner matching:**

```java
public boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    Map<Character, Character> map = Map.of(')', '(', ']', '[', '}', '{');
    
    for (char c : s.toCharArray()) {
        if (map.containsKey(c)) {
            // Closing bracket
            if (stack.isEmpty() || stack.peek() != map.get(c)) return false;
            stack.pop();
        } else {
            stack.push(c);
        }
    }
    
    return stack.isEmpty();
}
```

---

### Template 2: Expression Evaluation (Two Stacks)

Two stacks: one for numbers, one for operators. Apply operators based on precedence.

```java
// Basic Calculator II: handles +, -, *, / (no parentheses)
public int calculate(String s) {
    Deque<Integer> stack = new ArrayDeque<>();
    int num = 0;
    char op = '+';  // Start with '+' so first number gets pushed
    
    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        
        if (Character.isDigit(c)) {
            num = num * 10 + (c - '0');
        }
        
        // Process operator when we hit a new operator or end of string
        if ((!Character.isDigit(c) && c != ' ') || i == s.length() - 1) {
            if (op == '+') stack.push(num);
            else if (op == '-') stack.push(-num);
            else if (op == '*') stack.push(stack.pop() * num);
            else if (op == '/') stack.push(stack.pop() / num);
            
            op = c;
            num = 0;
        }
    }
    
    // Sum everything in the stack
    int result = 0;
    while (!stack.isEmpty()) result += stack.pop();
    return result;
}
```

**Key insight:** Push `+num` or `-num` for addition/subtraction (defer to final sum). For `*` and `/`, immediately apply to the top of the stack because they have higher precedence.

**Execution trace for `"3+2*2"`:**

```
i=0, c='3', num=3
i=1, c='+', op='+' → push(3). stack:[3]. op='+', num=0
i=2, c='2', num=2
i=3, c='*', op='+' → push(2). stack:[3,2]. op='*', num=0
i=4, c='2', num=2, end of string
     op='*' → push(pop()*2) = push(2*2) = push(4). stack:[3,4]

sum = 3 + 4 = 7
```

---

### Template 3: Stack as Simulation/History

Process elements one by one. Use the stack to track state, undo operations, or simulate collisions.

```java
// Generic simulation template
public int[] simulate(int[] input) {
    Deque<Integer> stack = new ArrayDeque<>();
    
    for (int val : input) {
        // Condition to pop/modify based on problem rules
        while (!stack.isEmpty() && shouldResolve(stack.peek(), val)) {
            stack.pop();
            // possibly modify val or break
        }
        
        // Push current element (or skip if consumed)
        if (shouldPush(val)) {
            stack.push(val);
        }
    }
    
    // Convert stack to result array
    int[] result = new int[stack.size()];
    for (int i = result.length - 1; i >= 0; i--) {
        result[i] = stack.pop();
    }
    return result;
}
```

**Backspace simulation example:**

```java
// Build the "typed" string using a stack
public String processTyping(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
        if (c == '#') {
            if (!stack.isEmpty()) stack.pop();
        } else {
            stack.push(c);
        }
    }
    StringBuilder sb = new StringBuilder();
    while (!stack.isEmpty()) sb.append(stack.pop());
    return sb.reverse().toString();
}
```

---

### Template 4: Queue-Based Level Processing (BFS Foundation)

This template is the foundation for BFS in trees (Topic 11) and graphs (Topic 14). Introduced here because the queue mechanics are the core of the pattern.

```java
// Level-order processing with a queue
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new ArrayDeque<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();  // Snapshot: how many nodes at this level
        List<Integer> level = new ArrayList<>();
        
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            
            // Enqueue children for next level
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        
        result.add(level);
    }
    
    return result;
}
```

**Key mechanics:**
1. Seed the queue with the starting node(s)
2. Snapshot `queue.size()` before the inner loop — this is the current level's count
3. Process exactly that many nodes, enqueuing their neighbors
4. Repeat until queue is empty

This exact pattern appears in LC 102, LC 107, LC 199, LC 994, LC 1091, and dozens more. Mastering it here pays dividends across Topics 11 and 14.

---

## Real-World Applications

### 1. Browser Back/Forward Navigation

Your browser maintains two stacks: `backStack` and `forwardStack`.

- Visit a new page: push current URL to `backStack`, clear `forwardStack`
- Click Back: pop from `backStack`, push current URL to `forwardStack`
- Click Forward: pop from `forwardStack`, push current URL to `backStack`

This is exactly the "Implement Stack" design pattern, applied to navigation history.

### 2. Undo/Redo in Text Editors

Two stacks: `undoStack` and `redoStack`.

- Make a change: push the change to `undoStack`, clear `redoStack`
- Undo: pop from `undoStack`, push to `redoStack`, reverse the change
- Redo: pop from `redoStack`, push to `undoStack`, reapply the change

VS Code, IntelliJ, and every major editor uses this exact pattern.

### 3. Function Call Stack

When your program calls a function, the runtime pushes a stack frame containing local variables, parameters, and the return address. When the function returns, the frame is popped and execution resumes at the return address.

This is why stack overflow happens: too many nested calls fill the call stack. Recursion is literally a stack. Converting recursive algorithms to iterative ones means managing your own explicit stack (see Pattern Comparison section).

### 4. Expression Evaluation in Compilers

Compilers parse expressions using the shunting-yard algorithm (Dijkstra, 1961), which uses two stacks to convert infix notation (`3 + 4 * 2`) to postfix (`3 4 2 * +`). The postfix form is then trivially evaluated with a single stack. LC 150 and LC 224 are direct implementations of this.

### 5. Message Queues (RabbitMQ, Kafka)

Message queues like RabbitMQ and Kafka consumer groups implement FIFO semantics at scale. Producers push messages to the back of the queue. Consumers pull from the front. The queue decouples producers from consumers, allowing them to operate at different speeds.

Kafka's consumer groups add a twist: each consumer group maintains its own offset (position in the queue), so multiple groups can independently consume the same stream.

### 6. Print Spooler

When multiple users send print jobs to a shared printer, the jobs queue up in FIFO order. The printer processes them in the order received. This is a classic queue application — fairness through ordering.

---

## Problem Categories and Solutions

---

### Category A: Parentheses / Brackets

---

#### LC 20 — Valid Parentheses (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

**Problem:** Given a string containing only `'('`, `')'`, `'{'`, `'}'`, `'['`, `']'`, determine if the input string is valid. A string is valid if open brackets are closed by the same type of bracket, in the correct order.

**Pattern:** Bracket matching (Template 1)

**Approach:**
- Push every open bracket onto the stack
- For every close bracket, pop the stack and verify the types match
- At the end, the stack must be empty

**Java Solution:**

```java
class Solution {
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if (c == ')' && top != '(') return false;
                if (c == ']' && top != '[') return false;
                if (c == '}' && top != '{') return false;
            }
        }
        
        return stack.isEmpty();
    }
}
```

**Dry Run — `"({[]})"`:**

```
Input: ( { [ ] } )
       0 1 2 3 4 5

i=0, c='(' → open → push '('     stack: ['(']
i=1, c='{' → open → push '{'     stack: ['(', '{']
i=2, c='[' → open → push '['     stack: ['(', '{', '[']
i=3, c=']' → close → pop '[' → '[' matches ']' ✓   stack: ['(', '{']
i=4, c='}' → close → pop '{' → '{' matches '}' ✓   stack: ['(']
i=5, c=')' → close → pop '(' → '(' matches ')' ✓   stack: []

stack.isEmpty() = true → return true ✓
```

**Dry Run — `"([)]"`:**

```
Input: ( [ ) ]
       0 1 2 3

i=0, c='(' → push '('     stack: ['(']
i=1, c='[' → push '['     stack: ['(', '[']
i=2, c=')' → close → pop '[' → '[' != '(' → return false ✗
```

**Dry Run — `"("`:**

```
i=0, c='(' → push '('     stack: ['(']

stack.isEmpty() = false → return false ✗
```

**Edge Cases:**
- Empty string → stack is empty at end → return `true`
- Single character → either push (stack non-empty → false) or close with empty stack → false
- All same type: `"((("` → stack has 3 elements → false
- Interleaved: `"([)]"` → mismatch on pop → false

**Complexity:** O(n) time, O(n) space.

---

#### LC 1249 — Minimum Remove to Make Valid Parentheses (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given a string with lowercase letters and parentheses, remove the minimum number of parentheses to make the string valid. Return any valid result.

**Pattern:** Bracket matching with index tracking

**Approach:**
- Use a stack to track indices of unmatched `'('`
- Use a set to track indices of unmatched `')'`
- After processing, all indices remaining in the stack (unmatched `'('`) and the set (unmatched `')'`) should be removed

```java
class Solution {
    public String minRemoveToMakeValid(String s) {
        Deque<Integer> stack = new ArrayDeque<>();  // indices of unmatched '('
        Set<Integer> toRemove = new HashSet<>();
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                stack.push(i);
            } else if (c == ')') {
                if (stack.isEmpty()) {
                    toRemove.add(i);  // unmatched ')'
                } else {
                    stack.pop();  // matched pair
                }
            }
        }
        
        // Remaining indices in stack are unmatched '('
        while (!stack.isEmpty()) toRemove.add(stack.pop());
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (!toRemove.contains(i)) sb.append(s.charAt(i));
        }
        
        return sb.toString();
    }
}
```

**Trace for `"lee(t(c)o)de)"`:**

```
i=0  'l' → skip
i=1  'e' → skip
i=2  'e' → skip
i=3  '(' → push 3.   stack: [3]
i=4  't' → skip
i=5  '(' → push 5.   stack: [3, 5]
i=6  'c' → skip
i=7  ')' → pop 5.    stack: [3]
i=8  'o' → skip
i=9  ')' → pop 3.    stack: []
i=10 'd' → skip
i=11 'e' → skip
i=12 ')' → stack empty → toRemove.add(12)

toRemove = {12}
Result: "lee(t(c)o)de"
```

**Complexity:** O(n) time, O(n) space.

---

#### LC 32 — Longest Valid Parentheses (Hard)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given a string containing only `'('` and `')'`, find the length of the longest valid (well-formed) parentheses substring.

**Pattern:** Stack tracking indices of unmatched brackets

**Approach:**
- Push indices onto the stack instead of characters
- Initialize the stack with `-1` as a base boundary
- When a valid pair is matched, the length is `i - stack.peek()`
- When an unmatched `')'` is found, push its index as the new boundary

```java
class Solution {
    public int longestValidParentheses(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1);  // base boundary
        int maxLen = 0;
        
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();  // try to match
                if (stack.isEmpty()) {
                    stack.push(i);  // new boundary: this ')' is unmatched
                } else {
                    maxLen = Math.max(maxLen, i - stack.peek());
                }
            }
        }
        
        return maxLen;
    }
}
```

**Trace for `"(()"`:**

```
stack: [-1]

i=0, '(' → push 0.   stack: [-1, 0]
i=1, '(' → push 1.   stack: [-1, 0, 1]
i=2, ')' → pop 1.    stack: [-1, 0]
           not empty → maxLen = max(0, 2-0) = 2

return 2
```

**Trace for `")()()"`:**

```
stack: [-1]

i=0, ')' → pop -1.   stack: []
           empty → push 0.   stack: [0]
i=1, '(' → push 1.   stack: [0, 1]
i=2, ')' → pop 1.    stack: [0]
           not empty → maxLen = max(0, 2-0) = 2
i=3, '(' → push 3.   stack: [0, 3]
i=4, ')' → pop 3.    stack: [0]
           not empty → maxLen = max(2, 4-0) = 4

return 4
```

**Complexity:** O(n) time, O(n) space.

---

#### LC 921 — Minimum Add to Make Parentheses Valid (Medium)

**Companies:** Amazon, Google, Meta

**Problem:** Given a parentheses string, return the minimum number of parentheses you must add to make the string valid.

**Pattern:** Count unmatched brackets

**Approach:** Track open count and close count. When a `')'` has no matching `'('`, increment close count. At the end, open count holds unmatched `'('`.

```java
class Solution {
    public int minAddToMakeValid(String s) {
        int open = 0;   // unmatched '('
        int close = 0;  // unmatched ')'
        
        for (char c : s.toCharArray()) {
            if (c == '(') {
                open++;
            } else {
                if (open > 0) open--;  // match with existing '('
                else close++;          // no match available
            }
        }
        
        return open + close;
    }
}
```

This is O(1) space — no stack needed because we only track counts, not positions.

**Complexity:** O(n) time, O(1) space.

---

### Category B: Expression Evaluation

---

#### LC 150 — Evaluate Reverse Polish Notation (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Evaluate an expression in Reverse Polish Notation (postfix). Valid operators are `+`, `-`, `*`, `/`. Each operand may be an integer or another expression.

**Pattern:** Single stack for operands

**Approach:**
- Push numbers onto the stack
- When you see an operator, pop two numbers, apply the operator, push the result
- The final answer is the only element left in the stack

```java
class Solution {
    public int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        
        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") ||
                token.equals("*") || token.equals("/")) {
                int b = stack.pop();  // second operand
                int a = stack.pop();  // first operand
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                }
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        
        return stack.pop();
    }
}
```

**Trace for `["2","1","+","3","*"]`:**

```
token="2" → push 2.   stack: [2]
token="1" → push 1.   stack: [2, 1]
token="+" → b=1, a=2 → push(2+1)=3.   stack: [3]
token="3" → push 3.   stack: [3, 3]
token="*" → b=3, a=3 → push(3*3)=9.   stack: [9]

return 9  (which is (2+1)*3)
```

**Important:** Pop order matters. `b = stack.pop()` first, then `a = stack.pop()`. For subtraction and division, `a - b` and `a / b` (not `b - a`).

**Complexity:** O(n) time, O(n) space.

---

#### LC 227 — Basic Calculator II (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Implement a basic calculator to evaluate a string expression containing `+`, `-`, `*`, `/` and non-negative integers. No parentheses.

**Pattern:** Single stack, deferred addition/subtraction

**Key insight:** `*` and `/` have higher precedence than `+` and `-`. Handle `*`/`/` immediately (apply to top of stack). Defer `+`/`-` by pushing `+num` or `-num`. Sum the stack at the end.

```java
class Solution {
    public int calculate(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        int num = 0;
        char op = '+';
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }
            
            if ((!Character.isDigit(c) && c != ' ') || i == s.length() - 1) {
                switch (op) {
                    case '+': stack.push(num); break;
                    case '-': stack.push(-num); break;
                    case '*': stack.push(stack.pop() * num); break;
                    case '/': stack.push(stack.pop() / num); break;
                }
                op = c;
                num = 0;
            }
        }
        
        int result = 0;
        while (!stack.isEmpty()) result += stack.pop();
        return result;
    }
}
```

**Trace for `"14-3/2"`:**

```
i=0, c='1', num=1
i=1, c='4', num=14
i=2, c='-', op='+' → push(14). stack:[14]. op='-', num=0
i=3, c='3', num=3
i=4, c='/', op='-' → push(-3). stack:[14,-3]. op='/', num=0
i=5, c='2', num=2, end of string
     op='/' → push(pop()/2) = push(-3/2) = push(-1). stack:[14,-1]

sum = 14 + (-1) = 13
```

**Complexity:** O(n) time, O(n) space.

---

#### LC 224 — Basic Calculator (Hard)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Implement a basic calculator to evaluate a string expression containing `+`, `-`, `(`, `)`, and non-negative integers.

**Pattern:** Stack for sign context, handle parentheses by saving/restoring sign state

**Key insight:** Parentheses change the "sign context." When you enter `-(...)`, every operator inside is flipped. Track the current sign and use a stack to save/restore sign context when entering/leaving parentheses.

```java
class Solution {
    public int calculate(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        int result = 0;
        int num = 0;
        int sign = 1;  // 1 for positive, -1 for negative
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            } else if (c == '+') {
                result += sign * num;
                num = 0;
                sign = 1;
            } else if (c == '-') {
                result += sign * num;
                num = 0;
                sign = -1;
            } else if (c == '(') {
                // Save current result and sign, start fresh inside parens
                stack.push(result);
                stack.push(sign);
                result = 0;
                sign = 1;
            } else if (c == ')') {
                result += sign * num;
                num = 0;
                result *= stack.pop();   // multiply by sign before '('
                result += stack.pop();   // add result before '('
            }
        }
        
        result += sign * num;
        return result;
    }
}
```

**Trace for `"1+(4+5+2)-3"`:**

```
c='1' → num=1
c='+' → result += 1*1 = 1. num=0, sign=1
c='(' → push(1), push(1). stack:[1,1]. result=0, sign=1
c='4' → num=4
c='+' → result += 1*4 = 4. num=0, sign=1
c='5' → num=5
c='+' → result += 1*5 = 9. num=0, sign=1
c='2' → num=2
c=')' → result += 1*2 = 11. num=0
        result *= pop() = 11*1 = 11
        result += pop() = 11+1 = 12
c='-' → result += 1*0 = 12. num=0, sign=-1
c='3' → num=3

Final: result += -1*3 = 12-3 = 9
```

**Complexity:** O(n) time, O(n) space.

---

#### LC 394 — Decode String (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given an encoded string like `"3[a2[c]]"`, decode it. The encoding rule is `k[encoded_string]`, meaning the `encoded_string` inside the brackets is repeated exactly `k` times.

**Pattern:** Two stacks — one for counts, one for strings

**Approach:**
- When you see a digit, build the number
- When you see `[`, push the current string and current count onto their respective stacks, reset both
- When you see `]`, pop count and previous string, repeat current string `count` times, append to previous string
- When you see a letter, append to current string

```java
class Solution {
    public String decodeString(String s) {
        Deque<Integer> countStack = new ArrayDeque<>();
        Deque<StringBuilder> strStack = new ArrayDeque<>();
        StringBuilder current = new StringBuilder();
        int k = 0;
        
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                k = k * 10 + (c - '0');
            } else if (c == '[') {
                countStack.push(k);
                strStack.push(current);
                current = new StringBuilder();
                k = 0;
            } else if (c == ']') {
                int count = countStack.pop();
                StringBuilder prev = strStack.pop();
                for (int i = 0; i < count; i++) {
                    prev.append(current);
                }
                current = prev;
            } else {
                current.append(c);
            }
        }
        
        return current.toString();
    }
}
```

**Trace for `"3[a2[c]]"`:**

```
c='3' → k=3
c='[' → push(3), push(""). current="", k=0
c='a' → current="a"
c='2' → k=2
c='[' → push(2), push("a"). current="", k=0
c='c' → current="c"
c=']' → count=2, prev="a". prev += "c"*2 = "acc". current="acc"
c=']' → count=3, prev="". prev += "acc"*3 = "accaccacc". current="accaccacc"

return "accaccacc"
```

**Complexity:** O(n * maxK) time where maxK is the maximum repetition count, O(n) space.

---

### Category C: Stack Simulation

---

#### LC 155 — Min Stack (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

**Problem:** Design a stack that supports `push`, `pop`, `top`, and `getMin` in O(1) time.

**Pattern:** Auxiliary min stack

**Key insight:** Maintain a second stack that tracks the minimum at each level. When you push a value, also push the new minimum (min of current value and previous minimum). When you pop, pop from both stacks.

```java
class MinStack {
    private Deque<Integer> stack;
    private Deque<Integer> minStack;
    
    public MinStack() {
        stack = new ArrayDeque<>();
        minStack = new ArrayDeque<>();
    }
    
    public void push(int val) {
        stack.push(val);
        // minStack top is always the current minimum
        int currentMin = minStack.isEmpty() ? val : Math.min(val, minStack.peek());
        minStack.push(currentMin);
    }
    
    public void pop() {
        stack.pop();
        minStack.pop();
    }
    
    public int top() {
        return stack.peek();
    }
    
    public int getMin() {
        return minStack.peek();
    }
}
```

**Dry Run:**

```
push(5):
  stack: [5]
  minStack: [5]  (min = min(5, nothing) = 5)

push(3):
  stack: [5, 3]
  minStack: [5, 3]  (min = min(3, 5) = 3)

push(7):
  stack: [5, 3, 7]
  minStack: [5, 3, 3]  (min = min(7, 3) = 3)

getMin() → minStack.peek() = 3 ✓

pop():
  stack: [5, 3]
  minStack: [5, 3]

getMin() → minStack.peek() = 3 ✓

pop():
  stack: [5]
  minStack: [5]

getMin() → minStack.peek() = 5 ✓
```

**Why this works:** The minStack mirrors the main stack. At every depth, `minStack.peek()` holds the minimum of all elements currently in the stack. Popping from both stacks simultaneously keeps them in sync.

**Alternative — single stack with pairs:**

```java
class MinStack {
    // Each entry: [value, currentMin]
    private Deque<int[]> stack = new ArrayDeque<>();
    
    public void push(int val) {
        int min = stack.isEmpty() ? val : Math.min(val, stack.peek()[1]);
        stack.push(new int[]{val, min});
    }
    
    public void pop() { stack.pop(); }
    public int top() { return stack.peek()[0]; }
    public int getMin() { return stack.peek()[1]; }
}
```

**Complexity:** O(1) for all operations, O(n) space.

---

#### LC 735 — Asteroid Collision (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given an array of integers representing asteroids in a row, each asteroid moves at the same speed. Positive = moving right, negative = moving left. Find the state after all collisions. When two asteroids meet, the smaller one explodes. If equal size, both explode.

**Pattern:** Stack simulation

**Rules:**
- Collision only happens when a right-moving asteroid (`+`) is followed by a left-moving asteroid (`-`)
- Two right-moving or two left-moving asteroids never collide

```java
class Solution {
    public int[] asteroidCollision(int[] asteroids) {
        Deque<Integer> stack = new ArrayDeque<>();
        
        for (int asteroid : asteroids) {
            boolean destroyed = false;
            
            // Collision: current is moving left (-), top of stack is moving right (+)
            while (!stack.isEmpty() && asteroid < 0 && stack.peek() > 0) {
                int top = stack.peek();
                if (top < -asteroid) {
                    stack.pop();  // top asteroid destroyed, continue
                } else if (top == -asteroid) {
                    stack.pop();  // both destroyed
                    destroyed = true;
                    break;
                } else {
                    destroyed = true;  // current asteroid destroyed
                    break;
                }
            }
            
            if (!destroyed) stack.push(asteroid);
        }
        
        // Convert to array (stack is in reverse order)
        int[] result = new int[stack.size()];
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = stack.pop();
        }
        return result;
    }
}
```

**Trace for `[5, 10, -5]`:**

```
asteroid=5:  stack empty → push 5.   stack: [5]
asteroid=10: top=5 > 0, asteroid=10 > 0 → no collision → push 10.   stack: [5, 10]
asteroid=-5: top=10 > 0, asteroid=-5 < 0 → collision!
             top(10) > -(-5)=5 → current destroyed. destroyed=true.
             
push nothing. stack: [5, 10]

result: [5, 10]
```

**Trace for `[10, 2, -5]`:**

```
asteroid=10: push 10.   stack: [10]
asteroid=2:  push 2.    stack: [10, 2]
asteroid=-5: top=2 > 0, asteroid=-5 < 0 → collision!
             top(2) < -(-5)=5 → pop 2. stack: [10]
             top=10 > 0, asteroid=-5 < 0 → collision!
             top(10) > 5 → current destroyed. destroyed=true.

push nothing. stack: [10]

result: [10]
```

**Complexity:** O(n) time, O(n) space.

---

#### LC 844 — Backspace String Compare (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given two strings `s` and `t`, return `true` if they are equal when both are typed into empty text editors. `#` means a backspace character.

**Pattern:** Stack simulation (or two-pointer from Topic 2)

**Stack approach:**

```java
class Solution {
    public boolean backspaceCompare(String s, String t) {
        return process(s).equals(process(t));
    }
    
    private String process(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '#') {
                if (!stack.isEmpty()) stack.pop();
            } else {
                stack.push(c);
            }
        }
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) sb.append(stack.pop());
        return sb.reverse().toString();
    }
}
```

**Two-pointer approach (O(1) space — see Topic 2):**

```java
class Solution {
    public boolean backspaceCompare(String s, String t) {
        int i = s.length() - 1, j = t.length() - 1;
        int skipS = 0, skipT = 0;
        
        while (i >= 0 || j >= 0) {
            while (i >= 0) {
                if (s.charAt(i) == '#') { skipS++; i--; }
                else if (skipS > 0) { skipS--; i--; }
                else break;
            }
            while (j >= 0) {
                if (t.charAt(j) == '#') { skipT++; j--; }
                else if (skipT > 0) { skipT--; j--; }
                else break;
            }
            if (i >= 0 && j >= 0 && s.charAt(i) != t.charAt(j)) return false;
            if ((i >= 0) != (j >= 0)) return false;
            i--; j--;
        }
        return true;
    }
}
```

The two-pointer approach is O(1) space and is the follow-up interviewers ask for. Know both.

**Complexity:** Stack: O(n) time, O(n) space. Two-pointer: O(n) time, O(1) space.

---

#### LC 946 — Validate Stack Sequences (Medium)

**Companies:** Amazon, Google, Microsoft

**Problem:** Given two integer arrays `pushed` and `popped`, return `true` if this could be the result of a sequence of push and pop operations on an initially empty stack.

**Pattern:** Stack simulation — simulate the push/pop sequence

```java
class Solution {
    public boolean validateStackSequences(int[] pushed, int[] popped) {
        Deque<Integer> stack = new ArrayDeque<>();
        int popIdx = 0;
        
        for (int val : pushed) {
            stack.push(val);
            // Pop as many as possible that match the popped sequence
            while (!stack.isEmpty() && stack.peek() == popped[popIdx]) {
                stack.pop();
                popIdx++;
            }
        }
        
        return stack.isEmpty();
    }
}
```

**Trace for `pushed=[1,2,3,4,5], popped=[4,5,3,2,1]`:**

```
push 1: stack=[1]. peek=1, popped[0]=4 → no match
push 2: stack=[1,2]. peek=2, popped[0]=4 → no match
push 3: stack=[1,2,3]. peek=3, popped[0]=4 → no match
push 4: stack=[1,2,3,4]. peek=4, popped[0]=4 → match! pop. popIdx=1
        peek=3, popped[1]=5 → no match
push 5: stack=[1,2,3,5]. peek=5, popped[1]=5 → match! pop. popIdx=2
        peek=3, popped[2]=3 → match! pop. popIdx=3
        peek=2, popped[3]=2 → match! pop. popIdx=4
        peek=1, popped[4]=1 → match! pop. popIdx=5
        stack empty

stack.isEmpty() = true → return true ✓
```

**Complexity:** O(n) time, O(n) space.

---

### Category D: Design Problems

---

#### LC 232 — Implement Queue using Stacks (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Implement a FIFO queue using only two stacks. Implement `push`, `pop`, `peek`, and `empty`.

**Pattern:** Two stacks — inbox and outbox

**Key insight:** Use two stacks. `inbox` receives all pushes. `outbox` serves all pops and peeks. When `outbox` is empty and you need to pop/peek, transfer everything from `inbox` to `outbox` (this reverses the order, making the oldest element on top).

```java
class MyQueue {
    private Deque<Integer> inbox;   // receives pushes
    private Deque<Integer> outbox;  // serves pops/peeks
    
    public MyQueue() {
        inbox = new ArrayDeque<>();
        outbox = new ArrayDeque<>();
    }
    
    public void push(int x) {
        inbox.push(x);
    }
    
    public int pop() {
        transfer();
        return outbox.pop();
    }
    
    public int peek() {
        transfer();
        return outbox.peek();
    }
    
    public boolean empty() {
        return inbox.isEmpty() && outbox.isEmpty();
    }
    
    private void transfer() {
        if (outbox.isEmpty()) {
            while (!inbox.isEmpty()) {
                outbox.push(inbox.pop());
            }
        }
    }
}
```

**Trace:**

```
push(1): inbox=[1], outbox=[]
push(2): inbox=[1,2], outbox=[]
push(3): inbox=[1,2,3], outbox=[]

peek():
  outbox empty → transfer: outbox=[1,2,3] (reversed), inbox=[]
  outbox.peek() = 1 ✓

pop():
  outbox not empty → outbox.pop() = 1. outbox=[2,3]

push(4): inbox=[4], outbox=[2,3]

pop():
  outbox not empty → outbox.pop() = 2. outbox=[3]
```

**Amortized O(1) analysis:** Each element is pushed to `inbox` once and transferred to `outbox` once. Total work across all operations is O(n), so amortized O(1) per operation. Worst case for a single `pop` is O(n) (when transfer happens), but this only happens after n pushes.

**Complexity:** O(1) amortized for all operations, O(n) space.

---

#### LC 225 — Implement Stack using Queues (Easy)

**Companies:** Amazon, Google, Microsoft

**Problem:** Implement a LIFO stack using only two queues. Implement `push`, `pop`, `top`, and `empty`.

**Pattern:** Single queue with rotation on push

**Key insight:** After pushing a new element, rotate the queue so the new element is at the front. This makes every `pop` and `top` O(1) at the cost of O(n) push.

```java
class MyStack {
    private Queue<Integer> queue;
    
    public MyStack() {
        queue = new ArrayDeque<>();
    }
    
    public void push(int x) {
        queue.offer(x);
        // Rotate: move all elements before x to the back
        for (int i = 0; i < queue.size() - 1; i++) {
            queue.offer(queue.poll());
        }
    }
    
    public int pop() {
        return queue.poll();
    }
    
    public int top() {
        return queue.peek();
    }
    
    public boolean empty() {
        return queue.isEmpty();
    }
}
```

**Trace:**

```
push(1): queue=[1]. rotate 0 times. queue=[1]
push(2): queue=[1,2]. rotate 1 time: move 1 to back. queue=[2,1]
push(3): queue=[2,1,3]. rotate 2 times: move 2,1 to back. queue=[3,2,1]

top() → queue.peek() = 3 ✓
pop() → queue.poll() = 3. queue=[2,1]
top() → queue.peek() = 2 ✓
```

**Complexity:** O(n) push, O(1) pop/top/empty, O(n) space.

---

#### LC 895 — Maximum Frequency Stack (Hard)

**Companies:** Amazon, Google, Meta, Bloomberg

**Problem:** Design a stack-like data structure that pushes and pops the most frequent element. If there is a tie, pop the element closest to the top of the stack.

**Pattern:** HashMap of frequency to stack + frequency tracking

**Key insight:** Maintain:
1. `freq`: a map from element to its current frequency
2. `group`: a map from frequency to a stack of elements with that frequency
3. `maxFreq`: the current maximum frequency

On `push(x)`:
- Increment `freq[x]`
- Add `x` to `group[freq[x]]`
- Update `maxFreq`

On `pop()`:
- Get the stack at `group[maxFreq]`, pop from it
- Decrement `freq[popped]`
- If `group[maxFreq]` is now empty, decrement `maxFreq`

```java
class FreqStack {
    private Map<Integer, Integer> freq;          // element → frequency
    private Map<Integer, Deque<Integer>> group;  // frequency → stack of elements
    private int maxFreq;
    
    public FreqStack() {
        freq = new HashMap<>();
        group = new HashMap<>();
        maxFreq = 0;
    }
    
    public void push(int val) {
        int f = freq.getOrDefault(val, 0) + 1;
        freq.put(val, f);
        maxFreq = Math.max(maxFreq, f);
        group.computeIfAbsent(f, k -> new ArrayDeque<>()).push(val);
    }
    
    public int pop() {
        Deque<Integer> stack = group.get(maxFreq);
        int val = stack.pop();
        freq.put(val, freq.get(val) - 1);
        if (stack.isEmpty()) {
            group.remove(maxFreq);
            maxFreq--;
        }
        return val;
    }
}
```

**Trace:**

```
push(5): freq={5:1}, group={1:[5]}, maxFreq=1
push(7): freq={5:1,7:1}, group={1:[5,7]}, maxFreq=1
push(5): freq={5:2,7:1}, group={1:[5,7],2:[5]}, maxFreq=2
push(7): freq={5:2,7:2}, group={1:[5,7],2:[5,7]}, maxFreq=2
push(4): freq={5:2,7:2,4:1}, group={1:[5,7,4],2:[5,7]}, maxFreq=2
push(5): freq={5:3,7:2,4:1}, group={1:[5,7,4],2:[5,7],3:[5]}, maxFreq=3

pop():
  maxFreq=3, group[3]=[5] → pop 5. freq[5]=2.
  group[3] empty → remove, maxFreq=2.
  return 5

pop():
  maxFreq=2, group[2]=[5,7] → pop 7. freq[7]=1.
  group[2]=[5] (not empty).
  return 7

pop():
  maxFreq=2, group[2]=[5] → pop 5. freq[5]=1.
  group[2] empty → remove, maxFreq=1.
  return 5

pop():
  maxFreq=1, group[1]=[5,7,4] → pop 4. freq[4]=0.
  group[1]=[5,7] (not empty).
  return 4
```

**Why the stack per frequency works:** When multiple elements have the same frequency, we want the most recently pushed one. A stack at each frequency level naturally gives us the most recently pushed element at that frequency.

**Complexity:** O(1) for both push and pop, O(n) space.

---

### Category E: Path / String Processing

---

#### LC 71 — Simplify Path (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given a Unix-style absolute path, simplify it. Handle `.` (current directory), `..` (parent directory), and multiple slashes.

**Pattern:** Stack for directory components

```java
class Solution {
    public String simplifyPath(String path) {
        Deque<String> stack = new ArrayDeque<>();
        
        for (String part : path.split("/")) {
            if (part.equals("..")) {
                if (!stack.isEmpty()) stack.pop();
            } else if (!part.isEmpty() && !part.equals(".")) {
                stack.push(part);
            }
            // Empty string (from "//") and "." are ignored
        }
        
        // Build result from bottom to top
        StringBuilder sb = new StringBuilder();
        // stack is LIFO, so iterate from bottom
        List<String> parts = new ArrayList<>(stack);
        Collections.reverse(parts);
        for (String part : parts) {
            sb.append("/").append(part);
        }
        
        return sb.length() == 0 ? "/" : sb.toString();
    }
}
```

**Cleaner version using Deque as a list:**

```java
class Solution {
    public String simplifyPath(String path) {
        Deque<String> stack = new ArrayDeque<>();
        
        for (String part : path.split("/")) {
            if (part.equals("..")) {
                if (!stack.isEmpty()) stack.pollLast();
            } else if (!part.isEmpty() && !part.equals(".")) {
                stack.offerLast(part);
            }
        }
        
        StringBuilder sb = new StringBuilder();
        for (String part : stack) {  // iterates front to back
            sb.append("/").append(part);
        }
        
        return sb.length() == 0 ? "/" : sb.toString();
    }
}
```

**Trace for `"/home//foo/"`:**

```
split by "/": ["", "home", "", "foo", ""]

"" → skip (empty)
"home" → push. stack: [home]
"" → skip
"foo" → push. stack: [home, foo]
"" → skip

result: "/home/foo"
```

**Trace for `"/a/./b/../../c/"`:**

```
split: ["", "a", ".", "b", "..", "..", "c", ""]

"" → skip
"a" → push. stack: [a]
"." → skip
"b" → push. stack: [a, b]
".." → pop. stack: [a]
".." → pop. stack: []
"c" → push. stack: [c]
"" → skip

result: "/c"
```

**Complexity:** O(n) time, O(n) space.

---

#### LC 856 — Score of Parentheses (Medium)

**Companies:** Amazon, Google, Meta

**Problem:** Given a balanced parentheses string, compute its score. `()` has score 1. `AB` has score `A + B`. `(A)` has score `2 * A`.

**Pattern:** Stack tracking accumulated scores

```java
class Solution {
    public int scoreOfParentheses(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);  // base score
        
        for (char c : s.toCharArray()) {
            if (c == '(') {
                stack.push(0);  // new context
            } else {
                int v = stack.pop();
                int w = stack.pop();
                stack.push(w + Math.max(2 * v, 1));
                // If v=0, this was "()" → score 1
                // If v>0, this was "(A)" → score 2*v
            }
        }
        
        return stack.pop();
    }
}
```

**Trace for `"(()(()))"`:**

```
stack: [0]
'(' → push 0. stack: [0, 0]
'(' → push 0. stack: [0, 0, 0]
')' → v=0, w=0. push(0 + max(0,1)) = push(1). stack: [0, 1]
'(' → push 0. stack: [0, 1, 0]
'(' → push 0. stack: [0, 1, 0, 0]
')' → v=0, w=0. push(0 + max(0,1)) = push(1). stack: [0, 1, 1]
')' → v=1, w=0. push(0 + max(2,1)) = push(2). stack: [0, 1, 2]
')' → v=1+2=3, w=0. push(0 + max(6,1)) = push(6). stack: [6]

return 6
```

**Complexity:** O(n) time, O(n) space.

---

## Common Mistakes and Edge Cases

### Mistake 1: Using `java.util.Stack`

```java
// WRONG
Stack<Integer> stack = new Stack<>();

// RIGHT
Deque<Integer> stack = new ArrayDeque<>();
```

`Stack` extends `Vector`. Every operation is synchronized. In single-threaded code (all interview problems), this is pure overhead. `ArrayDeque` is 2-3x faster in practice.

### Mistake 2: Not Checking Empty Before Pop/Peek

```java
// WRONG — throws NoSuchElementException
char top = stack.pop();

// RIGHT
if (!stack.isEmpty()) {
    char top = stack.pop();
}
// OR use peek() which returns null on empty ArrayDeque
```

Always check `isEmpty()` before `pop()`. In bracket matching, an empty stack when you encounter a closing bracket means the string is invalid.

### Mistake 3: Not Matching Bracket Types

```java
// WRONG — only checks if stack is non-empty
if (!stack.isEmpty()) stack.pop();

// RIGHT — verify the type matches
char top = stack.pop();
if (c == ')' && top != '(') return false;
if (c == ']' && top != '[') return false;
if (c == '}' && top != '{') return false;
```

`"([)]"` has balanced counts but mismatched types. Always verify the specific bracket type.

### Mistake 4: Operator Precedence in Expression Evaluation

```java
// WRONG — processes left to right, ignores precedence
// "3+2*2" would give (3+2)*2 = 10 instead of 3+(2*2) = 7

// RIGHT — push +/- as signed numbers, apply */÷ immediately
if (op == '+') stack.push(num);
else if (op == '-') stack.push(-num);
else if (op == '*') stack.push(stack.pop() * num);
else if (op == '/') stack.push(stack.pop() / num);
```

### Mistake 5: Pop Order in RPN Evaluation

```java
// WRONG — operands in wrong order for subtraction/division
int a = stack.pop();
int b = stack.pop();
stack.push(a - b);  // wrong: should be b - a

// RIGHT
int b = stack.pop();  // second operand (pushed last)
int a = stack.pop();  // first operand (pushed first)
stack.push(a - b);    // correct order
```

### Mistake 6: Not Handling Multi-Digit Numbers

```java
// WRONG — only handles single digits
int num = c - '0';

// RIGHT — accumulate digits
if (Character.isDigit(c)) {
    num = num * 10 + (c - '0');
}
```

### Edge Cases to Always Test

| Case | Example | Expected behavior |
|------|---------|------------------|
| Empty string | `""` | Valid parentheses: true |
| Single bracket | `"("` | Invalid: false |
| All same type | `"((("` | Invalid: false |
| Interleaved | `"([)]"` | Invalid: false |
| Deeply nested | `"(((...)))"` | Valid: true |
| Negative numbers | `"-3+2"` | Handle sign correctly |
| Multi-digit | `"12+34"` | Accumulate digits |
| Spaces in expression | `"3 + 2 * 2"` | Skip spaces |
| Path with `..` at root | `"/../"` | Return `"/"` |
| Consecutive slashes | `"//home"` | Return `"/home"` |

---

## Pattern Comparison

### Stack vs Recursion

Every recursive algorithm has an equivalent iterative version using an explicit stack. The call stack IS a stack — each recursive call pushes a frame, each return pops one.

```java
// Recursive DFS
void dfs(TreeNode node) {
    if (node == null) return;
    process(node);
    dfs(node.left);
    dfs(node.right);
}

// Iterative DFS with explicit stack
void dfs(TreeNode root) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);
    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        if (node == null) continue;
        process(node);
        stack.push(node.right);  // push right first (processed second)
        stack.push(node.left);   // push left second (processed first)
    }
}
```

When to prefer iterative: deep recursion risks stack overflow. Iterative is safer for large inputs. Interviewers sometimes ask for the iterative version as a follow-up.

### Stack vs Monotonic Stack (Topic 7)

| Property | Regular Stack | Monotonic Stack |
|----------|--------------|-----------------|
| Order invariant | None | Sorted (increasing or decreasing) |
| Pop condition | Problem-specific | Violates sorted order |
| Use case | Matching, simulation, history | Next greater/smaller element, area problems |
| Example | LC 20, LC 155 | LC 84, LC 496, LC 739 |

A monotonic stack is a regular stack with a disciplined push operation. The discipline (popping elements that violate sorted order) is what enables O(n) solutions to "next greater element" problems.

When you see "next greater", "next smaller", "largest rectangle", "trapping rain water" → monotonic stack (Topic 7).

When you see "matching brackets", "evaluate expression", "undo history" → regular stack (this document).

### Queue vs Priority Queue/Heap (Topic 12)

| Property | Queue (ArrayDeque) | Priority Queue |
|----------|-------------------|----------------|
| Order | FIFO (insertion order) | Priority order (min or max) |
| Poll | O(1) | O(log n) |
| Use case | BFS, level-order, FIFO processing | Dijkstra, top-k, scheduling |
| Java class | `ArrayDeque` | `PriorityQueue` |

Use a regular queue when you need to process elements in the order they arrived. Use a priority queue when you need to process the most important element first, regardless of arrival order.

BFS uses a regular queue (process nodes level by level, in discovery order). Dijkstra uses a priority queue (process the closest unvisited node first).

### Stack vs Deque

A deque is a generalization of both stack and queue. Use `ArrayDeque` for everything. When you need stack behavior, use `push`/`pop`/`peek`. When you need queue behavior, use `offer`/`poll`/`peek`. When you need both ends (sliding window maximum, palindrome checking), use `offerFirst`/`offerLast`/`pollFirst`/`pollLast`.

---

## Quick Reference Cheat Sheet

### Java ArrayDeque API

```java
Deque<Integer> d = new ArrayDeque<>();

// AS A STACK (top = front)
d.push(x);          // addFirst(x)
d.pop();            // removeFirst() — throws if empty
d.peek();           // peekFirst() — returns null if empty

// AS A QUEUE (front = oldest, back = newest)
d.offer(x);         // addLast(x)
d.poll();           // removeFirst() — returns null if empty
d.peek();           // peekFirst() — returns null if empty

// DEQUE OPERATIONS
d.offerFirst(x);    // add to front
d.offerLast(x);     // add to back
d.pollFirst();      // remove from front
d.pollLast();       // remove from back
d.peekFirst();      // view front
d.peekLast();       // view back

// UTILITY
d.isEmpty();
d.size();
```

### Template Selector

```
Problem involves...
├── Matching brackets/tags → Template 1 (Bracket Matching)
├── Evaluate expression with operators → Template 2 (Expression Eval)
│   ├── No parentheses → LC 227 approach (single stack)
│   └── With parentheses → LC 224 approach (sign stack)
├── Simulate process with undo/collision → Template 3 (Simulation)
├── Level-by-level processing → Template 4 (Queue BFS)
└── Design problem
    ├── Queue using stacks → LC 232 (two stacks, lazy transfer)
    └── Stack using queues → LC 225 (single queue, rotate on push)
```

### Complexity Summary

| Problem | Time | Space | Key Technique |
|---------|------|-------|---------------|
| LC 20 Valid Parentheses | O(n) | O(n) | Push open, pop on close |
| LC 1249 Min Remove Valid | O(n) | O(n) | Track unmatched indices |
| LC 32 Longest Valid | O(n) | O(n) | Stack of indices with boundary |
| LC 150 Eval RPN | O(n) | O(n) | Single operand stack |
| LC 227 Basic Calc II | O(n) | O(n) | Deferred +/-, immediate */÷ |
| LC 224 Basic Calc | O(n) | O(n) | Sign context stack |
| LC 394 Decode String | O(n·k) | O(n) | Two stacks (count + string) |
| LC 155 Min Stack | O(1) all | O(n) | Parallel min stack |
| LC 735 Asteroid Collision | O(n) | O(n) | Simulate with stack |
| LC 844 Backspace Compare | O(n) | O(1) | Two-pointer (O(n) stack also works) |
| LC 946 Validate Sequences | O(n) | O(n) | Simulate push/pop |
| LC 232 Queue via Stacks | O(1) amort | O(n) | Lazy transfer between stacks |
| LC 225 Stack via Queues | O(n) push | O(n) | Rotate queue on push |
| LC 895 Max Freq Stack | O(1) all | O(n) | freq map + stack per frequency |
| LC 71 Simplify Path | O(n) | O(n) | Stack of path components |
| LC 856 Score Parens | O(n) | O(n) | Score accumulation stack |

### Common Patterns at a Glance

```
Bracket matching:
  push open → pop on close → verify type → check empty at end

Expression evaluation (no parens):
  op='+' → push(+num)
  op='-' → push(-num)
  op='*' → push(pop()*num)
  op='/' → push(pop()/num)
  sum stack at end

Min stack:
  push(val) → also push min(val, minStack.peek()) to minStack
  pop() → pop both stacks
  getMin() → minStack.peek()

Queue via two stacks:
  push → always to inbox
  pop/peek → transfer inbox→outbox if outbox empty, then outbox.pop/peek
```

---

## Practice Roadmap

### Week 1 — Easy Problems (15 minutes each)

Build the foundation. These problems are straightforward applications of the templates. Get them to the point where you can write the solution without thinking.

| Problem | Difficulty | Pattern | Target Time |
|---------|-----------|---------|-------------|
| LC 20 — Valid Parentheses | Easy | Bracket matching | 10 min |
| LC 232 — Implement Queue using Stacks | Easy | Design | 15 min |
| LC 225 — Implement Stack using Queues | Easy | Design | 15 min |
| LC 844 — Backspace String Compare | Easy | Stack simulation | 15 min |

**Goal:** Internalize the bracket matching template and the two-stack queue design. These appear in interviews as warmups or as sub-problems inside harder questions.

---

### Week 2 — Medium Problems (25 minutes each)

Apply the templates to more complex scenarios. These are the most common interview problems in this category.

| Problem | Difficulty | Pattern | Target Time |
|---------|-----------|---------|-------------|
| LC 155 — Min Stack | Medium | Auxiliary min stack | 20 min |
| LC 150 — Evaluate Reverse Polish Notation | Medium | Single operand stack | 20 min |
| LC 394 — Decode String | Medium | Two stacks | 25 min |
| LC 735 — Asteroid Collision | Medium | Stack simulation | 25 min |
| LC 71 — Simplify Path | Medium | Stack + string split | 20 min |
| LC 946 — Validate Stack Sequences | Medium | Stack simulation | 20 min |
| LC 1249 — Min Remove to Make Valid | Medium | Index tracking | 25 min |

**Goal:** Recognize which template applies within the first 2 minutes of reading the problem. Practice explaining your approach before coding.

---

### Week 3 — Hard Problems (35 minutes each)

These require combining multiple techniques or handling complex edge cases. Expect these at senior-level interviews or as the hard problem in a 45-minute session.

| Problem | Difficulty | Pattern | Target Time |
|---------|-----------|---------|-------------|
| LC 224 — Basic Calculator | Hard | Sign context stack | 35 min |
| LC 227 — Basic Calculator II | Medium | Deferred operators | 25 min |
| LC 32 — Longest Valid Parentheses | Hard | Index stack | 30 min |
| LC 895 — Maximum Frequency Stack | Hard | Freq map + stacks | 35 min |

**Goal:** Handle the edge cases (negative numbers, spaces, nested parentheses) without bugs. These problems have many failure modes — practice on paper first.

---

### Total: ~15 problems over 3 weeks

After completing this roadmap, you'll have covered every major stack/queue pattern that appears in FAANG interviews. The remaining problems in this category (LC 772 Basic Calculator III, LC 856 Score of Parentheses) are variations on patterns you've already mastered.

**Connection to upcoming topics:**
- Topic 11 (Trees): The queue BFS template (Template 4) is the foundation for all tree level-order problems
- Topic 14 (Graphs): The same BFS template applies to graph traversal, shortest path in unweighted graphs
- Topic 7 (Monotonic Stack): Already covered — revisit if you need a refresher on next-greater-element problems

---

*Document 8 of 20 — FAANG DSA Prep Series*

*Previous: [Topic 7: Monotonic Stack & Queue](07-monotonic-stack-and-queue.md)*
