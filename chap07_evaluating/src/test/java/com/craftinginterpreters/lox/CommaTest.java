package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class CommaTest {

  private void doTest(String source, String expectedString, Object expectedObject) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    Expr expression = parser.parse();
    assertEquals(expectedObject, new Interpreter().evaluate(expression));
    assertEquals(expectedString, new AstPrinter().print(expression));
  }

  @Test
  void simpleBoolean() {
    doTest("0!=1,2==3",
        "(, (!= 0.0 1.0) (== 2.0 3.0))",
        false);
  }

  @Test
  void simpleMixed() {
    doTest("\"a\"==\"a\",5+3",
        "(, (== a a) (+ 5.0 3.0))",
        8.0);
  }
}
