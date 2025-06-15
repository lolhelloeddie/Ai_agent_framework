// ChatFragment.java - Complete version
package com.aiagent.framework.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aiagent.framework.MainActivity;
import com.aiagent.framework.R;
import com.aiagent.framework.core.AIAgent;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private AIAgent aiAgent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupSendButton();
        
        return view;
    }

    private void initializeViews(View view) {
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.send_button);
        
        messages = new ArrayList<>();
        aiAgent = ((MainActivity) getActivity()).getAIAgent();
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupSendButton() {
        sendButton.setOnClickListener(v -> sendMessage());
        messageInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            // Add user message
            messages.add(new ChatMessage(message, true));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            messageInput.setText("");
            
            // Scroll to bottom
            chatRecyclerView.scrollToPosition(messages.size() - 1);
            
            // Get AI response
            aiAgent.processQuery(message).thenAccept(response -> {
                getActivity().runOnUiThread(() -> {
                    messages.add(new ChatMessage(response, false));
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    chatRecyclerView.scrollToPosition(messages.size() - 1);
                });
            });
        }
    }
}

// CodeEditorFragment.java
package com.aiagent.framework.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.aiagent.framework.MainActivity;
import com.aiagent.framework.R;
import com.aiagent.framework.core.AIAgent;
import com.aiagent.framework.core.CodeExecutionResult;

public class CodeEditorFragment extends Fragment {
    private EditText codeEditor;
    private TextView outputText;
    private Button runButton;
    private Button clearButton;
    private Spinner languageSpinner;
    private AIAgent aiAgent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_editor, container, false);
        
        initializeViews(view);
        setupSpinner();
        setupButtons();
        
        return view;
    }

    private void initializeViews(View view) {
        codeEditor = view.findViewById(R.id.code_editor);
        outputText = view.findViewById(R.id.output_text);
        runButton = view.findViewById(R.id.run_button);
        clearButton = view.findViewById(R.id.clear_button);
        languageSpinner = view.findViewById(R.id.language_spinner);
        
        aiAgent = ((MainActivity) getActivity()).getAIAgent();
    }

    private void setupSpinner() {
        String[] languages = {"Java", "Python", "JavaScript"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
    }

    private void setupButtons() {
        runButton.setOnClickListener(v -> executeCode());
        clearButton.setOnClickListener(v -> {
            codeEditor.setText("");
            outputText.setText("Ready to execute code...");
        });
    }

    private void executeCode() {
        String code = codeEditor.getText().toString().trim();
        String language = languageSpinner.getSelectedItem().toString().toLowerCase();
        
        if (code.isEmpty()) {
            outputText.setText("Please enter some code to execute.");
            return;
        }
        
        outputText.setText("Executing...");
        
        aiAgent.executeCode(code, language).thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                if (result.isSuccess()) {
                    outputText.setText(result.getOutput());
                } else {
                    outputText.setText("Error: " + result.getOutput());
                }
            });
        });
    }
}

// AnalyticsFragment.java
package com.aiagent.framework.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.aiagent.framework.R;

public class AnalyticsFragment extends Fragment {
    private TextView analyticsText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);
        
        analyticsText = view.findViewById(R.id.analytics_text);
        analyticsText.setText("Analytics Dashboard\n\nThis section will show:\n" +
                "• Query statistics\n" +
                "• Code execution history\n" +
                "• Learning progress\n" +
                "• Performance metrics\n\n" +
                "Feature coming soon!");
        
        return view;
    }
}

// ChatMessage.java
package com.aiagent.framework.ui;

public class ChatMessage {
    private String message;
    private boolean isUser;
    private long timestamp;

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() { return message; }
    public boolean isUser() { return isUser; }
    public long getTimestamp() { return timestamp; }
}

// ChatAdapter.java
package com.aiagent.framework.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aiagent.framework.R;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private TextView senderLabel;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            senderLabel = itemView.findViewById(R.id.sender_label);
        }

        public void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
            senderLabel.setText(message.isUser() ? "You" : "AI Agent");
            
            // Style based on sender
            if (message.isUser()) {
                itemView.setBackgroundResource(R.color.user_message_bg);
            } else {
                itemView.setBackgroundResource(R.color.ai_message_bg);
            }
        }
    }
}