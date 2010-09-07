/**
 * Domain Model.
 * This package contains the JAXB-generated domain model.  This application
 * implements the domain model using an XML Schema and JAXB-generated 
 * classes.  This is a non-standard way of implementing a model, but we
 * do not have or need a database so the standard DB driven model is not
 * appropriate.
 * 
 * Instead, the model is implemented in XML Schema and manipulated by binding
 * XML elements into class instances and vice-versa.  This technique has one
 * large benefit that we take advantage of.  Domain objects can be generated
 * or manipulated using XSL Transforms.
 */
package standup.xml;