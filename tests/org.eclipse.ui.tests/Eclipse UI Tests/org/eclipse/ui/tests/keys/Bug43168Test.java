/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.tests.keys;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.keys.KeySequenceText;
import org.eclipse.ui.keys.KeySequence;
import org.eclipse.ui.keys.ParseException;
import org.eclipse.ui.tests.util.UITestCase;

/**
 * Tests Bug 43168
 * 
 * @since 3.0
 */
public class Bug43168Test extends UITestCase {

	/**
	 * Constructor for Bug43168Test.
	 * 
	 * @param name
	 *            The name of the test
	 */
	public Bug43168Test(String name) {
		super(name);
	}

	/**
	 * Tests that a <code>StackOverflowError</code> does not occur when
	 * trying to set the key sequence in a key sequence entry widget.
	 * 
	 * @throws ParseException
	 *             If "CTRL+" is not recognized as a key sequence.
	 */
	public void testStackOverflow() throws ParseException {
		Display display = Display.getCurrent();
		Shell shell = new Shell(display);
		shell.setLayout(new RowLayout());
		KeySequenceText text = new KeySequenceText(shell);

		shell.pack();
		shell.open();
		text.setKeySequence(KeySequence.getInstance("CTRL+")); //$NON-NLS-1$
		shell.close();
	}
}
