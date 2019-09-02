# myBitbucket

This plugin for Intellij IDEA allows you to see and approve pull-requests assigned to you in Bitbucket Server. The plugin is integrated with Git to allow checking out corresponding branches.

![plugin example image](https://github.com/BigBurritoInc/BitbucketHelper4Idea/raw/master/src/main/resources/myBitbucket_example01.png)

## Configuring the plugin
To configure the plugin open Idea's Settings window, navigate to **myBibucket** section, enter the base url with protocol, project and repository name (usually any pull-request’s url contains them in the following format: _<base_url>/projects/<project_name>/repos/<repo_name>/pull-requests/id_).

The plugin supports two types of authentication: **Access Token auth** (requires Bitbucket Server 5.5 or higher) and **Basic auth** with login/password

##### Access Token Auth
If you are a happy user of Bitbucket Server version 5.5 or newer, you can use this convenient type of authentication.
To generate a personal access token from within Bitbucket Server go to _Manage account > Account settings > Personal access tokens._
Create a token with a **Write** permission to be able to approve and merge pull requests from the plugin.
Copy it to the myBitbucket Settings window. After you hit OK, plugin should show your pull requests.

##### Basic Auth with login and password
For older versions you need to specify your Bibucket Server login in the Settings window and enter a password
in the "Login" tab of myBitbucket panel. Password is not persisted between sessions due to security reasons.

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
