package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import me.app.coinwallet.data.wallets.WalletInfoDao;
import me.app.coinwallet.data.wallets.WalletInfoDatabase;
import me.app.coinwallet.data.wallets.WalletInfoEntry;

import java.io.*;
import java.util.List;

public class InitPageViewModel extends AndroidViewModel {
    private final WalletInfoDao walletInfoDao;

    public InitPageViewModel(@NonNull final Application application) {
        super(application);
        walletInfoDao = WalletInfoDatabase.getDatabase(application.getApplicationContext()).walletInfoDao();
    }

    public LiveData<List<WalletInfoEntry>> getWalletInfos() {
        return walletInfoDao.getAll();
    }


    public void saveWalletInfo(String label){
        walletInfoDao.insertOrUpdate(new WalletInfoEntry(label,"BITCOIN"));
    }


    public String restoreMnemonic(){
        File file = new File(getApplication().getFilesDir(),"mnemonic");
        String mnemonic = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            mnemonic = convertStreamToString(fis);
            fis.close();
        } catch (FileNotFoundException ex){
            Log.e("HD","File not found "+ file.getName());
        } catch (IOException e){
            Log.e("HD","File read fail "+file.getName());
        }
        Log.e("HD","Restore mnemonic: "+ mnemonic);
        return mnemonic;
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
