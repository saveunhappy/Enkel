package com.bendcap.enkel.compiler.bytecodegeneration.instructions;

import com.bendcap.enkel.antlr.EnkelLexer;
import com.bendcap.enkel.compiler.parsing.domain.Variable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by KevinOfNeu on 2018/7/18  21:14.
 */
public class VariableDeclaration implements Instruction, Opcodes {
    Variable variable;

    public VariableDeclaration(Variable variable) {
        this.variable = variable;
    }

    @Override
    public void apply(MethodVisitor mv) {
        //这个type在antlr这个框架中生成的代码中就能解析到，这个type也是你那个g4文件声明的顺序，从1开始，
        final int type = variable.getType();
        if (type == EnkelLexer.NUMBER) {
            //这里就是，比如你传的第一个代码，var five = 5，但是经过解析之后，得到的5是一个字符串
            int val = Integer.parseInt(variable.getValue());
            //visitIntInsn用于访问将整数值加载到操作数栈中的指令 例如，BIPUSH、SIPUSH和NEWARRAY指令。具体来说，BIPUSH指令将一个字节大小的整数常量(-128到127)推送到栈顶，SIPUSH指令将一个短整型常量(-32768到32767)推送到栈顶，NEWARRAY指令创建一个指定类型和大小的新数组对象并将其推送到栈顶。
            mv.visitIntInsn(BIPUSH, val);
            //在Java字节码中，visitVarInsn用于访问将本地变量加载到操作数栈中或从操作数栈中存储本地变量的指令。例如，ILOAD、ISTORE、ALOAD和ASTORE指令。
            //在ASM中，visitVarInsn方法用于访问这些指令。它接受两个参数：操作码和本地变量索引，并生成对应的指令将本地变量加载到操作数栈中或从操作数栈中存储本地变量
            //具体来说，ILOAD和ALOAD指令将一个整数或引用类型的本地变量加载到栈顶，ISTORE和ASTORE指令将栈顶的整数或引用类型的值存储到本地变量中。
            //ILOAD和ALOAD指令是用于将本地变量加载到操作数栈中的指令，它们的主要区别在于所加载的本地变量的类型。
            //ILOAD指令用于将一个整型类型的本地变量加载到操作数栈中。它的操作码为0x15，其语法格式为： perl语言 ：iload <index>
            //ALOAD指令用于将一个引用类型的本地变量加载到操作数栈中。它的操作码为0x19，其语法格式为：perl语言 ：aload <index>
            //因此，ILOAD指令和ALOAD指令的区别在于它们所操作的本地变量的类型不同。如果需要加载整型类型的本地变量，就应该使用ILOAD指令；如果需要加载引用类型的本地变量，就应该使用ALOAD指令。
            //这个variable.getId()是antlr返回回来的，我们把它存到了一个对象中，所以这里能拿到，这个id也是按照顺序弄的，一个一个的执行。
            mv.visitVarInsn(ISTORE, variable.getId());
        } else if (type == EnkelLexer.STRING) {

            mv.visitLdcInsn(variable.getValue());//visitLdcInsn方法接受一个常量作为参数，并生成对应的LDC指令，将常量加载到操作数栈中。
            mv.visitVarInsn(ASTORE, variable.getId());
        }
    }
}
