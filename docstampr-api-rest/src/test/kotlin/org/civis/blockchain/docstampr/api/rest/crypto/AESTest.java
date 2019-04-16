package org.civis.blockchain.docstampr.api.rest.crypto;

import org.junit.Test;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESTest {

    @Test
    public void whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] content = "TestTest".getBytes();

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        int keySize = 256;
        kgen.init(keySize);
        SecretKey key = kgen.generateKey();
        byte[] aesKey = key.getEncoded();
        SecretKeySpec aesKeySpec = new SecretKeySpec(aesKey, "AES");
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
        byte[] encryptedContent = aesCipher.doFinal(content);

        System.out.println(Base64.getEncoder().encode(encryptedContent));
    }

    @Test
    public void runExperiments() {

        CipherOutputStream output = null;
        CipherInputStream input = null;

        FileOutputStream fileOutput = null;
        FileInputStream fileInput = null;

        try {
            fileOutput = new FileOutputStream("CipherOutput.txt");



            output = getEncryptCipher(output, fileOutput);

            final PrintWriter pw = new PrintWriter(output);
            pw.println("Cipher Streams are working correctly.");
            pw.flush();
            pw.close();

            fileInput = new FileInputStream("CipherOutput.txt");
            input = getDecryptCipher(input, fileInput);

            final InputStreamReader r = new InputStreamReader(input);
            final BufferedReader reader = new BufferedReader(r);
            final String line = reader.readLine();
            System.out.println("Line : " + line);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Specified Algorithm does not exist");
        } catch (NoSuchPaddingException e) {
            System.out.println("Specified Padding does not exist");
        } catch (FileNotFoundException e) {
            System.out.println("Could not find specified file to read / write to");
        } catch (InvalidKeyException e) {
            System.out.println("Specified key is invalid");
        } catch (IOException e) {
            System.out.println("IOException from BufferedReader when reading file");
        } finally {
            try {
                if (fileInput != null) {
                    fileInput.close();
                }
                if (fileOutput != null) {
                    fileOutput.flush();
                    fileOutput.close();
                }
                if (output != null) {
                    output.flush();
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private CipherInputStream getDecryptCipher(CipherInputStream input, FileInputStream fileInput) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        final SecretKey key2 = generateSecretKey();
        final Cipher c2 = Cipher.getInstance("AES");
        c2.init(Cipher.DECRYPT_MODE, key2);
        input = new CipherInputStream(fileInput, c2);
        return input;
    }

    private CipherOutputStream getEncryptCipher(CipherOutputStream output, FileOutputStream fileOutput) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        final SecretKey key = generateSecretKey();
        final Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        output = new CipherOutputStream(fileOutput, c);
        return output;
    }

    private SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        final KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(new SecureRandom(new byte[]{1, 2, 3}));
        return kg.generateKey();
    }

}
