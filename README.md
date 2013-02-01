# fanClub

**fanClub** is an open-source Android app that provides a platform to crowdsource music playlist creation among friends.

## git/Github Workflow

In order to write good code for our project, we are going to fork the main project into our own private repositories and pull in changes to the main repo through pull requests, also known as the Fork & Pull Model.This adds a little complexity, but below there are some guides to clarify the how the process works.

### Setting Up Your Own Repo

These steps will create your own private repo and allow you to pull in changes from the main repo.

1. On the Github page for the main repo, click the "Fork" button at the top. This will copy over the repo to your own account.
2. Clone\(copy\) the repo in your own account onto your computer.

    git clone https://github.com/[your-user-name]/[repo-name].git
    # Clones your fork of the repo into the current directory in terminal

3. Add a remote for the main repo to your own. When a repo is cloned, it has a default remote called 'origin' that points to your fork. The following commands will add another remote called 'upstream' that points to the main repo.

    cd [repo-name]
    # Changes the active directory in the prompt to your fork that you just cloned
    git remote add upstream https://github.com/[main-repo-user-name]/[repo-name].git
    # Assigns the original repo to a remote called "upstream"

### Pull in Changes from the Main Repo to Your Repo

These steps will pull in the latest changes into your own repo from the main repo.

    git fetch upstream
    # Fetches any new changes from the original repo
    git merge upstream/master
    # Merges any changes fetched into your working files

Optionally, after these commands, you can commit and push these changes to your repo.

### Push Your Changes to the Main Repo \(Create a Pull Request\)

After committing and pushing your changes to your own repo, click the "Pull Request" button at the top of your project's Github page. Make sure the base \(main\) repo and the head \(your\) repo are correct, as well as the branches. Add a description to the pull request and click the "Send pull request" button.

### Edit a Pull Request

If members want you to make changes to your pull request before accepting it, you can edit your pull request with the changes by following the steps below.

1. Make a commit with the necessary changes and push it to your own repo.
2. On your fork's Github page, click the "Pull Request" button again. You will get a message stating that you already have a pull request, and that you can adjust the commit range for it.
3. Include your new commit with the changes.
