package com.bendcap.enkel.compiler.visitor;

import com.bendcap.enkel.antlr.EnkelBaseVisitor;
import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.antlr.domain.expression.*;
import com.bendcap.enkel.antlr.domain.scope.FunctionSignature;
import com.bendcap.enkel.antlr.domain.scope.LocalVariable;
import com.bendcap.enkel.antlr.domain.scope.Scope;
import com.bendcap.enkel.antlr.domain.type.Type;
import com.bendcap.enkel.antlr.util.TypeResolver;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by KevinOfNeu on 2018/8/22  16:04.
 */
public class ExpressionVisitor extends EnkelBaseVisitor<Expression> {
    private Scope scope;

    public ExpressionVisitor(Scope scope) {
        this.scope = scope;
    }


    @Override
    public Expression visitVarReference(EnkelParser.VarReferenceContext ctx) {
        String varName = ctx.getText();
        LocalVariable localVariable = scope.getLocalVariable(varName);
        return new VarReference(varName, localVariable.getType());
    }


    @Override
    public Expression visitValue(EnkelParser.ValueContext ctx) {
        String value = ctx.getText();
        Type type = TypeResolver.getFromValue(value);
        return new Value(type, value);
    }


    @Override
    public Expression visitFunctionCall(EnkelParser.FunctionCallContext ctx) {
        String funName = ctx.functionName().getText();
        FunctionSignature signature = scope.getSignature(funName);
        List<FunctionParameter> signatureParameters = signature.getArguments();
        List<EnkelParser.ExpressionContext> calledParameters = ctx.expressionList().expression();//这个就是 funa(funb(a),func(c)) funb(int a){var a = 1 print a fund(a)}
        List<Expression> arguments = calledParameters.stream()//这里是去scope中找对应的变量，string[] args中的args也是参数，可以接受并且使用的，还有自己定义的 var x = "hello" var y = 1,获取到的就是x和y
                .map(expressionContext -> {
                    return expressionContext.accept(new ExpressionVisitor(scope));
                })
                .collect(Collectors.toList());
        Type returnType = signature.getReturnType();
        return new FunctionCall(signature, arguments, null);//返回函数签名和获取的参数名，定义的是(int a,int b)，调用的时候是fun(x,y)，把这两个给组合一下

    }
}

