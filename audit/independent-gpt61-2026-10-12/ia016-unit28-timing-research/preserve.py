"""Research-checkpoint preservation proof, not an implementation/final audit test."""
import hashlib,json,subprocess
from pathlib import Path
ROOT=Path(__file__).resolve().parents[3]
HERE=Path(__file__).resolve().parent
sha=lambda b:hashlib.sha256(b).hexdigest()
research=json.loads((HERE/'SOURCE_CONTEXT_RESEARCH.json').read_text())
baseline=research['baselineCommit']
protected=['app','catalog','review','release','docs/governance','audit/curriculum-readiness','audit/owner-governance']
changed=subprocess.check_output(['git','diff',baseline,'--',*protected],cwd=ROOT)
assert not changed,'Research must not change production, source, review, release or governed evidence.'
assert sha((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes())==research['curriculumFileSha256']
for file,identity in research['retainedSourceIdentities'].items():
 b=(ROOT/file).read_bytes();assert len(b)==identity['bytes'] and sha(b)==identity['sha256'],file
receipt=json.loads((ROOT/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json').read_text())
for f in receipt['files']:
 b=(ROOT/'audit/independent-gpt61-2026-10-12'/f['path']).read_bytes()
 assert len(b)==f['bytes'] and sha(b)==f['sha256'],f['path']
assert len(receipt['files'])==9
result=dict(baselineCommit=baseline,protectedTreesUnchanged=protected,originalDiagnosticFilesPreserved=9,retainedFileIdentitiesPreserved=len(research['retainedSourceIdentities']),applicationCorrection=False,formalAcceptance=False)
(HERE/'PRESERVATION.json').write_text(json.dumps(result,indent=2)+'\n')
print(json.dumps(result))
