package io.unequal.reuse.http;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


public class HeadResponse extends HttpServletResponseWrapper {

	private final NoOutputStream _stream = new NoOutputStream();
    private PrintWriter _writer;
    
	public HeadResponse(HttpServletResponse response) {
		super(response);
	}

	public ServletOutputStream getOutputStream() throws IOException {
    	return _stream;
	}

	public PrintWriter getWriter() throws UnsupportedEncodingException {
		if(_writer == null) {
			_writer = new PrintWriter(new OutputStreamWriter(_stream, getCharacterEncoding()));
		}
        return _writer;
    }

    void setContentLength() {
        super.setContentLength(_stream.getContentLength());
    }
    
    private class NoOutputStream extends ServletOutputStream {
    	private int _contentLength = 0;

    	int getContentLength() {
    		return _contentLength;
    	}

    	public void write(int b) {
    		_contentLength++;
    	}

    	public void write(byte buf[], int offset, int len) throws IOException {
    		_contentLength += len;
    	}

		public boolean isReady() {
			return true;
		}

		public void setWriteListener(WriteListener listener) {
		}
    }
}
