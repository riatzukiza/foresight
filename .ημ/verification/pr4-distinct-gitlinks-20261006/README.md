# Distinct gitlink fixture review repair

Native review5431650098 item cr-comment:v1:8666527dbe53d75d462486f4 identifies a uniform-revision fixture blind spot at originalhead165b6037b7823ad3a6da2a418e98095fb97ce9d5.

Production planner already looks revisions up by path. Original suite12tests135assertions passes both actual code and a deliberate mutation copying (:revision (first links)) to every child, proving the fixture could not distinguish incorrect pin assignment. Test fixture now assigns other-sha to .agents and child-sha to remaining children, updates the independently authored reference plan, checks both distinct revisions exist, and compares the input path-to-revision map to the actual planned map. The wrong-head negative observation uses root-sha so it remains incorrect for .agents after the fixture change.

Exact CI-pinned NBB1.4.207 results: mutated planner with old tests12/135PASS exit0; mutated planner with strengthened tests12/136 has28failures0errors exit1; restored original planner with strengthened tests12/136PASS exit0. Project suite14/176PASS exit0. clj-kondo --lint src scripts test:0errors0warnings. Logs and exit codes retained. Production source and Rheos ledger bytes equal originalhead. No child fetch, initialization, runtime service, workflow or configuration change.

This is isolated preparation in a new private clone/worktree. Root owns final ordinary publication and native summary settlement. Prior approval remains historical and native reviews must qualify any published newhead. Raw mutation artifacts are evidence; no incorrect production code or audit-branch commits are carried. Automatic merge remains off; no invitation, merge or deployment activation.
