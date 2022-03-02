# Contributing

## SAP CX Tools Contributor License Agreement

- You will only submit contributions where you have authored 100% of the content.
- You will only submit contributions to which you have the necessary rights. This means that if you are employed you
  have received the necessary permissions from your employer to make the contributions.
- Whatever content you contribute will be provided under the project license(s).

### Project Licenses

- All modules use [GNU General Public License v3.0](LICENSE.md).

## Commit Messages

As a general rule, the style and formatting of commit messages should follow the guidelines in
[How to Write a Git Commit Message](https://chris.beams.io/posts/git-commit/).

In addition, any commit that is related to an existing issue must reference the issue. For example, if a commit in a
pull request addresses issue \#999, it must contain the following at the bottom of the commit message.

```
Issue: #999
```

## Pull Requests

Our definition of done offers some guidelines on what we expect from a pull request. Feel free to open a pull request
that does not fulfill all criteria, e.g. to discuss a certain change before polishing it, but please be aware that we
will only merge it in case the DoD is met:

- There are no TODOs left in the code
- Method preconditions are checked and documented in the method's Javadoc
- Coding conventions (e.g. for logging) have been followed
- Change is covered by automated tests
- Public API has Javadoc
- Change is documented in user guide and release notes
- All continuous integration builds pass

Please add the following lines to your pull request description:

```markdown
---

I hereby agree to the terms of the SAP CX Tools Contributor License Agreement.
```

## Coding Conventions

### Naming Conventions

Whenever an acronym is included as part of a type name or method name, keep the first letter of the acronym uppercase
and use lowercase for the rest of the acronym. Otherwise, it becomes _impossible_ to perform camel-cased searches in
IDEs, and it becomes potentially very difficult for mere humans to read or reason about the element without reading
documentation (if documentation even exists).

Consider for example a use case needing to support an HTTP URL. Calling the method `getHTTPURL()` is absolutely
horrible in terms of usability; whereas, `getHttpUrl()` is great in terms of usability. The same applies for types
`HTTPURLProvider` vs. `HttpUrlProvider`, etc.

Whenever an acronym is included as part of a field name or parameter name:

- If the acronym comes at the start of the field or parameter name, use lowercase for the entire acronym -- for
  example, `String url;`.
- Otherwise, keep the first letter of the acronym uppercase and use lowercase for the rest of the acronym -- for
  example, `String defaultUrl;`.

### Formatting

#### Code

Code formatting is enforced using the [Spotless](https://github.com/diffplug/spotless) Gradle plugin. You can use
`gradle spotlessApply` to format new code and add missing license headers to source files. Formatter and import order
settings for Eclipse are available in the repository under [conventions/eclipse-formatter-settings.xml](conventions/eclipse-formatter-settings.xml)
and [conventions/eclipse.importorder](conventions/eclipse.importorder), respectively. For IntelliJ IDEA there's a
[plugin](https://plugins.jetbrains.com/plugin/6546) you can use in conjunction with the Eclipse settings.

#### Documentation

Text in `*.md` files should be wrapped at 120 characters whenever technically possible.

In multi-line bullet point entries, subsequent lines should be indented.

### Spelling

Use American English spelling rules when writing documentation as well as for code -- class names, method names, variable names, etc.

### Javadoc

- Javadoc comments should be wrapped after 80 characters whenever possible.
- This first paragraph must be a single, concise sentence that ends with a period (".").
- Place `<p>` on the same line as the first line in a new paragraph and precede `<p>` with a blank line.
- Insert a blank line before at-clauses/tags.
- Favor `{@code foo}` over `<code>foo</code>`.
- Favor literals (e.g., `{@literal @}`) over HTML entities.
- New classes and methods should have `@since ...` annotation.
- Favor `@since 1.0` over `@since 1.0.0`.

### Tests

#### Naming

- All test classes must end with a `Tests` suffix.

#### Assertions

- Use AssertJ when assertions are needed.
- Do not use `org.junit.Assert` or `junit.framework.Assert`.

#### Mocking

- Use either [Mockito](https://github.com/mockito/mockito) or hand-written test doubles.

### Logging

- In general, logging should be used sparingly.
- All logging must be performed via the SLF4j `Logger` façade provided via the SLF4j [LoggerFactory](https://www.slf4j.org/manual.html).
- Log levels and their usage.
  - `ERROR`: extra information (in addition to an Exception) about errors that will halt execution
  - `WARN`: potential usage or configuration errors that should not halt execution
  - `INFO`: information the users might want to know but not by default
  - `DEBUG`: information the developers might want to know to understand execution

## Extensions

All of the extensions have their own repository and are integrated using `git subtree` into this repository or into project. This
procedure was choosen for two major reasons:

1. A centralized repository holding defined releases of the individual extensions and guaranteeing the compatibility with the standard.
1. The individual extension repositories allow a clean integration into projects, i.e. direct integration of the source code.

### How-to use

In order to use an extension from the sapcx.tools you can either download the build artefact from our build pipeline (work in progress) or
integration the extension directly into your repository using git subtree. The following commands shall give you a guidance on how to
integration the extensions into your local repository. Keep in mind, that you need to run these commands from the root of your project.
For the sake of simplicity, we assume that you use the default CCv2 repository layout for your project:

- The following command is used to add an extension "foobar" into your repository:
`git subtree add --squash --message="add extension foobar from SAP CX Tools" --prefix=core-customize/hybris/bin/custom/sapcxtools/foobar git@github.com:sapcxtools/foobar.git main`
- For future updates, please use the following command:
`git subtree pull --squash --message="update extension foobar from SAP CX Tools" --prefix=core-customize/hybris/bin/custom/sapcxtools/foobar git@github.com:sapcxtools/foobar.git main`
- If you want to push changes from the local repository into the extension, please use:
`git subtree push --prefix=core-customize/hybris/bin/custom/sapcxtools/foobar git@github.com:sapcxtools/foobar.git feature/<name-of-your-feature>`

Please note, we typically use `--squash` to reduce the number of commits within your project repository. If you want the whole history
available in your project, feel free to leave out this parameter. Still, we do not recommend this. We also do not recommend to pull other
branches than main. This could lead to conflicts once you want to update back to main.

### Guidelines for the individual extension repositories

Before merging a feature into the `main` branch of an extension, a github workflow sends the feature-branch to the centralized repository.
Let's assume a new feature `feature/my-idea` was pushed via `git subtree push` into extension `foobar`. The workflow checks out the
centralized repository, and creates a branch called `foobar/feature/my-idea`. Then it performs a `git subtree pull` command without
`--squash` and pointing to the feature branch `feature/my-idea` from the `foobar` extension. It pushes the new branch to the centralized
repository.

The centralized repository now also runs a github workflow that automatically creates a pull-request into the `develop` branch for this
feature. With that pull-request, the default workflows from the centralized repository are executed, verifying if everything is fine. In
addition, the repository maintainer will perform custom reviews and may ask you for changes. You can perform your changes within your
project repository and by pushing those to the extension repository `foobar` by updating your feature-branch `feature/my-idea`. This can
be done by running the same `git subtree push` command as before, after you have made your changes in the project repository.

The github workflow will detect your updates and updates the feature-branch on the centralized repository by resetting it to the previous
state, typically develop, and run `git subtree pull` just like the first time. We need to reset the feature-branch `foobar/feature/my-idea`
first, otherwise we would get an additional merge commit for every update of the the feature-branch.

Once the pull-request from feature-branch `foobar/feature/my-idea` was accepted and merged into `develop` the feature-branch `feature-my-idea`
will also be removed from the extensions repository. Another github workflow is triggered that automatically pulls the changes with the 
`develop` branch of the centralized repository and updates the `develop` branch of the extensions repository by running:
`git subtree pull --squash --message="merge feature/my-idea into develop" --prefix=core-customize/hybris/bin/custom/sapcxtools/foobar git@github.com:sapcxtools/foobar.git develop`

When the next version of SAP CX tools is released, the same action is done, all included extensions are triggered to also merge their 
`develop` branches into their `main` branches, just like the centralized repository. After the release we recommend you to run an update
as mentioned above. Again, we do not recommend to skip the `--squash` option, as you will see all your commits twice in the history.

__CAUTION:__ For those who use this repository itself to develop on features, please make sure that you do not push your changes from the
extensions to the extensions repositories using `git subtree push`. This will be done automatically for you, once your pull-request has
been accepted and merged into `develop`. Also make sure that you only use the pattern `feature/<name>` for your feature-branches, so there
won't be any kind of conflicts with the feature-branches that have automatically been created.

While this process seems to be complicated, it guarantees that all extensions included in SAP CX tools are interoperatable and work
individually, with only their own dependencies resolved. If you have questions, feel free to get in touch with us in the discussion board.
