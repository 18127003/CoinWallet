package me.app.coinwallet.activities;

import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.R;
import me.app.coinwallet.viewmodels.EncryptPageViewModel;

/***
 * for test purpose only
 */
public class EncryptActivity extends AppCompatActivity {

    Button extractButton;
    Button restoreButton;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        final EncryptPageViewModel viewModel = new ViewModelProvider(this)
                .get(EncryptPageViewModel.class);
        extractButton = findViewById(R.id.extract_btn);
        password = findViewById(R.id.password_text);
        restoreButton = findViewById(R.id.restore_btn);
    }
}