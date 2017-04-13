package at.bestsolution.fxide.jdt.editor;

import org.eclipse.fx.code.editor.Input;
import org.eclipse.fx.code.editor.services.ProposalComputer;
import org.eclipse.fx.code.editor.services.ProposalComputerTypeProvider;
import org.osgi.service.component.annotations.Component;

@SuppressWarnings("restriction")
@Component
public class JDTProposalComputerTypeProvider implements ProposalComputerTypeProvider {

	@Override
	public Class<? extends ProposalComputer> getType(Input<?> s) {
		return JDTProposalComputer.class;
	}

	@Override
	public boolean test(Input<?> t) {
		return t instanceof JDTSourceFileInput;
	}

}
