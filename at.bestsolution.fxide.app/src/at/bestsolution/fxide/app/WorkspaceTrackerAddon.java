package at.bestsolution.fxide.app;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.fx.code.editor.Constants;
import org.eclipse.fx.core.ThreadSynchronize;

@SuppressWarnings("restriction")
public class WorkspaceTrackerAddon {
	private final ThreadSynchronize threadSync;
	private final IWorkspace workspace;
	private final MApplication application;
	private final EModelService modelService;

	@Inject
	public WorkspaceTrackerAddon(ThreadSynchronize threadSync, IWorkspace workspace, MApplication application, EModelService modelService) {
		this.threadSync = threadSync;
		this.workspace = workspace;
		this.application = application;
		this.modelService = modelService;
		workspace.addResourceChangeListener(this::handleResourceChanged);
	}

	private void handleResourceChanged(IResourceChangeEvent event) {
		Runnable code = () -> {
			try {
				event.getDelta().accept(this::visitDelta);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};

		if( threadSync.isCurrent() ) {
			code.run();
		} else {
			threadSync.asyncExec( code );
		}
	}

	public boolean visitDelta(IResourceDelta delta) throws CoreException {
		if( delta.getKind() == IResourceDelta.REMOVED && delta.getResource() instanceof IFile ) {
			IFile f = (IFile) delta.getResource();

			String path = "module-file:" + f.getProject().getName() + "/" + f.getProjectRelativePath();
			modelService.findElements(application, null, MPart.class, null).stream()
				.filter( m -> path.equals(m.getPersistedState().get(Constants.DOCUMENT_URL)))
				.forEach( m -> m.setToBeRendered(false));
		}

		return true;
	}

	@PreDestroy
	void close() {
		try {
			workspace.save(true, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
