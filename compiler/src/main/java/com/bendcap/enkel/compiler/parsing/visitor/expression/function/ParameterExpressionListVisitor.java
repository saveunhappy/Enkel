package com.bendcap.enkel.compiler.parsing.visitor.expression.function;

import com.bendcap.enkel.antlr.EnkelBaseVisitor;
import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.compiler.domain.node.expression.Parameter;
import com.bendcap.enkel.compiler.parsing.visitor.expression.ExpressionVisitor;
import com.google.common.collect.Lists;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KevinOfNeu on 2018/8/31  12:22.
 */
public class ParameterExpressionListVisitor extends EnkelBaseVisitor<List<Parameter>> {
    private final ExpressionVisitor expressionVisitor;
    public ParameterExpressionListVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }
    @Override
    public List<Parameter> visitParametersList(@NotNull EnkelParser.ParametersListContext ctx) {
        List<EnkelParser.ParameterContext> paramsCtx = ctx.parameter();
        ParameterExpressionVisitor parameterExpressionVisitor = new ParameterExpressionVisitor(expressionVisitor);
        List<Parameter> parameters = new ArrayList<>();
        if(paramsCtx != null) {
            //ParameterExpressionVisitor就是解析Parameter的，然后返回的就是Parameter
            List<Parameter> params = Lists.transform(paramsCtx, p -> p.accept(parameterExpressionVisitor));
            parameters.addAll(params);
        }
        List<EnkelParser.ParameterWithDefaultValueContext> paramsWithDefaultValueCtx = ctx.parameterWithDefaultValue();
        if(paramsWithDefaultValueCtx != null && paramsWithDefaultValueCtx.size() > 0) {
            List<Parameter> params = Lists.transform(paramsWithDefaultValueCtx, p -> p.accept(parameterExpressionVisitor));
            parameters.addAll(params);
        }
        return parameters;
    }
    /**
     * import com.google.common.collect.Lists;
     * import com.google.common.base.Function;
     *
     * public class TransformTest {
     *     public static void main(String[] args) {
     *         List<Integer> sourceList = Lists.newArrayList(1, 2, 3, 4, 5);
     *         List<String> targetList = Lists.transform(sourceList, new Function<Integer, String>() {
     *             @Override
     *             public String apply(Integer input) {
     *                 return "string_" + input;
     *             }
     *         });
     *
     *         System.out.println("sourceList: " + sourceList); // [1, 2, 3, 4, 5]
     *         System.out.println("targetList: " + targetList); // [string_1, string_2, string_3, string_4, string_5]
     *     }
     * }
     * 其中，fromList 参数为需要转换的原始 List，function 参数为需要进行转换的函数。
     * 在这个例子中，我们创建了一个包含 1 到 5 的整数列表 sourceList。
     * 然后，我们使用 Lists.transform() 方法将 sourceList 的每个元素转换为一个字符串元素，
     * 新的列表保存在 targetList 中。我们使用了匿名内部类实现了 Function 接口，并重写了 apply() 方法，
     * 将整数元素转换为带有前缀 “string_” 的字符串。
     * 最后，我们输出了原始列表 sourceList 和新的列表 targetList，
     * 分别为 [1, 2, 3, 4, 5] 和 [string_1, string_2, string_3, string_4, string_5]。
     * */
}
