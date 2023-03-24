package com.bendcap.enkel.compiler.bytecodegeneration.instructions;

import com.bendcap.enkel.antlr.EnkelLexer;
import com.bendcap.enkel.compiler.parsing.domain.Variable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by KevinOfNeu on 2018/7/18  21:14.
 */
public class PrintVariable implements Instruction, Opcodes {

    private Variable variable;

    public PrintVariable(Variable variable) {
        this.variable = variable;
    }

    @Override
    public void apply(MethodVisitor mv) {
        final int type = variable.getType();
        final int id = variable.getId();
        // GETSTATIC：操作码，表示获取一个静态字段的值。
        //"java/lang/System"：类名，表示要访问的类的全限定名。
        //"out"：字段名，表示要访问的静态字段的名称。
        //"Ljava/io/PrintStream;"：字段类型，表示要访问的静态字段的类型。这里使用了Java虚拟机规范中的类型描述符，"L"表示引用类型，"java/io/PrintStream"表示全限定名，";"表示结束符。
        //因此，这段代码访问了System类的静态字段out，它的类型是PrintStream，表示要获取一个PrintStream类型的静态字段的值。此外，由于这个字段是静态的，所以不需要指定对象实例，可以直接使用GETSTATIC指令来获取这个字段的值。
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        if (type == EnkelLexer.NUMBER) {
            //压到栈顶，整型是ILOAD
            mv.visitVarInsn(ILOAD, id);
            //INVOKEVIRTUAL：操作码，表示调用一个实例方法。
            //"java/io/PrintStream"：类名，表示要调用方法的类的全限定名。
            //"println"：方法名，表示要调用的方法名称。
            //"(I)V"：方法描述符，表示要调用的方法的参数类型和返回类型。这里使用了Java虚拟机规范中的方法描述符，"(I)"表示一个int类型的参数，"V"表示没有返回值。
            //false：表示方法的调用不是接口方法。
            //因此，这段代码调用了PrintStream类的println方法，它的参数类型是int类型，表示要将栈顶的int类型数值作为参数传递给这个方法，而方法本身没有返回值。由于这个方法是实例方法，因此在调用它之前需要先将PrintStream对象引用推到栈顶，这通常使用aload指令来实现。在这个例子中，前面的代码已经将System.out对象推到了栈顶，所以这里直接使用了INVOKEVIRTUAL指令来调用PrintStream的实例方法println，并将栈顶的int类型数值作为参数传递给这个方法。
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        } else if (type == EnkelLexer.STRING) {
            //压到栈顶，引用类型是ALOAD
            mv.visitVarInsn(ALOAD, id);
            //INVOKEVIRTUAL：操作码，表示调用一个实例方法。
            //"java/io/PrintStream"：类名，表示要调用方法的类的全限定名。
            //"println"：方法名，表示要调用的方法名称。
            //"(Ljava/lang/String;)V"：方法描述符，表示要调用的方法的参数类型和返回类型。这里使用了Java虚拟机规范中的方法描述符，"(Ljava/lang/String;)"表示一个String类型的参数，"V"表示没有返回值。
            //false：表示方法的调用不是接口方法。
            //因此，这段代码调用了PrintStream类的println方法，它的参数类型是String类型，表示要将栈顶的String类型数值作为参数传递给这个方法，而方法本身没有返回值。由于这个方法是实例方法，因此在调用它之前需要先将PrintStream对象引用推到栈顶，这通常使用aload指令来实现。在这个例子中，前面的代码已经将System.out对象推到了栈顶，所以这里直接使用了INVOKEVIRTUAL指令来调用PrintStream的实例方法println，并将栈顶的String类型数值作为参数传递给这个方法。
            //mv.visitMethodInsn(INVOKEINTERFACE, "com/example/MyInterface", "myMethod", "(Ljava/lang/String;)Ljava/lang/String;", true); 这个表示执行接口，方法名是myMethod，参数类型和返回值类型都是String，true表示调用的是接口方法
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }
}
