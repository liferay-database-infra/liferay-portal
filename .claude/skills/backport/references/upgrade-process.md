# Backporting an Upgrade Process

Applies whenever the cherry-pick touches `PortalUpgradeProcessRegistryImpl` or a module's upgrade step registrator. The registry conflict is expected, because the target lacks everything master added after the branch point. Choosing the schema version is the whole job here, and getting it wrong silently loses upgrades on a customer's database. Never just copy master's number, and never renumber entries the target already has.

## The Mechanism That Decides Everything

`PortalUpgradeProcess.getPendingSchemaVersions` runs `_upgradeVersionTreeMap.tailMap(fromSchemaVersion, false)`, strictly greater than the database's current version. **Any version at or below where a database already sits never runs, permanently.**

Two upgrade paths must both stay correct:

1. **On the target branch.** A database upgrading within the release branch runs everything above its current version. Easy, and the only one people check.
1. **The forward path.** That same customer later moves to another branch, which may be the next quarterly, a later one, or master. Whatever version the release branch left them at now censors the target's whole registry through the same `tailMap`. This is where the damage happens, and it is invisible from the backport PR. It is not one path but many, so see Check Every Upgrade Path below.

## Why Master Always Has Entries The Release Branch Will Never Get

Only bug fixes are backported. Stories stay on master and upward. **No BPR ticket means it is not a bug fix, so it is never coming to the release branch**, and its upgrade will only ever run on the forward path.

Confirm the type per neighbouring version rather than assuming, since this drives everything:

```bash
curl -s -u "${JIRA_API_USER}:${JIRA_API_TOKEN}" -H "Accept: application/json" \
  "https://liferay.atlassian.net/rest/api/3/issue/<KEY>?fields=issuetype,status,summary"
```

Worked example on release-2026.q3, where master holds 38.7.7 LPD-98544 (Story), 38.8.0 LPD-101498 (Bug), 38.8.1 LPD-99950 (Bug), 38.8.2 LPD-82361 (Story). The two Stories will never exist on q3, so **the version q3 leaves a database at must sort below 38.7.7**, or the audit configuration upgrade is lost forever for every q3 customer.

## The Old "Gap" Trick Is Luck, Not A Mechanism

Release branches q1 and q2 put all seven of their backports in 38.2.4 through 38.2.10, which works only because master happened to jump 38.2.3 straight to 38.3.0, leaving that micro range free.

Do not rely on it. Master consumed q3's next micro after q3 was cut: q3's merge-base is 2026-08-02, and LPD-98544 took 38.7.7 with a commit date of 2026-08-10. When that happens there is no free micro at all, and every plain three-part number you could pick skips something.

## The Fix: Open A Gap In Master

When master has already consumed the branch's next micro, free that number **on master** rather than squeezing underneath it. Register the occupied version as a `DummyUpgradeProcess` and move whatever held it up one micro, in a pull to master that lands *before* the backport:

```java
upgradeVersionTreeMap.put(new Version(38, 7, 7), new DummyUpgradeProcess());

upgradeVersionTreeMap.put(
	new Version(38, 7, 8),
	UpgradeModulesFactory.create(
		new String[] {"com.liferay.portal.security.audit.router"},
		null));
```

The release branch then registers the plain vacated number, `new Version(38, 7, 7)`. A database that q3 leaves at 38.7.7 skips master's own 38.7.7 on the forward path, and that is safe **only because what now sits there does nothing**. This is why the slot has to become a dummy rather than simply be renumbered away: the skip is real, and the dummy is what makes it harmless.

This is what the database team recommended (2026-08-19), in their words: "what we usually do is move up the upgrade process (converting for example 38.7.7 in a dummy upgrade process) to be able to generate a gap". The pattern is already in master six times (`DummyUpgradeProcess` at 16.0.0, 25.1.2, 29.1.1, 34.1.0, 38.1.0 and 38.2.3), three of them introduced exactly this way:

- `01419cc7afa64` LPD-87577, restored 38.2.3 as a no-op so master would recognise a q1 database's schema version
- `17f16f2e3c38e` LPD-81218, replaced a reverted ERC upgrade with an empty step
- `74dac584ec5ea` LPD-101498, replaced the superseded 34.1.0 DB2 upgrade with a dummy

Five things this commits you to, all of which belong in the report:

1. **The master pull merges first.** Until it does, the release branch's number still collides with a live entry on master, and merging the backport into that window loses the master entry permanently for every customer who upgrades through it.
1. **Two processes must be re-runnable, and you name them both.** The backported one, because it runs again under master's own number on the forward path. And the one you moved up, because a database sitting at *exactly* the vacated version now runs it a second time under the new number. Databases above that version are untouched. Read the moved process rather than assuming: a module `UpgradeModules` entry is gated on that module's own `Release_` row, and the step itself may also be idempotent, which is two independent reasons but only if you check.
1. **Confirm with the team that owns the moved entry.** It is their upgrade taking a new number and a possible second run, and it is almost never the team doing the backport.
1. **It does not scale for free.** The next upgrade backported to that same branch needs the *next* number vacated the same way, so it is one master pull per backport that needs a slot.
1. **It assumes no intermediate release branch.** With a branch between the target and master, the intermediate ones have to be moved up too, which is a much larger change. Confirm the target is the newest release branch before proposing it.

The commit takes the **driving ticket's key**, not a new "create a gap" ticket: all three precedent commits carry the key of the work that needed the gap. File a separate ticket only when the driving one is closed or when the entry being moved belongs to another team, and say which of those applies.

## Rejected: A Qualifier Slot Below Master's Next Entry

`Version` has a fourth component, and `Version.compareTo` negates the comparison when either qualifier is blank, so `38.7.6 < 38.7.7.step-1 < 38.7.7`. That makes `new Version(38, 7, 7, "step-1")` fit between the branch's last version and master's next entry with no master change at all, and it round-trips through `Release_.schemaVersion`, whose pattern allows a qualifier of `[-_\da-zA-Z]+`.

**Do not propose it.** The mechanism works, and it is still banned. `9531a060c603f` (LPD-44331, 2025-11-13) is the commit that introduced the `32.0.0.step-1/2/3` qualifiers, and its own message ends: "Using qualifier since there is no room. Qualifiers won't be allowed for future ones." Master's six qualifier uses are all sub-steps of a single version, no release branch has ever used one for a backport, and the database team rejected it again on 2026-08-19 in favour of the gap above.

Keep the ordering fact itself, since it is what makes the forward-path loss visible when you are working out what a candidate number would skip. Verify it against the compiled class rather than by reading:

```bash
R=<repo>
CP=".:$R/portal-kernel/classes:$R/modules/core/petra/petra-lang/build/tmp/jar/com.liferay.petra.lang-*.jar:$R/modules/core/petra/petra-string/build/tmp/jar/com.liferay.petra.string-*.jar"
# build a TreeMap of master's entries, then print tailMap(candidate, false) and headMap(candidate, true)
java --add-opens java.base/java.lang.invoke=ALL-UNNAMED -cp "$CP" VT
```

## Check Every Upgrade Path, Not Just The One To Master

A customer does not only go from a release branch to master. They can move q1.1 to q1.2 within a branch, q1 to q2, or jump straight from q1 to the latest quarterly. **Every pair has to be coherent**, so a version is never chosen against master alone.

Two properties keep the whole set safe, and both must hold:

1. **The same backported fix takes the same number on every branch that shares a base.** q1 and q2 both sit at 38.2.10, so this fix takes 38.2.11 on both. That is what makes q1 to q2 safe: the customer already ran 38.2.11, and q2's 38.2.11 is the same upgrade, so skipping it is correct. The failure this prevents is two *different* fixes claiming one number on two branches, where moving across silently loses one.
1. **A lower branch's numbers stay below the next branch's.** q1 and q2 end at 38.2.11 while q3's entries start again from 38.3.0 upward, so jumping q2 to q3 replays everything q3 has that q2 never did. Some upgrades re-run, which is fine and expected; nothing is skipped.

Verify the whole matrix rather than reasoning about it, using each branch's registry with the fix applied:

```bash
for b in q1 q2 q3; do git show <branch-$b>:$f > reg-$b.java; done
git show upstream/master:$f > reg-master.java
```

Then, for every ordered pair, compute the entries the target holds at or below the source's maximum version whose registered process differs from the source's at that same version. Those are the ones the customer never ran and never will. Compare with `Version` semantics, including the blank-qualifier negation, not with string or tuple ordering.

Confirmed for this fix across all six paths: q1 to q2, q1 to q3, q2 to q3 and each branch to master skip nothing, the only entry dropped being master's `DummyUpgradeProcess` at 34.1.0, which superseded the real `UpgradeDB2` that the branches still run there. Skipping a no-op you have already satisfied is not a loss.

## Constraints On The Number

- **No major or minor bumps in a backport.** The version must stay under master's next entry, which a bump cannot do by definition.
- Never renumber or repurpose a version the target already has. Never give a number a different meaning than it has on master **while master still has a real process there**: taking master's live 38.7.7 for a different process makes the same number mean two things on two branches, and master's own 38.7.7 is then skipped by collision. Opening a gap is the sanctioned exception and the only one, because it turns master's entry into a `DummyUpgradeProcess` first, so the collision skips nothing.
- The property is **branch-wide, not per-PR**: it holds only if *every* portal-schema backport on that branch stays below master's next never-backported entry. A single backport registering at or above it destroys the guarantee for every customer on that branch, so it has to be agreed with the release team and with whoever owns the sibling BPRs.

## Check The Siblings Before Choosing

Adjacent versions usually belong to other tickets backported in the same batch, sometimes by another team, and the block should land together in master's relative order:

```bash
jql='project = BPR AND summary ~ "<target>" ORDER BY created DESC'
curl -s -u "${JIRA_API_USER}:${JIRA_API_TOKEN}" -H "Accept: application/json" -G \
  --data-urlencode "jql=$jql" --data-urlencode "fields=summary,status,assignee" \
  "https://liferay.atlassian.net/rest/api/3/search/jql"
```

Identify the ticket behind each neighbouring version with `git log upstream/master -S'<registered expression>' -- $f`, and report per neighbour: issue type, is it already on the target, does a BPR exist, who owns it, is a PR open. Also check whether the target has shipped at all (`git ls-remote --tags upstream '<version>*'`); no tags means nothing is upgraded in the field yet and ordering mistakes are still recoverable.

## Bundling Several Processes Into One Version

`upgradeVersionTreeMap.put` takes varargs, so a whole block can share one slot. This is what fix-pack branches do, and it is the clean answer when several upgrades must land together but only one slot is available:

```java
upgradeVersionTreeMap.put(
	new Version(38, 2, 4), new UpgradeAssetEntryPublishDate(),
	new LayoutStagingExternalReferenceCodeUpgradeProcess(),
	new LayoutDuplicateExternalReferenceCodeUpgradeProcess());
```

## The Upgrade Must Be Safe To Re-Run

Under every correct option the upgrade runs a second time on the forward path, from master's own registration. Confirm it is a no-op when already applied and say so in the report. `LayoutDuplicateExternalReferenceCodeUpgradeProcess` qualifies because its query only selects groups `having count(*) > 1`, so once the duplicates are gone it matches nothing. **An upgrade that is not re-runnable cannot take a slot below master's entry**, which removes the only clean option and forces an escalation.

## Report Before Pushing

For an upgrade-process backport, always get a decision before pushing. Show: the version chosen and why, each neighbouring version with its ticket, issue type and backport status, the explicit list of what a customer would skip on the forward path per candidate version, whether the process is re-runnable, and whether the choice depends on other teams' backports doing the same.

When the choice needs a gap, the report also carries the master side: the exact master diff, which entry moves and whose ticket owns it, whether that team has confirmed a second run is safe, and the statement that the master pull merges first. Two pulls on two branches with an ordering between them is the deliverable, not one.
