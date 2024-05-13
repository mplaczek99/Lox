package com.craftinginterpreters.lox;

import java.util.HashSet;
import java.util.List;

class LoxFunction implements LoxCallable {
  private final Stmt.Function declaration;
  private final Expr.AnonymousFunction anonymousDeclaration;
  private final Environment closure;

  // Constructor for named functions
  LoxFunction(Stmt.Function declaration, Environment closure) {
    this.closure = closure;
    this.declaration = declaration;
    this.anonymousDeclaration = null;
  }

  // Constructor for anonymous functions
  LoxFunction(Expr.AnonymousFunction anonymousDeclaration, Environment closure) {
    this.closure = closure;
    this.declaration = null;
    this.anonymousDeclaration = anonymousDeclaration;
  }

  @Override
  public String toString() {
    if (declaration != null) {
      return "<fn " + declaration.name.lexeme + ">"; // print name only
    } else {
      return "<anonymous fn>";
    }
  }

  @Override
  public int arity() {
    if (declaration != null) {
      return declaration.params.size();
    } else {
      return anonymousDeclaration.params.size();
    }
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment environment = new Environment(closure); // this is static scope
    List<Token> params;
    List<Stmt> body;

    if (declaration != null) {
      params = declaration.params;
      body = declaration.body;
    } else {
      params = anonymousDeclaration.params;
      body = anonymousDeclaration.body;
    }

    for (int i = 0; i < params.size(); i++) {
      environment.define(params.get(i).lexeme, arguments.get(i));
    }

    try {
      interpreter.executeBlock(body, environment);
    } catch (Return returnValue) {
      return returnValue.value;
    }
    return null;
  }

  private HashSet<LoxFunction> functionSet = new HashSet<>();
  private HashSet<Environment> closureSet = new HashSet<>();

  private String printFunctionWithEnvironment() {
    String name = (declaration != null) ? declaration.name.lexeme : "anonymous";
    String result = "[fn " + name + " " + closure.hashCode() % 100; 
    if (functionSet.contains(this)) {
      return result + "]";
    } else {
      functionSet.add(this);
      if (closureSet.contains(closure)) {
        return result + "]";
      } else {
        closureSet.add(closure);
        return result + " " + closure.values.toString() 
          + (closure.enclosing == null ? "" : " -> " + closure.enclosing.hashCode() % 100)
          + "]";
      }
    }
  } 
}

