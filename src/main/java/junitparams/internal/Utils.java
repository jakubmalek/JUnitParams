package junitparams.internal;

import java.lang.reflect.Method;

/**
 * Some String utils to handle parameterised tests' results.
 *
 * @author Pawel Lipinski
 */
public class Utils {
    public static final String REGEX_ALL_NEWLINES = "(\\r\\n|\\n|\\r)";

    public static String stringify(Object paramSet, int paramIdx) {
        String result = "[" + paramIdx + "] ";

        if (paramSet == null)
            result += "null";
        else if (paramSet instanceof String)
            result += paramSet;
        else
            result += asCsvString(safelyCastParamsToArray(paramSet));

        return trimSpecialChars(result);
    }

    private static String trimSpecialChars(String result) {
        return result.replace('(', '[').replace(')', ']').replaceAll(REGEX_ALL_NEWLINES, " ");
    }

    static Object[] safelyCastParamsToArray(Object paramSet) {
        final Object[] params;
        if (paramSet instanceof Object[]) {
            params = (Object[]) paramSet;
        } else {
            params = new Object[] {paramSet};
        }
        return params;
    }

    private static String asCsvString(Object[] params) {
        if (params == null)
            return "null";

        if (params.length == 0)
            return "";

        String result = "";

        for (int i = 0; i < params.length - 1; i++) {
            Object param = params[i];
            result = addParamToResult(result, param) + ", ";
        }
        result = addParamToResult(result, params[params.length - 1]);

        return result;
    }

    private static String addParamToResult(String result, Object param) {
        if (param == null)
            result += "null";
        else {
            try {
                tryFindingOverridenToString(param);
                result += param.toString();
            } catch (Exception e) {
                result += param.getClass().getSimpleName();
            }
        }
        return result;
    }

    private static void tryFindingOverridenToString(Object param)
            throws NoSuchMethodException {
        final Method toString = param.getClass().getMethod("toString");

        if (toString.getDeclaringClass().equals(Object.class)) {
            throw new NoSuchMethodException();
        }
    }
}
