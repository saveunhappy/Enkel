package com.bendcap.enkel.antlr.util;

import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.antlr.domain.type.BuiltInType;
import com.bendcap.enkel.antlr.domain.type.ClassType;
import com.bendcap.enkel.antlr.domain.type.Type;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by KevinOfNeu on 2018/8/22  11:00.
 */
public class TypeResolver {
    public static Type getFromTypeName(EnkelParser.TypeContext typeContext) {
        if (typeContext == null) return BuiltInType.VOID;
        String typeName = typeContext.getText();//获取参数类型 (string[] args)类型就是string[],而且获取对应的枚举有三个值，自己定义这门语言的参数类型，对应Java中的参数类型，以及创建字节码的描述符
        Optional<? extends Type> buildInType = getBuiltInType(typeName);
        if (buildInType.isPresent()) return buildInType.get();
        return new ClassType(typeName);
    }

    public static Type getFromValue(String value) {
        if (StringUtils.isEmpty(value)) return BuiltInType.VOID;
        if (StringUtils.isNumeric(value)) {
            return BuiltInType.INT;
        }
        return BuiltInType.STRING;
    }


    private static Optional<BuiltInType> getBuiltInType(String typeName) {
        return Arrays.stream(BuiltInType.values())
                .filter(type -> type.getName().equals(typeName))
                .findFirst();
    }
}
