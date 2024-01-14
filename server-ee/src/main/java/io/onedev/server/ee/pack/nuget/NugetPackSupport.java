package io.onedev.server.ee.pack.nuget;

import io.onedev.server.OneDev;
import io.onedev.server.entitymanager.PackManager;
import io.onedev.server.model.Pack;
import io.onedev.server.model.Project;
import io.onedev.server.pack.PackSupport;
import org.apache.wicket.Component;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.servlet.http.HttpServletRequest;

public class NugetPackSupport implements PackSupport {
	
	public static final String TYPE = "NuGet";
	
	@Override
	public int getOrder() {
		return 250;
	}

	@Override
	public String getPackType() {
		return TYPE;
	}

	@Override
	public String getPackIcon() {
		return "nuget";
	}

	@Override
	public String getProjectSeparator() {
		return ":";
	}

	@Override
	public String getReference(Pack pack) {
		return pack.getName() + "-" + pack.getVersion();
	}

	@Override
	public Component renderContent(String componentId, Pack pack) {
		var packId = pack.getId();
		return new NugetPackPanel(componentId, new LoadableDetachableModel<>() {
			@Override
			protected Pack load() {
				return OneDev.getInstance(PackManager.class).load(packId);
			}

		});
	}

	@Override
	public Component renderHelp(String componentId, Project project) {
		return new NugetHelpPanel(componentId, project.getPath());
	}
	
}
