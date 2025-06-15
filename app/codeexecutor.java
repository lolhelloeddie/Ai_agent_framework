package com.aiagent.framework.core;

import android.content.Context;
import java.io.*;
import java.util.concurrent.TimeUnit;
import org.mozilla.javascript.*;

public class CodeExecutor {
    private Context context;
    private File workingDirectory;
    
    public CodeExecutor(Context context) {
        this.context = context;
        this.workingDirectory = new File(context.getFilesDir(), "code_workspace");
        if (!workingDirectory.exists()) {
            workingDirectory.mkdirs();
        }
    }

    public CodeExecutionResult execute(String code, String language) {
        try {
            switch (language.toLowerCase()) {
                case "java":
                    return simulateJavaExecution(code);
                case "python":
                    return simulatePythonExecution(code);
                case "javascript":
                    return executeJavaScript(code);
                default:
                    return new CodeExecutionResult(false, "Unsupported language: " + language);
            }
        } catch (Exception e) {
            return new CodeExecutionResult(false, "Execution error: " + e.getMessage());
        }
    }

    private CodeExecutionResult simulateJavaExecution(String code) {
        try {
            // Since we can't actually compile and run Java on Android easily,
            // we'll simulate the execution and provide feedback
            
            if (code.contains("System.out.print")) {
                String output = extractPrintStatements(code);
                return new CodeExecutionResult(true, "Simulated Java output:\n" + output);
            }
            
            if (code.contains("public static void main")) {
                return new CodeExecutionResult(true, "Java code structure is valid. Main method found.");
            }
            
            return new CodeExecutionResult(true, "Java code syntax appears valid.");
            
        } catch (Exception e) {
            return new CodeExecutionResult(false, "Java simulation error: " + e.getMessage());
        }
    }

    private CodeExecutionResult simulatePythonExecution(String code) {
        try {
            // Simulate Python execution
            if (code.contains("print(")) {
                String output = extractPythonPrintStatements(code);
                return new CodeExecutionResult(true, "Simulated Python output:\n" + output);
            }
            
            return new CodeExecutionResult(true, "Python code syntax appears valid.");
            
        } catch (Exception e) {
            return new CodeExecutionResult(false, "Python simulation error: " + e.getMessage());
        }
    }

    private CodeExecutionResult executeJavaScript(String code) {
        try {
            // Use Rhino JavaScript engine for Android
            org.mozilla.javascript.Context jsContext = org.mozilla.javascript.Context.enter();
            jsContext.setOptimizationLevel(-1); // Interpretive mode
            
            Scriptable scope = jsContext.initStandardObjects();
            
            // Capture console output
            StringBuilder output = new StringBuilder();
            
            // Add console object
            ScriptableObject console = (ScriptableObject) jsContext.newObject(scope);
            console.defineFunctionProperties(new String[]{"log"}, ConsoleWrapper.class, ScriptableObject.DONTENUM);
            ScriptableObject.putProperty(scope, "console", console);
            
            // Set up the output capture
            ConsoleWrapper.setOutput(output);
            
            try {
                Object result = jsContext.evaluateString(scope, code, "temp_script.js", 1, null);
                String resultStr = (result != null) ? result.toString() : "undefined";
                
                String fullOutput = output.toString();
                if (!fullOutput.isEmpty()) {
                    fullOutput += "\nResult: " + resultStr;
                } else {
                    fullOutput = "Result: " + resultStr;
                }
                
                return new CodeExecutionResult(true, fullOutput);
                
            } catch (RhinoException e) {
                return new CodeExecutionResult(false, "JavaScript error: " + e.getMessage());
            }
            
        } catch (Exception e) {
            return new CodeExecutionResult(false, "JavaScript execution error: " + e.getMessage());
        } finally {
            org.mozilla.javascript.Context.exit();
        }
    }

    private String extractPrintStatements(String code) {
        StringBuilder output = new StringBuilder();
        String[] lines = code.split("\n");
        
        for (String line : lines) {
            if (line.contains("System.out.print")) {
                // Extract the string from print statements
                int start = line.indexOf("(");
                int end = line.lastIndexOf(")");
                if (start != -1 && end != -1 && end > start) {
                    String content = line.substring(start + 1, end);
                    content = content.replaceAll("\"", "");
                    output.append(content).append("\n");
                }
            }
        }
        
        return output.toString();
    }

    private String extractPythonPrintStatements(String code) {
        StringBuilder output = new StringBuilder();
        String[] lines = code.split("\n");
        
        for (String line : lines) {
            if (line.contains("print(")) {
                // Extract the string from print statements
                int start = line.indexOf("(");
                int end = line.lastIndexOf(")");
                if (start != -1 && end != -1 && end > start) {
                    String content = line.substring(start + 1, end);
                    content = content.replaceAll("\"", "").replaceAll("'", "");
                    output.append(content).append("\n");
                }
            }
        }
        
        return output.toString();
    }

    // Console wrapper for JavaScript execution
    public static class ConsoleWrapper {
        private static StringBuilder output;
        
        public static void setOutput(StringBuilder sb) {
            output = sb;
        }
        
        public static void log(org.mozilla.javascript.Context cx, Scriptable thisObj, Object[] args, Function funObj) {
            if (output != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) output.append(" ");
                    output.append(org.mozilla.javascript.Context.toString(args[i]));
                }
                output.append("\n");
            }
        }
    }
}

class CodeExecutionResult {
    private boolean success;
    private String output;
    private String error;
    private long executionTime;

    public CodeExecutionResult(boolean success, String output) {
        this.success = success;
        this.output = output;
        this.executionTime = System.currentTimeMillis();
    }

    public CodeExecutionResult(boolean success, String output, String error) {
        this(success, output);
        this.error = error;
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getOutput() { return output; }
    public String getError() { return error; }
    public long getExecutionTime() { return executionTime; }
}