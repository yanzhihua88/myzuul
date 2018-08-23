package com.yzh.myzuul.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;

public class AccessFilter extends ZuulFilter {

	private static Logger log = LoggerFactory.getLogger(AccessFilter.class);

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();

		log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

//		Object accessToken = request.getParameter("accessToken");
//		if (accessToken == null) {
//			log.warn("access token is empty");
//			ctx.setSendZuulResponse(false);
//			ctx.setResponseStatusCode(401);
//			return null;
//		}
//		log.info("access token ok");
		try {
			InputStream in = (InputStream) ctx.get("requestEntity");
			if (in == null) {
				in = ctx.getRequest().getInputStream();
			}
			String body = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
			log.info("request body-->"+body);
			byte[] bytes = body.getBytes("UTF-8");
			
			ctx.setRequest(new HttpServletRequestWrapper(ctx.getRequest()) {
				@Override
				public ServletInputStream getInputStream() throws IOException {
					return new ServletInputStreamWrapper(bytes);
				}

				@Override
				public int getContentLength() {
					return bytes.length;
				}

				@Override
				public long getContentLengthLong() {
					return bytes.length;
				}
			});
		} catch (IOException e) {
		}
		return null;
	}

}