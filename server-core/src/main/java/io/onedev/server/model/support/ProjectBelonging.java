package io.onedev.server.model.support;

import io.onedev.server.model.AbstractEntity;
import io.onedev.server.model.Project;
import io.onedev.server.util.facade.ProjectBelongingFacade;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ProjectBelonging extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	public abstract Project getProject();
	
	@Override
	public ProjectBelongingFacade getOldVersion() {
		return (ProjectBelongingFacade) super.getOldVersion();
	}

	@Override
	public ProjectBelongingFacade getFacade() {
		return new ProjectBelongingFacade(getId(), getProject().getId());
	}
	
}
