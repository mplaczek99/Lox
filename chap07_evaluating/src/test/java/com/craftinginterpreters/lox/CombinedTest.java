package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class CombinedTest {

  private void doTest(String source, String expectedString, Object expectedObject) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    Expr expression = parser.parse();
    assertEquals(expectedObject, new Interpreter().evaluate(expression));
    assertEquals(expectedString, new AstPrinter().print(expression));
  }

  @Test
  void precedence1() {
    doTest("true ? 2 : 3 , false ? 5 : 6",
        "(, (?: true 2.0 3.0) (?: false 5.0 6.0))",
        6.0);
  }
}
