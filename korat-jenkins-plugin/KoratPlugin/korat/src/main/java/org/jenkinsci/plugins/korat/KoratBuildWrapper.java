package org.jenkinsci.plugins.korat;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Set the name twice.
 *
 * Once early on in the build, and another time later on.
 *
 * @author Kohsuke Kawaguchi
 */
public class KoratBuildWrapper extends BuildWrapper implements MatrixAggregatable {

    public final String template;   

    @DataBoundConstructor
    public KoratBuildWrapper(String template) {
        this.template = template;       
    }

    @Override
    public BuildWrapper.Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener)
              throws IOException, InterruptedException{
    	
    	return  new Environment(){    		
    	};
    }
    
    //THis method is called before SCM checkout
    public void preCheckout(AbstractBuild build, Launcher launcher, BuildListener listener)
              throws IOException, InterruptedException{
    	//This is for debugging. Probably some other way is better to get filename and function name
/*    	String funcname = "preCheckout";
    	String filename = "KoratBuildWrapper";
    	listener.getLogger().println(filename + " " + funcname + " Start");
    	
    	//Get Workspace
    	FilePath fpws = build.getWorkspace();
    	listener.getLogger().println(filename + " " + funcname + " fpws.getName " + fpws.getName());
    	listener.getLogger().println(filename + " " + funcname + " fpws.getBaseName " + fpws.getBaseName());
    	listener.getLogger().println(filename + " " + funcname + " fpws.getRemote " + fpws.getRemote());
    	
    	//Get Module Root
       	FilePath fpmr = build.getModuleRoot();
    	listener.getLogger().println(filename + " " + funcname + " fpmr.getName " + fpmr.getName());
    	listener.getLogger().println(filename + " " + funcname + " fpmr.getBaseName " + fpmr.getBaseName());
    	listener.getLogger().println(filename + " " + funcname + " fpmr.getRemote " + fpmr.getRemote());
    	
    	//Go through all the files in the workspace
    	List<FilePath> l =   fpws.list();
    	Iterator<FilePath> ifp = l.iterator();
    	listener.getLogger().println(filename + " " + funcname + "Going through workspace @" + fpws.getRemote());
    	int num_dir=0, num_files=0;
    	while(ifp.hasNext()){
    		FilePath cur_fp = ifp.next();
    		if(cur_fp.isDirectory()){
    			listener.getLogger().println(filename + " " + funcname + " Found directory: " + cur_fp.getName());
    			num_dir++;
    		}
    		else{
    			listener.getLogger().println(filename + " " + funcname + " Found file: " + cur_fp.getName());
    			num_files++;
    		}
    	}
    	listener.getLogger().println(filename + " " + funcname + "Found " + num_dir + " dirs and " + num_files + " files in workspace @" + fpws.getRemote());
    	
    	listener.getLogger().println(filename + " " + funcname + " End");
*/    	
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {
        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Enable Korat Plugin";
        }
    }

	public MatrixAggregator createAggregator(MatrixBuild build,
			Launcher launcher, BuildListener listener) {
		// TODO Auto-generated method stub
		return null;
	}

}
