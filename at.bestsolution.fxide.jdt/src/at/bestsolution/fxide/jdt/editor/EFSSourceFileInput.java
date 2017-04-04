package at.bestsolution.fxide.jdt.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.code.editor.Constants;
import org.eclipse.fx.code.editor.SourceFileInput;
import org.eclipse.fx.core.IOUtils;
import org.eclipse.fx.core.event.EventBus;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

@SuppressWarnings("restriction")
public class EFSSourceFileInput implements SourceFileInput {
	private IDocument document = new Document();
	private IFile file;
	private Map<String, Object> map;
	private final String url;

	@Inject
	public EFSSourceFileInput(IWorkspace workspace, @Named(Constants.DOCUMENT_URL) String url, @Optional EventBus eventBus) {
		this.url = url;
		String path = url.substring("module-file:".length());
		String project = path.substring(0,path.indexOf('/'));
		map = new HashMap<>();

		IProject p = workspace.getRoot().getProject(project);
		file = p.getFile(new Path(path.substring(project.length()+1)));
		readContent();
	}

	@Override
	public void updateData(int offset, int length, String replacement) {
		try {
			document.replace(offset, length, replacement);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		document = null;
		map.clear();
	}

	@Override
	public String getData() {
		return document.get();
	}

	@Override
	public void setData(String data) {
		document.set(data);
	}

	@Override
	public void persist() {
		try(ByteArrayInputStream in = new ByteArrayInputStream(document.get().getBytes(file.getCharset()))) {
			file.setContents(in, true, true, null);
		} catch (IOException | CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> getTransientData() {
		return map;
	}

	@Override
	public String getURI() {
		return url;
	}

	private void readContent() {
		try (InputStream in = file.getContents(true)) {
			document.set(IOUtils.readToString(in, Charset.forName(file.getCharset(true))));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void reload() {
		readContent();
	}

}
