# Patch Version Constraints (Patcher Portal)

These are two different deliverables, not two flavours of the same one. A **release branch (BPR) backport** is a pull request into the product, so it carries the tests and follows the LBM rules in [`SKILL.md`](../SKILL.md). A **patch version** is a patch built for a specific customer through Patcher Portal, carrying only the runtime fix, so it is screened far more narrowly and the failure mode is unhelpful: the fix just fails with no clear reason. Nearly always that is one of the forbidden file types riding along in a cherry-picked commit.

| File | Allowed in a Patcher fix |
| --- | --- |
| `bnd.bnd` | **no** |
| `packageinfo` | **no** |
| Test changes | **no** |
| `build.gradle` | yes |

A patch version only ever exists to fix a **customer bug raised through an LPP**, so the tests are not wanted there in the first place. They stay on master and travel with the release-branch backport. **A patch carries only the solution.**

So when the target is a **patch version**, the cherry-pick is not simply "all the ticket's commits". Leave out the test commits, and leave out any commit that only bumps a `packageinfo` or edits a `bnd.bnd`. When the fix itself cannot avoid touching one of those, the patch cannot be delivered this way, so say so rather than submitting something that will fail opaquely.

Check the result before submitting:

```bash
git diff <target>..HEAD --name-only | grep -E "bnd\.bnd$|packageinfo$|[Tt]est" || echo "clean for Patcher"
```

This does not apply to a release-branch (BPR) pull, where tests are expected and the bump rules are the LBM ones in [`SKILL.md`](../SKILL.md).
