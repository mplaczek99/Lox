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
          interpreter.append("Runtime error");
          for (var stmt : statements) System.out.println(new AstPrinter().print(stmt));
        }
      }
    }
    assertEquals(expectedOutput, interpreter.toString());
  }

  @Test
  void classMethods1() {
    outputTest("""
      class Math {
        class square(n) {
          return n * n;
        }
      }
      class C {
        init (x) {
          this.x = x;
        }
        inc () {
          this.x = this.x + 1;
          return this.x;
        }
        class double (z) {
          return z + z;
        }
        get (y) {
          return C.double(this.inc() + y);
        }
      }
      
      print Math.square(3); // Prints "9".
      print C(10).get(2);   // Prints "26".    
      """, 
      "9:26");
  }  
  @Test
  void classMethods2() {
    outputTest("""
      class X {
        class addOne(n) {
          return n + 1;
        }
      }
      print X.addOne(3); // Prints "4".
      """, 
      "4");
  }  
  @Test
  void classGetters1() {
    outputTest("""  
      class Circle {
        init(radius) {
          this.radius = radius;
        }
      
        area {
          return 3.141592653 * this.radius * this.radius;
        }
      }
      var circle = Circle(4);
      print circle.area;        // Prints "50.265482448"
      """,
      "50.265482448");
  }
          
  @Test
  void classGetters2() {
    outputTest("""
      class C {
        init(x) {
          this.x = x;
        }
        inc () {
          this.x = this.x + 1;
          return this;
        }
        double {
          return this.x + this.x;
        }
      }
      print C(10).inc().double; // Prints "22"
      """,
      "22");
  }
      
  @Test
  void classGetters3() {
    outputTest("""
      class D {
        init() {
          this.x = 10;
        }
        x {
          return 20;
        }
      }
      print D().x; // Prints "10" -- fields take precedence over getters (just like fields take precedence over methods)
      """, 
      "10");
  }
}
