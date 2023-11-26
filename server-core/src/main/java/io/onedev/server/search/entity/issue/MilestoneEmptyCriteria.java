package io.onedev.server.search.entity.issue;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import io.onedev.server.model.Issue;
import io.onedev.server.model.IssueSchedule;
import io.onedev.server.util.criteria.Criteria;

public class MilestoneEmptyCriteria extends Criteria<Issue> {

	private static final long serialVersionUID = 1L;
	
	private final int operator;
	
	public MilestoneEmptyCriteria(int operator) {
		this.operator = operator;
	}
	
	@Override
	public Predicate getPredicate(CriteriaQuery<?> query, From<Issue, Issue> from, CriteriaBuilder builder) {
		var predicate = builder.isEmpty(from.get(Issue.PROP_SCHEDULES));
		if (operator == IssueQueryLexer.IsNotEmpty)
			predicate = builder.not(predicate);
		return predicate;
	}

	@Override
	public boolean matches(Issue issue) {
		var matches = issue.getSchedules().isEmpty();
		if (operator == IssueQueryLexer.IsNotEmpty)
			matches = !matches;
		return matches;
	}

	@Override
	public String toStringWithoutParens() {
		return quote(IssueSchedule.NAME_MILESTONE) + " " + IssueQuery.getRuleName(operator);
	}

}
