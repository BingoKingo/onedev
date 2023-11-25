package io.onedev.server.buildspec.step;

import io.onedev.agent.BuiltInRegistryLogin;
import io.onedev.commons.codeassist.InputSuggestion;
import io.onedev.commons.utils.StringUtils;
import io.onedev.k8shelper.CommandFacade;
import io.onedev.server.OneDev;
import io.onedev.server.annotation.*;
import io.onedev.server.buildspec.BuildSpec;
import io.onedev.server.buildspec.step.commandinterpreter.DefaultInterpreter;
import io.onedev.server.buildspec.step.commandinterpreter.Interpreter;
import io.onedev.server.entitymanager.SettingManager;
import io.onedev.server.model.support.administration.jobexecutor.RegistryLoginAware;
import io.onedev.server.model.support.administration.jobexecutor.JobExecutor;
import io.onedev.server.model.support.administration.jobexecutor.RegistryLogin;
import io.onedev.server.util.UrlUtils;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

import static io.onedev.agent.DockerExecutorUtils.buildDockerConfig;
import static java.util.stream.Collectors.toList;

@Editable(order=200, name="Build Docker Image (Kaniko)", description="Build and publish docker image with Kaniko. " +
		"This step can be executed by server docker executor, remote docker executor, or Kubernetes executor, " +
		"without the need to mount docker sock")
public class BuildImageWithKanikoStep extends CommandStep {

	private static final long serialVersionUID = 1L;

	private String buildContext;
	
	private String destinations;
	
	private String trustCertificates;
	
	private String moreOptions;

	@Editable
	@Override
	public boolean isRunInContainer() {
		return true;
	}

	@Editable
	@Override
	public String getImage() {
		return "1dev/kaniko:1.0.2";
	}

	@Override
	public boolean isUseTTY() {
		return true;
	}
	
	@Editable(order=100, description="Optionally specify build context path relative to <a href='https://docs.onedev.io/concepts#job-workspace' target='_blank'>job workspace</a>. "
			+ "Leave empty to use job workspace itself. The file <code>Dockerfile</code> is expected to exist in build context " +
			"directory, unless you specify a different location with option <code>--dockerfile</code>")
	@Interpolative(variableSuggester="suggestVariables")
	@SafePath
	public String getBuildContext() {
		return buildContext;
	}

	public void setBuildContext(String buildContext) {
		this.buildContext = buildContext;
	}

	@Editable(order=300, description="Specify destinations, for instance <tt>myorg/myrepo:latest</tt>, "
			+ "<tt>myorg/myrepo:1.0.0</tt>, or <tt>myregistry:5000/myorg/myrepo:1.0.0</tt>. "
			+ "Multiple destinations should be separated with space.<br>")
	@Interpolative(variableSuggester="suggestVariables")
	@NotEmpty
	public String getDestinations() {
		return destinations;
	}

	public void setDestinations(String destinations) {
		this.destinations = destinations;
	}
	
	@Editable(order=325, name="Certificates to Trust", placeholder = "Base64 encoded PEM format, starting with " +
			"-----BEGIN CERTIFICATE----- and ending with -----END CERTIFICATE-----",
			description = "Specify certificates to trust if you are using self-signed certificates for your docker registries")
	@Multiline(monospace = true)
	@Interpolative(variableSuggester="suggestVariables")
	public String getTrustCertificates() {
		return trustCertificates;
	}

	public void setTrustCertificates(String trustCertificates) {
		this.trustCertificates = trustCertificates;
	}

	@Editable(order=340, name="Built-in Registry Access Token Secret", descriptionProvider = "getBuiltInRegistryAccessTokenSecretDescription")
	@ChoiceProvider("getAccessTokenSecretChoices")
	@Password
	@Override
	public String getBuiltInRegistryAccessTokenSecret() {
		return super.getBuiltInRegistryAccessTokenSecret();
	}

	@Override
	public void setBuiltInRegistryAccessTokenSecret(String builtInRegistryAccessTokenSecret) {
		super.setBuiltInRegistryAccessTokenSecret(builtInRegistryAccessTokenSecret);
	}

	private static String getBuiltInRegistryAccessTokenSecretDescription() {
		var serverUrl = OneDev.getInstance(SettingManager.class).getSystemSetting().getServerUrl();
		var server = UrlUtils.getServer(serverUrl);
		return "Optionally specify a secret to be used as access token for built-in registry server " +
				"<code>" + server + "</code>";
	}
	
	@Editable(order=350, description="Optionally specify additional options to build image, " +
			"separated by spaces")
	@Interpolative(variableSuggester="suggestVariables")
	@ReservedOptions({"(--context)=.*", "(--destination)=.*"})
	public String getMoreOptions() {
		return moreOptions;
	}

	public void setMoreOptions(String moreOptions) {
		this.moreOptions = moreOptions;
	}

	static List<InputSuggestion> suggestVariables(String matchWith) {
		return BuildSpec.suggestVariables(matchWith, true, true, false);
	}

	@Override
	public Interpreter getInterpreter() {
		return new DefaultInterpreter() {
			
			@Override
			public CommandFacade getExecutable(JobExecutor jobExecutor, String jobToken, String image, 
											   String builtInRegistryAccessToken, boolean useTTY) {
				var commands = new ArrayList<String>();
				if (jobExecutor instanceof RegistryLoginAware) {
					RegistryLoginAware registryLoginAware = (RegistryLoginAware) jobExecutor;
					commands.add("cat <<EOF>> /kaniko/.docker/config.json");
					var registryLogins = registryLoginAware.getRegistryLogins().stream().map(RegistryLogin::getFacade).collect(toList());
					var builtInRegistryUrl = OneDev.getInstance(SettingManager.class).getSystemSetting().getServerUrl();
					var builtInRegistryLogin = new BuiltInRegistryLogin(builtInRegistryUrl, jobToken, builtInRegistryAccessToken);
					commands.add(buildDockerConfig(registryLogins, builtInRegistryLogin));
					commands.add("EOF");
				}
				if (getTrustCertificates() != null) {
					commands.add("cat <<EOF>> /kaniko/ssl/certs/additional-ca-cert-bundle.crt");
					commands.add(getTrustCertificates().replace("\r\n", "\n"));
					commands.add("EOF");
				}
				
				var builder = new StringBuilder("/kaniko/executor");
				if (getBuildContext() != null)
					builder.append(" --context=\"/onedev-build/workspace/" + getBuildContext() + "\"");
				else
					builder.append(" --context=/onedev-build/workspace");
				for (var destination: StringUtils.splitAndTrim(getDestinations(), " "))
					builder.append(" --destination=").append(destination);
				if (getMoreOptions() != null)
					builder.append(" ").append(getMoreOptions());
				
				commands.add(builder.toString());
				return new CommandFacade(image, builtInRegistryAccessToken, commands, useTTY);
			}
			
		};
	}

}
