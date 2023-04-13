package com.bendcap.enkel.compiler.visitor;

import com.bendcap.enkel.antlr.EnkelBaseVisitor;
import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.antlr.domain.clazz.Function;
import com.bendcap.enkel.antlr.domain.global.ClassDeclaration;
import com.bendcap.enkel.antlr.domain.global.MetaData;
import com.bendcap.enkel.antlr.domain.scope.FunctionSignature;
import com.bendcap.enkel.antlr.domain.scope.Scope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by KevinOfNeu on 2018/8/21  09:24.
 */
public class ClassVisitor extends EnkelBaseVisitor<ClassDeclaration> {
    private Scope scope;

    @Override
    public ClassDeclaration visitClassDeclaration(EnkelParser.ClassDeclarationContext ctx) {
        String name = ctx.className().getText();//获取代码中的类名，比如这个代码中的类是First，那么获取的就是类中的名字，不是文件的名字
        FunctionSignatureVisitor functionSignatureVisitor = new FunctionSignatureVisitor();//方法签名，就像C语言中的方法，要使用的话必须先定义，这个和Java中的也是一样，你如果先调用了一个方法，但是这个方法在下面才声明，所以要先获取到所有的方法签名
        List<EnkelParser.FunctionContext> methodCtx = ctx.classBody().function();//因为classBody中的function后面有个星号，那么正则的意思就是可以获取任意一个，所以获取到的是一个list
        MetaData metaData = new MetaData(ctx.className().getText());
        scope = new Scope(metaData);//signatures为什么没有使用？因为peek的时候添加到scope中去了，就是在一个代码块中用到了哪些方法，那么它就有哪些方法的签名，或者说声明，而且这个语言的方法全部都是static的，所以生成代码的时候就得能调用所有的方法，也就是说应该每一个scope中都应该有所有方法的定义？
        List<FunctionSignature> signatures = methodCtx.stream()
                .map(method -> method.functionDeclaration().accept(functionSignatureVisitor))
                .peek(scope::addSignature)
                .collect(Collectors.toList());
        List<Function> methods = methodCtx.stream()//这个其实就是在获取所有的参数的定义了，参数名是什么，参数的类型是什么，而且所有的参数值，参数类型，打印什么，都有了，这里是全部的，lambda中的是获取的某一个方法的，这里负责组合
                .map(method -> method.accept(new FunctionVisitor(scope)))
                .collect(Collectors.toList());
        return new ClassDeclaration(name, methods);
    }
}
