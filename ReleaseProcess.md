> This guide is for Apache Gearpump Committers only. 

# Release process overview
Release process of Apache Gearpump boils down to the following steps:
* Community decides that we want to release new version
* Release Manager (RM) is chosen
* Release Candidate (RC) is prepared
* RC is voted
* RC is published as the new version

This document explains certain aspects of the process in details.

# Preparing for the release
We try to automate/script things as much as possible in order to make the development process easy (see: https://github.com/apache/incubator-gearpump/tree/master/dev-tools).
Nevertheless, there are some preconditions you need to fulfill in order to be able to release a new version of Apache Gearpump.

## GPG Key - one-time setup
The basic fact in Apache Community is every artifact distributed by the Apache Software Foundation must be easily verifiable (signed, accompanied by one file containing an OpenPGP compatible ASCII armored detached signature and another file containing an MD5 checksum).

Release Manager needs GPG Key in order to sign the artifacts that are about to be published.

Please, take a look at http://www.apache.org/dev/release-signing for instructions on creating the key and other useful info on the subject.

## Required credentials
A person that wnats to prepare Apache Gearpump relase should have:
* Apache account: https://id.apache.org
* Git repos access: git://git.apache.org/incubator-gearpump.git (main repo), git@github.com:apache/incubator-gearpump-site.git (gearpump.apache.com site repo)
* SVN repos access: https://dist.apache.org/repos/dist/dev/incubator/gearpump, https://dist.apache.org/repos/dist/release/incubator/gearpump
* Apache Maven (Nexus) access:  https://repository.apache.org

Basically, if you have Apache identity (https//id.apache.org) and you are a Gearpump committer, you should have all the required credentials granted.

## New committers
Every Apache project contains KEYS file that allows for checking signatures of published artifacts.
When new committer/RM are added for the release, the file should be updated accordingly. 
(See: http://www.apache.org/dev/release-signing#keys-policy for details.)

In this case, we should add their keys to distribution repo (SVN).

When you take a look at https://dist.apache.org/repos/dist/dev/incubator/gearpump/KEYS, for example, you'll find a description on how to add a key to the file.

(Userfull SVN commands are listed in "SVN commands" section [TBD])    


## Repository [set up]
[TBD]
[Fork, add remotes]

## Release branch
* Creating a JIRA ticket for the work (in order trace changes in Git according to our standards) - for example, "Prepare 0.8.3 release"
* Creating a branch in your repo 

## Review JIRA issues
Issues that are addressed for given release should have `Fix version` set to desired value.
This step helps to make sure that all features/fixes planned for the release are included.

The list of the issues can be found on `Versions` page.
    https://issues.apache.org/jira/browse/GEARPUMP/?selectedTab=com.atlassian.jira.jira-projects-plugin:versions-panel

Review JIRA tickets and:
* make sure that all the issues planned for the release have `Fix version` properly set 
* make sure that issues that have been dealt with are marked as resolved (JIRA was not updated)
* decide on the issues left (open) if they need to be fixed (and the release should wait for them) or moved to the next release

Make sure to discuss problematic issues with the team (dev@ mailing list), especially potentially postponed or blocking issues.

Also, before beginning, it's a good idea to ask the folks (dev@) to review and clean up the issues they have been working on.


# Prepare release candidate
In this step, we prepare a Release Candidate to vote upon. 

The process includes:
* Checking out the release branch
* Updating version numbers
* Verifying if the build works
* Publishing artifacts to distribution repository (SVN)
* Tagging the sources

[TBD]

[Describe the steps above]

# Voting for the candidate

When you are convinced that this RC is ready to be shared with the world, you start the voting.

(See http://www.apache.org/foundation/voting.html for the model of voting encouraged by Apache Foundation.)

There are two phases here: 
* "internal" voting,  among committers
* "external" voting, among IPMC Community

## "Internal" voting

Mail template:

To: dev@gearpump.incubator.apache.org

Subject:  

|         |   | 
|---------|---|
|to |dev@gearpump.incubator.apache.org  |
|subject |[VOTE] Release of Apache Gearpump 0.8.3-incubating, release candidate #1 |

```
Hi Gearpump Community,

This is a call for a discussion releasing Apache Gearpump
0.8.3-incubating, release candidate #1.

The source and binary tarballs, including signatures, digests, etc.
can be found at:
https://dist.apache.org/repos/dist/dev/incubator/gearpump/0.8.3-incubating/RC1/

Release artifacts are signed with the key with fingerprint:
3F12 81A2 DB58 0842 5ABA  6962 D8A8 4FBC 0A83 B291

The KEYS file is available here:
https://dist.apache.org/repos/dist/dev/incubator/gearpump/KEYS

The tag to be voted upon is:
https://git-wip-us.apache.org/repos/asf?p=incubator-gearpump.git;a=shortlog;h=refs/tags/0.8.3-RC1

The release hash is:
https://git-wip-us.apache.org/repos/asf?p=incubator-gearpump.git;a=commit;h=80f49154428cd18b5a27d946b8c9536124849cc9

For information about the contents of this release see:
https://issues.apache.org/jira/browse/GEARPUMP-294?jql=project%20%3D%20GEARPUMP%20AND%20status%20in%20(Resolved%2C%20Closed)%20AND%20fixVersion%20%3D%200.8.3

This vote will be open for 72 hours (Sunday, April 9, 2017 at 4:00 PM PST).    

Please download the release candidate and evaluate the necessary items
including checking hashes, signatures, build from source, run the
binary artifacts in the binary release and test.
Please vote:

[ ] +1 Release this package as gearpump-0.8.3
[ ] +0 no opinion
[ ] -1 Do not release this package because because...

Thanks,
Karol
```


## "External" voting

Mail template for starting the vote:

|         |   | 
|---------|---|
|to |general@incubator.apache.org |
|cc |dev@gearpump.incubator.apache.org |
|subject |[VOTE] Apache Gearpump (incubating) 0.8.3-RC1 |

```
Hi IPMC Community,

The PPMC vote to release Apache Gearpump (incubating) 0.8.3-RC1 has passed.
We would like to now submit this release candidate to the IPMC.

The PPMC vote thread is here:
https://lists.apache.org/thread.html/0e1022d2f3b5b2a2b879e4c278d7fc44d094058550d47ae7e07702ec@%3Cdev.gearpump.apache.org%3E

The source and binary tarballs, including signatures, digests, etc.
can be found at:
https://dist.apache.org/repos/dist/dev/incubator/gearpump/0.8.3-incubating/RC1/

Release artifacts are signed with the key with fingerprint:
3F12 81A2 DB58 0842 5ABA  6962 D8A8 4FBC 0A83 B291

The KEYS file is available here:
https://dist.apache.org/repos/dist/dev/incubator/gearpump/KEYS

The tag to be voted upon is:
https://git-wip-us.apache.org/repos/asf?p=incubator-gearpump.git;a=shortlog;h=refs/tags/0.8.3-RC1

The release hash is:
https://git-wip-us.apache.org/repos/asf?p=incubator-gearpump.git;a=commit;h=80f49154428cd18b5a27d946b8c9536124849cc9

For information about the contents of this release see:
https://issues.apache.org/jira/browse/GEARPUMP-294?jql=project%20%3D%20GEARPUMP%20AND%20status%20in%20(Resolved%2C%20Closed)%20AND%20fixVersion%20%3D%200.8.3

This vote will be open for at least 72 hours (Thursday, April 13, 2017 at 3:00 AM PST).

Please download the release candidate and evaluate the necessary items
including checking hashes, signatures, build from source, run the
binary artifacts in the binary release and test.
Please vote:

[ ] +1 Release this package as gearpump-0.8.3
[ ] +0 no opinion
[ ] -1 Do not release this package because because...

Thanks,
Karol
```

Mail template for vote results:

|         |   | 
|---------|---|
|to |general@incubator.apache.org |
|cc |dev@gearpump.incubator.apache.org |
|subject |[RESULT][VOTE] Apache Gearpump (incubating) 0.8.3-RC1 |

```
Dear IPMC Community,

I am pleased to announce that the Incubator PMC has approved 0.8.3-RC1
of Apache Gearpump (Incubating) for release as version 0.8.3 (Incubating).

The vote has passed with:
- three binding "+1" votes
- no "0" votes
- no "-1" votes

The votes were (https://www.mail-archive.com/general@incubator.apache.org/msg59297.html):
- +1, Willem Jiang (binding)
- +1, John D. Ament (binding)
- +1, Jean-Baptiste Onofré (binding)
- +1, Wang, Gang1 (non-binding)

Thank you, for your support!

We'll continue with the release now.

Karol,
on behalf of Apache Gearpump PPMC
```



# Doing the release
## Publishing artifacts


## Announcement

Announcement email template:

|         |   | 
|---------|---|
|to |general@incubator.apache.org |
|cc |dev@gearpump.incubator.apache.org |
|subject |[ANNOUNCE] Apache Gearpump 0.8.3-incubating Release |

```
The Apache Gearpump team would like to announce the release of Apache
Gearpump 0.8.3-incubating.

Apache Gearpump (incubating) is a reactive real-time streaming engine based
on the micro-service Actor model. It provides extremely high performance
stream processing while maintaining millisecond latency message delivery.
Apache Gearpump (incubating) enables reusable, composable flows or partial
graphs that can be remotely deployed and executed in a diverse set of
environments. These flows may be deployed and modified at runtime.

More details regarding Apache Gearpump (incubating) can be found here:
http://gearpump.apache.org/

The release artifacts can be downloaded here:
https://dist.apache.org/repos/dist/release/incubator/gearpump/0.8.3-incubating/

The ChangeLog can be found here:
https://issues.apache.org/jira/secure/ReleaseNote.jspa?projectId=12319920&version=12338681

Thanks!
The Apache Gearpump (incubating) Team

DISCLAIMER
Apache Gearpump (incubating) is an effort undergoing incubation at the
Apache Software Foundation (ASF), sponsored by the Apache Incubator PMC.
Incubation is required of all newly accepted projects until a further
review indicates that the infrastructure, communications, and decision
making process have stabilized in a manner consistent with other successful
ASF projects. While incubation status is not necessarily a reflection of
the completeness or stability of the code, it does indicate that the
project has yet to be fully endorsed by the ASF.
```