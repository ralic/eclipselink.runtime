/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.queries;

import java.security.AccessController;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;

/**
 * Manager class that maintains the {@link JPAQueryBuilder} to be used in parsing a JPQL query and
 * converting it to a {@link DatabaseQuery DatabaseQuery} in the the EclipseLink environment. In the
 * absence of designating a query builder specifically ({@link JPAQueryBuilderManager#setQueryBuilder
 * (JPAQueryBuilder)}), the default builder is {@link ANTLRQueryBuilder}.
 *
 * @see JPAQueryBuilder
 * @see ANTLRQueryBuilder
 * @see HermesParser
 *
 * @version 2.4
 * @since 2.2
 * @author John Bracken
 */
public final class JPAQueryBuilderManager {

    /**
     * The {@link JPAQueryBuilder} that will be used to create a {@link DatabaseQuery} by parsing
     * a JPQL query.
     */
    private static JPAQueryBuilder systemQueryBuilder;

    /**
     * Creates the default {@link JPAQueryBuilder} instance.
     *
     * @return The {@link ANTLRQueryBuilder}.
     */
   private static JPAQueryBuilder buildDefaultQueryBuilder() {

   	try {
   		// PERFORMANCE: Load the parser class through reflection so there is no direct dependency
         Class parserClass = loadParserClass();

         if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return (JPAQueryBuilder) AccessController.doPrivileged(
               new PrivilegedNewInstanceFromClass(parserClass)
            );
         }
         return (JPAQueryBuilder) PrivilegedAccessHelper.newInstanceFromClass(parserClass);
      }
      catch (Exception e) {
         e.printStackTrace();
         throw new IllegalStateException("Could not load the parser class." /* TODO: Localize string */, e);
      }
   }

   /**
    * This method returns the {@link JPAQueryBuilder} that has been set for the EclipseLink
    * environment. If no query builder has been explicitly designated, then the {@link
    * ANTLRQueryBuilder} will be used.
    *
    * @return The {@link JPAQueryBuilder} designated for the environment
    */
   public static JPAQueryBuilder getQueryBuilder() {
      if (systemQueryBuilder == null) {
         systemQueryBuilder = buildDefaultQueryBuilder();
      }
      return systemQueryBuilder;
   }

   private static Class loadParserClass() throws Exception {

//      String parserClassName = "org.eclipse.persistence.internal.jpa.jpql.HermesParser";
      String parserClassName = "org.eclipse.persistence.queries.ANTLRQueryBuilder";

      // Use class.forName() to avoid loading parser classes for JAXB
      // Use Class.forName not thread class loader to avoid class loader issues
      if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
         return (Class) AccessController.doPrivileged(
            new PrivilegedClassForName(parserClassName)
         );
      }

      return PrivilegedAccessHelper.getClassForName(parserClassName);
	}

   /**
    * Sets the system {@link JPAQueryBuilder} to be used across the EclipseLink environment.
    *
    * @param queryBuilder The query builder to set
    */
   public static void setQueryBuilder(JPAQueryBuilder queryBuilder) {
      systemQueryBuilder = queryBuilder;
   }
}