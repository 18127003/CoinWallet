package me.app.coinwallet.viewmodels.factory;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.utils.BiometricUtil;

import java.lang.reflect.InvocationTargetException;

public class HomePageViewModelFactory implements ViewModelProvider.Factory {
    private final BiometricUtil biometricUtil;
    private final Application application;

    public HomePageViewModelFactory(Application application, BiometricUtil biometricUtil){
        this.application = application;
        this.biometricUtil = biometricUtil;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(Application.class, BiometricUtil.class).newInstance(application, biometricUtil);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            Log.e("HD", "Model view has no constructor with "+BiometricUtil.class.getSimpleName());
            e.printStackTrace();
        }
        return null;
    }
}
