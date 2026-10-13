# OpenCode free MiMo routing memory

Recorded from the user's routing history and saved observations on 2026-10-12. This is a project memory note, not a replacement for the canonical review, identity, retry or merge contracts.

## Signal

(汝, p=1.00) The user reports that this was previously solved: **use MiMo through OpenCode's free public-provider route, without supplying credentials**. The user remembers free limits as IP-based and the latest fast MiMo model slug as changing over time. Preserve that intended route; a failed request is not permission to switch to a paid provider, buy credits, change accounts or request a key.

(世, p=0.99) The credential-free catalog completed at **2026-10-12 16:38:41 UTC** and listed `opencode/mimo-v2.6-flash-free` as active, with input/output/cache cost `0` and tool calling supported. This slug is a dated observation, not a permanent selection guarantee. Catalog availability alone does not prove inference or hosted review health.

## Evidence

(世, p=0.99) The earlier session is recovered: `01a0fe9a-76ba-7db1-9538-fd709ddbc3b1`, retained in [the October 2 transcript](/home/err/.codex/sessions/2026/10/02/rollout-2026-10-02T16-52-10-01a0fe9a-76ba-7db1-9538-fd709ddbc3b1.jsonl:290). Its final recorded control matrix used OpenCode 1.18.18 without a supplied API key. Blanket `bash: deny` rejected the reviewer; keeping the Bash tool registered with deny-all except `true` admitted requests while an attempted `touch PWNED` remained blocked. The patched reviewer produced two successful tiny replies and one generic upstream error. These are recovered historical observations, distinct from the October 12 measurements below.

(世, p=0.99) That session persisted the explanation in [Muse PR17](https://github.com/octave-commons/muse/pull/17), source `.ημ/config/opencode/agents/github-reviewer.md`, commit `05b4f1c5e5bf2297bccf113a56c17e246d769d47`; the consuming eta-mu commit was `cbe1b986`. The retained [hosted run36979654351](https://github.com/open-hax/eta-mu/actions/runs/36979654351) completed the MiMo job and recorded `review_submit` returning `ok` at 2026-10-02 08:03:33 UTC. Its final deterministic evidence gate failed, so this is not evidence of a passing whole run. The earlier session explicitly retracted its CI-network causal guess. The user's IP-based-limit history does not establish that rejected hypothesis or a measured current quota.

(己, p=1.00) The [sealed recovery](/home/err/.local/share/openhax-codex/20261010/review-state/mimo-free-route-probe-v1/prior-session-memory-recovery-v1/selected-records-and-memory-catalog-recovery-v1.json), SHA256 `651f559c6f45cd5ab61015bff722236d5df15328f65f4a1c645c872843e82ae6`, preserves 15 selected records with exact raw-line/text hashes. Imported transcript timestamps are import times; the embedded hosted timestamps establish the narrower execution chronology. This host's `memories_1.sqlite` contained zero `stage1_outputs` rows when inspected, and its usual `~/.codex/memories/` directory was absent. The old source comment and transcript existed; generated automatic memory was not populated.

(世, p=0.99) Existing eta-mu documentation already records this route: [agent-workflows.md:285](/home/err/.local/share/openhax-codex/20261010/eta-mu-actionability-host/docs/agent-workflows.md:285) describes the anonymous public-provider path and no required `OPENCODE_API_KEY`; [github-automation-architecture.md:84](/home/err/.local/share/openhax-codex/20261010/eta-mu-actionability-host/docs/github-automation-architecture.md:84) describes read-only operation and retaining Bash registration with only the exact no-op `true`. Their observed whole-file SHA256 values are `918ae5168dbfa85ed8e0735d235d7f6990ffea969eaeaad9702240dbb74d3a35` and `534e4478133ce8223a342649d0395eb4ea79161d2e8b84575f46155ee1782261` respectively. Their other workflow revisions are historical documentation, not current pin authority.

(世, p=0.99) The current [github-reviewer profile](/home/err/.config/opencode/agents/github-reviewer.md:25), SHA256 `518c646482d97e1c50e64339bc01481fca6289761f04d915820456b0b7afce62`, contains this exact warning and restriction:

```yaml
# Keep the bash tool registered but unusable. A blanket deny drops the tool
# from the request, and OpenCode Zen's free tier then rejects it with
# "free tier can only be used from within OpenCode". `true` is a no-op.
bash:
  "*": deny
  "true": allow
```

(世, p=0.99) Fresh saved local observations distinguish three different profiles. All used a private empty auth store, an explicit environment without inherited provider credentials, project configuration disabled, no account changes and no purchased credits:

| Saved profile | Completed UTC | Actual observation |
| --- | --- | --- |
| All tools denied | 16:39:19 | Child exit `1`; HTTP `403`, `FreeTierError`: free tier can only be used from within OpenCode. |
| Only Bash registration with exact `true` allowed | 16:40:58 | Child exit `1`; the same HTTP `403` / `FreeTierError`. The Bash allowance alone did not repair this narrow profile. |
| Read/glob/grep/list plus exact `true`, other tools denied | 16:44:00 | Child exit `0`; actual text `MIMO_ROUTE_OK`, terminal `stop`, recorded cost `0`, complete cleanup and no truncated output. |

(世, p=0.99) The third observation establishes **one tiny credential-free local inference**. It does not establish the full reviewer profile, eight review tools, accepted complete-input trace, a native review, hosted runner health or approval. Saved execution/output pairs are under [mimo-free-route-probe-v1](/home/err/.local/share/openhax-codex/20261010/review-state/mimo-free-route-probe-v1/credential-free-current-cli-catalog-v1/execution.json). Relevant hashes:

- Catalog execution `8ff046dfbf630a6fda49a06a80172e73b286086969cfc56c457dd00086e77db1`; complete output `4886c6567ec3d26ceaed50db965d7ed75d7035cbfc92e28e16855d81740e084a`.
- All-denied execution `b475946be26c999792eae66d77f306caa62d0e7d4e31bab55080f03390b1f23a`; output `5bc369cbcb621aaf86b865763de84cb2b07d08f0410b1cc168188e343996af8d`.
- True-only execution `bb2d91456f9aac744af142ab99ca416ddd9e5b534e7a4af217f78e382ccfe7f3`; output `88a418e91e274bbc4a88b9c4851d601683795187c50f3e04e09017ebc7f6ffbd`.
- Read-only profile execution `d1e94a1e0f88f13e16b1c0027210917347b08994e339d041b10806e31f4d762c`; output `75fc8c6d62ee399aba51413a31f5d03c13c34b104abbbab597a960ce858ef005`.

(世, p=0.99) The distinct [hosted current614 failed log](/home/err/.local/share/openhax-codex/20261010/review-state/services-02128b-native-review-readback-v1/prod-goal-eta350-614eee-hosted-mimo-complete-failed-log-1557-v1.stdout:13), SHA256 `3ff7ffb9365f5087fc1e92cd1cb27ed12c680987f4f977611727ba7529b1381e`, records HTTP `401` at 15:45:09 UTC: `Upstream request failed: Invalid credential`, with an upstream `server_error` body. That authentic error does not identify a user-owned credential or establish a need for OpenRouter, an account change or a new provider key. The later tiny local success does not erase this hosted failure.

(世, p=0.99) The actual workflow-pinned **OpenCode 1.18.18** was subsequently checked in a separate owned runtime. Its credential-free catalog completed at 16:48:17 UTC with the exact same 12,554-byte output and SHA256 `4886c6567ec3d26ceaed50db965d7ed75d7035cbfc92e28e16855d81740e084a`. Its read-only-profile inference completed at 16:48:57 UTC with `MIMO_ROUTE_OK`, terminal `stop`, cost `0`, child exit `0` and complete cleanup. The [pinned inference execution](</home/err/.local/share/openhax-codex/20261010/review-state/mimo-free-route-probe-v1/credential-free-pinned11818-readonly-inference-v1/execution.json>) binds output SHA256 `61ba7f64b37c4853cd084dbb8ac0074984fa1959079220fd6019f278fae44a0d`. This verifies the same tiny public-route inference for the pinned client; it does not establish a complete hosted review.

(世, p=0.99) A later **complete hosted review** succeeded on 2026-10-12: [run38302209062](https://github.com/open-hax/eta-mu/actions/runs/38302209062) completed all four jobs, including the MiMo review and final evidence gate. Its exact source was `e53b524130458dd2732bc7797db303bc1d3ca279`. The native reviewer `eta-mu-ai[bot]` submitted [APPROVED review5495756990](https://github.com/open-hax/eta-mu/pull/350#pullrequestreview-5495756990) at 20:42:46 UTC; its body records 85 immutable source pages across 25 files, approximately 688 KB, and no findings. The complete saved run readback has SHA256 `46a2b2463a810dc5289092dbff7aac2fff6bf740de6c4c735dddba064cf0398c`; the complete native review readback has SHA256 `e182f441e5f0c4c527563f5d8d3a34c55db7f9ad29ea3616a9cdb46ebba668bd`. This independently establishes that the anonymous OpenCode route worked for the full hosted reviewer on that source, without introducing a user-supplied provider key or changing an account restriction. The subsequent source `d5b6c96aebcfe7014ef5f6c2080977e28d5b0a06` invalidates that approval for current-head merge credit; its own review and checks must complete.

## Frames

(汝, p=1.00) **Remembered route:** free OpenCode MiMo, no supplied credential, IP-based limits, changing fast-model slug. This preserves the user's prior operational knowledge.

(世, p=0.99) **Observed route:** an active zero-cost model in the fresh catalog, successful tiny inference with a read-only tool profile, and the separately bound successful complete hosted review on `e53b524`. These captures do not measure IP quota accounting, reset time or a durable guarantee of access. The historical registration warning is useful evidence; two current narrower profiles still failed.

(己, p=0.98) **Failure classification:** the all-denied/true-only `403 FreeTierError` is an OpenCode client/tool-recognition refusal. The hosted `401` explicitly reports an upstream credential error. A quota or IP-rate-limit diagnosis requires its own actual response; neither string alone establishes quota exhaustion or a bad user's key.

## Countermoves

(己, p=0.99) Recover the route in this order, preserving complete outcomes:

1. Inspect the current actual OpenCode client and obtain a checked current public catalog; select the currently active zero-cost tool-capable MiMo entry rather than relying on this note's dated slug. The historical command was `opencode models opencode --refresh --verbose`, but the pinned 1.18.18 client can swallow a refresh failure and still print success. Its missing-path fallback can also select bundled data. Keep the same checked physical catalog carrier through discovery and inference using `OPENCODE_MODELS_PATH` and `OPENCODE_DISABLE_MODELS_FETCH=true`, and compare the selected native ID and eligibility across raw and effective data. Root's actual [positive/negative/version control](/home/err/.local/share/openhax-codex/20261010/review-state/t350-actual11818-future-catalog-carrier-control-v1/actual-future-catalog-carrier-positive-negative-version-admission-v1.json), SHA256 `e239ca6a9886c857b85fe0f29777f974f377c709d223632ad49157100db61004`, proves that an artificial future row is recognized with the carrier and absent without it. This is a catalog ABI check, with no artificial model availability or inference credit; the workflow repair remains separately unqualified.
2. Inspect the effective agent/config and actual registered tools. Keep the read-only profile and exact `true` exception where that existing contract requires Bash registration; do not broaden shell permissions to make a request pass. A config comment or advertised model capability is not proof of what a request sent.
3. Separate client/tool-recognition refusal, explicit quota/IP-limit response, and upstream/provider error using the exact response body/status, timestamp, client/model/profile identity and terminal result. Retain failed attempts; retries remain owned by the existing verified recovery contract.
4. Qualify the hosted review independently through its existing full-input, tool-stage, accepted-invocation and native publication checks. A tiny local answer or zero-cost catalog entry supplies none of that credit.

(己, p=0.99) This note records supplied and already saved evidence only. Writing it did not run a model, modify a profile/account/provider, publish a native review, alter a ledger or promote a skill/spore.

## Next

(己, p=0.99) Use the recovered anonymous-route guidance and qualify the new catalog-discovery review workflow against its exact current source and hosted result.

## Additive observation: 2026-10-13 hosted review timeout

(世, p=0.99) The subsequent [run38422777933](https://github.com/open-hax/eta-mu/actions/runs/38422777933), attempt 1, reviewed exact source `a58faf81bfbd02526539ff39312e0a438b663fc0`. GitHub cancelled review job `115394481084` at **16:52:36 UTC** with the explicit annotation **“The job has exceeded the maximum execution time of 1h0m0s”**. The review step ran from 15:52:33 through 16:52:31 UTC. Its final submission/publication steps were skipped, and the evidence gate failed. This is an established job-budget failure, not a completed review or current-head approval.

(世, p=0.99) Before cancellation, actual OpenCode **1.18.18** used the checked public catalog carrier and selected the then-eligible `opencode/mimo-v2.6-flash-free`. The retained attempt contains **869 valid events, 287 completed inference steps with recorded cost 0, and 285 completed tool calls**, with no recorded error event and empty stderr. All **132 immutable diff pages** were read and assessed; their reconstructed text equals the supplied **1,014,186-byte** diff. No stage notes, finding, final submission, receipt or native review publication completed. The job-limit annotation does not explain the final in-flight inference or measure provider latency or IP quotas.

(己, p=0.99) The complete [read-only recovery dossier](/home/err/.local/share/openhax-codex/20261010/review-state/t350-a58-mimo-run38422777933-complete-read-only-recovery-v1/current-a58-mimo-complete-artifact-and-explicit-job-timeout-dossier-v1.json), SHA256 `1e6a16640a71398adbbd6f3d6bdf283fb5d8dc5d6650756f8bcf910c1f8f40cb`, binds the official logs and all three native artifacts. Its [frozen evidence manifest](/home/err/.local/share/openhax-codex/20261010/review-state/t350-a58-mimo-run38422777933-complete-read-only-recovery-v1/frozen-current-a58-mimo-native-run-recovery-v1.json), SHA256 `3ff53e40c8f589a274855eaacc60c45554e2b99ffc9ebcd66382e6b66de81981`, preserves the unsuccessful attempt. The annotation capture has SHA256 `e05a28ce56cdc19a5725f09b853642ebf60bf9a9bfc5481647feb0ad5111cc79`.

(己, p=0.99) Preserve the anonymous OpenCode route and select the current eligible free MiMo model from verified catalog data. A proposed 120-minute default, preserving explicit valid caller budgets from 60 through 180 minutes, was still private and unexecuted when this observation was recorded. Its future qualification cannot turn this cancelled attempt into a pass. Neither this timeout nor the earlier upstream credential error establishes a need for an OpenRouter key or an account/provider change.


## Additive observation: 2026-10-13 complete coverage followed by an invalid page request

(世, p=0.99) [Run38456739987](https://github.com/open-hax/eta-mu/actions/runs/38456739987), attempt 1, reviewed exact source `31301ba0c21bb04132c356e4098d2f8c1ab052e5`. OpenCode **1.18.18** completed with exit **0**, selecting the then-eligible `opencode/mimo-v2.6-flash-free` from a fresh catalog with an empty requested-model override. This slug is a dated observation. The host subsequently rejected the review with `unestablished-review-trace` / `page-id-binding`; no timeout or provider-credential failure was reported.

(世, p=0.99) All **153** advertised pages were read and assessed exactly once. Their text reconstructs the complete **1,165,478-byte** input diff. Status then reported complete coverage, missing `[]`, and zero stage evidence. The next request selected nonexistent page **154**, received an explicit unknown-chunk refusal, and ended the invocation. No evidence-stage or final-submission call followed; the attempt supplies no accepted invocation, native review, approval or completed round. Actual ordinary runtime custody identifies Muse `c1369c223cf3c57e3e31934a6d746bfcdfe73f5a`; it is distinct from the adopted b9 assessment profile.

(己, p=0.99) The [complete terminal dossier](/home/err/.local/share/openhax-codex/20261010/review-state/t350-31301b-live-mimo-ci-readonly-recovery-v1/third-terminal-native-run-and-attempt-recovery-v3/complete-terminal313-mimo-native-outcome-input-custody-and-page154-cause-dossier-v3.json), SHA256 `2b5eebb1dcc01ba13d57238cdaa9bc83ffb7df1e2312b1e3737e0b190c202be4`, binds native logs, artifact digests, runtime custody and actual tool calls. Select review page IDs from returned missing-ID coverage; never infer another page by incrementing the last assessed ID or probing for EOF. After complete coverage, proceed through the required stages and submit using retained content. Preserve failed-call STOP, immutable page validation and the existing two-attempt boundary. This protocol failure does not establish a need for OpenRouter credentials or an account change; the earlier one-hour cancellation remains a separate failure.
