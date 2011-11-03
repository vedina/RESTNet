package net.idea.restnet.c.task;

import java.io.File;

import org.restlet.data.MediaType;

public class TaskCreatorFile<USERID, T> extends TaskCreator<USERID, T,File> {
	protected MediaType mediaType ;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1341551608684088669L;

	public TaskCreatorFile(File input, MediaType mediaType, boolean async) throws Exception {
		super(input, async);
		this.mediaType = mediaType;
	}

	
	
	
}
