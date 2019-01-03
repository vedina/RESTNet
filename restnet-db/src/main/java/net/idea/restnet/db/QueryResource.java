package net.idea.restnet.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.idea.modbcum.i.IDBProcessor;
import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.BatchProcessingException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.AbstractResource;
import net.idea.restnet.c.ChemicalMediaType;
import net.idea.restnet.c.PageParams;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.c.exception.RResourceException;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.c.task.TaskCreator;
import net.idea.restnet.c.task.TaskCreatorFile;
import net.idea.restnet.c.task.TaskCreatorForm;
import net.idea.restnet.c.task.TaskCreatorMultiPartForm;
import net.idea.restnet.i.aa.OpenSSOCookie;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.owasp.encoder.Encode;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.CacheDirective;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 * Abstract parent class for all resources , which retrieves something from the
 * database
 * 
 * @author nina
 * 
 * @param <Q>
 * @param <T>
 */
public abstract class QueryResource<Q extends IQueryRetrieval<T>, T extends Serializable>
		extends AbstractResource<Q, T, IProcessor<Q, Representation>> {
	protected enum RDF_WRITER {
		jena, stax
	}

	protected RDF_WRITER rdfwriter = RDF_WRITER.jena;
	protected boolean changeLineSeparators;
	protected boolean dataset_prefixed_compound_uri = false;
	public final static String query_resource = "/query";
	protected String configFile = "conf/restnet-db.pref";

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	/**
	 * TODO http://markmail.org/search/?q=restlet+statusservice+variant#query:
	 * restlet
	 * %20statusservice%20variant+page:1+mid:2qrzgzbendopxg5t+state:results an
	 * alternate design where you would leverage the new RepresentationInfo
	 * class added to Restlet 2.0 by overriding the
	 * "ServerResource#getInfo(Variant)" method. This would allow you to support
	 * content negotiation and conditional processing without having to connect
	 * to your database. Then, when the "get(Variant)" method calls you back,
	 * you would connect to your database, throw any exception that occurs and
	 * return a verified representation.
	 */

	protected int maxRetry = 3;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] { MediaType.TEXT_HTML,
				MediaType.TEXT_PLAIN, MediaType.TEXT_URI_LIST,
				MediaType.TEXT_CSV, MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE, MediaType.TEXT_RDF_N3,ChemicalMediaType.APPLICATION_JSONLD,
				MediaType.TEXT_RDF_NTRIPLES, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVASCRIPT,
				MediaType.APPLICATION_JAVA_OBJECT

		});
		if (queryObject != null) {
			Form form = getParams();
			setPaging(form, queryObject);
		}

	}

	protected Form getParams() {
		return getRequest().getResourceRef().getQueryAsForm();
	}

	protected Q returnQueryObject() {
		return queryObject;
	}

	public void configureDatasetMembersPrefixOption(boolean prefix) {
		dataset_prefixed_compound_uri = prefix;
	}

	protected void configureRDFWriterOption(String defaultWriter) {
		try {
			Object jenaOption = getRequest().getResourceRef().getQueryAsForm()
					.getFirstValue("rdfwriter");
			// if no option ?rdfwriter=jena|stax , then take from properties
			// rdf.writer
			// if not defined there, use jena
			rdfwriter = RDF_WRITER.valueOf(jenaOption == null ? defaultWriter
					: jenaOption.toString().toLowerCase());
		} catch (Exception x) {
			rdfwriter = RDF_WRITER.jena;
		}
	}

	protected void configureSDFLineSeparators(boolean defaultSeparator) {
		try {
			Object lsOption = getResourceRef(getRequest()).getQueryAsForm()
					.getFirstValue("changeLineSeparators");
			changeLineSeparators = lsOption == null ? defaultSeparator
					: Boolean.parseBoolean(lsOption.toString());
		} catch (Exception x) {
			changeLineSeparators = defaultSeparator;
		}
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		if (isHtmlbyTemplate()
				&& MediaType.TEXT_HTML.equals(variant.getMediaType())) {
			OpenSSOCookie.setCookieSetting(this.getResponse().getCookieSettings(),getToken(),
							useSecureCookie(getRequest()));
			return getHTMLByTemplate(variant);
		} else
			return getRepresentation(variant);
	}

	protected DBConnection getConnection(Context context, String configFile)
			throws SQLException, AmbitException {
		return new DBConnection(context, configFile);
	}

	protected Representation getRepresentation(Variant variant)
			throws ResourceException {
		try {

			OpenSSOCookie.setCookieSetting(this.getResponse().getCookieSettings(),
					getToken(), useSecureCookie(getRequest()));
			setXHeaders();
			setCacheHeaders();
			/*
			 * if (variant.getMediaType().equals(MediaType.APPLICATION_WADL)) {
			 * return new WadlRepresentation(); } else
			 */
			if (MediaType.APPLICATION_JAVA_OBJECT
					.equals(variant.getMediaType())) {
				if ((queryObject != null)
						&& (queryObject instanceof Serializable))
					return new ObjectRepresentation(
							(Serializable) returnQueryObject(),
							MediaType.APPLICATION_JAVA_OBJECT);
				else
					throw new ResourceException(
							Status.CLIENT_ERROR_NOT_ACCEPTABLE);
			}
			if (queryObject != null) {

				IProcessor<Q, Representation> convertor = null;
				Reporter reporter = null;
				Connection connection = null;
				int retry = 0;
				while (retry < maxRetry) {
					DBConnection dbc = null;
					try {
						dbc = getConnection(getContext(), getConfigFile());
						configureRDFWriterOption(dbc.rdfWriter());
						configureSDFLineSeparators(((IFreeMarkerApplication) getApplication())
								.isChangeLineSeparators());
						configureDatasetMembersPrefixOption(dbc
								.dataset_prefixed_compound_uri());
						convertor = createConvertor(variant);
						if (convertor instanceof RepresentationConvertor)
							((RepresentationConvertor) convertor)
									.setLicenseURI(getLicenseURI());

						connection = dbc.getConnection();
						reporter = ((RepresentationConvertor) convertor)
								.getReporter();
						if (reporter instanceof IDBProcessor)
							((IDBProcessor) reporter).setConnection(connection);
						Representation r = convertor.process(queryObject);
						r.setCharacterSet(CharacterSet.UTF_8);
						return r;

					} catch (ResourceException x) {
						try {
							if ((reporter != null) && (reporter != null))
								reporter.close();
						} catch (Exception ignored) {
						}
						try {
							if (connection != null)
								connection.close();
						} catch (Exception ignored) {
						}
						;
						throw x;
					} catch (NotFoundException x) {
						Representation r = processNotFound(x, retry);
						retry++;
						try {
							if ((reporter != null) && (reporter != null))
								reporter.close();
						} catch (Exception ignored) {
						}
						try {
							if (connection != null)
								connection.close();
						} catch (Exception ignored) {
						}
						;
						if (r != null)
							return r;

					} catch (SQLException x) {
						Representation r = processSQLError(x, retry, variant);
						retry++;
						try {
							if ((reporter != null) && (reporter != null))
								reporter.close();
						} catch (Exception ignored) {
						}
						try {
							if (connection != null)
								connection.close();
						} catch (Exception ignored) {
						}
						;
						if (r == null)
							continue;
						else
							return r;
					} catch (BatchProcessingException x) {
						Representation r = null;
						Exception batchException = null;
						if (x.getCause() instanceof NotFoundException) {
							r = processNotFound(
									(NotFoundException) x.getCause(), retry);
							retry++;
							if (r == null)
								batchException = new ResourceException(
										Status.CLIENT_ERROR_NOT_FOUND, x
												.getCause().getMessage(),
										x.getCause());
						}
						try {
							if ((reporter != null) && (reporter != null))
								reporter.close();
						} catch (Exception ignored) {
						}
						try {
							if (connection != null)
								connection.close();
						} catch (Exception ignored) {
						}
						;
						if (r != null)
							return r;
						batchException = batchException == null ? new RResourceException(
								Status.SERVER_ERROR_INTERNAL, x, variant)
								: batchException;
						Context.getCurrentLogger().severe(x.getMessage());
						throw batchException;
					} catch (Exception x) {
						try {
							if ((reporter != null) && (reporter != null))
								reporter.close();
						} catch (Exception ignored) {
						}
						try {
							if (connection != null)
								connection.close();
						} catch (Exception ignored) {
						}
						;
						Context.getCurrentLogger().severe(x.getMessage());
						throw new RResourceException(
								Status.SERVER_ERROR_INTERNAL, x, variant);

					} finally {
						dbc = null;
						// if no exceptions, will be closed by reporters
					}
				}
				return null;

			} else {
				if (variant.getMediaType().equals(MediaType.TEXT_HTML))
					try {
						IProcessor<Q, Representation> convertor = createConvertor(variant);
						Representation r = convertor.process(null);
						return r;
					} catch (Exception x) {
						throw new RResourceException(
								Status.CLIENT_ERROR_BAD_REQUEST, x, variant);
					}
				else {
					throw new RResourceException(
							Status.CLIENT_ERROR_BAD_REQUEST, error, variant);
				}

			}
		} catch (RResourceException x) {
			throw x;
		} catch (ResourceException x) {
			throw new RResourceException(x.getStatus(), x, variant);
		} catch (Exception x) {
			throw new RResourceException(Status.SERVER_ERROR_INTERNAL, x,
					variant);
		} finally {

		}
	}

	protected Representation processNotFound(NotFoundException x, int retry)
			throws Exception {
		throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,
				String.format("Query returns no results! %s", x.getMessage()));
	}

	protected Representation processSQLError(SQLException x, int retry,
			Variant variant) throws Exception {
		Context.getCurrentLogger().severe(x.getMessage());
		if (retry < maxRetry) {
			getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, x,
					String.format("Retry %d ", retry));
			return null;
		} else {
			throw new RResourceException(
					Status.SERVER_ERROR_SERVICE_UNAVAILABLE, x, variant);
		}
	}

	/**
	 * POST - create entity based on parameters in http header, creates a new
	 * entry in the databaseand returns an url to it
	 * 
	 * public void executeUpdate(Representation entity, T entry, AbstractUpdate
	 * updateObject) throws ResourceException {
	 * 
	 * Connection c = null; //TODO it is inefficient to instantiate executor in
	 * all classes UpdateExecutor executor = new UpdateExecutor(); try {
	 * DBConnection dbc = new DBConnection(getContext(),getConfigFile()); c =
	 * dbc.getConnection(getRequest());
	 * 
	 * executor.setConnection(c); executor.open();
	 * executor.process(updateObject);
	 * 
	 * customizeEntry(entry, c);
	 * 
	 * QueryURIReporter<T,Q> uriReporter = getURUReporter(getRequest()); if
	 * (uriReporter!=null) {
	 * getResponse().setLocationRef(uriReporter.getURI(entry));
	 * getResponse().setEntity(uriReporter.getURI(entry),MediaType.TEXT_HTML); }
	 * getResponse().setStatus(Status.SUCCESS_OK);
	 * 
	 * } catch (SQLException x) {
	 * Context.getCurrentLogger().severe(x.getMessage());
	 * getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN,x,x.getMessage());
	 * getResponse().setEntity(null); } catch (ProcessorException x) {
	 * Context.getCurrentLogger().severe(x.getMessage());
	 * getResponse().setStatus((x.getCause() instanceof
	 * SQLException)?Status.CLIENT_ERROR_FORBIDDEN:Status.SERVER_ERROR_INTERNAL,
	 * x,x.getMessage()); getResponse().setEntity(null); } catch (Exception x) {
	 * Context.getCurrentLogger().severe(x.getMessage());
	 * getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x,x.getMessage());
	 * getResponse().setEntity(null); } finally { try {executor.close();} catch
	 * (Exception x) {} try {if(c != null) c.close();} catch (Exception x) {} }
	 * } /** POST - create entity based on parameters in the query, creates a
	 * new entry in the databaseand returns an url to it TODO Refactor to allow
	 * multiple objects
	 * 
	 * public void createNewObject(Representation entity) throws
	 * ResourceException { T entry = createObjectFromHeaders(null, entity);
	 * executeUpdate(entity, entry, createUpdateObject(entry));
	 * 
	 * }
	 */

	/**
	 * DELETE - create entity based on parameters in the query, creates a new
	 * entry in the database and returns an url to it
	 * 
	 * public void deleteObject(Representation entity) throws ResourceException
	 * { Form queryForm = getRequest().getResourceRef().getQueryAsForm(); T
	 * entry = createObjectFromHeaders(queryForm, entity); executeUpdate(entity,
	 * entry, createDeleteObject(entry));
	 * 
	 * }
	 */
	/*
	 * protected Representation delete(Variant variant) throws ResourceException
	 * { Representation entity = getRequestEntity(); Form queryForm = null; if
	 * (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) queryForm
	 * = new Form(entity); T entry = createObjectFromHeaders(queryForm, entity);
	 * executeUpdate(entity, entry, createDeleteObject(entry));
	 * getResponse().setStatus(Status.SUCCESS_OK); return new
	 * EmptyRepresentation(); };
	 */
	protected QueryURIReporter<T, Q> getURIReporter(Request baseReference)
			throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,
				String.format("%s getURUReporter()", getClass().getName()));
	}

	/*
	 * protected AbstractUpdate createUpdateObject(T entry) throws
	 * ResourceException { throw new
	 * ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED
	 * ,String.format("%s createUpdateObject()", getClass().getName())); }
	 * protected AbstractUpdate createDeleteObject(T entry) throws
	 * ResourceException { throw new
	 * ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED
	 * ,String.format("%s createDeleteObject()", getClass().getName())); }
	 * protected RDFObjectIterator<T> createObjectIterator(Reference reference,
	 * MediaType mediaType) throws ResourceException { throw new
	 * ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,String.format(
	 * "%s createObjectIterator()", getClass().getName())); }
	 * 
	 * protected RDFObjectIterator<T> createObjectIterator(Representation
	 * entity) throws ResourceException { throw new
	 * ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED
	 * ,String.format("%s createObjectIterator", getClass().getName())); }
	 */
	/**
	 * Return this object if can't parse source_uri
	 * 
	 * @param uri
	 * @return
	 */
	protected T onError(String uri) {
		return null;
	}

	/*
	 * protected T createObjectFromHeaders(Form queryForm, Representation
	 * entity) throws ResourceException { RDFObjectIterator<T> iterator = null;
	 * if (!entity.isAvailable()) { //using URI throw new
	 * ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty content"); }
	 * else if (MediaType.TEXT_URI_LIST.equals(entity.getMediaType())) { return
	 * createObjectFromURIlist(entity); } else if
	 * (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) { return
	 * createObjectFromWWWForm(entity); } else if
	 * (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType())) { return
	 * createObjectFromMultiPartForm(entity);
	 * 
	 * } else // assume RDF try { iterator = createObjectIterator(entity);
	 * iterator.setCloseModel(true);
	 * iterator.setBaseReference(getRequest().getRootRef());
	 * 
	 * while (iterator.hasNext()) { T nextObject = iterator.next(); if
	 * (accept(nextObject)) return nextObject; } throw new
	 * ResourceException(Status
	 * .CLIENT_ERROR_BAD_REQUEST,"Nothing to write! "+getRequest().getRootRef()
	 * ); } catch (ResourceException x) { throw x; } catch (Exception x) { throw
	 * new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
	 * } finally { try { iterator.close(); } catch (Exception x) {} }
	 * 
	 * }
	 * 
	 * protected boolean accept(T object) throws ResourceException { return
	 * true; }
	 * 
	 * 
	 * protected T createObjectFromWWWForm(Representation entity) throws
	 * ResourceException { Form queryForm = new Form(entity); String sourceURI =
	 * getObjectURI(queryForm); RDFObjectIterator<T> iterator = null; try {
	 * iterator = createObjectIterator(new
	 * Reference(sourceURI),entity.getMediaType
	 * ()==null?MediaType.APPLICATION_RDF_XML:entity.getMediaType());
	 * iterator.setCloseModel(true);
	 * iterator.setBaseReference(getRequest().getRootRef()); while
	 * (iterator.hasNext()) { return iterator.next(); } //if none return
	 * onError(sourceURI); } catch (Exception x) { return onError(sourceURI); }
	 * finally { try { iterator.close(); } catch (Exception x) {} } } protected
	 * T createObjectFromMultiPartForm(Representation entity) throws
	 * ResourceException { throw new
	 * ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED); } protected T
	 * createObjectFromURIlist(Representation entity) throws ResourceException {
	 * BufferedReader reader = null; try { reader = new BufferedReader(new
	 * InputStreamReader(entity.getStream())); String line = null; while ((line
	 * = reader.readLine())!= null) return onError(line); throw new
	 * ResourceException(Status.CLIENT_ERROR_NOT_FOUND); } catch (Exception x) {
	 * throw new
	 * ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x); }
	 * finally { try { reader.close();} catch (Exception x) {} }
	 * 
	 * }
	 */

	protected String getObjectURI(Form queryForm) throws ResourceException {
		return getParameter(queryForm, PageParams.params.source_uri.toString(),
				PageParams.params.source_uri.getDescription(), true);
	}

	@Override
	protected Representation processAndGenerateTask(final Method method,
			Representation entity, Variant variant, boolean async)
			throws ResourceException {

		Connection conn = null;
		try {

			IQueryRetrieval<T> query = createUpdateQuery(method, getContext(),
					getRequest(), getResponse());

			TaskCreator taskCreator = getTaskCreator(entity, variant, method,
					async);

			List<UUID> r = null;
			if (query == null) { // no db querying, just return the task
				r = taskCreator.process(null);
			} else {
				DBConnection dbc = new DBConnection(getApplication()
						.getContext(), getConfigFile());
				conn = dbc.getConnection();

				try {
					taskCreator.setConnection(conn);
					r = taskCreator.process(query);
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					throw x;
				} finally {
					try {
						taskCreator.setConnection(null);
						taskCreator.close();
					} catch (Exception x) {
					}
					try {
						conn.close();
						conn = null;
					} catch (Exception x) {
					}
				}
			}

			if ((r == null) || (r.size() == 0))
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			else {
				ITaskStorage storage = ((ITaskApplication) getApplication())
						.getTaskStorage();
				FactoryTaskConvertor<Object> tc = getFactoryTaskConvertor(storage);
				if (r.size() == 1) {
					ITask<ITaskResult, Object> task = storage
							.findTask(r.get(0));
					task.update();
					if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
						getResponse().redirectSeeOther(task.getUri().getUri());
						return null;
					} else {
						setStatus(task.isDone() ? Status.SUCCESS_OK
								: Status.SUCCESS_ACCEPTED);
						return tc
								.createTaskRepresentation(r.get(0), variant,
										getRequest(), getResponse(),
										getDocumentation());
					}
				} else
					return tc.createTaskRepresentation(r.iterator(), variant,
							getRequest(), getResponse(), getDocumentation());

			}
		} catch (RResourceException x) {
			throw x;
		} catch (ResourceException x) {
			throw new RResourceException(x.getStatus(), x, variant);
		} catch (AmbitException x) {
			throw new RResourceException(new Status(
					Status.SERVER_ERROR_INTERNAL, x), variant);
		} catch (SQLException x) {
			throw new RResourceException(new Status(
					Status.SERVER_ERROR_INTERNAL, x), variant);
		} catch (Exception x) {
			throw new RResourceException(new Status(
					Status.SERVER_ERROR_INTERNAL, x), variant);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception x) {
			}
		}

	}

	/**
	 * TODO allow to set taskCreator.getProcessors().setAbortOnError(true);
	 * 
	 * @param entity
	 * @param variant
	 * @param method
	 * @param async
	 * @return
	 * @throws Exception
	 */
	protected TaskCreator getTaskCreator(Representation entity,
			Variant variant, Method method, boolean async) throws Exception {

		if (entity == null) {
			return getTaskCreator(null, method, async, null);
		} else if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			Form form = new Form(entity);
			final Reference reference = new Reference(getObjectURI(form));
			return getTaskCreator(form, method, async, reference);
		} else if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
				true)) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// factory.setSizeThreshold(100);
			RestletFileUpload upload = new RestletFileUpload(factory);
			List<FileItem> items = upload.parseRequest((Request) getRequest());
			return getTaskCreator(items, method, async);
		} else if (isAllowedMediaType(entity.getMediaType())) {
			return getTaskCreator(downloadRepresentation(entity, variant),
					entity.getMediaType(), method, async);

		} else
			throw new ResourceException(
					Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);

	}

	protected File downloadRepresentation(Representation entity, Variant variant)
			throws Exception {

		String extension = getExtension(entity.getMediaType());
		File file = null;
		
		if (entity.getDisposition().getFilename() == null) {
			file = File
					.createTempFile(
							String.format("_download_%s", UUID.randomUUID()),
							extension);
			file.deleteOnExit();
		} else
			file = new File(String.format("%s/%s",
					System.getProperty("java.io.tmpdir"),
					entity.getDisposition().getFilename()));
		FileOutputStream out = new FileOutputStream(file);
		entity.write(out);
		out.flush();
		out.close();

		return file;

	}

	protected String getExtension(MediaType mediaType) {

		if (MediaType.APPLICATION_RDF_XML.equals(mediaType))
			return ".rdf";
		else if (MediaType.APPLICATION_RDF_TURTLE.equals(mediaType))
			return ".turtle";
		else if (MediaType.TEXT_RDF_N3.equals(mediaType))
			return ".n3";
		else if (ChemicalMediaType.APPLICATION_JSONLD.equals(mediaType))
			return ".jsonld";
		else if (MediaType.APPLICATION_EXCEL.equals(mediaType))
			return ".xls";
		else if (MediaType.TEXT_CSV.equals(mediaType))
			return ".csv";
		else if (MediaType.TEXT_PLAIN.equals(mediaType))
			return ".txt";
		else if (MediaType.APPLICATION_MSOFFICE_XLSX.equals(mediaType))
			return ".xlsx";
		else if (MediaType.APPLICATION_PDF.equals(mediaType))
			return ".pdf";
		else
			return null;
	}

	/**
	 * Create a task, given a file
	 * 
	 * @param form
	 * @param method
	 * @param async
	 * @param reference
	 * @return
	 * @throws Exception
	 */
	protected TaskCreator getTaskCreator(File file, MediaType mediaType,
			final Method method, boolean async) throws Exception {
		return new TaskCreatorFile<Object, T>(file, mediaType, async) {
			protected ICallableTask getCallable(File file, T item)
					throws ResourceException {
				return createCallable(method, file, mediaType, item);
			}

			@Override
			protected ITask<Reference, Object> createTask(
					ICallableTask callable, T item) throws ResourceException {
				return addTask(callable, item, null);
			}
		};
	}

	/**
	 * Create a task, given a web form
	 * 
	 * @param form
	 * @param method
	 * @param async
	 * @param reference
	 * @return
	 * @throws Exception
	 */
	protected TaskCreator getTaskCreator(Form form, final Method method,
			boolean async, final Reference reference) throws Exception {
		return new TaskCreatorForm<Object, T>(form, async) {
			@Override
			protected ICallableTask getCallable(Form form, T item)
					throws ResourceException {
				return createCallable(method, form, item);
			}

			@Override
			protected ITask<Reference, Object> createTask(
					ICallableTask callable, T item) throws ResourceException {
				return addTask(callable, item, reference);
			}
		};
	}

	/**
	 * Create task, given multipart web form (file uploads)
	 * 
	 * @param fileItems
	 * @param method
	 * @param async
	 * @param reference
	 * @return
	 * @throws Exception
	 */
	protected TaskCreator getTaskCreator(List<FileItem> fileItems,
			final Method method, boolean async) throws Exception {
		return new TaskCreatorMultiPartForm<Object, T>(fileItems, async) {
			protected ICallableTask getCallable(java.util.List<FileItem> input,
					T item) throws ResourceException {
				return createCallable(method, input, item);
			};

			@Override
			protected ITask createTask(ICallableTask callable, T item)
					throws ResourceException {
				return addTask(callable, item, (Reference) null);
			}
		};
	}

	protected ITask<Reference, Object> addTask(ICallableTask callable, T item,
			Reference reference) throws ResourceException {

		return ((ITaskApplication) getApplication()).addTask(String.format(
				"Apply %s %s %s", item == null ? "" : item.toString(),
				reference == null ? "" : "to", reference == null ? ""
						: reference), callable, getRequest().getRootRef(),
				getToken());

	}

	protected CallableProtectedTask<String> createCallable(Method method,
			File file, MediaType mediaType, T item) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}

	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, T item) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}

	protected CallableProtectedTask<String> createCallable(Method method,
			List<FileItem> input, T item) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}

	protected void setPaging(Form form, IQueryObject queryObject) {
		String max = form.getFirstValue(max_hits);
		String page = form.getFirstValue(PageParams.params.page.toString());
		String pageSize = form.getFirstValue(PageParams.params.pagesize
				.toString());
		if (max != null)
			try {
				queryObject.setPage(0);
				queryObject.setPageSize(Long.parseLong(form.getFirstValue(
						max_hits).toString()));
				return;
			} catch (Exception x) {

			}
		try {
			queryObject.setPage(Integer.parseInt(page));
		} catch (Exception x) {
		}
		try {
			queryObject.setPageSize(Long.parseLong(pageSize));
		} catch (Exception x) {

		}
	}

	/*
	 * protected Template createTemplate(Form form) throws ResourceException {
	 * String[] featuresURI = OpenTox.params.feature_uris.getValuesArray(form);
	 * return createTemplate(getContext(),getRequest(),getResponse(),
	 * featuresURI); } protected Template createTemplate(Context context,
	 * Request request, Response response,String[] featuresURI) throws
	 * ResourceException {
	 * 
	 * try { Template profile = new Template(null); profile.setId(-1);
	 * 
	 * ProfileReader reader = new
	 * ProfileReader(getRequest().getRootRef(),profile);
	 * reader.setCloseConnection(false);
	 * 
	 * 
	 * 
	 * DBConnection dbc = new DBConnection(getContext()); Connection conn =
	 * dbc.getConnection(getRequest()); try { for (String
	 * featureURI:featuresURI) { if (featureURI == null) continue;
	 * reader.setConnection(conn); profile = reader.process(new
	 * Reference(featureURI)); reader.setProfile(profile);
	 * 
	 * } // readFeatures(featureURI, profile); if (profile.size() == 0) {
	 * reader.setConnection(conn); String templateuri =
	 * getDefaultTemplateURI(context,request,response); if (templateuri!= null)
	 * profile = reader.process(new Reference(templateuri));
	 * reader.setProfile(profile); } } catch (Exception x) {
	 * System.out.println(getRequest().getResourceRef()); //x.printStackTrace();
	 * } finally { //the reader closes the connection
	 * reader.setCloseConnection(true); try { reader.close();} catch (Exception
	 * x) {} //try { conn.close();} catch (Exception x) {} } return profile; }
	 * catch (Exception x) { getLogger().info(x.getMessage()); throw new
	 * ResourceException(Status.SERVER_ERROR_INTERNAL,x); }
	 * 
	 * } protected String getDefaultTemplateURI(Context context, Request
	 * request,Response response) { return null; }
	 */

	protected String getLicenseURI() {
		return null;
	}

	protected boolean isAllowedMediaType(MediaType mediaType)
			throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}

	protected Representation getHTMLByTemplate(Variant variant)
			throws ResourceException {
		Map<String, Object> map = new HashMap<String, Object>();
		if (getClientInfo().getUser() != null)
			map.put("username", getClientInfo().getUser().getIdentifier());
		configureTemplateMap(map, getRequest(),
				(IFreeMarkerApplication) getApplication());
		return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);
	}

	protected Reference cleanedResourceRef(Reference ref) {
		return new Reference(Encode.forJavaScriptSource(ref.toString()));
	}

	@Override
	protected void setCacheHeaders() {
		getResponse().getCacheDirectives().add(CacheDirective.privateInfo());
		// getResponse().getCacheDirectives().add(CacheDirective.maxAge(2700));
		getResponse().getCacheDirectives().add(CacheDirective.noCache());
	}
}
