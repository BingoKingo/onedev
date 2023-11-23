package io.onedev.server.search.entity.issue;

import io.onedev.server.model.Issue;
import io.onedev.server.util.DateUtils;
import io.onedev.server.util.criteria.Criteria;

import javax.persistence.criteria.*;

import static io.onedev.server.model.Issue.NAME_SPENT_TIME;
import static io.onedev.server.model.Issue.PROP_TOTAL_SPENT_TIME;


public class SpentTimeCriteria extends Criteria<Issue> {

	private static final long serialVersionUID = 1L;

	private final int value;
	
	private final int operator;
	
	public SpentTimeCriteria(int value, int operator) {
		this.value = value;
		this.operator = operator;
	}

	@Override
	public Predicate getPredicate(CriteriaQuery<?> query, From<Issue, Issue> from, CriteriaBuilder builder) {
		Path<Integer> spentTimeAttribute = from.get(PROP_TOTAL_SPENT_TIME);
		if (operator == IssueQueryLexer.Is)
			return builder.equal(spentTimeAttribute, value);
		else if (operator == IssueQueryLexer.IsNot)
			return builder.not(builder.equal(spentTimeAttribute, value));
		else if (operator == IssueQueryLexer.IsGreaterThan)
			return builder.greaterThan(spentTimeAttribute, value);
		else
			return builder.lessThan(spentTimeAttribute, value);
	}

	@Override
	public boolean matches(Issue issue) {
		if (operator == IssueQueryLexer.Is)
			return issue.getTotalSpentTime() == value;
		else if (operator == IssueQueryLexer.IsNot)
			return issue.getTotalSpentTime() != value;			
		else if (operator == IssueQueryLexer.IsGreaterThan)
			return issue.getTotalSpentTime() > value;
		else
			return issue.getTotalSpentTime() < value;
	}

	@Override
	public String toStringWithoutParens() {
		return quote(NAME_SPENT_TIME) + " "
				+ IssueQuery.getRuleName(operator) + " "
				+ quote(DateUtils.formatWorkingPeriod(value));
	}

	@Override
	public void fill(Issue issue) {
	}

}
