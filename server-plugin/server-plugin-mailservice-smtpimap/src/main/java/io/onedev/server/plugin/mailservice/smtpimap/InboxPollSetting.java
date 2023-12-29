package io.onedev.server.plugin.mailservice.smtpimap;

import io.onedev.server.annotation.Editable;
import io.onedev.server.annotation.Password;
import io.onedev.server.model.support.administration.mailservice.ImapImplicitSsl;
import io.onedev.server.model.support.administration.mailservice.ImapSslSetting;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Editable
public class InboxPollSetting implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String imapHost;
	
	private ImapSslSetting sslSetting = new ImapImplicitSsl();
	
	private String imapUser;
	
	private String imapPassword;
	
	private int pollInterval = 60;
	
	private List<String> additionalTargetAddresses = new ArrayList<>();
	
	@Editable(order=100, name="IMAP Host")
	@NotEmpty
	public String getImapHost() {
		return imapHost;
	}

	public void setImapHost(String imapHost) {
		this.imapHost = imapHost;
	}

	@Editable(order=200)
	@NotNull
	public ImapSslSetting getSslSetting() {
		return sslSetting;
	}

	public void setSslSetting(ImapSslSetting sslSetting) {
		this.sslSetting = sslSetting;
	}

	@Editable(order=300, name="IMAP User", description="Specify IMAP user name.<br>"
			+ "<b class='text-danger'>NOTE: </b> This account should be able to receive emails sent to system "
			+ "email address specified above")
	@NotEmpty
	public String getImapUser() {
		return imapUser;
	}

	public void setImapUser(String imapUser) {
		this.imapUser = imapUser;
	}

	@Editable(order=400, name="IMAP Password")
	@Password(autoComplete="new-password")
	@NotEmpty
	public String getImapPassword() {
		return imapPassword;
	}

	public void setImapPassword(String imapPassword) {
		this.imapPassword = imapPassword;
	}

	@Editable(order=500, description="Specify incoming email poll interval in seconds")
	@Min(value=10, message="This value should not be less than 10")
	public int getPollInterval() {
		return pollInterval;
	}

	public void setPollInterval(int pollInterval) {
		this.pollInterval = pollInterval;
	}

	@Editable(order=600, name="Additional Email Addresses to Monitor", placeholder = "Input email address and press ENTER", description = "Emails sent to these " +
			"email addresses will also be processed besides system email address specified above")
	public List<String> getAdditionalTargetAddresses() {
		return additionalTargetAddresses;
	}

	public void setAdditionalTargetAddresses(List<String> additionalTargetAddresses) {
		this.additionalTargetAddresses = additionalTargetAddresses;
	}

}