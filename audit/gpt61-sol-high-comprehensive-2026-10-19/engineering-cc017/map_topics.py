"""Static MA2-068 inventory, not a qualification or execution result."""
from pathlib import Path
import csv, hashlib, json, subprocess, sys
sys.dont_write_bytecode = True
repo = Path(sys.argv[1]).resolve(); out = Path(sys.argv[2]).resolve()
sys.path.insert(0, str(repo))
from catalog.curriculum_storage import loads
raw = (repo/'app/src/main/assets/content/v2-curriculum.json').read_bytes()
pack = loads(raw)
refs = {r['id']: r for r in pack['references']}
objectives = {o['id']: o for u in pack['units'] for o in u['objectives']}
steps = {s['id']: s for u in pack['units'] for session in u['sessions'] for s in session['steps']}
# Manually reviewed reference depth. Declared instruction classification alone is not substantive coverage.
topics = [
 ('alphabet',['ref-cyrillic-print','ref-confusable','ref-ca-letter-names'],'PARTIAL_PRINT_AND_LETTER_NAMES',['obj-u1-letter-identity']),
 ('pronunciation',['ref-iotated','ref-signs','ref-pronunciation'],'NOTE_ONLY_NOT_PHONETIC_SYSTEM',['obj-u1-iotated','obj-u1-signs']),
 ('stress',['ref-stress'],'NOTE_ONLY_NOT_STRESS_RULES',['obj-u4-stress']),
 ('spelling',['ref-signs'],'NOTE_ONLY_NOT_SPELLING_RULES',['obj-u7-spelling']),
 ('grammar terminology',[],'NO_DEDICATED_EXPLANATORY_REFERENCE',[]),
 ('cases',['ref-ca-cases'],'CAUTION_ONLY_NO_CASE_SYSTEM',['obj-u4-nominative','obj-u5-prepositional']),
 ('agreement',['ref-ca-cases'],'CAUTION_ONLY_NO_WORKED_AGREEMENT',['obj-u3-gender','obj-u7-endings','obj-u8-agreement']),
 ('verbs',[],'NO_DEDICATED_VERB_REFERENCE',['obj-u4-present','obj-u11-past-read','obj-u12-future-imp']),
 ('aspect',['ref-ca-aspect'],'CAUTION_ONLY_NO_WORKED_ASPECT',['obj-u12-aspect','obj-u15-aspect-consolidate']),
 ('motion',['ref-ca-motion'],'CAUTION_ONLY_NO_WORKED_MOTION',['obj-u20-motion']),
 ('numbers',[],'NO_DEDICATED_NUMBER_REFERENCE',['obj-u9-cardinal','obj-u22-half-forms']),
 ('time',[],'NO_DEDICATED_TIME_REFERENCE',['obj-u17-hours','obj-u17-minutes']),
 ('questions',[],'NO_DEDICATED_QUESTION_REFERENCE',['obj-u5-questions']),
 ('negation',[],'NO_DEDICATED_NEGATION_REFERENCE',['obj-u24-verbal-ne','obj-u24-absence']),
 ('core prepositions',[],'NO_DEDICATED_PREPOSITION_REFERENCE',['obj-u5-prepositional','obj-u10-preposition','obj-u16-inst-prep']),
]
rows=[]
for topic,reference_ids,depth,candidate_ids in topics:
    assert all(i in refs for i in reference_ids), topic
    assert all(refs[i]['classification']=='LINGUISTIC_INSTRUCTION' for i in reference_ids), topic
    assert all(i in objectives for i in candidate_ids), topic
    rows.append(dict(topic=topic,existingInstructionReferenceIds=reference_ids,currentReferenceDepth=depth,
        candidateLessonObjectives=[dict(id=i,unit=objectives[i]['unitNumber'],title=objectives[i]['title'],
            teachingStepIds=[sid for sid in objectives[i]['instructionStepIds'] if steps[sid]['kind']=='TEACH'],
            exampleIds=objectives[i]['exampleIds'],qualification='NOT_ESTABLISHED_BY_THIS_MAP') for i in candidate_ids],
        reachedLevelProjection=[dict(reachedUnit=level,visibleInstructionReferenceIds=[i for i in reference_ids if refs[i]['introducedInUnit']<=level],
            state=depth if any(refs[i]['introducedInUnit']<=level for i in reference_ids) else 'NO_TOPIC_REFERENCE_VISIBLE') for level in range(1,31)],
        fullTopicCoverageVerified=False))
result=dict(sourceCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=repo,text=True).strip(),
    assetSha256=hashlib.sha256(raw).hexdigest(),authority='docs/governance/MASTER_AUTHORITY_v2.0.md MA2-068',
    topicCount=len(rows),referenceCount=len(refs),topics=rows,
    referenceInventory=[dict(id=e['id'],title=e['title'],introducedInUnit=e['introducedInUnit'],classification=e['classification'],
        detailBytes=len(e['detail'].encode()),detailSha256=hashlib.sha256(e['detail'].encode()).hexdigest()) for e in refs.values()],
    scope='Static content/depth review and introduced-unit projection. Not 30 UI executions, test counts, source/rights/linguistic qualification or full MA2-068 acceptance. Lesson pointers are research candidates, not app reference entries.',
    licenseNoticesExcludedFromInstructionCoverage=True)
(out/'MA2_068_TOPIC_MAP.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
with (out/'MA2_068_TOPIC_MAP.csv').open('w',newline='') as f:
    writer=csv.writer(f);writer.writerow(['topic','existing_instruction_reference_ids','current_reference_depth','candidate_lesson_objective_ids','full_topic_coverage_verified'])
    for row in rows: writer.writerow([row['topic'],';'.join(row['existingInstructionReferenceIds']),row['currentReferenceDepth'],';'.join(x['id'] for x in row['candidateLessonObjectives']),'FALSE'])
print(len(rows),'MA2-068 topic rows; none claimed fully qualified')
