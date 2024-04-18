package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

// In test output, lines are separated by ":" instead of "\n"
class TestInterpreter extends Interpreter {
  private StringBuffer sb = new StringBuffer();
  public String toString() {
    return sb.toString();
  }
  void append(String s) {
    if (sb.length() > 0) sb.append(":");
    sb.append(s);
  }
  public Void visitPrintStmt(Stmt.Print stmt) {
    Object value = evaluate(stmt.expression);
    append(stringify(value));
    return null;
  }
  // copied because private in Interpreter
  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }  
  // copied because private in Interpreter
  private String stringify(Object object) {
    if (object == null) return "nil";
    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }
    return object.toString();
  }  
}

class Tests {
  
  private void outputTest (String source, String expectedOutput) {
    //System.out.println("source: " + source);
    Lox.hadError = Lox.hadRuntimeError = false;
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();
    TestInterpreter interpreter = new TestInterpreter();
    if (Lox.hadError || Lox.hadRuntimeError) {
      interpreter.append("Parse error");
    } else {
      Resolver resolver = new Resolver(interpreter);
      resolver.resolve(statements);        
      if (Lox.hadError || Lox.hadRuntimeError) {
        interpreter.append("Resolve error");
      } else {
        interpreter.interpret(statements);
        if (Lox.hadError || Lox.hadRuntimeError) {
          interpreter.append("Interpreter error");
          for (var stmt : statements) System.out.println(new AstPrinter().print(stmt));
        }
      }
    }
    assertEquals(expectedOutput, interpreter.toString());
  }
  static String OABC = """
      class O { }
      class A < O {
        m() {
          print "[A]";
          inner(); 
          print "[/A]";
        }
      }
      class B < A  {
        m() {
          inner();
          print "  [B]";
          inner();
          print "  [/B]";
          inner();
        }
      }
      class C < B {
        m() {
          print "    [C /]";
          inner();
        }
      }
      class E < A {
        m() {
          print "  [E /]";
        }
      }  
      """;
  @Test
  void inner1() {
    outputTest(OABC + """
      A().m();      
      """, 
      "[A]:[/A]");
  }
  @Test
  void inner2() {
    outputTest(OABC + """
      B().m();      
      """, 
      "[A]:  [B]:  [/B]:[/A]");
  }
  @Test
  void inner3() {
    outputTest(OABC + """
      C().m();      
      """, 
      "[A]:    [C /]:  [B]:    [C /]:  [/B]:    [C /]:[/A]");
  }
  @Test
  void inner4() {
    outputTest(OABC + """
      E().m();      
      """, 
      "[A]:  [E /]:[/A]");
  }
  @Test
  void parameters() {
    outputTest("""
      class A {
        m(x) {
          print "[A " + x + "]";
          inner("J"); 
          print "[/A " + x + "]";
        }
      }
      class B < A  {
        m(y) {
          print "  [B " + y + "]";
          inner("K");
          print "  [/B " + y + "]";
        }
      }
      class C < B {
        m(y) {
          print "    [C " + y + "/]";
        }
      }
      C().m("I");
      """,
      "[A I]:  [B J]:    [C K/]:  [/B J]:[/A I]");
  }
  @Test
  void twoObjects () {
    outputTest("""
      fun toString (x) {
        if (x == 0) { return "0"; }
        if (x == 1) { return "1"; }
        if (x == 2) { return "2"; }
        if (x == 3) { return "3"; }
        if (x == 4) { return "4"; }
        if (x == 5) { return "5"; }
        return "Too big";
      }
      class O {
        init(s) {
          this.s = s;
        }
        setOther(other) {
          this.other = other;
        }
      }
      class A < O {
        m(x) {
          if (x > 0) {
            print "[A " + this.s + toString(x) + "]";
            inner(x); 
            print "[/A " + this.s + toString(x) + "]";
          }
        }
      }
      class B < A  {
        m(x) {
          this.other.m(x-1);
          print "  [B " + this.s + toString(x) + "]";
        }
      }
      class E < A  {
        m(x) {
          this.other.m(x-1);
          print "  [E " + this.s + toString(x) + "]";
        }
      }
      
      var b = B("b");
      var e = E("e");
      b.setOther(e);
      e.setOther(b);
      
      b.m(4);        
      """,
      "[A b4]:[A e3]:[A b2]:[A e1]:  [E e1]:[/A e1]:  [B b2]:[/A b2]:  [E e3]:[/A e3]:  [B b4]:[/A b4]");
  }
}
