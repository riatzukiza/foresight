---
status: incubating
created: 2026-10-16T20:44:35.659627636Z
source-session: /home/err/.local/share/openhax-codex/20261010/services
source-task: Keep the CMS browser owner and its second CDP client on one explicit download directory
p-efficiency: 0.38
p-friction: 0.82
p-skill-candidate: 0.79
promoted-to: ""
rejected-reason: ""
---

## Problem
The CMS browser suite stopped at its first export. Installed Playwright 1.58.2 and agent-browser 0.15.1 source shows a second CDP connection can replace persistent-context Browser.setDownloadBehavior with another artifacts directory. The original failed download Error remains unknown.

## Pattern
Two CDP clients can change browser-global download behavior while the original owner still tracks artifacts at its explicit directory. A passing UI click does not establish that the owner can save the resulting file.

## Candidate skill outline
- Name suggestion
- Trigger phrases
- Key steps or rules
- Anti-patterns to avoid

## Better path
Preserve one explicit owned downloadsPath. After trusted second-client attachment and browser/context identity checks, register CDP detach before sending Browser.setDownloadBehavior allowAndName back to that same directory. Preserve pending-download rejection, filename, regular-file, byte/digest and no-download assertions. In this pinned installation five causal controls and all eight real export scenarios passed; the complete 57-case suite still failed separately at feedback case54. Inspect current versions before reusing. Incubate locally; do not promote in this session.

## Receipt refs
- 1a3ddfd5-d476-4b5c-814a-4a1143503e01,96f67cc7-1ef5-4b82-a8d6-46626269794d
