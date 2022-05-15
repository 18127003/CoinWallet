package me.app.coinwallet.ui.fragments;

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
import me.app.coinwallet.R;
import me.app.coinwallet.data.language.LanguageOption;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.adapters.LanguageOptionAdapter;
import me.app.coinwallet.viewmodels.SettingViewModel;

public class ChangeLanguageFragment extends Fragment implements BaseAdapter.OnItemClickListener<LanguageOption> {

    private RecyclerView languageOptions;
    private SettingViewModel viewModel;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SettingViewModel.class);
        languageOptions = view.findViewById(R.id.language_option_list);
        LanguageOptionAdapter adapter = new LanguageOptionAdapter(this);
        adapter.update(viewModel.getLanguages());
        languageOptions.setAdapter(adapter);
        languageOptions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onClick(LanguageOption item) {

    }
}