package io.unequal.reuse.util;

public class Path {

	final String _source;

	public Path(String source) {
		Checker.empty(source);
		_source = source;
	}

	public String[] split() {
		String s = _source.charAt(0)=='/' ? _source.substring(1) : _source;
		return s.split("\\/");
	}
	
	public String last() {
		return Arrays.last(split());
	}
}
