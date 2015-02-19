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
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
    	String funcname = "perform";
    	String filename = "KoratBuilder";
        return true;
    }
    
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        public String getDisplayName() {
            return "Run Korat";
        }


    }
}

