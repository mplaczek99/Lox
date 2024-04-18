package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class ScannerTest {
    private void run(String source, int expectedSize, String expected) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        assertEquals(expectedSize, tokens.size());
        assertEquals(expected, tokens.toString());
    }    
    @Test
    void commentInComment() {        
        run("""
              4.1 /*//*/ + 3.dog()
            """,
            8,
            "[NUMBER 4.1 4.1, PLUS + null, NUMBER 3 3.0, DOT . null, IDENTIFIER dog null, LEFT_PAREN ( null, RIGHT_PAREN ) null, EOF  null]");
    }
    @Test
    void twoSingleLineComments() {
        run("""
              /* this is a comment */
              hi mom
              // this is a comment
              /* this is a comment */
            """,
            3,
            "[IDENTIFIER hi null, IDENTIFIER mom null, EOF  null]");
    }
    @Test
    void singleLineComment() {
        run("""
              hi mom
              // this is a comment
              /* this is a comment */
              1 + 3;
            """,
            7,
            "[IDENTIFIER hi null, IDENTIFIER mom null, NUMBER 1 1.0, PLUS + null, NUMBER 3 3.0, SEMICOLON ; null, EOF  null]");
    }
    @Test
    void twoCommentsOnOneLine() {
        run("""
              /* this is a comment */ 1+ 3 /*another comment*/
            """,
            4,
            "[NUMBER 1 1.0, PLUS + null, NUMBER 3 3.0, EOF  null]");
    }
    @Test
    void twoCommentsOnOneLineAdjacent() {
        run("""
              1+/* this is a comment *//*another comment*/3
            """,
            4,
            "[NUMBER 1 1.0, PLUS + null, NUMBER 3 3.0, EOF  null]");
    }
    @Test
    void nestedComments() {
        run("""
              1 + 
              /* this is a comment 
                /* another comment 
                 * /* yet another comment */
                */
              */
              3
            """,
            4,
            "[NUMBER 1 1.0, PLUS + null, NUMBER 3 3.0, EOF  null]");
    }   
}
