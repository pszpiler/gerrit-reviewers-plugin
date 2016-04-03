# Gerrit Reviewers Plugin

## Description

This plugin allows to add reviewers in [Gerrit](https://www.gerritcodereview.com/) tool automatically.

Tested with version **2.11.3**

## Idea

If during the patchset upload following conditions are met:

- project name, to which patchset is uploaded, is on whitelist
- group name, to which uploader belongs to, is on whitelist
 
Then:

- add every member of this group to review

Uploader can belong to multiple groups. This principle will be applied to every group, to which uploader belongs to.

Whitelist of projects and groups is defined in configuration file, what is described below.

## Installation

Checkout source:

```shell
git clone git@github.com:pszpiler/gerrit-reviewers-plugin.git
```

Generate jar file `gerrit-reviewers-plugin-1.0.0.jar`

```shell
mvn clean package
```

The file should be inside `target` dir

Copy jar file `gerrit-reviewers-plugin-1.0.0.jar` into `<GERRIT_PATH>/plugins/` dir:

```shell
cp target/gerrit-reviewers-plugin-1.0.0.jar <GERRIT_PATH>/plugins/
```

Based on `reviewers.config.dist` create your own configuration file `reviewers.config`

Adjust configuration file to your needs and copy it into `<GERRIT_PATH>/etc/` dir:

```shell
cp reviewers.config <GERRIT_PATH>/etc/
```

Restart Gerrit:

```shell
sh <GERRIT_PATH>/bin/gerrit.sh restart
```

After these steps plugin should be installed and ready to use.

## Configuration

All configuration is made through one file `<GERRIT_PATH>/etc/reviewers.config`. 

Configuration file example:

```shell
[whitelist "projects"]
project = "^some-project-name$"
project = ".*-performance-tests$"

[whitelist "groups"]
group = "^teamA$"
group = "^team[0-9]+$"
```

"projects" section defines whitelist of project names patterns. Every item on this list is a regular expression.

"groups" section defines whitelist of group names patterns. Every item on this list is a regular expression.

Gerrit needs to be restarted after any configuration change.

## Example

There is a group named GroupA with list of users: A1, A2 and U.

There is a group named GroupB with list of users: B1, B2 and U.

There is a group named GroupC with list of users: C1, C2.

There is a project named ProjectA.

Uploader U belongs to groups A and B.

Configuration file `reviewers.config` states:

```shell
[whitelist "projects"]
project = "^ProjectA$"

[whitelist "groups"]
group = "^GroupA$"
group = "^GroupC$"
```

If uploader U upload change into ProjectA, then user A1 and A2 will be automatically added as reviewers to this change.

Users B1, B2 won't be added, because GroupB is not on the whitelist.

Users C1, C2 won't be added, because uploader U doesn't belong to GroupC.
