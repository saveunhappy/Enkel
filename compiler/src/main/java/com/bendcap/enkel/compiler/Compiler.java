package com.bendcap.enkel.compiler;

import com.bendcap.enkel.compiler.bytecodegeneration.BytecodeGenerator;
import com.bendcap.enkel.compiler.bytecodegeneration.instructions.Instruction;
import com.bendcap.enkel.compiler.parsing.SyntaxTreeTraverser;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;

/**
 * Created by KevinOfNeu on 2018/7/18  21:14.
 */
public class Compiler implements Opcodes {

    public static void main(String[] args) throws Exception {
        String[] sourceCodeLocation = {"EnkelExample/first.enk"};
        new Compiler().compile(sourceCodeLocation);
    }

    public void compile(String[] args) throws Exception {
        //看看你传过来的参数有没有错，有的话直接就结束了。
        final ARGUMENT_ERRORS argumentsErrors = getArgumentValidationErrors(args);
        if (argumentsErrors != ARGUMENT_ERRORS.NONE) {
            System.out.println(argumentsErrors.getMessage());
            return;
        }
        //传过来的就是文件名带地址
        final File enkelFile = new File(args[0]);
        String fileName = enkelFile.getName();
        String fileAbsolutePath = enkelFile.getAbsolutePath();
        //获得文件的名字
        String className = StringUtils.remove(fileName, ".enk");
        //传入绝对值路径，获取到文件里面的内容，然后去解析里面的token，变量啊或者是print方法
        final Queue<Instruction> instructionsQueue = new SyntaxTreeTraverser().getInstructions(fileAbsolutePath);
        final byte[] byteCode = new BytecodeGenerator().generateBytecode(instructionsQueue, className);
        saveBytecodeToClassFile(fileName, byteCode);
    }

    private ARGUMENT_ERRORS getArgumentValidationErrors(String[] args) {
        //进来之后首先检查你传的是不是一个文件，因为接受的参数是一个数组，
        if (args.length != 1) {
            return ARGUMENT_ERRORS.NO_FILE;
        }
        //再然后检查你是不是以.enk结尾的文件，不是就报错了。
        String filePath = args[0];
        if (!filePath.endsWith(".enk")) {
            return ARGUMENT_ERRORS.BAD_FILE_EXTENSION;
        }
        return ARGUMENT_ERRORS.NONE;
    }

    private static void saveBytecodeToClassFile(String fileName, byte[] byteCode) throws IOException {

        final String classFile = StringUtils.replace(fileName, ".enk", ".class");
        OutputStream os = new FileOutputStream(classFile);
        /**
         * 这段代码没有显式指定生成的字节码文件的路径，因此字节码文件的路径取决于调用这段代码的上下文。具体来说，这段代码将生成的字节码文件保存在当前工作目录下，文件名为原文件名的基础上将扩展名 .enk 替换成了 .class。
         *
         * 如果你不确定当前工作目录是哪个，可以通过 System.getProperty("user.dir") 方法获取当前工作目录的绝对路径。
         **/
        os.write(byteCode);
        os.close();
    }
}
