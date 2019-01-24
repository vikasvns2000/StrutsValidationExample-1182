package com.jwt.struts.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExcludedParameterRequest extends HttpServletRequestWrapper {
	private static final Log LOG = LogFactory.getLog(ExcludedParameterRequest.class);
	private static final int BUFFER_SIZE = 128;
	private static final String CONTENT_LENGTH_PATTERN = "(?i)content-length";
	private static final List<String> CANCEL_REQUEST_PARAMS = Arrays.asList("org.apache.struts.taglib.html.CANCEL",
			"org.apache.struts.taglib.html.CANCEL.x");

	private final String body;
	private final Pattern pattern;
	private final Pattern content_length_pattern;
	private boolean read_stream = false;
	private final Enumeration<String> parameterNames;

	public ExcludedParameterRequest( HttpServletRequest request, Pattern pattern ) {
		super(request);
		this.pattern = pattern;
		this.content_length_pattern = Pattern.compile(CONTENT_LENGTH_PATTERN, Pattern.DOTALL);
		this.parameterNames = request.getParameterNames();

		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			InputStream inputStream = request.getInputStream();

			if (inputStream != null) {
				String characterEncoding = this.getCharacterEncoding();
				if (characterEncoding == null ) {
					bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				}
				else {
					bufferedReader = new BufferedReader(new InputStreamReader(inputStream, characterEncoding));
				}
				char[] charBuffer = new char[BUFFER_SIZE];
				int bytesRead = -1;

				while ( (bytesRead = bufferedReader.read(charBuffer)) > 0 ) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch ( IOException ex ) {
			logCatchedException(ex);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch ( IOException ex ) {
					logCatchedException(ex);
				}
			}
		}
		body = stringBuilder.toString();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		 List<String> finalParameterNames = new ArrayList<String>();
		 while(parameterNames.hasMoreElements()) {
			 String parameterName = (String) parameterNames.nextElement();
			 if (!isExcludedParam(parameterName)) {
                 finalParameterNames.add(parameterName);
             }
		 }
		
         return Collections.enumeration(finalParameterNames);
	}
	
	@Override
	public String getParameter(String paramName) {
		if(isExcludedParam(paramName)) {
			return null;
		}
        return super.getParameter(paramName);
    }
	
	private boolean isExcludedParam(String paramName) {
		return pattern.matcher(paramName).matches() ||  CANCEL_REQUEST_PARAMS.contains(paramName);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace(body);
		}
		final ByteArrayInputStream byteArrayInputStream;
		if (pattern.matcher(body).matches()) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("[getInputStream]: found body to match blacklisted parameter pattern");
			}
			byteArrayInputStream = new ByteArrayInputStream("".getBytes());
		} else if (read_stream) {
			byteArrayInputStream = new ByteArrayInputStream("".getBytes());
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("[getInputStream]: OK - body does not match blacklisted parameter pattern");
			}
			byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
			read_stream = true;
		}

		return new ServletInputStream() {
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
		};
	}

	@Override
	public String getHeader(String name) {
		if (pattern.matcher(body).matches() && content_length_pattern.matcher(name).matches()) {
			return "0";
		}
		return super.getHeader(name);
	}

	@Override
	public int getContentLength() {
		if (pattern.matcher(body).matches()) {
			return 0;
		}
		return super.getContentLength();
	}

	private void logCatchedException( IOException ex ) {
		LOG.error("[ParamFilteredRequest]: Exception catched: ", ex);
	}
}