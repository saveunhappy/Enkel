package com.bendcap.enkel.compiler.bytecodegeneration;

import com.bendcap.enkel.compiler.bytecodegeneration.classscopeinstructions.ClassScopeInstruction;
import com.bendcap.enkel.compiler.bytecodegeneration.classscopeinstructions.VariableDeclaration;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by KevinOfNeu on 2018/8/13  22:41.
 */
//原来没有这个类的时候，这个只是根据传进来的文件名作为类名，生成一个Main函数进行打印和输出，
//现在只是暂时迁移到这边来，还是只生成一个main函数。
public class CompilationUnit {
    private ClassDeclaration classDeclaration;

    public CompilationUnit(ClassDeclaration classDeclaration) {
        this.classDeclaration = classDeclaration;
    }

    public ClassDeclaration getClassDeclaration() {
        return classDeclaration;
    }

    public String getClassName() {
        return classDeclaration.getName();
    }

    public byte[] getByteCode() {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(52, ACC_PUBLIC + ACC_SUPER, getClassName(), null, "java/lang/Object", null);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        {
            final long localVariablesCount = classDeclaration.getClassScopeInstructions().stream()
                    .filter(instruction -> instruction instanceof VariableDeclaration)
                    .count();
            final int maxStack = 100;
            for (ClassScopeInstruction classScopeInstruction : classDeclaration.getClassScopeInstructions()) {
                classScopeInstruction.apply(mv);
            }
            mv.visitInsn(RETURN);
            mv.visitMaxs(maxStack, (int) localVariablesCount);
            mv.visitEnd();
        }
        cw.visitEnd();
        return cw.toByteArray();
    }
}
