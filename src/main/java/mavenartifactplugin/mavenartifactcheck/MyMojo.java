package mavenartifactplugin.mavenartifactcheck;
 
 
 
import java.io.IOException; 
import java.net.HttpURLConnection; 
import java.net.URL; 
import java.util.regex.Matcher; 
import java.util.regex.Pattern; 
 
 
import org.apache.maven.plugin.AbstractMojo; 
import org.apache.maven.plugin.MojoExecutionException; 
 
 
/** 
 * check for presence of a remote artifact 
 */ 

/**
* @goal exists
*/
public class MyMojo extends AbstractMojo { 

/**
* @parameter expression="${exists.repository}" default-value="${project.distributionManagement.repository.url}"
*/
private String repository; 
 
 
/**
* @parameter expression="${exists.project}" default-value = "${project.groupId}:${project.artifactId}:${project.version}"
*/
private String project; 

 
/**
* @parameter expression="${exists.artifact}" default-value = "${project.artifactId}-${project.version}.pom"
*/
private String artifact; 
 
 
/**
* @parameter expression="${exists.property}"
*/
private String property; 
 
 
/**
* @parameter default-value="false"
*/
private boolean skipIfSnapshot; 

 
private final Pattern GAV_PARSER = Pattern.compile("^([^:]*):([^:]*):([^:]*)$"); 

 
public void execute() throws MojoExecutionException { 
     try { 
     	 //hello
    	 doWork(); 
       
     } catch (Exception e) { // url is causing trouble
       throw new MojoExecutionException(e.getMessage(), e); 
     } 
   } 
 
 
   private void doWork() throws IOException { 
	   
     if(skipIfSnapshot && project.endsWith("-SNAPSHOT")) { 
      getLog().debug("skipping -SNAPSHOT"); 
       return; 
     } 
 
 
    String uri = getFetchUri(); 
    getLog().debug("checking for resource at " + uri) ; 
 
 
     HttpURLConnection con = (HttpURLConnection) new URL(uri).openConnection(); 
     con.setRequestMethod("HEAD"); 
 
     boolean exists = con.getResponseCode() == HttpURLConnection.HTTP_OK; 


     if(exists) throw new IOException("artifact is already existing in wished repository");
     else {getLog().debug("artifact is not existing in wished repository");}
   } 
 
 
   public String getFetchUri() { 

	   getLog().debug("repository before: " + repository);
	   if(repository==null) repository ="http://repo1.maven.org/maven2/";
	   getLog().debug("repository: " + repository);
	   getLog().debug("project:" + project);
	   getLog().debug("artifact" + artifact);
	   
     Matcher matcher = GAV_PARSER.matcher(project); 
     if (!matcher.matches()) { 
       return repository + '/' + project + '/' + artifact; 
     } 
     
     String groupId = matcher.group(1); 
     String artifactId = matcher.group(2); 
     String version = matcher.group(3); 
 
     String finalCheckString = repository + '/' 
         + groupId.replace('.', '/') + '/' 
         + artifactId.replace('.', '/') + '/' 
         + version + '/' 
         + artifact; 
     
     getLog().debug("final string:" + finalCheckString);
     
     return finalCheckString;
     
     
   }

public String getRepository() {
	return repository;
}

public void setRepository(String repository) {
	this.repository = repository;
}

public String getProject() {
	return project;
}

public void setProject(String project) {
	this.project = project;
}

public String getArtifact() {
	return artifact;
}

public void setArtifact(String artifact) {
	this.artifact = artifact;
}

public String getProperty() {
	return property;
}

public void setProperty(String property) {
	this.property = property;
}

public boolean isSkipIfSnapshot() {
	return skipIfSnapshot;
}

public void setSkipIfSnapshot(boolean skipIfSnapshot) {
	this.skipIfSnapshot = skipIfSnapshot;
}

public Pattern getGAV_PARSER() {
	return GAV_PARSER;
} 
 } 

