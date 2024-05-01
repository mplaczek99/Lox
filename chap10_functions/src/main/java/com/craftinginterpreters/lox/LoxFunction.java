package com.craftinginterpreters.lox;

import java.util.HashSet;
import java.util.List;

class LoxFunction implements LoxCallable {
  private final Stmt.Function declaration;
  private final Environment closure;

  LoxFunction(Stmt.Function declaration, Environment closure) {
    this.closure = closure;
    this.declaration = declaration;
  }
  @Override
  public String toString() {
    return "<fn " + declaration.name.lexeme + ">";             // print name only
    //return "<fn " + new AstPrinter().print(declaration) + ">"; // print code
    //return printFunctionWithEnvironment();                     // print closure
  }
  @Override
  public int arity() {
    return declaration.params.size();
  }
  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    //Environment environment = new Environment(interpreter.environment);  // this is dynamic scope
    Environment environment = new Environment(closure);  // this is static scope
    for (int i = 0; i < declaration.params.size(); i++) {
      environment.define(declaration.params.get(i).lexeme,
          arguments.get(i));
    }

    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      return returnValue.value;
    }
    return null;
  }


  private HashSet<LoxFunction> functionSet = new HashSet<LoxFunction>();
  private HashSet<Environment> closureSet = new HashSet<Environment>();
  private String printFunctionWithEnvironment() {
    String result = "[fn " + declaration.name.lexeme + " " + closure.hashCode()%100; 
      if (functionSet.contains(this)) {
      return  result + "]";
    } else {
      functionSet.add(this);
      if (closureSet.contains(closure)) {
        return result + "]";
      } else {
        closureSet.add(closure);
        return result + " " + closure.values.toString() 
        + (closure.enclosing == null ? "" : " -> " + closure.enclosing.hashCode()%100)
        + "]";
      }
    }
  } 
}
