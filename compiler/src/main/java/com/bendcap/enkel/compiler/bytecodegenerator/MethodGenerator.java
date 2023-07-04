package com.bendcap.enkel.compiler.bytecodegenerator;

import com.bendcap.enkel.compiler.bytecodegenerator.statement.StatementGenerator;
import com.bendcap.enkel.compiler.domain.Constructor;
import com.bendcap.enkel.compiler.domain.Function;
import com.bendcap.enkel.compiler.domain.node.expression.EmptyExpression;
import com.bendcap.enkel.compiler.domain.node.expression.SuperCall;
import com.bendcap.enkel.compiler.domain.scope.Scope;
import com.bendcap.enkel.compiler.domain.node.statement.Block;
import com.bendcap.enkel.compiler.domain.node.statement.ReturnStatement;
import com.bendcap.enkel.compiler.domain.node.statement.Statement;
import org.objectweb.asm.ClassWriter;
import com.bendcap.enkel.compiler.utils.DescriptorFactory;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by KevinOfNeu on 2018/8/22  15:44.
 */
public class MethodGenerator {
    public static final String MAIN_FUN_NAME = "main";
    private ClassWriter classWriter;

    public MethodGenerator(ClassWriter classWriter) {
        this.classWriter = classWriter;
    }

    public void generate(Function function) {
        String name = function.getName();
        //是否是main方法
        boolean isMain = name.equals(MAIN_FUN_NAME);
        //获取方法的描述，比如（）V就是void，无返回值的。
        String description = DescriptorFactory.getMethodDescriptor(function);
        Block block = (Block) function.getRootStatement();
        Scope scope = block.getScope();
        int access = Opcodes.ACC_PUBLIC + (isMain ? Opcodes.ACC_STATIC : 0);
        MethodVisitor mv = classWriter.visitMethod(access, name, description, null, null);
        mv.visitCode();
        StatementGenerator statementGenerator = new StatementGenerator(mv, scope);
        block.accept(statementGenerator);
        appendReturnIfNotExists(function, block, statementGenerator);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }


    public void generate(Constructor constructor) {
        Block block = (Block) constructor.getRootStatement();
        Scope scope = block.getScope();
        int access = Opcodes.ACC_PUBLIC;
        String description = DescriptorFactory.getMethodDescriptor(constructor);
        MethodVisitor mv = classWriter.visitMethod(access, "<init>", description, null, null);
        mv.visitCode();
        StatementGenerator statementScopeGenerator = new StatementGenerator(mv, scope);
        new SuperCall().accept(statementScopeGenerator);
        block.accept(statementScopeGenerator);
        appendReturnIfNotExists(constructor, block, statementScopeGenerator);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }


    private void appendReturnIfNotExists(Function function, Block block, StatementGenerator statementScopeGenrator) {
        boolean isLastStatementReturn = false;
        if(!block.getStatements().isEmpty()) {
            Statement lastStatement = block.getStatements().get(block.getStatements().size() - 1);
            isLastStatementReturn = lastStatement instanceof ReturnStatement;
        }
        if(!isLastStatementReturn) {
            EmptyExpression emptyExpression = new EmptyExpression(function.getReturnType());
            ReturnStatement returnStatement = new ReturnStatement(emptyExpression);
            returnStatement.accept(statementScopeGenrator);
        }
    }
}
