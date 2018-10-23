/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.modelrepository.grafico;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

/**
 * Status of Branches
 * 
 * @author Phillip Beauvoir
 */
public class BranchStatus {
    
    public final static String localPrefix = "refs/heads/"; //$NON-NLS-1$
    public final static String remotePrefix = "refs/remotes/origin/"; //$NON-NLS-1$

    public static List<String> getLocalBranchNames(IArchiRepository archiRepo) throws IOException, GitAPIException {
        List<String> list = new ArrayList<String>();
        
        for(Ref ref : getLocalBranchRefs(archiRepo)) {
            list.add(ref.getName());
        }
        
        return list;
    }
    
    public static List<Ref> getLocalBranchRefs(IArchiRepository archiRepo) throws IOException, GitAPIException {
        try(Git git = Git.open(archiRepo.getLocalRepositoryFolder())) {
            return git.branchList().call(); // Local branches
        }
    }
    
    public static List<Ref> getAllBranchRefs(IArchiRepository archiRepo) throws IOException, GitAPIException {
        try(Git git = Git.open(archiRepo.getLocalRepositoryFolder())) {
            return git.branchList().setListMode(ListMode.ALL).call();
        }
    }

    public static List<Ref> getRemoteBranchRefs(IArchiRepository archiRepo) throws IOException, GitAPIException {
        try(Git git = Git.open(archiRepo.getLocalRepositoryFolder())) {
            return git.branchList().setListMode(ListMode.REMOTE).call();
        }
    }
    
    public static String getCurrentLocalBranch(IArchiRepository archiRepo) throws IOException {
        try(Repository repository = Git.open(archiRepo.getLocalRepositoryFolder()).getRepository()) {
            return repository.getFullBranch();
        }
    }
    
    public static String getCurrentRemoteBranch(IArchiRepository archiRepo) throws IOException {
        return getRemoteBranchNameFor(getCurrentLocalBranch(archiRepo));
    }
    
    public static String getRemoteBranchNameFor(String localBranchName) {
        String shortName = getShortName(localBranchName);
        return remotePrefix + shortName;
    }
    
    public static boolean isCurrentBranch(IArchiRepository archiRepo, String branchName) throws IOException {
        return branchName.equals(getCurrentLocalBranch(archiRepo));
    }
    
    public static boolean localBranchExists(IArchiRepository archiRepo, String branchName) throws GitAPIException, IOException {
        String fullName = Constants.R_HEADS + branchName;
        boolean localBranchExists = false;
        
        try(Git git = Git.open(archiRepo.getLocalRepositoryFolder())) {
            for(Ref ref : git.branchList().call()) {
                String name = ref.getName();
                if(Objects.equals(name, fullName)) {
                    localBranchExists = true;
                }
            }
        }

        return localBranchExists;
    }

    public static String getShortName(String branchName) {
        int index = branchName.lastIndexOf("/"); //$NON-NLS-1$
        if(index != -1 && branchName.length() > index) {
            return branchName.substring(index + 1);
        }
        
        return branchName;
    }
    
}