# Contributing to the Android for Maven Eclipse

The **Android for Maven Eclipse** project follows the
[Github Workflow](http://scottchacon.com/2011/08/31/github-flow.html)
and contributions can be made by sending a
[pull requests](https://help.github.com/articles/creating-a-pull-request)
or [raising issues](https://github.com/rgladwell/m2e-android/issues/new).

##Pull requests should...

###...focus on quality

Code should be readable, maintainable,
[clean](http://www.amazon.co.uk/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882),
follow
[SOLID principals](http://butunclebob.com/ArticleS.UncleBob.PrinciplesOfOod),
not repeat code elsewhere ([DRY](http://c2.com/cgi/wiki?DontRepeatYourself))
and conform to the 
[style guide](https://github.com/rgladwell/m2e-android/blob/master/formatter.xml).

###...be fully testable

All new functionality or bugs should be developed using
[Test Driven Development](http://c2.com/cgi/wiki?TestDrivenDevelopment)
ensuring
[tests are written first](http://www.extremeprogramming.org/rules/testfirst.html),
you regularly [refactor](http://www.jamesshore.com/Blog/Red-Green-Refactor.html),
etc..

To run the tests in Maven execute the following from within the cloned project
folder:

```
$ mvn -Dtycho.showEclipseLog=true verify 
```

You can also run the tests inside Eclipse using the PDE JUnit launcher
(`[com.googlecode.eclipse.m2e.android.test] test`) in the test module. There is
also a launcher to run an instance of Eclipse with the latest m2e-android code in
your local workspace (`[com.googlecode.eclipse.m2e.android] run`).

_Note:_ You may find tests can stall because the ADT is waiting for user
interraction from dialogs. To avoid this execute the following command:

```
$ echo 'adtUsed=true'$'\n'pingId=844 > ~/.android/ddms.cfg
```

###...have a clear intention

Commits should be clear, traceable and grouped according to their intention as
outlined in
[this git style guide](https://github.com/agis-/git-style-guide/blob/master/README.md).
Commit messages should be clear and follow the
[standard git format](http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html).

##Bug reports should be...

###...Clear

Bug reports should have:

 - Precise, descriptive summaries.
 - Informative, concise descriptions.
 - A neutral tone, avoiding complaints or conjecture.

###...Reproducible

Bug reports should contain:

 - The simplest **steps to reproduce** the issue, or...
 - A failing **test fixture** for the bug.

###...Specific

Only publish **one bug** per report accompanied by:

 - A **detailed description** of the issue focusing on the **facts**.
 - **Expected** and **actual results**.
 - **Versions** of software, platform and operating system used.
 - **Crash data**, including [stack traces](http://i.imgur.com/jacoj.jpg), [log
files](https://wiki.eclipse.org/FAQ_Where_can_I_find_that_elusive_.log_file%3F), screenshots and other relevant information.

If you are sendling log files, please ensure that **debug mode** is enabled so we get full log files. To do so please follow these instructions:

  1. Open you [eclipse.ini](https://wiki.eclipse.org/Eclipse.ini) file in a text editor.
  2. At the bottom of the file add the following line:

```
-DM2E_ANDROID_DEBUG=true
```

You should now have debug logging for m2e-android enabled when you restart Eclipse.

###...Unique

Please search for duplicates before reporting a bug and ensure summaries
include relevant keywords to make it easier for others to find duplicates.
