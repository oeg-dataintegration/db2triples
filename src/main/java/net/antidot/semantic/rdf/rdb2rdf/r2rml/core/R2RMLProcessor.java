/* 
 * Copyright 2011-2013 Antidot opensource@antidot.net
 * https://github.com/antidot/db2triples
 * 
 * This file is part of DB2Triples
 *
 * DB2Triples is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * DB2Triples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/***************************************************************************
 *
 * R2RML : R2RML Mapper
 *
 * The R2RML Mapper constructs an RDF dataset from
 * 	a R2RML Mapping document and a MySQL database.  
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import net.antidot.sql.model.core.DriverType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;

public abstract class R2RMLProcessor {
	
	// Log
	protected static Log log = LogFactory.getLog(R2RMLProcessor.class);
	
	// JDBC used driver
	static InheritableThreadLocal<DriverType> driverType = new InheritableThreadLocal<>();
	
	/**
	 * Gets the current driver type.
	 * 
	 * Note: using ThreadLocal to still be able to use static method (required internally all over the place)
	 * @return
	 */
	public static DriverType getDriverType() {
	    return driverType.get();
	}	
	
	public static void setDriverType(DriverType dt) {
		driverType.set(dt);
	}
	
	/**
	 * Convert a database into a RDF graph from a database Connection
	 * and a R2RML instance (with native storage).
	 * @throws R2RMLDataError 
	 * @throws InvalidR2RMLSyntaxException 
	 * @throws InvalidR2RMLStructureException 
	 * @throws IOException 
	 * @throws RDFParseException 
	 * @throws RepositoryException 
	 */
	public static SesameDataSet convertDatabase(Connection conn,
			String pathToR2RMLMappingDocument, String baseIRI, String pathToNativeStore, DriverType driver) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException, R2RMLDataError, InvalidR2RMLStructureException, InvalidR2RMLSyntaxException, RepositoryException, RDFParseException, IOException {
		R2RMLEngine r2e = new R2RMLEngine(conn);
		return r2e.doConvertDatabase(conn, pathToR2RMLMappingDocument, baseIRI, pathToNativeStore, driver);
	}
	
	/**
	 * Convert a MySQL database into a RDF graph from a database Connection
	 * and a R2RML instance.
	 * @throws R2RMLDataError 
	 * @throws InvalidR2RMLSyntaxException 
	 * @throws InvalidR2RMLStructureException 
	 * @throws IOException 
	 * @throws RDFParseException 
	 * @throws RepositoryException 
	 */
	public static SesameDataSet convertDatabase(Connection conn,
			String pathToR2RMLMappingDocument,  String baseIRI, DriverType driver) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException, R2RMLDataError, InvalidR2RMLStructureException, InvalidR2RMLSyntaxException, RepositoryException, RDFParseException, IOException {
		return convertDatabase(conn, pathToR2RMLMappingDocument, baseIRI, null, driver);
	}


}
