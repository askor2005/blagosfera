package ru.radom.kabinet.services.script;

import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

/**
 * Сервис для выполнения скриптов
 * Created by vgusev on 01.02.2016.
 */
@Service
public class ScriptEngineService {

    /**
     *
     * @param script
     * @param resultVarName
     * @param scriptVariables
     * @param <T>
     * @return
     * @throws ScriptException
     */
    public <T> T runScript(String script, String resultVarName, Map<String, Object> scriptVariables) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            // инъектим переменные в скрипт
            for (String varName : scriptVariables.keySet()) {
                Object varValue = scriptVariables.get(varName);
                engine.put(varName, varValue);
            }

            // Выполняем скрипт
            engine.eval(script);

            // Получаем результат
            return (T) engine.get(resultVarName);
        } catch (ScriptException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     *
     * @param expression
     * @param <T>
     * @return
     */
    public <T> T evalExpression(String expression) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            // Выполняем скрипт
            return (T)engine.eval(expression);
        } catch (ScriptException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
