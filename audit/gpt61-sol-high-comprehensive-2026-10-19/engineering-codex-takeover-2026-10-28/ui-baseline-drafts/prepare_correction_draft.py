"""Prepare reviewable source copies outside the checkout; do not apply them."""
from pathlib import Path
import hashlib,json,difflib
R=Path('/workspace/russian-companion');W=Path(__file__).parent
screens='app/src/main/java/dev/russiancompanion/v2/ui/V2Screens.kt'
labs='app/src/main/java/dev/russiancompanion/v2/ui/V2PracticeFlows.kt'
review='app/src/main/java/dev/russiancompanion/v2/ui/V2CumulativeReviewScreen.kt'
helper='app/src/main/java/dev/russiancompanion/v2/ui/V2AnswerFeedback.kt'
original={p:(R/p).read_text()for p in [screens,labs,review]};updated=dict(original)
old='''    var page by remember { mutableIntStateOf(0) }
    var penguin by remember { mutableStateOf(true) }
    var anki by remember { mutableStateOf(false) }
    var binder by remember { mutableStateOf(false) }
    var tutor by remember { mutableStateOf(false) }
    var start by remember { mutableStateOf(V2StartingPoint.NEW_TO_CYRILLIC) }
    var minutes by remember { mutableIntStateOf(35) }'''
assert updated[screens].count(old)==1
updated[screens]=updated[screens].replace(old,old.replace('by remember {','by rememberSaveable {'),1)
old='''                val correct = selected == item.answer
                Text(
                    if (correct) "Recorded as correct. ${item.feedback}" else "Not the reviewed answer. ${item.feedback}",'''
new='''                val correct = selected == item.answer
                val explanation = selectedAnswerExplanation(item, selected)
                Text(
                    if (correct) "Recorded as correct. $explanation" else "Not the reviewed answer. $explanation",'''
assert updated[screens].count(old)==1;updated[screens]=updated[screens].replace(old,new,1)
for before,after in [
    ('New to Cyrillic is the recommended start. Resume from a later book lesson is not available while Units 3–30 are not installed.','New to Cyrillic is the recommended start. These choices adjust guidance; you will still begin at Unit 1.'),
    ('Later units are not yet installed. You will still begin at Unit 1.','You will begin at Unit 1. Choosing a starting point does not record any completed work.')]:
    assert updated[screens].count(before)==1;updated[screens]=updated[screens].replace(before,after,1)
old='''        if (recorded != null) {
            Text(
                if (recorded.correct) {
                    "Recorded as correct. This optional practice does not complete a required check. ${item.feedback}"
                } else {
                    "Not the reviewed answer. ${item.feedback}"
                },'''
new='''        if (recorded != null) {
            val explanation = selectedAnswerExplanation(item, recorded.selected)
            Text(
                if (recorded.correct) {
                    "Recorded as correct. This optional practice does not complete a required check. $explanation"
                } else {
                    "Not the reviewed answer. $explanation"
                },'''
assert updated[labs].count(old)==1;updated[labs]=updated[labs].replace(old,new,1)
old='''            if (latest != null) {
                Text(if (index in state.completed) "Review answer recorded. ${item.feedback}" else "Not the reviewed answer. ${item.feedback}",'''
new='''            if (latest != null) {
                val explanation = selectedAnswerExplanation(item, latest.selected)
                Text(if (index in state.completed) "Review answer recorded. $explanation" else "Not the reviewed answer. $explanation",'''
assert updated[review].count(old)==1;updated[review]=updated[review].replace(old,new,1)
assert not(R/helper).exists()
updated[helper]='''package dev.russiancompanion.v2.ui

import dev.russiancompanion.v2.curriculum.V2CheckItem

/** Explanation only: authored option values determine the rationale; grading and credit are unchanged. */
internal fun selectedAnswerExplanation(item: V2CheckItem, selected: String): String {
    if (selected == item.answer) return item.feedback
    val index = item.options.indexOf(selected)
    val rationale = item.distractorRationales.getOrNull(index)?.takeIf { it.isNotBlank() }
        ?: return item.feedback
    if (rationale in item.feedback) return item.feedback
    return if (item.feedback.isBlank()) rationale else "$rationale\\n${item.feedback}"
}
'''
patch=[];identities={}
for p,new in updated.items():
    before=original.get(p,'');dest=W/'PROPOSED_MAIN'/p;dest.parent.mkdir(parents=True,exist_ok=True);dest.write_text(new)
    identities[p]={'beforeSha256':hashlib.sha256(before.encode()).hexdigest()if p in original else None,'afterSha256':hashlib.sha256(new.encode()).hexdigest()}
    patch.extend(difflib.unified_diff(before.splitlines(keepends=True),new.splitlines(keepends=True),fromfile='a/'+p if p in original else'/dev/null',tofile='b/'+p))
(W/'PROPOSED_CORRECTION.diff').write_text(''.join(patch))
(W/'PROPOSED_SOURCE_IDENTITIES.json').write_text(json.dumps(identities,indent=2)+'\n')
print(json.dumps({'draftOnly':True,'sourceFiles':len(updated),'repoEdited':False,'testsRun':False,'findings':['CC034','CC040'],'additionalObservation':'Onboarding incorrectly says all later units are uninstalled; draft removes that unconditional installation claim.'}))
