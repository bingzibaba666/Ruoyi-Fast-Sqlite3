package com.ruoyi.framework.security.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ruoyi.project.system.domain.SysUser;

public class AdminUserDetails  implements UserDetails {

	private SysUser user;
	private Set<String> permissionSet;
	

    public AdminUserDetails(SysUser user, Set<String> permissionList) {
        this.user = user;
        this.permissionSet = permissionList;
    }
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority simp=new SimpleGrantedAuthority(this.getPermissionSet().toString());
		return Arrays.asList(simp);
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return user.getDelFlag().equals("0")?false:true;
	}

	public SysUser getUser() {
		return user;
	}

	public void setUser(SysUser user) {
		this.user = user;
	}

	public Set<String> getPermissionSet() {
		return permissionSet;
	}

	public void setPermissionSet(Set<String> permissionSet) {
		this.permissionSet = permissionSet;
	}
	
}
