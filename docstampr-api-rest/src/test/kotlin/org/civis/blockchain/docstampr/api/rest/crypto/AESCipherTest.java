package org.civis.blockchain.docstampr.api.rest.crypto;

import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import org.civis.blockchain.ssm.client.Utils.FileUtils;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class AESCipherTest {

    @Test
    void encrypt() throws IOException {
        File fileToEncrypt = getFile("fileToCommit.txt");
        File encryptedFile = File.createTempFile("enc_", "tmp");
        File encryptedFileProof = getFile("fileToCommit.encrypted");
        try {
            OutputStream os = new FileOutputStream(encryptedFile);
            SecretKey key = AESCipher.secretKeyFromBase64("+cRaRuaSK1/RObE9oEOm6Q==");
            AESCipher.encrypt(key, fileToEncrypt, os);

            boolean areFileEquals = Files.equal(encryptedFile, encryptedFileProof);
            assertThat(areFileEquals).isTrue();
        } finally {
            encryptedFile.delete();
        }
    }

    @Test
    void decrypr() throws IOException, NoSuchAlgorithmException {
        File encryptedFile = getFile("fileToCommit.encrypted");
        InputStream decryptedStream = null;
        try {
            SecretKey key = AESCipher.secretKeyFromBase64("+cRaRuaSK1/RObE9oEOm6Q==");

            decryptedStream = AESCipher.decrypt(encryptedFile.getAbsolutePath(), key);
            String value = CharStreams.toString(new InputStreamReader(decryptedStream));
            assertThat(value).isEqualTo("to commit");
        } finally {
            encryptedFile.delete();
            decryptedStream.close();
        }
    }

    @Test
    void generateSecretKey() throws NoSuchAlgorithmException {
        SecretKey key = AESCipher.generateSecretKey();
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println(encodedKey);
        SecretKey keyBuilded = AESCipher.secretKeyFromBase64(encodedKey);
        assertThat(key).isEqualToComparingFieldByField(keyBuilded);
    }

    public static File getFile(String filename) throws MalformedURLException {
        URL url = FileUtils.getUrl(filename);
        return new File(url.getFile());
    }
}