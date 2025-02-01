package io.onedev.server.web.component.createtag;

import java.io.Serializable;

import javax.validation.ConstraintValidatorContext;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import javax.validation.constraints.NotEmpty;

import io.onedev.server.validation.Validatable;
import io.onedev.server.annotation.ClassValidating;
import io.onedev.server.annotation.Editable;
import io.onedev.server.annotation.Multiline;
import io.onedev.server.annotation.OmitName;

@Editable
@ClassValidating
public class CreateTagBean implements Validatable, Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	
	private String message;
	
	@Editable(order=100, name="Tag Name")
	@NotEmpty
	@OmitName
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Editable(order=200, name="Tag Message")
	@Multiline
	@OmitName
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean isValid(ConstraintValidatorContext context) {
		if (!Repository.isValidRefName(Constants.R_TAGS + getName())) {
            context.buildConstraintViolationWithTemplate("Invalid tag name")
		            .addPropertyNode("name").addConstraintViolation()
		            .disableDefaultConstraintViolation();
            return false;
		} else {
			return true;
		}
	}
	
}
