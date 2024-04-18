package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class TreeWalkTest {
    private void run (String source, String expected) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();
        assertEquals(expected, new AstPrinter().print(expression));
    }
    @Test
    void twoOpExpression() {
        run("""
        1 + 2 * 3;
        """,
        "1.0 2.0 3.0 * +");
    }
    @Test
    void fourOpExpression() {
        run("""
        1 + 2 * 3 - 4 / 5;
        """,
        "1.0 2.0 3.0 * + 4.0 5.0 / -");
    }
    @Test
    void parensExpression() {
        run("""
        (1 + 2) * (3 - 4) / 5;
        """,
        "1.0 2.0 + group 3.0 4.0 - group * 5.0 /");
    }
    @Test
    void allTheOperatorsExpression() {
        run("""
        1 + 2 * 3 - 4 / 5 == 6 >= 7 <= 8 > 9 < 10 != 11;
        """,
        "1.0 2.0 3.0 * + 4.0 5.0 / - 6.0 7.0 >= 8.0 <= 9.0 > 10.0 < == 11.0 !=");
    }
}
