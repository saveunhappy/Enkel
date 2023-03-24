package com.bendcap.enkel.compiler.parsing;

import com.bendcap.enkel.antlr.EnkelBaseListener;
import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.compiler.bytecodegeneration.instructions.Instruction;
import com.bendcap.enkel.compiler.bytecodegeneration.instructions.PrintVariable;
import com.bendcap.enkel.compiler.bytecodegeneration.instructions.VariableDeclaration;
import com.bendcap.enkel.compiler.parsing.domain.Variable;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by KevinOfNeu on 2018/7/18  21:14.
 */
public class EnkelTreeWalkListener extends EnkelBaseListener {

    Queue<Instruction> instructionsQueue = new ArrayDeque<>();
    Map<String, Variable> variables = new HashMap<>();

    public Queue<Instruction> getInstructionsQueue() {
        return instructionsQueue;
    }

    @Override
    public void exitVariable(@NotNull EnkelParser.VariableContext ctx) {
        final TerminalNode varName = ctx.ID(); //变量名称，第一行 var five = 5 这里就是返回five
        final EnkelParser.ValueContext varValue = ctx.value();//这里返回  [20,8] 不知道啥意思
        final int varType = varValue.getStart().getType();//值的类型，这里返回4，比如String是1，Integer是2，这个是在Enkel.g4中按照顺序定义的。
        final int varIndex = variables.size();//创建一个map，里面就是记录了每个token再第几个位置，什么类型，值是什么
        final String varTextValue = varValue.getText();//这里才是真正获取到值，就是5
        Variable var = new Variable(varIndex, varType, varTextValue);//创建对象索引位置，这个定义的关键字的类型，变量的值
        variables.put(varName.getText(), var);//map里面放了变量名对应的创建的变量名字
        instructionsQueue.add(new VariableDeclaration(var));//指令的队列中添加变量的声明，
        logVariableDeclarationStatementFound(varName, varValue);
    }

    @Override
    public void exitPrint(@NotNull EnkelParser.PrintContext ctx) {
        final TerminalNode varName = ctx.ID();//接下来不是要打印么，这里就获得要打印的那个变量
        final boolean printedVarNotDeclared = !variables.containsKey(varName.getText());//你要打印一个变量，首先得声明把，之前我们解析之后，就把它放到map中去了，这里就是在从map中获取，没有获取到，那就说明你没有声明过，直接打印，那不就出错了么。
        if (printedVarNotDeclared) {
            final String erroFormat = "ERROR: WTF? You are trying to print var '%s' which has not been declared!!!.";
            System.err.printf(erroFormat, varName.getText());
            return;
        }
        final Variable variable = variables.get(varName.getText());//获取到那个放到map中的对象
        instructionsQueue.add(new PrintVariable(variable));
        logPrintStatementFound(varName, variable);
    }

    private void logVariableDeclarationStatementFound(TerminalNode varName, EnkelParser.ValueContext varValue) {
        final int line = varName.getSymbol().getLine();//第几行
        final String format = "OK: You declared variable named '%s' with value of '%s' at line '%s'.\n";
        System.out.printf(format, varName, varValue.getText(), line);
    }

    private void logPrintStatementFound(TerminalNode varName, Variable variable) {
        final int line = varName.getSymbol().getLine();
        final String format = "OK: You instructed to print variable '%s' which has value of '%s' at line '%s'.'\n";
        System.out.printf(format, variable.getId(), variable.getValue(), line);
    }
}
