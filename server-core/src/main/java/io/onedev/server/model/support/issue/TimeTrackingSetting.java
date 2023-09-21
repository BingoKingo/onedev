package io.onedev.server.model.support.issue;

import io.onedev.server.OneDev;
import io.onedev.server.annotation.ChoiceProvider;
import io.onedev.server.annotation.Editable;
import io.onedev.server.entitymanager.LinkSpecManager;
import io.onedev.server.entitymanager.SettingManager;
import io.onedev.server.model.support.administration.GlobalIssueSetting;
import io.onedev.server.util.usage.Usage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Editable
public class TimeTrackingSetting implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String timeAggregationLink;
	
	@Editable(order=500, placeholder = "No aggregation")
	@ChoiceProvider("getLinkChoices")
	public String getTimeAggregationLink() {
		return timeAggregationLink;
	}

	public void setTimeAggregationLink(String timeAggregationLink) {
		this.timeAggregationLink = timeAggregationLink;
	}
	
	private static List<String> getLinkChoices() {
		var choices = new LinkedHashSet<String>();
		for (var linkSpec: OneDev.getInstance(LinkSpecManager.class).query()) {
			if (linkSpec.getOpposite() != null) {
				choices.add(linkSpec.getName());
				choices.add(linkSpec.getOpposite().getName());
			}
		}
		return new ArrayList<>(choices);
	}

	private static GlobalIssueSetting getIssueSetting() {
		return OneDev.getInstance(SettingManager.class).getIssueSetting();
	}

	public Usage onDeleteLink(String linkName) {
		Usage usage = new Usage();
		if (linkName.equals(timeAggregationLink))
			usage.add("time aggregation link");
		return usage;
	}

	public void onRenameLink(String oldName, String newName) {
		if (oldName.equals(timeAggregationLink))
			timeAggregationLink = newName;
	}
	
}
