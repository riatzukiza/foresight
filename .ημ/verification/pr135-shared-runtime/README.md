# Shared current-reader admission blocker

Original PR117/personal5 rejects both its new record and the historical union under current CI1.4.207. Original PR118/personal6 reads its new record successfully but rejects the historical union. Both former1.3.204 union controls pass with the same pinned source/dependency copies. Detailed raw logs, exits, orchestration scripts and before/after source hashes are retained here.

Root135 owns preservation and explicit append-only reconciliation of immutable corpus. [Clio1](https://github.com/open-hax/clio/issues/1) separately owns prevention at the canonical writer boundary: runtime append currently accepts programmatically created keywords that its strict current reader cannot read after writing. The reader and canonical hash protocol are not weakened.
