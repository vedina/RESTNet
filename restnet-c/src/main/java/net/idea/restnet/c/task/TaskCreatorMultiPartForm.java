package net.idea.restnet.c.task;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.apache.commons.fileupload.FileItem;


public class TaskCreatorMultiPartForm<USERID, T> extends TaskCreator<USERID, T, List<FileItem>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2751369512533076728L;

	public TaskCreatorMultiPartForm(List<FileItem> input, boolean async) throws Exception {
		super(input, async);
	}
}
