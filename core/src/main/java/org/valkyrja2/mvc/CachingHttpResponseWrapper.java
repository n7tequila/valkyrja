/*
 * PROJECT valkyrja2
 * core/CachingHttpResponseWrapper.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.mvc;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 支持多次从HttpServletResponse获取返回值的Wrapper对象
 * 
 * @author Tequila
 *
 */
public class CachingHttpResponseWrapper extends HttpServletResponseWrapper implements CachingResponse {

	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	private HttpServletResponse response;

	public CachingHttpResponseWrapper(HttpServletResponse response) {
		super(response);
		this.response = response;
	}

	public byte[] getBody() {
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public ServletOutputStream getOutputStream() {
		return new ServletOutputStreamWrapper(this.byteArrayOutputStream, this.response);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(
				new OutputStreamWriter(this.byteArrayOutputStream, this.response.getCharacterEncoding()));
	}

	private static class ServletOutputStreamWrapper extends ServletOutputStream {

		private ByteArrayOutputStream outputStream;
		private HttpServletResponse response;

		public ServletOutputStreamWrapper(ByteArrayOutputStream byteArrayOutputStream, HttpServletResponse response) {
			this.outputStream = byteArrayOutputStream;
			this.response = response;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setWriteListener(WriteListener listener) {

		}

		@Override
		public void write(int b) throws IOException {
			this.outputStream.write(b);
		}

		@Override
		public void flush() throws IOException {
			if (!this.response.isCommitted()) {
				byte[] body = this.outputStream.toByteArray();
				ServletOutputStream outputStream = this.response.getOutputStream();
				outputStream.write(body);
				outputStream.flush();
			}
		}
	}
}
