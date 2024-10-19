package com.skch.skchhostelservice.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

	private final byte[] cachedBody;

	public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
		super(request);
		// Cache the request body
		cachedBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()))
				.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new CachedBodyServletInputStream(cachedBody);
	}

	@Override
	public BufferedReader getReader() throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
		return new BufferedReader(new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8));
	}

	public String getRequestBody() throws IOException {
		return new String(cachedBody, StandardCharsets.UTF_8);
	}

	private static class CachedBodyServletInputStream extends ServletInputStream {

		private final ByteArrayInputStream byteArrayInputStream;

		public CachedBodyServletInputStream(byte[] cachedBody) {
			this.byteArrayInputStream = new ByteArrayInputStream(cachedBody);
		}

		@Override
		public int read() throws IOException {
			return byteArrayInputStream.read();
		}

		@Override
		public boolean isFinished() {
			return byteArrayInputStream.available() == 0;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener listener) {
			// No implementation needed for this simple wrapper
		}
	}
}
