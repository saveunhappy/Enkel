package com.bendcap.enkel.compiler.domain.scope;

import com.bendcap.enkel.compiler.domain.node.expression.Argument;
import com.bendcap.enkel.compiler.domain.node.expression.Parameter;
import com.bendcap.enkel.compiler.domain.type.Type;
import com.bendcap.enkel.compiler.exception.ParameterForNameNotFoundException;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Created by KevinOfNeu on 2018/8/22  09:39.
 */
public class FunctionSignature {
    private String name;
    private List<Parameter> parameters;
    private Type returnType;

    public FunctionSignature(String name, List<Parameter> parameters, Type returnType) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public Parameter getParameterForName(String name) {
        return parameters.stream()
                .filter(param -> param.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ParameterForNameNotFoundException(name, parameters));
    }

    public int getIndexOfParameters(String parameterName) {
        Parameter parameter = getParameterForName(parameterName);
        return parameters.indexOf(parameter);
    }

    public boolean matches(String otherSignatureName, List<Argument> arguments) {
        boolean namesAreEqual = this.name.equals(otherSignatureName);
        if (!namesAreEqual) return false;
        //没有默认值的参数个数,如果是0，那么就说明参数调用都有默认值
        long nonDefaultParametersCount = parameters.stream()
                .filter(p -> !p.getDefaultValue().isPresent())
                .count();
        if(nonDefaultParametersCount > arguments.size()) return false;
        //是否有参数带着值的，有一个也行。这个是x1->2这样的。
        boolean isNamedArgList = arguments.stream().anyMatch(a -> a.getParameterName().isPresent());
        if(isNamedArgList) {
            //就是看你指定参数，不按照顺序的调用，是否和你调用的这个方法签名的参数一一对应
            return arguments.stream().allMatch(a -> {
                String paramName = a.getParameterName().get();
                return parameters.stream()
                        .map(Parameter::getName)
                        .anyMatch(paramName::equals);
            });
        }
        //这边是为了判断传的参数和定义的类型是否是一样的，为什么这里有范围？因为有默认值，就像可变参数一样，只传前几个值。
        return IntStream.range(0, arguments.size())
                .allMatch(i -> {
                    Type argumentType = arguments.get(i).getType();
                    Type parameterType = parameters.get(i).getType();
                    return argumentType.equals(parameterType);
                });

    }

    public Type getReturnType() {
        return returnType;
    }
}
