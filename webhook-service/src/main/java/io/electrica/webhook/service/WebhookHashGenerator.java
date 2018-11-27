package io.electrica.webhook.service;

import io.electrica.common.helper.StringUtils;
import io.electrica.webhook.model.Webhook;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/*
 * WebhookHashGenerator uses  MD5 Message-Digest Algorithm hashing algorithm that produces a 128-bit (16-byte)
 * hash value. We have used salt as random data that used as an additional input to a one-way function
 * that hashes a pass-phrase. The SecureRandom class supports the “SHA1PRNG” pseudo random number generator algorithm.
 */
@Component
public class WebhookHashGenerator {

    public String generateHash(Webhook entity) {
        StringBuilder inputString = new StringBuilder();
        inputString.append(entity.getConnectorId()).append(StringUtils.COLON).append(entity.getConnectorId())
                .append(StringUtils.COLON).append(entity.getOrganizationId()).append(StringUtils.COLON)
                .append(entity.getUserId());
        try {
            return getSecurePassword(inputString.toString(), getSalt());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private String getSecurePassword(String passwordToHash, byte[] salt) throws NoSuchAlgorithmException {
        String generatedPassword = null;
        MessageDigest md = MessageDigest.getInstance("MD5");
        //Add password bytes to digest
        md.update(salt);
        //Get the hash's bytes
        byte[] bytes = md.digest(passwordToHash.getBytes(Charset.forName("UTF-8")));
        //This bytes[] has bytes in decimal format.Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        generatedPassword = sb.toString();
        return generatedPassword;
    }
}
