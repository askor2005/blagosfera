package ru.radom.kabinet.services;

import org.apache.el.lang.FunctionMapperImpl;
import org.apache.el.lang.VariableMapperImpl;

import javax.el.*;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * @author dfilinberg
 */
public class EmailTemplateContext extends ELContext {

    private final ExpressionFactory expressionFactory;
    private final BeanELResolver resolver = new BeanELResolver();
    private final FunctionMapper functionMapper = new FunctionMapperImpl() {
        @Override
        public Method resolveFunction(String prefix, String localName) {
            for (final Method method : EmailTemplateContextFunctions.class.getMethods()) {
                if (method.getName().equals(localName)) {
                    return method;
                }
            }
            return super.resolveFunction(prefix, localName);
        }
    };
    private final VariableMapper variableMapper = new VariableMapperImpl();

    public EmailTemplateContext(Map<String, Object> variables, final ExpressionFactory expressionFactory) {
        this.expressionFactory = expressionFactory;
        if (variables != null && !variables.isEmpty()) {
            for (Map.Entry<String, Object> variable : variables.entrySet()) {
                variableMapper.setVariable(variable.getKey(), expressionFactory.createValueExpression(variable.getValue(), variable.getValue().getClass()));
            }
        }
    }

    @Override
    public ELResolver getELResolver() {
        return resolver;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return functionMapper;
    }

    @Override
    public VariableMapper getVariableMapper() {
        return variableMapper;
    }
    
}
