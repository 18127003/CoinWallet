package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.data.language.LanguageOption;
import me.app.coinwallet.utils.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

public class SettingViewModel extends AndroidViewModel {

    private final List<LanguageOption> languages;
    private final LocalWallet localWallet;


    public SettingViewModel(@NonNull Application application) {
        super(application);
        localWallet = LocalWallet.getInstance();
        languages = new ArrayList<>();
        languages.add(new LanguageOption(1, "English", "en"));
        languages.add(new LanguageOption(2, "Vietnam", "vi"));
    }

    public List<LanguageOption> getLanguages() {
        return languages;
    }

    public void changeLanguage(String languageCode, OnConfigurationChange callback){
        LocaleUtil.setLocale(getApplication().getApplicationContext(), languageCode);
        callback.onLocaleChange();
    }

    public interface OnConfigurationChange{
        void onLocaleChange();
    }

    public void logout(){
        AsyncTask.execute(localWallet::stopWallet);
    }

    public boolean checkPassword(String password){
        try{
            return localWallet.checkPassword(password);
        } catch (IllegalStateException e){
            localWallet.encryptWallet(password);
            return true;
        }
    }
}
