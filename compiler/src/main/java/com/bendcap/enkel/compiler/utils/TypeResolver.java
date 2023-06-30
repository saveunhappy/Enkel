package com.bendcap.enkel.compiler.utils;

import com.bendcap.enkel.antlr.EnkelParser;
import com.bendcap.enkel.compiler.domain.type.BultInType;
import com.bendcap.enkel.compiler.domain.type.ClassType;
import com.bendcap.enkel.compiler.domain.type.Type;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by KevinOfNeu on 2018/8/22  11:00.
 */
public final class TypeResolver {
    public static Type getFromTypeName(EnkelParser.TypeContext typeContext) {
        if(typeContext == null) return BultInType.VOID;
        String typeName = typeContext.getText();
        if(typeName.equals("java.lang.String")) return BultInType.STRING;
        //返回的就是那个类型的枚举
        Optional<? extends Type> builtInType = getBuiltInType(typeName);
        if(builtInType.isPresent()) return builtInType.get();
        return new ClassType(typeName);
    }
    //核心就在这里，会根据你的值来判断是什么类型的
    public static Type getFromValue(String value) {
        if(StringUtils.isEmpty(value)) return BultInType.VOID;
        if(NumberUtils.isNumber(value)) {
            if (Ints.tryParse(value) != null) {
                return BultInType.INT;
            } else if(Floats.tryParse(value) != null) {
                return BultInType.FLOAT;
            } else if(Doubles.tryParse(value) != null) {
                return BultInType.DOUBLE;
            }
        } else if (BooleanUtils.toBoolean(value)) {
            return BultInType.BOOLEAN;
        }
        return BultInType.STRING;
    }

    public static Object getValueFromString(String stringValue, Type type) {
        if (TypeChecker.isInt(type)) {
            return Integer.valueOf(stringValue);
        }
        if (TypeChecker.isFloat(type)) {
            return Float.valueOf(stringValue);
        }
        if (TypeChecker.isDouble(type)) {
            return Double.valueOf(stringValue);
        }
        if (TypeChecker.isBool(type)) {
            return Boolean.valueOf(stringValue);
        }
        if (type == BultInType.STRING) {
            stringValue = StringUtils.removeStart(stringValue, "\"");
            stringValue = StringUtils.removeEnd(stringValue, "\"");
            return stringValue;
        }
        throw new AssertionError("Objects not yet implemented!");
    }

    private static Optional<BultInType> getBuiltInType(String typeName) {
        return Arrays.stream(BultInType.values())
                .filter(type -> type.getName().equals(typeName))
                .findFirst();
    }
}
