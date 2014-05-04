package org.jenkinsci.plugins.korat;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link KoratBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class KoratBuilder extends Builder {

    public final String finparams;   
    public Boolean forceKoratRun = Boolean.FALSE;
    
    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public KoratBuilder(String finparams, Boolean forceKoratRun) {
        this.finparams = finparams;        
        this.forceKoratRun = forceKoratRun;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getfinparams() {
        return finparams;
    }
    
    public Boolean getforceKoratRun() {
        return forceKoratRun;
    }

    @Override
    //This is the preBuild step
    public boolean prebuild(AbstractBuild build, BuildListener listener) {
    	//This is for debugging. Probably some other way is better to get filename and function name
    	String funcname = "preBuild";
    	String filename = "KoratBuilder";
    	listener.getLogger().println(filename + " " + funcname + " Start");
    	listener.getLogger().println(filename + " " + funcname + " Finitization Parameters " + this.finparams);
    	listener.getLogger().println(filename + " " + funcname + " Checkbox Selected " + this.forceKoratRun);
    	
    	//Get Workspace
    	FilePath fpws = build.getWorkspace();
    	
    	listener.getLogger().println("\n================================ Starting scan for class invariant repOK ================================" );
    	listener.getLogger().println("Using project workspace folder " + fpws.getRemote());
    	Path p = Paths.get(fpws.getRemote());
    	
    	try {
        	listener.getLogger().println("Deleting any previously existing runKorat script");
			Files.deleteIfExists(Paths.get("/var/lib/jenkins/userContent/runKorat.sh"));
		} catch (IOException e) {
			listener.getLogger().println("ERROR:" + e);
		}
    	
    	Map<String,String> classInvariantMap = new HashMap<String,String>();
	    SimpleFileVisitor<Path> fv = new SourceCodeFileVisitor(listener,classInvariantMap, this.finparams, this.forceKoratRun);
	    try {
	      Files.walkFileTree(p, fv);
	    } catch (IOException e) {
	    	listener.getLogger().println("ERROR:" + e);
	    }
    	listener.getLogger().println("================================ Finished scan for class invariant repOK ================================\n" );
    	
    	//Get Module Root
       	FilePath fpmr = build.getModuleRoot();
    	listener.getLogger().println(filename + " " + funcname + " End");
    	
    	//Return true if build can continue. 
    	return true;
    }
    
    @Override
    // This is where you 'build' the project.
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
    	//This is for debugging. Probably some other way is better to get filename and function name
    	String funcname = "perform";
    	String filename = "KoratBuilder";
        return true;
    }
    
    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link KoratBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/korat/KoratBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This humaFn readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Run Korat";
        }


    }
}

