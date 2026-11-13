---
name: backport
description: Backport a fix to a Liferay patch version or release branch by branching off the target and cherry-picking the master commits, including how to choose an upgrade process's schema version so no upgrade is skipped, and the LBM rules that get a backport pull rejected. Use when the user asks to backport a ticket to a quarterly patch version (e.g. 2026.q1.7) or a release branch (e.g. release-2026.q1), works a BPR ticket, asks which schema version a backported upgrade process should register, or ships a fix that exists only on a release branch.
argument-hint: "<ticket-key> <target> [beginner|advanced]"
---

# Backport a Fix to a Patch Version or Release Branch

Create a backport branch for a Liferay ticket by branching off a target ref and cherry-picking the fix commits from master, then push it. How the branch is delivered depends on the target (see Finalize): a **release branch** (BPR) gets a pull request to the Liferay repo, while a **patch version** is submitted by hand through Patcher Portal.

If the fix has no master commit, see A Fix With No Master Commit.

This skill exists for the manual fallback, and you only reach for it when the automated path fails. For a release branch, a **BPR** (Backport Request) ticket is opened when the automated backport *fails to auto cherry-pick* onto a specific release version. For a patch version, you run this skill when Patcher Portal's *auto fix* option cannot produce the fix automatically. Conflicts during the cherry-pick are therefore the expected case here, not an anomaly. Be ready to work through them (see Cherry-Pick the Commits).

**Two parts of this need reading before you touch the code.** When the change registers an upgrade process, the schema version is the whole job and picking it wrong silently loses upgrades on customer databases, so read Backporting an Upgrade Process first. Before pushing, read Backport Review Rules (LBM) for what gets a release-branch pull rejected outright, or Patch Version Constraints for the much narrower set of files a Patcher fix may touch.

## Resolve the Inputs

Resolve two values from `${ARGUMENTS}`. Ask the user for whichever is missing.

- **Ticket** - an LPD fix ticket, such as `LPD-94368`, or an LPP customer ticket, such as `LPP-65959` (or a browse URL).
- **Target** - the ref to backport onto. Two forms, distinguished by shape:
  - A **patch version**, such as `2026.q1.7` - a real Git **tag** on `upstream`.
  - A **release branch**, such as `release-2026.q1` - a **branch** on `upstream`.

  The idea is the same either way: branch off the target, then cherry-pick.
- **Level** - `beginner` or `advanced`, for the Patcher Portal output. The default is `advanced`.
- **Patcher build** - for a patch version only. This is the Patcher branch of the latest build of the customer: the tag `fix-pack-fix-<buildId>`, which is the target tag with each fix of the build merged in. Ask the user for it before the first pick. Accept the compare URL (`https://github.com/liferay/liferay-portal-ee/compare/7.4.13-u60...fix-pack-fix-301673081`), the tag, or the build ID. If the account has no build, there is nothing to probe. Say so, and skip Probe the Customer Build.

**When given an LPP**, the target is its Affects versions in lowercase (`2026.Q1.12` gives `2026.q1.12`). Find its LPD fixes in three places, and say where each key came from:

- its `is fixed by` links,
- the LPD keys in its description and comments,
- the fixes of each LPP that it links to (a similar case).

Then list the backport branches that exist already: `git ls-remote <remote> 'refs/heads/*<target>'`, and keep each branch whose name has one of the keys.

**When the LPP names an LPE key**, the fix is a security fix. The LPE is a public ticket that hides the real LPD ticket, so no commit carries the LPE key. Do these steps:

- Read the `issuelinks` of the LPE. The LPD ticket is the one with the link type `Relationship`. The other links are LRHC tickets of customers. They are not fixes.
- Get only the fields that you need (`summary`, `status`, `issuelinks`). The LPE has many links, and a full read is larger than the output limit.
- Find the commits with the LPD key: `git log --oneline --all -i --grep='<LPD key>'`.
- Cherry-pick the commits of the LPD key, but give the patch the LPE key. The branch is `<LPE key>-<target>`, and the Content field of the Patcher fix is the LPE key. The LPD key stays only in the commit messages.
- The title of the LPD ticket can tell how to exploit the issue. Do not copy the title into a branch name, a Patcher Content field, a commit message, or a pull. Read each commit message of the branch before the push. If a message tells how to exploit the issue, look for the same message on master first. If master has it, the text is already public: keep the message, tell the user in one line with the master commit, and go on. If master does not have it, stop and tell the user.
- Many LPPs can come for the same LPE, each with its own customer build. Each one is a separate Rebase Conflict, but the same fix branch on the target can serve all of them.

**When given an LPD**, the LPP of the build is the LPP in its `fixes` links whose Affects versions is the target. If none or more than one matches, ask the user. Then list the branches that exist already, the same way as for an LPP.

**When given a BPR ticket** (e.g. `BPR-90239`), both values come from it. Fetch the BPR ticket through the Jira Cloud REST API (`liferay.atlassian.net`, authenticated with `${JIRA_API_USER}` / `${JIRA_API_TOKEN}`); its summary follows the form `<target> <ORIGINAL-TICKET> | <description>` - e.g. `release-2026.q1 LPD-94368 | Only 20 picklists...` yields target `release-2026.q1` and ticket `LPD-94368`. Use the original ticket to locate the fix commits in Locate the Fix Commits.

## Choose the Target Branches

Backport a fix only to master and to the branch of the client version.
Do not backport it to the branches between them.

- If master has the defect, put the fix on master first.
- Put the fix on the release branch of the client version. For a client on
  2025.q1.22, this is `release-2025.q1`. The fix then goes into each later
  patch of that branch.
- Do not put the fix on the branches between the client branch and master.
- Each Q1 release is LTS. LBM accepts a Q1 branch while it is in Premium
  Support. To check that a branch is supported, look for new BPR tickets on
  it in Jira.
- Also make a patch for the client version through Patcher Portal. The fix on
  the release branch goes only into the next patch.

## A Fix With No Master Commit

If the defect is only on a release branch, write the fix on that branch.
There is no master commit to cherry-pick.

- Find the branches that have the defect. Read the code on master and on each
  `upstream/release-*` branch. Show the lines that prove it for each branch.
- Branch off the release branch, and name the branch `<TICKET>-<target>`.
- LBM calls this a "unique fix". The Closed rule (Hard Rejects) does not apply
  to it, so the pull can go while the ticket is open.
- Make the patch for the client version from the same commits.

Example: LPD-111354, on 2026-10-22. Only 2023.q3 to 2025.q1 share one segments
experience between the draft and the page. The client was on 2025.q1.22, so
the fix went on `release-2025.q1` only, and master did not get it.

## Prerequisites

Abort when the working tree has uncommitted changes - the cherry-pick needs a clean tree.

In the commands below, `<skill-dir>` is the directory of this file: `.claude/skills/backport` in the repo, or `~/.claude/skills/backport` for a personal copy.

## Locate the Fix Commits

Find every commit for the ticket, searching in this order and stopping at the first remote that has them:

1. **`upstream/master`** - `git fetch upstream master`, then `git log upstream/master --oneline --grep="<TICKET>"`.

1. **`brian/master`** - the fix often lands here before it syncs to `upstream`. `git fetch brian master`, then `git log brian/master --oneline --grep="<TICKET>"`.

There are usually several commits (e.g. the fix plus a separate "Unit tests" commit, plus any "SF" source-formatter commits). List them all to the user and confirm before proceeding. Note the **chronological order** (oldest first) - cherry-picks must be applied in that order.

**A build commit can carry the keys of tickets that you do not backport.** A build
commit (buildLang, buildService, buildREST, baseline) has the keys of all its source
commits. If the backport takes only some of these keys, do not cherry-pick the build
commit. Run the generator again on the backport branch, and commit the output with the
keys that you backport.

**A `--grep` on one key does not mean those commits stand alone.** The branch the fix shipped
on may have carried several tickets whose commits interleave in the same files, and then the
cherry-pick conflicts on a hunk belonging to a key you are not backporting. Check it before the
first pick instead of reading it out of a conflict: take the files the fix's commits touch and
log those files over the same range for any other key.

```bash
git log <first>^..<last> --format='%h %s' -- <the files the fix touches> |
	grep -vE '^[0-9a-f]+ <TICKET> '
```

Anything that comes back is a neighbour to resolve deliberately. On the `LPD-104529` branch,
LPD-105566 Use the design library group when adding a page from its page template set and
LPD-105567 Reject an unconnected design library page template set when adding a page interleave
inside `SelectLayoutPageTemplateEntryDisplayContext.java` (LPD-105566 at lines 11, 13, 17, 100
and 148, LPD-105567 at 8, 28 and 142), so either one alone lands on a file the other shaped.
Report the neighbours with their titles and let the user choose: backport the dependency too, or
adapt the hunk by hand.

**Backport only what the customer needs.** If a neighbour does not fix a large problem that the
customer has, do not take it. Adapt the hunk by hand on the target's version of the file, and
keep only the change of the fix. Take a neighbour only when one of these is true:

- the fix does not work without it, or
- the fix causes a regression on the target and the neighbour corrects it.

On LPP-65959 NullPointerExceptions During Publishing/Reindexing in Remote Staging envirnoment
(https://liferay.atlassian.net/browse/LPP-65959), on 2026-10-20, LPD-105885 conflicted on
2026.q1.12 with LPD-103697, LPD-104969, and LPD-103446. None of them fixed the customer's
problem, so the conflict was resolved by hand.

**Never backport a story, an epic, or any ticket under one.** Before the first pick, check
each key that you will backport, the fix and each neighbour. Read its `issuetype`, then follow
the `parent` field up to the top. If the key or any level above it is a Story or an Epic, do
not backport it. Tell the user, and adapt the hunk by hand. A Task under an epic is feature work
too, so one level is not enough. On LPP-65959 this kept out:

- LPD-103697 Fix the synthetic ThemeDisplay built by LayoutServiceContextHelperImpl, a
  Technical Task under the Task LPD-103696 Export a site as a static HTML bundle, under the
  epic LPD-88799 Productize "Page statification engine" PoC.
- LPD-104969 Render a page as the guest user without a request through LayoutPreviewRenderer,
  which is part of a story.

## Create the Backport Branch

Fetch the target, detach onto it, and branch. The new branch is named `<TICKET>-<target>`.

**`<TICKET>` is the LPD key of each fix that the branch holds, never the LPP.** One patch can serve more than one customer, so the customer ticket does not go in the name. If the branch holds more than one LPD, write all the keys in commit order. The separator depends on the target:

- **Patch version:** a comma and no space, `LPD-105885,LPD-103742-2026.q1.12`. Patcher Portal accepts it, and the keys are then easy to copy into the Content field of the fix.
- **Release branch:** a hyphen, `LPD-105885-LPD-103742-release-2026.q1`. The CI bot closes a release-branch pull when the sender branch name has a comma. It accepts letters, numbers, hyphens, underscores, and dots. This closed https://github.com/liferay/liferay-portal-ee/pull/41421 and https://github.com/liferay/liferay-portal-ee/pull/41422 on 2026-10-28.

Give the patch file the same name as the branch, with `.patch` at the end. If a fix can ship alone (for example a regression fix that another team owns), put it on its own branch off the same target: `LPD-106119-2026.q1.12`. This rule was set on 2026-10-20, on LPP-65959.

- **Patch version (tag)** - fetch the tag ref:

	```bash
	git fetch upstream refs/tags/<target>
	git checkout FETCH_HEAD
	git checkout -b <TICKET>-<target>
	```

	Backporting `LPD-94368` to `2026.q1.7` yields branch `LPD-94368-2026.q1.7` off tag `2026.q1.7`.

- **Release branch** - fetch the branch by name (`FETCH_HEAD` works the same way):

	```bash
	git fetch upstream <target>
	git checkout FETCH_HEAD
	git checkout -b <TICKET>-<target>
	```

	Backporting `LPD-94368` to `release-2026.q1` yields branch `LPD-94368-release-2026.q1`.

When the branch already exists, check the remote with the loop of Push the Branch. If each branch is pushed at the local head, tell the user that the branches are pushed already. Then go to Finalize and say what is left to do. If a branch is not pushed, or is not at the local head, ask the user whether to reuse or recreate it.

## Cherry-Pick the Commits

Cherry-pick the commits from Locate the Fix Commits in chronological order (oldest first):

```bash
git cherry-pick <oldest-sha> <next-sha> ...
```

**When the target is a patch version, do not cherry-pick the test commits**, and skip any `packageinfo` or `bnd.bnd` commit. See Patch Version Constraints below, since Patcher rejects those without saying why.

Conflicts are expected for BPR backports - that failure is why the manual backport exists. When one occurs, resolve each hunk: read both sides against the original master change, and keep the intent of the fix. Do not stop at each conflict. Record it, continue, and create the branch. Then report all the conflicts together at the end, so the user decides on the finished branch. For each conflict, give:

- the commit and the file,
- each ticket that caused it, with its title and its URL,
- what that ticket does, and why the patch does or does not need it,
- the resolution, as the diff from the target's file to the resolved file.

If a conflict is ambiguous, resolve it the most conservative way. Then mark it as open in that report. Also report each regression that the fix causes on the target, with the master commit that corrects it.

## Backporting an Upgrade Process

When the cherry-pick touches `PortalUpgradeProcessRegistryImpl` or a module's upgrade step registrator, choosing the schema version is the whole job, and getting it wrong silently loses upgrades on a customer's database. Follow [`references/upgrade-process.md`](references/upgrade-process.md) before picking a number, and never just copy master's.

The arithmetic half of that reference is one call. It parses each ref's registry, orders the versions the way `Version.compareTo` does, and prints what a database would skip on every forward path, so the choice is made against a computed list rather than by eye over a 151-entry file:

```bash
python3 <skill-dir>/schema.py \
	--ref <lowest-release-branch> ... --ref upstream/master \
	--candidate <version>
```

It decides nothing and writes nothing. The number, the re-runnability check, whether a gap in master is needed and who owns an entry that would move all stay in the reference's Report Before Pushing section.

## Verify

Confirm the branch is clean (`git status`) and the expected commits are present (`git log --oneline -n <count>`).

**Compile the branch against the release before you push it.** A cherry-pick with no conflict can still call a method that the target does not have. A full portal build of an old tag is heavy, so compile only the changed files against the jars of the real release bundle:

```bash
# 1. Get the bundle of the target into a folder outside the repo, if it is not there yet.
#    Read the file name from https://releases-cdn.liferay.com/dxp/<target>-lts/
cd <bundles-dir>
f=liferay-dxp-tomcat-<target>-lts-<build>.tar.gz
curl -fO https://releases-cdn.liferay.com/dxp/<target>-lts/$f
curl -f https://releases-cdn.liferay.com/dxp/<target>-lts/$f.sha512 -o $f.sha512
echo "$(cut -d' ' -f1 $f.sha512)  $f" | sha512sum -c
mkdir ${f%.tar.gz} && tar -xzf $f -C ${f%.tar.gz} && rm $f $f.sha512

# 2. Compile the files that the branch changes.
<skill-dir>/compile-against-bundle.sh <repo> <target> <branch> <bundles-dir>/${f%.tar.gz}/liferay-dxp
```

The script prints `COMPILES` or the javac errors. If an error names a package that the unchanged code also imports, the classpath misses a jar, and the patch is not at fault. Find the jar in the bundle and add it with `--extra-cp`. The script finds the OSGi component annotations jar by itself, because the bundle does not ship it. This check proves that the Java compiles. It does not prove the OSGi build (bnd, the baseline check) or the runtime behavior, so say that in the summary. On LPP-65959, on 2026-10-20, both patch branches for 2026.q1.12 compiled this way.

## Probe the Customer Build

For a patch version, check the branch against the Patcher build of the customer before the push. Patcher merges each fix into the build. A fix that does not merge stops the build with Rebase Conflict, and a fix that merges can still break the compile. Find this before Patcher does, and prepare the fixes so that the build has no conflict.

```bash
<skill-dir>/patcher-build-probe.sh <repo> <target> <patcher-build> <TICKET>-<target>
```

The script fetches the build tag into the local repo, so you can also read the build with `git log <target>..fix-pack-fix-<buildId>`. It writes nothing to the work tree. It prints:

- the fixes of the build, with the fix tag and the keys of each one,
- the keys of the branch that the build has already,
- each file that the branch and a fix of the build both change, with the fix tag,
- `DEPENDENCY RISK` when both sides change a `package.json` or a `yarn.lock`,
- the result of the same merge that Patcher does: `CLEAN` or `CONFLICT`,
- `LOCK MISMATCH` when a dependency of a changed `package.json` has no `yarn.lock` entry for its exact spec in the merged tree, with the commits that changed the two files. `lock-check.py` does this check.

The skill reads the exit code and acts on it. The user does not run the probe and does not act on the code. The user sees the result in the summary, and does only the Patcher Portal steps of Finalize.

- **0, CLEAN:** the fix can go as it is.
- **1, CONFLICT:** make a conflict fix for each fix tag that the conflict names. See below.
- **2, BASE MISMATCH:** the build is not on the target, or the branch is not on the target alone. If the build is on another version, tell the user, because the build must use the target as its Project Version. A fix branch must start from the target tag, never from the build tag.
- **4, CLEAN with LOCK MISMATCH:** git merges it, but the compile fails. Each fix is correct alone, but the merge loses a `yarn.lock` entry. On hotfix 301673079 on 7.4.13-u60, LPS-199806 (fix 301668996) pinned `"@clayui/drop-down": "3.83.1"`, and the `modules/yarn.lock` of LPS-168443 (fix 250979579) had only `3.84.0`. Yarn then took the newest Clay, and `tsc` failed in `analytics-settings-web`. Make a conflict fix with the fix that changes `yarn.lock`. In it, align the `package.json` spec with the lock, or generate `yarn.lock` again, and run the probe again.
- **3, CLEAN with DEPENDENCY RISK:** git merges it, and the lock check is OK, but the compile can still fail. Two fixes that each generate `yarn.lock` conflict. A `package.json` pin in one fix can lose its `yarn.lock` entry to the `yarn.lock` of the other fix. Make a conflict fix with the fix that changes the dependency file, and generate `yarn.lock` one time in it. On hotfix 301673079 on 7.4.13-u60, LPS-168443 (fix 250979579) changed `modules/yarn.lock` and LPS-199806 (fix 301668996) pinned Clay in a `package.json`. git merged them, and `tsc` failed in `analytics-settings-web`.

If a key of the branch is in the build already, find out why. If the branch redoes that fix, because the fix failed or must change, probe the build without it: add `--without <key>`. The script then rebuilds the build in memory without each fix that holds the key, the way Patcher builds it when the new fix replaces the old one. Otherwise do not create a new fix for the key. Tell the user which fix tag has it.

On 2026-11-04 this was tested on hotfix 301673079 on 7.4.13-u60 (build `fix-pack-fix-301673081`). With `--without LPS-199806`, the probe gave exit 4 for fix 1.2 of LPS-199806 (`fix-pack-fix-301668996`), the fix that failed in Patcher. The conflict fix that the skill made gave a build tree identical to the fix that solved it by hand (`fix-pack-fix-301732896`).

**Make a conflict fix.** The build can already hold conflict fixes, made for earlier conflicts. Such a fix carries a list of tickets, for example `fix-pack-fix-252440139` with ten LPS keys. The probe names the fix tag that the build really merged, so the new fix goes on top of that fix and its full list of tickets. Never branch off a smaller fix that has only the key whose hunk conflicts, because the build does not use it.

- **The probe names one fix:** branch off its tag. The tag is on the target and holds only that fix. Cherry-pick the backport commits on it.
- **The probe names more than one fix:** the new fix must hold all of them. Branch off the fix with the most keys. Cherry-pick the commits of each other fix in the order of the build (`git log --reverse <target>..fix-pack-fix-<fixId>`). Then cherry-pick the backport commits.

```bash
git fetch --no-tags upstream refs/tags/fix-pack-fix-<fixId>:refs/tags/fix-pack-fix-<fixId>
git checkout -b <all keys>-<target> fix-pack-fix-<fixId>
git cherry-pick <commits of the other fixes> <backport commits>
```

Resolve each conflict the same way as in Cherry-Pick the Commits.

- The branch name and the Content of the conflict fix list every key involved: all the keys of each fix that it holds (the probe prints them under "Fixes involved"), in the order of the build, then all the new keys, in commit order. Do not keep only the key whose hunk conflicts.
- If the probe names `first fix`, the fix has no tag in the merge commits. Ask the user for its Fix ID or its Branch Name.
- Run the probe again on the conflict fix, with `--without` and each fix that the conflict fix replaces: its new keys and the tag of each fix that it holds, for example `--without LPS-199806,fix-pack-fix-250979579`. It must give exit 0.
- Keep the standalone fix branch too. It serves the builds of other customers, and the conflict fix serves this build.
- Compile the conflict fix the same way as in Verify, and push it with the other branches.

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
- A sender branch name with a special character, such as a comma. The bot closes the pull and asks you to rename the branch and send it again.

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

Push the backport branch to the fork of the user who created it. That remote is normally `origin`, and Patcher Portal reads the branch from it:

```bash
git push -u <remote> <TICKET>-<target>
```

**The remote must be a fork of `liferay-portal-ee`**, for example `git@github.com:<github-username>/liferay-portal-ee.git`. Before the push, read it with `git remote get-url <remote>`. If it is not a fork of `liferay-portal-ee` (for example a `liferay-portal` fork, which has no release tags), stop and ask for the right remote.

**Do not end a backport with a local branch.** Push each branch that the backport made, also a separate branch for a fix that ships alone. Then make sure that the remote has each one at the local head:

```bash
for b in <branch>...; do
	[ "$(git ls-remote <remote> "refs/heads/$b" | cut -f1)" = "$(git rev-parse "$b")" ] && echo "pushed $b" || echo "NOT PUSHED $b"
done
```

If a branch shows `NOT PUSHED`, push it again before the summary.

## Finalize

How the backport is delivered depends on the target:

- **BPR / release branch** - once the branch is ready, open a pull request to the Liferay repo (`liferay/liferay-portal-ee`) with the **release branch as the base** and `<github-username>:<TICKET>-<target>` as the head. Derive `<github-username>` from the URL of the remote that you pushed to (e.g. `git@github.com:jdoe/liferay-portal-ee.git` yields `jdoe`):

	```bash
	gh pr create \
		--base <target> \
		--fill \
		--head <github-username>:<TICKET>-<target> \
		--repo liferay/liferay-portal-ee
	```

- **Patch version (tag)** - do **not** open a pull request. The pushed branch is submitted as a fix through Patcher Portal manually, at https://patcher.liferay.com/group/guest/patching (Patcher Portal has no public API).

	The user fills the **Create Fix** form in Patcher Portal. Give the values of the form in one code block for each branch, one block above the other, so the user can copy each value:

	```
	Product:          Quarterly Releases
	Project Version:  2026.q1.12
	Content:          LPD-105885,LPD-103742
	Auto Fix:         off
	Branch Name:      LPD-105885,LPD-103742-2026.q1.12
	GitHub URL:       git@github.com:<github-username>/liferay-portal-ee.git
	Workaround:       off
	```

	- Product is Quarterly Releases for a quarterly target. For a 7.4 update (`7.4.13-u63`), the Product is `DXP 7.4`. Do not use `DXP 7.4 EP2`, `DXP 7.4 EP3`, or a `DXP 7.4 Liferay ...` connector.
	- Project Version is the target.
	- Content is the LPD keys of the branch, in commit order, with a comma and no space.
	- Auto Fix is off, because this skill runs when the auto fix fails.
	- Branch Name is the pushed branch, and GitHub URL is the URL of the remote that you pushed to.
	- Give a Create Fix block for each conflict fix from Probe the Customer Build too. Above its block, write one line that says why it exists: the fix tag of the build that it extends, each ticket involved with its title, and the file in conflict or the dependency file.
	- In the build block, the Tickets List has the keys of each conflict fix.

	**Give the output for the level.** For `advanced`, give only the blocks: the Create Fix blocks, the build block, and the conflict fixes when there are some. For `beginner`, give the same blocks first, then the numbered steps: look in the **Fixes** tab for a fix with the same Content and Project Version, and create it only if none exists, wait until each is Complete, add the keys to the build, solve the conflicts, and follow Failed Build Or Failed Fix if the build fails. Give the place to click and the reason for each step.

	**Then give the build block in the same reply.** Read the account and the support ticket from the LPP. Do not ask the user:

	```bash
	curl -s -u "$JIRA_API_USER:$JIRA_API_TOKEN" \
		"https://liferay.atlassian.net/rest/api/3/issue/<LPP>?fields=customfield_12570,customfield_14683" |
		python3 -c 'import json,sys; f=json.load(sys.stdin)["fields"]; print(f["customfield_12570"], f["customfield_14683"])'
	```

	- If the account has a build, use **Actions** > **Use as Build Template** on the most recent one. Add the new keys at the end of its Tickets List.
	- If not, use **Create Build**.
	- Set Project Version to the target, because a template keeps its old version. If the target is not the Affects versions of the LPP, ask the user.
	- Add the build after each fix is created.

	```
	Product:          Quarterly Releases
	Project Version:  2026.q1.12
	Account Code:     ACME
	Support Ticket:   LRHC-000000
	Type:             Official
	Tickets List:     LPD-105885,LPD-103742,LPD-106119
	```

## Rebase Conflicts On The Build

Probe the Customer Build prevents most of these. If the build still stops with fixes in Patcher Status **Rebase Conflict** (the **Fixes** tab), ask the user for the Fix ID, Content, and Project Version of each one. Then run the probe with the new build ID to find the fix tag in conflict.

Before you make a fix, ask the user to filter the **Fixes** tab by Content and Project Version. If a fix for the key on the target is Complete, do not make a new one. Fetch its tag (`git ls-remote upstream 'refs/tags/fix-pack-fix-<fixId>'`) and compare it with the commits. Also look for a fix of Type **Generated** with the same key: Patcher makes it when it merges the build again, and a Complete one shows that the conflict is solved. A build tag of a build that stopped on a conflict is not on GitHub.

1. If the Project Version is not the target, correct the version of the build and build again. Do not fix the conflicts.
1. Fix the conflict fix with the fewest tickets first.
1. For one ticket, backport it onto the version of the build. For more tickets, branch off the latest fix branch that has the rest of the tickets (ask the user for its Branch Name and GitHub URL), and cherry-pick the new ticket on top. Content is the rest of the tickets plus the new one.
1. When the fix is Complete, use **Actions** > **Build** again. Repeat until there is no conflict.

## Failed Build Or Failed Fix

If the build or a fix has the Patcher Status **Failed**, find the error in the Jenkins log of the build page. The `fixpack-builder-agent(production)` job merges the fixes. The `fixpack-builder-hotfix-7x(production)` job compiles the hotfix, so a compile error is in that log.

Then read the Patcher Troubleshooting Guide: https://liferay.atlassian.net/wiki/spaces/SUPPORT/pages/1956577726/Patcher+Troubleshooting+Guide

1. Always read the live page. The team adds new entries to it, so do not use a copy from memory or from an earlier session.
1. The page is about 83 KB. Read it with `getConfluencePage` (cloudId `liferay.atlassian.net`, pageId `1956577726`, markdown), save the body to a file, and grep the `##` headings and the error text. Read only the section that matches.
1. If an entry matches, apply its solution. Usually the solution is to add a ticket to the Tickets List of the build, then use **Actions** > **Build** again.
1. If the error is an infrastructure fault (`Permission denied (publickey)`, a git clone error, a network timeout, a stopped agent), the fixes did not cause it. Compare the time of the failure with GitHub status (https://www.githubstatus.com). Convert the Jenkins time (Pacific) to UTC first. If a Git Operations incident covers that time, build again after GitHub resolves it. If not, build again one time, and note the Jenkins node.
1. If no entry matches, or the infrastructure fault comes back, ask in #help-patching (https://liferay.slack.com/archives/CKZRB4EPK). Give the build ID, the job number, the node, and the error lines.

The companion page CSE Hotfix Building and Troubleshooting Walk-through explains each Patcher Status: https://liferay.atlassian.net/wiki/spaces/SUPPORT/pages/3183673479/CSE+Hotfix+Building+and+Troubleshooting+Walk-through

**Give the output for the level.** For `advanced`, give only the guide entry that matches, its link, and the action. For `beginner`, give the same first, then the numbered steps: where to find the Jenkins log on the build page, how to search the guide for the error, and when to ask in #help-patching. Give the place to click and the reason for each step.

## Summarize

Report:

- The branch name and the target it was created from.
- The commits that were cherry-picked, and any intentionally skipped (and why).
- The pushed remote and branch.
- The delivery path taken: the PR link (BPR), or the Create Fix blocks and the build block (patch version).
- The result of Probe the Customer Build, and each conflict fix branch with the reason for it.
- Each Rebase Conflict fix and what was done for it.
