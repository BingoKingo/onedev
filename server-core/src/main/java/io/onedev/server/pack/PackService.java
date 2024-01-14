package io.onedev.server.pack;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface PackService {

	String getServiceId();
	
	void service(HttpServletRequest request, HttpServletResponse response,
				 Long projectId, @Nullable Long buildId, List<String> pathSegments);

	@Nullable
	String getApiKey(HttpServletRequest request);
	
}
