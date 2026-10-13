#!/usr/bin/env python3
"""Reconcile the diagnostic's original literal inventory, without semantic acceptance."""
import hashlib,json,subprocess
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
TARGET='ebd71fd0d1226a5ce3ec9a310bb3638c3bc34fb9'
ASSET='app/src/main/assets/content/v2-curriculum.json'
def main():
    raw=subprocess.check_output(['git','show',TARGET+':'+ASSET],cwd=ROOT)
    original=json.loads(raw);currentRaw=(ROOT/ASSET).read_bytes();current=json.loads(currentRaw)
    targets=[i for i in original['items'] if i['feedback'].startswith('Does')]
    assert len(targets)==135
    now={i['id']:i for i in current['items']};rows=[]
    scoreFields=['kind','language','prompt','promptLetter','options','answer','objectiveId','sourceId','containsGeneratedRussian']
    relationshipFields=['testedFeature','evidenceMode','contrastSetId','cueLanguage','independent','answerSourceIds','distractorSourceIds','contrastMembers','relationKind','relationParticipantIds','responseKind']
    for old in targets:
        item=now[old['id']];assert item['feedback']!=old['feedback'] and not item['feedback'].startswith('Does')
        assert item['assessment']['whyAnswerDemonstratesObjective']==item['feedback']
        assert item['distractorRationales']==item['assessment']['distractorDifference']
        assert all(not s.startswith('Does') for s in item['distractorRationales'])
        changed=[k for k in scoreFields if old.get(k)!=item.get(k)]
        changed+=['assessment.'+k for k in relationshipFields if old.get('assessment',{}).get(k)!=item.get('assessment',{}).get(k)]
        rows.append(dict(itemId=item['id'],objectiveId=item['objectiveId'],oldFeedback=old['feedback'],currentFeedback=item['feedback'],oldAnswer=old['answer'],currentAnswer=item['answer'],currentDistractorReasons=item['distractorRationales'],scoredSourceFieldsChangedSinceDiagnostic=changed,semanticAcceptance=False))
    assert not any(i['feedback'].startswith('Does') for i in current['items'])
    report=dict(originalDiagnosticTarget=TARGET,originalRawAssetSha256=hashlib.sha256(raw).hexdigest(),currentRawAssetSha256=hashlib.sha256(currentRaw).hexdigest(),originalLiteralFeedbackTargets=135,currentTargetsPresent=135,currentTargetsWithChangedFeedback=135,currentLiteralDoesFeedbackRows=0,targetsWithScoredSourceChangesSinceDiagnostic=sum(bool(r['scoredSourceFieldsChangedSinceDiagnostic']) for r in rows),rows=rows,scope='LITERAL_INVENTORY_AND_FIELD_RECONCILIATION_NOT_TEST_COUNT_OR_SEMANTIC_AUDIT',sourceTupleExplanation='Changes since the diagnostic include separately retained context/cue and complete-rule corrections. Individual feedback-only batches preserve their own baseline scored/source tuples; see each correction ledger and verification manifest. This report does not relabel those earlier changes as feedback-only.',remaining='Independent semantic/source/rights verification, broader feedback/teaching/remediation review, IA012 category cues, IA016 duplicates and other findings remain open.',formalAcceptance=False,publicRelease='NOT_APPROVED')
    out=ROOT/'audit/independent-gpt61-2026-10-12/IA009_135_TARGET_FEEDBACK_RECONCILIATION.json';out.write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    print({k:report[k] for k in ['originalLiteralFeedbackTargets','currentTargetsPresent','currentTargetsWithChangedFeedback','currentLiteralDoesFeedbackRows','targetsWithScoredSourceChangesSinceDiagnostic']})
if __name__=='__main__':main()
