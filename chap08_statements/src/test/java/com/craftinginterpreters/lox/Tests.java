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
    if (sb.length() > 0)
      sb.append(":");
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
    if (object == null)
      return "nil";
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

  private void outputTest(String source, String expectedOutput) {
    // System.out.println("source: " + source);
    Lox.hadError = Lox.hadRuntimeError = false;
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();
    TestInterpreter interpreter = new TestInterpreter();
    if (Lox.hadError || Lox.hadRuntimeError) {
      interpreter.append("Parse error");
    } else {
      interpreter.interpret(statements);
      if (Lox.hadError || Lox.hadRuntimeError) {
        interpreter.append("Runtime error");
        for (var stmt : statements)
          System.out.println(new AstPrinter().print(stmt));
      }
    }
    assertEquals(expectedOutput, interpreter.toString());
  }

  @Test
  void initializedImmediately() {
    outputTest("""
        // No initializers.
        var a = "assigned";
        var b;

        print a; // OK, was assigned first.
        """,
        "assigned");
  }

  @Test
  void initializedLater() {
    outputTest("""
        // No initializers.
        var a;
        var b;

        a = "assigned";
        print a; // OK, was assigned first.
        """,
        "assigned");
  }

  @Test
  void initializedNil() {
    outputTest("""
        // No initializers.
        var a;
        var b;

        a = nil;
        print a; // OK, was assigned first.
        """,
        "nil");
  }

  @Test
  void uninitializedSimple() {
    outputTest("""
        var a;
        print a; // Not ok, was not assigned.
        """,
        "Runtime error");
  }

  @Test
  void uninitializedUseAfterPrint() {
    outputTest("""
        // No initializers.
        var a;
        var b;

        a = "assigned";
        print a; // OK, was assigned first.
        print b; // Not ok, was not assigned.
        """,
        "assigned:Runtime error");
  }
}
