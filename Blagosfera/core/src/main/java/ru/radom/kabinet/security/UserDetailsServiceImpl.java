package ru.radom.kabinet.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityPermissionRepository;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.model.Role;
import ru.radom.kabinet.model.UserEntity;

import java.util.List;

@Transactional(readOnly = true, noRollbackFor = UsernameNotFoundException.class)
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    public static final String ROLE_PREFIX = "ROLE_";

	@Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityPermissionRepository communityPermissionRepository;

    public UserDetailsServiceImpl() {
    }

	@Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findOneByEmail(username);

        if ((userEntity == null) || userEntity.isDeleted()) throw new UsernameNotFoundException("User '" + username + "' not found.");

        UserDetailsImpl userDetails = new UserDetailsImpl();
        List<Role> roles = userEntity.getRoles();
        List<String> permissions = communityPermissionRepository.findSecurityPermissions(userEntity.getId());
        List<SimpleGrantedAuthority> authorities = userDetails.getAuthorities();

        for(Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getName()));
        }

        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + permission));
        }

        userDetails.setUser(userEntity.toDomain());
        userDetails.setPassword(userEntity.getPassword());
        userDetails.setUsername(username);

        return userDetails;
    }
}
