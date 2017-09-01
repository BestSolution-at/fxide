package at.bestsolution.fxide.base.vm.resources.e4;

import java.util.List;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.code.editor.services.EditorOpener;
import org.eclipse.fx.core.adapter.Adapt;

public class OpenFilesHandler {
	@Execute
	public void openFiles(@Named("fileURIs") @Adapt List<String> fileURIs, EditorOpener editorOpener) {
		for( String f : fileURIs ) {
			editorOpener.openEditor(f);
		}
	}
}
