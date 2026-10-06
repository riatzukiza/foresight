# Alpha integer composition remains a planning decision

This note refines existing Foresight issue51 and card `3f2c935b-7a8e-4324-9a5a-9181ae23b7a1`. It introduces no new umbrella, source implementation or operational transition. The card's original body, blocked frontmatter and event history remain intact.

## Exact evidence

The planning base is personal Foresight main `4927ab7d439ac7e1093c574096a2d5a6d6241391`; observed origin main is `fcfc2d17f28640203066ddfe0a22f87db7372a32`. Both retain the same issue51 card and Katamorph gitlink `3bd4cf26e68dc88fbe67f831baa4bc389e3363e7`. Origin issue53's Mermaid card still depends on issue51's portability envelope; it is not changed by this plan.

Katamorph [PR18](https://github.com/open-hax/katamorph/pull/18) merged on August30 at `7a754b751eb2ea5ff8ecba42986b04e35a0537c1`, with final source head `54ca5bc83146358646eb93b4ee99e750ca3e0fb8`. The accepted `src/cljc/katamorph/schema/condition.cljc` defines `portable-integer?` and `PortableInteger`. Current main `fe6017b28baa2d561550dc03768b3dd0da3f1480` is a descendant of that merge. The root's pinned version defines `PortableNumber` and `PortableValue`, but not `PortableInteger`.

GitHub's exact old-pin-to-merge comparison reports 66 commits and 33 changed files. This transition also contains action registries, provider bindings, workflow graph laws, sandbox and license changes. Availability of the merged integer declaration resolves the old missing-upstream-ownership premise, without proving compatibility or qualifying the whole dependency update.

## Semantic conflict to resolve

Issue51 requires doubles to fail as Alpha identities. The accepted upstream predicate explicitly permits a finite, mathematically integral JVM Double within the safe range. Focused execution of the unchanged accepted source under Babashka observed `1.0` accepted and `1.5`, infinity, NaN, an out-of-range integer, a ratio and a big decimal rejected. NBB observed the corresponding safe-range decisions and that `1` and `1.0` are identical JavaScript numeric values.

These are focused source observations, not full JVM or compiled CLJS suite passes. They expose why symbol reuse alone cannot settle the issue. Portable numeric payloads and Alpha identity values have distinct admission obligations; the owner must explicitly reconcile representation and value semantics. No changed meaning, copied grammar, or new portability kernel is accepted here. Original issue51 acceptance and source heads #19, #21 and #22 remain preserved.

## Remaining qualification

Before implementation, record the owner decision, the selected visible merged child revision, the complete transition and affected consumers. Require the same registered Alpha assertions under JVM and Node CLJS, two-reader lexical proof, negative host/nested payload cases, safe-integer boundaries, open-record envelopes, `:alpha/id` registration and current root laws at exact revisions. Retain unavailable evidence as a blocker.

Personal [Foresight PR2](https://github.com/riatzukiza/foresight/pull/2), observed at `0fdeb010174de5169984b9c841ab0d32f10d7147`, separately changes the source-bearing document law and its tests. Its source and reviews remain untouched; future composition must assess that consumer at its own revision. An active chat owner for PR2 was not established by the bounded ownership readback, so this plan claims no ownership of it. The independently active review-restoration lane owns EtaMu342/Proxx/Uxx workflow work and is also excluded.

Native planning review and lawful Rheos readiness precede implementation. This PR refines the existing blocked card body and evidence only. It does not repin a gitlink, update dependency metadata, change code, rewrite events, merge, request models, activate workflows, alter secrets/settings, or execute a service.

Raw source/native responses, focused probe output, Rheos readback and hash/prefix checks live in `.ημ/verification/issue51-integer-composition-planning-20261006/`. A successful planning audit does not imply owner acceptance, full behavioral verification or a qualified development head.
