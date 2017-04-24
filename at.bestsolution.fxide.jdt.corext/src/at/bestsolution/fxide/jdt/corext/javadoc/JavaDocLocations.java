/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package at.bestsolution.fxide.jdt.corext.javadoc;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import at.bestsolution.fxide.jdt.corext.util.JavaModelUtil;

public class JavaDocLocations {
	private static final String JAR_PROTOCOL= "jar"; //$NON-NLS-1$
	private static final QualifiedName PROJECT_JAVADOC= new QualifiedName("org.eclipse.jdt.ui", "project_javadoc_location"); //$NON-NLS-1$

	/**
	 * Returns the location of the Javadoc.
	 *
	 * @param element whose Javadoc location has to be found
	 * @param isBinary <code>true</code> if the Java element is from a binary container
	 * @return the location URL of the Javadoc or <code>null</code> if the location cannot be found
	 * @throws JavaModelException thrown when the Java element cannot be accessed
	 * @since 3.9
	 */
	public static String getBaseURL(IJavaElement element, boolean isBinary) throws JavaModelException {
		if (isBinary) {
// TODO not ported
//			// Source attachment usually does not include Javadoc resources
//			// => Always use the Javadoc location as base:
//			URL baseURL= JavaUI.getJavadocLocation(element, false);
//			if (baseURL != null) {
//				if (baseURL.getProtocol().equals(JAR_PROTOCOL)) {
//					// It's a JarURLConnection, which is not known to the browser widget.
//					// Let's start the help web server:
//					URL baseURL2= PlatformUI.getWorkbench().getHelpSystem().resolve(baseURL.toExternalForm(), true);
//					if (baseURL2 != null) { // can be null if org.eclipse.help.ui is not available
//						baseURL= baseURL2;
//					}
//				}
//				return baseURL.toExternalForm();
//			}
		} else {
			IResource resource= element.getResource();
			if (resource != null) {
				/*
				 * Too bad: Browser widget knows nothing about EFS and custom URL handlers,
				 * so IResource#getLocationURI() does not work in all cases.
				 * We only support the local file system for now.
				 * A solution could be https://bugs.eclipse.org/bugs/show_bug.cgi?id=149022 .
				 */
				IPath location= resource.getLocation();
				if (location != null)
					return location.toFile().toURI().toString();
			}
		}
		return null;
	}

	public static URL getJavadocBaseLocation(IJavaElement element) throws JavaModelException {
		if (element.getElementType() == IJavaElement.JAVA_PROJECT) {
			return getProjectJavadocLocation((IJavaProject) element);
		}

		IPackageFragmentRoot root= JavaModelUtil.getPackageFragmentRoot(element);
		if (root == null) {
			return null;
		}

		if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
			IClasspathEntry entry= root.getResolvedClasspathEntry();
			URL javadocLocation= getLibraryJavadocLocation(entry);
			if (javadocLocation != null) {
				return getLibraryJavadocLocation(entry);
			}
			entry= root.getRawClasspathEntry();
			switch (entry.getEntryKind()) {
				case IClasspathEntry.CPE_LIBRARY:
				case IClasspathEntry.CPE_VARIABLE:
					return getLibraryJavadocLocation(entry);
				default:
					return null;
			}
		} else {
			return getProjectJavadocLocation(root.getJavaProject());
		}
	}

	public static URL getProjectJavadocLocation(IJavaProject project) {
		if (!project.getProject().isAccessible()) {
			return null;
		}
		try {
			String prop= project.getProject().getPersistentProperty(PROJECT_JAVADOC);
			if (prop == null) {
				return null;
			}
			return parseURL(prop);
		} catch (CoreException e) {
			//TODO
			e.printStackTrace();
		}
		return null;
	}

	public static URL getLibraryJavadocLocation(IClasspathEntry entry) {
		if (entry == null) {
			throw new IllegalArgumentException("Entry must not be null"); //$NON-NLS-1$
		}

		int kind= entry.getEntryKind();
		if (kind != IClasspathEntry.CPE_LIBRARY && kind != IClasspathEntry.CPE_VARIABLE) {
			throw new IllegalArgumentException("Entry must be of kind CPE_LIBRARY or CPE_VARIABLE"); //$NON-NLS-1$
		}

		IClasspathAttribute[] extraAttributes= entry.getExtraAttributes();
		for (int i= 0; i < extraAttributes.length; i++) {
			IClasspathAttribute attrib= extraAttributes[i];
			if (IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME.equals(attrib.getName())) {
				return parseURL(attrib.getValue());
			}
		}
		return null;
	}

	/**
	 * Parse a URL from a String. This method first tries to treat <code>url</code> as a valid, encoded URL.
	 * If that didn't work, it tries to recover from bad URLs, e.g. the unencoded form we used to use in persistent storage.
	 *
	 * @param url a URL
	 * @return the parsed URL or <code>null</code> if the URL couldn't be parsed
	 * @since 3.9
	 */
	public static URL parseURL(String url) {
		try {
			try {
				return new URI(url).toURL();
			} catch (URISyntaxException e) {
				try {
					// don't log, since we used to store bad (unencoded) URLs
					if (url.startsWith("file:/")) { //$NON-NLS-1$
						// workaround for a bug in the 3-arg URI constructor for paths that contain '[' or ']':
						return new URI("file", null, url.substring(5), null).toURL(); //$NON-NLS-1$
					} else {
						return URIUtil.fromString(url).toURL();
					}
				} catch (URISyntaxException e1) {
					// last try, not expected to happen
					//TODO
					e.printStackTrace();
					return new URL(url);
				}
			}
		} catch (MalformedURLException e) {
			//TODO
			e.printStackTrace();
			return null;
		}
	}
}
