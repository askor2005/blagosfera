package ru.radom.kabinet.security;

import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;

@Deprecated
@Component("radomSaltSource")
public class RadomSaltSource implements SaltSource {

	@Override
	public Object getSalt(UserDetails user) {
        return ((UserDetailsImpl) user).getUser().getSalt();
	}
}
