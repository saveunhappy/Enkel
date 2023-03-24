package com.bendcap.enkel.compiler.bytecodegeneration;

import com.bendcap.enkel.compiler.bytecodegeneration.instructions.Instruction;
import com.bendcap.enkel.compiler.bytecodegeneration.instructions.VariableDeclaration;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Queue;

/**
 * Created by KevinOfNeu on 2018/7/18  21:14.
 */
public class BytecodeGenerator implements Opcodes {
    /**
    *
     *
     * 创建一个ClassWriter对象cw，用来生成Java字节码。
     * 创建一个MethodVisitor对象mv，用来生成一个静态的main方法。
     * 使用cw.visit()方法开始生成类，包括版本号，访问标志，类名，泛型签名，基类和接口列表。
     * 在方法中遍历instructionQueue，将队列中的指令应用于mv上。每个指令都是一个Instruction对象，它实现了apply()方法来将自己的操作应用于mv上。
     * 使用mv.visitInsn()方法添加一个返回指令(RETURN)。
     * 使用mv.visitMaxs()方法设置最大堆栈大小和最大局部变量数量。
     * 调用mv.visitEnd()方法表示方法的生成结束。
     * 调用cw.visitEnd()方法表示类的生成结束。
     * 返回生成的字节码数组。
    */
    public byte[] generateBytecode(Queue<Instruction> instructionQueue, String name) throws Exception {
        /**
         *  在创建ClassWriter对象时，参数表示生成的字节码的版本，如果传递0作为参数，
         *  则表示使用当前版本的JVM来生成字节码。在ASM库中，每个版本的JVM都有一个对应的版本号，
         *  该版本号可以在Opcodes类中找到。例如，JVM版本1.5对应的版本号是49，
         *  JVM版本1.6对应的版本号是50，而JVM版本1.8对应的版本号是52。
         *  如果不确定应该使用哪个版本号，则可以传递0作为参数，使用当前版本的JVM生成字节码。
         */
        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;
        //version , acess, name, signature, base class, interfaes
        /**
         * version：生成的字节码的版本号，这里是52，表示该字节码是为JVM版本1.8及以上生成的。
         * access：访问修饰符，这里使用ACC_PUBLIC + ACC_SUPER，表示该类是public访问修饰符，并且使用了ACC_SUPER标志，用于指示JVM执行某些特定操作。
         * name：类的全限定名。
         * signature：泛型签名，这里传递null表示不使用泛型。
         * superName：父类的全限定名，这里是"java/lang/Object"，表示该类是Object类的子类。
         * interfaces：实现的接口列表，这里传递null表示该类没有实现任何接口。
         *
         */
        cw.visit(52, ACC_PUBLIC + ACC_SUPER, name, null, "java/lang/Object", null);
        {
            //declare static void main
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            final long localVariablesCount = instructionQueue.stream()
                    .filter(instruction -> instruction instanceof VariableDeclaration)
                    .count();
            final int maxStack = 100; //TODO - do that properly
            //apply instructions generated from traversing parse tree!
            //把刚才按照顺序执行的东西写成字节码，按照顺序执行
            for (Instruction instruction : instructionQueue) {
                instruction.apply(mv);
            }
            /**
             *
             * 在Java字节码中，每个方法都必须以return指令结束，表示该方法的返回。如果不添加mv.visitInsn(RETURN)指令，生成的字节码会缺少方法的结束指令，导致在执行该方法时会出现异常。
             *
             * 在生成字节码的过程中，我们需要确保每个方法的结尾都添加了return指令，否则该方法将无法正常运行。
             *
             **/
            mv.visitInsn(RETURN); //add return instruction
            mv.visitMaxs(maxStack, (int) localVariablesCount); //set max stack and max local variables
            /**
             *
             * mv.visitEnd() 方法用于通知 ClassWriter 完成对当前方法的访问。当我们完成对方法的访问之后，需要调用这个方法以便通知 ClassWriter 已经完成了该方法的生成。
             *
             * 该方法必须在方法访问结束后调用，以便完成方法的生成，并且不能在该方法之后继续向该方法添加字节码指令。如果尝试在调用 visitEnd() 方法之后继续向该方法添加字节码指令，会抛出 IllegalStateException 异常。
             *
             **/
            mv.visitEnd();
        }
        /**
         * cw.visitEnd() 方法用于通知 ClassWriter 完成对当前类的访问。当我们完成对类的访问之后，需要调用这个方法以便通知 ClassWriter 已经完成了该类的生成。
         *
         * 如果我们没有调用 cw.visitEnd() 方法，那么生成的字节码将是不完整的，并且在使用这个类时会出现问题。具体来说，如果我们没有调用 cw.visitEnd() 方法，那么我们生成的字节码将不包括类的结尾标记，这将导致 JVM 在加载这个类时无法正确识别该类的结尾，并因此抛出 ClassFormatError 异常。
         *
         */
        cw.visitEnd();
        return cw.toByteArray();
    }
}
