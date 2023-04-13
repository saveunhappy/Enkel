package com.bendcap.enkel.compiler.visitor;

import com.bendcap.enkel.antlr.EnkelBaseVisitor;
import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.antlr.domain.global.ClassDeclaration;
import com.bendcap.enkel.antlr.domain.global.CompilationUnit;

/**
 * Created by KevinOfNeu on 2018/8/21  09:20.
 */
public class CompilationUnitVisitor extends EnkelBaseVisitor<CompilationUnit> {
    @Override
    public CompilationUnit visitCompilationUnit(EnkelParser.CompilationUnitContext ctx) {
        ClassVisitor classVisitor = new ClassVisitor();
        EnkelParser.ClassDeclarationContext classDeclarationContext = ctx.classDeclaration();
        ClassDeclaration classDeclaration = classDeclarationContext.accept(classVisitor);
        return new CompilationUnit(classDeclaration);//localVariables和FunctionParameter不是一个东西，FunctionParameter就是你进入的这个方法，参数携带的东西，localVariables是你能用到的所有变量，localVariables包含FunctionParameter
    }
}
