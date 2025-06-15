// KnowledgeBase.java
package com.aiagent.framework.core;

import android.content.Context;
import java.util.*;
import java.io.*;
import org.json.JSONObject;

public class KnowledgeBase {
    private Context context;
    private Map<String, String> knowledgeMap;
    private File knowledgeFile;
    
    public KnowledgeBase(Context context) {
        this.context = context;
        this.knowledgeMap = new HashMap<>();
        this.knowledgeFile = new File(context.getFilesDir(), "knowledge_base.json");
        loadKnowledge();
    }
    
    public String search(String query) {
        // Simple keyword matching
        for (Map.Entry<String, String> entry : knowledgeMap.entrySet()) {
            if (entry.getKey().toLowerCase().contains(query.toLowerCase()) ||
                query.toLowerCase().contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public void store(String query, String response) {
        knowledgeMap.put(query, response);
        saveKnowledge();
    }
    
    public Map<String, String> getCodeTemplates() {
        Map<String, String> templates = new HashMap<>();
        templates.put("hello world", "System.out.println(\"Hello, World!\");");
        templates.put("for loop", "for (int i = 0; i < n; i++) {\n    // code here\n}");
        templates.put("if statement", "if (condition) {\n    // code here\n}");
        return templates;
    }
    
    private void loadKnowledge() {
        try {
            if (knowledgeFile.exists()) {
                String content = readFile(knowledgeFile);
                JSONObject json = new JSONObject(content);
                Iterator<String> keys = json.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    knowledgeMap.put(key, json.getString(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void saveKnowledge() {
        try {
            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> entry : knowledgeMap.entrySet()) {
                json.put(entry.getKey(), entry.getValue());
            }
            writeFile(knowledgeFile, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }
    
    private void writeFile(File file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
}

// MemoryManager.java
package com.aiagent.framework.core;

import android.content.Context;
import java.util.*;

public class MemoryManager {
    private Context context;
    private Map<String, Object> shortTermMemory;
    private Map<String, Object> longTermMemory;
    private final int MAX_SHORT_TERM_SIZE = 100;
    
    public MemoryManager(Context context) {
        this.context = context;
        this.shortTermMemory = new LinkedHashMap<String, Object>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                return size() > MAX_SHORT_TERM_SIZE;
            }
        };
        this.longTermMemory = new HashMap<>();
    }
    
    public void storeShortTerm(String key, Object value) {
        shortTermMemory.put(key, value);
    }
    
    public void storeLongTerm(String key, Object value) {
        longTermMemory.put(key, value);
    }
    
    public Object getShortTerm(String key) {
        return shortTermMemory.get(key);
    }
    
    public Object getLongTerm(String key) {
        return longTermMemory.get(key);
    }
    
    public void clearShortTerm() {
        shortTermMemory.clear();
    }
}

// Simplified LearningEngine.java
package com.aiagent.framework.core;

import android.content.Context;
import java.util.*;
import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;

public class LearningEngine {
    private Context context;
    private Map<String, Double> featureWeights;
    private List<LearningPattern> patterns;
    private File learningDataFile;
    
    public LearningEngine(Context context) {
        this.context = context;
        this.featureWeights = new HashMap<>();
        this.patterns = new ArrayList<>();
        this.learningDataFile = new File(context.getFilesDir(), "learning_data.json");
        
        initializeWeights();
    }

    private void initializeWeights() {
        featureWeights.put("query_length", 0.5);
        featureWeights.put("response_quality", 0.8);
        featureWeights.put("code_success", 0.9);
    }

    public void learnFromInteraction(String query, String response) {
        try {
            LearningPattern pattern = new LearningPattern();
            pattern.input = query;
            pattern.output = response;
            pattern.timestamp = System.currentTimeMillis();
            pattern.features = extractSimpleFeatures(query, response);
            
            patterns.add(pattern);
            
            // Limit pattern storage
            if (patterns.size() > 100) {
                patterns.remove(0);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void learnFromCodeExecution(String code, String language, CodeExecutionResult result) {
        try {
            Map<String, Object> learningData = new HashMap<>();
            learningData.put("code", code);
            learningData.put("language", language);
            learningData.put("success", result.isSuccess());
            learningData.put("timestamp", System.currentTimeMillis());
            
            // Simple learning from success/failure
            if (result.isSuccess()) {
                Double currentWeight = featureWeights.getOrDefault("code_success", 0.5);
                featureWeights.put("code_success", Math.min(1.0, currentWeight + 0.01));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Double> extractSimpleFeatures(String query, String response) {
        Map<String, Double> features = new HashMap<>();
        features.put("query_length", (double) query.length());
        features.put("response_length", (double) response.length());
        features.put("has_code", response.contains("{") || response.contains("def ") ? 1.0 : 0.0);
        return features;
    }

    static class LearningPattern {
        String input;
        String output;
        Map<String, Double> features;
        long timestamp;
    }
}