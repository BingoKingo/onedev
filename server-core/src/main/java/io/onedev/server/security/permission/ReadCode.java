package io.onedev.server.security.permission;

import io.onedev.server.util.facade.UserFacade;
import org.apache.shiro.authz.Permission;
import org.jetbrains.annotations.Nullable;

public class ReadCode implements BasePermission {

	@Override
	public boolean implies(Permission p) {
		return p instanceof ReadCode || new AccessProject().implies(p);
	}

	@Override
	public boolean isApplicable(@Nullable UserFacade user) {
		return true;
	}
}
