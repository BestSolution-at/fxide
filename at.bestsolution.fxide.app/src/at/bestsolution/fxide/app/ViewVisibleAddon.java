package at.bestsolution.fxide.app;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.prefs.BackingStoreException;

public class ViewVisibleAddon {
	@PostConstruct
	void init(MApplication application, EModelService modelService) {
		try {
			MPart p = (MPart) modelService.find("at.bestsolution.fxide.app.part.console-view", application);
			IEclipsePreferences node = InstanceScope.INSTANCE.getNode("at.bestsolution.fxide.app.part.console-view");
			node.putBoolean("visible", p.isVisible());
			node.flush();
			node.addPreferenceChangeListener( e -> {
				boolean b = Boolean.parseBoolean((String)e.getNewValue());
				p.setVisible(b);
			});
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		List<MPart> list = modelService.findElements(application, MPart.class, EModelService.ANYWHERE, e -> Boolean.TRUE);
//		for( MPart l : list ) {
//			InstanceScope.INSTANCE.getNode(qualifier);
//		}
	}
}
