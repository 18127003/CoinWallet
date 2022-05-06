package me.app.coinwallet.utils;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Base64;
import android.util.Log;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class CryptoEngine {
    private static final String KEY_GEN_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEYSTORE_WORKAROUND_CIPHER_PROVIDER = "AndroidKeyStoreBCWorkaround";
    private static final String SALT = "secretSalt";
    private static final String AES_MODE = "AES/CBC/PKCS7Padding";
    private static CryptoEngine _instance = null;
    private static final int IV_LENGTH = 16;
    private KeyStore keyStore;

    public static synchronized CryptoEngine getInstance(){
        if(_instance == null){
            _instance = new CryptoEngine();
        }
        return _instance;
    }

    private CryptoEngine(){
        try{
            keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            keyStore.load(null);
        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException ioException) {
            ioException.printStackTrace();
        }
    }

    public String cipher(String alias, String input) {
        Key key;
        try{
            keyStore.deleteEntry(alias);
            if(!keyStore.containsAlias(alias)){
                Log.e("HD","Keystore not contains alias, generating secret key with key generator");
                key = generateKeyAndroidM(alias);

            } else {
                Log.e("HD","Keystore contains alias, get secret key from key store");
                key = keyStore.getKey(alias, null);
            }

            byte[] encrypted = encrypt(key, input.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);

        } catch (UserNotAuthenticatedException e) {
            Log.e("HD","Unauthorized");
        } catch (InvalidAlgorithmParameterException e) {
            Log.e("HD","Invalid algo param");
        } catch (UnrecoverableKeyException e) {
            Log.e("HD","unrecoverable key");
        } catch (NoSuchPaddingException e) {
            Log.e("HD","no such padding");
        } catch (IllegalBlockSizeException e) {
            Log.e("HD","illegal block size");
        } catch (KeyStoreException e) {
            e.printStackTrace();
            Log.e("HD","key store exception");
        } catch (NoSuchAlgorithmException e) {
            Log.e("HD","no such algo");
        } catch (BadPaddingException e) {
            Log.e("HD","bad padding");
        } catch (NoSuchProviderException e) {
            Log.e("HD","no such provider");
        } catch (InvalidKeyException e){
            Log.e("HD", "Invalid key");
        }
        return null;
    }

    public String decipher(String alias, String input) {
        try{
            if(keyStore.containsAlias(alias)){
                Key key = keyStore.getKey(alias, null);
                byte[] decrypted = decrypt(key, Base64.decode(input, Base64.DEFAULT));
                return new String(decrypted);
            }
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                BadPaddingException | InvalidKeyException | NoSuchProviderException | UnrecoverableKeyException |
                KeyStoreException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SecretKey generateKeyAndroidM(String alias) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        )
                .setKeySize(256)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setRandomizedEncryptionRequired(true)
                .setUserAuthenticationRequired(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            builder.setUserAuthenticationParameters(10, KeyProperties.AUTH_DEVICE_CREDENTIAL | KeyProperties.AUTH_BIOMETRIC_STRONG);
        } else {
            builder.setUserAuthenticationValidityDurationSeconds(10);
        }
        keyGenerator.init(builder.build());
        return keyGenerator.generateKey();
    }

    private byte[] encrypt(Key secretKey, byte[] input) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
            NoSuchProviderException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance(AES_MODE, KEYSTORE_WORKAROUND_CIPHER_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] iv = cipher.getIV();
        byte[] encryptedInput = cipher.doFinal(input);
        byte[] result = new byte[encryptedInput.length + iv.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encryptedInput, 0, result, iv.length, encryptedInput.length);
        return result;
    }

    private byte[] decrypt(Key secretKey, byte[] input) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            NoSuchProviderException {

        Cipher cipher = Cipher.getInstance(AES_MODE, KEYSTORE_WORKAROUND_CIPHER_PROVIDER);
        byte[] ivByte = Arrays.copyOfRange(input, 0, IV_LENGTH);
        IvParameterSpec iv = new IvParameterSpec(ivByte);
        byte[] encryptedInput = Arrays.copyOfRange(input, IV_LENGTH, input.length);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return cipher.doFinal(encryptedInput);
    }

    /***
     * To be used for Android pre M
     * ***/
    private static SecretKey getKeyFromPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_GEN_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
    }

    public static byte[] generateSalt(){
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
