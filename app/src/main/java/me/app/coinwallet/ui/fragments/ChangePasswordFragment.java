package me.app.coinwallet.ui.fragments;

import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.textfield.TextInputEditText;
import me.app.coinwallet.R;

public class ChangePasswordFragment extends Fragment {

    private TextInputEditText newPassword;
    private TextInputEditText confirmPassword;
    private Button changeBtn;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newPassword = view.findViewById(R.id.new_password_text_field);
        confirmPassword = view.findViewById(R.id.confirm_password_text_field);
        changeBtn = view.findViewById(R.id.change_password_button);
    }
}