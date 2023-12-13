package io.onedev.server.web.component.pack.choice;

import com.google.common.collect.Lists;
import io.onedev.server.OneDev;
import io.onedev.server.entitymanager.PackManager;
import io.onedev.server.model.Pack;
import io.onedev.server.web.component.select2.ChoiceProvider;
import org.hibernate.Hibernate;
import org.json.JSONException;
import org.json.JSONWriter;
import org.unbescape.html.HtmlEscape;

import java.util.Collection;
import java.util.List;

public abstract class PackChoiceProvider extends ChoiceProvider<Pack> {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void toJson(Pack choice, JSONWriter writer) throws JSONException {
		writer
			.key("id").value(choice.getId())
			.key("version").value(HtmlEscape.escapeHtml5(choice.getTag()));
	}

	@Override
	public Collection<Pack> toChoices(Collection<String> ids) {
		List<Pack> packs = Lists.newArrayList();
		for (String id: ids) {
			var pack = getPackManager().load(Long.valueOf(id)); 
			Hibernate.initialize(pack);
			packs.add(pack);
		}
		return packs;
	}
	
	private PackManager getPackManager() {
		return OneDev.getInstance(PackManager.class);
	}
	
}