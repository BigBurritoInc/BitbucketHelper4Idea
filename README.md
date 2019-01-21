# myBitbucket

This plugin for Intellij IDEA allows you to see and approve pull-requests assigned to you in Bitbucket Server. The plugin is integrated with Git to allow checking out corresponding branches.

![plugin example image](https://github.com/BigBurritoInc/BitbucketHelper4Idea/raw/master/src/main/resources/myBitbucket_example01.png)

## Configuring the plugin
To configure the plugin open Idea's Settings window, navigate to **myBibucket** section, enter the base url with protocol, project and repository name (usually any pull-request’s url contains them in the following format: _<base_url>/projects/<project_name>/repos/<repo_name>/pull-requests/id_) and a username.

Then open myBibucket tab and enter your password. This plugin currently supports only basic authorization so it doesn’t store your password between sessions. If everything is fine, you will see a list of pull-requests assigned to you. If you don’t see any, check Idea’s event log for errors.

## Dependencies
Requires “Git Integration” plugin to be enabled to use Git checkout.

## Working with servers that use a self-signed certificate
The plugin performs remote http requests to Bitbucket Server, if your Bitbucket Server uses a self-signed certificate, it needs to be imported to the Idea’s JRE (by default Idea uses a bundled JRE from the <idea_install_dir>/jre64 directory). Use [keytool](https://docs.oracle.com/javase/tutorial/security/toolfilex/rstep1.html) to do that. 

## Compatibility
The plugin is compatible with Intellij IDEA up to 2018.3 and is expected to work with any Bitbucket Server that implements 
Bitbucket Server REST API 1.0
Was tested using IDEA 2017.X, 2018.X and Bitbucket Server v.4.9.1. 

## Reporting an issue
If you find any issue, please report it to [GitHub](https://github.com/BigBurritoInc/BitbucketHelper4Idea/issues) or [email us](mailto:bitbucket.plugin@gmail.com)
