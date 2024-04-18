package com.craftinginterpreters.lox;

/**
 * Modified to complete HW1B
 */
class AstPrinter implements Expr.Visitor<String> {
  String print(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    // In RPN, we visit the left operand, then the right, then the operator.
    return formatRpn(expr.left.accept(this), expr.right.accept(this), expr.operator.lexeme);
  }

  @Override
  public String visitGroupingExpr(Expr.Grouping expr) {
    // Grouping does not change the order in RPN, but we still need to process the
    // expression inside
    return expr.expression.accept(this);
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    if (expr.value == null)
      return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    // In RPN, unary operators follow the operand.
    return formatRpn(expr.right.accept(this), expr.operator.lexeme);
  }

  // Using this method instead of the other parenthesize() method
  private String formatRpn(String... parts) {
    // This method joins parts of an RPN expression with a space.
    return String.join(" ", parts);
  }

  /*
   * private String parenthesize(String name, Expr... exprs) {
   * StringBuilder builder = new StringBuilder();
   * 
   * builder.append("(").append(name);
   * for (Expr expr : exprs) {
   * builder.append(" ");
   * builder.append(expr.accept(this));
   * }
   * builder.append(")");
   * 
   * return builder.toString();
   * }
   */
}
