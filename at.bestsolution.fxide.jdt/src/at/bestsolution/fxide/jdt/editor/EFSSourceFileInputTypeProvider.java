package at.bestsolution.fxide.jdt.editor;

import org.eclipse.fx.code.editor.Input;
import org.eclipse.fx.code.editor.services.InputTypeProvider;
import org.osgi.service.component.annotations.Component;

@SuppressWarnings("restriction")
@Component(property="service.ranking:Integer=1")
public class EFSSourceFileInputTypeProvider implements InputTypeProvider {

	@Override
	public Class<? extends Input<?>> getType(String s) {
		return EFSSourceFileInput.class;
	}

	@Override
	public boolean test(String t) {
		return t.startsWith("module-file:");
	}

}
