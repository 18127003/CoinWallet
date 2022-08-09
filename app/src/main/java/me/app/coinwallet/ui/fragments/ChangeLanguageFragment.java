package me.app.coinwallet.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.activities.HomeActivity;
import me.app.coinwallet.ui.adapters.ConfigurationOptionAdapter;
import me.app.coinwallet.utils.LocaleUtil;
import me.app.coinwallet.viewmodels.SettingViewModel;

public class ChangeLanguageFragment extends Fragment {

    private RecyclerView languageOptions;
    private Configuration configuration;

    public ChangeLanguageFragment() {
        // Required empty public constructor
    }

    public static ChangeLanguageFragment newInstance() {
        return new ChangeLanguageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_language, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        configuration = ((BaseActivity) requireActivity()).configuration;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        languageOptions = view.findViewById(R.id.language_option_list);
        ConfigurationOptionAdapter<String> adapter = new ConfigurationOptionAdapter<>(item -> {
            configuration.changeLanguage(item.code);
            LocaleUtil.setLocale(requireContext(), item.code);
            Intent intent = new Intent(getContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }, configuration.getSelectedLanguage());
        adapter.update(configuration.getLanguages());
        languageOptions.setAdapter(adapter);
        languageOptions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

}