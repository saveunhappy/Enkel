package com.bendcap.enkel.compiler.compiler;

import com.bendcap.enkel.compiler.domain.CompilationUnit;
import com.bendcap.enkel.compiler.bytecodegenerator.BytecodeGenerator;
import com.bendcap.enkel.compiler.parsing.Parser;
import com.bendcap.enkel.compiler.validation.ARGUMENT_ERRORS;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by KevinOfNeu on 2018/7/18  21:14.
 */
public class Compiler {

    public static void main(String[] args) {
        try {
            String[] path = {"EnkelExamples/Fields.enk"};
//            new Compiler().compile(args);
            new Compiler().compile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compile(String[] args) throws Exception {
        final ARGUMENT_ERRORS argumentsErrors = getArgumentValidationErrors(args);
        if (argumentsErrors != ARGUMENT_ERRORS.NONE) {
            System.out.println(argumentsErrors.getMessage());
            return;
        }
        final File enkelFile = new File(args[0]);
        String fileAbsolutePath = enkelFile.getAbsolutePath();
        final CompilationUnit compilationUnit = new Parser().getCompilationUnit(fileAbsolutePath);
        saveBytecodeToClassFile(compilationUnit);
    }

    private ARGUMENT_ERRORS getArgumentValidationErrors(String[] args) {
        if (args.length != 1) {
            return ARGUMENT_ERRORS.NO_FILE;
        }
        String filePath = args[0];
        if (!filePath.endsWith(".enk")) {
            return ARGUMENT_ERRORS.BAD_FILE_EXTENSION;
        }
        return ARGUMENT_ERRORS.NONE;
    }

    private static void saveBytecodeToClassFile(CompilationUnit compilationUnit) throws IOException {
        BytecodeGenerator bytecodeGenerator = new BytecodeGenerator();
        final byte[] byteCode = bytecodeGenerator.generate(compilationUnit);
        String className = compilationUnit.getClassName();
        String fileName = className + ".class";
        OutputStream os = Files.newOutputStream(Paths.get(fileName));
        IOUtils.write(byteCode, os);
    }
}
