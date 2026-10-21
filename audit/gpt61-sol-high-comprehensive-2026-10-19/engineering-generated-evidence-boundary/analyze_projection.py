from pathlib import Path
import json,re,hashlib,subprocess
r=Path('/workspace/russian-companion');w=Path(__file__).parent;o=w/'PROJECTION_NOT_ACCEPTED';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();rows=[]
for line in (w/'PROJECTION_1.txt').read_text().splitlines():
 if not re.match(r'^\d+ ',line):continue
 n,content,pack,package,repo,source=line.split();n=int(n);path=r/f'review/unit-{n}/UNIT_{n}_REVIEW_MANIFEST.json';old=json.loads(path.read_text());current=dict(contentReviewDigest=content,packContentReviewDigest=pack,reviewPackageDigest=package,repositorySourceDigest=repo,curriculumSourceCommit=source)
 rows.append(dict(unit=n,committedManifest=path.relative_to(r).as_posix(),committedSha256=sha(path),current=current,recorded={k:old[k] for k in current},changedFields=[k for k in current if old[k]!=current[k]],status=old['unitReviewStatus'],qualifiedReview=False))
assert len(rows)==30
summary={key:[x['unit'] for x in rows if key in x['changedFields']] for key in rows[0]['current']}
assert summary['contentReviewDigest']==[4,7,8,10,15]
assert summary['reviewPackageDigest']==[4,7,8,10,15]
assert summary['packContentReviewDigest']==list(range(1,31)) and summary['repositorySourceDigest']==list(range(1,31))
assert summary['curriculumSourceCommit']==[]
pairs=[('READINESS.md','audit/curriculum-readiness/V2_CURRICULUM_READINESS_RECONCILIATION.md'),('READINESS.json','audit/curriculum-readiness/V2_CURRICULUM_READINESS_RECONCILIATION.json'),('OWNER_PREPARATION.json','audit/owner-governance/FINAL_AUDIT_PREPARATION_MANIFEST.json')]
for p in sorted((o/'unit-1').iterdir()):pairs.append(('unit-1/'+p.name,'review/unit-1/'+p.name))
file_rows=[dict(projection=a,trackedPath=b,recordedSha256=sha(r/b),projectedSha256=sha(o/a),recordedBytes=(r/b).stat().st_size,projectedBytes=(o/a).stat().st_size,byteEqual=(r/b).read_bytes()==(o/a).read_bytes()) for a,b in pairs]
(w/'DRIFT_RECONCILIATION.json').write_text(json.dumps(dict(kind='DIAGNOSTIC_PROJECTION_NOT_REVIEW_OR_ACCEPTANCE',projectionSourceCommit=json.loads((w/'PROJECTION_CONTEXT.json').read_text())['sourceCommit'],unitCount=30,unitsChangedByField=summary,units=rows,generatedFileComparisons=file_rows,trackedFilesOverwritten=False),indent=2)+'\n')
print(summary)
