package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class TernaryTest {

  private void doTest(String source, String expectedString, Object expectedObject) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    Expr expression = parser.parse();
    assertEquals(expectedObject, new Interpreter().evaluate(expression));
    assertEquals(expectedString, new AstPrinter().print(expression));
  }

  @Test
  void simpleFalse() {
    doTest("1 == 3 ? 4 : 5",
        "(?: (== 1.0 3.0) 4.0 5.0)",
        5.0);
  }

  @Test
  void simpleTrue() {
    doTest("1 != 3 ? 4 : 5",
        "(?: (!= 1.0 3.0) 4.0 5.0)",
        4.0);
  }

  @Test
  void simpleString() {
    doTest("false ? \"a\" : \"b\"",
        "(?: false a b)",
        "b");
  }

  @Test
  void associativity1() {
    doTest("false ? \"a\" : true ? \"b\" : \"c\"",
        "(?: false a (?: true b c))",
        "b");
  }

  @Test
  void associativity2() {
    doTest("true ? true ? \"a\" : \"b\" : \"c\"",
        "(?: true (?: true a b) c)",
        "a");
  }

  @Test
  void associativity3() {
    doTest("false ? true ? \"a\" : \"b\" : true ? \"c\" : \"d\"",
        "(?: false (?: true a b) (?: true c d))",
        "c");
  }

  @Test
  void precedence1() {
    doTest("false==true ? true : \"b\" == \"c\"",
        "(?: (== false true) true (== b c))",
        false);
  }
}
