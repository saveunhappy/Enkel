package com.bendcap.enkel.compiler.visitor;

import com.bendcap.enkel.antlr.EnkelBaseVisitor;
import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.compiler.domain.clazz.Function;
import com.bendcap.enkel.compiler.domain.global.ClassDeclaration;
import com.bendcap.enkel.compiler.domain.global.MetaData;
import com.bendcap.enkel.compiler.domain.scope.FunctionSignature;
import com.bendcap.enkel.compiler.domain.scope.Scope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by KevinOfNeu on 2018/8/21  09:24.
 */
public class ClassVisitor extends EnkelBaseVisitor<ClassDeclaration> {
    private Scope scope;

    @Override
    public ClassDeclaration visitClassDeclaration(EnkelParser.ClassDeclarationContext ctx) {
        String name = ctx.className().getText();
        FunctionSignatureVisitor functionSignatureVisitor = new FunctionSignatureVisitor();
        List<EnkelParser.FunctionContext> methodCtx = ctx.classBody().function();
        MetaData metaData = new MetaData(ctx.className().getText());
        scope = new Scope(metaData);
        methodCtx.stream()
                .map(method -> method.functionDeclaration().accept(functionSignatureVisitor))
                .forEach(scope::addSignature);
        //这个时候scope中是只有方法签名，就是方法的定义，然后在stream中循环一次就把把scope传过去一次
        //有几个方法就传几次scope，然后scope中初始化了一个List的本地变量，每个方法自己获取到自己
        //作用域内的参数后，就放到这个scope中
        /**
         *      public Scope(Scope scope) {
         *         metaData = scope.metaData;
         *         localVariables = Lists.newArrayList(scope.localVariables);
         *         functionSignatures = Lists.newArrayList(scope.functionSignatures);
         *     } 有这个构造器的，现在有没有了不太清楚，之前的章节是有的，这里只是做个回顾
         * */
        List<Function> methods = methodCtx.stream()
                .map(method -> method.accept(new FunctionVisitor(scope)))
                .collect(Collectors.toList());
        return new ClassDeclaration(name, methods);
    }
}
