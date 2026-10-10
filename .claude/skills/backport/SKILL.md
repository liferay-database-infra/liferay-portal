---
name: backport
description: Backport a fix to a Liferay patch version or release branch by branching off the target and cherry-picking the master commits, including how to choose an upgrade process's schema version so no upgrade is skipped, and the LBM rules that get a backport pull rejected. Use when the user asks to backport a ticket to a quarterly patch version (e.g. 2026.q1.7) or a release branch (e.g. release-2026.q1), works a BPR ticket, or asks which schema version a backported upgrade process should register.
argument-hint: "<ticket-key> <target>"
---

# Backport a Fix to a Patch Version or Release Branch

Create a backport branch for a Liferay ticket by branching off a target ref and cherry-picking the fix commits from master, then push it. How the branch is delivered depends on the target (see Finalize): a **release branch** (BPR) gets a pull request to the Liferay repo, while a **patch version** is submitted by hand through Patcher Portal.

This skill exists for the manual fallback, and you only reach for it when the automated path fails. For a release branch, a **BPR** (Backport Request) ticket is opened when the automated backport *fails to auto cherry-pick* onto a specific release version. For a patch version, you run this skill when Patcher Portal's *auto fix* option cannot produce the fix automatically. Conflicts during the cherry-pick are therefore the expected case here, not an anomaly. Be ready to work through them (see Cherry-Pick the Commits).

**Two parts of this need reading before you touch the code.** When the change registers an upgrade process, the schema version is the whole job and picking it wrong silently loses upgrades on customer databases, so read Backporting an Upgrade Process first. Before pushing, read Backport Review Rules (LBM) for what gets a release-branch pull rejected outright, or Patch Version Constraints for the much narrower set of files a Patcher fix may touch.

## Resolve the Inputs

Resolve two values from `${ARGUMENTS}`. Ask the user for whichever is missing.

- **Ticket** — the original fix ticket, such as `LPD-94368` (or a browse URL).
- **Target** — the ref to backport onto. Two forms, distinguished by shape:
  - A **patch version**, such as `2026.q1.7` — a real Git **tag** on `upstream`.
  - A **release branch**, such as `release-2026.q1` — a **branch** on `upstream`.

  The idea is the same either way: branch off the target, then cherry-pick.

**When given a BPR ticket** (e.g. `BPR-90239`), both values come from it. Fetch the BPR ticket through the Jira Cloud REST API (`liferay.atlassian.net`, authenticated with `${JIRA_API_USER}` / `${JIRA_API_TOKEN}`); its summary follows the form `<target> <ORIGINAL-TICKET> | <description>` — e.g. `release-2026.q1 LPD-94368 | Only 20 picklists...` yields target `release-2026.q1` and ticket `LPD-94368`. Use the original ticket to locate the fix commits in Locate the Fix Commits.

## Prerequisites

Abort when the working tree has uncommitted changes — the cherry-pick needs a clean tree.

## Locate the Fix Commits

Find every commit for the ticket, searching in this order and stopping at the first remote that has them:

1. **`upstream/master`** — `git fetch upstream master`, then `git log upstream/master --oneline --grep="<TICKET>"`.

1. **`brian/master`** — the fix often lands here before it syncs to `upstream`. `git fetch brian master`, then `git log brian/master --oneline --grep="<TICKET>"`.

There are usually several commits (e.g. the fix plus a separate "Unit tests" commit, plus any "SF" source-formatter commits). List them all to the user and confirm before proceeding. Note the **chronological order** (oldest first) — cherry-picks must be applied in that order.

## Create the Backport Branch

Fetch the target, detach onto it, and branch. The new branch is named `<TICKET>-<target>`.

- **Patch version (tag)** — fetch the tag ref:

	```bash
	git fetch upstream refs/tags/<target>
	git checkout FETCH_HEAD
	git checkout -b <TICKET>-<target>
	```

	Backporting `LPD-94368` to `2026.q1.7` yields branch `LPD-94368-2026.q1.7` off tag `2026.q1.7`.

- **Release branch** — fetch the branch by name (`FETCH_HEAD` works the same way):

	```bash
	git fetch upstream <target>
	git checkout FETCH_HEAD
	git checkout -b <TICKET>-<target>
	```

	Backporting `LPD-94368` to `release-2026.q1` yields branch `LPD-94368-release-2026.q1`.

When the branch already exists, stop and ask the user whether to reuse or recreate it.

## Cherry-Pick the Commits

Cherry-pick the commits from Locate the Fix Commits in chronological order (oldest first):

```bash
git cherry-pick <oldest-sha> <next-sha> ...
```

**When the target is a patch version, do not cherry-pick the test commits**, and skip any `packageinfo` or `bnd.bnd` commit. See Patch Version Constraints below, since Patcher rejects those without saying why.

Conflicts are expected for BPR backports — that failure is why the manual backport exists. When one occurs, report the conflicting files and resolve each hunk by reading both sides against the original master change, preserving the intent of the fix. Show the user your resolution before continuing with `git cherry-pick --continue`. When a conflict is genuinely ambiguous, surface it and let the user decide rather than guessing.

## Backporting an Upgrade Process

When the cherry-pick touches `PortalUpgradeProcessRegistryImpl` or a module's upgrade step registrator, choosing the schema version is the whole job, and getting it wrong silently loses upgrades on a customer's database. Follow [`references/upgrade-process.md`](references/upgrade-process.md) before picking a number, and never just copy master's.

## Verify

Confirm the branch is clean (`git status`) and the expected commits are present (`git log --oneline -n <count>`).

## Backport Review Rules (LBM)

Backport pulls to a `liferay-portal-ee` `release-*` branch are screened automatically by the Liferay Branch Manager against a documented checklist: [Backport PR LBM Rules Review Checklist Explanations](https://liferay.atlassian.net/wiki/spaces/QA/pages/2081391024/Backport+PR+LBM+Rules+Review+Checklist+Explanations). Check the page itself when a rejection cites something not listed here, since the rules change.

### The API Policy Is Bump-Driven, And Only Major Is An Automatic Reject

bnd's bump already encodes customer impact, so the verdict follows the bump and the diff is only consulted where it matters:

- **major** = breaking: a member or class modified or removed, or a method added to a `@ConsumerType` interface.
- **minor** = additive and backwards compatible: a new class, or a new method on a class or a `@ProviderType` interface.
- **micro** = implementation only, no API change.

Any module `packageinfo` or `bnd.bnd` `Bundle-Version`, including non-model `portal-kernel` packages:

| Change | Bump | Verdict |
| --- | --- | --- |
| Major bump | major | **reject** |
| Exported-API edit whose baseline *requires* a major, even with the bump absent from the diff (never committed, or committed then reverted) and attributable to this pull | major | **reject** |
| Minor on a `portal-kernel` non-model package | minor | review |
| Minor on a non-kernel module | minor | safe, routine |
| Implementation-only | micro | safe |
| A change that never tripped a bump | none | safe |

`kernel.model` is the frozen surface customer code binds to, and gets one extra carve-out, because editing or deleting a member breaks that code even when bnd lets it ride as a minor:

| Change | Bump | Verdict |
| --- | --- | --- |
| Major bump | major | **reject** |
| Minor that edits or deletes existing public API | minor | **reject** |
| Minor that only adds API | minor | review |
| Implementation-only | micro | safe |

"Reject" means do not merge even on a green test signal, and an exception needs explicit sign-off.

**So a backport can never carry a major bump**, and stripping the bump does not rescue it: the baseline requirement alone is grounds for rejection. Minors are a different matter, routine outside `portal-kernel`, a review nudge inside it, and a reject only on `kernel.model` when existing public API is edited or removed.

Reviewers sometimes ask for more than the policy says, for instance to inline a variable purely to avoid a minor. That happened twice on `https://github.com/liferay/liferay-portal-ee/pull/38570`, and the pushback there is what produced the tables above. When a reviewer asks for a code change to dodge a *minor*, cite the policy and ask where the rule is written before diverging the backport from master, since every such edit conflicts with everything backported behind it and erodes the auto cherry-pick over time.

**Check whether a reported bump is even yours.** A reverted commit still sitting on the release branch can make the baseline report a bump in a package your diff never touches. Compare the reported package against your own diff, and when they disagree, rebase onto the latest target branch, force-push, and ask for a retrigger. On that same pull the reported minor was `com.liferay.portal.kernel.model` from someone else's `PortletCategory` revert, while the change only touched `com.liferay.portal.kernel.util`.

### Rules That Specifically Catch Upgrade Backports

- **Upgrade changes.** Touching upgrade classes labels the pull and the BPR tickets, for ER team investigation, as potentially heavy.
- **Upgrade post-review.** The Upgrade team is notified and the ticket gets `pt-upgrade-review`. Expect the schema-version choice to be questioned, so put the reasoning in the PR description rather than waiting to be asked.
- **Schema version changes.** Changing `release.schema.versions` in `@Reference` targets can produce fix packs that cannot be reverted.
- **New modules.** Avoid backporting one, since its semantic versioning collides with the upper branches. See Backporting A New Module below.
- **Reindexing.** If the change forces customers to reindex, it is treated as heavy and shelved to the upcoming service pack, or flagged Important.
- **Rolling restart.** Touching rolling-restart-related classes moves the pull to the next SP shelf.

### Backporting A New Module

A new module cannot simply be added to a release branch, because the version ordering runs the other way from the branch ordering: the **lowest-ranked branch must hold the lowest version and master the highest**, so that new methods can keep being added on master. A module that already exists further up has therefore already taken the low numbers.

The documented example is Portal-Workflow-API, which sat at 1.0.0 on 7.1.x and 2.0.0 on 7.2.x. Adding it to 7.0.x at 1.0.0 was impossible, since 1.0.0 was taken. The fix renumbered every branch at once: 3.0.0 on 7.0.x, 4.0.0 on 7.1.x, 5.0.0 on 7.2.x, 6.0.0 on master.

Two ways out, in order of preference:

1. **Do not add the module.** Rework the fix to avoid it, which is nearly always the cheaper answer.
1. **Land it on every affected branch at once**, doing the semver work as one coordinated change: bump the major versions on all of them, **master included**, starting from the lowest version on the lowest-ranked branch, then confirm the OSGi import requirements are all satisfied.

Option 2 deliberately introduces major bumps, which the API policy above rejects by default, so it needs explicit sign-off before you start. Raise it rather than assuming it will be waved through.

### Hard Rejects

- A major semver change or a downgrade.
- A self-revert inside the pull. Leave the commit out, or revert under a new ticket.
- The original ticket not **Closed** on Jira, unless the backport is a unique fix. The pull stays open two workdays, then is rejected.
- A ticket key that does not exist on Jira, usually a typo with an extra digit.
- A commit message without a valid Jira project prefix (`APIO, ASAH, BLADE, CEREBRO, COMMERCE, CLDSVCS, FARO, IDE, LHC, LOOP, LPD, LPS, LRAC, LRCI, LRDCOM, LRDOCS, LRIS, LRQA, OAUTH2, RELEASE, SYNC, TR, WCM`).
- More than 20 distinct tickets, which nearly always means the base branch is wrong.
- A linked regression not included, when that regression is present on the base branch.
- An unsupported base branch. Supported are the release under preparation, the latest quarterly, and LTS quarterlies in Premium Support. The legacy branches (`ee-6.2.x`, `7.0.x`, `7.1.x`, `7.2.x`, `7.3.x`) warn rather than close.

### Asked To Fix, Not Rejected

- `Language.properties` changed without the region-specific files, which must go in a new commit. Direct per-key translations are no longer allowed in the repo at all, they belong in Crowdin, though `buildLang` is still required.
- `package.json` changed without the matching `yarn.lock` or `package-lock.json`.
- JSON variable naming: `*JSONArray` for a `JSONArray`, `*Element` for an `Element`, `*JSON` for a String of JSON.
- JavaScript `debugger` statements outside tests.
- Front-end theme changes, where the sender is asked whether they are also on the private branch.
- Mixed backports, where fix-packable components and marketplace apps are split into two pulls.
- Third-party library updates, which are not allowed outside master for DXP 7.0 and up.
- A sender branch named after the target branch, which overwrites the upstream branch. Always name the branch `<TICKET>-<target>`, as this skill does.

### Labels And Post-Reviews Triggered Automatically

- Security-related areas notify the Security team and add `pt-app-security-review`.
- Upgrade-related areas notify the Upgrade team and add `pt-upgrade-review`.
- An "Important change" marks the BPR tickets Important and surfaces them in the fix pack release notes.
- Multiple Elasticsearch connectors on the target branch notify the Search SME.

### When Processing Is Skipped Entirely

Sub-repository pulls, pulls from senders that should not send backports (for example `liferay-continuous-integration`), pulls labelled `gauntlet`, and pulls with no valid development-project ticket.

## Patch Version Constraints (Patcher Portal)

A patch version is a patch built for a specific customer through Patcher Portal, not a pull request into the product, so it is screened far more narrowly and fails opaquely when a forbidden file rides along. Follow [`references/patch-version-constraints.md`](references/patch-version-constraints.md) when the target is a patch version.

## Push the Branch

Push the backport branch to the user's fork remote (default `origin`), which is what Patcher Portal references:

```bash
git push -u origin <TICKET>-<target>
```

## Finalize

How the backport is delivered depends on the target:

- **BPR / release branch** — once the branch is ready, open a pull request to the Liferay repo (`liferay/liferay-portal-ee`) with the **release branch as the base** and `<github-username>:<TICKET>-<target>` as the head. Derive `<github-username>` from the `origin` remote URL (e.g. `git@github.com:georgel-pop-lr/liferay-portal-ee.git` yields `georgel-pop-lr`):

	```bash
	gh pr create \
		--base <target> \
		--fill \
		--head <github-username>:<TICKET>-<target> \
		--repo liferay/liferay-portal-ee
	```

- **Patch version (tag)** — do **not** open a pull request. The pushed branch is submitted as a fix through Patcher Portal manually, at https://patcher.liferay.com/group/guest/patching (Patcher Portal has no public API).

## Summarize

Report:

- The branch name and the target it was created from.
- The commits that were cherry-picked, and any intentionally skipped (and why).
- The pushed remote and branch.
- The delivery path taken: the PR link (BPR) or a note that it is ready for Patcher Portal (patch version).
