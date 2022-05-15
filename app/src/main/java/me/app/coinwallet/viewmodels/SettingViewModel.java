package me.app.coinwallet.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import me.app.coinwallet.data.language.LanguageOption;

import java.util.ArrayList;
import java.util.List;

public class SettingViewModel extends AndroidViewModel {

    private final List<LanguageOption> languages;


    public SettingViewModel(@NonNull Application application) {
        super(application);
        languages = new ArrayList<>();
        languages.add(new LanguageOption(1, "English", "en"));
        languages.add(new LanguageOption(2, "Vietnam", "vn"));
    }

    public List<LanguageOption> getLanguages() {
        return languages;
    }

}
