import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLProcessor;
import net.antidot.sql.model.core.DriverType;



@Ignore
@RunWith(Parameterized.class)
public class H2 {

    private static Log log = LogFactory.getLog(H2.class);
    
    private static final String baseURI = "http://example.com/base/";
    
    private static final String driver = DriverType.H2.toString();
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
	
    private MainIT.NormTested tested = null;
    private String directory = null;
    private String r2rmlSuffix = null;

    public H2(MainIT.NormTested tested, String directory, String suffix)
	    throws SQLException, InstantiationException,
	    IllegalAccessException, ClassNotFoundException {
		this.tested = tested;
		this.directory = directory;
		this.r2rmlSuffix = suffix;
    }
    
    @Parameters
    public static Collection<Object[]> getTestsFiles() throws Exception {
     return MainIT.getTestsFiles();
    }
    
    @Test
    public void testNorm() throws Exception {
    	
    	Class.forName(driver);
	    log.info("[W3CTester:testNorm] Run tests for " + tested.name()
		    + " from : " + directory);
	    if(tested.equals( MainIT.NormTested.R2RML)){
	    	testR2RML(this.r2rmlSuffix,this.directory);
	    }
    }

	public void testR2RML(String suffix, String directory ) throws Exception{
		
		String connString = getFreshH2DatabaseConnection(directory+suffix);
		String mapped_filepath = directory+ "/mapped" + suffix+ ".nq";	
		
		SesameDataSet result;
		try(Connection conn = getH2DatabaseConnection(connString)){
			Statement stat = conn.createStatement();

		    stat.execute("runscript from '"+directory+"/create.sql'");
		    conn.commit();
		     
		 	final String test_filepath = directory+ "/r2rml" + suffix+ ".ttl";	
 
		 	File r2rml_def_file = new File(test_filepath);
		 	try{
		 		result = R2RMLProcessor.convertDatabase(conn, r2rml_def_file.getAbsolutePath(), baseURI, new DriverType(driver));
			}	catch (Exception e) {
	 			if (new File(mapped_filepath).exists()) {
	 			    e.printStackTrace();
	 			    fail("R2RML error: " + new File(directory).getName() +"\n Mapping" + test_filepath) ;
	 			}
	 			else {
	 			    log.info("[W3CTester:runR2RML] R2RML data was not valid");
	 			}
	 			return;
 		    }

 		    // Serialize result
 		    //result.dumpRDF(directory + "/mapped" + suffix+ "-db2triples.nq", RDFFormat.NQUADS);

 		    // Load ref
 		    SesameDataSet ref = new SesameDataSet();
 		    try {
 		    	ref.loadDataFromFile(mapped_filepath, RDFFormat.NQUADS);
 		    }catch (Exception e) {
 		    	e.printStackTrace();
 		    	fail("Unable to load ref " + mapped_filepath);
 		    	return;
 		    }
 		    
 		    // Compare
 		    assertTrue("R2RML test failed: " + directory + "- Case: "+suffix, ref.isEqualTo(result));
		}
				

	}
	
	private Connection getH2DatabaseConnection(String connString) throws Exception{
		
        return DriverManager.getConnection(connString) ;
	}
	
	private String getFreshH2DatabaseConnection(String filePath) throws IOException{
		return "jdbc:h2:file:"+tempFolder.newFolder("temp"+filePath.hashCode()).getAbsolutePath();
	}
}
