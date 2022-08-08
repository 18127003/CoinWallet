package me.app.coinwallet.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.activities.InitActivity;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.dialogs.ConfirmDialog;
import me.app.coinwallet.ui.dialogs.SingleTextFieldDialog;
import me.app.coinwallet.viewmodels.SettingViewModel;

/***
 * Do not pass data through fragment constructor, use View Model to share data instead.
 */
public class SettingFragment extends Fragment {

    private MaterialCardView walletManage;
    private MaterialCardView mnemonic;
    private MaterialCardView changePassword;
    private SwitchMaterial fingerprintEnable;
    private MaterialCardView changeLanguage;
    private MaterialCardView about;
    private MaterialCardView logout;
    private SettingViewModel viewModel;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SettingViewModel.class);
        walletManage = view.findViewById(R.id.wallet_manage_card);
        mnemonic = view.findViewById(R.id.mnemonic_card);
        changePassword = view.findViewById(R.id.change_password_card);
        fingerprintEnable = view.findViewById(R.id.fingerprint);
        changeLanguage = view.findViewById(R.id.change_language_card);
        about = view.findViewById(R.id.about_card);
        changePassword.setOnClickListener(v-> moveToSettingSection(ChangePasswordFragment.class, R.string.change_password_page_label));
        changeLanguage.setOnClickListener(v-> moveToSettingSection(ChangeLanguageFragment.class, R.string.change_language_page_label));
        fingerprintEnable.setChecked(((BaseActivity)requireActivity()).configuration.isFingerprintEnabled());
        fingerprintEnable.setOnCheckedChangeListener((v, isChecked)->
                ((BaseActivity) requireActivity()).configuration.setFingerprintEnabled(isChecked));
        logout = view.findViewById(R.id.logout_card);
        logout.setOnClickListener(v -> logout());
        mnemonic.setOnClickListener(v->moveToSettingSection(MnemonicFragment.class, R.string.mnemonic));
        walletManage.setOnClickListener(v->moveToSettingSection(WalletManageFragment.class, R.string.manage_wallet_page_label));
    }

    private void logout(){
        viewModel.logout();
        Intent intent = new Intent(getContext(), InitActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void moveToSettingSection(Class<? extends Fragment> fragment, @StringRes int title){
        ((BaseActivity) requireActivity()).loadFragmentOut(fragment, title);
    }
}