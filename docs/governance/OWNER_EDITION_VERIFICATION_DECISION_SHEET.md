# Product Owner edition-verification decision sheet

Status: READY_FOR_OWNER_MAPPING_AND_VARIANCE_ACCEPTANCE

This sheet records a reference-file comparison. It does not accept a mapping, activate a variance, inspect a copy on the Product Owner's behalf, or approve release. Reviewer identity, qualification, date, and signature stay blank. `owned_copy_verified` stays blank. `isbn_observation` stays `UNRESOLVED`. Variances 27, 29, and 30 stay `PROPOSED_NOT_ACTIVATED`.

The repository source digest is not printed here. This file is an input to that digest. The live value is `sourceDigest` on the unfrozen candidate and `repositorySourceDigest` in `audit/owner-governance/FINAL_AUDIT_PREPARATION_MANIFEST.json`.

## Verified edition identity and file hash

Observed from the three-part reference file. PDF indices 2 and 4 are blank, so no copyright year was read. The introduction signature is the only year reading.

| Fact | Observation |
|---|---|
| Title | The New Penguin Russian Course: A Complete Course for Beginners |
| Author | Nicholas J. Brown |
| Imprint | Penguin Books |
| Year | Introduction signed Nicholas J. Brown, SSEES, London, 1996. Not a copyright-page year. |
| ISBN | Back cover prints `0-14-012041-6`. The barcode digits read `9780140120417`. Unresolved. The worksheet isbn column stays blank. |
| PDF extent | 528 PDF indices: parts 1–176, 177–352, and 353–528 |
| Printed pages | For the lesson body, printed page N is PDF index N+12. PDF index 528 is the back cover. Grammatical tables begin at printed page 378. |
| Contents crop | The contents page for lessons 11–27 does not show the right-edge page numbers. Those boundaries were taken from the lesson-opening pages. |

File identity. None of these hashes is the retained private-file hash.

| File | SHA-256 |
|---|---|
| Part 1, PDF indices 1–176 | `de6715e5ff4c52394a5a68462fafbf1d9608906fe01704af73d44eceb4da1bcd` |
| Part 2, PDF indices 177–352 | `7bab8f041ed0a2f6f8e4a27c336c39c395b97b6fcd9d542781f755cef42ee304` |
| Part 3, PDF indices 353–528 | `72384a3305f64115ecf9b6028944ac06b40ca33705ff2b113f9e3ecb7eb3a1da` |
| Byte concatenation of the three parts | `4b3374d001f007a6dd92dfdc8f1a386d5b2591a102a166cf5c003642fdc793bc` |
| Retained private-file hash | `2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303` |

Mismatch: the part hashes and the concatenation do not match the retained private-file hash. File continuity is not established. The comparison is not ownership verification.

## Proposed mappings

Each row is still `PENDING_CURRICULUM_REVIEWER`. No shared printed page was found at a lesson boundary. Prior ranges that differ are named in the discrepancy column. Packaged `mappingNote` text was not edited.

| Unit | Lesson | Printed pages | Discrepancy |
|---:|---|---|---|
| 1 | Lesson 1 | 1–11 | None on this copy |
| 2 | Lesson 2 | 12–23 | None on this copy |
| 3 | Lesson 3 | 24–35 | None on this copy |
| 4 | Lesson 4 | 36–45 | None on this copy |
| 5 | Lesson 5 | 46–55 | None on this copy |
| 6 | Lesson 6 | 56–67 | None on this copy |
| 7 | Lesson 7 | 68–80 | None on this copy |
| 8 | Lesson 8 | 81–91 | None on this copy |
| 9 | Lesson 9 | 92–106 | None on this copy |
| 10 | Lesson 10 | 107–121 | None on this copy |
| 11 | Lesson 11 | 122–132 | None on this copy |
| 12 | Lesson 12 | 133–148 | None on this copy |
| 13 | Lesson 13 | 149–161 | None on this copy |
| 14 | Lesson 14 | 162–170 | None on this copy |
| 15 | Lesson 15 | 171–187 | None on this copy |
| 16 | Lesson 16 | 188–200 | None on this copy |
| 17 | Lesson 17 | 201–214 | None on this copy |
| 18 | Lesson 18 | 215–229 | None on this copy |
| 19 | Lesson 19 | 230–239 | None on this copy |
| 20 | Lesson 20 | 240–256 | None on this copy |
| 21 | Lesson 21 | 257–268 | Prior proposal 259–270. Packaged note still says 259–270. |
| 22 | Lesson 22 | 269–282 | Prior proposal 271–284. Packaged note still says 271–284. |
| 23 | Lesson 23 | 283–293 | Prior proposal 285–295. Packaged note still says 285–295. |
| 24 | Lesson 24 | 294–304 | Prior proposal 296–306. Packaged note still says 296–306. |
| 25 | Lesson 25 | 305–319 | Prior proposal 307–321. Packaged note still says 307–321. |
| 26 | Lesson 26 | 320–331 | Start and end match. The prior overlap with 307–321 is not a shared page. |
| 27 | Lesson 27 | 332–342 | None on the page range. Topic now names types and stress. |
| 28 | Lesson 28 | 343–354 | None on this copy |
| 29 | Lesson 29 | 355–367 | Page range matches. Topic no longer says punctuation is optional. |
| 30 | Lesson 30 | 368–377 | None on the page range. Letter names here review Lesson 1 section 1.5. |

Lesson titles on this copy match the public schedule titles at topic level. Topics in the worksheets are the proposed descriptions, not book prose.

## Exact proposed variance wording

All three stay inactive. Production validation still blocks Units 27, 29, and 30.

### Unit 27

Recommendation: ACTIVATE, not activated.

Unit 27 teaches passive participles only. Past passive material is required. Present passive material is optional and non-gating. Active participles first appear in Unit 29. The public schedule line remains “Participles: types and stress.” The reference file uses that title. Its body teaches past passive formation and stress as the main lesson, marks present passive as an extra section, and does not teach active participle formation.

### Unit 29

Recommendation: REVISE, and the revised text is the wording below. It is not activated.

Corrected proposed-resolution item 7, in full:

Unit 29 punctuation is in scope and is not unavailable. The shipped instruction on `obj-u29-punctuation`, claim `participle-punctuation`, steps `u29-st-teach-punctuation-2` and `u29-st-rem-punctuation`, is: when a participial phrase stands before the word it modifies, commas are not used, except when that word is a personal pronoun or the participial phrase states a cause. The paired admitted examples distinguish a phrase after the modified word, set off by commas on both sides, from a phrase before the modified word, which is not set off. The reference-file lesson includes a punctuation section marked extra. That book label does not make the shipped objective unavailable. The withdrawn sentence “Unit 29 punctuation remains optional and unavailable until sufficiently sourced and reviewed” is not the governing text. This item does not activate the variance.

Effects: `V2Validator.UNRESOLVED_VARIANCE_UNITS` is unchanged. Unit 29 stays `NOT_PREPARED`. The Unit 29 content-review digest is unchanged. A later activation of this corrected text would still require final curriculum audit, independent verification, and an explicit Product Owner instruction before the unit could leave `NOT_PREPARED`. That later activation would not install the unit, accept the mapping, pass a final audit, or approve release.

### Unit 30

Recommendation: ACTIVATE, not activated.

Letter names are introduced in Lesson 1, section 1.5, of the reference file. Lesson 30 reviews and applies those names for abbreviations and does not introduce them for the first time. The public schedule places “names of Russian letters” on unit 30. Dictionary use is absent from that public line. The reference file has a dictionaries section in Lesson 30. The shipped dictionary objective is original research synthesis on `obj-u30-dictionary` and is not a copy of that section. Unit 1’s shipped letter work does not by itself prove the full Lesson 1.5 inventory.

## Decisions needed from Justin Kulka

1. Accept or reject file continuity. The three reference parts do not match `2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303`.
2. Accept or reject the edition identity above, including back-cover ISBN `0-14-012041-6`, knowing the copyright page in this file is blank.
3. State whether this file is the owned copy. `owned_copy_verified` stays blank until that signature.
4. Accept, reject, or revise each of the 30 proposed mappings, including the corrected Unit 21–25 ranges and the rejected Unit 25/26 overlap.
5. Supply reviewer identity, qualification, date, and signature for any mapping that is accepted. Blank fields fail closed.
6. For Unit 27, choose ACTIVATE, REJECT, or REVISE on the passive-only wording. The choice is unsigned.
7. For Unit 29, choose ACTIVATE, REJECT, or REVISE on the corrected punctuation wording. Do not sign the withdrawn “unavailable” sentence.
8. For Unit 30, choose ACTIVATE, REJECT, or REVISE on the letter-name-review boundary and the separately sourced dictionary objective.
9. Do not treat this sheet, green CI, or a model name as acceptance.

Product Owner disposition:
Product Owner name:
Product Owner date:
Product Owner signature:

## Digest identities an eventual acceptance binds

Pack content-review digest, unchanged: `dc56345a9309e7dc8f5fe5e159f3ec935958d64b83443d4a9f17d6c653911a2f`.

Curriculum source commits, unchanged: Units 1–2 `4a6c21da07b515df226a090cc0ff6b1ff4a78aa1`; Units 3–5 and 11–15 `b22fa0cca895a677386a77d03a63301f7454aa07`; Units 6–10 `ae777d85fe3616fc8e065890201d40c241d113bc`; Units 16–20 `100ed31a3e3a8fee3ce6b7f8fc186a7ff1422cee`; Units 21–25 `f87523c4e80919c48ad39b80ea840e8ff164505f`; Units 26–30 `345448c16f326114ccb2f6738c75fb4206798cae`.

A mapping acceptance binds that unit’s content-review digest and mapping-package digest. A variance acceptance binds the same pair plus the repository source digest recorded on the candidate at the time of the signature. Review-package digests identify the pending packets.

| Unit | Content-review digest | Mapping-package digest | Review-package digest |
|---:|---|---|---|
| 1 | `9adca20544f28770210fe3029c2e0e4c437e3b409216e9288227c316e9ebf61a` | `bc0662b88ba1b3eab9f8c0b9484f692e8e30be9b2d061a05dd9eca30c3ffea54` | `35cd31ff9d44f6ea942a64e80e1951ee357b9fd01ac63f2972b6f784550afc5b` |
| 2 | `d51310f9ac1596ab50d2c5df7dffd9b10db23d712875680dbfb942bf577e056b` | `1edda6c24104bcdd79e5d58f0368d028b940b725b00d165d931953ca76e4dc20` | `207f0cd9077458385aba384ca7008992fbc85b88f27c2a96e223ebbc4c276288` |
| 3 | `deadcf10b8af7efca034b8d89921412f309d291180c09c4af490eb132b1bbb86` | `d0ad345ff7eb069a4ea0cbb94a923b4a766bc4666eab85781eb150172327da3e` | `dd4d54bcc49699a374841cf642a88a4d6558961817a352ecdc8e26362191989c` |
| 4 | `5a9be201954e4a4c37188ed7aca9fae1ee67e20622c7134271c1a68cd520e59c` | `b1b401f398b5b910cd300252e40fde1678886a42e7795b1da72efbae1c4aaacf` | `6773bc8bb20d958138c11c5933bf21e6cc956b61147652b93e92f41331769d93` |
| 5 | `9470cc61d50236425fb01771a4d20788c88d6c98f8a8694420609f86d7b9710d` | `1b78fc92b133fb43bc2143c4c27930ec9b900744bddb6e9fb44e6b504949ffac` | `4ce122143656b79bcbac77f20bd2468a9a3da13dd92f6af6bd3648c6c761adf9` |
| 6 | `fa6501022bd7ff4a2eca1e47c0ad488d96254cab0eafbecc61a8420835a3ab9c` | `b3554ab07e8578a9c5ff88e546e32c9fc2a5853ffde630bd2035f61c92ea83c8` | `53f560fe66038ad0032a561306ecbde1100c326799c87a465b3fc55a945ac91f` |
| 7 | `ec283b3bc0e58306d84e536947ab747d5ea264cea729bf4838124404ca958359` | `291453d0e1e635835556daff70c3d2c944c2434084f3986174d666f7c686a135` | `a132bfded20b47d1b57181455f33061fae72b4111180bd5476b6e9fd3daf87ca` |
| 8 | `e1dd456abf8a7b961f2f8471f35d4178961a53147c354f3e410e88d75f025c78` | `e22f9d8bb71b042b180824c379a75f471f2e031c76659755ef8900381442d35a` | `de892a181ff97fcec321e425364c700b09bec4a17fc5926ee792efde7c9a77a8` |
| 9 | `d0f135c408fc429392cf695a8122134013fca69637a4f9c1587db3fec6b3c65f` | `22f7654d5bc846d6b2a9db1d4f6ab13a598c63b0578be1cc21250960fb1da0af` | `6bb389b499b0ac91076552c51148701b464cbdc6490a9c9bad1f5946ff2456a6` |
| 10 | `6845b398da6ad901b06443dd0f56ee6ace4ba4e2da6af9e36d33934dc286c526` | `13f470a5e8fdac18c387ee94d060775014cc6d27962c52e68d8b5f37b76c5e0e` | `fb25539bb9af2f135a9d6a2d175cc06bc9da9670d246f096563fd1c98841d013` |
| 11 | `7a70c3b29f7c88934d0dcf21f40d531a45f1eff9daa8593f5af09d509bd10324` | `9553cd4380ecc619a42db5bd31a490fb1d930d1d015834e6ca21881a1810470b` | `eb67a1c5e37577237041c8e7a03ee3dcc31614c7c6c77f3e0c1f10ca8a8f4a7d` |
| 12 | `c610495bf16604d555187f66e456c2cd1cb127c77881ef87097332f5e2d426bb` | `6f119dfe3147a05c54dbfff09c38dee65df5dbae2f22a295285e70542acffe19` | `0a8819bcf0346adcc29f809808514ae14a36293e72d8418137a95d405b65a379` |
| 13 | `e8a04394dc8c90059f01a0b7fdf46a57951a98a85ac8050a29781ab1a4a027c5` | `74225a8d00a5bbfb5a58e2c4198a5f64cdb288452f024896902e3c7279aec341` | `ae21424de12d7da022b829acabd90c2315c4084634fc5f2e478b534bb1b1d279` |
| 14 | `270cbad6d62543931aa65932bdaeda9d10475bab78f7c12f05a8f75ac1308ec6` | `2553c9301afcb66dfc44a9e7f0a176885b4c625993c46822f119ce355bc47251` | `e31e34a0de94e9f8a4c23b9810ef15a1b756c0d3378e6eb0e2bc3f5152418e25` |
| 15 | `1fceb09b1709fcd76676925c5e363736fad187db50e2830cbd115bb5891219dd` | `fc0047cfb49a753bbcd8642a0d36c37c065161e687056847fca7bd1bf6070dd5` | `ae9487cc505c40243dc4a1f356979057ebb45cd5535dcc825c37e12e64a15e9c` |
| 16 | `6797fdb9c88927be744444861238a7eb3c2574e25b059df57dde02f45a8015c5` | `fc4c594c788f3e78d7cb280e48ad37b8fc5d41e2e37b7608889b3de2a43efc17` | `99850881bfe1b7d3c68d99cf504c153323cb724559400f65aa6a0dcd837bb4b1` |
| 17 | `a3d3b89b7359bac0dc1ba33483c819e31f22f69c0344be860dbac286637a404c` | `88fece04e7b8a1a707237548f2cc8f62f066bdf9392e8263c92c9d57518e24a2` | `82f356856af7a78a2866c2ecafb70072448df274e6b60e595221565fb856d947` |
| 18 | `6e6faca384de88b45092fcbdfe63d86a244f4bcc8459f290c2821d05faa083ae` | `2a31c6c0065f91d0d93616f5696f050921b62e01f040e472eb75cd9b6bbd944f` | `5a0e3dd7f4334b7f1cdbbf20eb2e327bab8fc279081c89a8b4680d69d9471aac` |
| 19 | `75cf1e4e1baa64f6b3025d3daa9fc63ab854d0c10fb40246e55d484077f1b6f3` | `64395530bd58a50c415e90549eeb941d829f5f81cb259012a80a1d7d646d9746` | `48294a82a8881543753fa764b7d60be0fc5e5f89a61ef55fdebe406a63f2ab41` |
| 20 | `01625d197e02f19603213ae6694002fa0bfa5418cc10484a7209b9f7c581129c` | `3b1d24b0a0a4448be815c4baf1c0b314761a29fdd65617ee3737e3c239830295` | `2fd220a238dfdfb7f3e78f4c188c3178f150430155b132c5654ac944d8bdb128` |
| 21 | `53a04dc4f69a5f19af1c9d4b09984e06a3d54ba586148ecef3531a0f6e850afa` | `25b6e3547f1a743a92e3cebebaaa7fc3915e24bf35df6f53cc9a376b38fea0b6` | `2f7ef22ca80f05c4fd52b2f2e8f267fe6276406aad4b72f8476fdccff8ec9cfe` |
| 22 | `d408624ff0aa05793384f6a2b4d9e2a27b008225f31498795e5ccf9b3225e169` | `124c97b28ff93bc8090ca2d30c10d17fed694e89b5a9f70392301d9e6e933c3b` | `91b52a2e2846e3e9f5d6d6c931cd6d2b72888c7810c58e8284d9349f3cc040b8` |
| 23 | `c46606f18f1b6c044a48934370c8ff82e32a4fe900164a779d846d7937855151` | `5c5561eacd86686a73ffd88d81fc083ae208ada14e3a7b9c3f43749bfdefe254` | `b19da1257d74f83b59e36f98e5b618718ab13f9c325690af2024f8bce53e413b` |
| 24 | `497c9978cd3a89159732f7c4b53a09d0923d98bbaa01f61722a769d1a808f74c` | `b8db8481ea62d3f88768f74385ed7411fe70559bbcab4c37a8c1f14792d20f87` | `8317cd5d2e8af21340436434e926083d0b9c0661d2ac4178e5e44a05032fb1cd` |
| 25 | `4f7acdd8f55829490434d73308921fb4d63da86f6792ec65623720c2dfc89957` | `5fffe6bca512b1a1e7fa9470fb8566e736aeecfabc363cadb0d94a0cbdd7fce3` | `58b25263b6afc7e4ce51db941ea9f2f78702b9434138a5d69750b438c05bed06` |
| 26 | `042693573ae94ddc31e1c68c625d854f8495934fbf1d39cd800684afcb078b06` | `9f46e2d70aeefde9067f0868c5099ea7df08c9cb5f924e858094d817d91a83fa` | `c00427fa210a835655b729de6574d285b9be804c29f18f6ff77f0f21adc90d82` |
| 27 | `b43404505a7bc8bc72a34d2af039d5695d1e919cdc309643ecc7c835cfd99db0` | `a15bf8dc76fa2ab5d7bc1448a9f2129da7ee281461338e280c503ee3a5150bdb` | `3a68a30ed98a92fccec81208ebc4cb3f8f15ecd478a06893693637fff62c0303` |
| 28 | `afa29cbd6d710bcb853d74189d012584660996b25270af78c3d23efe35cf1cc5` | `44452909b63f4e4b67632c3c24e9f997efa75bdf314d8b4fbb4e0933f29278ff` | `8a9a200da55420a44cc1368ad057a4698fdfcdcf0895277902d0f8a0f0547b51` |
| 29 | `bb8645fa3be7f3ac9b7cc7a7d61d551843975f8deccb419a3ad529416c3fb0e3` | `a40de565a8d86d7288e31bfaeb75d9c41761b810f99ee56afa1add74df334f21` | `cd7eed324e12a8c25f4a38d69290c4aa7ec6d7e65919adac59576b348ee62619` |
| 30 | `ed3a589a8d91e48c49675840118ca2951ad752f9784b0c8eeb21a74f66285187` | `5361634a4e364130af45cfe253e75c60882f293cd9079bd403373e12cfde6505` | `02a4ddb5c659428962eed21d738bfdccb58d70ad692746d65b5de4fc482cef18` |
