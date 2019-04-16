package org.civis.blockchain.docstampr.api.rest.crypto;

import com.google.common.io.ByteStreams;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESCipher {

    public static final String ALGO = "AES";
    private static final Logger logger = LoggerFactory.getLogger(AESCipher.class);

    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        final KeyGenerator kg = KeyGenerator.getInstance(ALGO);
        kg.init(new SecureRandom(new byte[]{1, 2, 3}));
        return kg.generateKey();
    }

    public static SecretKey secretKeyFromBase64(String b64Key) {
        byte[] key = Base64.getDecoder().decode(b64Key);
        return new SecretKeySpec(key, ALGO);
    }

    public static void encrypt(SecretKey key, File file, OutputStream outputStream) throws CryptoException {
        FileInputStream fileInput = null;
        CipherOutputStream output = null;
        try {
            fileInput = new FileInputStream(file);
            output = getEncryptCipher(outputStream, key);

            ByteStreams.copy(fileInput, output);
        } catch (Exception e) {
            throw new CryptoException("Error encrypting file:" + file.getName(), e);
        } finally {
            closeQuietly(fileInput);
            closeQuietly(output);
        }
    }

    public static InputStream decrypt(String fileName, SecretKey key) {
        try {
            FileInputStream fileInput = new FileInputStream(fileName);
            return getDecryptCipher(key, fileInput);
        } catch (Exception e) {
            throw new CryptoException("Error decrypting file:" + fileName, e);
        }
    }

    private static CipherInputStream getDecryptCipher(SecretKey key, FileInputStream fileInput) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        final Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new CipherInputStream(fileInput, cipher);
    }

    private static CipherOutputStream getEncryptCipher(OutputStream fileOutput, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        final Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new CipherOutputStream(fileOutput, cipher);
    }

    private static void closeQuietly(@Nullable Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            logger.warn("IOException thrown while closing Closeable.", e);
        }
    }

}
