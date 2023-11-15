package io.onedev.server.jetty;

import com.google.inject.servlet.GuiceFilter;
import io.onedev.commons.bootstrap.Bootstrap;
import io.onedev.commons.utils.ExceptionUtils;
import io.onedev.server.cluster.ClusterManager;
import org.apache.tika.mime.MimeTypes;
import org.eclipse.jetty.http.HttpCookie.SameSite;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.SessionDataStoreFactory;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Singleton
public class DefaultJettyLauncher implements JettyLauncher, Provider<ServletContextHandler> {

	private static final int MAX_CONTENT_SIZE = 5000000;
	
	private final ClusterManager clusterManager;
	
	private final SessionDataStoreFactory sessionDataStoreFactory;
	
	private Server jettyServer;
	
	private ServletContextHandler servletContextHandler;
	
	private final Provider<Set<ServerConfigurator>> serverConfiguratorsProvider;
	
	private final Provider<Set<ServletConfigurator>> servletConfiguratorsProvider;
	
	@Override
	public ServletContextHandler get() {
		return servletContextHandler;
	}

	/*
	 * Inject providers here to avoid circurlar dependencies when dependency graph gets complicated
	 */
	@Inject
	public DefaultJettyLauncher(
			ClusterManager clusterManager, SessionDataStoreFactory sessionDataStoreFactory,
			Provider<Set<ServerConfigurator>> serverConfiguratorsProvider, 
			Provider<Set<ServletConfigurator>> servletConfiguratorsProvider) {
		this.clusterManager = clusterManager;
		this.sessionDataStoreFactory = sessionDataStoreFactory;
		this.serverConfiguratorsProvider = serverConfiguratorsProvider;
		this.servletConfiguratorsProvider = servletConfiguratorsProvider;
	}
	
	@Override
	public void start() {
		jettyServer = new Server();

		jettyServer.addBean(sessionDataStoreFactory);
		
        servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setMaxFormContentSize(MAX_CONTENT_SIZE);

        servletContextHandler.setClassLoader(DefaultJettyLauncher.class.getClassLoader());
        
        servletContextHandler.setErrorHandler(new ErrorPageErrorHandler());
        servletContextHandler.addFilter(DisableTraceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        
        servletContextHandler.getSessionHandler().setSessionIdPathParameterName(null);
        servletContextHandler.getSessionHandler().setSameSite(SameSite.LAX);  
        servletContextHandler.getSessionHandler().setHttpOnly(true);  
        
        /*
         * By default contributions is in reverse dependency order. We reverse the order so that 
         * servlet and filter contributions in dependency plugins comes first. 
         */
        List<ServletConfigurator> servletConfigurators = new ArrayList<>(servletConfiguratorsProvider.get());
        Collections.reverse(servletConfigurators);
        for (ServletConfigurator configurator: servletConfigurators) {
        	configurator.configure(servletContextHandler);
        }

        /*
         *  Add Guice filter as last filter in order to make sure that filters and servlets
         *  configured in Guice web module can be filtered correctly by filters added to 
         *  Jetty context directly.  
         */
        servletContextHandler.addFilter(GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

		ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
	    errorHandler.addErrorPage(HttpServletResponse.SC_NOT_FOUND, "/~errors/404");
	    servletContextHandler.setErrorHandler(errorHandler);

        GzipHandler gzipHandler = new GzipHandler();
		gzipHandler.setHandler(servletContextHandler);
		gzipHandler.setIncludedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name());
		gzipHandler.setExcludedMimeTypes(MimeTypes.OCTET_STREAM);

        jettyServer.setHandler(gzipHandler);
        
        for (ServerConfigurator configurator: serverConfiguratorsProvider.get()) 
        	configurator.configure(jettyServer);
        
        if (Bootstrap.command == null) {
			try {
				jettyServer.start();
			} catch (Exception e) {
				throw ExceptionUtils.unchecked(e);
			}
		}
	}

	@Override
	public void stop() {
		if (jettyServer != null && jettyServer.isStarted()) {
			try {
				jettyServer.stop();
			} catch (Exception e) {
				throw ExceptionUtils.unchecked(e);
			}
		}
	}

}
