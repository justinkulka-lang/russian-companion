#!/usr/bin/env python3
"""Open Word-packaged audit inputs; verify before reviewing. No Android execution."""
import argparse,csv,hashlib,io,json,stat,subprocess,sys,zipfile
from pathlib import Path
sys.dont_write_bytecode=True
sha=lambda b:hashlib.sha256(b).hexdigest()
def extract_checked(archive,destination):
    names=archive.namelist()
    assert len(names)==len(set(names)), 'Duplicate archive member'
    assert sum(i.file_size for i in archive.infolist()) < 2*1024**3, 'Unexpected expansion size'
    for info in archive.infolist():
        name=Path(info.filename)
        assert not name.is_absolute() and '..' not in name.parts and '\\' not in info.filename
        assert not stat.S_ISLNK(info.external_attr >> 16), 'Unexpected symlink'
    assert archive.testzip() is None, 'Archive CRC failure'
    destination.mkdir(exist_ok=False,parents=True)
    archive.extractall(destination)
def main():
    parser=argparse.ArgumentParser()
    parser.add_argument('word_file',type=Path)
    parser.add_argument('fresh_output',type=Path)
    args=parser.parse_args()
    args.fresh_output.mkdir(exist_ok=False,parents=True)
    with zipfile.ZipFile(args.word_file) as doc:
        manifest=json.loads(doc.read('word/embeddings/INPUT_MANIFEST.json'))
        payloads={}
        for row in manifest['files']:
            data=doc.read(row['docxPart'])
            assert len(data)==row['bytes'] and sha(data)==row['sha256'],row['docxPart']
            payloads[row['role']]=data
    snapshot=args.fresh_output/'snapshot'
    with zipfile.ZipFile(io.BytesIO(payloads['pinnedRepositorySnapshot'])) as archive:
        extract_checked(archive,snapshot)
    result=subprocess.run([sys.executable,str(snapshot/'VERIFY_PACKET.py')],cwd=args.fresh_output,capture_output=True,text=True)
    (args.fresh_output/'snapshot_verification.log').write_text(result.stdout+result.stderr)
    assert result.returncode==0,result.stderr
    identity=json.loads((snapshot/'PACKET_IDENTITY.json').read_text())
    assert identity['auditTarget']=='5c176eb22f4a93bf5f7fd20af15e79ec8c4748ed'
    current=args.fresh_output/'current_results'
    with zipfile.ZipFile(io.BytesIO(payloads['currentSixteenOutputAudit'])) as archive:
        sums={name:digest for digest,name in (line.split(None,1) for line in archive.read('SHA256SUMS.txt').decode().splitlines() if line.strip())}
        assert set(sums)==set(archive.namelist())-{'SHA256SUMS.txt'}
        for name,digest in sums.items():assert sha(archive.read(name))==digest,name
        extract_checked(archive,current)
    for role,name in [('finishPrompt','FINISH_AUDIT_PROMPT.txt'),('returnSpecification','REQUIRED_RETURNS.md'),('gapPlan','GAP_COMPLETION_PLAN.md')]:
        (args.fresh_output/name).write_bytes(payloads[role])
    findings=list(csv.DictReader((current/'FINDINGS.csv').open()))
    assert len(findings)==13 and {r['finding_id'] for r in findings}=={f'CC{i:03}' for i in range(1,14)}
    raw=snapshot/'repo/app/src/main/assets/content/v2-curriculum.json'
    assert raw.stat().st_size==20036214 and sha(raw.read_bytes())=='33e5655c99d6d5003da0ab1af9249dc814472e3436e0d28afd2f878f6ade4940'
    summary={'inputVerification':'PASS','target':identity['auditTarget'],'snapshotDirectory':str(snapshot),'currentResultsDirectory':str(current),'filesAtTarget':6427,'currentFindings':13,'newAndroidExecution':False,'auditCompletion':'NOT_ESTABLISHED_BY_INPUT_VERIFICATION','physicalAcceptance':'OUTSTANDING_LAST'}
    (args.fresh_output/'OPENED_INPUTS.json').write_text(json.dumps(summary,indent=2)+'\n')
    print(json.dumps(summary))
if __name__=='__main__':main()
