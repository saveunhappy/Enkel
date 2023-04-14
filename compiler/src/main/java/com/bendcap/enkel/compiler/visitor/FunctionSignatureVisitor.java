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
        //参数当然是有多个了，int a,int b所以返回的也是一个List
        List<EnkelParser.FunctionArgumentContext> argsCtx = ctx.functionArgument();
        List<FunctionParameter> parameters = new ArrayList<>();
        for (int i = 0; i < argsCtx.size(); i++) {
            EnkelParser.FunctionArgumentContext argCtx = argsCtx.get(i);
            //(string[] args)这里获取的就是args,参数的名字
            String name = argCtx.ID().getText();
            // functionArgument : type ID functionParamdefaultValue? ;
            // 类型，参数名，值，就是说可以是（int a = 0,int b = 1）
            // 问号就是0次或者一次，那么就是可以有默认值，可以没有，这个type对应的是枚举的名字，
            // string[]对应的就是string的数组，就是STRING_ARR
            Type type = TypeResolver.getFromTypeName(argCtx.type());
            //name:args,type:STRING_ARR
            FunctionParameter functionParameter = new FunctionParameter(name, type);
            parameters.add(functionParameter);
        }
        // 返回值类型，String 啊，int 啊，ResponseDto啊
        //void main (string[] args)  这里void对应的就是VOID枚举
        Type returnType = TypeResolver.getFromTypeName(ctx.type());
        //main,(args,STRING_ARR),VOID
        return new FunctionSignature(functionName, parameters, returnType);
    }
}
