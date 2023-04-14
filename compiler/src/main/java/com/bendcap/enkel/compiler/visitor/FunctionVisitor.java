package com.bendcap.enkel.compiler.visitor;

import com.bendcap.enkel.antlr.EnkelBaseVisitor;
import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.antlr.domain.clazz.Function;
import com.bendcap.enkel.antlr.domain.expression.FunctionParameter;
import com.bendcap.enkel.antlr.domain.scope.LocalVariable;
import com.bendcap.enkel.antlr.domain.scope.Scope;
import com.bendcap.enkel.antlr.domain.statement.Statement;
import com.bendcap.enkel.antlr.domain.type.Type;
import com.bendcap.enkel.antlr.util.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by KevinOfNeu on 2018/8/22  16:03.
 */
public class FunctionVisitor extends EnkelBaseVisitor<Function> {
    private Scope scope;

    public FunctionVisitor(Scope scope) {
        this.scope = new Scope(scope);
    }

    @Override
    public Function visitFunction(@NotNull EnkelParser.FunctionContext ctx) {
        //main
        String name = getName(ctx);
        //VOID
        Type returnType = getReturnType(ctx);
        //args, STRING_ARR { name = string[],typeClass = String.class,
        // descriptor = [Ljava/lang/String; , Enum.name = STRING_ARR}
        List<FunctionParameter> arguments = getArguments(ctx);
        //这里获取到的就可以写到字节码中的东西了。参数名字，参数值，参数签名，返回类型，都有了，
        // 注意，这是FunctionVisitor，所以可以有好多个方法，那么这个也是一直在循环进行的
        List<Statement> instructions = getStatements(ctx);
        return new Function(scope, name, returnType, arguments, instructions);
    }

    private String getName(EnkelParser.FunctionContext functionDeclarationContext) {
        return functionDeclarationContext.functionDeclaration().functionName().getText();
    }

    private Type getReturnType(EnkelParser.FunctionContext functionDeclarationContext) {
        EnkelParser.TypeContext typeCtx = functionDeclarationContext.functionDeclaration().type();
        return TypeResolver.getFromTypeName(typeCtx);
    }

    private List<FunctionParameter> getArguments(EnkelParser.FunctionContext functionDeclarationContext) {
        //获取所有的参数相关的东西，还是那个，(int a,int b)(int a = 1,int b = 2)
        List<EnkelParser.FunctionArgumentContext> argsCtx = functionDeclarationContext.functionDeclaration().functionArgument();
        List<FunctionParameter> parameters = argsCtx.stream()
                .map(paramCtx -> new FunctionParameter(paramCtx.ID().getText(), TypeResolver.getFromTypeName(paramCtx.type())))
                .peek(param -> scope.addLocalVariable(new LocalVariable(param.getName(), param.getType())))
                .collect(Collectors.toList());
        return parameters;
    }

    private List<Statement> getStatements(@NotNull EnkelParser.FunctionContext ctx) {
        //这个statement就是一个标志，所有实现了这个接口的，都要去经过visitor去做对应的事情，
        // Expression也继承了Statement，注意，这个还是FunctionVisitor，
        // 那么就有好多个函数声明，是在lambda中反复调用的，所以，所有的函数都能调用到
        StatementVisitor statementVisitor = new StatementVisitor(scope);
        ExpressionVisitor expressionVisitor = new ExpressionVisitor(scope);
        CompositeVisitor<Statement> compositeVisitor = new CompositeVisitor<>(statementVisitor, expressionVisitor);
        return ctx.blockStatement().stream()
                .map(context -> compositeVisitor.accept(context))
                .collect(Collectors.toList());
    }
}
