package com.aiagent.framework;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.aiagent.framework.core.AIAgent;
import com.aiagent.framework.ui.ChatFragment;
import com.aiagent.framework.ui.CodeEditorFragment;
import com.aiagent.framework.ui.AnalyticsFragment;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AIAgent aiAgent;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        try {
            // Initialize AI Agent
            aiAgent = new AIAgent(this);
            
            setupViewPager();
            setupTabs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment(), "Chat");
        adapter.addFragment(new CodeEditorFragment(), "Code");
        adapter.addFragment(new AnalyticsFragment(), "Analytics");
        viewPager.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public AIAgent getAIAgent() {
        return aiAgent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aiAgent != null) {
            aiAgent.shutdown();
        }
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}