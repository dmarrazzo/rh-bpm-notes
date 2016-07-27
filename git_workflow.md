# Tentative Git workflow for RH BPMS
## Introduction
The following document aims to provide an isolated development environment for each BPM developer.

Each developer has:

 - its own Business Central instance
 - a local git repository that should synchronized with the local BC

The team shares the consolidated work in a shared git repository, let's call it "team".
In this simple model each developer can push changes in the team repository.

![repositories schema](./imgs/git_workflow_01.png)

** WARNING ** Except for really simple cases, the graphical assets cannot be merged (BPMN diagram, forms, etc). So it's VITAL to ensure that developers adhere to a strict discipline avoiding to modify the same artifact in parallel.

## Gitlab set up
Gitlab repository `http://gitlab.consulting.redhat.com`

Create your username and password

Git globals to use the gitlab

    git config --global user.name "Donato Marrazzo"
    git config --global user.email "dmarrazzo@redhat.com"

## Project set up for first developer (the repository creator)
1. Create a repository in BC
2. Clone it locally 

    ```
    $ git clone ssh://127.0.0.1:8001/<repo_name>
    ```
    
3. Create and checkout your branch (each developer has its own branch)

    ```
    $ git checkout -b <dev1_branch>
    ```

4. Push the new branch in the BC repo (origin)

    ```
    $ git push origin <dev1_branch>
    ```

    If you find problem pushing, you can try the *force* option: `git push --all -f`

5. Restart the BPMS in order to reload the new repository structure


6. In the BC select your branch, this operation will ensure that your next changes will stored on the your branch.

    ![Repository Editor](./imgs/git_workflow_02.png)

    *Note: if you cannot see the new branch in the Project Explorer, try to select again tree structure (from org unit)* 
    
7. Add the centralized git repository (`https://gitlab.consulting.redhat.com/dmarrazz/elsevier.git`)

    ```
    $ git remote add team <team_repo_url>
    ```
    
8. Push the master branch to the centralized structure

    ```
    $ git push -u team master
    ```

    *Note: your working branch <dev_branch> is not replicated in the centralized repository. This should be fine, because your local branch is full of useless commits.*
    
## Project set up for other developer

Other developer should create the repository in their Business Central environment then add their own branch.

1. Clone the centralized repository

    ![Repository Editor](./imgs/git_workflow_03.png)

2. Clone it locally 

    ```
    $ git clone ssh://127.0.0.1:8001/<repo_name>
    ```
    
3. Create and checkout your branch (each developer has its own branch)

    ```
    $ git checkout -b <dev2_branch>
    ```

4. Push the new branch in the BC repo (origin)

    ```
    $ git push origin <dev2_branch>
    ```

    If you find problem pushing, you can try the *force* option: `git push --all -f`

5. Restart the BPMS in order to reload the new repository structure

6. In the BC select your own branch ("donato" in the picture), this operation will ensure that your next changes will stored on the your branch.

    ![Repository Editor](./imgs/git_workflow_02.png)

    *Note: if you cannot see the new branch in the Project Explorer, try to select again tree structure (from org unit)* 
    
7. Add the centralized git repository (`https://gitlab.consulting.redhat.com/dmarrazz/elsevier.git`)

        $ git remote add team <team_repo_url>
    
8. You can check that your git is correctly set up


        $ git remote show team  
        * remote team
          Fetch URL: https://gitlab.consulting.redhat.com/<your_project>
          Push  URL: https://gitlab.consulting.redhat.com/<your_project>
          HEAD branch: master
          Remote branch:
            master new (next fetch will store in remotes/team)
          Local ref configured for 'git push':
            master pushes to master (up to date)
    

## Usual workflow
Let's use `master` branch for consolidated commits.
When your work worths to be shared (at least you are able to get a clean built), 

    git fetch --all # get changes from internal git server and from team git server
    git checkout master # switch to the master branch
    git merge team/master # merge updates coming from the team
    git merge <dev1_branch> # merge your personal changes
    
If you find conflict use usual git procedure to manual merge the code.

Push the merged work:

    git push -u team master# update the team repository
    git push -u origin # update the internal repository

**Pay Attention:** BPMN diagram are almost impossible to merge, so **BE SURE** that just one person per time works on a specific process diagram.

## Git tips

### Don't type your password every time

    git config --global credential.helper 'cache --timeout=2628000'


