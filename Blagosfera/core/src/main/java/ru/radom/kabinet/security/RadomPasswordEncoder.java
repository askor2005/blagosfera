package ru.radom.kabinet.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.BasePasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// TODO заменить на BCryptPasswordEncoder
@Deprecated
@Transactional
@Component("radomPasswordEncoder")
public class RadomPasswordEncoder extends BasePasswordEncoder {

    // временно необходимо на период миграции со старого PasswordEncoder
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final String SHA_256 = "SHA-256";

	private String digest(final String algorithm, final String salt,
			final String string) {
		if (string == null) {
			return null;
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
		md.reset();
		try {
			if (salt != null) {
				md.update(salt.getBytes("UTF-8"));
			}
			return toHex(md.digest(string.getBytes("UTF-8")));
		} catch (final UnsupportedEncodingException e) {
			return null;
		}
	}

	private  String toHex(final byte[] bytes) {
		final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		final char[] chars = new char[bytes.length * 2];
		int j = 0;
		int k;
		for (final byte element : bytes) {
			k = element;
			chars[j++] = hexDigits[(k >>> 4) & 0x0F];
			chars[j++] = hexDigits[k & 0x0F];
		}
		return new String(chars);
	}

	public String hash(final String salt, final String string) {
		return digest(SHA_256, salt, string);
	}

	@Override
	public String encodePassword(String rawPass, Object salt) {
		return hash(salt != null ? salt.toString() : null, rawPass);
	}

	@Override
	public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
		boolean isPasswordValid = hash(salt != null ? salt.toString() : null, rawPass).equals(encPass);

        // TODO удалить после завершения миграции на новый PasswordEncoder
        if (isPasswordValid) userRepository.updateBCryptPassword(encPass, bCryptPasswordEncoder.encode(rawPass));

        return isPasswordValid;
	}
}
