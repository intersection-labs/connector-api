package io.unequal.reuse.http;
import io.unequal.reuse.util.Checker;


public class Settings {

	private int _port;
	private String _staticFiles;
	
	public Settings() {
		_port = 5000;
		_staticFiles = "/public";
	}

	public void port(int port) {
		Checker.checkMinValue(port, 0);
		_port = port;
	}
	
	public int port() {
		return _port;
	}
	
	public void staticFiles(String staticFiles) {
		Checker.checkEmpty(staticFiles);
		_staticFiles = staticFiles;
	}
	
	public String staticFiles() {
		return _staticFiles;
	}
}
