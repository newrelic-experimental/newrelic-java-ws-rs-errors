package com.newrelic.instrumentation.ws.rs.errors;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.StatusType;

@Weave(originalName = "jakarta.ws.rs.ext.ExceptionMapper", type = MatchType.Interface)
public class ExceptionMapper_instrumentation<E extends Throwable> {

	@Trace
	public Response toResponse(E exception) {
		Response response =  Weaver.callOriginal();
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("Http-StatusCode", response.getStatus());
		StatusType statusType = response.getStatusInfo();
		if(statusType != null) {
			String reasonPhrase = statusType.getReasonPhrase();
			if(reasonPhrase != null && !reasonPhrase.isEmpty()) {
				attributes.put("Status-ReasonPhrase", reasonPhrase);
			}
		}
		URI uri = response.getLocation();
		if(uri != null) {
			attributes.put("Location-URI", uri);
		}
		NewRelic.noticeError(exception, attributes);
		return response;
	}
}
