# Contributing
We expect to accept external contributions to the project in the form of feedback, bug reports and even better - pull requests.

If you have not done so already, please review and honor our [code-of-conduct](CODE_OF_CONDUCT.md).

## Issue Submission
In order for us to help with any technical requests please check that you've completed the following steps:

* Make sure you're evaluating the latest appropriate version of the add-on.
* Used the search feature to ensure that the bug hasn't been reported before
  
[Submit your issue](https://github.com/OrderOfTheBee/ootbee-support-tools/issues/new)

## Review Criteria
We expect all submissions to conform to the [inclusion criteria][inclusion-criteria] outlined by the add-ons comitte
of the [Order of the Bee][ootbee]. This project requires the following additional criteria be met:

- **@AFaust to add any additional criteria here.**

## Git Workflow
We utilize a combination of the [Forking Workflow][git-forking-wf] and the 
[Feature Branch Workflow][git-feature-branching-wf]. We try to maintain a clean linear version history for our
master branch. As such we expect contributions to have one or more organized commits on top of our master commit.
Master and feature commits should not be interleved. If this is confusing or combersome, don't worry about it
too much, we'll simply rebase and squash your commits if/when we merge your pull request.

## Quick Start

- Fork this [project][homepage]
- Checkout your fork on your dev machine
- Add this project as a remote named upstream 
  `git remote add upstream git@github.com:OrderOfTheBee/ootbee-support-tools.git`

At this point you can start working on your contribution.

- Create a branch to work in `git checkout -b YOUR-FEATURE`
- Start hacking :)
- Run `mvn install` to compile the add-on with your changes and publish them to your local maven repository
- Test our your custom build. You could create a project with the SDK and then reference the assets as we have outlined
in our [README.md][readme] or you could grab either the JAR or AMP artifacts from your build and deploy them to a full
fledged Alfresco installation. For complex contributions we expect you to test against the supported versions of Alfresco.
- Commit your changes regularly

You can keep YOUR-FEATURE branch up to date with this projects master branch by running `git pull --rebase upstream master`
from YOUR-FEATURE branch.

As we said above, we expect feature history to be clean. For a simple customization we expect a single commit. If the
feature is more complex where it makes sense to have several clearly broken down commits that is also fine. If
there are multiple commits for the feature, we expect them to be all stacked up on top of our master. The 
`git pull --rebase upstream master` command will help here. Additionally you may need to do an 
[interactive rebase][rebase-docs] in order to restructure your commits with:

```
git fetch upstream
git rebase --interactive upstream/master
````

When you are ready to push your feature branch to your fork on Github for the first time use 
`git push -u origin YOUR_FEATURE`. Subsequently, while you have your branch checked out, you should be able to just
`git push`.

<!--
Before submitting a [pull request](https://github.com/binduwavell/generator-alfresco/pulls) from your feature branch,
make sure you've completed all of the items outlined in the checklist section of our
[pull request template](.github/PULL_REQUEST_TEMPLATE.md).
-->

## Style Guide

- **@AFaust to reference/summarize any style requirements**
- **@AFaust do we want to add any kind of linting to the build/tests?**
- **Maybe just a link to here: https://github.com/OrderOfTheBee/ootbee-support-tools/wiki/Conventions ? ***

Please ensure any pull requests follow the guidelines closely. If you notice existing code which doesn't follow
these practices, feel free to ping us and we will address this.

## Contacting Us

Join our chat room on [Gitter][gitter-url] 

## Pull Request Guidelines

* Please check to make sure that there aren't existing pull requests attempting to address the issue mentioned
* Non-trivial changes should be discussed in an issue first
* Develop in a feature branch, not master
* Write a convincing description of your PR and why we should land it
* If you are not responsive to feedback on your PR we will not accept the PR

When we say we will not accept a PR, we will likely close the PR. You are encouraged to address the concernes
that we will outline before we close it and then re-submit the PR. We really do want contributions. We also
need for them to be of high quality and high value and to not take undue resources away from the features and
enhancements we are working on. Thanks for understanding!

[git-feature-branching-wf]: https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow
[git-forking-wf]: https://www.atlassian.com/git/tutorials/comparing-workflows/forking-workflow
[gitter-url]: https://gitter.im/OrderOfTheBee/support-tools?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge
[homepage]: https://github.com/OrderOfTheBee/ootbee-support-tools
[inclusion-criteria]: https://github.com/OrderOfTheBee/addons/wiki/Inclusion-criteria-overview
[ootbee]: http://orderofthebee.org/
[readme]: ./README.md
[rebase-docs]: https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History
