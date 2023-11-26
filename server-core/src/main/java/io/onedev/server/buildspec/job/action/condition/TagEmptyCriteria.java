package io.onedev.server.buildspec.job.action.condition;

import io.onedev.server.model.Build;
import io.onedev.server.util.criteria.Criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import static io.onedev.server.buildspec.job.action.condition.ActionCondition.getRuleName;
import static io.onedev.server.model.Build.NAME_TAG;

public class TagEmptyCriteria extends Criteria<Build> {

	private static final long serialVersionUID = 1L;
	
	private final int operator;
	
	public TagEmptyCriteria(int operator) {
		this.operator = operator;
	}
	
	@Override
	public Predicate getPredicate(CriteriaQuery<?> query, From<Build, Build> from, CriteriaBuilder builder) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean matches(Build build) {
		var matches = build.getTag() == null;
		if (operator == ActionConditionLexer.IsNotEmpty)
			matches = !matches;
		return matches;
	}

	@Override
	public String toStringWithoutParens() {
		return quote(NAME_TAG) + " " + getRuleName(operator);
	}
	
}
