package at.bestsolution.fxide.jdt.editor;

import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.code.editor.services.ContextInformation;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jface.text.IDocument;

@SuppressWarnings("restriction")
public class JDTCompletionProposal implements CompletionProposal {
	private org.eclipse.jdt.core.CompletionProposal jdtProposal;

	public JDTCompletionProposal(org.eclipse.jdt.core.CompletionProposal jdtProposal) {
		this.jdtProposal = jdtProposal;
//		System.err.println(String.valueOf(jdtProposal.getName()) + " => " + Flags.isPublic(jdtProposal.getFlags()));
	}

	public org.eclipse.jdt.core.CompletionProposal getJdtProposal() {
		return jdtProposal;
	}

	@Override
	public CharSequence getLabel() {
		// TODO Auto-generated method stub
		return jdtProposal.getName() != null ? String.valueOf(jdtProposal.getName()) : "<unknown>";
	}

	@Override
	public void apply(IDocument document) {
		// TODO Auto-generated method stub

	}

	@Override
	public TextSelection getSelection(IDocument document) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContextInformation getContextInformation() {
		// TODO Auto-generated method stub
		return null;
	}

}
