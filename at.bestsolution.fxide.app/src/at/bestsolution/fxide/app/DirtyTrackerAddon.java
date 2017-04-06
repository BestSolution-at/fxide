package at.bestsolution.fxide.app;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.fx.code.editor.Constants;
import org.eclipse.fx.code.editor.SourceFileChange;
import org.eclipse.fx.code.editor.SourceFileInput;
import org.eclipse.fx.core.event.EventBus;

@SuppressWarnings("restriction")
public class DirtyTrackerAddon {
	private final MApplication application;
	private final EModelService modelService;

	@Inject
	public DirtyTrackerAddon(MApplication application, EModelService modelService) {
		this.application = application;
		this.modelService = modelService;
	}

	@PostConstruct
	public void init(EventBus eventBus) {
		eventBus.subscribe(Constants.TOPIC_SOURCE_FILE_INPUT_MODIFIED, EventBus.data(this::handleModified));
		eventBus.subscribe(Constants.TOPIC_SOURCE_FILE_INPUT_SAVED, EventBus.data(this::handleSaved));
		eventBus.subscribe(Constants.TOPIC_SOURCE_FILE_INPUT_DISPOSED, EventBus.data(this::handleDisposed));
	}

	private void handleModified(SourceFileChange c) {
		List<MPart> list = modelService.findElements(application, MPart.class, EModelService.IN_ANY_PERSPECTIVE, e -> {
			if( c.input.getURI().equals(e.getPersistedState().get(Constants.DOCUMENT_URL)) ) {
				return true;
			}
			return false;
		});
		list.forEach( m -> m.setDirty(true));
	}

	private void handleSaved(SourceFileInput s) {
		List<MPart> list = modelService.findElements(application, MPart.class, EModelService.IN_ANY_PERSPECTIVE, e -> {
			if( s.getURI().equals(e.getPersistedState().get(Constants.DOCUMENT_URL)) ) {
				return true;
			}
			return false;
		});
		list.forEach( m -> m.setDirty(false));
	}

	private void handleDisposed(SourceFileInput s) {
		List<MPart> list = modelService.findElements(application, MPart.class, EModelService.IN_ANY_PERSPECTIVE, e -> {
			if( s.getURI().equals(e.getPersistedState().get(Constants.DOCUMENT_URL)) ) {
				return true;
			}
			return false;
		});
		list.forEach( m -> m.setDirty(false));
	}
}
