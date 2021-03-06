package net.idea.restnet.app;

import java.io.StringWriter;

import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.ClientInfo;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.Enroler;
import org.restlet.security.RoleAuthorizer;
import org.restlet.security.Verifier;
import org.restlet.service.TunnelService;
import org.restlet.util.RouteList;

import net.idea.modbcum.i.config.Preferences;
import net.idea.restnet.aa.opensso.OpenSSOAuthenticator;
import net.idea.restnet.aa.opensso.OpenSSOAuthorizer;
import net.idea.restnet.aa.opensso.OpenSSOVerifierSetUser;
import net.idea.restnet.aa.opensso.policy.CallablePolicyCreator;
import net.idea.restnet.aa.opensso.policy.PolicyProtectedTask;
import net.idea.restnet.aa.opensso.users.OpenSSOUserResource;
import net.idea.restnet.aa.resource.AdminResource;
import net.idea.restnet.aa.resource.AdminRouter;
import net.idea.restnet.aa.resource.PolicyResource;
import net.idea.restnet.c.ChemicalMediaType;
import net.idea.restnet.c.TaskApplication;
import net.idea.restnet.c.filter.RESTnetTunnelFilter;
import net.idea.restnet.c.resource.RESTNetStatusService;
import net.idea.restnet.c.resource.TaskResource;
import net.idea.restnet.c.routers.MyRouter;
import net.idea.restnet.c.routers.TaskRouter;
import net.idea.restnet.c.task.TaskStorage;
import net.idea.restnet.db.aalocal.ChallengeAuthenticatorDBLocal;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.UserRolesResource;
import net.idea.restnet.db.aalocal.policy.RESTPolicyResource;
import net.idea.restnet.i.aa.RESTPolicy;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.Task;
import net.idea.restnet.sparql.TDBEndpointRouter;

/**
 * Demonstration of how to use RESTNet to create a web application & web
 * services fusion.
 * 
 * @author nina
 */

/* 

 *
 */
public class RESTNetApplication extends TaskApplication<String> {

	public RESTNetApplication() {
		super();
		setName("AMBIT REST services");
		setDescription("AMBIT implementation of OpenTox framework");
		setOwner("Ideaconsult Ltd.");
		setAuthor("Ideaconsult Ltd.");

		/*
		 * String tmpDir = System.getProperty("java.io.tmpdir"); File logFile =
		 * new File(tmpDir,"ambit2-www.log");
		 * System.setProperty("java.util.logging.config.file"
		 * ,logFile.getAbsolutePath());
		 */

		setStatusService(new RESTNetStatusService());
		setTunnelService(new TunnelService(true, true) {
			@Override
			public Filter createInboundFilter(Context context) {
				return new RESTnetTunnelFilter(context);
			}
		});
		getTunnelService().setUserAgentTunnel(true);
		getTunnelService().setExtensionsTunnel(false);

		Preferences.setProperty(Preferences.MAXRECORDS, "0");

		getMetadataService().setEnabled(true);
		getMetadataService().addExtension("sdf", ChemicalMediaType.CHEMICAL_MDLSDF, true);
		getMetadataService().addExtension("mol", ChemicalMediaType.CHEMICAL_MDLMOL, true);
		getMetadataService().addExtension("inchi", ChemicalMediaType.CHEMICAL_INCHI, true);
		getMetadataService().addExtension("cml", ChemicalMediaType.CHEMICAL_CML, true);
		getMetadataService().addExtension("smiles", ChemicalMediaType.CHEMICAL_SMILES, true);

	}

	@Override
	public Restlet createInboundRoot() {
		Router router = new MyRouter(this.getContext());
		// router.attach("/help", AmbitResource.class);

		router.attach("/", OpenSSOUserResource.class);
		router.attach("", OpenSSOUserResource.class);

		/**
		 * Points to the Ontology service /sparqlendpoint
		 */
		// router.attach(SPARQLPointerResource.resource,
		// SPARQLPointerResource.class);

		/**
		 * * /admin Various admin tasks, like database creation
		 */

		router.attach(String.format("/%s", AdminResource.resource),
				createProtectedResource(createAdminRouter(), "admin"));

		/**
		 * List of datasets /dataset , /datasets
		 * 
		 * Router allDatasetsRouter = new MyRouter(getContext());
		 * allDatasetsRouter.attachDefault(DatasetsResource.class);
		 * 
		 * router.attach(DatasetsResource.datasets,
		 * createProtectedResource(allDatasetsRouter,"datasets"));
		 */
		/** /task */
		router.attach(TaskResource.resource, new TaskRouter(getContext()));

		/**
		 * OpenSSO login / logout Sets a cookie with OpenSSO token
		 */
		router.attach("/" + OpenSSOUserResource.resource, createOpenSSOLoginRouter());

		Router tdbRouter = new TDBEndpointRouter(getContext());
		router.attach(TDBEndpointRouter.resource, tdbRouter);
		/** /bookmark */
		// router.attach(BookmarkResource.resource,createBookmarksRouter());

		/**
		 * Images, styles, favicons, applets
		 */
		attachStaticResources(router);

		router.attach(String.format("/%s", PolicyResource.resource), PolicyResource.class);

		/**
		 * login/logout for local users. For demo purposes only. Requires
		 * database as in net.idea.restnet.db.aalocal.sql.auth.sql
		 */
		/** /policy - used for testing only */

		// Available only for publishers
		Router protectedRouter = new Router(getContext());
		protectedRouter.attach("/test", UserRolesResource.class);
		RoleAuthorizer ra = new RoleAuthorizer();
		ra.getAuthorizedRoles().add(new DBRole("qmrf_admin", "qmrf_admin"));
		ra.setNext(protectedRouter);

		ChallengeAuthenticator ca = new ChallengeAuthenticatorDBLocal(getContext(), false,
				
				"ambit2/rest/config/ambit2.pref", "tomcat_users");
		ca.setNext(ra);
		router.attach("/protected", ca);
		// router.attach(SwitchUserResource.resource,createGuardGuest(SwitchUserResource.class));

		router.setDefaultMatchingMode(Template.MODE_STARTS_WITH);
		router.setRoutingMode(Router.MODE_BEST_MATCH);

		// StringWriter w = new StringWriter();
		// AmbitApplication.printRoutes(router,">",w);
		// System.out.println(w.toString());

		return router;
	}

	protected Restlet createOpenSSOLoginRouter() {
		Filter userAuthn = new OpenSSOAuthenticator(getContext(), true, "opentox.org",
				new OpenSSOVerifierSetUser(false));
		userAuthn.setNext(OpenSSOUserResource.class);
		return userAuthn;
	}

	protected Restlet createProtectedResource(Restlet router) {
		return createProtectedResource(router, null);
	}

	protected Restlet createProtectedResource(Restlet router, String prefix) {
		Filter authN = new OpenSSOAuthenticator(getContext(), false, "opentox.org", new OpenSSOVerifierSetUser(false));
		OpenSSOAuthorizer authZ = new OpenSSOAuthorizer();
		authZ.setPrefix(prefix);
		authN.setNext(authZ);
		authZ.setNext(router);
		return authN;
	}

	/**
	 * Everything under /query
	 * 
	 * @return
	 */
	protected Router createQueryRouter() {

		Router queryRouter = new MyRouter(getContext());
		// queryRouter.attachDefault(QueryListResource.class);

		/**
		 * PubChem query
		 */
		/*
		 * Router pubchem = new MyRouter(getContext());
		 * queryRouter.attach(PubchemResource.resource,pubchem);
		 * pubchem.attachDefault(PubchemResource.class);
		 * pubchem.attach(PubchemResource.resourceID,PubchemResource.class);
		 */
		/**
		 * CIR query
		 * 
		 * Router cir = new MyRouter(getContext());
		 * queryRouter.attach(CSLSResource.resource,cir);
		 * cir.attachDefault(CSLSResource.class);
		 * cir.attach(CSLSResource.resourceID,CSLSResource.class);
		 * cir.attach(CSLSResource
		 * .resourceID+CSLSResource.representationID,CSLSResource.class);
		 */
		/**
		 * ChEBI query
		 * 
		 * Router chebi = new MyRouter(getContext());
		 * queryRouter.attach(ChEBIResource.resource,chebi);
		 * chebi.attachDefault(ChEBIResource.class);
		 * chebi.attach(ChEBIResource.resourceID,ChEBIResource.class);
		 */
		/**
		 * Compound search /query/compound/lookup
		 * 
		 * Router lookup = new MyRouter(getContext());
		 * queryRouter.attach(CompoundLookup.resource,lookup);
		 * lookup.attachDefault(CompoundLookup.class);
		 * lookup.attach(CompoundLookup.resourceID,CompoundLookup.class);
		 * lookup.
		 * attach(CompoundLookup.resourceID+CompoundLookup.representationID
		 * ,CompoundLookup.class);
		 */
		return queryRouter;
	}

	/**
	 * Check for OpenSSO token and set the user, if available but don't verify
	 * the policy
	 * 
	 * @return
	 */
	protected Restlet createAuthenticatedOpenResource(Router router) {
		Filter algAuthn = new OpenSSOAuthenticator(getContext(), false, "opentox.org",
				new OpenSSOVerifierSetUser(false));
		algAuthn.setNext(router);
		return algAuthn;
	}

	protected TaskStorage<String> createTaskStorage() {
		return new TaskStorage<String>(getName(), getLogger()) {

			@Override
			protected Task<ITaskResult, String> createTask(String user, ICallableTask callable) {

				return new PolicyProtectedTask(user, !(callable instanceof CallablePolicyCreator)) {
					@Override
					public synchronized void setPolicy() throws Exception {

						super.setPolicy();
					}
				};
			}
		};
	}

	/**
	 * Resource /bookmark
	 * 
	 * @return
	 * 
	 * 		protected Restlet createBookmarksRouter() { BookmarksRouter
	 *         bookmarkRouter = new BookmarksRouter(getContext());
	 * 
	 *         Filter bookmarkAuth = new
	 *         OpenSSOAuthenticator(getContext(),false,"opentox.org"); Filter
	 *         bookmarkAuthz = new BookmarksAuthorizer();
	 *         bookmarkAuth.setNext(bookmarkAuthz);
	 *         bookmarkAuthz.setNext(bookmarkRouter); return bookmarkAuth; }
	 */
	/**
	 * Resource /admin
	 * 
	 * @return
	 */
	protected Restlet createAdminRouter() {
		AdminRouter adminRouter = new AdminRouter(getContext()) {
			@Override
			protected void init() {
				super.init();
				attach(String.format("/%s", RESTPolicyResource.resource), RESTPolicyResource.class);
				attach(String.format("/%s/{%s}", RESTPolicyResource.resource, RESTPolicyResource.resourceid),
						RESTPolicy.class);
			}
		};
		// DBCreateAllowedGuard dbguard = new DBCreateAllowedGuard();
		// dbguard.setNext(adminRouter);
		// return dbguard;
		return adminRouter;
	}

	/**
	 * /ontology RDF playground, not used currently
	 * 
	 * @return
	 */
	protected Restlet createRDFPlayground() {
		// test removed, there is ontology service
		// router.attach(RDFGraphResource.resource,RDFGraphResource.class);
		// router.attach(RDFGraphResource.resource+"/test",OntologyPlayground.class);

		// router.attach(OntologyResource.resource, OntologyResource.class);
		// router.attach(OntologyResource.resourceID, OntologyResource.class);
		// router.attach(OntologyResource.resourceTree, OntologyResource.class);
		return null;
	}

	/**
	 * Resource protection via local MySQL/ Ambit database users. Not used
	 * currenty;
	 * 
	 * @return
	 */
	protected Restlet createLocalUsersGuard() {

		//

		/**
		 * // These are users from the DB // /user DBVerifier verifier = new
		 * DBVerifier(this); Router usersRouter = new MyRouter(getContext());
		 * usersRouter.attachDefault(UserResource.class); // /user/{userid}
		 * Router userRouter = new MyRouter(getContext());
		 * userRouter.attachDefault(UserResource.class);
		 * usersRouter.attach(UserResource.resourceID,userRouter); //
		 * authentication mandatory for users resource Filter guard =
		 * createGuard(verifier,false); // Simple authorizer MethodAuthorizer
		 * authorizer = new MethodAuthorizer();
		 * authorizer.getAnonymousMethods().add(Method.GET);
		 * authorizer.getAnonymousMethods().add(Method.HEAD);
		 * authorizer.getAnonymousMethods().add(Method.OPTIONS);
		 * authorizer.getAuthenticatedMethods().add(Method.PUT);
		 * authorizer.getAuthenticatedMethods().add(Method.DELETE);
		 * authorizer.getAuthenticatedMethods().add(Method.POST);
		 * authorizer.getAuthenticatedMethods().add(Method.OPTIONS);
		 * authorizer.setNext(usersRouter); guard.setNext(authorizer);
		 * router.attach(UserResource.resource, guard);
		 * router.attach(UserResource.resource, usersRouter);
		 */
		return null;
	}

	/**
	 * Images, styles, icons Works if packaged as war only!
	 * 
	 * @return
	 */
	protected void attachStaticResources(Router router) {
		/*
		 * router.attach("/images",new Directory(getContext(),
		 * LocalReference.createFileReference("/webapps/images")));
		 */

		Directory metaDir = new Directory(getContext(), "war:///META-INF");
		Directory imgDir = new Directory(getContext(), "war:///images");
		Directory jmolDir = new Directory(getContext(), "war:///jmol");
		Directory jmeDir = new Directory(getContext(), "war:///jme");
		Directory styleDir = new Directory(getContext(), "war:///style");
		Directory jquery = new Directory(getContext(), "war:///jquery");

		router.attach("/meta/", metaDir);
		router.attach("/images/", imgDir);
		router.attach("/jmol/", jmolDir);
		router.attach("/jme/", jmeDir);
		router.attach("/jquery/", jquery);
		router.attach("/style/", styleDir);
		router.attach("/favicon.ico", FavIconResource.class);
		router.attach("/favicon.png", FavIconResource.class);
	}

	/**
	 * Standalone, for testing mainly
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// Create a component
		Component component = new RESTNetComponent();
		final Server server = component.getServers().add(Protocol.HTTP, 8080);
		component.start();

		System.out.println("Server started on port " + server.getPort());
		System.out.println("Press key to stop server");
		System.in.read();
		System.out.println("Stopping server");
		component.stop();
		System.out.println("Server stopped");
	}

	protected ChallengeAuthenticator createGuard(Verifier verifier, boolean optional) {

		Enroler enroler = new Enroler() {
			public void enrole(ClientInfo subject) {
				System.out.println(subject);

			}
		};
		// Create a Guard
		ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(), optional, ChallengeScheme.HTTP_BASIC,
				"ambit2") {
			@Override
			protected boolean authenticate(Request request, Response response) {
				return super.authenticate(request, response);
			}
		};
		guard.setVerifier(verifier);
		guard.setEnroler(enroler);

		return guard;
	}

	public static String printRoutes(Restlet re, String delimiter, StringWriter b) {

		while (re != null) {

			b.append(re.getClass().toString());
			b.append('\t');
			if (re instanceof Finder) {
				b.append(((Finder) re).getTargetClass().getName());
				b.append('\n');
				re = null;
			} else if (re instanceof Filter)
				re = ((Filter) re).getNext();
			else if (re instanceof Router) {
				b.append('\n');
				RouteList list = ((Router) re).getRoutes();
				for (Route r : list) {

					b.append(delimiter);
					b.append(r.getTemplate().getPattern());
					b.append('\t');
					b.append(r.getTemplate().getVariableNames().toString());
					printRoutes(r.getNext(), '\t' + delimiter + r.getTemplate().getPattern(), b);
				}

				break;
			} else {
				break;
			}

		}

		return b.toString();

	}

}
