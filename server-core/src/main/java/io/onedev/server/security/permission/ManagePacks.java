package io.onedev.server.security.permission;

import io.onedev.server.model.User;
import org.apache.shiro.authz.Permission;
import org.jetbrains.annotations.Nullable;

public class ManagePacks implements BasePermission {

	@Override
	public boolean implies(Permission p) {
		return p instanceof ManagePacks || new PackPermission("*", new WritePack()).implies(p);
	}

	@Override
	public boolean isApplicable(@Nullable User user) {
		return user != null && !user.isEffectiveGuest();
	}
	
}
