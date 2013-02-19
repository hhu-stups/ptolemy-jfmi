/* An Functional Mock-up Interface Real Type.

 Copyright (c) 2012 The Regents of the University of California.
 All rights reserved.
 Permission is hereby granted, without written agreement and without
 license or royalty fees, to use, copy, modify, and distribute this
 software and its documentation for any purpose, provided that the above
 copyright notice and the following two paragraphs appear in all copies
 of this software.

 IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
 FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.

 PT_COPYRIGHT_VERSION_2
 COPYRIGHTENDKEY

 */
package org.ptolemy.fmi.type;

import org.w3c.dom.Element;

///////////////////////////////////////////////////////////////////
//// FMIRealType

/**
 * An Functional Mock-up Interface type that represents a Real.
 * 
 * <p>A Functional Mock-up Unit file is a .fmu file in zip format that
 * contains a .xml file named "modelDescription.xml".  In that file,
 * the ModelVariables element may contain elements such as
 * ScalarVariable that in turn may contain elements like Real.  This
 * class represents the Real type.</p>
 *
 * <p>FMI documentation may be found at
 * <a href="http://www.modelisar.com/fmi.html">http://www.modelisar.com/fmi.html</a>.
 * </p>
 * 
 * @author Christopher Brooks
 * @version $Id: FMIRealType.java 63411 2012-04-23 03:58:05Z cxh $
 * @Pt.ProposedRating Red (cxh)
 * @Pt.AcceptedRating Red (cxh)
 */
public class FMIRealType extends FMIType {
    /** Construct a Real FMU variable.
     *  @param name The name of this variable.
     *  @param description A description of this variable.
     *  @param element The XML element whose attributes are used to
     *  set the fields of this object.
     */
    public FMIRealType(String name, String description, Element element) {
        super(name, description, element);
        if (element.hasAttribute("start")) {
            start = Double.valueOf(element.getAttribute("start"));
        }
    }

    /** Return the string value of the base element.
     *  @return The string value.
     */
    public String toString() {
        return Double.toString(start);
    }

    // FIXME: need more documentation and to describe other variables.

    /** The starting value of this real. */
    public double start;
}
