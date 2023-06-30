package com.bendcap.enkel.compiler.parsing.visitor.expression;

import com.bendcap.enkel.antlr.EnkelBaseVisitor;
import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.compiler.domain.node.expression.ConditionalExpression;
import com.bendcap.enkel.compiler.domain.node.expression.Expression;
import com.bendcap.enkel.compiler.domain.scope.Scope;
import com.bendcap.enkel.compiler.parsing.visitor.expression.function.CallExpressionVisitor;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * Created by KevinOfNeu on 2018/8/31  12:20.
 */
public class ExpressionVisitor extends EnkelBaseVisitor<Expression> {
    private final ArithmeticExpressionVisitor arithmeticExpressionVisitor;
    private final VariableReferenceExpressionVisitor variableReferenceExpressionVisitor;
    private final ValueExpressionVisitor valueExpressionVisitor;
    private final CallExpressionVisitor callExpressionVisitor;
    private final ConditionalExpressionVisitor conditionalExpressionVisitor;
    public ExpressionVisitor(Scope scope) {
        //进行算数运算，需要的就是表达式进行解析，在当前类中有，所以就是把this传进去
        arithmeticExpressionVisitor = new ArithmeticExpressionVisitor(this);
        //变量引用，变量得在自己的作用域中，所以把scope传进去
        variableReferenceExpressionVisitor = new VariableReferenceExpressionVisitor(scope);
        //值解析，这个只解析值，所以不需要其他的
        valueExpressionVisitor = new ValueExpressionVisitor();
        //函数调用，得确定这个函数是在自己的类中，所以需要scope，也需要根据antlr进行解析，所以this传进去
        callExpressionVisitor = new CallExpressionVisitor(this, scope);
        //for循环，也是需要解析antlr
        conditionalExpressionVisitor = new ConditionalExpressionVisitor(this);
    }
    @Override
    public Expression visitVarReference(@NotNull EnkelParser.VarReferenceContext ctx) {
        return variableReferenceExpressionVisitor.visitVarReference(ctx);
    }
    @Override
    public Expression visitValue(@NotNull EnkelParser.ValueContext ctx) {
        return valueExpressionVisitor.visitValue(ctx);
    }
    @Override
    public Expression visitFunctionCall(@NotNull EnkelParser.FunctionCallContext ctx) {
        return callExpressionVisitor.visitFunctionCall(ctx);
    }
    @Override
    public Expression visitConstructorCall(@NotNull EnkelParser.ConstructorCallContext ctx) {
        return callExpressionVisitor.visitConstructorCall(ctx);
    }
    @Override
    public Expression visitSupercall(@NotNull EnkelParser.SupercallContext ctx) {
        return callExpressionVisitor.visitSupercall(ctx);
    }
    @Override
    public Expression visitAdd(@NotNull EnkelParser.AddContext ctx) {
        return arithmeticExpressionVisitor.visitAdd(ctx);
    }
    @Override
    public Expression visitMultiply(@NotNull EnkelParser.MultiplyContext ctx) {
        return arithmeticExpressionVisitor.visitMultiply(ctx);
    }
    @Override
    public Expression visitSubstract(@NotNull EnkelParser.SubstractContext ctx) {
        return arithmeticExpressionVisitor.visitSubstract(ctx);
    }
    @Override
    public Expression visitDivide(@NotNull EnkelParser.DivideContext ctx) {
        return arithmeticExpressionVisitor.visitDivide(ctx);
    }
    @Override
    public ConditionalExpression visitConditionalExpression(@NotNull EnkelParser.ConditionalExpressionContext ctx) {
        return conditionalExpressionVisitor.visitConditionalExpression(ctx);
    }
}
