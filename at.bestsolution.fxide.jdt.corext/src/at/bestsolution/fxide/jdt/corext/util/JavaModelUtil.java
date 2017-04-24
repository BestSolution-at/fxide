/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt Chapman, mpchapman@gmail.com - 89977 Make JDT .java agnostic
 *******************************************************************************/
package at.bestsolution.fxide.jdt.corext.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class JavaModelUtil {
	/**
	 * The name of the package-info.java file.
	 * @since 3.8
	 */
	public static final String PACKAGE_INFO_JAVA= "package-info.java"; //$NON-NLS-1$

	/**
	 * The name of the package-info.class file.
	 * @since 3.9
	 */
	public static final String PACKAGE_INFO_CLASS= "package-info.class"; //$NON-NLS-1$

	/**
	 * The name of the package.html file.
	 * @since 3.9
	 */
	public static final String PACKAGE_HTML= "package.html"; //$NON-NLS-1$

	/**
	 * Evaluates if a member in the focus' element hierarchy is visible from
	 * elements in a package.
	 * @param member The member to test the visibility for
	 * @param pack The package of the focus element focus
	 * @return returns <code>true</code> if the member is visible from the package
	 * @throws JavaModelException thrown when the member can not be accessed
	 */
	public static boolean isVisibleInHierarchy(IMember member, IPackageFragment pack) throws JavaModelException {
		int type= member.getElementType();
		if  (type == IJavaElement.INITIALIZER ||  (type == IJavaElement.METHOD && member.getElementName().startsWith("<"))) { //$NON-NLS-1$
			return false;
		}

		int otherflags= member.getFlags();

		IType declaringType= member.getDeclaringType();
		if (Flags.isPublic(otherflags) || Flags.isProtected(otherflags) || (declaringType != null && isInterfaceOrAnnotation(declaringType))) {
			return true;
		} else if (Flags.isPrivate(otherflags)) {
			return false;
		}

		IPackageFragment otherpack= (IPackageFragment) member.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
		return (pack != null && pack.equals(otherpack));
	}

	/**
	 * @param type the type to test
	 * @return <code>true</code> iff the type is an interface or an annotation
	 * @throws JavaModelException thrown when the field can not be accessed
	 */
	public static boolean isInterfaceOrAnnotation(IType type) throws JavaModelException {
		return type.isInterface();
	}

	/**
	 * Returns the package fragment root of <code>IJavaElement</code>. If the given
	 * element is already a package fragment root, the element itself is returned.
	 * @param element the element
	 * @return the package fragment root of the element or <code>null</code>
	 */
	public static IPackageFragmentRoot getPackageFragmentRoot(IJavaElement element) {
		return (IPackageFragmentRoot) element.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
	}

	/**
	 * Helper method that tests if an classpath entry can be found in a
	 * container. <code>null</code> is returned if the entry can not be found
	 * or if the container does not allows the configuration of source
	 * attachments
	 * @param jproject The container's parent project
	 * @param containerPath The path of the container
	 * @param libPath The path of the library to be found
	 * @return IClasspathEntry A classpath entry from the container of
	 * <code>null</code> if the container can not be modified.
	 * @throws JavaModelException thrown if accessing the container failed
	 */
	public static IClasspathEntry getClasspathEntryToEdit(IJavaProject jproject, IPath containerPath, IPath libPath) throws JavaModelException {
		IClasspathContainer container= JavaCore.getClasspathContainer(containerPath, jproject);
		ClasspathContainerInitializer initializer= JavaCore.getClasspathContainerInitializer(containerPath.segment(0));
		if (container != null && initializer != null && initializer.canUpdateClasspathContainer(containerPath, jproject)) {
			return findEntryInContainer(container, libPath);
		}
		return null; // attachment not possible
	}

	/**
	 * Finds an entry in a container. <code>null</code> is returned if the entry can not be found
	 * @param container The container
	 * @param libPath The path of the library to be found
	 * @return IClasspathEntry A classpath entry from the container of
	 * <code>null</code> if the container can not be modified.
	 */
	public static IClasspathEntry findEntryInContainer(IClasspathContainer container, IPath libPath) {
		IClasspathEntry[] entries= container.getClasspathEntries();
		for (int i= 0; i < entries.length; i++) {
			IClasspathEntry curr= entries[i];
			IClasspathEntry resolved= JavaCore.getResolvedClasspathEntry(curr);
			if (resolved != null && libPath.equals(resolved.getPath())) {
				return curr; // return the real entry
			}
		}
		return null; // attachment not possible
	}

	/**
	 * Returns the classpath entry of the given package fragment root. This is the raw entry, except
	 * if the root is a referenced library, in which case it's the resolved entry.
	 *
	 * @param root a package fragment root
	 * @return the corresponding classpath entry
	 * @throws JavaModelException if accessing the entry failed
	 * @since 3.6
	 */
	public static IClasspathEntry getClasspathEntry(IPackageFragmentRoot root) throws JavaModelException {
		IClasspathEntry rawEntry= root.getRawClasspathEntry();
		int rawEntryKind= rawEntry.getEntryKind();
		switch (rawEntryKind) {
			case IClasspathEntry.CPE_LIBRARY:
			case IClasspathEntry.CPE_VARIABLE:
			case IClasspathEntry.CPE_CONTAINER: // should not happen, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=305037
				if (root.isArchive() && root.getKind() == IPackageFragmentRoot.K_BINARY) {
					IClasspathEntry resolvedEntry= root.getResolvedClasspathEntry();
					if (resolvedEntry.getReferencingEntry() != null)
						return resolvedEntry;
					else
						return rawEntry;
				}
		}
		return rawEntry;
	}

	public static boolean isPolymorphicSignature(IMethod method) {
		return method.getAnnotation("java.lang.invoke.MethodHandle$PolymorphicSignature").exists(); //$NON-NLS-1$
	}
}
