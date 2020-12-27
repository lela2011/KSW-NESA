package com.example.nesa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nesa.databinding.ActivityMainBinding;
import com.example.nesa.fragments.AbsencesFragment;
import com.example.nesa.fragments.AccountFragment;
import com.example.nesa.fragments.GradesFragment;
import com.example.nesa.fragments.HomeFragment;
import com.example.nesa.fragments.SettingsFragment;
import com.example.nesa.scrapers.ContentScrapers;
import com.example.nesa.tables.AccountInfo;
import com.example.nesa.tables.BankStatement;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

    public class MainActivity extends AppCompatActivity {

        public ActivityMainBinding binding;
        public static String username;
        public static String password;
        public static ViewModel viewModel;

        Document mainPage, markPage, absencesPage, bankPage;

        List<AccountInfo> oldInfo = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);

            BottomNavigationView bottomNav = binding.bottomNavigation;
            bottomNav.setOnNavigationItemSelectedListener(navListener);

            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
            }

            viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(ViewModel.class);

            if(SplashActivity.netWorkAvailable) {
                mainPage = SplashActivity.mainPage;
                markPage = SplashActivity.markPage;
                absencesPage = SplashActivity.absencesPage;
                bankPage = SplashActivity.bankPage;
                initializeScraping();
            }

            username = SplashActivity.username;
            password = SplashActivity.password;
        }

        @SuppressLint("NonConstantResourceId")
        public final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_grades:
                    selectedFragment = new GradesFragment();
                    break;
                case R.id.nav_absences:
                    selectedFragment = new AbsencesFragment();
                    break;
                case R.id.nav_account:
                    selectedFragment = new AccountFragment();
                    break;
                case R.id.nav_settings:
                    selectedFragment = new SettingsFragment();
                    break;

            }

            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();

            return true;
        };

        private void initializeScraping() {
            scrapeMain();
            scrapeAccount();
        }

        private void scrapeMain() {
            ArrayList<AccountInfo> info = ContentScrapers.scrapeMain(mainPage);
            viewModel.getAccountInfo().observe(this, accountInfo -> {
                if (accountInfo.size() == 8) {
                    List<AccountInfo> infoList = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        int id = accountInfo.get(i).getId();
                        AccountInfo updatedEntry = new AccountInfo(info.get(i).value, info.get(i).order);
                        updatedEntry.setId(id);
                        infoList.add(updatedEntry);
                    }
                    if (!compareLists(oldInfo, infoList)) {
                        viewModel.updateInfo(infoList);
                        oldInfo = infoList;
                    }
                } else if (accountInfo.size() == 0) {
                    List<AccountInfo> infoList = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        AccountInfo newEntry = new AccountInfo(info.get(i).value, info.get(i).order);
                        infoList.add(newEntry);
                    }
                    viewModel.insertInfo(infoList);
                }
            });
        }

        private void scrapeMarks() {

        }

        private void scrapeAbsences() {

        }

        private void scrapeAccount() {
            ArrayList<BankStatement> debits = ContentScrapers.scrapeAccount(bankPage);
            viewModel.getBankStatements().observe(this, statements -> {
                if (statements.size() == 0) {
                    viewModel.insertAllBank(debits);
                } else if (statements.size() != debits.size()) {
                    if (statements.size() < debits.size()) {
                        List<BankStatement> updatedStatements = debits.subList(statements.size(), debits.size());
                        viewModel.insertAllBank(updatedStatements);
                    } else {
                        viewModel.deleteAllBank();
                        viewModel.insertAllBank(debits);
                    }
                }
            });
        }

        private boolean compareLists(List<AccountInfo> oldInfo, List<AccountInfo> newInfo) {
            if (oldInfo.size() == newInfo.size()) {
                for (int i = 0; i < oldInfo.size(); i++) {
                    if (!(oldInfo.get(i).order == newInfo.get(i).order && oldInfo.get(i).value.equals(newInfo.get(i).value))) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }