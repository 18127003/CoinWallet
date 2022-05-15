package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.dialogs.ConfirmDialog;
import me.app.coinwallet.ui.dialogs.SingleTextFieldDialog;
import me.app.coinwallet.viewmodels.SettingViewModel;

/***
 * Do not pass data through fragment constructor, use View Model to share data instead.
 */
public class SettingFragment extends Fragment {

    private ImageButton walletManage;
    private ImageButton changePassword;
    private SwitchMaterial fingerprintEnable;
    private ImageButton changeLanguage;
    private ImageButton about;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SettingViewModel.class);
        walletManage = view.findViewById(R.id.manage_wallet);
        changePassword = view.findViewById(R.id.change_password);
        fingerprintEnable = view.findViewById(R.id.fingerprint);
        changeLanguage = view.findViewById(R.id.change_language);
        about = view.findViewById(R.id.about);
        changePassword.setOnClickListener(v-> accessPasswordDialog());
        changeLanguage.setOnClickListener(v-> moveToSettingSection(ChangeLanguageFragment.class, "Change Language"));
    }

    private void moveToSettingSection(Class<? extends Fragment> fragment, String title){
        Intent intent = new Intent(getContext(), SingleFragmentActivity.class);
        intent.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME, fragment);
        intent.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME, title);
        startActivity(intent);
    }

    private void accessPasswordDialog(){
        ConfirmDialog dialog = SingleTextFieldDialog.passwordDialog(getLayoutInflater(),
                new SingleTextFieldDialog.DialogListener() {
                    @Override
                    public void onConfirm(String text) {
                        if(viewModel.checkPassword(text)){
                            moveToSettingSection(ChangePasswordFragment.class, "Change Password");
                        }
                    }

                    @Override
                    public void onCancel() { }
                });
        dialog.show(requireActivity().getSupportFragmentManager(), "password_access");
    }
}