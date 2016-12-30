package io.unequal.reuse.http;


public interface Processor { 

	public void process(Request req, Context ctx, Response resp);
}
