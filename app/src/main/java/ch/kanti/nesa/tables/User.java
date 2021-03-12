package ch.kanti.nesa.tables;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Entity(tableName = "login_table")
public class User {

    private static SecretKeySpec secretKey;

    //column names
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String password;
    private String department;
    //initialize object
    public User(String username, String password, String department) {
        this.username = username;
        this.password = password;
        this.department = department;
    }
    //get id
    public int getId() {
        return id;
    }
    //set id
    public void setId(int id) {
        this.id = id;
    }
    //get username
    public String getUsername() {
        return username;
    }
    //set username
    public void setUsername(String username) {
        this.username = username;
    }
    //get password
    public String getPassword() {
        return password;
    }
    //set password
    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public static void setKey(String myKey){
        MessageDigest sha;
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt, String secret){
        try{
            setKey(secret);
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e){
            Log.d("decrypting", "Error while encrypting: " + e.toString());
        }
        return null;
    }

    //decrypt passed parameters
    public static String decrypt(String strToDecrypt, String secret){
        try {
            setKey(secret);
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e){
            Log.d("decrypting", "Error while decrypting: " + e.toString());
        }
        return null;
    }
}
