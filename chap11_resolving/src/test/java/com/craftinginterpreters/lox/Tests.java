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
  void anonymous1() {
    outputTest("var f = fun (x) { return x + 1;}; print f(1);", "2");
  }
  @Test
  void anonymous2() {
    outputTest("print (fun (x) { return x + 1;}) (1);", "2");
  }
  @Test
  void anonymous3() {
    outputTest("print fun (x) { return x + 1;} (1);", "2");
  }
  @Test
  void anonymous4() {
    outputTest("print fun (x) { return x + 1;};", "<fn>");
  }
  @Test
  void anonymous5() {
    outputTest("fun (x) { print x; }(1);", "1");
  }  
  @Test
  void anonymous6() {
    outputTest("fun (x) { print x; };", "");
  }    
  @Test
  void break1() {
    outputTest("var j=0; while (j<30) { print 0; break; j=j+1;}", "0");
  }
  @Test
  void break2() {
    outputTest("for (var i=0; i<3; i=i+1) { var j=0; while (j<30) { print i; break; j=j+1;} }", "0:1:2");
  }  
  @Test
  void break3() {
    outputTest("""
      for (var i = 1; i < 5; i = i + 1) {
        for (var j = 10; j < 50; j = j + 10) {
          print(i * j);
          if (j == 30) {
            break;
          }
        }
        if (i == 3) {
          break;
        }
      }      
      """, 
      "10:20:30:20:40:60:30:60:90");
  }  
  @Test
  void break4() {
    outputTest("""
      var i = 1;
      while (i < 5) {
        var j = 10;
        while (j < 50) {
          print(i * j);
          if (j == 30) {
            break;
          }
          j = j + 10;
        }
        if (i == 3) {
          break;
        }
        i = i + 1;
      }      
      """, 
      "10:20:30:20:40:60:30:60:90");
  }  
  @Test
  void break5() {
    outputTest("""
      for (var i = 0; i < 10; i = i + 1) {
        print(i);
        for (var j = 0; j < 10; j = j + 1) {
          print("a");
          var k = 0;
          while (k < 10) {
            k = k + 1;
            print ("b");
            if (k == 2) {
              break;
            }
          }
          if (j == 1) {
            break;
          }
        }
        if (i == 2) {
          break;
        }
      }      
      """, 
      "0:a:b:b:a:b:b:1:a:b:b:a:b:b:2:a:b:b:a:b:b");
  }  
  @Test
  void breakResolve1() {
    outputTest("break;", 
      "Resolve error");
  }  
  @Test
  void breakResolve2() {
    outputTest("fun f() { if (true) {break;} }", 
      "Resolve error");
  }    
}
