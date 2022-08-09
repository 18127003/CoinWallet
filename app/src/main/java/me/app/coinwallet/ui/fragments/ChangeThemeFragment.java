package me.app.coinwallet.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.R;
import me.app.coinwallet.data.configuration.ConfigurationOption;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.adapters.ConfigurationOptionAdapter;
import me.app.coinwallet.viewmodels.SettingViewModel;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChangeThemeFragment extends Fragment {

    RecyclerView themes;
    Configuration configuration;

    public ChangeThemeFragment() {
        // Required empty public constructor
    }

    public static ChangeThemeFragment newInstance() {
        return new ChangeThemeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        configuration = ((BaseActivity) requireActivity()).configuration;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_theme, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        themes = view.findViewById(R.id.theme_option_list);
        ConfigurationOptionAdapter<Integer> adapter = new ConfigurationOptionAdapter<>(item -> {
            configuration.changeTheme(item.code);
            AppCompatDelegate.setDefaultNightMode(item.code);
        }, configuration.uiMode);
        adapter.update(configuration.getThemes());
        themes.setAdapter(adapter);
        themes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
}