package com.bendcap.enkel.compiler.visitor;

import com.bendcap.enkel.antlr.EnkelBaseVisitor;
import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.antlr.domain.expression.Expression;
import com.bendcap.enkel.antlr.domain.scope.LocalVariable;
import com.bendcap.enkel.antlr.domain.scope.Scope;
import com.bendcap.enkel.antlr.domain.statement.PrintStatement;
import com.bendcap.enkel.antlr.domain.statement.Statement;
import com.bendcap.enkel.antlr.domain.statement.VariableDeclarationStatement;


/**
 * Created by KevinOfNeu on 2018/8/21  09:48.
 */
public class StatementVisitor extends EnkelBaseVisitor<Statement> {
    private Scope scope;

    public StatementVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Statement visitPrintStatement(EnkelParser.PrintStatementContext ctx) {
        EnkelParser.ExpressionContext expressionContext = ctx.expression();
        ExpressionVisitor expressionVisitor = new ExpressionVisitor(scope);
        Expression expression = expressionContext.accept(expressionVisitor);
        return new PrintStatement(expression);
    }


    @Override
    public Statement visitVariableDeclaration(EnkelParser.VariableDeclarationContext ctx) {
        String varName = ctx.name().getText();
        EnkelParser.ExpressionContext expressionCtx = ctx.expression();
        ExpressionVisitor expressionVisitor = new ExpressionVisitor(scope);
        //这个expression可以是一个变量，可以是一个具体的值，也可以是一个函数
        Expression expression = expressionCtx.accept(expressionVisitor);
        scope.addLocalVariable(new LocalVariable(varName, expression.getType()));
        return new VariableDeclarationStatement(varName, expression);
    }
}
