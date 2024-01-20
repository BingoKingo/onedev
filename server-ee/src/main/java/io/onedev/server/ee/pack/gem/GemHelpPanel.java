package io.onedev.server.ee.pack.gem;

import io.onedev.server.OneDev;
import io.onedev.server.entitymanager.SettingManager;
import io.onedev.server.web.component.codesnippet.CodeSnippetPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import static io.onedev.server.ee.pack.gem.GemPackService.SERVICE_ID;

public class GemHelpPanel extends Panel {
	
	private final String projectPath;
	
	public GemHelpPanel(String id, String projectPath) {
		super(id);
		this.projectPath = projectPath;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		var registryUrl = getServerUrl() + "/" + projectPath + "/~" + SERVICE_ID;
		var addSourceCommands = "" +
				"---\n" +
				registryUrl + ": Bearer <onedev_access_token>"; 
		add(new CodeSnippetPanel("addSource", Model.of(addSourceCommands)));
		
		var pushCommand = "gem push --host " + registryUrl + " /path/to/<package>-<version>.gem";
		add(new CodeSnippetPanel("pushCommand", Model.of(pushCommand)));

		var jobCommands = "" +
				"mkdir -p $HOME/.gem\n" +
				"\n" +
				"# Use job token to tell OneDev the build publishing the package\n" +
				"# Job secret 'access-token' should be defined in project build setting as an access token with package write permission\n\n" +
				"cat << EOF > $HOME/.gem/credentials\n" +
				"---\n" +
				registryUrl + ": Bearer @job_token@:@secret:access-token@\n" +
				"EOF\n" +
				"\n" +
				"chmod 0600 $HOME/.gem/credentials";
		
		add(new CodeSnippetPanel("jobCommands", Model.of(jobCommands)));
	}

	private String getServerUrl() {
		return OneDev.getInstance(SettingManager.class).getSystemSetting().getServerUrl();
	}
	
}
