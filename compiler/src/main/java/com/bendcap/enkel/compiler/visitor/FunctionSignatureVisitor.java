package com.bendcap.enkel.compiler.visitor;

import com.bendcap.enkel.antlr.EnkelBaseVisitor;
import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.antlr.domain.expression.FunctionParameter;
import com.bendcap.enkel.antlr.domain.scope.FunctionSignature;
import com.bendcap.enkel.antlr.domain.type.Type;
import com.bendcap.enkel.antlr.util.TypeResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KevinOfNeu on 2018/8/22  16:03.
 */
public class FunctionSignatureVisitor extends EnkelBaseVisitor<FunctionSignature> {
    @Override
    public FunctionSignature visitFunctionDeclaration(EnkelParser.FunctionDeclarationContext ctx) {
        String functionName = ctx.functionName().getText();
        List<EnkelParser.FunctionArgumentContext> argsCtx = ctx.functionArgument();
        List<FunctionParameter> parameters = new ArrayList<>();
        for (int i = 0; i < argsCtx.size(); i++) {
            EnkelParser.FunctionArgumentContext argCtx = argsCtx.get(i);
            String name = argCtx.ID().getText();
            Type type = TypeResolver.getFromTypeName(argCtx.type());
            FunctionParameter functionParameter = new FunctionParameter(name, type);
            parameters.add(functionParameter);
        } // 返回值类型，String 啊，int 啊，ResponseDto啊
        Type returnType = TypeResolver.getFromTypeName(ctx.type());
        return new FunctionSignature(functionName, parameters, returnType);
    }
}
